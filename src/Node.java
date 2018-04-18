//this is a test!!!!!!!
abstract public class Node {

    private Role role = Role.FOLLOWER;
    private long currentTerm;
    private int votedFor;
    private String[] log;
    private long commitIndex;
    private int lastAppliedIndex;
    private int[] nextIndex;
    private int[] matchIndex;
    private long leaderId;

    //When servers start up, they begin as followers

    public enum Role {
        FOLLOWER, LEADER, CANDIDATE
    }

    /*
    public Role() {

    }
    */


    //All Servers:

    public void run(int t) {

        if(commitIndex > lastAppliedIndex) {
            lastAppliedIndex++;
            apply(log[lastApplied]);
        }

        //if RPC request or response contains term T > currentTerm:
            //set currentTerm = t, convert to follower
        if(t > currentTerm) {
            currentTerm = t;
            changeRole(Role.FOLLOWER); //convert to follower
        }


    }

    //1 for requestVote, 2 for appendEntries, 3 for requestVoteResponse, 4 for appendEntriesResponse
    public void sendMessage(Address destination, int type, int size, byte[] data) {
        //send destination, then type, then szie, then data
    }


    public abstract Boolean receiveAppendEntries();
        //Reply false if term < currentTerm
        // Reply false if log doesn't contain an entry at prevLogIndex whose term matches prevLogTerm
        //If an existing entry conflicts with a new one (same index but different terms), delete the existing entry and all that follow it
        //Append any new entries not already in the log
        // If leaderCommit > commitIndex, set commitIndex = min(leaderCommit, index of last new entry)



    public abstract Boolean receiveRequestVote();

        //Reply false if term < currentTerm
        // If votedFor is null or candidateId, and candidate's log is at least as up-to-date as receiver's log, grant vote

}
