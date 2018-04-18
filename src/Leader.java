import com.reber.raft.AppendEntriesProtos;

public class Leader extends Node {

    

    //if leaderCommit > follower commit, follower can update
    public void AppendEntries(long term, long leaderId, int prevLogIndex, String[] logEntries, int leaderCommit) {
            //currentTerm for leader to update itself
            //success is true if follower contained entry matching pervLogIndex and prevLogTerm

    }


    public void sendHeartBeat(int term, String leader) {

    }

}
