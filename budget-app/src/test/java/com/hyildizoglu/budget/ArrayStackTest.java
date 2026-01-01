package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.stackqueue.ArrayStack;

@DisplayName("ArrayStack Algorithm Tests")
class ArrayStackTest {

	@Test
	@DisplayName("Push and Pop operations")
	void testPushAndPop() {
		ArrayStack<Integer> stack = new ArrayStack<>();
		assertTrue(stack.isEmpty());
		stack.push(1);
		stack.push(2);
		stack.push(3);
		assertFalse(stack.isEmpty());
		assertEquals(Integer.valueOf(3), stack.pop());
		assertEquals(Integer.valueOf(2), stack.pop());
		assertEquals(Integer.valueOf(1), stack.pop());
		assertTrue(stack.isEmpty());
	}

	@Test
	@DisplayName("Peek operation")
	void testPeek() {
		ArrayStack<String> stack = new ArrayStack<>();
		stack.push("first");
		stack.push("second");
		assertEquals("second", stack.peek());
		assertEquals("second", stack.peek()); // Should not remove
		assertEquals("second", stack.pop());
	}

	@Test
	@DisplayName("isEmpty check")
	void testIsEmpty() {
		ArrayStack<Integer> stack = new ArrayStack<>();
		assertTrue(stack.isEmpty());
		stack.push(1);
		assertFalse(stack.isEmpty());
		stack.pop();
		assertTrue(stack.isEmpty());
	}
}
