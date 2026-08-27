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
}