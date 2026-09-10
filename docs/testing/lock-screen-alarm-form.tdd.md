# TDD Evidence — Lock-screen named-alarm form

Journeys were derived from `.state/PLAN.md` Milestone 2 during this TDD run (no `*.plan.md` handoff). Source intent: Compose screen that opens over the keyguard with 24-hour hour/minute pickers, required title validation, Confirm, and Cancel. AlarmManager / Clock scheduling and persistence of created alarms are out of scope.

## User journeys

1. As a user, I want the named-alarm form to open over the lock screen so I can create an alarm without unlocking.
2. As a user, I want 24-hour hour and minute pickers with no AM/PM so the time I confirm is unambiguous.
3. As a user, I want Confirm to require a non-blank title so I cannot save an unnamed alarm draft.
4. As a user, I want Cancel to dismiss the form without confirming a draft.

## Task report

| Task | Summary | Command | Evidence |
|------|---------|---------|----------|
| RED | Unit-test compile failed on unresolved `AlarmFormValidator`, `CreateAlarmScreen`, `LockScreenAlarmActivity`, and Home `onCreateAlarm` | `./gradlew.bat :app:testDebugUnitTest` | Checkpoint `3f2a8ce`. Failures were unresolved production types and APIs, not setup errors. |
| GREEN | Same suite compiled and passed after form, pickers, keyguard activity, Home entry, and notification tap retarget | `./gradlew.bat :app:testDebugUnitTest` | `BUILD SUCCESSFUL`, 95 Remainder unit tests, 0 failures. |
| Emulator | Debug APK instrumented tests passed on `Remainder_ATD_36` (API 36 ATD), including open form, title error, and Cancel back to Home | `./gradlew.bat :app:connectedDebugAndroidTest` | `Finished 6 tests on Remainder_ATD_36(AVD) - 16`. |
| Refactor | Form state uses `rememberSaveable`; pickers expose `selected` semantics and scroll to the initial hour/minute; Confirm is full-width | `./gradlew.bat :app:testDebugUnitTest :app:jacocoLogicReport` | Tests remained green (95 Remainder unit tests). Logic JaCoCo LINE 84/84 (100%), BRANCH 21/22 (95.5%). |
| Review | Kotlin review found no CRITICAL/HIGH issues | kotlin-reviewer | MEDIUM: hardcoded strings match existing Home/Settings; LazyRow was not used because off-screen options must stay in the tree for picker tests. Initial scroll addresses the default hour being off-screen. |

## Test specification

| # | What is guaranteed | Test file or command | Test type | Result | Evidence |
|---|--------------------|----------------------|-----------|--------|----------|
| 1 | Manifest declares `LockScreenAlarmActivity` with `showWhenLocked`, `turnScreenOn`, and `exported=false` | `LockScreenAlarmManifestTest` | unit | PASS | `:app:testDebugUnitTest` |
| 2 | Keyguard activity hosts the form and does not finish on launch | `LockScreenAlarmActivityTest.launchesAlarmFormWithoutFinishing` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 3 | Cancel finishes the keyguard activity | `LockScreenAlarmActivityTest.cancelFinishesActivity` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 4 | Confirm without a title keeps the activity open and shows `Title is required` | `LockScreenAlarmActivityTest.confirmWithoutTitleKeepsActivityOpen` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 5 | Confirm with a title finishes the activity | `LockScreenAlarmActivityTest.confirmWithTitleFinishesActivity` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 6 | Empty and whitespace titles are rejected; valid titles are trimmed | `AlarmFormValidatorTest` | unit | PASS | `:app:testDebugUnitTest` |
| 7 | Hour is coerced to 0–23 and minute to 0–59 | `AlarmFormValidatorTest` | unit | PASS | `:app:testDebugUnitTest` |
| 8 | Unicode titles are accepted | `AlarmFormValidatorTest.unicodeTitleIsAccepted` | unit | PASS | `:app:testDebugUnitTest` |
| 9 | Form shows title field, 24-hour pickers (0–23 / 0–59), Confirm, Cancel, and no AM/PM | `CreateAlarmScreenTest.showsTitlePickersAndActions` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 10 | Confirm without title shows error and does not submit | `CreateAlarmScreenTest.confirmWithoutTitleShowsErrorAndDoesNotSubmit` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 11 | Confirm with title submits a trimmed draft; hour/minute picks update the draft | `CreateAlarmScreenTest` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 12 | Cancel does not confirm | `CreateAlarmScreenTest.cancelDismissesWithoutConfirming` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 13 | Typing after an error clears `Title is required` | `CreateAlarmScreenTest.typingAfterErrorClearsValidationMessage` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 14 | Home "New alarm" opens the form; notification tap targets `LockScreenAlarmActivity` | `HomeScreenTest`, `LockScreenTapIntentTest` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 15 | Emulator: open form from Home, confirm without title shows error, Cancel returns Home | `AppLaunchTest` | instrumented E2E | PASS | `:app:connectedDebugAndroidTest` |

## Coverage and known gaps

- Logic JaCoCo (`:app:jacocoLogicReport`): **LINE 84/84 (100%)**, **BRANCH 21/22 (95.5%)**, INSTRUCTION 500/525 (95.2%), METHOD 26/36 (72.2%).
- METHOD is below 80% because JaCoCo counts unused `data class` generated members (`componentN`, `toString`) on `AlarmDraft` and `AlarmConfirmResult`. Line and branch coverage on validator logic are 100% / 100% for `AlarmFormValidator`.
- Android wrappers (`LockScreenAlarmActivity`) and Compose (`CreateAlarmScreen`, `TwentyFourHourPickers`) are excluded from the logic report because Robolectric/Compose do not attribute hits in this project's JaCoCo setup. They are covered by passing Robolectric and emulator tests.
- Confirm currently finishes without persisting or scheduling. That is out of requested scope.
- Copy remains hardcoded, matching existing Home/Settings screens.

## Merge evidence

- RED checkpoint: `3f2a8ce` `test: add lock-screen alarm form reproducers`
- GREEN: implementation is in the working tree (activity, validator, form, pickers, Home entry, tap intent, manifest). Commit when asked.
