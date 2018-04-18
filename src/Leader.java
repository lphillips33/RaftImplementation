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

    public void AppendEntries(long term, long leaderId, int prevLogIndex, String[] logEntries, int leaderCommit) {
            //currentTerm for leader to update itself
            //success is true if follower cointained entry matching pervLogIndex and prevLogTerm

    }


    public void sendHeartBeat(int term, Address leader) {

    }
}
