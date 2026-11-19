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
 *
 * Represents a single chat message. It validates, hashes, sends, disregards
 * and stores messages. The session data is kept in PARALLEL ARRAYS: for each
 * kind of message (sent, disregarded, stored) the arrays line up, so position 0
 * in every array describes the same message, position 1 the next, and so on.
 *
 * Reference (JSON reading and writing):
 * Google (n.d.) Gson: a Java library to convert Java objects into JSON and
 * back. Available at: https://github.com/google/gson [Accessed 17 November 2026].
 */
public class Message {

    //  Fields for THIS message 
    private String messageID;
    private String sender;
    private String recipient;
    private String messageText;
    private String messageHash;
    private int messageNumber;

    // PARALLEL ARRAYS (shared by the whole session)
    

    // Sent messages: index i of each array is the same message
    private static List<String> sentMessages = new ArrayList<>();
    private static List<String> sentRecipients = new ArrayList<>();
    private static List<String> sentHashes = new ArrayList<>();
    private static List<String> sentIDs = new ArrayList<>();

    // Disregarded messages
    private static List<String> disregardedMessages = new ArrayList<>();
    private static List<String> disregardedRecipients = new ArrayList<>();
    private static List<String> disregardedHashes = new ArrayList<>();
    private static List<String> disregardedIDs = new ArrayList<>();

    // Stored messages (read back from the JSON file at startup)
    private static List<String> storedMessages = new ArrayList<>();
    private static List<String> storedSenders = new ArrayList<>();
    private static List<String> storedRecipients = new ArrayList<>();
    private static List<String> storedHashes = new ArrayList<>();
    private static List<String> storedIDs = new ArrayList<>();

    // Every message hash and every message ID (sent, disregarded and stored)
    private static List<String> messageHashes = new ArrayList<>();
    private static List<String> messageIDs = new ArrayList<>();

    private static int totalMessagesSent = 0;

    // Who is sending the messages (set after login) and where JSON is saved
    private static String currentSender = "Developer";
    private static String storageFile = "storedMessages.json";

    // Used only to write and read the JSON file
    static class StoredRecord {
        String messageID;
        String sender;
        String recipient;
        String messageText;
        String messageHash;
    }

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
    // Each choice adds this message to the matching set of parallel arrays.
    
    public String sentMessage(int choice) {
        String result;

        if (choice == 1) {
            sentMessages.add(messageText);
            sentRecipients.add(recipient);
            sentHashes.add(messageHash);
            sentIDs.add(messageID);
            messageIDs.add(messageID);
            messageHashes.add(messageHash);
            totalMessagesSent++;
            result = "Message successfully sent.";
        } else if (choice == 2) {
            disregardedMessages.add(messageText);
            disregardedRecipients.add(recipient);
            disregardedHashes.add(messageHash);
            disregardedIDs.add(messageID);
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
            for (int i = 0; i < sentMessages.size(); i++) {
                sb.append("Message ID: ").append(sentIDs.get(i)).append("\n");
                sb.append("Message Hash: ").append(sentHashes.get(i)).append("\n");
                sb.append("Recipient: ").append(sentRecipients.get(i)).append("\n");
                sb.append("Message: ").append(sentMessages.get(i)).append("\n");
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
    // parallel arrays, then writes them to the JSON file as a JSON array
    // using Gson (see reference at the top of this file).
   
    private void storeMessage() {
        storedMessages.add(messageText);
        storedSenders.add(sender);
        storedRecipients.add(recipient);
        storedHashes.add(messageHash);
        storedIDs.add(messageID);
        saveStoredMessages();
    }

    // Writes the stored arrays to the JSON file as one JSON array
    private static void saveStoredMessages() {
        List<StoredRecord> records = new ArrayList<>();

        for (int i = 0; i < storedMessages.size(); i++) {
            StoredRecord record = new StoredRecord();
            record.messageID = storedIDs.get(i);
            record.sender = storedSenders.get(i);
            record.recipient = storedRecipients.get(i);
            record.messageText = storedMessages.get(i);
            record.messageHash = storedHashes.get(i);
            records.add(record);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(storageFile)) {
            gson.toJson(records, writer);
        } catch (IOException e) {
            System.out.println("Could not store message: " + e.getMessage());
        }
    }

    
    // Reads the JSON file back into the stored parallel arrays
    // (Part 3: "read the JSON file you stored into an array")
    
    public static void loadStoredMessages() {
        // Remove any stored hashes and IDs from the "all" arrays first
        for (int i = 0; i < storedHashes.size(); i++) {
            messageHashes.remove(storedHashes.get(i));
            messageIDs.remove(storedIDs.get(i));
        }

        storedMessages.clear();
        storedSenders.clear();
        storedRecipients.clear();
        storedHashes.clear();
        storedIDs.clear();

        File file = new File(storageFile);

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                StoredRecord[] loaded = new Gson().fromJson(reader, StoredRecord[].class);

                if (loaded != null) {
                    for (StoredRecord record : loaded) {
                        storedMessages.add(record.messageText);
                        storedSenders.add(record.sender);
                        storedRecipients.add(record.recipient);
                        storedHashes.add(record.messageHash);
                        storedIDs.add(record.messageID);
                        messageHashes.add(record.messageHash);
                        messageIDs.add(record.messageID);
                    }
                }
            } catch (IOException | JsonParseException e) {
                System.out.println("Could not read stored messages: " + e.getMessage());
            }
        }
    }

   
    // PART 3: Stored Messages menu features (all loop over the arrays)
    

    // The text of every message sent, in the order they were sent
    public static List<String> getSentMessageTexts() {
        return new ArrayList<>(sentMessages);
    }

    // a. Sender and recipient of all stored messages
    public static String displaySenderAndRecipient() {
        StringBuilder sb = new StringBuilder();

        if (storedMessages.isEmpty()) {
            sb.append("No stored messages.");
        } else {
            for (int i = 0; i < storedMessages.size(); i++) {
                sb.append("Sender: ").append(storedSenders.get(i)).append("\n");
                sb.append("Recipient: ").append(storedRecipients.get(i)).append("\n");
                sb.append("----------------------------\n");
            }
        }

        return sb.toString();
    }

    // b. The longest stored message (compares the lengths in the array)
    public static String displayLongestMessage() {
        String result;

        if (storedMessages.isEmpty()) {
            result = "No stored messages.";
        } else {
            int longestIndex = 0;

            for (int i = 1; i < storedMessages.size(); i++) {
                if (storedMessages.get(i).length() > storedMessages.get(longestIndex).length()) {
                    longestIndex = i;
                }
            }

            result = storedMessages.get(longestIndex);
        }

        return result;
    }

    // c. Search for a message ID. Returns {recipient, message}, or null if not found.
    public static String[] searchByMessageID(String id) {
        String[] found = null;

        for (int i = 0; i < sentIDs.size(); i++) {
            if (sentIDs.get(i).equals(id) && found == null) {
                found = new String[]{sentRecipients.get(i), sentMessages.get(i)};
            }
        }
        for (int i = 0; i < storedIDs.size(); i++) {
            if (storedIDs.get(i).equals(id) && found == null) {
                found = new String[]{storedRecipients.get(i), storedMessages.get(i)};
            }
        }
        for (int i = 0; i < disregardedIDs.size(); i++) {
            if (disregardedIDs.get(i).equals(id) && found == null) {
                found = new String[]{disregardedRecipients.get(i), disregardedMessages.get(i)};
            }
        }

        return found;
    }

    // d. All messages (sent or stored) for one recipient
    public static List<String> searchByRecipient(String recipientNumber) {
        List<String> matches = new ArrayList<>();

        for (int i = 0; i < sentRecipients.size(); i++) {
            if (sentRecipients.get(i).equals(recipientNumber)) {
                matches.add(sentMessages.get(i));
            }
        }
        for (int i = 0; i < storedRecipients.size(); i++) {
            if (storedRecipients.get(i).equals(recipientNumber)) {
                matches.add(storedMessages.get(i));
            }
        }

        return matches;
    }

    // e. Delete a STORED message using its message hash.
    // The same position is removed from every stored array so they stay parallel.
    public static String deleteByMessageHash(String hash) {
        String result = "Message hash not found.";
        int position = -1;

        for (int i = 0; i < storedHashes.size(); i++) {
            if (storedHashes.get(i).equals(hash) && position == -1) {
                position = i;
            }
        }

        if (position != -1) {
            String deletedText = storedMessages.get(position);

            messageHashes.remove(storedHashes.get(position));
            messageIDs.remove(storedIDs.get(position));

            storedMessages.remove(position);
            storedSenders.remove(position);
            storedRecipients.remove(position);
            storedHashes.remove(position);
            storedIDs.remove(position);

            saveStoredMessages();
            result = "Message: \"" + deletedText + "\" successfully deleted.";
        }

        return result;
    }

    // Report of all SENT messages: hash, recipient and message
    public static String displayReport() {
        StringBuilder sb = new StringBuilder();

        if (sentMessages.isEmpty()) {
            sb.append("No messages have been sent yet.");
        } else {
            for (int i = 0; i < sentMessages.size(); i++) {
                sb.append("Message Hash: ").append(sentHashes.get(i)).append("\n");
                sb.append("Recipient: ").append(sentRecipients.get(i)).append("\n");
                sb.append("Message: ").append(sentMessages.get(i)).append("\n");
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
            for (int i = 0; i < storedMessages.size(); i++) {
                sb.append("Message ID: ").append(storedIDs.get(i)).append("\n");
                sb.append("Message Hash: ").append(storedHashes.get(i)).append("\n");
                sb.append("Sender: ").append(storedSenders.get(i)).append("\n");
                sb.append("Recipient: ").append(storedRecipients.get(i)).append("\n");
                sb.append("Message: ").append(storedMessages.get(i)).append("\n");
                sb.append("----------------------------\n");
            }
        }

        return sb.toString();
    }

    // Helpers used by ChatApp and the unit tests
    public static void setSender(String senderName) {
        currentSender = senderName;
    }

    public static void setStorageFile(String fileName) {
        storageFile = fileName;
    }

    // Empties every array so each unit test starts clean
    public static void clearAll() {
        sentMessages.clear();
        sentRecipients.clear();
        sentHashes.clear();
        sentIDs.clear();

        disregardedMessages.clear();
        disregardedRecipients.clear();
        disregardedHashes.clear();
        disregardedIDs.clear();

        storedMessages.clear();
        storedSenders.clear();
        storedRecipients.clear();
        storedHashes.clear();
        storedIDs.clear();

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

    //  Getters, since fields are private
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