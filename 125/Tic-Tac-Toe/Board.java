
/**
 * Class to implement a tic tac toe board and moves on that board by two players.
 * By changing the numbers of rows, columns, and levels, this can be made to support
 * other games such as Cubic and Connect-4
 * 
 * @author Scott
 *
 */
public class Board {
	public  final int MAX_PLAYERS = 2;
	
	protected  Player[] players = new Player[MAX_PLAYERS];
	private int numPlayers;
	
	private int dims   = 2;
	private int rows   = 3;
	private int cols   = 3;
	private int levels = 1;
	int[][]     board;

	private boolean useGUI = false;
	
	Board(int rows, int columns, boolean useGUI, Player player1, Player player2) {
		this.players[0] = player1;
		this.players[1] = player2;
		this.numPlayers = 2;
		this.dims       = 2;
		this.rows       = rows;
		this.cols       = columns;
		this.useGUI     = useGUI;
		this.levels     = 1;
		
		this.board = new int[this.rows][this.cols];
	}
	
	public void clearBoard() {
		for (int i=0; i<rows; i++)
			for (int j=0; j<cols; j++)
				board[i][j] = 0;
	}
	
	public boolean gameover() {
		boolean over = false;
		
		if (drawn() || hasWon(players[0]) || hasWon(players[1]))
			over = true;
		
		return over;
	}
	
	private int numEmpty() {
		int emptyCount = 0;
		
		for (int i=0; i<rows; i++)
			for (int j=0; j<cols; j++)
				if (board[i][j] == 0)
					emptyCount++;
		
		return emptyCount;
	}
	
	private boolean hasWon(Player player) {
		boolean allInARow  = false;
		boolean mismatch   = false;
		int     playerNum  = player.getPlayerNum();
		
		// Search if three in a row horizontally
		for (int r=0; (!allInARow) && (r<rows); r++)
		{
		 mismatch = false;
		 for (int c=0; c<cols; c++)
		 {
			 if (board[r][c] != playerNum)
				 mismatch = true;
		 }
		 if (!mismatch)
			 allInARow = true;
		}
		
		// Search if three in a row vertically
		for (int c=0; (!allInARow) && (c<cols); c++)
		{
	     mismatch = false;
		 for (int r=0; r<cols; r++)
		 {
			 if (board[r][c] != playerNum)
				 mismatch = true;
		 }
		 if (!mismatch)
			 allInARow = true;
		}
		
		// Check the left to right diagonal 
		mismatch = false;
		for (int i=0; i<rows; i++)
		{
			if ((board[i][i] != playerNum))
				mismatch = true;
		}
		if (!mismatch)
			allInARow = true;
		
		// Check the right to left diagonal 
		mismatch = false;
		for (int i=0; i<rows; i++)
		{
			if ((board[i][rows-(i+1)] != playerNum))
				mismatch = true;
		}
		if (!mismatch)
			allInARow = true;
		
		return allInARow;
	}
	
	public boolean drawn() {
		boolean aDraw = false;
		
		if ((numEmpty()==0) && !hasWon(players[0]) && !hasWon(players[1]))
			aDraw = true;
	
		return aDraw;
	}
	
	public Player winner() {
		Player winningPlayer = null;
		
		for(int i=0; i<numPlayers; i++)
			if (hasWon(players[i]))
				winningPlayer = players[i];
		
		return winningPlayer;
	}
	
	public boolean emptySquare(int row, int col) {
		boolean isEmpty = false;
		
		if (board[row][col] == 0)
		{
			isEmpty = true;
		}
		
		return isEmpty;
	}
	
	
    /**
     * This routine returns true if the specified player owns the square on the board[row][col].
     * @param row - the row of the square (starting with zero) to test if it is owned by player.
     * @param col - the column of the square (starting with zero) to test if it is owned by player.
     * @param player - the player to check if it owns the square
     * @return - boolean equals to true if and only if the specified board square board[row][col] is empty, and
     *         returns false otherwise.
     */
    public boolean squareOwnedByPlayerHelper(int row, int col, Player player) {
    	
            boolean ownsTheSquare = false;

            if (board[row][col] == player.getPlayerNum())
            {
                    ownsTheSquare = true;
            }

            return ownsTheSquare;
    }
	
	public void makeMove(Player player, Move move) {
		if (emptySquare(move.row(),move.col()))
		{
			board[move.row()][move.col()] = player.getPlayerNum();
		}
	}
	
	private String getToken(int row, int col) {
		String token = " ";
		if (board[row][col] == 0)
			token = ((Integer)(row*rows + col + 1)).toString();
		else
			token = (Player.getPlayer(board[row][col])).getPlayerToken();
	    return token;
	}
	
	public String toString() {
		String b = "";
		for (int i=0; i<rows; i++)
		{
			for (int j=0; j<cols; j++) 
			{
				b += " " + getToken(i,j) + " ";
		        if ((j+1)<cols)
					b += "|";
			}
			b += "\n";
			
			if ((i+1)<rows) 
			{
				for (int j=0; j<cols; j++) 
				{
					b += "---";
					if ((j+1)<cols)
						b += "+";
				}
				b += "\n";
			}
		}
        return b;
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getCols() {
		return cols;
	}


}
