/* Program to assist in the challenge of solving sudoku puzzles.

   Skeleton program written by Alistair Moffat, ammoffat@unimelb.edu.au,
   August 2021, with the intention that it be modified by students
   to add functionality, as required by the assignment specification.

   Student Authorship Declaration:

   (1) I certify that except for the code provided in the initial skeleton
   file, the  program contained in this submission is completely my own
   individual work, except where explicitly noted by further comments that
   provide details otherwise.  I understand that work that has been developed
   by another student, or by me in collaboration with other students, or by
   non-students as a result of request, solicitation, or payment, may not be
   submitted for assessment in this subject.  I understand that submitting for
   assessment work developed by or in collaboration with other students or
   non-students constitutes Academic Misconduct, and may be penalized by mark
   deductions, or by other penalties determined via the University of
   Melbourne Academic Honesty Policy, as described at
   https://academicintegrity.unimelb.edu.au.

   (2) I also certify that I have not provided a copy of this work in either
   softcopy or hardcopy or any other form to any other student, and nor will I
   do so until after the marks are released. I understand that providing my
   work to other students, regardless of my intention or any undertakings made
   to me by that other student, is also Academic Misconduct.

   (3) I further understand that providing a copy of the assignment
   specification to any form of code authoring or assignment tutoring service,
   or drawing the attention of others to such services and code that may have
   been made available via such a service, may be regarded as Student General
   Misconduct (interfering with the teaching activities of the University
   and/or inciting others to commit Academic Misconduct).  I understand that
   an allegation of Student General Misconduct may arise regardless of whether
   or not I personally make use of such solutions or sought benefit from such
   actions.

   Signed by: [Ider Byambadorj 1198613]
   Dated:     [03/09/2021]

*/

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

/* these #defines provided as part of the initial skeleton */

#define NDIM 3		/* sudoku dimension, size of each inner square */
#define NDIG (NDIM*NDIM)
			/* total number of values in each row */
#define NGRP 3		/* number of sets each cell is a member of */
#define NSET (NGRP*NDIG)
			/* total number of sets in the sudoku */
#define NCLL (NDIG*NDIG)
			/* total number of cells in the sudoku */

#define ERROR	(-1)	/* error return value from some functions */

/* these global constant arrays provided as part of the initial skeleton,
   you may use them in your code but must not alter them in any way,
   regard them as being completely fixed. They describe the relationships
   between the cells in the sudoku and provide a basis for all of the
   sudoku processing loops */

/* there are 27 different different sets of elements that need to be
   checked against each other, this array converts set numbers to cells,
   that's why its called s2c */
int s2c[NSET][NDIM*NDIM] = {
	/* the first group of nine sets describe the sudoku's rows */
	{  0,  1,  2,  3,  4,  5,  6,  7,  8 },
	{  9, 10, 11, 12, 13, 14, 15, 16, 17 },
	{ 18, 19, 20, 21, 22, 23, 24, 25, 26 },
	{ 27, 28, 29, 30, 31, 32, 33, 34, 35 },
	{ 36, 37, 38, 39, 40, 41, 42, 43, 44 },
	{ 45, 46, 47, 48, 49, 50, 51, 52, 53 },
	{ 54, 55, 56, 57, 58, 59, 60, 61, 62 },
	{ 63, 64, 65, 66, 67, 68, 69, 70, 71 },
	{ 72, 73, 74, 75, 76, 77, 78, 79, 80 },
	/* the second group of nine sets describes the sudoku's columns */
	{  0,  9, 18, 27, 36, 45, 54, 63, 72 },
	{  1, 10, 19, 28, 37, 46, 55, 64, 73 },
	{  2, 11, 20, 29, 38, 47, 56, 65, 74 },
	{  3, 12, 21, 30, 39, 48, 57, 66, 75 },
	{  4, 13, 22, 31, 40, 49, 58, 67, 76 },
	{  5, 14, 23, 32, 41, 50, 59, 68, 77 },
	{  6, 15, 24, 33, 42, 51, 60, 69, 78 },
	{  7, 16, 25, 34, 43, 52, 61, 70, 79 },
	{  8, 17, 26, 35, 44, 53, 62, 71, 80 },
	/* the last group of nine sets describes the inner squares */
	{  0,  1,  2,  9, 10, 11, 18, 19, 20 },
	{  3,  4,  5, 12, 13, 14, 21, 22, 23 },
	{  6,  7,  8, 15, 16, 17, 24, 25, 26 },
	{ 27, 28, 29, 36, 37, 38, 45, 46, 47 },
	{ 30, 31, 32, 39, 40, 41, 48, 49, 50 },
	{ 33, 34, 35, 42, 43, 44, 51, 52, 53 },
	{ 54, 55, 56, 63, 64, 65, 72, 73, 74 },
	{ 57, 58, 59, 66, 67, 68, 75, 76, 77 },
	{ 60, 61, 62, 69, 70, 71, 78, 79, 80 },
};


/* there are 81 cells in a dimension-3 sudoku, and each cell is a
   member of three sets, this array gets filled by the function 
   fill_c2s(), based on the defined contents of the array s2c[][] */
int c2s[NCLL][NGRP];

void
fill_c2s() {
	int s=0, d=0, c;
	for ( ; s<NSET; s++) {
		/* record the first set number each cell is part of */
		for (c=0; c<NDIM*NDIM; c++) {
			c2s[s2c[s][c]][d] = s;
		}
		if ((s+1)%(NGRP*NDIM) == 0) {
			d++;
		}
	}
#if 0
	/* this code available here if you want to see the array
	   cs2[][] that gets created, just change that 0 two lines back
	   to a 1 and recompile */
	for (c=0; c<NCLL; c++) {
		printf("cell %2d: sets ", c);
		for (s=0; s<NGRP; s++) {
			printf("%3d", c2s[c][s]);
		}
		printf("\n");
	}
	printf("\n");
#endif
	return;
}

/* find the row number a cell is in, counting from 1
*/
int
rownum(int c) {
	return 1 + (c/(NDIM*NDIM));
}

/* find the column number a cell is in, counting from 1
*/
int
colnum(int c) {
	return 1 + (c%(NDIM*NDIM));
}

/* find the minor square number a cell is in, counting from 1
*/
int
sqrnum(int c) {
	return 1 + 3*(c/NSET) + (c/NDIM)%NDIM;
}

/* If you wish to add further #defines, put them below this comment,
   then prototypes for the functions that you add

   The only thing you should alter above this line is to complete the
   Authorship Declaration 
*/


/****************************************************************/


#define HORIZONTALLINE "------+-------+------" 
		/* Horizontal line to separate the sudoku squares */
#define VERTICALLINE " | "
		/* Vertical line to separate the sudoku squares */
#define FALSE 0
#define TRUE 1

/****************************************************************/


void takeinput(int array[]);
void printsudoku(int array[]);
void sortarray(int array[], int length);
int gridcheck(int array[]);
int strategy1(int array[]);


/****************************************************************/


/* main program controls all the action
*/

/* 	Sudoku assistant - takes sudoku as input and solves it using
	strategy 1 */
int
main(int argc, char *argv[]) {
	/* all done, so pack up bat and ball and head home */

	int sudoku[NCLL]; 
		/* one-dimensional array that holds all sudoku cells */

	int impossible = FALSE;
		/* integer to indicate whether the sudoku is solvable or not */

	takeinput(sudoku);
	
	printf("\n");
	printsudoku(sudoku);
	printf("\n");
	
	fill_c2s();
	if (!gridcheck(sudoku)) {
		return 0;
	}

	while (strategy1(sudoku)) {
		printf("\n");
	}
	printsudoku(sudoku);
	int cell;
	for (cell=0; cell<NCLL; cell++) {
		if (!sudoku[cell]) {
			impossible = TRUE;
			break;
		}
	}
	if (!impossible) {
		printf("\nta daa!!!\n");
	}


	return 0;
}


/****************************************************************/


/* 	Takes the input and stores it in a one-dimensional
	array */
void takeinput(int array[]) {
	int next, i;
	for (i=0; i<NCLL; i++) {
		if (scanf("%d", &next) == 1) {
			array[i] = next;
		}
	}
}


/****************************************************************/


/* 	Stage 1 - Prints out the sudoku (argument) in a 
	neatly-formatted representation and counts the number of 
	unknowns */
void printsudoku(int array[]) {
	int i, unknown=0, j;
	for (i=0; i<NCLL; i++) {
		
		if (!array[i]) {
			printf(".");
			unknown++;
		} else {
			printf("%d", array[i]);
		}
		
		j= i+1; // cell number, counting from 1.
		
		/* sudoku format for printing */
		if (j%NDIM==0 && j%NDIG!=0) {
			printf("%s", VERTICALLINE);
		} else if (j%NDIM==0 && j%NSET!=0) {
			printf("\n");
		} else if (j%NSET==0 && j!=NCLL) {
			printf("\n%s\n", HORIZONTALLINE);
		} else if (j==NCLL){
			printf("\n\n");
		} else {
			printf(" ");
		}
	}

	printf("%2d cells are unknown\n", unknown);
}


/****************************************************************/


/* 	Helper function - Takes an array and its length as arguments and
	sorts the array in ascending order */
void sortarray(int array[], int length) {
	int curr, next, temp;
	for (curr=0; curr<length; curr++) {
		for (next=curr+1; next<length; next++) {
			if (array[curr] > array[next]) {
				temp = array[curr];
				array[curr] = array[next];
				array[next] = temp;
			}
		}
	}
}


/****************************************************************/


/* 	Stage 2 - this function checks for errors and returns 1 if 
	there is no error and returns 0 if there is an error */
int gridcheck(int array[]) {
	int i, j, current=-1;
	int sorted[NDIG];
	int diff=0, total=0;
	
	for (i=0; i<NSET; i++) {
		// Copy the set into array and sort the array
		for (j=0; j<NDIG; j++) {
			sorted[j] = array[s2c[i][j]];
		}
		sortarray(sorted, NDIG);

		// Starting point = 0
		int res = 0;
		int cell, dup;
		
		for (cell=0; cell<NDIG; cell++) {
			int errors=1;
			
			// check for duplicates in the sorted array
			if (sorted[cell]!=res) {
				for (dup=cell+1; dup<NDIG;dup++) {
					if (sorted[dup] == sorted[cell]) {
						errors++;
					}
				}
				res = sorted[cell];
				if (errors!=1) {
					if (i<NDIG) {
						printf("set %2d (row %d): %d instances of %d\n",
							i, i+1, errors, sorted[cell]);
					} else if (i < 2*NDIG) {
						printf("set %2d (col %d): %d instances of %d\n",
							i, i-NDIG+1, errors, sorted[cell]);
					} else {
						printf("set %2d (sqr %d): %d instances of %d\n",
							i, i-2*NDIG+1, errors, sorted[cell]);
					}
					if (current==i) {
						diff--;
					}
					diff++;
					total++;
					current = i;
				}
			}
		}
	}
	if (total) {
		printf("\n%d different sets have violations\n", diff);
		printf("%d violations in total\n", total);
		return 0;
	}

	return 1;
}


/****************************************************************/


/* 	Strategy 1 - iterates over each cell and if there is only
	one possibility, changes the cell value. The function
	returns 1 if the change has been made, and 0 otherwise. */
int strategy1(int array[]) {
	int possible[NDIG];
	int set, count, res, change=0;
	int changes[NCLL][NDIM-1];
	int successful=0;
	int value, i, j, k, p, b;
	for (i=0; i<NCLL; i++) {
		
		// Iterates over each cell - 81
		for (value=0; value<NDIG; value++) {
			possible[value] = value+1;
		}

		// Cell must be non-zero
		if (!array[i]) {
			
			// Iterates over each possible/array values
			for (j=0; j<NDIG; j++) {
				
				// Iterates over each set values
				for (k=0; k<NDIM; k++) {
					set = c2s[i][k];
					for (p=0; p<NDIG; p++) {
						if (array[s2c[set][p]] == possible[j]) {
							possible[j] = 0;
							break;
						}
					}
					if (!possible[j]) {
						break;
					}
				}
			}
		}
		count = 0;
		
		// Changes the starting point to the non-zero number.
		for (b=0; b<NDIG; b++) {
			if (!possible[b]) {
				count++;
			} else {
				res = possible[b];
			}
		}

		// If there is only one option, print it
		if (count==8) {
			if (!successful) {
				printf("strategy one\n");
				successful = 1;
			}
			printf("row %d col %d must be %d\n", rownum(i), colnum(i), res);
			changes[change][0] = i;
			changes[change][1] = res;
			change++;
		}
	}

	int q;
	// modify the original array
	for (q=0; q<change; q++) {
		array[changes[q][0]] = changes[q][1];
	}

	if (change) {
		return 1;
	}
	return 0;
}


/****************************************************************/

/* Algorithms are fun! */
