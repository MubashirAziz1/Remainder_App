# TDD Evidence — Composer state via ViewModel; essential preferences via DataStore

Date: 2026-09-15
Source request: "Manage composer state using ViewModels and persist only essential preferences such as notification visibility and onboarding completion using DataStore."

## User journeys

1. As a user, I want my "show lock-screen reminder" choice and onboarding completion to survive app restarts, so I don't have to reconfigure or re-onboard.
2. As a returning user upgrading from a prior build, I want my existing notification/onboarding settings preserved, so the migration to DataStore doesn't silently reset me.
3. As a developer, I want transient UI state (permission flag, denial-observed, rationale) to live in a ViewModel so it survives rotation without being persisted, keeping storage limited to what truly matters.

## What changed

- **DataStore layer** (`data/RemainderPreferences.kt`): single `remainder_preferences` DataStore with two boolean keys (`lock_screen_reminder_enabled`, `onboarding_completed`). `RemainderPreferenceStore` interface exposes `Flow<Boolean>` getters + suspend setters; `RemainderPreferences` implements it. Reads are `.catch`-guarded for `IOException` (fall back to defaults); writes log `IOException` instead of crashing. `SharedPreferencesMigration` migrates the two legacy files on first access (`remainder_notifications`, `remainder_onboarding`).
- **ViewModel** (`ui/RemainderViewModel.kt`): owns `RemainderUiState` (the two durable flags plus transient `permissionGranted`, `denialObserved`, `showNotificationRationale`) and a `SharedFlow<RemainderEvent>` for one-shot effects. Collects the two DataStore flows; reconciles the lock-screen notification as a side-effect of preference changes (`reconcileNotification`). Exposes `sync`, `refreshRationale(Boolean)`, `onLockScreenReminderChange`, `onPermissionResult(granted, showRationale)`, `onOnboardingCompleted`. Emits `RequestNotificationPermission` when the user toggles on without permission.
- **Coordinator refactor** (`notification/LockScreenNotificationCoordinator.kt`): dropped preference ownership and `LockScreenEnableResult`. Now a thin side-effect executor: `hasPermission`, `ensureChannel`, `show`, `hide`, `sync(desiredEnabled)`.
- **App shell** (`ui/RemainderApp.kt`): consumes `viewModel.uiState` via `collectAsStateWithLifecycle`; observes `viewModel.events` to launch the runtime permission request; on `ON_RESUME` pushes the rationale value and calls `sync`. The Activity-bound rationale lambda is composable-scoped (not retained by the ViewModel).
- **MainActivity**: builds `RemainderPreferences` + coordinator, creates the `RemainderViewModel` via `ViewModelProvider.Factory`, and calls `coordinator.ensureChannel()` synchronously on cold start so the channel exists before the async DataStore-driven reconcile.
- Removed obsolete SharedPreferences gateways: `LockScreenNotificationPreferences`, `LockScreenReminderStore`, `OnboardingStore`, and their tests.

## Checkpoint commits (branch `main`)

| Stage | Commit | Evidence |
|---|---|---|
| RED | (preferences) `:app:testDebugUnitTest --tests RemainderPreferencesTest` → `compileDebugUnitTestKotlin FAILED: Unresolved reference 'RemainderPreferences'` | compile-time RED |
| GREEN | preferences implemented | `RemainderPreferencesTest` 6/6 PASS |
| RED | (ViewModel) `:app:compileDebugKotlin FAILED` (RemainderApp still referenced old coordinator API) | compile-time RED driving the rewire |
| GREEN | ViewModel + rewire | `RemainderViewModelTest` 13/13 PASS; full suite green |
| Review fix | migration + IOException handling + Activity-capture fix | reviewer APPROVE, 0 CRITICAL/HIGH |

## Test specification

| # | What is guaranteed | Test | Type | Result |
|---|---|---|---|---|
| 1 | Defaults: lock-screen off, onboarding not completed; permission reflects checker; startOnboarding true for fresh user | `RemainderViewModelTest.initialStateReflectsPreferenceDefaultsAndPermission` | unit | PASS |
| 2 | onOnboardingCompleted persists and clears startOnboarding | `RemainderViewModelTest.startOnboardingIsTrueUntilOnboardingCompleted` | unit | PASS |
| 3 | Enabling with permission persists + shows notification | `RemainderViewModelTest.enablingWithPermissionPersistsAndShowsNotification` | unit | PASS |
| 4 | Enabling without permission emits RequestNotificationPermission, does not persist or show | `RemainderViewModelTest.enablingWithoutPermissionRequestsPermissionAndDoesNotPersistOrShow` | unit | PASS |
| 5 | Disabling hides + persists false | `RemainderViewModelTest.disablingHidesNotificationAndPersists` | unit | PASS |
| 6 | Permission granted persists true, shows, clears denial | `RemainderViewModelTest.permissionGrantedPersistsShowsAndClearsDenial` | unit | PASS |
| 7 | Permission denied persists false, hides, records denial | `RemainderViewModelTest.permissionDeniedPersistsFalseHidesAndRecordsDenial` | unit | PASS |
| 8 | refreshRationale(value) updates state | `RemainderViewModelTest.refreshRationaleUpdatesStateFromProvider` | unit | PASS |
| 9 | Guidance None when granted; RetryRequest after soft denial with rationale; OpenAppSettings after denial without rationale | `RemainderViewModelTest.guidanceIs*` (3) | unit | PASS |
| 10 | sync shows when enabled+granted; hides when enabled but no permission | `RemainderViewModelTest.sync*` (2) | unit | PASS |
| 11 | DataStore defaults + persistence across instances + key independence | `RemainderPreferencesTest` (6) | unit (Robolectric) | PASS |
| 12 | Two migrations target both legacy files; lock-screen migration runs+migrates when legacy present, not when absent; onboarding migration runs+migrates when present | `RemainderPreferencesTest.migrations*` (4) | unit (Robolectric) | PASS |
| 13 | Coordinator: hasPermission/show/hide/ensureChannel/sync(desiredEnabled) | `LockScreenNotificationCoordinatorTest` (7) | unit | PASS |

## Coverage and known gaps

- Command: `.\gradlew :app:jacocoLogicReport` → LINE 273/283 = **96.5%**, BRANCH 77/91 = **84.6%** (report scope excludes Compose screens and Android-bound gateways per `app/build.gradle.kts`; `RemainderPreferences` excluded like other gateways — still behavior-tested via Robolectric, tests #11–12).
- Missed lines/branches: pre-existing `AlarmUiStateSaver` corrupt-restore Elvis paths and `ClockAlarmScheduler` catch-branch (unchanged by this work).

## Notes

- Kotlin review (kotlin-reviewer subagent): initial pass found 2 HIGH (no SharedPreferences migration; unhandled DataStore IOException) + 1 MEDIUM (retained ViewModel captured the Activity via a rationale callback). All fixed: migration added, reads/writes hardened against IOException, and the rationale is now a Boolean value pushed from the composable-scoped `RemainderApp` (the ViewModel retains no Activity reference). Re-review: APPROVE, 0 CRITICAL/HIGH.
- DataStore migration is exercised by calling `DataMigration.shouldMigrate`/`migrate` directly (the `preferencesDataStore` delegate is a per-process singleton, so a full isolated DataStore test was impractical with this library version's factory API).
- Out of scope: persisting created alarms, AlarmManager scheduling, OEM battery whitelists.
