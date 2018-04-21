import com.reber.raft.AppendEntriesProtos;
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
    public void sendMessage(String destination, int type, int size, byte[] data) {
        //send destination, then type, then szie, then data

        InetAddress address = null;
        Socket socket = null;
        DataOutputStream dOut = null;
        DataInputStream dIn;
        try{
            InetAddress localIp = InetAddress.getLocalHost();
            address = InetAddress.getByName(destination);
            socket = new Socket(destination, 6666);
            dOut = new DataOutputStream(socket.getOutputStream());
            dIn = new DataInputStream(socket.getInputStream());
        } catch(Exception e) {
            e.printStackTrace();
        }

        switch (type) {
            case 1: //requestVote

                try {
                    dOut.write(type);

                    if(data != null)
                        dOut.write(data);
                    else
                        System.out.println("data was empty");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case 2: //appendEntries

                try {
                    dOut.write(type);
                } catch(IOException e) {
                    e.printStackTrace();
                }

                break;

            case 3: //requestVoteResponse

                try {
                    dOut.write(type);
                } catch(IOException e) {
                    e.printStackTrace();
                }

                break;

            case 4: // appendEntriesResponse

                try {
                    dOut.write(type);
                } catch(IOException e) {
                    e.printStackTrace();
                }

                break;

        }


    }


    /*

        APPEND ENTRIES

         //Reply false if term < currentTerm
        // Reply false if log doesn't contain an entry at prevLogIndex whose term matches prevLogTerm
        //If an existing entry conflicts with a new one (same index but different terms), delete the existing entry and all that follow it
        //Append any new entries not already in the log
        // If leaderCommit > commitIndex, set commitIndex = min(leaderCommit, index of last new entry)


        REQUEST VOTE

         //Reply false if term < currentTerm
        // If votedFor is null or candidateId, and candidate's log is at least as up-to-date as receiver's log, grant vote

     */


    // 1 for requestVote, 2 for appendEntries, 3 for requestVoteResponse, 4 for appendEntriesResponse
    public Boolean receiveMessage() throws IOException {

        ServerSocket serverSoc = new ServerSocket(6666);

        Socket connection = serverSoc.accept();
        System.out.println("Accepting client " + connection.getRemoteSocketAddress().toString());
        DataInputStream is = new DataInputStream(connection.getInputStream());

        //read in the type

        int type = is.readInt();

        return true;
    }

}
