# MCS Fall 2015 Advanced Database Organization Projects
This repository contains the Assignments / Projects done for Advanced Database Organization (ADO) course during my Masters Graduation in Computer Science in Fall 2015.
All the Assignments / Projects are developed using C in Linux. These assignments were done by me & my two teammates.

##1. Storage Manager
The goal of this assignment was to implement a simple Storage Manager - a module that is capable of reading blocks from a file on disk into memory and writing blocks from memory to a file on disk. The Storage Manager deals with pages (blocks) of fixed size (PAGE_SIZE). In addition to reading and writing pages from a file, it provides methods for creating, opening, and closing files. The Storage Manager has to maintain several types of information for an open file: The number of total pages in the file, the current page position (for reading and writing), the file name, and a POSIX file descriptor or FILE pointer.

##2. Buffer Manager
The Buffer Manager manages a fixed number of pages in memory that represent pages from a page file managed by the storage manager implemented in assignment 1. The memory pages managed by the Buffer Manager are called page frames or frames for short. We call the combination of a page file and the page frames storing pages from that file a Buffer Pool. The Buffer Manager should be able to handle more than one open buffer pool at the same time. However, there can only be one Buffer Pool for each page file. Each Buffer Pool uses one Page Replacement Strategy that is determined when the Buffer Pool is initialized. FIFO and LRU Page Replacement Strategies were implemented as part of this assignment.

##3. Record Manager
The Record Manager handles tables with a fixed schema. Clients can insert records, delete records, update records, and scan through the records in a table. A scan is associated with a search condition and only returns records that match the search condition. Each table should be stored in a separate page file and Record Manager accesses the pages of the file through the Buffer Manager implemented in the earlier assignment.

##4. B+-Tree
In this assignment we have implemented a B+-Tree Index. The index should be backed up by a page file and pages of the index should be accessed through Buffer Manager. Each node occupies one page. A B+-Tree stores pointer to records (the RID introduced in the Record Manager assignment) index by a keys of a given datatype. Pointers to intermediate nodes are represented by the page number of the page the node is stored in.