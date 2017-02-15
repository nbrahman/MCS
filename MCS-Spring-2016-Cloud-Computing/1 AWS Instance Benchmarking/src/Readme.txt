Run Instructions:

All the experiments are part of one single C file with a bit of user interaction.

To compile the source file:
	gcc benchmarking.c -o benchmarking -lpthread

	or

	<makefile command>

To run all experiments in one go:
		./benchmarking

The main() function displays a console based menu asking user to select the type of test ot be run. 
1 - All Tests
2 - CPU
3 - Memory
4 - Disk
0 - Exit

Depending upon the choice entered by user, main() function calls each or either of the 4 functions (Experiment 1a, 1b, 2 & 3) one after another. These functions creates threads and calls the main functions respectively. The results are displayed on the screen after completion of each experiment.

Once a test is completed, again console based menu will be displayed to user for the next choice till the time user selects 0 as choice and exits the program.

The "makefile" is already provided to compile the source code.