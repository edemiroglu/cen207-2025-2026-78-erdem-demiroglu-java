package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.userAuthentication.UserAuthService;
import com.hyildizoglu.userAuthentication.UserFileRepository;
import com.hyildizoglu.models.User;

@DisplayName("UserAuthService Tests")
class UserAuthServiceTest {

	private UserAuthService userAuthService;
	private UserFileRepository repository;
	private Path testFile;

	@BeforeEach
	void setUp() throws Exception {
		testFile = Files.createTempFile("test_user", ".dat");
		repository = new UserFileRepository(testFile);
		userAuthService = new UserAuthService(repository);
	}

	@AfterEach
	void tearDown() throws Exception {
		Files.deleteIfExists(testFile);
	}

	@Test
	@DisplayName("User registration should succeed")
	void testRegister_Success() {
		Optional<User> user = userAuthService.register("testuser", "password123");

		assertTrue(user.isPresent());
		assertEquals("testuser", user.get().getUsername());
		// Password should be hashed
		assertNotEquals("password123", user.get().getPassword());
	}

	@Test
	@DisplayName("Registration with duplicate username should fail")
	void testRegister_DuplicateUsername() {
		userAuthService.register("testuser", "password123");
		Optional<User> user = userAuthService.register("testuser", "password456");

		assertFalse(user.isPresent());
	}

	@Test
	@DisplayName("Login should succeed")
	void testLogin_Success() {
		userAuthService.register("testuser", "password123");
		Optional<User> user = userAuthService.login("testuser", "password123");

		assertTrue(user.isPresent());
		assertEquals("testuser", user.get().getUsername());
	}

	@Test
	@DisplayName("Login with wrong password should fail")
	void testLogin_InvalidPassword() {
		userAuthService.register("testuser", "password123");
		Optional<User> user = userAuthService.login("testuser", "wrongpassword");

		assertFalse(user.isPresent());
	}

	@Test
	@DisplayName("Login with non-existent user should fail")
	void testLogin_UserNotFound() {
		Optional<User> user = userAuthService.login("nonexistent", "password123");

		assertFalse(user.isPresent());
	}

	@Test
	@DisplayName("Create guest user")
	void testCreateGuestUser() {
		User guest = userAuthService.createGuestUser();

		assertNotNull(guest);
		assertEquals("guest", guest.getUsername());
		assertEquals(-1, guest.getId());
	}
}
