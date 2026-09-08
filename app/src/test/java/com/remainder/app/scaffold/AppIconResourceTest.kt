package com.remainder.app.scaffold

import com.remainder.app.testutil.ProjectPaths
import org.junit.Assert.assertTrue
import org.junit.Test

class AppIconResourceTest {

    @Test
    fun adaptiveLauncherIconExists() {
        val icon = ProjectPaths.appDir.resolve("src/main/res/mipmap-anydpi-v26/ic_launcher.xml")
        assertTrue("Expected adaptive launcher icon at ${icon.path}", icon.isFile)
    }

    @Test
    fun roundLauncherIconExists() {
        val icon = ProjectPaths.appDir.resolve("src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml")
        assertTrue("Expected round launcher icon at ${icon.path}", icon.isFile)
    }

    @Test
    fun foregroundVectorExists() {
        val foreground = ProjectPaths.appDir.resolve("src/main/res/drawable/ic_launcher_foreground.xml")
        assertTrue("Expected launcher foreground vector at ${foreground.path}", foreground.isFile)
    }

    @Test
    fun backgroundDrawableExists() {
        val background = ProjectPaths.appDir.resolve("src/main/res/drawable/ic_launcher_background.xml")
        assertTrue("Expected launcher background at ${background.path}", background.isFile)
    }

    @Test
    fun manifestReferencesLauncherIcon() {
        val manifest = ProjectPaths.appDir.resolve("src/main/AndroidManifest.xml").readText()
        assertTrue(manifest.contains("@mipmap/ic_launcher"))
        assertTrue(manifest.contains("@mipmap/ic_launcher_round"))
    }
}
