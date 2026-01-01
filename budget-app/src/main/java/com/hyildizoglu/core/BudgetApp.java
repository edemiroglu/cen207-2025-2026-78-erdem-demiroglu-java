package com.hyildizoglu.core;

import com.hyildizoglu.algorithms.graph.CategoryGraphService;
import com.hyildizoglu.budgetCreation.BudgetFileRepository;
import com.hyildizoglu.budgetCreation.BudgetService;
import com.hyildizoglu.expenseLogging.ExpenseFileRepository;
import com.hyildizoglu.expenseLogging.ExpenseService;
import com.hyildizoglu.financialSummary.FinancialSummaryService;
import com.hyildizoglu.savingsGoal.GoalFileRepository;
import com.hyildizoglu.savingsGoal.GoalService;
import com.hyildizoglu.userAuthentication.UserAuthService;
import com.hyildizoglu.userAuthentication.UserFileRepository;

/**
 * Main application entry point for the Budget Management System.
 * Initializes all repositories, services, and starts the console user interface.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class BudgetApp {
	
	/**
	 * Main method - entry point of the application.
	 * Initializes the dependency injection container manually and starts the UI.
	 * 
	 * @param args Command line arguments (not used)
	 */
	public static void main(String[] args) {
		// initialize repositories
		UserFileRepository userRepo = new UserFileRepository();
		BudgetFileRepository budgetRepo = new BudgetFileRepository();
		ExpenseFileRepository expenseRepo = new ExpenseFileRepository();
		GoalFileRepository goalRepo = new GoalFileRepository();

		// initialize services
		UserAuthService userAuthService = new UserAuthService(userRepo);
		BudgetService budgetService = new BudgetService(budgetRepo);
		ExpenseService expenseService = new ExpenseService(expenseRepo);
		GoalService goalService = new GoalService(goalRepo);
		FinancialSummaryService financialSummaryService = new FinancialSummaryService(budgetRepo, expenseRepo,
				goalRepo);
		CategoryGraphService categoryGraphService = new CategoryGraphService(expenseService);

		// start UI
		ConsoleUI ui = new ConsoleUI(userAuthService, budgetService, expenseService, goalService,
				financialSummaryService, categoryGraphService);
		ui.start();
	}
}
