package project;
/**
 * A class representing the session ID
 */
public class Session {
	protected static int idCounter = 1;
	protected int ID;
	protected String userID;
	protected FileManager fm;
	
	/**
	 * initialize a Session object
	 * @param userID the user ID of an logged in user
	 * @param f the file manager of this Session
	 */
	public Session(String userID, FileManager f) {
		this.userID = userID;
		fm = f;
		ID = idCounter;
		idCounter++;
	}

	/**
	 * Get the id of a specific Session object
	 * @return ID : the id of this Session object
	 */
	public int getID() {
		return ID;
	}
	
	/**
	 * Get the user of a specific Session object
	 * @return User: the user with the userID recorded in this Session object
	 */
	public User getUser() {
		return fm.getUser(userID);
	}
	
	/**
	 * return true if the user has logged in the system
	 * @param userID the user ID of the user
	 * @return return true if the userID is recorded in this Session
	 */
	public boolean isLoggedin(String userID) {
		return this.userID.equals(userID);
	}

	/**
	 * ToString of the Session object
	 */
	@Override
	public String toString() {
		return "Session [userID=" + userID + "session ID: " + ID + "]";
	}
	
}
