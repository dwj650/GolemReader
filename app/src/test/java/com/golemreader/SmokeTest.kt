package com.golemreader

import org.junit.Assert.assertEquals
import org.junit.Test

class SmokeTest {
    @Test
    fun appNameIsGolemReader() {
        assertEquals("Golem Reader", AppInfo.name)
    }
}
