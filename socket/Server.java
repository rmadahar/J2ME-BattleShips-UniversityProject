package socket;
import com.sun.midp.lcdui.Text;
import javax.microedition.midlet.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import java.io.*;
import javax.wireless.messaging.Message;

public class Server implements Runnable, CommandListener {
private Battleship parent;
private Display display;
private Form f;
private Form finalf;
private Form fire;
private StringItem si;
private TextField tf;
private Font ff;
private boolean stop;
private Command sendCommand = new Command("Send", Command.ITEM, 1);
private Command exitCommand = new Command("Exit", Command.EXIT, 1);
private Command okCommand = new Command("Ok", Command.OK, 1);

InputStream is;

private TextField stb1;
private TextField stb2;
private TextField stc1;
private TextField stc2;

private String sbval1;
private String sbval2;
private String sc1val;
private String sc2val;

private String srecpos;
private String finish;
private String error;
private String array[][]={{"a0","a1","a2","a3"},{"b0","b1","b2","b3"},{"c0","c1","c2","c3"},{"d0","d1","d2","d3"}};
private TextField sfplace;
private int counter;
private String svalifire;
private String staus;
private String staus1;
private String staus2;
private String staus3;
private String staus4;

InputStream isfval;
OutputStream osfval;
String message;
OutputStream os;
SocketConnection sc;
ServerSocketConnection scn;
Sender sender;
private TextField tableItem;


public Server(){}

/* This constructor creates the form for the player in the server to place the battleship and the cruises */

public Server(Battleship m) {
    parent = m;
    display = Display.getDisplay(parent);
    f = new Form("Socket Server");
    si = new StringItem("Status:", " ");

    stb1=new TextField("Battleship position1 :", "", 4, TextField.ANY);
    stb2=new TextField("Battleship position2 :", "", 4, TextField.ANY);
    stc1=new TextField("Cruise1 position :", "", 4, TextField.ANY);
    stc2=new TextField("Cruise2 position :", "", 4, TextField.ANY);


    ff = Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_PLAIN,Font.SIZE_MEDIUM);
    si.setFont(ff);

    f.append(si);
    f.append(stb1);
    f.append(stb2);
    f.append(stc1);
    f.append(stc2);
   
    f.addCommand(exitCommand);
    f.addCommand(okCommand);
    f.setCommandListener(this);
    display.setCurrent(f);
}
public void start() {
    Thread t = new Thread(this); //Create instance of thread
    t.start();
}

public void run() {
try {
 
    //Open socket connection
scn = (ServerSocketConnection) Connector.open("socket://:5000");     String myAddress = scn.getLocalAddress();
    si.setText("My address: " + myAddress);

    // Wait for a connection.
    sc = (SocketConnection) scn.acceptAndOpen();
    //Get address of socket connection
    String yourAddress = sc.getAddress();
    si.setText("Connected to: " + yourAddress);
    is = sc.openInputStream();
    os = sc.openOutputStream();
    // Create thread to do sending of messages
    sender = new Sender(os);
    // Allow sending of messages only after Sender is created
    f.addCommand(sendCommand);
    while (true) {
        StringBuffer sb = new StringBuffer();
        int c = 0;
        while (((c = is.read()) != '\n') && (c != -1)) {
            sb.append((char) c);
        }
        // Return -1 if connection terminated
        
        
        if (c == -1) {
             break;
        }
        si.setText("Message received - " + sb.toString());
        //Assign received fire position to variable  
        srecpos=sb.toString();
       
        //check is there any ship in this position or game is over 
        checkBtt();
          
    }
    // checkBtt(recpos);
    stop();
    si.setText("Connection is closed");
    f.removeCommand(sendCommand);
    
} catch (IOException ioe) {
    if (ioe.getMessage().equals("ServerSocket Open")) {
    Alert a = new Alert("Server", "Port 5000 is already taken.",
    null, AlertType.ERROR);
    a.setTimeout(Alert.FOREVER);
    a.setCommandListener(this);
    display.setCurrent(a);
    } else {
        if (!stop) {
             ioe.printStackTrace();
        }
    }
} catch (Exception e) {
    e.printStackTrace();
}
}



/* this will invoke the validation when the button is clicked */

public void commandAction(Command c, Displayable s) {
    
    if (c == okCommand  ){
       
       //Assign all positions used to place ships into variables   
       sbval1=stb1.getString();
       sbval2=stb2.getString();
       sc1val=stc1.getString();
       sc2val=stc2.getString();
       
//validate inserted position values 
       validate();
                
/*If user inserted values for all four positions then add new text field to insert fire position*/

 if(counter==4){
           sfplace = new TextField("Fire position :", "", 2, TextField.ANY);
           
           f.append(sfplace);
           f.setCommandListener(this);
           display.setCurrent( f);
           
        }
    }    

    if (c == sendCommand){
        svalifire=sfplace.getString();
	  //validate fire position
        validatefire();
        sender.send(sfplace.getString());

    }
    if ((c == Alert.DISMISS_COMMAND) || (c == exitCommand)) {
        parent.notifyDestroyed();
        parent.destroyApp(true);
    }
}

//validate values in all four positions inserted by user
public void validate(){
    counter=0;
    staus1="";
    staus2="";
    staus3="";
    staus4="";
    System.out.println("conter before: "+counter);
//validate value in 1st positon
    for(int i=0;i<4;i++){
        for(int j=0;j<4;j++){
            if((array[i][j].charAt(0) == sbval1.charAt(0)) && (array[i][j].charAt(1) == sbval1.charAt(1)) ){
                staus1="ok";
                break;
            }
            else if(j==3 && i==3){
                error="You have insert wrong position for Battleship position1"; 
                f.append(error);
            }
        }
        if(staus1.equals("ok")){
            counter++;
            break;
        }
    }
    
//Validate value in 2nd position
    for(int i=0;i<4;i++){
        for(int j=0;j<4;j++){
            if((array[i][j].charAt(0) == sbval2.charAt(0)) && (array[i][j].charAt(1) == sbval2.charAt(1))){
                staus2="ok";
                break;
            }
            else if(j==3 && i==3){
                error="You have insert wrong position for Battleship position2"; 
                f.append(error);
            }
        }
        if(staus2.equals("ok")){
            counter ++;
            break;
        }
    }
  //Validate value in 3rd position
  
    for(int i=0;i<4;i++){
        for(int j=0;j<4;j++){
            if((array[i][j].charAt(0) == sc1val.charAt(0)) && (array[i][j].charAt(1) == sc1val.charAt(1))){
                staus3="ok";
                break;
            }
            else if(j==3 && i==3){
                error="You have insert wrong position for Cruise1 position"; 
                f.append(error);
            }
        }
        if(staus3.equals("ok")){
            counter ++;
            break;
        }
    }

    //Validate value in 4th position

     for(int i=0;i<4;i++){
        for(int j=0;j<4;j++){
            if((array[i][j].charAt(0) == sc2val.charAt(0)) && (array[i][j].charAt(1) == sc2val.charAt(1))){
                staus4="ok";
                break;
            }
            else if(j==3 && i==3){
                error="You have insert wrong position for Cruise2 position"; 
                f.append(error);
            }
        }
        if(staus4.equals("ok")){
            counter ++;
            break;
        }
    }
    System.out.println("conter : "+counter);
}

//validate fire position

public void validatefire(){
   
    staus="";
    for(int i=0;i<4;i++){
        for(int j=0;j<4;j++){
            String test=array[i][j];
            if((test.charAt(0) == svalifire.charAt(0)) && (test.charAt(1) == svalifire.charAt(1))){
               staus="ok";
                break;
            }
            else if((i==3) && (j==3)){
                error="You have insert wrong position for fire"; 
                f.append(error);
            }
        }
        if(staus.equals("ok")){
            break;
        }
       
    }
}
/**
* Close all open streams
*/

public void stop() {
try {
    stop = true;
    if (is != null) {
        is.close();
    }
    if (os != null) {
        os.close();
    }
    if (sc != null) {
        sc.close();
    }
    if (scn != null) {
        scn.close();
    }
} catch (IOException ioe) {}
}

//check whether there is a ship in the receiver fire position or game is   over.
 
public void checkBtt(){

    if((sbval1.equals("0") || sbval2.equals("0")) && sc1val.equals("0") &&  sc2val.equals("0")){
        finish="Game is over. client win";
        f.append(finish);

    }
    else{
        if( ((srecpos.charAt(0) == sbval1.charAt(0)) && (srecpos.charAt(1) == sbval1.charAt(1))) ||  ((srecpos.charAt(0) == sbval2.charAt(0)) && (srecpos.charAt(1) == sbval2.charAt(1)))){

            sbval1="0";
            sbval2="0";
            stb1.setString("Fire");
            stb2.setString("Fire");
            System.out.println("server val"+sbval1);

        }

        else if((srecpos.charAt(0) == sc1val.charAt(0)) && (srecpos.charAt(1) == sc1val.charAt(1))){
            sc1val="0";
            stc1.setString("Fire");
            System.out.println("server val"+sc1val);
        }
        else if((srecpos.charAt(0) == sc2val.charAt(0)) && (srecpos.charAt(1) == sc2val.charAt(1))){
            sc2val="0";
            stc2.setString("Fire");
            System.out.println("server val"+sc2val);
        }
        sfplace.setString("");

    }
}


public synchronized void serversend(String msg) {
    message = msg;
    notify();
    System.out.println(message);
}

}

