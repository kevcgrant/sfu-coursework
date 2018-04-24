import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * CMPT 471 Assignment 3
 * <p>
 * A simple client for a TCP connection.
 * A host and port may be specified when running the client, otherwise a default of the 
 * local IPv4 address and port 12323 are used.
 * <p>
 * e.g.
 * <pre>
 * # java Client 172.20.3.2 12345
 * # java Client
 * </pre>
 *
 * @author Kevin Grant - 301192898
 *
 */
public class Client {

	private static final String DEFAULT_HOST = getInetAddress();
	private static final int DEFAULT_PORT = 12323;
	
	private Socket socket;
	
	/**
	 * Get the local IPv4 address.
	 */
	private static String getInetAddress() {
		try {
			return Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new RuntimeException("Failed to retrieve Ipv4 address");
		}
	}
	
	/**
	 * Connect to the server.
	 * @param host	the hostname of the server
	 * @param port	the port the server is listening on
	 * @throws IOException if the connection fails
	 */
	private void connect(String host, int port) throws IOException {
		System.out.println("Connecting to " + host + ":" + port);
		socket = new Socket(host, port);
		System.out.println("Connected to " + socket.getRemoteSocketAddress());
	}
	
	/**
	 * Sends a message to the server.
	 * @param socket	the client socket
	 * @throws IOException if an error occurs when writing.
	 */
	private void sendMessage() throws IOException {
		OutputStream os = socket.getOutputStream();
		DataOutputStream dos = new DataOutputStream(os);
		dos.writeUTF("Hello!");
	}
	
	/**
	 * Read the message from the server.
	 * @param socket 	the client socket
	 * @throws IOException if an error occurs when reading from the server.
	 */
	private void readMessage() throws IOException {
		InputStream is = socket.getInputStream();
		DataInputStream dis = new DataInputStream(is);
		System.out.println(dis.readUTF());
	}
	
	/**
	 * Close the socket.
	 * @throws IOException if an error occurs closing the socket.
	 */
	private void closeSocket() throws IOException {
		socket.close();
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
		Client client =  new Client();
		String host = getHostFromArgs(args);
		int port = getPortFromArgs(args);
		try {
			client.connect(host, port);
			client.sendMessage();
			client.readMessage();
			client.closeSocket();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
