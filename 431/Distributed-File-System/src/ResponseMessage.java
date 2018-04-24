
/**
 * CMPT 431 Assignment 5
 * <p>
 * Represents a response message.
 *
 * @author Kevin Grant - 301192898
 * @author Johnny Lou - 301172395
 *
 */
public class ResponseMessage {

	public enum Method {
		ACK, ASK_RESEND, ERROR
	}
	
	public enum ErrorCode {
		NO_ERROR(0), INVALID_TRANSACTION_ID(201), INVALID_OPERATION(202), FILE_IO_ERROR(205), FILE_NOT_FOUND(206);
		
		private final int code;
		
		private ErrorCode(int code) {
			this.code = code;
		}
		
		public int getCode() {
			return code;
		}
	}
	
	private final Method method;
	private final ErrorCode errorCode;
	private final String reason;
	private final int transactionId;
	private final int sequenceNumber;
	private final int contentLength;
	
	private ResponseMessage(Builder b) {
		this.method = b.method;
		this.errorCode = b.errorCode;
		this.reason = b.reason;
		this.transactionId = b.transactionId;
		this.sequenceNumber = b.sequenceNumber;
		this.contentLength = b.contentLength;
	}
	
	@Override
	public String toString() {
		String headers = 
				method.toString() + " " + transactionId + " " + 
				sequenceNumber + " " + errorCode.code + " " + contentLength + "\r\n\r\n";
		return reason.isEmpty() ? headers + "\r\n" : headers + reason;
	}
	
	public static class Builder {
		private final int transactionId;
		private Method method;
		private ErrorCode errorCode = ErrorCode.NO_ERROR;
		private String reason = "";
		private int sequenceNumber = -1;
		private int contentLength = 0;
		
		public Builder(int transactionId) {
			this.transactionId = transactionId;
		}
		
		public Builder ack() {
			this.method = Method.ACK;
			return this;
		}
		
		public Builder askResend(int sequenceNumber) {
			this.sequenceNumber = sequenceNumber;
			this.method = Method.ASK_RESEND;
			return this;
		}
		
		public Builder error(ErrorCode errorCode, String reason) {
			this.errorCode = errorCode;
			this.method = Method.ERROR;
			this.reason = reason;
			this.contentLength = reason.length();
			return this;
		}
		
		public ResponseMessage build() {
			return new ResponseMessage(this);
		}
	}
}
