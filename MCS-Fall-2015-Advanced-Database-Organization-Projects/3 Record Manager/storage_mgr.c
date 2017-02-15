#include "stdlib.h"
#include "stdio.h"
#include "dberror.h"
#include "unistd.h"
#include "string.h"
#include "sys/types.h"
#include "sys/stat.h"
#include "storage_mgr.h"
#include "time.h"

#define NSBStrMgrMetaData "./NSBStrMgrMetaData.lst"
#define strFieldDelim '\t'
#define strLineDelim '\n'
FILE *fPageFile = NULL;
/*Global Variable declarations starts from here*/
SM_FileHandle *fHandle;
SM_PageHandle ph;
/* function to initialize Storage Manager starts from here*/
void initStorageManager ()
{
	/*code to initialise the SM_FileHandle values*/
	fHandle = (SM_FileHandle *) malloc (sizeof (SM_FileHandle));
	fHandle->fileName = NULL;
	fHandle->totalNumPages = 0;
	fHandle->curPagePos = -1;
	free(fHandle); /*Freeing up the allocated memory for SM_FileHandle*/
}
/* function to initialize Storage Manager ends over here*/

/* function to create Page File starts from here*/
RC createPageFile (char *fileName)
{
	RC flgRC;
	 /*Defining the file pointer to perform file operations*/
	char str[PAGE_SIZE] = {0};

	struct stat sb;
	/*Management Info related declarations starts here*/
	char *strFileType;
	char *strFileMode;
	unsigned int intFileSize;
	char *strFileSize;
	char *strLastModificationDateTime;
	char *strLastAccessDateTime;
	char *strCreationDateTime;
	/*Management Info related declarations ends here*/
	int intStatStatus;

	/*Function to check if the file is already existing,if exists send RC_FILE_ALREADY_FOUND else continue with the creation of page*/
	if(access(fileName,F_OK) !=-1)
	{
		flgRC = RC_FILE_ALREADY_FOUND;
		printError(flgRC);
		return flgRC;
	}
	/*File opening function call in Write Binary mode*/
	fPageFile = fopen (fileName, "wb+");
	if (fPageFile != NULL)
	{
		/*initialising the block to NULL values on successful opening of file using fwrite function*/
		fwrite (str, 1, PAGE_SIZE, fPageFile);
		if (ferror (fPageFile))
		{
			flgRC = RC_WRITE_FAILED;//Nikhil: no return that means the initialization code will be executed
		}
		else
		{
			/*Initializing mgmtInfo structure starts from here*/
			intStatStatus = stat (fileName, &sb);
			if (intStatStatus == 0)
			{
				switch (sb.st_mode & S_IFMT)
				{
					case S_IFBLK:
						strFileType = (char *) malloc (strlen ("Block Device"));
						strcpy (strFileType, "Block Device");
						break;
					case S_IFCHR:
						strFileType = (char *) malloc (strlen ("Character Device"));
						strcpy (strFileType, "Character Device");
						break;
					case S_IFDIR:
						strFileType = (char *) malloc (strlen ("Directory"));
						strcpy (strFileType, "Directory");
						break;
					case S_IFIFO:
						strFileType = (char *) malloc (strlen ("FIFO / Pipe"));
						strcpy (strFileType, "FIFO / Pipe");
						break;
					case S_IFLNK:
						strFileType = (char *) malloc (strlen ("SymLink"));
						strcpy (strFileType, "SymLink");
						break;
					case S_IFREG:
						strFileType = (char *) malloc (strlen ("Regular File"));
						strcpy (strFileType, "Regular File");
						break;
					case S_IFSOCK:
						strFileType = (char *) malloc (strlen ("Socket"));
						strcpy (strFileType, "Socket");
						break;
					default:
						strFileType = (char *) malloc (strlen ("Unknown?"));
						strcpy (strFileType, "Unknown?");
						break;
				}

				strFileMode = (char *) malloc (20);
				sprintf (strFileMode, "%lo",(unsigned long) sb.st_mode);

				
				strFileSize = (char *) malloc (100);
				intFileSize = (long long) sb.st_size;
				sprintf (strFileSize, "%lld", (long long) sb.st_size);
				
				strLastModificationDateTime = (char *) malloc (strlen (ctime(&sb.st_mtime)));
				strcpy (strLastModificationDateTime, (char *)(ctime(&sb.st_mtime)));

				
				strLastAccessDateTime = (char *) malloc (strlen (ctime(&sb.st_atime)));
				strcpy (strLastAccessDateTime, (char *)(ctime(&sb.st_atime)));

				
				strCreationDateTime = (char *) malloc (strlen (ctime(&sb.st_ctime)));
				
				strcpy (strCreationDateTime, (char *)(ctime(&sb.st_ctime)));
				
			}
			else
			{
				strFileType = NULL;
				strFileMode = NULL;
				strFileSize = NULL;
				strLastModificationDateTime = NULL;
				strLastAccessDateTime = NULL;
				strCreationDateTime = NULL;
			}

			
			fHandle = (SM_FileHandle*)malloc(sizeof(SM_FileHandle));
			fHandle->mgmtInfo = (SM_FileData*)malloc(sizeof(SM_FileData));
			SM_FileData *sm_fd = fHandle->mgmtInfo;
			
			sm_fd->FileType = (char *) malloc (strlen (strFileType));
			strcpy (sm_fd->FileType, strFileType);
			
			sm_fd->FileMode = (char *) malloc (strlen (strFileMode));
			strcpy (sm_fd->FileMode, strFileMode);
			
			sm_fd->FileSize = intFileSize;
			
			sm_fd->LastModificationDateTime = (char *) malloc (strlen (strLastModificationDateTime));
			strcpy (sm_fd->LastModificationDateTime, strLastModificationDateTime);
			
			sm_fd->LastAccessDateTime = (char *) malloc (strlen (strLastAccessDateTime));
			strcpy (sm_fd->LastAccessDateTime, strLastAccessDateTime);
			
			sm_fd->CreationDateTime = (char *) malloc (strlen (strCreationDateTime));
			strcpy (sm_fd->CreationDateTime, strCreationDateTime);
			/*Initializing mgmtInfo structure ends here*/
			flgRC = RC_OK;
			/*function to close the file that was opened for this functionality*/
			free(fHandle);
		}
	}
	else
	{
		flgRC = RC_FILE_HANDLE_NOT_INIT;
	}
	fclose (fPageFile);
	printError(flgRC);
	return flgRC;
}
/* function to create Page File ends over here*/

/* function to open existing Page File starts from here*/
RC openPageFile (char *fileName, SM_FileHandle *fHandle)
{
	//FILE *fp;
	RC flgRC;
	int intStatStatus,intNoOfPages;
	struct stat sb;
	/*File opening function call in read Binary mode*/
	fPageFile = fopen (fileName, "wb+");
	perror(fileName);
	if (fPageFile != NULL)
	{
		/*Assign the values for fileName and other attributes in SM_FileHandle pointer*/
		fHandle->fileName = fileName;
		
		fHandle->curPagePos = 0;
		
		intStatStatus = stat (fileName, &sb);
		if (intStatStatus == 0)
		{
			/*Calculating the total number of pages on opening the file successfully*/
			long intFileSize = (long long) sb.st_size;
			intNoOfPages = (int) intFileSize / PAGE_SIZE;
			if ((intFileSize % PAGE_SIZE)!=0)
			{
				intNoOfPages++;
			}
		}
		else
		{
			intNoOfPages = 0;
		}
		/*Updating the calculated value to the totalNumPages, member of SM_FileHandle*/
		fHandle->totalNumPages = intNoOfPages;
		flgRC = RC_OK;
	}
	else
	{
		flgRC = RC_FILE_HANDLE_NOT_INIT;
	}
	fclose(fPageFile);
	printError(flgRC);
	return flgRC;
}
/* function to open existing Page File ends over here*/

/*function to write block to the Page File on the Disk starts from here*/
RC writeBlock (int pageNum, SM_FileHandle *fHandle, SM_PageHandle memPage)
{
	/*required variables declarations for writeBlock function starts here*/
	FILE *filePointer; // define FILE pointer to point to the file opened
	int blockSize =0;// to check the size of the block written to disk
	int offset_write=0; //to calculate the offset to write to correct page
	int offset_initial = (fHandle->totalNumPages) * PAGE_SIZE;
	int i=0;
	char buffer[PAGE_SIZE]={'\0'};
	/*required variables declarations for writeBlock function ends here*/
	
	/*Check if the pageNum in the referring file has atleast one page*/
	if(pageNum>=0){
		/*If the pageNum value exceeds the totalNumPages, call ensureCapacity function*/
		if(pageNum+1>(fHandle->totalNumPages))
		{
			for (i=0;i<=(pageNum+1)-fHandle->totalNumPages;i++)
			{
					appendEmptyBlock(fHandle);
			}
		}
		/*function to open the file in read Binary mode*/
		filePointer = fopen(fHandle->fileName,"rb+"); // open the binary file in write mode
		/*seek the file pointer to the end of the file*/
		printf("The page number passed in writeBlock is %d\n",pageNum);
		fseek(filePointer,0,SEEK_SET);
		int position = ftell(filePointer);
		
		/*Find the offset to seek to from the end of the file to the desired location to do the write operation*/
			offset_initial = pageNum * PAGE_SIZE;
		/*seek the file pointer to the calculated value as above*/
		fseek(filePointer,offset_initial,SEEK_SET); //seek to the end of the file -> to set total number of pages of the file
		int pos = ftell(filePointer);

		/*write the file at the position pointed to by the file pointer using fwrite function*/
		for (i=0;i<PAGE_SIZE;i++)
		{
			if (i<strlen (memPage))
			{
				buffer[i] = *(memPage+i);
			}
			else
			{
				buffer[i] = '\0';
			}
		}
		blockSize = fwrite(buffer,1,PAGE_SIZE,filePointer);//write the data pointed to by memPage to file on disk pointed by filePointer
		int pos1 = ftell(filePointer);
		fHandle->curPagePos=pageNum;//update the current page position
	}
	if(blockSize>0)
	{
		return RC_OK;
	}
	else{
		printError(RC_WRITE_FAILED);
		return RC_WRITE_FAILED;
	}
}
/*function to write block to the Page File on the Disk ends over here*/

/*function to write current block to the Page File on the Disk starts from here*/
RC writeCurrentBlock (SM_FileHandle *fHandle, SM_PageHandle memPage)
{
	/*call the writeBlock function by passing the current page position as the desired page number to write to*/
	int returnValue = writeBlock(fHandle->curPagePos,fHandle,memPage);	
	return returnValue;
}
/*function to write current block to the Page File on the Disk ends over here*/

/*function to append a block to existing Page File starts from here*/
RC appendEmptyBlock (SM_FileHandle *fHandle)
{
	//printf("Inside append block");
	FILE *filePointer;
	char buffer[PAGE_SIZE]= {'\0'};
	filePointer=fopen(fHandle->fileName,"rb+");
	/*Seek to the end of the file*/
	fseek(filePointer,(fHandle->totalNumPages)*PAGE_SIZE,SEEK_SET);
	/*write an empty block at the end of the file*/
	int blockSize = fwrite(buffer,1,PAGE_SIZE,filePointer);
	fHandle->totalNumPages++;//increment the total number of pages in the file
	fclose(filePointer);
	if(blockSize>0)
		return RC_OK;
	else
	{	
		printError(RC_WRITE_FAILED);
		return RC_WRITE_FAILED;
	}
}
/*function to append a block to existing Page File ends over here*/

/*function to ensure Page Capacity starts from here*/
RC ensureCapacity (int numberOfPages, SM_FileHandle *fHandle)
{
	FILE *filePointer;
	char *buffer;
	int i = 0;
	if(fHandle->totalNumPages < numberOfPages){
		filePointer=fopen(fHandle->fileName,"rb+");
		/*Calculate the number of pages to increase*/
		int pagesToInitialise = numberOfPages - fHandle->totalNumPages;
		/*calculate the offset to seek to, for increasing the number of pages */
		int offset = (numberOfPages) * PAGE_SIZE;
		/*Seek to the calculated offset from the beginning of the file*/
		fseek(filePointer,offset,SEEK_SET);
		int begin = ftell(filePointer);
		/*The offset to seek to from the end of the file in order to initialise the blocks to null*/
		offset = (fHandle->totalNumPages - numberOfPages)*PAGE_SIZE;
		/*Seek to the calculated offset*/
		fseek(filePointer,offset,SEEK_CUR);
		begin = ftell(filePointer);
		/*Calculate the number of bytes to be written*/
		offset = (pagesToInitialise) * PAGE_SIZE;
		buffer = malloc(sizeof(offset));//allocate memory to the character pointer
		/*Write null for the calculated count(offset)*/		
		int blockSize = fwrite(buffer,1,offset,filePointer);		
		int finalPos = ftell(filePointer);
		/*Update the total number of pages*/
		fHandle->totalNumPages = numberOfPages;
		fclose(filePointer);
		free(buffer);
		return RC_OK;
	}
	else{
		printError(RC_INCREASE_CAPACITY_FAILED);
		return RC_INCREASE_CAPACITY_FAILED;
	}
}
/*function to ensure Page Capacity ends over here*/

/*function to get the current block position starts from here*/
int getBlockPos(SM_FileHandle *fHandle)
{
	/*Seek to the end*/
	/*find the size of SM_filehandle pointer*/
	return fHandle->curPagePos;
}
/*function to get the current block position ends over here*/

/*function to read the block starts from here*/
RC readBlock(int pagenum, SM_FileHandle *fHandle, SM_PageHandle memPage)
{
	char *p;
	FILE *fileHandle;
	char dataRead[PAGE_SIZE];
	int isValidPage=1;
	int offset;
	RC flgRC;
	SM_PageHandle *memPage1;

	/*code check to see if the pageNum passed as a parameter is valid and exists as part of the fileName*/
	if((pagenum>=0)&&(pagenum<(fHandle->totalNumPages)))
	{
		isValidPage=1;
	}
	else
	{
		isValidPage=0;
	}
	
	if(isValidPage==1)
	{
		/*function to open the file in read Binary mode*/
		fileHandle = fopen(fHandle->fileName,"rb");
		if(fileHandle!=NULL)
		{
			offset = (pagenum)*PAGE_SIZE;
			/* Seek to the beginning of the file in case it as the end or middle*/
			fseek(fileHandle,offset,SEEK_SET);

			fread(memPage,PAGE_SIZE,1,fileHandle);

			fclose(fileHandle);			

			/* Update the curPagePos to pageNum*/
			fHandle->curPagePos = pagenum;

			flgRC = RC_OK;

		}
		else
		{
			/*return the RC_FILE_NOT_FOUND if the file was unable to open*/			
			flgRC = RC_FILE_NOT_FOUND;
		}
	}
	else
	{
		/*return RC_READ_NON_EXISTING_PAGE if the pageNum doesnt fall in the given range*/
		flgRC = RC_READ_NON_EXISTING_PAGE;
	}
	printError(flgRC);
	return flgRC;
}
/*function to read the first block starts from here*/
RC readFirstBlock(SM_FileHandle *fHandle,SM_PageHandle memPage)
{
	FILE *fp;
	RC flgRC;
	/*opening the file in read Binary mode*/
	fp = fopen(fHandle->fileName,"rb");
	

	if(fp!=NULL)
	{
		/*seek to the beginning - not required as every page open will be at the beginning*/
		fseek(fp,0,SEEK_SET);
		/*read and print first block of 4096 bytes*/
		fread(memPage,PAGE_SIZE,1,fp);
		fclose(fp);
		/*update the curPagePos to 1 since we are reading the first block*/
		fHandle->curPagePos = 1;
		flgRC = RC_OK;
	}
	else
	{
		flgRC = RC_FILE_NOT_FOUND;
	}
	printError(flgRC);
	return flgRC;
}
/*function to read the first block ends over here*/

/*function to read last block starts from here*/
RC readLastBlock(SM_FileHandle *fHandle,SM_PageHandle memPage)
{
	FILE *fp;
	
	RC flgRC;
	/*file function to open the file in read Binary mode*/
	fp = fopen(fHandle->fileName,"rb");
	if(fp!=NULL)
	{
		/*seek to the last from the current pos*/
		/*after going to current end - seek back to 4096 to read the     last block.*/
		fseek(fp,-4096,SEEK_END);
		/*reading the last block of the file using fread function*/
		fread(memPage,PAGE_SIZE,1,fp);
		/*update the curPagePos to the totalNumPages as we read the last block of the file*/
		fHandle->curPagePos = fHandle->totalNumPages;
		fclose(fp);
		flgRC = RC_OK;
	}
	else
	{
		flgRC = RC_FILE_NOT_FOUND;
	}
	printError (flgRC);
	return flgRC;
}
/*function to read last block ends over here*/

/*function to read previous block starts from here*/
RC readPreviousBlock(SM_FileHandle *fHandle,SM_PageHandle memPage)
{
	FILE *fp;
	
	int offset;
	RC flgRC;

	/*function to open the file in read binary mode*/
	fp = fopen(fHandle->fileName,"rb");
	if(fp!=NULL)
	{
		/*calculate the offset to seek to the desired location*/
		offset = ((fHandle->curPagePos)-1)*PAGE_SIZE;
		
		/*seek to the previous block from current position by 4096*/
		fseek(fp,offset,SEEK_SET);
		/*Read thte data from the pointer position*/
		fread(memPage,PAGE_SIZE,1,fp);
		fclose(fp);
		flgRC = RC_OK;
	}
	else
	{
		flgRC = RC_FILE_NOT_FOUND;
	}
	printError (flgRC);
	return flgRC;
}
/*function to read previous block ends over here*/

/*function to read current block starts from here*/
RC readCurrentBlock(SM_FileHandle *fHandle,SM_PageHandle memPage)
{
	FILE *fp;
	
	RC flgRC;
	/*function to open the file in read binary mode*/
	fp = fopen(fHandle->fileName,"rb");
	int offset;
	if(fp!=NULL)
	{
		/*calculate the offset to seek to the desired location*/
		offset = ((fHandle->curPagePos)*PAGE_SIZE);
		
		/*Read thte data from the pointer position*/
		
		fread(memPage,PAGE_SIZE,1,fp);
		/*increment the curPagePos by 1 as we read the current block and have reached the end of it after reading*/
		fHandle->curPagePos++;
		fclose(fp);
		flgRC = RC_OK;
	}
	else
	{
		flgRC = RC_FILE_NOT_FOUND;
	}
	printError(flgRC);
	return flgRC;
}
/*function to read current block ends over here*/

/*function to read next block starts from here*/
RC readNextBlock(SM_FileHandle *fHandle,SM_PageHandle memPage)
{
	FILE *fp;
	
	/*function to open the file in read binary mode*/
	fp = fopen(fHandle->fileName,"rb");
	int offset;
	if(fp!=NULL)
	{
		/*calculate the offset to seek to the desired location*/
		offset = ((fHandle->curPagePos)+1)*PAGE_SIZE;
		/*seek to the next block from current position by 4096*/
		fseek(fp,offset,SEEK_CUR);
		/*Read the data from the pointer position*/
		fread(memPage,PAGE_SIZE,1,fp);
		/*increment the curPagePos by 1 as we read the current block and have reached the end of it after reading*/
		fHandle->curPagePos++;
		return RC_OK;
	}
	else
	{
		printError(RC_FILE_NOT_FOUND);
		return RC_FILE_NOT_FOUND;
	}
}
/*function to read next block ends over here*/

/*Function to destroy the Page File starts from here*/
RC destroyPageFile (char *fileName)
{
	RC fileDestroyStatus = remove(fileName);//deletes the file from the disk
	
	if(fileDestroyStatus == 0)
	{
		fileDestroyStatus = RC_OK;
	}
	else
	{
		fileDestroyStatus = RC_FILE_NOT_FOUND;
	}
	printError(fileDestroyStatus);
	return fileDestroyStatus;
}
/*Function to destroy the Page File ends over here*/

/*Function to close the Page File starts from here*/
RC closePageFile (SM_FileHandle *fHandle)
{
	FILE *filePointer;
	RC fileCloseStatus;
	filePointer = fopen(fHandle->fileName,"r");
	fileCloseStatus= fclose(filePointer);//deletes the file from the disk
	if(fileCloseStatus == 0)
	{
		fileCloseStatus = RC_OK;
	}
	else
	{
		fileCloseStatus = RC_FILE_NOT_FOUND;
	}
	printError(fileCloseStatus);
	return fileCloseStatus;
}
/*Function to close the Page File ends over here*/