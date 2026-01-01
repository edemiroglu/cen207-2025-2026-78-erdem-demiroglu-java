package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.tree.BPlusTree;

@DisplayName("BPlusTree Algorithm Tests")
class BPlusTreeTest {

	@Test
	@DisplayName("Put and Get operations")
	void testPutAndGet() {
		BPlusTree<String, Integer> tree = new BPlusTree<>();
		tree.put("key1", 1);
		tree.put("key2", 2);
		tree.put("key3", 3);
		
		// get() returns List, checking first element
		List<Integer> result1 = tree.get("key1");
		List<Integer> result2 = tree.get("key2");
		List<Integer> result3 = tree.get("key3");
		
		assertEquals(1, result1.size());
		assertEquals(Integer.valueOf(1), result1.get(0));
		assertEquals(1, result2.size());
		assertEquals(Integer.valueOf(2), result2.get(0));
		assertEquals(1, result3.size());
		assertEquals(Integer.valueOf(3), result3.get(0));
	}

	@Test
	@DisplayName("Date range query")
	void testRangeQuery() {
		BPlusTree<LocalDate, String> tree = new BPlusTree<>();
		LocalDate d1 = LocalDate.of(2025, 1, 1);
		LocalDate d2 = LocalDate.of(2025, 1, 15);
		LocalDate d3 = LocalDate.of(2025, 1, 30);
		LocalDate d4 = LocalDate.of(2025, 2, 1);
		tree.put(d1, "Jan 1");
		tree.put(d2, "Jan 15");
		tree.put(d3, "Jan 30");
		tree.put(d4, "Feb 1");
		List<String> result = tree.rangeQuery(d1, d3);
		assertEquals(3, result.size());
		assertTrue(result.contains("Jan 1"));
		assertTrue(result.contains("Jan 15"));
		assertTrue(result.contains("Jan 30"));
	}
	
	@Test
	@DisplayName("Empty list for non-existent key")
	void testGetNonExistent() {
		BPlusTree<String, Integer> tree = new BPlusTree<>();
		List<Integer> result = tree.get("nonexistent");
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
	
	@Test
	@DisplayName("Multiple values per key")
	void testMultipleValuesPerKey() {
		BPlusTree<String, Integer> tree = new BPlusTree<>();
		tree.put("key1", 1);
		tree.put("key1", 2);
		tree.put("key1", 3);
		
		List<Integer> result = tree.get("key1");
		assertEquals(3, result.size());
		assertTrue(result.contains(1));
		assertTrue(result.contains(2));
		assertTrue(result.contains(3));
	}
}
