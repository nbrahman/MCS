#include<unistd.h>
#include<string.h>
#include<stdbool.h>
#include <sys/stat.h>
#include <sys/types.h>
#include "storage_mgr.h"
#include "dberror.h"
#include "test_helper.h"
#include "buffer_mgr.h"
#include "dt.h"


#define BUFFER_SIZE 3

/*Global variable declaration starts here*/
BM_PageHandle *pageHandle;
SM_FileHandle *sm_fHandle;
int *pageNumbers;
int numberOfNodes;
int counter = 0;
int intSequence = -1;
char *strOriginalFileName;
PageNumber getReplacementPage (BM_BufferPool *const);
void getDetails (BM_BufferPool *const, int, int, int[*][7]);
RC readBlock1(int, SM_FileHandle *);
SM_PageHandle memPage;
int m=0;
int n=0;
/*structure for doubly linked list for FIFO and LRU replacement strat*/

/* function to initialize Buffer Manager starts from here*/
RC initBufferPool (BM_BufferPool *const bm, const char *const pageFileName,const int numPages, ReplacementStrategy strategy,
		void *stratData)
{
	BM_BufferPool *bmTemp;
	RC flgRC;
	char* strFileName;
	printf("Allocating values to buffer variables\n");
	bmTemp = (BM_BufferPool*) malloc (sizeof (BM_BufferPool));
	bmTemp->mgmtData = (BufferInfo*)malloc(sizeof(BufferInfo));
	printf("Allocating values to buffer variables\n");
	int i;
	Node *prevNode, *header, *curNode;

	printf("Before alloc of mempage. Its address is %p\n",memPage);
	memPage = (SM_PageHandle)malloc (PAGE_SIZE);
	printf("After memPage\n");
	char str[PAGE_SIZE] = {'\0'};
	printf("Temp variables for file name\n");
	strFileName = (char*) malloc (strlen (pageFileName)+5);
	strOriginalFileName = (char*) malloc (strlen (pageFileName)+5);
	strcpy (strFileName, pageFileName);
	strcpy (strOriginalFileName, pageFileName);
	sm_fHandle = (SM_FileHandle*) malloc (sizeof (SM_FileHandle));
	sm_fHandle->fileName = (char*) malloc (strlen (pageFileName)+5);
	strcpy (sm_fHandle->fileName, strFileName);
	openPageFile (strFileName, sm_fHandle);
	bmTemp->mgmtData->fHandle = sm_fHandle;
	bmTemp->numPages = numPages;
	bmTemp->pageFile =(char*) strFileName;
	prevNode = NULL;
	header = NULL;
	printf("Before for loop\n");
	for (i=0; i<numPages;i++)
	{
		curNode = (Node*)malloc(sizeof (Node));
		curNode->frameData = (BM_PageHandle *)malloc(sizeof(BM_PageHandle));
		curNode->frameData->pageNum = -1;
		curNode->frameData->data = (char *) malloc(PAGE_SIZE);
		strcpy (curNode->frameData->data, str);
		curNode->rightLink = NULL;
		curNode->leftLink = prevNode;
		curNode->intDirtyFlag = 0;
		curNode->intFixCount = 0;
		curNode->intReadIO = 0;
		curNode->intWriteIO = 0;
		curNode->intAccessOrder = 0;
		curNode->intSequence = -1;

		if (header==NULL)
		{
			header = curNode;
			bmTemp->mgmtData->header = header;
		}
		if (prevNode != NULL)
		{
			prevNode->rightLink = curNode;
		}
		prevNode = curNode;
	}
	printf("After for loop\n copying is going on\n");
	bmTemp->strategy = strategy;
	flgRC = RC_OK;
	*bm = *bmTemp;
	bm->mgmtData->fHandle->fileName = (char *)pageFileName;
	bm->pageFile = (char *)pageFileName;
	//free(curNode);
	return flgRC;
}
/* function to initialize Buffer Manager ends over here*/

/*function to shutdown Buffer Manager starts from here*/
RC shutdownBufferPool(BM_BufferPool *const bm)
{
	RC flgRC;
	Node *temp;
	if (bm->mgmtData->header != NULL)
	{
		temp = bm->mgmtData->header;
		int i = 0;
		int intTotalPinCount = 0;
		flgRC = forceFlushPool(bm);
		while (temp != NULL)
		{
			if (flgRC == RC_OK)
			{
				temp->intDirtyFlag = 0;//mark the entry as clean
				intTotalPinCount = intTotalPinCount + temp->intFixCount;
				temp = temp->rightLink;
			}
			else
			{
				return flgRC;
			}
		}
		if (intTotalPinCount == 0)
		{
			if (bm->mgmtData->header != NULL)
			{
				temp = bm->mgmtData->header;
				while (temp != NULL)
				{
					free (temp->frameData->data);
					free (temp->frameData);
					temp->leftLink = NULL;
					temp = temp->rightLink;
				}
			}
			free (bm->mgmtData->fHandle);
			free (bm->mgmtData->header);
			free (bm->mgmtData);
			free (bm->pageFile);
		}
		flgRC = RC_OK;
		return flgRC;
	}
	else
	{
		flgRC = RC_ERROR_BUFFER;
		return flgRC;
	}
}
/* function to shutdown Buffer Manager ends over here*/

/* function to forcefully write down all dirty page from Buffer Manager starts from here*/
RC forceFlushPool(BM_BufferPool *const bm)
{
	RC flgRC;
	Node *temp;
	if (bm->mgmtData->header != NULL)
	{
		temp = bm->mgmtData->header;
		int i = 0;
		while (temp != NULL)
		{
			if ((temp->intDirtyFlag == 1) && (temp->intFixCount == 0))
			{
				flgRC = forcePage (bm, temp->frameData);

				if (flgRC == RC_OK)
				{
					temp->intDirtyFlag = 0;//mark the entry as clean
				}
				else
				{
					return flgRC;
				}
			}
			temp = temp->rightLink;
		}
		flgRC = RC_OK;
	}
	else
	{
		flgRC = RC_ERROR_BUFFER;
	}
	return flgRC;
}
/* function to forcefully write down all dirty page from Buffer Manager ends over here*/

/* function to get dirty flag status from Buffer Manager starts from here*/
bool *getDirtyFlags (BM_BufferPool *const bm)
{
	Node *temp;
	bool *arrDirtyStatus = (bool *)malloc((sizeof(bool))*(bm->numPages));
	temp = bm->mgmtData->header;
	int k = 0;
	if (temp != NULL)
	{
		temp = bm->mgmtData->header;
		int i = 0;
		while (temp != NULL)
		{
			if (temp->intDirtyFlag == 1)
			{
				arrDirtyStatus[i] = true;
			}
			else
			{
				arrDirtyStatus[i] = false;
			}
			i++;
			temp = temp->rightLink;
		}
	}
	return arrDirtyStatus;
}
/* function to get dirty flag status from Buffer Manager ends over here*/

// Buffer Manager Interface Access Pages
RC markDirty (BM_BufferPool *const bm, BM_PageHandle *const page)
{
	int i, j;
	int arrDetailsResult[bm->numPages][7];
	RC flgRC;
	Node* temp;
	int intPageFound=-1;
	if (bm->mgmtData->header != NULL)
	{
		temp = bm->mgmtData->header;
		while ((temp != NULL) && (intPageFound == -1))
		{
			if (temp->frameData->pageNum == page->pageNum)
			{
				temp->intDirtyFlag = 1;
				intPageFound = 0;
			}
			temp = temp->rightLink;
		}
		if (intPageFound == -1)
		{
			flgRC = RC_PAGEFRAME_NOT_FOUND;
			getDetails(bm, bm->numPages, 7, arrDetailsResult);
			return flgRC;
		}
		else
		{
			getDetails(bm, bm->numPages, 7, arrDetailsResult);
			flgRC = RC_OK;
			return flgRC;
		}
	}
	else
	{
		getDetails(bm, bm->numPages, 7, arrDetailsResult);
		flgRC = RC_BUFFER_EMPTY;
		return flgRC;
	}
}

//Method to unpin the page whose pageNum is sent as a parameter
RC unpinPage (BM_BufferPool *const bm, BM_PageHandle *const page)
{
	int i, j;
	int arrDetailsResult[bm->numPages][7];
	RC flgRC;
	Node* temp;
	int intPageFound=-1;
	if (bm->mgmtData->header != NULL)
	{
		temp = bm->mgmtData->header;
		while ((temp != NULL) && (intPageFound == -1))
		{
			if (temp->frameData->pageNum == page->pageNum)
			{
				temp->intFixCount = temp->intFixCount - 1;
				intPageFound = 0;
			}
			temp = temp->rightLink;
		}
		if (intPageFound == -1)
		{
			getDetails(bm, bm->numPages, 7, arrDetailsResult);
			flgRC = RC_PAGEFRAME_NOT_FOUND;
			return flgRC;
		}
		else
		{
			getDetails(bm, bm->numPages, 7, arrDetailsResult);
			flgRC = RC_OK;
			return flgRC;
		}
	}
	else
	{
		getDetails(bm, bm->numPages, 7, arrDetailsResult);
		flgRC = RC_BUFFER_EMPTY;
		return flgRC;
	}
}

//Function to write the data from the buffer to the disk.
RC forcePage (BM_BufferPool *const bm, BM_PageHandle *const page)
{
	Node *temp;
	int i=0,j=0, intNodeFound = -1;
	RC returnValue = writeBlock(page->pageNum, bm->mgmtData->fHandle, (SM_PageHandle)page->data);
	if(returnValue == RC_OK)
	{
		if (bm->mgmtData->header != NULL)
		{
			temp = bm->mgmtData->header;
			while ((temp != NULL) && (intNodeFound == -1))
			{
				if(temp->frameData->pageNum == page->pageNum)
				{
					temp->intDirtyFlag = 0;
					temp->intWriteIO++;
				}
				temp = temp->rightLink;
			}
		}
		return RC_OK;
	}
	else
	{
		return RC_ERROR_BUFFER;	
	}
}

//Function to pin the page and apply the replacement stratergy.
RC pinPage (BM_BufferPool *const bm, BM_PageHandle *const page, const PageNumber pageNum)
{
	int arrDetailsResult[bm->numPages][7];
	char str[PAGE_SIZE] = {'\0'};
	PageNumber pgToBeReplaced;
	RC flgRC;
	int i, j;
	int intPageFound = -1;
	int intFreePagesCount = 0;
	Node *temp, *temp2;
	memPage = (char *)page->data;
	if (bm->mgmtData->header != NULL)
	{
		temp = bm->mgmtData->header;
		temp2 = bm->mgmtData->header;
		while ((temp != NULL) && (intPageFound == -1))
		{
			if (temp->frameData->pageNum == (int)pageNum)
			{
				intPageFound = 0;
				//(*page).data = temp->frameData->data;
				(*page).pageNum = temp->frameData->pageNum;
				temp->intFixCount = temp->intFixCount + 1;
				counter++;
				temp->intAccessOrder = counter;
			}
			else if (temp->frameData->pageNum == -1)
			{
				intFreePagesCount++;
			}
			temp = temp->rightLink;
		}

		if (intPageFound == 0)
		{
			return RC_OK;
		}
		//page unavailable so checking for empty frames and page loading code starts from here
		else if ((intPageFound == -1) && (intFreePagesCount > 0))
		{
			bm->mgmtData->header->intReadIO=bm->mgmtData->header->intReadIO+1;;
			if (pageNum >= bm->mgmtData->fHandle->totalNumPages)
			{
				memPage = str;
			}
			else
			{
				flgRC = readBlock1 (pageNum, bm->mgmtData->fHandle);
				if(flgRC == 0)
					temp2=bm->mgmtData->header;
			}
			if (bm->mgmtData->header != NULL)
			{
				temp = bm->mgmtData->header;
				while ((temp != NULL) && (intPageFound == -1))
				{
					if (temp->frameData->pageNum == -1)
					{
						if (pageNum < bm->mgmtData->fHandle->totalNumPages)
						{
							strcpy (temp->frameData->data, memPage);
						}
						temp->frameData->pageNum = pageNum;
						temp->intFixCount = temp->intFixCount + 1;
						counter++;
						temp->intAccessOrder = counter;//temp->intAccessOrder + 1;
						intPageFound = 0;
						(*page).data = temp->frameData->data;
						(*page).pageNum = temp->frameData->pageNum;
						intSequence++;
						*(page)=*(temp->frameData);
						temp->intSequence = intSequence;
					}
					temp = temp->rightLink;
				}
			}
			flgRC = RC_OK;
			getDetails(bm, bm->numPages, 7, arrDetailsResult);
			return flgRC;
		}
		//Replacement code starts from here
		else if ((intPageFound == -1) && (intFreePagesCount <= 0))
		{
			bm->mgmtData->header->intReadIO++;
			pgToBeReplaced = getReplacementPage(bm);
			if (pgToBeReplaced != -1)
			{
				if (bm->mgmtData->header != NULL)
				{
					temp = bm->mgmtData->header;
					while ((temp!= NULL) && (intPageFound == -1))
					{
						if (temp->frameData->pageNum == pgToBeReplaced)
						{
							flgRC = forcePage (bm, temp->frameData);
							if(bm->strategy == RS_LRU)
							{
								temp->intWriteIO = 0;
							}
							if (flgRC == RC_OK)
							{
								temp->intDirtyFlag = 0;

								strcpy (bm->mgmtData->fHandle->fileName, strOriginalFileName);
								strcpy (bm->pageFile, strOriginalFileName);
								if (pageNum >= bm->mgmtData->fHandle->totalNumPages)
								{

									memPage = str;
								}
								else
								{

									flgRC = readBlock1 (pageNum, bm->mgmtData->fHandle);
								}
								if (pageNum < bm->mgmtData->fHandle->totalNumPages)
								{
									//printf ("Inside pinPage21\n");
									strcpy (temp->frameData->data, memPage);
								}
								temp->frameData->pageNum = pageNum;
								temp->intFixCount = 1;
								counter++;
								temp->intAccessOrder = counter++;
								intPageFound = 0;
								(*page).data = temp->frameData->data;
								(*page).pageNum = temp->frameData->pageNum;
								intSequence++;
								temp->intSequence = intSequence;
								printf("Before getDetails in pinPage function \n");
								getDetails(bm, bm->numPages, 7, arrDetailsResult);
								flgRC = RC_OK;
								return flgRC;
							}
							else
							{

								getDetails(bm, bm->numPages, 7, arrDetailsResult);
								return flgRC;
							}
							if (intPageFound == -1)
							{
								getDetails(bm, bm->numPages, 7, arrDetailsResult);
								flgRC = RC_PAGEFRAME_NOT_FOUND;
								return flgRC;
							}
						}
						else
						{
							getDetails(bm, bm->numPages, 7, arrDetailsResult);
							temp = temp->rightLink;
						}
					}
				}
				else
				{

					getDetails(bm, bm->numPages, 7, arrDetailsResult);
					flgRC = RC_ERROR_BUFFER;
					return flgRC;
				}
			}
			else
			{

				getDetails(bm, bm->numPages, 7, arrDetailsResult);
				flgRC = RC_ERROR_UNPIN;
				return flgRC;
			}
		}
		else
		{

			getDetails(bm, bm->numPages, 7, arrDetailsResult);
			flgRC = RC_PAGEFRAME_NOT_FOUND;
			return flgRC;
		}
	}
	else
	{
		getDetails(bm, bm->numPages, 7, arrDetailsResult);
		flgRC = RC_ERROR_BUFFER;
		return flgRC;
	}
}

//function to get the fixCount for each page
int *getFixCounts (BM_BufferPool *const bm)
{
	int *fixCountArray = malloc(bm->numPages*sizeof(int));
	Node *temp;
	int i=0;
	if (bm->mgmtData->header != NULL)
	{
		temp = bm->mgmtData->header;
		while (temp != NULL)
		{
			fixCountArray[i] = temp->intFixCount;
			i++;
			temp = temp->rightLink;
		}
	}
	return fixCountArray;
}

//function to get the fixCount for each page
PageNumber *getFrameContents (BM_BufferPool *const bm)
{
	PageNumber* frameContents = malloc(bm->numPages*sizeof(PageNumber));
	Node *temp;
	int i=0;
	if (bm->mgmtData->header != NULL)
	{
		temp = bm->mgmtData->header;
		while (temp != NULL)
		{
			if (temp->frameData->pageNum != -1)
			{
				frameContents[i]= temp->frameData->pageNum;
			}
			else
			{
				frameContents[i]= NO_PAGE;
			}
			i++;
			temp = temp->rightLink;
		}
	}
	return frameContents;
}

//Procedure to get the array of integers for read IO operations
int getNumReadIO (BM_BufferPool *const bm)
{
	int numReadIOArray=0;
	Node *temp;
	if (bm->mgmtData->header != NULL)
	{
		numReadIOArray = bm->mgmtData->header->intReadIO;
	}
	return numReadIOArray;
}

//Procedure to get the array of integers for write IO functionality
int getNumWriteIO (BM_BufferPool *const bm)
{
	int numWriteIOArray=0; // integer to store the number of writeIO
	Node *temp;
	if (bm->mgmtData->header != NULL)
	{
		numWriteIOArray = bm->mgmtData->header->intWriteIO;
	}
	return numWriteIOArray;
}

//function to get the fixCount for each page
void getDetails (BM_BufferPool *const bm, int intRows, int intCols, int arrDetails[intRows][intCols])
{
	Node *temp;
	int i=0;
	if (bm->mgmtData->header != NULL)
	{
		temp = bm->mgmtData->header;
		while (temp != NULL)
		{
			arrDetails[i][0] = temp->frameData->pageNum;//page number
			arrDetails[i][1] = temp->intDirtyFlag;//dirty flag
			arrDetails[i][2] = temp->intFixCount;//fix count
			arrDetails[i][3] = temp->intReadIO;//read io
			arrDetails[i][4] = temp->intWriteIO;//write io
			arrDetails[i][5] = temp->intAccessOrder;//LRU Data
			arrDetails[i][6] = temp->intSequence;//FIFO Sequence

			printf ("%d\t%d\t%d\t%d\t%d\t%d\t%d\t%s\t%p\n", arrDetails[i][0], arrDetails[i][1], arrDetails[i][2], arrDetails[i][3],
					arrDetails[i][4], arrDetails[i][5], arrDetails[i][6], temp->frameData->data, temp->frameData);
			i++;
			temp = temp->rightLink;
		}
	}
}

PageNumber getReplacementPage (BM_BufferPool *const bm)
{
	PageNumber pgReplacement = -1;
	int intTempSequence = -1;
	int i, j;
	int arrDetailsResult[bm->numPages][7];
	getDetails (bm, bm->numPages, 7, arrDetailsResult);
	switch (bm->strategy)
	{
	case RS_FIFO:
		for (i=0;i<bm->numPages;i++)
		{
			if (arrDetailsResult[i][2] == 0)
			{
				if (intTempSequence == -1)
				{
					pgReplacement = arrDetailsResult[i][0];
					intTempSequence = arrDetailsResult[i][6];
				}
				else if (intTempSequence > arrDetailsResult[i][6])
				{
					pgReplacement = arrDetailsResult[i][0];
					intTempSequence = arrDetailsResult[i][6];
				}
			}
		}
		break;
	case RS_LRU:
		for (i=0;i<bm->numPages;i++)
		{
			if (arrDetailsResult[i][2] == 0)
			{
				if (intTempSequence == -1)
				{
					pgReplacement = arrDetailsResult[i][0];
					intTempSequence = arrDetailsResult[i][5];
				}
				else if (intTempSequence > arrDetailsResult[i][5])
				{
					pgReplacement = arrDetailsResult[i][0];
					intTempSequence = arrDetailsResult[i][5];
				}
			}
		}
		break;
	}
	return pgReplacement;
}

RC readBlock1(int pagenum, SM_FileHandle *fHandle)
{
	printf ("Inside readBlock1\n");
	char *p;
	FILE *fileHandle = NULL;
	char dataRead[PAGE_SIZE];
	int isValidPage=1;
	int offset;
	RC flgRC;

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

		fileHandle = fopen(fHandle->fileName,"r+");

		perror(fHandle->fileName);

		if(fileHandle!=NULL)
		{

			offset = (pagenum)*PAGE_SIZE;
			/* Seek to the beginning of the file in case it as the end or middle*/
			fseek(fileHandle,offset,SEEK_SET);
			/*Reading the actual data from the given directory/path*/

			fread(memPage,PAGE_SIZE,1,fileHandle);
			/* Update the curPagePos to pageNum*/
			fHandle->curPagePos = pagenum;
			flgRC = RC_OK;
			fclose(fileHandle);
			return flgRC;
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
	//fclose(fileHandle);
	printError(flgRC);
	fclose(fileHandle);
	return flgRC;
}
