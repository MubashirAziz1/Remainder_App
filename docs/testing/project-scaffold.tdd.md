# TDD Evidence — Project Scaffold

Journeys were derived during this TDD run (no `*.plan.md` handoff).

## User journeys

1. As a user, I open Remainder and see a branded Home screen.
2. As a user, I open Settings from Home and can return with Back.
3. As a developer, I build with minSdk 27, compileSdk 37, and targetSdk 36.
4. As a user, the app has a distinct launcher icon.
5. As a developer, Git ignores build outputs, SDK local config, and harness folders.

## Task report

| Task | Summary | Command | Evidence |
|------|---------|---------|----------|
| RED | 22 of 30 unit tests failed for missing SDK 27, theme tokens, routes, icons, gitignore, and Home/Settings UI | `./gradlew.bat :app:testDebugUnitTest` | Checkpoint `fe3b5fe`. Failures were assertion errors on the intended gaps, not setup errors. |
| GREEN | Same unit suite passed after scaffold implementation | `./gradlew.bat :app:testDebugUnitTest` | `BUILD SUCCESSFUL`, 30 tests (then 32 after theme dark/light cases). |
| Emulator | Debug APK installed and instrumented tests passed on `Remainder_ATD_36` (API 36 ATD) | `./gradlew.bat :app:installDebug :app:connectedDebugAndroidTest` | `Finished 2 tests on Remainder_ATD_36(AVD) - 16`. `MainActivity` was resumed after `am start`. |
| Coverage | Logic classes 100% line coverage; full Compose bytecode report remains lower because Robolectric does not attribute `@Composable` hits | `./gradlew.bat :app:jacocoLogicReport` and `:app:createDebugUnitTestCoverageReport` | Logic: 43/43 lines (100%). Full unit report: 43/119 lines (~36%) with Home/Settings at 0% JaCoCo despite passing UI tests. |

## Test specification

| # | What is guaranteed | Test file or command | Test type | Result | Evidence |
|---|--------------------|----------------------|-----------|--------|----------|
| 1 | Home route is `home` and start destination is Home | `RemainderDestinationsTest` | unit | PASS | `:app:testDebugUnitTest` |
| 2 | Settings route is `settings` | `RemainderDestinationsTest` | unit | PASS | `:app:testDebugUnitTest` |
| 3 | minSdk 27, compileSdk 37, targetSdk 36 in Gradle and `ProjectSdk` | `GradleSdkTest`, `ProjectSdkTest` | unit | PASS | `:app:testDebugUnitTest` |
| 4 | Compose and navigation-compose are enabled | `GradleSdkTest` | unit | PASS | `:app:testDebugUnitTest` |
| 5 | Light/dark theme tokens match Remainder teal/paper | `ThemeColorTest`, `RemainderThemeTest` | unit | PASS | `:app:testDebugUnitTest` |
| 6 | Type scale uses 36.sp display and 16.sp body | `TypeScaleTest` | unit | PASS | `:app:testDebugUnitTest` |
| 7 | Adaptive launcher icons exist and are referenced in the manifest | `AppIconResourceTest` | unit | PASS | `:app:testDebugUnitTest` |
| 8 | `.gitignore` covers Gradle, build, local.properties, `.cursor/`, `.state/` | `GitignoreTest` | unit | PASS | `:app:testDebugUnitTest` |
| 9 | Home shows "Remainder" and Settings action fires | `HomeScreenTest` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 10 | Home → Settings → Back returns Home | `NavigationFlowTest` | unit (Robolectric) | PASS | `:app:testDebugUnitTest` |
| 11 | Cold launch and navigation on emulator | `AppLaunchTest` | instrumented E2E | PASS | `:app:connectedDebugAndroidTest` |

## Coverage and known gaps

- Logic JaCoCo (`:app:jacocoLogicReport`): **100% lines** on destinations, SDK constants, and color/type tokens. Instruction coverage 333/343 (~97%).
- Full unit JaCoCo including Compose: ~36% lines. `@Composable` screens and `MainActivity` are not attributed by Robolectric JaCoCo; they are covered by passing UI unit tests and emulator instrumented tests.
- No alarm creation, lock-screen flags, or Clock intents were implemented (out of this feature's scope).

## Merge evidence

- RED checkpoint: `fe3b5fe` `test: add scaffold reproducers for Compose project`
- GREEN checkpoint: this commit after passing unit tests, emulator install, and instrumented tests
