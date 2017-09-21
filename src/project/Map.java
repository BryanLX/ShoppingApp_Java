package project;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import heap.City_Node;
import heap.Heap;
import heap.HeapEmptyException;
import heap.HeapFullException;

/**
 * This class store all the map information of the whole process, including all the city, distribution center
 * and distance between each cities, it mainly used for generating the cost-efficient route for delivery product
 * from distribution to each city.
 * @author group
 *
 */
public class Map {
	/**
	 * max is integer variable used to limit the size of the grid
	 * distance is a list that store the distance information of the grid
	 * cities is for recording the city name in this map
	 * Dcs records all the distribution center in this map
	 */
	protected int max;
	protected int[][] distance;
	protected List<String> cities;
	protected List<String> DCs;
	
	/**
	 * Constructor:Create a instance of a Map
	 * @param max  the max size of the grid
	 */
	public Map(int max) {
		// first give the size of the grid by reading the file
		// for convenient , give it the size ten before start the method
		this.max = max;
		this.distance = new int[max][max];
		this.cities = new ArrayList<String>();
		this.DCs = new ArrayList<String>();
	}
	
	/**
	 * This method add a city into cities List if it is not already in
	 * in the cities List; If already exist, do nothing;
	 * @param city  The name of the city to be added
	 */
	public void addCity(String city) {
		if(!cities.contains(city)) {
			EnlargeDistance();
			cities.add(city);
		}
	}
	
	/**
	 * This method add a distribution center into the list of DCS, if the dc is
	 * not yet in the cities List, add it.
	 * @param dc  the name of the distribution center
	 * @return  boolean true if distribution center is successfully added, false otherwise
	 */
	public boolean addDC(String dc) {
		if(!DCs.contains(dc)) {
			DCs.add(dc);
			return true;
		}
		return false;
	}
	
	/**
	 * This method add two city into the cities list if it is not in the list, then reset the distance between
	 * these two city to the given distance.
	 * @param cityA  the name of the first city
	 * @param cityB  the name of the second city
	 * @param dist  the distance between cityA and cityB
	 */
	public void addFRs(String cityA,String cityB,int dist){
		addCity(cityA);
		addCity(cityB);
		int index_A = cities.indexOf(cityA);
		int index_B = cities.indexOf(cityB);
		if(distance[index_A][index_B] ==0){
			distance[index_A][index_B] = dist;
			distance[index_B][index_A] = dist;
		}
	}
	
	/**
	 * This method use Dijkstra algorithm to find the shortestRoute(cheapest) from the nearby distribution 
	 * center to that city : if CityA can't reach the distribution center anyway, return an empty ArrayList;
	 * if cityA itself is a distribution center, return an ArrayList contains only cityA;else return the path
	 * to the nearest Distribution;
	 * @param cityA  the name of this city
	 * @return  a arrayList of string which contain the shortestRoute,a empty arrayList is returned if no route find
	 */
	public ArrayList<String> ShortestRoute(String cityA) {
		Set<String> close = new HashSet<String>();
		
		Heap<City_Node> open = new Heap<City_Node>(this.max);
		ArrayList<String> result = new ArrayList<String>();
		City_Node target = null;
		
		// in case that cityA is not a member of cities
		if(!cities.contains(cityA)) return new ArrayList<String>();
		result.add(cityA);
		try {
			// find neighbor and process based on the cumulative distance small to big
			open.add(new City_Node(cityA));
			while(!open.isEmpty() && (target==null)){
				City_Node current = open.removeFirst();			
				ArrayList<City_Node> neigh = getneighbors(current);
				String city_from = current.city;
				int index_from = cities.indexOf(city_from);
				// if This city_node is the DC node, stop
				if (IsDistributionCenter(current)){
					target = current;
					break;
				}
				
				for(int i = 0; i<neigh.size();i++){
					City_Node near_Node = neigh.get(i);
					String city_to = neigh.get(i).city;
					int index_to = cities.indexOf(city_to);
					int new_distance = distance[index_from][index_to];
					
					
					int new_cost = current.distance_cost + new_distance;
					// if in the close set, check whether it can be update
					if (close.contains(near_Node.city)){
						if(new_cost < near_Node.distance_cost){
							close.remove(near_Node.city);
							near_Node.distance_cost = new_cost;
							near_Node.parent = current;
							open.add(near_Node);
						}
						continue;
					}
					
					// if in the open set and can be update, do it
					if (open.contains(near_Node)){
						if (new_cost < near_Node.distance_cost){
							near_Node.parent = current;
							near_Node.distance_cost = new_cost;
							open.updateItem(near_Node);
						}
						continue;
					}
					
					
					// if not in the close set and not in the open set, add it
					if(!open.contains(near_Node)){
						near_Node.parent = current;
						near_Node.distance_cost = current.distance_cost + new_distance;
						open.add(near_Node);
					}
				}
				close.add(current.city);
			}
		} catch (HeapFullException e) {
			
		} catch (HeapEmptyException e) {
			
		}
		
		// if can't get to the distribution center anyway
		if (target == null)
		return new ArrayList<String>();
		
		ArrayList<String> path = retracePath(target);
		return path;
	}
	
	/**
	 * This method used to calculate the distance of the given path
	 * @param path  a list of string that form a path
	 * @return int the distance of the given path
	 */
	public int PathDistance(List<String> path){
		int result = 0;
		if (path.size() == 0 || path.size() == 1 ) return 0;
		for (int i=0;i<path.size()-1;i++){
			String cur_city = path.get(i);
			String next_city = path.get(i+1);
			int index_cur = cities.indexOf(cur_city);
			int index_next = cities.indexOf(next_city);
			result += distance[index_cur][index_next];
		}
		return result;
	}
	
	// =====================helper function =====================	
	private ArrayList<String> retracePath(City_Node target){
		ArrayList<String> inverse_path = new ArrayList<String>();
		City_Node current = target;
		while(current.parent != null){
			inverse_path.add(current.city);
			current = current.parent;
		}
		
		inverse_path.add(current.city);
		
		
		return inverse_path;
	}
	
	
	private ArrayList<City_Node> getneighbors(City_Node cityA_Node){
		ArrayList<City_Node> result = new ArrayList<City_Node>();
		String cityA = cityA_Node.city;
		
		// if not in the city list , return empty ArrayList
		if (!cities.contains(cityA)) return result;
		
		int index_A = -1;
		for ( int i=0;i<cities.size();i++){
			if (cities.get(i).equals(cityA)) index_A = i;
		}
		
		for(int j=0;j<max;j++){
			if(distance[index_A][j] > 0) result.add(new City_Node(cities.get(j)));
		}
		return result;
	}
	
	
	private boolean IsDistributionCenter(City_Node cityA_Node){
		String cityA = cityA_Node.city;
		for (int i =0;i<DCs.size();i++){
			if(DCs.get(i).equals(cityA)) return true;
		}
		return false;
	}
	
	
	
	private void EnlargeDistance(){
		int new_max = this.max + 1;
		int[][] new_distance = new int[new_max][new_max];
		for(int i =0;i<this.max;i++)
			for(int j=0;j<this.max;j++)
				new_distance[i][j] = distance[i][j];
		
		this.max = new_max;
		this.distance = new_distance;
	}

}