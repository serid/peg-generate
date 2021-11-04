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
        if (expr.tag == Program.Value.ValueKind.KIND_1)
            return expr.kind_1.data;
        else if (expr.tag == Program.Value.ValueKind.KIND_2)
            return denote(expr.kind_2);
        else
            throw new RuntimeException();
    }

    private static int denoteProduct(Program.Product expr) {
        var n = denoteValue(expr.field_1);

        for (var t : expr.field_2) {
            if (t.field_1.tag == Program.ProductC.ProductCKind.KIND_1)
                n *= denoteValue(t.field_2);
            else if (t.field_1.tag == Program.ProductC.ProductCKind.KIND_2)
                n /= denoteValue(t.field_2);
            else
                throw new RuntimeException();
        }

        return n;
    }

    private static int denoteSum(Program.Sum expr) {
        var n = denoteProduct(expr.field_1);

        for (var t : expr.field_2) {
            if (t.field_1.tag == Program.SumC.SumCKind.KIND_1)
                n += denoteProduct(t.field_2);
            else if (t.field_1.tag == Program.SumC.SumCKind.KIND_2)
                n -= denoteProduct(t.field_2);
            else
                throw new RuntimeException();
        }

        return n;
    }

    private static int denote(Program.Expr expr) {
        return denoteSum(expr.field_1);
    }
}
