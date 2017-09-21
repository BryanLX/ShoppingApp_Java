package heap;

/**
 * This class is a class implement the HeapItem 
 * we only use this class while find the shortest deliver path
 * @author group_0491
 *
 */
public class City_Node implements HeapItem{
	
	/**
	 * the String representing a city
	 */
	public String city;
	/**
	 * the parent node of this node
	 */
	public City_Node parent;
	
	/**
	 * this cost is just like the f cost in the A -star alogrithm
	 */
	public int distance_cost;
	
	/**
	 * the index of this node in the heap
	 */
	public int heapindex;
	
	/**
	 * initialize a City_Node object
	 * @param city -> the String representing a city
	 */
	public City_Node(String city){
		this.city = city;
	}
	
	/**
	 * return the difference between two heapitem
	 */
	@Override
	public int compareTo(HeapItem o) {
		// TODO Auto-generated method stub
		return this.distance_cost - ((City_Node)o).distance_cost;
	}

	/**
	 * Set the heapindex of the City_Node
	 */
	@Override
	public void setHeapIndex(int index) {
		// TODO Auto-generated method stub
		this.heapindex = index;
		
	}
	
	/**
	 * get the heap index of this City_Node
	 * @return heapindex
	 */
	@Override
	public int getHeapIndex() {
		// TODO Auto-generated method stub
		return heapindex;
	}

}
