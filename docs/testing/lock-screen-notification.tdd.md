# TDD Evidence — Persistent lock-screen notification

Journeys were derived during this TDD run (no `*.plan.md` handoff). Source intent: product request for a persistent public lock-screen notification with channel setup, Android 13+ permission handling, tap action, and an in-app show/hide toggle.

## User journeys

1. As a user, I want an ongoing public lock-screen notification so I can see Remainder without unlocking.
2. As a user on Android 13+, I want Remainder to request notification permission before posting that reminder.
3. As a user, I want tapping the notification to open Remainder.
4. As a user, I want a Settings toggle to show or hide the reminder, and I want that choice remembered.

## Task report

| Task | Summary | Command | Evidence |
|------|---------|---------|----------|
| RED | Unit-test compile failed on unresolved `LockScreenNotification*` types, missing Settings toggle parameters, and missing `POST_NOTIFICATIONS` / status icon | `./gradlew.bat :app:testDebugUnitTest` | Checkpoint `1581278`. Failures were unresolved production types and APIs, not setup errors. |
| GREEN | Same suite compiled and passed after notification + Settings implementation | `./gradlew.bat :app:testDebugUnitTest` | Checkpoint `a1b59c1`. `BUILD SUCCESSFUL`, 68 Remainder unit tests. |
| Emulator | Debug APK instrumented tests passed on `Remainder_ATD_36` (API 36 ATD), including Settings toggle visibility | `./gradlew.bat :app:connectedDebugAndroidTest` | `Finished 3 tests on Remainder_ATD_36(AVD) - 16`. |
| Refactor | Coordinator uses store/notifier interfaces (plain JUnit), permission policy is pure, toggle reflects granted+enabled, tap intent adds `NEW_TASK` | `./gradlew.bat :app:testDebugUnitTest :app:jacocoLogicReport` | Tests remained green (74 Remainder unit tests). Logic JaCoCo LINE 70/70 (100%), BRANCH 17/18 (94%). |
| Review | Kotlin review found no CRITICAL/HIGH issues | kotlin-reviewer | MEDIUM: reboot restore not implemented (out of requested scope); strings remain hardcoded to match existing screens. Toggle/permission mismatch was fixed in refactor. |

## Test specification

| # | What is guaranteed | Test file or command | Test type | Result | Evidence |
|---|--------------------|----------------------|-----------|--------|----------|
| 1 | Channel id `lock_screen_reminder` with public lock-screen visibility and DEFAULT importance | `LockScreenNotificationChannelTest` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 2 | Show posts an ongoing public notification titled Remainder | `LockScreenNotificationControllerTest` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 3 | Notification tap intent targets `MainActivity` with SINGLE_TOP, CLEAR_TOP, NEW_TASK | `LockScreenNotificationControllerTest`, `LockScreenTapIntentTest` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 4 | Hide cancels the posted notification and is idempotent | `LockScreenNotificationControllerTest` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 5 | Android 13+ requires runtime permission; API 27 does not | `NotificationPermissionPolicyLogicTest`, `NotificationPermissionPolicyTest` | unit | PASS | `:app:testDebugUnitTest` |
| 6 | Enable without permission returns `NeedsPermission` and does not post | `LockScreenNotificationCoordinatorTest` | unit | PASS | `:app:testDebugUnitTest` |
| 7 | Enable with permission posts and persists; disable hides | `LockScreenNotificationCoordinatorTest` | unit | PASS | `:app:testDebugUnitTest` |
| 8 | Sync restores or hides based on stored intent and grant | `LockScreenNotificationCoordinatorTest` | unit | PASS | `:app:testDebugUnitTest` |
| 9 | Toggle is off when permission is missing even if preference is on | `LockScreenNotificationCoordinatorTest` | unit | PASS | `:app:testDebugUnitTest` |
| 10 | Preference defaults off and persists enable/disable | `LockScreenNotificationPreferencesTest` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 11 | Manifest declares `POST_NOTIFICATIONS`; status icon exists | `ManifestNotificationPermissionTest`, `NotificationIconResourceTest` | unit | PASS | `:app:testDebugUnitTest` |
| 12 | Settings shows the lock-screen reminder switch and toggling fires the callback | `SettingsScreenTest` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 13 | Launching `MainActivity` creates the notification channel | `MainActivityNotificationSyncTest` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 14 | Emulator Settings screen shows "Show lock-screen reminder" | `AppLaunchTest.settingsShowsLockScreenReminderToggle` | instrumented E2E | PASS | `:app:connectedDebugAndroidTest` |

## Coverage and known gaps

- Logic JaCoCo (`:app:jacocoLogicReport`): **LINE 70/70 (100%)**, **BRANCH 17/18 (94%)**, INSTRUCTION 438/448 (~98%), METHOD 22/27 (~82%).
- Android wrappers (`LockScreenNotificationController`, channel, preferences, tap intent, `NotificationPermissionReader`) and Compose (`RemainderApp`, screens) are excluded from the logic report because Robolectric/Compose do not attribute hits in this project's JaCoCo setup. They are covered by passing Robolectric and emulator tests.
- Reboot restore is not implemented. An enabled reminder returns after the user opens Remainder (`sync` on launch/resume), not automatically after reboot.
- Notification and Settings copy is hardcoded, matching existing Home/Settings screens.

## Merge evidence

- RED checkpoint: `1581278` `test: add lock-screen notification reproducers`
- GREEN checkpoint: `a1b59c1` `feat: add persistent public lock-screen reminder`
- Refactor checkpoint: this commit after interfaces, permission policy extraction, and toggle/permission alignment
