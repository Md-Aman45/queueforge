package io.github.mdaman45.queueforge.jobservice.exception;

public class InvalidJobStateException extends RuntimeException {

    public InvalidJobStateException(String message) {
        super(message);
    }

}