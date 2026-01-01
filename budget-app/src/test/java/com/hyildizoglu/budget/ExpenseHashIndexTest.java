package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.hash.ExpenseHashIndex;

@DisplayName("ExpenseHashIndex Algorithm Tests")
class ExpenseHashIndexTest {

	private ExpenseHashIndex index;

	@BeforeEach
	void setUp() {
		index = new ExpenseHashIndex();
	}

	@Test
	@DisplayName("Put operation")
	void testPut() {
		index.put(1, 0);
		index.put(2, 1);
		index.put(3, 2);

		assertEquals(Integer.valueOf(0), index.getPosition(1));
		assertEquals(Integer.valueOf(1), index.getPosition(2));
		assertEquals(Integer.valueOf(2), index.getPosition(3));
	}

	@Test
	@DisplayName("Get position - found")
	void testGetPosition_Found() {
		index.put(1, 5);

		Integer position = index.getPosition(1);

		assertEquals(Integer.valueOf(5), position);
	}

	@Test
	@DisplayName("Get position - not found")
	void testGetPosition_NotFound() {
		Integer position = index.getPosition(999);
		assertNull(position);
	}

	@Test
	@DisplayName("Remove operation")
	void testRemove() {
		index.put(1, 0);
		index.put(2, 1);

		boolean removed = index.remove(1);
		assertTrue(removed);

		assertNull(index.getPosition(1));
		assertEquals(Integer.valueOf(1), index.getPosition(2));
	}

	@Test
	@DisplayName("Remove - not found")
	void testRemove_NotFound() {
		boolean removed = index.remove(999);
		assertFalse(removed);
	}

	@Test
	@DisplayName("Collision handling")
	void testCollisionHandling() {
		// Hash collision test - different IDs with same hash value
		index.put(1, 0);
		index.put(2, 1);
		index.put(3, 2);

		// All values should be stored correctly
		assertEquals(Integer.valueOf(0), index.getPosition(1));
		assertEquals(Integer.valueOf(1), index.getPosition(2));
		assertEquals(Integer.valueOf(2), index.getPosition(3));
	}
}
