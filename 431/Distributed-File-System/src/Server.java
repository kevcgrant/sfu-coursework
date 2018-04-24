import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * CMPT 431 Assignment 5
 * <p>
 * A simple server for a TCP connection.
 * <p>
 * e.g.
 * <pre>
 * # java Server 1.2.3.4 12345 /some/existing/path
 * </pre>
 *
 * @author Kevin Grant - 301192898
 * @author Johnny Lou - 301172395
 *
 */
public class Server implements ClientHandler.ClientListener {
	
	private final ServerSocket serverSocket;
	private final String directory;
	private final InetAddress host;
	
	/** Set to keep track of clients currently running. */
	private Set<ClientHandler> clients = new CopyOnWriteArraySet<>();

	/**
	 * Open a new connection on the specified port, binding it to the specified IP address.
	 * @throws IOException if the socket does not open correctly.
	 */
	public Server(int port, String host, String directory) throws IOException {
		this.directory = directory;
		this.host = InetAddress.getByName(host);
		this.serverSocket = new ServerSocket(port, 0, this.host);
	}
	
	/**
	 * Run the server.
	 */
	public void run() {
		maybeRestoreTransactions();
		System.out.println(host.getHostAddress() + " Listening on port " + serverSocket.getLocalPort());
		while (true) {
			try {
				new Thread(new ClientHandler(serverSocket.accept(), directory, this)).start();
			} catch (SocketException e) {
				return; // socket closed
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void maybeRestoreTransactions() {
		if (TransactionManager.doesStateFileExist()) {
			System.out.println("Server crash detected. Restoring state...");
			TransactionManager.restore();
			System.out.println("State restored.");
		}
	}
	
	@Override
	public void onClientStart(ClientHandler client) {
		clients.add(client);
	}
	
	@Override
	public void onClientFinish(ClientHandler client) {
		clients.remove(client);
	}
	
	public void exit() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.out.println("Error closing server socket: " + e.getMessage());
		}
		TransactionManager.removeStateFile();
		closeClients();
		System.out.println("\nServer exited.");
	}
	
	private void closeClients() {
		Iterator<ClientHandler> iterator = clients.iterator();
		while (iterator.hasNext()) {
			ClientHandler ch = iterator.next();
			if (!ch.isPerformingAction()) {
				ch.closeSocket();
			}
		}
	}
	
	private static void printHelp() {
		System.out.println("Usage:\njava Server <ip> <port> <directory>");
	}
	
	/** Runs when interrupted (SIGINT / Ctrl+C) */
	private static void addShutdownHook(Server server) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.exit();
			}
		});
	}
	
	public static void main(String[] args) {
		if (args.length < 3) {
			printHelp();
			return;
		}
		String host = args[0];
		int port = Integer.valueOf(args[1]);
		String directory = args[2];
		
		TransactionManager.setDirectory(directory);
		Server server = null;
		try {
			server = new Server(port, host, directory);
			addShutdownHook(server);
			server.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
