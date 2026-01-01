package com.hyildizoglu.algorithms.stackqueue;

import java.util.EmptyStackException;

/**
 * Array-based stack (LIFO) implementation.
 * Provides O(1) amortized time complexity for push and pop operations.
 * 
 * @param <T> The type of elements stored in this stack
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class ArrayStack<T> {

	/** Internal array to store elements. */
	private Object[] elements;
	
	/** Current number of elements in the stack. */
	private int size;

	/**
	 * Creates a new ArrayStack with default capacity of 16.
	 */
	public ArrayStack() {
		this(16);
	}

	/**
	 * Creates a new ArrayStack with specified initial capacity.
	 * 
	 * @param capacity Initial capacity (minimum 1)
	 */
	public ArrayStack(int capacity) {
		elements = new Object[Math.max(1, capacity)];
	}

	/**
	 * Pushes an element onto the top of the stack.
	 * 
	 * @param value The element to push
	 */
	public void push(T value) {
		ensureCapacity(size + 1);
		elements[size++] = value;
	}

	/**
	 * Removes and returns the element at the top of the stack.
	 * 
	 * @return The element at the top of the stack
	 * @throws EmptyStackException if the stack is empty
	 */
	@SuppressWarnings("unchecked")
	public T pop() {
		if (size == 0) {
			throw new EmptyStackException();
		}
		T value = (T) elements[--size];
		elements[size] = null;
		return value;
	}

	/**
	 * Returns the element at the top of the stack without removing it.
	 * 
	 * @return The element at the top of the stack
	 * @throws EmptyStackException if the stack is empty
	 */
	@SuppressWarnings("unchecked")
	public T peek() {
		if (size == 0) {
			throw new EmptyStackException();
		}
		return (T) elements[size - 1];
	}

	/**
	 * Checks if the stack is empty.
	 * 
	 * @return true if the stack contains no elements, false otherwise
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns the number of elements in the stack.
	 * 
	 * @return The number of elements
	 */
	public int size() {
		return size;
	}

	/**
	 * Ensures the internal array has sufficient capacity.
	 * Doubles the capacity if needed.
	 * 
	 * @param capacity The required minimum capacity
	 */
	private void ensureCapacity(int capacity) {
		if (capacity <= elements.length) {
			return;
		}
		int newCap = elements.length * 2;
		Object[] newArr = new Object[newCap];
		System.arraycopy(elements, 0, newArr, 0, size);
		elements = newArr;
	}
}
