package com.golemreader.theme

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class NoHardcodeGuardTest {
    private val repoRoot = findRepoRoot()

    @Test
    fun guardPassesCurrentCodebase() {
        val result = runGuard()

        assertEquals(result.output, 0, result.exitCode)
    }

    @Test
    fun guardFailsSeededUiLiteralOutsideThemePackage() {
        val seed = File(repoRoot, "app/src/main/java/com/golemreader/ui/SeededHardcodeViolation.kt")
        try {
            seed.writeText(
                """
                package com.golemreader.ui

                private val seededViolation = androidx.compose.ui.graphics.Color(0xFF123456)
                """.trimIndent(),
            )

            val result = runGuard()

            assertNotEquals(result.output, 0, result.exitCode)
        } finally {
            seed.delete()
        }
    }

    private fun runGuard(): ProcessResult {
        val process = ProcessBuilder("bash", "guards/no-hardcode-check.sh")
            .directory(repoRoot)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        return ProcessResult(process.waitFor(), output)
    }

    private fun findRepoRoot(): File =
        generateSequence(File(System.getProperty("user.dir")).absoluteFile) { it.parentFile }
            .first { File(it, "guards/no-hardcode-check.sh").isFile }

    private data class ProcessResult(
        val exitCode: Int,
        val output: String,
    )
}
