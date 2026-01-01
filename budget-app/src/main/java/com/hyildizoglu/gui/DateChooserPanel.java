package com.hyildizoglu.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * A custom date chooser panel that displays a calendar for date selection.
 * Provides a visual calendar interface for selecting dates without manual text entry.
 * 
 * <p>Features include:</p>
 * <ul>
 *   <li>Month and year navigation</li>
 *   <li>Visual calendar grid showing days</li>
 *   <li>Current date highlighting</li>
 *   <li>Selected date highlighting</li>
 * </ul>
 * 
 * <p>Usage example:</p>
 * <pre>
 * DateChooserPanel dateChooser = new DateChooserPanel();
 * dateChooser.setSelectedDate(LocalDate.now());
 * LocalDate selected = dateChooser.getSelectedDate();
 * </pre>
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 * @since 1.0
 */
public class DateChooserPanel extends JPanel {

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 1L;
    
    /** Currently displayed year and month. */
    private YearMonth currentYearMonth;
    
    /** Currently selected date. */
    private LocalDate selectedDate;
    
    /** Panel containing the day buttons. */
    private JPanel daysPanel;
    
    /** Label showing current month and year. */
    private JLabel monthYearLabel;
    
    /** Array of day buttons for the calendar grid. */
    private JButton[] dayButtons;
    
    /** Listener called when a date is selected. */
    private ActionListener dateSelectedListener;

    /**
     * Creates a new DateChooserPanel initialized to the current date.
     * The panel displays a calendar for the current month with navigation controls.
     */
    public DateChooserPanel() {
        this.currentYearMonth = YearMonth.now();
        this.selectedDate = LocalDate.now();
        initializeUI();
    }

    /**
     * Creates a new DateChooserPanel initialized to a specific date.
     * 
     * @param initialDate The initial date to display and select.
     *                    If null, defaults to current date.
     */
    public DateChooserPanel(LocalDate initialDate) {
        this.selectedDate = initialDate != null ? initialDate : LocalDate.now();
        this.currentYearMonth = YearMonth.from(this.selectedDate);
        initializeUI();
    }

    /**
     * Initializes the user interface components.
     * Creates the header with navigation buttons and the calendar grid.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(5, 5));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header with month/year and navigation
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Days of week labels
        JPanel weekDaysPanel = createWeekDaysPanel();
        add(weekDaysPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the header panel with month/year label and navigation buttons.
     * 
     * @return The configured header panel with navigation controls
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Previous month button
        JButton prevButton = createNavButton("◀");
        prevButton.addActionListener(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            updateCalendar();
        });

        // Next month button
        JButton nextButton = createNavButton("▶");
        nextButton.addActionListener(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            updateCalendar();
        });

        // Month and year label
        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        monthYearLabel.setForeground(BudgetGUI.PRIMARY_COLOR);

        panel.add(prevButton, BorderLayout.WEST);
        panel.add(monthYearLabel, BorderLayout.CENTER);
        panel.add(nextButton, BorderLayout.EAST);

        return panel;
    }

    /**
     * Creates the week days panel containing day labels and calendar grid.
     * 
     * @return The panel containing week day headers and day buttons
     */
    private JPanel createWeekDaysPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 5));
        container.setBackground(Color.WHITE);

        // Week day headers
        JPanel weekHeaders = new JPanel(new GridLayout(1, 7, 2, 2));
        weekHeaders.setBackground(Color.WHITE);
        String[] days = {"Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz"};
        for (String day : days) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setForeground(BudgetGUI.TEXT_COLOR);
            weekHeaders.add(label);
        }
        container.add(weekHeaders, BorderLayout.NORTH);

        // Days grid
        daysPanel = new JPanel(new GridLayout(6, 7, 2, 2));
        daysPanel.setBackground(Color.WHITE);
        
        dayButtons = new JButton[42];
        for (int i = 0; i < 42; i++) {
            dayButtons[i] = createDayButton();
            daysPanel.add(dayButtons[i]);
        }
        container.add(daysPanel, BorderLayout.CENTER);

        updateCalendar();
        return container;
    }

    /**
     * Creates a navigation button for month navigation.
     * 
     * @param text The button text (arrow symbol)
     * @return A styled navigation button
     */
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(BudgetGUI.PRIMARY_COLOR);
        button.setBackground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(40, 30));
        return button;
    }

    /**
     * Creates a day button for the calendar grid.
     * 
     * @return A styled day button
     */
    private JButton createDayButton() {
        JButton button = new JButton();
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(35, 30));
        return button;
    }

    /**
     * Updates the calendar display to show the current month.
     * Recalculates and redraws all day buttons with correct dates.
     */
    private void updateCalendar() {
        // Update month/year label
        String monthName = currentYearMonth.getMonth().getDisplayName(TextStyle.FULL, new Locale("tr", "TR"));
        monthYearLabel.setText(monthName + " " + currentYearMonth.getYear());

        // Calculate first day of month
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
        int daysInMonth = currentYearMonth.lengthOfMonth();

        // Clear all buttons
        for (JButton button : dayButtons) {
            button.setText("");
            button.setEnabled(false);
            button.setBackground(Color.WHITE);
            button.setForeground(BudgetGUI.TEXT_COLOR);
            for (java.awt.event.ActionListener listener : button.getActionListeners()) {
                button.removeActionListener(listener);
            }
        }

        // Fill in the days
        LocalDate today = LocalDate.now();
        for (int day = 1; day <= daysInMonth; day++) {
            int buttonIndex = dayOfWeek - 1 + day - 1;
            if (buttonIndex < 42) {
                JButton btn = dayButtons[buttonIndex];
                btn.setText(String.valueOf(day));
                btn.setEnabled(true);
                
                LocalDate buttonDate = currentYearMonth.atDay(day);
                
                // Highlight today
                if (buttonDate.equals(today)) {
                    btn.setBackground(new Color(232, 240, 254));
                }
                
                // Highlight selected date
                if (buttonDate.equals(selectedDate)) {
                    btn.setBackground(BudgetGUI.PRIMARY_COLOR);
                    btn.setForeground(Color.WHITE);
                }

                final int selectedDay = day;
                btn.addActionListener(e -> {
                    selectedDate = currentYearMonth.atDay(selectedDay);
                    updateCalendar();
                    if (dateSelectedListener != null) {
                        dateSelectedListener.actionPerformed(e);
                    }
                });
            }
        }
    }

    /**
     * Returns the currently selected date.
     * 
     * @return The selected LocalDate, or null if no date is selected
     */
    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    /**
     * Sets the selected date and updates the calendar display.
     * 
     * @param date The date to select. If null, no change is made.
     */
    public void setSelectedDate(LocalDate date) {
        if (date != null) {
            this.selectedDate = date;
            this.currentYearMonth = YearMonth.from(date);
            updateCalendar();
        }
    }

    /**
     * Sets a listener to be notified when a date is selected.
     * 
     * @param listener The ActionListener to call when a date is selected
     */
    public void setDateSelectedListener(ActionListener listener) {
        this.dateSelectedListener = listener;
    }

    /**
     * Creates a popup dialog containing a DateChooserPanel for date selection.
     * 
     * @param parent      The parent component for dialog positioning
     * @param title       The dialog title
     * @param initialDate The initial date to display (null for current date)
     * @return The selected LocalDate, or null if cancelled
     */
    public static LocalDate showDialog(Component parent, String title, LocalDate initialDate) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), title, true);
        dialog.setSize(300, 320);
        dialog.setLocationRelativeTo(parent);
        
        DateChooserPanel chooser = new DateChooserPanel(initialDate);
        final LocalDate[] result = {null};
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(chooser, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton okButton = new JButton("Seç");
        okButton.setBackground(BudgetGUI.PRIMARY_COLOR);
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.addActionListener(e -> {
            result[0] = chooser.getSelectedDate();
            dialog.dispose();
        });
        
        JButton cancelButton = new JButton("İptal");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
        
        return result[0];
    }
}

