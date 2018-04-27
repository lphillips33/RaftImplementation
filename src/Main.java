import com.reber.raft.AppendEntriesProtos.AppendEntries;
import com.reber.raft.AppendEntriesResponseProtos.AppendEntriesResponse;
import com.reber.raft.RequestVoteProtos.RequestVote;
import com.reber.raft.RequestVoteResponseProtos.RequestVoteResponse;
import sun.nio.ch.Net;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");

        Node node = new Node();

        node.run();

        Network network = new Network(node);

        //AppendEntries appendEntries = AppendEntries.newBuilder().set

    }
}
