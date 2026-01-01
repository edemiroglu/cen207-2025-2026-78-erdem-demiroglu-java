package com.hyildizoglu.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

import com.hyildizoglu.budgetCreation.BudgetService;
import com.hyildizoglu.expenseLogging.ExpenseService;
import com.hyildizoglu.financialSummary.FinancialSummaryService;
import com.hyildizoglu.models.Budget;
import com.hyildizoglu.models.Expense;
import com.hyildizoglu.models.Goal;
import com.hyildizoglu.models.User;
import com.hyildizoglu.savingsGoal.GoalService;

/**
 * Dashboard panel showing an overview of the user's financial status.
 * Displays summary cards with key metrics and recent activity.
 * 
 * <p>The dashboard provides at-a-glance information including:</p>
 * <ul>
 *   <li>Total budget limit across all budgets</li>
 *   <li>Total expenses spent</li>
 *   <li>Remaining budget (with warning if exceeded)</li>
 *   <li>Goal completion status</li>
 *   <li>Recent expense transactions</li>
 * </ul>
 * 
 * <p>This panel is the default view shown after successful login,
 * providing users with an immediate overview of their financial health.</p>
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 * @since 1.0
 * @see FinancialSummaryService
 * @see BudgetService
 * @see ExpenseService
 * @see GoalService
 */
public class DashboardPanel extends JPanel {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;
    
    /** Currently logged-in user whose data is displayed. */
    private final User user;
    
    /** Service for budget operations. */
    private final BudgetService budgetService;
    
    /** Service for expense operations. */
    private final ExpenseService expenseService;
    
    /** Service for goal operations. */
    private final GoalService goalService;
    
    /** Service for financial summary calculations. */
    private final FinancialSummaryService financialSummaryService;

    /**
     * Creates a new DashboardPanel for the specified user.
     * 
     * @param user                    The currently logged-in user. Must not be null.
     * @param budgetService           Service for budget operations. Must not be null.
     * @param expenseService          Service for expense operations. Must not be null.
     * @param goalService             Service for goal operations. Must not be null.
     * @param financialSummaryService Service for financial summaries. Must not be null.
     * @throws NullPointerException if any parameter is null
     */
    public DashboardPanel(User user, BudgetService budgetService, ExpenseService expenseService,
            GoalService goalService, FinancialSummaryService financialSummaryService) {
        this.user = user;
        this.budgetService = budgetService;
        this.expenseService = expenseService;
        this.goalService = goalService;
        this.financialSummaryService = financialSummaryService;
        
        initializeUI();
    }

    /**
     * Initializes the user interface components.
     * Creates the header, summary cards, and recent activity sections.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BudgetGUI.BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel("Dashboard");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(BudgetGUI.TEXT_COLOR);
        add(headerLabel, BorderLayout.NORTH);

        // Main content
        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setBackground(BudgetGUI.BG_COLOR);
        mainContent.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Summary cards row
        JPanel cardsPanel = createSummaryCards();
        mainContent.add(cardsPanel, BorderLayout.NORTH);

        // Recent activity section
        JPanel activityPanel = createRecentActivityPanel();
        mainContent.add(activityPanel, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    /**
     * Creates the summary cards panel with key financial metrics.
     * 
     * @return A panel containing four summary cards
     */
    private JPanel createSummaryCards() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(BudgetGUI.BG_COLOR);

        // Calculate metrics
        BigDecimal totalBudget = financialSummaryService.calculateTotalBudgetLimit(user.getId());
        BigDecimal totalExpenses = financialSummaryService.calculateTotalExpenses(user.getId());
        BigDecimal remaining = financialSummaryService.calculateRemainingBudget(user.getId());
        
        // Get counts
        List<Budget> budgets = budgetService.listBudgetsForUser(user.getId());
        List<Expense> expenses = expenseService.listExpensesForUser(user.getId());
        List<Goal> goals = goalService.listGoalsForUser(user.getId());
        long completedGoals = goals.stream().filter(Goal::isCompleted).count();

        // Create cards
        panel.add(createCard("Total Budget", "₺" + totalBudget.toString(), BudgetGUI.PRIMARY_COLOR, 
                budgets.size() + " active budgets"));
        panel.add(createCard("Total Expenses", "₺" + totalExpenses.toString(), BudgetGUI.ERROR_COLOR,
                expenses.size() + " transactions"));
        panel.add(createCard("Remaining", "₺" + remaining.toString(), 
                remaining.compareTo(BigDecimal.ZERO) >= 0 ? BudgetGUI.SUCCESS_COLOR : BudgetGUI.ERROR_COLOR,
                remaining.compareTo(BigDecimal.ZERO) >= 0 ? "Within budget" : "Budget exceeded!"));
        panel.add(createCard("Goals", completedGoals + "/" + goals.size() + " completed", BudgetGUI.ACCENT_COLOR,
                goals.size() + " total goals"));

        return panel;
    }

    /**
     * Creates a single summary card with title, value, and subtitle.
     * 
     * @param title    The card title (metric name)
     * @param value    The main value to display
     * @param color    The accent color for the card
     * @param subtitle Additional context text
     * @return A configured card panel
     */
    private JPanel createCard(String title, String value, Color color, String subtitle) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(3, 0, 0, 0, color),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(BudgetGUI.TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);

        card.add(Box.createVerticalStrut(10));

        // Value label
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(valueLabel);

        card.add(Box.createVerticalStrut(5));

        // Subtitle label
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitleLabel.setForeground(new Color(150, 150, 150));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(subtitleLabel);

        return card;
    }

    /**
     * Creates the recent activity panel showing latest expenses.
     * 
     * @return A panel displaying recent expense transactions
     */
    private JPanel createRecentActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Section title
        JLabel titleLabel = new JLabel("Recent Expenses");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(BudgetGUI.TEXT_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Expense list
        List<Expense> expenses = expenseService.listExpensesForUser(user.getId());
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        if (expenses.isEmpty()) {
            JLabel emptyLabel = new JLabel("No expenses recorded yet.");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            emptyLabel.setForeground(new Color(150, 150, 150));
            listPanel.add(emptyLabel);
        } else {
            // Show last 5 expenses
            int count = Math.min(expenses.size(), 5);
            for (int i = expenses.size() - 1; i >= Math.max(0, expenses.size() - count); i--) {
                Expense expense = expenses.get(i);
                JPanel expenseRow = createExpenseRow(expense);
                listPanel.add(expenseRow);
                if (i > Math.max(0, expenses.size() - count)) {
                    listPanel.add(Box.createVerticalStrut(10));
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates a row displaying a single expense.
     * 
     * @param expense The expense to display
     * @return A panel representing the expense row
     */
    private JPanel createExpenseRow(Expense expense) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(248, 249, 250));
        row.setBorder(new EmptyBorder(10, 15, 10, 15));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Left side: description and date
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(new Color(248, 249, 250));
        
        JLabel descLabel = new JLabel(expense.getDescription());
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(BudgetGUI.TEXT_COLOR);
        leftPanel.add(descLabel);
        
        JLabel dateLabel = new JLabel("  •  " + expense.getDate().toString());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateLabel.setForeground(new Color(150, 150, 150));
        leftPanel.add(dateLabel);
        
        row.add(leftPanel, BorderLayout.WEST);

        // Right side: amount
        JLabel amountLabel = new JLabel("-₺" + expense.getAmount().toString());
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        amountLabel.setForeground(BudgetGUI.ERROR_COLOR);
        row.add(amountLabel, BorderLayout.EAST);

        return row;
    }
}
