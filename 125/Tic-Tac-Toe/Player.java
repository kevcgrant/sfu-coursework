/**
 * This is an abstract class that creates a design pattern for
 * players of a board game with different styles. To add a new
 * player type, extend playerTypeNames, add a new case to the
 * createPlayer method.
 * 
 * @author Scott
 *
 */
abstract public class Player {
	final private static int MAX_PLAYERS = 6;
	private static Player[] players = new Player[MAX_PLAYERS];
		
	// Static variables (one copy per Program)
	private static int numPlayers = 0;
	
	// Instance Variables (one copy per player)
	private String playerName  = "";
	private String token       = "-";
	private int    playerNum   = 0;
	private int    gamesWon    = 0;
	private int    gamesLost   = 0;
	private int    gamesDrawn  = 0;

	/**
	 * Constructor for Player which records which player number
	 * was created. The player number is used for keeping track
	 * of which player has played on each square of the board.
	 * It would be better to use Tokens in the future.
	 */
	Player(String playerName) {
		if (numPlayers < MAX_PLAYERS)
		{
			this.playerNum          = ++numPlayers;
			this.playerName         = playerName;
			players[this.playerNum] = this;
		}
		System.out.println("Player " + playerNum + " has been created " + this.playerName);
	}
			
	/** 
	 * Each child class of the Player class must implement a getNextMove method to return
	 * the player's next move using this exact signature to fulfill the software contract 
	 * defined by class Player. This abstract class is used by class TicTacToe to play a game
	 * of tic-tac-toe by two players.
	 * <p> 
	 * Each child class must "extends Player" in order implement a working tic-tac-toe player. 
	 * The child class may define its own private methods and local variables as required
	 * to implement its specific functionality. See HumanPlayer and RandomPlayer for examples.
	 * 
	 * @param board - An <font face="Courier New, Courier, monospace">int[3][3]</font> array representing the current position of the board. 
	 * Child class versions of the <font face="Courier New, Courier, monospace">getNextMove</font> method
	 * can test if a given square is empty by calling 
	 * <font face="Courier New, Courier, monospace">board.emptySquare(row,col)</font> which
	 * returns true if the array element in board at <font face="Courier New, Courier, monospace">int[row][col]</font>
	 * is currently empty.
	 * <p> To determine which player currently has a token in a given position within the array, check the value of the int 
	 * in that row and column of the two dimensional array. The int contains the player number which has a token at that square, 
	 * or contains the value zero if it is empty. The first player is player 1 while the second player is player 2. These int values
	 * will be saved in the board array to mark which player has a token in each square.
	 * <p> To create a move,
	 * the method should instantiate a new Move with the row and column in the array where the method wants
	 * to place a token using "<font face="Courier New, Courier, monospace">new Move(row,col)</font>".
	 * @return - Returns a Move object which specifies which row and column the player wants to place his token.
	 */
    abstract protected Move getNextMove(Board board);
    

	
	public String toString() {
		return this.playerName + " ("+ this.token +")";
	}
	
	public void   setToken(String token) {
		this.token = token;
	}
	
	public String getToken() {
		return this.token;
	}
	
	public String getPlayerToken() {
		return token;
	}
	
	public int getPlayerNum() {
		return this.playerNum;
	}
	
	public static Player getPlayer(int playerNum) {
		return players[playerNum];
	}
	
	public static int getNumPlayers() {
		return numPlayers;
	}
	
	protected void setPlayerName(String name) {
		playerName = name;
	}
	
	/**
	 * Returns the name of this player
	 * @return String playerName
	 */
    String getPlayerName() {
    	return playerName;
    }
    
    public int gamesPlayed() {
    	return gamesWon + gamesDrawn + gamesLost;
    }

    public void incrementGamesWon() {
    	gamesWon++;
    }
    
    public int gamesWon() {
    	return gamesWon;
    }

    
    public void incrementGamesLost() {
    	gamesLost++;    	
    }
    
    public int gamesLost() {
    	return gamesLost;
    }
    
    public void incrementGamesDrawn() {
    	gamesDrawn++;
    }
 
    public int gamesDrawn() {
    	return gamesDrawn;
    }
}


