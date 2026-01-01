package com.hyildizoglu.algorithms.hash;

/**
 * Hash-based index for expense records.
 * Maps expense IDs to their positions in storage for fast lookup.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class ExpenseHashIndex {

	/** Internal hash table mapping expense IDs to positions. */
	private final OpenAddressingHashTable<Integer, Integer> idToPosition = new OpenAddressingHashTable<>();

	/**
	 * Associates an expense ID with a storage position.
	 * 
	 * @param expenseId The unique expense ID
	 * @param position  The position/index in storage
	 */
	public void put(int expenseId, int position) {
		idToPosition.put(expenseId, position);
	}

	/**
	 * Retrieves the storage position for an expense ID.
	 * 
	 * @param expenseId The expense ID to look up
	 * @return The storage position, or null if not found
	 */
	public Integer getPosition(int expenseId) {
		return idToPosition.get(expenseId);
	}

	/**
	 * Removes an expense ID from the index.
	 * 
	 * @param expenseId The expense ID to remove
	 * @return true if the ID was found and removed, false otherwise
	 */
	public boolean remove(int expenseId) {
		return idToPosition.remove(expenseId);
	}
}
