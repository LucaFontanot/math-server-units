package com.lucaf.evaluator;

import static java.lang.Double.NaN;

/**
 * Class to represent a node of the tree
 */
public class Node {

    /**
     * Parent node
     */
    private Node parent = null;

    /**
     * Left child node
     */
    private Node left = null;

    /**
     * Right child node
     */
    private Node right = null;

    /**
     * Value of the node
     */
    private String value = "";

    /**
     * Parser object of the tree
     */
    private Parser parser;

    /**
     * Constructor of the Node
     *
     * @param value  value of the node
     * @param parser parser object of the tree
     */
    public Node(String value, Parser parser) {
        this.value = value;
        this.parser = parser;
    }

    /**
     * Get the value of the node
     *
     * @return value of the node
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value of the node
     *
     * @param value value of the node
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Get the parent node
     *
     * @return parent node
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Set the parent node
     *
     * @param parent parent node
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Set the left child node
     *
     * @param left left child node
     */
    public void setLeft(Node left) {
        this.left = left;
    }

    /**
     * Set the right child node
     *
     * @param right right child node
     */
    public void setRight(Node right) {
        this.right = right;
    }

    /**
     * Compute the value of the tree using the current node as root of the tree
     *
     * @return value of the tree
     */
    public Double toDouble() {
        if (Tokens.isNumber(value)) {
            return Double.parseDouble(value);
        } else if (Tokens.isVariable(value)) {
            return parser.getVariables().get(value);
        } else {
            double[] children = new double[2];
            if (left != null) {
                children[0] = left.toDouble();
            }
            if (right != null) {
                children[1] = right.toDouble();
            }
            Double result = Tokens.operators.get(value).apply(children);
            if (result.isInfinite()) {
                result = NaN;
            }
            return result;
        }
    }

}
