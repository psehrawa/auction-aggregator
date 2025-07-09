# Auction Aggregator Platform

A comprehensive full-stack auction aggregation platform that brings together multiple auction sources into a unified marketplace with real-time bidding, advanced analytics, and secure payment processing.

## Architecture Overview

This project is built as a microservices architecture with the following components:

### Backend Services (Java Spring Boot)
- **auction-service**: Core auction management and bidding engine
- **user-service**: Authentication, user profiles, and KYC management
- **payment-service**: Multi-gateway payment processing
- **notification-service**: Real-time notifications via WebSocket, Email, SMS
- **analytics-service**: Price analytics, trends, and reporting
- **api-gateway**: Central routing, authentication, and rate limiting

### Frontend (React + TypeScript)
- Modern React 18 application with TypeScript
- Redux Toolkit for state management
- Material-UI components
- Real-time WebSocket integration
- Progressive Web App capabilities

### Infrastructure
- PostgreSQL for primary data storage
- Elasticsearch for search functionality
- Redis for caching and session management
- RabbitMQ for asynchronous message processing
- Docker containers for deployment

## Features

### Core Auction Features
- Multi-source auction aggregation (GeM, Copart, etc.)
- Real-time bidding with WebSocket updates
- Proxy bidding and bid automation
- Advanced search with filters
- Price analytics and trends
- Auction watchlists and alerts

### User Management
- JWT-based authentication
- OAuth 2.0 social login
- KYC verification system
- Role-based access control
- User profiles and bidding history

### Payment Processing
- Multiple payment gateways (UPI, Stripe, PayPal)
- Secure payment handling (PCI-DSS compliant)
- Transaction tracking and receipts
- Refund management
- Escrow services

### Real-time Features
- Live auction updates
- Push notifications
- Real-time chat during auctions
- Live auction streaming
- Instant bid confirmations

## Getting Started

### Prerequisites
- Java 17+
- Node.js 18+
- Docker and Docker Compose
- PostgreSQL 14+
- Redis 7+
- Elasticsearch 8+

### Quick Start

1. Clone the repository:
```bash
git clone https://github.com/yourusername/auction-aggregator.git
cd auction-aggregator
```

2. Set up environment variables:
```bash
cp .env.example .env
# Edit .env with your configuration
```

3. Start infrastructure services:
```bash
docker-compose up -d postgres redis elasticsearch rabbitmq
```

4. Run backend services:
```bash
cd backend
./gradlew bootRun
```

5. Start frontend development server:
```bash
cd frontend
npm install
npm run dev
```

## Development

### Backend Development
Each microservice is a separate Spring Boot application with its own database schema. Services communicate via REST APIs and RabbitMQ for asynchronous operations.

### Frontend Development
The React application uses Vite for fast development builds and hot module replacement. Components are built with TypeScript for type safety.

### Testing
- Backend: JUnit 5, Mockito, Testcontainers
- Frontend: Jest, React Testing Library, Cypress
- API: Postman collections provided

## Deployment

### Docker Deployment
```bash
docker-compose -f docker-compose.prod.yml up -d
```

### Kubernetes Deployment
Helm charts are provided in the `k8s/` directory for Kubernetes deployment.

## API Documentation

API documentation is available via Swagger UI:
- Development: http://localhost:8090/swagger-ui.html
- Production: https://api.yourdomain.com/swagger-ui.html

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support and questions:
- Documentation: [docs/](docs/)
- Issues: GitHub Issues
- Email: support@auctionaggregator.com