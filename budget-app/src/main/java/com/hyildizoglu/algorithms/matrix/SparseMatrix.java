package com.hyildizoglu.algorithms.matrix;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Sparse matrix implementation for efficient storage of matrices with many zero values.
 * Uses nested HashMaps to store only non-zero values.
 * Row and column indices are integers, values are BigDecimal.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class SparseMatrix {

	/** Internal storage: row -> (column -> value). */
	private final Map<Integer, Map<Integer, BigDecimal>> data = new HashMap<>();

	/**
	 * Adds a value to the specified cell.
	 * If the result becomes zero, the cell is removed.
	 * 
	 * @param row   Row index
	 * @param col   Column index
	 * @param value Value to add (null values are ignored)
	 */
	public void addTo(int row, int col, BigDecimal value) {
		if (value == null) {
			return;
		}
		Map<Integer, BigDecimal> rowMap = data.computeIfAbsent(row, k -> new HashMap<>());
		BigDecimal current = rowMap.getOrDefault(col, BigDecimal.ZERO);
		BigDecimal updated = current.add(value);
		if (updated.compareTo(BigDecimal.ZERO) == 0) {
			rowMap.remove(col);
		} else {
			rowMap.put(col, updated);
		}
	}

	/**
	 * Gets the value at the specified cell.
	 * 
	 * @param row Row index
	 * @param col Column index
	 * @return The value at the cell, or BigDecimal.ZERO if not set
	 */
	public BigDecimal get(int row, int col) {
		Map<Integer, BigDecimal> rowMap = data.get(row);
		if (rowMap == null) {
			return BigDecimal.ZERO;
		}
		return rowMap.getOrDefault(col, BigDecimal.ZERO);
	}

	/**
	 * Computes the sum of all values in a row.
	 * 
	 * @param row Row index
	 * @return Sum of all values in the row
	 */
	public BigDecimal rowSum(int row) {
		Map<Integer, BigDecimal> rowMap = data.get(row);
		if (rowMap == null) {
			return BigDecimal.ZERO;
		}
		return rowMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	/**
	 * Computes the sum of all values in a column.
	 * 
	 * @param col Column index
	 * @return Sum of all values in the column
	 */
	public BigDecimal columnSum(int col) {
		BigDecimal sum = BigDecimal.ZERO;
		for (Map<Integer, BigDecimal> rowMap : data.values()) {
			BigDecimal v = rowMap.get(col);
			if (v != null) {
				sum = sum.add(v);
			}
		}
		return sum;
	}

	/**
	 * Returns the internal map representation of the matrix.
	 * 
	 * @return The underlying data structure
	 */
	public Map<Integer, Map<Integer, BigDecimal>> asMap() {
		return data;
	}
}
