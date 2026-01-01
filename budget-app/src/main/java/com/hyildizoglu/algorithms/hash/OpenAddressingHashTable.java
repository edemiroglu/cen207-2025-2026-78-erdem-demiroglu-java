package com.hyildizoglu.algorithms.hash;

/**
 * Open addressing hash table implementation using linear probing.
 * Provides O(1) average time complexity for put, get, and remove operations.
 * 
 * @param <K> The type of keys
 * @param <V> The type of values
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class OpenAddressingHashTable<K, V> {

	/**
	 * Internal entry class to store key-value pairs.
	 * 
	 * @param <K> The type of key
	 * @param <V> The type of value
	 */
	private static class Entry<K, V> {
		/** The key of this entry. */
		K key;
		
		/** The value of this entry. */
		V value;
		
		/** Flag indicating if this entry has been deleted. */
		boolean deleted;

		/**
		 * Creates a new Entry with the specified key and value.
		 * 
		 * @param key   The key
		 * @param value The value
		 */
		Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}
	}

	/** Internal array to store entries. */
	private Entry<K, V>[] table;
	
	/** Current number of elements in the table. */
	private int size;

	/**
	 * Creates a new hash table with the specified initial capacity.
	 * 
	 * @param capacity Initial capacity of the table
	 */
	@SuppressWarnings("unchecked")
	public OpenAddressingHashTable(int capacity) {
		table = (Entry<K, V>[]) new Entry[capacity];
	}

	/**
	 * Creates a new hash table with default capacity of 128.
	 */
	public OpenAddressingHashTable() {
		this(128);
	}

	/**
	 * Computes the index for a given key using its hash code.
	 * 
	 * @param key The key to compute index for
	 * @return The computed index in the table
	 */
	private int index(Object key) {
		return (key.hashCode() & 0x7fffffff) % table.length;
	}

	/**
	 * Inserts or updates a key-value pair in the table.
	 * If the key already exists, its value is updated.
	 * 
	 * @param key   The key to insert
	 * @param value The value to associate with the key
	 */
	public void put(K key, V value) {
		if (size * 2 >= table.length) {
			resize();
		}
		int idx = index(key);
		while (table[idx] != null && !table[idx].deleted && !table[idx].key.equals(key)) {
			idx = (idx + 1) % table.length;
		}
		if (table[idx] == null || table[idx].deleted) {
			table[idx] = new Entry<>(key, value);
			size++;
		} else {
			table[idx].value = value;
		}
	}

	/**
	 * Retrieves the value associated with the specified key.
	 * 
	 * @param key The key to look up
	 * @return The associated value, or null if key not found
	 */
	public V get(K key) {
		int idx = index(key);
		int start = idx;
		while (table[idx] != null) {
			if (!table[idx].deleted && table[idx].key.equals(key)) {
				return table[idx].value;
			}
			idx = (idx + 1) % table.length;
			if (idx == start) {
				break;
			}
		}
		return null;
	}

	/**
	 * Removes the entry with the specified key.
	 * Uses lazy deletion by marking the entry as deleted.
	 * 
	 * @param key The key to remove
	 * @return true if the key was found and removed, false otherwise
	 */
	public boolean remove(K key) {
		int idx = index(key);
		int start = idx;
		while (table[idx] != null) {
			if (!table[idx].deleted && table[idx].key.equals(key)) {
				table[idx].deleted = true;
				size--;
				return true;
			}
			idx = (idx + 1) % table.length;
			if (idx == start) {
				break;
			}
		}
		return false;
	}

	/**
	 * Doubles the capacity of the table and rehashes all entries.
	 * Called automatically when load factor exceeds 0.5.
	 */
	@SuppressWarnings("unchecked")
	private void resize() {
		Entry<K, V>[] old = table;
		table = (Entry<K, V>[]) new Entry[old.length * 2];
		size = 0;
		for (Entry<K, V> e : old) {
			if (e != null && !e.deleted) {
				put(e.key, e.value);
			}
		}
	}
}
