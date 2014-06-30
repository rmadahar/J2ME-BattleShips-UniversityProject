package socket;
import javax.microedition.midlet.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import java.io.*;

public class Battleship extends MIDlet implements CommandListener {
    
    private static final String SERVER = "Server";
    private static final String CLIENT = "Client";
    private static final String[] names = {SERVER, CLIENT};
    private static Display display;
    private Form f;
    private ChoiceGroup cg;
    private boolean isPaused;
    private Server server;
    private Client client;
    private Command exitCommand = new Command("Exit", Command.EXIT, 1);
    private Command startCommand = new Command("Start", Command.ITEM, 1);

    /* Constructor to initialize the form */
   
    public Battleship() {
        
        display = Display.getDisplay(this);
        f = new Form("Battleship");
        cg = new ChoiceGroup("Please select peer",
        Choice.EXCLUSIVE, names, null);
        f.append(cg);
        f.addCommand(exitCommand);
        f.addCommand(startCommand);
        f.setCommandListener(this);
        display.setCurrent(f);
    }
    public boolean isPaused() {
        return isPaused;
    }
    public void startApp() {
        isPaused = false;
    }
    public void pauseApp() {
        isPaused = true;
    }
    public void destroyApp(boolean unconditional) {
        if (server != null) {
            server.stop();
        }
        if (client != null) {
            client.stop();
        }
    }

  /* This will be executed when the client press the button */
    
    public void commandAction(Command c, Displayable s) {
        if (c == exitCommand) {
            destroyApp(true);
            notifyDestroyed();
        } else if (c == startCommand) {
            String name = cg.getString(cg.getSelectedIndex());
            if (name.equals(SERVER)) {
                server = new Server(this);
                server.start();
            } else {
                client = new Client(this);
                client.start();
            }
        }
    }
}
















