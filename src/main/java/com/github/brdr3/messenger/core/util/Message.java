package com.github.brdr3.messenger.core.util;

public class Message {
    private Long id;
    private String content;
    private String to;
    private String from;
    
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
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
    
    public static class MessageBuilder {
         
        Message m;
        
        public MessageBuilder() {
            m = new Message();
        }
        
        public MessageBuilder id(Long id) {
            m.setId(id);
            return this;
        }
        
        public MessageBuilder to(String to) {
            m.setTo(to);
            return this;
        }
        
        public MessageBuilder from(String from) {
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
