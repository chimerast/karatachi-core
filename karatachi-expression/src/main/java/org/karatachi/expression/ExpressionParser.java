package org.karatachi.expression;

import java.util.List;

import org.codehaus.jparsec.OperatorTable;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.Terminals;
import org.codehaus.jparsec.Tokens;
import org.codehaus.jparsec.Tokens.Fragment;
import org.codehaus.jparsec.Tokens.Tag;
import org.codehaus.jparsec.functors.Binary;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Map4;
import org.codehaus.jparsec.functors.Unary;
import org.codehaus.jparsec.misc.Mapper;
import org.karatachi.expression.ast.Bool.And;
import org.karatachi.expression.ast.Bool.Or;
import org.karatachi.expression.ast.Calculator.Add;
import org.karatachi.expression.ast.Calculator.Divide;
import org.karatachi.expression.ast.Calculator.Multiply;
import org.karatachi.expression.ast.Calculator.Negative;
import org.karatachi.expression.ast.Calculator.Parentheses;
import org.karatachi.expression.ast.Calculator.Subtract;
import org.karatachi.expression.ast.Comparator.Equal;
import org.karatachi.expression.ast.Comparator.GreaterEqual;
import org.karatachi.expression.ast.Comparator.GreaterThan;
import org.karatachi.expression.ast.Comparator.LessEqual;
import org.karatachi.expression.ast.Comparator.LessThan;
import org.karatachi.expression.ast.Comparator.Not;
import org.karatachi.expression.ast.Comparator.NotEqual;
import org.karatachi.expression.ast.ConditionalExpression;
import org.karatachi.expression.ast.Expression;
import org.karatachi.expression.ast.Function;
import org.karatachi.expression.ast.Identifier;
import org.karatachi.expression.ast.Literal;
import org.karatachi.expression.ast.Variable;

public class ExpressionParser {

    private enum BinaryOperator implements Binary<Expression> {
        PLUS {
            public Expression map(Expression left, Expression right) {
                return new Add(left, right);
            }
        },
        MINUS {
            public Expression map(Expression left, Expression right) {
                return new Subtract(left, right);
            }
        },
        MUL {
            public Expression map(Expression left, Expression right) {
                return new Multiply(left, right);
            }
        },
        DIV {
            public Expression map(Expression left, Expression right) {
                return new Divide(left, right);
            }
        },
        GT {
            public Expression map(Expression left, Expression right) {
                return new GreaterThan(left, right);
            }
        },
        LT {
            public Expression map(Expression left, Expression right) {
                return new LessThan(left, right);
            }
        },
        GE {
            public Expression map(Expression left, Expression right) {
                return new GreaterEqual(left, right);
            }
        },
        LE {
            public Expression map(Expression left, Expression right) {
                return new LessEqual(left, right);
            }
        },
        EQ {
            public Expression map(Expression left, Expression right) {
                return new Equal(left, right);
            }
        },
        NE {
            public Expression map(Expression left, Expression right) {
                return new NotEqual(left, right);
            }
        },
        AND {
            public Expression map(Expression left, Expression right) {
                return new And(left, right);
            }
        },
        OR {
            public Expression map(Expression left, Expression right) {
                return new Or(left, right);
            }
        }
    }

    private enum UnaryOperator implements Unary<Expression> {
        NEG {
            public Expression map(Expression expression) {
                return new Negative(expression);
            }
        },
        NOT {
            public Expression map(Expression expression) {
                return new Not(expression);
            }
        }
    }

    private static final Parser<Fragment> NAN_TOKENIZER =
            Scanners.string("NaN").source().map(new Map<String, Fragment>() {
                public Fragment map(String text) {
                    return Tokens.fragment(text, Tag.DECIMAL);
                }
            });

    private static final Parser<Expression> LITERAL =
            Terminals.DecimalLiteral.PARSER.map(new Map<String, Expression>() {
                public Literal map(String s) {
                    return new Literal(Double.valueOf(s));
                }
            });

    private static final Parser<Identifier> IDENTIFIER =
            Terminals.Identifier.PARSER.map(new Map<String, Identifier>() {
                public Identifier map(String s) {
                    return new Identifier(s);
                }
            });

    private static final Terminals OPERATORS = Terminals.operators("+", "-",
            "*", "/", "(", ")", ">", "<", ">=", "<=", "==", "!=", "&&", "||",
            "!", "?", ":", ",");

    private static final Parser<?> TOKENIZER = Parsers.or(
            Terminals.DecimalLiteral.TOKENIZER, NAN_TOKENIZER,
            Terminals.Identifier.TOKENIZER, OPERATORS.tokenizer());

    private static final Parser<Void> IGNORED = Scanners.JAVA_DELIMITER;

    private static Parser<?> term(String... names) {
        return OPERATORS.token(names);
    }

    private static <T> Parser<T> op(String name, T value) {
        return term(name).retn(value);
    }

    private static Parser.Reference<Expression> CALCULATOR_REF =
            Parser.newReference();

    private static Parser<Expression> VARIABLE =
            IDENTIFIER.map(new Map<Identifier, Expression>() {
                @Override
                public Expression map(Identifier from) {
                    return new Variable(from);
                }
            });

    private static final Parser<Expression> FUNCTION =
            Parsers.sequence(
                    IDENTIFIER,
                    term("("),
                    CALCULATOR_REF.lazy().sepBy(term(",")),
                    term(")"),
                    new Map4<Identifier, Object, List<Expression>, Object, Expression>() {
                        public Expression map(Identifier identifier,
                                Object lparen, List<Expression> args,
                                Object rparen) {
                            return new Function(identifier, args);
                        }
                    });

    static Parser<Binary<Expression>> conditionalOperator(
            Parser<Expression> consequence) {
        return Mapper.<Expression> curry(ConditionalExpression.class).infix(
                consequence.between(term("?"), term(":")));
    }

    private static Parser<Expression> expression() {
        Parser<Expression> atom = Parsers.or(FUNCTION, VARIABLE, LITERAL);

        Parser<Expression> parenetheses =
                CALCULATOR_REF.lazy().between(term("("), term(")")).map(
                        new Unary<Expression>() {
                            @Override
                            public Expression map(Expression from) {
                                return new Parentheses(from);
                            }
                        });

        Parser<Expression> unit = parenetheses.or(atom);

        Parser<Expression> parser = new OperatorTable<Expression>() //
        .prefix(op("!", UnaryOperator.NOT), 80) //
        .prefix(op("-", UnaryOperator.NEG), 80) //
        .infixl(op("*", BinaryOperator.MUL), 70) //
        .infixl(op("/", BinaryOperator.DIV), 70) //
        .infixl(op("+", BinaryOperator.PLUS), 60) //
        .infixl(op("-", BinaryOperator.MINUS), 60) //
        .infixl(op(">", BinaryOperator.GT), 50) //
        .infixl(op("<", BinaryOperator.LT), 50) //
        .infixl(op(">=", BinaryOperator.GE), 50) //
        .infixl(op("<=", BinaryOperator.LE), 50) //
        .infixl(op("==", BinaryOperator.EQ), 40) //
        .infixl(op("!=", BinaryOperator.NE), 40) //
        .infixl(op("&&", BinaryOperator.AND), 30) //
        .infixl(op("||", BinaryOperator.OR), 20) //
        .infixr(conditionalOperator(CALCULATOR_REF.lazy()), 10) //
        .build(unit);

        CALCULATOR_REF.set(parser);
        return parser;
    }

    private static final Parser<Expression> EXPRESSION = expression();

    private static final Parser<Expression> PARSER = EXPRESSION.from(TOKENIZER,
            IGNORED);

    public static Expression parse(String str) {
        return PARSER.parse(str);
    }
}
