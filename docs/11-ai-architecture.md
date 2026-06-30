# QueueFlow - AI Architecture

## Version

**2.0**

---

# 1. Purpose

This document defines the Artificial Intelligence (AI) architecture of QueueFlow.

It describes how AI enhances the platform by providing intelligent operational assistance, automated failure analysis, and natural language interaction.

The AI Service operates independently from the core job-processing pipeline and is designed to improve operational efficiency without becoming a dependency for the platform.

---

# 2. AI Overview

The AI Service is an intelligent operational assistant for QueueFlow.

Unlike workers, which execute business jobs, the AI Service analyzes system behavior and assists operators by providing explanations, recommendations, and natural language capabilities.

The AI Service does not directly execute background jobs or modify production data.

---

# 3. Objectives

The primary objectives of the AI Service are:

* Reduce troubleshooting time.
* Improve operational visibility.
* Explain system failures.
* Simplify job creation.
* Assist developers and operators.
* Provide intelligent recommendations.

---

# 4. High-Level Architecture

```text
                    QueueFlow Platform
                           │
      ┌────────────────────┼────────────────────┐
      │                    │                    │
 PostgreSQL             Redis             Prometheus
      │                    │                    │
      └────────────────────┼────────────────────┘
                           │
                    Context Builder
                           │
                    Prompt Builder
                           │
                     AI Service API
                           │
                    LLM Provider Layer
                           │
                  Gemini / OpenAI / Future LLM
```

The AI Service collects operational data from QueueFlow, prepares contextual prompts, communicates with an LLM provider, and returns meaningful insights to users.

---

# 5. Responsibilities

The AI Service is responsible for:

* Failure Analysis
* Root Cause Analysis
* Operations Chatbot
* Natural Language Job Creation
* Recommendation Generation
* AI Response Caching

The AI Service is **not responsible** for:

* Processing RabbitMQ messages
* Executing business jobs
* Managing workers
* Updating job status
* Performing scheduling

---

# 6. Internal Architecture

The AI Service consists of the following modules.

```text
controller/
service/
context/
prompt/
provider/
parser/
cache/
config/
exception/
util/
```

Each module follows the Single Responsibility Principle.

---

# 7. AI Modules

## 7.1 Context Builder

Collects information required for AI processing.

Possible sources include:

* Job Details
* Job History
* Retry History
* Queue Statistics
* Worker Status
* System Metrics
* Error Logs

Only relevant information is collected.

---

## 7.2 Prompt Builder

Transforms structured system information into optimized prompts for the LLM.

Responsibilities:

* Organize context
* Reduce unnecessary information
* Improve prompt consistency
* Generate reusable prompt templates

---

## 7.3 Provider Layer

Acts as an abstraction between QueueFlow and external AI providers.

Supported providers may include:

* Gemini
* OpenAI
* Azure OpenAI

Future versions may support locally hosted models.

Changing the AI provider should require minimal application changes.

---

## 7.4 Response Parser

Converts AI responses into structured objects that QueueFlow can understand.

Responsibilities:

* Parse responses
* Validate content
* Handle malformed output
* Generate standard API responses

---

## 7.5 Cache Manager

Stores AI responses inside Redis.

Benefits include:

* Faster response times
* Lower API costs
* Reduced latency
* Reduced duplicate requests

---

# 8. AI Features

## Failure Analysis

When a job repeatedly fails, the AI Service analyzes:

* Job Type
* Failure Reason
* Retry History
* Worker Information
* System State

The AI generates:

* Root Cause
* Confidence Score
* Suggested Resolution

---

## Operations Chatbot

Operators may ask operational questions using natural language.

Examples:

* Why are email jobs failing?
* Which queue is overloaded?
* Which worker is unhealthy?
* Show failed jobs today.
* Which worker processed the most jobs?

The chatbot retrieves relevant context before generating a response.

---

## Natural Language Job Creation

Users can describe a task in plain English.

Example:

> Send a welcome email to [aman@example.com](mailto:aman@example.com).

The AI converts the request into a structured job definition that is validated by the Job Service before processing.

---

# 9. Request Flow

```text
User Request

↓

AI Controller

↓

Context Builder

↓

Prompt Builder

↓

LLM Provider

↓

Response Parser

↓

Redis Cache

↓

API Response
```

Every AI request follows this standardized pipeline.

---

# 10. Context Sources

The AI Service gathers information from multiple components.

| Source           | Purpose              |
| ---------------- | -------------------- |
| PostgreSQL       | Job Data, History    |
| Redis            | Runtime State, Cache |
| Prometheus       | Metrics              |
| Worker Logs      | Failure Context      |
| Queue Statistics | Queue Health         |

This contextual information improves AI accuracy.

---

# 11. Error Handling

If the AI provider is unavailable:

* Return a meaningful error message.
* Log the incident.
* Continue normal QueueFlow operations.

AI failures must never interrupt job processing.

---

# 12. Security

The AI Service must never expose sensitive information.

Sensitive information includes:

* Passwords
* JWT Secrets
* API Keys
* Database Credentials
* Internal Tokens

Sensitive fields should be masked before sending data to the AI provider.

---

# 13. Monitoring

The AI Service exposes operational metrics.

Examples:

* Total Requests
* Successful Responses
* Failed Requests
* Average Response Time
* Cache Hit Ratio
* AI Provider Latency

These metrics are collected using Prometheus.

---

# 14. Performance

To improve performance:

* Cache repeated responses.
* Minimize prompt size.
* Use asynchronous processing where appropriate.
* Reuse context when possible.

The AI Service should not become a performance bottleneck.

---

# 15. Future Enhancements

Future AI capabilities may include:

* Incident Report Generation
* Automatic Log Summarization
* System Health Recommendations
* Deployment Recommendations
* Documentation Generation
* Capacity Planning Assistance
* Intelligent Queue Optimization

---

# 16. Design Principles

The AI Service follows these principles:

* AI assists, not controls.
* Context before prompting.
* Provider independence.
* Fail-safe architecture.
* Reusable prompt templates.
* Response validation.
* Secure data handling.

---

# 17. Design Decisions

The following architectural decisions were made:

* AI is implemented as a separate service.
* AI communicates through REST APIs.
* AI never consumes RabbitMQ jobs.
* Redis is used for AI response caching.
* Context is prepared before interacting with the LLM.
* Providers are abstracted behind a common interface.
* AI failures must not impact QueueFlow operations.

---

# Summary

The AI Service provides intelligent operational assistance for QueueFlow by combining contextual system information with Large Language Models.

Rather than executing business logic, the AI Service focuses on explaining failures, answering operational questions, assisting with job creation, and improving developer productivity.

Its modular architecture, provider abstraction, secure context handling, and caching strategy ensure that AI capabilities remain scalable, maintainable, and independent from the core distributed job-processing platform.
