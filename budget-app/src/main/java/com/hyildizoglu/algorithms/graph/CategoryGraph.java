package com.hyildizoglu.algorithms.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Graph data structure for representing category relationships.
 * Supports both directed and undirected graphs using adjacency list representation.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class CategoryGraph {

	/** Adjacency list: maps each category ID to its neighbors. */
	private final Map<Integer, List<Integer>> adjacency = new HashMap<>();
	
	/** Whether this is a directed graph. */
	private final boolean directed;

	/**
	 * Creates a new CategoryGraph.
	 * 
	 * @param directed true for directed graph, false for undirected
	 */
	public CategoryGraph(boolean directed) {
		this.directed = directed;
	}

	/**
	 * Adds an edge between two category nodes.
	 * For undirected graphs, adds edges in both directions.
	 * 
	 * @param fromCategoryId Source category ID
	 * @param toCategoryId   Target category ID
	 */
	public void addEdge(int fromCategoryId, int toCategoryId) {
		adjacency.computeIfAbsent(fromCategoryId, k -> new ArrayList<>()).add(toCategoryId);
		if (!directed) {
			adjacency.computeIfAbsent(toCategoryId, k -> new ArrayList<>()).add(fromCategoryId);
		}
	}

	/**
	 * Returns the list of neighboring category IDs.
	 * 
	 * @param categoryId The category ID to get neighbors for
	 * @return List of neighbor category IDs (empty list if none)
	 */
	public List<Integer> neighbors(int categoryId) {
		return adjacency.getOrDefault(categoryId, new ArrayList<>());
	}

	/**
	 * Returns the underlying adjacency list representation.
	 * 
	 * @return Map of category ID to list of neighbor IDs
	 */
	public Map<Integer, List<Integer>> getAdjacency() {
		return adjacency;
	}
}
