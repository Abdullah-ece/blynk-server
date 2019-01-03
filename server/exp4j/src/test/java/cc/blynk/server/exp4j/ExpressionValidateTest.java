/*
 * Copyright 2014 Bartosz Firyn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.blynk.server.exp4j;

import cc.blynk.server.exp4j.exceptions.ParseExpressionException;
import cc.blynk.server.exp4j.function.DynamicArgumentFunction;
import cc.blynk.server.exp4j.function.Function;
import cc.blynk.server.exp4j.function.PredefinedArgumentFunction;
import cc.blynk.server.exp4j.function.ZeroArgumentFunction;
import org.junit.Test;

import java.util.Date;


public class ExpressionValidateTest {

	/**
	 * Dummy function with 2 arguments.
	 */
	private Function beta = new DynamicArgumentFunction("beta", 1, 2) {

		@Override
		public double apply(double... args) {
			return args[1] - args[0];
		}
	};

	/**
	 * Dummy function with 3 arguments.
	 */
	private Function gamma = new PredefinedArgumentFunction("gamma", 3) {

		@Override
		public double apply(double... args) {
			return args[0] * args[1] / args[2];
		}
	};

	/**
	 * Dummy function with 7 arguments.
	 */
	private Function eta = new PredefinedArgumentFunction("eta", 7) {

		@Override
		public double apply(double... args) {
			double eta = 0;
			for (double a : args) {
				eta += a;
			}
			return eta;
		}
	};

	// valid scenarios

	@Test
	public void testValidateNumber() {
		Expression exp = new ExpressionBuilder("1")
			.build();
		exp.validateExpression();
	}

	@Test
	public void testValidateNumberPositive() {
		Expression exp = new ExpressionBuilder("+1")
			.build();
		exp.validateExpression();
	}

	@Test
	public void testValidateNumberNegative() {
		Expression exp = new ExpressionBuilder("-1")
			.build();
		exp.validateExpression();
	}

	@Test
	public void testValidateOperator() {
		Expression exp = new ExpressionBuilder("x + 1 + 2")
			.variable("x")
			.build();
		exp.validateExpression();
	}

	@Test
	public void testValidateFunction() {
		Expression exp = new ExpressionBuilder("sin(x)")
			.variable("x")
			.build();
		exp.validateExpression();
	}

	@Test
	public void testValidateFunctionPositive() {
		Expression exp = new ExpressionBuilder("+sin(x)")
			.variable("x")
			.build();
		exp.validateExpression();
	}

	@Test
	public void testValidateFunctionNegative() {
		Expression exp = new ExpressionBuilder("-sin(x)")
			.variable("x")
			.build();
		exp.validateExpression();
	}

	@Test
	public void testValidateFunctionAndOperator() {
		Expression exp = new ExpressionBuilder("sin(x + 1 + 2)")
			.variable("x")
			.build();
		exp.validateExpression();
	}

	@Test
	public void testValidateFunctionWithTwoArguments() {
		Expression exp = new ExpressionBuilder("beta(x, y)")
			.variables("x", "y")
			.functions(beta)
			.build();
		exp.validateExpression();
	}

	@Test
	public void testValidateFunctionWithTwoArgumentsAndOperator() {
		Expression exp = new ExpressionBuilder("beta(x, y + 1)")
			.variables("x", "y")
			.functions(beta)
			.build();
		exp.validateExpression();
	}

	@Test
	public void testValidateFunctionWithThreeArguments() {
		Expression exp = new ExpressionBuilder("gamma(x, y, z)")
			.variables("x", "y", "z")
			.functions(gamma)
			.build();
		exp.validateExpression();
	}

	@Test
	public void testValidateFunctionWithThreeArgumentsAndOperator() {
		Expression exp = new ExpressionBuilder("gamma(x, y, z + 1)")
			.variables("x", "y", "z")
			.functions(gamma)
			.build();
		exp.validateExpression();
	}

	@Test
	public void testValidateFunctionWithTwoAndThreeArguments() {
		Expression exp = new ExpressionBuilder("gamma(x, beta(y, h), z)")
			.variables("x", "y", "z", "h")
			.functions(gamma, beta)
			.build();
		exp.validateExpression();
	}

	@Test
	public void testValidateFunctionWithTwoAndThreeArgumentsAndOperator() {
		Expression exp = new ExpressionBuilder("gamma(x, beta(y, h), z + 1)")
			.variables("x", "y", "z", "h")
			.functions(gamma, beta)
			.build();
		exp.validateExpression();
	}

	@Test
	public void testValidateFunctionWithTwoAndThreeArgumentsAndMultipleOperator() {
		Expression exp = new ExpressionBuilder("gamma(x * 2 / 4, beta(y, h + 1 + 2), z + 1 + 2 + 3 + 4)")
			.variables("x", "y", "z", "h")
			.functions(gamma, beta)
			.build();
		exp.validateExpression();
	}

	@Test
	public void testValidateFunctionWithSevenArguments() {
		Expression exp = new ExpressionBuilder("eta(1, 2, 3, 4, 5, 6, 7)")
			.functions(eta)
			.build();
		exp.validateExpression();
	}

	@Test
	public void testValidateFunctionWithSevenArgumentsAndoperator() {
		Expression exp = new ExpressionBuilder("eta(1, 2, 3, 4, 5, 6, 7) * 2 * 3 * 4")
			.functions(eta)
			.build();
		exp.validateExpression();
	}

	// invalid scenarios

	@Test(expected = ParseExpressionException.class)
	public void testValidateInvalidFunction() {
		Expression exp = new ExpressionBuilder("sin()")
			.build();
		exp.validateExpression();
	}

	@Test(expected = ParseExpressionException.class)
	public void testValidateInvalidOperand() {
		Expression exp = new ExpressionBuilder("1 + ")
			.build();
		exp.validateExpression();
	}

	@Test(expected = ParseExpressionException.class)
	public void testValidateInvalidFunctionWithTooFewArguments() {
		Expression exp = new ExpressionBuilder("beta()")
			.functions(beta)
			.build();
		exp.validateExpression();
	}

	@Test(expected = ParseExpressionException.class)
	public void testValidateInvalidFunctionWithTooFewArgumentsAndOperands() {
		Expression exp = new ExpressionBuilder("beta(1 + )")
			.functions(beta)
			.build();
		exp.validateExpression();
	}

	@Test(expected = ParseExpressionException.class)
	public void testValidateInvalidFunctionWithManyArguments() {
		Expression exp = new ExpressionBuilder("beta(1, 2, 3)")
			.functions(beta)
			.build();
		exp.validateExpression();
	}

	@Test(expected = ParseExpressionException.class)
	public void testValidateInvalidOperator() {
		Expression exp = new ExpressionBuilder("+")
			.build();
		exp.validateExpression();
	}

	// Thanks go out to werwiesel for reporting the issue
	// https://github.com/fasseg/exp4j/issues/59
	@Test
	public void testNoArgFunctionValidation() {
		Function now = new ZeroArgumentFunction("now") {
			@Override
			public double apply() {
				return (double) new Date().getTime();
			}
		};
		Expression e = new ExpressionBuilder("14*now()")
				.function(now)
				.build();
		e.validateExpression();

		e = new ExpressionBuilder("now()")
				.function(now)
				.build();
		e.validateExpression();

		e = new ExpressionBuilder("sin(now())")
				.function(now)
				.build();
		e.validateExpression();

		e = new ExpressionBuilder("sin(now()) % 14")
				.function(now)
				.build();
		e.validateExpression();
	}

}
