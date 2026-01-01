package com.hyildizoglu.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Optional;

import com.hyildizoglu.core.InputValidator;
import com.hyildizoglu.models.User;
import com.hyildizoglu.userAuthentication.UserAuthService;

/**
 * Login panel providing user authentication functionality.
 * This panel allows users to log in with existing credentials, register new accounts,
 * or continue as a guest user.
 * 
 * <p>The panel features a modern, centered form design with:</p>
 * <ul>
 *   <li>Username and password input fields with validation</li>
 *   <li>Login button for existing users</li>
 *   <li>Register button for new users</li>
 *   <li>Guest mode button for anonymous access</li>
 *   <li>Status messages for feedback</li>
 * </ul>
 * 
 * <p>Input validation is performed using {@link InputValidator} to ensure
 * usernames and passwords meet security requirements before authentication.</p>
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 * @since 1.0
 * @see BudgetGUI
 * @see UserAuthService
 * @see InputValidator
 */
public class LoginPanel extends JPanel {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;
    
    /** Reference to the main GUI frame for navigation callbacks. */
    private final BudgetGUI mainFrame;
    
    /** Service handling user authentication operations. */
    private final UserAuthService userAuthService;
    
    /** Text field for entering username. */
    private JTextField usernameField;
    
    /** Password field for secure password entry. */
    private JPasswordField passwordField;
    
    /** Label for displaying status messages (success/error). */
    private JLabel statusLabel;

    /**
     * Creates a new LoginPanel with the specified main frame and auth service.
     * 
     * @param mainFrame      Reference to the main GUI frame for navigation.
     *                       Must not be null.
     * @param userAuthService Service for user authentication operations.
     *                        Must not be null.
     * @throws NullPointerException if mainFrame or userAuthService is null
     */
    public LoginPanel(BudgetGUI mainFrame, UserAuthService userAuthService) {
        this.mainFrame = mainFrame;
        this.userAuthService = userAuthService;
        initializeUI();
    }

    /**
     * Initializes the user interface components.
     * Creates a centered layout with the login form panel.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BudgetGUI.BG_COLOR);

        // Center panel with login form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BudgetGUI.BG_COLOR);
        
        JPanel formPanel = createFormPanel();
        centerPanel.add(formPanel);
        
        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the login form panel containing all input fields and buttons.
     * 
     * @return A configured JPanel containing the complete login form
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BudgetGUI.PRIMARY_COLOR, 2),
            new EmptyBorder(40, 50, 40, 50)
        ));

        // Application title
        JLabel titleLabel = new JLabel("Budget Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(BudgetGUI.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        
        panel.add(Box.createVerticalStrut(10));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Sign in to continue");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(BudgetGUI.TEXT_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitleLabel);
        
        panel.add(Box.createVerticalStrut(30));

        // Username field section
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        usernameLabel.setForeground(BudgetGUI.TEXT_COLOR);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(usernameLabel);
        
        panel.add(Box.createVerticalStrut(5));
        
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setMaximumSize(new Dimension(300, 35));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BudgetGUI.SECONDARY_COLOR),
            new EmptyBorder(5, 10, 5, 10)
        ));
        panel.add(usernameField);
        
        panel.add(Box.createVerticalStrut(15));

        // Password field section
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(BudgetGUI.TEXT_COLOR);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(passwordLabel);
        
        panel.add(Box.createVerticalStrut(5));
        
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(300, 35));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BudgetGUI.SECONDARY_COLOR),
            new EmptyBorder(5, 10, 5, 10)
        ));
        panel.add(passwordField);
        
        panel.add(Box.createVerticalStrut(20));

        // Status message label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(statusLabel);
        
        panel.add(Box.createVerticalStrut(10));

        // Login and Register buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);
        
        JButton loginButton = createStyledButton("Login", BudgetGUI.PRIMARY_COLOR);
        loginButton.addActionListener(e -> performLogin());
        buttonsPanel.add(loginButton);
        
        JButton registerButton = createStyledButton("Register", BudgetGUI.ACCENT_COLOR);
        registerButton.addActionListener(e -> performRegister());
        buttonsPanel.add(registerButton);
        
        panel.add(buttonsPanel);
        
        panel.add(Box.createVerticalStrut(15));
        
        // Guest mode button
        JButton guestButton = createStyledButton("Continue as Guest", BudgetGUI.SECONDARY_COLOR);
        guestButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        guestButton.addActionListener(e -> performGuestLogin());
        panel.add(guestButton);

        return panel;
    }

    /**
     * Creates a styled button with consistent appearance.
     * 
     * @param text  The button text to display
     * @param color The background color for the button
     * @return A configured JButton with hover effects
     */
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect for better user feedback
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            /**
             * Darkens the button color when mouse enters.
             * @param evt The mouse event
             */
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            
            /**
             * Restores the button color when mouse exits.
             * @param evt The mouse event
             */
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    /**
     * Performs the login operation with validation.
     * Validates username and password, then attempts authentication.
     * Shows appropriate success/error messages.
     */
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        try {
            InputValidator.validateUsername(username);
            InputValidator.validatePassword(password);
            
            Optional<User> user = userAuthService.login(username, password);
            if (user.isPresent()) {
                statusLabel.setForeground(BudgetGUI.SUCCESS_COLOR);
                statusLabel.setText("Login successful!");
                clearFields();
                mainFrame.onLoginSuccess(user.get());
            } else {
                showError("Invalid username or password.");
            }
        } catch (InputValidator.ValidationException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Performs the registration operation with validation.
     * Validates username and password, then attempts to create a new account.
     * Shows appropriate success/error messages.
     */
    private void performRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        try {
            InputValidator.validateUsername(username);
            InputValidator.validatePassword(password);
            
            Optional<User> user = userAuthService.register(username, password);
            if (user.isPresent()) {
                statusLabel.setForeground(BudgetGUI.SUCCESS_COLOR);
                statusLabel.setText("Registration successful! You can now login.");
            } else {
                showError("Username already exists.");
            }
        } catch (InputValidator.ValidationException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Performs guest login by creating a temporary guest user.
     * Guest users have full access but data is not persisted between sessions.
     */
    private void performGuestLogin() {
        User guest = userAuthService.createGuestUser();
        statusLabel.setForeground(BudgetGUI.SUCCESS_COLOR);
        statusLabel.setText("Logged in as guest.");
        clearFields();
        mainFrame.onLoginSuccess(guest);
    }

    /**
     * Displays an error message in the status label.
     * 
     * @param message The error message to display
     */
    private void showError(String message) {
        statusLabel.setForeground(BudgetGUI.ERROR_COLOR);
        statusLabel.setText(message);
    }

    /**
     * Clears all input fields and resets the status label.
     * Called after successful login or registration.
     */
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
    }
}
