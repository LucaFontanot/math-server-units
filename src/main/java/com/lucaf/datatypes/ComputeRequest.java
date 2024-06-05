package com.lucaf.datatypes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class to represent a ComputeRequest
 */
public class ComputeRequest extends Request {

    /**
     * Enum to represent the kind of computation
     */
    public enum ComputationKind {

        /**
         * MIN_GRID: Minimum value of the computations
         */
        MIN,

        /**
         * MAX_GRID: Maximum value of the computations
         */
        MAX,

        /**
         * AVG_GRID: Average value of the computations
         */
        AVG,

        /**
         * COUNT_GRID: Count all the possible combinations of variables values
         */
        COUNT,
    }

    /**
     * Enum to represent the kind of values for the computation
     */
    public enum ValuesKind {
        /**
         * GRID: Values as a cartesian product
         */
        GRID,

        /**
         * LIST: Values as element-wise merging
         */
        LIST,
    }

    /**
     * Class to represent the variable values function
     */
    public class VariableValuesFunction {

        /**
         * Variable name
         * Must validate [a-z][a-z0-9]*
         */
        private final String variable;

        /**
         * Lower bound of the range
         */
        private final Double lower;

        /**
         * Upper bound of the range
         */
        private final Double upper;

        /**
         * Step of the range
         */
        private final Double step;

        /**
         * Number of values in the range between lower and upper with step
         */
        private final int size;

        /**
         * Getter for variable
         *
         * @return variable name
         */
        public String getVariable() {
            return variable;
        }

        /**
         * Getter for lower
         *
         * @return lower bound
         */
        public Double getLower() {
            return lower;
        }

        /**
         * Getter for upper
         *
         * @return upper bound
         */
        public Double getUpper() {
            return upper;
        }

        /**
         * Getter for step
         *
         * @return step
         */
        public Double getStep() {
            return step;
        }

        /**
         * Getter for size
         *
         * @return size
         */
        public int getSize() {
            return size;
        }

        /**
         * Constructor for VariableValuesFunction
         *
         * @param variable variable name
         * @param values   array of values [lower, step, upper]
         */
        public VariableValuesFunction(String variable, Double[] values) {
            this.variable = variable;
            this.lower = values[0];
            this.step = values[1];
            this.upper = values[2];
            this.size = (int) Math.ceil((upper - lower) / step) + 1;
        }
    }

    /**
     * List of expressions to compute
     */
    private List<String> expressions;

    /**
     * List of variable values functions for the expressions
     */
    private List<VariableValuesFunction> variableValuesFunctions;

    /**
     * Kind of computation
     */
    private ComputationKind computationKind;

    /**
     * Kind of values
     */
    private ValuesKind valueKind;

    /**
     * Error message (empty if no error)
     */
    private String errorMessage = "";

    /**
     * Error message of the request
     *
     * @param errorMessage error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Getter for computationKind
     *
     * @return computationKind
     */
    public ComputationKind getComputationKind() {
        return computationKind;
    }

    /**
     * Getter for valueKind
     *
     * @return valueKind
     */
    public ValuesKind getValueKind() {
        return valueKind;
    }

    /**
     * Getter for expressions
     *
     * @return expressions
     */
    public List<String> getExpressions() {
        return expressions;
    }

    /**
     * Getter for variableValuesFunctions
     *
     * @return variableValuesFunctions
     */
    public List<VariableValuesFunction> getVariableValuesFunctions() {
        return variableValuesFunctions;
    }

    /**
     * Getter for errorMessage
     *
     * @return errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Constructor for ComputeRequest
     *
     * @param request request to extend from
     */
    public ComputeRequest(Request request) {
        super(request.getMessage());
        setComputationAndValue();
        setVariableValuesFunctions();
        if (computationKind != ComputationKind.COUNT) {
            setExpressions();
        }
    }

    /**
     * Check if the item is a valid expression
     * The expression must contain only numbers, operators, and variables set in the variableValuesFunctions
     *
     * @param item item to check
     * @return true if the item is a valid expression
     */
    private boolean isExpression(String item) {
        String regexStart = "(";
        String regexEnd = "[0-9+\\-*\\/^().])*";
        StringBuilder regex = new StringBuilder();
        regex.append(regexStart);
        for (VariableValuesFunction variableValuesFunction : variableValuesFunctions) {
            regex.append(variableValuesFunction.getVariable());
            regex.append("|");
        }
        regex.append(regexEnd);
        Pattern pattern = Pattern.compile(regex.toString());
        return pattern.matcher(item).matches();
    }

    /**
     * Check if the item is a valid variable value function string
     * The variable value function must be in the format [a-z][a-z0-9]*:[lower]:[step]:[upper]
     *
     * @param item item to check
     * @return true if the item is a valid variable value function string
     */
    private boolean isVariableValueFunction(String item) {
        String regex = "[a-z][a-z0-9]*(:[0-9\\-]+(\\.[0-9]+)?){3}";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(item).matches();
    }

    /**
     * Calculate the computation kind and the value kind from the command string
     * The command string must be in the format [computationKind]_[valueKind]
     */
    private void setComputationAndValue() {
        String[] parts = this.getCommand().split("_");
        if (parts.length != 2) {
            throw new RuntimeException("Invalid message format");
        }
        String computationKindString = parts[0];
        String valueKindString = parts[1];
        this.computationKind = ComputationKind.valueOf(computationKindString);
        this.valueKind = ValuesKind.valueOf(valueKindString);
    }

    /**
     * Set the variable values functions from the message
     * The message must be in the format [computationKind]_[valueKind];[variableValuesFunction1],[variableValuesFunction2],...;[expression1];[expression2];...
     */
    private void setVariableValuesFunctions() {
        variableValuesFunctions = new ArrayList<>();
        String[] parts = this.getMessage().split(";");
        if (parts.length < 2) {
            errorMessage = "(NotEnoughArguments) Invalid message format";
            return;
        }
        String[] expressions = parts[1].split(",");
        for (String expression : expressions) {
            if (!isVariableValueFunction(expression)) {
                errorMessage = "(InvalidExpression) Invalid Variable Value Function";
                return;
            }
            String[] variableValues = expression.split(":");
            String variable = variableValues[0];
            Double[] doubleValues = new Double[variableValues.length - 1];
            for (int i = 1; i < variableValues.length; i++) {
                doubleValues[i - 1] = Double.parseDouble(variableValues[i]);
            }
            if (doubleValues[1] <= 0) {
                errorMessage = "(InvalidStep) Invalid Step";
                return;
            }
            if (doubleValues[0] > doubleValues[2]) {
                errorMessage = "(InvalidRange) Invalid Range";
                return;
            }
            VariableValuesFunction variableValuesFunction = new VariableValuesFunction(variable, doubleValues);
            variableValuesFunctions.add(variableValuesFunction);
        }
    }

    /**
     * Set the expressions from the message
     * The VariableValuesFunctions must be set before calling this method, and there must be no errors in the process
     */
    private void setExpressions() {
        if (!errorMessage.isEmpty()) {
            return;
        }
        expressions = new ArrayList<>();
        String[] parts = this.getMessage().split(";");
        if (parts.length < 3) {
            errorMessage = "(NotEnoughArguments) Invalid message format";
            return;
        }
        for (int i = 2; i < parts.length; i++) {
            if (!isExpression(parts[i])) {
                errorMessage = "(InvalidExpression) Invalid Expression";
                return;
            }
            parts[i] = parts[i].replaceAll(" ", "");
            this.expressions.add(parts[i]);
        }
    }
}
