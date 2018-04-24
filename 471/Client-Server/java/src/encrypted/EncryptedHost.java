package encrypted;

import java.io.IOException;

/**
 * CMPT 471 Assignment 4
 * <p>
 * Interface representing either a client or a server.
 * 
 * @author Kevin Grant - 301192898
 *
 */
public interface EncryptedHost {

	/**
	 * Sends a message from one host to the other. A connection must already
	 * have been established.
	 */
	void sendMessage(String message) throws IOException;
	
	/**
	 * Reads a message from the other host. A connection must already have been
	 * established.
	 */
	String readMessage() throws IOException;
	
	/**
	 * Run the client or server.
	 */
	void run() throws IOException;
}
