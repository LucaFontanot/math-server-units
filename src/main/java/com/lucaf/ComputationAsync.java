package com.lucaf;

import com.lucaf.datatypes.Response;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

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
    private final ComputationListener computationListener;

    /**
     * Constructor to set the computation and the events
     * @param computation computation to run
     * @param computationListener events of the computation
     */
    public ComputationAsync(Callable<Response> computation, ComputationListener computationListener) {
        this.computation = computation;
        this.computationListener = computationListener;
    }

    /**
     * Run the computation and notify the events
     */
    @Override
    public void run() {
        Response response = null;
        try {
            Future<Response> future = Config.executorService.submit(computation);
            response = future.get();
            computationListener.onComputationAsyncComplete(response);
        } catch (Exception e) {
            computationListener.onComputationAsyncError(e);
        }
    }
}
