package com.github.brdr3.messenger.core.util;

import com.google.gson.Gson;

public class Message {
    private Long id;
    private String content;
    private User to;
    private User from;
    
    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    
    public static class MessageBuilder {
         
        Message m;
        
        public MessageBuilder() {
            m = new Message();
        }
        
        public MessageBuilder id(Long id) {
            m.setId(id);
            return this;
        }
        
        public MessageBuilder to(User to) {
            m.setTo(to);
            return this;
        }
        
        public MessageBuilder from(User from) {
            m.setFrom(from);
            return this;
        }
        
        public MessageBuilder content(String content) {
            m.setContent(content);
            return this;
        }
        
        public Message build() {
            return m;
        }
    }
}
