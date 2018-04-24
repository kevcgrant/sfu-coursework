import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * CMPT 471 Assignment 3
 * <p>
 * A simple server for a TCP connection.
 * A port may be specified when running the server, otherwise a default of 12323 is used.
 * <p>
 * e.g.
 * <pre>
 * # java Server 12345
 * # java Server
 * </pre>
 * You may also pass a parameter that will cause the server to wait after making the 
 * connection to be able to turn down an interface to capture retransmission:
 * <pre>
 * # java Server wait
 * # java Server 12345 wait
 * </pre>
 *
 * @author Kevin Grant - 301192898
 *
 */
public class Server {

	private static final int DEFAULT_PORT = 12323;
	private static final int SLEEP_TIME_MS = 10000;
	
	private ServerSocket socket;
	private boolean doWait;
	
	/**
	 * Open a new connection on the default port.
	 * @throws IOException if the socket does not open correctly.
	 */
	public Server() throws IOException {
		new Server(DEFAULT_PORT, false);
	}
	
	/**
	 * Open a new connection on the specified port.
	 * @throws IOException if the socket does not open correctly.
	 */
	public Server(int port, boolean doWait) throws IOException {
		this.doWait = doWait;
		initializeSocket(port);
	}
	
	/**
	 * Initialize the socket.
	 * @param port	the port to initialize the socket with
	 * @throws IOException if the socket does not open correctly.
	 */
	private void initializeSocket(int port) throws IOException {
		socket = new ServerSocket(port);
	}
	
	/**
	 * Run the server.
	 */
	private void runServer() {
		while (true) {
			try {
				Socket server = acceptConnection();
				maybeWait();
				readInput(server);
				closeSocket(server);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	/**
	 * Wait for a connection to the server socket and accept it.
	 * @return the new socket
	 * @throws IOException if an error occurs while waiting to accept a connection.
	 */
	private Socket acceptConnection() throws IOException {
		System.out.println(getInetAddress() + " Listening on port " + socket.getLocalPort() + "...");
		Socket server = socket.accept();
		System.out.println("Accepted connection from " + server.getRemoteSocketAddress());
		
		return server;
	}
	
	/**
	 * Wait for 10 seconds, if the "wait" parameter was passed to the program.
	 */
	private void maybeWait() {
		if (doWait) {
			try {
				Thread.sleep(SLEEP_TIME_MS);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			} 
		}
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
	 * @param server	the socket
	 * @throws IOException if an error occurs when creating an input stream.
	 */
	private void readInput(Socket server) throws IOException {
		DataInputStream inputStream = new DataInputStream(server.getInputStream());
		System.out.println(inputStream.readUTF());
	}
	
	/**
	 * Send a goodbye message to the client and close the socket.
	 * @param server	the socket
	 * @throws IOException if there is an error creating the output stream.
	 */
	private void closeSocket(Socket server) throws IOException {
		DataOutputStream outputStream = new DataOutputStream(server.getOutputStream());
        outputStream.writeUTF("Closing the connection! Bye!");
        server.close();
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
	
	/**
	 * Get the argument that will cause the server to wait after connecting to 
	 * read input. This allows time to turn down the interface and capture TCP retransmission.
	 * @param args the arguments
	 */
	private static boolean getDoWaitFromArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("wait")) return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		int port = getPortFromArgs(args);
		boolean doWait = getDoWaitFromArgs(args);
		try {
			Server server = new Server(port, doWait);
			server.runServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
