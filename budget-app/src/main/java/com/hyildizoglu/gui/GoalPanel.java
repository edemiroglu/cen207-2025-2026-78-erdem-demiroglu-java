package com.hyildizoglu.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.hyildizoglu.models.Goal;
import com.hyildizoglu.models.User;
import com.hyildizoglu.savingsGoal.GoalService;

/**
 * Panel for savings goal management operations.
 * Provides a complete interface for creating, viewing, updating, and deleting
 * savings goals with progress tracking.
 * 
 * <p>Features include:</p>
 * <ul>
 *   <li>Goal listing in a table with progress indicators</li>
 *   <li>Add new goal with calendar date picker for deadline</li>
 *   <li>Edit existing goals</li>
 *   <li>Delete goals with confirmation</li>
 *   <li>Visual progress percentage display</li>
 *   <li>Completion status tracking</li>
 * </ul>
 * 
 * <p>Goals show their progress as a percentage and display completion status
 * to help users track their savings targets.</p>
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 * @since 1.0
 * @see GoalService
 * @see Goal
 * @see DateChooserPanel
 */
public class GoalPanel extends JPanel {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;
    
    /** Currently logged-in user whose goals are displayed. */
    private final User user;
    
    /** Service for performing goal operations. */
    private final GoalService goalService;
    
    /** Table component for displaying goals. */
    private JTable goalTable;
    
    /** Table model for managing goal data. */
    private DefaultTableModel tableModel;

    /**
     * Creates a new GoalPanel for the specified user.
     * 
     * @param user        The currently logged-in user. Must not be null.
     * @param goalService Service for goal operations. Must not be null.
     * @throws NullPointerException if user or goalService is null
     */
    public GoalPanel(User user, GoalService goalService) {
        this.user = user;
        this.goalService = goalService;
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
        
        JLabel headerLabel = new JLabel("Savings Goals");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(BudgetGUI.TEXT_COLOR);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        JButton addButton = createStyledButton("+ Add Goal", BudgetGUI.ACCENT_COLOR);
        addButton.addActionListener(e -> showAddDialog());
        headerPanel.add(addButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // Goal table panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Load initial data
        refreshTable();
    }

    /**
     * Creates the table panel with goal data display.
     * 
     * @return A configured JPanel containing the goal table
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Define table columns
        String[] columns = {"ID", "Name", "Target", "Current", "Progress", "Deadline", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            /**
             * Only allow editing the actions column.
             * @param row The row index
             * @param column The column index
             * @return true if the column is the actions column
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };
        
        // Configure table appearance
        goalTable = new JTable(tableModel);
        goalTable.setRowHeight(45);
        goalTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        goalTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        goalTable.getTableHeader().setBackground(BudgetGUI.PRIMARY_COLOR);
        goalTable.getTableHeader().setForeground(Color.WHITE);
        goalTable.setSelectionBackground(new Color(232, 240, 254));
        goalTable.setGridColor(new Color(230, 230, 230));
        
        // Configure column widths and renderers
        goalTable.getColumn("ID").setPreferredWidth(50);
        goalTable.getColumn("Progress").setPreferredWidth(80);
        goalTable.getColumn("Status").setPreferredWidth(80);
        goalTable.getColumn("Actions").setPreferredWidth(150);
        goalTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        goalTable.getColumn("Actions").setCellEditor(new ButtonEditor(this));

        JScrollPane scrollPane = new JScrollPane(goalTable);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Refreshes the table with current goal data from the service.
     * Calculates and displays progress percentage for each goal.
     */
    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Goal> goals = goalService.listGoalsForUser(user.getId());
        for (Goal goal : goals) {
            // Calculate progress percentage
            BigDecimal progress = BigDecimal.ZERO;
            if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
                progress = goal.getCurrentAmount()
                        .multiply(new BigDecimal(100))
                        .divide(goal.getTargetAmount(), 0, BigDecimal.ROUND_HALF_UP);
            }
            
            tableModel.addRow(new Object[]{
                goal.getId(),
                goal.getName(),
                "₺" + goal.getTargetAmount().toString(),
                "₺" + goal.getCurrentAmount().toString(),
                progress + "%",
                goal.getDeadline().toString(),
                goal.isCompleted() ? "✅ Completed" : "⏳ In Progress",
                "Edit / Delete"
            });
        }
    }

    /**
     * Shows the add goal dialog with calendar date picker for deadline.
     * Allows user to create a new savings goal.
     */
    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Goal", true);
        dialog.setSize(450, 420);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);

        // Goal name field
        JTextField nameField = createFormField(panel, "Goal Name:");
        
        // Target amount field
        JTextField targetField = createFormField(panel, "Target Amount (₺):");
        
        // Current amount field
        JTextField currentField = createFormField(panel, "Current Amount (₺):");
        currentField.setText("0");
        
        // Deadline with calendar button
        JLabel deadlineLabel = new JLabel("Deadline:");
        deadlineLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deadlineLabel.setForeground(BudgetGUI.TEXT_COLOR);
        deadlineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(deadlineLabel);
        panel.add(Box.createVerticalStrut(5));
        
        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        datePanel.setBackground(Color.WHITE);
        datePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        datePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField deadlineField = new JTextField();
        deadlineField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deadlineField.setEditable(false);
        deadlineField.setText(LocalDate.now().plusMonths(6).toString());
        deadlineField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        JButton calBtn = new JButton("📅");
        calBtn.setPreferredSize(new Dimension(45, 35));
        calBtn.setFocusPainted(false);
        calBtn.addActionListener(e -> {
            LocalDate selected = DateChooserPanel.showDialog(dialog, "Select Deadline", 
                    LocalDate.parse(deadlineField.getText()));
            if (selected != null) {
                deadlineField.setText(selected.toString());
            }
        });
        
        datePanel.add(deadlineField, BorderLayout.CENTER);
        datePanel.add(calBtn, BorderLayout.EAST);
        panel.add(datePanel);
        panel.add(Box.createVerticalStrut(20));

        // Save button
        JButton saveButton = createStyledButton("Save", BudgetGUI.PRIMARY_COLOR);
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                BigDecimal target = new BigDecimal(targetField.getText().trim());
                BigDecimal current = new BigDecimal(currentField.getText().trim());
                LocalDate deadline = LocalDate.parse(deadlineField.getText().trim());
                
                if (name.isEmpty()) {
                    showError("Name cannot be empty.");
                    return;
                }
                
                goalService.createGoal(user.getId(), name, target, current, deadline);
                refreshTable();
                dialog.dispose();
                showSuccess("Goal created successfully!");
            } catch (NumberFormatException ex) {
                showError("Invalid amount format.");
            }
        });
        panel.add(saveButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * Shows the edit goal dialog for the specified goal.
     * Pre-fills the form with existing goal data.
     * 
     * @param goalId The ID of the goal to edit
     */
    public void showEditDialog(int goalId) {
        Goal goal = goalService.listGoalsForUser(user.getId()).stream()
                .filter(g -> g.getId() == goalId)
                .findFirst()
                .orElse(null);
        
        if (goal == null) {
            showError("Goal not found.");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Goal", true);
        dialog.setSize(450, 420);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);

        // Goal name field
        JTextField nameField = createFormField(panel, "Goal Name:");
        nameField.setText(goal.getName());
        
        // Target amount field
        JTextField targetField = createFormField(panel, "Target Amount (₺):");
        targetField.setText(goal.getTargetAmount().toString());
        
        // Current amount field
        JTextField currentField = createFormField(panel, "Current Amount (₺):");
        currentField.setText(goal.getCurrentAmount().toString());
        
        // Deadline with calendar button
        JLabel deadlineLabel = new JLabel("Deadline:");
        deadlineLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deadlineLabel.setForeground(BudgetGUI.TEXT_COLOR);
        deadlineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(deadlineLabel);
        panel.add(Box.createVerticalStrut(5));
        
        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        datePanel.setBackground(Color.WHITE);
        datePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        datePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField deadlineField = new JTextField();
        deadlineField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deadlineField.setEditable(false);
        deadlineField.setText(goal.getDeadline().toString());
        deadlineField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        JButton calBtn = new JButton("📅");
        calBtn.setPreferredSize(new Dimension(45, 35));
        calBtn.setFocusPainted(false);
        calBtn.addActionListener(e -> {
            LocalDate selected = DateChooserPanel.showDialog(dialog, "Select Deadline", 
                    LocalDate.parse(deadlineField.getText()));
            if (selected != null) {
                deadlineField.setText(selected.toString());
            }
        });
        
        datePanel.add(deadlineField, BorderLayout.CENTER);
        datePanel.add(calBtn, BorderLayout.EAST);
        panel.add(datePanel);
        panel.add(Box.createVerticalStrut(20));

        // Update button
        JButton saveButton = createStyledButton("Update", BudgetGUI.PRIMARY_COLOR);
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                BigDecimal target = new BigDecimal(targetField.getText().trim());
                BigDecimal current = new BigDecimal(currentField.getText().trim());
                LocalDate deadline = LocalDate.parse(deadlineField.getText().trim());
                
                goalService.updateGoal(goalId, name, target, current, deadline);
                refreshTable();
                dialog.dispose();
                showSuccess("Goal updated successfully!");
            } catch (Exception ex) {
                showError("Error updating goal: " + ex.getMessage());
            }
        });
        panel.add(saveButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * Deletes a goal after user confirmation.
     * Shows a confirmation dialog before performing the deletion.
     * 
     * @param goalId The ID of the goal to delete
     */
    public void deleteGoal(int goalId) {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this goal?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            if (goalService.deleteGoal(goalId)) {
                refreshTable();
                showSuccess("Goal deleted successfully!");
            } else {
                showError("Failed to delete goal.");
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
        private GoalPanel panel;
        
        /** The current row being edited. */
        private int currentRow;

        /**
         * Creates a new ButtonEditor for the specified panel.
         * 
         * @param panel The parent GoalPanel
         */
        public ButtonEditor(GoalPanel panel) {
            super(new JCheckBox());
            this.panel = panel;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                int goalId = (int) panel.tableModel.getValueAt(currentRow, 0);
                String[] options = {"Edit", "Delete", "Cancel"};
                int choice = JOptionPane.showOptionDialog(panel, "Choose action:", "Goal Action",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                if (choice == 0) {
                    panel.showEditDialog(goalId);
                } else if (choice == 1) {
                    panel.deleteGoal(goalId);
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
