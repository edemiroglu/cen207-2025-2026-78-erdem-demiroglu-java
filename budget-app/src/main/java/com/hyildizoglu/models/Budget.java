package com.hyildizoglu.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a budget configuration for a user.
 * A budget defines spending limits for a specific time period.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class Budget {

	/** Unique identifier for the budget. */
	private final int id;
	
	/** ID of the user who owns this budget. */
	private final int userId;
	
	/** Name/title of the budget. */
	private final String name;
	
	/** Maximum spending limit for this budget. */
	private final BigDecimal totalLimit;
	
	/** Start date of the budget period. */
	private final LocalDate startDate;
	
	/** End date of the budget period. */
	private final LocalDate endDate;

	/**
	 * Creates a new Budget instance.
	 * 
	 * @param id         Unique identifier for the budget
	 * @param userId     ID of the user who owns this budget
	 * @param name       Name/title of the budget
	 * @param totalLimit Maximum spending limit
	 * @param startDate  Start date of the budget period
	 * @param endDate    End date of the budget period
	 */
	public Budget(int id, int userId, String name, BigDecimal totalLimit, LocalDate startDate, LocalDate endDate) {
		this.id = id;
		this.userId = userId;
		this.name = name;
		this.totalLimit = totalLimit;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	/**
	 * Returns the unique identifier of the budget.
	 * 
	 * @return The budget ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the ID of the user who owns this budget.
	 * 
	 * @return The user ID
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Returns the name of the budget.
	 * 
	 * @return The budget name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the maximum spending limit for this budget.
	 * 
	 * @return The total limit as BigDecimal
	 */
	public BigDecimal getTotalLimit() {
		return totalLimit;
	}

	/**
	 * Returns the start date of the budget period.
	 * 
	 * @return The start date
	 */
	public LocalDate getStartDate() {
		return startDate;
	}

	/**
	 * Returns the end date of the budget period.
	 * 
	 * @return The end date
	 */
	public LocalDate getEndDate() {
		return endDate;
	}

	/**
	 * Checks if this budget is equal to another object.
	 * Two budgets are equal if they have the same ID.
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
		Budget budget = (Budget) o;
		return id == budget.id;
	}

	/**
	 * Returns the hash code for this budget.
	 * 
	 * @return The hash code based on ID
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	/**
	 * Returns a string representation of this budget.
	 * 
	 * @return String containing budget details
	 */
	@Override
	public String toString() {
		return "Budget{id=" + id + ", userId=" + userId + ", name='" + name + '\'' + ", totalLimit=" + totalLimit
				+ ", startDate=" + startDate + ", endDate=" + endDate + '}';
	}
}
