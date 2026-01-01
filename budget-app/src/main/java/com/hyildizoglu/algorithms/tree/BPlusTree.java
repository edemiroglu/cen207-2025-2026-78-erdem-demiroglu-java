package com.hyildizoglu.algorithms.tree;// LCOV_EXCL_LINE 

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * B+ tree abstraction for efficient range queries.
 * Uses TreeMap internally to provide ordered key access and range query support.
 * 
 * @param <K> The type of keys (must be Comparable)
 * @param <V> The type of values
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class BPlusTree<K extends Comparable<? super K>, V> {

	/** Internal sorted map for storing key-value pairs. */
	private final NavigableMap<K, List<V>> map = new TreeMap<>();

	/**
	 * Inserts a key-value pair into the tree.
	 * Multiple values can be associated with the same key.
	 * 
	 * @param key   The key
	 * @param value The value to associate with the key
	 */
	public void put(K key, V value) {
		map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
	}

	/**
	 * Retrieves all values associated with a key.
	 * 
	 * @param key The key to look up
	 * @return List of values for the key (empty list if key not found)
	 */
	public List<V> get(K key) {
		return map.getOrDefault(key, Collections.<V>emptyList());
	}

	/**
	 * Performs a range query returning all values within the key range.
	 * 
	 * @param fromInclusive Start of range (inclusive)
	 * @param toInclusive   End of range (inclusive)
	 * @return List of all values in the range
	 */
	public List<V> rangeQuery(K fromInclusive, K toInclusive) {
		List<V> result = new ArrayList<>();
		for (Map.Entry<K, List<V>> e : map.subMap(fromInclusive, true, toInclusive, true).entrySet()) {
			result.addAll(e.getValue());
		}
		return result;
	}
}
