package messenger.core.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Base64;

public class MessengerServer {
    private String userName;
    public DatagramSocket socket;
    public String serverIP;
    public int serverPort;
    private byte buffer[];            
    private DatagramPacket datagramPacket;
    private InetAddress address;
    
    public MessengerServer(String username) {
        this.userName = username;
        this.serverIP = "localhost";
        this.serverPort = 15672;
        this.buffer = new byte[10000];
    }
    
    public void run () {
        try {
            socket = new DatagramSocket(serverPort);
            while(true) {
                datagramPacket = new DatagramPacket(buffer, buffer.length);

                socket.receive(datagramPacket);
                
                address = datagramPacket.getAddress();
                serverPort = datagramPacket.getPort();
                
                datagramPacket = new DatagramPacket(buffer, 
                                                    buffer.length, 
                                                    address, 
                                                    serverPort);
                
                String clientMessage = new String(datagramPacket.getData(), 
                                                  0, 
                                                  datagramPacket.getLength());
                String clientUsername = 
                        clientMessage.substring(0, clientMessage.indexOf(": "));
                String userMessage = 
                        clientMessage.substring(clientMessage.indexOf(": ")+2,
                                                clientMessage.indexOf("\0"));
                System.out.println("Message Received!");
                System.out.println(clientUsername + ": \"" 
                                 + userMessage + "\"");
                
                for(int i = 0; i < buffer.length; i++) {
                    buffer[i] = 0;
                }
                
                if(userMessage.equals("alo"))
                    break;

            }
            socket.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
