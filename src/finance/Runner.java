import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.HashMap;

/*
Main function for initializing the server client and site.
 */


public class Runner {

    public static void main(String[] args) throws IOException{
        
        ObjectMapper mapper = new ObjectMapper();
        File from = new File("config.txt");
        TypeReference<HashMap<Integer,String>> typeRef
                = new TypeReference<HashMap<Integer,String>>() {};

        HashMap<Integer,String> ips = mapper.readValue(from, typeRef);

        File f = new File("database.json");
        if(f.exists() && !f.isDirectory()) {
            System.out.println("file already exists, recovering data!");
            ObjectMapper siteMapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }else{
        }

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