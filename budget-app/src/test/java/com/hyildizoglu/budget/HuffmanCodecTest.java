package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.compression.HuffmanCodec;

@DisplayName("HuffmanCodec Algorithm Tests")
class HuffmanCodecTest {

	private HuffmanCodec codec = new HuffmanCodec();

	@Test
	@DisplayName("Simple string compression")
	void testCompress_SimpleString() {
		String input = "hello";
		byte[] compressed = codec.compress(input);

		assertNotNull(compressed);
		assertTrue(compressed.length > 0);
	}

	@Test
	@DisplayName("Simple string decompression")
	void testDecompress_SimpleString() {
		String input = "hello";
		byte[] compressed = codec.compress(input);
		String decompressed = codec.decompress(compressed);

		assertEquals(input, decompressed);
	}

	@Test
	@DisplayName("Compress-decompress round trip")
	void testCompressDecompress_RoundTrip() {
		String input = "test string for compression";
		byte[] compressed = codec.compress(input);
		String decompressed = codec.decompress(compressed);

		assertEquals(input, decompressed);
	}

	@Test
	@DisplayName("Empty string compression")
	void testCompress_EmptyString() {
		byte[] compressed = codec.compress("");
		assertNotNull(compressed);
	}

	@Test
	@DisplayName("Empty string decompression")
	void testDecompress_EmptyString() {
		byte[] compressed = codec.compress("");
		String decompressed = codec.decompress(compressed);
		assertEquals("", decompressed);
	}

	@Test
	@DisplayName("Null string compression")
	void testCompress_NullString() {
		byte[] compressed = codec.compress(null);
		assertNotNull(compressed);
		assertEquals(0, compressed.length);
	}

	@Test
	@DisplayName("Null data decompression")
	void testDecompress_NullData() {
		String decompressed = codec.decompress(null);
		assertEquals("", decompressed);
	}

	@Test
	@DisplayName("Repeated characters")
	void testCompressDecompress_RepeatedCharacters() {
		String input = "aaaa";
		byte[] compressed = codec.compress(input);
		String decompressed = codec.decompress(compressed);

		assertEquals(input, decompressed);
	}

	@Test
	@DisplayName("Special characters")
	void testCompressDecompress_SpecialCharacters() {
		String input = "test@123#$%";
		byte[] compressed = codec.compress(input);
		String decompressed = codec.decompress(compressed);

		assertEquals(input, decompressed);
	}
}
