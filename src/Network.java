import com.reber.raft.AppendEntriesProtos;
import com.reber.raft.RequestVoteProtos;
import com.reber.raft.RequestVoteResponseProtos;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

//this has everything with sending messages and stuff
public class Network {

    Node node;

    public Network() {
         node = new Node();
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

        ServerSocket serverSoc = new ServerSocket(666);

        Socket connection = serverSoc.accept();
        System.out.println("Accepting client " + connection.getRemoteSocketAddress().toString());
        DataInputStream is = new DataInputStream(connection.getInputStream());

        //read in the type

        int type = is.readInt();

        switch (type) {
            case 1: //requestVote
                RequestVoteProtos.requestVote requestVoteMessage = RequestVoteProtos.requestVote.parseDelimitedFrom(is);

                System.out.println("From server: " + requestVoteMessage.toString());

                int term = requestVoteMessage.getTerm();
                int currentTerm = 0;

                if(term < currentTerm)
                    return false;

                String votedFor = node.getVotedFor();
                String candidateId = requestVoteMessage.getCandidateId();

                if((votedFor == null || votedFor.equals(candidateId)) && /* candidates log is at least up to date
                 as recievers log */ )
                    //grant vote

                break;
            case 2: //appendEntries
                AppendEntriesProtos.AppendEntries appendEntriesMessage = AppendEntriesProtos.AppendEntries.parseDelimitedFrom(is);
                break;
            case 3: //requestVoteResponse
                RequestVoteResponseProtos.requestVoteResponse requestVoteResponseMessage = RequestVoteResponseProtos.requestVoteResponse.parseDelimitedFrom(is);
                break;
            case 4: // appendEntriesResponse
                AppendEntriesProtos.AppendEntries appendEntriesResponseMessage = AppendEntriesProtos.AppendEntries.parseDelimitedFrom(is);
                break;
        }

    }

}
