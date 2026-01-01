package com.hyildizoglu.userAuthentication;

import java.util.Optional;

import com.hyildizoglu.models.User;

/**
 * Service for user authentication operations.
 * Handles user registration, login, and guest access.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class UserAuthService {

	/** Repository for user data access. */
	private final UserFileRepository userRepository;

	/**
	 * Creates a new UserAuthService with the specified repository.
	 * 
	 * @param userRepository The repository for user data access
	 */
	public UserAuthService(UserFileRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Registers a new user if the username is not already taken.
	 * Password is hashed before storage.
	 * 
	 * @param username The desired username
	 * @param password The password (will be hashed)
	 * @return Optional containing the created user, or empty if username exists
	 */
	public Optional<User> register(String username, String password) {
		if (userRepository.findByUsername(username).isPresent()) {
			return Optional.empty();
		}
		User user = userRepository.save(username, password);
		return Optional.of(user);
	}

	/**
	 * Attempts to authenticate a user with the given credentials.
	 * 
	 * @param username The username
	 * @param password The password to verify
	 * @return Optional containing the authenticated user, or empty if invalid
	 */
	public Optional<User> login(String username, String password) {
		return userRepository.findByUsername(username)
				.filter(u -> com.hyildizoglu.core.PasswordHasher.verify(password, u.getPassword()));
	}

	/**
	 * Creates a temporary guest user for anonymous access.
	 * Guest users have ID -1 and no password.
	 * 
	 * @return A guest user instance
	 */
	public User createGuestUser() {
		return new User(-1, "guest", "");
	}
}
