package com.hyildizoglu.algorithms.heap;

import java.util.List;

/**
 * Heap sort utilities for sorting lists using a max-heap.
 * Provides O(n log n) time complexity sorting in descending order.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class HeapSort {

	/**
	 * Sorts a list in descending order using heap sort.
	 * Modifies the list in place.
	 * 
	 * @param <T>  The type of elements (must be Comparable)
	 * @param list The list to sort
	 */
	public static <T extends Comparable<T>> void sortDescending(List<T> list) {
		MaxHeap<T> heap = new MaxHeap<>();
		for (T item : list) {
			heap.add(item);
		}
		list.clear();
		while (!heap.isEmpty()) {
			list.add(heap.poll());
		}
	}
}
