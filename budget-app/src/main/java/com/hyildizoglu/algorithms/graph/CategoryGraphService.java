package com.hyildizoglu.algorithms.graph;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hyildizoglu.expenseLogging.ExpenseService;
import com.hyildizoglu.models.Expense;

/**
 * Service for managing and analyzing category hierarchy graphs.
 * Provides traversal and spending calculation across category hierarchies.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class CategoryGraphService {

	/** The underlying category graph. */
	private final CategoryGraph graph;
	
	/** Service for accessing expense data. */
	private final ExpenseService expenseService;

	/**
	 * Creates a new CategoryGraphService with an undirected graph.
	 * 
	 * @param expenseService Service for accessing expense data
	 */
	public CategoryGraphService(ExpenseService expenseService) {
		this.expenseService = expenseService;
		this.graph = new CategoryGraph(false); // Undirected graph
	}

	/**
	 * Adds a parent-child relationship between categories.
	 * 
	 * @param parentCategoryId The parent category ID
	 * @param childCategoryId  The child category ID
	 */
	public void addCategoryRelation(int parentCategoryId, int childCategoryId) {
		graph.addEdge(parentCategoryId, childCategoryId);
	}

	/**
	 * Traverses the category hierarchy using Breadth-First Search.
	 * 
	 * @param rootCategoryId The starting category ID
	 * @return List of category IDs in BFS order
	 */
	public List<Integer> traverseCategoryHierarchyBFS(int rootCategoryId) {
		return GraphTraversal.bfs(graph, rootCategoryId);
	}

	/**
	 * Traverses the category hierarchy using Depth-First Search.
	 * 
	 * @param rootCategoryId The starting category ID
	 * @return List of category IDs in DFS order
	 */
	public List<Integer> traverseCategoryHierarchyDFS(int rootCategoryId) {
		return GraphTraversal.dfs(graph, rootCategoryId);
	}

	/**
	 * Calculates total spending for a category and all its sub-categories.
	 * 
	 * @param userId         The user ID to filter expenses
	 * @param rootCategoryId The root category ID
	 * @return Map of category ID to total spending amount
	 */
	public Map<Integer, BigDecimal> calculateCategoryHierarchySpending(int userId, int rootCategoryId) {
		List<Integer> allCategories = traverseCategoryHierarchyBFS(rootCategoryId);
		Map<Integer, BigDecimal> spending = new HashMap<>();
		List<Expense> expenses = expenseService.listExpensesForUser(userId);
		for (Expense e : expenses) {
			if (allCategories.contains(e.getCategoryId())) {
				spending.merge(e.getCategoryId(), e.getAmount(), BigDecimal::add);
			}
		}
		return spending;
	}

	/**
	 * Returns the underlying category graph structure.
	 * 
	 * @return The CategoryGraph instance
	 */
	public CategoryGraph getGraph() {
		return graph;
	}
}
