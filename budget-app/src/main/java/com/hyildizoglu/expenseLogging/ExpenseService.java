package com.hyildizoglu.expenseLogging;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.hyildizoglu.algorithms.graph.CategoryGraph;
import com.hyildizoglu.algorithms.graph.GraphTraversal;
import com.hyildizoglu.algorithms.stackqueue.ArrayQueue;
import com.hyildizoglu.algorithms.stackqueue.ArrayStack;
import com.hyildizoglu.algorithms.text.KMPMatcher;
import com.hyildizoglu.models.Expense;

/**
 * Service layer for expense logging and management operations.
 * Provides CRUD operations, undo functionality, scheduled payments,
 * and expense searching capabilities.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class ExpenseService {

	/** Repository for expense data access. */
	private final ExpenseFileRepository expenseRepository;

	/** Stack for undo operations (stores expense IDs). */
	private final ArrayStack<Integer> undoStack = new ArrayStack<>();

	/** Queue for scheduled/planned payments. */
	private final ArrayQueue<Expense> plannedPayments = new ArrayQueue<>();

	/**
	 * Creates a new ExpenseService with the specified repository.
	 * 
	 * @param expenseRepository The repository for expense data access
	 */
	public ExpenseService(ExpenseFileRepository expenseRepository) {
		this.expenseRepository = expenseRepository;
	}

	/**
	 * Logs a new expense and adds it to the undo stack.
	 * 
	 * @param userId      The user ID
	 * @param budgetId    The budget ID
	 * @param categoryId  The category ID
	 * @param amount      Expense amount
	 * @param date        Expense date
	 * @param description Description of the expense
	 * @return The saved expense with assigned ID
	 */
	public Expense logExpense(int userId, int budgetId, int categoryId, BigDecimal amount, LocalDate date,
			String description) {
		Expense expense = new Expense(0, userId, budgetId, categoryId, amount, date, description);
		Expense saved = expenseRepository.save(expense);
		undoStack.push(saved.getId());
		return saved;
	}

	/**
	 * Undoes the last logged expense by deleting it.
	 * 
	 * @return The deleted expense, or null if undo stack is empty
	 */
	public Expense undoLastExpense() {
		if (undoStack.isEmpty()) {
			return null;
		}
		Integer lastId = undoStack.pop();
		Expense toDelete = expenseRepository.findById(lastId);
		if (toDelete != null && expenseRepository.deleteById(lastId)) {
			return toDelete;
		}
		return null;
	}

	/**
	 * Schedules a planned payment for future processing.
	 * The payment is added to a queue and processed when its due date arrives.
	 * 
	 * @param userId      The user ID
	 * @param budgetId    The budget ID
	 * @param categoryId  The category ID
	 * @param amount      Payment amount
	 * @param dueDate     Due date for the payment
	 * @param description Description of the payment
	 */
	public void schedulePlannedPayment(int userId, int budgetId, int categoryId, BigDecimal amount, LocalDate dueDate,
			String description) {
		Expense planned = new Expense(0, userId, budgetId, categoryId, amount, dueDate, description);
		plannedPayments.enqueue(planned);
	}

	/**
	 * Processes all planned payments due up to the specified date.
	 * Due payments are converted to actual expenses.
	 * 
	 * @param dateInclusive Process payments due on or before this date
	 * @return List of processed (saved) expenses
	 */
	public List<Expense> processPlannedPaymentsUpTo(LocalDate dateInclusive) {
		List<Expense> processed = new ArrayList<>();
		int initialSize = plannedPayments.size();
		for (int i = 0; i < initialSize; i++) {
			Expense e = plannedPayments.dequeue();
			if (e == null) {
				break;
			}
			if (!e.getDate().isAfter(dateInclusive)) {
				// Due, convert to actual expense
				Expense saved = logExpense(e.getUserId(), e.getBudgetId(), e.getCategoryId(), e.getAmount(),
						e.getDate(), e.getDescription());
				processed.add(saved);
			} else {
				// Not yet due, re-add to queue
				plannedPayments.enqueue(e);
			}
		}
		return processed;
	}

	/**
	 * Lists all expenses for a user.
	 * 
	 * @param userId The user ID
	 * @return List of user's expenses
	 */
	public List<Expense> listExpensesForUser(int userId) {
		return expenseRepository.findByUserId(userId);
	}

	/**
	 * Updates an existing expense.
	 * 
	 * @param expenseId   The expense ID to update
	 * @param budgetId    New budget ID
	 * @param categoryId  New category ID
	 * @param amount      New amount
	 * @param date        New date
	 * @param description New description
	 * @return Updated expense, or null if not found
	 */
	public Expense updateExpense(int expenseId, int budgetId, int categoryId, BigDecimal amount, LocalDate date,
			String description) {
		Expense existing = expenseRepository.findById(expenseId);
		if (existing != null) {
			Expense updated = new Expense(expenseId, existing.getUserId(), budgetId, categoryId, amount, date,
					description);
			return expenseRepository.update(updated);
		}
		return null;
	}

	/**
	 * Deletes an expense.
	 * 
	 * @param expenseId The expense ID to delete
	 * @return true if deleted, false if not found
	 */
	public boolean deleteExpense(int expenseId) {
		return expenseRepository.deleteById(expenseId);
	}

	/**
	 * Searches expenses by description keyword using KMP algorithm.
	 * 
	 * @param userId  The user ID
	 * @param keyword Keyword to search for (case-insensitive)
	 * @return List of matching expenses
	 */
	public List<Expense> searchExpensesByDescription(int userId, String keyword) {
		String pattern = keyword == null ? "" : keyword.toLowerCase();
		List<Expense> all = listExpensesForUser(userId);
		if (pattern.isEmpty()) {
			return all;
		}
		List<Expense> result = new ArrayList<>();
		for (Expense e : all) {
			String desc = e.getDescription() == null ? "" : e.getDescription().toLowerCase();
			if (KMPMatcher.contains(desc, pattern)) {
				result.add(e);
			}
		}
		return result;
	}

	/**
	 * Calculates total spending for a category hierarchy.
	 * Uses BFS traversal to include all sub-categories.
	 * 
	 * @param userId         The user ID
	 * @param rootCategoryId Root category ID
	 * @param graph          Category hierarchy graph
	 * @return Total spending across the category hierarchy
	 */
	public BigDecimal sumExpensesForCategoryHierarchy(int userId, int rootCategoryId, CategoryGraph graph) {
		List<Integer> allCategories = GraphTraversal.bfs(graph, rootCategoryId);
		BigDecimal total = BigDecimal.ZERO;
		for (Expense e : listExpensesForUser(userId)) {
			if (allCategories.contains(e.getCategoryId())) {
				total = total.add(e.getAmount());
			}
		}
		return total;
	}
}
