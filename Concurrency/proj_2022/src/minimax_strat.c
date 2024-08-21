/* vim: :se ai :se sw=4 :se ts=4 :se sts :se et */

/*H**********************************************************************
 *
 *    This is a skeleton to guide development of Othello engines that can be used
 *    with the Ingenious Framework and a Tournament Engine. 
 *
 *    The communication with the referee is handled by an implementaiton of comms.h,
 *    All communication is performed at rank 0.
 *
 *    Board co-ordinates for moves start at the top left corner of the board i.e.
 *    if your engine wishes to place a piece at the top left corner, 
 *    the "gen_move_master" function must return "00".
 *
 *    The match is played by making alternating calls to each engine's 
 *    "gen_move_master" and "apply_opp_move" functions. 
 *    The progression of a match is as follows:
 *        1. Call gen_move_master for black player
 *        2. Call apply_opp_move for white player, providing the black player's move
 *        3. Call gen move for white player
 *        4. Call apply_opp_move for black player, providing the white player's move
 *        .
 *        .
 *        .
 *        N. A player makes the final move and "game_over" is called for both players
 *    
 *    IMPORTANT NOTE:
 *        Write any (debugging) output you would like to see to a file. 
 *        	- This can be done using file fp, and fprintf()
 *        	- Don't forget to flush the stream
 *        	- Write a method to make this easier
 *        In a multiprocessor version 
 *        	- each process should write debug info to its own file 
 *H***********************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <arpa/inet.h>
#include <mpi.h>
#include <time.h>
#include <assert.h>
#include "comms.h"

const int EMPTY = 0;
const int BLACK = 1;
const int WHITE = 2;

const int OUTER = 3;
const int ALLDIRECTIONS[8] = {-11, -10, -9, -1, 1, 9, 10, 11};
const int BOARDSIZE = 100;
const int WIN = 20000;
const int LOSS = -20000;

const int LEGALMOVSBUFSIZE = 65;
const char piecenames[4] = {'.','b','w','?'};

int weights[100] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 20, 0, 10, 10, 10, 10, 0, 20, 0,
                    0, 0, 0, 5, 5, 5, 5, 0, 0, 0,
                    0, 10, 5, 3, 1, 1, 3, 5, 10, 0,
                    0, 10, 5, 1, 7, 7, 1, 5, 10, 0,
                    0, 10, 5, 1, 7, 7, 1, 5, 10, 0,
                    0, 10, 5, 3, 1, 1, 3, 5, 10, 0,
                    0, 0, 0, 5, 5, 5, 5, 0, 0, 0,
                    0, 20, 0, 10, 10, 10, 10, 0, 20, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

const int PLY = 5;

void run_master(int argc, char *argv[]);
int initialise_master(int argc, char *argv[], int *time_limit, int *my_colour, FILE **fp);
void gen_move_master(char *move, int my_colour, FILE *fp);
void apply_opp_move(char *move, int my_colour, FILE *fp);
void game_over();
void run_worker();
void initialise_board();
void free_board();

//All my added functions:
int minimax(int player, int *moves, int numMoves, int *board, int ply);
int maxchoice(int player, int *board, int ply);
int minchoice(int player, int *board, int ply);
int evaluation(int player, int *board);
int *temp_board(int *board);
int has_legal_moves(int player, int *board);
int nexttoplay(int *board, int previousplayer, int printflag);
int opponent_no_fp(int player);
void make_temp_move(int move, int player, int *tempBoard);
void makeflips_for_tempBoard(int move, int player, int *board, int dir);
int *legal_moves_no_fp(int player, int *board);
int legalp_no_fp(int move, int player, int *board);
int wouldflip_no_fp(int move, int player, int *board, int dir);
int findbracketpiece_no_fp(int square, int player, int *board, int dir);
int optimized_evaluation(int player, int *board);
//-------------------------

void legal_moves(int player, int *moves, FILE *fp);
int legalp(int move, int player, FILE *fp);
int validp(int move);
int would_flip(int move, int dir, int player, FILE *fp);
int opponent(int player, FILE *fp);
int find_bracket_piece(int square, int dir, int player, FILE *fp);
int random_strategy(int *moves, int numMoves);
void make_move(int move, int player, FILE *fp);
void make_flips(int move, int dir, int player, FILE *fp);
int get_loc(char* movestring);
void get_move_string(int loc, char *ms);
void print_board(FILE *fp);
char nameof(int piece);
int count(int player, int * board);

int *board;

int main(int argc, char *argv[]) {
	int rank;
	
	MPI_Init(&argc, &argv);
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	
	initialise_board(); //one for each process

	if (rank == 0) {
	    run_master(argc, argv);
	} else {
		run_worker();
	}
	game_over();
}

void run_master(int argc, char *argv[]) {
	char cmd[CMDBUFSIZE];
	char my_move[MOVEBUFSIZE];
	char opponent_move[MOVEBUFSIZE];
	int time_limit;
	int my_colour;
	int running = 0;
	FILE *fp = NULL;

	if (initialise_master(argc, argv, &time_limit, &my_colour, &fp) != FAILURE) {
		running = 1;
	}
	if (my_colour == EMPTY) my_colour = BLACK;
	// Broadcast my_colour
	MPI_Bcast(&my_colour, 1, MPI_INT, 0, MPI_COMM_WORLD);

	while (running == 1) {
		/* Receive next command from referee */
		if (comms_get_cmd(cmd, opponent_move) == FAILURE) {
			fprintf(fp, "Error getting cmd\n");
			fflush(fp);
			running = 0;
			break;
		}

		/* Received game_over message */
		if (strcmp(cmd, "game_over") == 0) {
			running = 0;
			fprintf(fp, "Game over\n");
			fflush(fp);
			break;

		/* Received gen_move message */
		} else if (strcmp(cmd, "gen_move") == 0) {
			// Broadcast running
			MPI_Bcast(&running, 1, MPI_INT, 0, MPI_COMM_WORLD);
			// Broadcast board 
			MPI_Bcast(board, BOARDSIZE, MPI_INT, 0, MPI_COMM_WORLD);

			gen_move_master(my_move, my_colour, fp);
			print_board(fp);

			if (comms_send_move(my_move) == FAILURE) { 
				running = 0;
				fprintf(fp, "Move send failed\n");
				fflush(fp);
				break;
			}

		/* Received opponent's move (play_move mesage) */
		} else if (strcmp(cmd, "play_move") == 0) {
			apply_opp_move(opponent_move, my_colour, fp);
			print_board(fp);

		/* Received unknown message */
		} else {
			fprintf(fp, "Received unknown command from referee\n");
		}
		
	}
	// Broadcast running
	MPI_Bcast(&running, 1, MPI_INT, 0, MPI_COMM_WORLD);
}

int initialise_master(int argc, char *argv[], int *time_limit, int *my_colour, FILE **fp) {
	int result = FAILURE;

	if (argc == 5) { 
		unsigned long ip = inet_addr(argv[1]);
		int port = atoi(argv[2]);
		*time_limit = atoi(argv[3]);

		*fp = fopen(argv[4], "w");
		if (*fp != NULL) {
			fprintf(*fp, "Initialise communication and get player colour \n");
			if (comms_init_network(my_colour, ip, port) != FAILURE) {
				result = SUCCESS;
			}
			fflush(*fp);
		} else {
			fprintf(stderr, "File %s could not be opened", argv[4]);
		}
	} else {
		fprintf(*fp, "Arguments: <ip> <port> <time_limit> <filename> \n");
	}
	
	return result;
}

void initialise_board() {
	int i;
	board = (int *) malloc(BOARDSIZE * sizeof(int));
	for (i = 0; i <= 9; i++) board[i] = OUTER;
	for (i = 10; i <= 89; i++) {
		if (i%10 >= 1 && i%10 <= 8) board[i] = EMPTY; else board[i] = OUTER;
	}
	for (i = 90; i <= 99; i++) board[i] = OUTER;
	board[44] = WHITE; board[45] = BLACK; board[54] = BLACK; board[55] = WHITE;
}

void free_board() {
	free(board);
}

/**
 *   Rank i (i != 0) executes this code 
 *   ----------------------------------
 *   Called at the start of execution on all ranks except for rank 0.
 *   - run_worker should play minimax from its move(s) 
 *   - results should be send to Rank 0 for final selection of a move 
 */
void run_worker() {
	int running = 0;
	int my_colour, my_rank, loc, comm_sz;
	
	MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);
	MPI_Comm_size(MPI_COMM_WORLD, &comm_sz);

	// Broadcast colour
	MPI_Bcast(&my_colour, 1, MPI_INT, 0, MPI_COMM_WORLD);
	// Broadcast running
	MPI_Bcast(&running, 1, MPI_INT, 0, MPI_COMM_WORLD);
	
	while (running == 1) {
		// Broadcast board
		MPI_Bcast(board, BOARDSIZE, MPI_INT, 0, MPI_COMM_WORLD);
		// Generate move

		int *sendCounts = calloc(comm_sz, sizeof(int));
		MPI_Bcast(sendCounts, comm_sz, MPI_INT, 0, MPI_COMM_WORLD);

		int *moves_local = calloc(sendCounts[my_rank], sizeof(int));

		MPI_Scatterv(NULL, NULL, NULL, MPI_DATATYPE_NULL, moves_local, sendCounts[my_rank], MPI_INT, 0, MPI_COMM_WORLD);

		int *tempBoard = temp_board(board);
		int eval = optimized_evaluation(my_colour, tempBoard);
		if(eval == -1) eval = -10000;
		loc = minimax(my_colour, moves_local, sendCounts[my_rank], tempBoard, PLY);
		// loc = random_strategy(moves_local, sendCounts[my_rank]);

		MPI_Gather(&loc, 1, MPI_INT, NULL, 0, MPI_INT, 0, MPI_COMM_WORLD);
		MPI_Gather(&eval, 1, MPI_INT, NULL, 0, MPI_INT, 0, MPI_COMM_WORLD);
		// Broadcast running
		MPI_Bcast(&running, 1, MPI_INT, 0, MPI_COMM_WORLD);
		
	 	free(sendCounts);
		free(moves_local);
	}
	
}

/**
 *  Rank 0 executes this code: 
 *  --------------------------
 *  Called when the next move should be generated 
 *  - gen_move_master should play minimax from its move(s)
 *  - the ranks may communicate during execution 
 *  - final results should be gathered at rank 0 for final selection of a move 
 */
void gen_move_master(char *move, int my_colour, FILE *fp) {
	int loc, comm_sz;
	MPI_Comm_size(MPI_COMM_WORLD, &comm_sz);
	/* generate move */

	int *moves = calloc(LEGALMOVSBUFSIZE, sizeof(int));

	legal_moves(my_colour, moves, fp);
	int *actual_moves = &moves[1];
	
	int numMoves = moves[0];

	int *displacement = calloc(comm_sz, sizeof(int));
	
	int *sendCounts = calloc(comm_sz, sizeof(int));
	
	int sum = 0;
	int i; //Example with numMoves = 7 and comm_sz = 3:
	for(i = 0; i<comm_sz; i++){ // {[0] []} {[2] []} {[4] [] []}
		displacement[i] = sum;// 0 2 4 
		sendCounts[i] = (numMoves + i) / comm_sz; // 2 2 3
		sum += sendCounts[i]; // 2 4 7
	}

	MPI_Bcast(sendCounts, comm_sz, MPI_INT, 0, MPI_COMM_WORLD);

	int *moves_local = calloc(sendCounts[0], sizeof(int));

	MPI_Scatterv(actual_moves, sendCounts, displacement, MPI_INT, moves_local, sendCounts[0], MPI_INT, 0, MPI_COMM_WORLD);

	int *tempBoard = temp_board(board);
	int eval = optimized_evaluation(my_colour, tempBoard);
	if(eval == -1) eval = -10000;
	loc = minimax(my_colour, moves_local, sendCounts[0], tempBoard, PLY);
	// loc = random_strategy(moves_local, sendCounts[0]);

	int locs[comm_sz];
	int evals[comm_sz];

	MPI_Gather(&loc, 1, MPI_INT, locs, 1, MPI_INT, 0, MPI_COMM_WORLD);
	MPI_Gather(&eval, 1, MPI_INT, evals, 1, MPI_INT, 0, MPI_COMM_WORLD);
	
	int maxLoc = 0;
	int maxEval = -20000;
	int j;
	for(j = 0; j < comm_sz; j++){
		if(evals[j] > maxEval){
			maxEval = evals[j];
			maxLoc = locs[j];
		}
	}
	
	if (maxLoc == -1) {
		strncpy(move, "pass\n", MOVEBUFSIZE);
	} else {
		/* apply move */
		get_move_string(maxLoc, move);
		make_move(maxLoc, my_colour, fp);
	}
	free(moves);
	free(displacement);
	free(sendCounts);
}

void apply_opp_move(char *move, int my_colour, FILE *fp) {
	int loc;
	if (strcmp(move, "pass\n") == 0) {
		return;
	}
	loc = get_loc(move);
	make_move(loc, opponent(my_colour, fp), fp);
}

void game_over() {
	free_board();
	MPI_Finalize();
}

void get_move_string(int loc, char *ms) {
	int row, col, new_loc;
	new_loc = loc - (9 + 2 * (loc / 10));
	row = new_loc / 8;
	col = new_loc % 8;
	ms[0] = row + '0';
	ms[1] = col + '0';
	ms[2] = '\n';
	ms[3] = 0;
}

int get_loc(char* movestring) {
	int row, col;
	/* movestring of form "xy", x = row and y = column */ 
	row = movestring[0] - '0'; 
	col = movestring[1] - '0'; 
	return (10 * (row + 1)) + col + 1;
}

void legal_moves(int player, int *moves, FILE *fp) {
	int move, i;
	moves[0] = 0;
	i = 0;
	for (move = 11; move <= 88; move++)
		if (legalp(move, player, fp)) {
		i++;
		moves[i] = move;
	}
	moves[0] = i;
}

int legalp(int move, int player, FILE *fp) {
	int i;
	if (!validp(move)) return 0;
	if (board[move] == EMPTY) {
		i = 0;
		while (i <= 7 && !would_flip(move, ALLDIRECTIONS[i], player, fp)) i++;
		if (i == 8) return 0; else return 1;
	}
	else return 0;
}

int legalp_no_fp(int move, int player, int *board) {
	int i;
	if (!validp(move)) return 0;
	if (board[move]==EMPTY) {
		i=0;
		while (i<=7 && !wouldflip_no_fp(move, player, board, ALLDIRECTIONS[i])) i++;
		if (i==8) return 0; else return 1;
	}   
	else return 0;
}

int wouldflip_no_fp (int move, int player, int *board, int dir) {
	int c;
	c = move + dir;
	if (board[c] == opponent_no_fp(player))
		return findbracketpiece_no_fp(c+dir, player, board, dir);
	else return 0;
}

int findbracketpiece_no_fp(int square, int player, int *board, int dir) {
	while (board[square] == opponent_no_fp(player)) square = square + dir;
	if (board[square] == player) return square;
	else return 0;
}

int validp(int move) {
	if ((move >= 11) && (move <= 88) && (move%10 >= 1) && (move%10 <= 8))
		return 1;
	else return 0;
}

int would_flip(int move, int dir, int player, FILE *fp) {
	int c;
	c = move + dir;
	if (board[c] == opponent(player, fp))
		return find_bracket_piece(c+dir, dir, player, fp);
	else return 0;
}

int find_bracket_piece(int square, int dir, int player, FILE *fp) {
	while (board[square] == opponent(player, fp)) square = square + dir;
	if (board[square] == player) return square;
	else return 0;
}

int opponent(int player, FILE *fp) {
	if (player == BLACK) return WHITE;
	if (player == WHITE) return BLACK;
	fprintf(fp, "illegal player\n"); return EMPTY;
}

int opponent_no_fp(int player) {
	if (player == BLACK) return WHITE;
	if (player == WHITE) return BLACK;
	return EMPTY;
}

int random_strategy(int *moves, int numMoves) {
	int r;
	// int *moves = (int *) malloc(LEGALMOVSBUFSIZE * sizeof(int));
	// memset(moves, 0, LEGALMOVSBUFSIZE);
	
	// legal_moves(my_colour, moves, fp);
	if (numMoves == 0) {
		return -1;
	}

	srand (time(NULL));
	r = moves[(rand() % numMoves)];
	// free(moves);
	return(r);
}

int minimax(int player, int *moves, int numMoves, int *board, int ply) {
	int i, max, ntm, *tempBoard, newscore = 0, bestmove = 0;
	if (numMoves == 0) {
		return -1;
	}

	max = LOSS - 1;  /* any legal move will exceed this score */
	
	for (i=0; i < numMoves; i++) {
		tempBoard = temp_board(board);
		make_temp_move(moves[i], player, tempBoard);
		ntm = nexttoplay(tempBoard, player, 0);
		if (ntm == 0) {  /* game over, so determine winner */
				newscore = optimized_evaluation(player, tempBoard);
				if (newscore > 0) newscore = WIN; /* a win for player */
				if (newscore < 0) newscore = LOSS; /* a win for opp */
		}
		if (ntm == player){   /* opponent cannot move */
			newscore = maxchoice(player, tempBoard, ply-1);
		}
		if (ntm == opponent_no_fp(player)){
			newscore = minchoice(player, tempBoard, ply-1);
		}
		if (newscore > max) {
			max = newscore;
			bestmove = moves[i];  /* a better move found */
		}
		free(tempBoard);
	}
	return(bestmove);
}

int minchoice (int player, int *board, int ply) {
	int i, min, ntm, newscore = 0, *newboard;
	int *moves_local = legal_moves_no_fp(opponent_no_fp(player), board);
	int numMoves = moves_local[0];

	if (ply == 0) return(optimized_evaluation(player, board));
	min = WIN+1;
	for (i=1; i <= numMoves; i++) {
		newboard = temp_board(board); 
		make_temp_move(moves_local[i], opponent_no_fp(player), newboard);
		ntm = nexttoplay(newboard, opponent_no_fp(player), 0);
		if (ntm == 0) {
			newscore = optimized_evaluation(player, newboard);
			if (newscore > 0) newscore = WIN;
			if (newscore < 0) newscore = LOSS;
		}
		if (ntm == player) 
		newscore = maxchoice(player, newboard, ply-1);
		if (ntm == opponent_no_fp(player))
		newscore = minchoice(player, newboard, ply-1);
		if (newscore < min) min = newscore;
		free(newboard);
	}
	return(min);
}

int maxchoice (int player, int *board, int ply) {
	int i, max, ntm, newscore = 0, *newboard;
	int *moves_local = legal_moves_no_fp(player, board);
	int numMoves = moves_local[0];

	if (ply == 0) return(optimized_evaluation(player, board));
	max = LOSS - 1;
	for (i=1; i <= numMoves; i++) {
		newboard = temp_board(board);
		make_temp_move(moves_local[i], player, newboard);
		ntm = nexttoplay(newboard, player, 0);
		if (ntm == 0) {
			newscore = optimized_evaluation(player, newboard);
			if (newscore > 0) newscore = WIN;
			if (newscore < 0) newscore = LOSS;
		}
		if (ntm == player) 
		newscore = maxchoice(player, newboard, ply-1);
		if (ntm == opponent_no_fp(player))
		newscore = minchoice(player, newboard, ply-1);
		if (newscore > max) max = newscore;
		free(newboard);
	}
	return(max);
}

int evaluation (int player, int *board) {
	int i, pcnt, ocnt, opp;
	pcnt=0; ocnt = 0;
	opp = opponent_no_fp(player); 
	for (i=1; i<=88; i++) {
		if (board[i]==player) {
			pcnt++;
		}
		if (board[i]==opp) {
			ocnt++;
		}
	}
	return (pcnt-ocnt);
}

int optimized_evaluation(int player, int *board) {
    int pcnt, ocnt, move;
    pcnt = 0;
    ocnt = 0;
    for (move = 11; move <= 88; move++)
    {
        if (legalp_no_fp(move, player, board)) {
            pcnt = weights[move] + pcnt;
		}
        if (legalp_no_fp(move, opponent_no_fp(player), board)) {
            ocnt = weights[move] + ocnt;
		}
    }
    return (pcnt-ocnt);
}

int *temp_board (int *board) {
  int i, *tempBoard;
  tempBoard = (int *)malloc(BOARDSIZE * sizeof(int));
  for (i=0; i<BOARDSIZE; i++) tempBoard[i] = board[i];
  return tempBoard;
}

/* has_legal_moves returns 1 if a player has at least one legal
   move and 0 otherwise.
*/
int has_legal_moves (int player, int *board) {
  int move;
  move = 11;
  while (move <= 88 && !legalp_no_fp(move, player, board)) move++;
  if (move <= 88) return 1; else return 0;
}


int *legal_moves_no_fp(int player, int * board) {
	int move, i, *moves;
	moves = (int *)malloc(LEGALMOVSBUFSIZE * sizeof(int));
	moves[0] = 0;
	i = 0;
	for (move=11; move<=88; move++) {
		if (legalp_no_fp(move, player, board)) {
			i++;
			moves[i]=move;
		}
	}
	moves[0]=i;
	return moves;
}

/* choose the player with the next move. Typically this
will be the player other than previousplayer, unless
the former has no legal move available, in which case
previousplayer remains the current player. If no players
have a legal move available, then nexttoplay returns 0.
*/
int nexttoplay(int *board, int previousplayer, int printflag) {
	int opp;
	opp = opponent_no_fp(previousplayer);
	if (has_legal_moves(opp, board)) return opp;
	if (has_legal_moves(previousplayer, board)) {
		if (printflag) printf("%c has no moves and must pass.\n", nameof(opp));
		return previousplayer;
	}
	return 0;
}

void make_temp_move(int move, int player, int *tempBoard) {
  int i;
  tempBoard[move] = player;
  for (i=0; i<=7; i++) makeflips_for_tempBoard(move, player, tempBoard, ALLDIRECTIONS[i]);
}

void makeflips_for_tempBoard(int move, int player, int *board, int dir) {
  int bracketer, c;
  bracketer = wouldflip_no_fp(move, player, board, dir);
  if (bracketer) {
     c = move + dir;
     do {
         board[c] = player;
         c = c + dir;
        } while (c != bracketer);
  }
}

void make_move(int move, int player, FILE *fp) {
	int i;
	board[move] = player;
	for (i = 0; i <= 7; i++) make_flips(move, ALLDIRECTIONS[i], player, fp);
}

void make_flips(int move, int dir, int player, FILE *fp) {
	int bracketer, c;
	bracketer = would_flip(move, dir, player, fp);
	if (bracketer) {
		c = move + dir;
		do {
			board[c] = player;
			c = c + dir;
		} while (c != bracketer);
	}
}

void print_board(FILE *fp) {
	int row, col;
	fprintf(fp, "   1 2 3 4 5 6 7 8 [%c=%d %c=%d]\n",
		nameof(BLACK), count(BLACK, board), nameof(WHITE), count(WHITE, board));
	for (row = 1; row <= 8; row++) {
		fprintf(fp, "%d  ", row);
		for (col = 1; col <= 8; col++)
			fprintf(fp, "%c ", nameof(board[col + (10 * row)]));
		fprintf(fp, "\n");
	}
	fflush(fp);
}

char nameof(int piece) {
	assert(0 <= piece && piece < 5);
	return(piecenames[piece]);
}

int count(int player, int * board) {
	int i, cnt;
	cnt = 0;
	for (i = 1; i <= 88; i++)
		if (board[i] == player) cnt++;
	return cnt;
}
