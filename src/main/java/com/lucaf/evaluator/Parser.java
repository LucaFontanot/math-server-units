package com.lucaf.evaluator;

import java.util.*;

/**
 * Class for parsing and evaluating mathematical expressions
 */
public class Parser {

    /**
     * Expression to parse
     */
    private String expression;

    /**
     * Map of the variables
     */
    private Map<String, Double> variables;

    /**
     * Root node of the tree
     */
    private Node root;

    /**
     * Constructor of the parser
     *
     * @param expression expression to parse
     */
    public Parser(String expression) {
        this.expression = expression;
    }

    /**
     * Set the variables of the expression
     *
     * @param variables map of the variables
     */
    public void setVariables(Map<String, Double> variables) {
        this.variables = variables;
    }

    /**
     * Get the variables of the expression
     *
     * @return map of the variables
     */
    public Map<String, Double> getVariables() {
        return this.variables;
    }

    /**
     * Gets the start and end position for the operator future tree
     *
     * @param tokens list of the tokens
     * @param index  index of the operator
     * @return array with the start and end indexes
     */
    private int[] getOperatorAroundValuesIndex(List<String> tokens, int index) {
        int[] result = new int[2];
        if (Tokens.isVariable(tokens.get(index + 1)) || Tokens.isNumber(tokens.get(index + 1))) {
            result[1] = index + 1;
        } else if (tokens.get(index + 1).equals("(")) {
            int open = 1;
            int i = index + 2;
            while (open > 0 && i < tokens.size()) {
                if (tokens.get(i).equals("(")) {
                    open++;
                } else if (tokens.get(i).equals(")")) {
                    open--;
                }
                i++;
            }
            i--;
            result[1] = i;
        }
        if (Tokens.isVariable(tokens.get(index - 1)) || Tokens.isNumber(tokens.get(index - 1))) {
            result[0] = index - 1;
        } else if (tokens.get(index - 1).equals(")")) {
            int open = 1;
            int i = index - 2;
            while (open > 0 && i >= 0) {
                if (tokens.get(i).equals("(")) {
                    open--;
                } else if (tokens.get(i).equals(")")) {
                    open++;
                }
                i--;
            }
            i++;
            result[0] = i;
        }
        return result;
    }

    /**
     * Fix the wrap of the expression by adding the missing parenthesis
     *
     * @param tokens list of the tokens
     */
    private void fixWrap(List<String> tokens) {
        fixWrap(tokens, 0, List.of("^"));
        fixWrap(tokens, 0, List.of("*", "/"));
        fixWrap(tokens, 0, List.of("+", "-"));
    }

    /**
     * Fix the wrap of the expression by adding the missing parenthesis
     *
     * @param tokens    list of the tokens
     * @param last      number of last operator wrapped
     * @param operators list of the operators to check and wrap
     */
    private void fixWrap(List<String> tokens, int last, List<String> operators) {
        int count = 0;
        for (int i = 0; i < tokens.size(); i++) {
            if (operators.contains(tokens.get(i))) {
                count++;
                if (count <= last) {
                    continue;
                }
                int[] indexes = getOperatorAroundValuesIndex(tokens, i);
                if (indexes[0] == 0 || indexes[1] == tokens.size() - 1) {
                    tokens.add(indexes[1] + 1, ")");
                    tokens.add(indexes[0], "(");
                    fixWrap(tokens, count, operators);
                    break;
                }
                if (tokens.get(indexes[0] - 1).equals("(") && tokens.get(indexes[1] + 1).equals(")")) {
                    continue;
                } else {
                    tokens.add(indexes[1] + 1, ")");
                    tokens.add(indexes[0], "(");
                    fixWrap(tokens, count, operators);
                    break;
                }
            }
        }
    }

    /**
     * Tokenize the expression
     *
     * @param exp expression to tokenize
     * @return array of the tokens
     */
    private String[] tokenize(String exp) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (char c : exp.toCharArray()) {
            if (Tokens.isOperator(String.valueOf(c))) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current = new StringBuilder();
                }
                tokens.add(String.valueOf(c));
            } else if (c == '(' || c == ')') {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current = new StringBuilder();
                }
                tokens.add(String.valueOf(c));
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            tokens.add(current.toString());
        }
        fixWrap(tokens);
        return tokens.toArray(String[]::new);
    }

    /**
     * Builds the Parse Tree from the expression
     *
     * @throws IllegalArgumentException if the expression is invalid
     * @throws IllegalStateException    if the expression is empty
     */
    public void initialize() throws IllegalArgumentException, IllegalStateException {
        if (this.expression == null) {
            throw new IllegalStateException("The expression is empty");
        }
        this.expression = this.expression.replaceAll(" ", "");
        String[] tokenized = tokenize(this.expression);
        root = new Node("", this);
        Node current = root;
        for (String token : tokenized) {
            if (token.equals("(")) {
                Node newNode = new Node("", this);
                newNode.setParent(current);
                current.setLeft(newNode);
                current = newNode;
            } else if (token.equals(")")) {
                current = current.getParent();
            } else {
                if (Tokens.isOperator(token)) {
                    current.setValue(token);
                    Node newNode = new Node("", this);
                    newNode.setParent(current);
                    current.setRight(newNode);
                    current = newNode;
                } else if (Tokens.isNumber(token) || Tokens.isVariable(token)) {
                    current.setValue(token);
                    current = current.getParent();
                } else {
                    throw new IllegalArgumentException("Invalid token: " + token);
                }
            }
        }
    }

    /**
     * Evaluate the expression
     *
     * @return result of the expression
     * @throws IllegalStateException if the expression has not been initialized
     */
    public Double evaluate() throws IllegalStateException {
        if (root == null) {
            throw new IllegalStateException("The expression has not been initialized");
        }
        return root.toDouble();
    }
}
