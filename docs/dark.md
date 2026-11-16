# DARK for Android

DARK is a personal-use fork of Metrolist. The original Metrolist copyright notices, contributor credits, community links and [GPLv3 license](../LICENSE) are retained. The Android About screen displays DARK while retaining the original project's credits.

## Phase one

The Android application ID is `io.github.xenbw.dark`; debug builds use `io.github.xenbw.dark.debug`. Android namespaces and source packages remain unchanged. Providers derive their authorities from the application ID, and launcher shortcuts and widget actions use the DARK action prefix. DARK can therefore be installed alongside Metrolist with separate application data.

Android uses a fixed black background, white primary text and neutral gray secondary roles, including at startup in either system theme. Wallpaper and artwork colors no longer recolor the base application theme. Existing appearance preferences remain stored; light mode and base theme color selections do not override this phase's fixed DARK palette. Player-specific palettes, artwork extraction, layouts and synchronized lyrics are unchanged; their visual redesign is deferred.

The launcher icon, GPL notices and original contributor credits are unchanged. Shared application names for desktop and iOS remain Metrolist. The Android name uses the non-translatable `dark_app_name` entry in the shared resource catalog.

## Build

Follow [development.md](development.md) for Temurin 25, Android SDK 37.1 and native toolchains. Run:

```sh
./gradlew :androidApp:assembleDebug --no-scan
adb install -r androidApp/build/outputs/apk/debug/androidApp-debug.apk
```

Debug signing is for local testing. Release signing retains the existing environment-based configuration. No version bump or upstream publishing configuration is part of this phase. Before distributing future updates, review the inherited updater and release workflow configuration for this fork.

Use the [Android checks](checks.md#platform-checks) and verify startup, navigation, playback, search, library, recommendations and system media controls on a device. Phone/tablet layouts, landscape, Samsung media controls and playback behavior require device verification; a successful APK build alone does not establish those results.
