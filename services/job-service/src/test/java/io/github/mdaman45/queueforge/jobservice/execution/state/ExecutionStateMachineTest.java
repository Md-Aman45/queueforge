package io.github.mdaman45.queueforge.jobservice.execution.state;

import io.github.mdaman45.queueforge.jobservice.execution.enums.ExecutionStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionStateMachineTest {

    @Test
    void startedToRunningShouldBeValid() {

        assertTrue(
                ExecutionStateMachine.isValidTransition(
                        ExecutionStatus.STARTED,
                        ExecutionStatus.RUNNING
                )
        );
    }

    @Test
    void runningToSucceededShouldBeValid() {

        assertTrue(
                ExecutionStateMachine.isValidTransition(
                        ExecutionStatus.RUNNING,
                        ExecutionStatus.SUCCEEDED
                )
        );
    }

    @Test
    void runningToFailedShouldBeValid() {

        assertTrue(
                ExecutionStateMachine.isValidTransition(
                        ExecutionStatus.RUNNING,
                        ExecutionStatus.FAILED
                )
        );
    }

    @Test
    void failedToRunningShouldBeInvalid() {

        assertFalse(
                ExecutionStateMachine.isValidTransition(
                        ExecutionStatus.FAILED,
                        ExecutionStatus.RUNNING
                )
        );
    }

    @Test
    void succeededToRunningShouldBeInvalid() {

        assertFalse(
                ExecutionStateMachine.isValidTransition(
                        ExecutionStatus.SUCCEEDED,
                        ExecutionStatus.RUNNING
                )
        );
    }

    @Test
    void startedToSucceededShouldBeInvalid() {

        assertFalse(
                ExecutionStateMachine.isValidTransition(
                        ExecutionStatus.STARTED,
                        ExecutionStatus.SUCCEEDED
                )
        );
    }

    @Test
    void failedToWaitingForRetryShouldBeValid() {

        assertTrue(
                ExecutionStateMachine.isValidTransition(
                        ExecutionStatus.FAILED,
                        ExecutionStatus.WAITING_FOR_RETRY
                )
        );
    }

    @Test
    void waitingForRetryToStartedShouldBeValid() {

        assertTrue(
                ExecutionStateMachine.isValidTransition(
                        ExecutionStatus.WAITING_FOR_RETRY,
                        ExecutionStatus.STARTED
                )
        );
    }

    @Test
    void waitingForRetryToCancelledShouldBeValid() {

        assertTrue(
                ExecutionStateMachine.isValidTransition(
                        ExecutionStatus.WAITING_FOR_RETRY,
                        ExecutionStatus.CANCELLED
                )
        );
    }

    @Test
    void failedToStartedShouldBeInvalid() {

        assertFalse(
                ExecutionStateMachine.isValidTransition(
                        ExecutionStatus.FAILED,
                        ExecutionStatus.STARTED
                )
        );
    }

    @Test
    void waitingForRetryToRunningShouldBeInvalid() {

        assertFalse(
                ExecutionStateMachine.isValidTransition(
                        ExecutionStatus.WAITING_FOR_RETRY,
                        ExecutionStatus.RUNNING
                )
        );
    }
}