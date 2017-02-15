# MCS Fall 2015 Advanced Operating System Projects
This repository contains the Assignments / Projects done for Advanced Operating System (AOS) course during my Masters Graduation in Computer Science in Fall 2015.
##1. A Simple Centralized Peer to Peer File Sharing System
This project had two purposes:

1. to get us familiarize with concepts like Sockets / RPCs / RMIs, Processes, Threads, Makefiles
2. to learn the design and internals of a Napster-style peer-to-peer (P2P) file sharing system.
The project was coded in Java using Sockets, and Threads concepts in Linux environment.

In this project, we needed to design a simple P2P system that has two components:

**1. A central Indexing Server:**

This server indexes the contents of all of the peers that register with it. It also provides search facility to peers. In our simple version, we implemented an exact match based search algorithm. The server provided the following interface to the peer clients:

- **registry(peer id, file name, ...):** invoked by a peer to register all its files with the Indexing Server. The server then builds the index for the peer. Additional information like Client IP, Port, complete path to the file, file size, etc. was also stored on the server to make it more 'real'. 

- **search(file name):** this procedure searches the index and returns all the matching peers to the requesting peer.

**2. A Peer:** 

A peer is both a client and a server. As a client, the user specifies a file name with the Indexing Server using "lookup". The indexing server returns a list of all other peers that hold the file. The user picks one such peer and the client then connects to this peer and downloads the file. As a server, the peer waits for requests from other peers and sends the requested file when receiving a request. The peer server provides the following interfaces to the peer client: 

- **obtain(file name):** invoked by a peer to download a file from another peer.

**Other features:**

- Both the indexing server and a peer server accepts multiple client requests at the same time.
- Both text and binary files are supported for transfer.
- Simple command line interfaces are used to select the menu driven options.
- The “.properties” files allows administrator or user to define the various options (like IP Address, Port, Default Sharing directory, Default Download directory, etc.) for keeping the System scalable and parametrized.

##2. A Simple Distributed Hash Table
In this programming assignment, we implemented a Distributed Hash Table concepts like sockets & threads in Java under Linux environment.

Distributed Hash Table ((hereinafter referred to as “DHT”) System simulates the behavior of Hash Table Data Structure across network by taking advantages of the Distributed Systems. This concept will help to achieve scalability if resources are increased based on the demand. DHT concept can be used as File Indexing Servers, Domain Name Servers, etc. It utilizes the advantages of decentralized distribution system by providing Dictionary like interface. Nodes of this Dictionary like interface can be spread across the network.

It provides

- A distributed decentralized database where users can store the data using Key – Value pair concepts
- DHT Client can perform Put, Get and Delete operations on DHT database

In this assignment, each peer acts as both a server and a client. As a client, it provides interfaces through which users can issue queries and view search results. As a server, it accepts queries from other peers, checks for matches against its local hash table, and responds with corresponding results. In addition, since there's no central indexing server, search is done through consistent hashing.

**Assumptions:**

- The structure of the network and servers is assumed static. Network and servers will be initialized statically using a configuration file that is read by each server at initialization time.
- Messages are fixed size at 1024 bytes with Keys' length limited to be 20 bytes & Values' length limited to 1000 bytes. Remaining 4 bytes can be used as optional header to indicate additional information like type of operation to be performed, etc.

**Operations:**

- **put(key,value):** This operation is used to store the Key - Value pair data into DHT. It returns success or failure.

- **get(key):** This operation is used to retrieve the already stored Value data for the specified Key from DHT. It returns the value or null.

- **del(key):** This operation is used to remove / delete the Key - Value pair data from DHT. It returns success or failure.

The above mentioned operations are sent to the correct server by performing a hash on the key. 


##3. A Simple Decentralized Peer to Peer File Sharing System
This project combines the earlier two projects "A simple Centralized Peer to Peer File Sharing System" and "A simple Distributed Hash Table".

Similar to our first project, we needed to design a simple P2P system that has two components:

**1. A decentralized Indexing Server:**

This server indexes the contents of all of the peers that register with it. It also provides search facility to peers. In our simple version, we implemented an exact match based search algorithm. The server provided the following interface to the peer clients:

- **registry(peer id, file name, ...):** invoked by a peer to register all its files with the Indexing Server. The server then builds the index for the peer. Additional information like Client IP, Port, complete path to the file, file size, etc. was also stored on the server to make it more 'real'. 

- **search(file name):** this procedure searches the index and returns all the matching peers to the requesting peer.


**2. A Peer:** 

A peer is both a client and a server. As a client, the user specifies a file name with the Indexing Server using "lookup". The indexing server returns a list of all other peers that hold the file. The user picks one such peer and the client then connects to this peer and downloads the file. As a server, the peer waits for requests from other peers and sends the requested file when receiving a request. The peer server provides the following interfaces to the peer client: 

- **obtain(file name):** invoked by a peer to download a file from another peer.

**Other features:**

- Both the indexing server and a peer server accepts multiple client requests at the same time.
- Both text and binary files are supported for transfer.
- Simple command line interfaces are used to select the menu driven options.
- Maintaining multiple Indexing Servers also facilitates the replicas of other Indexing Servers’ data to achieve Data Resilience. Data Resilience in turn works as fail-safe mechanism ensuring 100% operational time and data availability. This decentralized architecture also helps to maintain Data Resilience for P2P Files (actual data to be shared) by keeping a copy of these files on Indexing Servers. Number of copies to be maintained for Data Resilience (either for Indexing Servers’ or Peer Servers’ data) depends upon the Data Resilience factor (ReplicationFactor parameter in properties file). Data Resilience Factor can be parametrized and can have maximum value **‘(n-1)’ (excluding the data from the original Index / Peer Server)** where **‘n’** is the number of Indexing Servers used by Peer to Peer System.
- All the above mentioned Server and Peer operations are done by performing a hash on the File Name.
- The “.properties” files allows administrator or user to define the various options (like IP Address, Port, Default Sharing directory, Default Download directory, etc.) for keeping the System scalable and parametrized.

##4. An Empirical Evaluation of Distributed Key-Value Storage Systems

In this project, we evaluated various distributed key/value storage systems using AWS cloud ’s EC2 service. We evaluated MongoDB, Cassandra, CouchDB, Redis. The evaluation results were compared Project 2 ("A Simple Distributed Hash Table") results. A poster (2 feet by 3 feet) was prepared to explain the complete project findings and the comparison results.
