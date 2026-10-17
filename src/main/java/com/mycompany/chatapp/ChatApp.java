package com.mycompany.chatapp;

import java.util.Scanner;

public class ChatApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        // Registration 
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

        //  Login 
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

        
        System.out.println("\nWelcome to QuickChat.");

        System.out.print("How many messages would you like to send this session? ");
        int numMessages = Integer.parseInt(scanner.nextLine().trim());

        int messagesSentThisSession = 0;
        boolean running = true;

        while (running) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {

                if (messagesSentThisSession >= numMessages) {
                    System.out.println("You have reached your limit of " + numMessages
                            + " messages for this session.");
                } else {
                    System.out.print("Enter recipient cell number (with country code): ");
                    String recipientCell = scanner.nextLine();

                    // Reuse Login's cell check for a quick pre-check message, since
                    // Message.checkRecipientCell() follows the same rule
                    Message tempCheck = new Message(recipientCell, "placeholder", 0);
                    String cellCheckResult = tempCheck.checkRecipientCell(recipientCell);
                    System.out.println(cellCheckResult);

                    if (cellCheckResult.contains("successfully")) {
                        System.out.print("Enter your message: ");
                        String messageText = scanner.nextLine();

                        Message message = new Message(recipientCell, messageText,
                                messagesSentThisSession);

                        String lengthCheck = message.checkMessageLength(messageText);
                        System.out.println(lengthCheck);

                        if (lengthCheck.equals("Message ready to send.")) {
                            System.out.print("Choose: Send / Disregard / Store: ");
                            String sendChoice = scanner.nextLine();

                            String outcome = message.sentMessage(sendChoice);
                            System.out.println(outcome);

                            if (outcome.equals("Message successfully sent.")) {
                                messagesSentThisSession++;
                                System.out.println("\nMessage ID: " + message.getMessageID());
                                System.out.println("Message Hash: " + message.getMessageHash());
                                System.out.println("Recipient: " + message.getRecipient());
                                System.out.println("Message: " + message.getMessageText());
                            }
                        }
                    }
                }

            } else if (choice.equals("2")) {
                System.out.println("Coming Soon.");

            } else if (choice.equals("3")) {
                running = false;
                System.out.println("Total messages sent: " + Message.returnTotalMessages());
                System.out.println("\nAll sent messages:");
                System.out.println(Message.printMessages());

            } else {
                System.out.println("Invalid option, please choose 1, 2, or 3.");
            }
        }

        scanner.close();
    }
}
