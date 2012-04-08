package org.karatachi.expression;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;
import org.karatachi.expression.ast.Expression;
import org.karatachi.expression.function.Sum;

public class ExpressionParserTest {

    private static class BaseEnvironment extends AbstractEnvironment {
        {
            addFunction(new Sum());
        }

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
        env.setRepresentationExpanded(false);
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
        env.setRepresentationExpanded(false);
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
}
