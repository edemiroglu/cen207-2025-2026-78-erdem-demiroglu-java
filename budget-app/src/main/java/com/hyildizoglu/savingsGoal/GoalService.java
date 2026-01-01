package com.hyildizoglu.savingsGoal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hyildizoglu.algorithms.graph.SCCAnalyzer;
import com.hyildizoglu.models.Goal;

/**
 * Service layer for savings goal management operations.
 * Provides CRUD operations and goal dependency analysis.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class GoalService {

	/** Repository for goal data access. */
	private final GoalFileRepository goalRepository;

	/**
	 * Creates a new GoalService with the specified repository.
	 * 
	 * @param goalRepository The repository for goal data access
	 */
	public GoalService(GoalFileRepository goalRepository) {
		this.goalRepository = goalRepository;
	}

	/**
	 * Creates a new savings goal for a user.
	 * 
	 * @param userId        The user ID
	 * @param name          Goal name
	 * @param targetAmount  Target amount to save
	 * @param currentAmount Current saved amount
	 * @param deadline      Deadline date
	 * @return The created goal with assigned ID
	 */
	public Goal createGoal(int userId, String name, BigDecimal targetAmount, BigDecimal currentAmount,
			LocalDate deadline) {
		Goal goal = new Goal(0, userId, name, targetAmount, currentAmount, deadline);
		return goalRepository.save(goal);
	}

	/**
	 * Lists all goals for a user.
	 * 
	 * @param userId The user ID
	 * @return List of user's goals
	 */
	public List<Goal> listGoalsForUser(int userId) {
		return goalRepository.findByUserId(userId);
	}

	/**
	 * Updates an existing goal.
	 * 
	 * @param goalId        The goal ID to update
	 * @param name          New goal name
	 * @param targetAmount  New target amount
	 * @param currentAmount New current amount
	 * @param deadline      New deadline
	 * @return Updated goal, or null if not found
	 */
	public Goal updateGoal(int goalId, String name, BigDecimal targetAmount, BigDecimal currentAmount,
			LocalDate deadline) {
		List<Goal> goals = goalRepository.findAll();
		for (Goal g : goals) {
			if (g.getId() == goalId) {
				Goal updated = new Goal(goalId, g.getUserId(), name, targetAmount, currentAmount, deadline);
				return goalRepository.update(updated);
			}
		}
		return null;
	}

	/**
	 * Deletes a goal.
	 * 
	 * @param goalId The goal ID to delete
	 * @return true if deleted, false if not found
	 */
	public boolean deleteGoal(int goalId) {
		return goalRepository.deleteById(goalId);
	}

	/**
	 * Analyzes goal dependencies using Strongly Connected Components (SCC).
	 * Useful for finding circular dependencies between goals.
	 * 
	 * @param dependencyEdges Map where key is goal ID and value is list of dependent goal IDs
	 * @return List of SCCs, each being a set of mutually dependent goal IDs
	 */
	public List<Set<Integer>> analyzeGoalDependencies(Map<Integer, List<Integer>> dependencyEdges) {
		SCCAnalyzer analyzer = new SCCAnalyzer();
		// Ensure all goals are in the graph
		Map<Integer, List<Integer>> graph = new HashMap<>(dependencyEdges);
		for (Goal g : goalRepository.findAll()) {
			graph.putIfAbsent(g.getId(), Collections.<Integer>emptyList());
		}
		return analyzer.findSCCs(graph);
	}
}
