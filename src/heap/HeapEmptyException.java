package heap;
/**
 * 
 * An exception class used when the heap is empty and someone still want to
 * remove something from it
 * @author group_0491
 *
 */
public class HeapEmptyException extends Exception {
	
	/**
	 * Initialize the HeapEmptyException object
	 */
	public HeapEmptyException() {
		super("Heap is empty!");
	}
	
	/**
	 * Initialize the HeapEmptyException object given specific String
	 * @param message -> the message we want the user to know when this exception occurs
	 */
	public HeapEmptyException(String message) {
		super(message);
	}

}