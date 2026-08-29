package io.github.mdaman45.queueforge.jobservice.execution.controller;

import io.github.mdaman45.queueforge.jobservice.common.response.ApiResponse;
import io.github.mdaman45.queueforge.jobservice.execution.dto.ExecutionResponse;
import io.github.mdaman45.queueforge.jobservice.execution.service.ExecutionService;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/executions")
public class ExecutionController {

    private final ExecutionService executionService;

    public ExecutionController(
            ExecutionService executionService
    ) {
        this.executionService = executionService;
    }


    @GetMapping("/{executionId}")
    public ResponseEntity<ApiResponse<ExecutionResponse>> getExecutionById(
            @PathVariable String executionId
    ) {

        ExecutionResponse response =
                executionService.getExecutionById(executionId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Execution retrieved successfully",
                        response,
                        Instant.now()
                )
        );
    }


    @PatchMapping("/{executionId}/start")
    public ResponseEntity<ApiResponse<ExecutionResponse>> startExecution(
            @PathVariable String executionId
    ) {

        ExecutionResponse response =
                executionService.startExecution(executionId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Execution started successfully",
                        response,
                        Instant.now()
                )
        );
    }
}