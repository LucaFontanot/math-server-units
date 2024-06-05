package com.lucaf.evaluator;

import java.util.Map;
import java.util.function.Function;

/**
 * Utils class to manage the tokens and operators
 */
public class Tokens {

    /**
     * Regex for the operators
     */
    public static String operatorsRegex = "[\\+\\-\\*\\/\\^]";

    /**
     * Regex for the variables
     */
    public static String variableRegex = "[a-zA-Z]+[0-9]+";

    /**
     * Regex for the numbers
     */
    public static String numbersRegex = "[0-9]+(\\.[0-9]+)?";

    /**
     * Map of the operators for the computation
     */
    public static Map<String, Function<double[], Double>> operators = Map.of(
            "+", a -> a[0] + a[1],
            "-", a -> a[0] - a[1],
            "*", a -> a[0] * a[1],
            "/", a -> a[0] / a[1],
            "^", a -> Math.pow(a[0], a[1])
    );

    /**
     * Check if the token is an operator
     *
     * @param token token to check
     * @return true if the token is an operator, false otherwise
     */
    public static boolean isOperator(String token) {
        return token.matches(operatorsRegex);
    }

    /**
     * Check if the token is a number
     *
     * @param token token to check
     * @return true if the token is a number, false otherwise
     */
    public static boolean isNumber(String token) {
        return token.matches(numbersRegex);
    }

    /**
     * Check if the token is a variable
     *
     * @param token token to check
     * @return true if the token is a variable, false otherwise
     */
    public static boolean isVariable(String token) {
        return token.matches(variableRegex);
    }

}
