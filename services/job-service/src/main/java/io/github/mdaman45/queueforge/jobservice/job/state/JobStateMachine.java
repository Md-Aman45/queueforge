package io.github.mdaman45.queueforge.jobservice.job.state;

import io.github.mdaman45.queueforge.jobservice.job.enums.JobStatus;

import java.util.Map;
import java.util.Set;

public class JobStateMachine {

    private static final Map<JobStatus, Set<JobStatus>> VALID_TRANSITIONS =
            Map.of(

                    JobStatus.ACCEPTED,
                    Set.of(JobStatus.RUNNING),

                    JobStatus.RUNNING,
                    Set.of(
                            JobStatus.COMPLETED,
                            JobStatus.FAILED
                    ),

                    JobStatus.FAILED,
                    Set.of(JobStatus.RETRYING),

                    JobStatus.RETRYING,
                    Set.of(
                            JobStatus.RUNNING,
                            JobStatus.DEAD_LETTER
                    ),

                    JobStatus.COMPLETED,
                    Set.of(),

                    JobStatus.DEAD_LETTER,
                    Set.of()

            );

    public static boolean isValidTransition(
            JobStatus current,
            JobStatus next
    ) {

        return VALID_TRANSITIONS
                .getOrDefault(current, Set.of())
                .contains(next);

    }

}