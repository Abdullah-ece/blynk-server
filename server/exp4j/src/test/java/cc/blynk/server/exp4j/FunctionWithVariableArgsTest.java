package cc.blynk.server.exp4j;

import cc.blynk.server.exp4j.function.DynamicArgumentFunction;
import cc.blynk.server.exp4j.function.Function;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FunctionWithVariableArgsTest {

    private final List<Function> variableArgsFunctions = new LinkedList<>();

    @Before
    public void fillWithSomeFunctions(){
        Function avg = new DynamicArgumentFunction("avg", 1, 100) {

            @Override
            public double apply(double... args) {
                double sum = 0;
                for (double arg : args) {
                    sum += arg;
                }
                return sum / args.length;
            }
        };

        Function sum = new DynamicArgumentFunction("sum", 1, 100) {

            @Override
            public double apply(double... args) {
                double sum = 0;
                for (double arg : args) {
                    sum += arg;
                }
                return sum;
            }
        };

        Function max = new DynamicArgumentFunction("max", 1, 100) {

            @Override
            public double apply(double... args) {
                double max = args[0];
                for (double arg : args) {
                    if (arg > max)
                        max = arg;
                }
                return max;
            }
        };

        Function min = new DynamicArgumentFunction("min", 1,100) {

            @Override
            public double apply(double... args) {
                double min = args[0];
                for (double arg : args) {
                    if (arg < min)
                        min = arg;
                }
                return min;
            }
        };

        Function atLeast4atMax8 = new DynamicArgumentFunction("atLeast4atMax8", 4, 8) {
            @Override
            public double apply(double... args) {
                return 0;
            }
        };

        variableArgsFunctions.add(min);
        variableArgsFunctions.add(max);
        variableArgsFunctions.add(avg);
        variableArgsFunctions.add(sum);
        variableArgsFunctions.add(atLeast4atMax8);
    }

    @Test
    public void testFunctionsWithVariableArgs1() {
        double result,expected;
        result = new ExpressionBuilder("avg(1,2,3,4)")
                .functions(variableArgsFunctions)
                .build()
                .evaluate();

        expected = 2.5d;
        assertEquals(expected, result, 0d);

        result = new ExpressionBuilder("avg(1,1,1,1,1,1,1,1,1)")
                .functions(variableArgsFunctions)
                .build()
                .evaluate();

        expected = 1.0d;
        assertEquals(expected, result, 0d);

    }

    @Test
    public void testFunctionsWithVariableArgs2() {
        double result,expected;
        result = new ExpressionBuilder("min(1,1,1,1,1,1,1,1)")
                .functions(variableArgsFunctions)
                .build()
                .evaluate();

        expected = 1.0d;
        assertEquals(expected, result, 0d);

        result = new ExpressionBuilder("min(1,1,1,1,1,1,1)")
                .functions(variableArgsFunctions)
                .build()
                .evaluate();

        expected = 1.0d;
        assertEquals(expected, result, 0d);

        result = new ExpressionBuilder("min(1,1,1,1)")
                .functions(variableArgsFunctions)
                .build()
                .evaluate();

        expected = 1.0d;
        assertEquals(expected, result, 0d);

        result = new ExpressionBuilder("max(1,1,1,1,1,1,1,1)")
                .functions(variableArgsFunctions)
                .build()
                .evaluate();

        expected = 1.0d;
        assertEquals(expected, result, 0d);

        result = new ExpressionBuilder("max(1,1,1,1,1,1)")
                .functions(variableArgsFunctions)
                .build()
                .evaluate();

        expected = 1.0d;
        assertEquals(expected, result, 0d);

    }

    @Test
    public void testFunctionsWithVariableArgs3(){
        Expression e = new ExpressionBuilder("sum(1,avg(1.11,-3.14,2.03))")
                .functions(variableArgsFunctions)
                .build();
        assertEquals(1.0d, e.evaluate(), 1.0d);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFunctionsWithVariableArgs4(){
        new ExpressionBuilder("5 + atLeast4atMax8(1)")
                .functions(variableArgsFunctions)
                .build()
                .evaluate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFunctionsWithVariableArgs5(){
        new ExpressionBuilder("5 + atLeast4atMax8(2,3)")
                .functions(variableArgsFunctions)
                .build()
                .evaluate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFunctionsWithVariableArgs6(){
        new ExpressionBuilder("5 + atLeast4atMax8(2,3,3)")
                .functions(variableArgsFunctions)
                .build()
                .evaluate();
    }

    @Test
    public void testFunctionsWithVariableArgs7(){
        double result = new ExpressionBuilder("5 + atLeast4atMax8(2,3,3,2,4,max(1,2,3))")
                .functions(variableArgsFunctions)
                .build()
                .evaluate();
        assertEquals(5, result, 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFunctionsWithVariableArgs8(){
        new ExpressionBuilder("5 + atLeast4atMax8(1,2,3,4,5,6,7,8,9,10)")
                .functions(variableArgsFunctions)
                .build()
                .evaluate();
    }

}
