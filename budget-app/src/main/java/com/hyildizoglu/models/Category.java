package com.hyildizoglu.models;

import java.util.Objects;

/**
 * Represents an expense or budget category.
 * Categories are used to classify and organize expenses.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class Category {

	/** Unique identifier for the category. */
	private final int id;
	
	/** Name of the category. */
	private final String name;
	
	/** Detailed description of the category. */
	private final String description;

	/**
	 * Creates a new Category instance.
	 * 
	 * @param id          Unique identifier for the category
	 * @param name        Name of the category
	 * @param description Detailed description of the category
	 */
	public Category(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	/**
	 * Returns the unique identifier of the category.
	 * 
	 * @return The category ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the name of the category.
	 * 
	 * @return The category name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the description of the category.
	 * 
	 * @return The category description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Checks if this category is equal to another object.
	 * Two categories are equal if they have the same ID.
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
		Category category = (Category) o;
		return id == category.id;
	}

	/**
	 * Returns the hash code for this category.
	 * 
	 * @return The hash code based on ID
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	/**
	 * Returns a string representation of this category.
	 * 
	 * @return String containing category details
	 */
	@Override
	public String toString() {
		return "Category{id=" + id + ", name='" + name + '\'' + '}';
	}
}
