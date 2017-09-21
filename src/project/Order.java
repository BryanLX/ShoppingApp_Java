package project;

/**
 * A class representing an Order.
 */
public class Order {
	protected static int IDcounter = 1;
	protected int orderID;
	protected int custID;
	protected String custCity;
	protected double price;
	protected int proID;
	protected int Qty;
	protected double deliveryCharge;
	
	/**
	 * Initialize an Order object.
	 *
	 * @param custID customer ID
	 * @param prod product
	 * @param quantity quantity
	 * @param custCity destination city
	 * @param deliveryCharge delivery charge
     */
	public Order(int custID, Product prod, int quantity, String custCity, double deliveryCharge) {
		orderID = IDcounter;
		IDcounter++;
		this.custID = custID;
		this.proID = prod.ID;
		this.price = prod.price;
		this.Qty= quantity;
		this.custCity = custCity;
		this.deliveryCharge = deliveryCharge;
	}
	
	/**
	 * Initialize an Order object.
	 *
	 * @param oID Order ID
	 * @param custID customer ID
	 * @param proID product ID
	 * @param price price
	 * @param quantity quantity
	 * @param custCity Destination City
     * @param deliveryCharge delivery charge
     */
	public Order(int oID, int custID, int proID, double price, int quantity, String custCity, double deliveryCharge) {
		orderID = oID;
		if (IDcounter <= oID)
			IDcounter = oID + 1;
		this.custID = custID;
		this.proID = proID;
		this.price = price;
		this.Qty= quantity;
		this.custCity = custCity;
		this.deliveryCharge = deliveryCharge;
	}

	/**
	 * Return true iff id is equal to this Order's ID.
	 *
	 * @param id ID
	 * @return boolean true iff id is equal to this Order's ID.
     */
	public boolean isID(int id) {
		return id == orderID;
	}
	
	/**
	 * Return invoice payment amount.
	 *
	 * @return double payment total
     */
	public double prodPrice() {
		return price;
	}
	
	/**
	 * Return true iff o is equal to this Order.
	 *
	 * @param o object being compared to
	 * @return boolean true iff o is equal to this Order.
     */
	@Override
	public boolean equals(Object o){
		if (o == null) return false;
		if (!(o instanceof Order)) return false;
		if (o == this) return true;
		return orderID == ((Order)o).orderID;
	}
	
	/**
	 * Return a string representation of this Order.
	 *
	 * @return String the string representation of this Order.
     */
	@Override
	public String toString() {
		//orderID,custID,custCity,proID,price,Qty,deliveryCharge
		return orderID + "," + custID + "," + custCity + "," 
				+ proID + "," + price + "," + Qty + "," + deliveryCharge + "\n";
	}


}
