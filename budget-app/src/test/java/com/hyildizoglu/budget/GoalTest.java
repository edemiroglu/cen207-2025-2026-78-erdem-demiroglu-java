package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.models.Goal;

@DisplayName("Goal Model Tests")
class GoalTest {

	@Test
	@DisplayName("Goal creation")
	void testGoalCreation() {
		Goal goal = new Goal(1, 1, "Vacation", new BigDecimal("5000"), new BigDecimal("1000"),
				LocalDate.of(2025, 12, 31));

		assertEquals(1, goal.getId());
		assertEquals(1, goal.getUserId());
		assertEquals("Vacation", goal.getName());
		assertEquals(new BigDecimal("5000"), goal.getTargetAmount());
		assertEquals(new BigDecimal("1000"), goal.getCurrentAmount());
		assertEquals(LocalDate.of(2025, 12, 31), goal.getDeadline());
	}

	@Test
	@DisplayName("Getter methods")
	void testGetters() {
		Goal goal = new Goal(1, 2, "Test", new BigDecimal("1000"), new BigDecimal("500"),
				LocalDate.of(2025, 6, 30));

		assertEquals(1, goal.getId());
		assertEquals(2, goal.getUserId());
		assertEquals("Test", goal.getName());
		assertEquals(new BigDecimal("1000"), goal.getTargetAmount());
		assertEquals(new BigDecimal("500"), goal.getCurrentAmount());
	}

	@Test
	@DisplayName("Goal completed - equal")
	void testIsCompleted_True() {
		Goal goal = new Goal(1, 1, "Test", new BigDecimal("1000"), new BigDecimal("1000"),
				LocalDate.of(2025, 12, 31));

		assertTrue(goal.isCompleted());
	}

	@Test
	@DisplayName("Goal completed - exceeded")
	void testIsCompleted_Exceeds() {
		Goal goal = new Goal(1, 1, "Test", new BigDecimal("1000"), new BigDecimal("1500"),
				LocalDate.of(2025, 12, 31));

		assertTrue(goal.isCompleted());
	}

	@Test
	@DisplayName("Goal not completed")
	void testIsCompleted_False() {
		Goal goal = new Goal(1, 1, "Test", new BigDecimal("1000"), new BigDecimal("500"),
				LocalDate.of(2025, 12, 31));

		assertFalse(goal.isCompleted());
	}

	@Test
	@DisplayName("Equals method - same ID")
	void testEquals() {
		Goal goal1 = new Goal(1, 1, "Test", new BigDecimal("1000"), new BigDecimal("100"),
				LocalDate.of(2025, 12, 31));
		Goal goal2 = new Goal(1, 2, "Different", new BigDecimal("2000"), new BigDecimal("200"),
				LocalDate.of(2025, 6, 30));
		Goal goal3 = new Goal(2, 1, "Test", new BigDecimal("1000"), new BigDecimal("100"),
				LocalDate.of(2025, 12, 31));

		assertTrue(goal1.equals(goal2)); // Same ID
		assertFalse(goal1.equals(goal3)); // Different ID
	}

	@Test
	@DisplayName("HashCode method")
	void testHashCode() {
		Goal goal1 = new Goal(1, 1, "Test", new BigDecimal("1000"), new BigDecimal("100"),
				LocalDate.of(2025, 12, 31));
		Goal goal2 = new Goal(1, 2, "Different", new BigDecimal("2000"), new BigDecimal("200"),
				LocalDate.of(2025, 6, 30));

		assertEquals(goal1.hashCode(), goal2.hashCode());
	}

	@Test
	@DisplayName("ToString method")
	void testToString() {
		Goal goal = new Goal(1, 1, "Test", new BigDecimal("1000"), new BigDecimal("500"),
				LocalDate.of(2025, 12, 31));

		String str = goal.toString();
		assertTrue(str.contains("Goal"));
		assertTrue(str.contains("Test"));
		assertTrue(str.contains("1000"));
	}
}
