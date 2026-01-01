package com.hyildizoglu.algorithms.lists;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A doubly linked list implementation.
 * Each node has references to both next and previous nodes,
 * allowing bidirectional traversal.
 * 
 * @param <T> The type of elements stored in this list
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class DoublyLinkedList<T> implements Iterable<T> {

	/**
	 * Represents a node in the doubly linked list.
	 * 
	 * @param <T> The type of value stored in this node
	 */
	public static class Node<T> {
		/** The value stored in this node. */
		public T value;
		
		/** Reference to the next node. */
		public Node<T> next;
		
		/** Reference to the previous node. */
		public Node<T> prev;

		/**
		 * Creates a new Node with the specified value.
		 * 
		 * @param value The value to store in this node
		 */
		public Node(T value) {
			this.value = value;
		}
	}

	/** Reference to the first node in the list. */
	private Node<T> head;
	
	/** Reference to the last node in the list. */
	private Node<T> tail;
	
	/** Current number of elements in the list. */
	private int size;

	/**
	 * Adds an element to the end of the list.
	 * 
	 * @param value The element to add
	 */
	public void addLast(T value) {
		Node<T> node = new Node<>(value);
		if (head == null) {
			head = tail = node;
		} else {
			tail.next = node;
			node.prev = tail;
			tail = node;
		}
		size++;
	}

	/**
	 * Returns the number of elements in the list.
	 * 
	 * @return The number of elements
	 */
	public int size() {
		return size;
	}

	/**
	 * Checks if the list is empty.
	 * 
	 * @return true if the list contains no elements, false otherwise
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns the first node (head) of the list.
	 * 
	 * @return The head node, or null if list is empty
	 */
	public Node<T> getHeadNode() {
		return head;
	}

	/**
	 * Returns the last node (tail) of the list.
	 * 
	 * @return The tail node, or null if list is empty
	 */
	public Node<T> getTailNode() {
		return tail;
	}

	/**
	 * Returns an iterator over the elements in this list.
	 * 
	 * @return An iterator that traverses from head to tail
	 */
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private Node<T> current = head;

			@Override
			public boolean hasNext() {
				return current != null;
			}

			@Override
			public T next() {
				if (current == null) {
					throw new NoSuchElementException();
				}
				T value = current.value;
				current = current.next;
				return value;
			}
		};
	}
}
