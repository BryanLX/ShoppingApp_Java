package project;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * A class representing the shopper who browses and purchases products.
 */
public class Shopper extends User{
	protected HashMap<Product, Integer> Cart = null;
	protected List<List<String>> fromDC = null;
	protected List<List<Integer>> quantityDC = null;
	protected List<Integer> proIDs = null;
	protected List<Integer> invoices = null;
	protected List<Integer> orders = null;
	protected Customer cust = null;
	
	/**
	 * Initialize a Shopper with a user ID and a password.
	 *
	 * @param userID
	 * @param password
     */
	public Shopper(String userID, String password) {
		this.userID = userID;
		this.password = password;
		Cart = new HashMap<Product, Integer>();
		invoices = new ArrayList<Integer>();
		orders = new ArrayList<Integer>();
		
		proIDs = new ArrayList<Integer>();
		fromDC = new ArrayList<List<String>>();
		quantityDC = new ArrayList<List<Integer>>();
	}
	
	/**
	 * If userID is valid, adds name and address to this Shopper and return a unique
	 * ID. Otherwise return -1.
	 *
	 * @param custName Name
	 * @param city City
	 * @param street Street
     * @return int Shopper ID or -1.
     */
	public int addRecord(String custName, String city, String street) {
		fm.readUsers();
		int result = -1;
		User u = fm.getUser(userID);
		if (u != null && u instanceof Shopper) {
			Shopper sh = (Shopper)u;
			if (sh.cust == null) {
				cust = new Customer(custName, city, street);
				result = cust.custID;
			}
		}
		fm.writeUsers();
		return result;
	}
	
	/**
	 * Add Qty number of a Product to the cart.
	 *
	 * @param id ID of the product
	 * @param Qty quantity
     */
	public boolean addtoCart(int id, int Qty) {
		fm.readUsers();
		fm.readProducts();
		boolean result = false;
		Product p = fm.getProduct(id);
		if (cust != null) {
			// Find the closest distribution center, and the path
			List<String> path = fm.map.ShortestRoute(cust.city);
			if(path.size() == 0) return result;
			String closestDC = path.get(0);
			
			// If total quantity is enough, then ship this product
			if (p.totalQty >= Qty) {
				// Add the desired product to this shopper's cart
				if (Cart.containsKey(p))
					Cart.put(p, Cart.get(p) + Qty);
				else 
					Cart.put(p, Qty);
				
				// Deduct the quantity from total quantity
				p.totalQty -= Qty;
				
				// If the nearest distribution center(DC) has enough quantity,
				// then ship the product(s) from this DC only.
				if (p.DCsQty.get(closestDC) >= Qty) {
					int oldQty = p.DCsQty.get(closestDC);
					p.DCsQty.put(closestDC, oldQty - Qty);
					
					this.addItem(p.ID, closestDC, Qty);
				} else {
					// One nearest DC doesn't satisfy the order, 
					// transfer the rest of products from other DCs to the nearest one.
					this.addItem(p.ID, closestDC, p.DCsQty.get(closestDC));
					Qty -= p.DCsQty.get(closestDC); // Quantity of the rest of the products
					p.DCsQty.put(closestDC, 0);
					for (String dcName : p.DCsQty.keySet()) {
						int dcQty = p.DCsQty.get(dcName);
						if (dcQty >= Qty) {
							p.DCsQty.put(dcName, dcQty - Qty);
							
							this.addItem(p.ID, dcName, Qty);
							break;
						}
						else {
							this.addItem(p.ID, dcName, p.DCsQty.get(dcName));
							p.DCsQty.put(dcName, 0);
							Qty -= dcQty;
						}
					}
				}
				result = true;
			}
		}
		fm.writeUsers();
		fm.writeProducts();
		return result;
	}
	
	/**
	 * Cancel this shopper's cart, and return all the items.
	 */
	public void cancelCart() {
		fm.readUsers();
		fm.readProducts();
		if (cust != null) {
			// Find the closest distribution center, and the path
			List<String> path = fm.map.ShortestRoute(cust.city);
			if(path.size() == 0) return ;
			String closestDC = path.get(0);
			
			// Return every product in the Cart to the closest distribution center.
			for (Product p : Cart.keySet()) {
				p.updateQuantity(closestDC, Cart.get(p));
			}
			Cart = new HashMap<Product, Integer>();
			
			proIDs = new ArrayList<Integer>();
			fromDC = new ArrayList<List<String>>();
			quantityDC = new ArrayList<List<Integer>>();

		}
		fm.writeUsers();
		fm.writeProducts();
	}
	
	public void changeQuantity(Product p, int qty) {
		fm.readProducts();
		fm.writeUsers();
		if (cust != null) {
			// Find the closest distribution center, and the path
			List<String> path = fm.map.ShortestRoute(cust.city);
			if(path.size() == 0) return ;
			
			// qty == 0 means this items is removed from cart.
			if (qty == 0) {
				remove(p.ID);
				Cart.remove(p);
			}
			// If 0 < qty < quantity in cart, then decrease the quantity in the cart
			// and return the unwanted products to the closest warehouse.
			else if (qty > 0 && qty < Cart.get(p)) {
				int q = Cart.get(p);
				q -= qty;
				remove(p.ID);
				Cart.put(p,0);
				fm.writeUsers();
				fm.writeProducts();
				addtoCart(p.ID, qty);
			// If qty > quantity in cart, then add the extra products to cart, 
			//which may fail due to insufficient total quantity.
			} else if (qty > Cart.get(p)) {
				qty -= Cart.get(p);
				addtoCart(p.ID, qty);
			}
		}
		fm.writeProducts();
		fm.writeUsers();
	}
	
	public void removeProd(Product p) {
		fm.readProducts();
		fm.writeUsers();
		if (cust != null) {
			remove(p.ID);
			Cart.remove(p);
		}
		fm.writeProducts();
		fm.writeUsers();
	}
	
	// =================== log in an log out =====================
	/**
	 * Log in this shopper.
	 */
	public void login() {
		fm.readUsers();
		fm.readProducts();
		HashMap<Product, Integer> cartDup = Cart;
		Cart = new HashMap<Product, Integer>();
		proIDs = new ArrayList<Integer>();
		fromDC = new ArrayList<List<String>>();
		quantityDC = new ArrayList<List<Integer>>();
		fm.writeUsers();

		if (cust != null) {
			for (Product p : cartDup.keySet()) {
				if (!addtoCart(p.ID, cartDup.get(p))) {
					Cart.put(p, 0);					
				}
			}
		}
		fm.writeUsers();
		fm.writeProducts();
	}
	
	private void returnItem(int id) {
		int i = proIDs.indexOf(id);
		if ( i >= 0 ) {
			Product p = fm.getProduct(id);
			for (int j = 0;j<fromDC.get(i).size();j++) {
				String frm = fromDC.get(i).get(j);
				p.DCsQty.put(frm, p.DCsQty.get(frm) + quantityDC.get(i).get(j));
				p.totalQty += quantityDC.get(i).get(j);
			}
		}
	}

	private void addItem(int id, String dc, int qty) {
		if (this.proIDs.contains(id)) {
			int i = this.proIDs.indexOf(id);
			if (this.fromDC.get(i).contains(dc)) {
				int dcIdx = this.fromDC.get(i).indexOf(dc);
				int curQty = this.quantityDC.get(i).get(dcIdx);
				this.quantityDC.get(i).set(dcIdx, curQty + qty);
			} else {
				this.fromDC.get(i).add(dc);
				this.quantityDC.get(i).add(qty);	
			}
		} else {
			this.proIDs.add(id);
			int i = this.proIDs.indexOf(id);
			fromDC.add(new ArrayList<String>());
			quantityDC.add(new ArrayList<Integer>());
			fromDC.get(i).add(dc);
			quantityDC.get(i).add(qty);
		}
	}
	
	private void remove(int id) {
		if (this.proIDs.contains(id)) {
			int i = this.proIDs.indexOf(id);
			returnItem(id);
			this.proIDs.remove(i);
			this.fromDC.remove(i);
			this.quantityDC.remove(i);
		}
	}
	
	/**
	 * return all the items.
	 */
	public void logout() {
		fm.readUsers();
		fm.readProducts();
		if (cust != null) {
			// Return all product to the closest warehouse,
			// but does not clear cart.
			for (Product p : Cart.keySet()) {
				returnItem(p.ID);
			}
		}
		fm.writeUsers();
		fm.writeProducts();
	}

	
	/**
	 * Place order and deducts quantity from product if the closest distribution center has enough quantity. Return
	 * order ID if successful. Otherwise, return -1
	 *
	 * @param prodID Product ID
	 * @param quantity quantity purchased
	 * @param city City to ship to
     * @return int order ID if successful. Else, -1.
     */
	public int placeOrder(int prodID, int Qty) {
		fm.readOrders();
		fm.readProducts();
		fm.readUsers();
		int odrID = -1;
		addtoCart(prodID, Qty);
		odrID = checkout();
		fm.writeUsers();
		fm.writeOrders();
		fm.writeProducts();
		return odrID;
	}

	/**
	 * Checkout the cart
	 * @return the order ID
	 */
	public int checkout() {
		int result = -1;
		fm.readProducts();
		fm.readInvoices();
		fm.readUsers();
		if (cust != null) {
			List<String> path = fm.map.ShortestRoute(cust.city);
			double charge = fm.map.PathDistance(path) * 0.01;
			Calendar c = Calendar.getInstance();
			Date d = c.getTime();
			String time = d + "";
			Invoice newInv = new Invoice(cust.custID, cust.city, Cart, charge,time);
			fm.invoices.add(newInv);
			invoices.add(newInv.ID);
			Cart = new HashMap<Product, Integer>();
			result = newInv.ID;
			
			proIDs = new ArrayList<Integer>();
			fromDC = new ArrayList<List<String>>();
			quantityDC = new ArrayList<List<Integer>>();
		}
		fm.writeInvoices();
		fm.writeProducts();
		fm.writeUsers();
		return result;
	}
	
	/**
	 * Return the invoices of this Shopper
	 * @return List<Invoice> -> all this invoices of this shopper's
	 */
	public List<Invoice> purchases() {
		List<Invoice> invs = new ArrayList<Invoice>();
		for (int i : invoices) {
			invs.add(fm.getInvoice(i));
		}
		return invs;
	}
	
	/**
	 * Return a string representation of this Shopper.
	 *
	 * @return String string representation of this Shopper.
     */
	public String toString() {
		//userID,password,sh/ad,custID,custName,custCity,custStreet,Cart,invoices,orders
		String result = super.toString() + ",sh,";
		if (cust != null)
			result += cust.toString();
		else
			result += ",,,";
		result += ",";
		for (Product key : Cart.keySet()) {
		    result += key.ID + "=" + Cart.get(key) + " ";
		}
		result += ",";
		for (Integer i : invoices) {
			result += i.toString() + " ";
		}		
		result += ",";
		for (Integer i : orders) {
			result += i.toString() + " ";
		}
		result += "\n";
		return result;
	}
}
