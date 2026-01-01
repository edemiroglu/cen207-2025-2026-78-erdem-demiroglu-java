package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.matrix.SparseMatrix;
import com.hyildizoglu.budgetCreation.BudgetFileRepository;
import com.hyildizoglu.expenseLogging.ExpenseFileRepository;
import com.hyildizoglu.financialSummary.FinancialSummaryService;
import com.hyildizoglu.models.Expense;
import com.hyildizoglu.models.Goal;
import com.hyildizoglu.savingsGoal.GoalFileRepository;

@DisplayName("FinancialSummaryService Tests")
class FinancialSummaryServiceTest {

	private FinancialSummaryService service;
	private BudgetFileRepository budgetRepo;
	private ExpenseFileRepository expenseRepo;
	private GoalFileRepository goalRepo;
	private Path budgetFile;
	private Path expenseFile;
	private Path goalFile;

	@BeforeEach
	void setUp() throws Exception {
		budgetFile = Files.createTempFile("test_budget", ".dat");
		expenseFile = Files.createTempFile("test_expense", ".dat");
		goalFile = Files.createTempFile("test_goal", ".dat");

		budgetRepo = new BudgetFileRepository(budgetFile);
		expenseRepo = new ExpenseFileRepository(expenseFile);
		goalRepo = new GoalFileRepository(goalFile);

		service = new FinancialSummaryService(budgetRepo, expenseRepo, goalRepo);
	}

	@AfterEach
	void tearDown() throws Exception {
		Files.deleteIfExists(budgetFile);
		Files.deleteIfExists(expenseFile);
		Files.deleteIfExists(goalFile);
	}

	@Test
	@DisplayName("Calculate total expenses")
	void testCalculateTotalExpenses() {
		expenseRepo.save(new com.hyildizoglu.models.Expense(0, 1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Expense 1"));
		expenseRepo.save(new com.hyildizoglu.models.Expense(0, 1, 1, 1, new BigDecimal("200"),
				LocalDate.of(2025, 1, 16), "Expense 2"));

		BigDecimal total = service.calculateTotalExpenses(1);
		assertEquals(new BigDecimal("300"), total);
	}

	@Test
	@DisplayName("Total should be zero for empty expense list")
	void testCalculateTotalExpenses_Empty() {
		BigDecimal total = service.calculateTotalExpenses(1);
		assertEquals(BigDecimal.ZERO, total);
	}

	@Test
	@DisplayName("Calculate total budget limit")
	void testCalculateTotalBudgetLimit() {
		budgetRepo.save(new com.hyildizoglu.models.Budget(0, 1, "Budget 1", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)));
		budgetRepo.save(new com.hyildizoglu.models.Budget(0, 1, "Budget 2", new BigDecimal("2000"),
				LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28)));

		BigDecimal total = service.calculateTotalBudgetLimit(1);
		assertEquals(new BigDecimal("3000"), total);
	}

	@Test
	@DisplayName("Calculate remaining budget")
	void testCalculateRemainingBudget() {
		budgetRepo.save(new com.hyildizoglu.models.Budget(0, 1, "Budget", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)));
		expenseRepo.save(new com.hyildizoglu.models.Expense(0, 1, 1, 1, new BigDecimal("300"),
				LocalDate.of(2025, 1, 15), "Expense"));

		BigDecimal remaining = service.calculateRemainingBudget(1);
		assertEquals(new BigDecimal("700"), remaining);
	}

	@Test
	@DisplayName("Build expense matrix for month")
	void testBuildExpenseMatrixForMonth() {
		expenseRepo.save(new com.hyildizoglu.models.Expense(0, 1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Expense"));

		YearMonth month = YearMonth.of(2025, 1);
		SparseMatrix matrix = service.buildExpenseMatrixForMonth(1, month);

		assertNotNull(matrix);
		BigDecimal day15Expense = matrix.get(15, 1);
		assertEquals(new BigDecimal("100"), day15Expense);
	}

	@Test
	@DisplayName("Get top N expenses")
	void testTopNExpenses() {
		expenseRepo.save(new com.hyildizoglu.models.Expense(0, 1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Expense 1"));
		expenseRepo.save(new com.hyildizoglu.models.Expense(0, 1, 1, 1, new BigDecimal("500"),
				LocalDate.of(2025, 1, 16), "Expense 2"));
		expenseRepo.save(new com.hyildizoglu.models.Expense(0, 1, 1, 1, new BigDecimal("300"),
				LocalDate.of(2025, 1, 17), "Expense 3"));

		List<Expense> top = service.topNExpenses(1, 2);
		assertEquals(2, top.size());
		// Should have highest amount
		assertEquals(new BigDecimal("500"), top.get(0).getAmount());
	}

	@Test
	@DisplayName("Get top N categories by spending")
	void testTopNCategoriesBySpending() {
		expenseRepo.save(new com.hyildizoglu.models.Expense(0, 1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Expense 1"));
		expenseRepo.save(new com.hyildizoglu.models.Expense(0, 1, 1, 1, new BigDecimal("200"),
				LocalDate.of(2025, 1, 16), "Expense 2"));
		expenseRepo.save(new com.hyildizoglu.models.Expense(0, 1, 1, 2, new BigDecimal("500"),
				LocalDate.of(2025, 1, 17), "Expense 3"));

		List<Map.Entry<Integer, BigDecimal>> top = service.topNCategoriesBySpending(1, 2);
		assertEquals(2, top.size());
		// Category 2 should have highest spending
		assertEquals(Integer.valueOf(2), top.get(0).getKey());
	}

	@Test
	@DisplayName("Get expenses in date range")
	void testExpensesInDateRange() {
		expenseRepo.save(new com.hyildizoglu.models.Expense(0, 1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Expense 1"));
		expenseRepo.save(new com.hyildizoglu.models.Expense(0, 1, 1, 1, new BigDecimal("200"),
				LocalDate.of(2025, 1, 20), "Expense 2"));
		expenseRepo.save(new com.hyildizoglu.models.Expense(0, 1, 1, 1, new BigDecimal("300"),
				LocalDate.of(2025, 2, 1), "Expense 3"));

		LocalDate from = LocalDate.of(2025, 1, 1);
		LocalDate to = LocalDate.of(2025, 1, 31);
		List<Expense> inRange = service.expensesInDateRange(1, from, to);

		assertEquals(2, inRange.size());
	}

	@Test
	@DisplayName("Budget vs actual spending comparison")
	void testBudgetVsActual() {
		com.hyildizoglu.models.Budget budget = budgetRepo.save(new com.hyildizoglu.models.Budget(0, 1, "Test",
				new BigDecimal("1000"), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)));
		expenseRepo.save(new com.hyildizoglu.models.Expense(0, 1, budget.getId(), 1, new BigDecimal("300"),
				LocalDate.of(2025, 1, 15), "Expense"));

		Map<String, BigDecimal> comparison = service.budgetVsActual(1, budget.getId());

		assertFalse(comparison.isEmpty());
		assertEquals(new BigDecimal("1000"), comparison.get("budgetLimit"));
		assertEquals(new BigDecimal("300"), comparison.get("actualSpending"));
		assertEquals(new BigDecimal("700"), comparison.get("difference"));
	}

	@Test
	@DisplayName("Comparison for non-existent budget should be empty")
	void testBudgetVsActual_NotFound() {
		Map<String, BigDecimal> comparison = service.budgetVsActual(1, 999);
		assertTrue(comparison.isEmpty());
	}

	@Test
	@DisplayName("Savings progress")
	void testSavingsProgress() {
		goalRepo.save(new com.hyildizoglu.models.Goal(0, 1, "Goal 1", new BigDecimal("1000"),
				new BigDecimal("500"), LocalDate.of(2025, 12, 31)));
		goalRepo.save(new com.hyildizoglu.models.Goal(0, 1, "Goal 2", new BigDecimal("2000"),
				new BigDecimal("1000"), LocalDate.of(2025, 12, 31)));

		Map<Goal, Map<String, BigDecimal>> progress = service.savingsProgress(1);

		assertFalse(progress.isEmpty());
		assertEquals(2, progress.size());
	}

	@Test
	@DisplayName("Savings progress for empty goal list")
	void testSavingsProgress_Empty() {
		Map<Goal, Map<String, BigDecimal>> progress = service.savingsProgress(1);
		assertTrue(progress.isEmpty());
	}

	@Test
	@DisplayName("Sort expenses by amount descending")
	void testSortExpensesByAmountDescending() {
		expenseRepo.save(new com.hyildizoglu.models.Expense(0, 1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Expense 1"));
		expenseRepo.save(new com.hyildizoglu.models.Expense(0, 1, 1, 1, new BigDecimal("500"),
				LocalDate.of(2025, 1, 16), "Expense 2"));
		expenseRepo.save(new com.hyildizoglu.models.Expense(0, 1, 1, 1, new BigDecimal("300"),
				LocalDate.of(2025, 1, 17), "Expense 3"));

		List<Expense> sorted = service.sortExpensesByAmountDescending(1);

		assertEquals(3, sorted.size());
		assertEquals(new BigDecimal("500"), sorted.get(0).getAmount());
		assertEquals(new BigDecimal("300"), sorted.get(1).getAmount());
		assertEquals(new BigDecimal("100"), sorted.get(2).getAmount());
	}
}
