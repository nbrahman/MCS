-----------------------------------
	Read ME 
-----------------------------------

1) InitIndexManager

This function is unsed to initialise the indexmanager that is used for the BTree implementation.On successfull initialisation, RC_OK is returned.

2) ShutDownBuffer

This function is used to deallocate the memories that was used for the implementation of the BTree like BtreeHandle and BufferManger handles.On successfull deallocation of memory, we return RC_OK as its return status.

3) CreateBTree

This function is used to create the Btree. The function accepts the idxId (Index Id), DataType of the key values that will be stored in the nodes and n that is the size of the nodes.
With the value that is passed in idxId we call the createPageFile function that creates the page file of the name that is passed as the arguement. on sucessfull creation of the file, we will initalize the values of the structure BtreeHandle 
with values of its members like keyType, idxId and mgmtData. On success, we will return RC_OK.
If the value passed to createPageFile is already present, we will return the value RC_FILE_ALREADY_
FOUND.

4) OpenBTree

This function is used to open the created BTree. The function accepts the idxID and the BTreeHanlde structure variable. We initialise the bufferPool by calling the InitBufferPool in FIFO method. And then we call the pinPage method.
After pinPage function call,we will save the value into a character pointer variable, data post which we call the unpinPage funcionality.
Once unpinPage is successfull, we will return RC_OK.

5) DeleteBTree

This function is used to delete the create BTree. It accepts the char pointer idxId as the parameter to the function call. And internally we will destroy the page file. Once 
the page file is successfully deleted, we will return RC_OK. If not we will return the error value.
 
6) CloseBTree

This function is used to close the Btree that is been created. It accepts the BTreeHandle value as the parameter. Internally,we will invoke the function call to shutDownBufferPool which accepts the buffer manager handle.
Once the buffer pool is shut down properly, RC_OK value is returned. If there was any error while shutting the bufferpool , we shall return the error code to confirm its failure while shutting down the buffer.

7) getNumNodes

This function is used to calculate the number of nodes that is present in the BTree that is created previously. Accepts the BTreeHandle as one parameter and another paramter which is of integer pointer named as result.
First we check if the Btree is empty, if the Btree is empty/NULL , we initialise result as 0 and return RC_OK as the function return value.
If the BTree is not empty, we will recursively call the same function i.e., getNumNodes for the nodes present in the Btree and then sum up the values and assign the value to the result variable and return RC-OK.



8) getNumEntries

This function is used to find the number of entries in the BTree that is created previously. Accepts the BTreeHandle as one parameter and another paramter which is of integer pointer named as result.
First we check if the Btree is empty, if the Btree is empty/NULL , we initialise result as 0 and return RC_OK as the function return value.
If the BTree is not empty, we will recursively call the same function i.e., getNumEntries for the nodes present in the Btree and then sum up the values and assign the value to the result variable and return RC-OK.


9)getKeyType

This function is used to find the key type of the entries in the BTree that is created previously. Accepts the BTreeHandle as one parameter and another paramter which is of integer pointer named as result.
First we check if the Btree is not empty, if the Btree is not empty/NULL , we initialise result as the value present in the tree handle and the vavriable keyType.


10) findKey

This function is used to find the key value in the BTree. The function accepts BTreeHandle , Value and RID as its parameters.
First we check if the tree is NULL or empty, if its not empty we will invoke searchKeys as the function ,if the searchKeys returns NULL , intialise the value to -1 and if not we will initalise the value of page and slot to the
corresponding value that is obtained
 

11) insertKey

This function is used to insert the keys that are passed as values to the function to be inserted into the Btree that is been create..
The insertKey function follows the basic rules that the B+Tree is followed while creating. It considers the number of entries in each node, the value to be inserted in each node etc.

12) deleteKey

This function is used to delete the key that is passed as value to the function to be deleted from the BTree that is present. If the key value passed is not found, we will return key not found, if not we will delete the key value from the Btree, 
still maintaing its implementation rules that needs to be followed.


13) openTreeScan

This function is used to open the tree for scanning the function in order to find the specific key value in the Btree.
The scanning function is mainly used to check if the key value has to be deleted or inserted
Returns RC_OK when it is success.


14) nextEntry

This function is called while we invoke the openTreeScan function, where it called recursively to find the particular key value in the BTree.
Returns RC_OK when the function call is successfull

15) closeTreeScan

CloseTree scan accepts the scanHandle to close the scanning functionality.Returns RC_OK when the function call is successfull.



