package encrypted.schemes;

/**
 * CMPT 471 Assignment 4
 * <p>
 * The substitution encryption scheme.
 *
 * @author Kevin Grant - 301192898
 *
 */
public class SubstitutionScheme {

	/** The starting decimal value of the ASCII characters represented */
	private static final int SMALLEST_CHAR_DECIMAL_VALUE = 32;
	
	private final String substitutionKey;
	
	public SubstitutionScheme(String substitutionKey) {
		this.substitutionKey = substitutionKey;
	}
	
	/**
	 * Encrypts the plaintext by substitution. For each character 'c' in the plaintext, the
	 * character at the substitution key's index <code> [decimalValue(c) - 32] </code> is
	 * substituted.
	 * <p>
	 * For example, if the character is D, which has decimal value 68, then the index 
	 * (68 - 32) = 36 is used in for reading the key. If the character at index 36 of the 
	 * key is 't', then the 'D' is substituted with 't'.
	 */
	public String encrypt(String plaintext) {
		String cipher = "";
		for (char c : plaintext.toCharArray()) {
			cipher += substitutionKey.charAt((int) c - SMALLEST_CHAR_DECIMAL_VALUE);
		}
		
		return cipher;
	}
	
	/**
	 * Decrypts the ciphertext by reversing the process of the encryption.
	 */
	public String decrypt(String ciphertext) {
		String text = "";
		for (char c : ciphertext.toCharArray()) {
			text += (char) (substitutionKey.indexOf((int) c) + SMALLEST_CHAR_DECIMAL_VALUE);
		}
		
		return text;
	}
}
