package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.heap.MaxHeap;

@DisplayName("MaxHeap Algorithm Tests")
class MaxHeapTest {

	@Test
	@DisplayName("Add and Poll operations")
	void testAddAndPoll() {
		MaxHeap<Integer> heap = new MaxHeap<>();
		heap.add(3);
		heap.add(1);
		heap.add(5);
		heap.add(2);
		assertEquals(Integer.valueOf(5), heap.poll());
		assertEquals(Integer.valueOf(3), heap.poll());
		assertEquals(Integer.valueOf(2), heap.poll());
		assertEquals(Integer.valueOf(1), heap.poll());
	}

	@Test
	@DisplayName("isEmpty check")
	void testIsEmpty() {
		MaxHeap<Integer> heap = new MaxHeap<>();
		assertTrue(heap.isEmpty());
		heap.add(1);
		assertFalse(heap.isEmpty());
		heap.poll();
		assertTrue(heap.isEmpty());
	}

	@Test
	@DisplayName("Peek operation")
	void testPeek() {
		MaxHeap<Integer> heap = new MaxHeap<>();
		heap.add(3);
		heap.add(1);
		heap.add(5);
		assertEquals(Integer.valueOf(5), heap.peek());
		assertEquals(Integer.valueOf(5), heap.peek()); // Should not remove
	}
}
