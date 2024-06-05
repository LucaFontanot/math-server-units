package com.lucaf.datatypes;

/**
 * Class to represent a StatRequest
 */
public class StatRequest extends Request{

    /**
     * Enum to represent the kind of stat
     */
    public enum StatKind {

        /**
         * STAT_REQS: Number of requests
         */
        REQS,

        /**
         * STAT_AVG_TIME: Average time for the calculations
         */
        AVG_TIME,

        /**
         * STAT_MAX_TIME: Maximum time for the calculations
         */
        MAX_TIME,
    }

    /**
     * Kind of stat
     */
    private StatKind statKind;

    /**
     * Error message, (empty if no error)
     */
    private String errorMessage = "";

    /**
     * Getter for the error message
     * @return error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Getter for the kind of stat
     * @return kind of stat
     */
    public StatKind getStatKind() {
        return statKind;
    }

    /**
     * Constructor for a StatRequest
     * @param request request to extend from
     */
    public StatRequest(Request request) {
        super(request.getMessage());
        setStatKind();
    }

    /**
     * Method to set the kind of stat
     */
    private void setStatKind() {
        String command = super.getCommand().replace("STAT_", "");
        statKind = StatKind.valueOf(command);
        if (statKind == null) {
            errorMessage = "Invalid stat kind";
        }
    }
}
