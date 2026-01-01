package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.models.User;

@DisplayName("User Model Tests")
class UserTest {

	// ==================== CONSTRUCTOR AND GETTERS ====================

	@Test
	@DisplayName("User creation")
	void testUserCreation() {
		User user = new User(1, "testuser", "password123");

		assertEquals(1, user.getId());
		assertEquals("testuser", user.getUsername());
		assertEquals("password123", user.getPassword());
	}

	@Test
	@DisplayName("Getter methods")
	void testGetters() {
		User user = new User(2, "username", "password");

		assertEquals(2, user.getId());
		assertEquals("username", user.getUsername());
		assertEquals("password", user.getPassword());
	}

	@Test
	@DisplayName("User with hashed password")
	void testUserWithHashedPassword() {
		String hashedPassword = "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3";
		User user = new User(1, "testuser", hashedPassword);

		assertEquals(hashedPassword, user.getPassword());
	}

	@Test
	@DisplayName("User with null values")
	void testUserWithNullValues() {
		User user = new User(1, null, null);

		assertEquals(1, user.getId());
		assertNull(user.getUsername());
		assertNull(user.getPassword());
	}

	@Test
	@DisplayName("getId returns correct id")
	void testGetId() {
		User user = new User(42, "test", "pass");
		assertEquals(42, user.getId());
	}

	@Test
	@DisplayName("getUsername returns correct username")
	void testGetUsername() {
		User user = new User(1, "myusername", "pass");
		assertEquals("myusername", user.getUsername());
	}

	@Test
	@DisplayName("getPassword returns correct password")
	void testGetPassword() {
		User user = new User(1, "test", "mypassword");
		assertEquals("mypassword", user.getPassword());
	}

	// ==================== EQUALS TESTS ====================

	@Test
	@DisplayName("Same instance should be equal")
	void testEquals_SameInstance() {
		User user = new User(1, "test", "pass");
		assertTrue(user.equals(user));
	}

	@Test
	@DisplayName("Null should not be equal")
	void testEquals_Null() {
		User user = new User(1, "test", "pass");
		assertFalse(user.equals(null));
	}

	@Test
	@DisplayName("Different class should not be equal")
	void testEquals_DifferentClass() {
		User user = new User(1, "test", "pass");
		assertFalse(user.equals("a string"));
	}

	@Test
	@DisplayName("Same id and username should be equal")
	void testEquals_SameIdAndUsername() {
		User user1 = new User(1, "testuser", "pass1");
		User user2 = new User(1, "testuser", "differentpass");

		assertTrue(user1.equals(user2));
		assertTrue(user2.equals(user1));
	}

	@Test
	@DisplayName("Different id should not be equal")
	void testEquals_DifferentId() {
		User user1 = new User(1, "testuser", "pass");
		User user2 = new User(2, "testuser", "pass");

		assertFalse(user1.equals(user2));
	}

	@Test
	@DisplayName("Different username should not be equal")
	void testEquals_DifferentUsername() {
		User user1 = new User(1, "user1", "pass");
		User user2 = new User(1, "user2", "pass");

		assertFalse(user1.equals(user2));
	}

	@Test
	@DisplayName("Same id different username should not be equal")
	void testEquals_SameIdDifferentUsername() {
		User user1 = new User(1, "alice", "pass");
		User user2 = new User(1, "bob", "pass");

		assertFalse(user1.equals(user2));
	}

	// ==================== HASHCODE TESTS ====================

	@Test
	@DisplayName("Same id and username should have same hashCode")
	void testHashCode_Same() {
		User user1 = new User(1, "testuser", "pass1");
		User user2 = new User(1, "testuser", "pass2");

		assertEquals(user1.hashCode(), user2.hashCode());
	}

	@Test
	@DisplayName("Different id should have different hashCode")
	void testHashCode_DifferentId() {
		User user1 = new User(1, "testuser", "pass");
		User user2 = new User(2, "testuser", "pass");

		assertNotEquals(user1.hashCode(), user2.hashCode());
	}

	@Test
	@DisplayName("Different username should have different hashCode")
	void testHashCode_DifferentUsername() {
		User user1 = new User(1, "user1", "pass");
		User user2 = new User(1, "user2", "pass");

		assertNotEquals(user1.hashCode(), user2.hashCode());
	}

	@Test
	@DisplayName("HashCode should be consistent")
	void testHashCode_Consistent() {
		User user = new User(1, "test", "pass");
		int hash1 = user.hashCode();
		int hash2 = user.hashCode();

		assertEquals(hash1, hash2);
	}

	// ==================== TOSTRING TESTS ====================

	@Test
	@DisplayName("toString should contain id")
	void testToString_ContainsId() {
		User user = new User(42, "test", "pass");
		String result = user.toString();

		assertTrue(result.contains("42"));
	}

	@Test
	@DisplayName("toString should contain username")
	void testToString_ContainsUsername() {
		User user = new User(1, "myusername", "pass");
		String result = user.toString();

		assertTrue(result.contains("myusername"));
	}

	@Test
	@DisplayName("toString should NOT contain password")
	void testToString_DoesNotContainPassword() {
		User user = new User(1, "test", "secretpassword");
		String result = user.toString();

		assertFalse(result.contains("secretpassword"));
	}

	@Test
	@DisplayName("toString should have correct format")
	void testToString_Format() {
		User user = new User(1, "testuser", "pass");
		String result = user.toString();

		assertTrue(result.startsWith("User{"));
		assertTrue(result.contains("id=1"));
		assertTrue(result.contains("username='testuser'"));
		assertTrue(result.endsWith("}"));
	}

	@Test
	@DisplayName("toString with null username")
	void testToString_NullUsername() {
		User user = new User(1, null, "pass");
		String result = user.toString();

		assertNotNull(result);
		assertTrue(result.contains("null"));
	}
}
