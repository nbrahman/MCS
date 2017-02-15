The zip file contains following folders
logfiles: Contains one folder, "shared_memory" containing the Shared Memory logfiles (including logfile for 1 TB dataset).

outputs: Contains the sorted output (first and last 10 lines of sorted file) files for all sorting methods (Shared Memory, Hadoop and Spark) with the naming convention as per the assignment. "shared_memory" folder includes the output for 1 TB dataset.

screenshots: Contains three folders, "hadoop", "shared_memory", and "spark". "hadoop" and "spark" folder contain starting and ending screenshots for all the Hadoop and Spark experiments. "shared_memory" folder contains the screenshot for complete experiments including the screenshots for 1 TB dataset. The screenshots are also included into the PA2 Report document to avoid additional overhead.

scripts: "scripts" folder includes the Shell Scripts for Hadoop Prerequisites installation and Hadoop installation. It also includes the script for downloading and extracting Spark.

src: Contains three folders, "hadoop", "shared_memory", and "spark". Respective source codes with relevant makefile or ant files are included into respective folders.

srctxt: Contains source code for Shared Memory in text file format ("ExternalMergeSort.txt", "MergeSort.txt" and "SharedMemorySort.h")

Execution steps for each file are as follows.

----------------------------------------------------------------------------------------------------
Setup & Execution Process for Shared Memory Sort

•	Copy the “ExternalMergeSort.cpp”, “MergeSort.cpp” and “SharedMemorySort.h” files
•	Compile the source files using makefile mechanism
•	Execute “ems” binary with Input File Name to be sorted, Number of Chunks / Blocks and Number of Threads to start the sorting process
----------------------------------------------------------------------------------------------------
Setup & Execution Process for Hadoop Sort
•	Open terminal
•	Copy the HadoopSort.java file to "hadoop" installation folder in system using cp command
•	Change working directory to ../hadoop/sbin
•	Start hadoop by running ./start-all.sh command
•	Run jps command to ensure hadoop status, assuming it works fine then
•	Compile Hadoop_Sort.java using below command
		./bin/hadoop com.sun.tools.javac.Main HadoopSort.java
•	Create directory in HDFS using command
		e.g.: hdfs dfs -mkdir -p input
•	Copy input file to hdfs using command
		hdfs dfs -put input/filename
•	Run program by using below coomand
		./bin/hadoop jar HadoopSort.jar HadoopSort input output
•	Copy output file by using below command
		hdfs dfs -get output/*
•	Check output file using valsort
----------------------------------------------------------------------------------------------------
Setup & Execution Process for Spark Sort
•	Copy the “SparkSort.py” and “SparkSort.scala” in Spark directory
•	Execute the above mentioned Python code using “spark-submit” command with Input File Path (HDFS) and Output Folder Path (HDFS) to start the sorting process.
•	Scala file can be executed through Spark Shell using command “spark-shell -I <scala file name>”. The input file name and output path are hardcoded into program.
----------------------------------------------------------------------------------------------------