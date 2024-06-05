package com.lucaf;

import com.lucaf.datatypes.Response;

import java.util.concurrent.Callable;

/**
 * Class to handle the statistics requests asynchronously
 */
public class StatsAsync implements Runnable {

    /**
     * StatsAsyncEvents to handle the events of the StatsAsync class
     */
    private final StatsAsyncEvents statsAsyncEvents;

    /**
     * Callable to get the statistics
     */
    private final Callable<Response> stats;

    /**
     * Constructor to initialize the StatsAsync class with the Callable to get the statistics and the StatsAsyncEvents to handle the events
     *
     * @param stats            Callable to get the statistics
     * @param statsAsyncEvents StatsAsyncEvents to handle the events of the StatsAsync class
     */
    public StatsAsync(Callable<Response> stats, StatsAsyncEvents statsAsyncEvents) {
        this.statsAsyncEvents = statsAsyncEvents;
        this.stats = stats;
    }

    /**
     * Run method to start the statistics response handler
     */
    @Override
    public void run() {
        Response response = null;
        try {
            response = stats.call();
            statsAsyncEvents.onStatsAsyncComplete(response);
        } catch (Exception e) {
            statsAsyncEvents.onStatsAsyncError(e);
        }
    }
}
