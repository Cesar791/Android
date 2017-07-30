# Android

This repo contains a client (Android) and server (Java) application. The client can get processed camera frames from the server side. The server performs facial recognition using JavaCV (OpenCV).

The client can also send messages to the server which can be built upon in the future for smart home application.
## Prerequisite
* JavaCV
* Android Studio

## Usage
* First open "ClientAndroid" folder in Android Studio.
* Edit "private static String hostname = "your IP-address";" in "MainActivity.java" (line 18) to your computers ip-address.
* "MainActivity.java" is located in app/Java in the project (left) sidebar.

### Initiate the server
Using Eclipse run "Server.java".

Using Terminal
1. "javac server/Server.java".
2. "java server.Server".

### Initiate the client
* Open ClientAndroid folder in Android Studio.
* Run "MainActivity.java" located in app/Java in the project (left) sidebar.
