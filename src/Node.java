import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.protobuf.InvalidProtocolBufferException;
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
    private int electionTimeout;
    private ArrayList<String> listOfNodes;
    private long electionStart;
    private String leaderId; //current leader

    ConcurrentLinkedQueue<MessageWrapper> messages; //holds our messages.  This is how we respond.

    public enum Role {
        FOLLOWER, LEADER, CANDIDATE
    }

    public Node() throws Exception {

        this.role = Role.FOLLOWER; //when servers start up, they begin as followers
        this.currentTerm = 0;
        this.votedFor = "0";
        this.log = new ArrayList<LogEntry>();
        this.commitIndex = 0;
        this.lastAppliedIndex = 0;
        this.nextIndex = null;
        this.matchIndex = null;
        this.electionTimeout = computeElectionTimeout(150000000, 350000000);
        numberOfNodes = this.listOfNodes.size();
        this.leaderId = "0";


        try {
            this.nodeId = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        network = new Network(this); //want this after all of the instance data is declared.
        this.listOfNodes = network.loadNodes();
        
    }

    public void run() throws UnknownHostException, InvalidProtocolBufferException {
        while (true) {
            switch (role) {
                case LEADER:
                    role = leader();
                    break;
                case FOLLOWER:
                    role = follower();
                    break;
                case CANDIDATE:
                    role = candidate();
                    break;
            }
        }
    }

    public Role leader() throws InvalidProtocolBufferException {
        long lastTimeReceivedMessageFromClient = 0;

        //upon election: send initial empty AppendEntries RPCs (heartbeat) to each server; repeat during idle periods to prevent election timeouts
        AppendEntries appendEntriesHeartbeat = AppendEntries.newBuilder().addEntries(AppendEntries.Entry.newBuilder().setTermNumber(this.currentTerm).setMessage(null)).build();

        byte[] dataToSend = appendEntriesHeartbeat.toByteArray();

        for (String destination : listOfNodes) {
            network.sendMessage(destination, 2, dataToSend);
        }

        while (true) {

            if (commitIndex > lastAppliedIndex) {
                lastAppliedIndex++;
                //apply(log.get(lastAppliedIndex));
            }

            if (!(messages.isEmpty())){
                MessageWrapper message = messages.poll();
                lastTimeReceivedMessageFromClient = System.nanoTime();
                int messageType = message.getMessageType();
                byte[] data = message.getData();

                RequestVoteResponse response = RequestVoteResponse.parseFrom(data);

                int termT = response.getTerm();
                if (termT > currentTerm) {
                    currentTerm = termT;
                    return Role.FOLLOWER;
                }
            }

            //We haven't received a message in over a second
            if((System.nanoTime() - lastTimeReceivedMessageFromClient) > 1000000000) {
                for (String destination : listOfNodes) {
                    network.sendMessage(destination, 2, dataToSend);
                }
            }
        }
    }

    public Role follower() throws UnknownHostException, InvalidProtocolBufferException {

        this.votedFor = null;

        int termT = 0;
        long lastTimeSinceReceivedAppendEntriesFromLeader = 0;
        long lastTimeSinceReceivedRequestVoteFromCandidate = 0;
        while (true) {

            //If commitIndex > lastApplied: increment lastApplied, apply log[lastApplied] to state machine
            if (commitIndex > lastAppliedIndex) {
                lastAppliedIndex++;
                //apply(log.get(lastAppliedIndex));
            }

            if (!messages.isEmpty()) {
                MessageWrapper message = messages.poll();
                int messageType = message.getMessageType();
                byte[] data = message.getData();

                RequestVote requestVote = null;
                AppendEntries appendEntries = null;

                switch (messageType) {
                    case 1: //RequestVote

                        lastTimeSinceReceivedRequestVoteFromCandidate = System.nanoTime();

                        requestVote = RequestVote.parseFrom(data);
                        int term = requestVote.getTerm();

                        String destination = requestVote.getCandidateId();

                        this.votedFor = requestVote.getCandidateId();

                        RequestVoteResponse requestVoteResponse = null; //respond to the candidate
                        byte[] dataToSend = null;
                        if (term < currentTerm) {
                            requestVoteResponse = RequestVoteResponse.newBuilder().setTerm(this.currentTerm).setVoteGranted(false).build();
                        }

                        //not sure about the part after &&
                        if ((this.votedFor == null || votedFor.equals(destination)) && requestVote.getLastLogIndex() > this.lastAppliedIndex) {
                            requestVoteResponse = RequestVoteResponse.newBuilder().setTerm(term).setVoteGranted(true).build();
                        }

                        dataToSend = requestVoteResponse.toByteArray();
                        network.sendMessage(destination, 3, dataToSend);

                        termT = requestVote.getTerm();


                        break;

                    case 2: //AppendEntries

                        //received AppendEntriesRequest from leader
                        lastTimeSinceReceivedAppendEntriesFromLeader = System.nanoTime();

                        appendEntries = AppendEntries.parseFrom(data);
                        term = appendEntries.getTerm();

                        AppendEntriesResponse appendEntriesResponse = null; //respond to the leader
                        destination = appendEntries.getLeaderId();

                        //reply false if term < currentTerm
                        if (term < currentTerm) {
                            appendEntriesResponse = AppendEntriesResponse.newBuilder().setTerm(this.currentTerm).setSuccess(false).build();
                        }

                        //Reply false if log doesn't contain an entry at prevLogIndex whose term matches prevLogTerm
                        if (!(log.get(appendEntries.getPrevLogIndex()).getTerm() == appendEntries.getPrevLogTerm())) {
                            appendEntriesResponse = AppendEntriesResponse.newBuilder().setTerm(this.currentTerm).setSuccess(false).build();
                        }

                        dataToSend = appendEntriesResponse.toByteArray();
                        network.sendMessage(destination, 4, dataToSend);

                        //If an existing entry conflcits with a new one(same index but different terms), delete the existing entry and all that follow it

                        //Append any new entries not already in the log


                        //If leaderCommit > commitIndex, set commitIndex = min(leaderCommit, index of last new entry
                        if (appendEntries.getLeaderCommit() > this.commitIndex) {
                            commitIndex = Math.min(appendEntries.getLeaderCommit(), appendEntries.getEntriesCount());
                        }

                        //If RPC request or response contains term T > currentTerm: set currentTerm = T, convert to follower
                        termT = appendEntries.getTerm();
                        if (termT > currentTerm) {
                            currentTerm = termT;
                            return Role.FOLLOWER;
                        }

                        break;
                    case 3: // RequestVoteResponse
                        break;
                    case 4: // AppendEntriesResponse
                        break;
                }
            }

            //If election timeout elapses without receiving AppendEntries RPC from current leader or granting vote to candidate: convert to candidate
            //no messages.  Compute the current time
            long currentTime = System.nanoTime();
            if(currentTime - lastTimeSinceReceivedAppendEntriesFromLeader > electionTimeout || currentTime - lastTimeSinceReceivedRequestVoteFromCandidate > electionTimeout)
                return Role.CANDIDATE;

        }
    }

    public Role candidate() throws UnknownHostException, InvalidProtocolBufferException {

        //start election
        currentTerm++;
        this.votedFor = InetAddress.getLocalHost().toString();
        resetElectionTimer();

        //send RequestVote RPCs to all other servers
        int tempCurrentTerm = this.currentTerm;
        String tempCandidateId = "";

        try {
            tempCandidateId = InetAddress.getLocalHost().toString(); //who is requesting a vote
        } catch (UnknownHostException e) {
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

        byte[] dataToSend = vote.toByteArray();

        //send RequestVote to all other servers
        for (String destination : listOfNodes) {
            network.sendMessage(destination, 1, dataToSend);
        }

        while (true) {

            if (commitIndex > lastAppliedIndex) {
                lastAppliedIndex++;
                //apply(log.get(lastAppliedIndex));
            }

            MessageWrapper message = messages.poll();
            int messageType = message.getMessageType();
            byte[] data = message.getData();

            RequestVote requestVote = null;
            AppendEntries appendEntries = null;

            switch (messageType) {
                case 1: //RequestVote
                    requestVote = RequestVote.parseFrom(data);
                    break;
                case 2: //AppendEntries
                    appendEntries = AppendEntries.parseFrom(data);

                    //If AppendEntries RPC received from new leader: convert to follower
                    if (requestVote.getCandidateId().equals(leaderId))
                        return Role.FOLLOWER;
                    break;
                case 3: //RequestVoteResponse
                    break;
                case 4: //AppendEntriesResponse

            }

            //If votes received from majority servers: become leader
            if (this.votesReceivedCount >= Math.ceil(numberOfNodes / 2)) {
                return Role.LEADER;
            }

            //if RPC request or response contains term T > currentTerm: set currentTerm = t, convert to follower
            int termT = requestVote.getTerm();
            if (termT > currentTerm) {
                currentTerm = termT;
                return Role.FOLLOWER; //convert to follower
            }

            //If election timeout elapses: start new election
            long currentTime = System.nanoTime();
            if ((currentTime - electionStart) > electionTimeout)
                return Role.CANDIDATE; //start a new election
        }
    }

    //receive a message from network class
    public void newMessage(int type, byte[] data) throws InvalidProtocolBufferException {
        MessageWrapper wrapper = new MessageWrapper(type, data);
        messages.add(wrapper);
    }

    //compute random election timeout between 150ms and 350 ms.  150000000, 350000000 are passed in as parameters
    public int computeElectionTimeout(int max, int min) {
        Random r = new Random();
        int result = r.nextInt(max - min) + min;
        return result;
    }

    public void resetElectionTimer() {
        this.electionStart = System.nanoTime();
    }

}