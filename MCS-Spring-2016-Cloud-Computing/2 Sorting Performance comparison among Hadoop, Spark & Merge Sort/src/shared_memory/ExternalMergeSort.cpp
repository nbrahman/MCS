/*
 * ExternalMergeSort7.cpp
 *
 *  Created on: Mar 15, 2016
 *      Author: niks
 */

#include <iostream>
#include <cstring>
#include <cmath>
#include <fstream>
#include <cstdlib>
#include <list>
#include <vector>
#include <sys/time.h>
#include <stdio.h>
#include <SharedMemorySort.h>
#include <iomanip>
#include <sstream>
#include <pthread.h>
#include <spawn.h>
#include <sys/wait.h>

using namespace std;

string FinalMergePass (std::vector<string>, string, int);
std::vector<string> SplitFiles (string strInputFileName, int intMaxNoOfFiles);
void *callSortingProgram(void *);

string strSortingProgram="./ms";

long lngInputFileCount=0;
long lngThreadSortingCompleted=0;
std::vector <string> vectInputFileNames;
std::vector <string> vectInputFileQueue;
std::vector <string> vectOutputFileNames;
extern char **environ;

int main(int argc, char* argv[])
{
	// Check the number of parameters
	if (argc < 4)
	{
		// Tell the user how to run the program
		std::cerr << "Usage: " << argv[0] << " <Data File Name> <Number of chunks / blocks> <Number of Threads>" << std::endl;
		return 0;
	}

	// Check the number of parameters
	if (argv[1] == "")
	{
		// Tell the user how to run the program
		std::cerr << "Data File Name to be sorted cannot be blank!" << std::endl;
		return 1;
	}

	if (argv[2] == "")
	{
		// Tell the user how to run the program
		std::cerr << "Number of chunks / blocks cannot be blank!" << std::endl;
		return 2;
	}

	if (argv[3] == "")
	{
		// Tell the user how to run the program
		std::cerr << "Number of Threads cannot be blank!" << std::endl;
		return 3;
	}

	char* cpNoOfBlocks = argv[2];
	for (int i=0; cpNoOfBlocks[i] != '\0'; i++)
	{
		if (isalpha(cpNoOfBlocks[i]))
		{
			std::cerr << "Number of chunks / blocks cannot contain character." << endl;
			return (4);
		}
	}

	char* cpNoOfThreads = argv[3];
	for (int i=0; cpNoOfThreads[i] != '\0'; i++)
	{
		if (isalpha(cpNoOfThreads[i]))
		{
			std::cerr << "Number of Threads cannot contain character." << endl;
			return (5);
		}
	}

	int intNoOfBlocks = atoi(argv[2]);
	if ((intNoOfBlocks<=0) || (intNoOfBlocks > 9999))
	{
		std::cerr << "Number of chunks / blocks must be between 1 and 9999." << endl;
		return (6);
	}

	int intNoOfThreads = atoi(argv[3]);
	if ((intNoOfThreads<=0) || (intNoOfThreads > 16))
	{
		std::cerr << "Number of Threads must be between 1 and 16." << endl;
		return (7);
	}

	string strInputFileName = argv[1];
	int intMaxNoOfFiles = atoi(argv[2]);
	intNoOfThreads = atoi(argv[3]);

	struct timeval clkStart, clkEnd;
	string strOutputFileName;
	pthread_t *threadedSorting;
	struct threadData td[intNoOfThreads];

	gettimeofday(&clkStart, NULL);
	gettimeofday(&clkStart, NULL);


	cout << "Splitting " << strInputFileName << " into " << intMaxNoOfFiles << " blocks.......\n";
	vectInputFileNames = SplitFiles(strInputFileName, intMaxNoOfFiles);

	if (vectInputFileNames.size()>0)
	{
		vectInputFileQueue = vectInputFileNames;
		cout << "Splitting completed\nCommencing sorting process........\n";
		lngInputFileCount = vectInputFileNames.size();
		if (!(threadedSorting = (pthread_t*)malloc (intNoOfThreads * sizeof(pthread_t))))
		{
			cout << "Unable to initialize thread pool with " << intNoOfThreads << " threads" << endl;
			return (9);
		}
		for (int i=0; i<intNoOfThreads; i++)
		{
			td[i].intThreadId = i;
			pthread_create(&threadedSorting[i], NULL, callSortingProgram, (void *)&td[i]);
		}
		for (int i=0; i<intNoOfThreads; i++)
		{
			pthread_join(threadedSorting[i], NULL);
		}
		free ((void*)threadedSorting);
	}
	else
	{
		std:cerr << "Unable to split the file.\n";
		return (8);
	}

	if (vectOutputFileNames.size()>0)
	{
		cout << "Final phase of merging all the sorted output files started\n";
		strOutputFileName = FinalMergePass (vectOutputFileNames, strInputFileName, (int)((memInfo()/(vectOutputFileNames.size()+1))/110)*1.5);
		cout << "Final phase of merging all the sorted output files completed\n";
	}
	cout << "Removing all the intermediate files\n";
	for (std::vector<string>::iterator it=vectOutputFileNames.begin();
			it!=vectOutputFileNames.end();it++)
	{
		string strFileName = *it;
		remove (strFileName.c_str());
	}
	for (std::vector<string>::iterator it=vectInputFileNames.begin();
			it!=vectInputFileNames.end();it++)
	{
		string strFileName = *it;
		remove (strFileName.c_str());
	}
	gettimeofday(&clkEnd, NULL);
	cout << "The final sorted output is available in file " << strOutputFileName << "\n";
	cout << strInputFileName << " data sorted in "
			<< ((double)clkEnd.tv_sec - (double)clkStart.tv_sec + (((double)clkEnd.tv_usec - (double)clkStart.tv_usec) / 1000000)) << " seconds\n";
	return 0;
}

string FinalMergePass (std::vector<string> vectFileName, string strInputFileName, int intBufferSize)
{
	//cout << "FMP 1\n";
	string strOutputFileName = strInputFileName;
	//cout << "FMP 1.1\n";
	//cout << "FMP 1.2\n";
	std::vector<string> vectLineFromFiles;
	vectLineFromFiles.resize(vectFileName.size());
	std::vector<string> vectBuffer;
	std::list<FileDet> lstFileDetails;
	//cout << "FMP 1.3\n";
	std::string key (".");
	//cout << "FMP 1.4\n";

	std::size_t found = strOutputFileName.rfind(key);
	//cout << "FMP 1.5\n";
	if (found!=std::string::npos)
	{
		//cout << "FMP 1.6\n";
		strOutputFileName.replace (found,key.length(),"_output.");
	}
	else
	{
		//cout << "FMP 1.7\n";
		strOutputFileName.append("_output");
	}

	ofstream ofOutputFileWrite;
	ofOutputFileWrite.open (strOutputFileName.c_str());
	int i = 0;
	//cout << vectOutputFileNames.size() << endl;
	for (int i=0; i<vectOutputFileNames.size(); i++)
	{
		//cout << "FMP 1.8\n";
		string strFileName = vectOutputFileNames[i];
		//cout << "FMP 1.9\n";
		//cout << "strFileName = " << strFileName << endl;// << "ccpFileName = " << ccpFileName << endl;
		ifstream ifReader (strFileName.c_str());
		FileDet curFileDetails (strFileName, false, 0);
		lstFileDetails.push_back(curFileDetails);
		//cout << "FMP 1.9.1\n";
		//cout << "FMP 1.9.2\n";
		//cout << "FMP 1.10\n";
	}
	//cout << "FMP 2\n";

	//bool blnFileFinished=false;

	int intNoOfFiles = vectFileName.size();
	for (int i=0; i<vectLineFromFiles.size();i++)
	{
		vectLineFromFiles[i]="";
	}
	while (intNoOfFiles>0)
	{
		//cout << "FMP 3\n";
		std::list<FileDet>::iterator itFileDet=lstFileDetails.begin();
		for (int i=0;i<vectFileName.size();i++)
		{
			//cout << "FMP 4\n";
			if (vectLineFromFiles[i]=="")
			{
				//cout << "FMP 3\n";
				//cout << "FMP 5\n";
				string line;
				//cout << "FMP 3.1\n";
				FileDet curFileDet = *itFileDet;
				//cout << curFileDet.getFileName() <<endl;
				//ofOutputFileWrite << "Reading from file " << i << endl;

				if (curFileDet.getFileReadCompleted()==false)
				{
					//cout << "FMP 6\n";
					ifstream ifReader (curFileDet.getFileName().c_str());
					long lngNoOfLinesRead = curFileDet.getLinesRead();
					//cout << "Number of lines read : " << lngNoOfLinesRead << endl;
					//cout << "FMP 3.2\n";
					//cout << curFileDet.getSize() << endl << lngNoOfLinesRead << endl;
					if ((lngNoOfLinesRead*100>=0) && (lngNoOfLinesRead*100<=curFileDet.getSize()))
					{
						ifReader.seekg(lngNoOfLinesRead*100, ios::beg);
						//cout << curFileDet.getFileName() << endl;
						//cout << "FMP 7\n";
						getline (ifReader, line);
						//cout << "FMP 8\n";
						//ofOutputFileWrite << "File Name: " << curFileDet.getFileName() <<  "Read Line is " << line << endl;
						//cout << line << endl;
						vectLineFromFiles[i]=line;
						//cout << "FMP 9\n";
						//cout << "lngNoOfLinesRead = " << lngNoOfLinesRead << endl;
						lngNoOfLinesRead++;
						//cout << "FMP 10\n";
						//cout << "Reading from "<< curFileDet.getFileName() << " and the line is: " << curFileDet. << endl;
					}
					else
					{
						if (curFileDet.getFileReadCompleted()==false)
						{
							curFileDet.setFileReadCompleted(true);
							//cout << "FMP 11\n";
							//cout  << "Reading for file " << curFileDet.getFileName() << "completed\n";
							intNoOfFiles--;
							//cout << "FMP 12\n";
						}
					}
					curFileDet.setLinesRead(lngNoOfLinesRead);
					//cout << "FMP 13\n";
					ifReader.close();
					//cout << "FMP 14\n";
					*itFileDet = curFileDet;
					//cout << "FMP 15\n";
				}
				else
				{
					if (curFileDet.getFileReadCompleted()==false)
					{
						//cout << "FMP 16\n";
						curFileDet.setFileReadCompleted(true);
						//cout << "FMP 17\n";
						//cout << "Reading for file " << curFileDet.getFileName() << "completed\n";
						intNoOfFiles--;
						//cout << "FMP 18\n";
					}
				}
			}
			itFileDet++;
		}
		//ofOutputFileWrite << "intNoOfFiles is " << intNoOfFiles << endl;

		int intMinPos=-1;
		string strMinPosValue="";

		/*ofOutputFileWrite << "The current array is \n";
		for (int k=0;k<vectLineFromFiles.size();k++)
		{
			ofOutputFileWrite << vectLineFromFiles[k] << endl;
		}*/

		for (int k=0;k<vectLineFromFiles.size();k++)
		{
			//cout << "FMP 19\n";
			//cout << vectLineFromFiles.size() << endl << k << endl;
			//ofOutputFileWrite << "k is " << k << endl;
			//ofOutputFileWrite << "intMinPos is " << intMinPos << endl;
			//ofOutputFileWrite << "intMinPos Val is " << strMinPosValue << endl;
			if (vectLineFromFiles[k]!="")
			{
				//cout << "FMP 20\n";
				if (intMinPos<0)
				{
					intMinPos = k;
					strMinPosValue=vectLineFromFiles[k];
				}
				if (vectLineFromFiles[k].compare(strMinPosValue)<0)
				{
					//cout << "FMP 21\n";
					intMinPos = k;
					strMinPosValue=vectLineFromFiles[k];
					//ofOutputFileWrite << "changed minimum position is " << intMinPos << endl;
				}
			}
		}
		//cout << "FMP 22\n";
		//ofOutputFileWrite << "Final Minimum Position is " << intMinPos << endl;
		if (intMinPos >= 0)
		{
			//vectBuffer.push_back(vectLineFromFiles[intMinPos]);
			//cout << "FMP 23\n";
			if (!vectLineFromFiles[intMinPos].empty())
			{
				//lngLocalLineCnt++;
				//if (lngLocalLineCnt<lngLineCnt)
				//{
					ofOutputFileWrite << vectLineFromFiles[intMinPos] << endl;
				//}
				//else
				//{
					//ofOutputFileWrite << vectLineFromFiles[intMinPos] << endl;
				//}
			}
			vectLineFromFiles[intMinPos]= "";
		}

		/*ofOutputFileWrite << "Final array is \n";

		if (vectBuffer.size()>=intBufferSize)
		{
			cout << "FMP 24\n";
			std::vector<string>::iterator it = vectBuffer.begin();
			for (int i=0;(i<vectBuffer.size());i++)
			{
				cout << "FMP 25\n";
				//cout << "ALTV 10\n";
				ofOutputFileWrite << vectBuffer[i] << endl;
				cout << "FMP 26\n";
				vectBuffer.erase (it+i);
				cout << "FMP 27\n";
			}
		}*/
		//cout << "ALTV 11\n";

		//ofOutputFileWrite << "\n\n\n\nLines List size is " << vectLineFromFiles.size() << endl;
		//ofOutputFileWrite << "File Name List Size is " << lstFileName.size() << "\nLines List Size is " << vectLineFromFiles.size() << endl;
	}

	//std::vector<string>::iterator it = vectLineFromFiles.begin();
	for (int i=0;(i<vectLineFromFiles.size());i++)
	{
		//cout << "FMP 24\n";
		//cout << "ALTV 10\n";
		//cout << "FMP 25\n";
		if (vectLineFromFiles[i]=="")
		{
			vectLineFromFiles.erase (vectLineFromFiles.begin()+i);
		}
		//cout << "FMP 26\n";
	}


	while (vectLineFromFiles.size()>0)
	{
		//ofOutputFileWrite << "Vector Size is: " << vectLineFromFiles.size();
		int intMinPos=-1;
		string strMinPosValue="";

		//ofOutputFileWrite << "The current array is \n";
		for (int k=0;k<vectLineFromFiles.size();k++)
		{
			//cout << "FMP 27\n";
			if (vectLineFromFiles[k]!="")
			{
				//cout << "FMP 28\n";
				if (intMinPos<0)
				{
					intMinPos = k;
					strMinPosValue=vectLineFromFiles[k];
				}
				if (vectLineFromFiles[k].compare(strMinPosValue)<0)
				{
					//cout << "FMP 29\n";
					intMinPos = k;
					strMinPosValue=vectLineFromFiles[k];
					//ofOutputFileWrite << "changed minimum position is " << intMinPos << endl;
				}
			}
			else
			{
				vectLineFromFiles.erase (vectLineFromFiles.begin()+k);
			}
		}
		//cout << "FMP 30\n";
		//ofOutputFileWrite << "Final Minimum Position is " << intMinPos << endl;
		if (intMinPos >= 0)
		{
			//vectBuffer.push_back(vectLineFromFiles[intMinPos]);
			//std::vector<string>::iterator it = vectLineFromFiles.begin();
			//cout << "FMP 31\n";
			if (!vectLineFromFiles[intMinPos].empty())
			{
				//lngLocalLineCnt++;
				//if (lngLocalLineCnt<lngLineCnt)
				//{
					ofOutputFileWrite << vectLineFromFiles[intMinPos] << endl;
				//}
				//else
				//{
					//ofOutputFileWrite << vectLineFromFiles[intMinPos] << endl;
				//}
			}
			vectLineFromFiles.erase(vectLineFromFiles.begin()+intMinPos);
		}
	}

	return strOutputFileName;
}

std::vector<string> SplitFiles (string strInputFileName, int intMaxNoOfFiles)
{
	std::vector<string> vectInputFileNames;
	if (strInputFileName!="")
	{
		ifstream fileReader (strInputFileName.c_str());
		long lngLineCnt, lngPendingLines, lngBufferSize;
		if (((filesize (strInputFileName.c_str()) / 100) % intMaxNoOfFiles) != 0)
		{
			lngLineCnt = (round(filesize (strInputFileName.c_str()) / 100 / intMaxNoOfFiles) + 1);
		}
		else
		{
			lngLineCnt = (round(filesize (strInputFileName.c_str()) / 100 / intMaxNoOfFiles));
		}
		lngPendingLines = lngLineCnt;
		string line;
		if (fileReader.is_open())
		{
			int i, j;
			for (i=0;((i<intMaxNoOfFiles) && (fileReader.eof()==false));i++)
			{
				string strInterimFileName = "temp_in_";
				stringstream ss;
				ss << setw(5) << setfill('0') << i;
				strInterimFileName += ss.str();
				strInterimFileName += ".txt";

				ofstream fileWriter;
				fileWriter.open (strInterimFileName.c_str(), ios::out);

				/*if (lngPendingLines<lngLineCnt)
				{
					lngBufferSize = lngPendingLines;
				}
				else
				{
					lngBufferSize = lngLineCnt;
				}*/
				//cout << "Buffer size is " << (lngBufferSize*100) << endl;

				char buff[(lngBufferSize*100)];

				for (j=0;((j<lngLineCnt) && (fileReader.eof()==false));j++)
				{
					//cout << fileReader.tellg() << endl;
					//cout << fileReader.seekg(i*lngLineCnt*100, ios::beg);
					//cout << fileReader.tellg() << endl;
					//fileReader.read(buff, sizeof(buff));
					getline (fileReader,line);
					//cout << buff << strInterimFileName << endl;
					//fileWriter << buff;
					fileWriter << line << endl;
					fileWriter.flush();
					lngPendingLines = lngPendingLines - (lngBufferSize / 100);
				}
				fileWriter.close();
				vectInputFileNames.push_back(strInterimFileName);
			}
		}
	}
	return vectInputFileNames;
}

void *callSortingProgram(void *args)
{
	while (true)
	{
		pthread_mutex_lock(&lock);
		//cout << "lngThreadSortingCompleted = " << lngThreadSortingCompleted << "\tlngInputFileCount=" << lngInputFileCount << endl;
		if (vectInputFileQueue.size()>0)
		{
			struct threadData *arg = (threadData*)args;
			string strThreadInputFileName=vectInputFileQueue[0];
			arg->strInputFileName = strThreadInputFileName;
			vectInputFileQueue.erase(vectInputFileQueue.begin());
			cout << "Thread " << "Sorting started for file " << strThreadInputFileName <<
					" using Thread " << arg->intThreadId << endl;

			//cout << "9\n";
			string strThreadOutputFileName=arg->strInputFileName;
			//cout << "10\n";
			std::string key ("_in_");
			//cout << "11\n";

			std::size_t found = strThreadOutputFileName.rfind(key);
			//cout << "12\n";
			//cout << "FMP 1.5\n";
			if (found!=std::string::npos)
			{
				//cout << "13\n";
				//cout << "FMP 1.6\n";
				strThreadOutputFileName.replace (found,key.length(),"_out_");
			}
			//cout << "14\n";
			arg->strOutputFileName = strThreadOutputFileName;

			pid_t pid;
			//cout << "1\n";
			char* cpSortingProgram = new char[strSortingProgram.length() + 1];
			strcpy(cpSortingProgram, strSortingProgram.c_str());

			char* cpInputFileName = new char[strThreadInputFileName.length() + 1];
			strcpy(cpInputFileName, strThreadInputFileName.c_str());

			char* cpOutputFileName = new char[strThreadOutputFileName.length() + 1];
			strcpy(cpOutputFileName, strThreadOutputFileName.c_str());

			char* argv[]={cpSortingProgram, cpInputFileName, cpOutputFileName, NULL};
			//cout << "2\n";
			//strcpy (argv[0], strSortingProgram.c_str());
			//cout << "3\n";
			//strcpy (argv[1], vectInputFileNames[lngThreadSortingCompleted].c_str());
			//cout << "4\n";
			//argv[2] = NULL;
			//cout << "5\n";
			int status;
			//cout << "6\n";
			status = posix_spawn(&pid, strSortingProgram.c_str(), NULL, NULL, argv, environ);
			pthread_mutex_unlock(&lock);
			//cout << "7\n";
			if (status == 0)
			{
				//cout << "8\n";
				if (waitpid(pid, &status, 0) != -1)
				{
					//if (lngThreadSortingCompleted<lngInputFileCount)
					//{
						pthread_mutex_lock(&lock);
						vectOutputFileNames.push_back(arg->strOutputFileName);
						//cout << vectOutputFileNames.size() << endl;
						pthread_mutex_unlock(&lock);
					//}
					//cout << "20\n";
				}
				else
				{
					//cout << "15\n";
					perror("waitpid");
				}
				//cout << "21\n" << endl;
			}
			else
			{
				//cout << "16\n";
				printf("posix_spawn: %s\n", strerror(status));
			}
			//cout << "22\n" << endl;
			pthread_mutex_lock(&lock);
			//cout << "23\n" << endl;
			lngThreadSortingCompleted++;
			//cout << "24\n" << endl;
			pthread_mutex_unlock(&lock);
			//cout << "25\n" << endl;
			delete [] cpSortingProgram;
			//cout << "26\n" << endl;
			delete [] cpOutputFileName;
			//cout << "27\n" << endl;
		}
		else
		{
			pthread_mutex_unlock(&lock);
			break;
		}
	}
	return NULL;
}
