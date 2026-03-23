# Java Swing Chat Application

A client-server based desktop chat application built using Java Swing and Socket Programming that enables secure real-time messaging between multiple users with authentication support.

## Features
- Client–server architecture
- Real-time messaging using TCP sockets
- Login authentication system
- Password hashing for security
- Database integration
- Multi-user support
- Swing-based graphical interface

## Project Structure
src/client → Client connection logic  
src/server → Server handling and authentication  
src/common → Message protocol classes  
src/db → Database connection handling  
src/ui → Login and chat interface  
src/utils → Password encryption utilities  

## Technologies Used
Java  
Swing  
Socket Programming  
Multithreading  
Maven  

## How to Run

### Start Server
Run:
ServerMain.java

### Start Client
Run:
ClientMain.java

Make sure server starts before client.

## Future Improvements
File sharing support  
Group chat support  
Message history sync  
Online user status indicator
