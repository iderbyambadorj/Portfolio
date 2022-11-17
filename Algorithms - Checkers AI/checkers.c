/* Program to print and play checker games.

  Skeleton program written by Artem Polyvyanyy, artem.polyvyanyy@unimelb.edu.au,
  September 2021, with the intention that it be modified by students
  to add functionality, as required by the assignment specification.

  Student Authorship Declaration:

  (1) I certify that except for the code provided in the initial skeleton file,
  the program contained in this submission is completely my own individual
  work, except where explicitly noted by further comments that provide details
  otherwise. I understand that work that has been developed by another student,
  or by me in collaboration with other students, or by non-students as a result
  of request, solicitation, or payment, may not be submitted for assessment in
  this subject. I understand that submitting for assessment work developed by
  or in collaboration with other students or non-students constitutes Academic
  Misconduct, and may be penalized by mark deductions, or by other penalties
  determined via the University of Melbourne Academic Honesty Policy, as
  described at https://academicintegrity.unimelb.edu.au.

  (2) I also certify that I have not provided a copy of this work in either
  softcopy or hardcopy or any other form to any other student, and nor will I
  do so until after the marks are released. I understand that providing my work
  to other students, regardless of my intention or any undertakings made to me
  by that other student, is also Academic Misconduct.

  (3) I further understand that providing a copy of the assignment specification
  to any form of code authoring or assignment tutoring service, or drawing the
  attention of others to such services and code that may have been made
  available via such a service, may be regarded as Student General Misconduct
  (interfering with the teaching activities of the University and/or inciting
  others to commit Academic Misconduct). I understand that an allegation of
  Student General Misconduct may arise regardless of whether or not I personally
  make use of such solutions or sought benefit from such actions.

  Signed by: [Ider Byambadorj 1198613]
  Dated:     [2021/10/04]

*/

#include <stdlib.h>
#include <stdio.h>
#include <limits.h>
#include <assert.h>

/* some #define's from my sample solution -----------------------------------*/
#define BOARD_SIZE          8       // board size
#define ROWS_WITH_PIECES    3       // number of initial rows with pieces
#define CELL_EMPTY          '.'     // empty cell character
#define CELL_BPIECE         'b'     // black piece character
#define CELL_WPIECE         'w'     // white piece character
#define CELL_BTOWER         'B'     // black tower character
#define CELL_WTOWER         'W'     // white tower character
#define COST_PIECE          1       // one piece cost
#define COST_TOWER          3       // one tower cost
#define TREE_DEPTH          3       // minimax tree depth
#define COMP_ACTIONS        10      // number of computed actions

/* one type definition from my sample solution ------------------------------*/
typedef unsigned char board_t[BOARD_SIZE][BOARD_SIZE];  // board type


/* #define's ----------------------------------------------------------------*/

#define HEADING "     A   B   C   D   E   F   G   H"
#define HORIZONTALLINE "   +---+---+---+---+---+---+---+---+"
#define VERTICALLINE "|"
#define ENDLINE "====================================="

#define FALSE 0
#define TRUE 1

#define AVALUE 65
#define HVALUE 72

#define BLACKTURN 1
#define WHITETURN 0

#define NOERROR 0
#define ERROR1 1
#define ERROR2 2
#define ERROR3 3
#define ERROR4 4
#define ERROR5 5
#define ERROR6 6
#define CAPTURE 2

#define NEXTACTION 'A'
#define NEXT10ACTION 'P'
#define NOACTION 'N'
#define BLACKWIN 1
#define WHITEWIN 2

#define MAXSIZE 1000


/* type definitions ---------------------------------------------------------*/

// Cell
typedef struct {
    char column;
    int row;
} cell_t;

// Action consisting of 2 cells: source and target
typedef struct {
    cell_t from;
    cell_t dest;
} action_t;

// Node 
typedef struct node node_t;
struct node {
    action_t *currentaction;
    node_t *next;
    node_t *firstchild;
    node_t *lastchild;
    int value;
};

// Array of actions
typedef action_t *actions_t[MAXSIZE];


/* functions ----------------------------------------------------------------*/

void resetboard(board_t board);
void printsetup(board_t board);
void printboard(board_t board);
int calculatecost(board_t board);
cell_t* readactions(actions_t actions);
int validaction(board_t board, action_t action, int turn);
void doaction(board_t board, action_t action, int actionnum);
void printaction(board_t board, action_t action, int actionnum, int computed);
void findaction(board_t board, node_t *parent, int col, int row, int actionnum);
int isfinished(board_t board, int actionnum);
int isthereaction(board_t board, int col, int row, int actionnum);
node_t* dostage1(board_t board, int actionnum);
void boardcopy(board_t boardcopy, board_t board);
int find_max(node_t *parent);
int find_min(node_t *parent);

node_t* make_empty_node();
node_t* insert_at_tail(node_t *parent, action_t *action);
action_t* create_action(char col1, int row1, char col2, int row2);
cell_t* create_cell(char col, int row);
void free_actions(actions_t actions, int actionnum);
void free_nodes(node_t *node);
/*****************************************************************************/


int
main(int argc, char *argv[]) {
    // YOUR IMPLEMENTATION OF STAGES 0-2
    
    board_t board;
    actions_t actions;
    // Initial setup
    resetboard(board);
    printsetup(board);

    // Read input from .txt file and store it in an array 'actions'
    cell_t *input = readactions(actions);
    char nextaction = input->column;
    int count = input->row;
    free(input);
    int i, error;
    if (!count) printf("%s\n", ENDLINE);
    for (i=0; i<count; i++) {
        // checks if the action is valid
        error = validaction(board, *actions[i], i);
        if (!error) {
            // if it is valid, then do that action
            printf("%s\n", ENDLINE);
            doaction(board, *actions[i], i);
            printaction(board, *actions[i], i, FALSE);
            
        } else if (error == ERROR1) {   //  Error Type 1
            printf("ERROR: Source cell is outside of the board.\n");
            return EXIT_FAILURE;
        } else if (error == ERROR2) {   //  Error Type 2
            printf("ERROR: Target cell is outside of the board.\n");
            return EXIT_FAILURE;
        } else if (error == ERROR3) {   //  Error Type 3
            printf("ERROR: Source cell is empty.");
            return EXIT_FAILURE;
        } else if (error == ERROR4) {   //  Error Type 4
            printf("ERROR: Target cell is not empty.\n");
            return EXIT_FAILURE;
        } else if (error == ERROR5) {   //  Error Type 5
            printf("ERROR: Source cell holds opponent's piece/tower.\n");
            return EXIT_FAILURE;
        } else if (error == ERROR6) {   //  Error Type 6
            printf("ERROR: Illegal action.\n");
            return EXIT_FAILURE;
        }
    }
    
    // Stage 1 Implementation
    int actioncount=FALSE, j, gamestate;
    if (nextaction==NEXTACTION) {
        actioncount=TRUE;
    } else if (nextaction==NEXT10ACTION) {
        actioncount=COMP_ACTIONS;
    }
    for (j=0; j<actioncount; j++) {
        // Do stage 1
        node_t* node = dostage1(board, count+j);
        action_t* curraction = node->currentaction;
        // Print the next action
        doaction(board, *curraction, count+j);
        printf("%s\n", ENDLINE);
        printaction(board, *curraction, count+j, TRUE);
        free(curraction);
        free_nodes(node);
        
        // Check if the game is finished
        gamestate = isfinished(board, count+j);
        if (gamestate==BLACKWIN) {
            printf("BLACK WIN!\n");
            return EXIT_SUCCESS;
        } else if (gamestate==WHITEWIN) {
            printf("WHITE WIN!\n");
            return EXIT_SUCCESS;
        }
    }
    
    free_actions(actions, count);

    return EXIT_SUCCESS;            // exit program with the success code
}


/*****************************************************************************/

//  Reset the board to original position
void resetboard(board_t board) {
    int col, row;

    for (col=1; col<=BOARD_SIZE; col++) {
        for (row=1; row<=BOARD_SIZE; row++) {
            
            if ((col%2==1 && row%2==0) || (col%2==0 && row%2==1)) {
                
                if (row<=ROWS_WITH_PIECES) {
                    board[col-1][row-1] = CELL_WPIECE;

                } else if (row>(BOARD_SIZE-ROWS_WITH_PIECES)) {
                    board[col-1][row-1] = CELL_BPIECE;

                } else {
                    board[col-1][row-1] = CELL_EMPTY;
                }
            } else {
                board[col-1][row-1] = CELL_EMPTY;
            }
        }
    }
}


/*****************************************************************************/

//  Prints the board - Stage 0 - Board pieces, board size
void printsetup(board_t board) {
    printf("BOARD SIZE: %dx%d\n", BOARD_SIZE, BOARD_SIZE);
    printf("#BLACK PIECES: 12\n");
    printf("#WHITE PIECES: 12\n");
    printboard(board);
}


/*****************************************************************************/

//  Prints the board - Stage 0 - without any additional information
//  Helper function for printsetup()
void printboard(board_t board) {
    int row, col;
    printf("%s\n", HEADING);
    printf("%s\n", HORIZONTALLINE);
    for (row=0; row<BOARD_SIZE; row++) {
        printf(" %d ", row+1);
        for (col=0; col<BOARD_SIZE; col++) {
            printf("%s", VERTICALLINE);
            printf(" %c ", board[col][row]);
        }
        printf("%s\n%s\n", VERTICALLINE, HORIZONTALLINE);
    }
    return;
}


/*****************************************************************************/

//  Calculates the cost of the board using the formula cost=b+3B-w-3W
int calculatecost(board_t board) {
    int cost=0;
    int i, j;
    for (i=0; i<BOARD_SIZE; i++) {
        for (j=0; j<BOARD_SIZE; j++) {
            if (board[i][j] == CELL_BPIECE) {
                cost += COST_PIECE;
            } else if (board[i][j] == CELL_WPIECE) {
                cost -= COST_PIECE;
            } else if (board[i][j] == CELL_BTOWER) {
                cost += COST_TOWER;
            } else if (board[i][j] == CELL_WTOWER) {
                cost -= COST_TOWER;
            }
        }
    }
    return cost;
}


/*****************************************************************************/

//  Reads actions from input and stores it in an array of actions.
//  Returns a cell with column being the Stage 1
//  action and row being the number of actions read
cell_t* readactions(actions_t actions) {
    char col1, col2, col1copy;
    int row1, row2, row1copy;
    int count=0;
    // Read actions and store it in temporary variable col1, col2, row1, row2
    while (scanf("%c%d-%c%d\n", &col1, &row1, &col2, &row2)==4) {
        // Store the action in an array 'actions'
        actions[count] = create_action(col1, row1, col2, row2);
        count++;
        // Copy the variable col1 and row1
        col1copy = col1;
        row1copy = row1;
    }
    // Use the copy of the variables to check if there is
    // any next action identifier (eg, 'A' or 'P')
    if (!(row1copy==row1 && col1copy!=col1)) {
        col1 = NOACTION;
    }

    cell_t *cell = create_cell(col1, count);
    
    return cell;
}


/*****************************************************************************/

//  Checks the action is valid or not.
//  Takes board, action and action number as arguments
//  and returns 0 if the action is valid or returns the error number
//  if the action isn't valid move.
int validaction(board_t board, action_t action, int actionnum) {
    int fromcol = (int)action.from.column;
    int fromrow = action.from.row-1;
    int destcol = (int)action.dest.column;
    int destrow = action.dest.row-1;
    int turn = (actionnum%2==0);

    // Error 1: Source cell is outside of the board.
    if (fromrow > BOARD_SIZE || fromrow < 0) {
        return ERROR1;
    } else if (fromcol < AVALUE || fromcol > HVALUE) {
        return ERROR1;
    }

    // Error 2: Target cell is outside of the board.
    if (destrow >= BOARD_SIZE || destrow < 0) {
        return ERROR2;
    } else if (destcol < AVALUE || destcol > HVALUE) {
        return ERROR2;
    }

    // Error 3: Source cell is empty.
    if (board[fromcol-AVALUE][fromrow]==CELL_EMPTY) {
        return ERROR3;
    }

    // Error 4: Target cell is not empty.
    if (board[destcol-AVALUE][destrow]!=CELL_EMPTY) {
        return ERROR4;
    }

    // Error 5: Source cell holds opponent's piece/tower.
    if (turn == BLACKTURN) {
        if (board[fromcol-AVALUE][fromrow] == CELL_WPIECE) {
            return ERROR5;
        } else if (board[fromcol-AVALUE][fromrow] == CELL_WTOWER) {
            return ERROR5;
        }
    } else {
        if (board[fromcol-AVALUE][fromrow] == CELL_BPIECE) {
            return ERROR5;
        } else if (board[fromcol-AVALUE][fromrow] == CELL_BTOWER) {
            return ERROR5;
        }
    }

    // Error 6: Illegal action.
    if (abs(destcol-fromcol)>CAPTURE) {
        return ERROR6;
    } else if (fromrow==destrow) {
        return ERROR6;
    } else if (fromcol==destcol) {
        return ERROR6;
    }
    if (abs(destcol-fromcol)==CAPTURE && abs(destrow-fromrow)!=CAPTURE) {
        return ERROR6;
    }
    if (abs(destcol-fromcol)!=CAPTURE && abs(destrow-fromrow)==CAPTURE) {
        return ERROR6;
    }
    if (turn == BLACKTURN) {
        if (abs(fromrow - destrow)>CAPTURE) {
            return ERROR6;
        }
        if ((fromrow < destrow)&&board[fromcol][fromrow]!=CELL_BTOWER) {
            return ERROR6;
        }
        if (abs(fromrow-destrow)==CAPTURE) {
            int midcol = (fromcol-AVALUE+destcol-AVALUE)/2;
            int midrow = (fromrow+destrow)/2;
            if (board[midcol][midrow]==CELL_BPIECE){
                return ERROR6;
            } else if (board[midcol][midrow]==CELL_BTOWER) {
                return ERROR6;
            } else if (board[midcol][midrow]==CELL_EMPTY) {
                return ERROR6;
            }
        }
    } else {
        if (abs(destrow - fromrow)>CAPTURE) {
            return ERROR6;
        }
        if ((fromrow > destrow)&&board[fromcol][fromrow]!=CELL_WTOWER) {
            return ERROR6;
        }
        if (abs(fromrow-destrow)==CAPTURE) {
            int midcol = (fromcol-AVALUE+destcol-AVALUE)/2;
            int midrow = (fromrow+destrow)/2;
            if (board[midcol][midrow]==CELL_WPIECE){
                return ERROR6;
            } else if (board[midcol][midrow]==CELL_WTOWER) {
                return ERROR6;
            } else if (board[midcol][midrow]==CELL_EMPTY) {
                return ERROR6;
            }
        }
    }
    // If all conditions have been satisfied, return NOERROR (=0).
    return NOERROR;
}


/*****************************************************************************/

//  Makes the move in board.
//  Takes board, action and action number as arguments
void doaction(board_t board, action_t action, int actionnum) {
    
    // Convert the cells into integer values
    int fromcol = (int)action.from.column - AVALUE;
    int fromrow = action.from.row-1;
    int destcol = (int)action.dest.column - AVALUE;
    int destrow = action.dest.row-1;

    // Transfer the cell to the target cell.
    board[destcol][destrow] = board[fromcol][fromrow];
    if ((actionnum+1)%2==BLACKTURN) {
        // If the row is 1, change into BTOWER
        if (!destrow) {
            board[destcol][destrow] = CELL_BTOWER;
        }
    } else {
        // If the row is 8, change into WTOWER
        if (destrow==BOARD_SIZE-1) {
            board[destcol][destrow] = CELL_WTOWER;
        }
    }
    // If the move is capture, remove the captured piece.
    if (abs(destcol-fromcol)==CAPTURE) {
        board[(fromcol+destcol)/2][(fromrow+destrow)/2] = CELL_EMPTY;
    }
    // Remove the piece from the source cell.
    board[fromcol][fromrow] = CELL_EMPTY;
    return;
}


/*****************************************************************************/

//  Prints the action with the board, cost.
//  Takes board, action, action numbers and integer computed as arguments.
//  Integer computed refers to whether the action is computed in Stage 1 or not.
void printaction(board_t board, action_t action, int actionnum, int computed) {
    
    char col1 = action.from.column;
    char col2 = action.dest.column;
    int row1 = action.from.row;
    int row2 = action.dest.row;

    // If the action is computed one, print the '***'
    if (computed) printf("*** ");

    // Print the action number
    if ((actionnum+1)%2==0) {
        printf("WHITE ACTION #%d: %c%d-%c%d\n", actionnum+1, col1, row1, col2, row2);
    } else {
        printf("BLACK ACTION #%d: %c%d-%c%d\n", actionnum+1, col1, row1, col2, row2);
    }
    // Print the board cost
    printf("BOARD COST: %d\n", calculatecost(board));

    // Print the board
    printboard(board);
    
    return;
}


/*****************************************************************************/

//  Findaction finds all possible moves from a cell and stores it in branches of
//  the tree *parent. Uses linked list.
void findaction(board_t board, node_t *parent, int col, int row, int actionnum) {
    col += AVALUE;
    row++;
    
    // Check all moves starting from North East
    // If the action is valid insert into the list (node_t *parent)
    // If the move isn't valid, free its memory.

    action_t *move1 = create_action((char)col, row, (char)col+1, row-1);
    if (!validaction(board, *move1, actionnum)) {
        parent = insert_at_tail(parent, move1);
    } else free(move1);

    action_t *move2 = create_action((char)col, row, (char)col+2, row-2);
    if (!validaction(board, *move2, actionnum)) {
        parent = insert_at_tail(parent, move2);
    } else free(move2);

    action_t *move3 = create_action((char)col, row, (char)col+1, row+1);
    if (!validaction(board, *move3, actionnum)) {
        parent = insert_at_tail(parent, move3);
    } else free(move3);

    action_t *move4 = create_action((char)col, row, (char)col+2, row+2);
    if (!validaction(board, *move4, actionnum)) {
        parent = insert_at_tail(parent, move4);
    } else free(move4);

    action_t *move5 = create_action((char)col, row, (char)col-1, row+1);
    if (!validaction(board, *move5, actionnum)) {
        parent = insert_at_tail(parent, move5);
    } else free(move5);

    action_t *move6 = create_action((char)col, row, (char)col-2, row+2);
    if (!validaction(board, *move6, actionnum)) {
        parent = insert_at_tail(parent, move6);
    } else free(move6);

    action_t *move7 = create_action((char)col, row, (char)col-1, row-1);
    if (!validaction(board, *move7, actionnum)) {
        parent = insert_at_tail(parent, move7);
    } else free(move7);

    action_t *move8 = create_action((char)col, row, (char)col-2, row-2);
    if (!validaction(board, *move8, actionnum)) {
        parent = insert_at_tail(parent, move8);
    } else free(move8);
}


/*****************************************************************************/

//  Checks whether the game is finished or not.
//  Takes board and action number as arguments and returns 0 if the game isn't
//  finished, or returns 1 if black wins and 2 if white wins.
int isfinished(board_t board, int actionnum) {
    
    int actionfound=FALSE;
    int col, row;

    // Check all cells and if there is any possible action 
    for (col=0; col<BOARD_SIZE; col++) {
        for (row=0; row<BOARD_SIZE; row++) {
            if (isthereaction(board, col, row, actionnum)) {
                actionfound=TRUE;
                break;
            }
        }
        if (actionfound==TRUE) break;
    }
    if (actionfound) {
        return FALSE;
    } else if ((actionnum+1)%2==BLACKTURN) {
        return BLACKWIN;
    } else {
        return WHITEWIN;
    }
}


/*****************************************************************************/

//  Checks if there are any valid moves available from the cell. Returns 1 if
//  there is a possible action or returns 0 if no moves are available.
int isthereaction(board_t board, int col, int row, int actionnum) {
    col += AVALUE;
    
    // Check all actions starting from North East
    // If the move is valid, return TRUE
    // If the move isn't valid check the next move.
    
    action_t *move1 = create_action((char)col, row, (char)col+1, row-1);
    if (!validaction(board, *move1, actionnum)) {
        free(move1);
        return TRUE;
    } else free(move1);
    action_t *move2 = create_action((char)col, row, (char)col+2, row-2);
    if (!validaction(board, *move2, actionnum)) {
        free(move2);
        return TRUE;
    } else free(move2);
    action_t *move3 = create_action((char)col, row, (char)col+1, row+1);
    if (!validaction(board, *move3, actionnum)) {
        free(move3);
        return TRUE;
    } else free(move3);
    action_t *move4 = create_action((char)col, row, (char)col+2, row+2);
    if (!validaction(board, *move4, actionnum)) {
        free(move4);
        return TRUE;
    } else free(move4);
    action_t *move5 = create_action((char)col, row, (char)col-1, row+1);
    if (!validaction(board, *move5, actionnum)) {
        free(move5);
        return TRUE;
    } else free(move5);
    action_t *move6 = create_action((char)col, row, (char)col-2, row+2);
    if (!validaction(board, *move6, actionnum)) {
        free(move6);
        return TRUE;
    } else free(move6);
    action_t *move7 = create_action((char)col, row, (char)col-1, row-1);
    if (!validaction(board, *move7, actionnum)) {
        free(move7);
        return TRUE;
    } else free(move7);
    action_t *move8 = create_action((char)col, row, (char)col-2, row-2);
    if (!validaction(board, *move8, actionnum)) {
        free(move8);
        return TRUE;
    } else free(move8);
    return FALSE;
}


/*****************************************************************************/

//  Implements Stage 1. Computes and returns the action that player should make
//  in the next move. Uses linked lists as trees to compute next move.
//  Returns the node_t tree which contains all possible moves.
node_t* dostage1(board_t board, int actionnum) {
    board_t board1, board2, board3;
    
    // Create empty list
    node_t *node;
    node = make_empty_node();

    // Copy the board into board1
    boardcopy(board1, board);
    int col, row;
    
    // Check all possible actions and add into the list - depth of 1
    for (row=0; row<BOARD_SIZE; row++) {
        for (col=0; col<BOARD_SIZE; col++) {
            findaction(board1, node, col, row, actionnum);
        }
    }

    int i, j, k, u;
    node_t *secondchild, *thirdchild;
    int finished = FALSE;
    node_t *child = node->firstchild;

    // Check all moves from the first move
    while (child!=NULL) {
        
        // Make the move
        doaction(board1, *(child->currentaction), actionnum);
        for (i=0; i<BOARD_SIZE; i++) {
            for (j=0; j<BOARD_SIZE; j++) {
                // Check all possible from the move and add into the list
                // Depth of 2
                findaction(board1, child, j, i, actionnum+1);
            }
        }

        // Check all moves from the second move
        boardcopy(board2, board1);
        secondchild = child->firstchild;
        while (secondchild!=NULL) {
            doaction(board2, *(secondchild->currentaction), actionnum+1);
            for (k=0; k<BOARD_SIZE; k++) {
                for (u=0; u<BOARD_SIZE; u++) {
                    // Check all possible from the move and add into the list
                    // Depth of 3
                    findaction(board2, secondchild, u, k, actionnum+2);
                }
            }
            
            boardcopy(board3, board2);
            thirdchild = secondchild->firstchild;

            // Calculate costs of all leaf boards.
            while (thirdchild!=NULL) {
                doaction(board3, *(thirdchild->currentaction), actionnum+1);
                thirdchild->value = calculatecost(board3);

                // If the board is finished, its value should be either
                // INT_MAX or INT_MIN
                finished=isfinished(board3, actionnum+2);
                if (finished==BLACKWIN) {
                    thirdchild->value = INT_MAX;
                } else if (finished==WHITEWIN) {
                    thirdchild->value = INT_MIN;
                }
                finished = FALSE;

                thirdchild = thirdchild->next;
                boardcopy(board3, board2);
            }

            // First implementation of minimax decision tree
            // Depth of 3
            if (secondchild->firstchild==NULL) {
                secondchild->value = calculatecost(board2);
            } else if ((actionnum+1)%2==BLACKTURN) {
                secondchild->value = find_max(secondchild);
            } else if ((actionnum+1)%2==WHITETURN) {
                secondchild->value = find_min(secondchild);
            }
            
            // If the board is finished, its value should be either 
            // INT_MAX or INT_MIN
            finished = isfinished(board2, actionnum+1);
            if (finished==BLACKWIN) {
                secondchild->value = INT_MAX;
                break;
            } else if (finished==WHITEWIN) {
                secondchild->value = INT_MIN;
                break;
            }
            finished = FALSE;
            secondchild = secondchild->next;
            boardcopy(board2, board1);
        }

        // Second implementation of the minimax rule
        // Depth of 2
        if (child->firstchild==NULL) {
            child->value = calculatecost(board1);
        } else if ((actionnum+1)%2==BLACKTURN) {
            child->value = find_min(child);
        } else if ((actionnum+1)%2==WHITETURN) {
            child->value = find_max(child);
        }
        
        child = child->next;
        boardcopy(board1, board);
    }
    // Third implementation of the minimax rule
    // Depth of 1
    if ((actionnum+1)%2==BLACKTURN) {
        node->value = find_max(node);
    } else if ((actionnum+1)%2==WHITETURN) {
        node->value = find_min(node);
    }
    
    // Find the action that the player should take
    child = node->firstchild;
    int found=FALSE;
    while (!found) {
        if (child->value == node->value) {
            node->currentaction = child->currentaction;
            found=TRUE;
        }
        child = child->next;
    }
    
    // Return the tree
    return node;
}


/*****************************************************************************/

//  Copies the board into boardcopy
void boardcopy(board_t boardcopy, board_t board) {
    int i, j;
    for (i=0; i<BOARD_SIZE; i++) {
        for (j=0; j<BOARD_SIZE; j++) {
            boardcopy[i][j] = board[i][j];
        }
    }
}


/*****************************************************************************/

//  Finds the maximum value of node_t children of node_t parent.
int find_max(node_t *parent) {
    assert(parent!=NULL);
    assert(parent->firstchild!=NULL);
    node_t *new = parent->firstchild;
    int max1 = new->value;
    while (new!=NULL) {
        if (new->value>max1) {
            max1 = new->value;
        }
        new = new->next;
    }
    free(new);
    return max1;
}


/*****************************************************************************/

//  Finds the minimum value among node_t children of node_t parent
int find_min(node_t *parent) {
    assert(parent!=NULL);
    assert(parent->firstchild!=NULL);
    node_t *new = parent->firstchild;
    int min1 = new->value;
    while (new!=NULL) {
        if (new->value<min1) {
            min1 = new->value;
        }
        new = new->next;
    }
    free(new);
    return min1;
}


/*****************************************************************************/


//  Adapted version of the make_empty_list function by Alistair Moffat:
//  https://people.eng.unimelb.edu.au/ammoffat/ppsaa/c/listops.c
//  Data types and variable names changed.
node_t* make_empty_node() {
    node_t *node;
    node = (node_t*)malloc(sizeof(*node));
    assert(node!=NULL);
    node->currentaction = NULL;
    node->firstchild = node->next = node->lastchild = NULL;
    return node;
}

//  Adapted version of the insert_at_tail function by Alistair Moffat:
//  https://people.eng.unimelb.edu.au/ammoffat/ppsaa/c/listops.c
//  Data types and variable names changed.
node_t* insert_at_tail(node_t *parent, action_t *action) {
    node_t *new;
    new = (node_t*)malloc(sizeof(*new));
    assert(new!=NULL);
    new->currentaction = action;
    new->firstchild = new->next = NULL;
    if (parent->firstchild==NULL) {
        parent->firstchild = new;
        parent->lastchild = new;
    } else {
        parent->lastchild->next = new;
        parent->lastchild = new;
    }
    return parent;
}

//  Adapted version of the make_empty_list function by Alistair Moffat:
//  https://people.eng.unimelb.edu.au/ammoffat/ppsaa/c/listops.c
//  Data types and variable names changed.
action_t* create_action(char col1, int row1, char col2, int row2) {
    action_t *action;
    action = (action_t*)malloc(sizeof(*action));
    assert(action!=NULL);
    action->from.column = col1;
    action->from.row = row1;
    action->dest.column = col2;
    action->dest.row = row2;
    return action;
}

//  Adapted version of the make_empty_list function by Alistair Moffat:
//  https://people.eng.unimelb.edu.au/ammoffat/ppsaa/c/listops.c
//  Data types and variable names changed.
cell_t* create_cell(char col, int row) {
    cell_t* cell;
    cell = (cell_t*)malloc(sizeof(*cell));
    assert(cell!=NULL);
    cell->column = col;
    cell->row = row;
    return cell;
}

//  Frees all the actions in an array 'actions'
void free_actions(actions_t actions, int actionnum) {
    int i;
    if (actions[0]!=NULL) {
        for (i=0; i<actionnum; i++) {
            free(actions[i]);
        }
    }
    return;
}

//  Free memories in tree
//  Adapted version of the free_list function by Alistair Moffat:
//  https://people.eng.unimelb.edu.au/ammoffat/ppsaa/c/listops.c
//  Data types, variable names and structure of the function changed.
void free_nodes(node_t *node) {
    node_t *curr, *prev;
    assert(node!=NULL);
    curr = node->firstchild;
    while (curr!=NULL) {
        while (curr->firstchild!=NULL) {
            while (curr->firstchild->firstchild!=NULL) {
                prev = curr->firstchild->firstchild;
                curr->firstchild->firstchild = curr->firstchild->firstchild->next;
                free(prev);
            }
            prev = curr->firstchild;
            curr->firstchild = curr->firstchild->next;
            free(prev);
        }
        prev = curr;
        curr = curr->next;
        free(prev);
    }
    free(node);
}

/* THE END -------------------------------------------------------------------*/

/* Algorithms are fun! */
