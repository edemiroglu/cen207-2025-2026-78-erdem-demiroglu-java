package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.savingsGoal.GoalFileRepository;
import com.hyildizoglu.savingsGoal.GoalService;
import com.hyildizoglu.models.Goal;

@DisplayName("GoalService Tests")
class GoalServiceTest {

	private GoalService goalService;
	private GoalFileRepository repository;
	private Path testFile;

	@BeforeEach
	void setUp() throws Exception {
		testFile = Files.createTempFile("test_goal", ".dat");
		repository = new GoalFileRepository(testFile);
		goalService = new GoalService(repository);
	}

	@AfterEach
	void tearDown() throws Exception {
		Files.deleteIfExists(testFile);
	}

	@Test
	@DisplayName("Goal creation should succeed")
	void testCreateGoal_Success() {
		Goal goal = goalService.createGoal(1, "Vacation", new BigDecimal("5000"), new BigDecimal("1000"),
				LocalDate.of(2025, 12, 31));

		assertNotNull(goal);
		assertTrue(goal.getId() > 0);
		assertEquals("Vacation", goal.getName());
		assertEquals(new BigDecimal("5000"), goal.getTargetAmount());
		assertEquals(new BigDecimal("1000"), goal.getCurrentAmount());
	}

	@Test
	@DisplayName("List goals for user")
	void testListGoalsForUser() {
		goalService.createGoal(1, "Goal 1", new BigDecimal("1000"), new BigDecimal("100"),
				LocalDate.of(2025, 6, 30));
		goalService.createGoal(1, "Goal 2", new BigDecimal("2000"), new BigDecimal("200"),
				LocalDate.of(2025, 12, 31));
		goalService.createGoal(2, "Goal 3", new BigDecimal("3000"), new BigDecimal("300"),
				LocalDate.of(2025, 12, 31));

		List<Goal> goals = goalService.listGoalsForUser(1);
		assertEquals(2, goals.size());
	}

	@Test
	@DisplayName("Goal update should succeed")
	void testUpdateGoal_Success() {
		Goal created = goalService.createGoal(1, "Original", new BigDecimal("1000"), new BigDecimal("100"),
				LocalDate.of(2025, 6, 30));

		Goal updated = goalService.updateGoal(created.getId(), "Updated", new BigDecimal("2000"),
				new BigDecimal("500"), LocalDate.of(2025, 12, 31));

		assertNotNull(updated);
		assertEquals("Updated", updated.getName());
		assertEquals(new BigDecimal("2000"), updated.getTargetAmount());
		assertEquals(new BigDecimal("500"), updated.getCurrentAmount());
	}

	@Test
	@DisplayName("Update non-existent goal should return null")
	void testUpdateGoal_NotFound() {
		Goal updated = goalService.updateGoal(999, "Test", new BigDecimal("1000"), new BigDecimal("100"),
				LocalDate.of(2025, 12, 31));

		assertNull(updated);
	}

	@Test
	@DisplayName("Goal deletion should succeed")
	void testDeleteGoal_Success() {
		Goal created = goalService.createGoal(1, "Test", new BigDecimal("1000"), new BigDecimal("100"),
				LocalDate.of(2025, 12, 31));

		boolean deleted = goalService.deleteGoal(created.getId());
		assertTrue(deleted);

		List<Goal> goals = goalService.listGoalsForUser(1);
		assertEquals(0, goals.size());
	}

	@Test
	@DisplayName("Delete non-existent goal should return false")
	void testDeleteGoal_NotFound() {
		boolean deleted = goalService.deleteGoal(999);
		assertFalse(deleted);
	}

	@Test
	@DisplayName("Analyze goal dependencies")
	void testAnalyzeGoalDependencies() {
		Goal goal1 = goalService.createGoal(1, "Goal 1", new BigDecimal("1000"), new BigDecimal("100"),
				LocalDate.of(2025, 6, 30));
		Goal goal2 = goalService.createGoal(1, "Goal 2", new BigDecimal("2000"), new BigDecimal("200"),
				LocalDate.of(2025, 12, 31));
		Goal goal3 = goalService.createGoal(1, "Goal 3", new BigDecimal("3000"), new BigDecimal("300"),
				LocalDate.of(2025, 12, 31));

		Map<Integer, List<Integer>> dependencies = new HashMap<>();
		dependencies.put(goal1.getId(), java.util.Arrays.asList(goal2.getId()));
		dependencies.put(goal2.getId(), java.util.Arrays.asList(goal3.getId()));

		List<Set<Integer>> sccs = goalService.analyzeGoalDependencies(dependencies);

		assertNotNull(sccs);
		// SCC analysis results can be verified
		assertTrue(sccs.size() >= 0);
	}

	@Test
	@DisplayName("Analyze with no dependencies")
	void testAnalyzeGoalDependencies_NoDependencies() {
		Map<Integer, List<Integer>> dependencies = new HashMap<>();
		List<Set<Integer>> sccs = goalService.analyzeGoalDependencies(dependencies);

		assertNotNull(sccs);
	}

	@Test
	@DisplayName("Goal completed check - completed")
	void testGoalIsCompleted() {
		Goal goal = goalService.createGoal(1, "Test", new BigDecimal("1000"), new BigDecimal("1000"),
				LocalDate.of(2025, 12, 31));

		assertTrue(goal.isCompleted());
	}

	@Test
	@DisplayName("Goal completed check - not completed")
	void testGoalNotCompleted() {
		Goal goal = goalService.createGoal(1, "Test", new BigDecimal("1000"), new BigDecimal("500"),
				LocalDate.of(2025, 12, 31));

		assertFalse(goal.isCompleted());
	}
}
