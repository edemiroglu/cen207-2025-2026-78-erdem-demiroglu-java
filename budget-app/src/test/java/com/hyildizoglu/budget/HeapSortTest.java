package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.heap.HeapSort;

@DisplayName("HeapSort Algorithm Tests")
class HeapSortTest {

	@Test
	@DisplayName("Sort integer list")
	void testSortDescending_Integer() {
		List<Integer> list = new ArrayList<>();
		list.add(3);
		list.add(1);
		list.add(4);
		list.add(2);

		HeapSort.sortDescending(list);

		assertEquals(4, list.size());
		assertEquals(Integer.valueOf(4), list.get(0));
		assertEquals(Integer.valueOf(3), list.get(1));
		assertEquals(Integer.valueOf(2), list.get(2));
		assertEquals(Integer.valueOf(1), list.get(3));
	}

	@Test
	@DisplayName("Sort string list")
	void testSortDescending_String() {
		List<String> list = new ArrayList<>();
		list.add("c");
		list.add("a");
		list.add("d");
		list.add("b");

		HeapSort.sortDescending(list);

		assertEquals(4, list.size());
		assertEquals("d", list.get(0));
		assertEquals("c", list.get(1));
		assertEquals("b", list.get(2));
		assertEquals("a", list.get(3));
	}

	@Test
	@DisplayName("Sort empty list")
	void testSortDescending_EmptyList() {
		List<Integer> list = new ArrayList<>();

		HeapSort.sortDescending(list);

		assertTrue(list.isEmpty());
	}

	@Test
	@DisplayName("Sort single element list")
	void testSortDescending_SingleElement() {
		List<Integer> list = new ArrayList<>();
		list.add(5);

		HeapSort.sortDescending(list);

		assertEquals(1, list.size());
		assertEquals(Integer.valueOf(5), list.get(0));
	}

	@Test
	@DisplayName("Already sorted list")
	void testSortDescending_AlreadySorted() {
		List<Integer> list = new ArrayList<>();
		list.add(5);
		list.add(4);
		list.add(3);
		list.add(2);
		list.add(1);

		HeapSort.sortDescending(list);

		assertEquals(5, list.size());
		assertEquals(Integer.valueOf(5), list.get(0));
		assertEquals(Integer.valueOf(1), list.get(4));
	}
}
