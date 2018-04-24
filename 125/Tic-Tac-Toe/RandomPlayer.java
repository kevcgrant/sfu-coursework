import java.util.Random;


/**
 * Class RandomPlayer extends Player and create a computer player
 * which picks an empty square at random. 
 * 
 * @author Scott
 *
 */
public class RandomPlayer extends Player {
	private Random rowGenerator = new Random();
	private Random colGenerator = new Random();
	
	public RandomPlayer()
	{
	 // Always invoke the Parent Class constructor
	 super("Random"); 		
	 this.setPlayerName("Random");
	}

	public Move getNextMove(Board board)
	{
	    int row = 0;
	    int col = 0;
	    
		do {
			row = rowGenerator.nextInt(board.getRows());
			col = colGenerator.nextInt(board.getCols());
		} while (!board.emptySquare(row,col));

		return new Move(row, col);
	}
	

}
