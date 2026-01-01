package com.hyildizoglu.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an expense record.
 * An expense tracks money spent by a user within a budget and category.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class Expense {

	/** Unique identifier for the expense. */
	private final int id;
	
	/** ID of the user who made this expense. */
	private final int userId;
	
	/** ID of the budget this expense belongs to. */
	private final int budgetId;
	
	/** ID of the category this expense belongs to. */
	private final int categoryId;
	
	/** Amount of money spent. */
	private final BigDecimal amount;
	
	/** Date when the expense was made. */
	private final LocalDate date;
	
	/** Description or note about the expense. */
	private final String description;

	/**
	 * Creates a new Expense instance.
	 * 
	 * @param id          Unique identifier for the expense
	 * @param userId      ID of the user who made this expense
	 * @param budgetId    ID of the budget this expense belongs to
	 * @param categoryId  ID of the category this expense belongs to
	 * @param amount      Amount of money spent
	 * @param date        Date when the expense was made
	 * @param description Description or note about the expense
	 */
	public Expense(int id, int userId, int budgetId, int categoryId, BigDecimal amount, LocalDate date,
			String description) {
		this.id = id;
		this.userId = userId;
		this.budgetId = budgetId;
		this.categoryId = categoryId;
		this.amount = amount;
		this.date = date;
		this.description = description;
	}

	/**
	 * Returns the unique identifier of the expense.
	 * 
	 * @return The expense ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the ID of the user who made this expense.
	 * 
	 * @return The user ID
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Returns the ID of the budget this expense belongs to.
	 * 
	 * @return The budget ID
	 */
	public int getBudgetId() {
		return budgetId;
	}

	/**
	 * Returns the ID of the category this expense belongs to.
	 * 
	 * @return The category ID
	 */
	public int getCategoryId() {
		return categoryId;
	}

	/**
	 * Returns the amount of money spent.
	 * 
	 * @return The expense amount as BigDecimal
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * Returns the date when the expense was made.
	 * 
	 * @return The expense date
	 */
	public LocalDate getDate() {
		return date;
	}

	/**
	 * Returns the description of the expense.
	 * 
	 * @return The expense description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Checks if this expense is equal to another object.
	 * Two expenses are equal if they have the same ID.
	 * 
	 * @param o The object to compare
	 * @return true if equal, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Expense expense = (Expense) o;
		return id == expense.id;
	}

	/**
	 * Returns the hash code for this expense.
	 * 
	 * @return The hash code based on ID
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	/**
	 * Returns a string representation of this expense.
	 * 
	 * @return String containing expense details
	 */
	@Override
	public String toString() {
		return "Expense{id=" + id + ", userId=" + userId + ", budgetId=" + budgetId + ", categoryId=" + categoryId
				+ ", amount=" + amount + ", date=" + date + '}';
	}
}
