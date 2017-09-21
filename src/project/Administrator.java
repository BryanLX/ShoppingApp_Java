package project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * Class representing the Administrator who manages the shopping application.
 */
public class Administrator extends User {
	
	/**
	 * constructor of the administrator
	 * @param userID  the ID of the user
	 * @param password  the password of the user
	 */
	public Administrator(String userID, String password) {
		this.userID = userID;
		this.password = password;
	}
	
// ================= Administrator's features ==================================
	// ------------------------add product and category----------------------------
	
	/**
	 * Add a category to the FileManager.
	 *
	 * @param description name of category to add.
	 * @return unique ID of category or -1 if unsuccessful.
     */
	public int addCategory(String description){
		int result = -1;
		// Synchronize fm.categories(all categories) with file
		fm.readCategories();
		// If no such category exists, add it to fm.categories
		if (fm.getCategory(description) == null) { 
			Category c = new Category(description);
			fm.categories.add(c);
			result = c.code;
		}
		// Update categories
		fm.writeCategories();
		return result; // Adding category unsuccessful
	}
	
	/**
	 * Add product to FileManager.
	 *
	 * @param des  name of Product
	 * @param CateCode  Category code
	 * @param price  price
     * @return Return product ID if successful. Else, -1.
     */
	public int addProduct(String des, int CateCode, double price){
		// Synchronize fm.Products(all products) with file.
		fm.readProducts();
		int result = -1;
		// If CateCode is valid and no such product exists, add it.
		Category cat = fm.getCategory(CateCode);
		Product pro = fm.getProduct(des);
		if (cat != null && pro == null) {
			Product pr = new Product(des, cat.code, price);
			fm.products.add(pr);
			// Add all distribution centers to this new product.
			List<String> DCs = fm.map.DCs;
			for (int i=0;i<DCs.size();i++)
				pr.DCsQty.put(DCs.get(i), 0);
			// Successfully added this product.
			    result = pr.ID; 
		}
		// Update products.csv
		fm.writeProducts();
		return result;
	}
	
	/**
	 * Update the quantity of a product at a distribution center.
	 *
	 * @param prodID Product ID
	 * @param distCentre Distribution Center
	 * @param quantity quantity
     * @return boolean true iff quantity update is successful.
     */
	public boolean updateQuantity(int prodID, String distCentre, int quantity) {
		// If there is a product of id prodID in fm.products,
		// add quantity to distCentre.
		boolean result = false;
		fm.readProducts();
		List<Product> Pros = fm.products;
		for (int i=0;i<Pros.size();i++)
			if (Pros.get(i).isID(prodID)) {
				result = Pros.get(i).updateQuantity(distCentre, quantity);
			}
		fm.writeProducts();
		return result;
	}
	
	// ----------------------- update map -----------------------------
	
	/**
	 * Add a route between two cities.
	 *
	 * @param cityA  first city
	 * @param cityB  second city
	 * @param distance distance
     */
	public void addRoute(String cityA, String cityB, int distance) {
		fm.readMap();
		fm.map.addFRs(cityA,cityB,distance);
		fm.writeMap();
	}
	
	/**
	 * Add a distribution center.
	 * @param city city
     */
	public void addDistributionCenter(String city) {
		fm.readMap();
		if (fm.map.addDC(city)) {
			for (Product p : fm.products) {
				p.addDC(city);
			}
		}
		fm.writeMap();
	}
	
	// ------------------------report existing categories------------------------
	/**
	 * Report existing categories.
	 *
	 * @return String the report
     */
	public String report() {
		String report = "";
		List<Category> Cates = fm.categories;
		for (int i=0;i<Cates.size();i++) {
			report += Cates.get(i).toString() + " \n";
		}
		return report;
	}
	
	// ------------------------sort products-------------------------------
	/**
	 *
	 * Sort products by increasing order.
	 * @return List&lt;Integer&gt; List of Products
    */
	public List<Product> sortIncreasing() {
		fm.readProducts();
		List<Product> result = new ArrayList<Product>(fm.products);
		Collections.sort(result);
		fm.writeProducts();
		return result;
	}
	
	/**
	 * Sort products by decreasing order.
	 *
	 * @return List&lt;Integer&gt; List of Products
     */
	public List<Product> sortDecreasing() {
		fm.readProducts();
		List<Product> result = new ArrayList<Product>(fm.products);
		Collections.sort(result);
		Collections.reverse(result);
		fm.writeProducts();
		return result;
	}

	/**
	 * Sort products by category.
	 *
	 * @return HashMap<Category, List<Product>> List of Products
     */
	public HashMap<Category, List<Product>> sortbyCategories() {
		fm.readProducts();
		fm.readCategories();
		HashMap<Category, List<Product>> result = new HashMap<Category, List<Product>>(); 
		for (int i=0;i<fm.categories.size();i++) {
			Category curCate = fm.categories.get(i);
			List<Product> sameCate = new ArrayList<Product>();
			for (int j=0;j<fm.products.size();j++) {
				if (fm.products.get(j).isCategory(curCate))
					sameCate.add(fm.products.get(j));
			}
			result.put(curCate, sameCate);
		}
		fm.writeProducts();
		fm.writeCategories();
		return result;
	}
	
	//-----------------------------------------
	/**
	 * Return a string representation of this Administrator.
	 *
	 * @return String a string representation of this Administrator.
     */
	public String toString() {
		return super.toString() + ",ad\n";
	}
	
}
