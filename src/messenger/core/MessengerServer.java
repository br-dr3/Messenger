package messenger.core;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MessengerServer {
    private String userName;
    public static Socket socket;
    public String serverIP;
    public int serverPort;
    
    public MessengerServer(String username) {
        this.userName = username;
        this.serverIP = "localhost";
        this.serverPort = 15672;
    }
    
    public void run () {
        try {
            ServerSocket serverSocket = new ServerSocket(this.serverPort);
            
            while(true) {
                socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                InputStreamReader inputStreamReader = 
                        new InputStreamReader(inputStream);
                BufferedReader bufferedReader = 
                        new BufferedReader(inputStreamReader);
                String clientMessage = bufferedReader.readLine();
                System.out.println("Message Received: " + clientMessage);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
