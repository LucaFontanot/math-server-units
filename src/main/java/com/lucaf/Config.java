package com.lucaf;

import java.util.concurrent.ExecutorService;

/**
 * Class to store the configuration of the server
 */
public class Config {

    /**
     * Port of the server
     */
    public static int port = 9000;

    /**
     * Executor with at most n computation for processing computation requests at the same time, with n being equal to the number of available processors on the machine where the server is running.
     */
    public static ExecutorService executorService = null;




}
