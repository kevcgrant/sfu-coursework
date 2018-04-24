package encrypted;

import java.util.Random;

/**
 * Utility class for generating random prime numbers.
 */
public class PrimeGenerator {

	private static final int MIN_PRIME_VALUE = 1001;
	private static final int PRIME_VALUE_RANGE = 10000;
	
	/**
	 * Generates a prime number within the range of [1001, 11001].
	 */
	public static int generatePrimeNumber() {
		int candidate = generateCandidateNumber();
		while (!isPrime(candidate)) {
			candidate++;
		}
		return candidate;
	}
	
	private static int generateCandidateNumber() {
		Random rand = new Random();
		return rand.nextInt(PRIME_VALUE_RANGE) + MIN_PRIME_VALUE;
	}
	
	private static boolean isPrime(int n) {
	    if (n % 2 == 0) return n == 2;
	    if (n % 3 == 0) return n == 3;
	    int step = 4, m = (int)Math.sqrt(n) + 1;
	    for(int i = 5; i < m; step = 6-step, i += step) {
	        if (n % i == 0) {
	            return false;
	        }
	    }
	    return true;
	}
}
