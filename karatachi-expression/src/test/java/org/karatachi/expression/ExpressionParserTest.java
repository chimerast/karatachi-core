package org.karatachi.expression;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.karatachi.expression.ast.Expression;

public class ExpressionParserTest {

    private static class BaseEnvironment extends AbstractEnvironment {
        @Override
        public double getVariableValue(String name) {
            if (name.equals("a")) {
                return 2;
            } else {
                throw new UnknownIdentifierException();
            }
        }
    };

    private static final BaseEnvironment env = new BaseEnvironment();

    @Before
    public void before() {
        env.setRepresentationExpanded(false);
    }

    @Test
    public void 数値演算() {
        Expression a = ExpressionParser.parse("2 + (1 + 1 * 2)");
        assertEquals(5.0, a.value(env), 0.01);
    }

    @Test
    public void 関数演算() {
        Expression a = ExpressionParser.parse("sum(2+2, 3)");
        assertEquals(7.0, a.value(env), 0.01);
    }

    @Test
    public void 変数() {
        Expression a = ExpressionParser.parse("sum(a, 3)");
        assertEquals(5.0, a.value(env), 0.01);
    }

    @Test
    public void 関数の中に関数() {
        Expression a = ExpressionParser.parse("sum(sum(a, 8, 9), 3)");
        assertEquals(22.0, a.value(env), 0.01);
    }

    @Test
    public void 式の展開() {
        env.setRepresentationExpanded(true);

        Expression a = ExpressionParser.parse("sum(sum(a, 8, 9), 3)");
        assertEquals("((2 + 8 + 9) + 3)", a.represent(env));
    }

    @Test
    public void 単純な条件式() {
        env.setRepresentationExpanded(true);

        Expression a = ExpressionParser.parse("1 > 2 ? 3 + 2 : 4 + 5");
        assertEquals(9.0, a.value(env), 0.01);
        assertEquals("4 + 5", a.represent(env));

        Expression b = ExpressionParser.parse("(1 > 2 ? 3 + 2 : 4 + 5) + 5");
        assertEquals(14.0, b.value(env), 0.01);
        assertEquals("(4 + 5) + 5", b.represent(env));
    }

    @Test
    public void ブール演算子() {
        Expression a =
                ExpressionParser.parse("4.0 > 2.0 && 3.0 > 2.0 ? 1.0 : 0.0");
        assertEquals(1.0, a.value(env), 0.01);
        Expression b =
                ExpressionParser.parse("4.0 < 2.0 && 3.0 > 2.0 ? 1.0 : 0.0");
        assertEquals(0.0, b.value(env), 0.01);
        Expression c =
                ExpressionParser.parse("4.0 < 2.0 || 3.0 > 2.0 ? 1.0 : 0.0");
        assertEquals(1.0, c.value(env), 0.01);
    }

    @Test
    public void 変数の数() {
        Expression a = ExpressionParser.parse("a + b + c / d");
        Set<String> variables = env.getUsedVariables(a);
        assertEquals(4, variables.size());
    }

    @Test
    public void NaNの動作() {
        Expression a = ExpressionParser.parse("NaN");
        assertEquals(Double.NaN, a.value(env), 0.0);
    }

    @Test
    public void 関数の動作() {
        env.setRepresentationExpanded(true);

        Expression a = ExpressionParser.parse("ifnan(0.0 / 0.0, 1.0)");
        assertEquals(1.0, a.value(env), 0.01);
        assertEquals("(1)", a.represent(env));

        Expression b = ExpressionParser.parse("ifzero(0.0 / 10.0, 1.0)");
        assertEquals(1.0, b.value(env), 0.01);
        assertEquals("(1)", b.represent(env));

        Expression c = ExpressionParser.parse("max(NaN, 10.0, 20.0, 5.0)");
        assertEquals(20.0, c.value(env), 0.01);
        assertEquals("(20)", c.represent(env));
    }

    @Test
    public void 関数の定義() {
        Expression c = ExpressionParser.parse("_1 + _2");
        System.out.println(c.represent(env));
    }
}
