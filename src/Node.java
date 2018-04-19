import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.reber.raft.RequestVoteProtos.requestVote;


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

    ConcurrentLinkedQueue<>
    ConcurrentLinkedQueue<byte[]> //this will hold our messages


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

        try{
            this.nodeId = InetAddress.getLocalHost().toString();
        } catch(UnknownHostException e){
            e.printStackTrace();
        }

    }

    //All Servers:

    public void run(int t) {

        //increment votes recieved

        while (true) {

            //dequeue here, send it correctly

            switch (role) {

                case LEADER:
                    role = leader();
                case FOLLOWER:
                    role = follower();
                case CANDIDATE:
                    role = candidate();
            }



            //if currentTime - lastRecievedTime > timeOut
        }


    }

    //How to get message?

    public Role leader() {

        //network.sendMessage();

        if(commitIndex > lastAppliedIndex) {
            lastAppliedIndex++;
            //apply(log.get(lastAppliedIndex));
        }

        //if RPC request or response contains term T > currentTerm:
        //set currentTerm = t, convert to follower
        if(t > currentTerm) {
            currentTerm = t;
            changeRole(Role.FOLLOWER); //convert to follower

        }

        return role;

    }

    public Role follower() {


        if(commitIndex > lastAppliedIndex) {
            lastAppliedIndex++;
            //apply(log.get(lastAppliedIndex));
        }

        //if RPC request or response contains term T > currentTerm:
        //set currentTerm = t, convert to follower
        if(t > currentTerm) {
            currentTerm = t;
            changeRole(Role.FOLLOWER); //convert to follower
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

    public Role candidate() {

        if(commitIndex > lastAppliedIndex) {
            lastAppliedIndex++;
            //apply(log.get(lastAppliedIndex));
        }

        //HOW TO ACCESS RPC REQUEST?

        //if RPC request or response contains term T > currentTerm:
        //set currentTerm = t, convert to follower
        if(t > currentTerm) {
            currentTerm = t;
            changeRole(Role.FOLLOWER); //convert to follower
        }

        //If AppendEntries RPC received from new leader: convert to follower

        //If election timeout elapses: start new election

        return role;
    }


    public void changeRole(Role new_role) {
        if(role == new_role)
            return;

        if(new_role == Role.LEADER) {

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

            requestVote vote = requestVote.newBuilder()
                    .setTerm(tempCurrentTerm)
                    .setCandidateId(tempCandidateId)
                    .setLastLogIndex(tempLastLogIndex)
                    .setLastLogTerm(tempLastLogTerm)
                    .build();


            byte[] data = vote.toByteArray();

            //send RequestVote to all other servers
            //1 for requestVote, 2 for appendEntries, 3 for requestVoteResponse, 4 for appendEntriesResponse
            for(String destination : listOfNodes) {
                network.sendMessage(destination, 1, data.length, data);
            }

            //If votes received from majority servers: become leader
            if(votesReceivedCount >=  Math.ceil(numberOfNodes / 2)) {
                role = Role.LEADER;
            }

            //If AppendEntries RPC received from new leader: convert to follower

        }
    }

    //recieve a message
    public void newMessage(int type, byte[] data) {
        //convert data to proper proto type, add it to queue
        //1 for requestVote, 2 for appendEntries, 3 for requestVoteResponse, 4 for appendEntriesResponse
        switch (type){
            case 1: //requestVote
                break;
            case 2: //AppendEntries
                break;
            case 3: //RequestVoteResponse
                break;
            case 4: //AppendEntriesResponse
                break;

        }

    }

    public long computeElectionTimeout(long min, long max) {
        long diff = max - min;
        long random = (int)((Math.random() * 10000) % diff) + min;
        return random;
    }

    public void resetElectionTimer() {

    }

}