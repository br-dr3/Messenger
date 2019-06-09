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
    private Map<Object, Message> messageHistory;
    private final Gson gson = new Gson();
    private final Thread receiver;
    private final Thread sender;
    private final User user;
    private final Map<String, Tuple<User, Boolean>> userConnection;
    private final boolean debug;

    public MessengerServer(String username, boolean b) 
            throws UnknownHostException {
        this.user
                = new User("server", InetAddress.getByName("localhost"), port);
        this.messageQueue = new ConcurrentLinkedQueue<>();
        this.messageHistory = new HashMap<>();

        this.receiver = new Thread() {
            @Override
            public void run() {
                receive();
            }
        };

        this.sender = new Thread() {
            @Override
            public void run() {
                send();
            }
        };

        debug = b;

        userConnection = new HashMap<>();
        userConnection.put(user.getUsername(),
                new Tuple<>(user, true));
    }

    public MessengerServer(String username) throws UnknownHostException {
        this(username, false);
    }

    public void receive() {
        DatagramSocket socket;
        DatagramPacket dgPacket;
        byte buffer[] = new byte[10000];
        MessageBuilder mb = new MessageBuilder();

        String jsonMessage;
        Message message;
        Message answer;

        try {
            socket = new DatagramSocket(user.getPort());
            while (true) {
                answer = null;
                dgPacket = new DatagramPacket(buffer, buffer.length,
                        user.getAddress(),
                        user.getPort());
                socket.receive(dgPacket);

                jsonMessage = new String(dgPacket.getData()).trim();
                debugPrint("jsonMessage = " + jsonMessage);
                message = gson.fromJson(jsonMessage, Message.class);
                messageHistory.put("" + message.getId() + " " + message.getTo(), message);
                syncronizedPrint("Message Received!");
                debugPrint("Message: " + message);
                
                if (message.getContent().equals("/test")) {
                    debugPrint("Test Message!");
                    answer = mb.to(message.getFrom())
                            .from(user)
                            .content("Test successfull!")
                            .build();
                    userConnection.put(message.getFrom().getUsername(), 
                                       new Tuple<>(message.getFrom(), true));
                    debugPrint("userConnection: " + userConnection.toString());
                } else if (message.getContent().equals("/exit")) {
                    debugPrint("User " + message.getFrom() + " had logout!");
                    userConnection.put(message.getFrom().getUsername(), 
                                       new Tuple<>(message.getFrom(), false));
                    debugPrint("userConnection: " + userConnection.toString());
                } else if (message.getContent()
                                  .split(" ")
                                  .length == 2
                           && message.getContent()
                                     .split(" ")[0]
                                     .equals("/username")) {
                    userConnection.put(message.getFrom().getUsername(), 
                                       new Tuple<>(message.getFrom(), true));
                    String newUsername = message.getContent().split(" ")[1];
                    Tuple<User, Boolean> tupleNewUser = 
                            userConnection.get(newUsername);
                    if (tupleNewUser == null) {
                        answer = mb.to(message.getFrom())
                               .from(user)
                               .id(new Long(1))
                               .content("Could not find requested user.. "
                                       + "Is it on?")
                               .build();
                        debugPrint("Could not find requested user");
                    } else {
                        User newUser = tupleNewUser.getX();
                        String jsonAnswer = gson.toJson(newUser);
                        answer = mb.to(message.getFrom())
                                   .from(user)
                                   .id(new Long(1))
                                   .content("requestedUser = " + jsonAnswer)
                                   .build();
                        debugPrint("Sended user to client " + message.getFrom());
                    }
                } else if (message.getContent()
                                  .trim()
                                  .split(" ")
                                  .length == 2
                           && message.getContent()
                                     .trim()
                                     .split(" ")[0]
                                     .equals("/getMessages")) {
                    Long missingId = Long.parseLong(message.getContent()
                                                           .trim()
                                                           .split(" ")[1]);
                    
                    System.out.println("\n\n\n\n" + missingId);
                    
                    String messageFromUsername = message.getFrom().getUsername();
                    
                    System.out.println("\n\n\n\n" + messageFromUsername);

                    messageHistory
                           .values()
                           .stream()
                           .filter(m -> m.getTo().getUsername()
                                         .equals(messageFromUsername))
                           .filter(m -> m.getId() >= missingId)
                           .forEach(m -> messageQueue.add(m));
                    continue;
                } else {
                    answer = message;
                    userConnection.put(message.getFrom().getUsername(), 
                            new Tuple<>(message.getFrom(), true));
                    debugPrint("userConnection: " + userConnection.toString());
                }

                if (answer != null 
                    && !answer.getTo().getUsername().equals("server")) {
                    messageQueue.add(answer);
                }
                cleanBuffer(buffer);
            }
        } catch (Exception e) {
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
            while (true) {
                if (!messageQueue.isEmpty()) {
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

                    syncronizedPrint("Message sended to client! ");
                    debugPrint(m.toString());
                    messageHistory.put(m.getId(), m);

                    cleanBuffer(buffer);
                    m = null;
                    jsonMessage = null;
                    packet = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        receiver.start();
        sender.start();
    }

    private void cleanBuffer(byte buf[]) {
        for (int i = 0; i < buf.length; i++) {
            buf[i] = 0;
        }
    }

    private void debugPrint(String s) {
        try {
            if (debug) {
                syncronizedPrint("[DEBUG] - " + s, true, false);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void syncronizedPrint(String s, 
                                  boolean newLine, 
                                  boolean cleanCurrentLine)
            throws InterruptedException {
        synchronized (System.out) {
            if (cleanCurrentLine) {
                System.out.print("\r                         "
                        + "                         \r");
            }

            System.out.print(s);

            if (newLine) {
                System.out.println();
            }
        }
    }

    private void syncronizedPrint(String s) throws InterruptedException {
        syncronizedPrint(s, true, false);
    }
}
