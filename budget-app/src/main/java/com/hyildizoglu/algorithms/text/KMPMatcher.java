package com.hyildizoglu.algorithms.text;

/**
 * Knuth-Morris-Pratt (KMP) string matching algorithm implementation.
 * Provides efficient O(n+m) time complexity for pattern matching in text.
 * 
 * @author Hayriye Nur Yıldızoğlu
 * @version 1.0
 */
public class KMPMatcher {

	/**
	 * Checks if the text contains the pattern.
	 * 
	 * @param text    The text to search in
	 * @param pattern The pattern to search for
	 * @return true if pattern is found in text, false otherwise
	 */
	public static boolean contains(String text, String pattern) {
		return indexOf(text, pattern) != -1;
	}

	/**
	 * Finds the first occurrence of pattern in text.
	 * 
	 * @param text    The text to search in
	 * @param pattern The pattern to search for
	 * @return Index of first occurrence, or -1 if not found
	 */
	public static int indexOf(String text, String pattern) {
		if (pattern == null || pattern.isEmpty()) {
			return 0;
		}
		if (text == null || text.length() < pattern.length()) {
			return -1;
		}
		int[] lps = buildLps(pattern);
		int i = 0; // text index
		int j = 0; // pattern index
		while (i < text.length()) {
			if (text.charAt(i) == pattern.charAt(j)) {
				i++;
				j++;
				if (j == pattern.length()) {
					return i - j;
				}
			} else if (j > 0) {
				j = lps[j - 1];
			} else {
				i++;
			}
		}
		return -1;
	}

	/**
	 * Builds the Longest Proper Prefix which is also Suffix (LPS) array.
	 * This array is used to skip characters in the pattern after a mismatch.
	 * 
	 * @param pattern The pattern to build LPS for
	 * @return The LPS array
	 */
	private static int[] buildLps(String pattern) {
		int[] lps = new int[pattern.length()];
		int len = 0;
		for (int i = 1; i < pattern.length();) {
			if (pattern.charAt(i) == pattern.charAt(len)) {
				lps[i++] = ++len;
			} else if (len > 0) {
				len = lps[len - 1];
			} else {
				lps[i++] = 0;
			}
		}
		return lps;
	}
}
