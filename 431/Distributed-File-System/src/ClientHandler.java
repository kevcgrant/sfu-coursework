import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;


/**
 * CMPT 431 Assignment 5
 * <p>
 * Runnable that handles client connections.
 *
 * @author Kevin Grant - 301192898
 * @author Johnny Lou - 301172395
 *
 */
public class ClientHandler implements Runnable {

	private static final int TIMEOUT_MS = 60000;
	
	private final Socket socket;
	private final String directory;
	private final ClientListener listener;
	
	private boolean isPerformingAction;
	
	public ClientHandler(Socket socket, String directory, ClientListener listener) {
		this.socket = socket;
		this.directory = directory;
		this.listener = listener;
		setSocketTimeout();
	}
	
	private void setSocketTimeout() {
		try {
			socket.setSoTimeout(TIMEOUT_MS);
		} catch (SocketException e) {
			throw new RuntimeException("Failed to set socket timeout:\n" + e.getMessage());
		}
	}
	
	@Override
	public void run() {
		listener.onClientStart(this);
		try {
			isPerformingAction = false;
			RequestMessage message = readMessage();
			isPerformingAction = true;
			performActionForMessage(message);
		} catch (SocketTimeoutException e) {
			return; // client failed
		} catch (SocketException e) {
			return; // socket closed by server
		} catch (IOException e) {
			// client failed
		} catch (IllegalArgumentException e) {
			sendInvalidOperationMessage(-1, "The header is malformed: " + e.getMessage());
		} finally {
			listener.onClientFinish(this);
			closeSocket();
		}
	}
	
	public void closeSocket() {
		try {
			socket.close();
		} catch (IOException e) {
			if (!socket.isClosed()) {
				throw new RuntimeException("Failed to close socket:\n" + e.getMessage());
			}
		}
	}
	
	/**
	 * Read the message from the client.
	 * @throws IOException if an error occurs when creating an input stream or client fails
	 * @throws SocketTimeoutException if the socket times out
	 * @throws IllegalArgumentException if the header is malformed
	 */
	private RequestMessage readMessage() throws IOException, SocketTimeoutException, IllegalArgumentException {
		InputStream is = socket.getInputStream();
		RequestMessage message = readHeader(is);
	
		int length = message.getContentLength();
		if (length > 0) {
			byte[] buf = new byte[length];
			readData(buf, is);
			message.setData(buf);
		} else {
			is.skip(5); // skip "/n/r/n/r/n"
		}
		return message;
	}
	
	/**
	 * Read the request header.
	 * @throws IOException if an I/O error occurs reading the header from the input stream
	 * @throws IllegalArgumentException if the header is malformed
	 */
	private RequestMessage readHeader(InputStream is) throws IOException, IllegalArgumentException {
		String header = readLine(is);
		return new RequestMessage(header);
	}
	
    private String readLine(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder(64);
        int in = -1;
        while ((in = is.read()) != '\r') {
            sb.append((char) in);
            if (in == -1) {
            	throw new IOException();
            }
        }
        return sb.toString();
    }

	private void readData(byte[] buf, InputStream is) throws IOException {
		skipNewlineCharacters(is);
		for (int i = 0; i < buf.length; i++) {
			buf[i] = (byte) is.read();
		}
	}
	
	private void skipNewlineCharacters(InputStream is) throws IOException {
		is.read();
		is.read();
		is.read();
	}
	
	private void sendMessage(String message) {
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeBytes(message);
		} catch (IOException e) {
			// client failed
		}
	}
	
	private void sendBytes(byte[] file) {
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.write(file);
		} catch (IOException e) {
			// client failed
		}
	}
	
	private void performActionForMessage(RequestMessage message) {
		switch (message.getMethod()) {
			case READ : {
				tryRead(new String(message.getData()));
				break;
			}
			case NEW_TXN : {
				createTransaction(new String(message.getData()));
				break;
			}
			case WRITE : {
				tryWrite(message);
				break;
			}
			case COMMIT : {
				tryCommit(message);
				break;
			}
			case ABORT : {
				tryAbort(message);
				break;
			} 
			default : {
				// will not execute
			}
		}
	}
	
	private void tryRead(String filename) {
		filename = filename.replace("\n", "");
		FileProcessor fp = FileProcessor.getInstance(directory, filename);
		try {
			byte[] bytes = fp.read();
			sendBytes(bytes);
		} catch (IOException e) {
			sendFileNotFoundMessage(filename);
		}
	}
	
	private void createTransaction(String filename) {
		filename = filename.replace("\n", "");
		Transaction t = new Transaction(directory, filename);
		ackTransaction(t.getId());
	}
	
	private void ackTransaction(int id) {
		ResponseMessage m = new ResponseMessage.Builder(id).ack().build();
		sendMessage(m.toString());
	}
	
	private void tryWrite(RequestMessage m) {
		Transaction t;
		try {
			t = TransactionManager.get(m.getTransactionId());
		} catch (IllegalStateException e) {
			sendInvalidTransactionIdMessage(m.getTransactionId(), e.getMessage());
			return;
		}
		try {
			int numWrites = t.write(m.getSequenceNumber(), m.getData());
			if (numWrites != 0) {
				tryCommit(t, numWrites);
			}
		} catch (IllegalStateException e) {
			sendInvalidOperationMessage(t.getId(), e.getMessage());
		} catch (MissingWritesException e) {
			sendMissingWritesMessage(t.getId(), e.getMissingNumbers());
		}
	}
	
	private void tryCommit(RequestMessage m) {
		Transaction t;
		try {
			t = TransactionManager.get(m.getTransactionId());
		} catch (IllegalStateException e) {
			sendInvalidTransactionIdMessage(m.getTransactionId(), e.getMessage());
			return;
		}
		tryCommit(t, m.getSequenceNumber());
	}
	
	private void tryCommit(Transaction t, int numWrites) {
		try {
			t.commit(numWrites);
			ackTransaction(t.getId());
		} catch (IllegalStateException e) {
			sendInvalidOperationMessage(t.getId(), e.getMessage());
		} catch (MissingWritesException e) {
			sendMissingWritesMessage(t.getId(), e.getMissingNumbers());
		} catch (IOException e) {
			ResponseMessage message = new ResponseMessage.Builder(t.getId())
					.error(ResponseMessage.ErrorCode.FILE_IO_ERROR, e.getMessage())
					.build();
			sendMessage(message.toString());		
		}
	}
	
	private void tryAbort(RequestMessage m) {
		Transaction t;
		try {
			t = TransactionManager.get(m.getTransactionId());
		} catch (IllegalStateException e) {
			sendInvalidTransactionIdMessage(m.getTransactionId(), e.getMessage());
			return;
		}
		try {
			t.abort();
			ackTransaction(t.getId());
		} catch (IllegalStateException e) {
			sendInvalidOperationMessage(t.getId(), e.getMessage());
		}
	}
	
	private void sendInvalidTransactionIdMessage(int id, String errorMessage) {
		ResponseMessage response = new ResponseMessage.Builder(id)
				.error(ResponseMessage.ErrorCode.INVALID_TRANSACTION_ID, errorMessage)
				.build();
		sendMessage(response.toString());
	}
	
	private void sendInvalidOperationMessage(int id, String errorMessage) {
		ResponseMessage m = new ResponseMessage.Builder(id)
				.error(ResponseMessage.ErrorCode.INVALID_OPERATION, errorMessage)
				.build();
		sendMessage(m.toString());
	}
	
	private void sendMissingWritesMessage(int id, List<Integer> missingWrites) {
		for (int i : missingWrites) {
			ResponseMessage response = new ResponseMessage.Builder(id).askResend(i).build();
			sendMessage(response.toString());
		}
	}
	
	private void sendFileNotFoundMessage(String filename) {
		ResponseMessage message = new ResponseMessage.Builder(0)
				.error(ResponseMessage.ErrorCode.FILE_NOT_FOUND, "File " + filename + " not found")
				.build();
		sendMessage(message.toString());
	}
	
	public interface ClientListener {
		
		/** Called when the ClientHandler starts */
		public void onClientStart(ClientHandler client);
		
		/** Called when the ClientHandler finishes */
		public void onClientFinish(ClientHandler client);
	}
	
	public boolean isPerformingAction() {
		return isPerformingAction;
	}
}
