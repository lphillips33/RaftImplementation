import com.reber.raft.AppendEntriesProtos;

public class Leader extends Node {

    //Upon election:


    public void AppendEntries(long term, long leaderId, int prevLogIndex, String[] logEntries, int leaderCommit) {
            //currentTerm for leader to update itself
            //success is true if follower cointained entry matching pervLogIndex and prevLogTerm
    }


    public void sendHeartBeat(int term, Address leader) {

    }
}
