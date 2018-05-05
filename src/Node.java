import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import java.util.LinkedList;

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
        this.commitIndex = -1; //we start at 0, the book starts at 1
        this.lastAppliedIndex = 0;
        this.nextIndex = null;
        this.matchIndex = null;
        this.electionTimeout = computeElectionTimeout(350000000, 150000000);
        this.leaderId = "0";


        try {
            String fullIP = InetAddress.getLocalHost().toString();
            this.nodeId = fullIP.substring(fullIP.lastIndexOf("/") + 1);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        network = new Network(this); //want this after all of the instance data is declared.
        this.listOfNodes = network.loadNodes();
        numberOfNodes = this.listOfNodes.size();

        this.network.listen(6666);

        messages = new ConcurrentLinkedQueue<MessageWrapper>();
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
        AppendEntries appendEntriesHeartbeat = AppendEntries.newBuilder().addEntries(AppendEntries.Entry.newBuilder().setTermNumber(this.currentTerm).setMessage("")).build();

        byte[] dataToSend = appendEntriesHeartbeat.toByteArray();

        //Once a candidate wins an election, it becomes leader.  It then sends
        //heartbeat messages to all of the other servers to establish its authority and prevent new elections
        for (String destination : listOfNodes) {
            System.out.println("I AM A LEADER: SENDING HEARTBEAT TO ALL OTHER SERVERS " + destination);
            network.sendMessage(destination, 2, dataToSend);
        }

        while (true) {

            if (commitIndex > lastAppliedIndex) {
                lastAppliedIndex++;
                //apply(log.get(lastAppliedIndex));
            }

            if (messages != null && !messages.isEmpty()) {
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

                lastTimeReceivedMessageFromClient = 0;
            }


            //Leaders send periodic heartbeats(AppendEntries RPCs that carry no log entries) to all followers to maintain their authority
            //We haven't received a message in over a second
            if ((System.nanoTime() - lastTimeReceivedMessageFromClient) > 1000000000) {
                for (String destination : listOfNodes) {
                    System.out.println("I AM A LEADER: SENDING HEARTBEATS TO ALL FOLLOWERS " + destination);
                    network.sendMessage(destination, 2, dataToSend);
                }
            }

        }
    }

    public Role follower() throws UnknownHostException, InvalidProtocolBufferException {

        this.votedFor = null;

        int termT = 0;
        long lastTimeSinceReceivedAppendEntriesFromLeader = 0;
        long lastTimeSinceGrantedVoteToCandidate = 0;
        while (true) {

            //If commitIndex > lastApplied: increment lastApplied, apply log[lastApplied] to state machine
            if (commitIndex > lastAppliedIndex) {
                lastAppliedIndex++;
                //apply(log.get(lastAppliedIndex));
            }

            //if (!messages.isEmpty()) {
            if (messages != null && !messages.isEmpty()) {
                MessageWrapper message = messages.poll();
                int messageType = message.getMessageType();
                byte[] data = message.getData();

                RequestVote requestVote;
                AppendEntries appendEntries = null;

                switch (messageType) {
                    case 1: //RequestVote

                        requestVote = RequestVote.parseFrom(data);
                        int term = requestVote.getTerm();

                        //String destination =  requestVote.getCandidateId();
                        String destination = requestVote.getCandidateId().substring(requestVote.getCandidateId().lastIndexOf("/") + 1);

                        RequestVoteResponse requestVoteResponse = null; //respond to the candidate

                        //Reply false if term < currentTerm
                        if (term < currentTerm) {
                            requestVoteResponse = RequestVoteResponse.newBuilder().setTerm(this.currentTerm).setVoteGranted(false).build();
                        }

                        //If votedFor is null or candidateId, and candidate's log is at least as up-to-data as
                        //receiver's log, grant vote.  not sure about the part after &&
                        if ((this.votedFor == null || votedFor.equals(requestVote.getCandidateId())) && requestVote.getLastLogIndex() >= this.lastAppliedIndex) {
                            requestVoteResponse = RequestVoteResponse.newBuilder().setTerm(term).setVoteGranted(true).build();
                            //this.votedFor = requestVote.getCandidateId();
                            this.votedFor = requestVote.getCandidateId().substring(requestVote.getCandidateId().lastIndexOf("/") + 1);

                            lastTimeSinceGrantedVoteToCandidate = System.nanoTime();
                        }

                        byte[] dataToSend = requestVoteResponse.toByteArray();

                        System.out.println("I am a follower: Sending RequestVote " + destination);
                        network.sendMessage(destination, 3, dataToSend);

                        termT = requestVote.getTerm();

                        break;

                    case 2: //AppendEntries

                        //received AppendEntriesRequest from leader
                        lastTimeSinceReceivedAppendEntriesFromLeader = System.nanoTime();

                        appendEntries = AppendEntries.parseFrom(data);
                        term = appendEntries.getTerm();

                        AppendEntriesResponse appendEntriesResponse = null; //respond to the leader

                        destination = appendEntries.getLeaderId().substring(appendEntries.getLeaderId().lastIndexOf("/") + 1);
                        //destination = appendEntries.getLeaderId();

                        //reply false if term < currentTerm
                        if (term < currentTerm) {
                            appendEntriesResponse = AppendEntriesResponse.newBuilder().setTerm(this.currentTerm).setSuccess(false).build();
                        }

                        //Reply false if log doesn't contain an entry at prevLogIndex whose term matches prevLogTerm
                        if (!(log.get(appendEntries.getPrevLogIndex()).getTerm() == appendEntries.getPrevLogTerm())) {
                            appendEntriesResponse = AppendEntriesResponse.newBuilder().setTerm(this.currentTerm).setSuccess(false).build();
                        }

                        dataToSend = appendEntriesResponse.toByteArray();
                        System.out.println("I am a follower: Sending AppendEntries " + destination);
                        network.sendMessage(destination, 4, dataToSend);

                        //If an existing entry conflicts with a new one(same index but different terms), delete the existing entry and all that follow it


                        //Append any new entries not already in the log
                        List<AppendEntries.Entry> list = appendEntries.getEntriesList();
                        for (int i = 0; i < list.size(); i++) {
                            AppendEntries.Entry tempEntry = list.get(i);
                            String tempMessage = tempEntry.getMessage();

                            if (!(log.get(i).containsCommand(tempMessage))) { //entry not in the log
                                LogEntry logToAdd = new LogEntry();
                                logToAdd.setTerm(currentTerm);
                                logToAdd.setCommands(null);
                                append(logToAdd);
                            }
                        }

                        //If leaderCommit > commitIndex, set commitIndex = min(leaderCommit, index of last new entry
                        if (appendEntries.getLeaderCommit() > this.commitIndex) {
                            commitIndex = Math.min(appendEntries.getLeaderCommit(), appendEntries.getEntriesCount());
                        }

                        break;

                    case 3: //RequestVoteResponse

                        //If RPC request or response contains term T > currentTerm
                        requestVoteResponse = RequestVoteResponse.parseFrom(data);
                        termT = requestVoteResponse.getTerm();
                        System.out.println("I am a follower, I got a RequestVoteResponse");

                        break;
                    case 4: //AppendEntriesResponse
                        //If RPC request or response contains term T > currentTerm
                        appendEntriesResponse = AppendEntriesResponse.parseFrom(data);
                        termT = appendEntriesResponse.getTerm();
                        System.out.println("I am a follower, got a AppendEntriesResponse");
                        break;
                }

                //If RPC request or response contains term T > currentTerm: set currentTerm = T, convert to follower
                termT = appendEntries.getTerm();
                if (termT > currentTerm) {
                    currentTerm = termT;
                    return Role.FOLLOWER;
                }
            }


            /*
             A server remains in follower state as long as it receives valid RPCs from a leader or candidate.
             If a follower receives no communication over a period of time called the election timeout, then it assumes
             there is no viable leader and begins an election to chose a new leader

             */

            //If election timeout elapses without receiving AppendEntries RPC from current leader or granting vote to candidate: convert to candidate
            //no messages.  Compute the current time
            long currentTime = System.nanoTime();
            if (currentTime - lastTimeSinceReceivedAppendEntriesFromLeader > electionTimeout || currentTime - lastTimeSinceGrantedVoteToCandidate > electionTimeout)
                return Role.CANDIDATE; //begin election to chose a new leader
        }
    }

    public Role candidate() throws UnknownHostException, InvalidProtocolBufferException {

        //start election
        currentTerm++; //To begin an election, a follower increments its current term and transitions to candidate state
        this.votedFor = InetAddress.getLocalHost().toString();
        resetElectionTimer();

        //send RequestVote RPCs to all other servers
        int tempCurrentTerm = this.currentTerm;
        //String tempCandidateId = InetAddress.getLocalHost().toString(); //who is requesting a vote
        String tempCandidateId = InetAddress.getLocalHost().toString().substring(InetAddress.getLocalHost().toString().lastIndexOf("/") + 1);

        int tempLastLogIndex = 0;
        int tempLastLogTerm = 0;

        if (log.isEmpty()) {
            tempLastLogIndex = -1;
            tempLastLogTerm = 0;
        } else {
            tempLastLogIndex = log.size();
            tempLastLogTerm = log.get(log.size() - 1).getTerm();
        }

        RequestVote vote = RequestVote.newBuilder()
                .setTerm(tempCurrentTerm)
                .setCandidateId(tempCandidateId)
                .setLastLogIndex(tempLastLogIndex)
                .setLastLogTerm(tempLastLogTerm)
                .build();

        byte[] dataToSend = vote.toByteArray();

        for (String destination : listOfNodes) {
            network.sendMessage(destination, 1, dataToSend);
        }

        while (true) {

            /*

               A candidate continues in this state until one of three things happens: it wins the election, another server
               establishes itself as leader, or a period of time goes by with no winner
             */

            if (commitIndex > lastAppliedIndex) {
                lastAppliedIndex++;
                //apply(log.get(lastAppliedIndex));
            }

            int termT = 0;

            if (messages!= null && !messages.isEmpty()) { //messages != null fixed null pointer exception

                MessageWrapper message = messages.poll();
                int messageType = message.getMessageType();
                byte[] data = message.getData();

                RequestVote requestVote = null;
                AppendEntries appendEntries = null;

                switch (messageType) {
                    case 1: //RequestVote

                        //While waiting for votes, a candidate may receieve an AppendEntries RPC from another server
                        //claiming to be leader.  If the leader's term is at least as large as the candidate's current term,
                        //then the candidate recognizes the leader as legitimate and returns to follower state.  If the term in the RPC
                        //is smaller than the candidate's current term, then the candidate rejects the RPC and continues in candidate state

                        requestVote = RequestVote.parseFrom(data);

                        termT = requestVote.getTerm();


                        System.out.println("I am a candidate received REQUESTVOTE from  " + requestVote.getCandidateId());
                        break;
                    case 2: //AppendEntries
                        appendEntries = AppendEntries.parseFrom(data);

                        int term = appendEntries.getTerm();

                        //If AppendEntries RPC received from new leader: convert to follower
                        if (requestVote.getCandidateId().equals(leaderId))
                            return Role.FOLLOWER;

                        AppendEntriesResponse appendEntriesResponse = null; //respond to the leader
                        String destination = appendEntries.getLeaderId();

                        //reply false if term < currentTerm
                        if (term < currentTerm) {
                            appendEntriesResponse = AppendEntriesResponse.newBuilder().setTerm(this.currentTerm).setSuccess(false).build();
                        }

                        //Reply false if log doesn't contain an entry at prevLogIndex whose term matches prevLogTerm
                        if (!(log.get(appendEntries.getPrevLogIndex()).getTerm() == appendEntries.getPrevLogTerm())) {
                            appendEntriesResponse = AppendEntriesResponse.newBuilder().setTerm(this.currentTerm).setSuccess(false).build();
                        }

                        dataToSend = appendEntriesResponse.toByteArray();
                        System.out.println("I am a candidate: sending APPEND ENTRIES to " + destination);
                        network.sendMessage(destination, 4, dataToSend);

                        //If an existing entry conflicts with a new one(same index but different terms), delete the existing entry and all that follow it

                        //Append any new entries not already in the log
                        List<AppendEntries.Entry> list = appendEntries.getEntriesList();
                        for (int i = 0; i < list.size(); i++) {
                            AppendEntries.Entry tempEntry = list.get(i);
                            String tempMessage = tempEntry.getMessage();

                            if (!(log.get(i).containsCommand(tempMessage))) { //entry not in the log
                                LogEntry logToAdd = new LogEntry();
                                logToAdd.setTerm(currentTerm);
                                logToAdd.setCommands(null);
                                append(logToAdd);
                            }
                        }

                        //If leaderCommit > commitIndex, set commitIndex = min(leaderCommit, index of last new entry
                        if (appendEntries.getLeaderCommit() > this.commitIndex) {
                            commitIndex = Math.min(appendEntries.getLeaderCommit(), appendEntries.getEntriesCount());
                        }

                        break;
                    case 3: //RequestVoteResponse

                        RequestVoteResponse requestVoteResponse = RequestVoteResponse.parseFrom(data);

                        boolean gotAVote = requestVoteResponse.getVoteGranted();

                        System.out.println("I am a candidate: " + " RECEIVED REQUESTVOTERESPONSE");
                        if (gotAVote)
                            votesReceivedCount++;


                        break;
                    case 4: //AppendEntriesResponse
                        System.out.println("I am a candidate: " + " RECEIVED APPENDENTRIESRESPONSE");
                        break;

                }

            }


            //If votes received from majority servers: become leader
            if (this.votesReceivedCount >= Math.ceil(numberOfNodes / 2)) {
                System.out.println("I HAVE BEEN ELECTED LEADER");
                return Role.LEADER;
            }

            //if RPC request or response contains term T > currentTerm: set currentTerm = t, convert to follower
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
        System.out.println("ADDING MESSAGE TO QUEUE OF TYPE: " + type + " BUT GOT " + wrapper.getMessageType());
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

    public void append(LogEntry entry) {
        log.add(entry);
    }
}