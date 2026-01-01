package com.hyildizoglu.algorithms.lists;

import java.util.ArrayList;
import java.util.List;

/**
 * A simplified XOR linked list implementation.
 * Instead of actual memory address XOR operations (which are not possible in Java),
 * this implementation uses an ArrayList with index-based navigation to simulate
 * bidirectional traversal similar to a real XOR linked list.
 * 
 * @param <T> The type of elements stored in this list
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class XORLinkedList<T> {

	/** Internal storage for elements. */
	private final List<T> data = new ArrayList<>();
	
	/** Current position in the list for navigation. */
	private int currentIndex = -1;

	/**
	 * Adds an element to the list.
	 * If this is the first element, sets current position to it.
	 * 
	 * @param value The element to add
	 */
	public void add(T value) {
		data.add(value);
		if (currentIndex == -1) {
			currentIndex = 0;
		}
	}

	/**
	 * Returns the element at the current position.
	 * 
	 * @return The current element, or null if list is empty or position is invalid
	 */
	public T current() {
		if (currentIndex >= 0 && currentIndex < data.size()) {
			return data.get(currentIndex);
		}
		return null;
	}

	/**
	 * Moves to the next element and returns it.
	 * If already at the end, stays at the last element.
	 * 
	 * @return The next element (or current if at end)
	 */
	public T next() {
		if (currentIndex < data.size() - 1) {
			currentIndex++;
		}
		return current();
	}

	/**
	 * Moves to the previous element and returns it.
	 * If already at the beginning, stays at the first element.
	 * 
	 * @return The previous element (or current if at beginning)
	 */
	public T previous() {
		if (currentIndex > 0) {
			currentIndex--;
		}
		return current();
	}

	/**
	 * Returns the number of elements in the list.
	 * 
	 * @return The number of elements
	 */
	public int size() {
		return data.size();
	}
}
