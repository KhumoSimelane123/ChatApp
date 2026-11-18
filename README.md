# ChatApp (QuickChat)

PROG5121 Programming 1A, Portfolio of Evidence.
A console-based Java chat application built in Apache NetBeans with Maven and JUnit 5.

## Part 1: Registration and Login
- Username must contain an underscore and be no more than 5 characters.
- Password must be at least 8 characters with a capital letter, a number and a special character.
- Cell phone number must contain the international code (+27...).
- Users can log in with the details they registered.

## Part 2: Sending Messages
After a successful login the user sees "Welcome to QuickChat." and chooses how many messages to enter.

1. **Send Messages**: enters the chosen number of messages using a for loop. A wrong recipient number or a message over 250 characters is asked for again. Each message is sent (1), disregarded (2) or stored (3).
2. **Show recently sent messages**: displays "Coming Soon."
3. **Quit**: shows the total messages sent and the details of each sent message.

## Part 3: Store Data and Display
4. **Stored Messages**: a sub-menu that uses the arrays to:
   - display the sender and recipient of all stored messages
   - display the longest stored message
   - search for a message ID and display the recipient and message
   - search for all messages for a recipient
   - delete a message using its message hash
   - display a report of all stored messages

The program keeps arrays of sent messages, disregarded messages, stored messages, message hashes and message IDs. Stored messages are saved to `storedMessages.json` as a JSON array (using the Gson library) and are read back into the stored messages array when the program starts.

### Message class

| `checkMessageID()` | Message ID must be no more than 10 characters |
| `checkRecipientCell()` | Recipient number must include the international code |
| `checkMessageLength()` | Message must not exceed 250 characters |
| `createMessageHash()` | Builds a hash such as `00:0:HITONIGHT` |
| `sentMessage()` | Send (1), Disregard (2) or Store (3) the message |
| `printMessages()` | Returns the details of all sent messages |
| `returnTotalMessages()` | Returns the total number of messages sent |
| `storeMessage()` | Adds the message to the stored array and saves the JSON array |

## Running the project
1. Open the project in Apache NetBeans.
2. Run `ChatApp.java`.

## Tests
JUnit 5 tests are in `LoginTest.java` (19 tests) and `MessageTest.java` (23 tests).
Run them with `mvn test` or right-click the project and choose Test.

## Continuous Integration
A GitHub Actions workflow (`.github/workflows/maven.yml`) runs `mvn test` automatically on every push and pull request.

## Workflow
Each part was developed on its own feature branch (`Part2-SendMessages`, `Part3-StoreData`) and merged into `master` with a pull request.

## References
Google (n.d.) Gson: a Java library to convert Java objects into JSON and back. Available at: https://github.com/google/gson [Accessed 17 November 2026].