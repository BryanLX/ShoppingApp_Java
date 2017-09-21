package heap;

/**
 * 
 * An exception class used when the heap is full and someone still want to
 * add something to it
 * @author group_0491
 *
 */
public class HeapFullException extends Exception {
	
	/**
	 * Initialize the HeapFullException object
	 */
	public HeapFullException() {
		super("Heap is full!");
	}
	
	/**
	 * Initialize the HeapFullException object given specific String
	 * @param message -> the message we want the user to know when this exception occurs
	 */
	public HeapFullException(String message) {
		super(message);
	}

}
