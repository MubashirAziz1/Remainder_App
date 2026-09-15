# TDD Evidence — Onboarding for notifications, lock screen, battery optimization

Date: 2026-09-15
Source plan: `.state/PLAN.md` Milestone 4 (added at session start from the user request). Journeys derived during this TDD run.

## User journeys

1. As a new user, I want a guided onboarding that explains notifications, lock-screen display, and battery optimization, so my reminders reliably reach me.
2. As a user, I want each onboarding step to link to the standard Android settings screen for that topic, so I can act without hunting through system Settings.
3. As a user who denied the notification permission, I want clear guidance on how to re-enable it (retry the request, or open app settings when permanently denied), so I'm never stuck.
4. As a returning user, I want onboarding shown only until completed, and re-openable from Settings, so it doesn't nag but stays reachable.

## Checkpoint commits (branch `main`)

| Stage | Commit | Evidence |
|---|---|---|
| RED | `d701eb7` test: add onboarding reproducers for notifications, lock screen, battery | `:app:testDebugUnitTest` fails at `compileDebugUnitTestKotlin`: unresolved `OnboardingStep`, `OnboardingPolicy`, `SystemSettingsIntentFactory`, `SystemSettingsIntentSpec`, `DeniedPermissionGuidance`, `NotificationGuidance`, `OnboardingPreferences`, `OnboardingScreen`, coordinator `hasPermission`, new `RemainderNavHost` params. Main sources compile; only new tests fail (compile-time RED). |
| GREEN | `b2923d4` feat: add onboarding for notifications, lock screen, and battery optimization | `:app:testDebugUnitTest` BUILD SUCCESSFUL, 168 tests, 0 failures, 0 errors, 0 skipped. |
| Refactor | `351ef03` refactor: cover onboarding route in destinations test | `:app:jacocoLogicReport` BUILD SUCCESSFUL, LINE 227/228 = 99.6%, BRANCH 76/91 = 83.5%. |
| RED (review fix) | `35773f3` test: add reproducers for onboarding review findings | `:app:testDebugUnitTest` 173 tests, 4 failed: `ContextSystemSettingsLauncherTest` (2), `OnboardingNavigationTest.completingFirstRunOnboardingRemovesItFromBackStack`, `OnboardingScreenTest.permanentDenialHidesRuntimeRequestButton` (runtime RED). |
| GREEN (review fix) | `fab5e90` fix: address onboarding review findings | `:app:jacocoLogicReport` BUILD SUCCESSFUL, 173 tests, 0 failures, LINE 227/228 = 99.6%, BRANCH 76/91 = 83.5%. `:app:assembleDebug` BUILD SUCCESSFUL. |

## Test specification

| # | What is guaranteed | Test | Type | Result |
|---|---|---|---|---|
| 1 | Steps ordered Notifications → Lock screen → Battery optimization; first/last/next/previous semantics | `OnboardingStepTest` (6 tests) | unit | PASS |
| 2 | Runtime notification request only on SDK 33+ when not granted | `OnboardingPolicyTest` (3 tests) | unit | PASS |
| 3 | Settings specs carry correct standard Android actions, package, channel, package-URI flags | `SystemSettingsIntentFactoryTest` (5 tests) | unit | PASS |
| 4 | Guidance: granted/no-denial → None; denial+rationale → RetryRequest; denial without rationale → OpenAppSettings; clear copy per state | `DeniedPermissionGuidanceTest` (7 tests) | unit | PASS |
| 5 | Onboarding completion persists across store instances; incomplete by default | `OnboardingPreferencesTest` (2 tests) | unit (Robolectric) | PASS |
| 6 | Manifest declares `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | `OnboardingManifestTest` | unit | PASS |
| 7 | Notification step first; Allow notifications requests permission; hidden when runtime request unneeded or permanently denied | `OnboardingScreenTest.notificationStepIsShownFirst` / `allowNotificationsRequestsRuntimePermission` / `allowNotificationsHiddenWhenRuntimeRequestNotNeeded` / `permanentDenialHidesRuntimeRequestButton` | unit (Robolectric Compose) | PASS |
| 8 | Each step opens its standard Android settings screen with the right spec | `OnboardingScreenTest.notificationStepLinksToAppNotificationSettings` / `lockScreenStepLinksToChannelSettings` / `batteryStepRequestsBatteryOptimizationExclusion` / `batteryStepLinksToBatterySettingsList` | unit (Robolectric Compose) | PASS |
| 9 | Next/Back navigate steps; Back absent on first step; Done completes | `OnboardingScreenTest.nextAdvancesToLockScreenStep` / `nextTwiceReachesBatteryStepWithDoneAction` / `backReturnsToPreviousStep` / `backIsNotOfferedOnFirstStep` / `doneOnLastStepInvokesCompletionCallback` | unit (Robolectric Compose) | PASS |
| 10 | Soft denial shows retry guidance that re-requests; permanent denial shows settings guidance that opens app details | `OnboardingScreenTest.softDenialShowsRetryGuidance` / `permanentDenialShowsGuidanceAndOpensAppSettings` | unit (Robolectric Compose) | PASS |
| 11 | Onboarding is start destination only when requested; completion lands on Home and reports completion; Settings reopens onboarding | `OnboardingNavigationTest` (4 tests) | unit (Robolectric Compose) | PASS |
| 12 | Completed first-run onboarding leaves nothing beneath Home (no return to onboarding on Back) | `OnboardingNavigationTest.completingFirstRunOnboardingRemovesItFromBackStack` | unit (Robolectric Compose) | PASS |
| 13 | Settings launches from application context carry `FLAG_ACTIVITY_NEW_TASK`, package URI, and channel extras | `ContextSystemSettingsLauncherTest` (2 tests) | unit (Robolectric) | PASS |
| 14 | Coordinator exposes raw permission state for guidance/policy decisions | `LockScreenNotificationCoordinatorTest.hasPermissionReflectsChecker` | unit | PASS |
| 15 | Onboarding route is `onboarding` | `RemainderDestinationsTest.onboardingRouteIsOnboarding` | unit | PASS |

## Coverage and known gaps

- Command: `.\gradlew :app:jacocoLogicReport` → LINE 227/228 = **99.6%**, BRANCH 76/91 = **83.5%** (report scope excludes Compose screens and Android-bound gateways per `app/build.gradle.kts`; `OnboardingScreenKt`, `ContextSystemSettingsLauncher`, `OnboardingPreferences` excluded like the project's other gateways — the launcher is still behavior-tested via Robolectric, test #13).
- Missed line: pre-existing `ClockAlarmScheduler` catch-branch line (unchanged by this work).
- Missed branches: 14 in `AlarmUiStateSaver` corrupt-restore Elvis paths (pre-existing, only partially exercised by `AlarmUiStateSaverTest.corruptSavedStateRestoresToNull`) and 1 pre-existing in `LockScreenNotificationCoordinator`. All onboarding-feature branches are covered.
- Emulator `connectedDebugAndroidTest` not run in this session (no emulator started); unit + Robolectric suite is the verification gate here.

## Notes

- Kotlin review (kotlin-reviewer subagent): 0 CRITICAL, 2 HIGH — settings launch from application context missing `FLAG_ACTIVITY_NEW_TASK`, and onboarding left beneath Home after first-run completion. Both fixed in `fab5e90` with reproducer tests (#12–13). MEDIUMs fixed: mutually exclusive guidance buttons (#7), scrollable onboarding, saveable `denialObserved`, `apply()` instead of `commit()`. Not changed: hardcoded strings (project convention, matches existing screens) and direct battery-exemption request (kept; reminder delivery is the app's core function — confirm Play policy eligibility before release).
- `DeniedPermissionGuidance.resolve` treats `denialObserved` as the gate: guidance appears only after an actual denial in this session, so first-run users (rationale false before any request) are not mislabeled as permanently denied.
