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
    private final int port = 15672;
    private final Queue<Message> messageQueue;
    private Map<Long, Message> messageHistory;
    private final Gson gson = new Gson();
    private final Thread receiver;
    private final Thread sender;
    private final User user;
    private final Map<String, Tuple<User, Boolean>> userConnection;
    
    public MessengerServer(String username) throws UnknownHostException {
        this.user = 
                new User("server", InetAddress.getByName("localhost"), port);
        this.messageQueue = new ConcurrentLinkedQueue<>();
        this.messageHistory = new HashMap<>();
        
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
        
        userConnection = new HashMap<>();
        userConnection.put(user.getUsername(), 
                              new Tuple<>(user, true));
    }
    
    public void receive() {
        DatagramSocket socket;
        DatagramPacket dgPacket;
        byte buffer[] = new byte[10000];
        User senderMessageUser;
        Boolean senderEnable;
        MessageBuilder mb = new MessageBuilder();
        
        String jsonMessage;
        Message message;
        Message answer;
        Tuple<User, Boolean> mapEntry;
        
        try {
            socket = new DatagramSocket(user.getPort());
            while(true) {
                answer = null;
                dgPacket = new DatagramPacket(buffer, buffer.length, 
                                              user.getAddress(), 
                                              user.getPort());
                socket.receive(dgPacket);
                 
                jsonMessage = new String(dgPacket.getData()).trim();
                message = gson.fromJson(jsonMessage, Message.class);
                
                System.out.println("Message Received!");
                System.out.println("Message: " + message);
                senderMessageUser = message.getFrom();
                senderEnable = true;
                
                mapEntry = new Tuple<>(senderMessageUser, senderEnable);
                putIfNotExists(mapEntry);
                
                if(message.getContent().equals("/test")) {
                    answer = mb.to(message.getFrom())
                               .from(user)
                               .content("test")
                               .build();
                } else {
                    answer = null;
                }
                
                if (answer != null)
                   messageQueue.add(answer);
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
                                                m.getTo().getAddress(),
                                                m.getTo().getPort());
                    socket = new DatagramSocket();
                    socket.send(packet);
                    socket.close();
                    
                    System.out.println("Message sended to client: " + m);
                    messageHistory.put(m.getId(), m);
                    
                    cleanBuffer(buffer);
                    m = null;
                    jsonMessage = null;
                    packet = null;
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
    
    private void cleanBuffer(byte buf[]) {
        for(int i = 0; i < buf.length; i++) {
            buf[i] = 0;
        }
    }
    
    private void putIfNotExists(Tuple<User, Boolean> tuple) {
        if(userConnection.containsKey(tuple.getX().getUsername())) {
            userConnection.put(tuple.getX().getUsername(), tuple);
            System.out.println("Added to map: (" + tuple.getX().getUsername() +
                               ", " + tuple + ")");
        }
    }
}
