package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.file.IndexedFileRepository;

@DisplayName("IndexedFileRepository Tests")
class IndexedFileRepositoryTest {

	private Path tempFile;
	private IndexedFileRepository repository;

	@BeforeEach
	void setUp() throws IOException {
		tempFile = Files.createTempFile("indexed_test_", ".dat");
		// Delete file so repository starts fresh
		Files.deleteIfExists(tempFile);
	}

	@AfterEach
	void tearDown() throws IOException {
		if (tempFile != null) {
			Files.deleteIfExists(tempFile);
		}
	}

	// ==================== CONSTRUCTOR TESTS ====================

	@Test
	@DisplayName("Default constructor should work")
	void testDefaultConstructor() {
		// Just test it doesn't throw
		IndexedFileRepository repo = new IndexedFileRepository();
		assertNotNull(repo);
	}

	@Test
	@DisplayName("Constructor with path should work")
	void testConstructorWithPath() {
		repository = new IndexedFileRepository(tempFile);
		assertNotNull(repository);
	}

	@Test
	@DisplayName("Constructor should load existing file")
	void testConstructorLoadsExistingFile() throws IOException {
		// Create file with data
		try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
			writer.write("line1");
			writer.newLine();
			writer.write("line2");
			writer.newLine();
		}
		
		repository = new IndexedFileRepository(tempFile);
		
		// Data should be loaded
		List<String> all = repository.findAll();
		assertEquals(2, all.size());
	}

	// ==================== SAVE TESTS ====================

	@Test
	@DisplayName("Save should store data")
	void testSave() {
		repository = new IndexedFileRepository(tempFile);
		
		repository.save(1, "test data");
		
		String result = repository.findById(1);
		assertEquals("test data", result);
	}

	@Test
	@DisplayName("Save multiple items should work")
	void testSaveMultiple() {
		repository = new IndexedFileRepository(tempFile);
		
		repository.save(1, "first");
		repository.save(2, "second");
		repository.save(3, "third");
		
		assertEquals("first", repository.findById(1));
		assertEquals("second", repository.findById(2));
		assertEquals("third", repository.findById(3));
	}

	// ==================== FIND BY ID TESTS ====================

	@Test
	@DisplayName("Find by ID should return data")
	void testFindById() {
		repository = new IndexedFileRepository(tempFile);
		repository.save(10, "test value");
		
		String result = repository.findById(10);
		assertEquals("test value", result);
	}

	@Test
	@DisplayName("Find by ID should return null for non-existent ID")
	void testFindByIdNotFound() {
		repository = new IndexedFileRepository(tempFile);
		
		String result = repository.findById(999);
		assertNull(result);
	}

	// ==================== DELETE BY ID TESTS ====================

	@Test
	@DisplayName("Delete by ID should remove data")
	void testDeleteById() {
		repository = new IndexedFileRepository(tempFile);
		repository.save(1, "to delete");
		
		boolean deleted = repository.deleteById(1);
		
		assertTrue(deleted);
		assertNull(repository.findById(1));
	}

	@Test
	@DisplayName("Delete by ID should return false for non-existent ID")
	void testDeleteByIdNotFound() {
		repository = new IndexedFileRepository(tempFile);
		
		boolean deleted = repository.deleteById(999);
		
		assertFalse(deleted);
	}

	@Test
	@DisplayName("Delete should not affect other items")
	void testDeleteDoesNotAffectOthers() {
		repository = new IndexedFileRepository(tempFile);
		repository.save(1, "first");
		repository.save(2, "second");
		repository.save(3, "third");
		
		repository.deleteById(2);
		
		assertEquals("first", repository.findById(1));
		assertNull(repository.findById(2));
		assertEquals("third", repository.findById(3));
	}

	// ==================== FIND ALL TESTS ====================

	@Test
	@DisplayName("Find all on empty should return empty list")
	void testFindAllEmpty() {
		repository = new IndexedFileRepository(tempFile);
		
		List<String> result = repository.findAll();
		
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	@DisplayName("Find all should return all from file")
	void testFindAllWithData() throws IOException {
		// Create file with data
		try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
			writer.write("line1");
			writer.newLine();
			writer.write("line2");
			writer.newLine();
			writer.write("line3");
			writer.newLine();
		}
		
		repository = new IndexedFileRepository(tempFile);
		
		List<String> result = repository.findAll();
		
		assertEquals(3, result.size());
		assertTrue(result.contains("line1"));
		assertTrue(result.contains("line2"));
		assertTrue(result.contains("line3"));
	}

	@Test
	@DisplayName("Find all should skip empty lines")
	void testFindAllSkipsEmptyLines() throws IOException {
		// Create file with empty lines
		try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
			writer.write("line1");
			writer.newLine();
			writer.write("   ");
			writer.newLine();
			writer.write("line2");
			writer.newLine();
			writer.write("");
			writer.newLine();
		}
		
		repository = new IndexedFileRepository(tempFile);
		
		List<String> result = repository.findAll();
		
		assertEquals(2, result.size());
	}

	// ==================== LOAD FROM FILE TESTS ====================

	@Test
	@DisplayName("Load from non-existent file should not throw")
	void testLoadFromNonExistentFile() {
		Path nonExistent = tempFile.getParent().resolve("non_existent_file_12345.dat");
		
		assertDoesNotThrow(() -> {
			new IndexedFileRepository(nonExistent);
		});
	}

	@Test
	@DisplayName("Load should skip empty lines in file")
	void testLoadSkipsEmptyLines() throws IOException {
		// Create file with empty lines
		try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
			writer.write("data1");
			writer.newLine();
			writer.write("");
			writer.newLine();
			writer.write("   ");
			writer.newLine();
			writer.write("data2");
			writer.newLine();
		}
		
		repository = new IndexedFileRepository(tempFile);
		
		// Should load 2 items (skipping empty lines)
		assertEquals("data1", repository.findById(1));
		assertEquals("data2", repository.findById(2));
	}

	// ==================== INTEGRATION TESTS ====================

	@Test
	@DisplayName("Save and find workflow should work")
	void testSaveAndFindWorkflow() {
		repository = new IndexedFileRepository(tempFile);
		
		// Save items
		repository.save(100, "item 100");
		repository.save(200, "item 200");
		repository.save(300, "item 300");
		
		// Find items
		assertEquals("item 100", repository.findById(100));
		assertEquals("item 200", repository.findById(200));
		assertEquals("item 300", repository.findById(300));
		
		// Delete one
		assertTrue(repository.deleteById(200));
		
		// Verify delete
		assertEquals("item 100", repository.findById(100));
		assertNull(repository.findById(200));
		assertEquals("item 300", repository.findById(300));
	}

	@Test
	@DisplayName("Save with same ID should add new entry")
	void testSaveSameId() {
		repository = new IndexedFileRepository(tempFile);
		
		repository.save(1, "first value");
		repository.save(1, "second value");
		
		// The latest save should be accessible
		// Note: This behavior depends on implementation
		assertNotNull(repository.findById(1));
	}
}



