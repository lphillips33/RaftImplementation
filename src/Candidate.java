public class Candidate extends Node {



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

    public Candidate() {
         Role role = Role.FOLLOWER;
         long currentTerm = 0;
         int votedFor = 0;
         String[] log = null;
         long commitIndex = 0;
         int lastAppliedIndex = 0;
         int[] nextIndex = null;
         int[] matchIndex = null;
    }


    public void RequestVote(int term, int candidateId, int lastLogIndex, int lastLogTerm) {


    }


    @Override
    public Boolean receiveAppendEntries() {
        return null;
    }

    @Override
    public Boolean receiveRequestVote() {
        return null;
    }
}
