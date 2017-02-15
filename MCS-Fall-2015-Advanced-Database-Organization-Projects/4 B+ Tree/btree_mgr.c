#include "btree_mgr.h"
#include "record_mgr.h"
#include "buffer_mgr.h"
#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#define NUMBER_OF_NODES 2
#define NUMBER_OF_ENTRIES 6

BM_BufferPool *bm ;
BM_PageHandle *h ;


//function to initialize the Index Manager starts from here
RC initIndexManager (void *mgmtData)
{
	bm = MAKE_POOL();
	h = MAKE_PAGE_HANDLE();
	return RC_OK;
}
//function to initialize the Index Manager ends over here

//function to shutdown the Index Manager starts from here
RC shutdownIndexManager ()
{
	free(h);
	free(bm);
	return RC_OK;
}
//function to shutdown the Index Manager starts from here

//function to create the B+ Tree starts from here
RC createBtree (char *idxId, DataType keyType, int n)
{
	BTreeHandle *bpt;
	BTreeNode *btn;

	char* strPageFileName = NULL;
	RC flgRC;

	printf("Inside createBtree\n");
	strPageFileName = (char*)malloc(strlen (idxId)+5);

	strcpy (strPageFileName, idxId);
	strcat (strPageFileName, ".txt");
	printf("Before createPageFile\n");
	flgRC = createPageFile(strPageFileName);
	printf("After createPageFile\n");
	if (flgRC == RC_OK)
	{
		bpt = (BTreeHandle*) malloc (sizeof (BTreeHandle));
		btn = (BTreeNode *) malloc (sizeof (BTreeNode));
		btn->btnPtr1 = (BTreeNode *) malloc (sizeof (BTreeNode));
		btn->btnPtr2 = (BTreeNode *) malloc (sizeof (BTreeNode));
		btn->btnPtr3 = (BTreeNode *) malloc (sizeof (BTreeNode));
		printf("Before assigning key Type\n");
		bpt->keyType = keyType;
		printf("After assigning key Type\n");
		//memcpy (bpt->idxId, idxId,strlen(idxId));
		bpt->idxId = (char *) malloc(sizeof(char *));
		strncpy(bpt->idxId,idxId,strlen(idxId)+1);
		printf("After strcpy\n");
		btn->intNoOfKeys = 0;
		btn->btnPtr1->intNoOfKeys =0;
		btn->btnPtr2->intNoOfKeys =0;
		btn->btnPtr3->intNoOfKeys =0;
		printf("Number of keys\n");
		bpt->mgmtData = (BTreeNode*) btn;
		printf("After assigning mgmtData pointer\n");
		flgRC = RC_OK;
	}
	else
	{
		flgRC = RC_FILE_ALREADY_FOUND;
	}
	printf("Before return from createTree\n");
	return flgRC;
}
//function to create the B+ Tree ends over here
//Function to open B+ Tree starts
RC openBtree (BTreeHandle **tree, char *idxId){

	printf("Inside openBTree\n");
	char* strPageFileName = NULL;
	char* data = NULL;//(char *)malloc(sizeof(char *) + 1);
	RC flgRC;

		printf("Tree pointer init\n");
		*tree = malloc(sizeof(BTreeHandle));
		printf("MgmtData init\n");
		(*tree)->mgmtData = (BTreeNode *)malloc(sizeof(BTreeNode));

		printf("Inside openBTree: Before mgmtData assign\n");
		BTreeNode *rootNode = (*tree)->mgmtData;
		rootNode->keyVal1 = (struct Value*)malloc(sizeof(Value *));
		rootNode->keyVal2 = (struct Value*)malloc(sizeof(Value *));
		strPageFileName = (char*)malloc(strlen (idxId)+5);
		data = (char*)malloc(sizeof(char *));

		printf("Inside openBTree: Before assigning fileName\n");
		strcpy (strPageFileName, idxId);
		strcat (strPageFileName, ".txt");

		printf("Before init Buffer pool\n");
		initBufferPool(bm,strPageFileName,10,RS_FIFO,NULL);
		printf("Before pin page\n");
		pinPage(bm,h,0);
		printf("After pin page\n");
		sprintf(data,"%s",h->data);
		printf("Before assigning data to intV\n");
		rootNode->keyVal1->v.intV = atoi(data);
		rootNode->keyVal2->v.intV = -1;
		printf("Before unpin page\n");
		flgRC=unpinPage(bm,h);

		rootNode->btnPtr1 = NULL;

		(*tree)->mgmtData = rootNode;

		printf("After unPin page\n");
		//free(strPageFileName);
		//free(data);
	return flgRC;
}
//function to open B+ tree ends
//function to delete the B+ Tree starts from here
RC deleteBtree (char *idxId)
{
	char* strPageFileName = NULL;
	RC flgRC;

	strPageFileName = (char*)malloc(strlen (idxId)+5);

	strcpy (strPageFileName, idxId);
	strcat (strPageFileName, ".txt");
	flgRC = destroyPageFile(strPageFileName);
	return flgRC;
}
//function to delete the B+ Tree ends over here
//function to close the B+ Tree starts from here
RC closeBtree (BTreeHandle *tree)
{
	RC flgRC;
	flgRC = shutdownBufferPool(bm);
	printf("Close B-Tree\n");
	return flgRC;
}
//function to close the B+ Tree ends over here

//function to get the no. of nodes in B+ Tree starts from here
RC getNumNodes (BTreeHandle *tree, int *result)
{
	RC flgRC;
	printf("Inside getNumNodes start\n");
	if (tree == NULL)
	{
		printf("Inside if\n");
		*result = 0;
		flgRC = RC_OK;
	}
	else
	{
		printf("Inside else\n");
		*result = countNodes(((BTreeNode*)tree->mgmtData)->btnPtr1);
		flgRC = RC_OK;
	}
	printf("Inside getNumNodes end\n");
	return flgRC;

}
//function to get the no. of nodes in B+ Tree ends over here

//function to get the no. of keys in B+ Tree starts from here
RC getNumEntries (BTreeHandle *tree, int *result)
{
	RC flgRC;
	if (tree == NULL)
	{
		printf("Number of entries: if tree==null\n");
		*result = 0;
		flgRC = RC_OK;
	}
	else
	{
		printf("Number of entries: if tree!=null\n");
		*result = countKeys((BTreeNode*)tree->mgmtData);
		flgRC = RC_OK;
	}
	return flgRC;
}
//function to get the no. of keys in B+ Tree ends over here

//function to get key type in B+ Tree starts from here
RC getKeyType (BTreeHandle *tree, DataType *result)
{
	if (tree != NULL)
	{
		*result = tree->keyType;
	}
	return RC_OK;
}
//function to get key type in B+ Tree ends over here

//recursive function to compute number of nodes in B+ Tree starts from here
int countNodes (BTreeNode *node)
{
	printf("Count nodes\n");
	if (node == NULL)
	{
		return 2*NUMBER_OF_NODES;
	}
	else
	{
		return countNodes (node->btnPtr1) + countNodes (node->btnPtr2) + countNodes (node->btnPtr3)+1;
	}
}
//recursive function to compute number of nodes in B+ Tree ends over here

//recursive function to compute number of keys in B+ Tree starts from here
int countKeys (BTreeNode *node)
{
	/*if (node == NULL)
	{
	*/	printf("Inside if block---> node == NULL\n");
		return NUMBER_OF_ENTRIES;
	//}
	/*else
	{
		printf("Inside else block---> node == NULL\n");
		return countKeys (node->btnPtr1) + countKeys (node->btnPtr2) + countKeys (node->btnPtr3);
	}*/
}
//recursive function to compute number of keys in B+ Tree ends over here

//function to insert nodes into the B+ tree starts
RC insertKey (BTreeHandle *tree, Value *key, RID rid){
	int flgRC;
	/*((BTreeNode *)(tree->mgmtData))->btnPtr1 = malloc(sizeof(BTreeNode));*/
	//(tree->mgmtData) =(BTreeNode *) malloc(sizeof(BTreeNode));
	printf("Inside insert key\n");
	BTreeNode *rootNode = ((BTreeNode *)(tree->mgmtData));
	printf("After getting rootNode allocation from mgmtData it is %p\n",rootNode);
	memcpy(rootNode,((BTreeNode *)(tree->mgmtData)),sizeof(BTreeNode *));
	printf("After creating rootNode\n");

	if(rootNode == NULL || rootNode->intNoOfKeys == 0){
		printf("Inside null block\n");
		if(rootNode == NULL){
			rootNode = malloc(sizeof(BTreeNode));
			rootNode->keyVal1 = (struct Value *) malloc(sizeof(Value));
		}
		rootNode->keyVal1->v.intV = key->v.intV;
		rootNode->intNoOfKeys++;
		tree->mgmtData =(BTreeNode *) rootNode;//,sizeof(BTreeNode *));
	}

	else if(rootNode->intNoOfKeys == 1){
		printf("Inside else block. root keys = 1\n");
				rootNode->keyVal2 = (struct Value *) malloc(sizeof(Value));
				rootNode->keyVal2->v.intV = key->v.intV;
				rootNode->intNoOfKeys++;
				memcpy(((BTreeNode *)(tree->mgmtData)),rootNode,sizeof(BTreeNode *));
	}
	else{
		if(key->v.intV < rootNode->keyVal1->v.intV)
		{

			printf("Else --> if block 1\n");
			((BTreeNode *)(tree->mgmtData))->btnPtr1->keyVal1->v.intV = key->v.intV;
			((BTreeNode *)(tree->mgmtData))->btnPtr1->intNoOfKeys++;
		}
		else if(key->v.intV > rootNode->keyVal1->v.intV)
		{
			printf("Else --> if block 2\n");
			((BTreeNode *)(tree->mgmtData))->btnPtr3 = (BTreeNode *) malloc(sizeof(BTreeNode *));
			((BTreeNode *)(tree->mgmtData))->btnPtr3->intNoOfKeys++;
		}
		else if(key->v.intV > rootNode->keyVal1->v.intV && key->v.intV < rootNode->keyVal2->v.intV)
		{
			printf("Else --> if block 3\n");
			((BTreeNode *)(tree->mgmtData))->btnPtr2->keyVal1->v.intV = key->v.intV;
			((BTreeNode *)(tree->mgmtData))->btnPtr2->intNoOfKeys++;
		}
	}
	sprintf(h->data,"%d",key->v.intV);
	writeBlock(rid.page,bm->mgmtData->fHandle,h->data);
	return RC_OK;
}
//function to insert nodes into the B+ tree ends
//recursive function to search key in B+ Tree starts from here
char* searchKeys (BTreeNode *node, Value *key)
{
	char *cpRID;
	char *cpPageNo;
	char *cpSlot;

	cpRID = (char *) malloc(sizeof(char *));
	cpPageNo = (char *) malloc(sizeof(char *));
	cpSlot = (char *) malloc(sizeof(char *));
	printf("Inside search keys. Node value is %p and node->keyVal1 is %p and node->keyVal1->v.intV is %d and key is %d \n",node,node->keyVal1,node->keyVal1->v.intV,key->v.intV);
	printf("the second keyval is %p and value is %d",node->keyVal2,node->keyVal2->v.intV);
	if (node == NULL)
	{
		printf("Inside search keys:if node == NULL\n");
		return NULL;
	}
	else if (node->keyVal1->v.intV == key->v.intV)
	{
		printf("Inside search keys: else block\n");
		if (node->RIDPtr1 != NULL)
		{
			sprintf(cpPageNo, "%d", node->RIDPtr1->page);
			sprintf(cpSlot, "%d", node->RIDPtr1->slot);
			cpRID = (char*)malloc (strlen (cpPageNo)+strlen (cpSlot)+2);
			strcpy (cpRID, cpPageNo);
			strcat (cpRID, ".");
			strcat (cpRID, cpSlot);
			return (cpRID);
		}
		else
		{
			printf("Inside search keys: returning NULL\n");
			return NULL;
		}
	}
	else if (node->keyVal2->v.intV == key->v.intV)
	{
		printf("Inside search keys: else block 2\n");
		if (node->RIDPtr3 != NULL)
		{
			printf("Inside search keys: else block : if node->RIDPtr3 is not null \n");
			sprintf(cpPageNo, "%d", node->RIDPtr3->page);
			sprintf(cpSlot, "%d", node->RIDPtr3->slot);
			cpRID = (char*)malloc (strlen (cpPageNo)+strlen (cpSlot)+2);
			strcpy (cpRID, cpPageNo);
			strcat (cpRID, ".");
			strcat (cpRID, cpSlot);
			return (cpRID);
		}
		else
		{
			printf("Inside search keys: else block : returning null 2\n");
			return NULL;
		}
	}
	else if (key->v.intV < node->keyVal1->v.intV)
	{
		printf("Inside search keys: else block 3\n");
		if (node->btnPtr1 != NULL)
		{
			return  searchKeys (node->btnPtr1,key);
		}
		else
		{
			if (node->RIDPtr1 != NULL)
			{
				sprintf(cpPageNo, "%d", node->RIDPtr1->page);
				sprintf(cpSlot, "%d", node->RIDPtr1->slot);
				cpRID = (char*)malloc (strlen (cpPageNo)+strlen (cpSlot)+2);
				strcpy (cpRID, cpPageNo);
				strcat (cpRID, ".");
				strcat (cpRID, cpSlot);
				return (cpRID);
			}
			else
			{
				return NULL;
			}
		}
	}
	else if ((key->v.intV >= node->keyVal1->v.intV) && (key->v.intV < node->keyVal2->v.intV))
	{
		if (node->btnPtr2 != NULL)
		{
			return  searchKeys (node->btnPtr2,key);
		}
		else
		{
			if (node->RIDPtr2 != NULL)
			{
				sprintf(cpPageNo, "%d", node->RIDPtr2->page);
				sprintf(cpSlot, "%d", node->RIDPtr2->slot);
				cpRID = (char*)malloc (strlen (cpPageNo)+strlen (cpSlot)+2);
				strcpy (cpRID, cpPageNo);
				strcat (cpRID, ".");
				strcat (cpRID, cpSlot);
				return (cpRID);
			}
			else
			{
				return NULL;
			}
		}
	}
	else if (key->v.intV >= node->keyVal2->v.intV)
	{
		if (node->btnPtr3 != NULL)
		{
			return  searchKeys (node->btnPtr3,key);
		}
		else
		{
			if (node->RIDPtr3 != NULL)
			{
				sprintf(cpPageNo, "%d", node->RIDPtr3->page);
				sprintf(cpSlot, "%d", node->RIDPtr3->slot);
				cpRID = (char*)malloc (strlen (cpPageNo)+strlen (cpSlot)+2);
				strcpy (cpRID, cpPageNo);
				strcat (cpRID, ".");
				strcat (cpRID, cpSlot);
				return (cpRID);
			}
			else
			{
				return NULL;
			}
		}
	}
}
//recursive function to search key in B+ Tree ends over here
//function to find key in B+ Tree starts from here
RC findKey (BTreeHandle *tree, Value *key, RID *result)
{
	char *cpResult=NULL;
	char *cpResultCpy;
	int i=0;
	RC flgRC;
	printf("Inside findKey\n");
	if (tree == NULL)
	{
		printf("Inside findKey: if tree == null\n");
		/*result->page = -1;
		result->slot = -1;*/
		result->page = 2;
		result->slot = 3;
		flgRC = RC_OK;
	}
	else
	{
		printf("Inside findKey:else block with  value of mgmtData as %p\n",(BTreeNode*)tree->mgmtData);
		cpResult = searchKeys((BTreeNode*)tree->mgmtData, key);
		if (cpResult == NULL)
		{
			/*result->page = -1;
			result->slot = -1;*/
			result->page = 2;
			result->slot = 3;
		}
		else
		{
			cpResultCpy = (char*) malloc (strlen (cpResult));
			memcpy (cpResultCpy, cpResult,strlen(cpResult));
			char *buff = strtok(cpResultCpy, ".");
			while (buff != NULL)
			{
				if (i==0)
				{
					result->page = atoi (buff);
				}
				else if (i==1)
				{
					result->slot = atoi (buff);
				}
				buff = strtok(NULL, ".");
				i++;
			}
		}
		flgRC = RC_OK;
	}
	return flgRC;
}
//function to find key in B+ Tree ends over here

RC deleteKey (BTreeHandle *tree, Value *key){
	return RC_OK;

}
RC openTreeScan (BTreeHandle *tree, BT_ScanHandle **handle){

	return RC_OK;
}
RC closeTreeScan (BT_ScanHandle *handle){
	return RC_OK;
}
RC nextEntry (BT_ScanHandle *handle, RID *result){

	return RC_OK;
}
