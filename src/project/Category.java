package project;

/**
 * Class representing a category of the shopping application.
 */

public class Category implements Comparable<Category> {
	protected static int codeCounter = 1;
	protected int code;
	protected String description;
	
	/**
	 * Initializes a Category object with a description.
	 *
	 * @param description  description
     */
	public Category(String description) {
		this.code = codeCounter;
		codeCounter++;
		this.description = description;
	}
	
	/**
	 * Initializes a Category object with a code and a description.
	 * I
	 * @param code  Category code
	 * @param description  description
     */
	public Category(int code, String description) {
		this.code = code;
		if (codeCounter <= code)
			codeCounter = code + 1;
		this.description = description;
	}	
	/**
	 * Return true iff this Category equals other.
	 *
	 * @param o  other object
	 * @return boolean true iff this Category equals other.
     */
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof Category)) return false;
		if (o == this) return true;
		return code == ((Category)o).code
				&& description.equals(((Category)o).description);
	}
	
	/**
	 * Return a string representation of this category.
	 *
	 * @return String string representation of this category.
     */
	@Override
	public String toString() {
		return String.format("%02d", code) + " " + description;
	}
	
	/**
	 * Return the difference between this category and other.
	 *
	 * @param other other Category
	 * @return int difference
     */
	@Override
	public int compareTo(Category other) {
		return code - other.code;
	}
}
