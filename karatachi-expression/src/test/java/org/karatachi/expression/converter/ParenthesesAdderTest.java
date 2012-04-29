package org.karatachi.expression.converter;

import static org.junit.Assert.*;

import org.junit.Test;
import org.karatachi.expression.AbstractEnvironment;
import org.karatachi.expression.ExpressionParser;
import org.karatachi.expression.ast.Expression;

public class ParenthesesAdderTest {

    private static class BaseEnvironment extends AbstractEnvironment {
    };

    private static final BaseEnvironment env = new BaseEnvironment();

    @Test
    public void 不要な括弧はつけない() {
        Expression a = ExpressionParser.parse("2 * (1 + 1 * 2)");
        a = a.accept(new ParenthesesAdder());
        assertEquals("2 * (1 + 1 * 2)", a.represent(env));
    }

    @Test
    public void 一度展開して戻す() {
        Expression a = ExpressionParser.parse("2 * (1 + 1 * 2)");
        a = a.accept(new ExpressionExtractor(env));
        assertEquals("2 * 1 + 1 * 2", a.represent(env));
        a = a.accept(new ParenthesesAdder());
        assertEquals("2 * (1 + 1 * 2)", a.represent(env));
    }
}
