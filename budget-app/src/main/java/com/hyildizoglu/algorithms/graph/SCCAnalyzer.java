package com.hyildizoglu.algorithms.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Strongly Connected Components (SCC) analyzer using Kosaraju's algorithm.
 * Finds all strongly connected components in a directed graph.
 * A strongly connected component is a maximal set of vertices where
 * every vertex is reachable from every other vertex.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class SCCAnalyzer {

	/**
	 * Finds all strongly connected components in the graph.
	 * Uses Kosaraju's two-pass algorithm:
	 * 1. First DFS to compute finish times
	 * 2. Second DFS on reversed graph in order of decreasing finish time
	 * 
	 * @param graph Adjacency list representation of directed graph
	 * @return List of SCCs, where each SCC is a Set of vertex IDs
	 */
	public List<Set<Integer>> findSCCs(Map<Integer, List<Integer>> graph) {
		List<Integer> order = new ArrayList<>();
		Set<Integer> visited = new HashSet<>();
		for (Integer v : graph.keySet()) {
			if (!visited.contains(v)) {
				dfs1(graph, v, visited, order);
			}
		}

		Map<Integer, List<Integer>> reversed = reverseGraph(graph);
		visited.clear();
		List<Set<Integer>> components = new ArrayList<>();
		for (int i = order.size() - 1; i >= 0; i--) {
			int v = order.get(i);
			if (!visited.contains(v)) {
				Set<Integer> comp = new HashSet<>();
				dfs2(reversed, v, visited, comp);
				components.add(comp);
			}
		}
		return components;
	}

	/**
	 * First DFS pass to compute finish order.
	 * 
	 * @param g       The graph
	 * @param v       Current vertex
	 * @param visited Set of visited vertices
	 * @param order   List to store finish order
	 */
	private void dfs1(Map<Integer, List<Integer>> g, int v, Set<Integer> visited, List<Integer> order) {
		if (!visited.add(v)) {
			return;
		}
		for (int n : g.getOrDefault(v, Collections.<Integer>emptyList())) {
			dfs1(g, n, visited, order);
		}
		order.add(v);
	}

	/**
	 * Second DFS pass on reversed graph to find components.
	 * 
	 * @param g       The reversed graph
	 * @param v       Current vertex
	 * @param visited Set of visited vertices
	 * @param comp    Current component being built
	 */
	private void dfs2(Map<Integer, List<Integer>> g, int v, Set<Integer> visited, Set<Integer> comp) {
		if (!visited.add(v)) {
			return;
		}
		comp.add(v);
		for (int n : g.getOrDefault(v, Collections.<Integer>emptyList())) {
			dfs2(g, n, visited, comp);
		}
	}

	/**
	 * Creates a reversed version of the graph (all edges reversed).
	 * 
	 * @param g The original graph
	 * @return The reversed graph
	 */
	private Map<Integer, List<Integer>> reverseGraph(Map<Integer, List<Integer>> g) {
		Map<Integer, List<Integer>> r = new HashMap<>();
		for (Map.Entry<Integer, List<Integer>> e : g.entrySet()) {
			int u = e.getKey();
			for (int v : e.getValue()) {
				r.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
			}
		}
		return r;
	}
}
