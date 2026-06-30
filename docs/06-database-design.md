# QueueFlow - Database Design

## Version

**1.0**

---

# 1. Purpose

This document defines the relational database design for QueueFlow.

It specifies:

* Database Schema
* Tables
* Relationships
* Constraints
* Indexes
* Enums
* Data Flow

The database is designed for PostgreSQL and follows normalization principles while keeping future scalability in mind.

---

# 2. Database Overview

QueueFlow uses a **single PostgreSQL database** in Version 1.

Reason:

* Easier development
* Easier deployment
* Easier maintenance
* Sufficient for current architecture

Future versions may split databases by service if required.

---

# 3. Database Schema

```text
queueflow_db

├── jobs
├── job_history
├── workers
├── retry_history
└── dead_letter_jobs
```

---

# 4. Entity Relationship Diagram (Conceptual)

```text
             jobs
              │
      ┌───────┼────────┐
      ▼       ▼        ▼
job_history retry_history dead_letter_jobs

workers
```

---

# 5. Table: jobs

## Purpose

Stores every job submitted to QueueFlow.

---

### Columns

| Column       | Type         | Description                    |
| ------------ | ------------ | ------------------------------ |
| id           | UUID         | Primary Key                    |
| job_name     | VARCHAR(150) | User-friendly job name         |
| job_type     | VARCHAR(50)  | EMAIL, PDF, IMAGE, etc.        |
| category     | VARCHAR(50)  | COMMUNICATION, DOCUMENT, MEDIA |
| priority     | VARCHAR(20)  | LOW, MEDIUM, HIGH              |
| status       | VARCHAR(30)  | Current job status             |
| payload      | JSONB        | Job request data               |
| retry_count  | INTEGER      | Current retry attempts         |
| max_retry    | INTEGER      | Maximum retries allowed        |
| created_at   | TIMESTAMP    | Creation time                  |
| updated_at   | TIMESTAMP    | Last update                    |
| started_at   | TIMESTAMP    | Processing start               |
| completed_at | TIMESTAMP    | Completion time                |

---

### Indexes

* id (Primary Key)
* status
* job_type
* category
* priority
* created_at

---

# 6. Table: job_history

## Purpose

Stores every status transition of a job.

---

### Columns

| Column          | Type      |
| --------------- | --------- |
| id              | UUID      |
| job_id          | UUID (FK) |
| previous_status | VARCHAR   |
| current_status  | VARCHAR   |
| remarks         | TEXT      |
| changed_at      | TIMESTAMP |

---

Relationship

```
One Job
↓

Many History Records
```

---

# 7. Table: workers

## Purpose

Stores registered workers.

Redis contains live status.

PostgreSQL stores persistent information.

---

### Columns

| Column          | Type      |
| --------------- | --------- |
| id              | UUID      |
| worker_name     | VARCHAR   |
| worker_type     | VARCHAR   |
| version         | VARCHAR   |
| registered_at   | TIMESTAMP |
| last_started_at | TIMESTAMP |

---

Worker status and heartbeat are intentionally **not stored here** because they belong in Redis.

---

# 8. Table: retry_history

## Purpose

Stores retry attempts.

---

### Columns

| Column         | Type      |
| -------------- | --------- |
| id             | UUID      |
| job_id         | UUID      |
| retry_number   | INTEGER   |
| failure_reason | TEXT      |
| retry_time     | TIMESTAMP |

---

# 9. Table: dead_letter_jobs

## Purpose

Stores permanently failed jobs.

---

### Columns

| Column          | Type      |
| --------------- | --------- |
| id              | UUID      |
| original_job_id | UUID      |
| failure_reason  | TEXT      |
| total_retries   | INTEGER   |
| payload         | JSONB     |
| moved_at        | TIMESTAMP |

---

# 10. Relationships

```text
jobs
 │
 ├────── job_history
 │
 ├────── retry_history
 │
 └────── dead_letter_jobs
```

All relationships use foreign keys.

---

# 11. Enums

## Job Status

```text
QUEUED
PROCESSING
COMPLETED
FAILED
RETRYING
CANCELLED
DLQ
```

---

## Job Priority

```text
LOW
MEDIUM
HIGH
```

---

## Worker Type

```text
COMMUNICATION
DOCUMENT
MEDIA
CLOUD
AI
```

---

## Job Category

```text
COMMUNICATION
DOCUMENT
MEDIA
CLOUD
AI
```

---

# 12. JSON Payload Design

Different job types require different fields.

Instead of creating separate tables for every job type, QueueFlow stores the payload in a JSONB column.

Example:

```json
{
  "recipient": "user@example.com",
  "subject": "Welcome",
  "message": "Hello"
}
```

Benefits:

* Flexible
* Extensible
* No schema changes for new job types

---

# 13. Constraints

The database shall enforce:

* Primary Keys
* Foreign Keys
* NOT NULL where required
* Default Values
* Check Constraints (where applicable)

Example:

```
retry_count >= 0
```

---

# 14. Indexing Strategy

Indexes will be created for frequently queried columns.

Examples:

* status
* job_type
* priority
* created_at
* job_id (foreign keys)

Purpose:

* Faster search
* Better pagination
* Improved filtering

---

# 15. Data Flow

```text
Client

↓

Create Job

↓

jobs

↓

RabbitMQ

↓

Worker

↓

job_history

↓

Completed

OR

retry_history

↓

Dead Letter Queue

↓

dead_letter_jobs
```

---

# 16. Future Database Enhancements

Future versions may introduce additional tables.

Examples:

* ai_analysis
* ml_predictions
* scheduled_jobs
* workflow_execution
* notifications
* audit_logs

These tables are intentionally excluded from Version 1.

---

# 17. Design Decisions

The database follows these principles:

* Single PostgreSQL database
* JSONB for flexible payloads
* Normalized relational structure
* Minimal duplication
* Future extensibility
* Separation of persistent and temporary data

Redis is responsible for temporary operational data, while PostgreSQL stores business-critical information.

---

# Summary

The QueueFlow database is designed to provide a reliable, maintainable, and scalable foundation for asynchronous job processing.

It balances relational integrity with flexibility by using normalized tables for core entities and JSONB for job-specific payloads, enabling new job types to be introduced without frequent schema changes.
