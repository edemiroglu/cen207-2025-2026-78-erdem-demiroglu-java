package com.hyildizoglu.core;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Input validation utility class.
 * Provides static methods for validating various types of user input.
 * All validation methods throw ValidationException on failure.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class InputValidator {

	/**
	 * Validates and parses an integer from string.
	 * Rejects null, empty, and negative values.
	 * 
	 * @param input     The input string to validate
	 * @param fieldName The field name for error messages
	 * @return Parsed non-negative integer
	 * @throws ValidationException if validation fails
	 */
	public static int validateInteger(String input, String fieldName) throws ValidationException {
		if (input == null || input.trim().isEmpty()) {
			throw new ValidationException(fieldName + " cannot be empty.");
		}
		try {
			int value = Integer.parseInt(input.trim());
			if (value < 0) {
				throw new ValidationException(fieldName + " cannot be negative.");
			}
			return value;
		} catch (NumberFormatException e) {
			throw new ValidationException(fieldName + " must be a valid number.");
		}
	}

	/**
	 * Validates and parses a BigDecimal from string.
	 * 
	 * @param input         The input string to validate
	 * @param fieldName     The field name for error messages
	 * @param allowNegative Whether negative values are allowed
	 * @return Parsed BigDecimal value
	 * @throws ValidationException if validation fails
	 */
	public static BigDecimal validateBigDecimal(String input, String fieldName, boolean allowNegative)
			throws ValidationException {
		if (input == null || input.trim().isEmpty()) {
			throw new ValidationException(fieldName + " cannot be empty.");
		}
		try {
			BigDecimal value = new BigDecimal(input.trim());
			if (!allowNegative && value.compareTo(BigDecimal.ZERO) < 0) {
				throw new ValidationException(fieldName + " cannot be negative.");
			}
			return value;
		} catch (NumberFormatException e) {
			throw new ValidationException(fieldName + " must be a valid number.");
		}
	}

	/**
	 * Validates and parses a LocalDate from string.
	 * Expected format: YYYY-MM-DD (ISO 8601).
	 * 
	 * @param input     The input string to validate
	 * @param fieldName The field name for error messages
	 * @return Parsed LocalDate
	 * @throws ValidationException if validation fails
	 */
	public static LocalDate validateDate(String input, String fieldName) throws ValidationException {
		if (input == null || input.trim().isEmpty()) {
			throw new ValidationException(fieldName + " cannot be empty.");
		}
		try {
			return LocalDate.parse(input.trim());
		} catch (DateTimeParseException e) {
			throw new ValidationException(fieldName + " has invalid date format. Format: YYYY-MM-DD");
		}
	}

	/**
	 * Validates that start date is not after end date.
	 * 
	 * @param startDate The start date
	 * @param endDate   The end date
	 * @throws ValidationException if start date is after end date
	 */
	public static void validateDateRange(LocalDate startDate, LocalDate endDate) throws ValidationException {
		if (startDate.isAfter(endDate)) {
			throw new ValidationException("Start date cannot be after end date.");
		}
	}

	/**
	 * Validates a non-empty string.
	 * 
	 * @param input     The input string to validate
	 * @param fieldName The field name for error messages
	 * @return Trimmed non-empty string
	 * @throws ValidationException if validation fails
	 */
	public static String validateNonEmptyString(String input, String fieldName) throws ValidationException {
		if (input == null || input.trim().isEmpty()) {
			throw new ValidationException(fieldName + " cannot be empty.");
		}
		return input.trim();
	}

	/**
	 * Validates username format.
	 * Requirements: non-empty, 3-50 characters.
	 * 
	 * @param username The username to validate
	 * @throws ValidationException if validation fails
	 */
	public static void validateUsername(String username) throws ValidationException {
		if (username == null || username.trim().isEmpty()) {
			throw new ValidationException("Username cannot be empty.");
		}
		if (username.trim().length() < 3) {
			throw new ValidationException("Username must be at least 3 characters.");
		}
		if (username.trim().length() > 50) {
			throw new ValidationException("Username cannot exceed 50 characters.");
		}
	}

	/**
	 * Validates password format.
	 * Requirements: non-empty, at least 3 characters.
	 * 
	 * @param password The password to validate
	 * @throws ValidationException if validation fails
	 */
	public static void validatePassword(String password) throws ValidationException {
		if (password == null || password.isEmpty()) {
			throw new ValidationException("Password cannot be empty.");
		}
		if (password.length() < 3) {
			throw new ValidationException("Password must be at least 3 characters.");
		}
	}

	/**
	 * Custom exception for validation errors.
	 * Thrown when user input fails validation checks.
	 */
	public static class ValidationException extends Exception {
		
		/** Serialization version UID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Creates a new ValidationException with the specified message.
		 * 
		 * @param message The error message
		 */
		public ValidationException(String message) {
			super(message);
		}
	}
}
