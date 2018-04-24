import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CMPT 431 Assignment 5
 * <p>
 * Manages transactions.
 *
 * @author Kevin Grant - 301192898
 * @author Johnny Lou - 301172395
 *
 */
public class TransactionManager {

	/** The transaction state file name */
	private static final String STATE_FILE = ".transactions.ser";
	
	/** Atomic counter used to assign ID's to transactions */
	private static final AtomicInteger ID_COUNTER = new AtomicInteger();
	
	/** The list of transactions currently in the system */
	private static final Map<Integer, Transaction> TRANSACTIONS = new ConcurrentHashMap<>();
	
	/** Absolute file path for the transactions file. Must be initialized by the {@link #setDirectory(String)} method. */
	private static File absoluteFilePath;
	
	/**
	 * Retrieves the transaction with the given ID.
	 * @throws IllegalStateException if the transaction does not exist
	 */
	public static Transaction get(int id) throws IllegalStateException {
		Transaction t = TRANSACTIONS.get(id);
		if (t == null) {
			throw new IllegalStateException("Transaction " + id + " does not exist.");
		}
		return t;
	}
	
	/**
	 * Writes the in-memory transactions to disk sequentially.
	 */
	public static void writeToDiskSequentially() {
		new TransactionWriter().run();
	}
	/**
	 * Writes the in-memory transactions to disk, saving the state. Executed on a new thread.
	 */
	public static void writeToDisk() {
		new Thread(new TransactionWriter()).start();
	}
	
	/** Runnable for writing the transactions to disk. */
	private static class TransactionWriter implements Runnable {
		
		private static final Semaphore LOCK = new Semaphore(1);
		
		@Override
		public void run() {
			try {
				LOCK.acquireUninterruptibly();
				FileOutputStream fileOutput = new FileOutputStream(absoluteFilePath);
				ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
				objectOutput.writeObject(TRANSACTIONS);
				objectOutput.flush();
				fileOutput.flush();
				fileOutput.getFD().sync();
				objectOutput.close();
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				LOCK.release();
			}
		}
	}
	
	/**
	 * Restore the transactions from disk, and any invalid file states.
	 */
	public static void restore() {
		restoreTransactions();
		restoreFileStates();
	}
	
	@SuppressWarnings("unchecked")
	private static void restoreTransactions() {
		Map<Integer, Transaction> transactions = null;
		try {
			FileInputStream fileInput = new FileInputStream(absoluteFilePath);
			ObjectInputStream objectInput = new ObjectInputStream(fileInput);
			transactions = (ConcurrentHashMap<Integer, Transaction>) objectInput.readObject();		
			objectInput.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		for (Integer key : transactions.keySet()) {
			TRANSACTIONS.put(key, transactions.get(key));
		}
        ID_COUNTER.set(TRANSACTIONS.size());
	}
	
	private static void restoreFileStates() {
		for (Transaction t : TRANSACTIONS.values()) {
			t.maybeRestoreAssociatedFile();
		}
	}
	
	/**
	 * Generates a unique ID for a transaction.
	 */
	public static int generateId() {
		return ID_COUNTER.getAndIncrement();
	}
	
	/**
	 * Adds the transaction to the map.
	 */
	public static void addTransaction(Transaction t) {
		TRANSACTIONS.put(t.getId(), t);
		writeToDisk();
	}
	
	public static void setDirectory(String directory) {
        new File(directory).mkdirs();
		absoluteFilePath = new File(directory, STATE_FILE);
	}
	
	public static boolean doesStateFileExist() {
		return absoluteFilePath.exists();
	}
	
	public static void removeStateFile() {
		absoluteFilePath.delete();
	}
}
