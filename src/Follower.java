public class Follower extends Node {


    /*
        Response to RPCs from candidates and leaders
        If election timeout elapses without receiving AppendEntries RPC from current leader
        or granting vote to candidate: convert to candidate

     */


    public void beginElection() {
        //increment its current term
        //transitions to candidate state
    }

}
