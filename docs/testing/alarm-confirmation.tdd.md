# TDD Evidence — Alarm confirmation with retry/cancel on failure

Date: 2026-09-10
Source plan: `.state/PLAN.md` Milestone 3 task requested mid-session ("show confirmation with alarm time, title, and today/tomorrow status; clear retry/cancel when alarm creation fails"). Journeys derived during this TDD run.

## User journeys

1. As a user, after confirming a valid alarm, I want to see a confirmation with the alarm time, title, and today/tomorrow status, so I know exactly when it will ring.
2. As a user, when alarm creation fails, I want a clear error with explicit Retry and Cancel actions, so I can re-attempt or dismiss without guessing.

## Checkpoint commits (branch `main`)

| Stage | Commit | Evidence |
|---|---|---|
| RED | `96d015b` test: add alarm confirmation and retry/cancel reproducers | `:app:testDebugUnitTest` fails at `compileDebugUnitTestKotlin`: unresolved `AlarmConfirmationFormatter`, `AlarmConfirmation`, `AlarmConfirmationScreen`, `AlarmFailureScreen`, `clock` param. Main sources compile; only new tests fail (compile-time RED). |
| GREEN | `6e29d53` feat: show alarm confirmation with retry/cancel on scheduling failure | `:app:testDebugUnitTest` BUILD SUCCESSFUL, 117 tests, 0 failures. |
| Refactor | `cc1d333` refactor: make alarm intent spec pure for measurable coverage | `:app:jacocoLogicReport` BUILD SUCCESSFUL, 119 tests, 0 failures, LINE 140/141 = 99.3%. |
| RED (review fix) | `e629797` test: add state-restoration reproducers for alarm flow | `:app:testDebugUnitTest` fails at compile: unresolved `AlarmUiStateSaver`/`AlarmUiState` in `ui.alarm`. |
| GREEN (review fix) | `0200353` fix: persist alarm flow state across recreation | `:app:jacocoLogicReport` BUILD SUCCESSFUL, 125 tests, 0 failures, LINE 174/175 = 99.4%. |

## Test specification

| # | What is guaranteed | Test | Type | Result |
|---|---|---|---|---|
| 1 | Confirmation time is zero-padded 24-hour (`07:05`) | `AlarmConfirmationTest.formatsTimeAsZeroPadded24Hour` | unit | PASS |
| 2 | Occurrence dated today is labeled "Today" | `AlarmConfirmationTest.occurrenceOnTodayIsLabeledToday` | unit | PASS |
| 3 | Occurrence dated next day is labeled "Tomorrow" | `AlarmConfirmationTest.occurrenceOnNextDayIsLabeledTomorrow` | unit | PASS |
| 4 | Confirmation carries the alarm title | `AlarmConfirmationTest.titleIsCarriedThrough` | unit | PASS |
| 5 | Confirmation screen shows time, title, day status, Done | `AlarmConfirmationScreenTest.showsTimeTitleAndDayStatus` / `tomorrowStatusIsShownForNextDayAlarm` | unit (Robolectric Compose) | PASS |
| 6 | Done invokes its callback | `AlarmConfirmationScreenTest.doneInvokesCallback` | unit (Robolectric Compose) | PASS |
| 7 | Failure screen shows error with Retry and Cancel | `AlarmFailureScreenTest.showsErrorMessageWithRetryAndCancelActions` | unit (Robolectric Compose) | PASS |
| 8 | Retry invokes its callback; Cancel does not retry | `AlarmFailureScreenTest.retryInvokesCallback` / `cancelInvokesCallbackWithoutRetrying` | unit (Robolectric Compose) | PASS |
| 9 | Successful confirm shows confirmation (time/title/Today) without finishing | `LockScreenAlarmActivityTest.successfulConfirmShowsConfirmationWithTimeTitleAndTodayStatus` | unit (Robolectric Compose) | PASS |
| 10 | Done on confirmation finishes the activity | `LockScreenAlarmActivityTest.doneOnConfirmationFinishesActivity` | unit (Robolectric Compose) | PASS |
| 11 | Failed confirm shows error + Retry + Cancel without finishing | `LockScreenAlarmActivityTest.failedConfirmShowsErrorWithRetryAndCancel` | unit (Robolectric Compose) | PASS |
| 12 | Retry re-schedules the same draft and reaches confirmation | `LockScreenAlarmActivityTest.retryAfterFailureSchedulesAgainAndShowsConfirmation` (scheduler attempts == 2) | unit (Robolectric Compose) | PASS |
| 13 | Cancel on failure dismisses | `LockScreenAlarmActivityTest.cancelOnFailureFinishesActivity` | unit (Robolectric Compose) | PASS |
| 14 | Failed/Scheduled state survives recreation (no lost retry context, no duplicate alarms) | `LockScreenAlarmActivityTest.failureStateSurvivesRecreation` / `confirmationStateSurvivesRecreation` (StateRestorationTester) | unit (Robolectric Compose) | PASS |
| 15 | UI state saver round-trips Editing/Failed/Scheduled; corrupt state falls back safely | `AlarmUiStateSaverTest` (5 tests) | unit | PASS |
| 16 | Scheduler maps ActivityNotFound/Security/IllegalState launch failures to `ClockLaunchFailed` | `ClockAlarmSchedulerTest` (5 tests) | unit | PASS |

## Coverage and known gaps

- Command: `.\gradlew :app:jacocoLogicReport` → LINE 174/175 = **99.4%**, BRANCH 27/28 (report scope excludes Compose screens and Android-bound gateways per `app/build.gradle.kts`).
- One missed line: a `ClockAlarmScheduler` catch-branch line partially covered (branch 27/28).
- `ContextAlarmLauncher` (Android `resolveActivity`/`startActivity` glue) is JaCoCo-excluded like the project's other Android gateways; its behavior is exercised indirectly via Robolectric flow tests.
- Robolectric's sandbox classloader bypasses the JaCoCo agent, so logic exercised only by Robolectric tests is kept in pure-JVM classes (`AlarmIntentSpec`, saver) to keep coverage measurable and honest.

## Notes

- Pre-existing broken test setup fixed during this run: `AlarmIntentFactoryTest`/`ClockAlarmSchedulerTest` used `android.content.Intent` under plain JUnit (stubbed to defaults) and were failing; they now assert against the pure `AlarmIntentSpec`.
- Kotlin review (kotlin-reviewer subagent): 0 CRITICAL, 1 HIGH (state not recreation-safe) — fixed in `0200353` with tests #14–15. Verdict after fix: issue resolved.
