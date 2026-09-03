# The Warehouse

Kotlin Multiplatform shopping app for The Warehouse. Shared Compose UI targets **Android** and **iOS**. You can search products, page through results, open product details, and see a Father’s Day featured row on the dashboard.

## How to run

The Gradle project lives in [`mobile/`](mobile/). Open that folder, not the repo root.

### Prerequisites

- JDK 21
- [Android Studio](https://developer.android.com/studio) (Android) and/or Xcode 16+ (iOS, deployment target 18.2)
- An Azure APIM subscription key (`OCP_APIM_SUBSCRIPTION_KEY`)

The key is required at **compile time**. Gradle writes it into a generated `GeneratedApiConfig` class. It is never committed.

Set it as an environment variable:

```bash
export OCP_APIM_SUBSCRIPTION_KEY="<your-key>"
```

Or add it to `~/.gradle/gradle.properties` so Android Studio and Xcode both pick it up:

```properties
OCP_APIM_SUBSCRIPTION_KEY=<your-key>
```

Without the key, any Gradle build fails with `OCP_APIM_SUBSCRIPTION_KEY is required`.

### Android

1. Open `mobile/` in Android Studio.
2. Wait for Gradle sync.
3. Run the `androidApp` configuration on an emulator or device (min SDK 24).

From the command line:

```bash
cd mobile
./gradlew :androidApp:installDebug
```

### iOS

1. Export `OCP_APIM_SUBSCRIPTION_KEY` (or put it in `~/.gradle/gradle.properties`) so the shared framework can compile.
2. Open [`mobile/iosApp/iosApp.xcodeproj`](mobile/iosApp/iosApp.xcodeproj) in Xcode.
3. Select a simulator or device and run.

### Tests

From `mobile/`:

```bash
./gradlew :shared:testAndroidHostTest
./gradlew ktlintCheck
```

iOS unit tests: `./gradlew :shared:iosSimulatorArm64Test`.

## Architecture

Shared UI and business logic live in `:shared`. Thin platform hosts start Koin and render `App()`.

```
androidApp / iosApp
        │
     App()  Compose Multiplatform + WarehouseTheme
        │
    AppNavHost
        ├── Dashboard          search + Father’s Day row
        ├── Product list       paginated search results
        └── Product details    images, price, stock, offers
                │
         ViewModels (Koin)
                │
         Repositories
                │
         Remote data sources  →  Ktor HttpClient
```

| Layer | Role |
|---|---|
| **UI** | Compose screens, ViewModels, type-safe Navigation Compose routes |
| **Domain** | Models only: `Product`, `SearchResult`, `ProductDetails`, `LoginSession` |
| **Data** | Repositories map API DTOs; Ktor talks to Azure APIM |
| **DI** | Koin modules (`dataModule`, `uiModule`) |

**No use-case layer.** ViewModels call repositories directly. This is a small app, so a domain/use-case layer would add extra code without much benefit. It is omitted on purpose.

### Screens

1. **Dashboard** — search field and a Father’s Day strip (search `"fathers day"`, limit 10).
2. **Product list** — results for the query, 20 per page, previous/next.
3. **Product details** — images, price, description, features, stock, promotions.

### Networking

All calls go to `https://legacy-apim.twg.co.nz/twgCSharpTest/`:

| Endpoint | Used for |
|---|---|
| `Login.json` | Guest token (`X-TWL-Token`) |
| `Search.json` | Dashboard featured row and product list (`Search`, `Start`, `Limit`) |
| `Product.json` | Details (`ProductId`) |

Every request sends `Authorization: Guest`, `X-TWL-Device` (`Android` or `iOS`), and `Ocp-Apim-Subscription-Key`. JSON unknown keys are ignored.

There are two Ktor clients:

- **Unauthenticated** — login only.
- **Authenticated** — search and product. A Ktor interceptor attaches `X-TWL-Token`, and on HTTP 401 it invalidates the session and retries once with a fresh token.

### Login

Login is implemented, but there is **no login screen**. The first search or product request lazily calls `Login.json`, caches the token in memory (`AuthLocalDataSource`), and refreshes it when it expires or a request returns 401.

**That token does not appear to be required.** Search and product succeed with the subscription key and `Authorization: Guest` alone. Login is still wired up so the client matches the documented API (token header, expiry, 401 retry), but it is not a user-facing gate and the rest of the app works without treating auth as mandatory.

## Assumptions

- **Guest-only.** No account, barcode scanner, or store-specific inventory. The dashboard greeting (“Hey Jane”) is placeholder copy.
- **Subscription key is the real gate.** The guest token is implemented for completeness; product APIs do not seem to need it.
- **In-memory session is enough.** The token is not persisted. A process restart logs in again on the next API call.
- **Search is keyword + offset pagination.** The API also returns sorts and facets; the UI does not use them.
- **Father’s Day is a hardcoded search**, not a dedicated merchandising endpoint.
- **Product HTML is stripped** to plain text (`<br>` becomes a newline, tags removed, common entities decoded).
- **Products without a name or id are dropped** from search results.
- **Prices are NZD** (`$15.00` style formatting).
- **Network is required.** Loading, empty, and error + retry are handled; there is no offline cache.

## Project layout

```
mobile/
  androidApp/     Android Application host
  iosApp/         SwiftUI host wrapping Compose
  shared/         Compose UI, ViewModels, data, domain, tests
```
