package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Represents a single Message. */
public class Message {
    public String sender, recipient, content, timestamp;

    public Message(String sender, String recipient, String content) {
        this.sender = sender; this.recipient = recipient; this.content = content;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
    public Message(String sender, String recipient, String content, String timestamp) {
        this.sender = sender; this.recipient = recipient; this.content = content;
        this.timestamp = timestamp;
    }

    public void display() {
        System.out.println("----------------------------------------");
        System.out.println("[" + timestamp + "] From: " + sender + " | To: " + recipient);
        System.out.println("  > " + content);
    }
}

