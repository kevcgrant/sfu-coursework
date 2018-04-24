package encrypted;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import encrypted.schemes.EncryptionScheme;

/**
 * CMPT 471 Assignment 4
 * <p>
 * A simple client for a TCP connection, that uses a secret key encryption scheme framework.
 * A host and port may be specified when running the client, otherwise a default of the 
 * local IPv4 address and port 12323 are used.
 * <p>
 * e.g.
 * <pre>
 * # java EncryptedClient 172.20.3.2 12345
 * # java EncryptedClient
 * </pre>
 *
 * @author Kevin Grant - 301192898
 *
 */
public class EncryptedClient implements EncryptedHost {

	private static final String DEFAULT_HOST;
	private static final int DEFAULT_PORT = 12323;
	
	/** Get the local IPv4 address. */
	static {
		try {
			DEFAULT_HOST = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new RuntimeException("Failed to retrieve Ipv4 address");
		}
	}
	
	/** The client socket used for connecting to the server */
	private Socket socket;
	
	/** The host (either hostname or IP address) */
	private String host;
	
	/** The port used by the socket */
	private int port;
	
	public EncryptedClient(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	/**
	 * Run the client. It connects to the server, uses the Diffie-Hellman algorithm to 
	 * establish a secret key with the server, reads user input for a message, then encrypts
	 * the message and sends it to the server.
	 */
	@Override
	public void run() throws IOException {
		connect();
		int privateKey = getPrivateKey();
		String plaintext = getUserInput();
		sendEncryptedMessage(plaintext, privateKey);
		socket.close();
	}
	
	/**
	 * Connect to the server.
	 * @throws IOException if the connection fails
	 */
	private void connect() throws IOException {
		System.out.println("Connecting to " + host + ":" + port);
		socket = new Socket(host, port);
		System.out.println("Connected to " + socket.getRemoteSocketAddress());
	}
	
	/**
	 * Delegates the establishment of a private key to the {@link KeyDeliverer}.
	 */
	private int getPrivateKey() throws IOException {
		KeyDeliverer creator = new KeyDeliverer(this, true);
		return creator.calculateKey();
	}
	
	/**
	 * Reads the user input that will be used as the message sent to the server.
	 */
	private String getUserInput() {
		Scanner input = new Scanner(System.in);
		try {
			String message = "";
			System.out.println("Please enter a message: ");
			do {
				message = input.nextLine();
			} while (message.length() < 1);
			return message;
		} finally {
			input.close();
		}
	}
	
	/**
	 * Encrypts the plaintext message and sends it to the server.
	 * The encryption is delegated to {@link EncryptionScheme}.
	 */
	private void sendEncryptedMessage(String plaintext, int privateKey) throws IOException {
		EncryptionScheme scheme = new EncryptionScheme(privateKey);
		String cipher = scheme.encrypt(plaintext);
		printMessage(cipher);
		sendMessage(cipher);
	}
	
	/**
	 * Print the encrypted message.
	 */
	private void printMessage(String cipher) {
		System.out.println("\nSending encrypted message:\n" + cipher + "\n");
	}
	
	/**
	 * Sends a message to the server.
	 */
	@Override
	public void sendMessage(String message) throws IOException {
		OutputStream os = socket.getOutputStream();
		DataOutputStream dos = new DataOutputStream(os);
		dos.writeUTF(message);
	}
	
	
	/**
	 * Read the message from the server.
	 */
	@Override
	public String readMessage() throws IOException {
		InputStream is = socket.getInputStream();
		DataInputStream dis = new DataInputStream(is);
		return dis.readUTF();
	}
	
	/**
	 * Retrieve the host from the args.
	 * @param args the arguments
	 * @return the specified host, or the default host if no host was provided
	 */
	private static String getHostFromArgs(String[] args) {
		if (args.length > 0) {
			return args[0];
		} else {
			return DEFAULT_HOST;
		}
	}
	
	/**
	 * Retrieve the port number from the args.
	 * @param args the arguments
	 * @return the specified port, or the default port if no port was provided
	 */
	private static int getPortFromArgs(String[] args) {
		if (args.length > 1) {
			try {
				return Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				return DEFAULT_PORT;
			}
		} else {
			return DEFAULT_PORT;
		}
	}
	
	public static void main(String args[]) {
		String host = getHostFromArgs(args);
		int port = getPortFromArgs(args);
		EncryptedClient client =  new EncryptedClient(host, port);
		try {
			client.run();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
