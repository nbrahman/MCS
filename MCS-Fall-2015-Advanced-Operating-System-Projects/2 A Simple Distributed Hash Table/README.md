##A Simple Distributed Hash Table
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
