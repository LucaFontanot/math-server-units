package com.lucaf;

import com.lucaf.datatypes.ComputeRequest;
import com.lucaf.datatypes.Response;
import com.lucaf.evaluator.Parser;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * Class to handle the computation requests
 */
public class Computation implements Callable<Response> {

    /**
     * ComputeRequest with the data to compute
     */
    public final ComputeRequest computeRequest;

    /**
     * Constructor to set the ComputeRequest
     *
     * @param computeRequest ComputeRequest with the data to compute
     */
    public Computation(ComputeRequest computeRequest) {
        this.computeRequest = computeRequest;
    }

    /**
     * List of all the possible combinations of values of the variables
     */
    private List<Map<String, Double>> values;

    /**
     * List of all the results of the computation for each combination of values and expression
     */
    private List<Double> results;

    /**
     * Call method to run the computation
     *
     * @return Response with the result of the computation
     * @throws Exception if any runtime exception occurs
     */
    @Override
    public Response call() throws Exception {
        long startTime = System.currentTimeMillis();
        if (!computeRequest.getErrorMessage().isEmpty()) {
            return new Response(new String[]{computeRequest.getErrorMessage()});
        }
        if (!setAllPossibleValues()) {
            return new Response(new String[]{computeRequest.getErrorMessage()});
        }
        if (computeRequest.getComputationKind() == ComputeRequest.ComputationKind.COUNT) {
            Response response = new Response(values.size());
            response.setTime(System.currentTimeMillis() - startTime);
            return response;
        }
        if (!computeExpressions()) {
            return new Response(new String[]{computeRequest.getErrorMessage()});
        }
        if (computeRequest.getComputationKind() == ComputeRequest.ComputationKind.MAX) {
            Response response = new Response(results.stream().mapToDouble(Double::doubleValue).max().orElse(0));
            response.setTime(System.currentTimeMillis() - startTime);
            return response;
        } else if (computeRequest.getComputationKind() == ComputeRequest.ComputationKind.MIN) {
            Response response = new Response(results.stream().mapToDouble(Double::doubleValue).min().orElse(0));
            response.setTime(System.currentTimeMillis() - startTime);
            return response;
        } else if (computeRequest.getComputationKind() == ComputeRequest.ComputationKind.AVG) {
            Response response = new Response(results.stream().mapToDouble(Double::doubleValue).average().orElse(0));
            response.setTime(System.currentTimeMillis() - startTime);
            return response;
        }

        return new Response(new String[]{"(IllegalArgument) Unknown computation kind"});
    }

    /**
     * Compute the expressions for all the possible values of the variables
     * and store the results in the results list
     */
    private boolean computeExpressions() {
        for (String expression : computeRequest.getExpressions()) {
            Parser parser = new Parser(expression);
            try {
                parser.initialize();
            }catch (IllegalArgumentException e){
                computeRequest.setErrorMessage("(InvalidExpression) The expression is not in a valid format");
                return false;
            }catch (IllegalStateException e){
                computeRequest.setErrorMessage("(InvalidExpression) The expression is empty");
                return false;
            }
            for (Map<String, Double> point : values) {
                parser.setVariables(point);
                try {
                    Double result = parser.evaluate();
                    if (!result.isNaN() && !result.isInfinite()) {
                        results.add(result);
                    }
                }catch (IllegalStateException e){
                    computeRequest.setErrorMessage("(InvalidExpression) The expression has not been initialized");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Set all the possible values of the variables
     *
     * @return true if the values are set correctly, false if an error occurs
     */
    private boolean setAllPossibleValues() {
        int[] indexes = new int[computeRequest.getVariableValuesFunctions().size()];
        values = new ArrayList<>();
        results = new ArrayList<>();
        if (computeRequest.getValueKind() == ComputeRequest.ValuesKind.GRID) {
            boolean isAvailable = true;
            while (isAvailable) {
                Map<String, Double> variables = new HashMap<>();
                for (int i = 0; i < indexes.length; i++) {
                    variables.put(
                            computeRequest.getVariableValuesFunctions().get(i).getVariable(),
                            computeRequest.getVariableValuesFunctions().get(i).getLower() + indexes[i] * computeRequest.getVariableValuesFunctions().get(i).getStep()
                    );
                }
                values.add(variables);
                indexes[indexes.length - 1]++;
                for (int i = indexes.length - 1; i >= 0; i--) {
                    if (indexes[i] >= (computeRequest.getVariableValuesFunctions().get(i)).getSize()) {
                        if (i == 0) {
                            isAvailable = false;
                            break;
                        }
                        indexes[i] = 0;
                        indexes[i - 1]++;
                    }
                }
            }
        } else {
            int[] sizes = new int[computeRequest.getVariableValuesFunctions().size()];
            for (int i = 0; i < sizes.length; i++) {
                sizes[i] = computeRequest.getVariableValuesFunctions().get(i).getSize();
            }
            boolean allSizesEqual = true;
            for (int i = 1; i < sizes.length; i++) {
                if (sizes[i] != sizes[i - 1]) {
                    allSizesEqual = false;
                    break;
                }
            }
            if (!allSizesEqual) {
                computeRequest.setErrorMessage("(IllegalArgument) The range of the variables must have the same size");
                return false;
            }
            for (int i = 0; i < sizes[0]; i++) {
                Map<String, Double> variables = new HashMap<>();
                for (int j = 0; j < computeRequest.getVariableValuesFunctions().size(); j++) {
                    variables.put(
                            computeRequest.getVariableValuesFunctions().get(j).getVariable(),
                            computeRequest.getVariableValuesFunctions().get(j).getLower() + i * computeRequest.getVariableValuesFunctions().get(j).getStep()
                    );
                }
                values.add(variables);
            }
        }
        return true;
    }
}