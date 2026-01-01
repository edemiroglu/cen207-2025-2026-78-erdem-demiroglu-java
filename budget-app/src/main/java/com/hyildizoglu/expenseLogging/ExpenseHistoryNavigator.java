package com.hyildizoglu.expenseLogging;

import java.util.List;

import com.hyildizoglu.algorithms.lists.DoublyLinkedList;
import com.hyildizoglu.algorithms.lists.XORLinkedList;
import com.hyildizoglu.models.Expense;

/**
 * Navigator for traversing through expense history.
 * Supports bidirectional navigation (previous/next) using custom list data structures.
 * Can use either DoublyLinkedList or XORLinkedList for navigation.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class ExpenseHistoryNavigator {

	/** Doubly linked list for standard navigation. */
	private final DoublyLinkedList<Expense> doublyList;
	
	/** XOR linked list for memory-efficient navigation. */
	private final XORLinkedList<Expense> xorList;
	
	/** Flag indicating which list implementation to use. */
	private boolean useXor;
	
	/** Current node in doubly linked list navigation. */
	private DoublyLinkedList.Node<Expense> currentNode;

	/**
	 * Creates a navigator using DoublyLinkedList (default).
	 * 
	 * @param expenses List of expenses to navigate
	 */
	public ExpenseHistoryNavigator(List<Expense> expenses) {
		this(expenses, false);
	}

	/**
	 * Creates a navigator with choice of list implementation.
	 * 
	 * @param expenses List of expenses to navigate
	 * @param useXor   true for XORLinkedList, false for DoublyLinkedList
	 */
	public ExpenseHistoryNavigator(List<Expense> expenses, boolean useXor) {
		this.useXor = useXor;
		this.doublyList = new DoublyLinkedList<>();
		this.xorList = new XORLinkedList<>();
		for (Expense e : expenses) {
			doublyList.addLast(e);
			xorList.add(e);
		}
		// XOR list maintains its own current index
		currentNode = doublyList.getHeadNode();
	}

	/**
	 * Returns the current expense in the navigation.
	 * 
	 * @return Current expense, or null if empty
	 */
	public Expense current() {
		if (useXor) {
			return xorList.current();
		}
		return currentNode != null ? currentNode.value : null;
	}

	/**
	 * Moves to the next expense and returns it.
	 * If at the end, stays at the last expense.
	 * 
	 * @return The next expense (or current if at end)
	 */
	public Expense next() {
		if (useXor) {
			return xorList.next();
		}
		if (currentNode != null && currentNode.next != null) {
			currentNode = currentNode.next;
		}
		return current();
	}

	/**
	 * Moves to the previous expense and returns it.
	 * If at the beginning, stays at the first expense.
	 * 
	 * @return The previous expense (or current if at beginning)
	 */
	public Expense previous() {
		if (useXor) {
			return xorList.previous();
		}
		if (currentNode != null && currentNode.prev != null) {
			currentNode = currentNode.prev;
		}
		return current();
	}
}
