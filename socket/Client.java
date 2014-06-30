package socket;
import javax.microedition.midlet.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import java.io.*;
public class Client implements Runnable, CommandListener {
private Battleship parent;
private Display display;
private Form f;
private Form finalf;
private Form fire;
private StringItem si;
private StringItem d;


private TextField ctb1;
private TextField ctb2;
private TextField ctc1;
private TextField ctc2;

private String cbval1;
private String cbval2;
private String cc1val;
private String cc2val;
private String crecpos;
private TextField cfplace;
private int counter=0;
private String finish;
private String cvalifire;
private String staus;
private String staus1;
private String staus2;
private String staus3;
private String staus4;
private String error;
private String array[][]={{"a0","a1","a2","a3"},{"b0","b1","b2","b3"},{"c0","c1","c2","c3"},{"d0","d1","d2","d3"}};
InputStream isfval;
OutputStream osfval;
String message;

private boolean stop;
private Command sendCommand = new Command("Send", Command.ITEM, 1);
private Command exitCommand = new Command("Exit", Command.EXIT, 1);
private Command okCommand = new Command("Ok", Command.OK, 1);

InputStream is;
OutputStream os;
SocketConnection sc;
Sender sender;

public Client(){}

/* this constructor creates the form for the player to place the battleship and the cruises */


public Client(Battleship m) {
    
    parent = m;
    display = Display.getDisplay(parent);
    f = new Form("Socket Client");
    si = new StringItem("Status:", " ");
    d = new StringItem("Battle: ", "");


    d.setText("Please enter two positions for the battleship");
    ctb1=new TextField("Battleship positions :", "", 4, TextField.ANY);
    ctb2=new TextField("                      ", "", 4, TextField.ANY);
    ctc1=new TextField("Cruise1 position :", "", 4, TextField.ANY);
    ctc2=new TextField("Cruise2 position :", "", 4, TextField.ANY);

    f.append(si);
    f.append(ctb1);
    f.append(ctb2);
    f.append(ctc1);
    f.append(ctc2);


    f.addCommand(exitCommand);
    f.addCommand(okCommand);

    f.setCommandListener(this);
    display.setCurrent(f);
}
/**
* Start the client thread
*/
public void start() {
    Thread t = new Thread(this);
    t.start();
}

public void run() {
try {
    //open socket connection
    sc = (SocketConnection)Connector.open("socket://localhost:5000");
    si.setText("Connected to server");
    is = sc.openInputStream();
    os = sc.openOutputStream();
    // Start the thread for sending messages - see Sender's main comment for explanation
    sender = new Sender(os);
    // Loop forever, receiving data
    f.addCommand(sendCommand);

    
    while (true) {
        StringBuffer sb = new StringBuffer();
        int c = 0;
        while (((c = is.read()) != '\n') && (c != -1)) {
            sb.append((char) c);
        }
        if (c == -1) {
            break;
        }
        // Display message to user
        si.setText("Message received - " + sb.toString());
        crecpos=sb.toString();

     //check is there any ship in this position or game is over 
        checkBtt();
    }
    
    //checkBtt(recpos); 
    stop();
    si.setText("Connection closed");
    f.removeCommand(sendCommand);
} 
catch (ConnectionNotFoundException cnfe) {
    Alert a = new Alert("Client", "Please run Server MIDlet first",
    null, AlertType.ERROR);
    a.setTimeout(Alert.FOREVER);
    a.setCommandListener(this);
    display.setCurrent(a);
} 

catch (IOException ioe) {
    if (!stop) {
        ioe.printStackTrace();
    }
} catch (Exception e) {
    e.printStackTrace();
}
}

public void commandAction(Command c, Displayable s) {
    
    if (c == okCommand ){

           cbval1=ctb1.getString();
           cbval2=ctb2.getString();
           cc1val=ctc1.getString();
           cc2val=ctc2.getString();
       
         //validate inserted values for all four positions
           validate();
         


         //If all positions inserted values correctly it will appear a new text field to insert fire position
           if(counter==4){
               cfplace = new TextField("Fire position :", "", 2, TextField.ANY);
               f.append(cfplace);
               f.setCommandListener(this);
               display.setCurrent( f);

          }
    }    

    if (c == sendCommand){
          cvalifire=cfplace.getString();
      //validate fire position
          validatefire();
          sender.send(cfplace.getString());
    }
    if ((c == Alert.DISMISS_COMMAND) || (c == exitCommand)) {
        parent.notifyDestroyed();
        parent.destroyApp(true);
    }
}

//Validate all four positions which are used to place ships
public void validate(){
    counter =0;
    staus1="";
    staus2="";
    staus3="";
    staus4="";
//validate 1st 
    for(int i=0;i<4;i++){
        for(int j=0;j<4;j++){
            if((array[i][j].charAt(0) == cbval1.charAt(0)) && (array[i][j].charAt(1) == cbval1.charAt(1)) ){
              
               staus1="ok";
                break;
            }
            else if(j==3 && i==3){
                error="You have insert wrong position for Battleship position1"; 
                f.append(error);
            }
        }
        if(staus1.equals("ok")){
           counter ++;
            break;
        }
    }
    
//validate 2nd position
    for(int i=0;i<4;i++){
        for(int j=0;j<4;j++){
            if((array[i][j].charAt(0) == cbval2.charAt(0)) && (array[i][j].charAt(1) == cbval2.charAt(1))){
              
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
    
//validate 3rd position
    for(int i=0;i<4;i++){
        for(int j=0;j<4;j++){
            if((array[i][j].charAt(0) == cc1val.charAt(0)) && (array[i][j].charAt(1) == cc1val.charAt(1))){
              
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
    
//validate 4th position
     for(int i=0;i<4;i++){
        for(int j=0;j<4;j++){
            if((array[i][j].charAt(0) == cc2val.charAt(0)) && (array[i][j].charAt(1) == cc2val.charAt(1))){
              
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
}

//validate fire position
public void validatefire(){
    staus="";
    for(int i=0;i<4;i++){
        for(int j=0;j<4;j++){
             String test2=array[i][j];
            
            
              if((test2.charAt(0) == cvalifire.charAt(0)) && (test2.charAt(1) == cvalifire.charAt(1))){
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
if (sender != null) {
sender.stop();
}
if (is != null) {
is.close();
}
if (os != null) {
os.close();
}
if (sc != null) {
sc.close();
}
} catch (IOException ioe) {}
}

//check whether there is a ship in the received fire position or game is over 
public void checkBtt(){
   
    if((cbval1.equals("0") || cbval2.equals("0")) && cc1val.equals("0") &&  cc2val.equals("0")){
        finish="Game is over. server win";
        f.append(finish);
    }
    else{
        if( ((crecpos.charAt(0) == cbval1.charAt(0)) && (crecpos.charAt(1) == cbval1.charAt(1))) || ((crecpos.charAt(0) == cbval2.charAt(0)) && (crecpos.charAt(1) == cbval2.charAt(1))) ){
         
            cbval1="0";
            cbval2="0";
            ctb1.setString("Fire");
            ctb2.setString("Fire");
            System.out.println("client vl" +  cbval1);
       
        }
  
        else if((crecpos.charAt(0) == cc1val.charAt(0)) && (crecpos.charAt(1) == cc1val.charAt(1))){
            cc1val="0";
            ctc1.setString("Fire");
            System.out.println("client vl" + cc1val);
        }
        else if((crecpos.charAt(0) == cc2val.charAt(0)) && (crecpos.charAt(1) == cc2val.charAt(1))){
            cc2val="0";
            ctc2.setString("Fire");
            System.out.println("client vl" +  cc2val);
        }
            cfplace.setString("");
   
    }

}

public synchronized void clientsend(String msg) {
    message = msg;
    notify();
    System.out.println(message);
}
/*public void clientCheck(String s){
    
}*/
}
