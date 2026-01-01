package com.hyildizoglu.expenseLogging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.hyildizoglu.algorithms.compression.HuffmanCodec;
import com.hyildizoglu.algorithms.hash.ExpenseHashIndex;
import com.hyildizoglu.models.Expense;

/**
 * Repository for storing and retrieving expense data from a file.
 * Uses a semicolon-separated format with optional Huffman compression.
 * Includes hash-based indexing for O(1) lookup by expense ID.
 * 
 * File format (one expense per line):
 * id;userId;budgetId;categoryId;amount;date;description
 * 
 * Compressed format:
 * HUFF:base64EncodedCompressedData
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class ExpenseFileRepository {

	/** Path to the expense data file. */
	private final Path filePath;
	
	/** Hash index for fast expense lookup by ID. */
	private final ExpenseHashIndex index = new ExpenseHashIndex();
	
	/** Huffman codec for optional compression. */
	private final HuffmanCodec codec = new HuffmanCodec();
	
	/** Flag to enable/disable compression. */
	private static final boolean USE_COMPRESSION = false;

	/**
	 * Creates a repository with the default file path (Expense.dat).
	 */
	public ExpenseFileRepository() {
		this(Paths.get("Expense.dat"));
	}

	/**
	 * Creates a repository with the specified file path.
	 * 
	 * @param filePath Path to the expense data file
	 */
	public ExpenseFileRepository(Path filePath) {
		this.filePath = filePath;
	}

	/**
	 * Retrieves all expenses from the file.
	 * Handles both compressed and uncompressed formats.
	 * 
	 * @return List of all expenses, or empty list if file doesn't exist
	 */
	public List<Expense> findAll() {
		List<Expense> expenses = new ArrayList<>();
		if (!Files.exists(filePath)) {
			return expenses;
		}
		try (BufferedReader reader = Files.newBufferedReader(filePath)) {
			String line;
			while ((line = reader.readLine()) != null) {
				// Handle Huffman compressed lines
				if (line.startsWith("HUFF:")) {
					String base64 = line.substring(5);
					byte[] compressed = java.util.Base64.getDecoder().decode(base64);
					line = codec.decompress(compressed);
				}
				Expense expense = parseExpense(line);
				if (expense != null) {
					index.put(expense.getId(), expenses.size());
					expenses.add(expense);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to read expenses from file", e);
		}
		return expenses;
	}

	/**
	 * Finds all expenses for a specific user.
	 * 
	 * @param userId The user ID to filter by
	 * @return List of expenses belonging to the user
	 */
	public List<Expense> findByUserId(int userId) {
		List<Expense> result = new ArrayList<>();
		for (Expense expense : findAll()) {
			if (expense.getUserId() == userId) {
				result.add(expense);
			}
		}
		return result;
	}

	/**
	 * Finds an expense by its ID using the hash index.
	 * Falls back to linear scan if not in index.
	 * 
	 * @param id The expense ID
	 * @return The expense, or null if not found
	 */
	public Expense findById(int id) {
		Integer pos = index.getPosition(id);
		if (pos == null) {
			// if not in index, scan normally
			for (Expense expense : findAll()) {
				if (expense.getId() == id) {
					return expense;
				}
			}
			return null;
		}
		List<Expense> all = findAll();
		return (pos >= 0 && pos < all.size()) ? all.get(pos) : null;
	}

	/**
	 * Saves a new expense to the file.
	 * Automatically assigns a new ID.
	 * 
	 * @param expense The expense to save (ID is ignored)
	 * @return The saved expense with assigned ID
	 */
	public Expense save(Expense expense) {
		List<Expense> expenses = findAll();
		int nextId = expenses.stream().mapToInt(Expense::getId).max().orElse(0) + 1;
		Expense toSave = new Expense(nextId, expense.getUserId(), expense.getBudgetId(), expense.getCategoryId(),
				expense.getAmount(), expense.getDate(), expense.getDescription());
		expenses.add(toSave);
		index.put(toSave.getId(), expenses.size() - 1);
		writeAll(expenses);
		return toSave;
	}

	/**
	 * Updates an existing expense.
	 * 
	 * @param expense The expense with updated data
	 * @return The updated expense, or null if not found
	 */
	public Expense update(Expense expense) {
		List<Expense> expenses = findAll();
		for (int i = 0; i < expenses.size(); i++) {
			if (expenses.get(i).getId() == expense.getId()) {
				expenses.set(i, expense);
				index.put(expense.getId(), i);
				writeAll(expenses);
				return expense;
			}
		}
		return null;
	}

	/**
	 * Deletes an expense by its ID.
	 * 
	 * @param id The expense ID to delete
	 * @return true if deleted, false if not found
	 */
	public boolean deleteById(int id) {
		List<Expense> expenses = findAll();
		boolean removed = expenses.removeIf(e -> e.getId() == id);
		if (removed) {
			index.remove(id);
			writeAll(expenses);
		}
		return removed;
	}

	/**
	 * Writes all expenses to the file.
	 * Optionally compresses using Huffman encoding.
	 * 
	 * @param expenses List of expenses to write
	 */
	private void writeAll(List<Expense> expenses) {
		try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
			for (Expense expense : expenses) {
				String line = formatExpense(expense);
				if (USE_COMPRESSION) {
					// Compress with Huffman (optional)
					byte[] compressed = codec.compress(line);
					// Write with Base64 encoding
					String base64 = java.util.Base64.getEncoder().encodeToString(compressed);
					writer.write("HUFF:" + base64);
				} else {
					writer.write(line);
				}
				writer.newLine();
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to write expenses to file", e);
		}
	}

	/**
	 * Parses a line from the file into an Expense object.
	 * 
	 * @param line The line to parse
	 * @return Parsed Expense, or null if parsing fails
	 */
	private Expense parseExpense(String line) {
		String[] parts = line.split(";", -1);
		if (parts.length < 7) {
			return null;
		}
		try {
			int id = Integer.parseInt(parts[0]);
			int userId = Integer.parseInt(parts[1]);
			int budgetId = Integer.parseInt(parts[2]);
			int categoryId = Integer.parseInt(parts[3]);
			BigDecimal amount = new BigDecimal(parts[4]);
			LocalDate date = LocalDate.parse(parts[5]);
			String description = parts[6];
			return new Expense(id, userId, budgetId, categoryId, amount, date, description);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Formats an expense as a line for file storage.
	 * 
	 * @param expense The expense to format
	 * @return Formatted string representation
	 */
	private String formatExpense(Expense expense) {
		return expense.getId() + ";" + expense.getUserId() + ";" + expense.getBudgetId() + ";"
				+ expense.getCategoryId() + ";" + expense.getAmount().toPlainString() + ";" + expense.getDate() + ";"
				+ expense.getDescription();
	}
}
