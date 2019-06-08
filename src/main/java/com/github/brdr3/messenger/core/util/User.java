package com.github.brdr3.messenger.core.util;

import java.net.InetAddress;

public class User {
    private String username;
    private InetAddress address;
    private Integer port;
    
    public User(String username, InetAddress address, Integer sendPort) {
        this.username = username;
        this.address = address;
        this.port = sendPort;
    }
    
    public User(String username) {
        this(username, null, null);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return username + "@" + address.getHostName() + ":" + port;
    }
}
