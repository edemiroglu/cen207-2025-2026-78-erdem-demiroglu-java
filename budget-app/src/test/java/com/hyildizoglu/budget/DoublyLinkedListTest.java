package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.lists.DoublyLinkedList;

@DisplayName("DoublyLinkedList Tests")
class DoublyLinkedListTest {

	private DoublyLinkedList<String> list;

	@BeforeEach
	void setUp() {
		list = new DoublyLinkedList<>();
	}

	// ==================== BASIC OPERATIONS ====================

	@Test
	@DisplayName("New list should be empty")
	void testNewListIsEmpty() {
		assertTrue(list.isEmpty());
		assertEquals(0, list.size());
	}

	@Test
	@DisplayName("Add single element should work")
	void testAddSingleElement() {
		list.addLast("first");
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
	}

	@Test
	@DisplayName("Add multiple elements should work")
	void testAddMultipleElements() {
		list.addLast("first");
		list.addLast("second");
		list.addLast("third");
		assertEquals(3, list.size());
		assertFalse(list.isEmpty());
	}

	// ==================== HEAD AND TAIL ====================

	@Test
	@DisplayName("Empty list head and tail should be null")
	void testEmptyListHeadTail() {
		assertNull(list.getHeadNode());
		assertNull(list.getTailNode());
	}

	@Test
	@DisplayName("Single element - head and tail should be same")
	void testSingleElementHeadTail() {
		list.addLast("only");
		assertNotNull(list.getHeadNode());
		assertNotNull(list.getTailNode());
		assertSame(list.getHeadNode(), list.getTailNode());
		assertEquals("only", list.getHeadNode().value);
	}

	@Test
	@DisplayName("Multiple elements - head and tail should be different")
	void testMultipleElementsHeadTail() {
		list.addLast("first");
		list.addLast("second");
		list.addLast("third");
		
		assertNotSame(list.getHeadNode(), list.getTailNode());
		assertEquals("first", list.getHeadNode().value);
		assertEquals("third", list.getTailNode().value);
	}

	// ==================== NODE STRUCTURE ====================

	@Test
	@DisplayName("Node should have correct value")
	void testNodeValue() {
		DoublyLinkedList.Node<String> node = new DoublyLinkedList.Node<>("test");
		assertEquals("test", node.value);
		assertNull(node.next);
		assertNull(node.prev);
	}

	@Test
	@DisplayName("Node links should be correct after adding elements")
	void testNodeLinks() {
		list.addLast("first");
		list.addLast("second");
		list.addLast("third");
		
		DoublyLinkedList.Node<String> head = list.getHeadNode();
		DoublyLinkedList.Node<String> tail = list.getTailNode();
		
		// Head checks
		assertNull(head.prev);
		assertNotNull(head.next);
		assertEquals("second", head.next.value);
		
		// Tail checks
		assertNull(tail.next);
		assertNotNull(tail.prev);
		assertEquals("second", tail.prev.value);
		
		// Middle node checks
		DoublyLinkedList.Node<String> middle = head.next;
		assertEquals("first", middle.prev.value);
		assertEquals("third", middle.next.value);
	}

	// ==================== ITERATOR ====================

	@Test
	@DisplayName("Iterator on empty list should not have next")
	void testIteratorEmptyList() {
		Iterator<String> iterator = list.iterator();
		assertFalse(iterator.hasNext());
	}

	@Test
	@DisplayName("Iterator should iterate all elements")
	void testIteratorAllElements() {
		list.addLast("first");
		list.addLast("second");
		list.addLast("third");
		
		Iterator<String> iterator = list.iterator();
		
		assertTrue(iterator.hasNext());
		assertEquals("first", iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals("second", iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals("third", iterator.next());
		
		assertFalse(iterator.hasNext());
	}

	@Test
	@DisplayName("Iterator next on exhausted iterator should throw exception")
	void testIteratorNextOnExhausted() {
		list.addLast("only");
		
		Iterator<String> iterator = list.iterator();
		iterator.next(); // consume the only element
		
		assertThrows(NoSuchElementException.class, () -> {
			iterator.next();
		});
	}

	@Test
	@DisplayName("Iterator next on empty list should throw exception")
	void testIteratorNextOnEmpty() {
		Iterator<String> iterator = list.iterator();
		
		assertThrows(NoSuchElementException.class, () -> {
			iterator.next();
		});
	}

	@Test
	@DisplayName("For-each loop should work")
	void testForEachLoop() {
		list.addLast("a");
		list.addLast("b");
		list.addLast("c");
		
		StringBuilder sb = new StringBuilder();
		for (String s : list) {
			sb.append(s);
		}
		
		assertEquals("abc", sb.toString());
	}

	// ==================== GENERIC TYPE TESTS ====================

	@Test
	@DisplayName("List should work with Integer type")
	void testIntegerList() {
		DoublyLinkedList<Integer> intList = new DoublyLinkedList<>();
		intList.addLast(1);
		intList.addLast(2);
		intList.addLast(3);
		
		assertEquals(3, intList.size());
		assertEquals(Integer.valueOf(1), intList.getHeadNode().value);
		assertEquals(Integer.valueOf(3), intList.getTailNode().value);
	}

	@Test
	@DisplayName("List should work with custom object type")
	void testCustomObjectList() {
		DoublyLinkedList<Object> objList = new DoublyLinkedList<>();
		Object obj1 = new Object();
		Object obj2 = new Object();
		
		objList.addLast(obj1);
		objList.addLast(obj2);
		
		assertEquals(2, objList.size());
		assertSame(obj1, objList.getHeadNode().value);
		assertSame(obj2, objList.getTailNode().value);
	}

	// ==================== SIZE TESTS ====================

	@Test
	@DisplayName("Size should increase as elements are added")
	void testSizeIncrease() {
		assertEquals(0, list.size());
		
		list.addLast("a");
		assertEquals(1, list.size());
		
		list.addLast("b");
		assertEquals(2, list.size());
		
		list.addLast("c");
		assertEquals(3, list.size());
	}

	@Test
	@DisplayName("isEmpty should return correct value")
	void testIsEmpty() {
		assertTrue(list.isEmpty());
		
		list.addLast("a");
		assertFalse(list.isEmpty());
	}
}



