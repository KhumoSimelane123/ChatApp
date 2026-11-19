package com.mycompany.chatapp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageTest.java
 *
 * Unit tests for the Message class (Part 2 and Part 3), using the test data
 * from the brief.
 */
public class MessageTest {

    //  Part 2 test data 
    private static final String RECIPIENT_1 = "+27718693002";
    private static final String MESSAGE_1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String RECIPIENT_2 = "08575975889";
    private static final String MESSAGE_2 = "Hi Keegan, did you receive the payment?";

    // Part 3 test data (Test Data Messages 1 to 5) 
    private static final String P3_RECIPIENT_1 = "+27834557896";
    private static final String P3_MESSAGE_1 = "Did you get the cake?";
    private static final String P3_RECIPIENT_2 = "+27838884567";
    private static final String P3_MESSAGE_2 = "Where are you? You are late! I have asked you to be on time.";
    private static final String P3_RECIPIENT_3 = "+27834484567";
    private static final String P3_MESSAGE_3 = "Yohoooo, I am at your gate.";
    private static final String P3_RECIPIENT_4 = "0838884567";
    private static final String P3_MESSAGE_4 = "It is dinner time!";
    private static final String P3_RECIPIENT_5 = "+27838884567";
    private static final String P3_MESSAGE_5 = "Ok, I am leaving without you.";

    private File storageFile;
    private Message message2;

    // Every test starts with empty arrays and its own temporary JSON file
    @BeforeEach
    public void setUp() throws IOException {
        storageFile = File.createTempFile("storedMessages", ".json");
        storageFile.delete();
        Message.setStorageFile(storageFile.getAbsolutePath());
        Message.setSender("Developer");
        Message.clearAll();
    }

    @AfterEach
    public void tearDown() {
        storageFile.delete();
    }

    // Fills the arrays with the five Part 3 test messages (fixed IDs)
    private void loadPart3TestData() {
        // Message 1: Sent
        new Message("0012345678", P3_RECIPIENT_1, P3_MESSAGE_1, 0).sentMessage(1);

        // Message 2: Stored
        message2 = new Message("0123456789", P3_RECIPIENT_2, P3_MESSAGE_2, 1);
        message2.sentMessage(3);

        // Message 3: Disregard
        new Message("0234567890", P3_RECIPIENT_3, P3_MESSAGE_3, 2).sentMessage(2);

        // Message 4: Sent (message ID 0838884567)
        new Message("0838884567", P3_RECIPIENT_4, P3_MESSAGE_4, 3).sentMessage(1);

        // Message 5: Stored
        new Message("0456789012", P3_RECIPIENT_5, P3_MESSAGE_5, 4).sentMessage(3);
    }

   
    // PART 2 TESTS
    
    @Test
    public void testMessageLengthSuccess() {
        Message message = new Message(RECIPIENT_1, MESSAGE_1, 0);
        assertEquals("Message ready to send.", message.checkMessageLength(MESSAGE_1));
    }

    @Test
    public void testMessageLengthFailure() {
        String longMessage = "a".repeat(260);
        Message message = new Message(RECIPIENT_1, MESSAGE_1, 0);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                message.checkMessageLength(longMessage));
    }

    @Test
    public void testRecipientCellSuccess() {
        Message message = new Message(RECIPIENT_1, MESSAGE_1, 0);
        assertEquals("Cell phone number successfully captured.",
                message.checkRecipientCell(RECIPIENT_1));
    }

    @Test
    public void testRecipientCellFailure() {
        Message message = new Message(RECIPIENT_2, MESSAGE_2, 1);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                message.checkRecipientCell(RECIPIENT_2));
    }

    @Test
    public void testMessageHashTestCase1() {
        Message message = new Message("0012345678", RECIPIENT_1, MESSAGE_1, 0);
        assertEquals("00:0:HITONIGHT", message.getMessageHash());
    }

    @Test
    public void testMessageHashesInLoop() {
        String[] messages = {MESSAGE_1, MESSAGE_2};
        String[] expectedHashes = {"00:0:HITONIGHT", "00:1:HIPAYMENT"};

        for (int i = 0; i < messages.length; i++) {
            Message message = new Message("0012345678", RECIPIENT_1, messages[i], i);
            assertEquals(expectedHashes[i], message.getMessageHash());
        }
    }

    @Test
    public void testMessageIDIsCreated() {
        Message message = new Message(RECIPIENT_1, MESSAGE_1, 0);
        assertNotNull(message.getMessageID());
        assertEquals(10, message.getMessageID().length());
        assertTrue(message.checkMessageID(message.getMessageID()));
    }

    @Test
    public void testMessageSent() {
        Message message = new Message(RECIPIENT_1, MESSAGE_1, 0);
        assertEquals("Message successfully sent.", message.sentMessage(1));
    }

    @Test
    public void testMessageDisregarded() {
        Message message = new Message(RECIPIENT_2, MESSAGE_2, 1);
        assertEquals("Press 0 to delete the message.", message.sentMessage(2));
    }

    @Test
    public void testMessageStored() {
        Message message = new Message(RECIPIENT_1, MESSAGE_1, 0);
        assertEquals("Message successfully stored.", message.sentMessage(3));
    }

    @Test
    public void testInvalidSendOption() {
        Message message = new Message(RECIPIENT_1, MESSAGE_1, 0);
        assertEquals("Invalid option selected.", message.sentMessage(9));
    }

    @Test
    public void testReturnTotalMessages() {
        int before = Message.returnTotalMessages();

        new Message(RECIPIENT_1, MESSAGE_1, 0).sentMessage(1);
        new Message(RECIPIENT_2, MESSAGE_2, 1).sentMessage(2);

        // Only the sent message counts
        assertEquals(before + 1, Message.returnTotalMessages());
    }

    
    // PART 3 TESTS
    

    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        loadPart3TestData();

        List<String> expected = Arrays.asList("Did you get the cake?", "It is dinner time!");
        assertEquals(expected, Message.getSentMessageTexts());
    }

    @Test
    public void testDisplayLongestMessage() {
        loadPart3TestData();

        assertEquals("Where are you? You are late! I have asked you to be on time.",
                Message.displayLongestMessage());
    }

    @Test
    public void testSearchForMessageID() {
        loadPart3TestData();

        String[] found = Message.searchByMessageID("0838884567");
        assertEquals("It is dinner time!", found[1]);
        assertEquals("0838884567", found[0]);
    }

    @Test
    public void testSearchMessageIDNotFound() {
        loadPart3TestData();

        assertNull(Message.searchByMessageID("9999999999"));
    }

    @Test
    public void testSearchAllMessagesForRecipient() {
        loadPart3TestData();

        List<String> expected = Arrays.asList(
                "Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you.");
        assertEquals(expected, Message.searchByRecipient("+27838884567"));
    }

    // After a delete the arrays must still line up (parallel arrays)
    @Test
    public void testParallelArraysStayAlignedAfterDelete() {
        loadPart3TestData();

        Message.deleteByMessageHash(message2.getMessageHash());

        // Message 5 moved to position 0; its ID must still give ITS recipient and text
        String[] found = Message.searchByMessageID("0456789012");
        assertEquals("+27838884567", found[0]);
        assertEquals("Ok, I am leaving without you.", found[1]);
        assertEquals("Ok, I am leaving without you.", Message.displayLongestMessage());
    }

    @Test
    public void testDeleteMessageUsingHash() {
        loadPart3TestData();

        String result = Message.deleteByMessageHash(message2.getMessageHash());

        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\""
                + " successfully deleted.", result);
        assertEquals(1, Message.getStoredCount());
    }

    @Test
    public void testDeleteMessageHashNotFound() {
        loadPart3TestData();

        assertEquals("Message hash not found.", Message.deleteByMessageHash("XX:9:NOTHING"));
        assertEquals(2, Message.getStoredCount());
    }

    @Test
    public void testDisplayReport() {
        loadPart3TestData();

        String expected = "Message Hash: 00:0:DIDCAKE\n"
                + "Recipient: +27834557896\n"
                + "Message: Did you get the cake?\n"
                + "----------------------------\n"
                + "Message Hash: 08:3:ITTIME\n"
                + "Recipient: 0838884567\n"
                + "Message: It is dinner time!\n"
                + "----------------------------\n";
        assertEquals(expected, Message.displayReport());
    }

    @Test
    public void testArraysPopulatedFromTestData() {
        loadPart3TestData();

        assertEquals(2, Message.getSentCount());
        assertEquals(1, Message.getDisregardedCount());
        assertEquals(2, Message.getStoredCount());
        assertEquals(5, Message.getMessageHashCount());
        assertEquals(5, Message.getMessageIDCount());
        assertEquals(2, Message.returnTotalMessages());
    }

    
    // JSON file: written as an array and read back into the array
    

    @Test
    public void testStoredMessagesWrittenAsJsonArray() throws IOException {
        loadPart3TestData();

        String json = Files.readString(storageFile.toPath()).trim();
        assertTrue(json.startsWith("["));
        assertTrue(json.endsWith("]"));
    }

    @Test
    public void testStoredMessagesReadBackFromJson() {
        loadPart3TestData();

        // Empty the arrays, then read the JSON file back in
        Message.clearAll();
        Message.loadStoredMessages();

        assertEquals(2, Message.getStoredCount());
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                Message.displayLongestMessage());
    }
}