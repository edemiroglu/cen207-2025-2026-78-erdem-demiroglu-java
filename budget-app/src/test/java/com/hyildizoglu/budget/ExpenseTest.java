package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.models.Expense;

@DisplayName("Expense Model Tests")
class ExpenseTest {

	@Test
	@DisplayName("Expense creation")
	void testExpenseCreation() {
		Expense expense = new Expense(1, 1, 1, 1, new BigDecimal("100.50"),
				LocalDate.of(2025, 1, 15), "Test expense");

		assertEquals(1, expense.getId());
		assertEquals(1, expense.getUserId());
		assertEquals(1, expense.getBudgetId());
		assertEquals(1, expense.getCategoryId());
		assertEquals(new BigDecimal("100.50"), expense.getAmount());
		assertEquals(LocalDate.of(2025, 1, 15), expense.getDate());
		assertEquals("Test expense", expense.getDescription());
	}

	@Test
	@DisplayName("Getter methods")
	void testGetters() {
		Expense expense = new Expense(1, 2, 3, 4, new BigDecimal("200"),
				LocalDate.of(2025, 1, 20), "Description");

		assertEquals(1, expense.getId());
		assertEquals(2, expense.getUserId());
		assertEquals(3, expense.getBudgetId());
		assertEquals(4, expense.getCategoryId());
		assertEquals(new BigDecimal("200"), expense.getAmount());
		assertEquals("Description", expense.getDescription());
	}

	@Test
	@DisplayName("Equals method - same ID")
	void testEquals() {
		Expense expense1 = new Expense(1, 1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Test");
		Expense expense2 = new Expense(1, 2, 2, 2, new BigDecimal("200"),
				LocalDate.of(2025, 1, 20), "Different");
		Expense expense3 = new Expense(2, 1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Test");

		assertTrue(expense1.equals(expense2)); // Same ID
		assertFalse(expense1.equals(expense3)); // Different ID
	}

	@Test
	@DisplayName("HashCode method")
	void testHashCode() {
		Expense expense1 = new Expense(1, 1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Test");
		Expense expense2 = new Expense(1, 2, 2, 2, new BigDecimal("200"),
				LocalDate.of(2025, 1, 20), "Different");

		assertEquals(expense1.hashCode(), expense2.hashCode());
	}

	@Test
	@DisplayName("ToString method")
	void testToString() {
		Expense expense = new Expense(1, 1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Test");

		String str = expense.toString();
		assertTrue(str.contains("Expense"));
		assertTrue(str.contains("100"));
	}
}
