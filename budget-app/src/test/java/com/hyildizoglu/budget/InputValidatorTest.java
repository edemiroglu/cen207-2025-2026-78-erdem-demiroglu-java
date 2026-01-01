package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.core.InputValidator;
import com.hyildizoglu.core.InputValidator.ValidationException;

@DisplayName("InputValidator Tests")
class InputValidatorTest {

	// ==================== INTEGER VALIDATION TESTS ====================

	@Test
	@DisplayName("Valid integer validation")
	void testValidateInteger_Valid() throws ValidationException {
		int result = InputValidator.validateInteger("123", "Test");
		assertEquals(123, result);
	}

	@Test
	@DisplayName("Zero integer should succeed")
	void testValidateInteger_Zero() throws ValidationException {
		int result = InputValidator.validateInteger("0", "Test");
		assertEquals(0, result);
	}

	@Test
	@DisplayName("Integer with whitespace should succeed")
	void testValidateInteger_WithWhitespace() throws ValidationException {
		int result = InputValidator.validateInteger("  42  ", "Test");
		assertEquals(42, result);
	}

	@Test
	@DisplayName("Empty integer should throw error")
	void testValidateInteger_Empty() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validateInteger("", "Test Field");
		});
		assertTrue(ex.getMessage().contains("cannot be empty"));
	}

	@Test
	@DisplayName("Null integer should throw error")
	void testValidateInteger_Null() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validateInteger(null, "Test Field");
		});
		assertTrue(ex.getMessage().contains("cannot be empty"));
	}

	@Test
	@DisplayName("Whitespace only integer should throw error")
	void testValidateInteger_WhitespaceOnly() {
		assertThrows(ValidationException.class, () -> {
			InputValidator.validateInteger("   ", "Test");
		});
	}

	@Test
	@DisplayName("Negative integer should throw error")
	void testValidateInteger_Negative() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validateInteger("-5", "Test Field");
		});
		assertTrue(ex.getMessage().contains("cannot be negative"));
	}

	@Test
	@DisplayName("Invalid integer should throw error")
	void testValidateInteger_Invalid() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validateInteger("abc", "Test Field");
		});
		assertTrue(ex.getMessage().contains("must be a valid number"));
	}

	@Test
	@DisplayName("Decimal as integer should throw error")
	void testValidateInteger_Decimal() {
		assertThrows(ValidationException.class, () -> {
			InputValidator.validateInteger("12.5", "Test");
		});
	}

	// ==================== BIGDECIMAL VALIDATION TESTS ====================

	@Test
	@DisplayName("Valid BigDecimal validation")
	void testValidateBigDecimal_Valid() throws ValidationException {
		BigDecimal result = InputValidator.validateBigDecimal("100.50", "Test", false);
		assertEquals(new BigDecimal("100.50"), result);
	}

	@Test
	@DisplayName("Zero BigDecimal should succeed")
	void testValidateBigDecimal_Zero() throws ValidationException {
		BigDecimal result = InputValidator.validateBigDecimal("0", "Test", false);
		assertEquals(BigDecimal.ZERO, result);
	}

	@Test
	@DisplayName("BigDecimal with whitespace should succeed")
	void testValidateBigDecimal_WithWhitespace() throws ValidationException {
		BigDecimal result = InputValidator.validateBigDecimal("  50.25  ", "Test", false);
		assertEquals(new BigDecimal("50.25"), result);
	}

	@Test
	@DisplayName("Empty BigDecimal should throw error")
	void testValidateBigDecimal_Empty() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validateBigDecimal("", "Test Field", false);
		});
		assertTrue(ex.getMessage().contains("cannot be empty"));
	}

	@Test
	@DisplayName("Null BigDecimal should throw error")
	void testValidateBigDecimal_Null() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validateBigDecimal(null, "Test Field", false);
		});
		assertTrue(ex.getMessage().contains("cannot be empty"));
	}

	@Test
	@DisplayName("Whitespace only BigDecimal should throw error")
	void testValidateBigDecimal_WhitespaceOnly() {
		assertThrows(ValidationException.class, () -> {
			InputValidator.validateBigDecimal("   ", "Test", false);
		});
	}

	@Test
	@DisplayName("Invalid BigDecimal format should throw error")
	void testValidateBigDecimal_InvalidFormat() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validateBigDecimal("abc", "Test Field", false);
		});
		assertTrue(ex.getMessage().contains("must be a valid number"));
	}

	@Test
	@DisplayName("Negative BigDecimal not allowed should throw error")
	void testValidateBigDecimal_NegativeNotAllowed() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validateBigDecimal("-10", "Test Field", false);
		});
		assertTrue(ex.getMessage().contains("cannot be negative"));
	}

	@Test
	@DisplayName("Negative BigDecimal allowed should succeed")
	void testValidateBigDecimal_NegativeAllowed() throws ValidationException {
		BigDecimal result = InputValidator.validateBigDecimal("-10", "Test", true);
		assertEquals(new BigDecimal("-10"), result);
	}

	@Test
	@DisplayName("Large BigDecimal should succeed")
	void testValidateBigDecimal_Large() throws ValidationException {
		BigDecimal result = InputValidator.validateBigDecimal("999999999.99", "Test", false);
		assertEquals(new BigDecimal("999999999.99"), result);
	}

	// ==================== DATE VALIDATION TESTS ====================

	@Test
	@DisplayName("Valid date validation")
	void testValidateDate_Valid() throws ValidationException {
		LocalDate result = InputValidator.validateDate("2025-01-15", "Test");
		assertEquals(LocalDate.of(2025, 1, 15), result);
	}

	@Test
	@DisplayName("Date with whitespace should succeed")
	void testValidateDate_WithWhitespace() throws ValidationException {
		LocalDate result = InputValidator.validateDate("  2025-06-20  ", "Test");
		assertEquals(LocalDate.of(2025, 6, 20), result);
	}

	@Test
	@DisplayName("Empty date should throw error")
	void testValidateDate_Empty() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validateDate("", "Test Field");
		});
		assertTrue(ex.getMessage().contains("cannot be empty"));
	}

	@Test
	@DisplayName("Null date should throw error")
	void testValidateDate_Null() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validateDate(null, "Test Field");
		});
		assertTrue(ex.getMessage().contains("cannot be empty"));
	}

	@Test
	@DisplayName("Whitespace only date should throw error")
	void testValidateDate_WhitespaceOnly() {
		assertThrows(ValidationException.class, () -> {
			InputValidator.validateDate("   ", "Test");
		});
	}

	@Test
	@DisplayName("Invalid date format should throw error")
	void testValidateDate_Invalid() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validateDate("invalid", "Test Field");
		});
		assertTrue(ex.getMessage().contains("invalid date format"));
	}

	@Test
	@DisplayName("Wrong date format should throw error")
	void testValidateDate_WrongFormat() {
		assertThrows(ValidationException.class, () -> {
			InputValidator.validateDate("15-01-2025", "Test");
		});
	}

	@Test
	@DisplayName("Invalid month should throw error")
	void testValidateDate_InvalidMonth() {
		assertThrows(ValidationException.class, () -> {
			InputValidator.validateDate("2025-13-01", "Test");
		});
	}

	@Test
	@DisplayName("Invalid day should throw error")
	void testValidateDate_InvalidDay() {
		assertThrows(ValidationException.class, () -> {
			InputValidator.validateDate("2025-02-30", "Test");
		});
	}

	// ==================== DATE RANGE VALIDATION TESTS ====================

	@Test
	@DisplayName("Valid date range validation")
	void testValidateDateRange_Valid() {
		LocalDate start = LocalDate.of(2025, 1, 1);
		LocalDate end = LocalDate.of(2025, 1, 31);
		assertDoesNotThrow(() -> {
			InputValidator.validateDateRange(start, end);
		});
	}

	@Test
	@DisplayName("Same start and end date should succeed")
	void testValidateDateRange_SameDate() {
		LocalDate date = LocalDate.of(2025, 1, 15);
		assertDoesNotThrow(() -> {
			InputValidator.validateDateRange(date, date);
		});
	}

	@Test
	@DisplayName("Invalid date range should throw error")
	void testValidateDateRange_Invalid() {
		LocalDate start = LocalDate.of(2025, 1, 31);
		LocalDate end = LocalDate.of(2025, 1, 1);
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validateDateRange(start, end);
		});
		assertTrue(ex.getMessage().contains("Start date cannot be after end date"));
	}

	// ==================== NON-EMPTY STRING VALIDATION TESTS ====================

	@Test
	@DisplayName("Non-empty string validation")
	void testValidateNonEmptyString_Valid() throws ValidationException {
		String result = InputValidator.validateNonEmptyString("test", "Test");
		assertEquals("test", result);
	}

	@Test
	@DisplayName("String with whitespace should be trimmed")
	void testValidateNonEmptyString_WithWhitespace() throws ValidationException {
		String result = InputValidator.validateNonEmptyString("  hello  ", "Test");
		assertEquals("hello", result);
	}

	@Test
	@DisplayName("Empty string should throw error")
	void testValidateNonEmptyString_Empty() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validateNonEmptyString("", "Test Field");
		});
		assertTrue(ex.getMessage().contains("cannot be empty"));
	}

	@Test
	@DisplayName("Null string should throw error")
	void testValidateNonEmptyString_Null() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validateNonEmptyString(null, "Test Field");
		});
		assertTrue(ex.getMessage().contains("cannot be empty"));
	}

	@Test
	@DisplayName("Whitespace only string should throw error")
	void testValidateNonEmptyString_WhitespaceOnly() {
		assertThrows(ValidationException.class, () -> {
			InputValidator.validateNonEmptyString("   ", "Test");
		});
	}

	// ==================== USERNAME VALIDATION TESTS ====================

	@Test
	@DisplayName("Valid username validation")
	void testValidateUsername_Valid() {
		assertDoesNotThrow(() -> {
			InputValidator.validateUsername("testuser");
		});
	}

	@Test
	@DisplayName("Minimum length username should succeed")
	void testValidateUsername_MinLength() {
		assertDoesNotThrow(() -> {
			InputValidator.validateUsername("abc");
		});
	}

	@Test
	@DisplayName("Username with whitespace should succeed after trim")
	void testValidateUsername_WithWhitespace() {
		assertDoesNotThrow(() -> {
			InputValidator.validateUsername("  testuser  ");
		});
	}

	@Test
	@DisplayName("Empty username should throw error")
	void testValidateUsername_Empty() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validateUsername("");
		});
		assertTrue(ex.getMessage().contains("cannot be empty"));
	}

	@Test
	@DisplayName("Null username should throw error")
	void testValidateUsername_Null() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validateUsername(null);
		});
		assertTrue(ex.getMessage().contains("cannot be empty"));
	}

	@Test
	@DisplayName("Whitespace only username should throw error")
	void testValidateUsername_WhitespaceOnly() {
		assertThrows(ValidationException.class, () -> {
			InputValidator.validateUsername("   ");
		});
	}

	@Test
	@DisplayName("Short username should throw error")
	void testValidateUsername_TooShort() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validateUsername("ab");
		});
		assertTrue(ex.getMessage().contains("at least 3 characters"));
	}

	@Test
	@DisplayName("Too long username should throw error")
	void testValidateUsername_TooLong() {
		// 51 characters - exceeds max of 50
		String longUsername = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validateUsername(longUsername);
		});
		assertTrue(ex.getMessage().contains("cannot exceed 50 characters"));
	}

	@Test
	@DisplayName("Maximum length username should succeed")
	void testValidateUsername_MaxLength() {
		// Exactly 50 characters
		String maxUsername = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		assertDoesNotThrow(() -> {
			InputValidator.validateUsername(maxUsername);
		});
	}

	// ==================== PASSWORD VALIDATION TESTS ====================

	@Test
	@DisplayName("Valid password validation")
	void testValidatePassword_Valid() {
		assertDoesNotThrow(() -> {
			InputValidator.validatePassword("password123");
		});
	}

	@Test
	@DisplayName("Minimum length password should succeed")
	void testValidatePassword_MinLength() {
		assertDoesNotThrow(() -> {
			InputValidator.validatePassword("abc");
		});
	}

	@Test
	@DisplayName("Empty password should throw error")
	void testValidatePassword_Empty() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validatePassword("");
		});
		assertTrue(ex.getMessage().contains("cannot be empty"));
	}

	@Test
	@DisplayName("Null password should throw error")
	void testValidatePassword_Null() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validatePassword(null);
		});
		assertTrue(ex.getMessage().contains("cannot be empty"));
	}

	@Test
	@DisplayName("Short password should throw error")
	void testValidatePassword_TooShort() {
		ValidationException ex = assertThrows(ValidationException.class, () -> {
			InputValidator.validatePassword("ab");
		});
		assertTrue(ex.getMessage().contains("at least 3 characters"));
	}

	@Test
	@DisplayName("Long password should succeed")
	void testValidatePassword_Long() {
		// 100 characters password
		String longPassword = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		assertDoesNotThrow(() -> {
			InputValidator.validatePassword(longPassword);
		});
	}

	// ==================== VALIDATION EXCEPTION TESTS ====================

	@Test
	@DisplayName("ValidationException message should be correct")
	void testValidationException_Message() {
		ValidationException ex = new ValidationException("Test error message");
		assertEquals("Test error message", ex.getMessage());
	}

	@Test
	@DisplayName("ValidationException is an Exception")
	void testValidationException_IsException() {
		ValidationException ex = new ValidationException("Test");
		assertTrue(ex instanceof Exception);
	}

	// ==================== CONSTRUCTOR TEST ====================

	@Test
	@DisplayName("InputValidator utility class can be instantiated")
	void testInputValidatorConstructor() throws Exception {
		// Use reflection to test the private default constructor for coverage
		java.lang.reflect.Constructor<InputValidator> constructor = 
			InputValidator.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		InputValidator instance = constructor.newInstance();
		assertNotNull(instance);
	}
}
