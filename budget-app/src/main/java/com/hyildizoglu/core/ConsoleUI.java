package com.hyildizoglu.core;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import com.hyildizoglu.algorithms.graph.CategoryGraphService;
import com.hyildizoglu.algorithms.matrix.SparseMatrix;
import com.hyildizoglu.budgetCreation.BudgetService;
import com.hyildizoglu.expenseLogging.ExpenseHistoryNavigator;
import com.hyildizoglu.expenseLogging.ExpenseService;
import com.hyildizoglu.financialSummary.FinancialSummaryService;
import com.hyildizoglu.models.Budget;
import com.hyildizoglu.models.Expense;
import com.hyildizoglu.models.Goal;
import com.hyildizoglu.models.User;
import com.hyildizoglu.savingsGoal.GoalService;
import com.hyildizoglu.userAuthentication.UserAuthService;

/**
 * Console-based user interface for the Budget Management System.
 * Handles all menu navigation, user input, and interaction with services.
 * Provides menus for authentication, budget management, expense logging,
 * goal tracking, and financial summaries.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class ConsoleUI {

	/** Scanner for reading user input from console. */
	private final Scanner scanner = new Scanner(System.in);

	/** Service for user authentication operations. */
	private final UserAuthService userAuthService;
	
	/** Service for budget management operations. */
	private final BudgetService budgetService;
	
	/** Service for expense logging operations. */
	private final ExpenseService expenseService;
	
	/** Service for savings goal operations. */
	private final GoalService goalService;
	
	/** Service for financial summary generation. */
	private final FinancialSummaryService financialSummaryService;
	
	/** Service for category graph operations. */
	private final CategoryGraphService categoryGraphService;

	/**
	 * Creates a new ConsoleUI with the required services.
	 * 
	 * @param userAuthService         Service for user authentication
	 * @param budgetService           Service for budget operations
	 * @param expenseService          Service for expense operations
	 * @param goalService             Service for goal operations
	 * @param financialSummaryService Service for financial summaries
	 * @param categoryGraphService    Service for category graph operations
	 */
	public ConsoleUI(UserAuthService userAuthService, BudgetService budgetService, ExpenseService expenseService,
			GoalService goalService, FinancialSummaryService financialSummaryService,
			CategoryGraphService categoryGraphService) {
		this.userAuthService = userAuthService;
		this.budgetService = budgetService;
		this.expenseService = expenseService;
		this.goalService = goalService;
		this.financialSummaryService = financialSummaryService;
		this.categoryGraphService = categoryGraphService;
	}

	/**
	 * Starts the console user interface.
	 * Displays authentication menu and then main menu after successful login.
	 */
	public void start() {
		System.out.println("=== Budget App ===");
		User currentUser = authMenu();
		if (currentUser == null) {
			System.out.println("Exiting...");
			return;
		}
		mainMenu(currentUser);
	}

	/**
	 * Displays the authentication menu and handles user choice.
	 * Provides options for login, registration, guest mode, or exit.
	 * 
	 * @return The authenticated User, or null if user chooses to exit
	 */
	private User authMenu() {
		while (true) {
			System.out.println("1) Login");
			System.out.println("2) Register");
			System.out.println("3) Guest mode");
			System.out.println("0) Exit");
			System.out.print("Choice: ");
			String choice = scanner.nextLine();
			switch (choice) {
			case "1":
				return login();
			case "2":
				register();
				break;
			case "3":
				User guest = userAuthService.createGuestUser();
				System.out.println("Logged in as guest. Welcome " + guest.getUsername() + "!");
				return guest;
			case "0":
				return null;
			default:
				System.out.println("Invalid choice.");
			}
		}
	}

	/**
	 * Handles user login by prompting for credentials.
	 * Validates username and password before attempting authentication.
	 * 
	 * @return The authenticated User, or null if login fails
	 */
	private User login() {
		try {
			System.out.print("Username: ");
			String username = scanner.nextLine();
			System.out.print("Password: ");
			String password = scanner.nextLine();
			InputValidator.validateUsername(username);
			InputValidator.validatePassword(password);
			Optional<User> user = userAuthService.login(username, password);
			if (user.isPresent()) {
				System.out.println("Login successful. Welcome " + user.get().getUsername() + "!");
				return user.get();
			} else {
				System.out.println("Invalid username or password.");
				return null;
			}
		} catch (InputValidator.ValidationException e) {
			System.out.println("Error: " + e.getMessage());
			return null;
		} catch (Exception e) {
			System.out.println("An unexpected error occurred: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Handles new user registration by prompting for credentials.
	 * Validates username and password before attempting registration.
	 */
	private void register() {
		try {
			System.out.print("New username: ");
			String username = scanner.nextLine();
			System.out.print("Password: ");
			String password = scanner.nextLine();
			InputValidator.validateUsername(username);
			InputValidator.validatePassword(password);
			if (userAuthService.register(username, password).isPresent()) {
				System.out.println("Registration successful. You can now login.");
			} else {
				System.out.println("This username already exists.");
			}
		} catch (InputValidator.ValidationException e) {
			System.out.println("Error: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("An unexpected error occurred: " + e.getMessage());
		}
	}

	/**
	 * Displays the main menu and handles user navigation.
	 * Provides access to budget, expense, goal, summary, and category operations.
	 * 
	 * @param user The currently logged-in user
	 */
	private void mainMenu(User user) {
		while (true) {
			System.out.println();
			System.out.println("=== Main Menu ===");
			System.out.println("1) Budget operations");
			System.out.println("2) Expense operations");
			System.out.println("3) Goal operations");
			System.out.println("4) Financial summary");
			System.out.println("5) Category graph operations");
			System.out.println("0) Exit");
			System.out.print("Choice: ");
			String choice = scanner.nextLine();
			switch (choice) {
			case "1":
				budgetMenu(user);
				break;
			case "2":
				expenseMenu(user);
				break;
			case "3":
				goalMenu(user);
				break;
			case "4":
				summaryMenu(user);
				break;
			case "5":
				categoryGraphMenu(user);
				break;
			case "0":
				System.out.println("Goodbye!");
				return;
			default:
				System.out.println("Invalid choice.");
			}
		}
	}

	/**
	 * Displays the budget management menu and handles operations.
	 * Provides options to create, list, update, delete budgets and view budget matrix.
	 * 
	 * @param user The currently logged-in user
	 */
	private void budgetMenu(User user) {
		while (true) {
			System.out.println("=== Budget Operations ===");
			System.out.println("1) Create budget");
			System.out.println("2) List budgets");
			System.out.println("3) Update budget");
			System.out.println("4) Delete budget");
			System.out.println("5) Show monthly budget matrix");
			System.out.println("0) Go back");
			System.out.print("Choice: ");
			String choice = scanner.nextLine();
			switch (choice) {
			case "1": {
				try {
					System.out.print("Budget name: ");
					String name = InputValidator.validateNonEmptyString(scanner.nextLine(), "Budget name");
					System.out.print("Total limit: ");
					BigDecimal totalLimit = InputValidator.validateBigDecimal(scanner.nextLine(), "Total limit", false);
					System.out.print("Start date (YYYY-MM-DD): ");
					LocalDate start = InputValidator.validateDate(scanner.nextLine(), "Start date");
					System.out.print("End date (YYYY-MM-DD): ");
					LocalDate end = InputValidator.validateDate(scanner.nextLine(), "End date");
					InputValidator.validateDateRange(start, end);
					Budget budget = budgetService.createBudget(user.getId(), name, totalLimit, start, end);
					System.out.println("Created budget: " + budget);
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "2": {
				List<Budget> budgets = budgetService.listBudgetsForUser(user.getId());
				System.out.println("Your budgets:");
				if (budgets.isEmpty()) {
					System.out.println("No budgets yet.");
				} else {
					for (Budget b : budgets) {
						System.out.println(" - " + b);
					}
				}
				break;
			}
			case "3": {
				try {
					System.out.print("Budget ID to update: ");
					int budgetId = InputValidator.validateInteger(scanner.nextLine(), "Budget ID");
					System.out.print("New budget name: ");
					String name = InputValidator.validateNonEmptyString(scanner.nextLine(), "Budget name");
					System.out.print("New total limit: ");
					BigDecimal totalLimit = InputValidator.validateBigDecimal(scanner.nextLine(), "Total limit", false);
					System.out.print("New start date (YYYY-MM-DD): ");
					LocalDate start = InputValidator.validateDate(scanner.nextLine(), "Start date");
					System.out.print("New end date (YYYY-MM-DD): ");
					LocalDate end = InputValidator.validateDate(scanner.nextLine(), "End date");
					InputValidator.validateDateRange(start, end);
					Budget updated = budgetService.updateBudget(budgetId, name, totalLimit, start, end);
					if (updated != null) {
						System.out.println("Budget updated: " + updated);
					} else {
						System.out.println("Budget not found or update failed.");
					}
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "4": {
				try {
					System.out.print("Budget ID to delete: ");
					int budgetId = InputValidator.validateInteger(scanner.nextLine(), "Budget ID");
					System.out.print("Are you sure? (y/n): ");
					String confirm = scanner.nextLine();
					if ("y".equalsIgnoreCase(confirm)) {
						if (budgetService.deleteBudget(budgetId)) {
							System.out.println("Budget deleted.");
						} else {
							System.out.println("Budget not found or deletion failed.");
						}
					} else {
						System.out.println("Deletion cancelled.");
					}
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "5": {
				try {
					System.out.print("Year (YYYY): ");
					int year = Integer.parseInt(scanner.nextLine());
					if (year < 1900 || year > 2100) {
						System.out.println("Error: Invalid year.");
						break;
					}
					System.out.print("Month (1-12): ");
					int month = Integer.parseInt(scanner.nextLine());
					if (month < 1 || month > 12) {
						System.out.println("Error: Month must be between 1-12.");
						break;
					}
					YearMonth ym = YearMonth.of(year, month);
					SparseMatrix matrix = budgetService.buildBudgetMatrixForMonth(user.getId(), ym);
					System.out.println("Monthly budget matrix (day -> total limit):");
					for (int day = 1; day <= ym.lengthOfMonth(); day++) {
						BigDecimal v = matrix.rowSum(day);
						if (v.compareTo(BigDecimal.ZERO) > 0) {
							System.out.println("Day " + day + ": " + v);
						}
					}
				} catch (NumberFormatException e) {
					System.out.println("Error: Invalid number format.");
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "0":
				return;
			default:
				System.out.println("Invalid choice.");
			}
		}
	}

	/**
	 * Displays the expense management menu and handles operations.
	 * Provides options to log, list, update, delete, undo expenses and manage scheduled payments.
	 * 
	 * @param user The currently logged-in user
	 */
	private void expenseMenu(User user) {
		while (true) {
			System.out.println("=== Expense Operations ===");
			System.out.println("1) Log expense");
			System.out.println("2) List and navigate expenses");
			System.out.println("3) Update expense");
			System.out.println("4) Delete expense");
			System.out.println("5) Undo last expense (UNDO)");
			System.out.println("6) Add scheduled payment");
			System.out.println("7) Process scheduled payments");
			System.out.println("8) Search expenses by description");
			System.out.println("0) Go back");
			System.out.print("Choice: ");
			String choice = scanner.nextLine();
			switch (choice) {
		case "1": {
			try {
				System.out.print("Budget ID: ");
				int budgetId = InputValidator.validateInteger(scanner.nextLine(), "Budget ID");
				System.out.print("Category ID (just a number, keeping it simple): ");
				int categoryId = InputValidator.validateInteger(scanner.nextLine(), "Category ID");
				System.out.print("Amount: ");
				BigDecimal amount = InputValidator.validateBigDecimal(scanner.nextLine(), "Amount", false);
				System.out.print("Date (YYYY-MM-DD): ");
				LocalDate date = InputValidator.validateDate(scanner.nextLine(), "Date");
				System.out.print("Description: ");
				String description = InputValidator.validateNonEmptyString(scanner.nextLine(), "Description");
				Expense expense = expenseService.logExpense(user.getId(), budgetId, categoryId, amount, date,
						description);
				System.out.println("Logged expense: " + expense);
			} catch (InputValidator.ValidationException e) {
				System.out.println("Error: " + e.getMessage());
			} catch (Exception e) {
				System.out.println("An unexpected error occurred: " + e.getMessage());
			}
			break;
		}
		case "2": {
			List<Expense> expenses = expenseService.listExpensesForUser(user.getId());
			if (expenses.isEmpty()) {
				System.out.println("No expenses found.");
				break;
			}
			ExpenseHistoryNavigator navigator = new ExpenseHistoryNavigator(expenses);
			System.out.println("Total " + expenses.size() + " expenses.");
			String cmd;
			do {
				System.out.println("Current record: " + navigator.current());
				System.out.print("(n = next, p = previous, q = quit): ");
				cmd = scanner.nextLine();
				if ("n".equalsIgnoreCase(cmd)) {
					navigator.next();
				} else if ("p".equalsIgnoreCase(cmd)) {
					navigator.previous();
				}
			} while (!"q".equalsIgnoreCase(cmd));
			break;
		}
			case "3": {
				try {
					System.out.print("Expense ID to update: ");
					int expenseId = InputValidator.validateInteger(scanner.nextLine(), "Expense ID");
					System.out.print("New budget ID: ");
					int budgetId = InputValidator.validateInteger(scanner.nextLine(), "Budget ID");
					System.out.print("New category ID: ");
					int categoryId = InputValidator.validateInteger(scanner.nextLine(), "Category ID");
					System.out.print("New amount: ");
					BigDecimal amount = InputValidator.validateBigDecimal(scanner.nextLine(), "Amount", false);
					System.out.print("New date (YYYY-MM-DD): ");
					LocalDate date = InputValidator.validateDate(scanner.nextLine(), "Date");
					System.out.print("New description: ");
					String description = InputValidator.validateNonEmptyString(scanner.nextLine(), "Description");
					Expense updated = expenseService.updateExpense(expenseId, budgetId, categoryId, amount, date,
							description);
					if (updated != null) {
						System.out.println("Expense updated: " + updated);
					} else {
						System.out.println("Expense not found or update failed.");
					}
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "4": {
				try {
					System.out.print("Expense ID to delete: ");
					int expenseId = InputValidator.validateInteger(scanner.nextLine(), "Expense ID");
					System.out.print("Are you sure? (y/n): ");
					String confirm = scanner.nextLine();
					if ("y".equalsIgnoreCase(confirm)) {
						if (expenseService.deleteExpense(expenseId)) {
							System.out.println("Expense deleted.");
						} else {
							System.out.println("Expense not found or deletion failed.");
						}
					} else {
						System.out.println("Deletion cancelled.");
					}
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "5": {
				Expense undone = expenseService.undoLastExpense();
				if (undone != null) {
					System.out.println("Undone expense: " + undone);
				} else {
					System.out.println("No expense to undo.");
				}
				break;
			}
			case "6": {
				try {
					System.out.print("Budget ID: ");
					int budgetId = InputValidator.validateInteger(scanner.nextLine(), "Budget ID");
					System.out.print("Category ID: ");
					int categoryId = InputValidator.validateInteger(scanner.nextLine(), "Category ID");
					System.out.print("Amount: ");
					BigDecimal amount = InputValidator.validateBigDecimal(scanner.nextLine(), "Amount", false);
					System.out.print("Due date (YYYY-MM-DD): ");
					LocalDate dueDate = InputValidator.validateDate(scanner.nextLine(), "Due date");
					System.out.print("Description: ");
					String description = InputValidator.validateNonEmptyString(scanner.nextLine(), "Description");
					expenseService.schedulePlannedPayment(user.getId(), budgetId, categoryId, amount, dueDate,
							description);
					System.out.println("Scheduled payment added.");
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "7": {
				try {
					System.out.print("Process up to date (YYYY-MM-DD): ");
					LocalDate upTo = InputValidator.validateDate(scanner.nextLine(), "Date");
					List<Expense> processed = expenseService.processPlannedPaymentsUpTo(upTo);
					System.out.println("Processed scheduled payments: " + processed.size());
					for (Expense e : processed) {
						System.out.println(" - " + e);
					}
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "8": {
				System.out.print("Search keyword: ");
				String keyword = scanner.nextLine();
				List<Expense> found = expenseService.searchExpensesByDescription(user.getId(), keyword);
				System.out.println("Found expenses: " + found.size());
				for (Expense e : found) {
					System.out.println(" - " + e);
				}
				break;
			}
			case "0":
				return;
			default:
				System.out.println("Invalid choice.");
			}
		}
	}

	/**
	 * Displays the savings goal management menu and handles operations.
	 * Provides options to create, list, update, delete goals and analyze dependencies.
	 * 
	 * @param user The currently logged-in user
	 */
	private void goalMenu(User user) {
		while (true) {
			System.out.println("=== Goal Operations ===");
			System.out.println("1) Create goal");
			System.out.println("2) List goals");
			System.out.println("3) Update goal");
			System.out.println("4) Delete goal");
			System.out.println("5) Goal dependency analysis (SCC)");
			System.out.println("0) Go back");
			System.out.print("Choice: ");
			String choice = scanner.nextLine();
			switch (choice) {
			case "1": {
				try {
					System.out.print("Goal name: ");
					String name = InputValidator.validateNonEmptyString(scanner.nextLine(), "Goal name");
					System.out.print("Target amount: ");
					BigDecimal target = InputValidator.validateBigDecimal(scanner.nextLine(), "Target amount", false);
					System.out.print("Current amount: ");
					BigDecimal current = InputValidator.validateBigDecimal(scanner.nextLine(), "Current amount", true);
					System.out.print("Deadline (YYYY-MM-DD): ");
					LocalDate deadline = InputValidator.validateDate(scanner.nextLine(), "Deadline");
					Goal goal = goalService.createGoal(user.getId(), name, target, current, deadline);
					System.out.println("Created goal: " + goal);
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "2": {
				List<Goal> goals = goalService.listGoalsForUser(user.getId());
				System.out.println("Your goals:");
				if (goals.isEmpty()) {
					System.out.println("No goals yet.");
				} else {
					for (Goal g : goals) {
						System.out.println(" - " + g + " (completed? " + g.isCompleted() + ")");
					}
				}
				break;
			}
			case "3": {
				try {
					System.out.print("Goal ID to update: ");
					int goalId = InputValidator.validateInteger(scanner.nextLine(), "Goal ID");
					System.out.print("New goal name: ");
					String name = InputValidator.validateNonEmptyString(scanner.nextLine(), "Goal name");
					System.out.print("New target amount: ");
					BigDecimal target = InputValidator.validateBigDecimal(scanner.nextLine(), "Target amount", false);
					System.out.print("New current amount: ");
					BigDecimal current = InputValidator.validateBigDecimal(scanner.nextLine(), "Current amount", true);
					System.out.print("New deadline (YYYY-MM-DD): ");
					LocalDate deadline = InputValidator.validateDate(scanner.nextLine(), "Deadline");
					Goal updated = goalService.updateGoal(goalId, name, target, current, deadline);
					if (updated != null) {
						System.out.println("Goal updated: " + updated);
					} else {
						System.out.println("Goal not found or update failed.");
					}
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "4": {
				try {
					System.out.print("Goal ID to delete: ");
					int goalId = InputValidator.validateInteger(scanner.nextLine(), "Goal ID");
					System.out.print("Are you sure? (y/n): ");
					String confirm = scanner.nextLine();
					if ("y".equalsIgnoreCase(confirm)) {
						if (goalService.deleteGoal(goalId)) {
							System.out.println("Goal deleted.");
						} else {
							System.out.println("Goal not found or deletion failed.");
						}
					} else {
						System.out.println("Deletion cancelled.");
					}
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "5": {
				try {
					System.out.println("Building goal dependency graph...");
					System.out.println("Example: If Goal 1 depends on Goal 2: 1 -> 2");
					System.out.print("How many dependencies to add? ");
					int count = InputValidator.validateInteger(scanner.nextLine(), "Dependency count");
					Map<Integer, List<Integer>> dependencies = new HashMap<>();
					for (int i = 0; i < count; i++) {
						System.out.print("Goal ID (dependent): ");
						int fromId = InputValidator.validateInteger(scanner.nextLine(), "Goal ID");
						System.out.print("Goal ID (depends on): ");
						int toId = InputValidator.validateInteger(scanner.nextLine(), "Goal ID");
						dependencies.computeIfAbsent(fromId, k -> new ArrayList<>()).add(toId);
					}
					List<java.util.Set<Integer>> sccs = goalService.analyzeGoalDependencies(dependencies);
					System.out.println("Strongly Connected Components:");
					for (int i = 0; i < sccs.size(); i++) {
						System.out.println("SCC " + (i + 1) + ": " + sccs.get(i));
					}
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "0":
				return;
			default:
				System.out.println("Invalid choice.");
			}
		}
	}

	/**
	 * Displays the financial summary menu and handles operations.
	 * Provides various reports including totals, expense matrix, top expenses, and savings progress.
	 * 
	 * @param user The currently logged-in user
	 */
	private void summaryMenu(User user) {
		while (true) {
			System.out.println("=== Financial Summary ===");
			System.out.println("1) General summary");
			System.out.println("2) Monthly expense matrix");
			System.out.println("3) Top N expenses");
			System.out.println("4) Top N categories by spending");
			System.out.println("5) Expenses in date range");
			System.out.println("6) Budget vs Actual Spending");
			System.out.println("7) Savings Progress");
			System.out.println("8) Sort expenses by amount (HeapSort)");
			System.out.println("0) Go back");
			System.out.print("Choice: ");
			String choice = scanner.nextLine();
			switch (choice) {
			case "1": {
				System.out.println(
						"Total budget limit: " + financialSummaryService.calculateTotalBudgetLimit(user.getId()));
				System.out.println("Total expenses: " + financialSummaryService.calculateTotalExpenses(user.getId()));
				System.out.println("Remaining budget: " + financialSummaryService.calculateRemainingBudget(user.getId()));
				break;
			}
			case "2": {
				try {
					System.out.print("Year (YYYY): ");
					int year = Integer.parseInt(scanner.nextLine());
					if (year < 1900 || year > 2100) {
						System.out.println("Error: Invalid year.");
						break;
					}
					System.out.print("Month (1-12): ");
					int month = Integer.parseInt(scanner.nextLine());
					if (month < 1 || month > 12) {
						System.out.println("Error: Month must be between 1-12.");
						break;
					}
					YearMonth ym = YearMonth.of(year, month);
					SparseMatrix matrix = financialSummaryService.buildExpenseMatrixForMonth(user.getId(), ym);
					System.out.println("Monthly expense matrix (day, category -> amount, zeros hidden):");
					for (int day = 1; day <= ym.lengthOfMonth(); day++) {
						for (int cat = 0; cat < 10; cat++) {
							BigDecimal v = matrix.get(day, cat);
							if (v.compareTo(BigDecimal.ZERO) > 0) {
								System.out.println("Day " + day + " / Category " + cat + ": " + v);
							}
						}
					}
				} catch (NumberFormatException e) {
					System.out.println("Error: Invalid number format.");
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "3": {
				try {
					System.out.print("How many? ");
					int n = InputValidator.validateInteger(scanner.nextLine(), "Count");
					List<Expense> top = financialSummaryService.topNExpenses(user.getId(), n);
					System.out.println("Top " + n + " expenses:");
					for (Expense e : top) {
						System.out.println(" - " + e);
					}
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "4": {
				try {
					System.out.print("How many categories? ");
					int n = InputValidator.validateInteger(scanner.nextLine(), "Count");
					List<Map.Entry<Integer, BigDecimal>> topCats = financialSummaryService
							.topNCategoriesBySpending(user.getId(), n);
					System.out.println("Top spending categories:");
					for (Map.Entry<Integer, BigDecimal> e : topCats) {
						System.out.println("Category " + e.getKey() + " -> " + e.getValue());
					}
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "5": {
				try {
					System.out.print("Start date (YYYY-MM-DD): ");
					LocalDate from = InputValidator.validateDate(scanner.nextLine(), "Start date");
					System.out.print("End date (YYYY-MM-DD): ");
					LocalDate to = InputValidator.validateDate(scanner.nextLine(), "End date");
					InputValidator.validateDateRange(from, to);
					List<Expense> inRange = financialSummaryService.expensesInDateRange(user.getId(), from, to);
					System.out.println("Expenses in range (" + inRange.size() + " items):");
					for (Expense e : inRange) {
						System.out.println(" - " + e);
					}
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "6": {
				try {
					System.out.print("Budget ID: ");
					int budgetId = InputValidator.validateInteger(scanner.nextLine(), "Budget ID");
					Map<String, BigDecimal> comparison = financialSummaryService.budgetVsActual(user.getId(), budgetId);
					if (comparison.isEmpty()) {
						System.out.println("Budget not found or does not belong to you.");
					} else {
						System.out.println("=== Budget vs Actual Spending ===");
						System.out.println("Budget Limit: " + comparison.get("budgetLimit"));
						System.out.println("Actual Spending: " + comparison.get("actualSpending"));
						System.out.println("Difference: " + comparison.get("difference"));
						System.out.println("Usage Percentage: %" + comparison.get("percentage"));
						if (comparison.get("difference").compareTo(BigDecimal.ZERO) < 0) {
							System.out.println("WARNING: Budget exceeded!");
						}
					}
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "7": {
				Map<Goal, Map<String, BigDecimal>> progress = financialSummaryService.savingsProgress(user.getId());
				if (progress.isEmpty()) {
					System.out.println("No goals found.");
				} else {
					System.out.println("=== Savings Progress ===");
					for (Map.Entry<Goal, Map<String, BigDecimal>> entry : progress.entrySet()) {
						Goal goal = entry.getKey();
						Map<String, BigDecimal> prog = entry.getValue();
						System.out.println("Goal: " + goal.getName());
						System.out.println("  Target Amount: " + prog.get("target"));
						System.out.println("  Current Amount: " + prog.get("current"));
						System.out.println("  Remaining: " + prog.get("remaining"));
						System.out.println("  Progress: %" + prog.get("percentage"));
						System.out.println("  Completed: " + goal.isCompleted());
						System.out.println();
					}
				}
				break;
			}
			case "8": {
				List<Expense> sorted = financialSummaryService.sortExpensesByAmountDescending(user.getId());
				System.out.println("Expenses (sorted by amount descending):");
				for (Expense e : sorted) {
					System.out.println(" - " + e);
				}
				break;
			}
			case "0":
				return;
			default:
				System.out.println("Invalid choice.");
			}
		}
	}

	/**
	 * Displays the category graph menu and handles operations.
	 * Provides options for category relations, BFS/DFS traversal, and hierarchy spending calculation.
	 * 
	 * @param user The currently logged-in user
	 */
	private void categoryGraphMenu(User user) {
		while (true) {
			System.out.println("=== Category Graph Operations ===");
			System.out.println("1) Add category relation");
			System.out.println("2) Traverse category hierarchy with BFS");
			System.out.println("3) Traverse category hierarchy with DFS");
			System.out.println("4) Calculate category hierarchy spending");
			System.out.println("0) Go back");
			System.out.print("Choice: ");
			String choice = scanner.nextLine();
			switch (choice) {
			case "1": {
				try {
					System.out.print("Parent category ID: ");
					int parentId = InputValidator.validateInteger(scanner.nextLine(), "Parent category ID");
					System.out.print("Child category ID: ");
					int childId = InputValidator.validateInteger(scanner.nextLine(), "Child category ID");
					categoryGraphService.addCategoryRelation(parentId, childId);
					System.out.println("Category relation added: " + parentId + " -> " + childId);
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "2": {
				try {
					System.out.print("Starting category ID: ");
					int rootId = InputValidator.validateInteger(scanner.nextLine(), "Category ID");
					List<Integer> order = categoryGraphService.traverseCategoryHierarchyBFS(rootId);
					System.out.println("BFS traversal order:");
					for (int catId : order) {
						System.out.println(" - Category " + catId);
					}
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "3": {
				try {
					System.out.print("Starting category ID: ");
					int rootId = InputValidator.validateInteger(scanner.nextLine(), "Category ID");
					List<Integer> order = categoryGraphService.traverseCategoryHierarchyDFS(rootId);
					System.out.println("DFS traversal order:");
					for (int catId : order) {
						System.out.println(" - Category " + catId);
					}
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "4": {
				try {
					System.out.print("Parent category ID: ");
					int rootId = InputValidator.validateInteger(scanner.nextLine(), "Category ID");
					Map<Integer, BigDecimal> spending = categoryGraphService
							.calculateCategoryHierarchySpending(user.getId(), rootId);
					System.out.println("Category hierarchy spending:");
					for (Map.Entry<Integer, BigDecimal> e : spending.entrySet()) {
						System.out.println("Category " + e.getKey() + ": " + e.getValue());
					}
				} catch (InputValidator.ValidationException e) {
					System.out.println("Error: " + e.getMessage());
				} catch (Exception e) {
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
				break;
			}
			case "0":
				return;
			default:
				System.out.println("Invalid choice.");
			}
		}
	}
}
