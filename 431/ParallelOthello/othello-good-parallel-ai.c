#include <cilk/cilk.h>
#include <cilk/reducer_max.h>
#include <stdlib.h>
#include <stdio.h>

#include "othello.h"

#define DEPTH 4
#define MIN -5000000
#define BOARD_SIZE 64

#define CORNERS \
 (BOARD_BIT(1,1) | BOARD_BIT(1,8) | BOARD_BIT(8,1) | BOARD_BIT(8,8))

// get the difference between the player's disks and the opponent's disks.
// used in the heuristic.
int getDifference(const int color, const Board board)
{
  int playerBits = CountBitsOnBoard(&board, color);
  int opponentBits = CountBitsOnBoard(&board, OTHERCOLOR(color));
  int difference = playerBits - opponentBits;

  if (opponentBits == 0) return 10000; // win
  else if (playerBits + opponentBits == BOARD_SIZE) return 10000 * difference; // last move
  else return difference;
}

// Gets a level of mobility, proportional to how many moves are available
// to the player (more available moves is better). Used in the heuristic.
int getMobility(const int num_moves)
{
  return 50 * num_moves;
}

// Get the corner occupancy heuristic.
// Corners are important to own, so they are weighted heavily.
int getCornerOccupancy(const int color, const Board board)
{
  ull bits_player = board.disks[color];
  ull bits_opponent = board.disks[OTHERCOLOR(color)];

  ull corners_player = (bits_player & CORNERS);
  ull corners_opponent = (bits_opponent & CORNERS);

  // occupancy = corners held by player - corners held by opponent
  int occupancy = 0;
  for (; corners_player ; occupancy++) {
    corners_player &= corners_player - 1;
  }

  for (; corners_opponent ; occupancy--) {
    corners_opponent &= corners_opponent - 1;
  }

  return 600 * occupancy;
}

// The heuristic function used by minimax to determine which move to make.
int heuristic(const int color, const int num_moves, const Board board, const Board legal_moves)
{
  int difference = getDifference(color, board);
  int mobility = getMobility(num_moves);
  int corners = getCornerOccupancy(color, board);
  
  return difference + mobility + corners;
}

// Retrieve the move to be made from the move number.
Move getMoveFromNumber(const int num, const int color, const Board legal_moves)
{   
  ull moves = legal_moves.disks[color];
  int highestBit = __builtin_clzll(moves);

  for (int i = 0; i < num; i++) {
    moves -= ((ull)1) << (63-highestBit);
    highestBit = __builtin_clzll(moves);
  }

  Move m = BIT_TO_MOVE(highestBit);
  return m;
}

// get the new board from the result of a move. 
Board getBoardResultFromMove(const Move move, const int color, Board board)
{
  Board newBoard = board;
  int nflips = FlipDisks(move, &newBoard, color, 0, 1);
  if (nflips == 0) {
    printf("Illegal move: no disks flipped!!\n");
  }

  PlaceOrFlip(move, &newBoard, color);
  return newBoard;
}

// The internal, serial minimax implementation that is called by the main
// minimax algorithm.
int minimax_internal(const int depth, const int color, const Board board) 
{
  Board legal_moves;
  int num_moves = EnumerateLegalMoves(board, color, &legal_moves);

  if (depth <= 0 || num_moves == 0) {
    return heuristic(color, num_moves, board, legal_moves);
  }

  int evaluation = MIN;
  for (int i = 0; i < num_moves; i++) {
    Move move = getMoveFromNumber(i, color, legal_moves);
    Board newBoard = getBoardResultFromMove(move, color, board);
    int result = -minimax_internal(depth - 1, color, newBoard);
    if (result > evaluation) {
      evaluation = result;
    }
  }

  return evaluation;
}

// The parallel minimax algorithm implementation.
// returns the move number of the "best" move.  
int minimax(const int depth, const int color, const Board board) 
{
  Board legal_moves;
  int num_moves = EnumerateLegalMoves(board, color, &legal_moves);

  if (depth <= 0 || num_moves == 0) {
    return heuristic(color, num_moves, board, legal_moves);
  }

  CILK_C_REDUCER_MAX_INDEX(best_move, int, 0);
  cilk_for (int i = 0; i < num_moves; i++) {
    Move move = getMoveFromNumber(i, color, legal_moves);
    Board newBoard = getBoardResultFromMove(move, color, board);
    int evaluation = -minimax_internal(depth - 1, color, newBoard);
    CILK_C_REDUCER_MAX_INDEX_CALC(best_move, i, evaluation);
  }

  return REDUCER_VIEW(best_move).index;
}

// Make the move on the board given the move number.
// return 1 if a move was made, or 0 if no move is available.
int makeMove(Board *b, const int color, const int move_number)
{
  Board legal_moves;
  int num_moves = EnumerateLegalMoves(*b, color, &legal_moves);
  if (num_moves != 0) {
    Move m = getMoveFromNumber(move_number, color, legal_moves);
    int nflips = FlipDisks(m, b, color, 0, 1);
    if (nflips == 0) {
      printf("Illegal move: no disks flipped!!\n");
    }
    PlaceOrFlip(m, b, color);
    return 1;
  } else {
    return 0;
  }
}

int GoodAITurn(Board *b, int color)
{
  int moveNum = minimax(DEPTH, color, *b);
  return makeMove(b, color, moveNum);
}

