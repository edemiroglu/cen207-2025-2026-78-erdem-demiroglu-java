package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.models.Category;

@DisplayName("Category Model Tests")
class CategoryTest {

	// ==================== CONSTRUCTOR AND GETTERS ====================

	@Test
	@DisplayName("Category creation with all fields")
	void testCategoryCreation() {
		Category category = new Category(1, "Food", "Food and dining expenses");

		assertEquals(1, category.getId());
		assertEquals("Food", category.getName());
		assertEquals("Food and dining expenses", category.getDescription());
	}

	@Test
	@DisplayName("Category with null name and description")
	void testCategoryWithNullValues() {
		Category category = new Category(1, null, null);

		assertEquals(1, category.getId());
		assertNull(category.getName());
		assertNull(category.getDescription());
	}

	@Test
	@DisplayName("Category with empty strings")
	void testCategoryWithEmptyStrings() {
		Category category = new Category(1, "", "");

		assertEquals("", category.getName());
		assertEquals("", category.getDescription());
	}

	@Test
	@DisplayName("getId should return correct id")
	void testGetId() {
		Category category = new Category(42, "Test", "Description");
		assertEquals(42, category.getId());
	}

	@Test
	@DisplayName("getName should return correct name")
	void testGetName() {
		Category category = new Category(1, "Transportation", "Transport costs");
		assertEquals("Transportation", category.getName());
	}

	@Test
	@DisplayName("getDescription should return correct description")
	void testGetDescription() {
		Category category = new Category(1, "Test", "Test description");
		assertEquals("Test description", category.getDescription());
	}

	// ==================== EQUALS TESTS ====================

	@Test
	@DisplayName("Same instance should be equal")
	void testEquals_SameInstance() {
		Category category = new Category(1, "Test", "Desc");
		assertTrue(category.equals(category));
	}

	@Test
	@DisplayName("Null should not be equal")
	void testEquals_Null() {
		Category category = new Category(1, "Test", "Desc");
		assertFalse(category.equals(null));
	}

	@Test
	@DisplayName("Different class should not be equal")
	void testEquals_DifferentClass() {
		Category category = new Category(1, "Test", "Desc");
		assertFalse(category.equals("a string"));
	}

	@Test
	@DisplayName("Same id should be equal")
	void testEquals_SameId() {
		Category cat1 = new Category(1, "Food", "Food expenses");
		Category cat2 = new Category(1, "Different", "Different desc");

		assertTrue(cat1.equals(cat2));
		assertTrue(cat2.equals(cat1));
	}

	@Test
	@DisplayName("Different id should not be equal")
	void testEquals_DifferentId() {
		Category cat1 = new Category(1, "Food", "Food expenses");
		Category cat2 = new Category(2, "Food", "Food expenses");

		assertFalse(cat1.equals(cat2));
	}

	// ==================== HASHCODE TESTS ====================

	@Test
	@DisplayName("Same id should have same hashCode")
	void testHashCode_SameId() {
		Category cat1 = new Category(1, "Food", "Food expenses");
		Category cat2 = new Category(1, "Different", "Different desc");

		assertEquals(cat1.hashCode(), cat2.hashCode());
	}

	@Test
	@DisplayName("Different id should have different hashCode")
	void testHashCode_DifferentId() {
		Category cat1 = new Category(1, "Food", "Food expenses");
		Category cat2 = new Category(2, "Food", "Food expenses");

		assertNotEquals(cat1.hashCode(), cat2.hashCode());
	}

	@Test
	@DisplayName("HashCode should be consistent")
	void testHashCode_Consistent() {
		Category category = new Category(1, "Test", "Desc");
		int hash1 = category.hashCode();
		int hash2 = category.hashCode();

		assertEquals(hash1, hash2);
	}

	// ==================== TOSTRING TESTS ====================

	@Test
	@DisplayName("toString should contain id")
	void testToString_ContainsId() {
		Category category = new Category(42, "Test", "Desc");
		String result = category.toString();

		assertTrue(result.contains("42"));
	}

	@Test
	@DisplayName("toString should contain name")
	void testToString_ContainsName() {
		Category category = new Category(1, "MyCategory", "Desc");
		String result = category.toString();

		assertTrue(result.contains("MyCategory"));
	}

	@Test
	@DisplayName("toString should have correct format")
	void testToString_Format() {
		Category category = new Category(1, "Food", "Food expenses");
		String result = category.toString();

		assertTrue(result.startsWith("Category{"));
		assertTrue(result.contains("id=1"));
		assertTrue(result.contains("name='Food'"));
		assertTrue(result.endsWith("}"));
	}

	@Test
	@DisplayName("toString with null name")
	void testToString_NullName() {
		Category category = new Category(1, null, "Desc");
		String result = category.toString();

		assertNotNull(result);
		assertTrue(result.contains("null"));
	}
}



