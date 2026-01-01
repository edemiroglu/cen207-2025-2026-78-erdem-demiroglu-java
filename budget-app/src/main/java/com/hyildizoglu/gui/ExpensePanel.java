package com.hyildizoglu.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.hyildizoglu.budgetCreation.BudgetService;
import com.hyildizoglu.expenseLogging.ExpenseService;
import com.hyildizoglu.models.Budget;
import com.hyildizoglu.models.Expense;
import com.hyildizoglu.models.User;

/**
 * Panel for expense management operations.
 * Provides a complete interface for logging, viewing, updating, searching,
 * and deleting expenses.
 * 
 * <p>Features include:</p>
 * <ul>
 *   <li>Expense listing in a sortable table</li>
 *   <li>Add new expense with calendar date picker</li>
 *   <li>Edit existing expenses</li>
 *   <li>Delete expenses with confirmation</li>
 *   <li>Search expenses by description</li>
 *   <li>Undo last expense operation</li>
 * </ul>
 * 
 * <p>The panel integrates with {@link BudgetService} to provide budget
 * selection when logging new expenses.</p>
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 * @since 1.0
 * @see ExpenseService
 * @see Expense
 * @see DateChooserPanel
 */
public class ExpensePanel extends JPanel {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;
    
    /** Currently logged-in user whose expenses are displayed. */
    private final User user;
    
    /** Service for performing expense operations. */
    private final ExpenseService expenseService;
    
    /** Service for budget operations (used for budget selection). */
    private final BudgetService budgetService;
    
    /** Table component for displaying expenses. */
    private JTable expenseTable;
    
    /** Table model for managing expense data. */
    private DefaultTableModel tableModel;
    
    /** Text field for searching expenses. */
    private JTextField searchField;

    /**
     * Creates a new ExpensePanel for the specified user.
     * 
     * @param user           The currently logged-in user. Must not be null.
     * @param expenseService Service for expense operations. Must not be null.
     * @param budgetService  Service for budget operations. Must not be null.
     * @throws NullPointerException if any parameter is null
     */
    public ExpensePanel(User user, ExpenseService expenseService, BudgetService budgetService) {
        this.user = user;
        this.expenseService = expenseService;
        this.budgetService = budgetService;
        initializeUI();
    }

    /**
     * Initializes the user interface components.
     * Sets up the header with action buttons, search panel, and expense table.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BudgetGUI.BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header panel with title and action buttons
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BudgetGUI.BG_COLOR);
        
        JLabel headerLabel = new JLabel("Expense Management");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(BudgetGUI.TEXT_COLOR);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        // Action buttons panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setBackground(BudgetGUI.BG_COLOR);
        
        JButton undoButton = createStyledButton("↩ Undo", BudgetGUI.SECONDARY_COLOR);
        undoButton.addActionListener(e -> undoLastExpense());
        actionsPanel.add(undoButton);
        
        JButton addButton = createStyledButton("+ Add Expense", BudgetGUI.ACCENT_COLOR);
        addButton.addActionListener(e -> showAddDialog());
        actionsPanel.add(addButton);
        
        headerPanel.add(actionsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Content panel with search and table
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(BudgetGUI.BG_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Search panel
        JPanel searchPanel = createSearchPanel();
        contentPanel.add(searchPanel, BorderLayout.NORTH);

        // Expense table
        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Load initial data
        refreshTable();
    }

    /**
     * Creates the search panel with search field and buttons.
     * 
     * @return A configured JPanel containing search controls
     */
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setBackground(BudgetGUI.BG_COLOR);
        
        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(searchLabel);
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        searchPanel.add(searchField);
        
        JButton searchButton = createStyledButton("Search", BudgetGUI.PRIMARY_COLOR);
        searchButton.setPreferredSize(new Dimension(80, 30));
        searchButton.addActionListener(e -> searchExpenses());
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchButton);
        
        JButton clearButton = createStyledButton("Clear", BudgetGUI.SECONDARY_COLOR);
        clearButton.setPreferredSize(new Dimension(80, 30));
        clearButton.addActionListener(e -> {
            searchField.setText("");
            refreshTable();
        });
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(clearButton);
        
        return searchPanel;
    }

    /**
     * Creates the table panel with expense data display.
     * 
     * @return A configured JPanel containing the expense table
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Define table columns
        String[] columns = {"ID", "Description", "Amount", "Date", "Category", "Budget", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            /**
             * Only allow editing the actions column.
             * @param row The row index
             * @param column The column index
             * @return true if the column is the actions column
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        
        // Configure table appearance
        expenseTable = new JTable(tableModel);
        expenseTable.setRowHeight(45);
        expenseTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        expenseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        expenseTable.getTableHeader().setBackground(BudgetGUI.PRIMARY_COLOR);
        expenseTable.getTableHeader().setForeground(Color.WHITE);
        expenseTable.setSelectionBackground(new Color(232, 240, 254));
        expenseTable.setGridColor(new Color(230, 230, 230));
        
        // Configure column widths and renderers
        expenseTable.getColumn("ID").setPreferredWidth(50);
        expenseTable.getColumn("Actions").setPreferredWidth(150);
        expenseTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        expenseTable.getColumn("Actions").setCellEditor(new ButtonEditor(this));

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Refreshes the table with current expense data from the service.
     * Clears existing data and reloads from the database.
     */
    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Expense> expenses = expenseService.listExpensesForUser(user.getId());
        for (Expense expense : expenses) {
            tableModel.addRow(new Object[]{
                expense.getId(),
                expense.getDescription(),
                "₺" + expense.getAmount().toString(),
                expense.getDate().toString(),
                "Category " + expense.getCategoryId(),
                "Budget " + expense.getBudgetId(),
                "Edit / Delete"
            });
        }
    }

    /**
     * Searches expenses by description and updates the table.
     * Uses the search term from the search field.
     */
    private void searchExpenses() {
        String keyword = searchField.getText().trim();
        tableModel.setRowCount(0);
        List<Expense> expenses = expenseService.searchExpensesByDescription(user.getId(), keyword);
        for (Expense expense : expenses) {
            tableModel.addRow(new Object[]{
                expense.getId(),
                expense.getDescription(),
                "₺" + expense.getAmount().toString(),
                expense.getDate().toString(),
                "Category " + expense.getCategoryId(),
                "Budget " + expense.getBudgetId(),
                "Edit / Delete"
            });
        }
    }

    /**
     * Undoes the last expense operation.
     * Shows success or error message based on result.
     */
    private void undoLastExpense() {
        Expense undone = expenseService.undoLastExpense();
        if (undone != null) {
            refreshTable();
            showSuccess("Expense undone: " + undone.getDescription());
        } else {
            showError("No expense to undo.");
        }
    }

    /**
     * Shows the add expense dialog with calendar date picker.
     * Allows user to create a new expense by entering details.
     */
    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Expense", true);
        dialog.setSize(450, 480);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);

        // Budget dropdown
        JLabel budgetLabel = new JLabel("Budget:");
        budgetLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        budgetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(budgetLabel);
        panel.add(Box.createVerticalStrut(5));
        
        List<Budget> budgets = budgetService.listBudgetsForUser(user.getId());
        JComboBox<String> budgetCombo = new JComboBox<>();
        for (Budget b : budgets) {
            budgetCombo.addItem(b.getId() + " - " + b.getName());
        }
        budgetCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        budgetCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(budgetCombo);
        panel.add(Box.createVerticalStrut(15));

        // Category field
        JTextField categoryField = createFormField(panel, "Category ID:");
        
        // Amount field
        JTextField amountField = createFormField(panel, "Amount (₺):");
        
        // Date with calendar button
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dateLabel.setForeground(BudgetGUI.TEXT_COLOR);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(dateLabel);
        panel.add(Box.createVerticalStrut(5));
        
        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        datePanel.setBackground(Color.WHITE);
        datePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        datePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField dateField = new JTextField();
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateField.setEditable(false);
        dateField.setText(LocalDate.now().toString());
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        JButton calBtn = new JButton("📅");
        calBtn.setPreferredSize(new Dimension(45, 35));
        calBtn.setFocusPainted(false);
        calBtn.addActionListener(e -> {
            LocalDate selected = DateChooserPanel.showDialog(dialog, "Select Date", 
                    LocalDate.parse(dateField.getText()));
            if (selected != null) {
                dateField.setText(selected.toString());
            }
        });
        
        datePanel.add(dateField, BorderLayout.CENTER);
        datePanel.add(calBtn, BorderLayout.EAST);
        panel.add(datePanel);
        panel.add(Box.createVerticalStrut(15));
        
        // Description field
        JTextField descField = createFormField(panel, "Description:");

        panel.add(Box.createVerticalStrut(10));

        // Save button
        JButton saveButton = createStyledButton("Save", BudgetGUI.PRIMARY_COLOR);
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(e -> {
            try {
                if (budgetCombo.getSelectedItem() == null) {
                    showError("Please select a budget.");
                    return;
                }
                
                String budgetStr = (String) budgetCombo.getSelectedItem();
                int budgetId = Integer.parseInt(budgetStr.split(" - ")[0]);
                int categoryId = Integer.parseInt(categoryField.getText().trim());
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                String description = descField.getText().trim();
                
                if (description.isEmpty()) {
                    showError("Description cannot be empty.");
                    return;
                }
                
                expenseService.logExpense(user.getId(), budgetId, categoryId, amount, date, description);
                refreshTable();
                dialog.dispose();
                showSuccess("Expense added successfully!");
            } catch (NumberFormatException ex) {
                showError("Invalid number format.");
            }
        });
        panel.add(saveButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * Shows the edit expense dialog for the specified expense.
     * Pre-fills the form with existing expense data.
     * 
     * @param expenseId The ID of the expense to edit
     */
    public void showEditDialog(int expenseId) {
        Expense expense = expenseService.listExpensesForUser(user.getId()).stream()
                .filter(e -> e.getId() == expenseId)
                .findFirst()
                .orElse(null);
        
        if (expense == null) {
            showError("Expense not found.");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Expense", true);
        dialog.setSize(450, 480);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);

        // Budget ID field
        JTextField budgetField = createFormField(panel, "Budget ID:");
        budgetField.setText(String.valueOf(expense.getBudgetId()));
        
        // Category field
        JTextField categoryField = createFormField(panel, "Category ID:");
        categoryField.setText(String.valueOf(expense.getCategoryId()));
        
        // Amount field
        JTextField amountField = createFormField(panel, "Amount (₺):");
        amountField.setText(expense.getAmount().toString());
        
        // Date with calendar button
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dateLabel.setForeground(BudgetGUI.TEXT_COLOR);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(dateLabel);
        panel.add(Box.createVerticalStrut(5));
        
        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        datePanel.setBackground(Color.WHITE);
        datePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        datePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField dateField = new JTextField();
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateField.setEditable(false);
        dateField.setText(expense.getDate().toString());
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        JButton calBtn = new JButton("📅");
        calBtn.setPreferredSize(new Dimension(45, 35));
        calBtn.setFocusPainted(false);
        calBtn.addActionListener(e -> {
            LocalDate selected = DateChooserPanel.showDialog(dialog, "Select Date", 
                    LocalDate.parse(dateField.getText()));
            if (selected != null) {
                dateField.setText(selected.toString());
            }
        });
        
        datePanel.add(dateField, BorderLayout.CENTER);
        datePanel.add(calBtn, BorderLayout.EAST);
        panel.add(datePanel);
        panel.add(Box.createVerticalStrut(15));
        
        // Description field
        JTextField descField = createFormField(panel, "Description:");
        descField.setText(expense.getDescription());

        panel.add(Box.createVerticalStrut(10));

        // Update button
        JButton saveButton = createStyledButton("Update", BudgetGUI.PRIMARY_COLOR);
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(e -> {
            try {
                int budgetId = Integer.parseInt(budgetField.getText().trim());
                int categoryId = Integer.parseInt(categoryField.getText().trim());
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                String description = descField.getText().trim();
                
                expenseService.updateExpense(expenseId, budgetId, categoryId, amount, date, description);
                refreshTable();
                dialog.dispose();
                showSuccess("Expense updated successfully!");
            } catch (Exception ex) {
                showError("Error updating expense: " + ex.getMessage());
            }
        });
        panel.add(saveButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * Deletes an expense after user confirmation.
     * Shows a confirmation dialog before performing the deletion.
     * 
     * @param expenseId The ID of the expense to delete
     */
    public void deleteExpense(int expenseId) {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this expense?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            if (expenseService.deleteExpense(expenseId)) {
                refreshTable();
                showSuccess("Expense deleted successfully!");
            } else {
                showError("Failed to delete expense.");
            }
        }
    }

    /**
     * Creates a form field with label in the given panel.
     * 
     * @param panel The panel to add the field to
     * @param label The label text for the field
     * @return The created text field
     */
    private JTextField createFormField(JPanel panel, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(BudgetGUI.TEXT_COLOR);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        
        panel.add(Box.createVerticalStrut(5));
        
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);
        
        panel.add(Box.createVerticalStrut(15));
        
        return field;
    }

    /**
     * Creates a styled button with consistent appearance.
     * 
     * @param text  The button text
     * @param color The background color
     * @return A configured JButton
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

    /**
     * Shows a success message dialog.
     * 
     * @param message The success message to display
     */
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Custom renderer for the actions button in table cells.
     */
    private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        
        /** Creates a new ButtonRenderer. */
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            setFont(new Font("Segoe UI", Font.PLAIN, 11));
            setForeground(BudgetGUI.PRIMARY_COLOR);
            setBackground(Color.WHITE);
            return this;
        }
    }

    /**
     * Custom editor for the actions button in table cells.
     */
    private static class ButtonEditor extends DefaultCellEditor {
        
        /** The button component. */
        private JButton button;
        
        /** Reference to the parent panel. */
        private ExpensePanel panel;
        
        /** The current row being edited. */
        private int currentRow;

        /**
         * Creates a new ButtonEditor for the specified panel.
         * 
         * @param panel The parent ExpensePanel
         */
        public ButtonEditor(ExpensePanel panel) {
            super(new JCheckBox());
            this.panel = panel;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                int expenseId = (int) panel.tableModel.getValueAt(currentRow, 0);
                String[] options = {"Edit", "Delete", "Cancel"};
                int choice = JOptionPane.showOptionDialog(panel, "Choose action:", "Expense Action",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                if (choice == 0) {
                    panel.showEditDialog(expenseId);
                } else if (choice == 1) {
                    panel.deleteExpense(expenseId);
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            button.setText(value == null ? "" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }
}
