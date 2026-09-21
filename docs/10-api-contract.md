# QueueForge - API Contract

## Version

**1.0**

---

# 1. Purpose

This document defines the REST API contract for QueueForge.

It specifies:

* Endpoints
* Request Format
* Response Format
* Authentication
* Validation Rules
* HTTP Status Codes
* Error Responses

The API contract serves as the agreement between backend services and API consumers.

---

# 2. API Design Principles

QueueForge APIs follow these principles:

* RESTful Design
* Consistent Naming
* Stateless Communication
* Versioning
* Standard Response Format
* Proper HTTP Status Codes

---

# 3. Base URL

```text
/api/v1
```

Future versions:

```text
/api/v2
/api/v3
```

---

# 4. Authentication

QueueForge uses JWT Authentication.

Protected endpoints require:

```http
Authorization: Bearer <JWT_TOKEN>
```

Public endpoints:

* Login
* Health Check

---

# 5. Standard Response Format

## Success Response

```json
{
  "success": true,
  "message": "Job created successfully.",
  "data": {},
  "timestamp": "2026-06-30T10:00:00Z"
}
```

---

## Error Response

```json
{
  "success": false,
  "message": "Invalid job type.",
  "errorCode": "INVALID_JOB_TYPE",
  "timestamp": "2026-06-30T10:00:00Z"
}
```

---

# 6. Job APIs

## Create Job

### Endpoint

```http
POST /api/v1/jobs
```

Purpose:

Creates a new asynchronous job.

### Request

```json
{
  "jobName": "Welcome Email",
  "jobType": "EMAIL",
  "priority": "HIGH",
  "payload": {
    "recipient": "user@example.com",
    "subject": "Welcome",
    "message": "Hello!"
  }
}
```

### Success

```http
201 Created
```

---

## Get Job

```http
GET /api/v1/jobs/{jobId}
```

Returns:

* Status
* Priority
* Payload
* Retry Count
* Created Time

---

## List Jobs

```http
GET /api/v1/jobs
```

Supports:

```
?page=1
&size=20
&status=COMPLETED
&type=EMAIL
&priority=HIGH
```

---

## Cancel Job

```http
PATCH /api/v1/jobs/{jobId}/cancel
```

Cancels jobs that have not started.

---

## Retry Job

```http
POST /api/v1/jobs/{jobId}/retry
```

Requeues a failed job.

---

## Delete Job

```http
DELETE /api/v1/jobs/{jobId}
```

Administrative operation.

---

# 7. Worker APIs

## List Workers

```http
GET /api/v1/workers
```

Returns:

* Worker ID
* Type
* Status
* Active Jobs

---

## Worker Details

```http
GET /api/v1/workers/{workerId}
```

Returns:

* Registration Time
* Version
* Health
* Metrics

---

# 8. Queue APIs

## List Queues

```http
GET /api/v1/queues
```

Returns:

* Queue Length
* Consumers
* Processing Rate

---

## Queue Details

```http
GET /api/v1/queues/{queueName}
```

Returns runtime statistics.

---

# 9. Dead Letter Queue APIs

## List DLQ Jobs

```http
GET /api/v1/dlq
```

---

## Retry DLQ Job

```http
POST /api/v1/dlq/{jobId}/retry
```

---

## Delete DLQ Job

```http
DELETE /api/v1/dlq/{jobId}
```

---

# 10. Metrics APIs

## System Metrics

```http
GET /api/v1/metrics
```

Returns:

* Total Jobs
* Completed Jobs
* Failed Jobs
* Queued Jobs

---

## Queue Metrics

```http
GET /api/v1/metrics/queues
```

---

## Worker Metrics

```http
GET /api/v1/metrics/workers
```

---

# 11. Health APIs

## System Health

```http
GET /api/v1/health
```

---

## RabbitMQ Health

```http
GET /api/v1/health/rabbitmq
```

---

## Redis Health

```http
GET /api/v1/health/redis
```

---

## PostgreSQL Health

```http
GET /api/v1/health/database
```

---

# 12. Authentication APIs

## Login

```http
POST /api/v1/auth/login
```

Returns JWT Token.

---

## Refresh Token

```http
POST /api/v1/auth/refresh
```

Returns new access token.

---

# 13. AI APIs (Version 2)

## Analyze Failure

```http
POST /api/v2/ai/analyze
```

---

## Operations Chat

```http
POST /api/v2/ai/chat
```

---

## Natural Language Job

```http
POST /api/v2/ai/create-job
```

---

# 14. ML APIs (Version 3)

## Failure Prediction

```http
POST /api/v3/ml/predict-failure
```

---

## Worker Recommendation

```http
POST /api/v3/ml/recommend-worker
```

---

## Queue Forecast

```http
GET /api/v3/ml/forecast
```

---

## Duration Prediction

```http
POST /api/v3/ml/predict-duration
```

---

## Anomaly Detection

```http
GET /api/v3/ml/anomalies
```

---

# 15. Validation Rules

Every request shall be validated.

Examples:

* Required Fields
* Valid Job Type
* Valid Priority
* Valid Payload Structure
* Maximum Payload Size

Invalid requests return:

```http
400 Bad Request
```

---

# 16. HTTP Status Codes

| Code | Meaning               |
| ---- | --------------------- |
| 200  | Success               |
| 201  | Created               |
| 202  | Accepted              |
| 204  | No Content            |
| 400  | Bad Request           |
| 401  | Unauthorized          |
| 403  | Forbidden             |
| 404  | Not Found             |
| 409  | Conflict              |
| 422  | Validation Failed     |
| 429  | Too Many Requests     |
| 500  | Internal Server Error |

---

# 17. Error Codes

Examples:

* INVALID_JOB_TYPE
* INVALID_PAYLOAD
* JOB_NOT_FOUND
* WORKER_NOT_FOUND
* QUEUE_NOT_FOUND
* JOB_ALREADY_COMPLETED
* MAX_RETRY_EXCEEDED
* INTERNAL_ERROR

Each error code should have a documented meaning.

---

# 18. API Versioning

QueueForge uses URI versioning.

Examples:

```text
/api/v1/jobs

/api/v2/ai/chat

/api/v3/ml/predict-duration
```

Older API versions remain supported until officially deprecated.

---

# 19. API Documentation

All APIs shall be documented using:

* OpenAPI 3.x
* Swagger UI

Documentation must remain synchronized with implementation.

---

# Summary

The QueueForge API Contract provides a standardized and versioned interface for interacting with the platform.

By following REST principles, consistent response structures, and clear validation rules, the APIs remain predictable, maintainable, and easy to integrate for clients, testing tools, and future frontend applications.
