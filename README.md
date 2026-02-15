# Splitwise
### Overview
Splitwise is my final project for the Modern Java Technologies 2025 course in FMI. It is a Java-based application designed to manage shared expenses among groups of users. It handles expense splitting, payment management, currency conversion, and user profiles with a network-based server architecture.
### Features
- User Management: Register and manage user profiles with secure password handling
- Group Management: Create and manage expense groups
- Expense Splitting: Track obligations and split expenses among group members
- Payment Management: Process and approve payments between users
- Currency Conversion: Support multiple currencies with real-time exchange rate conversion
- Notifications: Receive notifications for new obligations and approved payments
- Logging: Parallel logging system for tracking operations
- Server Architecture: Network-based communication using a TCP server
### Dependencies
- JUnit 5.11.4: Unit testing framework
- Mockito 5.15.2: Mocking library for tests
- GSON 2.12.0: JSON serialization/deserialization
- Byte Buddy 1.15.11: Bytecode manipulation for testing
### Getting Started
Open the project in IntelliJ IDEA 2024.2.3. 
Configure the library dependencies from the lib/ directory.
Run the Main.java class in the server/ package to start the server.
Connect clients to the server and use available commands:
```bash
$ register <username> <password>
$ login  <username> <password>
$ add-friend <username>
$ create-group <group_name> <username> <username> ... <username>
$ split <amount> <username> <reason_for_payment>
$ split-group <amount> <group_name> <reason_for_payment>
$ approve-payment <amount> <username>
$ approve-group-member-payment <amount> <username> <group>
$ convert-currency <currency>
$ get-status
```
### Testing
Use IntelliJ IDEA's built-in test runner to execute tests in the test/ directory.
Tests cover entities, commands, and the parallel logging manager
