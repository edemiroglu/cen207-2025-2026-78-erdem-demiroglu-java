package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.graph.SCCAnalyzer;

@DisplayName("SCCAnalyzer Algorithm Tests")
class SCCAnalyzerTest {

	@Test
	@DisplayName("Find SCC in simple graph")
	void testFindSCCs_SimpleGraph() {
		SCCAnalyzer analyzer = new SCCAnalyzer();
		Map<Integer, List<Integer>> graph = new HashMap<>();
		graph.put(1, java.util.Arrays.asList(2));
		graph.put(2, java.util.Arrays.asList(3));
		graph.put(3, java.util.Arrays.asList(1));

		List<Set<Integer>> sccs = analyzer.findSCCs(graph);

		assertNotNull(sccs);
		assertTrue(sccs.size() > 0);
	}

	@Test
	@DisplayName("Find SCC in complex graph")
	void testFindSCCs_ComplexGraph() {
		SCCAnalyzer analyzer = new SCCAnalyzer();
		Map<Integer, List<Integer>> graph = new HashMap<>();
		graph.put(1, java.util.Arrays.asList(2));
		graph.put(2, java.util.Arrays.asList(3, 4));
		graph.put(3, java.util.Arrays.asList(1));
		graph.put(4, java.util.Arrays.asList(5));
		graph.put(5, new ArrayList<>());

		List<Set<Integer>> sccs = analyzer.findSCCs(graph);

		assertNotNull(sccs);
		assertTrue(sccs.size() > 0);
	}

	@Test
	@DisplayName("Find SCC in graph with no edges")
	void testFindSCCs_NoEdges() {
		SCCAnalyzer analyzer = new SCCAnalyzer();
		Map<Integer, List<Integer>> graph = new HashMap<>();
		graph.put(1, new ArrayList<>());
		graph.put(2, new ArrayList<>());
		graph.put(3, new ArrayList<>());

		List<Set<Integer>> sccs = analyzer.findSCCs(graph);

		assertNotNull(sccs);
		assertEquals(3, sccs.size()); // Each node is its own SCC
	}

	@Test
	@DisplayName("Find SCC in empty graph")
	void testFindSCCs_EmptyGraph() {
		SCCAnalyzer analyzer = new SCCAnalyzer();
		Map<Integer, List<Integer>> graph = new HashMap<>();

		List<Set<Integer>> sccs = analyzer.findSCCs(graph);

		assertNotNull(sccs);
		assertTrue(sccs.isEmpty());
	}
}
