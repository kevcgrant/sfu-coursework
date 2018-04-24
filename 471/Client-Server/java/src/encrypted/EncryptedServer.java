package encrypted;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import encrypted.schemes.EncryptionScheme;

/**
 * CMPT 471 Assignment 4
 * <p>
 * A simple server for a TCP connection, that uses a secret key encryption framework.
 * A port may be specified when running the server, otherwise a default of 12323 is used.
 * <p>
 * e.g.
 * <pre>
 * # java EncryptedServer 12345
 * # java EncryptedServer
 * </pre>
 *
 * @author Kevin Grant - 301192898
 *
 */
public class EncryptedServer implements EncryptedHost {

	private static final int DEFAULT_PORT = 12323;
	
	/** The socket the server runs on */
	private ServerSocket server;
	
	/** The socket used to connect to the client */
	private Socket socket;
	
	/**
	 * Open a new connection on the default port.
	 * @throws IOException if the socket does not open correctly.
	 */
	public EncryptedServer() throws IOException {
		new EncryptedServer(DEFAULT_PORT);
	}
	
	/**
	 * Open a new connection on the specified port.
	 * @throws IOException if the socket does not open correctly.
	 */
	public EncryptedServer(int port) throws IOException {
		server = new ServerSocket(port);
	}

	/**
	 * Run the server.
	 */
	@Override
	public void run() throws IOException {
		while (true) {	
			acceptConnection();
			int privateKey = getPrivateKey();
			String cipher = readMessage();
			String decryption = decryptMessage(cipher, privateKey);
			printMessages(cipher, decryption);
			socket.close();
		}
	}

	/**
	 * Wait for a connection to the server socket from the client and accept it.
	 */
	private void acceptConnection() throws IOException {
		System.out.println(getInetAddress() + " Listening on port " + server.getLocalPort() + "...");
		this.socket = server.accept();
		System.out.println("Accepted connection from " + socket.getRemoteSocketAddress());
	}
	
	/**
	 * Delegates the establishment of a private key to the {@link KeyDeliverer}.
	 */
	private int getPrivateKey() throws IOException {
		KeyDeliverer creator = new KeyDeliverer(this, false);
		return creator.calculateKey();
	}
	
	/**
	 * Get the local IPv4 address.
	 */
	private String getInetAddress() {
		try {
			return Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return "";
		}
	}
	
	/**
	 * Read the input from the client and prints the data in UTF format.
	 */
	@Override
	public String readMessage() throws IOException {
		DataInputStream inputStream = new DataInputStream(socket.getInputStream());
		return inputStream.readUTF();
	}
	
	/**
	 * Decrypts the cipher sent by the client. The decryption is delegated
	 * to {@link EncryptionScheme}
	 */
	private String decryptMessage(String cipher, int privateKey) {
		EncryptionScheme scheme = new EncryptionScheme(privateKey);
		return scheme.decrypt(cipher);
	}
	
	/**
	 * Print the encrypted and decrypted messages.
	 */
	private void printMessages(String cipher, String decryption) {
		System.out.println("\nReceived encrypted message:\n" + cipher + "\n");
		System.out.println("Decrypted:\n" + decryption + "\n");
	}
	
	/**
	 * Sends a message to the client.
	 */
	@Override
	public void sendMessage(String message) throws IOException {
		DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.writeUTF(message);
	}
	
	/**
	 * Retrieve the port from the args array.
	 * @param args the arguments
	 * @return the specified port, or the default port if no port was supplied
	 */
	private static int getPortFromArgs(String[] args) {
		if (args.length > 0) {
			try {
				return Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				return DEFAULT_PORT;
			}
		}
		return DEFAULT_PORT;
	}
	
	public static void main(String[] args) {
		int port = getPortFromArgs(args);
		try {
			EncryptedServer server = new EncryptedServer(port);
			server.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
