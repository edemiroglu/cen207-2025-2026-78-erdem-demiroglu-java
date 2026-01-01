package com.hyildizoglu.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.hyildizoglu.budgetCreation.BudgetService;
import com.hyildizoglu.models.Budget;
import com.hyildizoglu.models.User;

/**
 * Panel for budget management operations.
 * Provides a complete interface for creating, viewing, updating, and deleting budgets.
 * 
 * <p>Features include:</p>
 * <ul>
 *   <li>Budget listing in a sortable table</li>
 *   <li>Add new budget with calendar date picker</li>
 *   <li>Edit existing budgets</li>
 *   <li>Delete budgets with confirmation</li>
 * </ul>
 * 
 * <p>The panel uses a table-based layout for displaying budgets with
 * action buttons for edit and delete operations on each row.</p>
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 * @since 1.0
 * @see BudgetService
 * @see Budget
 * @see DateChooserPanel
 */
public class BudgetPanel extends JPanel {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;
    
    /** Currently logged-in user whose budgets are displayed. */
    private final User user;
    
    /** Service for performing budget operations. */
    private final BudgetService budgetService;
    
    /** Table component for displaying budgets. */
    private JTable budgetTable;
    
    /** Table model for managing budget data. */
    private DefaultTableModel tableModel;

    /**
     * Creates a new BudgetPanel for the specified user.
     * 
     * @param user          The currently logged-in user. Must not be null.
     * @param budgetService Service for budget operations. Must not be null.
     * @throws NullPointerException if user or budgetService is null
     */
    public BudgetPanel(User user, BudgetService budgetService) {
        this.user = user;
        this.budgetService = budgetService;
        initializeUI();
    }

    /**
     * Initializes the user interface components.
     * Sets up the header, table, and loads initial data.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BudgetGUI.BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header panel with title and add button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BudgetGUI.BG_COLOR);
        
        JLabel headerLabel = new JLabel("Budget Management");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(BudgetGUI.TEXT_COLOR);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        JButton addButton = createStyledButton("+ Add Budget", BudgetGUI.ACCENT_COLOR);
        addButton.addActionListener(e -> showAddDialog());
        headerPanel.add(addButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // Budget table panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Load initial data
        refreshTable();
    }

    /**
     * Creates the table panel with budget data display.
     * 
     * @return A configured JPanel containing the budget table
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(0, 0, 0, 0)
        ));
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Define table columns
        String[] columns = {"ID", "Name", "Total Limit", "Start Date", "End Date", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            /**
             * Only allow editing the actions column.
             * @param row The row index
             * @param column The column index
             * @return true if the column is the actions column
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only actions column is editable
            }
        };
        
        // Configure table appearance
        budgetTable = new JTable(tableModel);
        budgetTable.setRowHeight(45);
        budgetTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        budgetTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        budgetTable.getTableHeader().setBackground(BudgetGUI.PRIMARY_COLOR);
        budgetTable.getTableHeader().setForeground(Color.WHITE);
        budgetTable.setSelectionBackground(new Color(232, 240, 254));
        budgetTable.setGridColor(new Color(230, 230, 230));
        
        // Configure column renderers and widths
        budgetTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        budgetTable.getColumn("Actions").setCellEditor(new ButtonEditor(this));
        budgetTable.getColumn("ID").setPreferredWidth(50);
        budgetTable.getColumn("Actions").setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(budgetTable);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Refreshes the table with current budget data from the service.
     * Clears existing data and reloads from the database.
     */
    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Budget> budgets = budgetService.listBudgetsForUser(user.getId());
        for (Budget budget : budgets) {
            tableModel.addRow(new Object[]{
                budget.getId(),
                budget.getName(),
                "₺" + budget.getTotalLimit().toString(),
                budget.getStartDate().toString(),
                budget.getEndDate().toString(),
                "Edit / Delete"
            });
        }
    }

    /**
     * Shows the add budget dialog with calendar date pickers.
     * Allows user to create a new budget by entering name, limit, and date range.
     */
    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Budget", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);

        // Budget name field
        JTextField nameField = createFormField(panel, "Budget Name:");
        
        // Budget limit field
        JTextField limitField = createFormField(panel, "Total Limit (₺):");
        
        // Start date with calendar button
        JLabel startLabel = new JLabel("Start Date:");
        startLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        startLabel.setForeground(BudgetGUI.TEXT_COLOR);
        startLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(startLabel);
        panel.add(Box.createVerticalStrut(5));
        
        JPanel startDatePanel = new JPanel(new BorderLayout(5, 0));
        startDatePanel.setBackground(Color.WHITE);
        startDatePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        startDatePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField startField = new JTextField();
        startField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        startField.setEditable(false);
        startField.setText(LocalDate.now().toString());
        startField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        JButton startCalBtn = new JButton("📅");
        startCalBtn.setPreferredSize(new Dimension(45, 35));
        startCalBtn.setFocusPainted(false);
        startCalBtn.addActionListener(e -> {
            LocalDate selected = DateChooserPanel.showDialog(dialog, "Select Start Date", 
                    LocalDate.parse(startField.getText()));
            if (selected != null) {
                startField.setText(selected.toString());
            }
        });
        
        startDatePanel.add(startField, BorderLayout.CENTER);
        startDatePanel.add(startCalBtn, BorderLayout.EAST);
        panel.add(startDatePanel);
        panel.add(Box.createVerticalStrut(15));
        
        // End date with calendar button
        JLabel endLabel = new JLabel("End Date:");
        endLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        endLabel.setForeground(BudgetGUI.TEXT_COLOR);
        endLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(endLabel);
        panel.add(Box.createVerticalStrut(5));
        
        JPanel endDatePanel = new JPanel(new BorderLayout(5, 0));
        endDatePanel.setBackground(Color.WHITE);
        endDatePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        endDatePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField endField = new JTextField();
        endField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        endField.setEditable(false);
        endField.setText(LocalDate.now().plusMonths(1).toString());
        endField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        JButton endCalBtn = new JButton("📅");
        endCalBtn.setPreferredSize(new Dimension(45, 35));
        endCalBtn.setFocusPainted(false);
        endCalBtn.addActionListener(e -> {
            LocalDate selected = DateChooserPanel.showDialog(dialog, "Select End Date", 
                    LocalDate.parse(endField.getText()));
            if (selected != null) {
                endField.setText(selected.toString());
            }
        });
        
        endDatePanel.add(endField, BorderLayout.CENTER);
        endDatePanel.add(endCalBtn, BorderLayout.EAST);
        panel.add(endDatePanel);
        panel.add(Box.createVerticalStrut(20));

        // Save button
        JButton saveButton = createStyledButton("Save", BudgetGUI.PRIMARY_COLOR);
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                BigDecimal limit = new BigDecimal(limitField.getText().trim());
                LocalDate start = LocalDate.parse(startField.getText().trim());
                LocalDate end = LocalDate.parse(endField.getText().trim());
                
                if (name.isEmpty()) {
                    showError("Name cannot be empty.");
                    return;
                }
                if (start.isAfter(end)) {
                    showError("Start date cannot be after end date.");
                    return;
                }
                
                budgetService.createBudget(user.getId(), name, limit, start, end);
                refreshTable();
                dialog.dispose();
                showSuccess("Budget created successfully!");
            } catch (NumberFormatException ex) {
                showError("Invalid amount format.");
            }
        });
        panel.add(saveButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * Shows the edit budget dialog for the specified budget.
     * Pre-fills the form with existing budget data.
     * 
     * @param budgetId The ID of the budget to edit
     */
    public void showEditDialog(int budgetId) {
        Budget budget = budgetService.listBudgetsForUser(user.getId()).stream()
                .filter(b -> b.getId() == budgetId)
                .findFirst()
                .orElse(null);
        
        if (budget == null) {
            showError("Budget not found.");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Budget", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);

        // Budget name field
        JTextField nameField = createFormField(panel, "Budget Name:");
        nameField.setText(budget.getName());
        
        // Budget limit field
        JTextField limitField = createFormField(panel, "Total Limit (₺):");
        limitField.setText(budget.getTotalLimit().toString());
        
        // Start date with calendar button
        JLabel startLabel = new JLabel("Start Date:");
        startLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        startLabel.setForeground(BudgetGUI.TEXT_COLOR);
        startLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(startLabel);
        panel.add(Box.createVerticalStrut(5));
        
        JPanel startDatePanel = new JPanel(new BorderLayout(5, 0));
        startDatePanel.setBackground(Color.WHITE);
        startDatePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        startDatePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField startField = new JTextField();
        startField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        startField.setEditable(false);
        startField.setText(budget.getStartDate().toString());
        startField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        JButton startCalBtn = new JButton("📅");
        startCalBtn.setPreferredSize(new Dimension(45, 35));
        startCalBtn.setFocusPainted(false);
        startCalBtn.addActionListener(e -> {
            LocalDate selected = DateChooserPanel.showDialog(dialog, "Select Start Date", 
                    LocalDate.parse(startField.getText()));
            if (selected != null) {
                startField.setText(selected.toString());
            }
        });
        
        startDatePanel.add(startField, BorderLayout.CENTER);
        startDatePanel.add(startCalBtn, BorderLayout.EAST);
        panel.add(startDatePanel);
        panel.add(Box.createVerticalStrut(15));
        
        // End date with calendar button
        JLabel endLabel = new JLabel("End Date:");
        endLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        endLabel.setForeground(BudgetGUI.TEXT_COLOR);
        endLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(endLabel);
        panel.add(Box.createVerticalStrut(5));
        
        JPanel endDatePanel = new JPanel(new BorderLayout(5, 0));
        endDatePanel.setBackground(Color.WHITE);
        endDatePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        endDatePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField endField = new JTextField();
        endField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        endField.setEditable(false);
        endField.setText(budget.getEndDate().toString());
        endField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        JButton endCalBtn = new JButton("📅");
        endCalBtn.setPreferredSize(new Dimension(45, 35));
        endCalBtn.setFocusPainted(false);
        endCalBtn.addActionListener(e -> {
            LocalDate selected = DateChooserPanel.showDialog(dialog, "Select End Date", 
                    LocalDate.parse(endField.getText()));
            if (selected != null) {
                endField.setText(selected.toString());
            }
        });
        
        endDatePanel.add(endField, BorderLayout.CENTER);
        endDatePanel.add(endCalBtn, BorderLayout.EAST);
        panel.add(endDatePanel);
        panel.add(Box.createVerticalStrut(20));

        // Update button
        JButton saveButton = createStyledButton("Update", BudgetGUI.PRIMARY_COLOR);
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                BigDecimal limit = new BigDecimal(limitField.getText().trim());
                LocalDate start = LocalDate.parse(startField.getText().trim());
                LocalDate end = LocalDate.parse(endField.getText().trim());
                
                budgetService.updateBudget(budgetId, name, limit, start, end);
                refreshTable();
                dialog.dispose();
                showSuccess("Budget updated successfully!");
            } catch (Exception ex) {
                showError("Error updating budget: " + ex.getMessage());
            }
        });
        panel.add(saveButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * Deletes a budget after user confirmation.
     * Shows a confirmation dialog before performing the deletion.
     * 
     * @param budgetId The ID of the budget to delete
     */
    public void deleteBudget(int budgetId) {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this budget?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            if (budgetService.deleteBudget(budgetId)) {
                refreshTable();
                showSuccess("Budget deleted successfully!");
            } else {
                showError("Failed to delete budget.");
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
     * Displays buttons with consistent styling.
     */
    private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        
        /**
         * Creates a new ButtonRenderer.
         */
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        /**
         * Returns the component used for drawing the cell.
         * 
         * @param table      The JTable being rendered
         * @param value      The value to be rendered
         * @param isSelected True if the cell is selected
         * @param hasFocus   True if the cell has focus
         * @param row        The row index
         * @param column     The column index
         * @return This button configured for the cell
         */
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
     * Handles click events to show edit/delete options.
     */
    private static class ButtonEditor extends DefaultCellEditor {
        
        /** The button component. */
        private JButton button;
        
        /** Reference to the parent panel. */
        private BudgetPanel panel;
        
        /** The current row being edited. */
        private int currentRow;

        /**
         * Creates a new ButtonEditor for the specified panel.
         * 
         * @param panel The parent BudgetPanel
         */
        public ButtonEditor(BudgetPanel panel) {
            super(new JCheckBox());
            this.panel = panel;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                int budgetId = (int) panel.tableModel.getValueAt(currentRow, 0);
                String[] options = {"Edit", "Delete", "Cancel"};
                int choice = JOptionPane.showOptionDialog(panel, "Choose action:", "Budget Action",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                if (choice == 0) {
                    panel.showEditDialog(budgetId);
                } else if (choice == 1) {
                    panel.deleteBudget(budgetId);
                }
                fireEditingStopped();
            });
        }

        /**
         * Returns the component used for editing the cell.
         * 
         * @param table      The JTable being edited
         * @param value      The value in the cell
         * @param isSelected True if the cell is selected
         * @param row        The row index
         * @param column     The column index
         * @return The button component
         */
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            button.setText(value == null ? "" : value.toString());
            return button;
        }

        /**
         * Returns the value contained in the editor.
         * 
         * @return The button text
         */
        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }
}
