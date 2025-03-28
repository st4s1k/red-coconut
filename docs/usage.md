# Red Coconut Integration Guide

This guide explains how to integrate Red Coconut into your application and implement your own provider adapters.

## Project Structure

Red Coconut follows the Adapter design pattern with a clean separation between interfaces and implementations:

```
com.st4s1k.red_coconut
├── service/           # Core interfaces
│   ├── auth/
│   │   └── AuthenticationService.kt
│   ├── storage/
│   │   └── StorageService.kt
│   └── FileService.kt
└── impl/              # Reference implementations (for testing)
    ├── auth/
    │   └── CognitoAuthenticationService.kt
    └── storage/
        └── S3StorageService.kt
```

## Setup

### 1. Add Dependency

```xml
<dependency>
    <groupId>com.st4s1k</groupId>
    <artifactId>red-coconut</artifactId>
    <version>${red-coconut.version}</version>
</dependency>
```

### 2. Implement Provider Adapters

Create your own implementations of the core interfaces:

#### Example: Azure AD Authentication

```kotlin
@Service("azureAuthService")
class AzureAuthenticationService(
    @Value("\${AZURE_TENANT_ID}")
    private val tenantId: String,
    
    @Value("\${AZURE_CLIENT_ID}")
    private val clientId: String
) : AuthenticationService {
    override fun getLogoutUrl(redirectUri: String): String {
        return "https://login.microsoftonline.com/$tenantId/oauth2/v2.0/logout?client_id=$clientId&post_logout_redirect_uri=$redirectUri"
    }

    override fun getUserAttributes(principal: Any): Map<String, Any> {
        // Azure AD-specific implementation
    }
    
    override fun getUserIdentifier(principal: Any): String? {
        // Azure AD-specific implementation
    }
}
```

#### Example: Azure Blob Storage

```kotlin
@Service("azureBlobStorageService")
class AzureBlobStorageService(
    @Value("\${AZURE_STORAGE_CONNECTION_STRING}")
    private val connectionString: String,
    
    @Value("\${AZURE_STORAGE_CONTAINER_NAME}")
    private val containerName: String
) : StorageService {
    override fun uploadFile(
        file: MultipartFile, 
        path: String,
        progressCallback: ((Long, Long) -> Unit)?
    ): URI {
        // Azure Blob Storage-specific implementation
    }
}
```

### 3. Register Your Providers

Create a configuration class that extends or replaces the existing `ServiceFactory`:

```kotlin
@Configuration
class MyServiceFactory {
    @Value("\${app.auth.provider}")
    private lateinit var authProvider: String
    
    @Value("\${app.storage.provider}")
    private lateinit var storageProvider: String
    
    @Bean
    @Primary
    fun authenticationService(
        @Qualifier("cognitoAuthService") cognitoAuthService: AuthenticationService,
        @Qualifier("azureAuthService") azureAuthService: AuthenticationService
    ): AuthenticationService {
        return when (authProvider.lowercase()) {
            "cognito" -> cognitoAuthService
            "azure" -> azureAuthService
            else -> throw IllegalArgumentException("Unsupported auth provider: $authProvider")
        }
    }
    
    @Bean
    @Primary
    fun storageService(
        @Qualifier("s3StorageService") s3StorageService: StorageService,
        @Qualifier("azureBlobStorageService") azureBlobStorageService: StorageService
    ): StorageService {
        return when (storageProvider.lowercase()) {
            "s3" -> s3StorageService
            "azure" -> azureBlobStorageService
            else -> throw IllegalArgumentException("Unsupported storage provider: $storageProvider")
        }
    }
}
```

### 4. Configure Your Application

Update your `application.yml` to select the appropriate providers:

```yaml
app:
  auth:
    provider: azure  # Use your implementation
  storage:
    provider: azure  # Use your implementation
```

### 5. Required Environment Variables

For your custom providers, define and document the environment variables they need:

```
# Azure AD Authentication
AZURE_TENANT_ID=your-tenant-id
AZURE_CLIENT_ID=your-client-id

# Azure Blob Storage
AZURE_STORAGE_CONNECTION_STRING=your-connection-string
AZURE_STORAGE_CONTAINER_NAME=your-container-name
```

## Using the Service Layer

Once your adapters are implemented and registered, you can use the core services:

```kotlin
@RestController
class MyController(
    private val fileService: FileService,
    private val authService: AuthenticationService
) {
    @PostMapping("/upload")
    fun upload(
        @AuthenticationPrincipal principal: Principal,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<*> {
        val userIdentifier = authService.getUserIdentifier(principal)
        val fileUri = fileService.upload(file, userIdentifier)
        // ...
    }
}
```

## Testing

The Red Coconut library includes reference implementations for AWS Cognito and S3
