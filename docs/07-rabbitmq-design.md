# QueueForge - RabbitMQ Design

## Version

**1.0**

---

# 1. Purpose

This document defines the RabbitMQ architecture used in QueueForge.

It describes:

* Exchanges
* Queues
* Routing Keys
* Producers
* Consumers
* Retry Mechanism
* Dead Letter Queue (DLQ)
* Message Lifecycle
* Message Format

The goal is to ensure reliable, asynchronous, and fault-tolerant communication between services.

---

# 2. Why RabbitMQ?

QueueForge performs long-running operations that should not block client requests.

RabbitMQ provides:

* Asynchronous communication
* Reliable message delivery
* Decoupled services
* Load balancing
* Retry support
* Dead Letter Queue support
* Horizontal worker scaling

---

# 3. Architecture Overview

```text
                    Job Service
                         │
                         ▼
                  RabbitMQ Exchange
                         │
     ┌────────────┬────────────┬────────────┬────────────┬────────────┐
     ▼            ▼            ▼            ▼            ▼
Communication   Document     Media       Cloud        AI
    Queue         Queue       Queue       Queue       Queue
     │             │            │            │            │
     ▼             ▼            ▼            ▼            ▼
Communication  Document     Media       Cloud       AI Worker
   Worker        Worker      Worker      Worker
```

---

# 4. Exchange Design

QueueForge uses a single Direct Exchange.

Exchange Name

```text
QueueForge.exchange
```

Type

```text
Direct Exchange
```

Reason:

Each job already knows its destination.

A Direct Exchange provides:

* Fast routing
* Simple configuration
* Predictable behavior

---

# 5. Queues

The platform contains the following primary queues.

| Queue               | Purpose            |
| ------------------- | ------------------ |
| communication.queue | Communication jobs |
| document.queue      | Document jobs      |
| media.queue         | Media jobs         |
| cloud.queue         | Cloud operations   |
| ai.queue            | AI processing      |

Each queue has a dedicated worker group.

---

# 6. Routing Keys

Routing Keys determine where messages are delivered.

Examples:

```text
communication.email

communication.sms

communication.push

document.pdf

document.csv

document.report

media.image

media.video

media.audio

cloud.upload

cloud.backup

ai.ocr

ai.translation
```

The Job Service Routing Module selects the routing key before publishing the message.

---

# 7. Producers

Current Producer

* Job Service

Responsibilities

* Validate Job
* Save Job
* Publish Message

Future producers may include:

* Scheduler Service
* Workflow Service
* Notification Service

---

# 8. Consumers

Every Worker is a RabbitMQ Consumer.

Current Consumers

* Communication Worker
* Document Worker
* Media Worker
* Cloud Worker
* AI Worker

Each consumer listens only to its assigned queue.

---

# 9. Retry Queues

Each domain maintains its own Retry Queue.

Examples

```text
communication.retry.queue

document.retry.queue

media.retry.queue

cloud.retry.queue

ai.retry.queue
```

Reasons:

* Different retry policies
* Independent tuning
* Better fault isolation

---

# 10. Dead Letter Queues

Each domain also maintains its own DLQ.

Examples

```text
communication.dlq

document.dlq

media.dlq

cloud.dlq

ai.dlq
```

A job is moved here after exceeding the maximum retry count.

---

# 11. Message Lifecycle

A message follows this lifecycle.

```text
Client

↓

Job Service

↓

RabbitMQ Exchange

↓

Queue

↓

Worker

↓

Processing

↓

Success
   │
   ▼
ACK

OR

Failure
   │
   ▼
Retry Queue

↓

Retry

↓

Failure Again

↓

Dead Letter Queue
```

---

# 12. Acknowledgement Strategy

QueueForge uses Manual Acknowledgements.

Flow

```text
Receive Message

↓

Process Job

↓

Success

↓

ACK
```

If processing fails before ACK:

RabbitMQ automatically requeues the message.

This prevents message loss.

---

# 13. Message Structure

Every RabbitMQ message follows a common format.

```json
{
  "jobId": "uuid",
  "jobType": "EMAIL",
  "category": "COMMUNICATION",
  "priority": "HIGH",
  "payload": {},
  "retryCount": 0,
  "createdAt": "timestamp",
  "traceId": "uuid"
}
```

Benefits

* Standardized communication
* Easier debugging
* Better monitoring
* Future extensibility

---

# 14. Retry Flow

```text
Worker

↓

Exception

↓

Retry Count Check

↓

Retry Queue

↓

Worker

↓

Success

OR

↓

DLQ
```

Retry count is maintained inside PostgreSQL.

---

# 15. Dead Letter Flow

Jobs move to the Dead Letter Queue when:

* Retry limit exceeded
* Permanent business failure
* Invalid payload
* Unsupported operation

DLQ jobs remain available for manual inspection and retry.

---

# 16. Error Handling

Recoverable Errors

Examples

* SMTP Timeout
* Network Failure
* Temporary API Failure

Action

Retry

---

Non-Recoverable Errors

Examples

* Invalid Payload
* Unsupported Job Type
* Corrupted Data

Action

Dead Letter Queue

---

# 17. Monitoring

RabbitMQ metrics include:

* Queue Length
* Consumer Count
* Message Rate
* Acknowledgement Rate
* Retry Count
* Dead Letter Count

These metrics are collected using Prometheus.

---

# 18. Security

RabbitMQ communication shall use:

* Username & Password Authentication
* Virtual Hosts
* Least Privilege Permissions
* Environment Variables

Credentials must never be hardcoded.

---

# 19. Future Enhancements

Future versions may introduce:

* Topic Exchange
* Delayed Message Exchange
* Priority Queues
* Publisher Confirms
* Transactional Outbox Pattern
* Event Bus
* Message Compression

---

# 20. Design Decisions

QueueForge adopts the following messaging principles:

* Direct Exchange
* Domain-Based Queues
* Manual Acknowledgements
* Dedicated Retry Queues
* Dedicated Dead Letter Queues
* Standardized Message Format
* Event-Driven Communication
* Loose Coupling

---

# Summary

RabbitMQ serves as the messaging backbone of QueueForge.

It enables asynchronous communication between the Job Service and specialized workers while ensuring reliable delivery, fault tolerance, retry handling, and scalable message processing.

This design allows QueueForge to process background tasks efficiently without blocking client requests and forms the foundation of the platform's distributed architecture.
