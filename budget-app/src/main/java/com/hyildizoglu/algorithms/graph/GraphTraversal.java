package com.hyildizoglu.algorithms.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Graph traversal algorithms for CategoryGraph.
 * Provides BFS (Breadth-First Search) and DFS (Depth-First Search) implementations.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class GraphTraversal {

	/**
	 * Performs Breadth-First Search starting from a given node.
	 * Visits nodes level by level.
	 * 
	 * @param graph The graph to traverse
	 * @param start Starting node ID
	 * @return List of visited node IDs in BFS order
	 */
	public static List<Integer> bfs(CategoryGraph graph, int start) {
		List<Integer> order = new ArrayList<>();
		Set<Integer> visited = new HashSet<>();
		Queue<Integer> q = new ArrayDeque<>();
		visited.add(start);
		q.add(start);
		while (!q.isEmpty()) {
			int v = q.poll();
			order.add(v);
			for (int n : graph.neighbors(v)) {
				if (visited.add(n)) {
					q.add(n);
				}
			}
		}
		return order;
	}

	/**
	 * Performs Depth-First Search starting from a given node.
	 * Visits nodes by going as deep as possible before backtracking.
	 * 
	 * @param graph The graph to traverse
	 * @param start Starting node ID
	 * @return List of visited node IDs in DFS order
	 */
	public static List<Integer> dfs(CategoryGraph graph, int start) {
		List<Integer> order = new ArrayList<>();
		Set<Integer> visited = new HashSet<>();
		dfsRec(graph, start, visited, order);
		return order;
	}

	/**
	 * Recursive helper method for DFS traversal.
	 * 
	 * @param graph   The graph to traverse
	 * @param v       Current node ID
	 * @param visited Set of already visited nodes
	 * @param order   List to store visit order
	 */
	private static void dfsRec(CategoryGraph graph, int v, Set<Integer> visited, List<Integer> order) {
		if (!visited.add(v)) {
			return;
		}
		order.add(v);
		for (int n : graph.neighbors(v)) {
			dfsRec(graph, n, visited, order);
		}
	}
}
