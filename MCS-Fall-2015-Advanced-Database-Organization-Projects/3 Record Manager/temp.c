/*
 * temp.c
 *
 *  Created on: Oct 30, 2015
 *      Author: nikhil
 */
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

void createTable (char*);

void createTable (char *name)
{
	char *strPageFileName = NULL;
	int intLength = strlen (name)+4;
	printf ("%d\n",intLength);

	//creating page file and initializing Buffer Manager
	strPageFileName = (char*)malloc(intLength);
	printf ("After Malloc\n");
	strcpy (strPageFileName, name);
	strcat (strPageFileName, ".txt");
	printf ("After strcpy\n");
	printf ("%s\n", strPageFileName);
}

void main ()
{
	createTable("test");
	createTable("test2");
	createTable("test32423");
}
