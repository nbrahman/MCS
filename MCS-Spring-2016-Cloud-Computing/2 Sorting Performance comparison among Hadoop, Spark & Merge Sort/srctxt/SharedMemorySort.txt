/*
 * SharedMemorySort.h
 *
 *  Created on: Mar 15, 2016
 *      Author: niks
 */

#include <sys/sysinfo.h>
#include <pthread.h>

#ifndef SHAREDMEMORYSORT_H_
#define SHAREDMEMORYSORT_H_

using namespace std;

std::ifstream::pos_type filesize(char const *);
long memInfo();

pthread_mutex_t lock = PTHREAD_MUTEX_INITIALIZER;

std::ifstream::pos_type filesize(char const * filename)
{
	std::ifstream in(filename, std::ifstream::ate | std::ifstream::binary);
	return in.tellg();
}

long memInfo(void)
{
	struct sysinfo myinfo;
	unsigned long total_bytes;

	sysinfo(&myinfo);

	total_bytes = myinfo.mem_unit * myinfo.totalram;

	//cout << "total usable main memory is " << total_bytes << "B, " << total_bytes/1024/1024 << " MB\n";
	return total_bytes;
}

class Data
{
	private:
		string strKey;
		string strValue;

	public:
		Data (string, string);
		string getKey() {return this->strKey;}
		string getValue() {return this->strValue;}
		void setKey(string strKey) {this->strKey = strKey;}
		void setValue(string strValue) {this->strValue = strValue;}
};

Data::Data (string Key, string Value)
{
	this->strKey = Key;
	this->strValue = Value;
}

class FileDet
{
	private:
		string strFileName;
		bool blnFileReadCompleted;
		long lngFileLength;
		long lngNoOfLinesRead;

	public:
		FileDet (string, bool, long);
		string getFileName() {return this->strFileName;}
		bool getFileReadCompleted() {return this->blnFileReadCompleted;}
		long getSize() {return this->lngFileLength;}
		long getLinesRead() {return this->lngNoOfLinesRead;}
		void setFileName(string strFileName) {this->strFileName=strFileName;}
		void setFileReadCompleted(bool blnFileReadCompleted) {this->blnFileReadCompleted=blnFileReadCompleted;}
		//void setSize (long lngFileSize) {this->ifReader=Reader;}
		void setLinesRead(long NoOfLinesRead) {this->lngNoOfLinesRead=NoOfLinesRead;}
};

FileDet::FileDet (string strFileName, bool blnFileReadCompleted, long NoOfLinesRead)
{
	this->strFileName = strFileName;
	this->blnFileReadCompleted = blnFileReadCompleted;
	this->lngFileLength = filesize(strFileName.c_str());
	this->lngNoOfLinesRead = NoOfLinesRead;
}

struct threadData
{
	int intThreadId;
	string strInputFileName;
	string strOutputFileName;
};

#endif /* SHAREDMEMORYSORT_H_ */
