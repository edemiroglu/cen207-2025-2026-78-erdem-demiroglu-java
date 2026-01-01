package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.core.PasswordHasher;
import com.hyildizoglu.userAuthentication.UserFileRepository;
import com.hyildizoglu.models.User;

@DisplayName("UserFileRepository Tests")
class UserFileRepositoryTest {

	private UserFileRepository repository;
	private Path testFile;

	@BeforeEach
	void setUp() throws Exception {
		testFile = Files.createTempFile("test_user", ".dat");
		repository = new UserFileRepository(testFile);
	}

	@AfterEach
	void tearDown() throws Exception {
		Files.deleteIfExists(testFile);
	}

	@Test
	@DisplayName("Save new user")
	void testSave_NewUser() {
		User user = repository.save("testuser", "password123");

		assertNotNull(user);
		assertTrue(user.getId() > 0);
		assertEquals("testuser", user.getUsername());
	}

	@Test
	@DisplayName("Auto-generate ID")
	void testSave_GenerateId() {
		User user1 = repository.save("user1", "password1");
		User user2 = repository.save("user2", "password2");

		assertTrue(user2.getId() > user1.getId());
	}

	@Test
	@DisplayName("Password hashing check")
	void testSave_PasswordHashed() {
		User user = repository.save("testuser", "password123");

		// Password should be hashed (not plain text)
		assertNotEquals("password123", user.getPassword());
		assertTrue(user.getPassword().length() > 0);
		// SHA-256 hash is 64 character hex string
		assertEquals(64, user.getPassword().length());
	}

	@Test
	@DisplayName("Find by username - success")
	void testFindByUsername_Found() {
		repository.save("testuser", "password123");

		Optional<User> found = repository.findByUsername("testuser");

		assertTrue(found.isPresent());
		assertEquals("testuser", found.get().getUsername());
	}

	@Test
	@DisplayName("Find by username - not found")
	void testFindByUsername_NotFound() {
		Optional<User> found = repository.findByUsername("nonexistent");
		assertFalse(found.isPresent());
	}

	@Test
	@DisplayName("Find by username - case insensitive")
	void testFindByUsername_CaseInsensitive() {
		repository.save("TestUser", "password123");

		Optional<User> found = repository.findByUsername("testuser");
		assertTrue(found.isPresent());
	}

	@Test
	@DisplayName("Password hash verification")
	void testPasswordHashing() {
		User user = repository.save("testuser", "password123");

		// Hash verification
		boolean verified = PasswordHasher.verify("password123", user.getPassword());
		assertTrue(verified);
	}

	@Test
	@DisplayName("File creation check")
	void testWriteAll_FileCreation() {
		repository.save("testuser", "password123");

		assertTrue(Files.exists(testFile));
	}

	@Test
	@DisplayName("Load users from file")
	void testLoadUsersFromFile() {
		repository.save("user1", "password1");
		repository.save("user2", "password2");

		// Test loading with new repository instance
		UserFileRepository newRepo = new UserFileRepository(testFile);
		Optional<User> found = newRepo.findByUsername("user1");

		assertTrue(found.isPresent());
		assertEquals("user1", found.get().getUsername());
	}
}
