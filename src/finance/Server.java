

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/*
Server is used to receive messages from over processes
 */

public class Server {

    private int id;

    public server(){   
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
                String clientMsg;
                String json = "";
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true); 
                //PARSER
                out.append(json);
            }
            catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}