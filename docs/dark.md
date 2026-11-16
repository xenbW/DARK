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

Debug signing is for local testing. Release signing retains the existing environment-based configuration. The DARK distribution workflow below overrides version metadata for alpha APKs. The inherited Metrolist stable/nightly workflows remain unchanged; DARK's updater integration is not changed by this distribution workflow.

Use the [Android checks](checks.md#platform-checks) and verify startup, navigation, playback, search, library, recommendations and system media controls on a device. Phone/tablet layouts, landscape, Samsung media controls and playback behavior require device verification; a successful APK build alone does not establish those results.

## GitHub Actions distribution

`.github/workflows/dark-android-alpha.yml` builds on pushes to `feat/dark-base-theme` and supports `workflow_dispatch` with a prerelease tag. It uses GitHub-hosted Ubuntu runners, Temurin 25 and the pinned Android toolchains. It does not merge or push to `main`. While the workflow exists only on this development branch, GitHub may not offer its manual dispatch button; pushes to this branch and rerunning an existing Actions run remain the supported triggers.

The build runs `assembleDebug`, ktlint and the palette/notification tests. Before publishing, it verifies the APK signature, package, label and version. The publication job uses `contents: write` and the runner's `GITHUB_TOKEN` to attach the APK, SHA-256 checksum and build metadata to `v0.1.0-alpha`. It keeps the release in draft while replacing files, downloads the uploaded APK again and compares it with the build output before publishing. A workflow artifact is also retained for 30 days if the release upload fails. A failed publication can leave a draft; rerun publication to finish it.

### Signing continuity

Create a repository Actions secret named `DARK_DEBUG_KEYSTORE_BASE64` containing the base64 encoding of the original DARK debug keystore. The original keystore is still in this Cloud environment at `/home/agent/.android/debug.keystore`; transferring this private signing credential to GitHub requires the owner's explicit authorization. Never commit the keystore or attach it to a release. The workflow restores it only on the build runner, excludes Gradle configuration caching and removes the file at the end of the job.

The pinned public certificate SHA-256 is:

```text
cf35916b8688602f910bf579cc1170389d8e463832cc994523300fd313196469
```

The workflow fails before compiling if the secret is missing, invalid or signed by a different certificate. It never generates a substitute key. Keep a secure backup of the original keystore: losing it prevents updates to installations signed with that key. This remains a debug testing channel, not production release signing. The package stays `io.github.xenbw.dark.debug`; switching to the release package or a new key creates a separate installation or requires an explicit migration plan.

### Future updates and versions

Pushes publish to `v0.1.0-alpha` by default. Manual dispatch accepts another prerelease tag, for example `v0.2.0-alpha`. Internal `versionName` comes from the tag and `versionCode` is `154 + github.run_number`, so the first run uses code 155, greater than the original APK's 154. Keep the workflow filename and its run-number sequence stable. Reruns of the same run retain the same versionCode.

Gradle accepts `-PdarkVersionCode` for CI without changing the normal local value 154. The workflow also passes `-PappVersion`, leaving the shared version property untouched. Existing release tags are not moved: release notes and the build JSON link to the actual source commit and Actions run. The previous pre-release tag can therefore still identify the first phase's original commit; the notes identify the revision compiled by Actions.

### Repository setup and recovery

In GitHub, open **Settings → Actions → General** and enable Actions for this repository if disabled. Allow the pinned actions used by the workflow. The publication job requests `contents: write`; repository or organization policy must allow that permission. An Actions secret administrator must add `DARK_DEBUG_KEYSTORE_BASE64` in **Settings → Secrets and variables → Actions** after authorizing transfer of the original key.

If a run stops at "Restore original DARK signing key", configure the secret and rerun the failed jobs from the Actions page. If only publication fails, rerun that job while the build artifact is retained. Inspect the exact failed step rather than assuming a successful build implies publication. Never replace an APK with an unverified or differently signed file.
