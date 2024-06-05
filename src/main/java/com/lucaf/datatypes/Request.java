package com.lucaf.datatypes;

/**
 * Class to represent a Request
 */
public class Request {

    /**
     * Enum to represent the possible request types
     */
    public enum RequestType {

        /**
         * QuitRequest: BYE
         */
        QuitRequest(new String[]{"BYE"}),

        /**
         * StatRequest: STAT_REQS, STAT_AVG_TIME, STAT_MAX_TIME
         */
        StatRequest(new String[]{"STAT_REQS", "STAT_AVG_TIME", "STAT_MAX_TIME"}),

        /**
         * ComputationRequest: MIN_GRID, MAX_GRID, AVG_GRID, COUNT_GRID, MIN_LIST, MAX_LIST, COUNT_LIST, AVG_LIST
         */
        ComputationRequest(new String[]{"MIN_GRID", "MAX_GRID", "AVG_GRID", "COUNT_GRID", "MIN_LIST", "MAX_LIST", "COUNT_LIST", "AVG_LIST"});

        /**
         * Strings that represent the request type to be matched
         */
        private final String[] strings;

        /**
         * Constructor to set the strings
         *
         * @param strings strings that represent the request type to be matched
         */
        RequestType(String[] strings) {
            this.strings = strings;
        }
    }

    /**
     * Enum rappresenting the current request type
     */
    private RequestType requestType;

    /**
     * Command of the request
     */
    private String command;

    /**
     * Message of the request
     */
    private String message;


    /**
     * Getter for the message
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Getter for the request type
     *
     * @return request type
     */
    public RequestType getRequestType() {
        return requestType;
    }

    /**
     * Getter for the command
     *
     * @return command
     */
    public String getCommand() {
        return command;
    }

    /**
     * Method to get the command from the message
     *
     * @return command
     */
    private String setCommand() {
        return message.split(";")[0];
    }

    /**
     * Constructor to set the message and the command
     *
     * @param message message of the request
     */
    public Request(String message) {
        this.message = message;
        this.command = setCommand();
        this.requestType = setRequestType();
    }

    /**
     * Method to get the request type from the command
     *
     * @return request type or null if the command is not recognized
     */
    private RequestType setRequestType() {
        for (RequestType requestType : RequestType.values()) {
            for (String string : requestType.strings) {
                if (command.equals(string)) {
                    return requestType;
                }
            }
        }
        return null;
    }

    /**
     * Method to get the extended request as a ComputeRequest from the current request
     *
     * @return ComputeRequest with the request information
     */
    public ComputeRequest getComputeRequest() {
        if (requestType != RequestType.ComputationRequest) {
            throw new RuntimeException("Request is not a computation request");
        }
        return new ComputeRequest(this);
    }

    /**
     * Method to get the extended request as a StatRequest from the current request
     *
     * @return StatRequest with the request information
     */
    public StatRequest getStatRequest() {
        if (requestType != RequestType.StatRequest) {
            throw new RuntimeException("Request is not a stat request");
        }
        return new StatRequest(this);
    }
}
