import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * CMPT 431 Assignment 5
 * <p>
 * Manages reading and writing to files, and associated locking.
 *
 * @author Kevin Grant - 301192898
 * @author Johnny Lou - 301172395
 *
 */
public class FileProcessor {

	private static final Map<File, FileProcessor> INSTANCES = new ConcurrentHashMap<>();
	
	private final File file;
	private final Path path;
	private final ReentrantReadWriteLock lock;
	
	public static FileProcessor getInstance(String directory, String filename) {
		return getInstance(new File(directory, filename));
	}
	
	public static synchronized FileProcessor getInstance(File file) {
		FileProcessor fp = INSTANCES.get(file);
		if (fp == null) {
			fp = new FileProcessor(file);
			INSTANCES.put(file, fp);
		}
		return fp;
	}
	
	private FileProcessor(File file) {
		this.file = file;
		this.path = file.toPath();
		this.lock = new ReentrantReadWriteLock();
	}
	
	/**
	 * Appends the bytes to the file on disk. The file is locked with a write lock.
	 * No reading can be performed while writing.
	 * @throws IOException if the write fails
	 */
	public void write(byte[] bytes, Transaction t) throws IOException {
		try {
			lock.writeLock().lock();
			t.setFileSizeBeforeCommit(file.length());
			TransactionManager.writeToDiskSequentially();
			FileOutputStream out = new FileOutputStream(file, true);
			out.write(bytes);
			out.flush();
			out.getFD().sync();
			out.close();
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Reads the bytes from the file on disk. The file is locked with a read lock.
	 * Multiple reads may be executed concurrently.
	 * @throws IOException if the read failed
	 */
	public byte[] read() throws IOException {
		try {
			lock.readLock().lock();
			return Files.readAllBytes(path);
		} finally {
			lock.readLock().unlock();
		}
	}
}
