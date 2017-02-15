initStorageManager:
This function allocates and initializes the Page File Handle structure.

createPageFile:
This function accepts File Name as parameter and used to create the Page File with a block of size PAGE_SIZE characters initialized with '\0' character.
The function first checks if the file is already existing. If yes then it returns error (RC_FILE_ALREADY_FOUND) to the calling function.
If the file is not existing then a new file will be created for writing. If the file is opened successfully then a page of PAGE_SIZE characters initialized with '\0' is written inside the referred file. If there is any error while writing the data into Page File then RC flag is set with an error value (RC_WRITE_FAILED).
If the Page File creation process is successful then mgmtInfo strucutre members (File Type, File Mode, File Size, Last Modification Date, Last Access Date, and Creation Date) are initialized with appropriate values using "stat" strucutre. If initializaing the "stat" structure for the newly created Page File is unsuccessful then mgmtInfo strucutre members (File Type, File Mode, File Size, Last Modification Date, Last Access Date, and Creation Date) are initialized with NULL. Finally, RC flag is set with a success value (RC_OK).
Function is completed by printing and returning the RC flag status (either success or error using RC flag) to the calling function.

openPageFile:
This function accepts File Name and Page File Handle as parameters and opens the page file for reading in "Read Binary" mode.
If the file opening process results in an error the RC flag is set with an error value (RC_FILE_HANDLE_NOT_INIT).
If the file is opened successfully for reading then the Page File Handle strcture variables (File Name, Total no. of Pages, Current Page Position) are initialized with appropriate values. Finally, RC flag is set with a success value (RC_OK).
Function is completed by printing and returning the RC flag status (either success or error using RC flag) to the calling function.

closePageFile:
This function accepts Page File Handle as parameter. It is used to open and close the file defined by File Handle.
If the file is opened and closed successfully then the RC flag is set with a success value else it will be set with an error value (RC_FILE_NOT_FOUND).
Function is completed by printing and returning the RC flag status (either success or error using RC flag) to the calling function.

destroyPageFile:
This function accepts File Name as parameter. It is used to delete the file defined by File Name from the Secondary Storage.
If the file is deleted successfully then the RC flag is set with a success value else it will be set with an error value (RC_FILE_NOT_FOUND).
Function is completed by printing and returning the RC flag status (either success or error using RC flag) to the calling function.

readBlock:
This function accepts Page No., File Handle, and Memory Page Content as parameters. It is used to read the block (usually of PAGE_SIZE characters) pointed by Page No. variable from a file referred by File Handle. It reads the characters equal to the PAGE_SIZE from the block or page and stores the read contents into Memory Page Content pointer.
It first checks the total number of pages available in referred file. If the total number of pages are less than the Page No. argument then the RC flag is set with an error value (RC_READ_NON_EXISTING_PAGE).
If the Page No. arugment is lesser than the total number of pages then the file referred in Page File Handle is opened in "Read Binary" mode. If there are any errors while opening the file then the RC flag is set with an error value (RC_FILE_NOT_FOUND).
If the file is opened successfully then the Offset is computed as (Page No. * PAGE_SIZE). The File Pointer is set to Offset location and the contents starting at Offset location are read with length of PAGE_SIZE characters. The contents read are stored in Memory Page Content variable and the Page File is closed. Current Page Position information from the Page File Handle structure is updated and the RC flag is set with a success value (RC_OK).
Function is completed by printing and returning the RC flag status (either success or error using RC flag) to the calling function.

getBlockPos:
This function accepts Page Page File Handle as parameter and returns the Current Page Information from the Page File Handle structure passed as parameter.

readFirstBlock:
This function accepts File Handle, and Memory Page Content as parameters. It is used to read the first block (usually of PAGE_SIZE characters) from a file referred by File Handle. It reads the characters equal to the PAGE_SIZE from the block or page and stores the read contents into Memory Page Content pointer.
The file referred in Page File Handle is opened in "Read Binary" mode. If there are any errors while opening the file then the RC flag is set with an error value (RC_FILE_NOT_FOUND).
If the file is opened successfully then the contents starting at first location are read with length of PAGE_SIZE characters. The contents read are stored in Memory Page Content variable and the Page File is closed. Current Page Position information from the Page File Handle structure is updated and the RC flag is set with a success value (RC_OK).
Function is completed by printing and returning the RC flag status (either success or error using RC flag) to the calling function.

readPreviousBlock:
This function accepts File Handle, and Memory Page Content as parameters. It is used to read the one block (usually of PAGE_SIZE characters) before the current block (indicated by Current Page Position information) from a file referred by File Handle. It reads the characters equal to the PAGE_SIZE from the block or page and stores the read contents into Memory Page Content pointer.
The file referred in Page File Handle is opened in "Read Binary" mode. If there are any errors while opening the file then the RC flag is set with an error value (RC_FILE_NOT_FOUND).
If the file is opened successfully then the Offset is computed (as ((Current Page Position - 1) * PAGE_SIZE)), the contents starting at Offset location are read with length of PAGE_SIZE characters. The contents read are stored in Memory Page Content variable and the Page File is closed. Current Page Position information from the Page File Handle structure is updated and the RC flag is set with a success value (RC_OK).
Function is completed by printing and returning the RC flag status (either success or error using RC flag) to the calling function.

readCurrentBlock:
This function accepts File Handle, and Memory Page Content as parameters. It is used to read the block (usually of PAGE_SIZE characters) pointed by Current Page Position variable from a file referred by File Handle. It reads the characters equal to the PAGE_SIZE from the block or page and stores the read contents into Memory Page Content pointer.
The file referred in Page File Handle is opened in "Read Binary" mode. If there are any errors while opening the file then the RC flag is set with an error value (RC_FILE_NOT_FOUND).
If the file is opened successfully then the Offset is computed (Current Page Position * PAGE_SIZE), the contents starting at Offset location are read with length of PAGE_SIZE characters. The contents read are stored in Memory Page Content variable and the Page File is closed. Current Page Position information from the Page File Handle structure is updated as Current Page Position + 1 and the RC flag is set with a success value (RC_OK).
Function is completed by printing and returning the RC flag status (either success or error using RC flag) to the calling function.

readNextBlock:
This function accepts File Handle, and Memory Page Content as parameters. It is used to read the one block (usually of PAGE_SIZE characters) after the current block (indicated by Current Page Position information) from a file referred by File Handle. It reads the characters equal to the PAGE_SIZE from the block or page and stores the read contents into Memory Page Content pointer.
The file referred in Page File Handle is opened in "Read Binary" mode. If there are any errors while opening the file then the function returns an error (RC_FILE_NOT_FOUND) to the calling function.
If the file is opened successfully then the Offset is computed (as ((Current Page Position + 1) * PAGE_SIZE)), the contents starting at Offset location are read with length of PAGE_SIZE characters. The contents read are stored in Memory Page Content variable. The function returns success (RC_OK) to the calling function.

readLastBlock:
This function accepts File Handle, and Memory Page Content as parameters. It is used to read the first block (usually of PAGE_SIZE characters) from a file referred by File Handle. It reads the characters equal to the PAGE_SIZE from the block or page and stores the read contents into Memory Page Content pointer.
The file referred in Page File Handle is opened in "Read Binary" mode. If there are any errors while opening the file then the function returns an error (RC_FILE_NOT_FOUND) to the calling function.
If the file is opened successfully then the contents starting at (File Size (in characters) - PAGE_SIZE) location are read with length of PAGE_SIZE characters. The contents read are stored in Memory Page Content variable and the function returns success (RC_OK) to the calling function.

writeBlock:
This function accepts Page No., File Handle, and Memory Page Content as parameters. It is used to write the data stored in Memory Page Content variable to the block or page (usually of PAGE_SIZE characters) pointed by Page No. variable from a file referred by File Handle.
It first checks the total number of pages available in referred file. If the total number of pages are less than the Page No. argument then the function returns an error (RC_WRITE_FAILED) to the calling function.
If the Page No. arugment is greater than or equal to zero and lesser than the total number of pages then the file referred in File Handle is opened in "Write Binary" mode. The Offset is computed (as Page No. * PAGE_SIZE), the contents starting at Offset location are read with length of PAGE_SIZE characters. The contents read are stored in Memory Page Content variable. Current Page Position information from the File Handle structure is updated and the function returns success (RC_OK) to the calling function.

writeCurrentBlock:
This function writes the data pointed to by memPage to the current pointer position within the binary file. The above mentioned writeBlock function is called by passing the current page pointer position as page number. 
After the write is successful, the current pointer position needs to be updated.If the write functionality is successful it returns RC_OK else it return RC_WRITE_FAILED.

appendEmptyBlock:
This function appends a block of size PAGE_SIZE to the end of the existing file and initialises it to null. The total number of pages is incremented after the write is successful.
It returns RC_OK if the block is appended successfully else it returns RC_WRITE_FAILED.

ensureCapacity:
This function takes the number of pages to which the total number of pages needs to be increased to. We seek to the end of the file currently and write the desired number of bytes to null in order to increase the file size.
The total number of pages needs to be updated to the page number passed as parameter. If the write is successful return RC_OK else return RC_INCREASE_CAPACITY_FAILED.