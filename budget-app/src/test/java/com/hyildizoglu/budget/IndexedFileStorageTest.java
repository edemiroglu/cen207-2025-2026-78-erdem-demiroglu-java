package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.file.IndexedFileStorage;

@DisplayName("IndexedFileStorage Tests")
class IndexedFileStorageTest {

	private IndexedFileStorage storage;

	@BeforeEach
	void setUp() {
		storage = new IndexedFileStorage();
	}

	// ==================== APPEND TESTS ====================

	@Test
	@DisplayName("Append single item should work")
	void testAppendSingle() {
		storage.append(1, "test line");
		
		String result = storage.getById(1);
		assertEquals("test line", result);
	}

	@Test
	@DisplayName("Append multiple items should work")
	void testAppendMultiple() {
		storage.append(1, "line 1");
		storage.append(2, "line 2");
		storage.append(3, "line 3");
		
		assertEquals("line 1", storage.getById(1));
		assertEquals("line 2", storage.getById(2));
		assertEquals("line 3", storage.getById(3));
	}

	@Test
	@DisplayName("Append with non-sequential IDs should work")
	void testAppendNonSequentialIds() {
		storage.append(100, "hundred");
		storage.append(50, "fifty");
		storage.append(200, "two hundred");
		
		assertEquals("hundred", storage.getById(100));
		assertEquals("fifty", storage.getById(50));
		assertEquals("two hundred", storage.getById(200));
	}

	@Test
	@DisplayName("Append with same ID should update index")
	void testAppendSameId() {
		storage.append(1, "first");
		storage.append(1, "second");
		
		// The second append should update the index to point to the new position
		assertEquals("second", storage.getById(1));
	}

	// ==================== GET BY ID TESTS ====================

	@Test
	@DisplayName("Get by ID should return correct data")
	void testGetById() {
		storage.append(10, "data for id 10");
		
		String result = storage.getById(10);
		assertEquals("data for id 10", result);
	}

	@Test
	@DisplayName("Get by ID should return null for non-existent ID")
	void testGetByIdNotFound() {
		String result = storage.getById(999);
		assertNull(result);
	}

	@Test
	@DisplayName("Get by ID should return null for deleted ID")
	void testGetByIdAfterDelete() {
		storage.append(1, "to be deleted");
		storage.deleteById(1);
		
		String result = storage.getById(1);
		assertNull(result);
	}

	@Test
	@DisplayName("Get by ID with negative ID should return null")
	void testGetByIdNegative() {
		String result = storage.getById(-1);
		assertNull(result);
	}

	// ==================== DELETE BY ID TESTS ====================

	@Test
	@DisplayName("Delete by ID should return true on success")
	void testDeleteByIdSuccess() {
		storage.append(1, "to delete");
		
		boolean result = storage.deleteById(1);
		
		assertTrue(result);
	}

	@Test
	@DisplayName("Delete by ID should return false for non-existent ID")
	void testDeleteByIdNotFound() {
		boolean result = storage.deleteById(999);
		
		assertFalse(result);
	}

	@Test
	@DisplayName("Delete should make data inaccessible")
	void testDeleteMakesDataInaccessible() {
		storage.append(1, "data");
		storage.deleteById(1);
		
		assertNull(storage.getById(1));
	}

	@Test
	@DisplayName("Delete should not affect other items")
	void testDeleteDoesNotAffectOthers() {
		storage.append(1, "first");
		storage.append(2, "second");
		storage.append(3, "third");
		
		storage.deleteById(2);
		
		assertEquals("first", storage.getById(1));
		assertNull(storage.getById(2));
		assertEquals("third", storage.getById(3));
	}

	@Test
	@DisplayName("Delete same ID twice should return false second time")
	void testDeleteTwice() {
		storage.append(1, "data");
		
		assertTrue(storage.deleteById(1));
		assertFalse(storage.deleteById(1));
	}

	// ==================== EDGE CASES ====================

	@Test
	@DisplayName("Empty storage get should return null")
	void testEmptyStorageGet() {
		assertNull(storage.getById(1));
	}

	@Test
	@DisplayName("Empty storage delete should return false")
	void testEmptyStorageDelete() {
		assertFalse(storage.deleteById(1));
	}

	@Test
	@DisplayName("Append empty string should work")
	void testAppendEmptyString() {
		storage.append(1, "");
		assertEquals("", storage.getById(1));
	}

	@Test
	@DisplayName("Append null should work")
	void testAppendNull() {
		storage.append(1, null);
		assertNull(storage.getById(1));
	}

	@Test
	@DisplayName("Large ID values should work")
	void testLargeIdValues() {
		storage.append(Integer.MAX_VALUE, "max int");
		assertEquals("max int", storage.getById(Integer.MAX_VALUE));
	}

	@Test
	@DisplayName("Zero ID should work")
	void testZeroId() {
		storage.append(0, "zero id");
		assertEquals("zero id", storage.getById(0));
	}

	// ==================== COMPLEX SCENARIOS ====================

	@Test
	@DisplayName("Multiple operations workflow")
	void testMultipleOperations() {
		// Add items
		storage.append(1, "a");
		storage.append(2, "b");
		storage.append(3, "c");
		
		// Verify all present
		assertEquals("a", storage.getById(1));
		assertEquals("b", storage.getById(2));
		assertEquals("c", storage.getById(3));
		
		// Delete middle
		assertTrue(storage.deleteById(2));
		
		// Verify state
		assertEquals("a", storage.getById(1));
		assertNull(storage.getById(2));
		assertEquals("c", storage.getById(3));
		
		// Add new item
		storage.append(4, "d");
		assertEquals("d", storage.getById(4));
		
		// Delete first
		assertTrue(storage.deleteById(1));
		assertNull(storage.getById(1));
		
		// Remaining items still accessible
		assertEquals("c", storage.getById(3));
		assertEquals("d", storage.getById(4));
	}

	@Test
	@DisplayName("Re-add after delete should work")
	void testReAddAfterDelete() {
		storage.append(1, "original");
		storage.deleteById(1);
		storage.append(1, "new value");
		
		assertEquals("new value", storage.getById(1));
	}
}
