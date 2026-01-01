package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.text.KMPMatcher;

@DisplayName("KMPMatcher Algorithm Tests")
class KMPMatcherTest {

	@Test
	@DisplayName("Pattern found")
	void testContains_PatternFound() {
		assertTrue(KMPMatcher.contains("hello world", "world"));
		assertTrue(KMPMatcher.contains("grocery shopping", "grocery"));
	}

	@Test
	@DisplayName("Pattern not found")
	void testContains_PatternNotFound() {
		assertFalse(KMPMatcher.contains("hello world", "java"));
		assertFalse(KMPMatcher.contains("test", "nonexistent"));
	}

	@Test
	@DisplayName("Empty pattern")
	void testContains_EmptyPattern() {
		assertTrue(KMPMatcher.contains("hello world", ""));
		assertTrue(KMPMatcher.contains("", ""));
	}

	@Test
	@DisplayName("Empty text")
	void testContains_EmptyText() {
		assertFalse(KMPMatcher.contains("", "pattern"));
	}

	@Test
	@DisplayName("Pattern equals text")
	void testContains_PatternEqualsText() {
		assertTrue(KMPMatcher.contains("hello", "hello"));
	}

	@Test
	@DisplayName("Pattern longer than text")
	void testContains_PatternLongerThanText() {
		assertFalse(KMPMatcher.contains("short", "longerPattern"));
	}

	@Test
	@DisplayName("indexOf - pattern found")
	void testIndexOf_PatternFound() {
		assertEquals(6, KMPMatcher.indexOf("hello world", "world"));
		assertEquals(0, KMPMatcher.indexOf("hello world", "hello"));
	}

	@Test
	@DisplayName("indexOf - pattern not found")
	void testIndexOf_PatternNotFound() {
		assertEquals(-1, KMPMatcher.indexOf("hello world", "java"));
	}

	@Test
	@DisplayName("indexOf - empty pattern")
	void testIndexOf_EmptyPattern() {
		assertEquals(0, KMPMatcher.indexOf("hello", ""));
	}

	@Test
	@DisplayName("Partial match")
	void testContains_PartialMatch() {
		assertTrue(KMPMatcher.contains("abcxabcdabcdabcy", "abcdabcy"));
	}
}
