package com.mycompany.chatapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Message.java
 *
 * Represents a single chat message and handles validation, hashing,
 * sending, and storing messages for Part 2 of the ChatApp PoE.
 */
public class Message {

    // ---- Fields for THIS message ----
    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;
    private int messageNumber;

    // ---- Shared across ALL Message objects in the session ----
    private static List<Message> sentMessages = new ArrayList<>();
    private static int totalMessagesSent = 0;

    public Message(String recipient, String messageText, int messageNumber) {
        this.messageID = generateMessageID();
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageNumber = messageNumber;
        this.messageHash = createMessageHash(this.messageID, this.messageNumber, this.messageText);
    }

    // ---------------------------------------------------------------
    // Testing constructor: lets JUnit tests supply a FIXED Message ID
    // instead of a random one, so the resulting hash is predictable
    // and can be checked with assertEquals.
    // ---------------------------------------------------------------
    public Message(String fixedMessageID, String recipient, String messageText, int messageNumber) {
        this.messageID = fixedMessageID;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageNumber = messageNumber;
        this.messageHash = createMessageHash(this.messageID, this.messageNumber, this.messageText);
    }

    // ---------------------------------------------------------------
    // Generates a random 10-digit Message ID (not something the user types)
    // ---------------------------------------------------------------
    private String generateMessageID() {
        Random random = new Random();
        long number = Math.abs(random.nextLong() % 10000000000L); // up to 10 digits
        return String.format("%010d", number);
    }

    // ---------------------------------------------------------------
    // 1. checkMessageID()
    // Rule: message ID must be no more than ten characters
    // ---------------------------------------------------------------
    public boolean checkMessageID(String messageID) {
        boolean result;

        if (messageID == null) {
            result = false;
        } else if (messageID.length() > 10) {
            result = false;
        } else {
            result = true;
        }

        return result;
    }

    // ---------------------------------------------------------------
    // 2. checkRecipientCell()
    // Rule: recipient cell number must be no more than ten characters
    // and must contain an international code. Reuses the same approach
    // as Login.checkCellPhoneNumber() from Part 1, per the brief's hint.
    // ---------------------------------------------------------------
    public String checkRecipientCell(String recipientCell) {
        String result;
        boolean isValid;

        if (recipientCell == null) {
            isValid = false;
        } else {
            // Same pattern used in Login.checkCellPhoneNumber() - see that
            // method's comment for the cited regex source.
            String regex = "^\\+27[0-9]{7,10}$";
            isValid = recipientCell.matches(regex);
        }

        if (isValid) {
            result = "Cell phone number successfully captured.";
        } else {
            result = "Cell phone number is incorrectly formatted or does not contain an "
                    + "international code. Please correct the number and try again.";
        }

        return result;
    }

    // ---------------------------------------------------------------
    // Message length check (separate from the hash/ID logic)
    // Rule: message must not exceed 250 characters
    // ---------------------------------------------------------------
    public String checkMessageLength(String messageText) {
        String result;

        if (messageText == null) {
            result = "Please enter a message of less than 250 characters.";
        } else if (messageText.length() > 250) {
            int over = messageText.length() - 250;
            result = "Message exceeds 250 characters by " + over + "; please reduce the size.";
        } else {
            result = "Message ready to send.";
        }

        return result;
    }

    // ---------------------------------------------------------------
    // 3. createMessageHash()
    // Format: first two digits of Message ID + ":" + message number +
    // ":" + first word + last word of the message, all in CAPS.
    // Example: "00:0:HITHANKS"
    // ---------------------------------------------------------------
    public String createMessageHash(String messageID, int messageNumber, String messageText) {
        String result;

        if (messageID == null || messageText == null || messageText.trim().isEmpty()) {
            result = "";
        } else {
            String idPrefix = messageID.substring(0, 2);

            String[] words = messageText.trim().split("\\s+");
            String firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
            String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "").toUpperCase();

            result = idPrefix + ":" + messageNumber + ":" + firstWord + lastWord;
        }

        return result;
    }

    // ---------------------------------------------------------------
    // 4. sentMessage()
    // Lets the user choose to Send, Disregard, or Store the message.
    // ---------------------------------------------------------------
    public String sentMessage(String choice) {
        String result;

        if (choice == null) {
            result = "Invalid option selected.";
        } else if (choice.equalsIgnoreCase("send")) {
            sentMessages.add(this);
            totalMessagesSent++;
            result = "Message successfully sent.";
        } else if (choice.equalsIgnoreCase("disregard") || choice.equalsIgnoreCase("discard")) {
            result = "Press 0 to delete the message.";
        } else if (choice.equalsIgnoreCase("store")) {
            storeMessage();
            result = "Message successfully stored.";
        } else {
            result = "Invalid option selected.";
        }

        return result;
    }

    // 5. printMessages()
    // Returns the full details of every message sent so far this session.
   
    public static String printMessages() {
        StringBuilder sb = new StringBuilder();

        if (sentMessages.isEmpty()) {
            sb.append("No messages have been sent yet.");
        } else {
            for (Message m : sentMessages) {
                sb.append("Message ID: ").append(m.messageID).append("\n");
                sb.append("Message Hash: ").append(m.messageHash).append("\n");
                sb.append("Recipient: ").append(m.recipient).append("\n");
                sb.append("Message: ").append(m.messageText).append("\n");
                sb.append("----------------------------\n");
            }
        }

        return sb.toString();
    }

    
    // 6. returnTotalMessages()
    // Returns the total number of messages sent (not stored/discarded).
    
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    
    // 7. storeMessage() - custom method, researched for JSON storage
    
    private void storeMessage() {
        String json = "{"
                + "\"messageID\":\"" + messageID + "\","
                + "\"recipient\":\"" + recipient + "\","
                + "\"message\":\"" + messageText.replace("\"", "\\\"") + "\","
                + "\"messageHash\":\"" + messageHash + "\""
                + "}";

        try (FileWriter writer = new FileWriter("storedMessages.json", true)) {
            writer.write(json + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Could not store message: " + e.getMessage());
        }
    }

    // ---- Getters, since fields are private ----
    public String getMessageID() {
        return messageID;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }
}