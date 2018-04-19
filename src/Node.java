import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.reber.raft.AppendEntriesProtos.AppendEntries;
import com.reber.raft.RequestVoteProtos.RequestVote;
import com.reber.raft.RequestVoteResponseProtos.RequestVoteResponse;
import com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse;

@SuppressWarnings("Duplicates")
public class Node {

    private Role role;
    private String nodeId;
    //start of state data
    private int currentTerm;
    private String votedFor;
    private ArrayList<LogEntry> log;
    private int commitIndex;
    private int lastAppliedIndex;
    private int[] nextIndex;
    private int[] matchIndex;
    //end of state data
    Network network;
    private int votesReceivedCount; // need this for candidates method
    private static int numberOfNodes; //need this for candidates method;
    private long lastTimeReceivedAppendEntriesFromLeader;
    private long electionTimeout;
    private ArrayList<String> listOfNodes;
    private long electionStart;
    private String leaderId; //current leader
    //private Timer

    ConcurrentLinkedQueue<MessageWrapper> messages; //holds our messages.  This is how we respond.

    public enum Role {
        FOLLOWER, LEADER, CANDIDATE
    }

    public Node() {
        network = new Network();
        this.role = Role.FOLLOWER; //when servers start up, they begin as followers
        this.currentTerm = 0;
        this.votedFor = "0";
        this.log = new ArrayList<LogEntry>();
        this.commitIndex = 0;
        this.lastAppliedIndex = 0;
        this.nextIndex = null;
        this.matchIndex = null;
        this.lastTimeReceivedAppendEntriesFromLeader = 0;
        this.electionTimeout = computeElectionTimeout(1, 5);
        this.listOfNodes = network.loadNodes();
        numberOfNodes = this.listOfNodes.size();
        this.leaderId = "0";

        try{
            this.nodeId = InetAddress.getLocalHost().toString();
        } catch(UnknownHostException e){
            e.printStackTrace();
        }

    }

    //All Servers:

    public void run() throws InvalidProtocolBufferException {

        //increment votes received

        while (true) {

            //dequeue here, send it correctly

            MessageWrapper tempMessage = null;

            if(!messages.isEmpty())
                tempMessage = messages.poll();

            //is role = correct here? role is already saved in the instance data
            switch (role) {
                case LEADER:
                    role = leader(tempMessage);
                    break;
                case FOLLOWER:
                    role = follower(tempMessage);
                    break;
                case CANDIDATE:
                    role = candidate(tempMessage);
                    break;
            }

            //if currentTime - lastRecievedTime > timeOut
        }


    }

    public Role leader(MessageWrapper message) throws InvalidProtocolBufferException {

        if (commitIndex > lastAppliedIndex) {
            lastAppliedIndex++;
            //apply(log.get(lastAppliedIndex));
        }

        if(message != null) {

            int type = message.getMessageType();
            byte[] data = message.getData();

            int termT = 0;

            if(type ==  1) {
                RequestVote voteRequest = RequestVote.parseFrom(data);
                termT = voteRequest.getTerm();
            } else if(type == 2){
                AppendEntries appendRequest = AppendEntries.parseFrom(data);
                termT = appendRequest.getTerm();
            } else if(type == 3){ //RequestVoteResponse
                RequestVote voteRequestResponse = RequestVote.parseFrom(data);
                termT = voteRequestResponse.getTerm();
            } else if(type == 4){ //AppendEntriesResponse
                AppendEntriesResponse appendEntriesResponse = AppendEntriesResponse.parseFrom(data);
                termT = appendEntriesResponse.getTerm();
            }

            //if RPC request or response contains term T > currentTerm: set currentTerm = t, convert to follower
            if(termT > currentTerm) {
                currentTerm = termT;
                changeRole(Role.FOLLOWER); //convert to follower
            }

            // IF COMMAND RECEIVED FROM CLIENT: APPEND ENTRY TO LOCAL LOG, RESPOND AFTER ENTRY APPLIED TO STATE MACHINE (COMMAND COMES FROM QUEUE)

            //SEND HEARTBEATS DURING IDLE PERIODS TO PREVENT ELECTION TIMEOUTS
            AppendEntries appendEntriesHeartbeat = AppendEntries.newBuilder().build();

            byte[] heartBeatData = appendEntriesHeartbeat.toByteArray();

            for(String destination : listOfNodes) {
                network.sendMessage(destination, 2, data.length, heartBeatData);
            }

        }
        return role;
    }

    public Role follower(MessageWrapper message) throws InvalidProtocolBufferException {

        if(commitIndex > lastAppliedIndex) {
            lastAppliedIndex++;
            //apply(log.get(lastAppliedIndex));
        }

        if(message != null) {
            // do something

            int type = message.getMessageType();
            byte[] data = message.getData();

            int termT = 0;

            if(type ==  1) {
                RequestVote voteRequest = RequestVote.parseFrom(data);
                termT = voteRequest.getTerm();

                //if RPC request or response contains term T > currentTerm: set currentTerm = t, convert to follower
                if(termT > currentTerm) {
                    currentTerm = termT;
                    changeRole(Role.FOLLOWER); //convert to follower
                    //send back false in requestVoteResponse
                }

                //create requestVoteResponse
                RequestVoteResponse requestVoteResponse = RequestVoteResponse.newBuilder().setTerm(currentTerm).setVoteGranted(true).build();

                String destination =  voteRequest.getCandidateId();
                network.sendMessage(destination, 1, data.length, data);


            } else if(type == 2){
                AppendEntries appendRequest = AppendEntries.parseFrom(data);
                termT = appendRequest.getTerm();

                //create appendEntriesResponse

                boolean success = false;

                int prevLogIndex = appendRequest.getPrevLogIndex();
                int prevLogTerm = appendRequest.getPrevLogterm();


                //set success = true if follower contained entry matching prevLogIndex and prevLogTerm

               for(LogEntry entry : log) {
                   //int term = entry.getTerm();
                   //ArrayList<String> entries = entry.getCommands();
                   success = true;
               }

                AppendEntriesResponse appendEntriesResponse =  AppendEntriesResponse.newBuilder().setTerm(currentTerm).setSuccess(success).build();

                String destination =  appendRequest.getLeaderId();
                network.sendMessage(destination, 1, data.length, data);

            } else if(type == 3){ //RequestVoteResponse
                RequestVote voteRequestResponse = RequestVote.parseFrom(data);
                termT = voteRequestResponse.getTerm();
            } else if(type == 4){ //AppendEntriesResponse
                AppendEntriesResponse appendEntriesResponse = AppendEntriesResponse.parseFrom(data);
                termT = appendEntriesResponse.getTerm();
            }


        }

        // RESPOND TO RPCs FROM CANDIDATES AND LEADERS ???


       // If election timeout elapses without receiving AppendEntries RPC from current leader or granting vote to candidate: convert to candidate
        if(System.nanoTime() - lastTimeReceivedAppendEntriesFromLeader > electionTimeout) {
            changeRole(Role.CANDIDATE);
            //set role to candidate
        }

        return role;

    }

    //KEEP
    public Role leader() throws InvalidProtocolBufferException{

        MessageWrapper message = messages.poll();
        int messageType = message.getMessageType();
        byte[] data = message.getData();

        RequestVoteResponse response =  RequestVoteResponse.parseFrom(data);

        //upon election: send initial empty AppendEntries RPCs (heartbeat) to each server; repeat during idle periods to prevent election timeouts
        AppendEntries appendEntriesHeartbeat = AppendEntries.newBuilder().build();

        byte[] dataToSend = appendEntriesHeartbeat.toByteArray();

        for(String destination : listOfNodes) {
            network.sendMessage(destination, 2, data.length, dataToSend);
        }


        while(true) {

            if (commitIndex > lastAppliedIndex) {
                lastAppliedIndex++;
                //apply(log.get(lastAppliedIndex));
            }

            int termT = response.getTerm();
            if(termT > currentTerm) {
                currentTerm = termT;
                return Role.FOLLOWER;
            }






        }




    }

    public Role follower() throws UnknownHostException, InvalidProtocolBufferException {
        while (true) {

            if(!messages.isEmpty()) {
                MessageWrapper message = messages.poll();
                int messageType = message.getMessageType();
                byte[] data = message.getData();

                RequestVote requestVote = null;
                AppendEntries appendEntries = null;

                switch (messageType) {
                    case 1: //RequestVote
                        requestVote = RequestVote.parseFrom(data);
                        int term = requestVote.getTerm();

                        String destination = requestVote.getCandidateId();
                        RequestVoteResponse requestVoteResponse;
                        byte[] dataToSend = null;
                        if(term < currentTerm) {
                            requestVoteResponse = RequestVoteResponse.newBuilder().setTerm(this.currentTerm).setVoteGranted(false).build();
                            dataToSend = requestVote.toByteArray();
                        }

                        if((this.votedFor == null || votedFor.equals(destination)) && requestVote.getLastLogIndex() > this.lastAppliedIndex) {
                            requestVoteResponse = RequestVoteResponse.newBuilder().setTerm(term).setVoteGranted(true).build();
                        }

                        network.sendMessage(destination, 3, dataToSend.length, dataToSend);

                        break;
                    case 2: //AppendEntries
                        appendEntries = AppendEntries.parseFrom(data);
                        term = appendEntries.getTerm();

                        AppendEntriesResponse appendEntriesResponse = null;
                        destination = appendEntries.getLeaderId();



                        if(term < currentTerm) {
                            appendEntriesResponse = AppendEntriesResponse.newBuilder().setTerm(this.currentTerm).setSuccess(false).build();
                        }

                        if(log.get(appendEntries.getPrevLogIndex()).getTerm() == appendEntries.getPrevLogTerm())

                        



                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                }

            }

        }
    }


    //KEEP
    public Role candidate() throws UnknownHostException, InvalidProtocolBufferException {

        //start election
        this.electionStart = System.nanoTime();
        currentTerm++;
        this.votedFor = InetAddress.getLocalHost().toString();
        resetElectionTimer();

        //send RequestVote RPCs to all other servers
        int tempCurrentTerm = this.currentTerm;
        String tempCandidateId = "";

        try {
            tempCandidateId = InetAddress.getLocalHost().toString(); //who is requesting a vote
        }catch (UnknownHostException e){
            e.printStackTrace();
        }

        int tempLastLogIndex = log.size();
        int tempLastLogTerm = log.get(log.size()).getTerm();

        RequestVote vote = RequestVote.newBuilder()
                .setTerm(tempCurrentTerm)
                .setCandidateId(tempCandidateId)
                .setLastLogIndex(tempLastLogIndex)
                .setLastLogTerm(tempLastLogTerm)
                .build();

        byte[] dataToSend= vote.toByteArray();

        //send RequestVote to all other servers
        for(String destination : listOfNodes) {
            network.sendMessage(destination, 1, dataToSend.length, dataToSend);
        }

        while(true) {

            if (commitIndex > lastAppliedIndex) {
                lastAppliedIndex++;
                //apply(log.get(lastAppliedIndex));
            }

            MessageWrapper message = messages.poll();
            int messageType = message.getMessageType();
            byte[] data = message.getData();

            RequestVote requestVote = null;

            switch (messageType){
                case 1: //RequestVote
                    requestVote = RequestVote.parseFrom(data);
                    break;
                case 2: //AppendEntries
                    break;
                case 3: //RequestVoteResponse
                    break;
                case 4: //AppendEntriesResponse

            }


            //If votes received from majority servers: become leader
            if(this.votesReceivedCount >=  Math.ceil(numberOfNodes / 2)) {
                return Role.LEADER;
            }

            //If AppendEntries RPC received from new leader: convert to follower
            if(requestVote.getCandidateId().equals(leaderId))
                return Role.FOLLOWER;

            //if RPC request or response contains term T > currentTerm: set currentTerm = t, convert to follower
            int termT = requestVote.getTerm();
            if(termT > currentTerm) {
                currentTerm = termT;
                return Role.FOLLOWER; //convert to follower
            }

            //If election timeout elapses: start new election
            if(System.nanoTime() > electionStart)
                return Role.CANDIDATE;

        }
    }






    public void changeRole(Role new_role) {
        if(role == new_role)
            return;

        if(new_role == Role.LEADER) {



            //REPEAT DURING IDLE PERIODS TO PREVENT ELECTION TIMEOUTS

        } else if(new_role == Role.FOLLOWER) {
            this.role = Role.FOLLOWER;

        } else if(new_role == Role.CANDIDATE) {
            this.role = Role.CANDIDATE;
            this.currentTerm++;

            try {
                votedFor = InetAddress.getLocalHost().toString();
                votesReceivedCount++;
            } catch(UnknownHostException e) {
                e.printStackTrace();
            }
            //reset election timer
            resetElectionTimer();


        }
    }

    //receive a message from network class
    public void newMessage(int type, byte[] data) throws InvalidProtocolBufferException {
        MessageWrapper wrapper = new MessageWrapper(type, data);
        messages.add(wrapper);
    }

    //processMessage()

    public long computeElectionTimeout(long min, long max) {
        long diff = max - min;
        long random = (int)((Math.random() * 10000) % diff) + min;
        return random;
    }

    public void startElectionTimer() {

    }

    public void stopElectionTimer() {

    }

    public void resetElectionTimer() {

    }

}