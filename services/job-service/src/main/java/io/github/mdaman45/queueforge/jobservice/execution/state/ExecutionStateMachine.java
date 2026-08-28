package io.github.mdaman45.queueforge.jobservice.execution.state;

import io.github.mdaman45.queueforge.jobservice.execution.enums.ExecutionStatus;

public final class ExecutionStateMachine {

    private ExecutionStateMachine() {
    }

    public static boolean isValidTransition(
            ExecutionStatus current,
            ExecutionStatus next
    ) {

        if (current == next) {
            return false;
        }

        return switch (current) {

            case STARTED ->
                    next == ExecutionStatus.RUNNING
                            || next == ExecutionStatus.CANCELLED;

            case RUNNING ->
                    next == ExecutionStatus.SUCCEEDED
                            || next == ExecutionStatus.FAILED
                            || next == ExecutionStatus.TIMED_OUT
                            || next == ExecutionStatus.CANCELLED;

            case FAILED ->
                    next == ExecutionStatus.WAITING_FOR_RETRY;

            case WAITING_FOR_RETRY ->
                    next == ExecutionStatus.STARTED
                            || next == ExecutionStatus.CANCELLED;

            case SUCCEEDED,
                 TIMED_OUT,
                 CANCELLED ->
                    false;
        };
    }
}