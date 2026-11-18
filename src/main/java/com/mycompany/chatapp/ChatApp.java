package com.mycompany.chatapp;

import java.util.List;
import java.util.Scanner;

public class ChatApp {

    // Shared by the helper methods below
    private static Scanner scanner = new Scanner(System.in);
    private static int numMessages = 0;
    private static int messageNumber = 0;
    private static boolean messagesEntered = false;

    public static void main(String[] args) {
        Login login = new Login();

        // ---------------- Registration ----------------
        System.out.println("=== Registration ===");

        System.out.print("Enter a username: ");
        String username = scanner.nextLine();

        System.out.print("Enter a password: ");
        String password = scanner.nextLine();

        System.out.print("Enter your cell phone number (with country code, e.g. +27...): ");
        String cellPhoneNumber = scanner.nextLine();

        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine();

        String registrationResult = login.registerUser(username, password, cellPhoneNumber,
                firstName, lastName);
        System.out.println(registrationResult);

        if (!registrationResult.contains("successful")) {
            System.out.println("Registration failed - please restart the program and try again.");
            scanner.close();
            return;
        }

        // Login 
        System.out.println("\n=== Login ===");

        System.out.print("Enter your username: ");
        String loginUsername = scanner.nextLine();

        System.out.print("Enter your password: ");
        String loginPassword = scanner.nextLine();

        boolean loginSuccess = login.loginUser(loginUsername, loginPassword);
        System.out.println(login.returnLoginStatus(loginSuccess));

        if (!loginSuccess) {
            // Requirement: users can only send messages if logged in successfully
            scanner.close();
            return;
        }

        // QuickChat 
        Message.setSender(firstName + " " + lastName);
        Message.loadStoredMessages();

        System.out.println("\nWelcome to QuickChat.");

        numMessages = readNumber("How many messages would you like to enter? ", 1, 1000);

        boolean running = true;

        while (running) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            int choice = readNumber("Choose an option (1-4): ", 1, 4);

            if (choice == 1) {
                sendMessages();

            } else if (choice == 2) {
                System.out.println("Coming Soon.");

            } else if (choice == 3) {
                running = false;
                System.out.println("Total messages sent: " + Message.returnTotalMessages());
                System.out.println("\nAll sent messages:");
                System.out.println(Message.printMessages());

            } else {
                storedMessagesMenu();
            }
        }

        scanner.close();
    }

    
    // Option 1: a FOR loop that enters the number of messages the user
    // chose at the start. Bad input is asked for again (while loops).
    
    private static void sendMessages() {
        if (messagesEntered) {
            System.out.println("You have already entered all " + numMessages + " messages.");
        } else {
            Message checker = new Message("", "", 0);

            for (int i = 0; i < numMessages; i++) {
                System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");

                // Ask again until the recipient number is valid
                String recipient = "";
                boolean validRecipient = false;

                while (!validRecipient) {
                    System.out.print("Enter recipient cell number (with country code): ");
                    recipient = scanner.nextLine().trim();

                    String cellResult = checker.checkRecipientCell(recipient);
                    System.out.println(cellResult);

                    if (cellResult.equals("Cell phone number successfully captured.")) {
                        validRecipient = true;
                    }
                }

                // Ask again until the message is no longer than 250 characters
                String messageText = "";
                boolean validLength = false;

                while (!validLength) {
                    System.out.print("Enter your message: ");
                    messageText = scanner.nextLine();

                    String lengthResult = checker.checkMessageLength(messageText);
                    System.out.println(lengthResult);

                    if (lengthResult.equals("Message ready to send.")) {
                        validLength = true;
                    }
                }

                // The message number counts every message entered, not only sent ones
                Message message = new Message(recipient, messageText, messageNumber);
                messageNumber++;

                System.out.println("1) Send   2) Disregard   3) Store");
                int sendChoice = readNumber("Choose 1, 2 or 3: ", 1, 3);

                String outcome = message.sentMessage(sendChoice);
                System.out.println(outcome);

                if (sendChoice == 1) {
                    System.out.println("\nMessage ID: " + message.getMessageID());
                    System.out.println("Message Hash: " + message.getMessageHash());
                    System.out.println("Recipient: " + message.getRecipient());
                    System.out.println("Message: " + message.getMessageText());
                }
            }

            messagesEntered = true;
        }
    }

    
    // Option 4: Stored Messages menu (Part 3)
    
    private static void storedMessagesMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n--- Stored Messages ---");
            System.out.println("1) Display the sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages for a recipient");
            System.out.println("5) Delete a message using its message hash");
            System.out.println("6) Display a report of all stored messages");
            System.out.println("7) Back to the main menu");
            int choice = readNumber("Choose an option (1-7): ", 1, 7);

            if (choice == 1) {
                System.out.println(Message.displaySenderAndRecipient());

            } else if (choice == 2) {
                System.out.println(Message.displayLongestMessage());

            } else if (choice == 3) {
                System.out.print("Enter the message ID: ");
                String id = scanner.nextLine().trim();
                Message found = Message.searchByMessageID(id);

                if (found == null) {
                    System.out.println("Message ID not found.");
                } else {
                    System.out.println("Recipient: " + found.getRecipient());
                    System.out.println("Message: " + found.getMessageText());
                }

            } else if (choice == 4) {
                System.out.print("Enter the recipient cell number: ");
                String number = scanner.nextLine().trim();
                List<Message> matches = Message.searchByRecipient(number);

                if (matches.isEmpty()) {
                    System.out.println("No messages found for that recipient.");
                } else {
                    for (Message m : matches) {
                        System.out.println("Message: " + m.getMessageText());
                    }
                }

            } else if (choice == 5) {
                System.out.print("Enter the message hash: ");
                String hash = scanner.nextLine().trim();
                System.out.println(Message.deleteByMessageHash(hash));

            } else if (choice == 6) {
                System.out.println(Message.displayStoredReport());

            } else {
                back = true;
            }
        }
    }

    // ---------------------------------------------------------------
    // Asks for a whole number between min and max, and asks again if the
    // user types letters or a number outside the range.
    // ---------------------------------------------------------------
    private static int readNumber(String prompt, int min, int max) {
        int value = 0;
        boolean valid = false;

        while (!valid) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                value = Integer.parseInt(input);

                if (value >= min && value <= max) {
                    valid = true;
                } else {
                    System.out.println("Please enter a number from " + min + " to " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("That is not a number. Please try again.");
            }
        }

        return value;
    }
}