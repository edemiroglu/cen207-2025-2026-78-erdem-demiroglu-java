package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.matrix.SparseMatrix;
import com.hyildizoglu.budgetCreation.BudgetFileRepository;
import com.hyildizoglu.budgetCreation.BudgetService;
import com.hyildizoglu.models.Budget;

@DisplayName("BudgetService Tests")
class BudgetServiceTest {

	private BudgetService budgetService;
	private BudgetFileRepository repository;
	private Path testFile;

	@BeforeEach
	void setUp() throws Exception {
		testFile = Files.createTempFile("test_budget", ".dat");
		repository = new BudgetFileRepository(testFile);
		budgetService = new BudgetService(repository);
	}

	@AfterEach
	void tearDown() throws Exception {
		Files.deleteIfExists(testFile);
	}

	@Test
	@DisplayName("Budget creation should succeed")
	void testCreateBudget_Success() {
		Budget budget = budgetService.createBudget(1, "Test Budget", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

		assertNotNull(budget);
		assertTrue(budget.getId() > 0);
		assertEquals("Test Budget", budget.getName());
		assertEquals(new BigDecimal("1000"), budget.getTotalLimit());
		assertEquals(1, budget.getUserId());
	}

	@Test
	@DisplayName("Budget creation with valid date range")
	void testCreateBudget_ValidDateRange() {
		LocalDate start = LocalDate.of(2025, 1, 1);
		LocalDate end = LocalDate.of(2025, 1, 31);
		Budget budget = budgetService.createBudget(1, "Test", new BigDecimal("500"), start, end);

		assertNotNull(budget);
		assertEquals(start, budget.getStartDate());
		assertEquals(end, budget.getEndDate());
	}

	@Test
	@DisplayName("List budgets for user")
	void testListBudgetsForUser() {
		budgetService.createBudget(1, "Budget 1", new BigDecimal("1000"), LocalDate.of(2025, 1, 1),
				LocalDate.of(2025, 1, 31));
		budgetService.createBudget(1, "Budget 2", new BigDecimal("2000"), LocalDate.of(2025, 2, 1),
				LocalDate.of(2025, 2, 28));
		budgetService.createBudget(2, "Budget 3", new BigDecimal("3000"), LocalDate.of(2025, 1, 1),
				LocalDate.of(2025, 1, 31));

		List<Budget> budgets = budgetService.listBudgetsForUser(1);
		assertEquals(2, budgets.size());
	}

	@Test
	@DisplayName("Budget update should succeed")
	void testUpdateBudget_Success() {
		Budget created = budgetService.createBudget(1, "Original", new BigDecimal("500"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

		Budget updated = budgetService.updateBudget(created.getId(), "Updated", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

		assertNotNull(updated);
		assertEquals("Updated", updated.getName());
		assertEquals(new BigDecimal("1000"), updated.getTotalLimit());
		assertEquals(created.getId(), updated.getId());
	}

	@Test
	@DisplayName("Update non-existent budget should return null")
	void testUpdateBudget_NotFound() {
		Budget updated = budgetService.updateBudget(999, "Test", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

		assertNull(updated);
	}

	@Test
	@DisplayName("Budget deletion should succeed")
	void testDeleteBudget_Success() {
		Budget created = budgetService.createBudget(1, "Test", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

		boolean deleted = budgetService.deleteBudget(created.getId());
		assertTrue(deleted);

		List<Budget> budgets = budgetService.listBudgetsForUser(1);
		assertEquals(0, budgets.size());
	}

	@Test
	@DisplayName("Delete non-existent budget should return false")
	void testDeleteBudget_NotFound() {
		boolean deleted = budgetService.deleteBudget(999);
		assertFalse(deleted);
	}

	@Test
	@DisplayName("Build budget matrix for month")
	void testBuildBudgetMatrixForMonth() {
		budgetService.createBudget(1, "Test", new BigDecimal("3100"), LocalDate.of(2025, 1, 1),
				LocalDate.of(2025, 1, 31));

		YearMonth month = YearMonth.of(2025, 1);
		SparseMatrix matrix = budgetService.buildBudgetMatrixForMonth(1, month);

		assertNotNull(matrix);
		// Budget should exist for each day (3100 / 31 ≈ 100 per day)
		BigDecimal day1Budget = matrix.rowSum(1);
		assertTrue(day1Budget.compareTo(BigDecimal.ZERO) > 0);
	}

	@Test
	@DisplayName("Build budget matrix for month without overlap")
	void testBuildBudgetMatrixForMonth_NoOverlap() {
		budgetService.createBudget(1, "Test", new BigDecimal("1000"), LocalDate.of(2025, 2, 1),
				LocalDate.of(2025, 2, 28));

		YearMonth month = YearMonth.of(2025, 1);
		SparseMatrix matrix = budgetService.buildBudgetMatrixForMonth(1, month);

		assertNotNull(matrix);
		// No budget in January
		BigDecimal day1Budget = matrix.rowSum(1);
		assertEquals(0, day1Budget.compareTo(BigDecimal.ZERO));
	}
}
