# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

NextRoom is an Android application for escape room enthusiasts. It's built using Clean Architecture with a multi-module structure using Kotlin and modern Android development practices.

## Module Architecture

The project follows Clean Architecture with five modules:

### Module Dependencies
```
app → presentation → domain ← data
       ↓              ↑         ↓
   commonutil ←──────┴─────────┘
```

- **domain**: Pure Kotlin module (Java library) containing business logic
  - `model/`: Domain entities
  - `repository/`: Repository interfaces
  - `usecase/`: Use case classes
  - `request/`: Request models
  - `util/`: Domain utilities

- **data**: Data layer implementing repository interfaces
  - `datasource/`: Data source implementations (Local/Remote)
  - `repository/`: Repository implementations
  - `network/`: API service, interceptors, authenticator
  - `db/`: Room database
  - `di/`: Hilt modules for data layer
  - Build variants: debug (with Flipper) and release

- **presentation**: UI layer with View Binding and Navigation Component
  - `ui/`: Feature-based UI organization (login, main, hint, theme_select, etc.)
  - `base/`: Base classes (BaseViewModel, BaseFragment, BaseViewModelFragment, NewBaseFragment)
  - `common/`: Shared UI components
  - `customview/`: Custom views
  - `extension/`: Extension functions
  - `model/`: Presentation models
  - **State Management**: Gradually migrating away from Orbit MVI (legacy)

- **app**: Application module
  - `di/`: Application-level Hilt modules
  - `NextRoomApplication`: Application class with Timber and Flipper initialization (debug only)

- **commonutil**: Shared utilities (e.g., DateTimeUtil)

## Build Commands

### Standard Build Commands
```bash
# Clean project
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release AAB (App Bundle)
./gradlew bundleRelease

# Run lint checks (uses lint-baseline.xml in app module)
./gradlew lint

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

### Module-Specific Commands
```bash
# Build specific module
./gradlew :presentation:assembleDebug
./gradlew :data:assembleDebug

# Test specific module
./gradlew :domain:test
./gradlew :data:testDebugUnitTest
```

## Configuration Requirements

### local.properties
The project requires `local.properties` in the root directory with these keys:
```properties
DEV_BASE_URL="https://..."
PROD_BASE_URL="https://..."
key_alias=...
key_password=...
store_password=...
o_auth_web_client_id="..."
```

### Signing
- Release signing uses `nextroom_key` keystore in the root directory
- Credentials are read from `local.properties` via `gradleLocalProperties()`

## Technology Stack

### Core Technologies
- **Language**: Kotlin 1.9.10, JVM target 17
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 35
- **Compile SDK**: 34

### Key Libraries
- **DI**: Hilt 2.48 with KAPT
- **Networking**: Retrofit 2.9.0, OkHttp logging interceptor
- **State Management**: Orbit MVI 6.0.0 (LEGACY - being phased out)
- **Navigation**: Navigation Component 2.7.4 with SafeArgs
- **Async**: Coroutines 1.7.3
- **Database**: Room 2.5.2 with KSP for annotation processing
- **Image Loading**: Glide 4.14.2
- **Logging**: Timber 5.0.1
- **UI**: View Binding, Material Design, Lottie 6.1.0
- **Firebase**: BOM 32.3.1 (Analytics, Crashlytics, Remote Config)
- **Billing**: Play Billing 8.0.0
- **Auth**: Credentials API, Google Identity
- **Serialization**: Kotlinx Serialization JSON 1.5.1
- **Debugging (debug only)**: Flipper 0.200.0, LeakCanary 2.14

Note: Project uses both KAPT (for Hilt, Glide) and KSP (for Room) for annotation processing.

### Architecture Patterns

- **MVI**: Orbit for unidirectional data flow (LEGACY - being phased out)
- **Repository Pattern**: Domain defines interfaces, data implements them
- **Use Cases**: Single responsibility business logic
- **Custom Result Type**: Network calls return `Result<T>` using custom `ResultCallAdapter`

## State Management with Orbit MVI (LEGACY)

**⚠️ DEPRECATION NOTICE**: Orbit MVI is being gradually phased out from this project. When working
on new features or refactoring existing code, prefer alternative state management approaches instead
of Orbit.

### Legacy Orbit Pattern (Still in use, but avoid for new features)

ViewModels extend `BaseViewModel<STATE, SIDE_EFFECT>` which implements:
- `ContainerHost` from Orbit
- Error handling via `baseViewModelScope` (includes CoroutineExceptionHandler)
- State and side effects pattern

Example ViewModel structure:
```kotlin
@HiltViewModel
class SomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SomeRepository
) : BaseViewModel<SomeState, SomeEvent>() {

    override val container: Container<SomeState, SomeEvent> =
        container(SomeState())
}
```

### Migration Strategy

- **For new features**: Do NOT use Orbit MVI
- **For existing features**: Gradually remove Orbit when refactoring
- **When in doubt**: Ask the user for guidance on state management approach

## Fragment Base Classes

- **NewBaseFragment**: Basic fragment with View Binding
- **BaseViewModelFragment**: Extends NewBaseFragment, adds ViewModel with automatic error handling
- **BaseFragment**: Legacy base class

Fragments use `repeatOnStarted` extension for lifecycle-aware flow collection.

## Network Layer

### API Service
- Single `ApiService` interface with all endpoints
- Returns `Result<T>` wrapper for success/error handling
- Base URLs configured per build variant in `BuildConfig`

### Authentication
- `AuthInterceptor`: Adds access token to requests
- `AuthAuthenticator`: Handles token refresh on 401 responses
- Tokens stored via `TokenDataSource`

### Custom Result Type
Uses `ResultCallAdapter` to wrap responses in `Result<T>` sealed interface defined in domain layer.

**Result types:**
- `Result.Success<T>`: Successful response with data
- `Result.Failure.HttpError`: HTTP errors with specific subtypes
  - `BadRequest` (400)
  - `Forbidden` (403)
  - `NotFound` (404)
  - `Conflict` (409)
  - `ServerError` (500)
- `Result.Failure.LocalError.IOError`: I/O errors
- `Result.Failure.NetworkError`: Network connectivity errors
- `Result.Failure.UnknownError`: Unexpected errors
- `Result.Failure.OperationError`: Operation/transformation errors

**Extension functions for Result handling:**
```kotlin
result
    .onSuccess { data -> /* handle success */ }
    .onFailure { error -> /* handle failure */ }
    .mapOnSuccess { data -> /* transform data */ }
```

Also available: `suspendOnSuccess`, `suspendMapOnSuccess`, `concatMap` for combining results.

## Build Variants

### Debug
- Application ID suffix: `.debug`
- Flipper integration enabled
- Timber debug logging
- Points to DEV_BASE_URL

### Release
- Signing config from keystore
- ProGuard disabled (minifyEnabled = false)
- Points to PROD_BASE_URL

## CI/CD (GitHub Actions)

**Workflow**: `.github/workflows/android.yml`

### Triggers
- **Push**: `develop`, `release/*` branches
- **Pull Request**: `feature/*` branches

### Jobs
- Lint checks on all builds
- **Feature branches**: Build debug APK → Upload to Firebase App Distribution → Notify Slack
- **Release branches**: Build release AAB → Publish to Play Store (internal track, draft status)

## Version Management

Versions are centralized in `gradle/libs.versions.toml`:
- `versionCode`: Currently 68
- `versionName`: Currently "1.4.7"

Update these values when releasing new versions.

## Data Storage

- **Room Database**: Local caching and persistence
- **DataStore**: User preferences and settings (via DataStoreRepository)
- **Firebase Remote Config**: Feature flags and remote configuration

## Common Development Patterns

### Creating a New Feature
1. Add domain models in `domain/model/`
2. Define repository interface in `domain/repository/`
3. Create use case in `domain/usecase/` if needed
4. Implement repository in `data/repository/`
5. Add data source in `data/datasource/`
6. Create UI in `presentation/ui/[feature]/` with Fragment, ViewModel, State, Event
7. Add navigation actions in `presentation/res/navigation/nav_graph.xml`

### Adding a New API Endpoint
1. Add endpoint method to `data/network/ApiService.kt`
2. Create request/response DTOs in `data/network/request/` or `data/network/response/`
3. Create domain model in `domain/model/`
4. Add mapping extension in data layer
5. Update data source and repository

### Debugging
- **Debug builds**: Use Flipper for network inspection, database viewing, and layout inspection
- **Logging**: Use Timber (only logs in debug builds)
- **Crash tracking**: Firebase Crashlytics (all builds)
- **Memory leaks**: LeakCanary (debug builds only)

## Navigation

Uses Navigation Component with SafeArgs:
- **Main navigation graph**: `presentation/res/navigation/nav_graph.xml`
- **Nested graphs**: `game_navigation.xml`, `mypage_navigation.xml`, `background_custom_navigation.xml`
- Arguments are type-safe via generated `*Args` and `*Directions` classes
- **Always use `safeNavigate()` extension** instead of `navigate()` to prevent crashes from multiple rapid clicks

The `safeNavigate()` extension checks if the current destination has the requested action before navigating, preventing IllegalArgumentException crashes.

## Dependency Injection

Hilt modules are organized by layer:
- **app/di/**: `BillingModule`
- **data/di/**: `NetworkModule`, `RepositoryModule`, `LocalDataModule`, `FirebaseModule`, `CoroutineModule`

Follow existing patterns when adding new dependencies.

## Git Workflow

The project follows a Git Flow-based branching strategy:

### Branch Structure
- **main**: Production-ready code
- **develop**: Integration branch for ongoing development
- **feature/\***: Feature branches (e.g., `feature/NR-122`)
- **release/\***: Release preparation branches

### Development Flow
1. Create feature branches from `develop`
2. Open PR from `feature/*` to `main` (triggers CI: lint, build debug APK, Firebase App Distribution)
3. Merge to `develop` for integration
4. Create `release/*` branch when ready for release
5. Push to `release/*` triggers release build (AAB to Play Store internal track as draft)

### Current Branch
You are on: `feature/NR-122`

## User Notifications

**IMPORTANT**: Always send notifications to the user using `terminal-notifier` in the following
scenarios:

### 1. Task Completion

After completing ANY task, send a notification:

```bash
terminal-notifier -title "Claude Code" -message "[Task description]" -sound default
```

Examples:

- File creation:
  `terminal-notifier -title "Claude Code" -message "Created Color.kt and Type.kt files" -sound default`
- Build completion:
  `terminal-notifier -title "Claude Code" -message "Build completed successfully" -sound default`
- Error fixes:
  `terminal-notifier -title "Claude Code" -message "Fixed 5 type errors" -sound default`

### 2. User Input Required

When asking the user a question (using AskUserQuestion tool or otherwise), send a notification
BEFORE asking:

```bash
terminal-notifier -title "Claude Code" -message "질문이 있습니다" -sound default
```

This applies to:

- Clarification questions about implementation approaches
- Choices between multiple options
- Requests for additional information
- **Permission to modify files**: When asking which files can be modified or if a specific file can
  be edited
- Any situation requiring user input to proceed

The notification helps the user know when their attention is needed.
