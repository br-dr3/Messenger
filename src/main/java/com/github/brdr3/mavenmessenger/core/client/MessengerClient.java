package com.github.brdr3.mavenmessenger.core.client;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class MessengerClient {
    public String userName;
    public String serverHost;
    public int clientPort;
    public DatagramSocket socket;
    public DatagramPacket packet;
    private byte buffer[];
    public final Scanner scanner  = new Scanner(System.in);
    
    public MessengerClient(String username, int clientPort) {
        this.userName = username;
        this.serverHost = "localhost";
        this.clientPort = clientPort;
        this.buffer = new byte[10000];
    }
    
    public void run () {
        try {
            InetAddress address = InetAddress.getByName(serverHost);
            String userMessage = null;
            while(true) {
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
                                
                String message = userName + ": " + userMessage;
                
                buffer = message.getBytes();
                userMessage = null;
                packet = new DatagramPacket(buffer, 
                            buffer.length, 
                            address, 
                            clientPort);

                socket = new DatagramSocket();
                socket.send(packet);
                
                System.out.println("Message sent: " + message);
                socket.close();
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }   
}
