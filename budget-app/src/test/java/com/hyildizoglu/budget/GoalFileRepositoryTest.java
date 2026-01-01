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

import com.hyildizoglu.savingsGoal.GoalFileRepository;
import com.hyildizoglu.models.Goal;

@DisplayName("GoalFileRepository Tests")
class GoalFileRepositoryTest {

	private GoalFileRepository repository;
	private Path testFile;

	@BeforeEach
	void setUp() throws Exception {
		testFile = Files.createTempFile("test_goal", ".dat");
		repository = new GoalFileRepository(testFile);
	}

	@AfterEach
	void tearDown() throws Exception {
		Files.deleteIfExists(testFile);
	}

	@Test
	@DisplayName("Save new goal")
	void testSave_NewGoal() {
		Goal goal = new Goal(0, 1, "Vacation", new BigDecimal("5000"), new BigDecimal("1000"),
				LocalDate.of(2025, 12, 31));

		Goal saved = repository.save(goal);

		assertNotNull(saved);
		assertTrue(saved.getId() > 0);
		assertEquals("Vacation", saved.getName());
	}

	@Test
	@DisplayName("Auto-generate ID")
	void testSave_GenerateId() {
		Goal goal1 = new Goal(0, 1, "Goal 1", new BigDecimal("1000"), new BigDecimal("100"),
				LocalDate.of(2025, 6, 30));
		Goal goal2 = new Goal(0, 1, "Goal 2", new BigDecimal("2000"), new BigDecimal("200"),
				LocalDate.of(2025, 12, 31));

		Goal saved1 = repository.save(goal1);
		Goal saved2 = repository.save(goal2);

		assertTrue(saved2.getId() > saved1.getId());
	}

	@Test
	@DisplayName("Find goal by ID")
	void testFindById_Found() {
		Goal goal = new Goal(0, 1, "Test", new BigDecimal("1000"), new BigDecimal("100"),
				LocalDate.of(2025, 12, 31));
		Goal saved = repository.save(goal);

		// GoalFileRepository doesn't have findById, test using findAll
		List<Goal> all = repository.findAll();
		Goal found = all.stream().filter(g -> g.getId() == saved.getId()).findFirst().orElse(null);

		assertNotNull(found);
		assertEquals("Test", found.getName());
	}

	@Test
	@DisplayName("Find goals by user ID")
	void testFindByUserId() {
		repository.save(new Goal(0, 1, "Goal 1", new BigDecimal("1000"), new BigDecimal("100"),
				LocalDate.of(2025, 6, 30)));
		repository.save(new Goal(0, 1, "Goal 2", new BigDecimal("2000"), new BigDecimal("200"),
				LocalDate.of(2025, 12, 31)));
		repository.save(new Goal(0, 2, "Goal 3", new BigDecimal("3000"), new BigDecimal("300"),
				LocalDate.of(2025, 12, 31)));

		List<Goal> goals = repository.findByUserId(1);

		assertEquals(2, goals.size());
	}

	@Test
	@DisplayName("Update goal - success")
	void testUpdate_Success() {
		Goal goal = new Goal(0, 1, "Original", new BigDecimal("1000"), new BigDecimal("100"),
				LocalDate.of(2025, 6, 30));
		Goal saved = repository.save(goal);

		Goal updated = new Goal(saved.getId(), 1, "Updated", new BigDecimal("2000"),
				new BigDecimal("500"), LocalDate.of(2025, 12, 31));
		Goal result = repository.update(updated);

		assertNotNull(result);
		assertEquals("Updated", result.getName());
		assertEquals(new BigDecimal("2000"), result.getTargetAmount());
	}

	@Test
	@DisplayName("Update goal - not found")
	void testUpdate_NotFound() {
		Goal goal = new Goal(999, 1, "Test", new BigDecimal("1000"), new BigDecimal("100"),
				LocalDate.of(2025, 12, 31));

		Goal result = repository.update(goal);
		assertNull(result);
	}

	@Test
	@DisplayName("Delete goal - success")
	void testDeleteById_Success() {
		Goal goal = new Goal(0, 1, "Test", new BigDecimal("1000"), new BigDecimal("100"),
				LocalDate.of(2025, 12, 31));
		Goal saved = repository.save(goal);

		boolean deleted = repository.deleteById(saved.getId());
		assertTrue(deleted);

		List<Goal> goals = repository.findByUserId(1);
		assertEquals(0, goals.size());
	}

	@Test
	@DisplayName("Delete goal - not found")
	void testDeleteById_NotFound() {
		boolean deleted = repository.deleteById(999);
		assertFalse(deleted);
	}

	@Test
	@DisplayName("File creation check")
	void testWriteAll_FileCreation() {
		repository.save(new Goal(0, 1, "Test", new BigDecimal("1000"), new BigDecimal("100"),
				LocalDate.of(2025, 12, 31)));

		assertTrue(Files.exists(testFile));
	}

	@Test
	@DisplayName("Goal parsing test")
	void testParseGoal_ValidLine() {
		Goal goal = new Goal(1, 1, "Test Goal", new BigDecimal("1000"), new BigDecimal("500"),
				LocalDate.of(2025, 12, 31));
		repository.save(goal);

		List<Goal> all = repository.findAll();
		assertTrue(all.size() > 0);
		assertEquals("Test Goal", all.get(0).getName());
	}
}
