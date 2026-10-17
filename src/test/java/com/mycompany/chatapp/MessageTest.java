/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.chatapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageTest.java
 *
 * Unit tests for the Message class, using the test data from the brief.
 */
public class MessageTest {

    // Test data from the brief
    private static final String RECIPIENT_1 = "+27718693002";
    private static final String MESSAGE_1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String RECIPIENT_2 = "08575975889";
    private static final String MESSAGE_2 = "Hi Keegan, did you receive the payment?";

    // -----------------------------------------------------------
    // Message length (250 characters)
    // -----------------------------------------------------------

    @Test
    public void testMessageLengthSuccess() {
        Message message = new Message(RECIPIENT_1, MESSAGE_1, 0);
        assertEquals("Message ready to send.", message.checkMessageLength(MESSAGE_1));
    }

    @Test
    public void testMessageLengthFailure() {
        // 260 characters = 10 over the limit
        String longMessage = "a".repeat(260);
        Message message = new Message(RECIPIENT_1, MESSAGE_1, 0);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                message.checkMessageLength(longMessage));
    }

    // -----------------------------------------------------------
    // Recipient number
    // -----------------------------------------------------------

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

    // -----------------------------------------------------------
    // Message hash (fixed ID so the result is predictable)
    // -----------------------------------------------------------

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

    // -----------------------------------------------------------
    // Message ID
    // -----------------------------------------------------------

    @Test
    public void testMessageIDIsCreated() {
        Message message = new Message(RECIPIENT_1, MESSAGE_1, 0);
        assertNotNull(message.getMessageID());
        assertEquals(10, message.getMessageID().length());
        assertTrue(message.checkMessageID(message.getMessageID()));
    }

    // -----------------------------------------------------------
    // Send / Disregard / Store
    // -----------------------------------------------------------

    @Test
    public void testMessageSent() {
        Message message = new Message(RECIPIENT_1, MESSAGE_1, 0);
        assertEquals("Message successfully sent.", message.sentMessage("send"));
    }

    @Test
    public void testMessageDisregarded() {
        Message message = new Message(RECIPIENT_2, MESSAGE_2, 1);
        assertEquals("Press 0 to delete the message.", message.sentMessage("disregard"));
    }

    @Test
    public void testMessageStored() {
        Message message = new Message(RECIPIENT_1, MESSAGE_1, 0);
        assertEquals("Message successfully stored.", message.sentMessage("store"));
    }

    // -----------------------------------------------------------
    // Total messages sent
    // -----------------------------------------------------------

    @Test
    public void testReturnTotalMessages() {
        int before = Message.returnTotalMessages();

        Message sent = new Message(RECIPIENT_1, MESSAGE_1, 0);
        sent.sentMessage("send");

        Message disregarded = new Message(RECIPIENT_2, MESSAGE_2, 1);
        disregarded.sentMessage("disregard");

        // Only the sent message counts
        assertEquals(before + 1, Message.returnTotalMessages());
    }
}


