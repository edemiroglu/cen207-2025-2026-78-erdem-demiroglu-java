package com.hyildizoglu.models;

import java.util.Objects;

/**
 * Represents an application user.
 * Users can create budgets, log expenses, and set financial goals.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class User {

	/** Unique identifier for the user. */
	private final int id;
	
	/** Username for login. */
	private final String username;
	
	/** Password for authentication (should be hashed in production). */
	private final String password;

	/**
	 * Creates a new User instance.
	 * 
	 * @param id       Unique identifier for the user
	 * @param username Username for login
	 * @param password Password for authentication
	 */
	public User(int id, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = password;
	}

	/**
	 * Returns the unique identifier of the user.
	 * 
	 * @return The user ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the username.
	 * 
	 * @return The username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Returns the password.
	 * 
	 * @return The password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Checks if this user is equal to another object.
	 * Two users are equal if they have the same ID and username.
	 * 
	 * @param o The object to compare
	 * @return true if equal, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		User user = (User) o;
		return id == user.id && Objects.equals(username, user.username);
	}

	/**
	 * Returns the hash code for this user.
	 * 
	 * @return The hash code based on ID and username
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, username);
	}

	/**
	 * Returns a string representation of this user.
	 * Note: Password is not included for security reasons.
	 * 
	 * @return String containing user details
	 */
	@Override
	public String toString() {
		return "User{id=" + id + ", username='" + username + '\'' + '}';
	}
}
