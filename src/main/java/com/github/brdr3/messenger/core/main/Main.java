package com.github.brdr3.messenger.core.main;

import com.github.brdr3.messenger.core.client.MessengerClient;
import com.github.brdr3.messenger.core.server.MessengerServer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws Exception {
        if (System.getProperty("server") != null
                && System.getProperty("server").equals("true")) {
            String userName = System.getProperty("username");
            System.out.println("Server!");
            MessengerServer messengerServer = 
                    new MessengerServer(userName, Boolean.parseBoolean(System.getProperty("debug")));
            messengerServer.run();
        } else {
            if (System.getProperty("username").isEmpty()) {
                throw new Exception("Client username cannot be null");
            } else {
                System.out.println("Client!");
                MessengerClient messengerClient
                        = new MessengerClient(System.getProperty("username"),
                                Integer.parseInt(System.getProperty("clientPort")),
                                System.getProperty("serverHostname"),
                                Integer.parseInt(System.getProperty("serverPort")));
                messengerClient.run();
                messengerClient.debug = Boolean.parseBoolean(System.getProperty("debug"));
            }
        }
    }
}