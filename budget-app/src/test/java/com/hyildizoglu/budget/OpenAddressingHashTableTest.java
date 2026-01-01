package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.hash.OpenAddressingHashTable;

@DisplayName("OpenAddressingHashTable Tests")
class OpenAddressingHashTableTest {

	private OpenAddressingHashTable<String, Integer> table;

	@BeforeEach
	void setUp() {
		table = new OpenAddressingHashTable<>();
	}

	// ==================== CONSTRUCTOR TESTS ====================

	@Test
	@DisplayName("Default constructor should create table")
	void testDefaultConstructor() {
		OpenAddressingHashTable<String, String> t = new OpenAddressingHashTable<>();
		assertNotNull(t);
	}

	@Test
	@DisplayName("Constructor with capacity should create table")
	void testConstructorWithCapacity() {
		OpenAddressingHashTable<String, String> t = new OpenAddressingHashTable<>(16);
		assertNotNull(t);
	}

	@Test
	@DisplayName("Constructor with small capacity should work")
	void testConstructorWithSmallCapacity() {
		OpenAddressingHashTable<Integer, String> t = new OpenAddressingHashTable<>(4);
		assertNotNull(t);
		t.put(1, "one");
		assertEquals("one", t.get(1));
	}

	// ==================== PUT TESTS ====================

	@Test
	@DisplayName("Put single element should work")
	void testPutSingleElement() {
		table.put("key1", 100);
		assertEquals(100, table.get("key1"));
	}

	@Test
	@DisplayName("Put multiple elements should work")
	void testPutMultipleElements() {
		table.put("a", 1);
		table.put("b", 2);
		table.put("c", 3);
		assertEquals(1, table.get("a"));
		assertEquals(2, table.get("b"));
		assertEquals(3, table.get("c"));
	}

	@Test
	@DisplayName("Put with same key should update value")
	void testPutUpdateExistingKey() {
		table.put("key", 100);
		assertEquals(100, table.get("key"));
		table.put("key", 200);
		assertEquals(200, table.get("key"));
	}

	@Test
	@DisplayName("Put with null value should work")
	void testPutNullValue() {
		table.put("key", null);
		assertNull(table.get("key"));
	}

	// ==================== GET TESTS ====================

	@Test
	@DisplayName("Get existing element should return value")
	void testGetExisting() {
		table.put("test", 42);
		assertEquals(42, table.get("test"));
	}

	@Test
	@DisplayName("Get non-existing element should return null")
	void testGetNonExisting() {
		assertNull(table.get("nonexistent"));
	}

	@Test
	@DisplayName("Get from empty table should return null")
	void testGetFromEmptyTable() {
		assertNull(table.get("anything"));
	}

	// ==================== REMOVE TESTS ====================

	@Test
	@DisplayName("Remove existing element should return true")
	void testRemoveExisting() {
		table.put("key", 100);
		assertTrue(table.remove("key"));
		assertNull(table.get("key"));
	}

	@Test
	@DisplayName("Remove non-existing element should return false")
	void testRemoveNonExisting() {
		assertFalse(table.remove("nonexistent"));
	}

	@Test
	@DisplayName("Remove from empty table should return false")
	void testRemoveFromEmptyTable() {
		assertFalse(table.remove("key"));
	}

	@Test
	@DisplayName("Remove same key twice should return false on second")
	void testRemoveTwice() {
		table.put("key", 100);
		assertTrue(table.remove("key"));
		assertFalse(table.remove("key"));
	}

	// ==================== COLLISION TESTS (Linear Probing) ====================

	@Test
	@DisplayName("Put with collision should use linear probing")
	void testPutWithCollision() {
		// Use small capacity to force collisions
		OpenAddressingHashTable<Integer, String> smallTable = new OpenAddressingHashTable<>(4);
		// These might cause collisions depending on hash
		smallTable.put(0, "zero");
		smallTable.put(4, "four"); // Same index as 0 in table of size 4
		smallTable.put(8, "eight"); // Same index
		
		assertEquals("zero", smallTable.get(0));
		assertEquals("four", smallTable.get(4));
		assertEquals("eight", smallTable.get(8));
	}

	@Test
	@DisplayName("Get with collision should find correct element")
	void testGetWithCollision() {
		OpenAddressingHashTable<Integer, String> smallTable = new OpenAddressingHashTable<>(4);
		smallTable.put(0, "zero");
		smallTable.put(4, "four");
		
		assertEquals("zero", smallTable.get(0));
		assertEquals("four", smallTable.get(4));
	}

	@Test
	@DisplayName("Remove with collision should work correctly")
	void testRemoveWithCollision() {
		OpenAddressingHashTable<Integer, String> smallTable = new OpenAddressingHashTable<>(4);
		smallTable.put(0, "zero");
		smallTable.put(4, "four");
		
		assertTrue(smallTable.remove(0));
		assertNull(smallTable.get(0));
		assertEquals("four", smallTable.get(4)); // Should still be accessible
	}

	// ==================== RESIZE TESTS ====================

	@Test
	@DisplayName("Resize should be triggered when load factor exceeds threshold")
	void testResizeTriggered() {
		// Default capacity is 128, resize at size * 2 >= length
		// So resize happens when size >= 64
		OpenAddressingHashTable<Integer, String> t = new OpenAddressingHashTable<>(8);
		
		// Add enough elements to trigger resize (need 4+ for capacity 8)
		for (int i = 0; i < 10; i++) {
			t.put(i, "value" + i);
		}
		
		// All elements should still be accessible after resize
		for (int i = 0; i < 10; i++) {
			assertEquals("value" + i, t.get(i));
		}
	}

	@Test
	@DisplayName("Resize should preserve all elements")
	void testResizePreservesElements() {
		OpenAddressingHashTable<String, Integer> t = new OpenAddressingHashTable<>(4);
		
		// Add elements to trigger multiple resizes
		for (int i = 0; i < 20; i++) {
			t.put("key" + i, i);
		}
		
		// Verify all elements are preserved
		for (int i = 0; i < 20; i++) {
			assertEquals(i, t.get("key" + i));
		}
	}

	@Test
	@DisplayName("Resize with deleted elements should not include deleted")
	void testResizeWithDeletedElements() {
		OpenAddressingHashTable<Integer, String> t = new OpenAddressingHashTable<>(4);
		
		// Add elements
		t.put(1, "one");
		t.put(2, "two");
		
		// Delete one
		t.remove(1);
		
		// Add more to trigger resize
		for (int i = 3; i < 10; i++) {
			t.put(i, "value" + i);
		}
		
		// Deleted element should not be found
		assertNull(t.get(1));
		assertEquals("two", t.get(2));
	}

	// ==================== PUT AFTER REMOVE (Deleted Slot Reuse) ====================

	@Test
	@DisplayName("Put should reuse deleted slot")
	void testPutReusesDeletedSlot() {
		OpenAddressingHashTable<Integer, String> t = new OpenAddressingHashTable<>(8);
		
		t.put(0, "zero");
		t.put(8, "eight"); // Collides with 0
		
		// Remove first element
		t.remove(0);
		
		// Add new element that hashes to same slot
		t.put(16, "sixteen");
		
		// All should work correctly
		assertNull(t.get(0));
		assertEquals("eight", t.get(8));
		assertEquals("sixteen", t.get(16));
	}

	@Test
	@DisplayName("Put same key after remove should work")
	void testPutSameKeyAfterRemove() {
		table.put("key", 100);
		table.remove("key");
		table.put("key", 200);
		assertEquals(200, table.get("key"));
	}

	// ==================== FULL TABLE TRAVERSAL TESTS ====================

	@Test
	@DisplayName("Get non-existing should traverse full table when full")
	void testGetFullTraversal() {
		OpenAddressingHashTable<Integer, String> t = new OpenAddressingHashTable<>(4);
		
		// Fill table partially with colliding keys
		t.put(0, "a");
		t.put(4, "b"); // Collides with 0
		
		// Search for non-existing key that would hash to same bucket
		assertNull(t.get(12)); // Would also collide
	}

	@Test
	@DisplayName("Remove non-existing should traverse correctly")
	void testRemoveFullTraversal() {
		OpenAddressingHashTable<Integer, String> t = new OpenAddressingHashTable<>(4);
		
		// Fill with colliding keys
		t.put(0, "a");
		t.put(4, "b");
		
		// Try to remove non-existing colliding key
		assertFalse(t.remove(12));
	}

	// ==================== EDGE CASES ====================

	@Test
	@DisplayName("Integer keys should work")
	void testIntegerKeys() {
		OpenAddressingHashTable<Integer, String> t = new OpenAddressingHashTable<>();
		t.put(1, "one");
		t.put(2, "two");
		t.put(-1, "negative one");
		
		assertEquals("one", t.get(1));
		assertEquals("two", t.get(2));
		assertEquals("negative one", t.get(-1));
	}

	@Test
	@DisplayName("Large number of elements should work")
	void testLargeNumberOfElements() {
		OpenAddressingHashTable<Integer, Integer> t = new OpenAddressingHashTable<>(16);
		
		// Add many elements to trigger multiple resizes
		for (int i = 0; i < 1000; i++) {
			t.put(i, i * 2);
		}
		
		// Verify all elements
		for (int i = 0; i < 1000; i++) {
			assertEquals(i * 2, t.get(i));
		}
	}

	@Test
	@DisplayName("Mixed operations should work correctly")
	void testMixedOperations() {
		table.put("a", 1);
		table.put("b", 2);
		table.put("c", 3);
		
		assertEquals(1, table.get("a"));
		assertTrue(table.remove("b"));
		assertNull(table.get("b"));
		
		table.put("b", 20);
		assertEquals(20, table.get("b"));
		
		table.put("a", 10);
		assertEquals(10, table.get("a"));
	}

	@Test
	@DisplayName("Keys with same hashcode should be handled")
	void testKeysWithSameHashcode() {
		// In Java, "Aa" and "BB" have the same hashcode
		OpenAddressingHashTable<String, Integer> t = new OpenAddressingHashTable<>(8);
		t.put("Aa", 1);
		t.put("BB", 2);
		
		assertEquals(1, t.get("Aa"));
		assertEquals(2, t.get("BB"));
		
		assertTrue(t.remove("Aa"));
		assertNull(t.get("Aa"));
		assertEquals(2, t.get("BB"));
	}

	// ==================== WRAP-AROUND TESTS ====================

	@Test
	@DisplayName("Linear probing should wrap around table")
	void testLinearProbingWrapAround() {
		// Small table to force wrap-around
		OpenAddressingHashTable<Integer, String> t = new OpenAddressingHashTable<>(4);
		
		// Fill most of the table
		t.put(3, "three"); // Likely at end of table
		t.put(7, "seven"); // Wraps around
		t.put(11, "eleven"); // Wraps around more
		
		assertEquals("three", t.get(3));
		assertEquals("seven", t.get(7));
		assertEquals("eleven", t.get(11));
	}

	@Test
	@DisplayName("Get should handle wrap-around correctly")
	void testGetWrapAround() {
		OpenAddressingHashTable<Integer, String> t = new OpenAddressingHashTable<>(4);
		
		t.put(3, "three");
		t.put(7, "seven");
		
		// Search for key that doesn't exist but would wrap
		assertNull(t.get(15));
	}

	@Test
	@DisplayName("Remove should handle wrap-around correctly")
	void testRemoveWrapAround() {
		OpenAddressingHashTable<Integer, String> t = new OpenAddressingHashTable<>(4);
		
		t.put(3, "three");
		t.put(7, "seven");
		
		assertTrue(t.remove(7));
		assertNull(t.get(7));
		assertEquals("three", t.get(3));
	}
}



