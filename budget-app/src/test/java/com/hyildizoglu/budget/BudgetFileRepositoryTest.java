package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.budgetCreation.BudgetFileRepository;
import com.hyildizoglu.models.Budget;

@DisplayName("BudgetFileRepository Tests")
class BudgetFileRepositoryTest {

	private BudgetFileRepository repository;
	private Path testFile;

	@BeforeEach
	void setUp() throws Exception {
		testFile = Files.createTempFile("test_budget", ".dat");
		repository = new BudgetFileRepository(testFile);
	}

	@AfterEach
	void tearDown() throws Exception {
		Files.deleteIfExists(testFile);
	}

	@Test
	@DisplayName("Save new budget")
	void testSave_NewBudget() {
		Budget budget = new Budget(0, 1, "Test Budget", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

		Budget saved = repository.save(budget);

		assertNotNull(saved);
		assertTrue(saved.getId() > 0);
		assertEquals("Test Budget", saved.getName());
	}

	@Test
	@DisplayName("Auto-generate ID")
	void testSave_GenerateId() {
		Budget budget1 = new Budget(0, 1, "Budget 1", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
		Budget budget2 = new Budget(0, 1, "Budget 2", new BigDecimal("2000"),
				LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28));

		Budget saved1 = repository.save(budget1);
		Budget saved2 = repository.save(budget2);

		assertTrue(saved2.getId() > saved1.getId());
	}

	@Test
	@DisplayName("Find budget by ID - success")
	void testFindById_Found() {
		Budget budget = new Budget(0, 1, "Test", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
		Budget saved = repository.save(budget);

		Optional<Budget> found = repository.findById(saved.getId());

		assertTrue(found.isPresent());
		assertEquals(saved.getId(), found.get().getId());
		assertEquals("Test", found.get().getName());
	}

	@Test
	@DisplayName("Find budget by ID - not found")
	void testFindById_NotFound() {
		Optional<Budget> found = repository.findById(999);
		assertFalse(found.isPresent());
	}

	@Test
	@DisplayName("Find budgets by user ID")
	void testFindByUserId() {
		repository.save(new Budget(0, 1, "Budget 1", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)));
		repository.save(new Budget(0, 1, "Budget 2", new BigDecimal("2000"),
				LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28)));
		repository.save(new Budget(0, 2, "Budget 3", new BigDecimal("3000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)));

		List<Budget> budgets = repository.findByUserId(1);

		assertEquals(2, budgets.size());
	}

	@Test
	@DisplayName("Update budget - success")
	void testUpdate_Success() {
		Budget budget = new Budget(0, 1, "Original", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
		Budget saved = repository.save(budget);

		Budget updated = new Budget(saved.getId(), 1, "Updated", new BigDecimal("2000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
		Budget result = repository.update(updated);

		assertNotNull(result);
		assertEquals("Updated", result.getName());
		assertEquals(new BigDecimal("2000"), result.getTotalLimit());
	}

	@Test
	@DisplayName("Update budget - not found")
	void testUpdate_NotFound() {
		Budget budget = new Budget(999, 1, "Test", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

		Budget result = repository.update(budget);
		assertNull(result);
	}

	@Test
	@DisplayName("Delete budget - success")
	void testDeleteById_Success() {
		Budget budget = new Budget(0, 1, "Test", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
		Budget saved = repository.save(budget);

		boolean deleted = repository.deleteById(saved.getId());
		assertTrue(deleted);

		Optional<Budget> found = repository.findById(saved.getId());
		assertFalse(found.isPresent());
	}

	@Test
	@DisplayName("Delete budget - not found")
	void testDeleteById_NotFound() {
		boolean deleted = repository.deleteById(999);
		assertFalse(deleted);
	}

	@Test
	@DisplayName("File creation check")
	void testWriteAll_FileCreation() {
		repository.save(new Budget(0, 1, "Test", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)));

		assertTrue(Files.exists(testFile));
	}

	@Test
	@DisplayName("Budget parsing test")
	void testParseBudget_ValidLine() {
		Budget budget = new Budget(1, 1, "Test", new BigDecimal("1000"),
				LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 31));
		repository.save(budget);

		List<Budget> all = repository.findAll();
		assertTrue(all.size() > 0);
		assertEquals("Test", all.get(0).getName());
	}
}
