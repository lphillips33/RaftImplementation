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

        printMessageBeforeConnection(type, data);

        new Thread(() -> {
            try(Socket socket = new Socket(destination, 6666);
                DataOutputStream out =
                    new DataOutputStream(socket.getOutputStream()))
            {

                out.write(type);
                out.write(data.length);
                out.write(data, 0, data.length);

            } catch(IOException e) {
                System.err.println("Could not establish connection to " + destination + " on port 6666");
                e.printStackTrace();
            }
        }).start();
    }


    void printMessageBeforeConnection(int type, byte[] payload) throws InvalidProtocolBufferException {
        switch (type) {
            case 1:
                RequestVoteProtos.RequestVote requestVote = RequestVoteProtos.RequestVote.parseFrom(payload);
                System.out.println("BEFORE TRYING TO CONNECT, RECEIVED THE FOLLOWING OF TYPE " +  type);
                System.out.println(requestVote.toString());
            case 2:
                AppendEntriesProtos.AppendEntries appendEntries = AppendEntriesProtos.AppendEntries.parseFrom(payload);
                System.out.println("BEFORE TRYING TO CONNECT, RECEIVED THE FOLLOWING OF TYPE " +  type);
                System.out.println(appendEntries.toString());
            case 3:
                RequestVoteResponseProtos.RequestVoteResponse requestVoteResponse =  RequestVoteResponseProtos.RequestVoteResponse .parseFrom(payload);
                System.out.println("BEFORE TRYING TO CONNECT, RECEIVED THE FOLLOWING OF TYPE " +  type);
                System.out.println(requestVoteResponse.toString());
            case 4:
                AppendEntriesResponseProtos.AppendEntriesResponse appendEntriesResponse = AppendEntriesResponseProtos.AppendEntriesResponse.parseFrom(payload);
                System.out.println("BEFORE TRYING TO CONNECT, RECEIVED THE FOLLOWING OF TYPE " +  type);
                System.out.println(appendEntriesResponse.toString());
        }
    }


    // 1 for requestVote, 2 for appendEntries, 3 for requestVoteResponse, 4 for appendEntriesResponse
    public void listen(int portNumber) throws IOException {

        boolean listening = true;

        Thread t = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
                while (listening) {
                    System.out.println("Listening for conenctions");
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
                int length = in.readInt();

                byte[] payload = new byte[length];
                in.readFully(payload);

                printMessage(type, payload);

                node.newMessage(type, payload);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void printMessage(int type, byte[] payload) throws InvalidProtocolBufferException {
            switch (type) {
                case 1:
                    RequestVoteProtos.RequestVote requestVote = RequestVoteProtos.RequestVote.parseFrom(payload);
                    System.out.println("RECEIVED THE FOLLOWING OF TYPE " +  type);
                    System.out.println(requestVote.toString());
                case 2:
                    AppendEntriesProtos.AppendEntries appendEntries = AppendEntriesProtos.AppendEntries.parseFrom(payload);
                    System.out.println("RECEIVED THE FOLLOWING OF TYPE " +  type);
                    System.out.println(appendEntries.toString());
                case 3:
                    RequestVoteResponseProtos.RequestVoteResponse requestVoteResponse =  RequestVoteResponseProtos.RequestVoteResponse .parseFrom(payload);
                    System.out.println("RECEIVED THE FOLLOWING OF TYPE " +  type);
                    System.out.println(requestVoteResponse.toString());
                case 4:
                    AppendEntriesResponseProtos.AppendEntriesResponse appendEntriesResponse = AppendEntriesResponseProtos.AppendEntriesResponse.parseFrom(payload);
                    System.out.println("RECEIVED THE FOLLOWING OF TYPE " +  type);
                    System.out.println(appendEntriesResponse.toString());
            }
        }



    }

}
