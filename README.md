# cs262-replicas

## Getting started
1. Install Maven, one of the Java build systems. On Mac, use homebrew: `brew install maven`.
> The `pom.xml` file in the project defines the Maven dependencies, which includes the gRPC library along with JUnit for unit/integration testing. Make sure Maven is installed by running `mvn -v`.
2. Clone this repository: `git clone`
3. Navigate to the root folder
4. Download the dependencies: `mvn clean install`
5. Compile the code: `mvn compile`

## Running

Server: run `mvn exec:java -Dexec.mainClass="com.chatapp.server.ChatServer" -Dexec.args="<Replica_Number>"`

> Replica numbers are 0, 1, and 2. Each replica (server instance) must be run with a unique replica number. You may run up to three server instances.

Client: run `mvn exec:java -Dexec.mainClass="com.chatapp.client.Client"`

After the Maven build output ends, the client will start waiting for user input on the command line. You may start inputting commands.

> The server must run before the client can perform 'connect'

## Testing
Open the project in VSCode and go to the testing tab. For unit tests, click on the green arrow to run them all. For integration tests, please run them one at a time by manually clicking on the green arrow next to each test.

## Client commands
- connect
- create_account \<account_name\>
- list_accounts \<wildcard_query\>
- login \<account_name\>
- logout
- delete_account \<account_name\>
- send \<recipient\> \<message\>

## Sample usage
```
connect
-> Connecting to server
-> Connected to server

create_account andy
-> Account created for andy

create_account bessie
-> Account created for bessie

list_accounts *e*i*
*** Accounts ***
bessie
****************

login andy
-> Logged in as andy

send bessie Here's to a (hopefully) functioning message service!
-> Queued message from andy for bessie!

logout
-> Logged out andy

login bessie
-> Logged in as bessie
[andy]: Here's to a (hopefully) functioning message service!

send andy Wow I'm surprised that worked!
-> Queued message from bessie for andy!
```

# Documentation
## Engineering Notebook
The engineering notebook can be found [here](https://docs.google.com/document/d/1TJoK3nFk3zBbZu7xBN-lICsxIV1tWJR6azA77zb5HfM/edit#heading=h.6z3u94icrrye).
## Protocol
### Bidirectional stream
The chat app is a client-server system that uses a bidirectional gRPC stream to send messages between the client and server. The bidirectional stream sends and recieves protocol buffers of type `Message`. `Message` uses the `oneof` keyword to allow for different types of messages to be sent over the same stream while still allowing gRPC to compress the actual data into small packages.
### Requests and responses
The different types of messages can be split into two categories: requests and responses. Requests are always sent by the initiating party and responses are always sent by the responding party. For example, when sending a message, the client is the initiating party; however, when distributing a message, the server is the initiating party.

Requests will usually carry arguments and responses will usually carry a Status message along with payload, depending on the specific response.
### Status
Responses usually include a Status message. This is from `com.google.rpc`. It includes a Google-defined `Code` and a human-readable `Message`.

## Client
The client is a command-line application that starts an infinite loop to wait for user input, parsing commands as they are entered. The parsed commands are placed into a blocking queue. When the `connect` command is issued successfully, the client creates a `ConnectionManager` in a new thread that:
1. Defines callback handlers that get called by the gRPC library when a message is received. This usually involves printing to the console.
2. Enters an infinite loop popping commands off of the blocking queue of commands and sending the proper message to the server based on the command.

This multi-threaded architecture allows the user interface to be responsive while the client is waiting for a response from the server.

The client automatically tries to connect to each of the server replicas listed in `protocol/Server.java` until it finds a living leader.

## Server
### Core Functionality
The server can be split into 3 main parts:
1. `ChatServer` manages server lifecycle events, like starting up and shutting down
2. `BusinessLogicServer` handles the actual logic of the chat app
3. `MessageDistributor` handles distributing messages to clients. One is created for each logged in client.

When the server is run, `ChatServer`'s main method is called, which performs some gRPC-related startup operations, but ultimately starts up the `BusinessLogicServer`.

`BusinessLogicServer` has two key parts:
1. It contains the data structures that maintain the list of users, whether they're logged in, and their pending, undelivered messages.
2. The callback handlers that get called by the gRPC library when a message is received. These handlers are responsible for performing the business logic of the chat app, like creating accounts, logging in, and sending messages.

When a user logs in, the log in callback handler creates a new `MessageDistributor` for that user in a new thread. In the new thread, the `MessageDistributor` enters an infinite loop reading from the user's message queue and sending messages to the client. This allows new messages to be delivered immediately without the user needing to "refresh" whenever a client is logged in or logs in.

### Replication
The server is designed to be replicated. Each replica is a separate instance of the server that runs on a different port. The server is designed to be run on three different ports, but it can be run on any number of ports.

The key functionality of replication is outlined by the `ReplicaManager` interface, which is implemented here by `Bully`, an implementation of the popular Bully algorithm. The replica manager is responsible for electing a leader replica and ensuring that all replicas are up-to-date with who is the leader.

The Bully algorithm was chosen because the project specs required that the system be crash and failstop failure tolerant, but not network tolerant. The bully algorithm is a leader election algorithm that is crash and failstop tolerant, but not network tolerant. It is also simpler to implement and understand, compared to other leader election algorithms like Paxos or Raft. Simple is always better.

`BusinessLogicServer` takes in a replica manager and uses that to alter its behavior depending on whether the replica manager says its supposed to be a leader or a follower. Leaders talk directly with the client and forward all messages to followers so that the followers can, well, follow. Leaders perform forward messages by using a `RelayGroup` containing `Relay`s to all the other replicas.

If a client tries to talk to a follower, the follower will reject the client.

See the figjam [here](https://www.figma.com/file/BPRxpFWtWyHKTcYvSOwCbF/CS-262-Design-Exercise-3?node-id=0-1&t=1840qM9g8eJSvYLL-0) for more detail on how this works.

### Persistence
Messages that have been sent to users but that have not been delivered (because the recipient hasn't logged in) are persisted even if the server is shut down. To this end, text files are dedicated to log all messages sent while the server remains alive. Text files are also used to log all creations and deletions of accounts. On startup, servers look for these files and load the data into memory if available.