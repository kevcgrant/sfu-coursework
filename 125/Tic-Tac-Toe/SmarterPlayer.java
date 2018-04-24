import java.util.Random;

/**
 * 
 * @author Kevin Grant // 301192898
 * @version SmarterPlayer
 * 
 * The SmarterPlayer class extends the Player class and creates a computer player.
 * If the opponent has an available winning move, the SmarterPlayer will block it.
 * If the SmarterPlayer has a winning move, the SmarterPlayer will take it.
 * 
 */

public class SmarterPlayer extends Player {
	
	//-------------------------------------------------------------------------
	// assigns the number of rows and columns to the board array. Instantiates
	// two generators to generate random moves for the player.
	//-------------------------------------------------------------------------
	private Random rowGenerator = new Random();
	private Random colGenerator = new Random();
	private int rows = 3;
	private int cols = 3;
		
    int row = 0;
    int col = 0;
	
	/**
	 * Invokes the parent class constructor.
	 */
	public SmarterPlayer()
	{
		super("Smarter"); 		
		this.setPlayerName("Smarter");
	}
	
	/**
	 * Checks if SmarterPlayer's opponent can make a winning move for a specific sequence, and sets the move to block.
	 * <p>
	 * 
	 * @param board 	the current state of the tictactoe board
	 * @param r1 		row position of first square
	 * @param r2		row position of second square
	 * @param c1		column position of first square
	 * @param c2 		column position of second square
	 * @param setRow	row position of move to be made
	 * @param setCol	column position of move to be made 
	 */
	public void opponentHasWinningMove(Board board, int r1, int r2, int c1, int c2, int setRow, int setCol)
	{		
		boolean twoInARow = false;
		 
		if (!board.squareOwnedByPlayerHelper(r1, c1, this) && !board.squareOwnedByPlayerHelper(r2, c2, this))
			if (!board.emptySquare(r1, c1) && !board.emptySquare(r2, c2))
				twoInARow = true;
	 
		if (twoInARow && board.emptySquare(setRow, setCol)) {
			row = setRow;
			col = setCol;
		}
	}
	
	/**
	 * Checks if SmarterPlayer can make a winning move for a specific sequence, and sets the move.
	 * <p>
	 * 
	 * @param board 	the current state of the tictactoe board
	 * @param r1 		row position of first square
	 * @param r2		row position of second square
	 * @param c1		column position of first square
	 * @param c2 		column position of second square
	 * @param setRow	row position of move to be made
	 * @param setCol	column position of move to be made 
	 */
	public void playerHasWinningMove(Board board, int r1, int r2, int c1, int c2, int setRow, int setCol)
	{	
		boolean twoInARow = false;

		if (board.squareOwnedByPlayerHelper(r1, c1, this) && board.squareOwnedByPlayerHelper(r2, c2, this))
			twoInARow = true;
	 
		if (twoInARow && board.emptySquare(setRow, setCol)) {
			row = setRow;
			col = setCol;
		}
	}
	
	/**
	 * Checks if SmarterPlayer's opponent can make a winning move, and sets the move to block it.
	 * <p>
	 * 
	 * Checks for winning moves for opponent horizontally, then vertically, then diagonally.
	 * 
	 * @param board 	the current state of the tictactoe board
	 * 
	 */
	public void checkIfOpponentHasWinningMove(Board board) 
	{		
		/* Check Horizontal */
		for (int r = 0; r < rows; r++) {
			// Searches for two in a row horizontally from the beginning
			opponentHasWinningMove(board, r, r, 0, 1, r, 2);
			
			// Searches for two in a row horizontally from the middle
			opponentHasWinningMove(board, r, r, 1, 2, r, 0);
			
			// Searches for two in the same row with the middle open 
			opponentHasWinningMove(board, r, r, 0, 2, r, 1);			
		}
		
		/* Check Vertical */
		for (int c = 0; c < cols; c++) {
			// Searches for two in a row vertically from the beginning
		    opponentHasWinningMove(board, 0, 1, c, c, 2, c);
			 
			// Searches for two in a row vertically from the middle
		    opponentHasWinningMove(board, 1, 2, c, c, 0, c);
			 
			// Searches for two in a row vertically with the middle open
		    opponentHasWinningMove(board, 0, 2, c, c, 1, c);
		}
		
		/* Check Diagonal */
	
		// Searches left to right diagonally from the beginning
		opponentHasWinningMove(board, 0, 1, 0, 1, 2, 2);
		
		// Searches left to right diagonally from the middle
		opponentHasWinningMove(board, 1, 2, 1, 2, 0, 0);

		// Searches left to right diagonally with the middle open
		opponentHasWinningMove(board, 0, 2, 0, 2, 1, 1);
		
		// Searches right to left diagonally from the top right corner
		opponentHasWinningMove(board, 0, 1, 2, 1, 2, 0);
		
		// Searches right to left diagonally from the bottom left corner
		opponentHasWinningMove(board, 1, 2, 1, 0, 0, 2);
		
		// Searches right to left diagonally with the middle open
		opponentHasWinningMove(board, 0, 2, 2, 0, 1, 1);
	}
	
	/**
	 * Checks if SmarterPlayer can make a winning move, and sets the move to block it.
	 * <p>
	 * 
	 * Checks for winning moves horizontally, then vertically, then diagonally.
	 * 
	 * @param board 	the current state of the tictactoe board
	 * 
	 */
	public void checkIfPlayerHasWinningMove(Board board)
	{		
		/* Check Horizontal */
		for (int r = 0; r < rows; r++) {
			// Searches for two in a row horizontally from the beginning
			playerHasWinningMove(board, r, r, 0, 1, r, 2);
			
			// Searches for two in a row horizontally from the middle
			playerHasWinningMove(board, r, r, 1, 2, r, 0);
			
			// Searches for two in the same row with the middle open 
			playerHasWinningMove(board, r, r, 0, 2, r, 1);			
		}
		
		/* Check Vertical */
		for (int c = 0; c < cols; c++) {
			// Searches for two in a row vertically from the beginning
			playerHasWinningMove(board, 0, 1, c, c, 2, c);
			 
			// Searches for two in a row vertically from the middle
			playerHasWinningMove(board, 1, 2, c, c, 0, c);
			 
			// Searches for two in a row vertically with the middle open
			playerHasWinningMove(board, 0, 2, c, c, 1, c);
		}
		
		/* Check Diagonal */
	
		// Searches left to right diagonally from the beginning
		playerHasWinningMove(board, 0, 1, 0, 1, 2, 2);
		
		// Searches left to right diagonally from the middle
		playerHasWinningMove(board, 1, 2, 1, 2, 0, 0);

		// Searches left to right diagonally with the middle open
		playerHasWinningMove(board, 0, 2, 0, 2, 1, 1);
		
		// Searches right to left diagonally from the top right corner
		playerHasWinningMove(board, 0, 1, 2, 1, 2, 0);
		
		// Searches right to left diagonally from the bottom left corner
		playerHasWinningMove(board, 1, 2, 1, 0, 0, 2);
		
		// Searches right to left diagonally with the middle open
		playerHasWinningMove(board, 0, 2, 2, 0, 1, 1);
	}
	
	/**
	 * Sets a starting move for SmarterPlayer if no moves or one move in total has been made.
	 * <p>
	 * 
	 * The routine first counts the number of empty squares. if 8 or 9, it sets its starting move.
	 * 
	 * If the middle square is open, that square is chosen. Otherwise, the top left square is chosen.
	 * 
	 * @param board 	the current state of the tictactoe board
	 * 
	 */
	public void setStartingMove(Board board) 
	{		
		int empty=0;
		
		// Count the number of empty squares
		for (int r=0; r<3; r++) {
			for (int c=0; c<3; c++) {
				if (board.emptySquare(r,c))
					empty++;
			}
			
			// Zero or two moves were made
			if (empty == 9 || empty == 8) {
				// Try to make move in middle
				if (board.emptySquare(1,1)) {
					row = 1;
					col = 1;
				}
				// Make move in top left corner
				else {
					row = 0;
					col = 0;
				}		
			}
		}
	}
	
	/**
	 * Chooses a move for the SmarterPlayer based on the current state of the TicTacToe board.
	 * <p>
	 * This method returns the move of SmarterPlayer.
	 * 
	 * The routine first assigns a random valid move to SmarterPlayer. Then, it makes a starting move
	 * of either the middle square if available, or the top left otherwise.
	 * 
	 * The routine then checks if the opponent has a winning move. If it does, SmarterPlayer's 
	 * assigned move is to block the opponent's winning move. The routine then checks if the 
	 * SmarterPlayer has a winning move. If it does, SmarterPlayer's new move is the winning
	 * move.
	 * 
	 * @param board 	the current state of the tictactoe board
	 * @return Move 	the SmarterPlayer's move
	 * 
	 */
	public Move getNextMove(Board board)  
	{			
	    /* Start with random valid move */
	    do {
			row = rowGenerator.nextInt(board.getRows());
			col = colGenerator.nextInt(board.getCols());
		} while (!board.emptySquare(row,col));
	    
	    /* Set starting move, if applicable */
		setStartingMove(board);
	    
	    /* Check if the opponent has a winning move, set move to block it */
	    checkIfOpponentHasWinningMove(board);
		
	    /* Check if smarter player has an available winning move, set move to take it */
	    checkIfPlayerHasWinningMove(board);
		
		/* Make the move */
		return new Move(row, col);
	}
			
}

