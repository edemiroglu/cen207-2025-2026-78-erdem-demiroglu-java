package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.graph.CategoryGraph;

@DisplayName("CategoryGraph Algorithm Tests")
class CategoryGraphTest {

	private CategoryGraph directedGraph;
	private CategoryGraph undirectedGraph;

	@BeforeEach
	void setUp() {
		directedGraph = new CategoryGraph(true);
		undirectedGraph = new CategoryGraph(false);
	}

	@Test
	@DisplayName("Add edge in directed graph")
	void testAddEdge_Directed() {
		directedGraph.addEdge(1, 2);

		List<Integer> neighbors = directedGraph.neighbors(1);
		assertTrue(neighbors.contains(2));

		List<Integer> reverse = directedGraph.neighbors(2);
		assertFalse(reverse.contains(1)); // Directed, no reverse
	}

	@Test
	@DisplayName("Add edge in undirected graph")
	void testAddEdge_Undirected() {
		undirectedGraph.addEdge(1, 2);

		List<Integer> neighbors1 = undirectedGraph.neighbors(1);
		assertTrue(neighbors1.contains(2));

		List<Integer> neighbors2 = undirectedGraph.neighbors(2);
		assertTrue(neighbors2.contains(1)); // Undirected, bidirectional
	}

	@Test
	@DisplayName("Empty neighbor list")
	void testNeighbors_Empty() {
		List<Integer> neighbors = undirectedGraph.neighbors(1);
		assertNotNull(neighbors);
		assertTrue(neighbors.isEmpty());
	}

	@Test
	@DisplayName("Multiple neighbors")
	void testNeighbors_Multiple() {
		undirectedGraph.addEdge(1, 2);
		undirectedGraph.addEdge(1, 3);
		undirectedGraph.addEdge(1, 4);

		List<Integer> neighbors = undirectedGraph.neighbors(1);
		assertEquals(3, neighbors.size());
		assertTrue(neighbors.contains(2));
		assertTrue(neighbors.contains(3));
		assertTrue(neighbors.contains(4));
	}

	@Test
	@DisplayName("Get adjacency map")
	void testGetAdjacency() {
		undirectedGraph.addEdge(1, 2);
		undirectedGraph.addEdge(1, 3);

		java.util.Map<Integer, List<Integer>> adj = undirectedGraph.getAdjacency();
		assertNotNull(adj);
		assertTrue(adj.containsKey(1));
	}
}
