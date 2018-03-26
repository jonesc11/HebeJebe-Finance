package finance;

import java.io.IOException;

/*
Main function for initializing the server client and site.
 */


public class Runner {

    public static void main(String[] args) throws IOException{

        startServer();
    }

   

    public static void startServer(){
        (new Thread() {
            @Override
            public void run() {
 
                new Server().run();
            }
        }).start();
    }
}