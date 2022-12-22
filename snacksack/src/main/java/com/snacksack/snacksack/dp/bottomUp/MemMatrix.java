package com.snacksack.snacksack.dp.bottomUp;

import java.util.Arrays;
import java.util.List;

public class MemMatrix {
    private final int[][] matrix;
    public final int nRows;
    public final int nCols;

    public MemMatrix(int m, int n) {
        if (m <= 0 || n <= 0) {
            throw new IllegalArgumentException("Dimensions must be positive");
        }

        this.nRows = m;
        this.nCols = n;
        this.matrix = new int[m][n];
    }

    private void checkCellInMatrix(int m, int n) {
        if (m < 0) {
            throw new IllegalArgumentException("Row index m must be >= 0");
        }
        if (n < 0) {
            throw new IllegalArgumentException("Col index m must be >= 0");
        }
        if (m >= this.nRows) {
            throw new IllegalArgumentException(String.format("m = %s greater than max row index: %s", m, this.nRows - 1));
        }
        if (n >= this.nCols) {
            throw new IllegalArgumentException(String.format("n = %s greater than max col index: %s", n, this.nCols - 1));
        }
    }

    public void setCell(int m, int n, int value) {
        this.checkCellInMatrix(m, n);
        this.matrix[m][n] = value;
    }

    public void setCell(int m, long n, int value) throws ArithmeticException {
        /**
         * Handle the case where long-valued price provided.
         * Will throw exception on overflow
         */
        final int intN = Math.toIntExact(n);
        this.checkCellInMatrix(m, intN);
        this.matrix[m][intN] = value;
    }

    public int getCell(int m, int n) {
        this.checkCellInMatrix(m, n);
        return this.matrix[m][n];
    }

    public int getCell(int m, long n) throws ArithmeticException {
        /**
         * Handle the case where long-valued price provided.
         * Will throw exception on overflow
         */
        final int intN = Math.toIntExact(n);
        this.checkCellInMatrix(m, intN);
        return this.matrix[m][intN];
    }

    public int[][] getMatrix() {
        return this.matrix;
    }

    public int getAnswer() {
        return this.matrix[this.nRows - 1][this.nCols - 1];
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("");
        for (int[] row : this.matrix) {
            List<Integer> listedRow = Arrays.stream(row)
                    .boxed().toList();

            builder.append(listedRow).append("\n");
        }
        final String withLastNewLine = builder.toString();
        // remove last new line chars
        return withLastNewLine.substring(0, withLastNewLine.length() - 1);
    }
}
