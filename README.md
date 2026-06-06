## About IKollect
**IKollect** is an application that allows you to keep track of your k-pop merch collection. 
Add your albums aтd photocards to the digital catalogue and access it anytime, anywhere.

## Core Functionality
- **Add your albums** to the catalogue by scanning barcodes on the back of the package. You can also
select a specific version of the album or add your own image of it.
- **Upload the photocards**. The photocards can be linked to an artist or the album you received
them from.
- **Sort your items by tags**, such as 'POB', 'Polaroid' and 'Fan-made'. If the system tag collection
does not suit your needs, you can always expand it by adding new ones.
- **Offline-first**. The application works perfectly fine without internet connection. However, you
can always sign in via your e-mail or Google account to access your collection from multiple devices.

## Supported Languages
- &#127468;&#127463; **English**
- &#127479;&#127482; **Russian**
- &#127472;&#127479;; **Korean** *(coming soon)*
- &#127471;&#127477; **Japanese** *(coming soon)*
- &#127464;&#127475; **Traditional Chinese** *(coming soon)*

## Stack
### 🏗 Architecture & Core
* **Clean Architecture & MVVM/MVI** — Strict separation of concerns and state-driven UI.
* **Dagger Hilt** — Dependency injection to ensure loose coupling and high testability.
* **Jetpack Navigation** — Type-safe, compile-time safe navigation for a single-activity architecture.
* **Jetpack DataStore** — Asynchronous key-value storage used for managing user preferences.

### 🎨 UI & Presentation
* **Jetpack Compose** — declarative UI toolkit for responsive layouts.
* **Material 3 & Dynamic Colors** — Fully adaptive theme utilizing dynamic color extraction via the
Android Palette library (matching UI elements with item covers).
* **Accompanist Permissions** — Reactive handling of runtime Android permissions (Camera and Media access).
* **Third-Party UI Extensions** — Integrated specialized community libraries to enhance user UX:
  * [compose-collapsing-toolbar](https://github.com/onebone/compose-collapsing-toolbar) (by *onebone*) — For smooth collapsing toolbar animations.
  * [reorderable](https://github.com/Calvin-LL/reorderable) (by *Calvin-LL*) — For fluid drag-and-drop item reordering within lists.

### 💾 Data & Cloud Backend
* **Offline-First Architecture** — Seamless user experience regardless of network state, with transparent synchronization.
* **Room DB** — Local SQLite abstraction layer handling complex data relations (many-to-many cross-references
for artists, albums, and photocards) with transaction safety.
* **Discogs API** — Utilized as the primary external data source to automatically fetch music metadata.
* **Supabase (Postgrest, Storage, Realtime)** — Remote cloud infrastructure for secure user authentication
(Email & Google Auth), remote database sync, and media bucket storage.
* **Ktor & Retrofit** — Resilient networking clients with Kotlinx Serialization for type-safe JSON parsing.

### ⚙️ Background Work & Hardware Integration
* **Jetpack WorkManager** — Guaranteed background processing used to sync local database changes with
the cloud backend even when the app is closed.
* **CameraX & Google ML Kit** — On-device text recognition and barcode scanning to automatically fetch
and process merchandise data.
* **Coil** — Performance-optimized, lifecycle-aware asynchronous image loading and caching.

### 🧪 Testing & Quality Assurance
* **JUnit 5** — Modern test runner environment for executing unit tests.
* **MockK** — Idiomatic Kotlin mocking library used to isolate business logic in Use Cases and Repositories.
* **Turbine** — Specialized library for clean and compact testing of Kotlin asynchronous Flows and StateFlows.
* **Kotlinx Coroutines Test** — Execution environment providing deterministic control over asynchronous coroutine scopes.