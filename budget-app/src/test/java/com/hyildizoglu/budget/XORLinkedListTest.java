package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.lists.XORLinkedList;

@DisplayName("XORLinkedList Tests")
class XORLinkedListTest {

	private XORLinkedList<String> list;

	@BeforeEach
	void setUp() {
		list = new XORLinkedList<>();
	}

	// ==================== BASIC OPERATIONS ====================

	@Test
	@DisplayName("New list should have size 0")
	void testNewListSize() {
		assertEquals(0, list.size());
	}

	@Test
	@DisplayName("Current on empty list should return null")
	void testCurrentOnEmpty() {
		assertNull(list.current());
	}

	@Test
	@DisplayName("Add single element should work")
	void testAddSingleElement() {
		list.add("first");
		assertEquals(1, list.size());
		assertEquals("first", list.current());
	}

	@Test
	@DisplayName("Add multiple elements should work")
	void testAddMultipleElements() {
		list.add("first");
		list.add("second");
		list.add("third");
		assertEquals(3, list.size());
	}

	// ==================== NAVIGATION ====================

	@Test
	@DisplayName("Current should return first element after add")
	void testCurrentAfterAdd() {
		list.add("first");
		list.add("second");
		assertEquals("first", list.current());
	}

	@Test
	@DisplayName("Next should move to next element")
	void testNext() {
		list.add("first");
		list.add("second");
		list.add("third");
		
		assertEquals("first", list.current());
		assertEquals("second", list.next());
		assertEquals("second", list.current());
		assertEquals("third", list.next());
		assertEquals("third", list.current());
	}

	@Test
	@DisplayName("Next at end should stay at last element")
	void testNextAtEnd() {
		list.add("first");
		list.add("second");
		
		list.next(); // move to second
		String result = list.next(); // try to go past
		
		assertEquals("second", result);
		assertEquals("second", list.current());
	}

	@Test
	@DisplayName("Previous should move to previous element")
	void testPrevious() {
		list.add("first");
		list.add("second");
		list.add("third");
		
		list.next(); // second
		list.next(); // third
		
		assertEquals("third", list.current());
		assertEquals("second", list.previous());
		assertEquals("second", list.current());
		assertEquals("first", list.previous());
		assertEquals("first", list.current());
	}

	@Test
	@DisplayName("Previous at beginning should stay at first element")
	void testPreviousAtBeginning() {
		list.add("first");
		list.add("second");
		
		String result = list.previous(); // try to go before first
		
		assertEquals("first", result);
		assertEquals("first", list.current());
	}

	@Test
	@DisplayName("Navigate forward and backward")
	void testForwardBackward() {
		list.add("a");
		list.add("b");
		list.add("c");
		list.add("d");
		
		assertEquals("a", list.current());
		assertEquals("b", list.next());
		assertEquals("c", list.next());
		assertEquals("d", list.next());
		assertEquals("c", list.previous());
		assertEquals("b", list.previous());
		assertEquals("a", list.previous());
		assertEquals("a", list.previous()); // should stay at first
	}

	// ==================== EDGE CASES ====================

	@Test
	@DisplayName("Next on empty list should return null")
	void testNextOnEmpty() {
		assertNull(list.next());
	}

	@Test
	@DisplayName("Previous on empty list should return null")
	void testPreviousOnEmpty() {
		assertNull(list.previous());
	}

	@Test
	@DisplayName("Single element navigation")
	void testSingleElementNavigation() {
		list.add("only");
		
		assertEquals("only", list.current());
		assertEquals("only", list.next()); // stay at only
		assertEquals("only", list.previous()); // stay at only
	}

	// ==================== SIZE TESTS ====================

	@Test
	@DisplayName("Size should increase as elements are added")
	void testSizeIncrease() {
		assertEquals(0, list.size());
		
		list.add("a");
		assertEquals(1, list.size());
		
		list.add("b");
		assertEquals(2, list.size());
		
		list.add("c");
		assertEquals(3, list.size());
	}

	// ==================== GENERIC TYPE TESTS ====================

	@Test
	@DisplayName("List should work with Integer type")
	void testIntegerList() {
		XORLinkedList<Integer> intList = new XORLinkedList<>();
		intList.add(1);
		intList.add(2);
		intList.add(3);
		
		assertEquals(3, intList.size());
		assertEquals(Integer.valueOf(1), intList.current());
		assertEquals(Integer.valueOf(2), intList.next());
		assertEquals(Integer.valueOf(3), intList.next());
	}

	@Test
	@DisplayName("List should work with Double type")
	void testDoubleList() {
		XORLinkedList<Double> doubleList = new XORLinkedList<>();
		doubleList.add(1.5);
		doubleList.add(2.5);
		
		assertEquals(2, doubleList.size());
		assertEquals(1.5, doubleList.current(), 0.001);
		assertEquals(2.5, doubleList.next(), 0.001);
	}

	@Test
	@DisplayName("List should work with null values")
	void testNullValues() {
		list.add(null);
		list.add("notNull");
		list.add(null);
		
		assertEquals(3, list.size());
		assertNull(list.current());
		assertEquals("notNull", list.next());
		assertNull(list.next());
	}

	// ==================== COMPLEX NAVIGATION ====================

	@Test
	@DisplayName("Complex navigation pattern")
	void testComplexNavigation() {
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		list.add("5");
		
		// Start at 1
		assertEquals("1", list.current());
		
		// Go to end
		assertEquals("2", list.next());
		assertEquals("3", list.next());
		assertEquals("4", list.next());
		assertEquals("5", list.next());
		assertEquals("5", list.next()); // stay at end
		
		// Go back to middle
		assertEquals("4", list.previous());
		assertEquals("3", list.previous());
		
		// Go forward again
		assertEquals("4", list.next());
		
		// Go to beginning
		assertEquals("3", list.previous());
		assertEquals("2", list.previous());
		assertEquals("1", list.previous());
		assertEquals("1", list.previous()); // stay at beginning
	}
}



