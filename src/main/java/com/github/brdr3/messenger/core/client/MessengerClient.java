package com.github.brdr3.messenger.core.client;

import com.github.brdr3.messenger.core.util.Message;
import com.github.brdr3.messenger.core.util.Message.MessageBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class MessengerClient {
    public String userName;
    public String serverHost;
    public int serverPort;
    public int clientPort;
    public DatagramSocket socket;
    public DatagramPacket packet;
    private byte buffer[];
    private final Gson gson = new Gson();
    public final Scanner scanner = new Scanner(System.in);
    
    public MessengerClient(String username, int clientPort) {
        this.userName = username;
        this.serverHost = "localhost";
        this.serverPort = 15672;
        this.clientPort = clientPort;
        this.buffer = new byte[10000];
    }
    
    public void run () {
        try {
            InetAddress address = InetAddress.getByName(serverHost);
            String userMessage;
            String jsonMessage;
            Message message;
            while(true) {
                userMessage = null;
                jsonMessage = null;
                while (userMessage == null) {
                    System.out.print("> ");
                    userMessage = scanner.nextLine();
                }
                
                if (userMessage.startsWith("/")) {
                    if(userMessage.equals("/exit")) {
                        break;
                    }
                    else if (userMessage.equals("/help")) {
                        System.out.println("Messenger Help: ");
                        System.out.println("\t/help -> Shows this text.");
                        System.out.println("\t/user:<username> -> change username conversation");
                        System.out.println("\t/exit -> exit messenger");
                        continue;
                    }
                    else {
                        System.out.println("Sorry, couldn't get the command.");
                    }
                }
                
                message = new MessageBuilder().from(userName)
                                              .content(userMessage)
                                              .to("")
                                              .build();
                
                jsonMessage = gson.toJson(message);
                
                buffer = jsonMessage.getBytes();
                packet = new DatagramPacket(buffer, 
                                            buffer.length, 
                                            address, 
                                            serverPort);

                socket = new DatagramSocket();
                socket.send(packet);
                
                System.out.println("Message sent: " + jsonMessage);
                jsonMessage = null;
                message = null;
                userMessage = null;
                
                socket.close();
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }   
}
