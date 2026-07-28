# QueueForge - Glossary

## Version

**1.0**

---

# Purpose

This glossary defines the common terms used throughout the QueueForge project.

It ensures that developers, testers, DevOps engineers, and contributors share the same understanding of the platform's architecture and terminology.

---

# A

## Acknowledgement (ACK)

A confirmation sent by a worker to RabbitMQ indicating that a message has been successfully processed.

Once a message is acknowledged, RabbitMQ removes it from the queue.

---

## API

Application Programming Interface.

The REST endpoints exposed by QueueForge for creating, managing, and monitoring jobs.

---

## Asynchronous Processing

A processing model where tasks execute independently of the user's request.

The client receives an immediate response while the task continues in the background.

---

# C

## Consumer

A service that receives and processes messages from RabbitMQ.

In QueueForge, all workers act as consumers.

---

## Communication Worker

A specialized worker responsible for communication-related tasks.

Examples:

* Email
* SMS
* Push Notifications
* WhatsApp

---

# D

## Dead Letter Queue (DLQ)

A queue that stores jobs which have permanently failed after exhausting all retry attempts.

These jobs require manual inspection or reprocessing.

---

## Direct Exchange

A RabbitMQ exchange that routes messages to queues using an exact routing key.

QueueForge uses a Direct Exchange for routing jobs to specialized queues.

---

## Document Worker

A worker responsible for document-related tasks.

Examples:

* PDF Generation
* Excel Generation
* CSV Generation
* Report Generation

---

# E

## Event

A record describing something that happened within the system.

Examples:

* Job Created
* Job Started
* Job Completed
* Job Failed

Events enable loose coupling between services.

---

## Exchange

A RabbitMQ component responsible for routing messages to one or more queues.

---

# H

## Heartbeat

A periodic signal sent by every worker indicating that it is alive and operational.

Heartbeats are stored in Redis.

---

# J

## Job

The smallest unit of work processed by QueueForge.

Examples:

* Send Email
* Generate PDF
* Resize Image
* Upload File

Each job has:

* Unique ID
* Type
* Priority
* Status
* Payload

---

## Job Payload

The data required to execute a job.

Example:

An email job payload may include:

* Recipient
* Subject
* Message

---

## Job Priority

Determines the execution importance of a job.

Possible values include:

* LOW
* MEDIUM
* HIGH

---

## Job Service

The central Spring Boot service responsible for:

* Job Management
* Validation
* Authentication
* Routing
* Publishing messages to RabbitMQ

---

# L

## Lock

A temporary reservation stored in Redis that prevents multiple workers from processing the same job simultaneously.

---

# M

## Media Worker

Processes media-related tasks.

Examples:

* Image Resize
* Image Compression
* Thumbnail Generation
* Video Processing
* Audio Processing

---

## Machine Learning (ML)

A component that predicts future system behavior based on historical data.

Examples:

* Failure Prediction
* Worker Recommendation
* Queue Forecasting
* Job Duration Prediction

---

## Monitoring

The process of observing system health and performance.

QueueForge uses:

* Prometheus
* Grafana

---

# P

## Producer

A service that publishes messages to RabbitMQ.

In QueueForge, the Job Service acts as the producer.

---

## PostgreSQL

The primary relational database used for persistent storage.

Stores:

* Jobs
* Job History
* Workers
* Retry History
* Dead Letter Jobs

---

# Q

## Queue

A RabbitMQ component that temporarily stores messages until they are processed by workers.

Each domain has its own queue.

Examples:

* communication.queue
* document.queue
* media.queue

---

## Queue Length

The number of pending jobs waiting inside a queue.

---

## QueueForge

An AI-powered distributed task processing platform designed for asynchronous background job execution.

---

# R

## RabbitMQ

The message broker responsible for reliable asynchronous communication between services and workers.

---

## Redis

An in-memory data store used for temporary and high-speed operations.

Examples:

* Worker Registry
* Heartbeats
* Distributed Locks
* Queue Statistics
* AI Cache
* ML Cache

---

## Retry

The process of attempting to execute a failed job again after a predefined delay.

---

## Retry Queue

A temporary queue that stores failed jobs waiting for another execution attempt.

---

## Routing Key

A value used by RabbitMQ to determine which queue should receive a message.

Example:

communication.email

---

# S

## Specialized Worker

A worker designed to process jobs belonging to a specific business domain.

Examples:

* Communication Worker
* Document Worker
* Media Worker

---

## Status

Represents the current lifecycle stage of a job.

Possible values:

* QUEUED
* PROCESSING
* COMPLETED
* FAILED
* RETRYING
* CANCELLED
* DLQ

---

# W

## Worker

A background service responsible for consuming and processing jobs from RabbitMQ.

Workers execute business logic independently of client requests.

---

## Worker Registry

A Redis-based registry that stores information about active workers.

Information includes:

* Worker ID
* Status
* Heartbeat
* Active Jobs
* Capabilities

---

## Worker Scaling

The process of increasing or decreasing the number of worker instances to handle varying workloads.

---

# AI

## Artificial Intelligence (AI)

A component that analyzes and explains system behavior rather than predicting it.

Current AI capabilities include:

* Failure Analysis
* Operations Chatbot
* Natural Language Job Creation

---

# Glossary Maintenance

This glossary should be updated whenever new architectural concepts, technologies, or platform components are introduced.

Keeping terminology consistent improves collaboration, documentation quality, and maintainability across the QueueForge project.
