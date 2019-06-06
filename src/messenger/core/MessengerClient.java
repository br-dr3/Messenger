package messenger.core;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MessengerClient {
    public String userName;
    public String serverHost;
    public int serverPort;
    public Socket socket;
    
    public MessengerClient(String username) {
        this.userName = username;
        this.serverHost = "localhost";
        this.serverPort = 15672;
    }
    
    public void run () {
        try {
            InetAddress address = InetAddress.getByName(serverHost);
            socket = new Socket(address, serverPort);
            
            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter outputStreamWriter = 
                    new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = 
                    new BufferedWriter(outputStreamWriter);
            String message = "Hi, I'm " + userName;
            bufferedWriter.write(message);
            bufferedWriter.flush();
            
            System.out.println("Message sent: " + message);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
