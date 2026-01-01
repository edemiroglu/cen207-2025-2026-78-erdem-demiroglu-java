package com.hyildizoglu.budgetCreation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import com.hyildizoglu.algorithms.matrix.SparseMatrix;
import com.hyildizoglu.models.Budget;

/**
 * Service layer for budget management operations.
 * Provides CRUD operations and budget matrix generation.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class BudgetService {

	/** Repository for budget data access. */
	private final BudgetFileRepository budgetRepository;

	/**
	 * Creates a new BudgetService with the specified repository.
	 * 
	 * @param budgetRepository The repository for budget data access
	 */
	public BudgetService(BudgetFileRepository budgetRepository) {
		this.budgetRepository = budgetRepository;
	}

	/**
	 * Creates a new budget for a user.
	 * 
	 * @param userId     The user ID
	 * @param name       Budget name
	 * @param totalLimit Maximum spending limit
	 * @param startDate  Start date of budget period
	 * @param endDate    End date of budget period
	 * @return The created budget with assigned ID
	 */
	public Budget createBudget(int userId, String name, BigDecimal totalLimit, LocalDate startDate,
			LocalDate endDate) {
		Budget budget = new Budget(0, userId, name, totalLimit, startDate, endDate);
		return budgetRepository.save(budget);
	}

	/**
	 * Lists all budgets for a user.
	 * 
	 * @param userId The user ID
	 * @return List of user's budgets
	 */
	public List<Budget> listBudgetsForUser(int userId) {
		return budgetRepository.findByUserId(userId);
	}

	/**
	 * Updates an existing budget.
	 * 
	 * @param budgetId   The budget ID to update
	 * @param name       New budget name
	 * @param totalLimit New spending limit
	 * @param startDate  New start date
	 * @param endDate    New end date
	 * @return Updated budget, or null if not found
	 */
	public Budget updateBudget(int budgetId, String name, BigDecimal totalLimit, LocalDate startDate,
			LocalDate endDate) {
		java.util.Optional<Budget> budgetOpt = budgetRepository.findById(budgetId);
		if (budgetOpt.isPresent()) {
			Budget budget = budgetOpt.get();
			Budget updated = new Budget(budget.getId(), budget.getUserId(), name, totalLimit, startDate, endDate);
			return budgetRepository.update(updated);
		}
		return null;
	}

	/**
	 * Deletes a budget.
	 * 
	 * @param budgetId The budget ID to delete
	 * @return true if deleted, false if not found
	 */
	public boolean deleteBudget(int budgetId) {
		return budgetRepository.deleteById(budgetId);
	}

	/**
	 * Builds a day-category budget limit matrix for a specific month.
	 * Distributes budget limits equally across the overlapping days.
	 * 
	 * Row index: day of month (1-31)
	 * Column index: category ID (0 for general budget)
	 * 
	 * @param userId The user ID
	 * @param month  The year-month to build matrix for
	 * @return Sparse matrix with daily budget allocations
	 */
	public SparseMatrix buildBudgetMatrixForMonth(int userId, YearMonth month) {
		SparseMatrix matrix = new SparseMatrix();
		List<Budget> budgets = listBudgetsForUser(userId);
		for (Budget b : budgets) {
			LocalDate start = b.getStartDate();
			LocalDate end = b.getEndDate();
			LocalDate current = month.atDay(1);
			LocalDate endOfMonth = month.atEndOfMonth();
			// find the overlapping date range
			LocalDate from = current.isAfter(start) ? current : start;
			LocalDate to = endOfMonth.isBefore(end) ? endOfMonth : end;
			if (from.isAfter(to)) {
				continue;
			}
			int days = (int) (to.toEpochDay() - from.toEpochDay() + 1);
			if (days <= 0) {
				continue;
			}
			// distribute budget equally across days
			java.math.BigDecimal perDay = b.getTotalLimit().divide(new java.math.BigDecimal(days),
					java.math.BigDecimal.ROUND_HALF_UP);
			LocalDate d = from;
			while (!d.isAfter(to)) {
				int dayIndex = d.getDayOfMonth();
				// using column 0 as general budget since there's no category ID
				matrix.addTo(dayIndex, 0, perDay);
				d = d.plusDays(1);
			}
		}
		return matrix;
	}
}
