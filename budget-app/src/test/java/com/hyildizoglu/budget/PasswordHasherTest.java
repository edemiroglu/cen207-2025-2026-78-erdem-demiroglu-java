package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.core.PasswordHasher;

@DisplayName("PasswordHasher Tests")
class PasswordHasherTest {

	@Test
	@DisplayName("Hash not null")
	void testHash_NotNull() {
		String hash = PasswordHasher.hash("testpassword");
		assertNotNull(hash);
		assertFalse(hash.isEmpty());
	}

	@Test
	@DisplayName("Hash is consistent")
	void testHash_Consistent() {
		String hash1 = PasswordHasher.hash("testpassword");
		String hash2 = PasswordHasher.hash("testpassword");
		assertEquals(hash1, hash2);
	}

	@Test
	@DisplayName("Different passwords produce different hashes")
	void testHash_DifferentPasswords() {
		String hash1 = PasswordHasher.hash("password1");
		String hash2 = PasswordHasher.hash("password2");
		assertNotEquals(hash1, hash2);
	}

	@Test
	@DisplayName("Verify correct password")
	void testVerify_Correct() {
		String password = "testpassword";
		String hash = PasswordHasher.hash(password);
		assertTrue(PasswordHasher.verify(password, hash));
	}

	@Test
	@DisplayName("Verify incorrect password")
	void testVerify_Incorrect() {
		String password = "testpassword";
		String hash = PasswordHasher.hash(password);
		assertFalse(PasswordHasher.verify("wrongpassword", hash));
	}

	@Test
	@DisplayName("Verify null password")
	void testVerify_NullPassword() {
		String hash = PasswordHasher.hash("test");
		assertFalse(PasswordHasher.verify(null, hash));
	}

	@Test
	@DisplayName("Verify null hash")
	void testVerify_NullHash() {
		assertFalse(PasswordHasher.verify("test", null));
	}
}
