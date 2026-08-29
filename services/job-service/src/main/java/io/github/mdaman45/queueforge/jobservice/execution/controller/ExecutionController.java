package io.github.mdaman45.queueforge.jobservice.execution.controller;

import io.github.mdaman45.queueforge.jobservice.common.response.ApiResponse;
import io.github.mdaman45.queueforge.jobservice.execution.dto.ExecutionResponse;
import io.github.mdaman45.queueforge.jobservice.execution.service.ExecutionService;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/executions")
public class ExecutionController {

        private final ExecutionService executionService;

        public ExecutionController(
                        ExecutionService executionService) {
                this.executionService = executionService;
        }

        @GetMapping("/{executionId}")
        public ResponseEntity<ApiResponse<ExecutionResponse>> getExecutionById(
                        @PathVariable String executionId) {

                ExecutionResponse response = executionService.getExecutionById(executionId);

                return ResponseEntity.ok(
                                new ApiResponse<>(
                                                true,
                                                "Execution retrieved successfully",
                                                response,
                                                Instant.now()));
        }

        @GetMapping("/job/{jobId}")
        public ResponseEntity<ApiResponse<List<ExecutionResponse>>> getExecutionsByJobId(
                        @PathVariable String jobId) {

                List<ExecutionResponse> response = executionService.getExecutionsByJobId(jobId);

                return ResponseEntity.ok(
                                new ApiResponse<>(
                                                true,
                                                "Executions retrieved successfully",
                                                response,
                                                Instant.now()));
        }

        @PatchMapping("/{executionId}/start")
        public ResponseEntity<ApiResponse<ExecutionResponse>> startExecution(
                        @PathVariable String executionId) {

                ExecutionResponse response = executionService.startExecution(executionId);

                return ResponseEntity.ok(
                                new ApiResponse<>(
                                                true,
                                                "Execution started successfully",
                                                response,
                                                Instant.now()));
        }

        @PatchMapping("/{executionId}/complete")
        public ResponseEntity<ApiResponse<ExecutionResponse>> completeExecution(
                        @PathVariable String executionId) {

                ExecutionResponse response = executionService.completeExecution(executionId);

                return ResponseEntity.ok(
                                new ApiResponse<>(
                                                true,
                                                "Execution completed successfully",
                                                response,
                                                Instant.now()));
        }

        @PatchMapping("/{executionId}/fail")
        public ResponseEntity<ApiResponse<ExecutionResponse>> failExecution(
                        @PathVariable String executionId) {

                var execution = executionService.failExecution(executionId);

                ExecutionResponse response = new ExecutionResponse(
                                execution.getId(),
                                execution.getJob().getId(),
                                execution.getStatus().name(),
                                execution.getAttemptNumber());

                return ResponseEntity.ok(
                                new ApiResponse<>(
                                                true,
                                                "Execution failed successfully",
                                                response,
                                                Instant.now()));
        }
}