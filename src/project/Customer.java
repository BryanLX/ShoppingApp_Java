package project;
/**
 * A class representing a customer who belong to a shopper.
 */
public class Customer {
	protected static int cusCounter = 1;
	protected int custID = -1;
	protected String name;
	protected String city;
	protected String street;
	/**
	 * The constructor of customer without given id
	 * @param custName the name of the customer
	 * @param city  the city customer lives
	 * @param street  the street customer lives
	 */
	
	public Customer(String custName, String city, String street) {
		this.custID = cusCounter;
		cusCounter++;
		this.name = custName;
		this.city = city;
		this.street = street;
	}
	/**
	 * The constructor of customer with given id
	 * @param id  the id of customer
	 * @param custName the name of the customer
	 * @param city  the city customer lives
	 * @param street  the street customer lives
	 */
	public Customer(int id, String custName, String city, String street) {
		this.custID = id;
		if (id > cusCounter)
			cusCounter = id +1;
		this.name = custName;
		this.city = city;
		this.street = street;
	}
	/**
	 * Return the string representation of customer
	 * @return String the string representation of customer
	 */
	@Override
	public String toString() {
		//userID,password,custID,custName,custCity,custStreet,Cart,invoices,orders
		String result = "";
		result += new Integer(custID).toString() + ",";
		result += name + ",";
		result += city + ",";
		result += street;
		return result;
	}
	
	

}
