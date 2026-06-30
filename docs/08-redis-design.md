# QueueFlow - Redis Design

## Version

**1.0**

---

# 1. Purpose

This document defines how Redis is used within QueueFlow.

Redis serves as the platform's high-speed, in-memory operational datastore.

Unlike PostgreSQL, which stores persistent business data, Redis manages temporary runtime information required for fast and efficient distributed processing.

---

# 2. Why Redis?

Redis provides:

* Extremely fast read/write operations
* In-memory data storage
* Distributed coordination
* Temporary data storage
* Atomic operations
* Expiration (TTL)

QueueFlow uses Redis to improve system performance without replacing PostgreSQL.

---

# 3. Responsibilities

Redis is responsible for:

* Worker Registry
* Worker Heartbeats
* Worker Load Tracking
* Distributed Locks
* Queue Statistics
* Rate Limiting
* AI Response Cache
* ML Prediction Cache

Redis is **not** used as the primary business database.

---

# 4. Architecture Overview

```text
                  Job Service
                       │
                       ▼
                     Redis
                       │
      ┌────────────┬────────────┬──────────────┐
      ▼            ▼            ▼
 Worker Cache   Queue Cache   AI/ML Cache
```

---

# 5. Worker Registry

Every worker registers itself during startup.

Example Key

```text
worker:communication:worker-01
```

Stored Value

```json
{
  "workerId": "worker-01",
  "type": "COMMUNICATION",
  "status": "ONLINE",
  "activeJobs": 2,
  "heartbeat": "2026-07-01T10:15:00Z"
}
```

Purpose:

* Worker discovery
* Worker monitoring
* Worker availability

---

# 6. Heartbeats

Every worker updates its heartbeat periodically.

Example

```text
worker:communication:worker-01:heartbeat
```

Example Value

```text
2026-07-01T10:15:00Z
```

Monitoring services use heartbeat timestamps to determine whether a worker is alive.

---

# 7. Worker Load Tracking

Redis stores the number of active jobs currently handled by each worker.

Example

```text
worker:communication:worker-01:load
```

Value

```text
4
```

Purpose

* Future ML worker recommendations
* Monitoring
* Capacity planning

---

# 8. Distributed Locks

Before processing a job, a worker acquires a temporary Redis lock.

Example Key

```text
lock:job:7d12b34c
```

Purpose

Prevent duplicate processing when multiple workers receive the same message or during worker recovery.

The lock is automatically released after processing or expiration.

---

# 9. Queue Statistics

Redis maintains lightweight runtime metrics.

Example

```text
queue:communication
```

Stored Information

* Pending Jobs
* Active Consumers
* Processing Rate

These values support monitoring dashboards without querying PostgreSQL.

---

# 10. Rate Limiting

Redis enables API rate limiting.

Example

```text
ratelimit:user:12345
```

Stored Value

```text
57
```

Meaning

The user has submitted 57 requests within the configured time window.

---

# 11. AI Cache

AI responses are cached to reduce repeated requests to the LLM.

Example

```text
ai:analysis:job:7d12b34c
```

Cached Value

```json
{
  "rootCause": "SMTP Authentication Failed",
  "confidence": 0.96,
  "suggestion": "Verify SMTP credentials."
}
```

Benefits

* Faster responses
* Lower AI cost
* Reduced latency

---

# 12. ML Cache

ML predictions are also cached.

Example

```text
ml:prediction:job:7d12b34c
```

Example Value

```json
{
  "failureProbability": 0.12,
  "recommendedWorker": "worker-03",
  "estimatedDuration": 4
}
```

Purpose

Avoid repeated prediction requests for the same job.

---

# 13. Redis Key Naming Convention

QueueFlow follows a consistent key naming strategy.

Examples

```text
worker:communication:worker-01

worker:communication:worker-01:heartbeat

worker:communication:worker-01:load

lock:job:uuid

queue:communication

ratelimit:user:123

ai:analysis:job:uuid

ml:prediction:job:uuid
```

Consistent naming improves maintainability and debugging.

---

# 14. TTL Strategy

Temporary Redis data should expire automatically.

| Data               | TTL                                     |
| ------------------ | --------------------------------------- |
| Worker Heartbeat   | 30 seconds                              |
| Job Lock           | Until processing completes (or timeout) |
| Rate Limit Counter | Configurable (e.g., 1 minute)           |
| AI Cache           | Configurable                            |
| ML Cache           | Configurable                            |

Persistent business data is **never** stored only in Redis.

---

# 15. Redis Data Structures

QueueFlow uses different Redis structures depending on the use case.

| Structure  | Use Case          |
| ---------- | ----------------- |
| String     | Locks, Counters   |
| Hash       | Worker Registry   |
| Set        | Active Worker IDs |
| Sorted Set | Future Scheduling |
| List       | Future Features   |

The simplest appropriate structure should always be preferred.

---

# 16. Failure Handling

If Redis becomes unavailable:

* PostgreSQL continues storing business data.
* RabbitMQ continues delivering messages.
* Workers continue processing jobs.

The platform may temporarily lose:

* Live worker status
* Distributed locks
* Cached AI responses
* Cached ML predictions

Business data remains safe.

---

# 17. Security

Redis should be configured using:

* Password Authentication
* Environment Variables
* Private Network Access
* No Public Exposure

Sensitive runtime information should never be publicly accessible.

---

# 18. Future Enhancements

Future Redis capabilities may include:

* Distributed Scheduler
* Pub/Sub Notifications
* Session Storage
* Distributed Counters
* Leader Election
* Sliding Window Rate Limiting

These enhancements are intentionally excluded from Version 1.

---

# 19. Design Decisions

QueueFlow follows these Redis design principles:

* Redis stores runtime state, not business state.
* PostgreSQL remains the source of truth.
* Temporary data should expire automatically.
* Keys follow a consistent naming convention.
* Redis failures must not cause business data loss.

---

# Summary

Redis provides QueueFlow with a high-performance operational layer that supports worker coordination, distributed locking, caching, monitoring, and runtime metrics.

By separating temporary operational data from persistent business data, QueueFlow achieves better performance, scalability, and maintainability while preserving data integrity in PostgreSQL.
