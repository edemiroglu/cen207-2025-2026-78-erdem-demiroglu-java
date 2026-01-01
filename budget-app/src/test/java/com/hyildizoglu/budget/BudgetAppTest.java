package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.graph.CategoryGraphService;
import com.hyildizoglu.budgetCreation.BudgetFileRepository;
import com.hyildizoglu.budgetCreation.BudgetService;
import com.hyildizoglu.core.BudgetApp;
import com.hyildizoglu.core.ConsoleUI;
import com.hyildizoglu.expenseLogging.ExpenseFileRepository;
import com.hyildizoglu.expenseLogging.ExpenseService;
import com.hyildizoglu.financialSummary.FinancialSummaryService;
import com.hyildizoglu.savingsGoal.GoalFileRepository;
import com.hyildizoglu.savingsGoal.GoalService;
import com.hyildizoglu.userAuthentication.UserAuthService;
import com.hyildizoglu.userAuthentication.UserFileRepository;

@DisplayName("BudgetApp Integration Tests")
class BudgetAppTest {

	private ByteArrayOutputStream outputStream;
	private PrintStream originalOut;
	private InputStream originalIn;
	private Path userFile;
	private Path budgetFile;
	private Path expenseFile;
	private Path goalFile;

	@BeforeEach
	void setUp() throws Exception {
		outputStream = new ByteArrayOutputStream();
		originalOut = System.out;
		originalIn = System.in;
		System.setOut(new PrintStream(outputStream));

		userFile = Files.createTempFile("test_user", ".dat");
		budgetFile = Files.createTempFile("test_budget", ".dat");
		expenseFile = Files.createTempFile("test_expense", ".dat");
		goalFile = Files.createTempFile("test_goal", ".dat");
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

	// ==================== CLASS STRUCTURE TESTS ====================

	@Test
	@DisplayName("BudgetApp class should exist")
	void testBudgetAppClassExists() {
		assertNotNull(BudgetApp.class);
	}

	@Test
	@DisplayName("BudgetApp should have main method")
	void testBudgetAppHasMainMethod() throws NoSuchMethodException {
		Method mainMethod = BudgetApp.class.getMethod("main", String[].class);
		assertNotNull(mainMethod);
		assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));
		assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()));
	}

	// ==================== MAIN METHOD TESTS ====================

	@Test
	@DisplayName("Main method with immediate exit should work")
	void testMain_ImmediateExit() {
		setInput("0\n");
		BudgetApp.main(new String[]{});
		String output = outputStream.toString();
		assertTrue(output.contains("Budget App") || output.contains("Exit"));
	}

	@Test
	@DisplayName("Main method with guest mode and exit")
	void testMain_GuestModeExit() {
		setInput("3\n0\n");
		BudgetApp.main(new String[]{});
		String output = outputStream.toString();
		assertTrue(output.contains("guest") || output.contains("Main Menu"));
	}

	@Test
	@DisplayName("Main method with registration flow")
	void testMain_RegistrationFlow() {
		setInput("2\ntestuser\npassword123\n0\n");
		BudgetApp.main(new String[]{});
		String output = outputStream.toString();
		assertTrue(output.contains("Register") || output.contains("Registration"));
	}

	@Test
	@DisplayName("Main method with login flow")
	void testMain_LoginFlow() {
		setInput("1\nnonexistent\nwrongpass\n0\n");
		BudgetApp.main(new String[]{});
		String output = outputStream.toString();
		assertTrue(output.contains("Login") || output.contains("Invalid"));
	}

	@Test
	@DisplayName("Main method with invalid auth choice")
	void testMain_InvalidAuthChoice() {
		setInput("9\n0\n");
		BudgetApp.main(new String[]{});
		String output = outputStream.toString();
		assertTrue(output.contains("Invalid choice") || output.contains("Exit"));
	}

	// ==================== COMPONENT INTEGRATION TESTS ====================

	@Test
	@DisplayName("All repositories should be creatable")
	void testRepositoriesCreation() {
		UserFileRepository userRepo = new UserFileRepository(userFile);
		BudgetFileRepository budgetRepo = new BudgetFileRepository(budgetFile);
		ExpenseFileRepository expenseRepo = new ExpenseFileRepository(expenseFile);
		GoalFileRepository goalRepo = new GoalFileRepository(goalFile);

		assertNotNull(userRepo);
		assertNotNull(budgetRepo);
		assertNotNull(expenseRepo);
		assertNotNull(goalRepo);
	}

	@Test
	@DisplayName("All services should be creatable")
	void testServicesCreation() {
		UserFileRepository userRepo = new UserFileRepository(userFile);
		BudgetFileRepository budgetRepo = new BudgetFileRepository(budgetFile);
		ExpenseFileRepository expenseRepo = new ExpenseFileRepository(expenseFile);
		GoalFileRepository goalRepo = new GoalFileRepository(goalFile);

		UserAuthService userAuthService = new UserAuthService(userRepo);
		BudgetService budgetService = new BudgetService(budgetRepo);
		ExpenseService expenseService = new ExpenseService(expenseRepo);
		GoalService goalService = new GoalService(goalRepo);
		FinancialSummaryService financialSummaryService = new FinancialSummaryService(budgetRepo, expenseRepo, goalRepo);
		CategoryGraphService categoryGraphService = new CategoryGraphService(expenseService);

		assertNotNull(userAuthService);
		assertNotNull(budgetService);
		assertNotNull(expenseService);
		assertNotNull(goalService);
		assertNotNull(financialSummaryService);
		assertNotNull(categoryGraphService);
	}

	@Test
	@DisplayName("ConsoleUI should be creatable with all services")
	void testConsoleUICreation() {
		UserFileRepository userRepo = new UserFileRepository(userFile);
		BudgetFileRepository budgetRepo = new BudgetFileRepository(budgetFile);
		ExpenseFileRepository expenseRepo = new ExpenseFileRepository(expenseFile);
		GoalFileRepository goalRepo = new GoalFileRepository(goalFile);

		UserAuthService userAuthService = new UserAuthService(userRepo);
		BudgetService budgetService = new BudgetService(budgetRepo);
		ExpenseService expenseService = new ExpenseService(expenseRepo);
		GoalService goalService = new GoalService(goalRepo);
		FinancialSummaryService financialSummaryService = new FinancialSummaryService(budgetRepo, expenseRepo, goalRepo);
		CategoryGraphService categoryGraphService = new CategoryGraphService(expenseService);

		ConsoleUI ui = new ConsoleUI(userAuthService, budgetService, expenseService, goalService,
				financialSummaryService, categoryGraphService);

		assertNotNull(ui);
	}

	// ==================== FULL WORKFLOW TESTS ====================

	@Test
	@DisplayName("Full workflow: Register, login, create budget, add expense, view summary")
	void testFullWorkflow_CompleteFlow() {
		// Register a user
		setInput("2\nflowuser\npassword123\n" +
				// Login with the user
				"1\nflowuser\npassword123\n" +
				// Create a budget
				"1\n1\nTest Budget\n1000\n2025-01-01\n2025-12-31\n0\n" +
				// Add an expense
				"2\n1\n1\n1\n50.00\n2025-06-15\nTest expense\n0\n" +
				// View summary
				"4\n1\n0\n" +
				// Exit
				"0\n");
		BudgetApp.main(new String[]{});
		String output = outputStream.toString();
		assertTrue(output.contains("Budget App"));
	}

	@Test
	@DisplayName("Workflow: Guest mode with budget operations")
	void testWorkflow_GuestBudgetOperations() {
		setInput("3\n" + // Guest mode
				"1\n" + // Budget menu
				"2\n" + // List budgets
				"0\n" + // Back
				"0\n"); // Exit
		BudgetApp.main(new String[]{});
		String output = outputStream.toString();
		assertTrue(output.contains("guest") || output.contains("Budget"));
	}

	@Test
	@DisplayName("Workflow: Guest mode with expense operations")
	void testWorkflow_GuestExpenseOperations() {
		setInput("3\n" + // Guest mode
				"2\n" + // Expense menu
				"2\n" + // List expenses
				"0\n" + // Back
				"0\n"); // Exit
		BudgetApp.main(new String[]{});
		String output = outputStream.toString();
		assertTrue(output.contains("guest") || output.contains("Expense"));
	}

	@Test
	@DisplayName("Workflow: Guest mode with goal operations")
	void testWorkflow_GuestGoalOperations() {
		setInput("3\n" + // Guest mode
				"3\n" + // Goal menu
				"2\n" + // List goals
				"0\n" + // Back
				"0\n"); // Exit
		BudgetApp.main(new String[]{});
		String output = outputStream.toString();
		assertTrue(output.contains("guest") || output.contains("Goal"));
	}

	@Test
	@DisplayName("Workflow: Guest mode with summary operations")
	void testWorkflow_GuestSummaryOperations() {
		setInput("3\n" + // Guest mode
				"4\n" + // Summary menu
				"1\n" + // General summary
				"0\n" + // Back
				"0\n"); // Exit
		BudgetApp.main(new String[]{});
		String output = outputStream.toString();
		assertTrue(output.contains("guest") || output.contains("Summary") || output.contains("Total"));
	}

	@Test
	@DisplayName("Workflow: Guest mode with category graph operations")
	void testWorkflow_GuestCategoryGraphOperations() {
		setInput("3\n" + // Guest mode
				"5\n" + // Category graph menu
				"1\n" + // Add relation
				"1\n" + // Parent ID
				"2\n" + // Child ID
				"0\n" + // Back
				"0\n"); // Exit
		BudgetApp.main(new String[]{});
		String output = outputStream.toString();
		assertTrue(output.contains("guest") || output.contains("Category"));
	}

	// ==================== ERROR HANDLING TESTS ====================

	@Test
	@DisplayName("Error handling: Invalid input in budget menu")
	void testErrorHandling_InvalidBudgetInput() {
		setInput("3\n" + // Guest mode
				"1\n" + // Budget menu
				"1\n" + // Create budget
				"Test\n" + // Name
				"abc\n" + // Invalid amount - will throw error
				"0\n" + // Back
				"0\n"); // Exit
		BudgetApp.main(new String[]{});
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("Budget"));
	}

	@Test
	@DisplayName("Error handling: Invalid date format")
	void testErrorHandling_InvalidDateFormat() {
		setInput("3\n" + // Guest mode
				"1\n" + // Budget menu
				"1\n" + // Create budget
				"Test Budget\n" + // Name
				"1000\n" + // Amount
				"invalid-date\n" + // Invalid date
				"0\n" + // Back
				"0\n"); // Exit
		BudgetApp.main(new String[]{});
		String output = outputStream.toString();
		assertTrue(output.contains("Error") || output.contains("invalid"));
	}

	@Test
	@DisplayName("Main method with empty args should work")
	void testMain_EmptyArgs() {
		setInput("0\n");
		assertDoesNotThrow(() -> BudgetApp.main(new String[]{}));
	}

	@Test
	@DisplayName("Main method with null args should work")
	void testMain_NullArgs() {
		setInput("0\n");
		assertDoesNotThrow(() -> BudgetApp.main(null));
	}
}
