package org.example.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class UtilTest {
    @Test
    void concat() {
        assertArrayEquals(Util.concat(new String[]{"a", "b"}, new String[]{"c"}), new String[]{"a", "b", "c"});
    }
}