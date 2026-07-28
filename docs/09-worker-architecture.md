# QueueForge - Worker Architecture

## Version

**1.0**

---

# 1. Purpose

This document describes the internal architecture of QueueForge workers.

Workers are responsible for consuming jobs from RabbitMQ, executing business logic, handling failures, updating job status, and reporting operational metrics.

Every worker follows the same architectural principles regardless of business domain.

---

# 2. Worker Types

QueueForge Version 1 contains the following workers.

| Worker               | Responsibility                          |
| -------------------- | --------------------------------------- |
| Communication Worker | Email, SMS, Push Notification, WhatsApp |
| Document Worker      | PDF, Excel, CSV, Reports                |
| Media Worker         | Image, Video, Audio Processing          |
| Cloud Worker         | Upload, Backup, Restore                 |
| AI Worker            | OCR, Translation, Summarization         |

Each worker processes only its own category of jobs.

---

# 3. Worker Lifecycle

Every worker follows the same lifecycle.

```text
Start

↓

Load Configuration

↓

Connect RabbitMQ

↓

Connect PostgreSQL

↓

Connect Redis

↓

Register Worker

↓

Start Heartbeat

↓

Listen Queue

↓

Receive Job

↓

Acquire Lock

↓

Execute Job

↓

Update Database

↓

ACK RabbitMQ

↓

Wait For Next Job
```

---

# 4. Internal Worker Architecture

Every worker contains the following modules.

```text
worker/

├── consumer/
├── processor/
├── retry/
├── heartbeat/
├── metrics/
├── lock/
├── registry/
├── service/
├── config/
├── exception/
└── util/
```

Each module has a single responsibility.

---

# 5. Consumer Module

Responsibilities:

* Listen to RabbitMQ
* Deserialize messages
* Validate messages
* Forward jobs to processors

The Consumer never contains business logic.

---

# 6. Processor Module

The Processor Module executes business logic.

QueueForge uses the **Strategy Design Pattern**.

Example:

```text
Communication Worker

processor/

├── EmailProcessor
├── SmsProcessor
├── PushProcessor
└── WhatsAppProcessor
```

Document Worker

```text
processor/

├── PdfProcessor
├── CsvProcessor
├── ExcelProcessor
└── ReportProcessor
```

Benefits:

* Open/Closed Principle
* Easy to extend
* Easy to test
* No large switch statements

---

# 7. Retry Handler

Responsibilities:

* Detect failures
* Check retry count
* Schedule retry
* Move jobs to DLQ

Retry logic is shared across all processors.

---

# 8. Heartbeat Manager

Every worker periodically sends a heartbeat.

Responsibilities:

* Update Redis
* Refresh worker status
* Detect stale workers

Heartbeat interval is configurable.

---

# 9. Worker Registry

On startup, every worker registers itself.

Stored Information:

* Worker ID
* Worker Type
* Version
* Supported Capabilities
* Registration Time

Runtime status is maintained in Redis.

---

# 10. Distributed Lock Manager

Before processing a job:

Worker acquires a Redis lock.

Example

```text
lock:job:{jobId}
```

Purpose:

Prevent duplicate processing.

The lock is released after processing completes.

---

# 11. Metrics Collector

Every worker records operational metrics.

Examples:

* Jobs Processed
* Success Count
* Failure Count
* Average Processing Time
* Retry Count

Metrics are exported to Prometheus.

---

# 12. Job Processing Flow

```text
Receive Message

↓

Acquire Lock

↓

Update Status

↓

Select Processor

↓

Execute Job

↓

Success?

↓

Yes → Update DB → ACK

↓

No → Retry Handler
```

---

# 13. Failure Handling

Recoverable Failures:

* SMTP Timeout
* Temporary Network Failure
* Third-Party API Timeout

Action:

Retry

---

Non-Recoverable Failures:

* Invalid Payload
* Unsupported Job Type
* Missing Required Data

Action:

Move to DLQ

---

# 14. Thread Pool

Workers process multiple jobs concurrently.

Each worker uses a configurable thread pool.

Example:

```text
Communication Worker

↓

10 Threads

↓

10 Jobs Executed Concurrently
```

Thread count should be configurable.

---

# 15. Worker Scaling

Workers support horizontal scaling.

Example:

```text
communication-worker

↓

Instance 1

Instance 2

Instance 3

Instance 4
```

RabbitMQ automatically distributes jobs among available instances.

No application changes are required.

---

# 16. Worker Shutdown

During graceful shutdown:

* Stop accepting new jobs
* Finish active jobs
* Release locks
* Update Redis status
* Close RabbitMQ connection
* Disconnect from PostgreSQL

Graceful shutdown prevents partial processing.

---

# 17. Worker Health

A worker is considered healthy if:

* RabbitMQ connection is active
* PostgreSQL connection is active
* Redis connection is active
* Heartbeats are current
* Thread pool is operational

Health endpoints expose current status.

---

# 18. Worker Communication

Workers never communicate directly.

Communication occurs through:

* RabbitMQ
* PostgreSQL
* Redis

This maintains loose coupling.

---

# 19. Design Principles

Workers follow:

* Single Responsibility Principle
* Strategy Pattern
* Event-Driven Processing
* Stateless Execution
* Independent Deployment
* Horizontal Scalability
* Fault Isolation

---

# 20. Future Enhancements

Future worker capabilities may include:

* Dynamic Plugin Loading
* Priority Scheduling
* Auto Scaling
* GPU Workers
* Batch Processing
* Distributed Scheduling
* Workflow Execution

These features are intentionally excluded from Version 1.

---

# Summary

Workers are the execution engine of QueueForge.

Each worker is an independent, stateless processing service responsible for consuming jobs, executing business logic through specialized processors, handling failures, maintaining operational state, and exporting metrics.

This architecture enables QueueForge to process large volumes of asynchronous jobs while remaining scalable, fault tolerant, maintainable, and easy to extend.
