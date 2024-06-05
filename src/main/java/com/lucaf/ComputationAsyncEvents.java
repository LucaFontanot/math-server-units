package com.lucaf;

import com.lucaf.datatypes.Response;

/**
 * Interface to handle the events of the Computation execution
 */
public interface ComputationAsyncEvents {

    /**
     * Method to handle the completion of the computation
     *
     * @param response Response with the result of the computation
     */
    void onComputationAsyncComplete(Response response);

    /**
     * Method to handle the error of the computation
     *
     * @param e Exception with the error of the computation
     */
    void onComputationAsyncError(Exception e);
}
