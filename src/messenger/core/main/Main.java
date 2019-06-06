package messenger.core.main;

import messenger.core.MessengerClient;
import messenger.core.MessengerServer;

public class Main {
    public static void main(String[] args) throws Exception {
        if (System.getProperty("server") != null 
               && System.getProperty("server").equals("true")) {
            String userName = System.getProperty("username");
            System.out.println("Server!");
            MessengerServer messengerServer = new MessengerServer(userName);
            messengerServer.run();
        } else {
            if (System.getProperty("username").isEmpty()) {
                throw new Exception("Client username cannot be null");
            } else {
                System.out.println("Client!");
                MessengerClient messengerClient = 
                        new MessengerClient(System.getProperty("username"));
                messengerClient.run();
            }
        }
    }
}
    