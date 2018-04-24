package encrypted;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

/**
 * CMPT 471 Assignment 4
 * <p>
 * Implementation of Diffie-Hellman key delivery.
 *
 * @author Kevin Grant - 301192898
 *
 */
public class KeyDeliverer {

	private static final int PRIVATE_NUMBER_MAX_VALUE = 10000;
	
	/** The public numbers (primes) */
	private BigInteger n;
	private BigInteger q;
	
	/** The private number for the host */
	private int privateNumber;
	
	/** Either the client or the server */
	private EncryptedHost host;
	
	/** 
	 * Flag specifying whether the host is a client or a server. 
	 * The client is responsible for sharing the public prime numbers with the server. 
	 */
	private boolean isClient;
	
	public KeyDeliverer(EncryptedHost host, boolean isClient) {
		this.isClient = isClient;
		this.host = host;
		initializePrivateNumber();
	}
	
	private void initializePrivateNumber() {
		Random rand = new Random();
		privateNumber = rand.nextInt(PRIVATE_NUMBER_MAX_VALUE) + 2;
	}
	
	/**
	 * Main algorithm performing the diffie-hellman encryption. 
	 * Must be called after a connection has been established between the client and the server.
	 * @throws IOException if there is an issue with the sending and receiving of messages.
	 */
	public int calculateKey() throws IOException {
		sharePublicPrimeNumbers();
		calculateAndSendMyPublicNumber();
		BigInteger otherPublicNumber = readOtherPublicNumber();
		
		return calculatePrivateKey(otherPublicNumber);
	}
	
	/**
	 * Share the public primes. If the host is the client, it sends a message to the 
	 * server containing the primes. If the host is the server, it receives the primes from the
	 * client.
	 */
	private void sharePublicPrimeNumbers() throws IOException {
		if (isClient) {
			n = BigInteger.valueOf(PrimeGenerator.generatePrimeNumber());
			q = BigInteger.valueOf(PrimeGenerator.generatePrimeNumber());
			host.sendMessage(n.toString() + " " + q.toString());
		} else {
			String publicNumbers = host.readMessage();
			n = new BigInteger(publicNumbers.split(" ")[0]);
			q = new BigInteger(publicNumbers.split(" ")[1]);
		}
	}
	
	/**
	 * Calculates the public number for the host and sends it to the other host.
	 */
	private void calculateAndSendMyPublicNumber() throws IOException {
		BigInteger myPublicNumber = calculatePublicNumber();
		host.sendMessage(myPublicNumber.toString());
	}
	
	/**
	 * (n ^ privateNum) mod q
	 */
	private BigInteger calculatePublicNumber() {
		return n.pow(privateNumber).mod(q);
	}
	
	/**
	 * Read the public number sent from the other host.
	 */
	private BigInteger readOtherPublicNumber() throws IOException {
		String message = host.readMessage();
		return new BigInteger(message);
	}
	
	/**
	 * (publicKey ^ privateNum) mod q
	 */
	private int calculatePrivateKey(BigInteger publicKey) {
		return publicKey.pow(privateNumber).mod(q).intValue();
	}
}
