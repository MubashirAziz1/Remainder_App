# Compatibility Matrix — Lock-screen, Keyguard & Notification Behavior

Date: 2026-09-15
Scope: how the Remainder lock-screen reminder, keyguard activity launch, and notification permission behave across Android versions and OEM ROMs, and how each behavior is verified.

## What the app does (version-agnostic contract)

- Persistent **ongoing** notification on channel `lock_screen_reminder` with `VISIBILITY_PUBLIC` lock-screen visibility, a tap intent that opens `LockScreenAlarmActivity`, and `setOnlyAlertOnce` + `setSilent` so it never buzzes.
- `LockScreenAlarmActivity` declares `android:showWhenLocked="true"` and `android:turnScreenOn="true"` in the manifest **and** calls `setShowWhenLocked(true)` / `setTurnScreenOn(true)` in `onCreate` (API 27+ Activity APIs — the OEM-respected modern path).
- Runtime `POST_NOTIFICATIONS` is requested only on Android 13+ (API 33+); below that, notifications are allowed by default.

## Android version matrix

| API | Version | Notification permission | Channel required | Keyguard launch | Verified by |
|---|---|---|---|---|---|
| 27 | 8.1 | Allowed by default | Yes (26+) | `setShowWhenLocked`/`setTurnScreenOn` available | `NotificationPermissionReaderSdkTest.preTiramisuReportsAccessWithoutRuntimePermission`, `LockScreenNotificationControllerCompatibilityTest` (sdk 27) |
| 28 | 9 | Allowed by default | Yes | Yes | `LockScreenNotificationControllerCompatibilityTest` (sdk 29) |
| 29 | 10 | Allowed by default | Yes | Yes | `NotificationPermissionReaderSdkTest.android10ReportsAccessWithoutRuntimePermission`, `LockScreenNotificationControllerCompatibilityTest` (sdk 29) |
| 30 | 11 | Allowed by default | Yes | Yes | `LockScreenNotificationControllerCompatibilityTest` (sdk 31) |
| 31 | 12 | Allowed by default | Yes | Yes | `NotificationPermissionReaderSdkTest.android12ReportsAccessWithoutRuntimePermission`, `LockScreenNotificationControllerCompatibilityTest` (sdk 31) |
| 32 | 12L | Allowed by default | Yes | Yes | `NotificationPermissionReaderSdkTest.android12LReportsAccessWithoutRuntimePermission`, `LockScreenNotificationControllerCompatibilityTest` (sdk 32) |
| 33 | 13 (Tiramisu) | **Runtime `POST_NOTIFICATIONS` required** | Yes | Yes | `NotificationPermissionReaderSdkTest.tiramisuDeniesAccessByDefault`, `LockScreenNotificationControllerCompatibilityTest` (sdk 33) |
| 34 | 14 (UpsideDownCake) | Runtime required | Yes | Yes | `NotificationPermissionReaderSdkTest.upsideDownCakeDeniesAccessByDefault`, `LockScreenNotificationControllerCompatibilityTest` (sdk 34) |
| 35 | 15 (VanillaIceCream) | Runtime required | Yes | Yes | `NotificationPermissionReaderSdkTest.vanillaIceCreamDeniesAccessByDefault`, `LockScreenNotificationControllerCompatibilityTest` (sdk 35) |
| 36 | 16 (targetSdk) | Runtime required | Yes | Yes | Manifest + policy tests cover the floor; Robolectric 4.14.1 has no Android 16 runtime, so no Robolectric run at sdk 36 yet |

### Keyguard / show-over-keyguard invariants (locked by tests)

- `LockScreenAlarmManifestTest` — manifest declares `showWhenLocked="true"`, `turnScreenOn="true"`, `exported="false"`.
- `LockScreenAlarmKeyguardApiTest` — source calls the modern Activity APIs and does **not** regress to deprecated window flags (`FLAG_SHOW_WHEN_LOCKED`, `FLAG_TURN_SCREEN_ON`, `FLAG_DISMISS_KEYGUARD`) that OEMs honor inconsistently.

## OEM behavior notes (manual verification — not automatable in Robolectric)

Robolectric runs against AOSP runtimes and cannot reproduce OEM ROM behavior. The following must be verified manually on physical devices or OEM emulator skins. The app's defensive choices are listed so an OEM regression is caught at the logic level even when the ROM behavior differs.

| OEM / ROM family | Known quirk | App mitigation | Manual check |
|---|---|---|---|
| AOSP / Pixel | Reference behavior; `VISIBILITY_PUBLIC` honored; keyguard activity shows over lock screen. | Uses standard APIs only. | Enable reminder, lock device, confirm notification visible and "New alarm" opens over keyguard. |
| Samsung (One UI) | Per-category lock-screen visibility toggle in Settings; battery optimization "Put unused apps to sleep". | Channel created with `VISIBILITY_PUBLIC`; ongoing flag resists cleanup; onboarding links to channel + battery settings. | Settings → Notifications → Remainder → Lock screen = Show content; Settings → Battery → Remainder not sleeping. |
| Xiaomi / MIUI | "Autostart" gate can block background notifications; older MIUI ignored `showWhenLocked` window flags. | Uses API 27+ Activity `setShowWhenLocked` (not window flags); ongoing notification. | Security → Permissions → Autostart → Remainder ON; lock device and confirm alarm form opens. |
| Huawei / EMUI | "Protected apps" / power management can kill notifications; keyguard dismissal varies. | Ongoing + public notification; onboarding battery step requests ignore-battery-optimizations. | Settings → Battery → App launch → Remainder managed manually; confirm reminder persists after screen-off. |
| Oppo / Realme / Vivo | "Startup management" can suppress background notifications. | Same ongoing-notification + battery-optimization request path. | Settings → Battery → Allow background activity for Remainder. |
| Stock Android 13+ | `POST_NOTIFICATIONS` prompt on first enable. | `NotificationPermissionPolicy` gates on SDK 33; onboarding requests runtime permission. | Deny on first launch, then grant via Settings, confirm reminder appears. |

## Manual verification checklist (run per device/OEM)

1. Install debug build, complete onboarding (notifications, lock screen, battery).
2. Enable the lock-screen reminder from Settings; confirm the persistent notification appears.
3. Lock the device; confirm the reminder is visible on the lock screen (public visibility).
4. Tap the reminder; confirm `LockScreenAlarmActivity` opens **over the keyguard** without unlocking.
5. On Android 13+: deny `POST_NOTIFICATIONS`, confirm the reminder is hidden; grant it, confirm it returns.
6. Reboot the device; confirm the channel still exists and the reminder can be re-shown from Settings.
7. Leave the device idle past the OEM battery threshold; confirm the ongoing reminder survives (autostart/protected-app settings may need enabling — see OEM table).

## Automated test summary (added 2026-09-15)

| File | SDKs exercised | What it guarantees |
|---|---|---|
| `LockScreenNotificationControllerCompatibilityTest` | 27, 29, 31, 32, 33, 34, 35 | Channel creation, public visibility, ongoing flag, tap intent, hide, idempotency are invariant across SDKs. |
| `NotificationPermissionReaderSdkTest` | 27, 29, 31, 32, 33, 34, 35 | Pre-33 grants access by default; 33+ denies by default (boundary pinned). |
| `LockScreenAlarmKeyguardApiTest` | n/a (source guard) | Modern keyguard Activity APIs used; deprecated window flags forbidden. |
