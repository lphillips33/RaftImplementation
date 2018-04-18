
//this is a test!!!!!!!

public class Node {

    private Role role;
    private long currentTerm;
    private String votedFor;
    private String[] log;
    private long commitIndex;
    private int lastAppliedIndex;
    private int[] nextIndex;
    private int[] matchIndex;
    private String leaderId; //do we need this?

    //When servers start up, they begin as followers

    public enum Role {
        FOLLOWER, LEADER, CANDIDATE
    }

    public Node() {
        this.role = Role.FOLLOWER;
        this.currentTerm = 0;
        this.votedFor = "0";
        this.log = null;
        this.commitIndex = 0;
        this.lastAppliedIndex = 0;
        this.nextIndex = null;
        this.matchIndex = null;
        this.leaderId = "0";

    }

    //All Servers:

    public void run(int t) {

        while (true) {

            switch (role) {

                case LEADER:
                    role = leader();
                case FOLLOWER:
                    role = follower();

                case CANDIDATE:
                    role = candidate();
            }



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


    public Role leader(int t) {

        Network network = new Network();

        if(commitIndex > lastAppliedIndex) {
            lastAppliedIndex++;
            apply(log[lastAppliedIndex]);
        }

        //if RPC request or response contains term T > currentTerm:
        //set currentTerm = t, convert to follower
        if(t > currentTerm) {
            currentTerm = t;
            changeRole(Role.FOLLOWER); //convert to follower
        }

    }

    public Role follower(int t) {


        if(commitIndex > lastAppliedIndex) {
            lastAppliedIndex++;
            apply(log[lastAppliedIndex]);
        }

        //if RPC request or response contains term T > currentTerm:
        //set currentTerm = t, convert to follower
        if(t > currentTerm) {
            currentTerm = t;
            changeRole(Role.FOLLOWER); //convert to follower
        }


        // Response to RPCs from candidates and leaders

        /*
        If election timeout elapses without receiving AppendEntries RPC from current leader
        or granting vote to candidate: convert to candidate
        */

        //election timeout?

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

    public Role candidate(int t) {

        if(commitIndex > lastAppliedIndex) {
            lastAppliedIndex++;
            apply(log[lastAppliedIndex]);
        }

        //HOW TO ACCESS RPC REQUEST?

        //if RPC request or response contains term T > currentTerm:
        //set currentTerm = t, convert to follower
        if(t > currentTerm) {
            currentTerm = t;
            changeRole(Role.FOLLOWER); //convert to follower
        }

    }

    public String getVotedFor() {
        return votedFor;
    }

}
