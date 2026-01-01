package com.hyildizoglu.gui;

import javax.swing.*;
import java.awt.*;

import com.hyildizoglu.algorithms.graph.CategoryGraphService;
import com.hyildizoglu.budgetCreation.BudgetFileRepository;
import com.hyildizoglu.budgetCreation.BudgetService;
import com.hyildizoglu.expenseLogging.ExpenseFileRepository;
import com.hyildizoglu.expenseLogging.ExpenseService;
import com.hyildizoglu.financialSummary.FinancialSummaryService;
import com.hyildizoglu.models.User;
import com.hyildizoglu.savingsGoal.GoalFileRepository;
import com.hyildizoglu.savingsGoal.GoalService;
import com.hyildizoglu.userAuthentication.UserAuthService;
import com.hyildizoglu.userAuthentication.UserFileRepository;

/**
 * Main GUI application entry point for the Budget Management System.
 * This class serves as the main frame that hosts all panels and manages
 * navigation between different views using CardLayout.
 * 
 * <p>The application provides a modern Swing-based graphical user interface
 * for managing personal finances including:</p>
 * <ul>
 *   <li>User authentication (login, registration, guest mode)</li>
 *   <li>Budget creation and management</li>
 *   <li>Expense tracking and history</li>
 *   <li>Savings goal setting and progress tracking</li>
 *   <li>Financial summaries and reports</li>
 *   <li>Category graph operations</li>
 * </ul>
 * 
 * <p>Usage example:</p>
 * <pre>
 * public static void main(String[] args) {
 *     SwingUtilities.invokeLater(() -> {
 *         BudgetGUI gui = new BudgetGUI();
 *         gui.setVisible(true);
 *     });
 * }
 * </pre>
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 * @since 1.0
 * @see LoginPanel
 * @see MainPanel
 */
public class BudgetGUI extends JFrame {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;
    
    /** Application window title displayed in the title bar. */
    private static final String APP_TITLE = "Budget Management System";
    
    /** 
     * Primary color used throughout the application theme.
     * A professional blue color suitable for financial applications.
     */
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    
    /** 
     * Secondary color used for sidebar and accents.
     * A dark slate color providing contrast with primary elements.
     */
    public static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    
    /** 
     * Accent color used for positive actions and highlights.
     * A green color indicating success or positive status.
     */
    public static final Color ACCENT_COLOR = new Color(46, 204, 113);
    
    /** 
     * Background color used for main content areas.
     * A light gray providing a clean, modern appearance.
     */
    public static final Color BG_COLOR = new Color(236, 240, 241);
    
    /** 
     * Text color used for labels and content.
     * A dark color ensuring readability.
     */
    public static final Color TEXT_COLOR = new Color(44, 62, 80);
    
    /** 
     * Error color used for error messages and warnings.
     * A red color indicating errors or negative status.
     */
    public static final Color ERROR_COLOR = new Color(231, 76, 60);
    
    /** 
     * Success color used for success messages.
     * A green color indicating successful operations.
     */
    public static final Color SUCCESS_COLOR = new Color(39, 174, 96);

    /** Service for user authentication operations including login, registration. */
    private final UserAuthService userAuthService;
    
    /** Service for budget CRUD operations. */
    private final BudgetService budgetService;
    
    /** Service for expense logging and management. */
    private final ExpenseService expenseService;
    
    /** Service for savings goal operations. */
    private final GoalService goalService;
    
    /** Service for generating financial summaries and reports. */
    private final FinancialSummaryService financialSummaryService;
    
    /** Service for category graph operations and traversals. */
    private final CategoryGraphService categoryGraphService;
    
    /** The currently authenticated user, null if not logged in. */
    private User currentUser;
    
    /** Main content panel using CardLayout for view switching. */
    private JPanel contentPanel;
    
    /** CardLayout manager for switching between login and main panels. */
    private CardLayout cardLayout;

    /**
     * Creates a new BudgetGUI application instance.
     * Initializes all required repositories and services, then sets up the UI.
     * 
     * <p>This constructor:</p>
     * <ol>
     *   <li>Creates file-based repositories for data persistence</li>
     *   <li>Initializes all service classes with their repositories</li>
     *   <li>Sets up the main UI with CardLayout for panel switching</li>
     *   <li>Displays the login panel as the initial view</li>
     * </ol>
     */
    public BudgetGUI() {
        // Initialize file-based repositories
        UserFileRepository userRepo = new UserFileRepository();
        BudgetFileRepository budgetRepo = new BudgetFileRepository();
        ExpenseFileRepository expenseRepo = new ExpenseFileRepository();
        GoalFileRepository goalRepo = new GoalFileRepository();

        // Initialize services with repositories
        this.userAuthService = new UserAuthService(userRepo);
        this.budgetService = new BudgetService(budgetRepo);
        this.expenseService = new ExpenseService(expenseRepo);
        this.goalService = new GoalService(goalRepo);
        this.financialSummaryService = new FinancialSummaryService(budgetRepo, expenseRepo, goalRepo);
        this.categoryGraphService = new CategoryGraphService(expenseService);

        initializeUI();
    }

    /**
     * Initializes the user interface components.
     * Sets up the main frame properties, look and feel, and initial panel.
     */
    private void initializeUI() {
        setTitle(APP_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        
        // Set system look and feel for native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default look and feel if system L&F unavailable
        }
        
        // Create main content panel with CardLayout for view switching
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_COLOR);
        
        // Add login panel as the initial view
        LoginPanel loginPanel = new LoginPanel(this, userAuthService);
        contentPanel.add(loginPanel, "login");
        
        add(contentPanel);
        
        // Show login panel first
        cardLayout.show(contentPanel, "login");
    }

    /**
     * Handles successful user login by transitioning to the main application view.
     * Creates and displays the main panel with all application features.
     * 
     * @param user The authenticated user object containing user information.
     *             Must not be null.
     * @throws NullPointerException if user is null
     * @see MainPanel
     */
    public void onLoginSuccess(User user) {
        this.currentUser = user;
        
        // Create and add main panel with all services
        MainPanel mainPanel = new MainPanel(this, user, budgetService, expenseService, 
                goalService, financialSummaryService, categoryGraphService);
        contentPanel.add(mainPanel, "main");
        
        // Switch to main panel
        cardLayout.show(contentPanel, "main");
    }

    /**
     * Logs out the current user and returns to the login screen.
     * Clears the current user reference and switches to the login panel.
     */
    public void logout() {
        this.currentUser = null;
        cardLayout.show(contentPanel, "login");
    }

    /**
     * Returns the currently logged-in user.
     * 
     * @return The current User object, or null if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Main method - entry point of the GUI application.
     * Creates and displays the main application window on the Event Dispatch Thread.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BudgetGUI gui = new BudgetGUI();
            gui.setVisible(true);
        });
    }
}
