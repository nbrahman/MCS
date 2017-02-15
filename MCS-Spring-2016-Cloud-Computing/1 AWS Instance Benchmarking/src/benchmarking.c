/*
 * benchmarking.c
 *
 *  Created on: Jan 31, 2016
 *      Author: niks
 */

#include<stdio.h>
#include<time.h>
#include<stdlib.h>
#include<pthread.h>
#include<string.h>
#include<fcntl.h>

// defining constants
#define ITERATIONS 1000000000
#define NOOFFOPS 85
#define NOOFFOPS2 105

// structure definition starts from here
struct thread_arg_struct
{
	int intThreadNo;
	int intRunDuration;
	int intSamplingInterval;
	long lngBlockSize;
	int intBlockSizeIndex;
};

struct Flops_details
{
	int intThreadNo;
	double dblIterations;
	double dblTotalNoOfOps;
	struct timeval clkStart;
	struct timeval clkEnd;
	double dblGFlops;
};

struct CPUB2Data
{
	int intSamplingSequence;
	long int lngSamplingIntervalCounter;
	long int lngTotalCounter;
	double dblSamplingTime;
};

struct MemData
{
	int intThreadNo;
	struct timeval clkThStart;
	struct timeval clkThEnd;
	struct timeval clkLatStart;
	struct timeval clkLatEnd;
	long lngBlockSize;
	long lngNoOfBlocks;
	unsigned long lngTotalMemSize;
	double dblThroughput;
	double dblLatency;
};

struct DiskData
{
	int intThreadNo;
	char chrOperation;
	struct timeval clkThStart;
	struct timeval clkThEnd;
	struct timeval clkLatStart;
	struct timeval clkLatEnd;
	long lngBlockSize;
	long lngNoOfBlocks;
	unsigned long lngTotalMemSize;
	double dblThroughput;
	double dblLatency;
};
// structure definition ends over here

// global structure variable declaration starts from here
struct Flops_details strctFD[1][4];
struct Flops_details strctID[1][4];
struct thread_arg_struct strctTAS[4];
struct CPUB2Data strctCPUP2FlopsData[600][4];
struct CPUB2Data strctCPUP2IopsData[600][4];
struct MemData strctMemSeqAccess[3][2];
struct MemData strctMemRandomAccess[3][2];
struct DiskData strctDiskSeqAccess[6][2];
struct DiskData strctDiskRandomAccess[6][2];
int intSecCounter = 0;
// global structure variable declaration starts from here

// function to compute Flops (Experiment 1a) starts from here
void *computeFlops(void *args)
{
	// variable declaration
	volatile double i;
	volatile double dblAddVar1 = 1.234567;
	volatile double dblAddVar2 = 2.098765;
	volatile double dblAddVar3 = 4.098765;
	volatile double dblAddVar4 = 1.098765;
	volatile double dblAddVar5 = 2.234567;
	volatile double dblAddVar6 = 3.234567;
	volatile double dblAddVar7 = 1.238906;
	volatile double dblAddVar8 = 3.056452;
	volatile double dblAddVar9 = 1.234567;
	volatile double dblAddVar10 = 2.098765;
	volatile double dblAddVar11 = 4.098765;
	volatile double dblAddVar12 = 1.098765;
	volatile double dblAddVar13 = 2.234567;
	volatile double dblAddVar14 = 3.234567;
	volatile double dblAddVar15 = 1.238906;
	volatile double dblAddVar16 = 3.056452;
	volatile double dblAddVar17 = 2.234567;
	volatile double dblAddVar18 = 3.234567;
	volatile double dblAddVar19 = 1.238906;
	volatile double dblAddVar20 = 3.056452;
	volatile double dblMulVar1 = 1.234567;
	volatile double dblMulVar2 = 2.098765;
	volatile double dblMulVar3 = 4.098765;
	volatile double dblMulVar4 = 1.098765;
	volatile double dblMulVar5 = 2.234567;
	volatile double dblMulVar6 = 3.234567;
	volatile double dblMulVar7 = 1.238906;
	volatile double dblMulVar8 = 3.056452;
	volatile double dblMulVar9 = 1.234567;
	volatile double dblMulVar10 = 2.098765;
	volatile double dblMulVar11 = 4.098765;
	volatile double dblMulVar12 = 1.098765;
	volatile double dblMulVar13 = 2.234567;
	volatile double dblMulVar14 = 3.234567;
	volatile double dblMulVar15 = 1.238906;
	volatile double dblMulVar16 = 3.056452;
	volatile double dblMulVar17 = 2.234567;
	volatile double dblMulVar18 = 3.234567;
	volatile double dblMulVar19 = 1.238906;
	volatile double dblMulVar20 = 3.056452;
	volatile double dblNoOfOpsCounter=0;
	struct timeval clkStart, clkPrev, clkEnd, clkTemp;
	struct thread_arg_struct *arg = args;

	// start the timer
	gettimeofday(&clkStart, NULL);

	for(i = 0; (i < ITERATIONS);i++)
	{
		/*
		 * 41 * 2 Addition or Multiplication ops (Arithmetic + Assignment)
		 * 1 * 2 i++ ops (Arithmetic + Assignment)
		 * 1 * 1 Condition Check
		 */
		// Additions
		dblAddVar1 = dblAddVar1 + 0.05;
		dblAddVar2 = dblAddVar2 + 0.03;
		dblAddVar3 = dblAddVar3 + 0.08;
		dblAddVar4 = dblAddVar4 + 0.08;
		dblAddVar5 = dblAddVar5 + 0.02;
		dblAddVar6 = dblAddVar6 + 0.02;
		dblAddVar7 = dblAddVar7 + 0.02;
		dblAddVar8 = dblAddVar8 + 0.02;
		dblAddVar9 = dblAddVar9 + 0.05;
		dblAddVar10 = dblAddVar10 + 0.03;
		dblAddVar11 = dblAddVar11 + 0.08;
		dblAddVar12 = dblAddVar12 + 0.08;
		dblAddVar13 = dblAddVar13 + 0.02;
		dblAddVar14 = dblAddVar14 + 0.05;
		dblAddVar15 = dblAddVar15 + 0.09;
		dblAddVar16 = dblAddVar16 + 0.001;
		dblAddVar17 = dblAddVar17 + 0.5;
		dblAddVar18 = dblAddVar18 + 0.2;
		dblAddVar19 = dblAddVar19 + 0.9;
		dblAddVar20 = dblAddVar20 + 0.1;

		// Multiplications
		dblMulVar1 = dblMulVar1 * 0.05;
		dblMulVar2 = dblMulVar2 * 0.03;
		dblMulVar3 = dblMulVar3 * 0.03;
		dblMulVar4 = dblMulVar4 * 0.08;
		dblMulVar5 = dblMulVar5 * 0.02;
		dblMulVar6 = dblMulVar6 * 0.02;
		dblMulVar7 = dblMulVar7 * 0.02;
		dblMulVar8 = dblMulVar8 * 0.02;
		dblMulVar9 = dblMulVar9 * 0.05;
		dblMulVar10 = dblMulVar10 * 0.03;
		dblMulVar11 = dblMulVar11 * 0.03;
		dblMulVar12 = dblMulVar12 * 0.08;
		dblMulVar13 = dblMulVar13 * 0.02;
		dblMulVar14 = dblMulVar14 * 0.02;
		dblMulVar15 = dblMulVar15 * 0.02;
		dblMulVar16 = dblMulVar16 * 0.02;
		dblMulVar17 = dblMulVar17 * 0.02;
		dblMulVar18 = dblMulVar18 * 0.02;
		dblMulVar19 = dblMulVar19 * 0.02;
		dblMulVar20 = dblMulVar20 * 0.02;

		// Increment the Total Operations' counter
		dblNoOfOpsCounter = dblNoOfOpsCounter + NOOFFOPS;
	}

	// set the end time
	gettimeofday(&clkEnd, NULL);

	// initialize the structure variables with appropriate values
	strctFD[0][arg->intThreadNo-1].intThreadNo = arg->intThreadNo;
	// + 1 because of the initial assignment of i in the beginning of loop
	// + 1 for last looping variable condition check
	strctFD[0][arg->intThreadNo-1].dblTotalNoOfOps = dblNoOfOpsCounter + 2;
	strctFD[0][arg->intThreadNo-1].dblIterations = i;
	strctFD[0][arg->intThreadNo-1].clkStart = clkStart;
	strctFD[0][arg->intThreadNo-1].clkEnd = clkEnd;

	//Flops computations formula & assignment
	strctFD[0][arg->intThreadNo-1].dblGFlops = (((double)dblNoOfOpsCounter)/
			((((double)clkEnd.tv_sec - (double)clkStart.tv_sec) + (((double)clkEnd.tv_usec - (double)clkStart.tv_usec) / 1000000)))) / 1000000000;
	return NULL;
}
// function to compute Flops (Experiment 1a) ends over here

// function to compute Iops (Experiment 1a) starts from here
void *computeIops(void *args)
{
	// variable declaration
	volatile int i = 0;
	volatile long lngAddVar1 = 1;
	volatile long lngAddVar2 = 2;
	volatile long lngAddVar3 = 9;
	volatile long lngAddVar4 = 4;
	volatile long lngAddVar5 = -7;
	volatile long lngAddVar6 = -3;
	volatile long lngAddVar7 = -3;
	volatile long lngAddVar8 = 5;
	volatile long lngAddVar9 = 1;
	volatile long lngAddVar10 = 2;
	volatile long lngAddVar11 = 9;
	volatile long lngAddVar12 = 4;
	volatile long lngAddVar13 = -7;
	volatile long lngAddVar14 = -3;
	volatile long lngAddVar15 = -3;
	volatile long lngAddVar16 = 5;
	volatile long lngAddVar17 = -7;
	volatile long lngAddVar18 = -3;
	volatile long lngAddVar19 = -3;
	volatile long lngAddVar20 = 5;
	volatile long lngMulVar1 = 1;
	volatile long lngMulVar2 = 2;
	volatile long lngMulVar3 = 9;
	volatile long lngMulVar4 = 4;
	volatile long lngMulVar5 = -7;
	volatile long lngMulVar6 = -3;
	volatile long lngMulVar7 = -3;
	volatile long lngMulVar8 = -3;
	volatile long lngMulVar9 = 1;
	volatile long lngMulVar10 = 2;
	volatile long lngMulVar11 = 9;
	volatile long lngMulVar12 = 4;
	volatile long lngMulVar13 = -7;
	volatile long lngMulVar14 = -3;
	volatile long lngMulVar15 = -3;
	volatile long lngMulVar16 = -3;
	volatile long lngMulVar17 = -7;
	volatile long lngMulVar18 = -3;
	volatile long lngMulVar19 = -3;
	volatile long lngMulVar20 = -3;
	volatile long int lngNoOfOpsCounter=0;
	struct timeval clkStart, clkPrev, clkEnd, clkTemp;
	struct thread_arg_struct *arg = args;

	// start the timer
	gettimeofday(&clkStart, NULL);

	for(i = 0; (i < ITERATIONS);i++)
	{
		/*
		 * 41 * 2 Addition or Multiplication ops (Arithmetic + Assignment)
		 * 1 * 2 i++ ops (Arithmetic + Assignment)
		 * 1 * 1 Condition Check
		 */
		// Additions
		lngAddVar1 = lngAddVar1 + 5;
		lngAddVar2 = lngAddVar2 + 3;
		lngAddVar3 = lngAddVar3 + 8;
		lngAddVar4 = lngAddVar4 + 7;
		lngAddVar5 = lngAddVar5 + 4;
		lngAddVar6 = lngAddVar6 + 6;
		lngAddVar7 = lngAddVar7 + 1;
		lngAddVar8 = lngAddVar8 + 2;
		lngAddVar9 = lngAddVar9 + 5;
		lngAddVar10 = lngAddVar10 + 3;
		lngAddVar11 = lngAddVar11 + 8;
		lngAddVar12 = lngAddVar12 + 7;
		lngAddVar13 = lngAddVar13 + 4;
		lngAddVar14 = lngAddVar14 + 6;
		lngAddVar15 = lngAddVar15 + 1;
		lngAddVar16 = lngAddVar16 + 2;
		lngAddVar17 = lngAddVar17 + 4;
		lngAddVar18 = lngAddVar18 + 6;
		lngAddVar19 = lngAddVar19 + 1;
		lngAddVar20 = lngAddVar20 + 2;

		// Multiplications
		lngMulVar1 = lngMulVar1 * 5;
		lngMulVar2 = lngMulVar2 * 3;
		lngMulVar3 = lngMulVar3 * 8;
		lngMulVar4 = lngMulVar4 * 7;
		lngMulVar5 = lngMulVar5 * 4;
		lngMulVar6 = lngMulVar6 * 6;
		lngMulVar7 = lngMulVar7 * 1;
		lngMulVar8 = lngMulVar8 * 2;
		lngMulVar9 = lngMulVar9 * 5;
		lngMulVar10 = lngMulVar10 * 3;
		lngMulVar11 = lngMulVar11 * 8;
		lngMulVar12 = lngMulVar12 * 7;
		lngMulVar13 = lngMulVar13 * 4;
		lngMulVar14 = lngMulVar14 * 6;
		lngMulVar15 = lngMulVar15 * 1;
		lngMulVar16 = lngMulVar16 * 2;
		lngMulVar17 = lngMulVar17 * 4;
		lngMulVar18 = lngMulVar18 * 6;
		lngMulVar19 = lngMulVar19 * 1;
		lngMulVar20 = lngMulVar20 * 2;

		// Increment the Total Operations' counter
		lngNoOfOpsCounter = lngNoOfOpsCounter + NOOFFOPS;
	}

	// set the end time
	gettimeofday(&clkEnd, NULL);

	// initialize the structure variables with appropriate values
	strctID[0][arg->intThreadNo-1].intThreadNo = arg->intThreadNo;
	// + 1 because of the initial assignment of i in the beginning of loop
	// + 1 for last looping variable condition check
	strctID[0][arg->intThreadNo-1].dblTotalNoOfOps = lngNoOfOpsCounter + 2;
	strctID[0][arg->intThreadNo-1].dblIterations = i;
	strctID[0][arg->intThreadNo-1].clkStart = clkStart;
	strctID[0][arg->intThreadNo-1].clkEnd = clkEnd;

	//Iops computations formula & assignment
	strctID[0][arg->intThreadNo-1].dblGFlops = (((double)lngNoOfOpsCounter)/
			((((double)clkEnd.tv_sec - (double)clkStart.tv_sec) + (((double)clkEnd.tv_usec - (double)clkStart.tv_usec) / 1000000)))) / 1000000000;
	return NULL;
}
// function to compute Iops (Experiment 1a) ends over here

// function to compute sample Flops (Experiment 1b - 600 samples at interval of 1 second each) starts from here
void *computeFlops2(void *args)
{
	// variable declaration
	volatile double i;
	volatile double dblAddVar1 = 1.234567;
	volatile double dblAddVar2 = 2.098765;
	volatile double dblAddVar3 = 4.098765;
	volatile double dblAddVar4 = 1.098765;
	volatile double dblAddVar5 = 2.234567;
	volatile double dblAddVar6 = 3.234567;
	volatile double dblAddVar7 = 1.238906;
	volatile double dblAddVar8 = 3.056452;
	volatile double dblAddVar9 = 1.234567;
	volatile double dblAddVar10 = 2.098765;
	volatile double dblAddVar11 = 4.098765;
	volatile double dblAddVar12 = 1.098765;
	volatile double dblAddVar13 = 2.234567;
	volatile double dblAddVar14 = 3.234567;
	volatile double dblAddVar15 = 1.238906;
	volatile double dblAddVar16 = 3.056452;
	volatile double dblAddVar17 = 2.234567;
	volatile double dblAddVar18 = 3.234567;
	volatile double dblAddVar19 = 1.238906;
	volatile double dblAddVar20 = 3.056452;
	volatile double dblMulVar1 = 1.234567;
	volatile double dblMulVar2 = 2.098765;
	volatile double dblMulVar3 = 4.098765;
	volatile double dblMulVar4 = 1.098765;
	volatile double dblMulVar5 = 2.234567;
	volatile double dblMulVar6 = 3.234567;
	volatile double dblMulVar7 = 1.238906;
	volatile double dblMulVar8 = 3.056452;
	volatile double dblMulVar9 = 1.234567;
	volatile double dblMulVar10 = 2.098765;
	volatile double dblMulVar11 = 4.098765;
	volatile double dblMulVar12 = 1.098765;
	volatile double dblMulVar13 = 2.234567;
	volatile double dblMulVar14 = 3.234567;
	volatile double dblMulVar15 = 1.238906;
	volatile double dblMulVar16 = 3.056452;
	volatile double dblMulVar17 = 2.234567;
	volatile double dblMulVar18 = 3.234567;
	volatile double dblMulVar19 = 1.238906;
	volatile double dblMulVar20 = 3.056452;
	volatile double dblTotalCounter=0;
	volatile double dblSamplingIntervalCounter=0;
	//struct timeval clkStart, clkPrev, clkEnd, clkTemp;
	struct thread_arg_struct *arg = args;

	// start the timer
	//gettimeofday(&clkStart, NULL);

	// initialize the current & previous timers with start timer
	//clkTemp = clkStart;
	//clkPrev = clkStart;

	// loop to run for run duration (600 seconds / 10 minutes)
	// compares the difference (in seconds) between current timer and start timer with run duration (in seconds)
	//printf ("Thread\ti\tdblTotalCounter\tdblSamplingIntervalCounter\n");
	/*while (((((((double)clkTemp.tv_sec - (double)clkStart.tv_sec) +
			 (((double)clkTemp.tv_usec - (double)clkStart.tv_usec) / 1000000))))<=arg->intRunDuration) || (i < arg->intRunDuration))*/
	while (intSecCounter<arg->intRunDuration)
	{
		/*
		 * 42 * 2 Addition or Multiplication ops (Arithmetic + Assignment)
		 * 1 * 4 Current - Start Seconds + Current - Start MicroSeconds + Converting MicroSeconds into Seconds + Condition Check for Run Duration
		 * 1 * 1 Condition Check
		 * 1 * 4 Current - Previous Seconds + Current - Previous MicroSeconds + Converting MicroSeconds into Seconds + Condition Check for Sampling Interval
		 */
		// Additions
		dblAddVar1 = dblAddVar1 + 0.05;
		dblAddVar2 = dblAddVar2 + 0.03;
		dblAddVar3 = dblAddVar3 + 0.08;
		dblAddVar4 = dblAddVar4 + 0.08;
		dblAddVar5 = dblAddVar5 + 0.02;
		dblAddVar6 = dblAddVar6 + 0.02;
		dblAddVar7 = dblAddVar7 + 0.02;
		dblAddVar8 = dblAddVar8 + 0.02;
		dblAddVar9 = dblAddVar9 + 0.05;
		dblAddVar10 = dblAddVar10 + 0.03;
		dblAddVar11 = dblAddVar11 + 0.08;
		dblAddVar12 = dblAddVar12 + 0.08;
		dblAddVar13 = dblAddVar13 + 0.02;
		dblAddVar14 = dblAddVar14 + 0.05;
		dblAddVar15 = dblAddVar15 + 0.09;
		dblAddVar16 = dblAddVar16 + 0.001;
		dblAddVar17 = dblAddVar17 + 0.5;
		dblAddVar18 = dblAddVar18 + 0.2;
		dblAddVar19 = dblAddVar19 + 0.9;
		dblAddVar20 = dblAddVar20 + 0.1;

		// Multiplications
		dblMulVar1 = dblMulVar1 * 0.05;
		dblMulVar2 = dblMulVar2 * 0.03;
		dblMulVar3 = dblMulVar3 * 0.03;
		dblMulVar4 = dblMulVar4 * 0.08;
		dblMulVar5 = dblMulVar5 * 0.02;
		dblMulVar6 = dblMulVar6 * 0.02;
		dblMulVar7 = dblMulVar7 * 0.02;
		dblMulVar8 = dblMulVar8 * 0.02;
		dblMulVar9 = dblMulVar9 * 0.05;
		dblMulVar10 = dblMulVar10 * 0.03;
		dblMulVar11 = dblMulVar11 * 0.03;
		dblMulVar12 = dblMulVar12 * 0.08;
		dblMulVar13 = dblMulVar13 * 0.02;
		dblMulVar14 = dblMulVar14 * 0.02;
		dblMulVar15 = dblMulVar15 * 0.02;
		dblMulVar16 = dblMulVar16 * 0.02;
		dblMulVar17 = dblMulVar17 * 0.02;
		dblMulVar18 = dblMulVar18 * 0.02;
		dblMulVar19 = dblMulVar19 * 0.02;
		dblMulVar20 = dblMulVar20 * 0.02;

		// Increment the Total Operations & Sampling Interval Operations counters
		dblTotalCounter = dblTotalCounter + NOOFFOPS2;
		dblSamplingIntervalCounter = dblSamplingIntervalCounter + NOOFFOPS2;
		//printf ("%d\t%d\t%f\t%f\n", arg->intThreadNo, intSecCounter, dblTotalCounter, dblSamplingIntervalCounter);

		// condition to check if sampling interval 1 second is completed
		// compares the difference (in seconds) between current timer and previous timer with run duration (in seconds)
		/*if ((((((double)clkTemp.tv_sec - (double)clkPrev.tv_sec) +
				 (((double)clkTemp.tv_usec - (double)clkPrev.tv_usec) / 1000000))))>=arg->intSamplingInterval)*/
		//{
			// + 4 because of storing the assignments of structure variables
			// + 3 for computing sample time (assignment operation is considered above)
			// + 1 for resetting Sampling Interval Counter to 0
			// + 2 incrementing & assigning i
			// + 2 for incrementing Sampling & Total Operations Counter by 12

			// initialize the structure variables with appropriate values
			strctCPUP2FlopsData[(int)intSecCounter][arg->intThreadNo-1].intSamplingSequence = intSecCounter;
			strctCPUP2FlopsData[(int)intSecCounter][arg->intThreadNo-1].lngSamplingIntervalCounter += (long int)dblSamplingIntervalCounter;
			strctCPUP2FlopsData[(int)intSecCounter][arg->intThreadNo-1].lngTotalCounter = (long int)dblTotalCounter + 12;
			strctCPUP2FlopsData[(int)intSecCounter][arg->intThreadNo-1].dblSamplingTime = 1;//((((double)clkTemp.tv_sec - (double)clkPrev.tv_sec) +
					// (((double)clkTemp.tv_usec - (double)clkPrev.tv_usec) / 1000000)));
			//printf ("%d\t%d\t%f\t%f\tInside Thread\n", arg->intThreadNo, intSecCounter, dblTotalCounter, dblSamplingIntervalCounter);
			dblSamplingIntervalCounter = 0;

			// set the previous time
			//gettimeofday(&clkPrev, NULL);
			//i++;
		//}

		// set the current time
		//gettimeofday(&clkTemp, NULL);
	}

	// set the end time
	//gettimeofday(&clkEnd, NULL);
	return NULL;
}
// function to compute sample Flops (Experiment 1b - 600 samples at interval of 1 second each) ends over here

// function to compute sample Iops (Experiment 1b - 600 samples at interval of 1 second each) starts from here
void *computeIops2(void *args)
{
	// variable declaration
	volatile int i = 0;
	volatile long lngAddVar1 = 1;
	volatile long lngAddVar2 = 2;
	volatile long lngAddVar3 = 9;
	volatile long lngAddVar4 = 4;
	volatile long lngAddVar5 = -7;
	volatile long lngAddVar6 = -3;
	volatile long lngAddVar7 = -3;
	volatile long lngAddVar8 = 5;
	volatile long lngAddVar9 = 1;
	volatile long lngAddVar10 = 2;
	volatile long lngAddVar11 = 9;
	volatile long lngAddVar12 = 4;
	volatile long lngAddVar13 = -7;
	volatile long lngAddVar14 = -3;
	volatile long lngAddVar15 = -3;
	volatile long lngAddVar16 = 5;
	volatile long lngAddVar17 = -7;
	volatile long lngAddVar18 = -3;
	volatile long lngAddVar19 = -3;
	volatile long lngAddVar20 = 5;
	volatile long lngMulVar1 = 1;
	volatile long lngMulVar2 = 2;
	volatile long lngMulVar3 = 9;
	volatile long lngMulVar4 = 4;
	volatile long lngMulVar5 = -7;
	volatile long lngMulVar6 = -3;
	volatile long lngMulVar7 = -3;
	volatile long lngMulVar8 = -3;
	volatile long lngMulVar9 = 1;
	volatile long lngMulVar10 = 2;
	volatile long lngMulVar11 = 9;
	volatile long lngMulVar12 = 4;
	volatile long lngMulVar13 = -7;
	volatile long lngMulVar14 = -3;
	volatile long lngMulVar15 = -3;
	volatile long lngMulVar16 = -3;
	volatile long lngMulVar17 = -7;
	volatile long lngMulVar18 = -3;
	volatile long lngMulVar19 = -3;
	volatile long lngMulVar20 = -3;
	volatile long int lngTotalCounter=0;
	volatile long int lngSamplingIntervalCounter=0;
	//struct timeval clkStart, clkPrev, clkEnd, clkTemp;
	struct thread_arg_struct *arg = args;

	// start the timer
	//gettimeofday(&clkStart, NULL);

	// initialize the current & previous timers with start timer
	//clkTemp = clkStart;
	//clkPrev = clkStart;

	// loop to run for run duration (600 seconds / 10 minutes)
	// compares the difference (in seconds) between current timer and start timer with run duration (in seconds)
	/*while (((((((double)clkTemp.tv_sec - (double)clkStart.tv_sec) +
			 (((double)clkTemp.tv_usec - (double)clkStart.tv_usec) / 1000000))))<=arg->intRunDuration) || (i < arg->intRunDuration))*/
	while (intSecCounter<arg->intRunDuration)
	{
		/*
		 * 42 * 2 Addition or Multiplication ops (Arithmetic + Assignment)
		 * 1 * 4 Current - Start Seconds + Current - Start MicroSeconds + Converting MicroSeconds into Seconds + Condition Check for Run Duration
		 * 1 * 1 Condition Check
		 * 1 * 4 Current - Previous Seconds + Current - Previous MicroSeconds + Converting MicroSeconds into Seconds + Condition Check for Sampling Interval
		 */
		// Additions
		lngAddVar1 = lngAddVar1 + 5;
		lngAddVar2 = lngAddVar2 + 3;
		lngAddVar3 = lngAddVar3 + 8;
		lngAddVar4 = lngAddVar4 + 7;
		lngAddVar5 = lngAddVar5 + 4;
		lngAddVar6 = lngAddVar6 + 6;
		lngAddVar7 = lngAddVar7 + 1;
		lngAddVar8 = lngAddVar8 + 2;
		lngAddVar9 = lngAddVar9 + 5;
		lngAddVar10 = lngAddVar10 + 3;
		lngAddVar11 = lngAddVar11 + 8;
		lngAddVar12 = lngAddVar12 + 7;
		lngAddVar13 = lngAddVar13 + 4;
		lngAddVar14 = lngAddVar14 + 6;
		lngAddVar15 = lngAddVar15 + 1;
		lngAddVar16 = lngAddVar16 + 2;
		lngAddVar17 = lngAddVar17 + 4;
		lngAddVar18 = lngAddVar18 + 6;
		lngAddVar19 = lngAddVar19 + 1;
		lngAddVar20 = lngAddVar20 + 2;

		// Multiplications
		lngMulVar1 = lngMulVar1 * 5;
		lngMulVar2 = lngMulVar2 * 3;
		lngMulVar3 = lngMulVar3 * 8;
		lngMulVar4 = lngMulVar4 * 7;
		lngMulVar5 = lngMulVar5 * 4;
		lngMulVar6 = lngMulVar6 * 6;
		lngMulVar7 = lngMulVar7 * 1;
		lngMulVar8 = lngMulVar8 * 2;
		lngMulVar9 = lngMulVar9 * 5;
		lngMulVar10 = lngMulVar10 * 3;
		lngMulVar11 = lngMulVar11 * 8;
		lngMulVar12 = lngMulVar12 * 7;
		lngMulVar13 = lngMulVar13 * 4;
		lngMulVar14 = lngMulVar14 * 6;
		lngMulVar15 = lngMulVar15 * 1;
		lngMulVar16 = lngMulVar16 * 2;
		lngMulVar17 = lngMulVar17 * 4;
		lngMulVar18 = lngMulVar18 * 6;
		lngMulVar19 = lngMulVar19 * 1;
		lngMulVar20 = lngMulVar20 * 2;

		// Increment the Total Operations & Sampling Interval Operations counters
		lngTotalCounter = lngTotalCounter + NOOFFOPS2;
		lngSamplingIntervalCounter = lngSamplingIntervalCounter + NOOFFOPS2;

		// condition to check if sampling interval 1 second is completed
		// compares the difference (in seconds) between current timer and previous timer with run duration (in seconds)
		/*if ((((((double)clkTemp.tv_sec - (double)clkPrev.tv_sec) +
				 (((double)clkTemp.tv_usec - (double)clkPrev.tv_usec) / 1000000))))>=arg->intSamplingInterval)*/
		//{
			// + 4 because of storing the assignments of of structure variables
			// + 3 for computing sample time (assignment operation is considered above)
			// + 1 for resetting Sampling Interval Counter to 0
			// + 2 incrementing & assigning i
			// + 2 for incrementing Sampling & Total Operations Counter by 12

			// initialize the structure variables with appropriate values
			strctCPUP2IopsData[(int)intSecCounter][arg->intThreadNo-1].intSamplingSequence = intSecCounter;
			strctCPUP2IopsData[(int)intSecCounter][arg->intThreadNo-1].lngSamplingIntervalCounter += (long int)lngSamplingIntervalCounter;
			strctCPUP2IopsData[(int)intSecCounter][arg->intThreadNo-1].lngTotalCounter = (long int)lngTotalCounter + 12;
			strctCPUP2IopsData[(int)intSecCounter][arg->intThreadNo-1].dblSamplingTime = 1;//((((double)clkTemp.tv_sec - (double)clkPrev.tv_sec) +
					// (((double)clkTemp.tv_usec - (double)clkPrev.tv_usec) / 1000000)));
			lngSamplingIntervalCounter = 0;

			// set the previous time
			//gettimeofday(&clkPrev, NULL);
			//i++;
		//}

		// set the current time
		//gettimeofday(&clkTemp, NULL);
	}

	// set the end time
	//gettimeofday(&clkEnd, NULL);
	return NULL;
}
// function to compute sample Iops (Experiment 1b - 600 samples at interval of 1 second each) ends over here

// function to initialize character pointer variables for Memory & Disk experiments starts from here
char* initializeCharPtr(int intLength)
{
	// variable & constant declarations
	const char* const strInitialize = "a1b2c3d4e5f6g7h8i9j0k11l12m13n14o15p16q17r18s19t20u21v22w23x24y25z26";

	// INITIAL_SIZE constant set to 100 MB
	long INITIAL_SIZE = 1024 * 1024 * 100 * sizeof (char);
	char *strReturn;
	int i=0;
	int intRand=0;

	// resetting the seed
	srand((unsigned)time(NULL));

	// allocating 100 MB block of memory
	strReturn = malloc (INITIAL_SIZE);

	while (i < intLength)
	{
		// generating random number
		intRand = (int) ((rand() * 1000) + 1);
		intRand = intRand % strlen (strInitialize);
		if ((intRand < strlen (strInitialize)) && (intRand >= 0))
		{
			// initializing string with random characters from array
			*(strReturn + i) = strInitialize[intRand];
			i++;
		}
	}
	return strReturn;
}
// function to initialize character pointer variables for Memory & Disk experiments ends over here

// function to compute Throughput & Latency for Memory (Experiment 2) starts from here
void *computeMemPerformance(void *args)
{
	// variable declaration
	struct thread_arg_struct *arg = args;
	char *strSrc, *strDest, *strOriginalSrc, *strOriginalDest;
	long lngBlockSize = arg->lngBlockSize;
	int i = 0, j = 0;
	long lngNoOfBlocks = 1024 * 1024 * 100 / lngBlockSize;
	struct timeval clkStart, clkEnd;
	long lngRandom, lngFinalDisplacement;

	// initialize strings with random characters each of 100 MB
	strSrc = initializeCharPtr(lngBlockSize);
	strDest = initializeCharPtr(lngBlockSize);

	// initialize dummy pointers to original strings to reset in future
	strOriginalSrc = strSrc;
	strOriginalDest = strDest;

	//code for sequential access memory performance starts from here

	// start the timer
	gettimeofday(&clkStart, NULL);
	for (i=0;i<lngNoOfBlocks;i++)
	{
		// copy the Blocks from Source to Destination within Memory itself
		memcpy(strDest, strSrc, lngBlockSize);

		// increment the Source & Destination pointers by Block Size
		strSrc += lngBlockSize;
		strDest += lngBlockSize;
	}

	// set the end timer
	gettimeofday(&clkEnd, NULL);

	// reset Source and Destination character pointers to their original position
	strSrc = strOriginalSrc;
	strDest = strOriginalDest;

	// initialize the structure variables with appropriate values
	strctMemSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].intThreadNo = arg->intThreadNo;
	strctMemSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].clkThStart = clkStart;
	strctMemSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].clkThEnd = clkEnd;
	strctMemSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].lngTotalMemSize = lngBlockSize * lngNoOfBlocks;
	strctMemSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].dblThroughput = (((double)lngBlockSize * (double)lngNoOfBlocks)) /
			((((double)clkEnd.tv_sec - (double)clkStart.tv_sec) + (((double)clkEnd.tv_usec - (double)clkStart.tv_usec) / 1000000)) * 1024 * 1024);
	strctMemSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].lngBlockSize = lngBlockSize;
	strctMemSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].lngNoOfBlocks = lngNoOfBlocks;

	// start the timer
	gettimeofday(&clkStart, NULL);
	for (i=0;i<lngNoOfBlocks;i++)
	{
		// initialize the Destination character pointer Blocks with random character
		memset(strDest, (char)((j % 26) + 65), lngBlockSize);

		// increment the Destination pointer by Block Size
		strDest += lngBlockSize;
	}

	// set the end timer
	gettimeofday(&clkEnd, NULL);

	// reset Source and Destination character pointers to their original position
	strSrc = strOriginalSrc;
	strDest = strOriginalDest;

	// initialize the structure variables with appropriate values
	strctMemSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].clkLatStart = clkStart;
	strctMemSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].clkLatEnd = clkEnd;
	strctMemSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].dblLatency =
			((((double)clkEnd.tv_sec - (double)clkStart.tv_sec) + (((double)clkEnd.tv_usec - (double)clkStart.tv_usec) / 1000000)) * 1000)
			/ (((double)lngBlockSize * (double)lngNoOfBlocks));
	//code for sequential access memory performance ends over here

	//code for random access memory performance starts from here

	// initialize strings with random characters each of 100 MB
	strSrc = initializeCharPtr(lngBlockSize);
	strDest = initializeCharPtr(lngBlockSize);

	// start the timer
	gettimeofday(&clkStart, NULL);
	for (i=0;i<lngNoOfBlocks;)
	{
		// generate random number between 0 and No. of Blocks
		lngRandom = random() % (lngNoOfBlocks / 2);
		if ((lngRandom >= 0) && (lngRandom < lngNoOfBlocks / 2))
		{
			// move Source and Destination character pointers to Random Blocks away from initial / starting position
			if (i % 2 == 0)
			{
				strSrc += (lngBlockSize * lngRandom);
				strDest += (lngBlockSize * lngRandom);
			}
			else
			{
				long lngSrcLength = strlen (strSrc);
				long lngDestLength = strlen (strDest);
				strSrc = strSrc + (strlen (strSrc) - (((lngSrcLength / lngBlockSize) - lngRandom) * lngBlockSize));
				strDest = strDest + (strlen(strDest) - (((lngDestLength / lngBlockSize) - lngRandom) * lngBlockSize));
			}

			// copy the Blocks from Source to Destination within Memory itself
			memcpy(strDest, strSrc, lngBlockSize);

			// reset Source and Destination character pointers to their original position
			strSrc = strOriginalSrc;
			strDest = strOriginalDest;

			// increment the loop variable only if the random number generated is between 0 and No. of Blocks
			i++;
		}
	}

	// set the end timer
	gettimeofday(&clkEnd, NULL);

	// reset Source and Destination character pointers to their original position
	strSrc = strOriginalSrc;
	strDest = strOriginalDest;

	// initialize the structure variables with appropriate values
	strctMemRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].intThreadNo = arg->intThreadNo;
	strctMemRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].clkThStart = clkStart;
	strctMemRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].clkThEnd = clkEnd;
	strctMemRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].lngTotalMemSize = lngBlockSize * lngNoOfBlocks;
	strctMemRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].dblThroughput = (((double)lngBlockSize * (double)lngNoOfBlocks)) /
			((((double)clkEnd.tv_sec - (double)clkStart.tv_sec) + (((double)clkEnd.tv_usec - (double)clkStart.tv_usec) / 1000000)) * 1024 * 1024);
	strctMemRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].lngBlockSize = lngBlockSize;
	strctMemRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].lngNoOfBlocks = lngNoOfBlocks;

	// start the timer
	gettimeofday(&clkStart, NULL);
	for (i=0;i<lngNoOfBlocks;)
	{
		// generate random number between 0 and No. of Blocks
		lngRandom = random() % lngNoOfBlocks;
		if ((lngRandom >= 0) && (lngRandom < lngNoOfBlocks))
		{
			// move Destination character pointer to Random Blocks away from initial / starting position
			strDest += (lngBlockSize * lngRandom);

			// initialize the Destination character pointer Blocks with random character
			memset(strDest, (char)((j % 26) + 65), lngBlockSize);

			// reset Source and Destination character pointers to their original position
			strSrc = strOriginalSrc;
			strDest = strOriginalDest;

			// increment the loop variable only if the random number generated is between 0 and No. of Blocks
			i++;
		}
	}

	// set the end timer
	gettimeofday(&clkEnd, NULL);

	// reset Source and Destination character pointers to their original position
	strSrc = strOriginalSrc;
	strDest = strOriginalDest;

	// initialize the structure variables with appropriate values
	strctMemRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].clkLatStart = clkStart;
	strctMemRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].clkLatEnd = clkEnd;
	strctMemRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].dblLatency =
			((((double)clkEnd.tv_sec - (double)clkStart.tv_sec) + (((double)clkEnd.tv_usec - (double)clkStart.tv_usec) / 1000000)) * 1000)
			/ (((double)lngBlockSize * (double)lngNoOfBlocks));
	//code for random access memory performance ends over here

	free (strSrc);
	free (strDest);
	return NULL;
}
// function to compute Throughput & Latency for Memory (Experiment 2) ends over here

// function to compute Throughput & Latency for Disk (Experiment 3) starts from here
void *computeDiskPerformance(void *args)
{
	// variable declaration
	struct thread_arg_struct *arg = args;
	char *strSrc, *strOriginalSrc, *strRead;
	char *strTemp;
	long lngBlockSize = arg->lngBlockSize;
	int i = 0, j = 0;
	long lngNoOfBlocks;
	if (lngBlockSize < 1024 * 1024)
	{
		lngNoOfBlocks = 1024 * 30 / lngBlockSize;
	}
	else
	{
		lngNoOfBlocks = 1024 * 1024 * 50 / lngBlockSize;
	}
	struct timeval clkStart, clkEnd;
	long lngRandom;
	int fdWrite, fdRead, intWriteOutput, intReadOutput, intSeek;

	// initialize strings with random characters each of 100 MB
	strSrc = initializeCharPtr(lngBlockSize * lngNoOfBlocks);
	strRead = initializeCharPtr(lngBlockSize * lngNoOfBlocks);

	// initialize dummy pointers to original strings to reset in future
	strOriginalSrc = strSrc;

	// creating a file for sequential and random read operations
	fdRead = open ("./FileTestRead.txt", O_WRONLY | O_CREAT, S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP | S_IROTH | S_IWOTH);
	for (i=0;i<lngNoOfBlocks;i++)
	{
		intWriteOutput = write (fdRead, strRead, lngBlockSize);
		if (intWriteOutput == -1)
		{
			printf ("Error while writing into file during Sequential Access Disk Benchmarking experiment\n");
			exit (0);
		}
		strRead += lngBlockSize;
	}

	if (close(fdRead)== -1)
	{
		printf ("Error while closing the reading file during Sequential Access Disk Benchmarking experiment\n");
		exit (0);
	}

	//code for sequential access disk performance write operation starts from here
	fdWrite = open ("./FileTestSeqWrite.txt", O_WRONLY | O_CREAT, S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP | S_IROTH | S_IWOTH);

	// start the timer
	gettimeofday(&clkStart, NULL);
	for (i=0;i<lngNoOfBlocks;i++)
	{
		// write the Block Size characters from Source character pointer into disk file
		intWriteOutput = write (fdWrite, strSrc, lngBlockSize);

		// check the status of write operation and exit in case of error
		if (intWriteOutput == -1)
		{
			printf ("Error while writing into file during Sequential Access Disk Benchmarking experiment\n");
			exit (0);
		}

		// increment the Source pointer by Block Size
		strSrc += lngBlockSize;
		fsync (fdWrite);
	}

	// set the end timer
	gettimeofday(&clkEnd, NULL);

	// reset Source and Destination character pointers to their original position
	strSrc = strOriginalSrc;

	// initialize the structure variables with appropriate values
	strctDiskSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].chrOperation='W';
	strctDiskSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].intThreadNo = arg->intThreadNo;
	strctDiskSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].clkThStart = clkStart;
	strctDiskSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].clkThEnd = clkEnd;
	strctDiskSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].lngTotalMemSize = lngBlockSize * lngNoOfBlocks;
	strctDiskSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].dblThroughput = (((double)lngBlockSize * (double)lngNoOfBlocks)) /
			((((double)clkEnd.tv_sec - (double)clkStart.tv_sec) + (((double)clkEnd.tv_usec - (double)clkStart.tv_usec) / 1000000)) * 1024 * 1024);
	strctDiskSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].lngBlockSize = lngBlockSize;
	strctDiskSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].lngNoOfBlocks = lngNoOfBlocks;
	strctDiskSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].clkLatStart = clkStart;
	strctDiskSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].clkLatEnd = clkEnd;
	strctDiskSeqAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].dblLatency =
			((((double)clkEnd.tv_sec - (double)clkStart.tv_sec) + (((double)clkEnd.tv_usec - (double)clkStart.tv_usec) / 1000000)) * 1000)
			/ (((double)lngNoOfBlocks));
	if (close(fdWrite)== -1)
	{
		printf ("Error while closing the file after sequential write operation during Disk Benchmarking experiment\n");
		exit (0);
	}
	//code for sequential access disk performance write operation ends over here

	//code for sequential access disk performance read operation starts from here
	fdRead = open ("./FileTestRead.txt", O_RDONLY, S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP | S_IROTH | S_IWOTH);

	// start the timer
	gettimeofday(&clkStart, NULL);
	for (i=0;i<lngNoOfBlocks;i++)
	{
		// memory allocation for read buffer using malloc
		strTemp = malloc (lngBlockSize * sizeof(char));

		// read the Block Size characters from disk file into buffer
		intReadOutput = read (fdRead, strTemp, lngBlockSize);

		// check the status of write operation and exit in case of error
		if (intReadOutput == -1)
		{
			printf ("Error while reading from file during Sequential Access Disk Benchmarking experiment\n");
			exit (0);
		}

		//free the buffer memory
		free (strTemp);
		fsync (fdRead);
	}

	// set the end timer
	gettimeofday(&clkEnd, NULL);

	// reset Source and Destination character pointers to their original position
	strSrc = strOriginalSrc;

	// initialize the structure variables with appropriate values
	strctDiskSeqAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].chrOperation='R';
	strctDiskSeqAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].intThreadNo = arg->intThreadNo;
	strctDiskSeqAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].clkThStart = clkStart;
	strctDiskSeqAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].clkThEnd = clkEnd;
	strctDiskSeqAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].lngTotalMemSize = lngBlockSize * lngNoOfBlocks;
	strctDiskSeqAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].dblThroughput = (((double)lngBlockSize * (double)lngNoOfBlocks)) /
			((((double)clkEnd.tv_sec - (double)clkStart.tv_sec) + (((double)clkEnd.tv_usec - (double)clkStart.tv_usec) / 1000000)) * 1024 * 1024);
	strctDiskSeqAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].lngBlockSize = lngBlockSize;
	strctDiskSeqAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].lngNoOfBlocks = lngNoOfBlocks;
	strctDiskSeqAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].clkLatStart = clkStart;
	strctDiskSeqAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].clkLatEnd = clkEnd;
	strctDiskSeqAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].dblLatency =
			((((double)clkEnd.tv_sec - (double)clkStart.tv_sec) + (((double)clkEnd.tv_usec - (double)clkStart.tv_usec) / 1000000)) * 1000)
			/ (((double)lngBlockSize * (double)lngNoOfBlocks));
	if (close(fdRead)== -1)
	{
		printf ("Error while closing the file after sequential read operation during Disk Benchmarking experiment\n");
		exit (0);
	}
	//code for sequential access disk performance read operation ends over here

	//code for random access disk performance write operation starts from here
	fdWrite = open ("./FileTestRandWrite.txt", O_WRONLY | O_CREAT, S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP | S_IROTH | S_IWOTH);

	// start the timer
	gettimeofday(&clkStart, NULL);
	for (i=0;i<lngNoOfBlocks;i++)
	{
		// generate a random number betwwen 0 and No. of Blocs
		lngRandom = random() % lngNoOfBlocks;

		// position the File Pointer to random blocks away
		if ((intSeek = lseek(fdWrite, lngRandom, SEEK_SET)) == -1)
		{
			printf ("Error while seeking in file during random write operation during Disk Benchmarking experiment\n");
			exit (0);
		}

		// write the Block Size characters from Source character pointer into disk file
		intWriteOutput = write (fdWrite, strSrc, strlen(strSrc));

		// check the status of write operation and exit in case of error
		if (intWriteOutput == -1)
		{
			printf ("Error while writing into file during Random Access Disk Benchmarking experiment\n");
			exit (0);
		}

		// increment the Source pointer by Block Size
		strSrc += lngBlockSize;
		fsync (fdWrite);
	}

	// set the end timer
	gettimeofday(&clkEnd, NULL);

	// reset Source and Destination character pointers to their original position
	strSrc = strOriginalSrc;

	// initialize the structure variables with appropriate values
	strctDiskRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].chrOperation='W';
	strctDiskRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].intThreadNo = arg->intThreadNo;
	strctDiskRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].clkThStart = clkStart;
	strctDiskRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].clkThEnd = clkEnd;
	strctDiskRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].lngTotalMemSize = lngBlockSize * lngNoOfBlocks;
	strctDiskRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].dblThroughput = (((double)lngBlockSize * (double)lngNoOfBlocks)) /
			((((double)clkEnd.tv_sec - (double)clkStart.tv_sec) + (((double)clkEnd.tv_usec - (double)clkStart.tv_usec) / 1000000)) * 1024 * 1024);
	strctDiskRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].lngBlockSize = lngBlockSize;
	strctDiskRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].lngNoOfBlocks = lngNoOfBlocks;
	strctDiskRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].clkLatStart = clkStart;
	strctDiskRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].clkLatEnd = clkEnd;
	strctDiskRandomAccess[arg->intBlockSizeIndex][arg->intThreadNo-1].dblLatency =
			((((double)clkEnd.tv_sec - (double)clkStart.tv_sec) + (((double)clkEnd.tv_usec - (double)clkStart.tv_usec) / 1000000)) * 1000)
			/ (((double)lngBlockSize * (double)lngNoOfBlocks));
	if (close(fdWrite)== -1)
	{
		printf ("Error while closing the file after random write operation during Disk Benchmarking experiment\n");
		exit (0);
	}
	//code for random access disk performance write operation ends over here

	//code for random access disk performance read operation starts from here
	fdRead = open ("./FileTestRead.txt", O_RDONLY, S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP | S_IROTH | S_IWOTH);

	// start the timer
	gettimeofday(&clkStart, NULL);
	for (i=0;i<lngNoOfBlocks;i++)
	{
		// allocate the memory to buffer
		strTemp = malloc (lngBlockSize * sizeof(char));

		// generate a random number betwwen 0 and No. of Blocs
		lngRandom = random() % lngNoOfBlocks;

		// position the File Pointer to random blocks away
		if (lseek(fdRead, lngRandom, SEEK_SET) == -1)
		{
			printf ("Error while seeking in file during random read operation during Disk Benchmarking experiment\n");
			exit (0);
		}

		// read the Block Size characters from Source character pointer into disk file
		intReadOutput = read (fdRead, strSrc, lngBlockSize);

		// check the status of read operation and exit in case of error
		if (intReadOutput == -1)
		{
			printf ("Error while reading from file during Random Access Disk Benchmarking experiment\n");
			exit (0);
		}

		// free the buffer memory
		free (strTemp);
		fsync (fdRead);
	}

	// set the end timer
	gettimeofday(&clkEnd, NULL);

	// reset Source and Destination character pointers to their original position
	strSrc = strOriginalSrc;

	// initialize the structure variables with appropriate values
	strctDiskRandomAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].chrOperation='R';
	strctDiskRandomAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].intThreadNo = arg->intThreadNo;
	strctDiskRandomAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].clkThStart = clkStart;
	strctDiskRandomAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].clkThEnd = clkEnd;
	strctDiskRandomAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].lngTotalMemSize = lngBlockSize * lngNoOfBlocks;
	strctDiskRandomAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].dblThroughput = (((double)lngBlockSize * (double)lngNoOfBlocks)) /
			((((double)clkEnd.tv_sec - (double)clkStart.tv_sec) + (((double)clkEnd.tv_usec - (double)clkStart.tv_usec) / 1000000)) * 1024 * 1024);
	strctDiskRandomAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].lngBlockSize = lngBlockSize;
	strctDiskRandomAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].lngNoOfBlocks = lngNoOfBlocks;
	strctDiskRandomAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].clkLatStart = clkStart;
	strctDiskRandomAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].clkLatEnd = clkEnd;
	strctDiskRandomAccess[arg->intBlockSizeIndex + 1][arg->intThreadNo-1].dblLatency =
			((((double)clkEnd.tv_sec - (double)clkStart.tv_sec)+(((double)clkEnd.tv_usec - (double)clkStart.tv_usec) / 1000000)) * 1000)
			/ (((double)lngBlockSize * (double)lngNoOfBlocks));
	if (close(fdRead)== -1)
	{
		printf ("Error while closing the file after random read operation during Disk Benchmarking experiment\n");
		exit (0);
	}
	//code for sequential access disk performance write operation ends over here

	free (strSrc);
	return NULL;
}
// function to compute Throughput & Latency for Disk (Experiment 3) ends over here

// function to govern Run Time duration & Sampling Interval starts from here
void *runTimer (void *args)
{
	struct thread_arg_struct *arg = args;

	intSecCounter = 0;
	while (intSecCounter < arg->intRunDuration)
	{
		sleep(arg->intSamplingInterval);
		intSecCounter = intSecCounter + 1;
	}
	return NULL;
}
// function to govern Run Time duration & Sampling Interval ends over here

// function to call Flops & Iops benchmarking (experiment 1a) with 4 threads starts from here
void CPUBenchmarking_Part1()
{
	pthread_t arrPTh[4];
	int i = 0;
	int intNoOfThreads=0;
	double dblSumOps, dblTime1, dblTime2, dblTime3, dblTime4, dblAvgTimeTaken;

	// FLOPS part starts from there
	// thread creation part
	intNoOfThreads = 1;

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo=i+1;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeFlops, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// wait for threads to be finished
	for (i=0;i<intNoOfThreads;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// print the results for the experiments conducted
	printf ("\n\nExperiment 1a - FLOPS computations - 1 Thread Statistics\nDescription\tThread 1 Values\n");
	printf ("Thread No: \t%d\n", strctFD[0][0].intThreadNo);
	printf ("Iterations: \t%f\n", strctFD[0][0].dblIterations);
	printf ("Total No of Operations: \t%f\n", strctFD[0][0].dblTotalNoOfOps);
	printf ("Flops: \t%f\n", strctFD[0][0].dblGFlops);
	printf ("Final Flops: \t%f\n", strctFD[0][0].dblGFlops);

	//Create worker thread
	intNoOfThreads = 2;

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo=i+1;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeFlops, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// wait for threads to be finished
	for (i=0;i<intNoOfThreads;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// print the results for the experiments conducted
	printf ("\n\nExperiment 1a - FLOPS computations - 2 Threads Statistics\nDescription\tThread 1 Values\tThread 2 Values\n");
	printf ("Thread No: \t%d\t%d\n", strctFD[0][0].intThreadNo, strctFD[0][1].intThreadNo);
	printf ("Iterations: \t%f\t%f\n", strctFD[0][0].dblIterations, strctFD[0][1].dblIterations);
	printf ("Total No of Operations: \t%f\t%f\n", strctFD[0][0].dblTotalNoOfOps, strctFD[0][1].dblTotalNoOfOps);
	printf ("Flops: \t%f\t%f\n", strctFD[0][0].dblGFlops, strctFD[0][1].dblGFlops);
	dblSumOps = strctFD[0][0].dblTotalNoOfOps + strctFD[0][1].dblTotalNoOfOps;
	dblTime1 = (double)(((double)strctFD[0][0].clkEnd.tv_sec - (double)strctFD[0][0].clkStart.tv_sec) +
			 (((double)strctFD[0][0].clkEnd.tv_usec - (double)strctFD[0][0].clkStart.tv_usec) / 1000000));
	dblTime2 = (double)(((double)strctFD[0][1].clkEnd.tv_sec - (double)strctFD[0][1].clkStart.tv_sec) +
			 (((double)strctFD[0][1].clkEnd.tv_usec - (double)strctFD[0][1].clkStart.tv_usec) / 1000000));
	dblAvgTimeTaken = (dblTime1 + dblTime2) / 2;
	printf ("Final Flops: \t%f\n", ((dblSumOps / dblAvgTimeTaken) / (double)1000000000));

	//Create worker thread
	// thread creation part
	intNoOfThreads = 4;

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo=i+1;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeFlops, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// wait for threads to be finished
	for (i=0;i<intNoOfThreads;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// print the results for the experiments conducted
	printf ("\n\nExperiment 1a - FLOPS computations - 4 Threads Statistics\nDescription\tThread 1 Values\tThread 2 Values\tThread 3 Values\tThread 4 Values\n");
	printf ("Thread No: \t%d\t%d\t%d\t%d\n", strctFD[0][0].intThreadNo, strctFD[0][1].intThreadNo, strctFD[0][2].intThreadNo, strctFD[0][3].intThreadNo);
	printf ("Iterations: \t%f\t%f\t%f\t%f\n", strctFD[0][0].dblIterations, strctFD[0][1].dblIterations, strctFD[0][2].dblIterations, strctFD[0][3].dblIterations);
	printf ("Total No of Operations: \t%f\t%f\t%f\t%f\n", strctFD[0][0].dblTotalNoOfOps, strctFD[0][1].dblTotalNoOfOps, strctFD[0][2].dblTotalNoOfOps, strctFD[0][3].dblTotalNoOfOps);
	printf ("Flops: \t%f\t%f\t%f\t%f\n", strctFD[0][0].dblGFlops, strctFD[0][1].dblGFlops, strctFD[0][2].dblGFlops, strctFD[0][3].dblGFlops);
	dblSumOps = strctFD[0][0].dblTotalNoOfOps + strctFD[0][1].dblTotalNoOfOps + strctFD[0][2].dblTotalNoOfOps + strctFD[0][3].dblTotalNoOfOps;
	dblTime1 = (double)(((double)strctFD[0][0].clkEnd.tv_sec - (double)strctFD[0][0].clkStart.tv_sec) +
			 (((double)strctFD[0][0].clkEnd.tv_usec - (double)strctFD[0][0].clkStart.tv_usec) / 1000000));
	dblTime2 = (double)(((double)strctFD[0][1].clkEnd.tv_sec - (double)strctFD[0][1].clkStart.tv_sec) +
			 (((double)strctFD[0][1].clkEnd.tv_usec - (double)strctFD[0][1].clkStart.tv_usec) / 1000000));
	dblTime3 = (double)(((double)strctFD[0][2].clkEnd.tv_sec - (double)strctFD[0][2].clkStart.tv_sec) +
			 (((double)strctFD[0][2].clkEnd.tv_usec - (double)strctFD[0][2].clkStart.tv_usec) / 1000000));
	dblTime4 = (double)(((double)strctFD[0][3].clkEnd.tv_sec - (double)strctFD[0][3].clkStart.tv_sec) +
			 (((double)strctFD[0][3].clkEnd.tv_usec - (double)strctFD[0][3].clkStart.tv_usec) / 1000000));
	dblAvgTimeTaken = (dblTime1 + dblTime2 + dblTime3 + dblTime4) / 4;
	printf ("Final Flops: \t%f\n", ((dblSumOps / dblAvgTimeTaken) / (double)1000000000));



	//IOPS part starts from there
	//Create worker thread
	// thread creation part
	intNoOfThreads = 1;

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo=i+1;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeIops, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// wait for threads to be finished
	for (i=0;i<intNoOfThreads;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// print the results for the experiments conducted
	printf ("\n\nExperiment 1a - IOPS computations - 1 Thread Statistics\nDescription\tThread 1 Values\n");
	printf ("Thread No: \t%d\n", strctID[0][0].intThreadNo);
	printf ("Iterations: \t%f\n", strctID[0][0].dblIterations);
	printf ("Total No of Operations: \t%f\n", strctID[0][0].dblTotalNoOfOps);
	printf ("Iops: \t%f\n", strctID[0][0].dblGFlops);
	printf ("Final Iops: \t%f\n", strctID[0][0].dblGFlops);

	//Create worker thread
	// thread creation part
	intNoOfThreads = 2;

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo=i+1;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeIops, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// wait for threads to be finished
	for (i=0;i<intNoOfThreads;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// print the results for the experiments conducted
	printf ("\n\nExperiment 1a - IOPS computations - 2 Threads Statistics\nDescription\tThread 1 Values\tThread 2 Values\n");
	printf ("Thread No: \t%d\t%d\n", strctID[0][0].intThreadNo, strctID[0][1].intThreadNo);
	printf ("Iterations: \t%f\t%f\n", strctID[0][0].dblIterations, strctID[0][1].dblIterations);
	printf ("Total No of Operations: \t%f\t%f\n", strctID[0][0].dblTotalNoOfOps, strctID[0][1].dblTotalNoOfOps);
	printf ("Iops: \t%f\t%f\n", strctID[0][0].dblGFlops, strctID[0][1].dblGFlops);
	dblSumOps = strctID[0][0].dblTotalNoOfOps + strctID[0][1].dblTotalNoOfOps;
	dblTime1 = (double)(((double)strctID[0][0].clkEnd.tv_sec - (double)strctID[0][0].clkStart.tv_sec) +
			 (((double)strctID[0][0].clkEnd.tv_usec - (double)strctID[0][0].clkStart.tv_usec) / 1000000));
	dblTime2 = (double)(((double)strctID[0][1].clkEnd.tv_sec - (double)strctID[0][1].clkStart.tv_sec) +
			 (((double)strctID[0][1].clkEnd.tv_usec - (double)strctID[0][1].clkStart.tv_usec) / 1000000));
	dblAvgTimeTaken = (dblTime1 + dblTime2) / 2;
	printf ("Final Iops: \t%f\n", ((dblSumOps / dblAvgTimeTaken) / (double)1000000000));

	//Create worker thread
	// thread creation part
	intNoOfThreads = 4;

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo=i+1;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeIops, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// wait for threads to be finished
	for (i=0;i<intNoOfThreads;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// print the results for the experiments conducted
	printf ("\n\nExperiment 1a - IOPS computations - 4 Threads Statistics\nDescription\tThread 1 Values\tThread 2 Values\tThread 3 Values\tThread 4 Values\n");
	printf ("Thread No: \t%d\t%d\t%d\t%d\n", strctID[0][0].intThreadNo, strctID[0][1].intThreadNo, strctID[0][2].intThreadNo, strctID[0][3].intThreadNo);
	printf ("Iterations: \t%f\t%f\t%f\t%f\n", strctID[0][0].dblIterations, strctID[0][1].dblIterations, strctID[0][2].dblIterations, strctID[0][3].dblIterations);
	printf ("Total No of Operations: \t%f\t%f\t%f\t%f\n", strctID[0][0].dblTotalNoOfOps, strctID[0][1].dblTotalNoOfOps, strctID[0][2].dblTotalNoOfOps, strctID[0][3].dblTotalNoOfOps);
	printf ("Iops: \t%f\t%f\t%f\t%f\n", strctID[0][0].dblGFlops, strctID[0][1].dblGFlops, strctID[0][2].dblGFlops, strctID[0][3].dblGFlops);
	dblSumOps = strctID[0][0].dblTotalNoOfOps + strctID[0][1].dblTotalNoOfOps + strctID[0][2].dblTotalNoOfOps + strctID[0][3].dblTotalNoOfOps;
	dblTime1 = (double)(((double)strctID[0][0].clkEnd.tv_sec - (double)strctID[0][0].clkStart.tv_sec) +
			 (((double)strctID[0][0].clkEnd.tv_usec - (double)strctID[0][0].clkStart.tv_usec) / 1000000));
	dblTime2 = (double)(((double)strctID[0][1].clkEnd.tv_sec - (double)strctID[0][1].clkStart.tv_sec) +
			 (((double)strctID[0][1].clkEnd.tv_usec - (double)strctID[0][1].clkStart.tv_usec) / 1000000));
	dblTime3 = (double)(((double)strctID[0][2].clkEnd.tv_sec - (double)strctID[0][2].clkStart.tv_sec) +
			 (((double)strctID[0][2].clkEnd.tv_usec - (double)strctID[0][2].clkStart.tv_usec) / 1000000));
	dblTime4 = (double)(((double)strctID[0][3].clkEnd.tv_sec - (double)strctID[0][3].clkStart.tv_sec) +
			 (((double)strctID[0][3].clkEnd.tv_usec - (double)strctID[0][3].clkStart.tv_usec) / 1000000));
	dblAvgTimeTaken = (dblTime1 + dblTime2 + dblTime3 + dblTime4) / 4;
	printf ("Final Iops: \t%f\n", ((dblSumOps / dblAvgTimeTaken) / (double)1000000000));
}
// function to call Flops & Iops benchmarking (experiment 1a) with 4 threads ends over here

// function to call Flops & Iops sampling benchmarking (experiment 1b) with 4 threads starts from here
void CPUBenchmarking_Part2(int intRunDuration, int intSamplingInterval)
{
	pthread_t arrPTh[5];
	int i = 0;
	int intNoOfThreads = 0;
	double dblSumOps, dblTime1, dblTime2, dblTime3, dblTime4, dblAvgTimeTaken, dblFinalFlops;

	//FLOPS part starts from here
	//Create worker thread
	// thread creation part
	intNoOfThreads = 4;

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo=i+1;
		strctTAS[i].intRunDuration = intRunDuration;
		strctTAS[i].intSamplingInterval = intSamplingInterval;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeFlops2, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// create the thread, call the respective function with appropriate arguments  and check creation status
	if (pthread_create(&arrPTh[i], NULL, runTimer, (void *)&(strctTAS[0]))!=0)
	{
		printf ("Unable to create thread %d", i+1);
		exit(0);
	}

	// wait for threads to be finished
	for (i=0;i<5;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// print the results for the experiments conducted
	/*printf ("\n\nExperiment 1b - FLOPS Sampling - 4 Threads Statistics\nSampling Sequence\tThread 1 Sampling Counter"
			"\tThread 2  Sampling Counter\tThread 3  Sampling Counter\tThread 4  Sampling Counter\tFinal Flops\n");*/
	printf ("\n\nExperiment 1b - FLOPS Sampling - 4 Threads Statistics\nSampling Sequence\tFinal Flops\n");
	for (i=0;i<intRunDuration;i++)
	{
		dblSumOps = ((double)strctCPUP2FlopsData[i][0].lngSamplingIntervalCounter + strctCPUP2FlopsData[i][1].lngSamplingIntervalCounter +
				strctCPUP2FlopsData[i][2].lngSamplingIntervalCounter + strctCPUP2FlopsData[i][3].lngSamplingIntervalCounter);
		dblTime1 = strctCPUP2FlopsData[i][0].dblSamplingTime;
		dblTime2 = strctCPUP2FlopsData[i][1].dblSamplingTime;
		dblTime3 = strctCPUP2FlopsData[i][2].dblSamplingTime;
		dblTime4 = strctCPUP2FlopsData[i][3].dblSamplingTime;
		dblAvgTimeTaken = (dblTime1 + dblTime2 + dblTime3 + dblTime4) / 4;
		dblFinalFlops = ((dblSumOps / dblAvgTimeTaken) / (double)1000000000);
		/*printf ("%d\t%ld\t%ld\t%ld\t%ld\t%f\n", strctCPUP2FlopsData[i][0].intSamplingSequence + 1,
				strctCPUP2FlopsData[i][0].lngSamplingIntervalCounter, strctCPUP2FlopsData[i][1].lngSamplingIntervalCounter,
				strctCPUP2FlopsData[i][2].lngSamplingIntervalCounter, strctCPUP2FlopsData[i][3].lngSamplingIntervalCounter,
				dblFinalFlops);*/
		printf ("%d\t%f\n", strctCPUP2FlopsData[i][0].intSamplingSequence + 1, dblFinalFlops);
		//printf ("%f\t%f\t%f\t%f\n", dblTime1, dblTime2, dblTime3, dblTime4);
		/*printf ("%d\t%ld\t%ld\t%ld\t%ld\t%f\t%f\t%f\t%f\n", strctCPUP2FlopsData[i][0].intSamplingSequence + 1,
						strctCPUP2FlopsData[i][0].lngSamplingIntervalCounter, strctCPUP2FlopsData[i][1].lngSamplingIntervalCounter,
						strctCPUP2FlopsData[i][2].lngSamplingIntervalCounter, strctCPUP2FlopsData[i][3].lngSamplingIntervalCounter,
						strctCPUP2FlopsData[i][0].dblSamplingTime, strctCPUP2FlopsData[i][1].dblSamplingTime,
						strctCPUP2FlopsData[i][2].dblSamplingTime, strctCPUP2FlopsData[i][3].dblSamplingTime);*/
	}



	//IOPS part starts from here
	//Create worker thread
	intNoOfThreads = 4;

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo=i+1;
		strctTAS[i].intRunDuration = intRunDuration;
		strctTAS[i].intSamplingInterval = intSamplingInterval;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeIops2, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// create the thread, call the respective function with appropriate arguments  and check creation status
	if (pthread_create(&arrPTh[i], NULL, runTimer, (void *)&(strctTAS[0]))!=0)
	{
		printf ("Unable to create thread %d", i+1);
		exit(0);
	}

	// wait for threads to be finished
	for (i=0;i<5;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// print the results for the experiments conducted
	/*printf ("\n\nExperiment 1b - IOPS Sampling - 4 Threads Statistics\nSampling Sequence\tThread 1 Sampling Counter"
			"\tThread 2  Sampling Counter\tThread 3  Sampling Counter\tThread 4  Sampling Counter\tFinal Iops\n");*/
	printf ("\n\nExperiment 1b - IOPS Sampling - 4 Threads Statistics\nSampling Sequence\tFinal Iops\n");
	for (i=0;i<intRunDuration;i++)
	{
		dblSumOps = ((double)strctCPUP2IopsData[i][0].lngSamplingIntervalCounter + strctCPUP2IopsData[i][1].lngSamplingIntervalCounter +
				strctCPUP2IopsData[i][2].lngSamplingIntervalCounter + strctCPUP2IopsData[i][3].lngSamplingIntervalCounter);
		dblTime1 = strctCPUP2IopsData[i][0].dblSamplingTime;
		dblTime2 = strctCPUP2IopsData[i][1].dblSamplingTime;
		dblTime3 = strctCPUP2IopsData[i][2].dblSamplingTime;
		dblTime4 = strctCPUP2IopsData[i][3].dblSamplingTime;
		dblAvgTimeTaken = (dblTime1 + dblTime2 + dblTime3 + dblTime4) / 4;
		dblFinalFlops = ((dblSumOps / dblAvgTimeTaken) / (double)1000000000);
		/*printf ("%d\t%ld\t%ld\t%ld\t%ld\t%f\n", strctCPUP2IopsData[i][0].intSamplingSequence,
				strctCPUP2IopsData[i][0].lngSamplingIntervalCounter, strctCPUP2IopsData[i][1].lngSamplingIntervalCounter,
				strctCPUP2IopsData[i][2].lngSamplingIntervalCounter, strctCPUP2IopsData[i][3].lngSamplingIntervalCounter,
				dblFinalFlops);*/
		printf ("%d\t%f\n", strctCPUP2IopsData[i][0].intSamplingSequence + 1, dblFinalFlops);
	}
}
// function to call Flops & Iops sampling benchmarking (experiment 1b) with 4 threads ends over here

// function to call Memory Benchmarking with 1 & 2 threads starts from here
void MemBenchmarking()
{
	pthread_t arrPTh[3];
	int i = 0;
	int intNoOfThreads=0;

	//Create worker thread
	intNoOfThreads = 1;

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo = i+1;
		strctTAS[i].lngBlockSize = 1;
		strctTAS[i].intBlockSizeIndex = 0;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeMemPerformance, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// wait for threads to be finished
	for (i=0;i<intNoOfThreads;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo = i+1;
		strctTAS[i].lngBlockSize = 1024;
		strctTAS[i].intBlockSizeIndex = 1;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeMemPerformance, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// wait for threads to be finished
	for (i=0;i<intNoOfThreads;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo = i+1;
		strctTAS[i].lngBlockSize = 1024 * 1024;
		strctTAS[i].intBlockSizeIndex = 2;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeMemPerformance, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// wait for threads to be finished
	for (i=0;i<intNoOfThreads;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// print the results for the experiments conducted
	printf ("\n\n\nSequential Memory Access Statistics\nDescription\tThread 1 Values\n");
	for (i=0;i<3;i++)
	{
		printf ("Block Size: \t%ld Bytes\n", strctMemSeqAccess[i][0].lngBlockSize);
		printf ("No. of Blocks: \t%ld\n", strctMemSeqAccess[i][0].lngNoOfBlocks);
		printf ("Total Memory Size: \t%ld Bytes\n", strctMemSeqAccess[i][0].lngTotalMemSize);
		printf ("Throughput: \t%7.4f MBPS\n", strctMemSeqAccess[i][0].dblThroughput);
		printf ("Latency: \t%02.15f milliSec\n\n", strctMemSeqAccess[i][0].dblLatency);
	}

	printf ("\n\n\nRandom Memory Access Statistics\nDescription\tThread 1 Values\n");
	for (i=0;i<3;i++)
	{
		printf ("Block Size: \t%ld Bytes\n", strctMemRandomAccess[i][0].lngBlockSize);
		printf ("No. of Blocks: \t%ld\n", strctMemRandomAccess[i][0].lngNoOfBlocks);
		printf ("Total Memory Size: \t%ld Bytes\n", strctMemRandomAccess[i][0].lngTotalMemSize);
		printf ("Throughput: \t%7.4f MBPS\n", strctMemRandomAccess[i][0].dblThroughput);
		printf ("Latency: \t%02.15f milliSec\n\n", strctMemRandomAccess[i][0].dblLatency);
	}

	//Create worker thread
	intNoOfThreads = 2;

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo = i+1;
		strctTAS[i].lngBlockSize = 1;
		strctTAS[i].intBlockSizeIndex = 0;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeMemPerformance, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// wait for threads to be finished
	for (i=0;i<intNoOfThreads;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo = i+1;
		strctTAS[i].lngBlockSize = 1024;
		strctTAS[i].intBlockSizeIndex = 1;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeMemPerformance, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// wait for threads to be finished
	for (i=0;i<intNoOfThreads;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo = i+1;
		strctTAS[i].lngBlockSize = 1024 * 1024;
		strctTAS[i].intBlockSizeIndex = 2;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeMemPerformance, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// wait for threads to be finished
	for (i=0;i<intNoOfThreads;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// print the results for the experiments conducted
	printf ("\n\n\nMemory - Sequential Access Statistics\nDescription\tThread 1 Values\tThread 2 Values\tFinal Values\n");
	for (i=0;i<3;i++)
	{
		printf ("Block Size: \t%ld Bytes\t%ld Bytes\t%ld Bytes\n", strctMemSeqAccess[i][0].lngBlockSize,
				strctMemSeqAccess[i][1].lngBlockSize, strctMemSeqAccess[i][1].lngBlockSize);
		printf ("No. of Blocks: \t%ld\t%ld\t%ld\n", strctMemSeqAccess[i][0].lngNoOfBlocks,
				strctMemSeqAccess[i][1].lngNoOfBlocks, (strctMemSeqAccess[i][0].lngNoOfBlocks + strctMemSeqAccess[i][1].lngNoOfBlocks));
		printf ("Total Memory Size: \t%ld Bytes\t%ld Bytes\t%ld Bytes\n", strctMemSeqAccess[i][0].lngTotalMemSize,
				strctMemSeqAccess[i][1].lngTotalMemSize, (strctMemSeqAccess[i][0].lngTotalMemSize + strctMemSeqAccess[i][1].lngTotalMemSize));
		printf ("Throughput: \t%7.4f MBPS\t%7.4f MBPS\t%7.4f MBPS\n", strctMemSeqAccess[i][0].dblThroughput,
				strctMemSeqAccess[i][1].dblThroughput, (strctMemSeqAccess[i][0].dblThroughput + strctMemSeqAccess[i][1].dblThroughput));
		printf ("Latency: \t%02.15f milliSec\t%02.15f milliSec\t%02.15f milliSec\n\n", strctMemSeqAccess[i][0].dblLatency,
				strctMemSeqAccess[i][1].dblLatency, (strctMemSeqAccess[i][0].dblLatency + strctMemSeqAccess[i][1].dblLatency)/2);
	}

	printf ("\n\n\nMemory - Random Access Statistics\nDescription\tThread 1 Values\tThread 2 Values\tFinal Values\n");
	for (i=0;i<3;i++)
	{
		printf ("Block Size: \t%ld Bytes\t%ld Bytes\t%ld Bytes\n", strctMemRandomAccess[i][0].lngBlockSize,
				strctMemRandomAccess[i][1].lngBlockSize, strctMemRandomAccess[i][1].lngBlockSize);
		printf ("No. of Blocks: \t%ld\t%ld\t%ld\n", strctMemRandomAccess[i][0].lngNoOfBlocks,
				strctMemRandomAccess[i][1].lngNoOfBlocks, (strctMemRandomAccess[i][0].lngNoOfBlocks + strctMemRandomAccess[i][1].lngNoOfBlocks));
		printf ("Total Memory Size: \t%ld Bytes\t%ld Bytes\t%ld Bytes\n", strctMemRandomAccess[i][0].lngTotalMemSize,
				strctMemRandomAccess[i][1].lngTotalMemSize, (strctMemRandomAccess[i][0].lngTotalMemSize + strctMemRandomAccess[i][1].lngTotalMemSize));
		printf ("Throughput: \t%7.4f MBPS\t%7.4f MBPS\n\t%7.4f MBPS\n", strctMemRandomAccess[i][0].dblThroughput,
				strctMemRandomAccess[i][1].dblThroughput, (strctMemRandomAccess[i][0].dblThroughput + strctMemRandomAccess[i][1].dblThroughput));
		printf ("Latency: \t%02.15f milliSec\t%02.15f milliSec\t%02.15f milliSec\n\n", strctMemRandomAccess[i][0].dblLatency,
				strctMemRandomAccess[i][1].dblLatency, (strctMemRandomAccess[i][0].dblLatency + strctMemRandomAccess[i][1].dblLatency)/2);
	}
}
// function to call Memory Benchmarking with 1 & 2 threads ends over here

// function to call Disk Benchmarking with 1 & 2 threads starts from here
void DiskBenchmarking()
{
	pthread_t arrPTh[2];
	int i = 0;
	int intNoOfThreads=0;

	//Create worker thread
	intNoOfThreads = 1;

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo = i+1;
		strctTAS[i].lngBlockSize = 1;
		strctTAS[i].intBlockSizeIndex = 0;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeDiskPerformance, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// wait for threads to be finished
	for (i=0;i<intNoOfThreads;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo = i+1;
		strctTAS[i].lngBlockSize = 1024;
		strctTAS[i].intBlockSizeIndex = 2;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeDiskPerformance, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// wait for threads to be finished
	for (i=0;i<intNoOfThreads;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo = i+1;
		strctTAS[i].lngBlockSize = 1024 * 1024;
		strctTAS[i].intBlockSizeIndex = 4;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeDiskPerformance, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// wait for threads to be finished
	for (i=0;i<intNoOfThreads;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// print the results for the experiments conducted
	printf ("\n\n\nDisk - Sequential Access Statistics\nDescription\tThread 1 Values\n");
	for (i=0;i<6;i++)
	{
		if (strctDiskSeqAccess[i][0].chrOperation=='R')
		{
			printf ("Operation: \tRead\n");
		}
		else
		{
			printf ("Operation: \tWrite\n");
		}
		printf ("Block Size: \t%ld Bytes\n", strctDiskSeqAccess[i][0].lngBlockSize);
		printf ("No. of Blocks: \t%ld\n", strctDiskSeqAccess[i][0].lngNoOfBlocks);
		printf ("Total Memory Size: \t%ld Bytes\n", strctDiskSeqAccess[i][0].lngTotalMemSize);
		printf ("Throughput: \t%7.4f MBPS\n", strctDiskSeqAccess[i][0].dblThroughput);
		printf ("Latency: \t%02.15f milliSec\n\n\n", strctDiskSeqAccess[i][0].dblLatency);
	}

	printf ("\n\n\nDisk - Random Access Statistics\nDescription\tThread 1 Values\n");
	for (i=0;i<6;i++)
	{
		if (strctDiskRandomAccess[i][0].chrOperation=='R')
		{
			printf ("Operation: \tRead\n");
		}
		else
		{
			printf ("Operation: \tWrite\n");
		}
		printf ("Block Size: \t%ld Bytes\n", strctDiskRandomAccess[i][0].lngBlockSize);
		printf ("No. of Blocks: \t%ld\n", strctDiskRandomAccess[i][0].lngNoOfBlocks);
		printf ("Total Memory Size: \t%ld Bytes\n", strctDiskRandomAccess[i][0].lngTotalMemSize);
		printf ("Throughput: \t%7.4f MBPS\n", strctDiskRandomAccess[i][0].dblThroughput);
		printf ("Latency: \t%02.15f milliSec\n\n\n", strctDiskRandomAccess[i][0].dblLatency);
	}

	//Create worker thread
	intNoOfThreads = 2;

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo = i+1;
		strctTAS[i].lngBlockSize = 1;
		strctTAS[i].intBlockSizeIndex = 0;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeDiskPerformance, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// wait for threads to be finished
	for (i=0;i<intNoOfThreads;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo = i+1;
		strctTAS[i].lngBlockSize = 1024;
		strctTAS[i].intBlockSizeIndex = 2;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeDiskPerformance, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// wait for threads to be finished
	for (i=0;i<intNoOfThreads;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// loop to create threads
	for (i=0;i<intNoOfThreads;i++)
	{
		// initialize the arguments to be passed to threading function in structure
		strctTAS[i].intThreadNo = i+1;
		strctTAS[i].lngBlockSize = 1024 * 1024;
		strctTAS[i].intBlockSizeIndex = 4;

		// create the thread, call the respective function with appropriate arguments  and check creation status
		if (pthread_create(&arrPTh[i], NULL, computeDiskPerformance, (void *)&(strctTAS[i]))!=0)
		{
			printf ("Unable to create thread %d", i+1);
			exit(0);
		}
	}

	// wait for threads to be finished
	for (i=0;i<intNoOfThreads;i++)
	{
		pthread_join(arrPTh[i], NULL);
	}

	// print the results for the experiments conducted
	printf ("\n\n\nDisk - Sequential Access Statistics\nDescription\tThread 1 Values\tThread 2 Values\tFinal Values\n");
	for (i=0;i<6;i++)
	{
		if (strctDiskSeqAccess[i][0].chrOperation=='R')
		{
			printf ("Operation: \tRead\tRead\tRead\n");
		}
		else
		{
			printf ("Operation: \tWrite\tWrite\tWrite\n");
		}
		printf ("Block Size: \t%ld Bytes\t%ld Bytes\t%ld Bytes\n", strctDiskSeqAccess[i][0].lngBlockSize,
				 strctDiskSeqAccess[i][1].lngBlockSize, strctDiskSeqAccess[i][1].lngBlockSize);
		printf ("No. of Blocks: \t%ld\t%ld\t%ld\n", strctDiskSeqAccess[i][0].lngNoOfBlocks,
				strctDiskSeqAccess[i][1].lngNoOfBlocks, (strctDiskSeqAccess[i][0].lngNoOfBlocks + strctDiskSeqAccess[i][1].lngNoOfBlocks));
		printf ("Total Memory Size: \t%ld Bytes\t%ld Bytes\t%ld Bytes\n", strctDiskSeqAccess[i][0].lngTotalMemSize,
				strctDiskSeqAccess[i][1].lngTotalMemSize, (strctDiskSeqAccess[i][0].lngTotalMemSize + strctDiskSeqAccess[i][1].lngTotalMemSize));
		printf ("Throughput: \t%7.4f MBPS\t%7.4f MBPS\t%7.4f MBPS\n", strctDiskSeqAccess[i][0].dblThroughput,
				strctDiskSeqAccess[i][1].dblThroughput, (strctDiskSeqAccess[i][0].dblThroughput + strctDiskSeqAccess[i][1].dblThroughput));
		printf ("Latency: \t%02.15f milliSec\t%02.15f milliSec\t%02.15f milliSec\n\n", strctDiskSeqAccess[i][0].dblLatency,
				strctDiskSeqAccess[i][1].dblLatency, (strctDiskSeqAccess[i][0].dblLatency + strctDiskSeqAccess[i][1].dblLatency) / 2);
	}

	printf ("\n\n\nDisk - Random Access Statistics\nDescription\tThread 1 Values\tThread 2 Values\tFinal Values\n");
	for (i=0;i<6;i++)
	{
		if (strctDiskRandomAccess[i][0].chrOperation=='R')
		{
			printf ("Operation: \tRead\tRead\tRead\n");
		}
		else
		{
			printf ("Operation: \tWrite\tWrite\tWrite\n");
		}
		printf ("Block Size: \t%ld Bytes\t%ld Bytes\t%ld Bytes\n", strctDiskRandomAccess[i][0].lngBlockSize,
				 strctDiskRandomAccess[i][1].lngBlockSize, strctDiskRandomAccess[i][1].lngBlockSize);
		printf ("No. of Blocks: \t%ld\t%ld\t%ld\n", strctDiskRandomAccess[i][0].lngNoOfBlocks,
				strctDiskRandomAccess[i][1].lngNoOfBlocks, (strctDiskRandomAccess[i][0].lngNoOfBlocks + strctDiskRandomAccess[i][1].lngNoOfBlocks));
		printf ("Total Memory Size: \t%ld Bytes\t%ld Bytes\t%ld Bytes\n", strctDiskRandomAccess[i][0].lngTotalMemSize,
				strctDiskRandomAccess[i][1].lngTotalMemSize, (strctDiskRandomAccess[i][0].lngTotalMemSize + strctDiskRandomAccess[i][1].lngTotalMemSize));
		printf ("Throughput: \t%7.4f MBPS\t%7.4f MBPS\t%7.4f MBPS\n", strctDiskRandomAccess[i][0].dblThroughput,
				strctDiskRandomAccess[i][1].dblThroughput, (strctDiskRandomAccess[i][0].dblThroughput + strctDiskRandomAccess[i][1].dblThroughput));
		printf ("Latency: \t%02.15f milliSec\t%02.15f milliSec\t%02.15f milliSec\n\n", strctDiskRandomAccess[i][0].dblLatency,
				strctDiskRandomAccess[i][1].dblLatency, (strctDiskRandomAccess[i][0].dblLatency + strctDiskRandomAccess[i][1].dblLatency) / 2);
	}
}
// function to call Disk Benchmarking with 1 & 2 threads ends over here

// main function to call Benchmarking Experiments starts from here
void main()
{
	int i=0;
	int intReportInterval=1;
	int intRunTime=600;
	int intInput;
	do
	{
		printf ("1 - All Tests\n2 - CPU Tests\n3 - Memory Tests\n4 - Disk Tests\n0 - Exit\n\nEnter your choice: ");
		scanf ("%d", &intInput);
		switch (intInput)
		{
			case 0:
				exit(0);
				break;
			case 1:
				CPUBenchmarking_Part1 ();
				CPUBenchmarking_Part2 (600, 1);
				MemBenchmarking();
				DiskBenchmarking();
				break;
			case 2:
				CPUBenchmarking_Part1 ();
				CPUBenchmarking_Part2 (600, 1);
				break;
			case 3:
				MemBenchmarking();
				break;
			case 4:
				DiskBenchmarking();
				break;
		}
	}
	while (intInput != 0);
}
// main function to call Benchmarking Experiments ends over here
