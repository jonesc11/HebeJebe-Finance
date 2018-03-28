package finance;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONException;

/*
Server is used to receive messages from over processes
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

    public class Handler extends Thread{
        private BufferedReader in;
        private PrintWriter out;
        private Socket socket;
        
        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
            	System.out.println("Connected to client...");
            	while (true) {
	        		String request;
	                String response;
	                
	                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	                request = in.readLine();
	                out = new PrintWriter(socket.getOutputStream(), true);
	                
	                System.out.println("Read: " + request);
	                
	                try {
	                	response = Runner.processRequest (request);
	                	System.out.println("Responding: " + response);
	                	out.println(response);
	                } catch (JSONException e) {
	                	e.printStackTrace();
	                	String errorMessage = "{\"ErrorMessage\":\"Processing failure.\"}";
	                	out.println (errorMessage);
	                }
	                
            	}
            }
            catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}