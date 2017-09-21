package project;

import java.util.HashMap;

/**
 * A class representing a product of the shopping application.
 */
public class Product implements Comparable<Product> {
	protected static int idCounter = 1;
	protected String image;
	protected int ID;
	protected String description;
	protected int cateCode;
	protected double price;
	protected int totalQty;
	protected HashMap<String, Integer> DCsQty;
	
	/**
	 * Initialize a Product.
	 *
	 * @param des Product description
	 * @param cateCode category code
	 * @param price price
     */
	public Product(String des, int cateCode, double price) {
		this.image = null;
		this.ID = idCounter;
		idCounter++;
		this.description = des;
		this.cateCode = cateCode;
		this.price = price;
		this.totalQty = 0;
		DCsQty = new HashMap<String, Integer>();
	}
	
	/**
	 * Initialize a Product.
	 * 
	 * @param id id of product
	 * @param des description of product
	 * @param cateCode category code of product
	 * @param price price of the product
	 */
	public Product(int id, String des, int cateCode, double price) {
		this.image = null;
		this.ID = id;
		if (idCounter <= id)
			idCounter = id + 1;
		this.description = des;
		this.cateCode = cateCode;
		this.price = price;
		this.totalQty = 0;
		DCsQty = new HashMap<String, Integer>();
	}	
	// ===========================================
	public String shipping(int Qty) {
		return "";
	}
	
	/**
	 * Update the quantity of the product at a given distribution center.
	 *
	 * @param distCentre distribution center
	 * @param quantity updated quantity
     * @return boolean true iff update is successful.
     */
	public boolean updateQuantity(String distCentre, int quantity) {
		boolean result = false;
		if (DCsQty.containsKey(distCentre)) {
			int oldQty = DCsQty.get(distCentre);
			totalQty += quantity;
			DCsQty.put(distCentre, oldQty + quantity);
			result = true;
		}
		return result;
	}
	
	/**
	 * Add a distribution center.
	 *
	 * @param city city
     */
	public void addDC(String city) {
		DCsQty.put(city, 0);
	}
	
	// =============some basic methods================
	/**
	 * Return the difference of quantity between this Product and other.
	 *
	 * @param other other Product
	 * @return int difference between the quantities of the two Products.
     */
	@Override
	public int compareTo(Product other) {
		return totalQty - other.totalQty;
	}
	
	/**
	 * Return true iff this Product equals other.
	 *
	 * @param other Object
	 * @return boolean true iff this Product equals other
     */
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (!(other instanceof Product)) return false;
		if (this == other) return true;
		return ID == ((Product)other).ID;
	}
	
	/**
	 * Return a string representation of this Product.
	 *
	 * @return String a string representation of this Product.
     */
	@Override
	public String toString() {
		// id,description,cateCode,price,image,totalQty,DCsQty
		String result = ID + "," + description + "," + cateCode + "," 
		+ price + "," + image + "," + totalQty + ",";
		for (String key : DCsQty.keySet())
			result += key + "=" + DCsQty.get(key) + " ";
		return result + "\n";
	}
	
	// ===============helpers===================
	/**
	 * Return true iff this category is equal to the category code of this Product.
	 *
	 * @param cate category code
	 * @return boolean true iff this category is equal to the category code of this Product.
     */
	public boolean isCategory(Category cate) {
		return this.cateCode == cate.code;
	}
	
	/**
	 * Return true iff price is within price range.
	 *
	 * @param min minimum price
	 * @param max maximum price
     * @return boolean true iff price is within price range.
     */
	public boolean inPriceRange(double min, double max) {
		return min <= price && price <= max;
	}
	
	/**
	 * Return true iff QtyDemanded is larger or equal to  total quantity.
	 *
	 * @param QtyDemanded quantity demanded
	 * @return boolean true iff QtyDemanded larger or equal to total quantity.
     */
	public boolean isAvailable(int QtyDemanded) {
		return totalQty >= QtyDemanded;
	}
	
	/**
	 * Return true iff id is equal to the ID of this Product.
	 *
	 * @param id id
	 * @return boolean true iff id is equal to the ID of this Product.
     */
	public boolean isID (int id) {
		return ID == id;
	}
	
	/**
	 * Return the quantity of the Product in a distribution center given the city it's located in.
	 *
	 * @param city city
	 * @return int quantity of the Product at the distribution center
     */
	public int quantityIn(String city) {
		int result = -1;
		if (DCsQty.containsKey(city)) {
			result = DCsQty.get(city);
		}
		return result;
	}
	
	/**
	 * Update total quantity by subtracting QtyDemanded from the total quantity.
	 *
	 * @param QtyDemanded quantity demanded
     */
	public void finishselling(int QtyDemanded) {
		totalQty -= QtyDemanded;
	}

	// =================getters and setters =====================
	/**
	 * Get image.
	 *
	 * @return String image pathname
     */
	public String getImage() {
		return image;
	}
	
	/**
	 * Set image.
	 *
	 * @param image image pathname
     */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * Get ID.
	 *
	 * @return int ID
     */
	public int getID() {
		return ID;
	}
	
	/**
	 * Set ID.
	 *
	 * @param iD ID
     */
	public void setID(int iD) {
		ID = iD;
	}
	
	/**
	 * Get description.
	 *
	 * @return String description
     */
	public String getDescription() {
		return description;
	}

	/**
	 * Set description
	 *
	 * @param description description
     */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Get category code.
	 *
	 * @return int category code
     */
	public int getCateCode() {
		return cateCode;
	}
	
	/**
	 * Set category code.
	 *
	 * @param cateCode Category code
     */
	public void setCateCode(int cateCode) {
		this.cateCode = cateCode;
	}

	/**
	 * Get price.
	 *
	 * @return double price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Set price
	 *
	 * @param price price
     */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * Get total quantity
	 * @return int total quantity.
     */
	public int getTotalQty() {
		return totalQty;
	}

	/**
	 * Set total quantity
	 * @param totalQty total quantity
     */
	public void setTotalQty(int totalQty) {
		this.totalQty = totalQty;
	}

	/**
	 * Get Hashmap of Destination centers and the quantity at each destination center.
	 *
	 * @return Hashmap of Destination centers and the quantity at each destination center.
     */
	public HashMap<String, Integer> getDCsQty() {
		return DCsQty;
	}

	/**
	 * Set the Destination centers and the quantity at each destination center.
	 *
	 * @param dCsQty destination center and the quantity at each destination center
     */
	public void setDCsQty(HashMap<String, Integer> dCsQty) {
		DCsQty = dCsQty;
	}
	
}
