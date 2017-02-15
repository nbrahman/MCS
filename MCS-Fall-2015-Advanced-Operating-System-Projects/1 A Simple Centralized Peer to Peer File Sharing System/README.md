##A Simple Centralized Peer to Peer File Sharing System
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
