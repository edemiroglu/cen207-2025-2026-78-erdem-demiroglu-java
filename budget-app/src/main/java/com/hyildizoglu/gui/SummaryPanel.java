package com.hyildizoglu.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.hyildizoglu.budgetCreation.BudgetService;
import com.hyildizoglu.financialSummary.FinancialSummaryService;
import com.hyildizoglu.models.Budget;
import com.hyildizoglu.models.Expense;
import com.hyildizoglu.models.Goal;
import com.hyildizoglu.models.User;
import com.hyildizoglu.savingsGoal.GoalService;

/**
 * Panel for displaying financial summaries and reports.
 * Provides various views of financial data including metrics, comparisons, and analysis.
 * 
 * <p>Available reports include:</p>
 * <ul>
 *   <li>General Summary - Overall budget and expense totals</li>
 *   <li>Budget vs Actual - Compare planned budget to actual spending</li>
 *   <li>Top Expenses - Highest expense transactions</li>
 *   <li>Top Categories - Categories with highest spending</li>
 *   <li>Savings Progress - Goal completion tracking with progress bars</li>
 *   <li>Sorted Expenses - Expenses sorted by amount</li>
 * </ul>
 * 
 * <p>The panel uses a menu-based navigation allowing users to switch
 * between different report views.</p>
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 * @since 1.0
 * @see FinancialSummaryService
 * @see BudgetService
 * @see GoalService
 */
public class SummaryPanel extends JPanel {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;
    
    /** Currently logged-in user whose data is displayed. */
    private final User user;
    
    /** Service for generating financial summaries. */
    private final FinancialSummaryService financialSummaryService;
    
    /** Service for budget operations. */
    private final BudgetService budgetService;
    
    /** Service for goal operations. */
    private final GoalService goalService;
    
    /** Content area for displaying report data. */
    private JPanel contentArea;

    /**
     * Creates a new SummaryPanel for the specified user.
     * 
     * @param user                    The currently logged-in user. Must not be null.
     * @param financialSummaryService Service for financial summaries. Must not be null.
     * @param budgetService           Service for budget operations. Must not be null.
     * @param goalService             Service for goal operations. Must not be null.
     * @throws NullPointerException if any parameter is null
     */
    public SummaryPanel(User user, FinancialSummaryService financialSummaryService,
            BudgetService budgetService, GoalService goalService) {
        this.user = user;
        this.financialSummaryService = financialSummaryService;
        this.budgetService = budgetService;
        this.goalService = goalService;
        initializeUI();
    }

    /**
     * Initializes the user interface components.
     * Creates the header, menu panel, and content area.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BudgetGUI.BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel("Financial Summary");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(BudgetGUI.TEXT_COLOR);
        add(headerLabel, BorderLayout.NORTH);

        // Main content with menu and report area
        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setBackground(BudgetGUI.BG_COLOR);
        mainContent.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Left side - Report menu
        JPanel menuPanel = createMenuPanel();
        mainContent.add(menuPanel, BorderLayout.WEST);

        // Right side - Report content
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Show general summary by default
        showGeneralSummary();
        
        mainContent.add(contentArea, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    /**
     * Creates the menu panel with report selection buttons.
     * 
     * @return A panel containing menu buttons for each report type
     */
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BudgetGUI.BG_COLOR);
        panel.setPreferredSize(new Dimension(200, 0));

        // Menu items with icons and actions
        String[][] menuItems = {
            {"📊 General Summary", "general"},
            {"💰 Budget vs Actual", "budget"},
            {"📈 Top Expenses", "top"},
            {"📂 Top Categories", "categories"},
            {"🎯 Savings Progress", "savings"},
            {"📋 Sorted Expenses", "sorted"}
        };

        for (String[] item : menuItems) {
            JButton btn = createMenuButton(item[0], item[1]);
            panel.add(btn);
            panel.add(Box.createVerticalStrut(10));
        }

        return panel;
    }

    /**
     * Creates a menu button for report selection.
     * 
     * @param text   The button text to display
     * @param action The action identifier for the report
     * @return A configured menu button with hover effects
     */
    private JButton createMenuButton(String text, String action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(BudgetGUI.TEXT_COLOR);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(200, 40));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(10, 15, 10, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Handle button click to show appropriate report
        button.addActionListener(e -> {
            switch (action) {
                case "general": showGeneralSummary(); break;
                case "budget": showBudgetVsActual(); break;
                case "top": showTopExpenses(); break;
                case "categories": showTopCategories(); break;
                case "savings": showSavingsProgress(); break;
                case "sorted": showSortedExpenses(); break;
            }
        });
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 240, 240));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
        
        return button;
    }

    /**
     * Displays the general summary report with total metrics.
     */
    private void showGeneralSummary() {
        contentArea.removeAll();
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("General Summary");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(BudgetGUI.TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        // Calculate and display metrics
        BigDecimal totalBudget = financialSummaryService.calculateTotalBudgetLimit(user.getId());
        BigDecimal totalExpenses = financialSummaryService.calculateTotalExpenses(user.getId());
        BigDecimal remaining = financialSummaryService.calculateRemainingBudget(user.getId());

        addSummaryRow(panel, "Total Budget Limit", "₺" + totalBudget.toString(), BudgetGUI.PRIMARY_COLOR);
        addSummaryRow(panel, "Total Expenses", "₺" + totalExpenses.toString(), BudgetGUI.ERROR_COLOR);
        addSummaryRow(panel, "Remaining Budget", "₺" + remaining.toString(), 
                remaining.compareTo(BigDecimal.ZERO) >= 0 ? BudgetGUI.SUCCESS_COLOR : BudgetGUI.ERROR_COLOR);

        contentArea.add(panel, BorderLayout.NORTH);
        contentArea.revalidate();
        contentArea.repaint();
    }

    /**
     * Displays the budget vs actual spending comparison.
     */
    private void showBudgetVsActual() {
        contentArea.removeAll();
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Budget vs Actual Spending");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(BudgetGUI.TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        List<Budget> budgets = budgetService.listBudgetsForUser(user.getId());
        
        if (budgets.isEmpty()) {
            JLabel emptyLabel = new JLabel("No budgets found.");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            emptyLabel.setForeground(new Color(150, 150, 150));
            panel.add(emptyLabel);
        } else {
            for (Budget budget : budgets) {
                Map<String, BigDecimal> comparison = financialSummaryService.budgetVsActual(user.getId(), budget.getId());
                if (!comparison.isEmpty()) {
                    JPanel budgetPanel = createBudgetComparisonPanel(budget.getName(), comparison);
                    panel.add(budgetPanel);
                    panel.add(Box.createVerticalStrut(15));
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        contentArea.add(scrollPane, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    /**
     * Creates a panel showing budget comparison data.
     * 
     * @param name The budget name
     * @param data Map containing budgetLimit, actualSpending, difference, percentage
     * @return A configured panel showing the comparison
     */
    private JPanel createBudgetComparisonPanel(String name, Map<String, BigDecimal> data) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(BudgetGUI.PRIMARY_COLOR);
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(10));

        BigDecimal percentage = data.get("percentage");
        boolean overBudget = data.get("difference").compareTo(BigDecimal.ZERO) < 0;

        JLabel limitLabel = new JLabel("Limit: ₺" + data.get("budgetLimit"));
        limitLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(limitLabel);

        JLabel spentLabel = new JLabel("Spent: ₺" + data.get("actualSpending"));
        spentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(spentLabel);

        JLabel percentLabel = new JLabel("Usage: " + percentage + "%" + (overBudget ? " ⚠️ OVER BUDGET" : ""));
        percentLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        percentLabel.setForeground(overBudget ? BudgetGUI.ERROR_COLOR : BudgetGUI.SUCCESS_COLOR);
        panel.add(percentLabel);

        return panel;
    }

    /**
     * Displays the top N expenses report.
     */
    private void showTopExpenses() {
        contentArea.removeAll();
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Top 10 Expenses");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(BudgetGUI.TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        List<Expense> topExpenses = financialSummaryService.topNExpenses(user.getId(), 10);
        
        if (topExpenses.isEmpty()) {
            JLabel emptyLabel = new JLabel("No expenses found.");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            panel.add(emptyLabel);
        } else {
            int rank = 1;
            for (Expense expense : topExpenses) {
                JPanel row = createExpenseRow(rank++, expense);
                panel.add(row);
                panel.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        contentArea.add(scrollPane, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    /**
     * Creates a row for displaying a ranked expense.
     * 
     * @param rank    The expense rank
     * @param expense The expense to display
     * @return A panel representing the expense row
     */
    private JPanel createExpenseRow(int rank, Expense expense) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(248, 249, 250));
        row.setBorder(new EmptyBorder(10, 15, 10, 15));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rankLabel = new JLabel("#" + rank + "  " + expense.getDescription());
        rankLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        row.add(rankLabel, BorderLayout.WEST);

        JLabel amountLabel = new JLabel("₺" + expense.getAmount().toString());
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        amountLabel.setForeground(BudgetGUI.ERROR_COLOR);
        row.add(amountLabel, BorderLayout.EAST);

        return row;
    }

    /**
     * Displays the top categories by spending report.
     */
    private void showTopCategories() {
        contentArea.removeAll();
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Top Categories by Spending");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(BudgetGUI.TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        List<Map.Entry<Integer, BigDecimal>> topCategories = 
                financialSummaryService.topNCategoriesBySpending(user.getId(), 10);
        
        if (topCategories.isEmpty()) {
            JLabel emptyLabel = new JLabel("No category data found.");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            panel.add(emptyLabel);
        } else {
            int rank = 1;
            for (Map.Entry<Integer, BigDecimal> entry : topCategories) {
                JPanel row = createCategoryRow(rank++, entry.getKey(), entry.getValue());
                panel.add(row);
                panel.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        contentArea.add(scrollPane, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    /**
     * Creates a row for displaying a ranked category.
     * 
     * @param rank       The category rank
     * @param categoryId The category ID
     * @param amount     The total spending amount
     * @return A panel representing the category row
     */
    private JPanel createCategoryRow(int rank, int categoryId, BigDecimal amount) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(248, 249, 250));
        row.setBorder(new EmptyBorder(10, 15, 10, 15));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel("#" + rank + "  Category " + categoryId);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        row.add(nameLabel, BorderLayout.WEST);

        JLabel amountLabel = new JLabel("₺" + amount.toString());
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        amountLabel.setForeground(BudgetGUI.PRIMARY_COLOR);
        row.add(amountLabel, BorderLayout.EAST);

        return row;
    }

    /**
     * Displays the savings progress report with progress bars.
     */
    private void showSavingsProgress() {
        contentArea.removeAll();
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Savings Progress");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(BudgetGUI.TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        Map<Goal, Map<String, BigDecimal>> progress = financialSummaryService.savingsProgress(user.getId());
        
        if (progress.isEmpty()) {
            JLabel emptyLabel = new JLabel("No goals found.");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            panel.add(emptyLabel);
        } else {
            for (Map.Entry<Goal, Map<String, BigDecimal>> entry : progress.entrySet()) {
                Goal goal = entry.getKey();
                Map<String, BigDecimal> data = entry.getValue();
                JPanel goalPanel = createGoalProgressPanel(goal, data);
                panel.add(goalPanel);
                panel.add(Box.createVerticalStrut(15));
            }
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        contentArea.add(scrollPane, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    /**
     * Creates a panel showing goal progress with a progress bar.
     * 
     * @param goal The goal to display
     * @param data Map containing target, current, remaining, percentage
     * @return A configured panel showing goal progress
     */
    private JPanel createGoalProgressPanel(Goal goal, Map<String, BigDecimal> data) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(goal.getName() + (goal.isCompleted() ? " ✅" : ""));
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(goal.isCompleted() ? BudgetGUI.SUCCESS_COLOR : BudgetGUI.PRIMARY_COLOR);
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(10));

        JLabel targetLabel = new JLabel("Target: ₺" + data.get("target") + " | Current: ₺" + data.get("current"));
        targetLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(targetLabel);

        // Progress bar
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(data.get("percentage").intValue());
        progressBar.setStringPainted(true);
        progressBar.setString(data.get("percentage") + "%");
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        progressBar.setForeground(goal.isCompleted() ? BudgetGUI.SUCCESS_COLOR : BudgetGUI.PRIMARY_COLOR);
        panel.add(Box.createVerticalStrut(5));
        panel.add(progressBar);

        return panel;
    }

    /**
     * Displays expenses sorted by amount (descending).
     */
    private void showSortedExpenses() {
        contentArea.removeAll();
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Expenses (Sorted by Amount)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(BudgetGUI.TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        List<Expense> sorted = financialSummaryService.sortExpensesByAmountDescending(user.getId());
        
        if (sorted.isEmpty()) {
            JLabel emptyLabel = new JLabel("No expenses found.");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            panel.add(emptyLabel);
        } else {
            int rank = 1;
            for (Expense expense : sorted) {
                JPanel row = createExpenseRow(rank++, expense);
                panel.add(row);
                panel.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        contentArea.add(scrollPane, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    /**
     * Adds a summary row with label and value to the panel.
     * 
     * @param panel      The panel to add the row to
     * @param label      The metric label
     * @param value      The metric value
     * @param valueColor The color for the value text
     */
    private void addSummaryRow(JPanel panel, String label, String value, Color valueColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblLabel.setForeground(BudgetGUI.TEXT_COLOR);
        row.add(lblLabel, BorderLayout.WEST);

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valLabel.setForeground(valueColor);
        row.add(valLabel, BorderLayout.EAST);

        panel.add(row);
        
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(separator);
    }
}
