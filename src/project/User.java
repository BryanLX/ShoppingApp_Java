package project;

import java.util.List;
import java.util.ArrayList;

/**
 * The abstract class representing a User
 */
public abstract class User{
	protected String userID;
	protected String password;
	protected static FileManager fm;
	
	/**
	 * return true if the userID and password are correct
	 * @param u  the input userID of the user
	 * @param p  the input password of the user
	 * @return true if the user_id and the password matches
	 */
	public boolean login(String u, String p) {
		return userID.equals(u)&& password.equals(p);
	}
	
	/**
	 * return true if the user's userID is the input id
	 * @param ID  the input ID
	 * @return return true if the the user's userID is the input id
	 */
	public boolean isUserID(String ID) {
		return userID.equals(ID);
	}
	
	/**
	 * return a list of products based on the input price range and category Name
	 * @param CategoryName  the idea Category Name
	 * @param minPrice  the minPrice a user want to browse
	 * @param maxPrice  the maxPrice a user want to browse
	 * @return A list of products whose category is the input categoryName and price is between min and max
	 * 
	 */
	public static List<Product> browse(String CategoryName, double minPrice, double maxPrice) {
		Category wantedCat = fm.getCategory(CategoryName);
		fm.readProducts();
		List<Product> result = new ArrayList<Product>();
		if (wantedCat != null) {// If one wants to look for products of a certain Category
			for (int i=0;i<fm.products.size();i++)
				if (fm.products.get(i).isCategory(wantedCat) && fm.products.get(i).inPriceRange(minPrice, maxPrice))
					result.add(fm.products.get(i));
		} else {
			for (int j=0;j<fm.products.size();j++)
				if (fm.products.get(j).inPriceRange(minPrice, maxPrice))
					result.add(fm.products.get(j));
		}
		fm.writeProducts();
		return result;
	}
	
	
	/**
	 * return true if the input object is equal to this
	 * @param o:  the input object
	 * @return return true if the input object is equal to this
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof User)) return false;
		if (o == this) return true;
		return userID.equals(((User)o).userID);
	}
	
	/**
	 * The ToString of this User
	 * @return the string of the user;
	 */
	@Override
	public String toString() {
		return userID + "," + password;
	}

}
