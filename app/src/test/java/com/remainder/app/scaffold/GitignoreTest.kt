package com.remainder.app.scaffold

import com.remainder.app.testutil.ProjectPaths
import org.junit.Assert.assertTrue
import org.junit.Test

class GitignoreTest {

    private val gitignore: String
        get() = ProjectPaths.rootDir.resolve(".gitignore").readText()

    @Test
    fun ignoresGradleCache() {
        assertTrue(gitignore.contains(".gradle"))
    }

    @Test
    fun ignoresBuildOutputs() {
        assertTrue(gitignore.contains("/build") || gitignore.contains("build/"))
    }

    @Test
    fun ignoresLocalProperties() {
        assertTrue(gitignore.contains("local.properties"))
    }

    @Test
    fun ignoresCursorHarness() {
        assertTrue(gitignore.contains(".cursor/"))
    }

    @Test
    fun ignoresProjectState() {
        assertTrue(gitignore.contains(".state/"))
    }
}
