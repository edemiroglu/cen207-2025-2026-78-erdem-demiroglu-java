package com.hyildizoglu.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import com.hyildizoglu.algorithms.graph.CategoryGraphService;
import com.hyildizoglu.budgetCreation.BudgetService;
import com.hyildizoglu.expenseLogging.ExpenseService;
import com.hyildizoglu.financialSummary.FinancialSummaryService;
import com.hyildizoglu.models.User;
import com.hyildizoglu.savingsGoal.GoalService;

/**
 * Main application panel with sidebar navigation and content area.
 * Provides access to all application features through a modern sidebar menu.
 * 
 * <p>The panel uses a two-column layout with:</p>
 * <ul>
 *   <li>Left sidebar with navigation menu buttons</li>
 *   <li>Right content area using CardLayout for panel switching</li>
 * </ul>
 * 
 * <p>Available navigation options:</p>
 * <ul>
 *   <li>Dashboard - Overview of financial status</li>
 *   <li>Budgets - Budget management operations</li>
 *   <li>Expenses - Expense tracking and logging</li>
 *   <li>Goals - Savings goal management</li>
 *   <li>Summary - Financial reports and analysis</li>
 *   <li>Categories - Category graph operations</li>
 *   <li>Logout - Return to login screen</li>
 * </ul>
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 * @since 1.0
 * @see BudgetGUI
 * @see DashboardPanel
 * @see BudgetPanel
 * @see ExpensePanel
 * @see GoalPanel
 * @see SummaryPanel
 * @see CategoryGraphPanel
 */
public class MainPanel extends JPanel {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;
    
    /** Reference to the main GUI frame for logout callback. */
    private final BudgetGUI mainFrame;
    
    /** Currently logged-in user. */
    private final User user;
    
    /** Content panel using CardLayout for view switching. */
    private JPanel contentPanel;
    
    /** CardLayout manager for the content area. */
    private CardLayout contentCardLayout;
    
    /** Currently selected menu button for visual highlighting. */
    private JButton selectedButton;

    /**
     * Creates a new MainPanel with all necessary services.
     * 
     * @param mainFrame              Reference to main GUI frame. Must not be null.
     * @param user                   Currently logged-in user. Must not be null.
     * @param budgetService          Service for budget operations. Must not be null.
     * @param expenseService         Service for expense operations. Must not be null.
     * @param goalService            Service for goal operations. Must not be null.
     * @param financialSummaryService Service for financial summaries. Must not be null.
     * @param categoryGraphService   Service for category graph operations. Must not be null.
     * @throws NullPointerException if any parameter is null
     */
    public MainPanel(BudgetGUI mainFrame, User user, BudgetService budgetService,
            ExpenseService expenseService, GoalService goalService,
            FinancialSummaryService financialSummaryService, CategoryGraphService categoryGraphService) {
        this.mainFrame = mainFrame;
        this.user = user;
        
        setLayout(new BorderLayout());
        setBackground(BudgetGUI.BG_COLOR);
        
        // Create sidebar navigation
        JPanel sidebar = createSidebar(budgetService, expenseService, goalService, 
                financialSummaryService, categoryGraphService);
        add(sidebar, BorderLayout.WEST);
        
        // Create content area with CardLayout
        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);
        contentPanel.setBackground(BudgetGUI.BG_COLOR);
        
        // Add all feature panels
        contentPanel.add(new DashboardPanel(user, budgetService, expenseService, goalService, 
                financialSummaryService), "dashboard");
        contentPanel.add(new BudgetPanel(user, budgetService), "budget");
        contentPanel.add(new ExpensePanel(user, expenseService, budgetService), "expense");
        contentPanel.add(new GoalPanel(user, goalService), "goal");
        contentPanel.add(new SummaryPanel(user, financialSummaryService, budgetService, goalService), "summary");
        contentPanel.add(new CategoryGraphPanel(user, categoryGraphService), "category");
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Show dashboard by default
        contentCardLayout.show(contentPanel, "dashboard");
    }

    /**
     * Creates the sidebar panel with navigation menu.
     * Includes user info header and menu buttons for all features.
     * 
     * @param budgetService          Service for budget operations
     * @param expenseService         Service for expense operations
     * @param goalService            Service for goal operations
     * @param financialSummaryService Service for financial summaries
     * @param categoryGraphService   Service for category graph operations
     * @return A configured sidebar panel
     */
    private JPanel createSidebar(BudgetService budgetService, ExpenseService expenseService,
            GoalService goalService, FinancialSummaryService financialSummaryService,
            CategoryGraphService categoryGraphService) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BudgetGUI.SECONDARY_COLOR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Header with user info
        JPanel headerPanel = createHeaderPanel();
        sidebar.add(headerPanel);
        
        sidebar.add(Box.createVerticalStrut(20));

        // Navigation buttons
        JButton dashboardBtn = createMenuButton("📊 Dashboard", "dashboard");
        selectedButton = dashboardBtn;
        dashboardBtn.setBackground(BudgetGUI.PRIMARY_COLOR);
        sidebar.add(dashboardBtn);
        
        sidebar.add(Box.createVerticalStrut(5));
        
        JButton budgetBtn = createMenuButton("💰 Budgets", "budget");
        sidebar.add(budgetBtn);
        
        sidebar.add(Box.createVerticalStrut(5));
        
        JButton expenseBtn = createMenuButton("💳 Expenses", "expense");
        sidebar.add(expenseBtn);
        
        sidebar.add(Box.createVerticalStrut(5));
        
        JButton goalBtn = createMenuButton("🎯 Goals", "goal");
        sidebar.add(goalBtn);
        
        sidebar.add(Box.createVerticalStrut(5));
        
        JButton summaryBtn = createMenuButton("📈 Summary", "summary");
        sidebar.add(summaryBtn);
        
        sidebar.add(Box.createVerticalStrut(5));
        
        JButton categoryBtn = createMenuButton("🔗 Categories", "category");
        sidebar.add(categoryBtn);
        
        // Spacer to push logout to bottom
        sidebar.add(Box.createVerticalGlue());
        
        // Logout button
        JButton logoutBtn = createMenuButton("🚪 Logout", null);
        logoutBtn.setBackground(BudgetGUI.ERROR_COLOR);
        logoutBtn.addActionListener(e -> mainFrame.logout());
        sidebar.add(logoutBtn);
        
        sidebar.add(Box.createVerticalStrut(20));

        return sidebar;
    }

    /**
     * Creates the header panel with app name and user welcome message.
     * 
     * @return A configured header panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BudgetGUI.PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 15, 20, 15));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JLabel appLabel = new JLabel("Budget Manager");
        appLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appLabel.setForeground(Color.WHITE);
        appLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(appLabel);
        
        headerPanel.add(Box.createVerticalStrut(5));
        
        JLabel userLabel = new JLabel("Welcome, " + user.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(new Color(200, 200, 200));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(userLabel);
        
        return headerPanel;
    }

    /**
     * Creates a menu button for the sidebar navigation.
     * Handles click events to switch content panels and update selection state.
     * 
     * @param text     The button text to display
     * @param cardName The CardLayout card name to show (null for logout button)
     * @return A configured menu button with hover effects
     */
    private JButton createMenuButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(BudgetGUI.SECONDARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (cardName != null) {
            button.addActionListener(e -> {
                contentCardLayout.show(contentPanel, cardName);
                
                // Update selected button visual state
                if (selectedButton != null) {
                    selectedButton.setBackground(BudgetGUI.SECONDARY_COLOR);
                }
                button.setBackground(BudgetGUI.PRIMARY_COLOR);
                selectedButton = button;
            });
        }
        
        // Add hover effect for non-selected buttons
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            /**
             * Highlights the button on mouse enter.
             * @param evt The mouse event
             */
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != selectedButton) {
                    button.setBackground(new Color(70, 90, 110));
                }
            }
            
            /**
             * Removes highlight on mouse exit.
             * @param evt The mouse event
             */
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != selectedButton) {
                    button.setBackground(BudgetGUI.SECONDARY_COLOR);
                }
            }
        });
        
        return button;
    }
}
