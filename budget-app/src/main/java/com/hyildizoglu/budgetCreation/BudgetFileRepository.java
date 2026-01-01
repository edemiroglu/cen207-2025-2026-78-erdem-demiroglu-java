package com.hyildizoglu.budgetCreation;

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
import java.util.Optional;

import com.hyildizoglu.models.Budget;

/**
 * Repository for storing and retrieving budget data from a file.
 * Uses a simple semicolon-separated format for persistence.
 * 
 * File format (one budget per line):
 * id;userId;name;totalLimit;startDate;endDate
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class BudgetFileRepository {

	/** Path to the budget data file. */
	private final Path filePath;

	/**
	 * Creates a repository with the default file path (Budget.dat).
	 */
	public BudgetFileRepository() {
		this(Paths.get("Budget.dat"));
	}

	/**
	 * Creates a repository with the specified file path.
	 * 
	 * @param filePath Path to the budget data file
	 */
	public BudgetFileRepository(Path filePath) {
		this.filePath = filePath;
	}

	/**
	 * Retrieves all budgets from the file.
	 * 
	 * @return List of all budgets, or empty list if file doesn't exist
	 */
	public List<Budget> findAll() {
		List<Budget> budgets = new ArrayList<>();
		if (!Files.exists(filePath)) {
			return budgets;
		}
		try (BufferedReader reader = Files.newBufferedReader(filePath)) {
			String line;
			while ((line = reader.readLine()) != null) {
				Budget budget = parseBudget(line);
				if (budget != null) {
					budgets.add(budget);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to read budgets from file", e);
		}
		return budgets;
	}

	/**
	 * Finds all budgets for a specific user.
	 * 
	 * @param userId The user ID to filter by
	 * @return List of budgets belonging to the user
	 */
	public List<Budget> findByUserId(int userId) {
		List<Budget> result = new ArrayList<>();
		for (Budget budget : findAll()) {
			if (budget.getUserId() == userId) {
				result.add(budget);
			}
		}
		return result;
	}

	/**
	 * Finds a budget by its ID.
	 * 
	 * @param id The budget ID
	 * @return Optional containing the budget if found
	 */
	public Optional<Budget> findById(int id) {
		return findAll().stream().filter(b -> b.getId() == id).findFirst();
	}

	/**
	 * Saves a new budget to the file.
	 * Automatically assigns a new ID.
	 * 
	 * @param budget The budget to save (ID is ignored)
	 * @return The saved budget with assigned ID
	 */
	public Budget save(Budget budget) {
		List<Budget> budgets = findAll();
		int nextId = budgets.stream().mapToInt(Budget::getId).max().orElse(0) + 1;
		Budget toSave = new Budget(nextId, budget.getUserId(), budget.getName(), budget.getTotalLimit(),
				budget.getStartDate(), budget.getEndDate());
		budgets.add(toSave);
		writeAll(budgets);
		return toSave;
	}

	/**
	 * Updates an existing budget.
	 * 
	 * @param budget The budget with updated data
	 * @return The updated budget, or null if not found
	 */
	public Budget update(Budget budget) {
		List<Budget> budgets = findAll();
		for (int i = 0; i < budgets.size(); i++) {
			if (budgets.get(i).getId() == budget.getId()) {
				budgets.set(i, budget);
				writeAll(budgets);
				return budget;
			}
		}
		return null;
	}

	/**
	 * Deletes a budget by its ID.
	 * 
	 * @param id The budget ID to delete
	 * @return true if deleted, false if not found
	 */
	public boolean deleteById(int id) {
		List<Budget> budgets = findAll();
		boolean removed = budgets.removeIf(b -> b.getId() == id);
		if (removed) {
			writeAll(budgets);
		}
		return removed;
	}

	/**
	 * Writes all budgets to the file.
	 * 
	 * @param budgets List of budgets to write
	 */
	private void writeAll(List<Budget> budgets) {
		try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
			for (Budget budget : budgets) {
				writer.write(formatBudget(budget));
				writer.newLine();
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to write budgets to file", e);
		}
	}

	/**
	 * Parses a line from the file into a Budget object.
	 * 
	 * @param line The line to parse
	 * @return Parsed Budget, or null if parsing fails
	 */
	private Budget parseBudget(String line) {
		String[] parts = line.split(";", -1);
		if (parts.length < 6) {
			return null;
		}
		try {
			int id = Integer.parseInt(parts[0]);
			int userId = Integer.parseInt(parts[1]);
			String name = parts[2];
			BigDecimal totalLimit = new BigDecimal(parts[3]);
			LocalDate startDate = LocalDate.parse(parts[4]);
			LocalDate endDate = LocalDate.parse(parts[5]);
			return new Budget(id, userId, name, totalLimit, startDate, endDate);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Formats a budget as a line for file storage.
	 * 
	 * @param budget The budget to format
	 * @return Formatted string representation
	 */
	private String formatBudget(Budget budget) {
		return budget.getId() + ";" + budget.getUserId() + ";" + budget.getName() + ";"
				+ budget.getTotalLimit().toPlainString() + ";" + budget.getStartDate() + ";" + budget.getEndDate();
	}
}
