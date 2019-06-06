package messenger.core;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class MessengerClient {
    public String userName;
    public String serverHost;
    public int serverPort;
    public Socket socket;
    public final Scanner scanner  = new Scanner(System.in);
    
    public MessengerClient(String username) {
        this.userName = username;
        this.serverHost = "localhost";
        this.serverPort = 15672;
    }
    
    public void run () {
        try {
            InetAddress address = InetAddress.getByName(serverHost);
            while(true) {
                String userMessage = scanner.nextLine();
                
                if (userMessage.equals("/exit"))
                    break;
                
                socket = new Socket(address, serverPort);

                OutputStream outputStream = socket.getOutputStream();
                OutputStreamWriter outputStreamWriter = 
                        new OutputStreamWriter(outputStream);
                BufferedWriter bufferedWriter = 
                        new BufferedWriter(outputStreamWriter);
                
                String message = userName + ": " + userMessage;
                
                bufferedWriter.write(message);
                bufferedWriter.flush();

                System.out.println("Message sent: " + message);
                socket.close();
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
