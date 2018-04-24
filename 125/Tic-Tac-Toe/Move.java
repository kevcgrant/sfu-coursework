/**
 *  This defines the Move class which contains the row and column number of a given move
 * @author Scott
 *
 */
public class Move {
	private int row = 0;
	private int col = 0;
	
	Move(int row, int col){
		this.row = row;
		this.col = col;
	}
	
	public int row() {
		return this.row;
	}
	
	public int col() {
		return this.col;
	}

    public String toString() {
    	return " square "+ (row*3+col+1) + " ("+row+","+col+")";
    }
}
