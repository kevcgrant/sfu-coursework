import java.util.Collections;
import java.util.List;

/**
 * CMPT 431 Assignment 5
 * <p>
 * Signals that a commit has been called without all the necessary writes.
 *
 * @author Kevin Grant - 301192898
 * @author Johnny Lou - 301172395
 *
 */
public class MissingWritesException extends Exception {

	private final List<Integer> missingNumbers;
	
	public MissingWritesException(String s, List<Integer> missingNumbers) {
		super(s);
		this.missingNumbers = Collections.unmodifiableList(missingNumbers);
	}
	
	public MissingWritesException(List<Integer> missingNumbers) {
		this(null, missingNumbers);
	}
	
	public List<Integer> getMissingNumbers() {
		return missingNumbers;
	}
	
	static final long serialVersionUID = -3296498987962245021L;
}
