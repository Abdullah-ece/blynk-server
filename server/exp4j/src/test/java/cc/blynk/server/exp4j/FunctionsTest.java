/*
* Copyright 2014 Frank Asseg
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

import cc.blynk.server.exp4j.function.DynamicArgumentFunction;
import cc.blynk.server.exp4j.function.Function;
import cc.blynk.server.exp4j.function.OneArgumentFunction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FunctionsTest {

    @Test(expected = IllegalArgumentException.class)
    public void testFunctionNameNull() {
        new DynamicArgumentFunction(null, 1, 1) {
            @Override
            public double apply(double... args) {
                return 0;
            }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFunctionNameEmpty() {
        new DynamicArgumentFunction("", 1, 1) {
            @Override
            public double apply(double... args) {
                return 0;
            }
        };
    }

    @Test
    public void testFunctionNameZeroArgs()  {
        DynamicArgumentFunction f =  new DynamicArgumentFunction("foo", 0, 0) {
            @Override
            public double apply(double... args) {
                return 0;
            }
        };
        assertEquals(0f, f.apply(null), 0f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFunctionNameNegativeArgs() {
        new DynamicArgumentFunction(null, 0, -1) {
            @Override
            public double apply(double... args) {
                return 0;
            }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalFunctionName1() {
        new DynamicArgumentFunction("1foo", 1, 1) {
            @Override
            public double apply(double... args) {
                return 0;
            }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalFunctionName2() {
        new DynamicArgumentFunction("_$oo", 1, 1) {
            @Override
            public double apply(double... args) {
                return 0;
            }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalFunctionName3() {
        new OneArgumentFunction("o+o") {
            @Override
            public double apply(double arg) {
                return 0;
            }
        };
    }

    @Test
    public void testCheckFunctionNames() {
        assertTrue(Function.isValidFunctionName("log"));
        assertTrue(Function.isValidFunctionName("sin"));
        assertTrue(Function.isValidFunctionName("abz"));
        assertTrue(Function.isValidFunctionName("alongfunctionnamecanhappen"));
        assertTrue(Function.isValidFunctionName("_log"));
        assertTrue(Function.isValidFunctionName("__blah"));
        assertTrue(Function.isValidFunctionName("foox"));
        assertTrue(Function.isValidFunctionName("aZ"));
        assertTrue(Function.isValidFunctionName("Za"));
        assertTrue(Function.isValidFunctionName("ZZaa"));
        assertTrue(Function.isValidFunctionName("_"));
        assertTrue(Function.isValidFunctionName("log2"));
        assertTrue(Function.isValidFunctionName("lo32g2"));
        assertTrue(Function.isValidFunctionName("_o45g2"));

        assertFalse(Function.isValidFunctionName("&"));
        assertFalse(Function.isValidFunctionName("_+log"));
        assertFalse(Function.isValidFunctionName("_k&l"));
        assertFalse(Function.isValidFunctionName("k&l"));
        assertFalse(Function.isValidFunctionName("+log"));
        assertFalse(Function.isValidFunctionName("fo-o"));
        assertFalse(Function.isValidFunctionName("log+"));
        assertFalse(Function.isValidFunctionName("perc%"));
        assertFalse(Function.isValidFunctionName("del$a"));
    }
}
