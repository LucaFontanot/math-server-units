package com.lucaf.datatypes;


import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Class to build a valid response
 */
public class Response {

    /**
     * OK code for the response
     */
    private final String OKCODE = "OK";

    /**
     * Error code for the response
     */
    private final String ERRORCODE = "ERR";

    /**
     * Time of the computation
     */
    private long time = 0;

    /**
     * Status of the response (true if OK, false if ERROR)
     */
    private final boolean status;

    /**
     * Codes of the response
     * <b>If status is true</b> codes[0] is the response of the computation
     * <b>If status is false</b> codes contains the errors and/or the stack trace
     */
    private final String[] codes;

    /**
     * Constructor for a generic response with a status
     *
     * @param status status of the response
     */
    public Response(boolean status) {
        this.status = status;
        this.codes = new String[]{};
    }

    /**
     * Constructor for a response with errors
     *
     * @param errors contains errors and/or the stack trace of the response
     */
    public Response(String[] errors) {
        this.status = false;
        this.codes = errors;
    }

    /**
     * Constructor for a response with a double value as computation result
     *
     * @param response response of the computation
     */
    public Response(double response) {
        this.status = true;
        NumberFormat formatter = new DecimalFormat("#0.000000");
        this.codes = new String[]{formatter.format(response).replace(",", ".")};
    }

    /**
     * Set the time of the computation in ms
     * The time is formatted as a string with 3 decimal digits
     * The time is in seconds
     *
     * @param time time in microseconds of the computation
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Method to get the time of the computation
     *
     * @return time of the computation in ms
     */
    public long getTime() {
        return time;
    }

    /**
     * Method to get if the response is OK
     *
     * @return true if the response is OK, false otherwise
     */
    public boolean isOk() {
        return status;
    }

    /**
     * Method to get the string representation of the response to send back to the client
     *
     * @return string representation
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (status) {
            stringBuilder.append(OKCODE);
            stringBuilder.append(";");
            NumberFormat formatter = new DecimalFormat("#0.000");
            String timeString = formatter.format(time / 1000.0).replace(",", ".");
            stringBuilder.append(timeString);
        } else {
            stringBuilder.append(ERRORCODE);
        }
        for (String code : codes) {
            stringBuilder.append(";");
            stringBuilder.append(code);
        }
        return stringBuilder.toString();
    }
}
