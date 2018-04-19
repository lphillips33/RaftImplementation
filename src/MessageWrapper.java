
//1 for requestVote, 2 for appendEntries, 3 for requestVoteResponse, 4 for appendEntriesResponse
public class MessageWrapper {
    private int messageType;
    private byte[] data;

    public MessageWrapper(int messageType, byte[] data) {
        this.messageType = messageType;
        this.data = data;
    }

    public int getMessageType() {
        return messageType;
    }

    public byte[] getData() {
        return data;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
