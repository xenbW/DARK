package com.metrolist.android.update

import com.metrolist.shared.settings.Settings
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [33])
class AppUpdatePrereleaseRegressionTest {
    @Before
    fun setUp() {
        Settings.replaceSharedForTest(Settings(MapSettings()))
        Settings.shared.updateCacheSource = CACHE_SOURCE
    }

    @After
    fun tearDown() {
        Settings.replaceSharedForTest(null)
    }

    @Test
    fun validDarkAlphaCanBeOfferedFromCache() =
        runTest {
            Settings.shared.cachedAvailableUpdateJson =
                """
                {
                  "tag_name":"v999.0.0-alpha",
                  "prerelease":true,
                  "html_url":"https://github.com/xenbW/DARK/releases/tag/v999.0.0-alpha",
                  "assets":[{
                    "name":"DARK-v999.0.0-alpha.apk", "state":"uploaded", "size":100,
                    "browser_download_url":"https://github.com/xenbW/DARK/releases/download/v999.0.0-alpha/DARK-v999.0.0-alpha.apk"
                  }]
                }
                """.trimIndent()
            assertEquals(
                "999.0.0-alpha",
                AppUpdateManager.checkAtStartup(RuntimeEnvironment.getApplication(), networkEnabled = false)?.version,
            )
        }

    @Test
    fun oldMetrolistCachesAreClearedEvenWithoutNetwork() =
        runTest {
            Settings.shared.updateCacheSource = "github-releases-metrolist-v1"
            Settings.shared.cachedAvailableUpdateJson =
                """{"tag_name":"v999.0.0","html_url":"https://github.com/MetrolistGroup/Metrolist/releases/tag/v999.0.0"}"""
            assertNull(AppUpdateManager.checkAtStartup(RuntimeEnvironment.getApplication(), networkEnabled = false))
            assertEquals(CACHE_SOURCE, Settings.shared.updateCacheSource)
            assertEquals("", Settings.shared.cachedAvailableUpdateJson)
        }

    @Test
    fun foreignReleaseIsNeverOfferedFromTheDarkCache() =
        runTest {
            Settings.shared.cachedAvailableUpdateJson =
                """{"tag_name":"v999.0.0","html_url":"https://github.com/MetrolistGroup/Metrolist/releases/tag/v999.0.0"}"""
            assertNull(AppUpdateManager.checkAtStartup(RuntimeEnvironment.getApplication(), networkEnabled = false))
        }

    @Test
    fun releaseHistoryRejectsForeignAndIncompleteReleases() =
        runTest {
            Settings.shared.cachedUpdateReleasesJson =
                """[{"tag_name":"v999.0.0","html_url":"https://github.com/MetrolistGroup/Metrolist/releases/tag/v999.0.0"}]"""
            AppUpdateManager.loadReleaseHistory()
            assertEquals(emptyList<GitHubRelease>(), AppUpdateManager.releases.value)
        }
}
