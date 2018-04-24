import java.util.Scanner;

/**
 * 
 * HumanPlayer is an example class that inherits methods from its Parent Class (Player).
 * 
 * @author Scott Kristjanson
 *
 */
public class HumanPlayer extends Player {
	
	Scanner scanLine;
	
	public HumanPlayer()
	{
		// Always invoke the Parent Class constructor
		super("Human"); 
		
		// Then initialize anything we need locally
	    this.setPlayerName("Human");
	 
	    // Create a Scanner to read moves from the keyboard
	    scanLine = new Scanner(System.in);
	}

	public Move getNextMove(Board board)
	{
		String  inputLine;
		boolean validMove = false;
		int     row       = 0;
		int     col       = 0;
		int     squareNum = 0;
		
		do {
			System.out.println("Current board:\n" + board);
			System.out.println("Player " + this + ", Please pick an empty square from 1 to 9 as your next move:");
			inputLine = scanLine.nextLine();
			
			Scanner scanMove = new Scanner(inputLine);
			if (scanMove.hasNextInt()) 
			{
				squareNum = scanMove.nextInt();
				if ((squareNum >= 1) && (squareNum <= 9))
				{
					row = (squareNum-1)/3;
					col = (squareNum-1)%3;
					if (board.emptySquare(row,col))
					{
				        validMove = true;	
					}
				}
				else
				{
					System.out.println("\"" + squareNum + "\" is an invalid move.");
				}
			 	
			}
			else 
			{
				System.out.println("\"" + scanMove.next() + "\" is an invalid move.");
			}
		} while (!validMove);
		
		
		return new Move(row, col);
	}
}
