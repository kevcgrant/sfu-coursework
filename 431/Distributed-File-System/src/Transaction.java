import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Semaphore;


/**
 * CMPT 431 Assignment 5
 * <p>
 * Represents transactions in the file system
 *
 * @author Kevin Grant - 301192898
 * @author Johnny Lou - 301172395
 *
 */
public class Transaction implements Serializable {
	
	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	
	/** Static serial ID for future state restores */
	private static final long serialVersionUID = 1113799434508676095L;
	
	private transient Semaphore actionLock = new Semaphore(1);
	private transient Semaphore writeLock = new Semaphore(1);
	
	private final File file;
	private final Map<Integer, byte[]> writes = new ConcurrentSkipListMap<>();
	private final int id = TransactionManager.generateId();
	
	private Set<Integer> writesRequiredForCommit = new HashSet<>();
	private long fileSizeBeforeCommit = -1L;
	private int fullyReceivedUpTo = 0;
	private int largestSequenceNumber;
	private boolean isAborted;
	private boolean isCommitted;
	
	/**
	 * Creates a new transaction with a unique ID.
	 */
	public Transaction(String directory, String filepath) {
		this.file = new File(directory, filepath);
		TransactionManager.addTransaction(this);
	}
	
	/**
	 * Writes the data to the in-memory file.
	 * @return 0 if the server should continue, or the number of writes if the server should
	 *         commit the transaction (all required messages have been resent)
	 * @throws IllegalStateException if the transaction has already been committed or aborted, and the
	 *         received message is not a duplicate
	 * @throws MissingWritesException if the sequence number is greater than what is expected  
	 */
	public int write(int sequenceNumber, byte[] data) throws IllegalStateException, MissingWritesException {
		actionLock.acquireUninterruptibly();
		if (isCompleted()) {
			if (!isResentMessage(sequenceNumber)) {
				throwExceptionForInvalidAction();
			}
			actionLock.release();
			return 0;
		} else {
			actionLock.release();
			writeLock.acquireUninterruptibly();
			byte[] previous = writes.putIfAbsent(sequenceNumber, data);
			int returnVal = 0;
			if (previous == null) {
				maybeIncrementLargestSequence(sequenceNumber);
				try {
					checkForMissingWrites(sequenceNumber);
				} finally {
					returnVal = maybeCommit(sequenceNumber);
					TransactionManager.writeToDisk();
					writeLock.release();
				}
			}
			writeLock.release();
			return returnVal;
		}
	}
	
	private boolean isCompleted() {
		return isCommitted || isAborted;
	}
	
	private boolean isResentMessage(int sequenceNumber) {
		return writes.containsKey(sequenceNumber);
	}
	
	private void maybeIncrementLargestSequence(int sequenceNumber) {
		if (largestSequenceNumber < sequenceNumber) {
			largestSequenceNumber = sequenceNumber;
		}
	}
	
	private void checkForMissingWrites(int sequenceNumber) throws MissingWritesException {
		List<Integer> missing = getMissingWrites(sequenceNumber);
		if (missing.size() != 0) {
			writeLock.release();
			throw new MissingWritesException(missing);
		} else {
			fullyReceivedUpTo++;
			while (writes.containsKey(fullyReceivedUpTo + 1)) {
				fullyReceivedUpTo++;
			}
		}
	}

	private int maybeCommit(Integer sequenceNumber) {
			if (writesRequiredForCommit.remove(sequenceNumber)) {
				if (writesRequiredForCommit.size() == 0) {
					writeLock.release();
					return writes.size();
				}
			} 
			return 0;
	}
	
	private void throwExceptionForInvalidAction() throws IllegalStateException {
		actionLock.release();
		String action = isAborted ? "aborted" : "committed";
		throw new IllegalStateException("Transaction " + id + " has already been " + action);
	}

	/**
	 * Commits the in-memory file to disk.
	 * @throws IllegalStateException if the transaction has already been aborted or committed, or more writes were received
	 *         than specified by the commit message
	 * @throws IOException if the file cannot be created or written to
	 * @throws MissingWritesException if not all writes for this transaction have been received
	 */
	public void commit(int numWrites) throws IllegalStateException, IOException, MissingWritesException {
		actionLock.acquireUninterruptibly();
		writeLock.acquireUninterruptibly();
		int size = writes.size();
		if (isCommitted) {
			releaseLocks();
			return; // resend ack
		} else if (isAborted) {
			writeLock.release();
			throwExceptionForInvalidAction();
		} else if (isMissingWrites(size, numWrites)) {
			handleMissingWrites(numWrites);
		} else if (numWrites < size) {
			releaseLocks();
			throw new IllegalStateException("Received " + size + " writes for transaction " + id + ", but COMMIT specifies only " + numWrites + ".");
		}  else {
			tryToCommitFile();
			isCommitted = true;
			TransactionManager.writeToDisk();
			flushData();
		}
		releaseLocks();
	}
	
	private void releaseLocks() {
		writeLock.release();
		actionLock.release();
	}
	
	private boolean isMissingWrites(int size, int numWrites) {
		return size < numWrites || numWrites < largestSequenceNumber || fullyReceivedUpTo != largestSequenceNumber;
	}
	
	private void handleMissingWrites(int numWrites) throws MissingWritesException {
		List<Integer> missingWrites = getMissingWrites(numWrites);
		writesRequiredForCommit.addAll(missingWrites);
		releaseLocks();
		throw new MissingWritesException("Transaction " + id + " has not received all necessary writes", missingWrites);
	}
	
	private List<Integer> getMissingWrites(int numWrites) {
		List<Integer> missing = new ArrayList<>();
		Set<Integer> keySet = writes.keySet();
		for (int i = fullyReceivedUpTo + 1; i <= numWrites; i++) {
			if (!keySet.contains(i)) {
				missing.add(i);
			}
		}
		return missing;
	}
	
	private void tryToCommitFile() throws IOException {
		try {
			byte[] bytes = getCombinedByteArrays();
			FileProcessor p = FileProcessor.getInstance(file);
			p.write(bytes, this);
		} finally {
			releaseLocks();
		}
	}
	
	private byte[] getCombinedByteArrays() {
		int size = getTotalBytes();
		byte[] bytes = new byte[size];
		int index = 0;
		for (byte[] b : writes.values()) {
			int length = b.length;
			for (int i = 0; i < length; i++) {
				bytes[index + i] = b[i];
			}
			index += length;
		}
		
		return bytes;
	}
	
	public int getTotalBytes() {
		int size = 0;
		for (byte[] b : writes.values()) {
			size += b.length;
		}
		return size;
	}
	
	private void flushData() {
		for (Integer key : writes.keySet()) {
			writes.put(key, EMPTY_BYTE_ARRAY);
		}
	}
	
	/**
	 * Aborts the transaction.
	 * @throws IllegalStateException if the transaction has already been aborted or committed.
	 */
	public void abort() throws IllegalStateException {
		actionLock.acquireUninterruptibly();
		if (isCommitted) {
			throwExceptionForInvalidAction();
		} else if (isAborted) {
			actionLock.release();
			return; // resend ack
		} else {
			isAborted = true;
			writes.clear();
			TransactionManager.writeToDisk();
			actionLock.release();
		}
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		actionLock = new Semaphore(1);
		writeLock = new Semaphore(1);
	}
	
	public int getId() {
		return id;
	}
	
	public void setFileSizeBeforeCommit(long size) {
		this.fileSizeBeforeCommit = size;
	}

	/**
	 * Restore the transaction's file if the server crashed during a commit.
	 */
	public void maybeRestoreAssociatedFile() {
		if (didCrashWhileCommitting()) {
			if (isFileMalformed()) {
				removePartialData();
			} else {
				isCommitted = true;
			} 
		}
	}
	
	private boolean didCrashWhileCommitting() {
		return fileSizeBeforeCommit != -1L && isCommitted == false;
	}
	
	private boolean isFileMalformed() {
		long difference = file.length() - fileSizeBeforeCommit;
		long transactionBytes = (long) getTotalBytes();
		return difference != 0 && difference < transactionBytes;
	}
	
	private void removePartialData() {
		try (RandomAccessFile raf = new RandomAccessFile(file, "rwd")) {
			raf.setLength(fileSizeBeforeCommit);
		} catch (FileNotFoundException e) {
			return; // no file
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fileSizeBeforeCommit = -1L;
		}
	}
}
