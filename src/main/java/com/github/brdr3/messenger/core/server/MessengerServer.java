package com.github.brdr3.messenger.core.server;

import com.github.brdr3.messenger.core.util.Message;
import com.google.gson.Gson;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class MessengerServer {
    private DatagramSocket socket;
    private String serverIP;
    private int serverPort;
    private byte buffer[];            
    private DatagramPacket datagramPacket;
    private InetAddress address;
    private final Gson gson = new Gson();
    
    private Map<String, Integer> userPortHashTable;
    
    public MessengerServer(String username) {
        this.serverIP = "localhost";
        this.serverPort = 15672;
        this.buffer = new byte[10000];
        
        userPortHashTable = new HashMap<>();
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
                
                String jsonMessage = 
                        new String(datagramPacket.getData()).trim();
                Message message = gson.fromJson(jsonMessage, Message.class);
                System.out.println("Message Received!");
                System.out.println(message.getContent());
                
                for(int i = 0; i < buffer.length; i++) {
                    buffer[i] = 0;
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
