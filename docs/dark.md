# DARK for Android

DARK is a personal-use fork of Metrolist. The original Metrolist copyright notices, contributor credits, community links and [GPLv3 license](../LICENSE) are retained. The Android About screen displays DARK while retaining the original project's credits.

## Android identity and appearance

The Android application ID is `io.github.xenbw.dark`; debug builds use `io.github.xenbw.dark.debug`. Android namespaces and source packages remain unchanged. Providers derive their authorities from the application ID, and launcher shortcuts and widget actions use the DARK action prefix. DARK can therefore be installed alongside Metrolist with separate application data.

Android uses a fixed black background, white primary text and neutral gray secondary roles, including at startup in either system theme. Wallpaper and artwork colors no longer recolor the base application theme. Existing appearance preferences remain stored; light mode and base theme color selections do not override this phase's fixed DARK palette. The player uses a black background, a large square cover with a subtle extracted-color border and shadow, compact icon controls and the existing wavy progress renderer. Wave animation follows the existing playback, visibility and reduced-motion rules; it does not analyze audio or require microphone access. Phone portrait/landscape and tablet layouts share these controls. Fullscreen artwork and animated canvas preferences remain stored, but DARK uses the static cover layout. Synchronized lyrics retain their existing presentation.

The default launcher icon is an independent monochrome D, including Android themed icons. GPL notices and original contributor credits are retained. Shared application names for desktop and iOS remain Metrolist. The Android name uses the non-translatable `dark_app_name` entry in the shared resource catalog.

## Build

Follow [development.md](development.md) for Temurin 25, Android SDK 37.1 and native toolchains. Run:

```sh
./gradlew :androidApp:assembleDebug --no-scan
adb install -r androidApp/build/outputs/apk/debug/androidApp-debug.apk
```

Debug signing is for local testing. Release signing retains the existing environment-based configuration. The DARK distribution workflow below overrides version metadata for alpha APKs. The inherited Metrolist stable/nightly workflows remain unchanged. DARK's Android updater queries only `xenbW/DARK` releases and requires the exact `DARK-<tag>.apk` asset from that repository. Old Metrolist update caches are discarded, including offline. Alpha installations receive newer DARK prereleases; stable installations only receive stable versions.

Use the [Android checks](checks.md#platform-checks) and verify startup, navigation, playback, search, library, recommendations and system media controls on a device. Phone/tablet layouts, landscape, Samsung media controls and playback behavior require device verification; a successful APK build alone does not establish those results.

## GitHub Actions distribution

`.github/workflows/dark-android-alpha.yml` builds and tests pushes to `feat/dark-phase2`. `.github/dark-alpha-tag.txt` selects the next prerelease version. These pushes do not publish a release. The workflow uses GitHub-hosted Ubuntu runners, Temurin 25 and pinned Android toolchains; it does not merge or push to `main`.

The build runs `assembleDebug`, Android lint, Android/common-source ktlint, all Android unit tests and shared Android host tests. It verifies the signing certificate, APK package, label and version. A separate API 35 emulator job downloads the original v0.1.0-alpha APK, checks its pinned SHA-256, installs it, writes a private data probe and installs the new APK with `adb install -r`. It then verifies that the data survived. This checks Android's signature/version compatibility and data retention; it does not establish visual behavior or Samsung-specific integration.

After reviewing a successful run, publish its exact tested commit without rebuilding:

```sh
git tag dark-release-v0.2.0-alpha <tested-commit>
git push origin dark-release-v0.2.0-alpha
```

The internal `dark-release-` tag triggers publication to the `v0.2.0-alpha` prerelease. The publication job uses `contents: write` and `actions: read`, finds a successful phase-2 build at that exact commit, requires a successful emulator upgrade job, and downloads that run's immutable artifact. It checks the artifact's source/version metadata and SHA-256 before uploading. It keeps the release in draft until a download of the uploaded APK matches the tested file. Attachments include the APK, SHA-256 and build JSON. Artifacts remain available for 30 days; if they expire, run a new verified build before publishing.

While the workflow exists only on a development branch, GitHub may not offer its manual dispatch button. Branch pushes and rerunning an existing run remain available. Never publish a release from a failed or unfinished verification run.

### Signing continuity

Create a repository Actions secret named `DARK_DEBUG_KEYSTORE_BASE64` containing the base64 encoding of the original DARK debug keystore. The original keystore is still in this Cloud environment at `/home/agent/.android/debug.keystore`; transferring this private signing credential to GitHub requires the owner's explicit authorization. Never commit the keystore or attach it to a release. The workflow restores it only on the build runner, excludes Gradle configuration caching and removes the file at the end of the job.

The pinned public certificate SHA-256 is:

```text
cf35916b8688602f910bf579cc1170389d8e463832cc994523300fd313196469
```

The workflow restores the keystore under `RUNNER_TEMP` and passes its explicit path to Gradle with `-PdarkDebugKeystore`. It does not depend on the runner's default Android debug keystore location. The workflow fails before compiling if the secret is missing, invalid or signed by a different certificate. Keep a secure backup of the original keystore: losing it prevents updates to installations signed with that key. This remains a debug testing channel, not production release signing. The package stays `io.github.xenbw.dark.debug`; switching to the release package or a new key creates a separate installation or requires an explicit migration plan.

### Future updates and versions

For another prerelease, update `.github/dark-alpha-tag.txt`, push and wait for every verification job to pass before pushing the matching internal publication tag. Internal `versionName` comes from the file and `versionCode` is `154 + github.run_number`, exceeding the installed v0.1.0-alpha code of 156 from subsequent runs. Keep the workflow filename and its run-number sequence stable. Reruns retain their versionCode. Publishing reuses the verified build's code, not the publication run number.

Gradle accepts `-PdarkVersionCode` for CI without changing the normal local value 154. The workflow also passes `-PappVersion`, leaving the shared version property untouched. Existing release tags are not moved: release notes and the build JSON link to the actual source commit and Actions run. The previous pre-release tag can therefore still identify the first phase's original commit; the notes identify the revision compiled by Actions.

### Repository setup and recovery

In GitHub, open **Settings → Actions → General** and enable Actions for this repository if disabled. Allow the pinned actions used by the workflow. The publication job requests `contents: write`; repository or organization policy must allow that permission. An Actions secret administrator must add `DARK_DEBUG_KEYSTORE_BASE64` in **Settings → Secrets and variables → Actions** after authorizing transfer of the original key.

If a run stops at "Restore original DARK signing key", configure the secret and rerun the failed jobs from the Actions page. If only publication fails, rerun the publication tag workflow while the verified build artifact is retained. Inspect the exact failed step rather than assuming a successful build implies publication. Never replace an APK with an unverified or differently signed file.
