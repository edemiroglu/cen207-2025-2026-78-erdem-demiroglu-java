package com.hyildizoglu.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Password hashing utility using SHA-256 algorithm.
 * Provides secure one-way hashing for password storage.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class PasswordHasher {

	/**
	 * Hashes a password using SHA-256 algorithm.
	 * 
	 * @param password The plain text password to hash
	 * @return Hashed password as hex string, or null if input is null
	 */
	public static String hash(String password) {
		if (password == null) {
			return null;
		}
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = md.digest(password.getBytes());
			return bytesToHex(hashBytes);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 algorithm not available", e);
		}
	}

	/**
	 * Verifies a password against a stored hash.
	 * 
	 * @param password The plain text password to verify
	 * @param hash     The stored hash to compare against
	 * @return true if password matches hash, false otherwise
	 */
	public static boolean verify(String password, String hash) {
		if (password == null || hash == null) {
			return false;
		}
		return hash(password).equals(hash);
	}

	/**
	 * Converts a byte array to hexadecimal string representation.
	 * 
	 * @param bytes The byte array to convert
	 * @return Hexadecimal string
	 */
	private static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
}
