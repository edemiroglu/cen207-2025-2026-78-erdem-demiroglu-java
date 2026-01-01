package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.models.Budget;

@DisplayName("Budget Model Tests")
class BudgetTest {

	@Test
	@DisplayName("Budget creation")
	void testBudgetCreation() {
		Budget budget = new Budget(1, 1, "Test Budget", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

		assertEquals(1, budget.getId());
		assertEquals(1, budget.getUserId());
		assertEquals("Test Budget", budget.getName());
		assertEquals(new BigDecimal("1000"), budget.getTotalLimit());
		assertEquals(LocalDate.of(2025, 1, 1), budget.getStartDate());
		assertEquals(LocalDate.of(2025, 1, 31), budget.getEndDate());
	}

	@Test
	@DisplayName("Getter methods")
	void testGetters() {
		Budget budget = new Budget(1, 2, "Test", new BigDecimal("500"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

		assertEquals(1, budget.getId());
		assertEquals(2, budget.getUserId());
		assertEquals("Test", budget.getName());
		assertEquals(new BigDecimal("500"), budget.getTotalLimit());
	}

	@Test
	@DisplayName("Equals method - same ID")
	void testEquals() {
		Budget budget1 = new Budget(1, 1, "Test", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
		Budget budget2 = new Budget(1, 2, "Different", new BigDecimal("2000"),
				LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28));
		Budget budget3 = new Budget(2, 1, "Test", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

		assertTrue(budget1.equals(budget2)); // Same ID
		assertFalse(budget1.equals(budget3)); // Different ID
	}

	@Test
	@DisplayName("HashCode method")
	void testHashCode() {
		Budget budget1 = new Budget(1, 1, "Test", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
		Budget budget2 = new Budget(1, 2, "Different", new BigDecimal("2000"),
				LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28));

		assertEquals(budget1.hashCode(), budget2.hashCode());
	}

	@Test
	@DisplayName("ToString method")
	void testToString() {
		Budget budget = new Budget(1, 1, "Test", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

		String str = budget.toString();
		assertTrue(str.contains("Budget"));
		assertTrue(str.contains("Test"));
		assertTrue(str.contains("1000"));
	}
}
