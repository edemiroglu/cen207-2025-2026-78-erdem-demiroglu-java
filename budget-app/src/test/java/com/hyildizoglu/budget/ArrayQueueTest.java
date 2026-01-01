package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.stackqueue.ArrayQueue;

@DisplayName("ArrayQueue Algorithm Tests")
class ArrayQueueTest {

	private ArrayQueue<Integer> queue;

	@BeforeEach
	void setUp() {
		queue = new ArrayQueue<>();
	}

	@Test
	@DisplayName("Enqueue and Dequeue operations")
	void testEnqueueAndDequeue() {
		assertTrue(queue.isEmpty());
		queue.enqueue(1);
		queue.enqueue(2);
		queue.enqueue(3);
		assertFalse(queue.isEmpty());
		assertEquals(Integer.valueOf(1), queue.dequeue());
		assertEquals(Integer.valueOf(2), queue.dequeue());
		assertEquals(Integer.valueOf(3), queue.dequeue());
		assertTrue(queue.isEmpty());
	}

	@Test
	@DisplayName("isEmpty check")
	void testIsEmpty() {
		assertTrue(queue.isEmpty());
		queue.enqueue(1);
		assertFalse(queue.isEmpty());
		queue.dequeue();
		assertTrue(queue.isEmpty());
	}

	@Test
	@DisplayName("Size check")
	void testSize() {
		assertEquals(0, queue.size());
		queue.enqueue(1);
		queue.enqueue(2);
		assertEquals(2, queue.size());
		queue.dequeue();
		assertEquals(1, queue.size());
	}

	@Test
	@DisplayName("Dequeue from empty queue")
	void testDequeue_EmptyQueue() {
		assertNull(queue.dequeue());
	}

	@Test
	@DisplayName("Circular queue behavior")
	void testCircularBehavior() {
		// Test circular queue behavior
		for (int i = 0; i < 20; i++) {
			queue.enqueue(i);
		}
		for (int i = 0; i < 10; i++) {
			queue.dequeue();
		}
		for (int i = 20; i < 30; i++) {
			queue.enqueue(i);
		}
		assertEquals(20, queue.size());
	}
}
