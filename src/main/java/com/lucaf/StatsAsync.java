package com.lucaf;

import com.lucaf.datatypes.Response;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Class to handle the statistics requests asynchronously
 */
public class StatsAsync implements Runnable {

    /**
     * StatsAsyncEvents to handle the events of the StatsAsync class
     */
    private final StatsListener statsListener;

    /**
     * Callable to get the statistics
     */
    private final Callable<Response> stats;

    /**
     * Constructor to initialize the StatsAsync class with the Callable to get the statistics and the StatsAsyncEvents to handle the events
     *
     * @param stats            Callable to get the statistics
     * @param statsListener StatsAsyncEvents to handle the events of the StatsAsync class
     */
    public StatsAsync(Callable<Response> stats, StatsListener statsListener) {
        this.statsListener = statsListener;
        this.stats = stats;
    }

    /**
     * Run method to start the statistics response handler
     */
    @Override
    public void run() {
        Response response = null;
        try {
            Future<Response> future = Config.executorService.submit(stats);
            response = future.get();
            statsListener.onStatsAsyncComplete(response);
        } catch (Exception e) {
            statsListener.onStatsAsyncError(e);
        }
    }
}
