package org.example.impl.peg.arith.assets;

public final class TestPlayground {
    public static int test(String code) {
        var tp = TokenProvider.from(code);
        var ps = new Program.Parser(tp, 0);
        var ex = ps.parseExpr(0);
        assert ex != null;
        return denote(ex);
    }

    private static int denoteValue(Program.Value expr) {
        if (expr.tag == Program.Value.ValueKind.KIND1)
            return expr.field_1.data;
        else if (expr.tag == Program.Value.ValueKind.KIND2)
            return denote(expr.field_2);
        else
            throw new RuntimeException();
    }

    private static int denoteProduct(Program.Product expr) {
        var n = denoteValue(expr.field_1);

        for (var t : expr.field_2) {
            if (t.field_1.tag == Program.ProductC.ProductCKind.KIND1)
                n *= denoteValue(t.field_2);
            else if (t.field_1.tag == Program.ProductC.ProductCKind.KIND2)
                n /= denoteValue(t.field_2);
            else
                throw new RuntimeException();
        }

        return n;
    }

    private static int denoteSum(Program.Sum expr) {
        var n = denoteProduct(expr.field_1);

        for (var t : expr.field_2) {
            if (t.field_1.tag == Program.SumC.SumCKind.KIND1)
                n += denoteProduct(t.field_2);
            else if (t.field_1.tag == Program.SumC.SumCKind.KIND2)
                n -= denoteProduct(t.field_2);
            else
                throw new RuntimeException();
        }

        return n;
    }

    private static int denote(Program.Expr expr) {
        return denoteSum(expr.field_0);
    }
}
