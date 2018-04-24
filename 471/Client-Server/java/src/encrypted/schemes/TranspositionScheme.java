package encrypted.schemes;

import java.util.Arrays;

/**
 * CMPT 471 Assignment 4
 * <p>
 * The transposition encryption scheme.
 *
 * @author Kevin Grant - 301192898
 *
 */
public class TranspositionScheme {

	private static final char MATRIX_FILLER_CHAR = '~';
	
	private final int[] transpositionKey;
	
	public TranspositionScheme(int[] transpositionKey) {
		this.transpositionKey = transpositionKey;
	}
	
	/**
	 * Arranges the text into a matrix, then transposes the matrix using the key.
	 * Unused matrix elements are filled with '~'.
	 * <p>
	 * For example,
	 * 
	 * <pre>
	 * Using the key {4, 5, 1, 2, 0, 3},
	 * 
	 * the message "hello, world!" would be arranged in the matrix:
	 * 
	 * 		h e l l o ,
	 * 		  w o r l d
	 *		! ~ ~ ~ ~ ~
	 *
	 * and then transposed with column 4 as row 0, column 5 as row 1, column 1 as row 3, etc:
	 * 
	 *		o l ~
	 *		, d ~
	 *		e w ~
	 *		l o ~
	 *		h   !
	 *		l r ~
	 *
	 * Reading the matrix from left to right and top to bottom results in the cipher:
	 * 
	 * 		ol~,d~ew~h !lr~
	 * 
	 * </pre>
	 */
	public String encrypt(String text) {
		char[][] matrix = arrangeTextIntoMatrix(text);
		char[][] transposedMatrix = transposeMatrix(matrix);
		
		return getTextFromMatrix(transposedMatrix);
	}
	
	/**
	 * Creates a matrix filled with the characters of the supplied message.
	 */
	private char[][] arrangeTextIntoMatrix(String text) {
		int numberOfColumns = transpositionKey.length;
		int numberOfRows = getNumberOfRows(text);
		
		return buildMatrix(numberOfRows, numberOfColumns, text);
	}
	
	/**
	 * Build the matrix with the specified number of rows and columns.
	 */
	private char[][] buildMatrix(int rows, int columns, String text) {
		char[][] matrix = new char[rows][columns];
		
		for (char[] row : matrix) {
			Arrays.fill(row, MATRIX_FILLER_CHAR);
		}
		
		char[] chars = text.toCharArray();
		for (int i = 0; i < text.length(); i++) {
			matrix[i / columns][i % columns] = chars[i];
		}

		return matrix;
	}
	
	/**
	 * Calculate the number of rows needed for the matrix, given the length of the 
	 * text and the length of the transposition key.
	 */
	private int getNumberOfRows(String text) {
		int fullRows = text.length() / transpositionKey.length;
		int remainder = text.length() % transpositionKey.length;
		return (remainder == 0) ? fullRows : ++fullRows;
	}
	
	/**
	 * Transpose the matrix, using the transposition key to determine the order
	 * of arranging columns into rows.
	 */
	private char[][] transposeMatrix(char[][] matrix) {
		char[][] transposedMatrix = new char[matrix[0].length][matrix.length];
		int numberOfRows = matrix.length;
		int i = 0;
		for (int col : transpositionKey) {
			for (int row = 0; row < numberOfRows; row++) {
				transposedMatrix[i][row] = matrix[row][col];
			}
			i++;
		}
		return transposedMatrix;
	}
	
	/**
	 * Iterates over the matrix left to right and top to bottom, returning the 
	 * text.
	 */
	private String getTextFromMatrix(char[][] matrix) {
		String text = "";
		for (char[] row : matrix) {
			for (char c : row) {
				if (c != Character.MIN_VALUE) text += c;
			}
		}
		return text;
	}
	
	/**
	 * Reverses the process of {@link #encrypt(String)}, decrpyting the ciphertext.
	 */
	public String decrypt(String ciphertext) {
		char[][] matrix = arrangeTextIntoTransposedMatrix(ciphertext);		
		char[][] detransposed = detransposeMatrix(matrix);
		removeTrailingFillerCharsFromMatrix(detransposed);
		
		return getTextFromMatrix(detransposed);
	}
	
	/**
	 * Arrange the ciphertext into a transposed matrix.
	 */
	private char[][] arrangeTextIntoTransposedMatrix(String text) {
		int numberOfRows = transpositionKey.length;
		int numberOfColumns = getNumberOfRows(text);
		
		return buildMatrix(numberOfRows, numberOfColumns, text);
	}
	
	/**
	 * transpose the transposed matrix to get it back to its original state. The transposition
	 * key is used to correctly arrange the columns back into rows.
	 */
	private char[][] detransposeMatrix(char[][] matrix) {
		char[][] detransposedMatrix = new char[matrix[0].length][matrix.length];
		int numberOfRows = matrix.length;
		int numberOfColumns = matrix[0].length;
		for (int row = 0; row < numberOfRows; row++) {
			for (int col = 0; col < numberOfColumns; col++) {
				detransposedMatrix[col][transpositionKey[row]] = matrix[row][col];
			}
		}
		return detransposedMatrix;
	}
	
	/**
	 * Removes all of the trailing filler characters from the matrix.
	 */
	private void removeTrailingFillerCharsFromMatrix(char[][] matrix) {
		for (int col = matrix[0].length - 1; col > 0; col--) {
			if (matrix[matrix.length - 1][col] == MATRIX_FILLER_CHAR) {
				matrix[matrix.length - 1][col] = Character.MIN_VALUE;
			} else {
				return;
			}
		}
	}
}
