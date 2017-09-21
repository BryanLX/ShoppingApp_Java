
package project;


import java.util.HashMap;

/**
 * A class representing an invoice.
 */
public class Invoice {
	protected static int IDcounter  = 1;
	protected int ID;
	protected int shopperID;
	protected String shopperCity;
	protected HashMap<Integer, Integer> items;
	protected double delCharge;
	protected String date;

	/**
	 * initialize an Invoice object
	 * @param shopperID the shopper's id
	 * @param shopperCity the shopper's city
	 * @param pros  the product
	 * @param delCharge the delcharge per km
	 * @param time the time
	 */
	public Invoice(int shopperID, String shopperCity, HashMap<Product, Integer> pros, double delCharge,String time) {
		ID = IDcounter;
		IDcounter++;
		this.shopperID = shopperID;
		this.shopperCity = shopperCity;
		items = new HashMap<Integer, Integer>();
		for (Product p : pros.keySet())
			items.put(p.ID, pros.get(p));
		this.delCharge = delCharge;
		this.date = time;
	}
	
	/**
	 * initialize an Invoice object
	 * @param ID id of Invoice
	 * @param shopperID shopper id if invoice
	 * @param shopperCity shopperCity of invoice
	 * @param pros	the pros of invoice
	 * @param delCharge the delivery charge per km
	 * @param time the time
	 */ 
	public Invoice(int ID, int shopperID, String shopperCity, HashMap<Integer, Integer> pros, double delCharge,String time) {
		this.ID = ID;
		if (IDcounter <= ID)
			IDcounter = ID + 1;
		this.shopperID = shopperID;
		this.shopperCity = shopperCity;
		items = pros;
		this.delCharge = delCharge;
		this.date = time;
	}	
	
	/**
	 * return true iff the ID of the invoice is equal to id
	 * @param id the input id number
	 * @return true if the ID of the invoice is id
	 */
	public boolean isID(int id) {
		return id == ID;
	}
	
	/**
	 * Return true iff o is equal to this Invoice.
	 *
	 * @param o object being compared to
	 * @return boolean true iff o is equal to this Invoice.
     */
	@Override
	public boolean equals(Object o){
		if (o == null) return false;
		if (!(o instanceof Invoice)) return false;
		if (o == this) return true;
		return ID == ((Invoice)o).ID;
	}
	/**
	 * Return the string representation of invoice
	 * @return String the string representation of invoice
	 */
	@Override
	public String toString() {
		// invID,shopperID,shopperCity,delCharge,items
		String result = "";
		result += new Integer(ID).toString() + ",";
		result += new Integer(shopperID).toString() + ",";
		result += shopperCity + ",";
		result += new Double(delCharge).toString() + ",";
		for (Integer key : items.keySet()) {
		    result += key.toString() + "=" + items.get(key) + " ";
		}
		result += ",";
		result += this.date;
		result += "\n";
		return result;
	}
	

}

