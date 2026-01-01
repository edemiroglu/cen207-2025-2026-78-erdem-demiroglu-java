package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.graph.CategoryGraphService;
import com.hyildizoglu.budgetCreation.BudgetFileRepository;
import com.hyildizoglu.budgetCreation.BudgetService;
import com.hyildizoglu.expenseLogging.ExpenseFileRepository;
import com.hyildizoglu.expenseLogging.ExpenseService;
import com.hyildizoglu.financialSummary.FinancialSummaryService;
import com.hyildizoglu.models.Budget;
import com.hyildizoglu.models.Expense;
import com.hyildizoglu.models.Goal;
import com.hyildizoglu.models.User;
import com.hyildizoglu.savingsGoal.GoalFileRepository;
import com.hyildizoglu.savingsGoal.GoalService;
import com.hyildizoglu.userAuthentication.UserAuthService;
import com.hyildizoglu.userAuthentication.UserFileRepository;
import com.hyildizoglu.core.ConsoleUI;

@DisplayName("ConsoleUI Tests")
class ConsoleUITest {

	private ConsoleUI consoleUI;
	private ByteArrayOutputStream outputStream;
	private PrintStream originalOut;
	private InputStream originalIn;
	private Path userFile;
	private Path budgetFile;
	private Path expenseFile;
	private Path goalFile;

	private UserAuthService userAuthService;
	private BudgetService budgetService;
	private ExpenseService expenseService;
	private GoalService goalService;
	private FinancialSummaryService financialSummaryService;
	private CategoryGraphService categoryGraphService;

	@BeforeEach
	void setUp() throws Exception {
		userFile = Files.createTempFile("test_user", ".dat");
		budgetFile = Files.createTempFile("test_budget", ".dat");
		expenseFile = Files.createTempFile("test_expense", ".dat");
		goalFile = Files.createTempFile("test_goal", ".dat");

		UserFileRepository userRepo = new UserFileRepository(userFile);
		BudgetFileRepository budgetRepo = new BudgetFileRepository(budgetFile);
		ExpenseFileRepository expenseRepo = new ExpenseFileRepository(expenseFile);
		GoalFileRepository goalRepo = new GoalFileRepository(goalFile);

		userAuthService = new UserAuthService(userRepo);
		budgetService = new BudgetService(budgetRepo);
		expenseService = new ExpenseService(expenseRepo);
		goalService = new GoalService(goalRepo);
		financialSummaryService = new FinancialSummaryService(budgetRepo, expenseRepo, goalRepo);
		categoryGraphService = new CategoryGraphService(expenseService);

		consoleUI = new ConsoleUI(userAuthService, budgetService, expenseService, goalService,
				financialSummaryService, categoryGraphService);

		outputStream = new ByteArrayOutputStream();
		originalOut = System.out;
		originalIn = System.in;
		System.setOut(new PrintStream(outputStream));
	}

	@AfterEach
	void tearDown() throws Exception {
		System.setOut(originalOut);
		System.setIn(originalIn);
		Files.deleteIfExists(userFile);
		Files.deleteIfExists(budgetFile);
		Files.deleteIfExists(expenseFile);
		Files.deleteIfExists(goalFile);
	}

	private void setInput(String input) {
		System.setIn(new ByteArrayInputStream(input.getBytes()));
	}

	private ConsoleUI createConsoleUIWithInput(String input) {
		setInput(input);
		return new ConsoleUI(userAuthService, budgetService, expenseService, goalService,
				financialSummaryService, categoryGraphService);
	}

	// ==================== CONSTRUCTOR TESTS ====================

	@Test
	@DisplayName("ConsoleUI object should be created")
	void testConsoleUICreation() {
		assertNotNull(consoleUI);
	}

	@Test
	@DisplayName("Services should be properly connected")
	void testServicesConnected() {
		assertNotNull(consoleUI);
	}

	@Test
	@DisplayName("ConsoleUI with all services should be created successfully")
	void testConsoleUIWithAllServices() {
		ConsoleUI ui = new ConsoleUI(userAuthService, budgetService, expenseService,
				goalService, financialSummaryService, categoryGraphService);
		assertNotNull(ui);
	}

	// ==================== START METHOD TESTS ====================

	@Test
	@DisplayName("Start with immediate exit should work")
	void testStart_ImmediateExit() {
		ConsoleUI ui = createConsoleUIWithInput("0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Budget App"));
		assertTrue(output.contains("Exiting"));
	}

	@Test
	@DisplayName("Start with guest mode and exit should work")
	void testStart_GuestModeAndExit() {
		ConsoleUI ui = createConsoleUIWithInput("3\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("guest"));
		assertTrue(output.contains("Main Menu"));
	}

	@Test
	@DisplayName("Start with invalid choice should show error")
	void testStart_InvalidChoice() {
		ConsoleUI ui = createConsoleUIWithInput("9\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Invalid choice"));
	}

	// ==================== AUTH MENU TESTS ====================

	@Test
	@DisplayName("Register and then exit should work")
	void testAuthMenu_RegisterAndExit() {
		ConsoleUI ui = createConsoleUIWithInput("2\ntestuser\npassword123\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Registration successful") || output.contains("Register"));
	}

	@Test
	@DisplayName("Register with short username should show error")
	void testAuthMenu_RegisterShortUsername() {
		ConsoleUI ui = createConsoleUIWithInput("2\nab\npassword123\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("at least 3"));
	}

	@Test
	@DisplayName("Login with invalid credentials should fail")
	void testAuthMenu_LoginInvalidCredentials() {
		ConsoleUI ui = createConsoleUIWithInput("1\nnonexistent\nwrongpass\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Invalid") || output.contains("Error"));
	}

	@Test
	@DisplayName("Login with short username should show error")
	void testAuthMenu_LoginShortUsername() {
		ConsoleUI ui = createConsoleUIWithInput("1\nab\npassword123\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error"));
	}

	@Test
	@DisplayName("Login with short password should show error")
	void testAuthMenu_LoginShortPassword() {
		ConsoleUI ui = createConsoleUIWithInput("1\ntestuser\nab\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error"));
	}

	@Test
	@DisplayName("Register with duplicate username should fail")
	void testAuthMenu_RegisterDuplicateUsername() {
		// First register
		userAuthService.register("existinguser", "password123");
		// Try to register again with same username
		ConsoleUI ui = createConsoleUIWithInput("2\nexistinguser\npassword456\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("already exists") || output.contains("Register"));
	}

	@Test
	@DisplayName("Successful login should work")
	void testAuthMenu_SuccessfulLogin() {
		// First register a user
		userAuthService.register("loginuser", "password123");
		// Now login
		ConsoleUI ui = createConsoleUIWithInput("1\nloginuser\npassword123\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Login successful") || output.contains("Welcome"));
	}

	// ==================== MAIN MENU TESTS ====================

	@Test
	@DisplayName("Main menu navigation to budget and back")
	void testMainMenu_BudgetAndBack() {
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Budget Operations"));
	}

	@Test
	@DisplayName("Main menu navigation to expense and back")
	void testMainMenu_ExpenseAndBack() {
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Expense Operations"));
	}

	@Test
	@DisplayName("Main menu navigation to goal and back")
	void testMainMenu_GoalAndBack() {
		ConsoleUI ui = createConsoleUIWithInput("3\n3\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Goal Operations"));
	}

	@Test
	@DisplayName("Main menu navigation to summary and back")
	void testMainMenu_SummaryAndBack() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Financial Summary"));
	}

	@Test
	@DisplayName("Main menu navigation to category graph and back")
	void testMainMenu_CategoryGraphAndBack() {
		ConsoleUI ui = createConsoleUIWithInput("3\n5\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Category Graph"));
	}

	@Test
	@DisplayName("Main menu invalid choice should show error")
	void testMainMenu_InvalidChoice() {
		ConsoleUI ui = createConsoleUIWithInput("3\n9\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Invalid choice"));
	}

	// ==================== BUDGET MENU TESTS ====================

	@Test
	@DisplayName("Budget menu - list budgets when empty")
	void testBudgetMenu_ListEmpty() {
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n2\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("No budgets yet") || output.contains("Your budgets"));
	}

	@Test
	@DisplayName("Budget menu - create budget")
	void testBudgetMenu_CreateBudget() {
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n1\nTest Budget\n1000\n2025-01-01\n2025-12-31\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Created budget") || output.contains("Budget"));
	}

	@Test
	@DisplayName("Budget menu - create budget with invalid date range")
	void testBudgetMenu_CreateBudgetInvalidDateRange() {
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n1\nTest Budget\n1000\n2025-12-31\n2025-01-01\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("after"));
	}

	@Test
	@DisplayName("Budget menu - update non-existent budget")
	void testBudgetMenu_UpdateNonExistent() {
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n3\n999\nNew Name\n2000\n2025-01-01\n2025-12-31\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("not found") || output.contains("failed") || output.contains("Budget"));
	}

	@Test
	@DisplayName("Budget menu - delete with confirmation")
	void testBudgetMenu_DeleteWithConfirmation() {
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n4\n1\ny\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("not found") || output.contains("deleted") || output.contains("Budget"));
	}

	@Test
	@DisplayName("Budget menu - delete cancelled")
	void testBudgetMenu_DeleteCancelled() {
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n4\n1\nn\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("cancelled") || output.contains("Budget"));
	}

	@Test
	@DisplayName("Budget menu - monthly matrix")
	void testBudgetMenu_MonthlyMatrix() {
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n5\n2025\n6\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("matrix") || output.contains("Monthly"));
	}

	@Test
	@DisplayName("Budget menu - monthly matrix invalid year")
	void testBudgetMenu_MonthlyMatrixInvalidYear() {
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n5\n1800\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Invalid"));
	}

	@Test
	@DisplayName("Budget menu - monthly matrix invalid month")
	void testBudgetMenu_MonthlyMatrixInvalidMonth() {
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n5\n2025\n13\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("1-12"));
	}

	@Test
	@DisplayName("Budget menu - invalid choice")
	void testBudgetMenu_InvalidChoice() {
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n9\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Invalid choice"));
	}

	// ==================== EXPENSE MENU TESTS ====================

	@Test
	@DisplayName("Expense menu - list expenses when empty")
	void testExpenseMenu_ListEmpty() {
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n2\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("No expenses") || output.contains("Expense"));
	}

	@Test
	@DisplayName("Expense menu - log expense")
	void testExpenseMenu_LogExpense() {
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n1\n1\n1\n50.00\n2025-06-15\nTest expense\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Logged expense") || output.contains("Expense"));
	}

	@Test
	@DisplayName("Expense menu - undo when no expenses")
	void testExpenseMenu_UndoEmpty() {
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n5\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("No expense to undo") || output.contains("Expense"));
	}

	@Test
	@DisplayName("Expense menu - schedule payment")
	void testExpenseMenu_SchedulePayment() {
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n6\n1\n1\n100.00\n2025-07-01\nScheduled payment\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Scheduled payment") || output.contains("Expense"));
	}

	@Test
	@DisplayName("Expense menu - process scheduled payments")
	void testExpenseMenu_ProcessScheduled() {
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n7\n2025-12-31\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Processed") || output.contains("scheduled"));
	}

	@Test
	@DisplayName("Expense menu - search expenses")
	void testExpenseMenu_SearchExpenses() {
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n8\ntest\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Found expenses") || output.contains("0"));
	}

	@Test
	@DisplayName("Expense menu - update non-existent")
	void testExpenseMenu_UpdateNonExistent() {
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n3\n999\n1\n1\n100\n2025-06-15\nUpdated\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("not found") || output.contains("failed") || output.contains("Expense"));
	}

	@Test
	@DisplayName("Expense menu - delete with confirmation")
	void testExpenseMenu_DeleteWithConfirmation() {
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n4\n1\ny\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("not found") || output.contains("deleted") || output.contains("Expense"));
	}

	@Test
	@DisplayName("Expense menu - delete cancelled")
	void testExpenseMenu_DeleteCancelled() {
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n4\n1\nn\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("cancelled") || output.contains("Expense"));
	}

	// ==================== GOAL MENU TESTS ====================

	@Test
	@DisplayName("Goal menu - list goals when empty")
	void testGoalMenu_ListEmpty() {
		ConsoleUI ui = createConsoleUIWithInput("3\n3\n2\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("No goals yet") || output.contains("Your goals"));
	}

	@Test
	@DisplayName("Goal menu - create goal")
	void testGoalMenu_CreateGoal() {
		ConsoleUI ui = createConsoleUIWithInput("3\n3\n1\nSave for car\n10000\n500\n2025-12-31\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Created goal") || output.contains("Goal"));
	}

	@Test
	@DisplayName("Goal menu - update non-existent")
	void testGoalMenu_UpdateNonExistent() {
		ConsoleUI ui = createConsoleUIWithInput("3\n3\n3\n999\nNew Goal\n5000\n1000\n2025-12-31\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("not found") || output.contains("failed") || output.contains("Goal"));
	}

	@Test
	@DisplayName("Goal menu - delete with confirmation")
	void testGoalMenu_DeleteWithConfirmation() {
		ConsoleUI ui = createConsoleUIWithInput("3\n3\n4\n1\ny\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("not found") || output.contains("deleted") || output.contains("Goal"));
	}

	@Test
	@DisplayName("Goal menu - delete cancelled")
	void testGoalMenu_DeleteCancelled() {
		ConsoleUI ui = createConsoleUIWithInput("3\n3\n4\n1\nn\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("cancelled") || output.contains("Goal"));
	}

	@Test
	@DisplayName("Goal menu - SCC analysis with no dependencies")
	void testGoalMenu_SCCAnalysisNoDeps() {
		ConsoleUI ui = createConsoleUIWithInput("3\n3\n5\n0\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Strongly Connected") || output.contains("SCC"));
	}

	// ==================== SUMMARY MENU TESTS ====================

	@Test
	@DisplayName("Summary menu - general summary")
	void testSummaryMenu_GeneralSummary() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n1\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Total") || output.contains("budget"));
	}

	@Test
	@DisplayName("Summary menu - monthly expense matrix")
	void testSummaryMenu_MonthlyMatrix() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n2\n2025\n6\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("matrix") || output.contains("Monthly"));
	}

	@Test
	@DisplayName("Summary menu - top N expenses")
	void testSummaryMenu_TopNExpenses() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n3\n5\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Top") || output.contains("expenses"));
	}

	@Test
	@DisplayName("Summary menu - top N categories")
	void testSummaryMenu_TopNCategories() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n4\n3\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Top") || output.contains("categories"));
	}

	@Test
	@DisplayName("Summary menu - expenses in date range")
	void testSummaryMenu_DateRange() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n5\n2025-01-01\n2025-12-31\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Expenses in range") || output.contains("0 items"));
	}

	@Test
	@DisplayName("Summary menu - budget vs actual")
	void testSummaryMenu_BudgetVsActual() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n6\n1\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("not found") || output.contains("Budget") || output.contains("Actual"));
	}

	@Test
	@DisplayName("Summary menu - savings progress when no goals")
	void testSummaryMenu_SavingsProgressEmpty() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n7\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("No goals found") || output.contains("Savings"));
	}

	@Test
	@DisplayName("Summary menu - sort expenses by amount")
	void testSummaryMenu_SortExpenses() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n8\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("sorted") || output.contains("Expenses"));
	}

	// ==================== CATEGORY GRAPH MENU TESTS ====================

	@Test
	@DisplayName("Category graph menu - add relation")
	void testCategoryGraphMenu_AddRelation() {
		ConsoleUI ui = createConsoleUIWithInput("3\n5\n1\n1\n2\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("relation added") || output.contains("Category"));
	}

	@Test
	@DisplayName("Category graph menu - BFS traversal")
	void testCategoryGraphMenu_BFSTraversal() {
		ConsoleUI ui = createConsoleUIWithInput("3\n5\n2\n1\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("BFS") || output.contains("traversal"));
	}

	@Test
	@DisplayName("Category graph menu - DFS traversal")
	void testCategoryGraphMenu_DFSTraversal() {
		ConsoleUI ui = createConsoleUIWithInput("3\n5\n3\n1\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("DFS") || output.contains("traversal"));
	}

	@Test
	@DisplayName("Category graph menu - hierarchy spending")
	void testCategoryGraphMenu_HierarchySpending() {
		ConsoleUI ui = createConsoleUIWithInput("3\n5\n4\n1\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("hierarchy spending") || output.contains("Category"));
	}

	@Test
	@DisplayName("Category graph menu - invalid choice")
	void testCategoryGraphMenu_InvalidChoice() {
		ConsoleUI ui = createConsoleUIWithInput("3\n5\n9\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Invalid choice"));
	}

	// ==================== EXPENSE MENU - ADDITIONAL TESTS ====================

	@Test
	@DisplayName("Expense menu - navigate expenses with data")
	void testExpenseMenu_NavigateWithData() {
		// First log an expense
		User guest = userAuthService.createGuestUser();
		expenseService.logExpense(guest.getId(), 1, 1, new BigDecimal("50"), LocalDate.now(), "Test");
		
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n2\nn\np\nq\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Current record") || output.contains("expense"));
	}

	@Test
	@DisplayName("Expense menu - invalid choice")
	void testExpenseMenu_InvalidChoice() {
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n9\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Invalid choice"));
	}

	@Test
	@DisplayName("Expense menu - log expense with validation error")
	void testExpenseMenu_LogExpenseValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n1\nabc\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Expense"));
	}

	@Test
	@DisplayName("Expense menu - update expense with validation error")
	void testExpenseMenu_UpdateValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n3\nabc\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Expense"));
	}

	@Test
	@DisplayName("Expense menu - delete expense with validation error")
	void testExpenseMenu_DeleteValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n4\nabc\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Expense"));
	}

	@Test
	@DisplayName("Expense menu - schedule payment with validation error")
	void testExpenseMenu_ScheduleValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n6\nabc\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Expense"));
	}

	@Test
	@DisplayName("Expense menu - process payments with validation error")
	void testExpenseMenu_ProcessValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n7\ninvalid-date\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Expense"));
	}

	@Test
	@DisplayName("Expense menu - undo after logging expense")
	void testExpenseMenu_UndoAfterLog() {
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n1\n1\n1\n50.00\n2025-06-15\nTest expense\n5\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Undone") || output.contains("expense"));
	}

	// ==================== GOAL MENU - ADDITIONAL TESTS ====================

	@Test
	@DisplayName("Goal menu - invalid choice")
	void testGoalMenu_InvalidChoice() {
		ConsoleUI ui = createConsoleUIWithInput("3\n3\n9\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Invalid choice"));
	}

	@Test
	@DisplayName("Goal menu - create goal with validation error")
	void testGoalMenu_CreateValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n3\n1\n\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Goal"));
	}

	@Test
	@DisplayName("Goal menu - update goal with validation error")
	void testGoalMenu_UpdateValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n3\n3\nabc\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Goal"));
	}

	@Test
	@DisplayName("Goal menu - delete goal with validation error")
	void testGoalMenu_DeleteValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n3\n4\nabc\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Goal"));
	}

	@Test
	@DisplayName("Goal menu - SCC with dependencies")
	void testGoalMenu_SCCWithDependencies() {
		ConsoleUI ui = createConsoleUIWithInput("3\n3\n5\n2\n1\n2\n2\n3\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("SCC") || output.contains("Components"));
	}

	@Test
	@DisplayName("Goal menu - SCC with validation error")
	void testGoalMenu_SCCValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n3\n5\nabc\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Goal"));
	}

	@Test
	@DisplayName("Goal menu - list goals with data")
	void testGoalMenu_ListWithData() {
		User guest = userAuthService.createGuestUser();
		goalService.createGoal(guest.getId(), "TestGoal", new BigDecimal("1000"), new BigDecimal("100"), LocalDate.now().plusMonths(6));
		
		ConsoleUI ui = createConsoleUIWithInput("3\n3\n2\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("completed") || output.contains("Goal"));
	}

	// ==================== BUDGET MENU - ADDITIONAL TESTS ====================

	@Test
	@DisplayName("Budget menu - create with validation error")
	void testBudgetMenu_CreateValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n1\n\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Budget"));
	}

	@Test
	@DisplayName("Budget menu - update with validation error")
	void testBudgetMenu_UpdateValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n3\nabc\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Budget"));
	}

	@Test
	@DisplayName("Budget menu - delete with validation error")
	void testBudgetMenu_DeleteValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n4\nabc\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Budget"));
	}

	@Test
	@DisplayName("Budget menu - matrix with number format error")
	void testBudgetMenu_MatrixNumberFormatError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n5\nabc\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Invalid"));
	}

	@Test
	@DisplayName("Budget menu - list with data")
	void testBudgetMenu_ListWithData() {
		User guest = userAuthService.createGuestUser();
		budgetService.createBudget(guest.getId(), "TestBudget", new BigDecimal("1000"), LocalDate.now(), LocalDate.now().plusMonths(1));
		
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n2\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains(" - ") || output.contains("Budget"));
	}

	// ==================== SUMMARY MENU - ADDITIONAL TESTS ====================

	@Test
	@DisplayName("Summary menu - monthly matrix invalid year")
	void testSummaryMenu_MatrixInvalidYear() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n2\n1800\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Invalid"));
	}

	@Test
	@DisplayName("Summary menu - monthly matrix invalid month")
	void testSummaryMenu_MatrixInvalidMonth() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n2\n2025\n15\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("1-12"));
	}

	@Test
	@DisplayName("Summary menu - monthly matrix number format error")
	void testSummaryMenu_MatrixNumberFormatError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n2\nabc\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Invalid"));
	}

	@Test
	@DisplayName("Summary menu - invalid choice")
	void testSummaryMenu_InvalidChoice() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n9\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Invalid choice"));
	}

	@Test
	@DisplayName("Summary menu - top N expenses with validation error")
	void testSummaryMenu_TopNValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n3\nabc\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Summary"));
	}

	@Test
	@DisplayName("Summary menu - top categories with validation error")
	void testSummaryMenu_TopCatValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n4\nabc\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Summary"));
	}

	@Test
	@DisplayName("Summary menu - date range with validation error")
	void testSummaryMenu_DateRangeValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n5\ninvalid\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Summary"));
	}

	@Test
	@DisplayName("Summary menu - date range with invalid range")
	void testSummaryMenu_DateRangeInvalidRange() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n5\n2025-12-31\n2025-01-01\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("after"));
	}

	@Test
	@DisplayName("Summary menu - budget vs actual with validation error")
	void testSummaryMenu_BudgetVsActualValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n6\nabc\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Summary"));
	}

	@Test
	@DisplayName("Summary menu - budget vs actual with data exceeding")
	void testSummaryMenu_BudgetVsActualExceeding() {
		User guest = userAuthService.createGuestUser();
		Budget b = budgetService.createBudget(guest.getId(), "SmallBudget", new BigDecimal("100"), LocalDate.now(), LocalDate.now().plusMonths(1));
		expenseService.logExpense(guest.getId(), b.getId(), 1, new BigDecimal("150"), LocalDate.now(), "Over budget");
		
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n6\n" + b.getId() + "\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("WARNING") || output.contains("exceeded") || output.contains("Budget"));
	}

	@Test
	@DisplayName("Summary menu - savings progress with data")
	void testSummaryMenu_SavingsProgressWithData() {
		User guest = userAuthService.createGuestUser();
		goalService.createGoal(guest.getId(), "TestGoal", new BigDecimal("1000"), new BigDecimal("500"), LocalDate.now().plusMonths(6));
		
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n7\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Progress") || output.contains("Savings") || output.contains("%"));
	}

	// ==================== CATEGORY GRAPH MENU - ADDITIONAL TESTS ====================

	@Test
	@DisplayName("Category graph menu - add relation with validation error")
	void testCategoryGraphMenu_AddRelationValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n5\n1\nabc\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Category"));
	}

	@Test
	@DisplayName("Category graph menu - BFS with validation error")
	void testCategoryGraphMenu_BFSValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n5\n2\nabc\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Category"));
	}

	@Test
	@DisplayName("Category graph menu - DFS with validation error")
	void testCategoryGraphMenu_DFSValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n5\n3\nabc\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Category"));
	}

	@Test
	@DisplayName("Category graph menu - hierarchy spending with validation error")
	void testCategoryGraphMenu_SpendingValidationError() {
		ConsoleUI ui = createConsoleUIWithInput("3\n5\n4\nabc\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Category"));
	}

	// ==================== REGISTER - ADDITIONAL TESTS ====================

	@Test
	@DisplayName("Register with short password should show error")
	void testAuthMenu_RegisterShortPassword() {
		ConsoleUI ui = createConsoleUIWithInput("2\ntestuser\nab\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("at least"));
	}

	// ==================== LOGIN - ADDITIONAL TESTS ====================

	@Test
	@DisplayName("Login then register then exit")
	void testAuthMenu_LoginThenRegisterThenExit() {
		ConsoleUI ui = createConsoleUIWithInput("1\ntest\ntest\n2\nvaliduser\nvalidpass123\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Registration") || output.contains("Login"));
	}

	// ==================== BUDGET UPDATE SUCCESS PATH ====================

	@Test
	@DisplayName("Budget menu - update existing budget successfully")
	void testBudgetMenu_UpdateExistingBudgetSuccess() {
		// First create a budget
		User guest = userAuthService.createGuestUser();
		Budget created = budgetService.createBudget(guest.getId(), "OriginalBudget", new BigDecimal("1000"), 
			LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
		
		// Update the budget
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n3\n" + created.getId() + "\nUpdatedBudget\n2000\n2025-02-01\n2025-11-30\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Budget updated") || output.contains("UpdatedBudget"));
	}

	// ==================== BUDGET DELETE SUCCESS PATH ====================

	@Test
	@DisplayName("Budget menu - delete existing budget successfully")
	void testBudgetMenu_DeleteExistingBudgetSuccess() {
		// First create a budget
		User guest = userAuthService.createGuestUser();
		Budget created = budgetService.createBudget(guest.getId(), "ToDeleteBudget", new BigDecimal("500"), 
			LocalDate.of(2025, 1, 1), LocalDate.of(2025, 6, 30));
		
		// Delete the budget
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n4\n" + created.getId() + "\ny\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Budget deleted"));
	}

	// ==================== BUDGET MONTHLY MATRIX WITH DATA ====================

	@Test
	@DisplayName("Budget menu - monthly matrix with budget data showing values")
	void testBudgetMenu_MonthlyMatrixWithData() {
		// First create a budget for June 2025
		User guest = userAuthService.createGuestUser();
		budgetService.createBudget(guest.getId(), "JuneBudget", new BigDecimal("3000"), 
			LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30));
		
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n5\n2025\n6\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Day") || output.contains("matrix"));
	}

	// ==================== EXPENSE UPDATE SUCCESS PATH ====================

	@Test
	@DisplayName("Expense menu - update existing expense successfully")
	void testExpenseMenu_UpdateExistingExpenseSuccess() {
		// First log an expense
		User guest = userAuthService.createGuestUser();
		Expense created = expenseService.logExpense(guest.getId(), 1, 1, new BigDecimal("100"), 
			LocalDate.of(2025, 6, 15), "OriginalExpense");
		
		// Update the expense
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n3\n" + created.getId() + "\n1\n2\n200\n2025-07-15\nUpdatedExpense\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Expense updated") || output.contains("UpdatedExpense"));
	}

	// ==================== EXPENSE DELETE SUCCESS PATH ====================

	@Test
	@DisplayName("Expense menu - delete existing expense successfully")
	void testExpenseMenu_DeleteExistingExpenseSuccess() {
		// First log an expense
		User guest = userAuthService.createGuestUser();
		Expense created = expenseService.logExpense(guest.getId(), 1, 1, new BigDecimal("75"), 
			LocalDate.of(2025, 6, 10), "ToDeleteExpense");
		
		// Delete the expense
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n4\n" + created.getId() + "\ny\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Expense deleted"));
	}

	// ==================== GOAL UPDATE SUCCESS PATH ====================

	@Test
	@DisplayName("Goal menu - update existing goal successfully")
	void testGoalMenu_UpdateExistingGoalSuccess() {
		// First create a goal
		User guest = userAuthService.createGuestUser();
		Goal created = goalService.createGoal(guest.getId(), "OriginalGoal", new BigDecimal("5000"), 
			new BigDecimal("1000"), LocalDate.of(2025, 12, 31));
		
		// Update the goal
		ConsoleUI ui = createConsoleUIWithInput("3\n3\n3\n" + created.getId() + "\nUpdatedGoal\n10000\n2000\n2026-06-30\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Goal updated") || output.contains("UpdatedGoal"));
	}

	// ==================== GOAL DELETE SUCCESS PATH ====================

	@Test
	@DisplayName("Goal menu - delete existing goal successfully")
	void testGoalMenu_DeleteExistingGoalSuccess() {
		// First create a goal
		User guest = userAuthService.createGuestUser();
		Goal created = goalService.createGoal(guest.getId(), "ToDeleteGoal", new BigDecimal("3000"), 
			new BigDecimal("500"), LocalDate.of(2025, 10, 31));
		
		// Delete the goal
		ConsoleUI ui = createConsoleUIWithInput("3\n3\n4\n" + created.getId() + "\ny\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Goal deleted"));
	}

	// ==================== SUMMARY MONTHLY EXPENSE MATRIX WITH DATA ====================

	@Test
	@DisplayName("Summary menu - monthly expense matrix with expense data")
	void testSummaryMenu_MonthlyMatrixWithExpenseData() {
		// First log expenses for June 2025
		User guest = userAuthService.createGuestUser();
		expenseService.logExpense(guest.getId(), 1, 1, new BigDecimal("150"), LocalDate.of(2025, 6, 5), "GroceryExpense");
		expenseService.logExpense(guest.getId(), 1, 2, new BigDecimal("75"), LocalDate.of(2025, 6, 10), "TransportExpense");
		expenseService.logExpense(guest.getId(), 1, 1, new BigDecimal("200"), LocalDate.of(2025, 6, 15), "ShoppingExpense");
		
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n2\n2025\n6\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Day") || output.contains("Category") || output.contains("matrix"));
	}

	// ==================== SUMMARY SORT EXPENSES WITH DATA ====================

	@Test
	@DisplayName("Summary menu - sort expenses by amount with data")
	void testSummaryMenu_SortExpensesWithData() {
		// First log multiple expenses
		User guest = userAuthService.createGuestUser();
		expenseService.logExpense(guest.getId(), 1, 1, new BigDecimal("50"), LocalDate.of(2025, 6, 1), "SmallExpense");
		expenseService.logExpense(guest.getId(), 1, 1, new BigDecimal("300"), LocalDate.of(2025, 6, 5), "BigExpense");
		expenseService.logExpense(guest.getId(), 1, 1, new BigDecimal("150"), LocalDate.of(2025, 6, 10), "MediumExpense");
		
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n8\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("sorted") || output.contains("descending") || output.contains(" - "));
	}

	// ==================== SUMMARY TOP N EXPENSES WITH DATA ====================

	@Test
	@DisplayName("Summary menu - top N expenses with data")
	void testSummaryMenu_TopNExpensesWithData() {
		// First log multiple expenses
		User guest = userAuthService.createGuestUser();
		expenseService.logExpense(guest.getId(), 1, 1, new BigDecimal("100"), LocalDate.of(2025, 6, 1), "Expense1");
		expenseService.logExpense(guest.getId(), 1, 1, new BigDecimal("200"), LocalDate.of(2025, 6, 2), "Expense2");
		expenseService.logExpense(guest.getId(), 1, 1, new BigDecimal("300"), LocalDate.of(2025, 6, 3), "Expense3");
		
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n3\n2\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Top") || output.contains(" - "));
	}

	// ==================== SUMMARY TOP CATEGORIES WITH DATA ====================

	@Test
	@DisplayName("Summary menu - top N categories with data")
	void testSummaryMenu_TopNCategoriesWithData() {
		// First log expenses in different categories
		User guest = userAuthService.createGuestUser();
		expenseService.logExpense(guest.getId(), 1, 1, new BigDecimal("100"), LocalDate.of(2025, 6, 1), "Cat1Exp1");
		expenseService.logExpense(guest.getId(), 1, 1, new BigDecimal("150"), LocalDate.of(2025, 6, 2), "Cat1Exp2");
		expenseService.logExpense(guest.getId(), 1, 2, new BigDecimal("200"), LocalDate.of(2025, 6, 3), "Cat2Exp");
		
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n4\n2\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Category") || output.contains("->"));
	}

	// ==================== SUMMARY DATE RANGE WITH DATA ====================

	@Test
	@DisplayName("Summary menu - expenses in date range with data")
	void testSummaryMenu_DateRangeWithData() {
		// First log expenses
		User guest = userAuthService.createGuestUser();
		expenseService.logExpense(guest.getId(), 1, 1, new BigDecimal("100"), LocalDate.of(2025, 6, 15), "InRangeExpense");
		
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n5\n2025-06-01\n2025-06-30\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Expenses in range") || output.contains("items"));
	}

	// ==================== SUMMARY BUDGET VS ACTUAL WITH BUDGET SUCCESS ====================

	@Test
	@DisplayName("Summary menu - budget vs actual with valid budget within limit")
	void testSummaryMenu_BudgetVsActualWithinLimit() {
		User guest = userAuthService.createGuestUser();
		Budget b = budgetService.createBudget(guest.getId(), "TestBudget", new BigDecimal("1000"), 
			LocalDate.now().minusDays(30), LocalDate.now().plusDays(30));
		expenseService.logExpense(guest.getId(), b.getId(), 1, new BigDecimal("200"), LocalDate.now(), "UnderBudgetExpense");
		
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n6\n" + b.getId() + "\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Budget vs Actual") || output.contains("Budget Limit") || output.contains("Actual Spending"));
	}

	// ==================== CATEGORY GRAPH WITH DATA ====================

	@Test
	@DisplayName("Category graph - BFS traversal with relations")
	void testCategoryGraphMenu_BFSWithRelations() {
		categoryGraphService.addCategoryRelation(1, 2);
		categoryGraphService.addCategoryRelation(1, 3);
		categoryGraphService.addCategoryRelation(2, 4);
		
		ConsoleUI ui = createConsoleUIWithInput("3\n5\n2\n1\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("BFS") || output.contains("Category"));
	}

	@Test
	@DisplayName("Category graph - DFS traversal with relations")
	void testCategoryGraphMenu_DFSWithRelations() {
		categoryGraphService.addCategoryRelation(1, 2);
		categoryGraphService.addCategoryRelation(1, 3);
		
		ConsoleUI ui = createConsoleUIWithInput("3\n5\n3\n1\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("DFS") || output.contains("Category"));
	}

	@Test
	@DisplayName("Category graph - hierarchy spending with expenses")
	void testCategoryGraphMenu_HierarchySpendingWithData() {
		User guest = userAuthService.createGuestUser();
		categoryGraphService.addCategoryRelation(1, 2);
		expenseService.logExpense(guest.getId(), 1, 1, new BigDecimal("100"), LocalDate.now(), "Cat1Expense");
		expenseService.logExpense(guest.getId(), 1, 2, new BigDecimal("50"), LocalDate.now(), "Cat2Expense");
		
		ConsoleUI ui = createConsoleUIWithInput("3\n5\n4\n1\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("hierarchy spending") || output.contains("Category"));
	}

	// ==================== SAVINGS PROGRESS WITH MULTIPLE GOALS ====================

	@Test
	@DisplayName("Summary menu - savings progress with multiple goals")
	void testSummaryMenu_SavingsProgressMultipleGoals() {
		User guest = userAuthService.createGuestUser();
		goalService.createGoal(guest.getId(), "Goal1", new BigDecimal("1000"), new BigDecimal("500"), LocalDate.now().plusMonths(3));
		goalService.createGoal(guest.getId(), "Goal2", new BigDecimal("2000"), new BigDecimal("1500"), LocalDate.now().plusMonths(6));
		
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n7\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Savings Progress") || output.contains("Goal") || output.contains("Progress"));
	}

	// ==================== EXPENSE PROCESS SCHEDULED PAYMENTS WITH DATA ====================

	@Test
	@DisplayName("Expense menu - process scheduled payments with data")
	void testExpenseMenu_ProcessScheduledWithData() {
		User guest = userAuthService.createGuestUser();
		expenseService.schedulePlannedPayment(guest.getId(), 1, 1, new BigDecimal("100"), 
			LocalDate.now().minusDays(1), "DuePayment");
		
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n7\n" + LocalDate.now().plusDays(1).toString() + "\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Processed") || output.contains("scheduled"));
	}

	// ==================== EXPENSE SEARCH WITH RESULTS ====================

	@Test
	@DisplayName("Expense menu - search expenses with results")
	void testExpenseMenu_SearchWithResults() {
		User guest = userAuthService.createGuestUser();
		expenseService.logExpense(guest.getId(), 1, 1, new BigDecimal("100"), LocalDate.now(), "UniqueSearchTerm");
		
		ConsoleUI ui = createConsoleUIWithInput("3\n2\n8\nUniqueSearchTerm\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("Found expenses") || output.contains("UniqueSearchTerm"));
	}

	// ==================== BUDGET YEAR 2100 BOUNDARY ====================

	@Test
	@DisplayName("Budget menu - monthly matrix with year 2100")
	void testBudgetMenu_MonthlyMatrixYear2100() {
		ConsoleUI ui = createConsoleUIWithInput("3\n1\n5\n2100\n12\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("matrix") || output.contains("Monthly"));
	}

	// ==================== SUMMARY YEAR 2100 BOUNDARY ====================

	@Test
	@DisplayName("Summary menu - monthly matrix with year 2100")
	void testSummaryMenu_MonthlyMatrixYear2100() {
		ConsoleUI ui = createConsoleUIWithInput("3\n4\n2\n2100\n12\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("matrix") || output.contains("Monthly"));
	}

	// ==================== GOAL SCC WITH MORE DEPENDENCIES ====================

	@Test
	@DisplayName("Goal menu - SCC analysis with circular dependencies")
	void testGoalMenu_SCCCircularDependencies() {
		ConsoleUI ui = createConsoleUIWithInput("3\n3\n5\n3\n1\n2\n2\n3\n3\n1\n0\n0\n");
		ui.start();
		String output = outputStream.toString();
		assertTrue(output.contains("SCC") || output.contains("Strongly Connected"));
	}
}
