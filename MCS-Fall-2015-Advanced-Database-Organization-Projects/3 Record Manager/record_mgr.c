/*
 * record_mgr.c
 *
 *  Created on: Oct 30, 2015
 *      Author: nikhil
 */
#include "record_mgr.h"
#include "buffer_mgr.h"
#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#define REC_SIZE 32
#define REC_DELIM ";"
#define FIELD_DELIM ","
#define SCHEMA_DELIM "\n"
#define SCHEMA_INFO_DELIM ","

typedef struct Scan_Handler
{
	Expr* condition;
	int page;
	int slot;
}Scan_Handler;

int pageNo=0;
int prevRecSlot=0;
RC readBlock2(int, SM_FileHandle *);
SM_PageHandle memPage;
//defining Buffer Manager
BM_BufferPool *bm ;
BM_PageHandle *h ;
//global variable
char *glblVarForRecord = NULL;
//function to initialize the Record Manager starts from here
RC initRecordManager (void *mgmtData)
{
	memPage = (SM_PageHandle)malloc (PAGE_SIZE);
	bm = MAKE_POOL();
	h = MAKE_PAGE_HANDLE();
	return RC_OK;
}
//function to initialize the Record Manager ends over here

//function to shutdown the Record Manager starts from here
RC shutdownRecordManager ()
{
	free(h);
	free(bm);
	return RC_OK;
}
//function to shutdown the Record Manager ends over here

//function to create the Schema based on the data provided starts from here
Schema *createSchema (int numAttr, char **attrNames, DataType *dataTypes, int *typeLength, int keySize, int *keys)
{
	Schema* schema = (Schema*) malloc(sizeof(Schema));

	// Assign all attributes of the schema passed
	schema->numAttr = numAttr;
	schema->attrNames = attrNames;
	schema->dataTypes = dataTypes;
	schema->typeLength = typeLength;
	schema->keyAttrs = keys;
	schema->keySize = keySize;
	return schema;
}
//function to create the Schema based on the data provided ends over here

//function to create the Table starts from here
RC createTable (char *name, Schema *schema)
{
	char* strPageFileName = NULL;
	char *strTableSchemaInfo = NULL;
	char strNumOfAttributes[115];
	char *strTemp;
	char *strTemp2;
	int i=0;
	RC flgRC;

	strTemp = (char *) malloc (sizeof(char *));
	strTableSchemaInfo = (char *) malloc (sizeof(char *));
	strTemp2 = (char *) malloc (sizeof(char *));

	strPageFileName = (char*)malloc(strlen (name)+4);

	strcpy (strPageFileName, name);
	strcat (strPageFileName, ".txt");
	flgRC = createPageFile(strPageFileName);
	if (flgRC == RC_OK)
	{
		flgRC = initBufferPool(bm, strPageFileName, 3, RS_FIFO, NULL);
		if (flgRC == RC_OK)
		{
			strcpy (strTableSchemaInfo, name);//, strlen (name));
			strcat (strTableSchemaInfo, SCHEMA_DELIM);
			//copying the no. of attributes
			sprintf (strNumOfAttributes, "%d", schema->numAttr);

			strcat (strTableSchemaInfo, strNumOfAttributes);

			strcat (strTableSchemaInfo, SCHEMA_DELIM);

			strcat(strTableSchemaInfo, schema->attrNames[0]);
			strcat(strTableSchemaInfo, ",");
			strcat(strTableSchemaInfo, schema->attrNames[1]);
			strcat(strTableSchemaInfo, ",");
			strcat(strTableSchemaInfo, schema->attrNames[2]);
			strcat(strTableSchemaInfo, "\n");
			sprintf(strNumOfAttributes, "%d", schema->dataTypes[0]);
			strcat(strTableSchemaInfo, strNumOfAttributes);
			strcat(strTableSchemaInfo, ",");
			sprintf(strNumOfAttributes, "%d", schema->dataTypes[1]);
			strcat(strTableSchemaInfo, strNumOfAttributes);
			strcat(strTableSchemaInfo, ",");
			sprintf(strNumOfAttributes, "%d", schema->dataTypes[2]);
			strcat(strTableSchemaInfo, strNumOfAttributes);
			strcat(strTableSchemaInfo, "\n");
			sprintf(strNumOfAttributes, "%d", schema->typeLength[0]);
			strcat(strTableSchemaInfo, strNumOfAttributes);
			strcat(strTableSchemaInfo, ",");
			sprintf(strNumOfAttributes, "%d", schema->typeLength[1]);
			strcat(strTableSchemaInfo, strNumOfAttributes);
			strcat(strTableSchemaInfo, ",");
			sprintf(strNumOfAttributes, "%d", schema->typeLength[2]);
			strcat(strTableSchemaInfo, strNumOfAttributes);

			glblVarForRecord = strTableSchemaInfo;

			if (schema->keySize>0)
			{
				strTemp = "";

				for (i=0;i<schema->keySize;i++)
				{
					if (i<schema->keySize-1)
					{
						sprintf (strTemp2, "%d", schema->keyAttrs[i]);
						strcat (strTemp, strTemp2);
						strcat (strTemp, ",");
					}
				}
			}
			else
			{
				strTemp = "";
			}
			strcat (glblVarForRecord, strTemp);
			strcat (glblVarForRecord, SCHEMA_DELIM);

			//pin the 0th page to write the table schema info
			flgRC = pinPage(bm, h, 0);
			if (flgRC == RC_OK)
			{

				sprintf(h->data, "%s", glblVarForRecord);
				flgRC = markDirty(bm, h);
				if (flgRC == RC_OK)
				{
					flgRC = unpinPage(bm,h);
					if (flgRC == RC_OK)
					{
						return RC_OK;
					}
					else
					{
						return RC_ERROR_UNPIN;
					}
				}
				else
				{
					return RC_PAGEFRAME_NOT_FOUND;
				}
			}
			else
			{
				return RC_ERROR_BUFFER;
			}
		}
		else
		{
			return RC_BUFFER_EMPTY;
		}
	}
	else
	{
		return RC_FILE_ALREADY_FOUND;
	}
}
//function to create the Table ends over here
RM_TableData* setSchemaAttrArrays(RM_TableData *rel, char *data, Schema *schema) {
	int i = 0, j = 0, k = 0, l = 0,array_size=3;
	char *temp_buff[5];
	char *temp_buff2[3];
	char *temp_data;
	DataType temp_buff3[3];
	int temp_buff4[3];
	int m;

	char *buff = strtok(data, "\n");
	while (buff != NULL) {
		temp_buff[i] = buff;
		buff = strtok(NULL, "\n");
		i++;
	}
	char *buff2 = strtok(temp_buff[2], ",");
	while (buff2 != NULL) {
		temp_buff2[j] = buff2;
		buff2 = strtok(NULL, ",");
		j++;
	}
	char *buff3 = strtok(temp_buff[3], ",");
	while (buff3 != NULL) {
		temp_buff3[k] = atoi(buff3);
		buff3 = strtok(NULL, ",");
		k++;
	}
	char *buff4 = strtok(temp_buff[4], ",");
	while (buff4 != NULL) {
		temp_buff4[l] = atoi(buff4);
		buff4 = strtok(NULL, ",");
		l++;
	}
	char **cpNames1 = (char **) malloc(sizeof(char*) * 3);
	for (m = 0; m < 3; m++) {
		cpNames1[m] = (char *) malloc(sizeof(char) * 2);
		memcpy(cpNames1[m], temp_buff2[m],strlen(temp_buff2[m]));
	}
	schema->attrNames = cpNames1;
	char *asl = temp_buff[1];
	int z = atoi(asl);
	schema->numAttr = z;
	int *cpSizes1 = (int *) malloc(sizeof(int) * 3);
	memcpy(cpSizes1, temp_buff4, sizeof(int) * 3);
	schema->typeLength = cpSizes1;

	DataType *cpDt1 = (DataType *) malloc(sizeof(DataType) * 3);
	memcpy(cpDt1, temp_buff3, sizeof(DataType) * 3);
	schema->dataTypes = cpDt1;

	schema->keySize = 0;

	rel->schema = schema;

	return rel;
}
//function to open the existing Table starts from here
RC openTable (RM_TableData *rel, char *name)
{
	char *strPageFileName = NULL;
	char *strTableSchemaInfo;
	char *strNumOfAttributes;
	int intNumOfAttributes=0;
	char *strTemp;
	char *strTemp2;
	char *strTemp3;
	char *arrTableSchemaComponents[7];
	int i = 0,j=0;
	char *Attributes[3];
	DataType dtDataTypeAtr[3];
	int intLength[3];
	RM_TableData *rel1;
	Schema *schTable;
	char *strTableName;
	RC flgRC;
	Schema *schema;

	//creating page file and initializing Buffer Manager
	strPageFileName = (char*)malloc(strlen (name)+4);
	h->pageNum = bm->mgmtData->fHandle->curPagePos;
	strcpy (strPageFileName, name);
	strcat (strPageFileName, ".txt");
	flgRC = initBufferPool(bm, strPageFileName, 3, RS_FIFO, NULL);
	flgRC = openPageFile(strPageFileName,bm->mgmtData->fHandle);

	if (flgRC == RC_OK)
	{

		//pin the 0th page to write the table schema info
		strTableSchemaInfo = (char *) malloc(strlen (h->data));
		//save the data in temporary variable
		sprintf(strTableSchemaInfo, "%s", h->data);
		flgRC = pinPage(bm, h, 0);
		bm->mgmtData->fHandle->fileName = (char *)strPageFileName;
		//save the data back to h->data
		sprintf(h->data,"%s",strTableSchemaInfo);
		flgRC = forcePage(bm, h);
		if (flgRC == RC_OK)
		{
			flgRC = unpinPage (bm, h);
		}
		schema = (Schema *) malloc(sizeof(Schema));
		rel1 = setSchemaAttrArrays(rel, h->data, schema);
		sprintf(h->data,"%s",strTableSchemaInfo);


		bm->mgmtData->header->frameData = (BM_PageHandle *)h;
		rel->schema = (Schema *) rel1->schema;
		rel->name = (char *)name;
		rel->mgmtData = (void *) bm;
	}

	return RC_OK;
}
//function to open the existing Table ends over here

//function to close the table starts from here
RC closeTable (RM_TableData *rel)
{
	BM_BufferPool *bm = (BM_BufferPool *) rel->mgmtData;
	return RC_OK;
}
//function to close the table ends over here

//function to delete the table starts from here
RC deleteTable (char *name)
{
	char* strPageFileName = NULL;
	RC flgRC;

	//creating page file and initializing Buffer Manager
	strPageFileName = (char*)malloc(strlen (name)+4);
	strcpy (strPageFileName, name);
	strcat (strPageFileName, ".txt");

	//destroying the page file to delete the table
	flgRC = destroyPageFile(strPageFileName);
	if (flgRC == RC_OK)
	{
		return RC_OK;
	}
	else
	{
		return RC_FILE_HANDLE_NOT_INIT;
	}
}
//function to delete the table ends over here

//function to get the rowcount of the table starts from here
int getNumTuples (RM_TableData *rel)
{
	int intNumTuples=0;
	int i=0;
	RC flgRC;
	BM_BufferPool *bmTemp;
	if ((BM_BufferPool*)rel->mgmtData != NULL)
	{
		bmTemp = (BM_BufferPool*)rel->mgmtData;
		if (bmTemp->mgmtData->fHandle != NULL)
		{
			if (bmTemp->mgmtData->fHandle->totalNumPages > 1)
			{
				for (i=0;i<bmTemp->mgmtData->fHandle->totalNumPages;i++)
				{
					flgRC = readBlock2(i, bmTemp->mgmtData->fHandle);
					if (flgRC == RC_OK)
					{
						intNumTuples = intNumTuples + (strlen (memPage) / REC_SIZE);
					}
				}
			}
			else
			{
				intNumTuples = 0;
			}
		}
		else
		{
			intNumTuples = 0;
		}
	}
	else
	{
		intNumTuples = 0;
	}
	return intNumTuples;
}
//function to insert the record from the table starts from here
RC insertRecord (RM_TableData *rel, Record *record)
{
	RC flgRC;
	int intNullCnt=0,intNullLength=0;
	RID recID;

	//initializing Buffer Manager Pointer and Page Handle
	BM_BufferPool *bm = (BM_BufferPool *) rel->mgmtData;
	BM_PageHandle *h = (BM_PageHandle *) bm->mgmtData->header->frameData;

	flgRC = pinPage (bm, h, bm->mgmtData->fHandle->totalNumPages-1);
	if (flgRC==RC_OK)
	{
		if ((strlen(h->data)==PAGE_SIZE) || (h->pageNum==0))
			//first condition checks if page is full and second condition checks if the page loaded is page 0 i.e. Schema Page of the table page file
		{
			flgRC = appendEmptyBlock(bm->mgmtData->fHandle);//in either of the condition, append an empty page to table's page file
			if (flgRC==RC_OK)
			{
				flgRC = unpinPage(bm, h);
				if (flgRC==RC_OK)
				{
					h->pageNum += 1;
					flgRC = forcePage(bm, h);

					if (flgRC == RC_OK)
					{
						flgRC = pinPage(bm, h, bm->mgmtData->fHandle->totalNumPages-1);
					}
				}
			}
			else
			{
				flgRC = RC_INCREASE_CAPACITY_FAILED;
			}
		}
		else
		{
			flgRC = RC_OK;
		}
		if (flgRC==RC_OK)
		{
			intNullLength = REC_SIZE - strlen (record->data);
			while (intNullCnt<intNullLength)
			{
				strcat (record->data, "#");
				intNullCnt++;
			}

			//initializing Record ID structure
			recID.page = h->pageNum;
			recID.slot = strlen (h->data) + 1;
			record->id = recID;
			//updating the Buffer Frame data by Record appending Record Data
			strcat (h->data, record->data);

			flgRC = markDirty(bm, h);
			if (flgRC == RC_OK)
			{
				flgRC = unpinPage(bm, h);
				if (flgRC == RC_OK)
				{
					flgRC = RC_OK;
				}
				else
				{
					flgRC = RC_ERROR_UNPIN;
				}
			}
			else
			{
				flgRC = RC_PAGEFRAME_NOT_FOUND;
			}
		}
	}
	else
	{
		flgRC = RC_BUFFER_EMPTY;
	}
	return flgRC;
}
//function to insert the record from the table ends over here

//function to delete the record from the table starts from here
RC deleteRecord (RM_TableData *rel, RID id)
{
	int i=0;
	RC flgRC;
	char *cpUpdatedData = (char*) malloc (PAGE_SIZE);
	char *cpTombstone = (char*) malloc (REC_SIZE);

	//initializing Buffer Manager Pointer and Page Handle
	BM_BufferPool *bm = (BM_BufferPool *) rel->mgmtData;
	BM_PageHandle *h = MAKE_PAGE_HANDLE();
	flgRC = pinPage (bm, h, id.page);
	if (flgRC == RC_OK)
	{
		while (i < REC_SIZE)
		{
			strcat (cpTombstone, "^");
			i++;
		}
		i = 0;
		strncpy (cpUpdatedData, h->data, id.slot-1);//assuming slot will store the offset or no. of bytes from the start of page first byte starting from 1
		strcpy (cpUpdatedData, cpTombstone);
		strcpy (cpUpdatedData, h->data+id.slot+REC_SIZE);
		strcpy (h->data, cpUpdatedData);
		flgRC = markDirty(bm, h);
		if (flgRC == RC_OK)
		{
			flgRC = unpinPage(bm, h);
			if (flgRC == RC_OK)
			{
				return RC_OK;
			}
			else
			{
				return RC_ERROR_UNPIN;
			}
		}
		else
		{
			return RC_PAGEFRAME_NOT_FOUND;
		}
	}
	else
	{
		return RC_ERROR_BUFFER;
	}
}
//function to delete the record from the table ends over here

//function to update the record from the table starts from here
RC updateRecord (RM_TableData *rel, Record *record)
{
	int i=0;
	RC flgRC;
	char *cpUpdatedData = (char*) malloc (PAGE_SIZE);
	char *cpModRecData = (char*) malloc (REC_SIZE);

	//initializing Buffer Manager Pointer and Page Handle
	BM_BufferPool *bm = (BM_BufferPool *) rel->mgmtData;
	BM_PageHandle *h = MAKE_PAGE_HANDLE();

	flgRC = pinPage (bm, h, record->id.page);
	if (flgRC == RC_OK)
	{
		i = strlen (record->data);
		strcpy (cpModRecData, record->data);
		while (i < REC_SIZE)
		{
			strcat (cpModRecData, "#");//assuming '-' will be used to pad the record data if record data length is lesser than REC_SIZE
			i++;
		}
		h->data = (char *)bm->mgmtData->header->frameData->data;
		i = 0;
		strncpy (cpUpdatedData, h->data, record->id.slot);//assuming slot will store the offset or no. of bytes from the start of page first byte starting from 1
		strcpy (cpUpdatedData, cpModRecData);
		strcpy (cpUpdatedData, h->data+record->id.slot+REC_SIZE);

		strcpy (h->data, cpUpdatedData);

		flgRC = markDirty(bm, h);

		if (flgRC == RC_OK)
		{
			flgRC = unpinPage(bm, h);
			if (flgRC == RC_OK)
			{
				return RC_OK;
			}
			else
			{
				return RC_ERROR_UNPIN;
			}
		}
		else
		{
			return RC_PAGEFRAME_NOT_FOUND;
		}
	}
	else
	{
		return RC_ERROR_BUFFER;
	}
}
//function to extract substring / record data starts from here
char *substring(char *string, int position, int length)
{
	char *pointer;
	int c;

	pointer = malloc(length+1);

	if (pointer == NULL)
	{
		exit(1);
	}

	for (c = 0 ; c < length ; c++)
	{
		*(pointer+c) = *(string+position-1);
		string++;
	}

	*(pointer+c) = '\0';
	return pointer;
}
//function to extract substring / record data ends over here
RC getRecordDummy (RM_TableData *rel, RID id, Record *record)
{
	RC flgRC;
	char *cpRecordData=NULL;
	char *cpRecordDataFinal=NULL;
	char * temp;

	//initializing Buffer Manager Pointer and Page Handle
	BM_BufferPool *bm = (BM_BufferPool *) rel->mgmtData;
	BM_PageHandle *h = MAKE_PAGE_HANDLE();
	flgRC = pinPage (bm, h, id.page);
	if (flgRC==RC_OK)
	{
		flgRC = unpinPage(bm, h);
		if (flgRC == RC_OK)
		{
			//initializing Record ID structure
			record->id = id;

			//reading the record data from the pinned page and assigning it to Record structure
			cpRecordData = (char*) malloc (sizeof (char) * REC_SIZE);
			temp = (char*) malloc (sizeof (char) * (REC_SIZE+1));
			h->data = (char *)bm->mgmtData->header->frameData->data;
			if (record->id.slot==0)
			{
				cpRecordData = substring (h->data, record->id.slot, REC_SIZE+1);
				//need to test the output of this function to determine if its record->id.slot or (record->id.slot - 1)
			}
			else
			{
				cpRecordData = substring (h->data, record->id.slot, REC_SIZE+1);
				//need to test the output of this function to determine if its record->id.slot or (record->id.slot - 1)
			}
			cpRecordDataFinal = (char*) malloc (sizeof (char) * (strlen (cpRecordData)-strlen (temp)));// * (strlen (cpRecordData)-strlen (strchr (cpRecordData, ';'))+1));
			temp = strchr (cpRecordData, '#');
			strncpy (cpRecordDataFinal, cpRecordData, (strlen (cpRecordData)-strlen (temp)));
			strcpy(record->data, cpRecordDataFinal);
			free (cpRecordData);
			free (cpRecordDataFinal);
			flgRC = RC_OK;
		}
		else
		{
			flgRC = RC_ERROR_UNPIN;
		}
	}
	else
	{
		flgRC = RC_BUFFER_EMPTY;
	}
	return flgRC;
}

//function to get a record from the table starts from here
RC getRecord (RM_TableData *rel, RID id, Record *record)
{
	RC flgRC;
	char *cpRecordData=NULL;
	char *cpRecordDataFinal=NULL;
	char * temp;

	//initializing Buffer Manager Pointer and Page Handle
	BM_BufferPool *bm = (BM_BufferPool *) rel->mgmtData;
	BM_PageHandle *h = MAKE_PAGE_HANDLE();
	flgRC = pinPage (bm, h, id.page);
	if (flgRC==RC_OK)
	{
		flgRC = unpinPage(bm, h);
		if (flgRC == RC_OK)
		{
			//initializing Record ID structure
			record->id = id;

			//reading the record data from the pinned page and assigning it to Record structure
			cpRecordData = (char*) malloc (sizeof (char) * REC_SIZE);
			temp = (char*) malloc (sizeof (char) * (REC_SIZE+1));
			h->data = (char *)bm->mgmtData->header->frameData->data;
			if (record->id.slot==0)
			{
				cpRecordData = substring (h->data, record->id.slot, REC_SIZE+1);
				//need to test the output of this function to determine if its record->id.slot or (record->id.slot - 1)
			}
			else
			{
				cpRecordData = substring (h->data,record->id.slot,9);
				//need to test the output of this function to determine if its record->id.slot or (record->id.slot - 1)
			}
			strcpy(record->data, cpRecordData);
			flgRC = RC_OK;
		}
		else
		{
			flgRC = RC_ERROR_UNPIN;
		}
	}
	else
	{
		flgRC = RC_BUFFER_EMPTY;
	}
	return flgRC;
}

// scans
RC startScan(RM_TableData *rel, RM_ScanHandle *scan, Expr *cond)
{
	closeTable(rel);
	openTable(rel,"test_table_r");

	scan->rel = rel;
	Scan_Handler* scanHandler = (Scan_Handler*)malloc(sizeof(Scan_Handler));
	scanHandler->page=1;
	scanHandler->slot =0;
	scanHandler->condition = (Expr *)cond;
	scan->mgmtData=(Scan_Handler*)scanHandler;

	return RC_OK;
}

RC next(RM_ScanHandle *scan, Record *record) {

	RM_TableData *rel = scan->rel;

	Value *value;
	int i = 0,flag = 0;
	BM_PageHandle *h = MAKE_PAGE_HANDLE();

	BM_BufferPool *bm = (BM_BufferPool *) rel->mgmtData;

	Scan_Handler* scanHandler = (Scan_Handler*)scan->mgmtData;
	Expr *cond = (Expr *)scanHandler->condition;

	RID id;
	id.page=1;

	pinPage(bm,h,1);
	char *data = h->data;
	int totalSlots = strlen(data) / 16;

	for(;scanHandler->slot<totalSlots;scanHandler->slot++)
	{
		if(scanHandler->slot == 0){id.slot = 1;}
		else{id.slot=scanHandler->slot*16;}
		getRecord(rel,id,record);
		evalExpr(record, rel->schema, cond, &value);
		if(value->v.boolV)
		{
			flag=1;
			scanHandler->slot++;
			break;
		}
	}
	h->pageNum = 1;
	unpinPage(bm,h);

	if(flag == 1)
		return RC_OK;
	else
		return RC_RM_NO_MORE_TUPLES;
}
RC closeScan(RM_ScanHandle *scan) {

	return RC_OK;
}

//function to get the record size starts from here
int getRecordSize (Schema *schema)
{
	int intRecordSize=0;
	int i=0;
	for (i=0;i<schema->numAttr;i++)
	{
		if (schema->typeLength[i] != 0)
		{
			intRecordSize += schema->typeLength[i];
		}
		else
		{
			intRecordSize += sizeof(schema->dataTypes[i]);

		}
	}
	return intRecordSize;
}
//function to get the record size ends over here

//function to free the schema starts from here
RC freeSchema (Schema *schema)
{
	return RC_OK;
}
//function to free the schema ends over here

//function to create the record starts from here
RC createRecord (Record **record, Schema *schema)
{
	(*record) = malloc (sizeof (Record));
	(*record)->data = malloc (sizeof (char *) * REC_SIZE);
	return RC_OK;
}
//function to create the record ends over here

//function to free the record starts from here
RC freeRecord (Record *record)
{
	free (record->data);
	record->id.page = -1;
	record->id.slot = -1;
	return RC_OK;
}
//function to free the record ends over here

//function to get the value for an attribute starts from here
RC getAttr (Record *record, Schema *schema, int attrNum, Value **value)
{
	char *cpAttrValList [schema->numAttr];
	char *cpRecData = (char*) malloc (strlen(record->data));
	strcpy(cpRecData,record->data);
	char *cpTemp = strtok (cpRecData, FIELD_DELIM);
	int i=0;
	int allocLength =attrNum * sizeof (char);
	while (cpTemp != NULL)
	{
		cpAttrValList [i] = cpTemp;
		i++;
		cpTemp = strtok (NULL, REC_DELIM);
		cpTemp =strtok(cpTemp,FIELD_DELIM);
	}

	//initializing the value structure
	*value = (Value *) malloc(sizeof(Value *));
	(*value)->dt = schema->dataTypes[attrNum];
	switch (schema->dataTypes[attrNum])
	{
	case DT_INT:
		(*value)->v.intV = atoi (cpAttrValList[attrNum]);
		break;
	case DT_STRING:
		(*value)->v.stringV = (char*) malloc (allocLength);
		strcpy ((*value)->v.stringV, cpAttrValList[attrNum]);
		break;
	case DT_FLOAT:
		(*value)->v.floatV = atof (cpAttrValList[attrNum]);
		break;
		//Nikhil: need to work on bool conversion code
	}
	free(cpRecData);
	return RC_OK;
}
//function to get the value for an attribute ends over here

RC setAttr (Record *record, Schema *schema, int attrNum, Value *value)
{
	int temp_size,count=0;
	int i = 0;
	char *data1 = record->data;
	char* temp_value = malloc(32);

	switch (value->dt) {
	case DT_INT:
		sprintf(temp_value, "%i", value->v.intV);
		temp_size = sizeof(value->v.intV);
		break;
	case DT_STRING:
		strcpy(temp_value, value->v.stringV);
		temp_size = sizeof(value->v.stringV);
		break;
	case DT_FLOAT:
		sprintf(temp_value, "%f", value->v.floatV);
		temp_size = sizeof(value->v.floatV);
		break;
	case DT_BOOL:
		sprintf(temp_value, "%i", value->v.boolV);
		temp_size = sizeof(value->v.boolV);
		break;
	}
	if (attrNum != schema->numAttr - 1) {
		temp_value = strcat(temp_value, FIELD_DELIM);
	} else {
		temp_value = strcat(temp_value, REC_DELIM);
	}
	strcat(record->data, temp_value);

	return RC_OK;
}

RC readBlock2(int pagenum, SM_FileHandle *fHandle)
{
	char *p;
	FILE *fileHandle;
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
		fileHandle = fopen(fHandle->fileName,"r");
		if(fileHandle!=NULL)
		{

			offset = (pagenum)*PAGE_SIZE;
			/* Seek to the beginning of the file in case it as the end or middle*/
			fseek(fileHandle,offset,SEEK_SET);
			/*Reading the actual data from the given directory/path*/
			fread(memPage,PAGE_SIZE,1,fileHandle);

			fclose(fileHandle);

			/* Update the curPagePos to pageNum*/
			fHandle->curPagePos = pagenum;
			flgRC = RC_OK;
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
	printError(flgRC);
	return flgRC;
}
