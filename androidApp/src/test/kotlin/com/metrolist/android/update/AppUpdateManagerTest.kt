package com.metrolist.android.update

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppUpdateManagerTest {
    @Test
    fun apkSelectionAcceptsOnlyTheDarkReleaseAsset() {
        val release = darkRelease()
        assertEquals("DARK-v0.2.0-alpha.apk", release.apk?.name)
        assertNull(release.copy(htmlUrl = "https://github.com/MetrolistGroup/Metrolist/releases/tag/v0.2.0-alpha").apk)
        assertNull(release.copy(assets = release.assets + release.assets).apk)
        assertNull(release.copy(assets = release.assets.map { it.copy(state = "new") }).apk)
        assertNull(release.copy(assets = release.assets.map { it.copy(downloadUrl = "https://example.com/update.apk") }).apk)
        assertNull(release.copy(assets = release.assets.map { it.copy(name = "Metrolist.apk") }).apk)
    }

    @Test
    fun alphaChannelAcceptsDarkPrereleasesButStableChannelDoesNot() {
        assertTrue(acceptsDarkReleaseChannel(darkRelease(), "0.1.0-alpha"))
        assertFalse(acceptsDarkReleaseChannel(darkRelease(), "0.1.0"))
        assertFalse(acceptsDarkReleaseChannel(darkRelease().copy(draft = true), "0.1.0-alpha"))
        assertFalse(
            acceptsDarkReleaseChannel(
                darkRelease().copy(htmlUrl = "https://github.com/MetrolistGroup/Metrolist/releases/tag/v0.2.0-alpha"),
                "0.1.0-alpha",
            ),
        )
        assertTrue(AppUpdateManager.compareVersions("0.2.0-alpha", "0.1.0-alpha") > 0)
    }

    private fun darkRelease() =
        GitHubRelease(
            tagName = "v0.2.0-alpha",
            prerelease = true,
            htmlUrl = "https://github.com/xenbW/DARK/releases/tag/v0.2.0-alpha",
            assets =
                listOf(
                    GitHubReleaseAsset(
                        "DARK-v0.2.0-alpha.apk",
                        "uploaded",
                        100,
                        downloadUrl = "https://github.com/xenbW/DARK/releases/download/v0.2.0-alpha/DARK-v0.2.0-alpha.apk",
                    ),
                ),
        )

    @Test
    fun apkSignerMustMatchCurrentSignerOrProveForwardRotation() {
        val old = "old".encodeToByteArray()
        val current = "current".encodeToByteArray()
        val next = "next".encodeToByteArray()

        assertTrue(AppUpdateManager.signerDigestsMatch(listOf(current), listOf(current), listOf(old, current)))
        assertTrue(AppUpdateManager.signerDigestsMatch(listOf(current), listOf(next), listOf(old, current, next)))
        assertFalse(AppUpdateManager.signerDigestsMatch(listOf(current), listOf(old), listOf(old)))
        assertFalse(AppUpdateManager.signerDigestsMatch(listOf(current), listOf(next), listOf(next)))
        assertTrue(AppUpdateManager.signerDigestsMatch(listOf(old, current), listOf(current, old), emptyList()))
        assertFalse(AppUpdateManager.signerDigestsMatch(listOf(old, current), listOf(current), listOf(old, current)))
        assertFalse(AppUpdateManager.signerDigestsMatch(emptyList(), listOf(current), listOf(current)))
        assertFalse(AppUpdateManager.signerDigestsMatch(listOf(current), emptyList(), listOf(current)))
        assertFalse(AppUpdateManager.signerDigestsMatch(emptyList(), emptyList(), listOf(current)))
    }
}
