import com.google.protobuf.InvalidProtocolBufferException;
import com.reber.raft.AppendEntriesProtos;
import com.reber.raft.AppendEntriesResponseProtos;
import com.reber.raft.RequestVoteProtos;
import com.reber.raft.RequestVoteResponseProtos;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

//this has everything with sending messages and stuff
public class Network {

    Node node;

    public Network(Node node) {
         this.node = node;
    }

    public ArrayList<String> loadNodes() throws Exception {
        ArrayList<String> nodes = new ArrayList<>();

        File file = new File("nodes.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        while((st = br.readLine()) != null) {
            nodes.add(st);
        }

        return nodes;
    }
    //1 for requestVote, 2 for appendEntries, 3 for requestVoteResponse, 4 for appendEntriesResponse
    public void sendMessage(String destination, int type, byte[] data) throws  InvalidProtocolBufferException {
        //send destination, then type, then size, then data

        //printMessageBeforeConnection(type, data);

        new Thread(() -> {
            try(Socket socket = new Socket(destination, 6666);
                DataOutputStream out =
                    new DataOutputStream(socket.getOutputStream()))
            {
                System.out.println("IN NETWORK CLASS: SENDING MESSAGE TO " + destination);
                out.writeInt(type);
                System.out.println("(NETWORK) SENT TYPE: " + type);
                out.writeInt(data.length);
                out.write(data, 0, data.length);

            } catch(IOException e) {
                System.err.println("Could not establish connection to " + destination + " on port 6666");
            }
        }).start();
    }

    // 1 for requestVote, 2 for appendEntries, 3 for requestVoteResponse, 4 for appendEntriesResponse
    public void listen(int portNumber) throws IOException {

        boolean listening = true;

        Thread t = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
                while (listening) {
                    System.out.println("Listening for connections");
                    new RaftNetThread(serverSocket.accept(), node).start();
                }
            } catch (IOException e) {
                System.err.println("Could not listen on port " + portNumber);
                System.exit(-1);
            }
        });
        t.start();
    }

    public class RaftNetThread extends Thread {
        public Node node;
        public Socket socket;

        public RaftNetThread(Socket socket, Node node) {
            this.socket = socket;
            this.node = node;
        }

        public void run() {
            try (
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    DataInputStream in = new DataInputStream(socket.getInputStream())
            ) {
                int type = in.readInt();
                System.out.println("(NETWORK) READ IN TYPE: " + type);
                int length = in.readInt();

                byte[] payload = new byte[length];

                in.readFully(payload);

                node.newMessage(type, payload);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
