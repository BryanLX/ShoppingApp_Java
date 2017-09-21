package heap;
/**
 * 
 * This class is the heap we used in Assignment,
 * now we use this as a container to implement the shortest path algorithm
 * @author group_0491
 *
 * @param <T>
 */
public class Heap<T extends HeapItem> {
	
	// Note the T is a parameter representing a type that extends the HeapItem interface
	// This a new way to use inheritance!

	protected T[] items; // Array that is used to store heap items. items[0] is the highest priority element.
	protected int maxHeapSize; // The capacity of the heap
	protected int currentItemCount; // How many elements we have currently on the heap
	
	/**
	 * Initialize a heap object
	 * @param maxHeapSize -> the max size of the heap
	 */
	public Heap(int maxHeapSize) {
		this.maxHeapSize = maxHeapSize;
		items = (T[]) new HeapItem[maxHeapSize];
		currentItemCount = 0; // heap is empty!
	}
	
	/**
	 * a class to check whether the heap is empty
	 * @return return true if the heap is empty
	 */
	public boolean isEmpty() {
		return currentItemCount == 0;
	}
	
	/**
	 * a class to check whether the heap is full
	 * @return return true if the heap is full
	 */
	public boolean isFull() {
		return currentItemCount == maxHeapSize;
	}

	/**
	 * add a new node to the heap if the heap is not full
	 * @param item -> the item we want to add to the heap
	 * @throws HeapFullException -> exception when the heap is full
	 */
	public void add(T item) throws HeapFullException
	// Adds item T to its correct position on the heap
	{
		if (isFull())
			throw new HeapFullException();
		else {
			item.setHeapIndex(currentItemCount);
			items[currentItemCount] = item;  // the element is added to the bottom
			sortUp(item); // Move the element up to its legitimate place. Check the diagram on the handout!
			currentItemCount++;
		}
	}
	
	/**
	 * A class to check whether the heap contains a specific item
	 * @param item -> the item we want to check
	 * @return return true if the heap contains the item
	 */
	public boolean contains(T item)
	// Returns true if item is on the heap
	// Otherwise returns false
	{
		return items[item.getHeapIndex()].equals(item);
	}

	/**
	 * return the number of the item in the heap now
	 * @return the current size of the heap
	 */
	public int count() {
		return currentItemCount;
	}

	/**
	 * update the position of the item in the heap
	 * @param item -> the item we want to update
	 */
	public void updateItem(T item) {
		sortUp(item);
	}
	
	/**
	 * return and remove the first element in the heap if heap not empty
	 * @return the first item in the previous heap
	 * @throws HeapEmptyException -> exception when the heap is empty
	 */
	public T removeFirst() throws HeapEmptyException
	// Removes and returns the element sitting on top of the heap
	{
		if (isEmpty())
			throw new HeapEmptyException();
		else {
			T firstItem = items[0]; // element of top of the heap is stored in firstItem variable
			currentItemCount--;
			items[0] = items[currentItemCount]; //last element moves on top
			items[0].setHeapIndex(0);
			sortDown(items[0]); // move the element to its legitimate position. Please check the diagram on the handout.
			return firstItem;
		}
	}
	
	/**
	 * Sout up the item's index position in the heao
	 * @param item -> the item we want to sort up
	 */
	private void sortUp(T item) {
		// Implement this method according to the diagram on the handout.
		// Also: the indices of children and parent elements satisfy some relationships.
		// The formulas are on the handout.
		
		// if item not in the heap, do nothing
		if (contains(item) == false) return;
		int index = item.getHeapIndex();
		// if already at 0 index , do nothing
		if (index == 0) return;
		int parent_index = (index - 1) / 2;
		
		// if this item is less than its parent, change the position with its parent,recursion follows
		if (items[index].compareTo(items[parent_index]) < 0){
			T change = items[index];
			items[index] = items[parent_index];
			items[index].setHeapIndex(index);
			items[parent_index] = change;
			items[parent_index].setHeapIndex(parent_index);
			sortUp(change);
		}
		
		else
			return;
	}
	
	/**
	 * sort down the index position of the item in the heap
	 * @param item -> the item we want to sort down
	 */
	private void sortDown(T item) {
		// Implement this method according to the diagram on the handout.
				// Also: the indices of children and parent elements satisfy some relationships.
				// The formulas are on the handout.
		
		// if item not in the heap, do nothing
		if (contains(item) == false) return;
		
		int index = item.getHeapIndex();
		int right = index * 2 + 2;
		int left = index * 2 + 1;
		boolean have_l,have_r;
		have_l = (left + 1 <= currentItemCount);  // have left or not
		have_r = right + 1 <= currentItemCount; // have right or not
		
		// no left child, do nothing
		if (have_l == false) return; 
		
		// only have left child, and this item is bigger than its left child
		// change the position with its left child
		if (have_l && have_r == false && items[index].compareTo(items[left]) > 0){
			T change = items[index];
			items[index] = items[left];
			items[index].setHeapIndex(index);
			items[left] = change;
			items[left].setHeapIndex(left);
			sortDown(change);
		}
		
		// have both children
		if (have_r && have_l ){
			boolean l_r = item.compareTo(items[right]) <= 0;
			boolean l_l = item.compareTo(items[left]) <= 0;
			boolean left_less = items[left].compareTo(items[right]) <= 0;
			
			// if this item is less than both of its children, do nothing
			if (l_r && l_l) return;
			
			// if one of the children is less than this item
			else{
				// if left child is less than right child, change the positiong of
				// this item and its left child
				if(left_less){
					T change = items[index];
					items[index] = items[left];
					items[index].setHeapIndex(index);
					items[left] = change;
					items[left].setHeapIndex(left);
					sortDown(change);
				}
				// if right child is less than left child, change the position of
				// this item and its right child
				else{
					T change = items[index];
					items[index] = items[right];
					items[index].setHeapIndex(index);
					items[right] = change;
					items[right].setHeapIndex(right);
					sortDown(change);
				}
			}
			
		}
	}
	
	// You may implement additional helper methods if desired. Make sure to make them private!
}