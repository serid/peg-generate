package org.example.util;

import org.example.util.exception.RedundantFormatArgumentException;

import java.util.MissingFormatArgumentException;

public final class StringBuilderFormat {
    private StringBuilderFormat() {
    }

    public static void format(StringBuilder out, String format_string, String... args) {
        // Resulting buffer capacity will be increased by format_string.length + sum(args.map(it -> it.length))
        int length = format_string.length();
        for (var s : args) {
            length += s.length();
        }
        out.ensureCapacity(out.length() + length);

        // Index of an argument in "args" that will be put in place of %s
        int arg_to_replace = 0;

        for (int i = 0; i < format_string.length(); ) {
            char c = format_string.charAt(i++);

            if (c != '%') {
                out.append(c);
                continue;
            }

            c = format_string.charAt(i++);

            if (c == 's') {
                if (arg_to_replace == args.length)
                    throw new MissingFormatArgumentException("%s");

                out.append(args[arg_to_replace++]);
            } else {
                throw new IllegalArgumentException("unrecognized format specifier");
            }
        }

        if (arg_to_replace < args.length)
            throw new RedundantFormatArgumentException(args[arg_to_replace]);
    }
}
