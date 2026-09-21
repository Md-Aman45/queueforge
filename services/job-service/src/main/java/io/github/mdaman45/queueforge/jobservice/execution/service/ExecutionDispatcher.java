package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.entity.Execution;

public interface ExecutionDispatcher {

    void dispatch(Execution execution);
}