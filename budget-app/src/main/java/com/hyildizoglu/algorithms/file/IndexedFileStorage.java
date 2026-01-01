package com.hyildizoglu.algorithms.file;

import java.util.ArrayList;
import java.util.List;

import com.hyildizoglu.algorithms.hash.ExpenseHashIndex;

/**
 * Simple line-based indexed storage abstraction.
 * Uses a hash index for O(1) lookup by ID and an ArrayList for data storage.
 * Instead of actual file offsets, stores indices within the internal list.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class IndexedFileStorage {

	/** List storing the actual data lines. */
	private final List<String> lines = new ArrayList<>();
	
	/** Hash index mapping IDs to positions in the lines list. */
	private final ExpenseHashIndex index = new ExpenseHashIndex();

	/**
	 * Appends a line with the specified ID.
	 * 
	 * @param id   The unique identifier for the line
	 * @param line The data to store
	 */
	public void append(int id, String line) {
		lines.add(line);
		index.put(id, lines.size() - 1);
	}

	/**
	 * Retrieves a line by its ID.
	 * 
	 * @param id The ID to look up
	 * @return The stored line, or null if not found or deleted
	 */
	public String getById(int id) {
		Integer pos = index.getPosition(id);
		if (pos == null || pos < 0 || pos >= lines.size()) {
			return null;
		}
		return lines.get(pos);
	}

	/**
	 * Deletes a line by its ID.
	 * Performs a soft delete by setting the position to null.
	 * 
	 * @param id The ID to delete
	 * @return true if the line was found and deleted, false otherwise
	 */
	public boolean deleteById(int id) {
		Integer pos = index.getPosition(id);
		if (pos == null || pos < 0 || pos >= lines.size()) {
			return false;
		}
		lines.set(pos, null);
		index.remove(id);
		return true;
	}
}
