import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.reber.raft.AppendEntriesProtos.AppendEntries;
import com.reber.raft.RequestVoteProtos.RequestVote;
import com.reber.raft.RequestVoteResponseProtos;
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
    private String leaderId; //current leader
    private Timer

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

            if(!messages.isEmpty()) {
                tempMessage = messages.poll();

                int type = tempMessage.getMessageType();
                byte[] data = tempMessage.getData();

                switch (type){
                    case 1: //requestVote
                        RequestVote vote = RequestVote.parseFrom(data);
                        break;
                    case 2: //AppendEntries
                        AppendEntries entries = AppendEntries.parseFrom(data);
                        break;
                    case 3: //RequestVoteResponse
                        RequestVoteResponse requestVoteResponse = RequestVoteResponseProtos.RequestVoteResponse.parseFrom(data);
                        break;
                    case 4: //AppendEntriesResponse
                        AppendEntriesResponse appendEntriesResponse = AppendEntriesResponse.parseFrom(data);
                        break;
                }


            }


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

        //might have to add if statements for different types of messages

        if(message != null) {
            // do something

            int type = message.getMessageType();
            byte[] data = message.getData();
            RequestVote vote = RequestVote.parseFrom(data);
            int term = vote.getTerm();


            //network.sendMessage();

            //if RPC request or response contains term T > currentTerm:
            //set currentTerm = t, convert to follower
            if (term > currentTerm) {
                currentTerm = term;
                changeRole(Role.FOLLOWER); //convert to follower

            }

                // IF COMMAND RECEIVED FROM CLIENT: APPEND ENTRY TO LOCAL LOG, RESPOND AFTER ENTRY APPLIED TO STATE MACHINE (COMMAND COMES FROM QUEUE)
        }
        return role;

    }

    public Role follower(MessageWrapper message) throws InvalidProtocolBufferException {

        if(commitIndex > lastAppliedIndex) {
            lastAppliedIndex++;
            //apply(log.get(lastAppliedIndex));
        }

        if(message != null ) {
            // do something

            int type = message.getMessageType();
            byte[] data = message.getData();
            RequestVote vote = RequestVote.parseFrom(data);
            int term = vote.getTerm();

            //if RPC request or response contains term T > currentTerm:
            //set currentTerm = t, convert to follower
            if (term > currentTerm) {
                currentTerm = term;
                changeRole(Role.FOLLOWER); //convert to follower
            }

        }

        // RESPOND TO RPCs FROM CANDIDATES AND LEADERS

        //take a message off of the queue and deal with it

       // If election timeout elapses without receiving AppendEntries RPC from current leader or granting vote to candidate: convert to candidate
        if(System.nanoTime() - lastTimeReceivedAppendEntriesFromLeader > electionTimeout) {
            changeRole(Role.CANDIDATE);
        }

        //election timeout?

        return role;

    }

    public Role candidate(MessageWrapper message) throws InvalidProtocolBufferException{

        if(commitIndex > lastAppliedIndex) {
            lastAppliedIndex++;
            //apply(log.get(lastAppliedIndex));
        }

        if(message != null) {


            int type = message.getMessageType();
            byte[] data = message.getData();

            if(type == 1) { //RequestVote
                RequestVote vote = RequestVote.parseFrom(data);
                int term = vote.getTerm();

                //if RPC request or response contains term T > currentTerm:
                //set currentTerm = t, convert to follower
                if (term > currentTerm) {
                    currentTerm = term;
                    changeRole(Role.FOLLOWER); //convert to follower
                }
            } else if(type == 2) { //AppendEntries
                //If AppendEntries RPC received from new leader: convert to follower

                    AppendEntries tempAppendEntries = AppendEntries.parseFrom(data);
                    String id = tempAppendEntries.getLeaderId();

                // If AppendEntries RPC received from new leader
                if(id.equals(leaderId)){
                    changeRole(Role.FOLLOWER);
                }
            }

        }
            //If election timeout elapses: start new election


        return role;
    }


    public void changeRole(Role new_role) {
        if(role == new_role)
            return;

        if(new_role == Role.LEADER) {

            //upon election: send initial empty AppendEntries RPCs (heartbeat) to each server; repeat during idle periods
            //to prevent election timeouts

            AppendEntries appendEntries = AppendEntries.newBuilder().build();

            byte[] data = appendEntries.toByteArray();

            for(String destination : listOfNodes) {
                network.sendMessage(destination, 2, data.length, data);
            }

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

            //send RequestVote RPCs to all other servers

            int tempCurrentTerm = this.currentTerm;
            String tempCandidateId = "";

            try {
                tempCandidateId = InetAddress.getLocalHost().toString(); //who is requesting a vote
            }catch (UnknownHostException e){
                e.printStackTrace();
            }

            int tempLastLogIndex = log.size();
            int tempLastLogTerm = log.get(log.size()).term;

            RequestVote vote = RequestVote.newBuilder()
                    .setTerm(tempCurrentTerm)
                    .setCandidateId(tempCandidateId)
                    .setLastLogIndex(tempLastLogIndex)
                    .setLastLogTerm(tempLastLogTerm)
                    .build();


            byte[] data = vote.toByteArray();

            //send RequestVote to all other servers
            for(String destination : listOfNodes) {
                network.sendMessage(destination, 1, data.length, data);
            }

            //If votes received from majority servers: become leader
            if(this.votesReceivedCount >=  Math.ceil(numberOfNodes / 2)) {
                changeRole(Role.LEADER);
            }

            //If AppendEntries RPC received from new leader: convert to follower

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