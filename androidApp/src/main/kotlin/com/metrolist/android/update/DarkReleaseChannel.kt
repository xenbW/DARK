/*
 * DARK, based on the Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.android.update

import com.metrolist.shared.update.ReleaseVersion

internal fun isDarkReleaseSource(release: GitHubRelease): Boolean =
    ReleaseVersion.parse(release.tagName) != null &&
        release.htmlUrl == "https://github.com/xenbW/DARK/releases/tag/${release.tagName}"

internal fun acceptsDarkReleaseChannel(
    release: GitHubRelease,
    currentVersion: String,
): Boolean {
    if (release.draft || !isDarkReleaseSource(release)) return false
    val current = ReleaseVersion.parse(currentVersion) ?: return false
    return current.prerelease.isNotEmpty() || release.isPublishedStable
}
