package com.hyildizoglu.financialSummary;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.time.LocalDate;

import com.hyildizoglu.algorithms.heap.HeapSort;
import com.hyildizoglu.algorithms.heap.MaxHeap;
import com.hyildizoglu.algorithms.tree.BPlusTree;
import com.hyildizoglu.algorithms.matrix.SparseMatrix;
import com.hyildizoglu.budgetCreation.BudgetFileRepository;
import com.hyildizoglu.expenseLogging.ExpenseFileRepository;
import com.hyildizoglu.models.Budget;
import com.hyildizoglu.models.Expense;
import com.hyildizoglu.models.Goal;
import com.hyildizoglu.savingsGoal.GoalFileRepository;

/**
 * Service for generating financial summaries and analytics.
 * Provides various reports including expense matrices, top expenses,
 * budget comparisons, and savings progress tracking.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class FinancialSummaryService {

	/** Repository for budget data. */
	private final BudgetFileRepository budgetRepository;
	
	/** Repository for expense data. */
	private final ExpenseFileRepository expenseRepository;
	
	/** Repository for goal data (optional). */
	private GoalFileRepository goalRepository;

	/**
	 * Creates a FinancialSummaryService without goal support.
	 * 
	 * @param budgetRepository  Repository for budget data
	 * @param expenseRepository Repository for expense data
	 */
	public FinancialSummaryService(BudgetFileRepository budgetRepository, ExpenseFileRepository expenseRepository) {
		this.budgetRepository = budgetRepository;
		this.expenseRepository = expenseRepository;
	}

	/**
	 * Creates a FinancialSummaryService with goal support.
	 * 
	 * @param budgetRepository  Repository for budget data
	 * @param expenseRepository Repository for expense data
	 * @param goalRepository    Repository for goal data
	 */
	public FinancialSummaryService(BudgetFileRepository budgetRepository, ExpenseFileRepository expenseRepository,
			GoalFileRepository goalRepository) {
		this.budgetRepository = budgetRepository;
		this.expenseRepository = expenseRepository;
		this.goalRepository = goalRepository;
	}

	/**
	 * Calculates the total expenses for a user.
	 * 
	 * @param userId The user ID
	 * @return Sum of all expense amounts
	 */
	public BigDecimal calculateTotalExpenses(int userId) {
		List<Expense> expenses = expenseRepository.findByUserId(userId);
		return expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	/**
	 * Calculates the total budget limit for a user.
	 * 
	 * @param userId The user ID
	 * @return Sum of all budget limits
	 */
	public BigDecimal calculateTotalBudgetLimit(int userId) {
		List<Budget> budgets = budgetRepository.findByUserId(userId);
		return budgets.stream().map(Budget::getTotalLimit).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	/**
	 * Calculates the remaining budget (total limit minus total expenses).
	 * 
	 * @param userId The user ID
	 * @return Remaining budget amount
	 */
	public BigDecimal calculateRemainingBudget(int userId) {
		return calculateTotalBudgetLimit(userId).subtract(calculateTotalExpenses(userId));
	}

	/**
	 * Builds a sparse matrix of expenses for a specific month.
	 * Row index represents day of month (1-31).
	 * Column index represents category ID.
	 * 
	 * @param userId The user ID
	 * @param month  The year-month to analyze
	 * @return Sparse matrix with expense data
	 */
	public SparseMatrix buildExpenseMatrixForMonth(int userId, YearMonth month) {
		SparseMatrix matrix = new SparseMatrix();
		List<Expense> expenses = expenseRepository.findByUserId(userId);
		for (Expense e : expenses) {
			if (e.getDate().getYear() == month.getYear() && e.getDate().getMonthValue() == month.getMonthValue()) {
				int dayIndex = e.getDate().getDayOfMonth();
				int categoryIndex = e.getCategoryId();
				matrix.addTo(dayIndex, categoryIndex, e.getAmount());
			}
		}
		return matrix;
	}

	/**
	 * Returns the top N expenses by amount using a max-heap.
	 * 
	 * @param userId The user ID
	 * @param n      Number of expenses to return
	 * @return List of top N expenses sorted by amount (highest first)
	 */
	public List<Expense> topNExpenses(int userId, int n) {
		List<Expense> expenses = new ArrayList<>(expenseRepository.findByUserId(userId));
		if (expenses.isEmpty() || n <= 0) {
			return new ArrayList<>();
		}
		// Wrapper to compare expenses by amount
		class ExpenseWrapper implements Comparable<ExpenseWrapper> {
			final Expense e;

			ExpenseWrapper(Expense e) {
				this.e = e;
			}

			@Override
			public int compareTo(ExpenseWrapper o) {
				return this.e.getAmount().compareTo(o.e.getAmount());
			}
		}

		MaxHeap<ExpenseWrapper> heap = new MaxHeap<>();
		for (Expense e : expenses) {
			heap.add(new ExpenseWrapper(e));
		}
		List<Expense> result = new ArrayList<>();
		for (int i = 0; i < n && !heap.isEmpty(); i++) {
			result.add(heap.poll().e);
		}
		return result;
	}

	/**
	 * Sorts all expenses by amount in descending order using HeapSort.
	 * 
	 * @param userId The user ID
	 * @return Sorted list of expenses (highest amount first)
	 */
	public List<Expense> sortExpensesByAmountDescending(int userId) {
		List<Expense> expenses = new ArrayList<>(expenseRepository.findByUserId(userId));
		// Wrapper to compare expenses by amount
		class ExpenseWrapper implements Comparable<ExpenseWrapper> {
			final Expense e;

			ExpenseWrapper(Expense e) {
				this.e = e;
			}

			@Override
			public int compareTo(ExpenseWrapper o) {
				return this.e.getAmount().compareTo(o.e.getAmount());
			}
		}
		List<ExpenseWrapper> wrappers = new ArrayList<>();
		for (Expense e : expenses) {
			wrappers.add(new ExpenseWrapper(e));
		}
		HeapSort.sortDescending(wrappers);
		List<Expense> result = new ArrayList<>();
		for (ExpenseWrapper w : wrappers) {
			result.add(w.e);
		}
		return result;
	}

	/**
	 * Finds the top N spending categories by total amount.
	 * 
	 * @param userId The user ID
	 * @param n      Number of categories to return
	 * @return List of category ID to total amount entries (highest first)
	 */
	public List<Map.Entry<Integer, BigDecimal>> topNCategoriesBySpending(int userId, int n) {
		List<Expense> expenses = expenseRepository.findByUserId(userId);
		Map<Integer, BigDecimal> totals = new HashMap<>();
		for (Expense e : expenses) {
			totals.merge(e.getCategoryId(), e.getAmount(), BigDecimal::add);
		}
		class EntryWrapper implements Comparable<EntryWrapper> {
			final Map.Entry<Integer, BigDecimal> entry;

			EntryWrapper(Map.Entry<Integer, BigDecimal> entry) {
				this.entry = entry;
			}

			@Override
			public int compareTo(EntryWrapper o) {
				return this.entry.getValue().compareTo(o.entry.getValue());
			}
		}
		MaxHeap<EntryWrapper> heap = new MaxHeap<>();
		for (Map.Entry<Integer, BigDecimal> e : totals.entrySet()) {
			heap.add(new EntryWrapper(e));
		}
		List<Map.Entry<Integer, BigDecimal>> result = new ArrayList<>();
		for (int i = 0; i < n && !heap.isEmpty(); i++) {
			result.add(heap.poll().entry);
		}
		// Sort from highest to lowest
		result.sort(Comparator.comparing(Map.Entry<Integer, BigDecimal>::getValue).reversed());
		return result;
	}

	/**
	 * Retrieves all expenses within a date range using B+ tree.
	 * 
	 * @param userId The user ID
	 * @param from   Start date (inclusive)
	 * @param to     End date (inclusive)
	 * @return List of expenses in the date range
	 */
	public List<Expense> expensesInDateRange(int userId, LocalDate from, LocalDate to) {
		List<Expense> expenses = expenseRepository.findByUserId(userId);
		BPlusTree<LocalDate, Expense> tree = new BPlusTree<>();
		for (Expense e : expenses) {
			tree.put(e.getDate(), e);
		}
		return tree.rangeQuery(from, to);
	}

	/**
	 * Compares budget limit vs actual spending for a specific budget.
	 * 
	 * @param userId   The user ID
	 * @param budgetId The budget ID
	 * @return Map with keys: budgetLimit, actualSpending, difference, percentage
	 */
	public Map<String, BigDecimal> budgetVsActual(int userId, int budgetId) {
		Map<String, BigDecimal> result = new HashMap<>();
		java.util.Optional<Budget> budgetOpt = budgetRepository.findById(budgetId);
		if (!budgetOpt.isPresent()) {
			return result;
		}
		Budget budget = budgetOpt.get();
		if (budget.getUserId() != userId) {
			return result;
		}
		BigDecimal budgetLimit = budget.getTotalLimit();
		BigDecimal actualSpending = BigDecimal.ZERO;
		List<Expense> expenses = expenseRepository.findByUserId(userId);
		for (Expense e : expenses) {
			if (e.getBudgetId() == budgetId) {
				actualSpending = actualSpending.add(e.getAmount());
			}
		}
		BigDecimal difference = budgetLimit.subtract(actualSpending);
		result.put("budgetLimit", budgetLimit);
		result.put("actualSpending", actualSpending);
		result.put("difference", difference);
		result.put("percentage", budgetLimit.compareTo(BigDecimal.ZERO) > 0
				? actualSpending.divide(budgetLimit, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100))
				: BigDecimal.ZERO);
		return result;
	}

	/**
	 * Returns progress report for all savings goals of a user.
	 * 
	 * @param userId The user ID
	 * @return Map of Goal to progress details (target, current, remaining, percentage)
	 */
	public Map<Goal, Map<String, BigDecimal>> savingsProgress(int userId) {
		Map<Goal, Map<String, BigDecimal>> result = new HashMap<>();
		if (goalRepository == null) {
			return result;
		}
		List<Goal> goals = goalRepository.findByUserId(userId);
		for (Goal goal : goals) {
			Map<String, BigDecimal> progress = new HashMap<>();
			BigDecimal target = goal.getTargetAmount();
			BigDecimal current = goal.getCurrentAmount();
			BigDecimal remaining = target.subtract(current);
			BigDecimal percentage = target.compareTo(BigDecimal.ZERO) > 0
					? current.divide(target, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100))
					: BigDecimal.ZERO;
			progress.put("target", target);
			progress.put("current", current);
			progress.put("remaining", remaining);
			progress.put("percentage", percentage);
			result.put(goal, progress);
		}
		return result;
	}
}
