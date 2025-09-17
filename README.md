# Subscription Management App

A Compose-first Android application that keeps recurring subscriptions, payment history, and renewal reminders in one place. The project is written in Kotlin and organised with Clean Architecture to separate presentation, domain logic, and data concerns.

## Overview
Subscription Management App centralises digital subscriptions so users can stay ahead of renewal dates and understand their recurring spend. The UI is powered by Jetpack Compose with Material 3 styling, while Room, Kotlin Coroutines, and StateFlow provide a reactive data layer that keeps screens in sync with the underlying database.

## Feature Highlights
- Subscription lifecycle management with create, edit, and delete flows covering billing cycles, currencies, reminder lead times, start/end dates, notes, and deep links to provider websites or apps.
- Home dashboard that surfaces the total monthly cost, quick actions, and the next renewals with status cues such as due soon or overdue.
- Detailed subscription screen with status chips, reminder configuration, and actionable shortcuts to edit or remove a subscription.
- Monthly statistics screen summarising total spend for the current month alongside individual payment history entries.
- Category directory that lists predefined and stored categories, including optional keyword tags for better filtering.
- Reminder scheduling implemented with `AlarmManager`, `NotificationScheduler`, and `ReminderBroadcastReceiver` to notify users before billing dates.

## Current Status
- Implemented: Core subscription CRUD flows, active subscription dashboard, statistics summary, Room-backed repositories, reminder scheduling, and StateFlow-driven ViewModels.
- In progress: Category creation/editing from the UI, search and advanced filtering on the subscription list, payment history management screens, Google Calendar sync (logic available via `GoogleCalendarManager` but UI workflows are stubbed), backup and restore triggers in settings, biometric and SQLCipher wiring, and automated tests that reflect the latest ViewModel contracts.
- Planned: Integrating the Compose chart components (bar, pie, line) with live statistics data, polishing reminder management screens, and introducing guided setup for optional integrations.

## Architecture
- **UI layer** — Jetpack Compose screens consume immutable view state from `StateFlow` exposed by Hilt-provided ViewModels. Reusable building blocks (`AppTopBar`, `AppBottomBar`, loading and error components) provide consistent UX patterns across screens.
- **Domain layer** — Focused use cases encapsulate business logic for subscriptions, categories, reminders, payments, analytics, calendar sync, backups, and security. Each use case exposes either suspending functions or cold flows to keep the UI reactive.
- **Data layer** — Room entities and DAOs model subscriptions, categories, reminders, and payment history. Repository implementations orchestrate database access alongside managers for notifications, backups, calendars, and encrypted storage.
- **Dependency injection** — Hilt modules provide database instances, repository bindings, and manager singletons, enabling easy testability and modular expansion.

## Technical Stack
- Kotlin 1.9, Coroutines, and Flow
- Jetpack Compose (Material 3, Navigation, Lifecycle runtime) for the UI
- Room with enum converters and migrations for persistence
- Hilt for dependency injection across activities, ViewModels, and managers
- AlarmManager + NotificationCompat for reminder delivery
- Google Calendar API client scaffolding for renewal sync
- AndroidX Security Crypto, Biometric, and SQLCipher dependencies for future secure storage
- Gradle Kotlin DSL build scripts with centralised dependency versions

## Project Structure
```
app/
├── src/main/java/com/example/subcriptionmanagementapp/
│   ├── data/
│   │   ├── local/          // Room database, DAOs, entities, converters
│   │   ├── repository/     // Repository interfaces and implementations
│   │   ├── notification/   // Alarm/notification schedulers and receivers
│   │   ├── calendar/       // Google Calendar integration scaffolding
│   │   ├── backup/         // Backup & restore manager
│   │   └── security/       // Encrypted storage helpers
│   ├── domain/
│   │   └── usecase/        // Use cases grouped by feature area
│   ├── ui/
│   │   ├── components/     // Reusable Compose components
│   │   ├── navigation/     // NavHost and destinations
│   │   ├── screens/        // Feature screens (home, subscriptions, categories, etc.)
│   │   └── viewmodel/      // Hilt ViewModels exposing StateFlow
│   └── util/               // Date and currency helpers
├── src/androidTest/        // Instrumented UI tests (placeholders)
├── src/test/               // Unit tests (require updates)
├── build.gradle.kts
└── gradle/libs.versions.toml
```

## Getting Started
1. **Prerequisites**
   - Android Studio (latest stable release) with Kotlin support
   - JDK 11 (configured inside Android Studio)
   - Android SDK 36 (target/compile) with minimum SDK 29
2. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Subcription_Management_App
   ```
3. **Open in Android Studio**
   - Let Gradle sync the project. Running `./gradlew tasks` from the terminal is a quick sanity check.
4. **Run the app**
   - Choose a device or emulator on Android 10 (API 29) or later and press Run. Alternatively, build from the command line with `./gradlew assembleDebug`.

## Optional Setup
- **Google Calendar sync** — Provide OAuth credentials and complete the sign-in flow before enabling calendar exports. The `GoogleCalendarManager` expects a `GoogleAccountCredential` instance.
- **Exact alarms** — Android 13+ devices may require the `SCHEDULE_EXACT_ALARM` permission to schedule reminders and the `POST_NOTIFICATIONS` permission to display them.
- **Backups** — The `BackupManager` writes JSON backups to internal storage; ensure you request storage permissions or integrate the Storage Access Framework before surfacing the UI action.

## Testing
- Run unit tests:
  ```bash
  ./gradlew test
  ```
- Run instrumentation tests on a connected device or emulator:
  ```bash
  ./gradlew connectedAndroidTest
  ```
  Several test classes still reference legacy ViewModel signatures; expect to update them as part of ongoing QA work.

## Roadmap
- Wire category creation/editing, search filters, and reminder management screens into the existing UI flows.
- Finish the Google Calendar, backup/restore, and biometric authentication experiences.
- Connect the Compose chart components to live statistics data and expand reporting.
- Refresh unit and instrumentation tests to cover repositories, managers, and new UI states.
- Evaluate SQLCipher enablement for full-database encryption once key management is finalised.

## Contributing
Contributions are welcome. Please fork the repository, branch from `main`, follow the existing Kotlin and Compose style, and include tests or notes for how you validated the change. Open a pull request with a clear description of the updates and any outstanding questions.

## License
This project is licensed under the MIT License. See [LICENSE](LICENSE) for the full text.
