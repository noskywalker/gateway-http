package com.baidu.fbu.mtp.common.retry;

public class RetryException extends Exception {

    private final int numberOfFailedAttempts;
    private final Exception lastFailedAttempt;

    /**
     * @param numberOfFailedAttempts times we've tried and failed
     * @param lastFailedAttempt what happened the last time we failed
     */
    public RetryException(int numberOfFailedAttempts, Exception lastFailedAttempt) {
        this("Retrying failed to complete successfully after " + numberOfFailedAttempts + " attempts.",
                numberOfFailedAttempts, lastFailedAttempt);
    }

    /**
     * @param message Exception description to be added to the stack trace
     * @param numberOfFailedAttempts times we've tried and failed
     * @param lastFailedAttempt what happened the last time we failed
     */
    public RetryException(String message, int numberOfFailedAttempts, Exception lastFailedAttempt) {
        super(message, lastFailedAttempt);
        this.numberOfFailedAttempts = numberOfFailedAttempts;
        this.lastFailedAttempt = lastFailedAttempt;
    }

    /**
     * Returns the number of failed attempts
     */
    public int getNumberOfFailedAttempts() {
        return numberOfFailedAttempts;
    }

    /**
     * Returns what happened the last time we failed
     */
    public Exception getLastFailedAttempt() {
        return lastFailedAttempt;
    }
}