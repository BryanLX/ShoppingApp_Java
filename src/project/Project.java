package project;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is intended to be your API
 * @author Ilir
 *
 */
public class Project {
	protected FileManager fm = new FileManager("categories.csv", "products.csv", 
			"map.csv", "users.csv", "orders.csv", "invoices.csv");
	protected List<Session> sessions = new ArrayList<Session>();
	/**
	 * This method must add a new shopper user or administrator user.
	 * @param userID user id
	 * @param password user password
	 * @param admin  if true, add an administrator user, otherwise add a shopper user
	 * @return  true if operation successful, false otherwise
	 * @throws NumberFormatException 
	 */
	public boolean addUser(String userID, String password, boolean admin) {
		boolean result = false;
		boolean isRegistered = false;
		fm.readUsers();
		for (User u : fm.users)
			if (u.isUserID(userID))
				isRegistered = true;
		if (!isRegistered)
			if (admin) {
				fm.users.add(new Administrator(userID, password));
				result = true;
			} else {
				fm.users.add(new Shopper(userID, password));
				result = true;
			}
		fm.writeUsers();
		return result;
	}
	
	/**
	 * Authenticates a user an creates an active work session
	 * @param userID 
	 * @param password
	 * @return  SessionID if authentication successful, -1 otherwise.
	 */
	public int login(String userID, String password) {
		// If this user is not logged in 
		// and has already been registered(i.e. has been added)
		// and password matches,
		// then create a new session for this user.
		// Check if logged in already
		for (int i=0;i<sessions.size();i++)
			if (sessions.get(i).isLoggedin(userID))
				return -1;
		
		// Check if registered already and password matches
		User u = null;
		for (User user : fm.users)
			if (user.login(userID, password))
				u = user;
		
		// Log in this user by creating a new session for the user
		if (u != null) {
			User.fm = fm;
			if (u instanceof Shopper)
				((Shopper)u).login();
			Session s = new Session(u.userID, fm);
			sessions.add(s);
			return s.getID(); // The session ID.
		}
		return -1;
	}
	
	/**
	 * Makes sessionID unavailable for connection
	 * @param sessionID
	 */
	public void logout(int sessionID) {
		for (int i=0;i<sessions.size();i++)
			if (sessions.get(i).getID() == sessionID) {
				if (sessions.get(i).getUser() instanceof Shopper)
					((Shopper)sessions.get(i).getUser()).logout();
				sessions.remove(i);
			}
	}
	
	/**
	 * This method must add a new category in your application.
	 * @param catName  The name of the category to be added.
	 * @param sessionID  A session ID that belongs to an authenticated administrator
	 * @return  The ID of the category you created if successful, -1 if not successful.
	 */
	public int addCategory(String catName, int sessionID) {
		for (int i=0;i<sessions.size();i++) {
			Session s = sessions.get(i);
			if (s.getID() == sessionID && s.getUser() instanceof Administrator) {
				Administrator ad = (Administrator)(s.getUser());
				return ad.addCategory(catName);
			}
		}
		return -1;
	}
	
	/**
	 * Adds a distribution center to your application.
	 * If the given distribution center exists, or sesionID invalid, do nothing.
	 * @param city  The city where distribution center must be based.
	 * @param sessionID  A session ID that belongs to an authenticated administrator
	 */
	public void addDistributionCenter(String city, int sessionID) {
		for (int i=0;i<sessions.size();i++) {
			Session s = sessions.get(i);
			if (s.getID() == sessionID && s.getUser() instanceof Administrator) {
				Administrator ad = (Administrator)(s.getUser());
				ad.addDistributionCenter(city);
			}
		}
	}
	
	/**
	 * Adds a new Customer to your application; the customer record that belongs 
	 * to a newly added shopper user that has no customer record on the system.
	 * @param custName  The name of the customer
	 * @param city  The city of the customer address
	 * @param street  The street address of the customer
	 * @param sessionID  A valid sessionID that belongs to an authenticated shopper user.
	 * @return  The added customer ID
	 */
	public int addCustomer(String custName, String city, String street, int sessionID) {
		for (int i=0;i<sessions.size();i++) {
			Session s = sessions.get(i);
			if (s.getID() == sessionID && s.getUser() instanceof Shopper) {
				Shopper sh = (Shopper)(s.getUser());
				return sh.addRecord(custName, city, street);
			}
		}
		return -1;
	}
	
	/**
	 * Adds a new Product to your application
	 * @param prodName  The product name
	 * @param category  The product category.
	 * @param price  The product sales price
	 * @param sessionID  A session ID that belongs to an authenticated administrator
	 * @return  Product ID if successful, -1 otherwise.
	 */
	public int addProduct(String prodName, int category, double price, int sessionID) {
		for (int i=0;i<sessions.size();i++) {
			Session s = sessions.get(i);
			if (s.getID() == sessionID && s.getUser() instanceof Administrator) {
				Administrator ad = (Administrator)(s.getUser());
				return ad.addProduct(prodName, category, price);
			}
		}
		return -1;
	}
	
	/**
	 * Computes the available quantity of prodID in a specific distribution center.
	 * @param prodID product id 
	 * @param center center 
	 * @return  Available quantity or -1 if prodID or center does not exist in the database
	 */
	public int prodInquiry(int prodID, String center) {
		fm.readProducts();
		Product pro = fm.getProduct(prodID);
		if (pro != null) 
			return pro.quantityIn(center);
		return -1;
	}
	
	/**
	 * Updates the stock quantity of the product identified by prodID
	 * @param prodID  The product ID to be updated
	 * @param distCentre  Distribution Center (in effect a city name)
	 * @param quantity  Quantity to add to the existing quantity
	 * @param sessionID  A session ID that belongs to an authenticated administrator
	 * If currently the product 112 has quantity 100 in Toronto,
	 * after the statement updateQuantity(112, "Toronto", 51)
	 * same product must have quantity 151 in the Toronto distribution center. 
	 * @return -> true if the operation could be performed, false otherwise.
	 */
	public boolean updateQuantity(int prodID, String distCentre, int quantity, int sessionID) {
		boolean cID = false;
		for (int i=0;i<sessions.size();i++) {
			Session s = sessions.get(i);
			if (s.getID() == sessionID && s.getUser() instanceof Administrator) {
				Administrator ad = (Administrator)(s.getUser());
				cID = ad.updateQuantity(prodID, distCentre, quantity);
			}
		}
		return cID;
	}
	
	/**
	 * Adds two nodes cityA, cityB to the shipping graph
	 * Adds a route (an edge to the shipping graph) from cityA to cityB with length distance
	 * If the nodes or the edge (or both) exist, does nothing
	 * @param cityA  the city a
	 * @param cityB  the city b
	 * @param distance  distance (in km, between cityA and cityB)
	 * @param sessionID  A session ID that belongs to an authenticated administrator
	 */
	public void addRoute(String cityA, String cityB, int distance, int sessionID) {
		for (int i=0;i<sessions.size();i++) {
			Session s = sessions.get(i);
			if (s.getID() == sessionID && s.getUser() instanceof Administrator) {
				Administrator ad = (Administrator)(s.getUser());
				ad.addRoute(cityA, cityB, distance);
			}
		}

	}
	
	/**
	 * Attempts an order in behalf of custID for quantity units of the prodID
	 * @param custID -> The customer ID
	 * @param prodID -> The product ID
	 * @param quantity -> The desired quantity
	 * @param sessionID -> A valid sessionID that belongs to an authenticated shopper user.
	 * @return -> The orderID if successful, -1 if not.
	 */
	public int placeOrder(int custID, int prodID, int quantity, int sessionID) {
		for (int i=0;i<sessions.size();i++) {
			Session s = sessions.get(i);
			if (s.getID() == sessionID && s.getUser() instanceof Shopper) {
				Shopper sh = (Shopper)(s.getUser());
				if (sh.cust != null && sh.cust.custID == custID)
					return sh.placeOrder(prodID, quantity);
			}
		}
		return -1;
	}

    
	/**
	 * Returns the best (shortest) delivery route for a given order 
	 * @param orderID -> The order ID we want the delivery route
	 * @param sessionID -> A valid sessionID that belongs to an authenticated shopper user.
	 * @return -> The actual route as an array list of cities, null if not successful
	 */
	public ArrayList<String> getDeliveryRoute(int orderID, int sessionID) {
		for (int i=0;i<sessions.size();i++) {
			Session s = sessions.get(i);
			if (s.getID() == sessionID && s.getUser() instanceof Shopper) {
				Shopper sh = (Shopper)(s.getUser());
				Invoice inv = fm.getInvoice(orderID);
				if (inv != null && inv.shopperID == sh.cust.custID)
					return fm.map.ShortestRoute(inv.shopperCity);
			}
		}
		return null;
	}

	
	/** 
	 * Computes the invoice amount for a given order.
	 * Please use the fixed price 0.01$/km to compute the shipping cost 
	 * @param orderID
	 * @param sessionID -> A valid sessionID that belongs to an authenticated shopper user.
	 * @return
	 */
	public double invoiceAmount(int orderID, int sessionID) {
		double amount = 0;
		for (int i=0;i<sessions.size();i++) {
			Session s = sessions.get(i);
			if (s.getID() == sessionID && s.getUser() instanceof Shopper) {
				Shopper sh = (Shopper)(s.getUser());
				Invoice inv = fm.getInvoice(orderID);
				if (inv != null && inv.shopperID == sh.cust.custID) {
					amount = inv.delCharge;
					for (int proID : inv.items.keySet()) {
						Product p = fm.getProduct(proID);
						if (p != null) {
							amount += p.price * inv.items.get(proID);
						}
					}
				}
			}
		}
		return amount;
	}

}
