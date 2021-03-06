package finance;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONException;

/*
Server is used to receive json objects from over processes aka the js 
 */

public class Server {

    public Server(){   
    }

    public void run(){
        try{
            int socketNumber = 9235;
            ServerSocket listener = new ServerSocket(socketNumber);            
            try {
                while (true) {
                    new Handler(listener.accept()).start();
                }
            } 
            finally {
                listener.close();
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    //spawns a server for each instance that the website is called
    public class Handler extends Thread{
        private BufferedReader in;
        private PrintWriter out;
        private Socket socket;
        
        public Handler(Socket socket) {
            this.socket = socket;
        }

        //running the server object and opening listing ports
        public void run() {
            try {
        		String request;
                String response;
                
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                request = in.readLine();
                out = new PrintWriter(socket.getOutputStream(), true);
                
                try {
                	//parsing the messages from json
                	response = Parser.processRequest (request);
                	out.println(response);
                } catch (JSONException e) {
                	e.printStackTrace();
                	String errorMessage = "{\"ErrorMessage\":\"Processing failure.\"}";
                	out.println (errorMessage);
                }
            }
            catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}