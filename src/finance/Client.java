import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

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
    PrintWriter outt;
    private int id;

    public static void clientrun(){
        try{
            client client = new client();
            client.run();  
        }
        catch (IOException e) {
            System.out.println(e);
        }   
    }
    public client(){
    }

    public void run() throws IOException  {

        int socketNumber = 9235;
        Socket socket = new Socket("localhost", socketNumber);
        outt = new PrintWriter(socket.getOutputStream(), true);
        while(true) {
            
                socket = sender(socket, outt, tweet);   //send message processed here
                outt = new PrintWriter(socket.getOutputStream(), true);

              //NODE ENTRIES
        }
        //Send to server aka api
        Message msg = site.sendmsg(twt, key);
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(msg);
        }catch (MismatchedInputException e){

        }
        jsonString.replace("\n", "");
        socket = new Socket(InetAddress.getByName(serverAddress), socketNumber);
        out = new PrintWriter(socket.getOutputStream(), true);
        out.append(jsonString);
        System.out.println("Connected to process " + key + " at " + serverAddress);
        closeSocket(socket, out);  
   
    }


    public void closeSocket(Socket socket, PrintWriter out){
        //closes all streams out and socket
        try{
            out.close();
            socket.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }        
    } 
}