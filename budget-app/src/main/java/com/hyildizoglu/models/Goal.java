package com.hyildizoglu.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a savings or financial goal.
 * A goal tracks progress towards a target amount by a deadline.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class Goal {

	/** Unique identifier for the goal. */
	private final int id;
	
	/** ID of the user who owns this goal. */
	private final int userId;
	
	/** Name/title of the goal. */
	private final String name;
	
	/** Target amount to save. */
	private final BigDecimal targetAmount;
	
	/** Current amount saved so far. */
	private final BigDecimal currentAmount;
	
	/** Deadline to achieve the goal. */
	private final LocalDate deadline;

	/**
	 * Creates a new Goal instance.
	 * 
	 * @param id            Unique identifier for the goal
	 * @param userId        ID of the user who owns this goal
	 * @param name          Name/title of the goal
	 * @param targetAmount  Target amount to save
	 * @param currentAmount Current amount saved so far
	 * @param deadline      Deadline to achieve the goal
	 */
	public Goal(int id, int userId, String name, BigDecimal targetAmount, BigDecimal currentAmount,
			LocalDate deadline) {
		this.id = id;
		this.userId = userId;
		this.name = name;
		this.targetAmount = targetAmount;
		this.currentAmount = currentAmount;
		this.deadline = deadline;
	}

	/**
	 * Returns the unique identifier of the goal.
	 * 
	 * @return The goal ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the ID of the user who owns this goal.
	 * 
	 * @return The user ID
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Returns the name of the goal.
	 * 
	 * @return The goal name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the target amount to save.
	 * 
	 * @return The target amount as BigDecimal
	 */
	public BigDecimal getTargetAmount() {
		return targetAmount;
	}

	/**
	 * Returns the current amount saved so far.
	 * 
	 * @return The current amount as BigDecimal
	 */
	public BigDecimal getCurrentAmount() {
		return currentAmount;
	}

	/**
	 * Returns the deadline to achieve the goal.
	 * 
	 * @return The deadline date
	 */
	public LocalDate getDeadline() {
		return deadline;
	}

	/**
	 * Checks if the goal has been completed.
	 * A goal is completed when currentAmount is greater than or equal to targetAmount.
	 * 
	 * @return true if the goal is completed, false otherwise
	 */
	public boolean isCompleted() {
		return currentAmount.compareTo(targetAmount) >= 0;
	}

	/**
	 * Checks if this goal is equal to another object.
	 * Two goals are equal if they have the same ID.
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
		Goal goal = (Goal) o;
		return id == goal.id;
	}

	/**
	 * Returns the hash code for this goal.
	 * 
	 * @return The hash code based on ID
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	/**
	 * Returns a string representation of this goal.
	 * 
	 * @return String containing goal details
	 */
	@Override
	public String toString() {
		return "Goal{id=" + id + ", userId=" + userId + ", name='" + name + '\'' + ", targetAmount=" + targetAmount
				+ ", currentAmount=" + currentAmount + ", deadline=" + deadline + '}';
	}
}
