#compiler to be used is g++
CC=g++
#define the target file
all:ems ms
ems: ExternalMergeSort.cpp SharedMemorySort.h
	$(CC) -o ems ExternalMergeSort.cpp -I . -lpthread
ms: MergeSort.cpp SharedMemorySort.h
	$(CC) -o ms MergeSort.cpp -I . -lpthread
clean :
	rm ems ms
