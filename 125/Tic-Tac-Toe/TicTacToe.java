/**
 * A Class to play a simple TicTacToe game.
 * <p>
 * It supports multiple class of players starting with humans and a computer player 
 * called RandomPlayer. Students must implement a computer player called SmarterPlayer.
 * Advanced students may implement LearningPlayer which learns how to play by trial and error.
 * 
 * @author Scott
 *
 */
public class TicTacToe {
		final   static int     MAX_PLAYERS  = 2;
		private static boolean wantGUI      = false;


	/**
	 * Plays a tic-tac-toe tournament between two players. By default, a single game is played
	 * between two human players. It plays the game, waits for the humans to select their moves, 
	 * and decides when the game is complete and reports a winner, or declares a draw. 
	 * <p>
	 * Using command line parameters, the user can invoke the program and choose which types of 
	 * players should play (human or computer) and how many games the tournament should last. 
	 * <p>
	 * The command syntax is:
	 * <br><b>
	 *     <font face="Courier New, Courier, monospace">java TicTacToe PlayerType1 PlayerType2 NumberOfGames wantGUI</font></b>
	 *  <p>
	 *  Where: <br>
	 *   - PlayerType1 - specifies which Player child class should play as player 1 (default is human)<br>
	 *   - PlayerType1 - specifies which Player child class should play as player 2 (default is human)<br>
	 *   - NumberOfGames - specifies how many games to play in the tournament (default is 1)<br>
	 *   - wantGUI       - if specified, then we should use a Graphical User Interface (default is no GUI) 
	 *  <p>
	 *  Valid choices for PlayerType are: Human, Random, Smarter, Learns <br>
	 *  <b><em>Note that only Human and Random are implemented in the sample implementation</em></b>
	 *     
	 * @param args - Command Line Parameters which specifies Player1 and Player2 class, plus the number of games in the tournament
	 * 
	 *
	 */
	public static void main(String[] args) {
		int numGamesToPlay    = 1;
		int gamesPlayed       = 0;
		
		// Set this to true if the user asks to quit early
		boolean playerWantsToQuit = false;

		// An array of Player Objects for the created players.
		Player[] player = new Player[MAX_PLAYERS];
		
		// Instantiate two players based on the Command Line Parameter options.
	    // Also checks to see if the user has specified how many games to play in the tournament.
		numGamesToPlay = parseCmdLineParms(args, player);
				
		// Define the tokens used by these players
		player[0].setToken("X");
		player[1].setToken("O");
		
		// Create a Board with a couple of players
		Board board = new Board(3, 3, wantGUI, player[0], player[1]);

		while ((gamesPlayed < numGamesToPlay) && (!playerWantsToQuit)) {
			// Start a new game
			boolean firstMove = true;
			Move move;
			gamesPlayed++;
			System.out.println("\nGame " + gamesPlayed + " begins");
			board.clearBoard();
			
			// Keep playing until the game is over or the user asks to quit
			while (!board.gameover() && !playerWantsToQuit)
			{
			   // Alternate who gets first move
			   if (!firstMove || (gamesPlayed%2 == 1))
			   {
			       move = player[0].getNextMove(board);
			       displayPlayersMove(player[0], move);
			       board.makeMove(player[0], move);
			   }
			   
			   if (!board.gameover())
			   {
				   move = player[1].getNextMove(board);
				   displayPlayersMove(player[1], move);
				   board.makeMove(player[1], move);
			   }
			   
			   firstMove = false;
			}

			System.out.print("Game " + gamesPlayed + " ");
			if (board.drawn()) {
				System.out.println("is a draw");
				player[0].incrementGamesDrawn();
				player[1].incrementGamesDrawn();
			}
			else 
			{
				int winner = board.winner().getPlayerNum()-1;
				int loser  = 1 - winner;
				System.out.println("is over, Player " + board.winner() + " wins!");
				player[winner].incrementGamesWon ();
				player[loser ].incrementGamesLost();
			}
			System.out.println("\nFinal position: ");
			System.out.println(board);
		}
		
		System.out.println("The tournament is over. Number of games played = " + gamesPlayed);
		System.out.println("Final stats:");
		for (int i=0; i<MAX_PLAYERS; i++) {
		    System.out.print  ("Player "+i+" :"+player[i]+" \t: Wins: "+player[i].gamesWon());
		    System.out.println("\t Losses: "+player[i].gamesLost()+"\t Draws: "+player[i].gamesDrawn());
		}
		if (player[0].gamesWon() > player[1].gamesWon()) 
			System.out.println("Player "+ player[0] +" wins!");
	    else if (player[0].gamesWon() < player[1].gamesWon()) 
			System.out.println("Player "+ player[1] +" wins!");
	    else
			System.out.println("The tournament is a draw!");


	}

	/**
	 * This routine reads the command line parameters to determine what player types
	 * were specified and how many games should be played in the tournament. 
	 * 
	 * The expected syntax for the command line parms is:
	 *    Player1Type  Player2Type [Number of Games to Play]
	 * 
	 * Player1Type defaults to Human
	 * Player2Type defaults to Random
	 * Player3Type defaults to 1
	 * 
	 * @param playerTypes - set according to cmd line parms (defaults to Human and Random)
	 * @return int - number of games in tournament (defaults to 1)
	 */
	private static int parseCmdLineParms(String[] args, Player[] player){
		int numGames = 100;
		
		// Set the  player types according to the command line parameters
		// If no type specified, used the list of names from Player as defaults
		for (int playerNum = 0; playerNum < MAX_PLAYERS; playerNum++) {
			if (args.length > playerNum)
				player[playerNum] = PlayerTypes.createPlayer(args[playerNum]);
			else
				player[playerNum] = PlayerTypes.createPlayer(PlayerTypes.playerTypeNames[playerNum]);
			
		}
				
		// See if the number of games was specified
		if (args.length > 2) {
			numGames = Integer.parseInt(args[2]);
			System.out.println("Tournament will consist of " + numGames + " games.");
		}
			
		// See if we should use a GUI instead of outputing the board to the Console
		if (args.length > 3) {
			wantGUI = args[3].equalsIgnoreCase("wantGUI");
		}
		
		return numGames;
	}
	
	private static void displayPlayersMove(Player player, Move move) {
	    System.out.println("Player " + player +" moves to " + move);
	}
}
