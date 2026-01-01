package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.expenseLogging.ExpenseFileRepository;
import com.hyildizoglu.models.Expense;

@DisplayName("ExpenseFileRepository Tests")
class ExpenseFileRepositoryTest {

	private ExpenseFileRepository repository;
	private Path testFile;

	@BeforeEach
	void setUp() throws Exception {
		testFile = Files.createTempFile("test_expense", ".dat");
		repository = new ExpenseFileRepository(testFile);
	}

	@AfterEach
	void tearDown() throws Exception {
		Files.deleteIfExists(testFile);
	}

	@Test
	@DisplayName("Save new expense")
	void testSave_NewExpense() {
		Expense expense = new Expense(0, 1, 1, 1, new BigDecimal("100.50"),
				LocalDate.of(2025, 1, 15), "Test expense");

		Expense saved = repository.save(expense);

		assertNotNull(saved);
		assertTrue(saved.getId() > 0);
		assertEquals(new BigDecimal("100.50"), saved.getAmount());
	}

	@Test
	@DisplayName("Auto-generate ID")
	void testSave_GenerateId() {
		Expense expense1 = new Expense(0, 1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Expense 1");
		Expense expense2 = new Expense(0, 1, 1, 1, new BigDecimal("200"),
				LocalDate.of(2025, 1, 16), "Expense 2");

		Expense saved1 = repository.save(expense1);
		Expense saved2 = repository.save(expense2);

		assertTrue(saved2.getId() > saved1.getId());
	}

	@Test
	@DisplayName("Find expense by ID - success")
	void testFindById_Found() {
		Expense expense = new Expense(0, 1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Test");
		Expense saved = repository.save(expense);

		Expense found = repository.findById(saved.getId());

		assertNotNull(found);
		assertEquals(saved.getId(), found.getId());
		assertEquals("Test", found.getDescription());
	}

	@Test
	@DisplayName("Find expense by ID - not found")
	void testFindById_NotFound() {
		Expense found = repository.findById(999);
		assertNull(found);
	}

	@Test
	@DisplayName("Find expenses by user ID")
	void testFindByUserId() {
		repository.save(new Expense(0, 1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Expense 1"));
		repository.save(new Expense(0, 1, 1, 1, new BigDecimal("200"),
				LocalDate.of(2025, 1, 16), "Expense 2"));
		repository.save(new Expense(0, 2, 1, 1, new BigDecimal("300"),
				LocalDate.of(2025, 1, 17), "Expense 3"));

		List<Expense> expenses = repository.findByUserId(1);

		assertEquals(2, expenses.size());
	}

	@Test
	@DisplayName("Update expense - success")
	void testUpdate_Success() {
		Expense expense = new Expense(0, 1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Original");
		Expense saved = repository.save(expense);

		Expense updated = new Expense(saved.getId(), 1, 1, 2, new BigDecimal("200"),
				LocalDate.of(2025, 1, 20), "Updated");
		Expense result = repository.update(updated);

		assertNotNull(result);
		assertEquals(new BigDecimal("200"), result.getAmount());
		assertEquals("Updated", result.getDescription());
		assertEquals(2, result.getCategoryId());
	}

	@Test
	@DisplayName("Update expense - not found")
	void testUpdate_NotFound() {
		Expense expense = new Expense(999, 1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Test");

		Expense result = repository.update(expense);
		assertNull(result);
	}

	@Test
	@DisplayName("Delete expense - success")
	void testDeleteById_Success() {
		Expense expense = new Expense(0, 1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Test");
		Expense saved = repository.save(expense);

		boolean deleted = repository.deleteById(saved.getId());
		assertTrue(deleted);

		Expense found = repository.findById(saved.getId());
		assertNull(found);
	}

	@Test
	@DisplayName("Delete expense - not found")
	void testDeleteById_NotFound() {
		boolean deleted = repository.deleteById(999);
		assertFalse(deleted);
	}

	@Test
	@DisplayName("Index usage test")
	void testIndexUsage() {
		Expense expense = new Expense(0, 1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Test");
		Expense saved = repository.save(expense);

		// Test fast lookup using index
		Expense found = repository.findById(saved.getId());
		assertNotNull(found);
	}

	@Test
	@DisplayName("File creation check")
	void testWriteAll_FileCreation() {
		repository.save(new Expense(0, 1, 1, 1, new BigDecimal("100"),
				LocalDate.of(2025, 1, 15), "Test"));

		assertTrue(Files.exists(testFile));
	}

	@Test
	@DisplayName("Expense parsing test")
	void testParseExpense_ValidLine() {
		Expense expense = new Expense(1, 1, 1, 1, new BigDecimal("100.50"),
				LocalDate.of(2025, 1, 15), "Test expense");
		repository.save(expense);

		List<Expense> all = repository.findAll();
		assertTrue(all.size() > 0);
		assertEquals("Test expense", all.get(0).getDescription());
	}
}
