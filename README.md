# Red Coconut

A flexible file storage service with pluggable storage and authentication providers.

## Features

- **File Upload**: Support for small and large file uploads
- **Progress Tracking**: Track upload progress in real-time
- **Multiple Storage Backends**: 
  - Local filesystem storage
  - S3-compatible storage (AWS S3, MinIO, etc.)
- **Authentication Integration**:
  - OAuth2/OpenID Connect
  - AWS Cognito specific support

## Quick Start

### Prerequisites

- Java 21
- Maven 3.6+

### Running locally

1. Clone this repository
2. Configure environment variables (see Configuration section)
3. Run using Maven:

```bash
mvn spring-boot:run
```

Or build and run the JAR:

```bash
mvn clean package
java -jar target/red-coconut-0.0.1-SNAPSHOT.jar
```

### Docker

```bash
docker build -t red-coconut .
docker run -p 8080:8080 \
  -e STORAGE_PROVIDER=local \
  -e STORAGE_LOCATION=/data \
  -v ./uploads:/data \
  red-coconut
```

## Configuration

### Storage Configuration

| Environment Variable | Description | Default |
|---------------------|-------------|---------|
| `STORAGE_PROVIDER` | Storage provider to use (local, s3) | s3 |
| `STORAGE_LOCATION` | Storage location (bucket name or directory) | ./uploads |
| `STORAGE_USE_CHUNKED` | Whether to use chunked uploads | true |
| `STORAGE_CHUNK_THRESHOLD` | Size threshold for chunked uploads | 100MB |
| `STORAGE_CHUNK_SIZE` | Size of each chunk | 10MB |

#### S3-specific Configuration

| Environment Variable | Description |
|---------------------|-------------|
| `S3_REGION` | AWS region |
| `S3_ENDPOINT_URL` | S3 endpoint URL (for non-AWS S3) |

### Authentication Configuration

| Environment Variable | Description | Default |
|---------------------|-------------|---------|
| `AUTH_PROVIDER` | Authentication provider | oauth |
| `AUTH_CLIENT_ID` | OAuth client ID | |
| `AUTH_CLIENT_SECRET` | OAuth client secret | |
| `AUTH_ISSUER_URI` | OAuth issuer URI | |
| `AUTH_USER_IDENTIFIER_FIELD` | User identifier attribute | email |

## API Documentation

API documentation is available at `/swagger-ui.html` when running the application.

### Main Endpoints

- **POST** `/api/files/upload` - Upload a file
- **GET** `/api/files/status/{uploadId}` - Get upload status
- **GET** `/api/files/download/{filePath}` - Download a file
- **GET** `/api/users/me` - Get current user info
- **GET** `/api/health` - Service health check

## Development

### Project Structure

```
com.st4s1k.red_coconut
├── controller/        # HTTP API controllers
├── service/           # Service interfaces
│   ├── auth/          # Authentication services
│   └── storage/       # Storage services
├── impl/              # Service implementations
│   ├── auth/          # Auth providers (Cognito, etc.)
│   └── storage/       # Storage providers (S3, Local, etc.)
└── util/              # Utility classes
```

### Adding a New Storage Provider

Implement the `StorageService` interface and register with Spring using conditional configuration.

See [Implementation Guide](docs/usage.md) for details.

## License

[License information]
