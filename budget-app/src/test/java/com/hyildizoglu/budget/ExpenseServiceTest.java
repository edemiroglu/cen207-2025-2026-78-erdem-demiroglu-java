package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.expenseLogging.ExpenseFileRepository;
import com.hyildizoglu.expenseLogging.ExpenseService;
import com.hyildizoglu.models.Expense;

@DisplayName("ExpenseService Tests")
class ExpenseServiceTest {

	private ExpenseService expenseService;
	private ExpenseFileRepository repository;
	private Path testFile;

	@BeforeEach
	void setUp() throws Exception {
		testFile = Files.createTempFile("test_expense", ".dat");
		repository = new ExpenseFileRepository(testFile);
		expenseService = new ExpenseService(repository);
	}

	@AfterEach
	void tearDown() throws Exception {
		Files.deleteIfExists(testFile);
	}

	@Test
	@DisplayName("Expense logging should succeed")
	void testLogExpense_Success() {
		Expense expense = expenseService.logExpense(1, 1, 1, new BigDecimal("100.50"),
				LocalDate.of(2025, 1, 15), "Test expense");

		assertNotNull(expense);
		assertTrue(expense.getId() > 0);
		assertEquals(new BigDecimal("100.50"), expense.getAmount());
		assertEquals("Test expense", expense.getDescription());
	}

	@Test
	@DisplayName("Log expense with normal amount")
	void testLogExpense_NormalAmount() {
		Expense expense = expenseService.logExpense(1, 1, 1, new BigDecimal("50.00"),
				LocalDate.of(2025, 1, 15), "Test");

		assertNotNull(expense);
	}

	@Test
	@DisplayName("List expenses for user")
	void testListExpensesForUser() {
		expenseService.logExpense(1, 1, 1, new BigDecimal("100"), LocalDate.of(2025, 1, 15), "Expense 1");
		expenseService.logExpense(1, 1, 2, new BigDecimal("200"), LocalDate.of(2025, 1, 16), "Expense 2");
		expenseService.logExpense(2, 1, 1, new BigDecimal("300"), LocalDate.of(2025, 1, 17), "Expense 3");

		List<Expense> expenses = expenseService.listExpensesForUser(1);
		assertEquals(2, expenses.size());
	}

	@Test
	@DisplayName("Expense update should succeed")
	void testUpdateExpense_Success() {
		Expense created = expenseService.logExpense(1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Original");

		Expense updated = expenseService.updateExpense(created.getId(), 1, 2, new BigDecimal("200"),
				LocalDate.of(2025, 1, 20), "Updated");

		assertNotNull(updated);
		assertEquals(new BigDecimal("200"), updated.getAmount());
		assertEquals("Updated", updated.getDescription());
		assertEquals(2, updated.getCategoryId());
	}

	@Test
	@DisplayName("Update non-existent expense should return null")
	void testUpdateExpense_NotFound() {
		Expense updated = expenseService.updateExpense(999, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Test");

		assertNull(updated);
	}

	@Test
	@DisplayName("Expense deletion should succeed")
	void testDeleteExpense_Success() {
		Expense created = expenseService.logExpense(1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Test");

		boolean deleted = expenseService.deleteExpense(created.getId());
		assertTrue(deleted);

		List<Expense> expenses = expenseService.listExpensesForUser(1);
		assertEquals(0, expenses.size());
	}

	@Test
	@DisplayName("Delete non-existent expense should return false")
	void testDeleteExpense_NotFound() {
		boolean deleted = expenseService.deleteExpense(999);
		assertFalse(deleted);
	}

	@Test
	@DisplayName("Undo last expense should succeed")
	void testUndoLastExpense_Success() {
		Expense expense1 = expenseService.logExpense(1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Expense 1");
		Expense expense2 = expenseService.logExpense(1, 1, 1, new BigDecimal("200"),
				LocalDate.of(2025, 1, 16), "Expense 2");

		Expense undone = expenseService.undoLastExpense();
		assertNotNull(undone);
		assertEquals(expense2.getId(), undone.getId());

		List<Expense> expenses = expenseService.listExpensesForUser(1);
		assertEquals(1, expenses.size());
	}

	@Test
	@DisplayName("Undo on empty stack should return null")
	void testUndoLastExpense_EmptyStack() {
		Expense undone = expenseService.undoLastExpense();
		assertNull(undone);
	}

	@Test
	@DisplayName("Schedule planned payment")
	void testSchedulePlannedPayment() {
		expenseService.schedulePlannedPayment(1, 1, 1, new BigDecimal("500"),
				LocalDate.of(2025, 2, 1), "Planned payment");

		// Planned payment should be added to queue
		// Cannot test directly as it's private, but can test via processPlannedPaymentsUpTo
		assertDoesNotThrow(() -> expenseService.schedulePlannedPayment(1, 1, 1, 
				new BigDecimal("100"), LocalDate.of(2025, 3, 1), "Another payment"));
	}

	@Test
	@DisplayName("Process planned payments")
	void testProcessPlannedPaymentsUpTo() {
		expenseService.schedulePlannedPayment(1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 10), "Payment 1");
		expenseService.schedulePlannedPayment(1, 1, 1, new BigDecimal("200"),
				LocalDate.of(2025, 1, 20), "Payment 2");
		expenseService.schedulePlannedPayment(1, 1, 1, new BigDecimal("300"),
				LocalDate.of(2025, 2, 1), "Payment 3");

		List<Expense> processed = expenseService.processPlannedPaymentsUpTo(LocalDate.of(2025, 1, 15));
		assertEquals(1, processed.size());
		assertEquals("Payment 1", processed.get(0).getDescription());

		// Remaining planned payments can be verified
		List<Expense> allExpenses = expenseService.listExpensesForUser(1);
		assertTrue(allExpenses.size() >= 1);
	}

	@Test
	@DisplayName("Search expenses by description")
	void testSearchExpensesByDescription() {
		expenseService.logExpense(1, 1, 1, new BigDecimal("100"), LocalDate.of(2025, 1, 15), "Grocery shopping");
		expenseService.logExpense(1, 1, 1, new BigDecimal("200"), LocalDate.of(2025, 1, 16), "Gas station");
		expenseService.logExpense(1, 1, 1, new BigDecimal("300"), LocalDate.of(2025, 1, 17), "Grocery store");

		List<Expense> found = expenseService.searchExpensesByDescription(1, "grocery");
		assertEquals(2, found.size());
	}

	@Test
	@DisplayName("Search expenses - not found")
	void testSearchExpensesByDescription_NotFound() {
		expenseService.logExpense(1, 1, 1, new BigDecimal("100"), LocalDate.of(2025, 1, 15), "Test");

		List<Expense> found = expenseService.searchExpensesByDescription(1, "nonexistent");
		assertEquals(0, found.size());
	}

	@Test
	@DisplayName("Search expenses with empty keyword")
	void testSearchExpensesByDescription_EmptyKeyword() {
		expenseService.logExpense(1, 1, 1, new BigDecimal("100"), LocalDate.of(2025, 1, 15), "Test");

		List<Expense> found = expenseService.searchExpensesByDescription(1, "");
		assertTrue(found.size() >= 1);
	}
}
