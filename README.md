Distributed Banking System

A production-style distributed banking platform built with Java, Spring Boot, Spring Cloud Gateway, Apache Kafka, PostgreSQL, Redis, Docker Compose, Prometheus, and Grafana.

This project demonstrates advanced backend engineering concepts including microservices architecture, event-driven communication, distributed transaction patterns, authentication and authorization, observability, and infrastructure-ready design.

Architecture Overview

The system is composed of independent microservices, each with its own database and bounded context.

Client
|
v
API Gateway
|
+--> Auth Service
+--> Customer Service
+--> Account Service
+--> Transaction Service
+--> Notification Service
+--> Audit Service


Infrastructure:
- PostgreSQL (database per service)
- Apache Kafka
- Redis
- MailHog
- Prometheus
- Grafana
  Microservices
  auth-service

Responsible for:

User registration
Login
JWT generation
Refresh token management
Publishing user.registered events
customer-service

Responsible for:

Automatic customer creation after user registration
Idempotent event consumption
account-service

Responsible for:

Account creation
Account activation and blocking
Balance management
Debit and credit operations
transaction-service

Responsible for:

Inter-account transfers
Transfer orchestration
Saga coordination
notification-service

Responsible for:

Email notifications
Event-driven messaging
audit-service

Responsible for:

Persisting critical audit events
System traceability
api-gateway

Responsible for:

Single entry point
JWT validation
Request routing
Centralized security
Technology Stack
Backend
Java 21
Spring Boot 3
Spring Security
Spring Data JPA
Spring Validation
Spring Cloud Gateway
Spring Kafka
Spring Actuator
Flyway
MapStruct
Lombok
JJWT
Databases and Messaging
PostgreSQL
Apache Kafka
Redis
Infrastructure and Observability
Docker
Docker Compose
Prometheus
Grafana
MailHog
Testing
JUnit 5
Spring Boot Test
Testcontainers
Spring Security Test
Implemented Architectural Patterns
Microservices Architecture
Event-Driven Architecture
API Gateway Pattern
Outbox Pattern
Saga Pattern
Idempotent Consumers
Correlation ID
Database per Service
Role-Based Access Control (RBAC)
Externalized Configuration
Security Features
JWT Access Tokens
Refresh Tokens
Role-based Authorization
Password hashing
Externalized secrets using environment variables
Main Functional Flow
User Registration
POST /auth/register
|
v
auth-service creates User
|
v
Publishes UserRegisteredEvent
|
v
customer-service consumes event
|
v
Creates Customer
|
v
audit-service stores audit record
|
v
notification-service sends welcome email
Bank Transfer
POST /transactions
|
v
transaction-service creates transfer
|
v
Debits source account
|
v
Credits destination account
|
v
Publishes TransferCompletedEvent
|
v
audit-service records event
|
v
notification-service sends notification
Event-Driven Communication
Published Events
user.registered
customer.created
account.created
account.activated
account.blocked
account.debited
account.credited
transfer.created
transfer.completed
transfer.failed
Observability

All microservices expose:

/actuator/health
/actuator/info
/actuator/prometheus

Prometheus scrapes metrics from all services. Grafana provides dashboards to visualize service availability and performance.

Monitored Services
api-gateway
auth-service
customer-service
account-service
transaction-service
notification-service
audit-service
Project Structure
banking-system/
├── api-gateway/
├── auth-service/
├── customer-service/
├── account-service/
├── transaction-service/
├── notification-service/
├── audit-service/
├── observability/
│   ├── prometheus/
│   └── grafana/
├── docker-compose.yml
├── .env.example
└── README.md
Running Locally
1. Clone the repository
   git clone https://github.com/your-username/distributed-banking-system.git
   cd distributed-banking-system
2. Create environment file
   cp .env.example .env
3. Start the platform
   docker compose up --build -d
4. Verify running services
   docker compose ps
   Access URLs
   Service	URL
   API Gateway	http://localhost:8080
   Swagger UI (Auth)	http://localhost:8082/swagger-ui.html
   Prometheus	http://localhost:9090
   Grafana	http://localhost:3000
   MailHog	http://localhost:8025
   Environment Variables

Example .env:

AUTH_JWT_SECRET=replace-with-a-long-secure-secret
Testing

Run all tests:

./gradlew test

Run a specific microservice tests:

cd auth-service/auth-service
./gradlew test
Docker Commands

Build all services:

docker compose build

Start all services:

docker compose up -d

Stop services:

docker compose down

Stop services and remove volumes:

docker compose down -v
Future Improvements
Terraform infrastructure provisioning
GitHub Actions CI/CD pipeline
JaCoCo code coverage reports
SonarCloud static analysis
OpenTelemetry + Jaeger distributed tracing
Resilience4j circuit breakers and retries
AWS ECS deployment
Skills Demonstrated
Java Backend Development
Spring Boot Ecosystem
Microservices Architecture
Event-Driven Systems
Distributed Transactions
Secure API Design
Docker and Containerization
Observability and Monitoring
Automated Testing
Infrastructure Readiness
Screenshots

Recommended screenshots to include:

Architecture diagram
Grafana dashboard
Prometheus targets page
Swagger documentation
MailHog emails
Successful transfer flow
Author

Percy — Systems Engineering Student focused on Backend Development, Data Engineering, and Software Architecture.

License

This project is for educational and portfolio purposes.