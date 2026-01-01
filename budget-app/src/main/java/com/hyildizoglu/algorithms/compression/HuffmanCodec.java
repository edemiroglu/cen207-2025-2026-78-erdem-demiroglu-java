package com.hyildizoglu.algorithms.compression;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Huffman coding implementation for text compression and decompression.
 * Uses a binary tree to create variable-length prefix codes based on character frequency.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class HuffmanCodec {

	/**
	 * Internal node class for building the Huffman tree.
	 */
	private static class Node implements Comparable<Node> {
		/** The character stored in this node (only for leaf nodes). */
		final char ch;
		
		/** Frequency of the character or sum of child frequencies. */
		final int freq;
		
		/** Left child node (represents bit 0). */
		final Node left;
		
		/** Right child node (represents bit 1). */
		final Node right;

		/**
		 * Creates a new Node.
		 * 
		 * @param ch    The character (or null character for internal nodes)
		 * @param freq  The frequency
		 * @param left  Left child
		 * @param right Right child
		 */
		Node(char ch, int freq, Node left, Node right) {
			this.ch = ch;
			this.freq = freq;
			this.left = left;
			this.right = right;
		}

		/**
		 * Checks if this node is a leaf (contains a character).
		 * 
		 * @return true if this is a leaf node
		 */
		boolean isLeaf() {
			return left == null && right == null;
		}

		/**
		 * Compares nodes by frequency for priority queue ordering.
		 * 
		 * @param o The other node
		 * @return Comparison result based on frequency
		 */
		@Override
		public int compareTo(Node o) {
			return Integer.compare(this.freq, o.freq);
		}
	}

	/**
	 * Compresses a string using Huffman coding.
	 * 
	 * @param input The string to compress
	 * @return Compressed data as byte array (includes header for decompression)
	 */
	public byte[] compress(String input) {
		if (input == null || input.isEmpty()) {
			return new byte[0];
		}
		Map<Character, Integer> freq = new HashMap<>();
		for (char c : input.toCharArray()) {
			freq.merge(c, 1, Integer::sum);
		}
		Node root = buildTree(freq);
		Map<Character, String> codes = new HashMap<>();
		buildCodes(root, "", codes);
		StringBuilder bits = new StringBuilder();
		for (char c : input.toCharArray()) {
			bits.append(codes.get(c));
		}
		// Combine frequency table and bit string with separator
		StringBuilder header = new StringBuilder();
		for (Map.Entry<Character, Integer> e : freq.entrySet()) {
			header.append((int) e.getKey()).append(':').append(e.getValue()).append(',');
		}
		header.append('|');
		String combined = header + bits.toString();
		return combined.getBytes(java.nio.charset.StandardCharsets.UTF_8);
	}

	/**
	 * Decompresses data that was compressed with this codec.
	 * 
	 * @param data The compressed byte array
	 * @return The original decompressed string
	 */
	public String decompress(byte[] data) {
		if (data == null || data.length == 0) {
			return "";
		}
		String combined = new String(data, java.nio.charset.StandardCharsets.UTF_8);
		int sep = combined.indexOf('|');
		if (sep < 0) {
			return combined;
		}
		String header = combined.substring(0, sep);
		String bitString = combined.substring(sep + 1);
		Map<Character, Integer> freq = new HashMap<>();
		if (!header.isEmpty()) {
			String[] parts = header.split(",");
			for (String p : parts) {
				if (p.isEmpty()) {
					continue;
				}
				String[] kv = p.split(":");
				char ch = (char) Integer.parseInt(kv[0]);
				int f = Integer.parseInt(kv[1]);
				freq.put(ch, f);
			}
		}
		Node root = buildTree(freq);
		StringBuilder out = new StringBuilder();
		Node current = root;
		for (int i = 0; i < bitString.length(); i++) {
			char b = bitString.charAt(i);
			current = (b == '0') ? current.left : current.right;
			if (current.isLeaf()) {
				out.append(current.ch);
				current = root;
			}
		}
		return out.toString();
	}

	/**
	 * Builds a Huffman tree from character frequencies.
	 * 
	 * @param freq Map of character to frequency
	 * @return The root of the Huffman tree
	 */
	private Node buildTree(Map<Character, Integer> freq) {
		PriorityQueue<Node> pq = new PriorityQueue<>();
		for (Map.Entry<Character, Integer> e : freq.entrySet()) {
			pq.add(new Node(e.getKey(), e.getValue(), null, null));
		}
		if (pq.size() == 1) {
			// Handle single character case
			Node only = pq.poll();
			return new Node('\0', only.freq, only, null);
		}
		while (pq.size() > 1) {
			Node a = pq.poll();
			Node b = pq.poll();
			pq.add(new Node('\0', a.freq + b.freq, a, b));
		}
		return pq.poll();
	}

	/**
	 * Recursively builds the code map from the Huffman tree.
	 * 
	 * @param node  Current node in traversal
	 * @param code  Current code string (path from root)
	 * @param codes Map to store character to code mappings
	 */
	private void buildCodes(Node node, String code, Map<Character, String> codes) {
		if (node.isLeaf()) {
			codes.put(node.ch, code.isEmpty() ? "0" : code);
			return;
		}
		buildCodes(node.left, code + "0", codes);
		if (node.right != null) {
			buildCodes(node.right, code + "1", codes);
		}
	}
}
