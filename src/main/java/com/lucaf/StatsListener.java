package com.lucaf;

import com.lucaf.datatypes.Response;

/**
 * Interface to handle the events of the StatsAsync class
 */
public interface StatsListener {

    /**
     * Method to handle the completion of the StatsAsync class
     *
     * @param response response of the computation
     */
    void onStatsAsyncComplete(Response response);

    /**
     * Method to handle the error of the StatsAsync class
     *
     * @param e exception thrown by the computation
     */
    void onStatsAsyncError(Exception e);
}
