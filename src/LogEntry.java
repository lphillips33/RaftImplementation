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

    public boolean containsCommand(String command) {
        for(int i = 0; i < commands.size(); i++) {
            if(commands.get(i).equals(command)) {
                return true;
            }
        }
        return false;
    }
}
