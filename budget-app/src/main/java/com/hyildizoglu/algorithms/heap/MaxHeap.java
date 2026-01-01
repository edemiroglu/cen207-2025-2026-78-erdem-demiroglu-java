package com.hyildizoglu.algorithms.heap;

import java.util.ArrayList;
import java.util.List;

/**
 * Max-heap implementation using an ArrayList.
 * Provides O(log n) insertion and removal of the maximum element.
 * 
 * @param <T> The type of elements (must be Comparable)
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class MaxHeap<T extends Comparable<T>> {

	/** Internal storage for heap elements. */
	private final List<T> data = new ArrayList<>();

	/**
	 * Adds an element to the heap.
	 * 
	 * @param value The element to add
	 */
	public void add(T value) {
		data.add(value);
		heapifyUp(data.size() - 1);
	}

	/**
	 * Returns the maximum element without removing it.
	 * 
	 * @return The maximum element, or null if heap is empty
	 */
	public T peek() {
		return data.isEmpty() ? null : data.get(0);
	}

	/**
	 * Removes and returns the maximum element.
	 * 
	 * @return The maximum element, or null if heap is empty
	 */
	public T poll() {
		if (data.isEmpty()) {
			return null;
		}
		T root = data.get(0);
		T last = data.remove(data.size() - 1);
		if (!data.isEmpty()) {
			data.set(0, last);
			heapifyDown(0);
		}
		return root;
	}

	/**
	 * Returns the number of elements in the heap.
	 * 
	 * @return The number of elements
	 */
	public int size() {
		return data.size();
	}

	/**
	 * Checks if the heap is empty.
	 * 
	 * @return true if the heap contains no elements
	 */
	public boolean isEmpty() {
		return data.isEmpty();
	}

	/**
	 * Restores heap property by moving an element up.
	 * 
	 * @param index The index of the element to move up
	 */
	private void heapifyUp(int index) {
		while (index > 0) {
			int parent = (index - 1) / 2;
			if (data.get(index).compareTo(data.get(parent)) > 0) {
				swap(index, parent);
				index = parent;
			} else {
				break;
			}
		}
	}

	/**
	 * Restores heap property by moving an element down.
	 * 
	 * @param index The index of the element to move down
	 */
	private void heapifyDown(int index) {
		int size = data.size();
		while (true) {
			int left = 2 * index + 1;
			int right = 2 * index + 2;
			int largest = index;
			if (left < size && data.get(left).compareTo(data.get(largest)) > 0) {
				largest = left;
			}
			if (right < size && data.get(right).compareTo(data.get(largest)) > 0) {
				largest = right;
			}
			if (largest != index) {
				swap(index, largest);
				index = largest;
			} else {
				break;
			}
		}
	}

	/**
	 * Swaps two elements in the data list.
	 * 
	 * @param i First index
	 * @param j Second index
	 */
	private void swap(int i, int j) {
		T tmp = data.get(i);
		data.set(i, data.get(j));
		data.set(j, tmp);
	}
}
