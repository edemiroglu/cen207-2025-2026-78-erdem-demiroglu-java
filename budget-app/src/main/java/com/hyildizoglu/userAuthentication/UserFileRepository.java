package com.hyildizoglu.userAuthentication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.hyildizoglu.core.PasswordHasher;
import com.hyildizoglu.models.User;

/**
 * Repository for storing and retrieving user data from a file.
 * Uses a simple semicolon-separated format for persistence.
 * Passwords are hashed before storage.
 * 
 * File format (one user per line):
 * id;username;hashedPassword
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class UserFileRepository {

	/** Path to the user data file. */
	private final Path filePath;

	/**
	 * Creates a repository with the default file path (User.dat).
	 */
	public UserFileRepository() {
		this(Paths.get("User.dat"));
	}

	/**
	 * Creates a repository with the specified file path.
	 * 
	 * @param filePath Path to the user data file
	 */
	public UserFileRepository(Path filePath) {
		this.filePath = filePath;
	}

	/**
	 * Retrieves all users from the file.
	 * 
	 * @return List of all users, or empty list if file doesn't exist
	 */
	public List<User> findAll() {
		List<User> users = new ArrayList<>();

		if (!Files.exists(filePath)) {
			return users;
		}

		try (BufferedReader reader = Files.newBufferedReader(filePath)) {
			String line;
			while ((line = reader.readLine()) != null) {
				User user = parseUser(line);
				if (user != null) {
					users.add(user);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to read users from file", e);
		}

		return users;
	}

	/**
	 * Finds a user by username (case-insensitive).
	 * 
	 * @param username The username to search for
	 * @return Optional containing the user if found
	 */
	public Optional<User> findByUsername(String username) {
		return findAll().stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
	}

	/**
	 * Saves a new user with hashed password.
	 * Automatically assigns a new ID.
	 * 
	 * @param username The username
	 * @param password The plain-text password (will be hashed)
	 * @return The saved user with assigned ID
	 */
	public User save(String username, String password) {
		List<User> users = findAll();
		int nextId = users.stream().mapToInt(User::getId).max().orElse(0) + 1;
		// Hash the password before saving
		String hashedPassword = PasswordHasher.hash(password);
		User user = new User(nextId, username, hashedPassword);
		users.add(user);
		writeAll(users);
		return user;
	}

	/**
	 * Writes all users to the file.
	 * 
	 * @param users List of users to write
	 */
	private void writeAll(List<User> users) {
		try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
			for (User user : users) {
				writer.write(formatUser(user));
				writer.newLine();
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to write users to file", e);
		}
	}

	/**
	 * Parses a line from the file into a User object.
	 * 
	 * @param line The line to parse
	 * @return Parsed User, or null if parsing fails
	 */
	private User parseUser(String line) {
		String[] parts = line.split(";", -1);
		if (parts.length < 3) {
			return null;
		}
		try {
			int id = Integer.parseInt(parts[0]);
			String username = parts[1];
			String password = parts[2];
			return new User(id, username, password);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Formats a user as a line for file storage.
	 * 
	 * @param user The user to format
	 * @return Formatted string representation
	 */
	private String formatUser(User user) {
		return user.getId() + ";" + user.getUsername() + ";" + user.getPassword();
	}
}
