/**
 * CMPT 431 Assignment 5
 * <p>
 * Represents a request message.
 *
 * @author Kevin Grant - 301192898
 * @author Johnny Lou - 301172395
 *
 */
public class RequestMessage {

	public enum Method {
		READ, NEW_TXN, WRITE, COMMIT, ABORT
	}

	private final Method method;
	private final int transactionId;
	private final int sequenceNumber;
	private final int contentLength;
	private byte[] data;
	
	/**
	 * Creates a new request message from the given header
	 * @throws IllegalArgumentException if the header is not valid
	 */
	public RequestMessage(String header) throws IllegalArgumentException {
		String[] fields = header.split("\r\n\r\n");
		String[] headerFields = fields[0].split(" ");
		
		try {
			this.method = Method.valueOf(headerFields[0]);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(headerFields[0] + " is not a valid method");
		}
		try {
			this.transactionId = Integer.valueOf(headerFields[1]);
			this.sequenceNumber = Integer.valueOf(headerFields[2]);
			this.contentLength = Integer.valueOf(headerFields[3]);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		if (method.equals(Method.WRITE) && sequenceNumber < 1) {
			throw new IllegalArgumentException("Sequence number for WRITE must be at least 1");
		}
	}

	public Method getMethod() {
		return method;
	}

	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public int getContentLength() {
		return contentLength;
	}
}
