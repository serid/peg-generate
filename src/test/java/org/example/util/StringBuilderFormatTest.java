package org.example.util;

import org.example.util.exception.RedundantFormatArgumentException;
import org.junit.jupiter.api.Test;

import java.util.MissingFormatArgumentException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringBuilderFormatTest {
    @Test
    void format() {
        var sb = new StringBuilder("doge: ");
        StringBuilderFormat.format(sb, "age = %s, length = %s", "10", "20");
        assertEquals(sb.toString(), "doge: age = 10, length = 20");
    }

    @Test
    void fail_not_enough() {
        var sb = new StringBuilder("doge: ");
        var e = assertThrows(MissingFormatArgumentException.class,
                () -> StringBuilderFormat.format(sb, "age = %s, length = %s", "10"));
        assertEquals(e.getFormatSpecifier(), "%s");
    }

    @Test
    void fail_too_many() {
        var sb = new StringBuilder("doge: ");
        var e = assertThrows(RedundantFormatArgumentException.class,
                () -> StringBuilderFormat.format(sb, "age = %s, length = %s", "10", "20", "30"));
        assertEquals(e.getFormatArgument(), "30");
    }
}