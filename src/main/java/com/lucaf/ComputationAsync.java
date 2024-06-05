package com.lucaf;

import com.lucaf.datatypes.Response;

import java.util.concurrent.Callable;

/**
 * Class to handle the computation requests asynchronously
 */
public class ComputationAsync implements Runnable{

    /**
     * Computation to run
     */
    private final Callable<Response> computation;

    /**
     * Events of the computation
     */
    private final ComputationAsyncEvents computationAsyncEvents;

    /**
     * Constructor to set the computation and the events
     * @param computation computation to run
     * @param computationAsyncEvents events of the computation
     */
    public ComputationAsync(Callable<Response> computation, ComputationAsyncEvents computationAsyncEvents) {
        this.computation = computation;
        this.computationAsyncEvents = computationAsyncEvents;
    }

    /**
     * Run the computation and notify the events
     */
    @Override
    public void run() {
        Response response = null;
        try {
            response = computation.call();
            computationAsyncEvents.onComputationAsyncComplete(response);
        } catch (Exception e) {
            computationAsyncEvents.onComputationAsyncError(e);
        }
    }
}
