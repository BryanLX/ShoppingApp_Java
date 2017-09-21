package heap;
/**
 * 
 * The heapItem class , we want to use it in the heap
 * @author group_0491
 *
 */
public interface HeapItem extends Comparable<HeapItem>{
	
	/**
	 * set the index of the heapitem
	 * @param index -> the index we want to set
	 */
	public void setHeapIndex(int index);
	
	/**
	 * get the heap index of a heap item
	 * @return -> the heap index of this heap item
	 */
	public int getHeapIndex();

}
