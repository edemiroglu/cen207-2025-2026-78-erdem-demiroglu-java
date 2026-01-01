package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hyildizoglu.algorithms.graph.CategoryGraphService;
import com.hyildizoglu.budgetCreation.BudgetService;
import com.hyildizoglu.expenseLogging.ExpenseService;
import com.hyildizoglu.financialSummary.FinancialSummaryService;
import com.hyildizoglu.models.User;
import com.hyildizoglu.savingsGoal.GoalService;
import com.hyildizoglu.userAuthentication.UserAuthService;
import com.hyildizoglu.core.ConsoleUI;

/**
 * Tests for ConsoleUI exception handlers using Mockito.
 * These tests cover the generic "catch (Exception e)" blocks.
 */
@DisplayName("ConsoleUI Exception Handler Tests")
class ConsoleUIExceptionTest {

	@Mock private UserAuthService userAuthService;
	@Mock private BudgetService budgetService;
	@Mock private ExpenseService expenseService;
	@Mock private GoalService goalService;
	@Mock private FinancialSummaryService financialSummaryService;
	@Mock private CategoryGraphService categoryGraphService;

	private ByteArrayOutputStream outputStream;
	private PrintStream originalOut;
	private InputStream originalIn;
	private AutoCloseable mocks;

	@BeforeEach
	void setUp() {
		mocks = MockitoAnnotations.openMocks(this);
		outputStream = new ByteArrayOutputStream();
		originalOut = System.out;
		originalIn = System.in;
		System.setOut(new PrintStream(outputStream));
	}

	@AfterEach
	void tearDown() throws Exception {
		System.setOut(originalOut);
		System.setIn(originalIn);
		mocks.close();
	}

	private void setInput(String input) {
		System.setIn(new ByteArrayInputStream(input.getBytes()));
	}

	private ConsoleUI createConsoleUI() {
		return new ConsoleUI(userAuthService, budgetService, expenseService, 
			goalService, financialSummaryService, categoryGraphService);
	}

	private User createMockUser() {
		User user = new User(1, "testuser", "password");
		return user;
	}

	// ==================== LOGIN EXCEPTION TESTS ====================

	@Test
	@DisplayName("Login with unexpected exception should be handled")
	void testLogin_UnexpectedException() {
		when(userAuthService.login(anyString(), anyString()))
			.thenThrow(new RuntimeException("Unexpected database error"));
		
		setInput("1\nvaliduser\nvalidpassword123\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Unexpected"));
	}

	// ==================== REGISTER EXCEPTION TESTS ====================

	@Test
	@DisplayName("Register with unexpected exception should be handled")
	void testRegister_UnexpectedException() {
		when(userAuthService.register(anyString(), anyString()))
			.thenThrow(new RuntimeException("Database connection failed"));
		
		setInput("2\nvaliduser\nvalidpassword123\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Unexpected"));
	}

	// ==================== BUDGET MENU EXCEPTION TESTS ====================

	@Test
	@DisplayName("Create budget with unexpected exception should be handled")
	void testBudgetMenu_CreateUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(budgetService.createBudget(anyInt(), anyString(), any(BigDecimal.class), 
			any(LocalDate.class), any(LocalDate.class)))
			.thenThrow(new RuntimeException("File write error"));
		
		setInput("3\n1\n1\nTestBudget\n1000\n2025-01-01\n2025-12-31\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	@Test
	@DisplayName("Update budget with unexpected exception should be handled")
	void testBudgetMenu_UpdateUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(budgetService.updateBudget(anyInt(), anyString(), any(BigDecimal.class), 
			any(LocalDate.class), any(LocalDate.class)))
			.thenThrow(new RuntimeException("Concurrent modification"));
		
		setInput("3\n1\n3\n1\nUpdated\n2000\n2025-02-01\n2025-11-30\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	@Test
	@DisplayName("Delete budget with unexpected exception should be handled")
	void testBudgetMenu_DeleteUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(budgetService.deleteBudget(anyInt()))
			.thenThrow(new RuntimeException("Cannot delete"));
		
		setInput("3\n1\n4\n1\ny\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	@Test
	@DisplayName("Budget matrix with unexpected exception should be handled")
	void testBudgetMenu_MatrixUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(budgetService.buildBudgetMatrixForMonth(anyInt(), any()))
			.thenThrow(new RuntimeException("Matrix calculation error"));
		
		setInput("3\n1\n5\n2025\n6\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	// ==================== EXPENSE MENU EXCEPTION TESTS ====================

	@Test
	@DisplayName("Log expense with unexpected exception should be handled")
	void testExpenseMenu_LogUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(expenseService.logExpense(anyInt(), anyInt(), anyInt(), 
			any(BigDecimal.class), any(LocalDate.class), anyString()))
			.thenThrow(new RuntimeException("Storage full"));
		
		setInput("3\n2\n1\n1\n1\n50.00\n2025-06-15\nTest\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	@Test
	@DisplayName("Update expense with unexpected exception should be handled")
	void testExpenseMenu_UpdateUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(expenseService.updateExpense(anyInt(), anyInt(), anyInt(), 
			any(BigDecimal.class), any(LocalDate.class), anyString()))
			.thenThrow(new RuntimeException("Update failed"));
		
		setInput("3\n2\n3\n1\n1\n1\n100\n2025-06-15\nUpdated\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	@Test
	@DisplayName("Delete expense with unexpected exception should be handled")
	void testExpenseMenu_DeleteUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(expenseService.deleteExpense(anyInt()))
			.thenThrow(new RuntimeException("Delete failed"));
		
		setInput("3\n2\n4\n1\ny\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	@Test
	@DisplayName("Schedule payment with unexpected exception should be handled")
	void testExpenseMenu_ScheduleUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		doThrow(new RuntimeException("Schedule failed"))
			.when(expenseService).schedulePlannedPayment(anyInt(), anyInt(), anyInt(), 
				any(BigDecimal.class), any(LocalDate.class), anyString());
		
		setInput("3\n2\n6\n1\n1\n100\n2025-07-01\nScheduled\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	@Test
	@DisplayName("Process payments with unexpected exception should be handled")
	void testExpenseMenu_ProcessUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(expenseService.processPlannedPaymentsUpTo(any(LocalDate.class)))
			.thenThrow(new RuntimeException("Process failed"));
		
		setInput("3\n2\n7\n2025-12-31\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	// ==================== GOAL MENU EXCEPTION TESTS ====================

	@Test
	@DisplayName("Create goal with unexpected exception should be handled")
	void testGoalMenu_CreateUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(goalService.createGoal(anyInt(), anyString(), any(BigDecimal.class), 
			any(BigDecimal.class), any(LocalDate.class)))
			.thenThrow(new RuntimeException("Goal creation failed"));
		
		setInput("3\n3\n1\nGoal\n1000\n100\n2025-12-31\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	@Test
	@DisplayName("Update goal with unexpected exception should be handled")
	void testGoalMenu_UpdateUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(goalService.updateGoal(anyInt(), anyString(), any(BigDecimal.class), 
			any(BigDecimal.class), any(LocalDate.class)))
			.thenThrow(new RuntimeException("Update failed"));
		
		setInput("3\n3\n3\n1\nUpdated\n2000\n500\n2026-06-30\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	@Test
	@DisplayName("Delete goal with unexpected exception should be handled")
	void testGoalMenu_DeleteUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(goalService.deleteGoal(anyInt()))
			.thenThrow(new RuntimeException("Delete failed"));
		
		setInput("3\n3\n4\n1\ny\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	@Test
	@DisplayName("SCC analysis with unexpected exception should be handled")
	void testGoalMenu_SCCUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(goalService.analyzeGoalDependencies(any()))
			.thenThrow(new RuntimeException("Analysis failed"));
		
		setInput("3\n3\n5\n0\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("SCC") || output.contains("Error"));
	}

	// ==================== SUMMARY MENU EXCEPTION TESTS ====================

	@Test
	@DisplayName("Summary matrix with unexpected exception should be handled")
	void testSummaryMenu_MatrixUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(financialSummaryService.buildExpenseMatrixForMonth(anyInt(), any()))
			.thenThrow(new RuntimeException("Matrix error"));
		
		setInput("3\n4\n2\n2025\n6\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	@Test
	@DisplayName("Top N expenses with unexpected exception should be handled")
	void testSummaryMenu_TopNUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(financialSummaryService.topNExpenses(anyInt(), anyInt()))
			.thenThrow(new RuntimeException("Query failed"));
		
		setInput("3\n4\n3\n5\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	@Test
	@DisplayName("Top categories with unexpected exception should be handled")
	void testSummaryMenu_TopCategoriesUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(financialSummaryService.topNCategoriesBySpending(anyInt(), anyInt()))
			.thenThrow(new RuntimeException("Aggregation failed"));
		
		setInput("3\n4\n4\n3\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	@Test
	@DisplayName("Date range with unexpected exception should be handled")
	void testSummaryMenu_DateRangeUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(financialSummaryService.expensesInDateRange(anyInt(), any(LocalDate.class), any(LocalDate.class)))
			.thenThrow(new RuntimeException("Range query failed"));
		
		setInput("3\n4\n5\n2025-01-01\n2025-12-31\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	@Test
	@DisplayName("Budget vs actual with unexpected exception should be handled")
	void testSummaryMenu_BudgetVsActualUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(financialSummaryService.budgetVsActual(anyInt(), anyInt()))
			.thenThrow(new RuntimeException("Comparison failed"));
		
		setInput("3\n4\n6\n1\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	// ==================== CATEGORY GRAPH EXCEPTION TESTS ====================

	@Test
	@DisplayName("Add relation with unexpected exception should be handled")
	void testCategoryGraph_AddRelationUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		doThrow(new RuntimeException("Graph error"))
			.when(categoryGraphService).addCategoryRelation(anyInt(), anyInt());
		
		setInput("3\n5\n1\n1\n2\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	@Test
	@DisplayName("BFS traversal with unexpected exception should be handled")
	void testCategoryGraph_BFSUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(categoryGraphService.traverseCategoryHierarchyBFS(anyInt()))
			.thenThrow(new RuntimeException("BFS failed"));
		
		setInput("3\n5\n2\n1\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	@Test
	@DisplayName("DFS traversal with unexpected exception should be handled")
	void testCategoryGraph_DFSUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(categoryGraphService.traverseCategoryHierarchyDFS(anyInt()))
			.thenThrow(new RuntimeException("DFS failed"));
		
		setInput("3\n5\n3\n1\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}

	@Test
	@DisplayName("Hierarchy spending with unexpected exception should be handled")
	void testCategoryGraph_SpendingUnexpectedException() {
		User mockUser = createMockUser();
		when(userAuthService.createGuestUser()).thenReturn(mockUser);
		when(categoryGraphService.calculateCategoryHierarchySpending(anyInt(), anyInt()))
			.thenThrow(new RuntimeException("Calculation failed"));
		
		setInput("3\n5\n4\n1\n0\n0\n");
		ConsoleUI ui = createConsoleUI();
		ui.start();
		
		String output = outputStream.toString();
		assertTrue(output.contains("unexpected error") || output.contains("Error"));
	}
}



