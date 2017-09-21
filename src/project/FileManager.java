package project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FileManager is responsible for data recording,data modification and data reading.It is widely
 * used in other classes that asking to process data in the storage file.
 * @author group
 *
 */
public class FileManager {
	private static final Logger logger = Logger.getLogger(FileManager.class.getName());
	private static final Handler consoleHandler = new ConsoleHandler();

	protected List<Category> categories;
	protected List<Product> products;
	protected List<User> users;
	protected List<Invoice> invoices;
	protected List<Order> orders;
	protected Map map;
	
	protected String catePathname;
	protected String proPathname;
	protected String mapPathname;
	protected String usersPathname;
	protected String ordersPathname;
	private String invoicesPathname;
	
	/**
	 * Constructor:Create a instance of a FileManager
	 * @param CatePathname  the pathname of the file where we store the category information
	 * @param MapPathname  the pathname of the file where we store the map information
	 * @param UsersPathname  the pathname of the file where we store the user information
	 * @param OrdersPathname  the pathname of the file where we store the orders information
	 * @param ProPathname the pathname of the file where we store the pro information
	 * @param invoicesPathname the pathname of the file where we store the invoice information
	 */
	public FileManager(String CatePathname, String ProPathname, String MapPathname, 
			String UsersPathname, String OrdersPathname, String invoicesPathname){
		logger.setLevel(Level.ALL);
		consoleHandler.setLevel(Level.ALL);
		logger.addHandler(consoleHandler);
		
		this.catePathname = CatePathname;
		this.proPathname = ProPathname;
		this.mapPathname = MapPathname;
		this.usersPathname = UsersPathname;
		this.ordersPathname = OrdersPathname;
		this.invoicesPathname = invoicesPathname;
		
		categories = new ArrayList<Category>();
		products = new ArrayList<Product>();
		users = new ArrayList<User>();
		orders = new ArrayList<Order>();
		invoices = new ArrayList<Invoice>();
		map = new Map(0);
		
		readCategories();
		readProducts();
		readMap();
		readUsers();
		readOrders();
		readInvoices();
	}

// =======================read and write categories======================
	/**
	 * readCategories reads the categories data form the document that stored in path catePathname
	 */
	public void readCategories() {
		try {
			File file = new File(catePathname);
			Scanner s = new Scanner(file);
			while (s.hasNext()) {
				String line = s.nextLine();
				String[] para = line.split(",");
				Category newCate = new Category(Integer.parseInt(para[0]), para[1]);
				if (!categories.contains(newCate))
					categories.add(newCate);
			}
			s.close();
		} 
		catch (IOException e) {
		}
	}
	
	/**
	 * writeCategories write the categories data into the document that stored in path catePathname
	 */
	public void writeCategories(){
		try {
			File file = new File(catePathname);
			if ( !file.exists() )
	            file.createNewFile();
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
			for (int i=0;i<categories.size();i++) {
				Category cur = categories.get(i);
				fw.append(String.format("%02d", cur.code)+","+cur.description+"\n");
			}
			fw.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, " can't write category to file", e);
		}
	}

// =======================read and write products======================
	/**
	 * readProduct read the product data from the document that stored in path proPathname
	 */
	public void readProducts(){
		try {
			// id,description,cateCode,price,image,totalQty,DCsQty
			File file = new File(proPathname);
			Scanner s = new Scanner(file);
			while (s.hasNext()) {
				String line = s.nextLine();
				String[] para = line.split(",");
				Product pro = getProduct(para[1]);
				Category cate = getCategory(Integer.parseInt(para[2]));
				double price = Double.parseDouble(para[3]);
				if (pro == null) pro = new Product(Integer.parseInt(para[0]), para[1], cate.code, price);
				pro.totalQty = Integer.parseInt(para[5]);
				pro.image = para[4];
				
				if (para.length >= 7) {
					String[] dcs = para[6].split(" ");
					for (String entry : dcs) {
						String[] item = entry.split("=");
						String city = item[0];
						int quantity = Integer.parseInt(item[1]);
						pro.DCsQty.put(city, quantity);
					}
				}
				if (!products.contains(pro))
					products.add(pro);
			}
			s.close();
			
		}catch (IOException e) {
	
		}
	}
	
	/**
	 * writeProduct write the product data into the document that stored in path proPathname
	 */
	public void writeProducts(){
		try {
			File file = new File(proPathname);
			if ( !file.exists() )
	            file.createNewFile();
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
			for (int i=0;i<products.size();i++)
				fw.append(products.get(i).toString());
			fw.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "can't write Products to file", e);
		}
	}


	// ----------------- helpers for products and categories -----------------
	/**
	 * This method is used to find the category by giving code
	 * @param code  the code for find the category
	 * @return Category the category that has given code,or null if it does not exist
	 */
	public Category getCategory(int code) {
		readCategories();
		Category result = null;
		int idx = -1;
		for (int i = 0; i < categories.size(); i++) {
			if (categories.get(i).code == code)
				idx = i;
		}
		if (idx != -1)
			result = categories.get(idx);
		return result;
	}
	
	/**
	 * This method is used to find the category by giving description
	 * @param des the description for find the category
	 * @return Category the category that contains given description,or null if it does not exist
	 */
	public Category getCategory(String des) {
		readCategories();
		Category result = null;
		int idx = -1;
		for (int i = 0; i < categories.size(); i++) {
			if (categories.get(i).description.equals(des))
				idx = i;
		}
		if (idx != -1)
			result = categories.get(idx);
		return result;
	}

	/**
	 * This method is used to find the product by giving description
	 * @param des  the description for finding the product
	 * @return Product the product that contains given description,or null if it does not exist
	 */
	public Product getProduct(String des) {
		for (int i = 0; i < products.size(); i++)
			if (products.get(i).description.equals(des))
				return products.get(i);
		return null;
	}
	
	
	/**
	 * This method is used to find the product by giving id
	 * @param id  the id for finding the product
	 * @return Product the product that has the given id,or null if it does not exist
	 */
	public Product getProduct(int id) {
		for (int i = 0; i < products.size(); i++)
			if (products.get(i).ID == id)
				return products.get(i);
		return null;
	}
	
// ========================read and write map========================
	/**
	 * This method is used to read the current map information that stored in mapPathname
	 */
	public void readMap() {
		try {
			this.map = new Map(0);
			
			File file = new File(mapPathname);
			Scanner s = new Scanner(file) ;
			
			while (s.hasNext()) {
				String line = s.nextLine();
				String[] para = line.split(",");
				if (para[0].equals("FlightRoute"))
					map.addFRs(para[1], para[2], Integer.parseInt(para[3]));
				else if (para[0].equals("City"))
					map.addCity(para[1]);
				else if (para[0].equals("DistributionCenter"))
					map.addDC(para[1]);
			}
			
			s.close();
		} catch (IOException e) {

		}
	}
	
	/**
	 * This method is used to write data into map that stored in mapPathname
	 */
	public void writeMap() {
		try {
			File file = new File(mapPathname);
			if ( !file.exists() )
	            file.createNewFile();
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
			for (int i=0;i<map.cities.size();i++) {
				fw.append("City," + map.cities.get(i) + "\n");
			}
			for (int i=0;i<map.DCs.size();i++){
				fw.append("DistributionCenter,"+map.DCs.get(i)+"\n");
			}
			
			for (int i = 0;i<map.max;i++){
				for(int j=0; j<=i;j++){
					if(map.distance[i][j] >0){
						String cityA = map.cities.get(i);
						String cityB = map.cities.get(j);
						int dist = map.distance[i][j];
						fw.append("FlightRoute,"+cityA+","+cityB+","+dist +"\n");
					}
				}
			}
			
			fw.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "can't write map to file", e);
		}
	}

// ==================== read and write Users ====================
	/**
	 * This method is used to read the information that stored in usersPathname
	 */
	public void readUsers() {
		//userID,password,sh/ad,custID,custName,custCity,custStreet,Cart,invoices,orders
		try {
			readProducts();
			File file = new File(usersPathname);
			Scanner s = new Scanner(file);
			while (s.hasNext()) {
				String line = s.nextLine();
				String[] para = line.split(",");
				User u = getUser(para[0]);
				u.fm = this;
				if (u == null && para[2].equals("ad")) {
						Administrator ad = new Administrator(para[0], para[1]);
						users.add(ad);
				}
				if (para[2].equals("sh")) {
					Shopper sh = null;
					if (u == null) {
						sh = new Shopper(para[0], para[1]);
						users.add(sh);
					} else {
						sh = (Shopper)u;
					}
					
					// Read info about customer
					if (para.length > 3) {
						int custID = Integer.parseInt(para[3]);
						sh.cust = new Customer(custID, para[4], para[5], para[6]);
					}

					// Read info about cart
					if (para.length >= 8) {
						String[] cart = para[7].split(" ");
						for (String entry : cart) 
							if (!entry.equals("")){
								String[] item = entry.split("=");
								Product pro;
								Integer Qty;
								pro = getProduct(Integer.parseInt(item[0]));
								Qty = Integer.parseInt(item[1]);
								sh.Cart.put(pro, Qty);
							}
					}

					// Read info about invoices(their IDs)
					if (para.length >= 9) {
						String[] invs = para[8].split(" ");
						for (String en : invs) 
							if (!en.equals("")){
								Integer id = Integer.parseInt(en);
								if (!sh.invoices.contains(id)) sh.invoices.add(id);
							}
					}

					// Read info about orders(their IDs)
					if (para.length >= 10) {
						String[] invs = para[9].split(" ");
						for (String en : invs) 
							if (!en.equals("")){
								Integer id = Integer.parseInt(en);
								if (!sh.orders.contains(id)) sh.orders.add(id);
							}
					}
				}
			}
			s.close();
		} catch (IOException e) {

		}
	}
	
	/**
	 * This method is used to write the information that stored in usersPathname
	 */
	public void writeUsers() {
		try {
			File file = new File(usersPathname);
			if ( !file.exists() )
	            file.createNewFile();
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
			for (int i=0;i<users.size();i++) {
				String line = users.get(i).toString();
				fw.append(line);
			}
			fw.close();
		} catch (IOException e){
			logger.log(Level.SEVERE, "can't write users to file", e);
		}
	}
	
	// --------------------helper for user-------------------------
	/**
	 * A helper method that finding the user by a given id
	 * @param userID  user's id
	 * @return User a User that has given id if found,null otherwise
	 */
	public User getUser(String userID) {
		for (User u : users)
			if (u.userID.equals(userID))
				return u;
		return null;
	}
	
// ====================read and write orders ====================
	/**
	 * This method is used to write the order information into file that stored in ordersPathname
	 */
	public void writeOrders() {
		try {
			File file = new File(ordersPathname);
			if ( !file.exists() )
	            file.createNewFile();
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
			for (int i=0;i<orders.size();i++) {
				String line = orders.get(i).toString();
				fw.append(line);
			}
			fw.close();
		} catch (IOException e){
			logger.log(Level.SEVERE, "can't read oders from file", e);
		}
	}
	
	/**
	 * This method is used to read the order information form file that stored in ordersPathname
	 */
	public void readOrders() {
		//orderID,custID,custCity,proID,price,Qty,deliveryCharge
		try {
			File file = new File(ordersPathname);
			Scanner s = new Scanner(file);
			while (s.hasNext()) {
				String line = s.nextLine();
				String[] para = line.split(",");
				Order newOdr = new Order(Integer.parseInt(para[0]), 
						Integer.parseInt(para[1]), Integer.parseInt(para[3]), 
						Double.parseDouble(para[4]), Integer.parseInt(para[5]), para[2], Double.parseDouble(para[6]));
				if (!orders.contains(newOdr))
					orders.add(newOdr);
			}
			s.close();
		} catch (IOException e) {
		}
	}
	
	// ------------------helper for order -----------------------
	/**
	 * A helper method for finding a order given order id
	 * @param oID the order id for find the order
	 * @return Order a Order has the given oID,or null otherwise
	 */
	public Order getOrder(int oID) {
		for (Order o : orders)
			if (o.orderID == oID)
				return o;
		return null;
	}
	
	// ====================read and write invoices ===========================
	/**
	 * This method read the Invoices information in File invociesPathname
	 */
	public void readInvoices() {
		// invID,shopperID,shopperCity,delCharge,items
		try {
			File file = new File(invoicesPathname);
			Scanner s = new Scanner(file);
			while (s.hasNext()) {
				String line = s.nextLine();
				String[] para = line.split(",");
				
				HashMap<Integer, Integer> items = new HashMap<Integer, Integer>();
				if (!para[4].equals("")) {
					String[] cart = para[4].split(" ");
					for (String entry : cart) 
						if (!entry.equals("")){
							String[] item = entry.split("=");
							Integer proID = Integer.parseInt(item[0]);
							Integer qty = Integer.parseInt(item[1]);
							items.put(proID, qty);
						}
				}

				
				Invoice newInv = new Invoice(Integer.parseInt(para[0]), 
						Integer.parseInt(para[1]), para[2], items, Double.parseDouble(para[3]),para[5]);
				if (!invoices.contains(newInv))
					invoices.add(newInv);
			}
			s.close();
		} catch (IOException e) {
		}		
	}
	/**
	 * This method write the Invoices information in File invociesPathname
	 */
	public void writeInvoices() {
		try {
			File file = new File(invoicesPathname);
			if ( !file.exists() )
	            file.createNewFile();
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
			for (int i=0;i<invoices.size();i++) {
				String line = invoices.get(i).toString();
				fw.append(line);
			}
			fw.close();
		} catch (IOException e){
			logger.log(Level.SEVERE, "can't read oders from file", e);
		}
	}
	/**
	 * Get the invoice at given id
	 * @param id the id
	 * @return Invoice the invoice 
	 */
	public Invoice getInvoice(int id) {
		for (Invoice inv : invoices)
			if (inv.ID == id)
				return inv;
		return null;
	}

}
