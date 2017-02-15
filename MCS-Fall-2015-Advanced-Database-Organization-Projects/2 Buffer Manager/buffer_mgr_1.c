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
//BM_BufferPool *bm;
BM_PageHandle *pageHandle;
SM_FileHandle *sm_fHandle;
int *pageNumbers;
int numberOfNodes;
int counter = 0;
int intSequence = -1;

//BufferInfo *bufferInfo;
//bufferInfo = (BufferInfo *)malloc(sizeof(BufferInfo));
//bm->mgmtData = bufferInfo;

//bufferInfo->bufferDetailsArray =(int *) malloc(sizeof(int));

int m=0;
int n=0;
/*structure for doubly linked list for FIFO and LRU replacement strat*/

/*
void initArray(BM_BufferPool *bm,int numPages)
{
	//int arraySize = sizeof(((BufferInfo *)bm->mgmtData)->bufferDetailsArray);
	printf("The array is being initialised\n");
	((BufferInfo *)(*bm).mgmtData)->bufferDetailsArray =(int **)malloc(numPages*sizeof(int *));
	for(m=0;m<6;m++)
	{
		((BufferInfo *)(*bm).mgmtData)->bufferDetailsArray[m] = (int *) malloc(6*sizeof(int));
	}
}
*/
// to be assigned to mgmtData in initBufferPool method and also call initStorageManager inside this method to initialise the file and page handle pointers

//**Node *header,*temp, *curNode, *prevNode;

/* function to initialize Buffer Manager starts from here*/
/*
RC initBufferPool2 (BM_BufferPool *const bm, const char *const pageFileName,const int numPages, ReplacementStrategy strategy,
 void *stratData)
{
	RC flgRC;
	//BM_PageHandle *page;
	printf("Inside initBufferPool\n");
	(*bm).mgmtData = (BufferInfo*)malloc(sizeof(BufferInfo));
	((BufferInfo *)bm->mgmtData)->fHandle = (SM_FileHandle *)malloc(sizeof(SM_FileHandle));
	(((BufferInfo *)bm->mgmtData)->fHandle)->fileName = (char *)malloc(sizeof(char));
	((BufferInfo *)bm->mgmtData)->ph = (SM_PageHandle)malloc(sizeof(char));
	if(access(pageFileName,F_OK) == 0)
	{
		printf("Inside access\n");
		curNode = (Node*)malloc(sizeof (Node));
		curNode->frameData = (BM_PageHandle *)malloc(sizeof(BM_PageHandle));
		curNode->frameData->data = (char *) malloc(PAGE_SIZE);
		//**curNode->rightLink = (Node*) malloc(sizeof(Node));
		//**curNode->leftLink = (Node*) malloc(sizeof(Node));

		header = (Node*)malloc(sizeof (Node));
		header->frameData = (BM_PageHandle *)malloc(sizeof(BM_PageHandle));
		header->frameData->data = (char *) malloc(PAGE_SIZE);
		//**header->rightLink = (Node) malloc(sizeof(Node));
		//**header->leftLink = (Node) malloc(sizeof(Node));

		char str[PAGE_SIZE] = {'\0'};
		//BufferInfo *bufferInfo;
		(*bm).numPages = numPages;
		printf("The number of pages is %d\n",(*bm).numPages);
		(*bm).pageFile = (char*)malloc(sizeof(char));
		strcpy ((*bm).pageFile, pageFileName);
		strcpy((char *)((SM_FileHandle *)(((BufferInfo *)bm->mgmtData)->fHandle)->fileName),pageFileName);
		printf("The file name is %s\n",(char *)((SM_FileHandle *)(((BufferInfo *)bm->mgmtData)->fHandle)->fileName));
		(*bm).strategy = strategy;
		initArray(bm,numPages);
		int i=0;
		//Initializing the Doubly Linked List for Buffer Manager Pages
		//**curNode->leftLink = NULL;
		//**curNode->rightLink = NULL;
		//**header->leftLink = NULL;
		//**header->rightLink = NULL;
		//**curNode->frameData = NULL;
		//**header->frameData = NULL;
		//**flgRC = RC_OK;
	}
	else
	{
		flgRC = RC_FILE_NOT_FOUND;
	}
	printf("The file name is %s\n",(char *)((SM_FileHandle *)(((BufferInfo *)bm->mgmtData)->fHandle)->fileName));
	return flgRC;
}
*/
/* function to initialize Buffer Manager ends over here*/

/* function to initialize Buffer Manager starts from here*/
RC initBufferPool (BM_BufferPool *const bm, const char *const pageFileName,const int numPages, ReplacementStrategy strategy,
 void *stratData)
{
	BM_BufferPool *bmTemp;
	RC flgRC;
	char* strFileName;
	//printf("Inside initBufferPool\n");
	bmTemp = (BM_BufferPool*) malloc (sizeof (BM_BufferPool));
	bmTemp->mgmtData = (BufferInfo*)malloc(sizeof(BufferInfo));
	int i;
	Node *prevNode, *header, *curNode;

	char str[PAGE_SIZE] = {'\0'};
	if(access(pageFileName,F_OK) == 0)
	{
		strFileName = (char*) malloc (strlen (pageFileName));
		strcpy (strFileName, pageFileName);

		sm_fHandle = (SM_FileHandle*) malloc (sizeof (SM_FileHandle));
		openPageFile (strFileName, sm_fHandle);
		bmTemp->mgmtData->fHandle = sm_fHandle;

		bmTemp->numPages = numPages;
		//printf("The number of pages is %d\n",bmTemp->numPages);
		bmTemp->pageFile = (char*)malloc(sizeof(char));
		bmTemp->pageFile = strFileName;

		//printf("Inside access\n");

		prevNode = NULL;
		header = NULL;
		for (i=0; i<numPages;i++)
		{
			//printf ("Inside initBufferPool 1\n");
			curNode = (Node*)malloc(sizeof (Node));
			//printf ("Inside initBufferPool 2\n");
			curNode->frameData = (BM_PageHandle *)malloc(sizeof(BM_PageHandle));
			//printf ("Inside initBufferPool 3\n");
			curNode->frameData->pageNum = -1;
			//printf ("Inside initBufferPool 4\n");
			curNode->frameData->data = (char *) malloc(PAGE_SIZE);
			//printf ("Inside initBufferPool 5\n");
			strcpy (curNode->frameData->data, str);
			//printf ("Inside initBufferPool 6\n");
			curNode->rightLink = NULL;
			//printf ("Inside initBufferPool 7\n");
			curNode->leftLink = prevNode;
			//printf ("Inside initBufferPool 8\n");
			curNode->intDirtyFlag = -1;
			//printf ("Inside initBufferPool 9\n");
			curNode->intFixCount = 0;
			//printf ("Inside initBufferPool 10\n");
			curNode->intReadIO = 0;
			//printf ("Inside initBufferPool 11\n");
			curNode->intWriteIO = -1;
			//printf ("Inside initBufferPool 12\n");
			curNode->intAccessOrder = 0;
			//printf ("Inside initBufferPool 13\n");
			curNode->intSequence = -1;
			//printf ("Inside initBufferPool 13\n");

			if (header==NULL)
			{
				//printf ("Inside initBufferPool 14\n");
				header = curNode;
				bmTemp->mgmtData->header = header;
				//printf ("Current Node's Address is %p\n", curNode);
				//printf ("Header Node's FrameData Address is %p\n", header);
				//printf ("Inside initBufferPool 15\n");
			}
			//printf ("Current Node's Address is %p\n", curNode);
			//printf ("Header Node's FrameData Address is %p\n", header);

			if (prevNode != NULL)
			{
				//printf ("Inside initBufferPool 16\n");
				prevNode->rightLink = curNode;
				//printf ("Inside initBufferPool 17\n");
			}
			//printf ("Inside initBufferPool 18\n");
			prevNode = curNode;
			//printf ("Inside initBufferPool 19\n");
		}
		if (header != NULL)
		{
			//printf ("Inside initBufferPool 20\n");
			curNode = header;
			//printf ("Inside initBufferPool 21\n");
			while (curNode != NULL)
			{
				//printf ("Current Node's Address is %p\n", curNode);
				//printf ("Header Node's FrameData Address is %p\n", header);
				//printf ("Inside initBufferPool 22\n");
				if (curNode->leftLink != NULL)
				{
					//printf ("Previous Node's Address is %p\n", curNode->leftLink);
					//printf ("Previous Node's FrameData Address is %p\n", curNode->leftLink->frameData);
					//printf ("Previous Node's FrameData->pageNum is %d\n", curNode->leftLink->frameData->pageNum);
					//printf ("Previous Node's FrameData->data is %s\n", curNode->leftLink->frameData->data);
					//printf ("Previous Node's intDirtyFlag is %d\n", curNode->leftLink->intDirtyFlag);
					//printf ("Previous Node's intFixCounts is %d\n", curNode->leftLink->intFixCount);
					//printf ("Previous Node's intReadIO is %d\n", curNode->leftLink->intReadIO);
					//printf ("Previous Node's intWriteIO is %d\n", curNode->leftLink->intWriteIO);
					//printf ("Previous Node's intAccessOrder is %d\n", curNode->leftLink->intAccessOrder);
					//printf ("Previous Node's intSequence is %d\n", curNode->leftLink->intSequence);
				}
				//printf ("Current Node's Address is %p\n", curNode);
				//printf ("Current Node's FrameData Address is %p\n", curNode->frameData);
				//printf ("Current Node's FrameData->pageNum is %d\n", curNode->frameData->pageNum);
				//printf ("Current Node's FrameData->data is %s\n", curNode->frameData->data);
				//printf ("Current Node's intDirtyFlag is %d\n", curNode->intDirtyFlag);
				//printf ("Current Node's intFixCounts is %d\n", curNode->intFixCount);
				//printf ("Current Node's intReadIO is %d\n", curNode->intReadIO);
				//printf ("Current Node's intWriteIO is %d\n", curNode->intWriteIO);
				//printf ("Current Node's intAccessOrder is %d\n", curNode->intAccessOrder);
				//printf ("Current Node's intSequence is %d\n", curNode->intSequence);
				curNode = curNode->rightLink;
				//printf ("Inside initBufferPool 23\n");
			}
		}
		//printf("The file name is %s\n",bmTemp->pageFile);
		//printf ("Inside initBufferPool 24\n");
		bmTemp->strategy = strategy;
		//printf ("Inside initBufferPool 25\n");
		//printf ("Inside initBufferPool 26\n");
		int i=0;
		flgRC = RC_OK;
	}
	else
	{
		flgRC = RC_FILE_NOT_FOUND;
	}
	*bm = *bmTemp;
	//printf("The header address is %p\n",bm->mgmtData->header);
	return flgRC;
}
/* function to initialize Buffer Manager ends over here*/

/*
// function to shutdown Buffer Manager starts from here
RC shutdownBufferPool2(BM_BufferPool *const bm)
{
	RC flgRC;
	/*
	Node temp;
	if (header != NULL)
	{
		printf ("Inside shutdownBufferPool if\n");
		temp = header;
		int i = 0;
		int intTotalPinCount = 0;
		flgRC = forceFlushPool(bm);
		while (temp != NULL)
		{
			printf ("Inside shutdownBufferPool if while loop\n");
			if (flgRC == RC_OK)
			{
				printf ("Inside shutdownBufferPool if while if\n");
				((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][1] = 0;//mark the entry as clean
				intTotalPinCount = intTotalPinCount + ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][2]; //getting the pin count for the page
				temp = header->rightLink;
				i++;
			}
			else
			{
				printf ("Inside shutdownBufferPool if while else\n");
				return flgRC;
			}
		}
		if (intTotalPinCount == 0)
		{
			printf ("Inside shutdownBufferPool if if\n");
			while (header != NULL)
			{
				printf ("Inside shutdownBufferPool if if while\n");
				temp = header;
				header = header->rightLink;
				free(temp->frameData);//freeing the Buffer Pool Page Handle Node
				free (temp);//freeing the current node
			}
		}
		printf ("Inside shutdownBufferPool if before free header\n");
		free (header);
		printf ("Inside shutdownBufferPool if before free curnode\n");
		free (curNode);
	}
	else
	{
		flgRC = RC_ERROR_BUFFER;
		return flgRC;
	}
	printf ("Inside shutdownBufferPool before free bm->pageFile\n");
	free(bm->pageFile);
	printf ("Inside shutdownBufferPool before free bm->mgmtData\n");
	free(bm->mgmtData);
	flgRC = RC_OK;
	return flgRC;
}
//function to shutdown Buffer Manager ends over here
*/

/*function to shutdown Buffer Manager starts from here*/
RC shutdownBufferPool(BM_BufferPool *const bm)
{
	RC flgRC;
	Node *temp;
	if (bm->mgmtData->header != NULL)
	{
		printf ("Inside shutdownBufferPool if\n");
		temp = bm->mgmtData->header;
		int i = 0;
		int intTotalPinCount = 0;
		flgRC = forceFlushPool(bm);
		while (temp != NULL)
		{
			printf ("Inside shutdownBufferPool if while loop\n");
			if (flgRC == RC_OK)
			{
				printf ("Inside shutdownBufferPool if while if\n");
				temp->intDirtyFlag = 0;//mark the entry as clean
				intTotalPinCount = intTotalPinCount + temp->intFixCount;
				temp = temp->rightLink;
			}
			else
			{
				printf ("Inside shutdownBufferPool if while else\n");
				return flgRC;
			}
		}
		if (intTotalPinCount == 0)
		{
			printf ("Inside shutdownBufferPool if if\n");
			if (bm->mgmtData->header != NULL)
			{
				temp = bm->mgmtData->header;
				while (temp != NULL)
				{
					printf ("Inside shutdownBufferPool if if while 1\n");
					printf ("Data in page before deleting is %s\n", temp->frameData->data);
					//free (temp->frameData->data);
					printf ("Inside shutdownBufferPool if if while 2\n");
					free (temp->frameData);
					printf ("Inside shutdownBufferPool if if while 3\n");
					temp->leftLink = NULL;
					printf ("Inside shutdownBufferPool if if while 4\n");
					temp = temp->rightLink;
					printf ("Inside shutdownBufferPool if if while 5\n");
				}
			}
			printf ("Inside shutdownBufferPool if if 1\n");
			//free (bm->mgmtData->fHandle->mgmtInfo->FileType);
			printf ("Inside shutdownBufferPool if if 2\n");
			//free (bm->mgmtData->fHandle->mgmtInfo->FileMode);
			printf ("Inside shutdownBufferPool if if 3\n");
			//free (bm->mgmtData->fHandle->mgmtInfo->LastModificationDateTime);
			printf ("Inside shutdownBufferPool if if 4\n");
			//free (bm->mgmtData->fHandle->mgmtInfo->LastAccessDateTime);
			printf ("Inside shutdownBufferPool if if 5\n");
			//free (bm->mgmtData->fHandle->mgmtInfo->CreationDateTime);
			printf ("Inside shutdownBufferPool if if 6\n");
			//free ((SM_FileData*)bm->mgmtData->fHandle->mgmtInfo);
			printf ("Inside shutdownBufferPool if if 7\n");
			//free (bm->mgmtData->fHandle->fileName);
			printf ("Inside shutdownBufferPool if if 8\n");
			free (bm->mgmtData->fHandle);
			printf ("Inside shutdownBufferPool if if 9\n");
			free (bm->mgmtData->header);
			printf ("Inside shutdownBufferPool if if 10\n");
			free (bm->mgmtData);
			printf ("Inside shutdownBufferPool if if 11\n");
			free (bm->pageFile);
			printf ("Inside shutdownBufferPool if if 12\n");
			free (bm);
			printf ("Inside shutdownBufferPool if if 13\n");
		}
		printf ("Inside shutdownBufferPool if before free header\n");
		printf ("Inside shutdownBufferPool if before free curnode\n");
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
/*
RC forceFlushPool2(BM_BufferPool *const bm)
{
	RC flgRC;
	/*
	Node temp;
	if (header != NULL)
	{
		temp = header;
		int i = 0;
		while (temp != NULL)
		{
			if(((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][1]==1)
			{
				//check and force dirty page to be written to disk
				flgRC = forcePage (bm, temp->frameData);
			}

			if (flgRC == RC_OK)
			{
				((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][1] = 0;//mark the entry as clean
				temp = header->rightLink;
				i++;
			}
			else
			{
				return flgRC;
			}
		}
		flgRC = RC_OK;
	}
	else
	{
		flgRC = RC_ERROR_BUFFER;
	}
	return flgRC;
}
*/
/* function to forcefully write down all dirty page from Buffer Manager ends over here*/

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
			if(temp->intDirtyFlag == 1)
			{
				flgRC = forcePage (bm, temp->frameData);
			}

			if (flgRC == RC_OK)
			{
				temp->intDirtyFlag = 0;//mark the entry as clean
				temp = temp->rightLink;
			}
			else
			{
				return flgRC;
			}
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
/*
bool *getDirtyFlags2 (BM_BufferPool *const bm)
{
	//**Node temp;
	bool *arrDirtyStatus = malloc(200*sizeof(bool));
	if (header != NULL)
	{
		temp = header;
		int i = 0;
		while (temp != NULL)
		{
			if (((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][1] == 0)
			{
				arrDirtyStatus[i] = TRUE;
			}
			else
			{
				arrDirtyStatus[i] = FALSE;
			}
			i++;
			temp = header->rightLink;
		}
	}
	else
	{
		//RC Error constant
	}
	return arrDirtyStatus;
}
*/
/* function to get dirty flag status from Buffer Manager ends over here*/

/* function to get dirty flag status from Buffer Manager starts from here*/
bool *getDirtyFlags (BM_BufferPool *const bm)
{
	Node *temp;
	bool *arrDirtyStatus = malloc(bm->numPages*sizeof(bool));
	temp = bm->mgmtData->header;
	if (temp != NULL)
	{
		temp = bm->mgmtData->header;
		int i = 0;
		while (temp != NULL)
		{
			if (temp->intDirtyFlag == 1)
			{
				arrDirtyStatus[i] = TRUE;
			}
			else
			{
				arrDirtyStatus[i] = FALSE;
			}
			i++;
			temp = temp->rightLink;
		}
	}
	else
	{
		//RC Error constant
	}
	return arrDirtyStatus;
}
/* function to get dirty flag status from Buffer Manager ends over here*/

/*The function to read the page number from linked list
int[] returnPageNumbers(Node data){
	int i=0;
	header = data;
	if(header == NULL){
		printf("The list is empty");
		return;
	}
//changed data to header
	while(header->rightLink!=NULL){
		pageNumbers[i]=header->frameData->pageNum;
		header = header->rightLink; //move to the right
		i++;
	}
	return pageNumbers;
}*/

// Buffer Manager Interface Access Pages
/*
RC markDirty2 (BM_BufferPool *const bm, BM_PageHandle *const page)
{
	int isBufferPool = 0;//buffer Pool not empty
	int isEmptyFrame= 0;//to keep track of number of empty frames.
	int desiredPageNum = page->pageNum;
	int i=0;
	int isPageFound=0;
	printf("The desired page number is %d\n",desiredPageNum);
	for(i=0;i<bm->numPages;i++)
	{
		//check first if the bufferPool is not free.
		if(((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][0]!=-1)
		{
			//check if the pageNum is present in the buffer pool
			printf("THe page number is stored in the array.\n");
			if(((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][0]  == desiredPageNum)
			{
				printf("Desired page matched\n");
				((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][1] = 1;//mark the entry as dirty
				isPageFound = 1;//setting the int to 1 to say that the page is Found.
				printf("The pageFound is set to %d and the array element is set to %d\n",isPageFound,((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][1]);
				break; //break from loop as the page is found
			}
		}
		else
			isEmptyFrame++;
	}
	if(isEmptyFrame!=0)
	{
		printf("Inside if loop--> if(isEmptyFrame!=0)");		
		isBufferPool =1;//buffer Pool is empty.
		printf("The buffer pool is empty.Cannot mark the empty buffer pool \n");
		return RC_BUFFER_EMPTY;
	}
	else
	{
	//bufferPool doesnt have the required pageNum
		printf("Inside else loop-->\n");
		printf("The pageFound is set to %d\n",isPageFound);
		if(isPageFound!=1)
		{
			printf("The flag isPageFound is not 1. The expected pageNum :%d is not present in the buffer pool \n",desiredPageNum);
			return RC_PAGEFRAME_NOT_FOUND;
		}
		else
		{
			printf("The page has been successfully marked dirty \n");
			return RC_OK;
		}
	}
}
*/

// Buffer Manager Interface Access Pages
RC markDirty (BM_BufferPool *const bm, BM_PageHandle *const page)
{
	int i, j;
	int arrDetailsResult[bm->numPages][7];
	//printf ("%d\n",page->pageNum);

	//printf ("markDirty 1\n");
	RC flgRC;
	Node* temp;
	int intPageFound=-1;
	//printf ("markDirty 2\n");
	//printf ("Page Data is %s\n", page->data);
	if (bm->mgmtData->header != NULL)
	{
		//printf ("markDirty 3\n");
		temp = bm->mgmtData->header;
		//printf ("markDirty 4\n");
		while ((temp != NULL) && (intPageFound == -1))
		{
			//printf ("markDirty 5\n");
			//printf ("%d\t%d\n",temp->frameData->pageNum, page->pageNum);
			if (temp->frameData->pageNum == page->pageNum)
			{
				//printf ("markDirty 6\n");
				temp->intDirtyFlag = 1;
				//printf ("markDirty 7\n");
				intPageFound = 0;
			}
			//printf ("markDirty 8\n");
			temp = temp->rightLink;
		}
		if (intPageFound == -1)
		{
			//printf ("markDirty 9\n");
			flgRC = RC_PAGEFRAME_NOT_FOUND;
			printf ("markDirty getDetails 1\n");
			getDetails(bm, bm->numPages, 7, arrDetailsResult);
			//printf ("markDirty 1\n");
			//for (i = 0; i < bm->numPages; i++)
			//{
				//for (j = 0; j < 7; j++)
				//{
					//printf ("%d\t", arrDetailsResult[i][j]);
				//}
				//printf ("\n");
			//}
			return flgRC;
		}
		else
		{
			//printf ("markDirty 10\n");
			printf ("markDirty getDetails 2\n");
			getDetails(bm, bm->numPages, 7, arrDetailsResult);
			/*printf ("markDirty 2\n");
			for (i = 0; i < bm->numPages; i++)
			{
				for (j = 0; j < 7; j++)
				{
					printf ("%d\t", arrDetailsResult[i][j]);
				}
				printf ("\n");
			}*/
			flgRC = RC_OK;
			return flgRC;
		}
	}
	else
	{
		printf ("markDirty getDetails 3\n");
		getDetails(bm, bm->numPages, 7, arrDetailsResult);
		/*printf ("markDirty 3\n");
		for (i = 0; i < bm->numPages; i++)
		{
			for (j = 0; j < 7; j++)
			{
				printf ("%d\t", arrDetailsResult[i][j]);
			}
			printf ("\n");
		}*/
		//printf ("markDirty 11\n");
		flgRC = RC_BUFFER_EMPTY;
		return flgRC;
	}
}

//Method to unpin the page whose pageNum is sent as a parameter
/*
RC unpinPage2 (BM_BufferPool *const bm, BM_PageHandle *const page)
{
	int i=0;
	int desiredPageNum = page->pageNum;
	int pageNumInArray = 0;
	int isPageFound = 0;//page Not found
	int noOfEmptyFrames=0;//counter to check the number of empty frames.
	printf("The unpin Page method begins\n");
	for(i=0;i<bm->numPages;i++)
	{
		printf("The page is %d and desiredPage is %d \n",((BufferInfo*)bm->mgmtData)->bufferDetailsArray[i][0],desiredPageNum);
		printf("The values in the array  are as follows: %d %d %d %d %d %d",((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][0],((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][1],((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][2],((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][3],((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][4],((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][5]);
		pageNumInArray = ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][0];
		printf("The value of page in the array is %d",pageNumInArray);
		if(pageNumInArray >= 0)
		{
			printf("Inside if loop. The page number field is not -1 or junk value\n");
			if(pageNumInArray == desiredPageNum)
			{
				printf("The values in the array  are as follows: %d %d %d %d %d %d",((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][0],((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][1],((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][2],((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][3],((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][4],((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][5]);
				printf("The page number matched and the fix count is found to be %d\n",((BufferInfo*)bm->mgmtData)->bufferDetailsArray[i][2]);
				isPageFound=1;//setting to 1 to indicate page is found.
				((BufferInfo*)bm->mgmtData)->bufferDetailsArray[i][2]=((BufferInfo*)bm->mgmtData)->bufferDetailsArray[i][2]-1;// decrementing the fix count by 1 for unpinning the page
				printf("Now the value of fix count is %d\n",((BufferInfo*)bm->mgmtData)->bufferDetailsArray[i][2]);
				printf("The number of empty frames is %d\n",noOfEmptyFrames);
				return RC_OK;
			}
			//**else
			//**{
				//**printf("The page %d is not in the buffer and hence cant be unpinned\n",((BufferInfo*)bm->mgmtData)->bufferDetailsArray[i][0]);
				//**return RC_ERROR_UNPIN;
			//**}
		}
		else
			noOfEmptyFrames++;
	}
	printf ("outside the loop\n");
	printf("The number of empty frames is %d",noOfEmptyFrames);
	if(noOfEmptyFrames!=0)
	{
		printf("The buffer pool is empty. Cannot unpin the page /n");
		return RC_BUFFER_EMPTY;
	}
	else
	{
		if(isPageFound!=1)
		{
			printf("The page frame %d is not present in the buffer pool. /n",desiredPageNum);
			return RC_PAGEFRAME_NOT_FOUND;
		}
		else
		{
			printf("The page frame %d has been successfully unpinned./n",desiredPageNum);
			return RC_OK;
		}
	}
}
*/

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
			printf ("unpinPage %d\t%d\n", temp->frameData->pageNum, page->pageNum);
			if (temp->frameData->pageNum == page->pageNum)
			{
				temp->intFixCount = temp->intFixCount - 1;
				//printf ("New count after unpinning is %d\n", temp->intFixCount);
				intPageFound = 0;
			}
			temp = temp->rightLink;
		}
		if (intPageFound == -1)
		{
			printf ("unPinPage getDetails 1\n");
			getDetails(bm, bm->numPages, 7, arrDetailsResult);
			//printf ("unPin 1\n");
			/*for (i = 0; i < bm->numPages; i++)
			{
				for (j = 0; j < 7; j++)
				{
					printf ("%d\t", arrDetailsResult[i][j]);
				}
				printf ("\n");
			}*/
			flgRC = RC_PAGEFRAME_NOT_FOUND;
			return flgRC;
		}
		else
		{
			printf ("unPinPage getDetails 2\n");
			getDetails(bm, bm->numPages, 7, arrDetailsResult);
			//printf ("unPin 2\n");
			/*for (i = 0; i < bm->numPages; i++)
			{
				for (j = 0; j < 7; j++)
				{
					printf ("%d\t", arrDetailsResult[i][j]);
				}
				printf ("\n");
			}*/
			flgRC = RC_OK;
			return flgRC;
		}
	}
	else
	{
		printf ("unPinPage getDetails 3\n");
		getDetails(bm, bm->numPages, 7, arrDetailsResult);
		//printf ("unPin 3\n");
		/*for (i = 0; i < bm->numPages; i++)
		{
			for (j = 0; j < 7; j++)
			{
				printf ("%d\t", arrDetailsResult[i][j]);
			}
			printf ("\n");
		}*/
		flgRC = RC_BUFFER_EMPTY;
		return flgRC;
	}
}

//Function to write the data from the buffer to the disk.
/*
RC forcePage2 (BM_BufferPool *const bm, BM_PageHandle *const page)
{
	int i=0,j=0;
	RC returnValue = writeBlock(page->pageNum,(SM_FileHandle *)(((BufferInfo*)bm->mgmtData)->fHandle),(SM_PageHandle)(((BufferInfo*)bm->mgmtData)->ph));
	if(returnValue == RC_OK){
		for(i=0;i<sizeof(((BufferInfo *)bm->mgmtData)->bufferDetailsArray);i++){
			if(((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][0] == page->pageNum){
				i=j;	//find the index to be updated
				break;
			}
		}
		 ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][1]=0;
		 ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][4]++;//increment write IO num
		  return RC_OK;
	}else{
		return RC_ERROR_BUFFER;
	}
}
*/

//Function to write the data from the buffer to the disk.
RC forcePage (BM_BufferPool *const bm, BM_PageHandle *const page)
{
	Node *temp;
	int i=0,j=0, intNodeFound = -1;
	printf ("forcePage Page # is %d\n", page->pageNum);
	printf ("forcePage Data is %s\n", page->data);
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
RC pinPage2 (BM_BufferPool *const bm, BM_PageHandle *const page, const PageNumber pageNum)
{
	/*
	//allocating the memory
	int isFreeFrame=0;//to get the number of the frames that are free in the buffer pool
	int pageFound = 0;
	int bufferFull = 0;//counter to check if buffer is full.
	int i,returnValue;
	Node temp;
	printf ("Before pinPage temp malloc\n");
	temp = (Node)malloc(sizeof(Node));
	printf ("After pinPage temp malloc\n");
	printf ("Before pinPage temp rightlink malloc\n");
	//temp->rightLink = (Node)malloc(sizeof(Node));
	printf ("After pinPage temp rightlink malloc\n");
	printf ("Before pinPage temp leftlink malloc\n");
	//temp->leftLink = (Node)malloc(sizeof(Node));
	printf ("After pinPage temp leftlink malloc\n");
	printf ("Before pinPage temp framedata malloc\n");
	//temp->frameData = (BM_PageHandle *)malloc(sizeof(BM_PageHandle));
	printf ("After pinPage temp framedata malloc\n");
	printf ("Before pinPage temp framedata data malloc\n");
	//temp->frameData->data = (SM_PageHandle)malloc(sizeof(char));
	printf ("After pinPage temp framedata data malloc\n");

	int temp1;int min; int replacementPageNum;
	printf("Inside pin page code\n");
	printf("The address of temp is %p and of current node is %p and header is %p\n",temp,curNode,header);
	printf("The number of pages is %d\n",(*bm).numPages);
	printf("The file name is %s\n",(char *)((SM_FileHandle *)(((BufferInfo *)bm->mgmtData)->fHandle)->fileName));
	int counter=0;
	int j=0;
	(*page).pageNum = pageNum;
	
	(((BufferInfo *)bm->mgmtData)->fHandle)->totalNumPages = pageNum+1;//totalNumberOfPages(bm->pageFile);
	(((BufferInfo *)bm->mgmtData)->fHandle)->curPagePos = pageNum;
	
	printf("The total number of pages in page file is %d\n",(((BufferInfo *)bm->mgmtData)->fHandle)->totalNumPages);
	printf("The given page number is %d and  updated page number is %d\n",pageNum,(*page).pageNum);
	//**Traverse the linked list and update the page number and the data of current frame
	for(i=0;i<sizeof(((BufferInfo*)bm->mgmtData)->bufferDetailsArray);i++)
	{
		printf("Inside for loop\n");
		if(((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][0] == pageNum)
		{
			printf("Inside if condition --> the matched page number is %d\ns",((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][0]);
			j =i;
			printf ("%d\n", j);
			break;
		}
	}
	printf ("%d\n", j);

	temp=header;
	printf ("The temp framedata pagenum is %d\n",temp->frameData->pageNum);
	if(temp->frameData->pageNum >= 0) //if the node in the list is the requested frame
	{
		printf("Inside if loop. Value of header is %d\n",header->frameData->pageNum);
		while(temp!=NULL && pageFound!=1)
		{
			if(temp->frameData->pageNum == pageNum)
			{
				printf("Inside while-> if loop.The temp value is %d\n",temp->frameData->pageNum);
				page->data = temp->frameData->data;
				page->pageNum = temp->frameData->pageNum;
				counter++;
				pageFound = 1;
				((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][5]=counter;
				//free(temp);
				returnValue= RC_OK;
			}
			else
			{
				temp = temp->rightLink;
				numberOfNodes++;
			}
		}//end while temp!= null
	} // end if temp->frameData != NULL
	printf("The page found value is %d\n",pageFound);
	printf("The number of nodes is %d\n",numberOfNodes);
	if(pageFound!= 1)
	{
		printf("The page is not found in buffer\n");
		printf("%d\n",numberOfNodes);
		printf("%d\n",bm->numPages);
		if(numberOfNodes<bm->numPages)
		{
			printf("Inside if.The number of nodes is %d\n",numberOfNodes);
			printf("The page handle is %p",(SM_PageHandle)(((BufferInfo *)bm->mgmtData)->ph));
			returnValue = readBlock(pageNum,(SM_FileHandle *)(((BufferInfo *)bm->mgmtData)->fHandle),(SM_PageHandle)(((BufferInfo *)bm->mgmtData)->ph));
			printf("The content that is read is %s and value of j is %d and number of nodes is %d\n",(SM_PageHandle)(((BufferInfo *)bm->mgmtData)->ph),j,numberOfNodes);
			printf("The address of temp is %p and of current node is %p and header is %p and data is %p\n",temp,curNode,header,temp->frameData->data);
			temp->frameData->data = (char *)(((BufferInfo *)bm->mgmtData)->ph);
			printf("The temp is initialised? Value is currently %s",temp->frameData->data);
			if(numberOfNodes == 0)
			{
				header = curNode =temp ;
				curNode->rightLink = NULL;
				curNode->leftLink = NULL;
			}
			else
			{
				curNode->rightLink = temp;
				temp->leftLink = curNode;
				temp->rightLink = NULL;
			}
			//curNode = temp;
			printf("The data has been assigned to temp and value of j is %d",j);
			numberOfNodes++;
			counter++;
			((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][0]=pageNum;
			((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][3]++;//increment read IO num
			((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][2]++;//increment the fix count
			((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][5]=counter;
			returnValue= RC_OK;
			printf("The number of nodes is %d",numberOfNodes);
			printf("The values in the array  are as follows: %d %d %d %d %d %d",((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][0],((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][1],((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][2],((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][3],((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][4],((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][5]);
		}//end if numberOfNodes < bm->numPages
		else if(numberOfNodes == bm->numPages)//apply page replacement algorithms
		{
			printf("Inside else.The number of nodes is %d\n",numberOfNodes);
			switch(bm->strategy)
			{
				case 0: //**Implement FIFO page replacement strategy
					printf("Case 0 -fifo\n");
					printf("The address of bm is %p and of mgmtData is %p and bufferdetailsarray is %p\n",bm,bm->mgmtData,((BufferInfo *)bm->mgmtData)->bufferDetailsArray);
					printf("%d\n",((BufferInfo *)bm->mgmtData)->bufferDetailsArray[0][2]);
					if(((BufferInfo *)bm->mgmtData)->bufferDetailsArray[0][2]!=0)
					{
						printf ("inside fifo first if\n");
						printf ("inside fifo first if before forcePage\n");
						forcePage(bm,page);
						printf ("inside fifo first if after forcePage\n");
					}//end if check of dirty write
					printf ("%d\t%d\n", ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[0][1], ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[0][2]);
					printf("The address of bm is %p and of mgmtData is %p and bufferdetailsarray is %p\n",bm,bm->mgmtData,((BufferInfo *)bm->mgmtData)->bufferDetailsArray);
					if(((BufferInfo *)bm->mgmtData)->bufferDetailsArray[0][1]==0 && ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[0][2]==0)
					{
						printf ("Inside big condition if\n");
						temp = header;
						printf ("Inside big condition if 2\n");
						header = header->rightLink;
						printf ("Inside big condition if 3\n");
						header->leftLink = NULL;
						printf ("Inside big condition if 4\n");
						//reassign temp to current header node
						temp = header;
						printf ("Inside big condition if 5\n");
						while(temp->rightLink!=NULL)
						{
							printf ("Inside big condition if 6\n");
							temp = temp->rightLink;//traversing to the end of linked list to insert at the end
							printf ("Inside big condition if 7\n");
							returnValue= readBlock(pageNum,(SM_FileHandle *)(((BufferInfo *)bm->mgmtData)->fHandle),(SM_PageHandle)(((BufferInfo *)bm->mgmtData)->ph));
							printf ("Inside big condition if 8\n");
						}//end while
						//insert the new frame at the end
						printf ("Inside big condition if 9\n");
						curNode->frameData->data = ((BufferInfo *)bm->mgmtData)->ph;
						printf ("Inside big condition if 10\n");
						curNode->leftLink = temp;
						printf ("Inside big condition if 11\n");
						curNode->rightLink = NULL;
						printf ("Inside big condition if 12\n");
						counter++;
						printf ("Inside big condition if 13\n");
						((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][0]=pageNum;
						printf ("Inside big condition if 14\n");
						((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][3]++;//increment read  IO num
						printf ("Inside big condition if 15\n");
						((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][2]++;//increment fix count
						printf ("Inside big condition if 16\n");
						((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][5]=counter;
						printf ("Inside big condition if 17\n");
						returnValue= RC_OK;
					} //if(no fix count and not dirty)
					else if(((BufferInfo *)bm->mgmtData)->bufferDetailsArray[0][2]!=0)//Nikhil: made [0][2] instead of [0][1] assuming that we are checking the pinCount not the dirty bit
					{
						printf ("%d\n",((BufferInfo *)bm->mgmtData)->bufferDetailsArray[0][2]);
						printf ("Inside big condition if 18\n");
						printf ("%d\n", i);
						printf("The address of bm is %p and of mgmtData is %p and bufferdetailsarray is %p\n",bm,bm->mgmtData,((BufferInfo *)bm->mgmtData)->bufferDetailsArray);
						printf("The page %d is still being used by other threads. Cannot perform replacement stratergy. /n",((BufferInfo *)bm->mgmtData)->bufferDetailsArray[0][0] );
						//free(temp);
						returnValue = RC_ERROR_UNPIN;
					}//non zero fix count
					for(i=0;i<sizeof(((BufferInfo *)bm->mgmtData)->bufferDetailsArray);i++)
					{
						printf ("Inside big condition if 19\n");
						printf ("%d\t%d\t%d\t%d\n",temp->frameData->pageNum, ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][0], ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][2], ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][1]);
						if(temp->frameData->pageNum == ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][0] && ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][2]==0 && ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][1]==0)
						{
							printf ("Inside big condition if 20\n");
							free(((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i]);
						}//end if
					}//end for
					printf("FIFO is done\n");
					break;

				case 1:
					//**Implement LRU page replacement strategy
					//**find the page number with minumum value of counter -> this is eligible for page replacement
					printf("In case 1. LRU");
					min = ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[0][5];
					for(i=1;i<sizeof(((BufferInfo *)bm->mgmtData)->bufferDetailsArray);i++)
					{
						min= ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][5] < min ?
						(((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][5]):min;
					}
					for(i=0;i<bm->numPages;i++)
					{
						if(min == ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][5])
						{
							replacementPageNum = ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][0];
							free(((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i]);//min counter page is eligible for replacement
						}//end if
					}//end for
					printf("LRU--> the page to be replaced is %d",replacementPageNum);
					while(header!=NULL)
					{
						if(header->frameData->pageNum == replacementPageNum)
						{
							curNode = header;
							header = header->rightLink;
							curNode->rightLink = NULL;
							curNode->leftLink =NULL;
						}//end if
						header = header->rightLink;
					}//end while
					returnValue = readBlock(pageNum,(SM_FileHandle *)(((BufferInfo *)bm->mgmtData)->fHandle),(SM_PageHandle)(((BufferInfo *)bm->mgmtData)->ph));
					printf("LRU-->read block is done");
					temp->frameData->data = (SM_PageHandle)(((BufferInfo *)bm->mgmtData)->ph);
					temp->leftLink = curNode;
					temp->rightLink = NULL;
					counter++;
					((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][0]=pageNum;
					((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][3]++;//increment read IO num
					((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][5]=counter;
					((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][2]++;//increment fix count
					break;
			}//end switch
		}//else if end
	}//end if pageFound!=1
	free(temp);
	printf ("%d\n", returnValue);
	return returnValue;
	*/
	return RC_OK; //** to be changed while finailizing the function
}

//Function to pin the page and apply the replacement stratergy.
RC pinPage (BM_BufferPool *const bm, BM_PageHandle *const page, const PageNumber pageNum)
{
	//printf ("Inside pinPage 1\n");
	printf ("page content are %d\t%s\n", (*page).pageNum, (*page).data);
	int arrDetailsResult[bm->numPages][7];
	char str[PAGE_SIZE] = {'\0'};
	char *strData;
	printf ("PinPage getDetails 1\n");
	getDetails(bm, bm->numPages, 7, arrDetailsResult);
	PageNumber pgToBeReplaced;
	SM_PageHandle ph;
	RC flgRC;
	int i, j;
	int intPageFound = -1;
	int intFreePagesCount = 0;
	Node *temp;
	//printf ("Inside pinPage 2\n");

	if (bm->mgmtData->header != NULL)
	{
		printf ("Inside pinPage 3\n");
		temp = bm->mgmtData->header;
		//printf ("Inside pinPage 4\n");
		while ((temp != NULL) && (intPageFound == -1))
		{
			//printf ("temp Address is %p\n",temp);
			//printf ("Inside pinPage 5\n");
			//printf ("%d\t%d\n",temp->frameData->pageNum, pageNum);
			if (temp->frameData->pageNum == pageNum)
			{
				printf ("Inside pinPage 6\n");
				intPageFound = 0;
				//printf ("Inside pinPage 7\n");
				//temp->frameData->data = (*page).data;
				//printf ("Inside pinPage 8\n");
				//temp->frameData->pageNum = pageNum;
				//(*page).pageNum = pageNum;
				//printf ("Inside pinPage 9\n");
				temp->intFixCount = temp->intFixCount + 1;
				//printf ("Inside pinPage 10\n");
				temp->intAccessOrder = temp->intAccessOrder + 1;
				//printf ("Inside pinPage 11\n");
				printf ("1 Final page content are %d\t%s\n", page->pageNum, page->data);
			}
			else if (temp->frameData->pageNum == -1)
			{
				//printf ("Inside pinPage 12\n");
				intFreePagesCount++;
			}
			//printf ("Inside pinPage 13\n");
			temp = temp->rightLink;
			//printf ("Inside pinPage 14\n");
		}

		if (intPageFound == 0)
		{
			//printf ("Inside pinPage 15\n");
			return RC_OK;
		}
		//page unavailable so checking for empty frames and page loading code starts from here
		else if ((intPageFound == -1) && (intFreePagesCount > 0))
		{
			printf ("Inside pinPage 16\n");
			//code to load the page in frame if free frames are available
			//ph = (SM_PageHandle) malloc (sizeof (PAGE_SIZE));
			//flgRC = readBlock (pageNum, bm->mgmtData->fHandle, ph);
			//if (flgRC != 0)
			//{
				//ph = str;
			//}
			//printf ("flgRC is %d\n", flgRC);
			//printf ("ph contents are %s\n", ph);
			//printf ("Inside pinPage 17\n");
			//printf ("Inside pinPage 18\n");
			if (bm->mgmtData->header != NULL)
			{
				printf ("Inside pinPage 19\n");
				temp = bm->mgmtData->header;
				//printf ("Inside pinPage 20\n");
				while ((temp != NULL) && (intPageFound == -1))
				{
					printf ("Inside pinPage 21\n");
					printf ("Frame Data is %s\n", temp->frameData->data);
					if (temp->frameData->pageNum == -1)
					{
						printf ("Inside pinPage 22\n");
						strData = (char*) malloc (PAGE_SIZE);
						strcpy (strData, (*page).data);
						printf ("strData is %s\n", strData);
						temp->frameData->data = strData;
						//printf ("Inside pinPage 23\n");
						//temp->frameData->pageNum = pageNum;
						//printf ("Inside pinPage 24\n");
						temp->intReadIO = temp->intReadIO + 1;
						//printf ("Inside pinPage 25\n");
						temp->intFixCount = temp->intFixCount + 1;
						//printf ("Inside pinPage 26\n");
						temp->intAccessOrder = temp->intAccessOrder + 1;
						//printf ("Inside pinPage 27\n");
						intPageFound = 0;
						printf ("Inside pinPage 28\n");
						//temp->frameData = (BM_PageHandle*) malloc (sizeof (BM_PageHandle));
						//temp->frameData->data = (char*) malloc (sizeof (PAGE_SIZE));
						temp->frameData->data = (*page).data;
						printf ("Inside pinPage 8\n");
						temp->frameData->pageNum = pageNum;
						//temp->frameData = page;
						(*page).pageNum = pageNum;
						printf ("Inside pinPage 9\n");
						intSequence++;
						temp->intSequence = intSequence;
						printf ("Sequence is %d\n", temp->intSequence);
						printf ("2 Final page content are %d\t%s\n", page->pageNum, page->data);
					}
					temp = temp->rightLink;
				}
			}
			printf ("Inside pinPage 29\n");
			flgRC = RC_OK;
			//printf ("Inside pinPage 30\n");
			printf ("bm->numPages is %d\n", bm->numPages);
			printf ("PinPage getDetails 2\n");
			getDetails(bm, bm->numPages, 7, arrDetailsResult);
			printf ("pinPage\n");
			/*for (i = 0; i < bm->numPages; i++)
			{
				for (j = 0; j < 7; j++)
				{
					printf ("%d\t", arrDetailsResult[i][j]);
				}
				printf ("\n");
			}*/
			return flgRC;
		}
		//Replacement code starts from here
		else if ((intPageFound == -1) && (intFreePagesCount <= 0))
		{
			printf ("Inside pinPage 32\n");
			pgToBeReplaced = getReplacementPage(bm);
			printf ("pgToBeReplaced is %d\n", pgToBeReplaced);
			if (pgToBeReplaced != -1)
			{
				printf ("Inside pgToBeReplaced 1\n");
				if (bm->mgmtData->header != NULL)
				{
					printf ("Inside pgToBeReplaced 2\n");
					temp = bm->mgmtData->header;
					printf ("Inside pgToBeReplaced 3\n");
					while ((temp!= NULL) && (intPageFound == -1))
					{
						printf ("Inside pgToBeReplaced 4\n");
						if (temp->frameData->pageNum == pgToBeReplaced)
						{
							printf ("Inside pgToBeReplaced 5\n");
							printf ("dATA IS %d-%s\n", temp->frameData->pageNum, temp->frameData->data);
							flgRC = forcePage (bm, temp->frameData);
							if (flgRC == RC_OK)
							{
								printf ("Inside pgToBeReplaced 6\n");
								temp->intDirtyFlag = 0;
								//printf ("Page number is %d\n", temp->frameData->pageNum);
								//ph = (SM_PageHandle) malloc (sizeof (PAGE_SIZE));
								//flgRC = readBlock (pageNum, bm->mgmtData->fHandle, ph);
								//if (flgRC != 0)
								//{
									//ph = str;
								//}
								//printf ("flgRC is %d\n", flgRC);
								//printf ("ph contents are %s\n", ph);
								printf ("Inside pinPage 33\n");
								temp->frameData->data = (*page).data;
								printf ("Page number is %d\n", (*page).pageNum);
								//printf ("Inside pinPage 34\n");
								temp->frameData->pageNum = pageNum;
								//printf ("Inside pinPage 35\n");
								temp->intReadIO = 1;
								//printf ("Inside pinPage 36\n");
								temp->intFixCount = 1;
								//printf ("Inside pinPage 37\n");
								temp->intAccessOrder = 1;
								//printf ("Inside pinPage 38\n");
								intPageFound = 0;
								//printf ("Inside pinPage 39\n");
								(*page).data = temp->frameData->data;
								//printf ("Inside pinPage 40\n");
								(*page).pageNum = temp->frameData->pageNum;
								//printf ("Inside pinPage 41\n");
								intSequence++;
								temp->intSequence = intSequence;
								printf ("Sequence is %d\n", temp->intSequence);
								printf ("Final page content are %d\t%s\n", page->pageNum, page->data);
								//printf ("Inside pinPage 42\n");
								printf ("PinPage getDetails 3\n");
								getDetails(bm, bm->numPages, 7, arrDetailsResult);
								printf ("pinPage\n");
								/*for (i = 0; i < bm->numPages; i++)
								{
									for (j = 0; j < 7; j++)
									{
										printf ("%d\t", arrDetailsResult[i][j]);
									}
									printf ("\n");
								}*/
								flgRC = RC_OK;
								return flgRC;
							}
							else
							{
								printf ("Inside pinPage 43\n");
								printf ("PinPage getDetails 4\n");
								getDetails(bm, bm->numPages, 7, arrDetailsResult);
								printf ("pinPage\n");
								/*for (i = 0; i < bm->numPages; i++)
								{
									for (j = 0; j < 7; j++)
									{
										printf ("%d\t", arrDetailsResult[i][j]);
									}
									printf ("\n");
								}*/
								return flgRC;
							}
							if (intPageFound == -1)
							{
								printf ("Inside pinPage 45\n");
								printf ("PinPage getDetails 5\n");
								getDetails(bm, bm->numPages, 7, arrDetailsResult);
								printf ("pinPage\n");
								/*for (i = 0; i < bm->numPages; i++)
								{
									for (j = 0; j < 7; j++)
									{
										printf ("%d\t", arrDetailsResult[i][j]);
									}
									printf ("\n");
								}*/
								flgRC = RC_PAGEFRAME_NOT_FOUND;
								return flgRC;
							}
						}
						else
						{
							printf ("Inside pinPage 44\n");
							printf ("PinPage getDetails 6\n");
							getDetails(bm, bm->numPages, 7, arrDetailsResult);
							printf ("pinPage\n");
							/*for (i = 0; i < bm->numPages; i++)
							{
								for (j = 0; j < 7; j++)
								{
									printf ("%d\t", arrDetailsResult[i][j]);
								}
								printf ("\n");
							}*/
							temp = temp->rightLink;
						}
					}
				}
				else
				{
					printf ("Inside pinPage 46\n");
					printf ("PinPage getDetails 7\n");
					getDetails(bm, bm->numPages, 7, arrDetailsResult);
					printf ("pinPage\n");
					/*for (i = 0; i < bm->numPages; i++)
					{
						for (j = 0; j < 7; j++)
						{
							printf ("%d\t", arrDetailsResult[i][j]);
						}
						printf ("\n");
					}*/
					flgRC = RC_ERROR_BUFFER;
					return flgRC;
				}
			}
			else
			{
				printf ("Inside pinPage 47\n");
				printf ("PinPage getDetails 8\n");
				getDetails(bm, bm->numPages, 7, arrDetailsResult);
				printf ("pinPage\n");
				/*for (i = 0; i < bm->numPages; i++)
				{
					for (j = 0; j < 7; j++)
					{
						printf ("%d\t", arrDetailsResult[i][j]);
					}
					printf ("\n");
				}*/
				flgRC = RC_ERROR_UNPIN;
				return flgRC;
			}
		}
		else
		{
			printf ("Inside pinPage 48\n");
			printf ("PinPage getDetails 9\n");
			getDetails(bm, bm->numPages, 7, arrDetailsResult);
			/*printf ("pinPage\n");
			for (i = 0; i < bm->numPages; i++)
			{
				for (j = 0; j < 7; j++)
				{
					printf ("%d\t", arrDetailsResult[i][j]);
				}
				printf ("\n");
			}*/
			flgRC = RC_PAGEFRAME_NOT_FOUND;
			return flgRC;
		}
	}
	else
	{
		printf ("Inside pinPage 49\n");
		printf ("PinPage getDetails 10\n");
		getDetails(bm, bm->numPages, 7, arrDetailsResult);
		printf ("pinPage\n");
		/*for (i = 0; i < bm->numPages; i++)
		{
			for (j = 0; j < 7; j++)
			{
				printf ("%d\t", arrDetailsResult[i][j]);
			}
			printf ("\n");
		}*/
		flgRC = RC_ERROR_BUFFER;
		return flgRC;
	}
	/*
				case 1:
					//**Implement LRU page replacement strategy
					//**find the page number with minumum value of counter -> this is eligible for page replacement
					printf("In case 1. LRU");
					min = ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[0][5];
					for(i=1;i<sizeof(((BufferInfo *)bm->mgmtData)->bufferDetailsArray);i++)
					{
						min= ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][5] < min ?
						(((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][5]):min;
					}
					for(i=0;i<bm->numPages;i++)
					{
						if(min == ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][5])
						{
							replacementPageNum = ((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i][0];
							free(((BufferInfo *)bm->mgmtData)->bufferDetailsArray[i]);//min counter page is eligible for replacement
						}//end if
					}//end for
					printf("LRU--> the page to be replaced is %d",replacementPageNum);
					while(header!=NULL)
					{
						if(header->frameData->pageNum == replacementPageNum)
						{
							curNode = header;
							header = header->rightLink;
							curNode->rightLink = NULL;
							curNode->leftLink =NULL;
						}//end if
						header = header->rightLink;
					}//end while
					returnValue = readBlock(pageNum,(SM_FileHandle *)(((BufferInfo *)bm->mgmtData)->fHandle),(SM_PageHandle)(((BufferInfo *)bm->mgmtData)->ph));
					printf("LRU-->read block is done");
					temp->frameData->data = (SM_PageHandle)(((BufferInfo *)bm->mgmtData)->ph);
					temp->leftLink = curNode;
					temp->rightLink = NULL;
					counter++;
					((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][0]=pageNum;
					((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][3]++;//increment read IO num
					((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][5]=counter;
					((BufferInfo *)bm->mgmtData)->bufferDetailsArray[j][2]++;//increment fix count
					break;
			}//end switch
		}//else if end
	}//end if pageFound!=1
	free(temp);
	printf ("%d\n", returnValue);
	return returnValue;
	*/
}

//function to get the fixCount for each page
/*
int *getFixCounts2 (BM_BufferPool *const bm)
{
	/*
	//int fixCountArray[3];
	int* fixCountArray = malloc(3*sizeof(int));
	int i=0;
	for(i=0;i<3;i++)
	{

			fixCountArray[i]= ((BufferInfo*)bm->mgmtData)->bufferDetailsArray[i][2];
	}
	return fixCountArray;
}
*/

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
/*
PageNumber *getFrameContents2 (BM_BufferPool *const bm)
{
	//int fixCountArray[3];
	PageNumber* frameContents = malloc(bm->numPages*sizeof(PageNumber));
	int i=0;
	for(i=0;i<bm->numPages;i++)
	{

			frameContents[i]= ((BufferInfo*)bm->mgmtData)->bufferDetailsArray[i][0];
	}
	return frameContents;
}
*/

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
/*
int getNumReadIO2 (BM_BufferPool *const bm)
{
	int numReadIOArray=0;
	int i=0;
	for(i=0;i<3;i++)
	{
			numReadIOArray+= ((BufferInfo*)bm->mgmtData)->bufferDetailsArray[i][3];
	}
	return numReadIOArray;
}
*/

//Procedure to get the array of integers for read IO operations
int getNumReadIO (BM_BufferPool *const bm)
{
	int numReadIOArray=0;
	Node *temp;
	if (bm->mgmtData->header != NULL)
	{
		temp = bm->mgmtData->header;
		while (temp != NULL)
		{
			numReadIOArray =+ temp->intReadIO;
			temp = temp->rightLink;
		}
	}
	return numReadIOArray;
}

//Procedure to get the array of integers for write IO functinality
/*
int getNumWriteIO2 (BM_BufferPool *const bm)
{
	/*
	int numWriteIOArray=0; // integer to store the number of writeIO
	int i=0;
	for(i=0;i<3;i++)
	{
			numWriteIOArray+= ((BufferInfo*)bm->mgmtData)->bufferDetailsArray[i][4];
	}
	return numWriteIOArray;
}
*/

//Procedure to get the array of integers for write IO functionality
int getNumWriteIO (BM_BufferPool *const bm)
{
	int numWriteIOArray=0; // integer to store the number of writeIO
	Node *temp;
	if (bm->mgmtData->header != NULL)
	{
		temp = bm->mgmtData->header;
		while (temp != NULL)
		{
			numWriteIOArray =+ temp->intReadIO;
			temp = temp->rightLink;
		}
	}
	return numWriteIOArray;
}

/*
int totalNumberOfPages(char * fileName)
{
	int intStatStatus ,intNoOfPages;
	struct stat sb;
	intStatStatus = stat (fileName, &sb);
		printf ("%d\n", intStatStatus);
		if (intStatStatus == 0)
		{
			//**Calculating the total number of pages on opening the file successfully
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
	return intNoOfPages;
	return 0;
}
*/

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

			printf ("%d\t%d\t%d\t%d\t%d\t%d\t%d\t%s\n", arrDetails[i][0], arrDetails[i][1], arrDetails[i][2], arrDetails[i][3],
					arrDetails[i][4], arrDetails[i][5], arrDetails[i][6], temp->frameData->data);
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
				printf ("getReplacementPage 1\n");
				if (arrDetailsResult[i][2] == 0)
				{
					printf ("getReplacementPage 2\n");
					if (intTempSequence == -1)
					{
						printf ("getReplacementPage 3\n");
						pgReplacement = arrDetailsResult[i][0];
						printf ("pgReplacement is %d\n",pgReplacement);
						intTempSequence = arrDetailsResult[i][6];
						printf ("intTempSequence is %d\n",intTempSequence);
					}
					else if (intTempSequence > arrDetailsResult[i][6])
					{
						printf ("getReplacementPage 4\n");
						pgReplacement = arrDetailsResult[i][0];
						printf ("pgReplacement is %d\n",pgReplacement);
						intTempSequence = arrDetailsResult[i][6];
						printf ("intTempSequence is %d\n",intTempSequence);
					}
				}
			}
			break;
		case RS_LRU:
			break;
	}
	return pgReplacement;
}
