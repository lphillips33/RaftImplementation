
//this is a test!!!!!!!

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Node {

    private Role role;
    private String nodeId;
    private int currentTerm;
    private String votedFor;
    private ArrayList<LogEntry> log;
    private int commitIndex;
    private int lastAppliedIndex;
    private int[] nextIndex;
    private int[] matchIndex;
    Network network;
    private int votesReceivedCount; // need this for candidates method
    private static int numberOfNodes; //need this for candidates method;
    private long lastTimeReceievedMessageFromLeader;
    private long electionTimeout;
    private ArrayList<String> listOfNodes;

    ConcurrentLinkedQueue<>
    //When servers start up, they begin as followers

    public enum Role {
        FOLLOWER, LEADER, CANDIDATE
    }

    public Node() {
        network = new Network();
        this.role = Role.FOLLOWER;
        this.currentTerm = 0;
        this.votedFor = "0";
        this.log = new ArrayList<LogEntry>();
        this.commitIndex = 0;
        this.lastAppliedIndex = 0;
        this.nextIndex = null;
        this.matchIndex = null;
        numberOfNodes = 0;
        this.lastTimeReceievedMessageFromLeader = 0;
        this.electionTimeout = computeElectionTimeout(1, 5);
        this.listOfNodes = network.loadNodes();

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

    /*Upon election: send initial empty AppendEntries RPCs (heartbeat) to each server; repeat
    during idle periods to prevent election timeouts

    If command receieved from client: append entry to local log, respond after entry applied to
    state machine

    If last log index >= nextIndex for a follower: send
    AppendEntries RPC with log entries starting at nextIndex
        If successful: update nextIndex and matchIndex for follower

    */

    //How to get message?

    public Role leader() {

        //network.sendMessage();

        if(commitIndex > lastAppliedIndex) {
            lastAppliedIndex++;
            apply(log[lastAppliedIndex]);
        }

        //if RPC request or response contains term T > currentTerm:
        //set currentTerm = t, convert to follower
        if(t > currentTerm) {
            currentTerm = t;
            role = Role.FOLLOWER; //convert to follower
        }

        return role;

    }

    public Role follower() {


        if(commitIndex > lastAppliedIndex) {
            lastAppliedIndex++;
            apply(log.get(lastAppliedIndex));
        }

        //if RPC request or response contains term T > currentTerm:
        //set currentTerm = t, convert to follower
        if(t > currentTerm) {
            currentTerm = t;
            role = Role.FOLLOWER; //convert to follower
        }

        // Response to RPCs from candidates and leaders

       // If election timeout elapses without receiving AppendEntries RPC from current leader or granting vote to candidate: convert to candidate

        if(System.nanoTime() - lastTimeReceievedMessageFromLeader > electionTimeout) {
            changeRole(Role.CANDIDATE);
        }



        //election timeout?

        return role;

    }


    /*
        On conversion to candidate, start election:
            Increment currentTerm
            Vote for self
            Reset election timer
            Send RequestVote RPCs to all other servers
            If Votes receieved from majority of servers: become leader
            If AppendEntries RPC recieved from new leader: convert to follower
            If election timeout elapses: start new election

     */

    public Role candidate() {

        if(commitIndex > lastAppliedIndex) {
            lastAppliedIndex++;
            apply(log.get(lastAppliedIndex));
        }

        //HOW TO ACCESS RPC REQUEST?

        //if RPC request or response contains term T > currentTerm:
        //set currentTerm = t, convert to follower
        if(t > currentTerm) {
            currentTerm = t;
            role = Role.FOLLOWER; //convert to follower
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

            //send to all other servers

            for(String destination : listOfNodes) {
                Network.sendRequestVote(destination, tempCurrentTerm, tempCandidateId, tempLastLogIndex, tempLastLogTerm);

            }

            
            //If votes received from majority servers: become leader
            if(votesReceivedCount >=  Math.ceil(numberOfNodes / 2)) {
                role = Role.LEADER;
            }

            //If AppendEntries RPC receieved from new leader: convert to follower



        }
    }


    public long computeElectionTimeout(long min, long max) {
        long diff = max - min;
        long random = (int)((Math.random() * 10000) % diff) + min;
        return random;
    }

    public void resetElectionTimer() {

    }

    public String getVotedFor() {
        return votedFor;
    }

}
