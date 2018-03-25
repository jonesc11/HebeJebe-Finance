import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.HashMap;

/*
TwitterClient is used to send messages from over processes
 */

public class Client{
    PrintWriter out;
    BufferedReader in;
    private int id;

    public client(){
    }

    public void runClient(String json) throws IOException  {

        int socketNumber = 9235;
        Socket socket = new Socket("localhost", socketNumber);
        out = new PrintWriter(socket.getOutputStream(), true); 
        out.append(json);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        closeSocket(socket, out, in);  
    }

    public void closeSocket(Socket socket, PrintWriter out, PrintReader in){
        //closes all streams out and socket
        try{
            out.close();
            in.close();
            socket.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }        
    } 
}