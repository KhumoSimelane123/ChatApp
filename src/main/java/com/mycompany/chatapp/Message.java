package com.mycompany.chatapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Message.java
 
  Represents a single chat message. It validates, hashes, sends, disregards
  and stores messages, and keeps the arrays used by the "Stored Messages"
  menu in Part 3 of the ChatApp PoE.
 
  Reference (JSON reading and writing):
  Google (n.d.) Gson: a Java library to convert Java objects into JSON and
  back. Available at: https://github.com/google/gson [Accessed 17 November 2026].
 */
public class Message {

    // ---- Fields for THIS message ----
    private String messageID;
    private String sender;
    private String recipient;
    private String messageText;
    private String messageHash;
    private int messageNumber;

    //  Arrays shared across ALL Message objects in the session 
    private static List<Message> sentMessages = new ArrayList<>();
    private static List<Message> disregardedMessages = new ArrayList<>();
    private static List<Message> storedMessages = new ArrayList<>();
    private static List<String> messageHashes = new ArrayList<>();
    private static List<String> messageIDs = new ArrayList<>();
    private static int totalMessagesSent = 0;

    // Who is sending the messages (set after login) and where JSON is saved
    private static String currentSender = "Developer";
    private static String storageFile = "storedMessages.json";

    public Message(String recipient, String messageText, int messageNumber) {
        this.messageID = generateMessageID();
        this.sender = currentSender;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageNumber = messageNumber;
        this.messageHash = createMessageHash(this.messageID, this.messageNumber, this.messageText);
    }

    
    // Testing constructor: lets JUnit tests supply a FIXED Message ID
    // instead of a random one, so IDs and hashes are predictable.
    
    public Message(String fixedMessageID, String recipient, String messageText, int messageNumber) {
        this.messageID = fixedMessageID;
        this.sender = currentSender;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageNumber = messageNumber;
        this.messageHash = createMessageHash(this.messageID, this.messageNumber, this.messageText);
    }

    
    // Generates a random 10-digit Message ID using string methods:
    // join the random number onto ten zeros, then keep the LAST ten
    // characters with substring().
    
    private String generateMessageID() {
        Random random = new Random();
        String digits = String.valueOf(Math.abs(random.nextLong() % 10000000000L));
        String padded = "0000000000" + digits;
        return padded.substring(padded.length() - 10);
    }

   
    // 1. checkMessageID(): message ID must be no more than ten characters
    
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

    
    // 2. checkRecipientCell(): same rule as Login.checkCellPhoneNumber()
    
    public String checkRecipientCell(String recipientCell) {
        String result;
        boolean isValid;

        if (recipientCell == null) {
            isValid = false;
        } else {
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

   
    // Message length check: must not exceed 250 characters
    
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

   
    // 3. createMessageHash()
    // First two digits of the ID : message number : first word + last word
    // in CAPS, e.g. "00:0:HITONIGHT"
    
    public String createMessageHash(String messageID, int messageNumber, String messageText) {
        String result;

        if (messageID == null || messageID.length() < 2
                || messageText == null || messageText.trim().isEmpty()) {
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

    
    // 4. sentMessage(): the user picks a NUMBER
    //    1 = Send, 2 = Disregard, 3 = Store
   
    public String sentMessage(int choice) {
        String result;

        if (choice == 1) {
            sentMessages.add(this);
            totalMessagesSent++;
            messageIDs.add(messageID);
            messageHashes.add(messageHash);
            result = "Message successfully sent.";
        } else if (choice == 2) {
            disregardedMessages.add(this);
            messageIDs.add(messageID);
            messageHashes.add(messageHash);
            result = "Press 0 to delete the message.";
        } else if (choice == 3) {
            storeMessage();
            messageIDs.add(messageID);
            messageHashes.add(messageHash);
            result = "Message successfully stored.";
        } else {
            result = "Invalid option selected.";
        }

        return result;
    }

   
    // 5. printMessages(): details of every message sent this session
    
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

    
    // 6. returnTotalMessages(): total number of messages sent
    
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

   
    // 7. storeMessage(): custom method. Adds this message to the stored
    // array, then writes the WHOLE array to the JSON file as a JSON array
    // using Gson (see reference at the top of this file).
    
    private void storeMessage() {
        storedMessages.add(this);
        saveStoredMessages();
    }

    // Writes the stored messages array to the JSON file as one JSON array
    private static void saveStoredMessages() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(storageFile)) {
            gson.toJson(storedMessages, writer);
        } catch (IOException e) {
            System.out.println("Could not store message: " + e.getMessage());
        }
    }

    
    // Reads the JSON file back into the stored messages array
    // (Part 3: "read the JSON file you stored into an array")
    
    public static void loadStoredMessages() {
        storedMessages.clear();
        File file = new File(storageFile);

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Message[] loaded = new Gson().fromJson(reader, Message[].class);

                if (loaded != null) {
                    for (Message m : loaded) {
                        storedMessages.add(m);
                        messageIDs.add(m.messageID);
                        messageHashes.add(m.messageHash);
                    }
                }
            } catch (IOException | JsonParseException e) {
                System.out.println("Could not read stored messages: " + e.getMessage());
            }
        }
    }

    // ===============================================================
    // PART 3: Stored Messages menu features
    

    // The text of every message sent, in the order they were sent
    public static List<String> getSentMessageTexts() {
        List<String> texts = new ArrayList<>();

        for (Message m : sentMessages) {
            texts.add(m.messageText);
        }

        return texts;
    }

    // a. Sender and recipient of all stored messages
    public static String displaySenderAndRecipient() {
        StringBuilder sb = new StringBuilder();

        if (storedMessages.isEmpty()) {
            sb.append("No stored messages.");
        } else {
            for (Message m : storedMessages) {
                sb.append("Sender: ").append(m.sender).append("\n");
                sb.append("Recipient: ").append(m.recipient).append("\n");
                sb.append("----------------------------\n");
            }
        }

        return sb.toString();
    }

    // b. The longest stored message (compares lengths one by one)
    public static String displayLongestMessage() {
        String result;

        if (storedMessages.isEmpty()) {
            result = "No stored messages.";
        } else {
            Message longest = storedMessages.get(0);

            for (Message m : storedMessages) {
                if (m.messageText.length() > longest.messageText.length()) {
                    longest = m;
                }
            }

            result = longest.messageText;
        }

        return result;
    }

    // c. Search for a message ID. Returns the message, or null if not found.
    public static Message searchByMessageID(String id) {
        Message found = null;

        for (Message m : sentMessages) {
            if (m.messageID.equals(id) && found == null) {
                found = m;
            }
        }
        for (Message m : storedMessages) {
            if (m.messageID.equals(id) && found == null) {
                found = m;
            }
        }
        for (Message m : disregardedMessages) {
            if (m.messageID.equals(id) && found == null) {
                found = m;
            }
        }

        return found;
    }

    // d. All messages (sent or stored) for one recipient
    public static List<Message> searchByRecipient(String recipientNumber) {
        List<Message> matches = new ArrayList<>();

        for (Message m : sentMessages) {
            if (m.recipient.equals(recipientNumber)) {
                matches.add(m);
            }
        }
        for (Message m : storedMessages) {
            if (m.recipient.equals(recipientNumber)) {
                matches.add(m);
            }
        }

        return matches;
    }

    // e. Delete a STORED message using its message hash
    public static String deleteByMessageHash(String hash) {
        String result = "Message hash not found.";
        Message toDelete = null;

        for (Message m : storedMessages) {
            if (m.messageHash.equals(hash) && toDelete == null) {
                toDelete = m;
            }
        }

        if (toDelete != null) {
            storedMessages.remove(toDelete);
            messageHashes.remove(toDelete.messageHash);
            messageIDs.remove(toDelete.messageID);
            saveStoredMessages();
            result = "Message: \"" + toDelete.messageText + "\" successfully deleted.";
        }

        return result;
    }

    // Report of all SENT messages: hash, recipient and message
    public static String displayReport() {
        StringBuilder sb = new StringBuilder();

        if (sentMessages.isEmpty()) {
            sb.append("No messages have been sent yet.");
        } else {
            for (Message m : sentMessages) {
                sb.append("Message Hash: ").append(m.messageHash).append("\n");
                sb.append("Recipient: ").append(m.recipient).append("\n");
                sb.append("Message: ").append(m.messageText).append("\n");
                sb.append("----------------------------\n");
            }
        }

        return sb.toString();
    }

    // f. Report with the FULL details of all stored messages
    public static String displayStoredReport() {
        StringBuilder sb = new StringBuilder();

        if (storedMessages.isEmpty()) {
            sb.append("No stored messages.");
        } else {
            for (Message m : storedMessages) {
                sb.append("Message ID: ").append(m.messageID).append("\n");
                sb.append("Message Hash: ").append(m.messageHash).append("\n");
                sb.append("Sender: ").append(m.sender).append("\n");
                sb.append("Recipient: ").append(m.recipient).append("\n");
                sb.append("Message: ").append(m.messageText).append("\n");
                sb.append("----------------------------\n");
            }
        }

        return sb.toString();
    }

    // ---- Helpers used by ChatApp and the unit tests ----
    public static void setSender(String senderName) {
        currentSender = senderName;
    }

    public static void setStorageFile(String fileName) {
        storageFile = fileName;
    }

    // Empties every array so each unit test starts clean
    public static void clearAll() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        totalMessagesSent = 0;
    }

    public static int getSentCount() {
        return sentMessages.size();
    }

    public static int getDisregardedCount() {
        return disregardedMessages.size();
    }

    public static int getStoredCount() {
        return storedMessages.size();
    }

    public static int getMessageHashCount() {
        return messageHashes.size();
    }

    public static int getMessageIDCount() {
        return messageIDs.size();
    }

    // ---- Getters, since fields are private ----
    public String getMessageID() {
        return messageID;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }
}