package com.hyildizoglu.budget;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyildizoglu.algorithms.matrix.SparseMatrix;

@DisplayName("SparseMatrix Algorithm Tests")
class SparseMatrixTest {

	private SparseMatrix matrix;

	@BeforeEach
	void setUp() {
		matrix = new SparseMatrix();
	}

	@Test
	@DisplayName("addTo operation")
	void testAddTo() {
		matrix.addTo(1, 1, new BigDecimal("100"));
		matrix.addTo(1, 2, new BigDecimal("200"));

		assertEquals(new BigDecimal("100"), matrix.get(1, 1));
		assertEquals(new BigDecimal("200"), matrix.get(1, 2));
	}

	@Test
	@DisplayName("Should return zero for empty cell")
	void testGet_ZeroForEmpty() {
		assertEquals(BigDecimal.ZERO, matrix.get(1, 1));
	}

	@Test
	@DisplayName("addTo accumulation")
	void testAddTo_Accumulate() {
		matrix.addTo(1, 1, new BigDecimal("100"));
		matrix.addTo(1, 1, new BigDecimal("50"));

		assertEquals(new BigDecimal("150"), matrix.get(1, 1));
	}

	@Test
	@DisplayName("Zero value removal")
	void testAddTo_ZeroRemoval() {
		matrix.addTo(1, 1, new BigDecimal("100"));
		matrix.addTo(1, 1, new BigDecimal("-100"));

		assertEquals(BigDecimal.ZERO, matrix.get(1, 1));
	}

	@Test
	@DisplayName("Row sum")
	void testRowSum() {
		matrix.addTo(1, 1, new BigDecimal("100"));
		matrix.addTo(1, 2, new BigDecimal("200"));
		matrix.addTo(1, 3, new BigDecimal("300"));

		assertEquals(new BigDecimal("600"), matrix.rowSum(1));
	}

	@Test
	@DisplayName("Empty row sum")
	void testRowSum_EmptyRow() {
		assertEquals(BigDecimal.ZERO, matrix.rowSum(1));
	}

	@Test
	@DisplayName("Column sum")
	void testColumnSum() {
		matrix.addTo(1, 1, new BigDecimal("100"));
		matrix.addTo(2, 1, new BigDecimal("200"));
		matrix.addTo(3, 1, new BigDecimal("300"));

		assertEquals(new BigDecimal("600"), matrix.columnSum(1));
	}

	@Test
	@DisplayName("Empty column sum")
	void testColumnSum_EmptyColumn() {
		assertEquals(BigDecimal.ZERO, matrix.columnSum(1));
	}
}
