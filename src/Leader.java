import com.reber.raft.AppendEntriesProtos;

public class Leader extends Node {

    /*Upon election: send initial empty AppendEntries RPCs (heartbeat) to each server; repeat
    during idle periods to prevent election timeouts

    If command receieved from client: append entry to local log, respond after entry applied to
    state machine

    If last log index >= nextIndex for a follower: send
    AppendEntries RPC with log entries starting at nextIndex
        If successful: update nextIndex and matchIndex for follower

    */

    //if leaderCommit > follower commit, follower can update
    public void AppendEntries(long term, long leaderId, int prevLogIndex, String[] logEntries, int leaderCommit) {
            //currentTerm for leader to update itself
            //success is true if follower contained entry matching pervLogIndex and prevLogTerm

    }


    public void sendHeartBeat(int term, Address leader) {

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
