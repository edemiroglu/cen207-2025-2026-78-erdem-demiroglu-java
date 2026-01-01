package com.hyildizoglu.savingsGoal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.hyildizoglu.models.Goal;

/**
 * Repository for storing and retrieving savings goal data from a file.
 * Uses a simple semicolon-separated format for persistence.
 * 
 * File format (one goal per line):
 * id;userId;name;targetAmount;currentAmount;deadline
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class GoalFileRepository {

	/** Path to the goal data file. */
	private final Path filePath;

	/**
	 * Creates a repository with the default file path (Goal.dat).
	 */
	public GoalFileRepository() {
		this(Paths.get("Goal.dat"));
	}

	/**
	 * Creates a repository with the specified file path.
	 * 
	 * @param filePath Path to the goal data file
	 */
	public GoalFileRepository(Path filePath) {
		this.filePath = filePath;
	}

	/**
	 * Retrieves all goals from the file.
	 * 
	 * @return List of all goals, or empty list if file doesn't exist
	 */
	public List<Goal> findAll() {
		List<Goal> goals = new ArrayList<>();
		if (!Files.exists(filePath)) {
			return goals;
		}
		try (BufferedReader reader = Files.newBufferedReader(filePath)) {
			String line;
			while ((line = reader.readLine()) != null) {
				Goal goal = parseGoal(line);
				if (goal != null) {
					goals.add(goal);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to read goals from file", e);
		}
		return goals;
	}

	/**
	 * Finds all goals for a specific user.
	 * 
	 * @param userId The user ID to filter by
	 * @return List of goals belonging to the user
	 */
	public List<Goal> findByUserId(int userId) {
		List<Goal> result = new ArrayList<>();
		for (Goal goal : findAll()) {
			if (goal.getUserId() == userId) {
				result.add(goal);
			}
		}
		return result;
	}

	/**
	 * Saves a new goal to the file.
	 * Automatically assigns a new ID.
	 * 
	 * @param goal The goal to save (ID is ignored)
	 * @return The saved goal with assigned ID
	 */
	public Goal save(Goal goal) {
		List<Goal> goals = findAll();
		int nextId = goals.stream().mapToInt(Goal::getId).max().orElse(0) + 1;
		Goal toSave = new Goal(nextId, goal.getUserId(), goal.getName(), goal.getTargetAmount(),
				goal.getCurrentAmount(), goal.getDeadline());
		goals.add(toSave);
		writeAll(goals);
		return toSave;
	}

	/**
	 * Updates an existing goal.
	 * 
	 * @param goal The goal with updated data
	 * @return The updated goal, or null if not found
	 */
	public Goal update(Goal goal) {
		List<Goal> goals = findAll();
		for (int i = 0; i < goals.size(); i++) {
			if (goals.get(i).getId() == goal.getId()) {
				goals.set(i, goal);
				writeAll(goals);
				return goal;
			}
		}
		return null;
	}

	/**
	 * Deletes a goal by its ID.
	 * 
	 * @param id The goal ID to delete
	 * @return true if deleted, false if not found
	 */
	public boolean deleteById(int id) {
		List<Goal> goals = findAll();
		boolean removed = goals.removeIf(g -> g.getId() == id);
		if (removed) {
			writeAll(goals);
		}
		return removed;
	}

	/**
	 * Writes all goals to the file.
	 * 
	 * @param goals List of goals to write
	 */
	private void writeAll(List<Goal> goals) {
		try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
			for (Goal goal : goals) {
				writer.write(formatGoal(goal));
				writer.newLine();
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to write goals to file", e);
		}
	}

	/**
	 * Parses a line from the file into a Goal object.
	 * 
	 * @param line The line to parse
	 * @return Parsed Goal, or null if parsing fails
	 */
	private Goal parseGoal(String line) {
		String[] parts = line.split(";", -1);
		if (parts.length < 6) {
			return null;
		}
		try {
			int id = Integer.parseInt(parts[0]);
			int userId = Integer.parseInt(parts[1]);
			String name = parts[2];
			BigDecimal targetAmount = new BigDecimal(parts[3]);
			BigDecimal currentAmount = new BigDecimal(parts[4]);
			LocalDate deadline = LocalDate.parse(parts[5]);
			return new Goal(id, userId, name, targetAmount, currentAmount, deadline);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Formats a goal as a line for file storage.
	 * 
	 * @param goal The goal to format
	 * @return Formatted string representation
	 */
	private String formatGoal(Goal goal) {
		return goal.getId() + ";" + goal.getUserId() + ";" + goal.getName() + ";"
				+ goal.getTargetAmount().toPlainString() + ";" + goal.getCurrentAmount().toPlainString() + ";"
				+ goal.getDeadline();
	}
}
