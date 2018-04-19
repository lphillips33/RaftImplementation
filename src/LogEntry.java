import java.util.ArrayList;

public class LogEntry {
    private int term;
    private ArrayList<String> commands;


    public int getTerm() {
        return term;
    }

    public ArrayList<String> getCommands() {
        return commands;
    }


    public void setTerm(int term) {
        this.term = term;
    }

    public void setCommands(ArrayList<String> commands) {
        this.commands = commands;
    }
}
