package com.github.brdr3.messenger.core.server;

import com.github.brdr3.messenger.core.util.Message;
import com.github.brdr3.messenger.core.util.Message.MessageBuilder;
import com.github.brdr3.messenger.core.util.Tuple;
import com.github.brdr3.messenger.core.util.User;
import com.google.gson.Gson;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessengerServer {
    private DatagramSocket socket;
    private Queue<Message> messageQueue;
    private DatagramPacket dgPacket;
    private InetAddress address;
    private final Gson gson = new Gson();
    private final Thread receiver;
    private final Thread sender;
    private User user;
    
    private Map<String,  
                Tuple<User, Boolean>> userPortHashTable;
    
    public MessengerServer(String username) throws UnknownHostException {
        this.user = new User("server", 
                             InetAddress.getByName("localhost"), 
                             15672);
        this.messageQueue = new ConcurrentLinkedQueue<>();
        this.receiver = new Thread(){
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
        
        this.userPortHashTable = new HashMap<>();
    }
    
    public void receive() {
        byte buffer[] = new byte[10000];
        User senderMessageUser;
        Boolean senderEnable;
        
        try {
            socket = new DatagramSocket(user.getPort());
            while(true) {
                dgPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(dgPacket);
                address = dgPacket.getAddress();
                dgPacket = new DatagramPacket(buffer, buffer.length, 
                                                    address, user.getPort());
                 
                String jsonMessage = new String(dgPacket.getData()).trim();
                Message message = gson.fromJson(jsonMessage, Message.class);
                
                System.out.println("Message Received!");
                System.out.println("Content -> \"" + message.getContent() 
                                                   + "\"");
                
                senderMessageUser = message.getFrom();
                senderEnable = true;
                System.out.println("User -> " + message.getFrom());
                Tuple<User, Boolean> mapEntry = 
                        new Tuple<>(senderMessageUser, senderEnable);
                
                if(userPortHashTable.containsKey(message.getFrom()
                                                        .getUsername())) {
                    userPortHashTable.put(senderMessageUser.getUsername(), 
                                          mapEntry);
                }
                
//                Message answer = new MessageBuilder()
//                                           .to(senderMessageUser)
//                                           .from(user)
//                                           .content("test")
//                                           .build();
//                messageQueue.add(answer);
                cleanBuffer(buffer);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void send() {
        byte buffer[] = new byte[10000];
        Message m;
        String jsonMessage;
        DatagramPacket packet;
        DatagramSocket socket;
        
        try {
            while(true) {
                if(!messageQueue.isEmpty()) {
                    m = messageQueue.poll();
                    jsonMessage = gson.toJson(m);
                    buffer = jsonMessage.getBytes();
                    packet = new DatagramPacket(buffer,
                                                buffer.length,
                                                m.getFrom().getAddress(),
                                                m.getFrom().getPort());
                    socket = new DatagramSocket(user.getPort());
                    socket.send(packet);
                    socket.close();
                    cleanBuffer(buffer);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void run () {
        receiver.start();
        sender.start();
    }
    
    public void cleanBuffer(byte buf[]) {
        for(int i = 0; i < buf.length; i++) {
            buf[i] = 0;
        }
    }
}
