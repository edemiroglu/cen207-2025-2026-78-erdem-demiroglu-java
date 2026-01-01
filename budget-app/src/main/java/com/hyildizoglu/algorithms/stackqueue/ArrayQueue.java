package com.hyildizoglu.algorithms.stackqueue;

/**
 * Array-based circular queue (FIFO) implementation.
 * Provides O(1) amortized time complexity for enqueue and dequeue operations.
 * 
 * @param <T> The type of elements stored in this queue
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class ArrayQueue<T> {

	/** Internal array to store elements. */
	private Object[] elements;
	
	/** Index of the front element (head of queue). */
	private int head;
	
	/** Index where next element will be inserted (tail of queue). */
	private int tail;
	
	/** Current number of elements in the queue. */
	private int size;

	/**
	 * Creates a new ArrayQueue with default capacity of 16.
	 */
	public ArrayQueue() {
		this(16);
	}

	/**
	 * Creates a new ArrayQueue with specified initial capacity.
	 * 
	 * @param capacity Initial capacity (minimum 1)
	 */
	public ArrayQueue(int capacity) {
		elements = new Object[Math.max(1, capacity)];
	}

	/**
	 * Adds an element to the end of the queue.
	 * 
	 * @param value The element to add
	 */
	public void enqueue(T value) {
		ensureCapacity(size + 1);
		elements[tail] = value;
		tail = (tail + 1) % elements.length;
		size++;
	}

	/**
	 * Removes and returns the element at the front of the queue.
	 * 
	 * @return The element at the front, or null if queue is empty
	 */
	@SuppressWarnings("unchecked")
	public T dequeue() {
		if (size == 0) {
			return null;
		}
		T value = (T) elements[head];
		elements[head] = null;
		head = (head + 1) % elements.length;
		size--;
		return value;
	}

	/**
	 * Checks if the queue is empty.
	 * 
	 * @return true if the queue contains no elements, false otherwise
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns the number of elements in the queue.
	 * 
	 * @return The number of elements
	 */
	public int size() {
		return size;
	}

	/**
	 * Ensures the internal array has sufficient capacity.
	 * Doubles the capacity and reorganizes elements if needed.
	 * 
	 * @param capacity The required minimum capacity
	 */
	private void ensureCapacity(int capacity) {
		if (capacity <= elements.length) {
			return;
		}
		int newCap = elements.length * 2;
		Object[] newArr = new Object[newCap];
		for (int i = 0; i < size; i++) {
			newArr[i] = elements[(head + i) % elements.length];
		}
		elements = newArr;
		head = 0;
		tail = size;
	}
}
