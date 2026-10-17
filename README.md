# ChatApp (QuickChat)

PROG5121 Programming 1A, Portfolio of Evidence.
A console-based Java chat application built in Apache NetBeans with Maven and JUnit 5.

# Part 1: Registration and Login
- Username must contain an underscore and be no more than 5 characters.
- Password must be at least 8 characters with a capital letter, a number and a special character.
- Cell phone number must contain the international code (+27...).
- Users can log in with the details they registered.

#Part 2: Sending Messages
After a successful login the user sees "Welcome to QuickChat." and a menu:

1. Send Messages**: the user chooses how many messages to enter at the start.
2. Show recently sent messages**: displays "Coming Soon."
3. Quit**: shows the total messages sent and a list of the sent messages.

# Message class
| Method | Purpose |
|---|---|
| `checkMessageID()` | Message ID must be no more than 10 characters |
| `checkRecipientCell()` | Recipient number must include the international code |
| `checkMessageLength()` | Message must not exceed 250 characters |
| `createMessageHash()` | Builds a hash such as `00:0:HITONIGHT` |
| `sentMessage()` | Send, Disregard or Store the message |
| `printMessages()` | Returns the details of all sent messages |
| `returnTotalMessages()` | Returns the total number of messages sent |
| `storeMessage()` | Saves a stored message to `storedMessages.json` |

# Running the project
1. Open the project in Apache NetBeans.
2. Run `ChatApp.java`.

# Tests
JUnit 5 tests are in `LoginTest.java` and `MessageTest.java`.
Run them with `mvn test` or right-click the project and choose Test.

# Continuous Integration
A GitHub Actions workflow (`.github/workflows/maven.yml`) runs `mvn test` automatically on every push and pull request.

# Workflow
Part 2 was developed on the `Part2-SendMessages` feature branch and merged into `master`.