package com.snacksack.snacksack.dp;

import com.snacksack.snacksack.dp.bottomUp.MemMatrix;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemMatrixTest {

    @Test
    public void testToString() {
        MemMatrix memMatrix = new MemMatrix(2, 3);
        String asStr = memMatrix.toString();
        assertThat(asStr, is(equalTo("[0, 0, 0]\n[0, 0, 0]")));
    }

    @Test
    public void testSetValues() {
        MemMatrix memMatrix = new MemMatrix(2, 3);
        memMatrix.setCell(0, 2, 5);
        memMatrix.setCell(1, 1, 9);
        String asStr = memMatrix.toString();
        assertThat(asStr, is(equalTo("[0, 0, 5]\n[0, 9, 0]")));
    }

    @Test
    public void testSetBadRow() {
        MemMatrix memMatrix = new MemMatrix(2, 3);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            memMatrix.setCell(2, 2, 1);
        });
        assertThat(exception.getMessage(), is(equalTo("m = 2 greater than max row index: 1")));
    }

    @Test
    public void testSetBadCol() {
        MemMatrix memMatrix = new MemMatrix(2, 3);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            memMatrix.setCell(1, 3, 1);
        });
        assertThat(exception.getMessage(), is(equalTo("n = 3 greater than max col index: 2")));
    }
}