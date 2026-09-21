package io.github.mdaman45.queueforge.jobservice.execution.enums;

public enum ExecutionStatus {

    STARTED,
    RUNNING,
    FAILED,
    WAITING_FOR_RETRY,
    SUCCEEDED,
    TIMED_OUT,
    CANCELLED
}