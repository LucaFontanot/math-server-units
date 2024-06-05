package com.lucaf;

import com.lucaf.datatypes.Response;
import com.lucaf.datatypes.StatRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * Class to handle the statistics requests
 */
public class Stats implements Callable<Response> {
    /**
     * Number of requests
     */
    private static Collection<Long> times = new ArrayList<>();

    /**
     * Add a request with time to the statistics
     *
     * @param time time of the request
     */
    public static void addRequest(long time) {
        times.add(time);
    }

    /**
     * Get the number of requests
     *
     * @return number of requests
     */
    public static int getRequests() {
        return times.size();
    }

    /**
     * Get the average time of the requests
     *
     * @return average time of the requests
     */
    public static double getAvgTime() {
        return ((double) times.stream().mapToLong(Long::longValue).average().orElse(0)) / 1000.0;
    }

    /**
     * Get the maximum time of the requests
     *
     * @return maximum time of the requests
     */
    public static double getMaxTime() {
        return ((double) times.stream().mapToLong(Long::longValue).max().orElse(0)) / 1000.0;
    }

    /**
     * StatRequest with the request information
     */
    private final StatRequest request;

    /**
     * Constructor to set the StatRequest
     *
     * @param request StatRequest with the request information
     */
    public Stats(StatRequest request) {
        this.request = request;
    }

    /**
     * Get the statistics
     *
     * @return Response with the statistics or an error message
     */
    @Override
    public Response call() throws Exception {
        if (!request.getErrorMessage().isEmpty()) {
            return new Response(new String[]{request.getErrorMessage()});
        }
        switch (request.getStatKind()) {
            case REQS:
                return new Response(getRequests());
            case AVG_TIME:
                return new Response(getAvgTime());
            case MAX_TIME:
                return new Response(getMaxTime());
        }
        return new Response(new String[]{"Invalid stat kind"});
    }
}
