package cc.blynk.server.exp4j.function;

import cc.blynk.server.exp4j.exceptions.VariableNotSetException;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing the builtin functions available for use in expressions
 */
public enum Functions {

    SIN(new OneArgumentFunction("sin") {
        @Override
        public double apply(double args) {
            return Math.sin(args);
        }
    }),
    COS(new OneArgumentFunction("cos") {
        @Override
        public double apply(double arg) {
            return Math.cos(arg);
        }
    }),
    TAN(new OneArgumentFunction("tan") {
        @Override
        public double apply(double arg) {
            return Math.tan(arg);
        }
    }),
    COT(new OneArgumentFunction("cot") {
        @Override
        public double apply(double arg) {
            double tan = Math.tan(arg);
            if (tan == 0d) {
                throw new ArithmeticException("Division by zero in cotangent!");
            }
            return 1d / Math.tan(arg);
        }
    }),
    LOG(new OneArgumentFunction("log") {
        @Override
        public double apply(double arg) {
            return Math.log(arg);
        }
    }),
    LOG1P(new OneArgumentFunction("log1p") {
        @Override
        public double apply(double arg) {
            return Math.log1p(arg);
        }
    }),
    ABS(new OneArgumentFunction("abs") {
        @Override
        public double apply(double arg) {
            return Math.abs(arg);
        }
    }),
    ACOS(new OneArgumentFunction("acos") {
        @Override
        public double apply(double arg) {
            return Math.acos(arg);
        }
    }),
    ASIN(new OneArgumentFunction("asin") {
        @Override
        public double apply(double arg) {
            return Math.asin(arg);
        }
    }),
    ATAN(new OneArgumentFunction("atan") {
        @Override
        public double apply(double arg) {
            return Math.atan(arg);
        }
    }),
    CBRT(new OneArgumentFunction("cbrt") {
        @Override
        public double apply(double arg) {
            return Math.cbrt(arg);
        }
    }),
    CEIL(new OneArgumentFunction("ceil") {
        @Override
        public double apply(double arg) {
            return Math.ceil(arg);
        }
    }),
    FLOOR(new OneArgumentFunction("floor") {
        @Override
        public double apply(double arg) {
            return Math.floor(arg);
        }
    }),
    SINH(new OneArgumentFunction("sinh") {
        @Override
        public double apply(double arg) {
            return Math.sinh(arg);
        }
    }),
    SQRT(new OneArgumentFunction("sqrt") {
        @Override
        public double apply(double arg) {
            return Math.sqrt(arg);
        }
    }),
    TANH(new OneArgumentFunction("tanh") {
        @Override
        public double apply(double arg) {
            return Math.tanh(arg);
        }
    }),
    COSH(new OneArgumentFunction("cosh") {
        @Override
        public double apply(double arg) {
            return Math.cosh(arg);
        }
    }),
    POW(new TwoArgumentFunction("pow") {
        @Override
        public double apply(double arg1, double arg2) {
            return Math.pow(arg1, arg2);
        }
    }),
    EXP(new OneArgumentFunction("exp") {
        @Override
        public double apply(double arg) {
            return Math.exp(arg);
        }
    }),
    EXPM1(new OneArgumentFunction("expm1") {
        @Override
        public double apply(double arg) {
            return Math.expm1(arg);
        }
    }),
    LOG10(new OneArgumentFunction("log10") {
        @Override
        public double apply(double arg) {
            return Math.log10(arg);
        }
    }),
    LOG2(new OneArgumentFunction("log2") {
        @Override
        public double apply(double arg) {
            return Math.log(arg) / Math.log(2d);
        }
    }),
    SIGNUM(new OneArgumentFunction("signum") {
        @Override
        public double apply(double arg) {
            if (arg > 0) {
                return 1;
            } else if (arg < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }),
    AVG(new DynamicArgumentFunction("avg", 1, 100) {
        @Override
        public double apply(double... args) {
            if (args.length == 0) {
                throw new VariableNotSetException("Function 'avg' has no arguments.");
            }
            double sum = 0;
            for (double arg : args) {
                sum += arg;
            }
            return sum / args.length;
        }
    }),
    AVG_FOR_REFERENCES(new DynamicArgumentFunction("avgForReferences", 1, Integer.MAX_VALUE, true) {
        @Override
        public double apply(double... args) {
            if (args.length == 0) {
                throw new VariableNotSetException("Function 'avgForReferences' has no arguments.");
            }
            double sum = 0;
            for (double arg : args) {
                sum += arg;
            }
            return sum / args.length;
        }
    });

    public static final Map<String, Function> ALL = init();
    public final Function function;

    Functions(Function function) {
        this.function = function;
    }

    private static Map<String, Function> init() {
        Map<String, Function> result = new HashMap<>();
        for (Functions functions : Functions.values()) {
            Function function = functions.function;
            result.put(function.name, function);
        }
        return result;
    }

    public static boolean isBuiltinFunction(String name) {
        return ALL.get(name) != null;
    }
}
