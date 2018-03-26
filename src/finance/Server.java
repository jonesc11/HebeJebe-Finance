package finance;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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
        private DataOutputStream out;
        private Socket socket;
        
        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
            	System.out.println("Connected to client...");
            	while (true) {
	        		String clientMsg;
	                String json = "";
	                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	                json = in.readLine();
	                System.out.println("Read: " + json);
	                out = new DataOutputStream(socket.getOutputStream()); 
	                //PARSER
	                out.writeBytes(json);
            	}
            }
            catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}