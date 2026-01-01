package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.graph.CategoryGraph;
import com.hyildizoglu.algorithms.graph.GraphTraversal;

@DisplayName("GraphTraversal Algorithm Tests")
class GraphTraversalTest {

	private CategoryGraph graph;

	@BeforeEach
	void setUp() {
		graph = new CategoryGraph(false); // Undirected
	}

	@Test
	@DisplayName("BFS traversal")
	void testBFS() {
		graph.addEdge(1, 2);
		graph.addEdge(1, 3);
		graph.addEdge(2, 4);
		graph.addEdge(3, 4);

		List<Integer> order = GraphTraversal.bfs(graph, 1);

		assertNotNull(order);
		assertTrue(order.contains(1));
		assertTrue(order.contains(2));
		assertTrue(order.contains(3));
		assertTrue(order.contains(4));
	}

	@Test
	@DisplayName("BFS on disconnected graph")
	void testBFS_DisconnectedGraph() {
		graph.addEdge(1, 2);
		graph.addEdge(3, 4);

		List<Integer> order = GraphTraversal.bfs(graph, 1);

		assertNotNull(order);
		assertTrue(order.contains(1));
		assertTrue(order.contains(2));
		assertFalse(order.contains(3)); // Disconnected
	}

	@Test
	@DisplayName("DFS traversal")
	void testDFS() {
		graph.addEdge(1, 2);
		graph.addEdge(1, 3);
		graph.addEdge(2, 4);
		graph.addEdge(3, 4);

		List<Integer> order = GraphTraversal.dfs(graph, 1);

		assertNotNull(order);
		assertTrue(order.contains(1));
		assertTrue(order.contains(2));
		assertTrue(order.contains(3));
		assertTrue(order.contains(4));
	}

	@Test
	@DisplayName("DFS on disconnected graph")
	void testDFS_DisconnectedGraph() {
		graph.addEdge(1, 2);
		graph.addEdge(3, 4);

		List<Integer> order = GraphTraversal.dfs(graph, 1);

		assertNotNull(order);
		assertTrue(order.contains(1));
		assertTrue(order.contains(2));
		assertFalse(order.contains(3)); // Disconnected
	}

	@Test
	@DisplayName("BFS single node")
	void testBFS_SingleNode() {
		graph.addEdge(1, 1);

		List<Integer> order = GraphTraversal.bfs(graph, 1);

		assertNotNull(order);
		assertTrue(order.size() >= 1);
	}

	@Test
	@DisplayName("DFS single node")
	void testDFS_SingleNode() {
		graph.addEdge(1, 1);

		List<Integer> order = GraphTraversal.dfs(graph, 1);

		assertNotNull(order);
		assertTrue(order.size() >= 1);
	}
}
