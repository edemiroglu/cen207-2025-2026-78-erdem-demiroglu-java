package com.hyildizoglu.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.hyildizoglu.algorithms.graph.CategoryGraphService;
import com.hyildizoglu.models.User;

/**
 * Panel for category graph operations.
 * Provides an interface for managing category relationships and performing
 * graph traversals (BFS/DFS) on the category hierarchy.
 * 
 * <p>Features include:</p>
 * <ul>
 *   <li>Add parent-child relationships between categories</li>
 *   <li>BFS (Breadth-First Search) traversal visualization</li>
 *   <li>DFS (Depth-First Search) traversal visualization</li>
 *   <li>Category hierarchy spending calculation</li>
 * </ul>
 * 
 * <p>The panel displays a control section on the left for input and buttons,
 * and an output area on the right showing operation results.</p>
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 * @since 1.0
 * @see CategoryGraphService
 */
public class CategoryGraphPanel extends JPanel {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;
    
    /** Currently logged-in user. */
    private final User user;
    
    /** Service for category graph operations. */
    private final CategoryGraphService categoryGraphService;
    
    /** Text area for displaying operation output. */
    private JTextArea outputArea;
    
    /** Text field for parent category ID input. */
    private JTextField parentField;
    
    /** Text field for child category ID input. */
    private JTextField childField;

    /**
     * Creates a new CategoryGraphPanel for the specified user.
     * 
     * @param user                 The currently logged-in user. Must not be null.
     * @param categoryGraphService Service for category graph operations. Must not be null.
     * @throws NullPointerException if user or categoryGraphService is null
     */
    public CategoryGraphPanel(User user, CategoryGraphService categoryGraphService) {
        this.user = user;
        this.categoryGraphService = categoryGraphService;
        initializeUI();
    }

    /**
     * Initializes the user interface components.
     * Creates the header, control panel, and output area.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BudgetGUI.BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel("Category Graph Operations");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(BudgetGUI.TEXT_COLOR);
        add(headerLabel, BorderLayout.NORTH);

        // Main content with controls and output
        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setBackground(BudgetGUI.BG_COLOR);
        mainContent.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Left panel - Controls
        JPanel controlPanel = createControlPanel();
        mainContent.add(controlPanel, BorderLayout.WEST);

        // Right panel - Output
        JPanel outputPanel = createOutputPanel();
        mainContent.add(outputPanel, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    /**
     * Creates the control panel with input fields and operation buttons.
     * 
     * @return A configured control panel
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        panel.setPreferredSize(new Dimension(300, 0));

        // Add Relation section
        JLabel addTitle = new JLabel("Add Category Relation");
        addTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addTitle.setForeground(BudgetGUI.TEXT_COLOR);
        addTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(addTitle);
        panel.add(Box.createVerticalStrut(15));

        // Parent category field
        JLabel parentLabel = new JLabel("Parent Category ID:");
        parentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        parentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(parentLabel);
        panel.add(Box.createVerticalStrut(5));
        
        parentField = new JTextField();
        parentField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        parentField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(parentField);
        panel.add(Box.createVerticalStrut(10));

        // Child category field
        JLabel childLabel = new JLabel("Child Category ID:");
        childLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        childLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(childLabel);
        panel.add(Box.createVerticalStrut(5));
        
        childField = new JTextField();
        childField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        childField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(childField);
        panel.add(Box.createVerticalStrut(15));

        // Add relation button
        JButton addButton = createStyledButton("Add Relation", BudgetGUI.ACCENT_COLOR);
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addButton.addActionListener(e -> addRelation());
        panel.add(addButton);

        panel.add(Box.createVerticalStrut(30));

        // Traversal section
        JLabel traversalTitle = new JLabel("Traversal Operations");
        traversalTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        traversalTitle.setForeground(BudgetGUI.TEXT_COLOR);
        traversalTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(traversalTitle);
        panel.add(Box.createVerticalStrut(15));

        // BFS button
        JButton bfsButton = createStyledButton("BFS Traversal", BudgetGUI.PRIMARY_COLOR);
        bfsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        bfsButton.addActionListener(e -> performBFS());
        panel.add(bfsButton);
        panel.add(Box.createVerticalStrut(10));

        // DFS button
        JButton dfsButton = createStyledButton("DFS Traversal", BudgetGUI.PRIMARY_COLOR);
        dfsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        dfsButton.addActionListener(e -> performDFS());
        panel.add(dfsButton);
        panel.add(Box.createVerticalStrut(10));

        // Category spending button
        JButton spendingButton = createStyledButton("Category Spending", BudgetGUI.SECONDARY_COLOR);
        spendingButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        spendingButton.addActionListener(e -> calculateSpending());
        panel.add(spendingButton);

        // Spacer
        panel.add(Box.createVerticalGlue());

        // Clear output button
        JButton clearButton = createStyledButton("Clear Output", new Color(150, 150, 150));
        clearButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        clearButton.addActionListener(e -> outputArea.setText(""));
        panel.add(clearButton);

        return panel;
    }

    /**
     * Creates the output panel for displaying operation results.
     * 
     * @return A configured output panel with scrollable text area
     */
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Output title
        JLabel outputTitle = new JLabel("Output");
        outputTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        outputTitle.setForeground(BudgetGUI.TEXT_COLOR);
        panel.add(outputTitle, BorderLayout.NORTH);

        // Output text area
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        outputArea.setEditable(false);
        outputArea.setMargin(new Insets(10, 10, 10, 10));
        outputArea.setBackground(new Color(248, 249, 250));
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(new EmptyBorder(10, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Adds a category relation (parent to child) using the input fields.
     * Validates input and displays success/error in the output area.
     */
    private void addRelation() {
        try {
            int parentId = Integer.parseInt(parentField.getText().trim());
            int childId = Integer.parseInt(childField.getText().trim());
            
            categoryGraphService.addCategoryRelation(parentId, childId);
            
            appendOutput("✓ Added relation: Category " + parentId + " -> Category " + childId + "\n");
            parentField.setText("");
            childField.setText("");
        } catch (NumberFormatException e) {
            showError("Please enter valid category IDs.");
        }
    }

    /**
     * Performs BFS (Breadth-First Search) traversal from a user-specified category.
     * Prompts for starting category ID and displays traversal order.
     */
    private void performBFS() {
        String input = JOptionPane.showInputDialog(this, 
                "Enter starting category ID:", 
                "BFS Traversal", 
                JOptionPane.QUESTION_MESSAGE);
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                int startId = Integer.parseInt(input.trim());
                List<Integer> result = categoryGraphService.traverseCategoryHierarchyBFS(startId);
                
                appendOutput("\n--- BFS Traversal from Category " + startId + " ---\n");
                if (result.isEmpty()) {
                    appendOutput("No connected categories found.\n");
                } else {
                    appendOutput("Order: " + result.toString() + "\n");
                    for (int i = 0; i < result.size(); i++) {
                        appendOutput("  " + (i + 1) + ". Category " + result.get(i) + "\n");
                    }
                }
            } catch (NumberFormatException e) {
                showError("Please enter a valid category ID.");
            }
        }
    }

    /**
     * Performs DFS (Depth-First Search) traversal from a user-specified category.
     * Prompts for starting category ID and displays traversal order.
     */
    private void performDFS() {
        String input = JOptionPane.showInputDialog(this, 
                "Enter starting category ID:", 
                "DFS Traversal", 
                JOptionPane.QUESTION_MESSAGE);
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                int startId = Integer.parseInt(input.trim());
                List<Integer> result = categoryGraphService.traverseCategoryHierarchyDFS(startId);
                
                appendOutput("\n--- DFS Traversal from Category " + startId + " ---\n");
                if (result.isEmpty()) {
                    appendOutput("No connected categories found.\n");
                } else {
                    appendOutput("Order: " + result.toString() + "\n");
                    for (int i = 0; i < result.size(); i++) {
                        appendOutput("  " + (i + 1) + ". Category " + result.get(i) + "\n");
                    }
                }
            } catch (NumberFormatException e) {
                showError("Please enter a valid category ID.");
            }
        }
    }

    /**
     * Calculates and displays spending for a category hierarchy.
     * Prompts for root category ID and shows spending breakdown.
     */
    private void calculateSpending() {
        String input = JOptionPane.showInputDialog(this, 
                "Enter root category ID:", 
                "Category Hierarchy Spending", 
                JOptionPane.QUESTION_MESSAGE);
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                int rootId = Integer.parseInt(input.trim());
                Map<Integer, BigDecimal> spending = 
                        categoryGraphService.calculateCategoryHierarchySpending(user.getId(), rootId);
                
                appendOutput("\n--- Category Hierarchy Spending (Root: " + rootId + ") ---\n");
                if (spending.isEmpty()) {
                    appendOutput("No spending data found for this category hierarchy.\n");
                } else {
                    BigDecimal total = BigDecimal.ZERO;
                    for (Map.Entry<Integer, BigDecimal> entry : spending.entrySet()) {
                        appendOutput("  Category " + entry.getKey() + ": ₺" + entry.getValue() + "\n");
                        total = total.add(entry.getValue());
                    }
                    appendOutput("  -------------------\n");
                    appendOutput("  Total: ₺" + total + "\n");
                }
            } catch (NumberFormatException e) {
                showError("Please enter a valid category ID.");
            }
        }
    }

    /**
     * Appends text to the output area and scrolls to show the new content.
     * 
     * @param text The text to append
     */
    private void appendOutput(String text) {
        outputArea.append(text);
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    /**
     * Creates a styled button with consistent appearance.
     * 
     * @param text  The button text
     * @param color The background color
     * @return A configured button
     */
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 35));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Shows an error message dialog.
     * 
     * @param message The error message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
