/*
 * MergeSort.cpp
 *
 *  Created on: Mar 15, 2016
 *      Author: niks
 */

#include <list>
#include <vector>
#include <cmath>
#include <fstream>
#include <iostream>
#include <stdlib.h>
#include <cstring>
#include <iomanip>
#include <sstream>
#include <SharedMemorySort.h>

using namespace std;

void ThreadMergeSort (int i, string strOutputFileName);
void ThreadSortPass (long lngBegin, long lngEnd);
void ThreadMergePass (long lngBegin, long lngMiddle, long lngEnd);
void WriteToFile(int i, string strOutputFileName);

string strOutputFileName;
std::vector<Data> vectKeyValue;
std::vector<Data> vectTemp;

void ThreadMergeSort(int i, string strOutputFileName)
{

	//cout << "Sorting File Block # " << i+1 << " started\n";
	//cout << "1" << endl;
	//cout << "14\n" << endl;
	//std::list<Data> lstSorted = ThreadSortPass (lstUnsorted);
	ThreadSortPass(0, vectKeyValue.size());
	//cout << "15\n" << endl;
	//cout << "Sorting File Block # " << i+1 << " completed\n";
	//cout << "2" << endl;
	//cout << "Writing the sorted output for File Block # " << i+1 << " started\n";
	WriteToFile (i, strOutputFileName);
	//cout << "16\n" << endl;
	//cout << "Writing the sorted output for File Block # " << i+1 << " completed\n";
	//return strReturnFileName;
	/*for (std::list<Data>::iterator it=lstSorted.begin();it!=lstSorted.end();it++)
	{
		//cout << "3" << endl;
		Data curItem = *it;
		//cout << "4" << endl;
		cout << i << "\t" << curItem.getKey() << endl;
	}*/
}

void ThreadSortPass (long lngBegin, long lngEnd)
{
	//cout << "lngBegin = " << lngBegin << "\tlngEnd = " << lngEnd << endl;
	//cout << "4" << endl;
	//cout << "ThreadSortPass called with lstUnsorted size " << lstUnsorted.size() << endl;
	if (lngEnd - lngBegin >= 2)
	{
		//cout << "5" << endl;
		long lngMiddle = (lngEnd + lngBegin) / 2;
		//std::list<Data> lstUnsortedLeft;
		//std::list<Data> lstUnsortedRight;
		/*int i =0;
		for (std::list<Data>::iterator it=lstUnsorted.begin();it!=lstUnsorted.end();it++)
		{
			//cout << "6" << endl;
			if (i<intMiddle)
			{
				//cout << "7" << endl;
				lstUnsortedLeft.push_back(*it);
			}
			else
			{
				//cout << "8" << endl;
				lstUnsortedRight.push_back(*it);
			}
			i++;
		}*/
		//cout << "9" << endl;
		//cout << lstUnsortedLeft.size() << endl;
		//cout << "Calling recursive sort function for left array with size " << lstUnsortedLeft.size() << endl;
		//std::list<Data> lstSortedLeft = ThreadSortPass (lstUnsortedLeft);
		ThreadSortPass (lngBegin, lngMiddle);
		//cout << "10" << endl;
		//cout << "Calling recursive sort function for right array with size " << lstUnsortedRight.size() << endl;
		//std::list<Data> lstSortedRight = ThreadSortPass (lstUnsortedRight);
		ThreadSortPass (lngMiddle, lngEnd);
		//cout << "11" << endl;
		//cout << "Calling Mergining function for sorted arrays with sizes Left Size = " << lstSortedLeft.size() <<
				//" and Right Size = " << lstSortedRight.size() << endl;
		//std::list<Data> lstSorted = ThreadMergePass (lstSortedLeft, lstSortedRight);
		ThreadMergePass (lngBegin, lngMiddle, lngEnd);
		for (long k=lngBegin; k<lngEnd; k++)
		{
			Data dataTemp("","");
			vectKeyValue[k] = vectTemp[k];
			vectTemp[k] = dataTemp;
		}
		//cout << "12" << endl;
		//return lstSorted;
	}
	else
	{
		//cout << "13" << endl;
		//return lstUnsorted;
	}
}

//std::list<Data> ThreadMergePass (std::list<Data> lstSortedLeft, std::list<Data> lstSortedRight)
void ThreadMergePass (long lngBegin, long lngMiddle, long lngEnd)
{
	//cout << "lngBegin = " << lngBegin << "\tlngMiddle = " << lngMiddle << "\tlngEnd = " << lngEnd << endl;
	//cout << "ThreadMergePass called with lstSortedLeft size " << lstSortedLeft.size() <<
			//" and lstSortedRight size " << lstSortedRight.size() << endl;
	//std::list<Data> lstSortedMerged;
	long i = lngBegin, j = lngMiddle;
	//int i=0, j=0;
	//cout << "14" << endl;
	//std::list<Data>::iterator itLeft=lstSortedLeft.begin();
	//cout << "15" << endl;
	//std::list<Data>::iterator itRight=lstSortedRight.begin();
	//cout << "16" << endl;
	for (long k=lngBegin; k<lngEnd; k++)
	{
		if (i<lngMiddle && ((j>=lngEnd) || (vectKeyValue[i].getKey()<=vectKeyValue[j].getKey())))
		{
			vectTemp[k] = vectKeyValue[i];
			i++;
		}
		else
		{
			vectTemp[k] = vectKeyValue[j];
			j++;
		}
	}

	/*while ((i < lngMiddle) && (j < lngEnd))
	{
		cout << "17" << endl;
		//Data dataLeft = *itLeft;
		//Data dataLeft = vectKeyValue[i];
		//cout << "17.1" << endl;
		//cout << lstSortedLeft.size() << endl;
		//cout << "Left = \t " << dataLeft.getValue() << endl;
		//Data dataRight = *itRight;
		//Data dataRight = vectKeyValue[j];
		//cout << lstSortedRight.size() << endl;
		//cout << "Right = \t " << dataRight.getValue() << endl;
		//cout << "17.2" << endl;
		cout << i << endl << j << endl;
		if (vectKeyValue[i].getKey()<= vectKeyValue[j].getKey())
		{
			//cout << "18" << endl;
			lstSortedMerged.push_back(*itLeft);
			lstSortedMerged.push_back(*itRight);
			itLeft++;
			itRight++;
			cout << vectKeyValue[i].getValue() << endl << vectKeyValue[j].getValue() << endl;
			Data dataTemp = vectKeyValue[j];
			vectKeyValue[j] = vectKeyValue[i];
			vectKeyValue[i] = dataTemp;
			cout << vectKeyValue[i].getValue() << endl << vectKeyValue[j].getValue() << endl;
			i++;
		}
		else //if (vectKeyValue[i].getKey().compare(vectKeyValue[j].getKey())>0)
		{
			cout << "19" << endl;
			lstSortedMerged.push_back(*itRight);
			itRight++;
			cout << vectKeyValue[i].getValue() << endl << vectKeyValue[j].getValue() << endl;
			Data dataTemp = vectKeyValue[i];
			vectKeyValue[i] = vectKeyValue[j];
			vectKeyValue[j] = dataTemp;
			cout << vectKeyValue[i].getValue() << endl << vectKeyValue[j].getValue() << endl;
			j++;
		}
		else if (dataLeft.getKey().compare(dataRight.getKey())<0)
		{
			//cout << "20" << endl;
			lstSortedMerged.push_back(*itLeft);
			itLeft++;
			i++;
		}
	}*/
	/*while (itLeft != lstSortedLeft.end())
	{
		//cout << "21" << endl;
		lstSortedMerged.push_back(*itLeft);
		itLeft++;
		i++;
	}
	while (itRight != lstSortedRight.end())
	{
		//cout << "22" << endl;
		lstSortedMerged.push_back(*itRight);
		itRight++;
		j++;
	}
	return lstSortedMerged;*/
}

void WriteToFile(int i, string strOutputFileName)
{
	//cout << "WriteToFile called with lstSortedMerged size " << lstSortedMerged.size() << endl;
	ofstream fileWriter;

	//char* strInterimFileName;
	//char* strFileNo;
	//strInterimFileName = (char *) malloc (200 * sizeof(char));
	//strFileNo = (char*) malloc (5 * sizeof (char));
	/*strcpy(strInterimFileName,"temp_out_");
	sprintf(strFileNo, "%05d", i);
	strcat(strInterimFileName, strFileNo);
	strcat(strInterimFileName, ".txt");*/

	//cout << "17\n" << endl;
	string strInterimFileName = strOutputFileName;
	//cout << strInterimFileName << endl << strOutputFileName << endl;
	//cout << "18\n" << endl;
	/*stringstream ss;
	ss << setw(5) << setfill('0') << i;
	strInterimFileName += ss.str();
	strInterimFileName += ".txt";
	//cout << strInterimFileName;
	//cout << strInterimFileName << '\n';    //The line I have commented -- Suraj*/

	fileWriter.open (strInterimFileName.c_str());
	//cout << "19\n" << endl;
	if (fileWriter.is_open())
	{
		//cout << "20\n" << endl;
		//cout << "Inside file created\n";
		//cout << "List No.: " << i << endl;
		//for (std::list<Data>::iterator it=vectKeyValue.begin();it!=vectKeyValue.end();it++)
		for (int i=0; i<vectKeyValue.size();i++)
		{
			//cout << "21\n" << endl;
			//Data curItem = *it;
			if (vectKeyValue[i].getValue()!="")
			{
				//cout << "22\n" << endl;
				//fileWriter << curItem.getValue() << endl;
				fileWriter << vectKeyValue[i].getValue() << endl;
				//cout << "23\n" << endl;
				fileWriter.flush();
				//cout << "24\n" << endl;
			}
		}

		fileWriter.close();
		//cout << "25\n" << endl;
		//return strInterimFileName;
	}
	else
	{
		//return NULL;
	}
	//free (strInterimFileName);
	//free (strFileNo);
}

int main(int argc, char *argv[])
{
	// Check the number of parameters
	if (argc < 3)
	{
		// Tell the user how to run the program
		std::cerr << "Usage: " << argv[0] << " <Input File Name> <Output File Name>" << std::endl;
		return 0;
	}

	// Check the number of parameters
	if (argv[1] == "")
	{
		// Tell the user how to run the program
		std::cerr << "Input File Name to be sorted cannot be blank!" << std::endl;
		return 0;
	}

	// Check the number of parameters
	if (argv[2] == "")
	{
		// Tell the user how to run the program
		std::cerr << "Output File Name to be sorted cannot be blank!" << std::endl;
		return 0;
	}

	string strInputFileName = argv[1];
	string strOutputFileName = argv[2];
	//cout << strInputFileName << endl;
	//cout << strOutputFileName << endl;
	//cout << "1\n";
	ifstream fileReader (strInputFileName.c_str());
	//cout << "2\n";
	long lngLineCnt;
	if (((filesize (strInputFileName.c_str()) / 100)) != 0)
	{
		//cout << "3\n";
		lngLineCnt = (round(filesize (strInputFileName.c_str()) / 100) + 1);
	}
	else
	{
		//cout << "4\n";
		lngLineCnt = (round(filesize (strInputFileName.c_str()) / 100));
	}
	string line;
	if (fileReader.is_open())
	{
		//cout << "5\n";
		int i, j;
		for (i=0;(fileReader.eof()==false);i++)
		{
			//cout << "6\n";
			//cout << "Reading File Block # " << i+1 << " started\n";
			for (j=0;((j<lngLineCnt) && (fileReader.eof()==false));j++)
			{
				//cout << "7\n";
				getline (fileReader,line);
				//cout << "8\n";
				Data curItem (line.substr(0, 10), line);
				//cout << "9\n";
				//cout << curItem.getValue() <<endl;
				vectKeyValue.push_back(curItem);
				//cout << "10\n";
			}
			//cout << "Reading File Block # " << i+1 << " completed\n";

			//cout << "11\n";

			for ( long j=0; j<vectKeyValue.size(); j++)
			{
				Data dataTemp ("","");
				vectTemp.push_back(dataTemp);
			}
			ThreadMergeSort(i, strOutputFileName);
		}
		//cout << "12\n";
		int intTotalNoOfFiles = i;
		//cout << "13\n";
		fileReader.close();
	}

	return 1;
}

