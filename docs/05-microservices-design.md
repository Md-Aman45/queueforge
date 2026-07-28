# QueueForge - Microservices Design

## Version

**1.0**

---

# 1. Purpose

This document describes the microservices architecture of QueueForge.

It defines the responsibility, boundaries, communication, dependencies, and internal structure of each microservice.

The goal is to ensure that every service follows the **Single Responsibility Principle (SRP)** while remaining loosely coupled and independently deployable.

---

# 2. Microservices Overview

QueueForge consists of the following services:

| Service              | Responsibility                          |
| -------------------- | --------------------------------------- |
| Job Service          | API Gateway, Job Management, Routing    |
| Communication Worker | Email, SMS, Push Notification, WhatsApp |
| Document Worker      | PDF, Excel, CSV, Reports                |
| Media Worker         | Image, Video, Audio                     |
| Cloud Worker         | Upload, Backup, Restore                 |
| AI Worker            | OCR, Translation, Summarization         |
| AI Service           | AI-powered Analysis & Chat              |
| ML Service           | Machine Learning Predictions            |

---

# 3. Job Service

## Purpose

The Job Service is the entry point of QueueForge.

Every client request enters the platform through this service.

---

## Responsibilities

* REST API
* Authentication
* Authorization
* Input Validation
* Job Management
* Job Persistence
* Routing Module
* RabbitMQ Producer
* Status Tracking

---

## Internal Modules

```text
controller/
service/
repository/
entity/
dto/
mapper/
producer/
routing/
security/
config/
exception/
util/
```

---

## Dependencies

* PostgreSQL
* RabbitMQ
* Redis
* Shared Module

---

## Produces Events

* JobCreated
* JobQueued
* JobCancelled
* JobRetried

---

## Consumes Events

None

---

# 4. Communication Worker

## Purpose

Processes communication-related jobs.

---

## Supported Jobs

* Email
* SMS
* Push Notification
* WhatsApp

---

## Responsibilities

* RabbitMQ Consumer
* Job Execution
* Retry Handling
* Status Updates
* Metrics Collection
* Heartbeat

---

## Internal Modules

```text
consumer/
processor/
service/
config/
retry/
heartbeat/
metrics/
exception/
```

---

## Dependencies

* RabbitMQ
* PostgreSQL
* Redis
* Shared Module

---

## Consumes Events

* Communication Job Created

---

## Produces Events

* JobStarted
* JobCompleted
* JobFailed

---

# 5. Document Worker

## Supported Jobs

* PDF
* Excel
* CSV
* Report Generation

Responsibilities and structure are identical to Communication Worker.

---

# 6. Media Worker

## Supported Jobs

* Image Resize
* Image Compression
* Thumbnail Generation
* Video Processing
* Audio Processing

Responsibilities remain identical.

---

# 7. Cloud Worker

## Supported Jobs

* Upload
* Download
* Backup
* Restore

Responsibilities remain identical.

---

# 8. AI Worker

## Supported Jobs

* OCR
* Translation
* Summarization
* Image Captioning

Responsibilities remain identical.

---

# 9. AI Service

## Purpose

Provides intelligent assistance to operators and developers.

The AI Service is **not responsible for executing business jobs**.

---

## Responsibilities

* Failure Analysis
* Operations Chatbot
* Natural Language Job Creation

---

## Internal Modules

```text
controller/
service/
prompt/
llm/
parser/
cache/
config/
```

---

## Dependencies

* FastAPI
* LLM Provider
* Redis
* PostgreSQL

---

## Consumes Events

* JobFailed
* JobCompleted

---

## Produces Events

None

---

# 10. ML Service

## Purpose

Provides predictive analytics.

---

## Responsibilities

* Failure Prediction
* Worker Recommendation
* Queue Forecast
* Duration Prediction
* Retry Prediction
* Anomaly Detection

---

## Internal Modules

```text
controller/
model/
training/
prediction/
feature/
config/
cache/
```

---

## Dependencies

* Python
* FastAPI
* Scikit-Learn
* Redis
* PostgreSQL

---

## Consumes Events

* JobCompleted
* WorkerMetricsUpdated

---

## Produces Events

Prediction Results

---

# 11. Shared Module

## Purpose

Contains reusable components shared across all services.

---

## Contents

```text
dto/
events/
enums/
constants/
exceptions/
responses/
utils/
```

Examples:

* JobStatus
* WorkerStatus
* JobPriority
* JobType
* ApiResponse
* ErrorCode

---

# 12. Service Communication

QueueForge follows a hybrid communication model.

## 12.1 Synchronous Communication

Synchronous communication is used only when an immediate response is required.

### Examples

* Client → Job Service (REST API)
* Swagger/OpenAPI → Job Service
* Monitoring Dashboard → Metrics APIs

REST communication should remain minimal to reduce service coupling.

---

## 12.2 Asynchronous Communication

Most communication inside QueueForge is asynchronous using RabbitMQ.

Examples:

* Job Service → RabbitMQ
* RabbitMQ → Communication Worker
* RabbitMQ → Document Worker
* RabbitMQ → Media Worker
* RabbitMQ → Cloud Worker
* RabbitMQ → AI Worker

This enables:

* Loose coupling
* Scalability
* Fault tolerance
* Independent deployment

---

# 13. Event Flow

The following events are exchanged between services.

## Job Events

* JobCreated
* JobQueued
* JobStarted
* JobCompleted
* JobFailed
* JobRetried
* JobCancelled

---

## Worker Events

* WorkerRegistered
* WorkerOnline
* WorkerOffline
* WorkerHeartbeat
* WorkerBusy
* WorkerIdle

---

## Queue Events

* QueueCreated
* QueueBacklogDetected
* RetryScheduled
* DeadLetterCreated

---

## AI Events

* FailureAnalysisCompleted
* JobCreatedFromNaturalLanguage

---

## ML Events

* FailurePredictionGenerated
* WorkerRecommendationGenerated
* QueueForecastGenerated

---

# 14. Service Dependencies

| Service              | PostgreSQL | RabbitMQ | Redis | Shared Module |
| -------------------- | ---------: | -------: | ----: | ------------: |
| Job Service          |          ✅ |        ✅ |     ✅ |             ✅ |
| Communication Worker |          ✅ |        ✅ |     ✅ |             ✅ |
| Document Worker      |          ✅ |        ✅ |     ✅ |             ✅ |
| Media Worker         |          ✅ |        ✅ |     ✅ |             ✅ |
| Cloud Worker         |          ✅ |        ✅ |     ✅ |             ✅ |
| AI Worker            |          ✅ |        ✅ |     ✅ |             ✅ |
| AI Service           |          ✅ |        ❌ |     ✅ |             ❌ |
| ML Service           |          ✅ |        ❌ |     ✅ |             ❌ |

---

# 15. Deployment Strategy

Every service is packaged independently.

Each service contains:

* Spring Boot Application (Java Services)
* FastAPI Application (AI/ML Services)
* Dockerfile
* Environment Configuration
* Health Endpoint

Each service is deployed as its own Docker container.

This architecture allows services to be updated independently without affecting other components.

---

# 16. Scaling Strategy

Each service scales independently according to workload.

Examples:

### High Email Traffic

Scale:

* Communication Worker

No changes are required for:

* Document Worker
* Media Worker
* Cloud Worker

---

### Large Number of AI Requests

Scale:

* AI Service

Core job processing remains unaffected.

---

### Heavy PDF Generation

Scale:

* Document Worker

Only the required worker type is increased.

This minimizes infrastructure cost while improving throughput.

---

# 17. Fault Isolation

Every worker category operates independently.

Examples:

If the Document Worker becomes unavailable:

* PDF jobs stop temporarily.
* Email jobs continue.
* Image jobs continue.
* AI jobs continue.

Failures remain isolated to the affected domain.

---

# 18. Design Principles

Each microservice follows these principles.

## Single Responsibility

Every service performs one business function.

Examples:

Communication Worker

* Email
* SMS
* Push Notification

Document Worker

* PDF
* Excel
* CSV

---

## Loose Coupling

Services communicate through RabbitMQ instead of direct service-to-service calls.

---

## High Cohesion

Related business logic remains inside the same service.

---

## Independent Deployment

Every service can be built, tested, and deployed independently.

---

## Horizontal Scalability

Services can increase or decrease instances according to demand.

---

## Fault Tolerance

Failure in one service should not impact unrelated services.

---

# 19. Future Microservices

Future versions of QueueForge may introduce additional services.

Examples:

* Notification Service
* Scheduler Service
* Workflow Engine
* Audit Service
* Analytics Service
* API Gateway
* Service Discovery
* Configuration Server
* Identity Service
* Billing Service

These services are intentionally excluded from Version 1 to keep the initial platform focused and manageable.

---

# 20. Summary

QueueForge follows a modular microservices architecture where each service owns a single business responsibility.

The Job Service acts as the entry point for all requests, RabbitMQ enables asynchronous communication, specialized workers execute business logic, PostgreSQL provides persistent storage, Redis supports high-speed operational data, and AI/ML services deliver intelligent insights without impacting the core processing pipeline.

The architecture emphasizes:

* Scalability
* Reliability
* Maintainability
* Fault Tolerance
* Loose Coupling
* High Cohesion
* Independent Deployment
* Event-Driven Communication

This microservices design forms the foundation for implementing QueueForge as a production-grade distributed task processing platform.
