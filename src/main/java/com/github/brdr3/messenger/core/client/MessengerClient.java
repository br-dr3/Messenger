package com.github.brdr3.messenger.core.client;

import com.github.brdr3.messenger.core.util.Message;
import com.github.brdr3.messenger.core.util.Message.MessageBuilder;
import com.github.brdr3.messenger.core.util.Tuple;
import com.github.brdr3.messenger.core.util.User;
import com.google.gson.Gson;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class MessengerClient {
    public User user;
    public User server;
    public User to;
    private final Gson gson = new Gson();
    public final Scanner scanner = new Scanner(System.in);
    private final Thread sender;
    private final Thread receiver;
    public boolean debug = false;
    
    public MessengerClient(String username, 
                           int port, 
                           String serverHostname, 
                           int serverPort) 
            throws Exception {
        
        this.server = new User("server", 
                               InetAddress.getByName(serverHostname),
                               serverPort);
        this.user = 
            new User(username, 
                     InetAddress.getByName(InetAddress.getLocalHost()
                                                      .getHostAddress()), 
                     port);
        
        this.receiver = new Thread() {
            @Override
            public void run() {
                receive();
            }
        };
        
        this.sender = new Thread() {
            @Override
            public void run () {
                send();
            }
        };
        
        to = this.server;
    }
    
    public void run () {
        sender.start();
        receiver.start();
    }
    
    public void send() {

        String userMessage;
        String content="";
        MessageBuilder mb = new MessageBuilder();
        
        try {
            byte buffer[] = new byte [10000];
            
            while(true) {
                userMessage = null;
                while (userMessage == null) {
                    printTerminalIdentifier(user);
                    userMessage = scanner.nextLine();
                }

                debugPrint("userMessage = " + userMessage);
                
                if (userMessage.startsWith("/")) {
                    if(userMessage.trim().equals("/exit")) {
                        to = server;
                        content = userMessage.trim();
                    } else if (userMessage.trim().equals("/help")) {
                        System.out.println("Messenger Help: ");
                        System.out.println("\t/help -> Shows this text.");
                        System.out.println("\t/exit -> Exit messenger");
                        System.out.println("\t/username <username> ->"
                                + " Change receiver (default: server)");
                        System.out.println("\t/test -> Send test "
                                + "communication");
                        continue;
                    } else if(userMessage.split(" ").length == 2
                              && userMessage.split(" ")[0]
                                            .equals("/username")) {
                        to = server;
                        if (userMessage.split(" ")[1].equals("server")) {
                            debugPrint("User is conversating with server");
                            continue;
                        } else {
                            content = userMessage;
                        }
                    } else if(userMessage.equals("/test")) {
                        to = server;
                        content = userMessage;
                    } else {
                        System.out.println("Sorry, couldn't get the command.");
                        continue;
                    }
                    userMessage = null;
                } else {
                    content = userMessage;
                }
                
                sendMessage(mb.from(user).to(to).content(content).build());
                
                if(content.equals("/exit")) {
                    syncronizedPrint("ByeBye!");
                    System.exit(0);
                    break;
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void receive() {
        DatagramSocket socket;
        DatagramPacket dgPacket;
        byte buffer[] = new byte[10000];
        
        String jsonMessage;
        Message message;
        
        try {
            socket = new DatagramSocket(user.getPort());
            while(true) {
                dgPacket = new DatagramPacket(buffer, buffer.length, 
                                              user.getAddress(), 
                                              user.getPort());
                socket.receive(dgPacket);
                
                jsonMessage = new String(dgPacket.getData()).trim();
                message = gson.fromJson(jsonMessage, Message.class);
                
                if(debug) {
                    debugPrint("Message Received: " + message);
                }
                
                if(message.getFrom().getUsername().equals("server")
                   && message.getContent().startsWith("requestedUser = ")) {
                    debugPrint("Server answer: " +
                               message.getContent().substring("requestedUser = "
                                                                    .length()));
                    to = gson.fromJson(message.getContent()
                                              .substring("requestedUser = "
                                                                     .length()), 
                                       User.class);
                    debugPrint("Changed conversation: new User {" + to + "}");
                }
                
                printTerminalIdentifier(message.getFrom());
                syncronizedPrint(message.getContent(), true, false);
                printTerminalIdentifier(user);
                
                cleanBuffer(buffer);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private void cleanBuffer(byte buf[]) {
        for(int i = 0; i < buf.length; i++) {
            buf[i] = 0;
        }
    }
    
    private void sendMessage(Message m) throws Exception {
        String jsonMessage = new Gson().toJson(m);
        byte buffer[] = new byte[10000];
        DatagramSocket socket;
        DatagramPacket packet;

        buffer = jsonMessage.getBytes();
        packet = new DatagramPacket(buffer, 
                                    buffer.length, 
                                    server.getAddress(),
                                    server.getPort());

        socket = new DatagramSocket();
        socket.send(packet);
        
        if(debug) {
            debugPrint("Message sent: " + jsonMessage);
        }
        
        cleanBuffer(buffer);
        socket.close();
    }
    
    private void syncronizedPrint(String s, 
                                  boolean newLine, 
                                  boolean cleanCurrentLine) 
             throws InterruptedException {
        synchronized(System.out) {
            if(cleanCurrentLine)
                System.out.print("\r                          "
                        + "                       \r");
                System.out.print(s);
            if(newLine)
                System.out.println();
        }
    }
    
    private void syncronizedPrint(String s) throws InterruptedException {
        syncronizedPrint(s, true, true);
    }
    
    private void printTerminalIdentifier(User u) throws Exception {
        syncronizedPrint(u.toString(), false, false);
        
        if (!to.getUsername().equals("server")) {
            syncronizedPrint(" -> " + to + " > ", false, false);
        } else {
            syncronizedPrint(" > ", false, false);
        }
    }
    
    private void debugPrint(String s) {
        try {
            if(debug) {
                syncronizedPrint("[DEBUG] - " + s, true, true);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}