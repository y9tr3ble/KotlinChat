> **Warning**
> Alpha version, It may contain a variety of errors

# KotlinChat

A simple chat application written in Kotlin using Ktor networking library.
# Description
KotlinChat includes a simple TCP server and client implementation where multiple clients can connect and send messages that will be relayed to all other connected clients.
# Architecture
## Client
The client is implemented in a ChatClient.kt class, which handles connecting to the server, reading input from the user, and sending these messages to the server, as well as reading and printing messages from the server.
Each client communicates with the server in a separate coroutine.
## Server
The server is implemented in the ChatServer.kt class and listens for incoming connections.
For each connected client, we create a ClientHandler instance responsible for reading input from the particular client, broadcasting messages to all other clients.
