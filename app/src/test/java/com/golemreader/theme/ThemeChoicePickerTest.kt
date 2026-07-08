package com.golemreader.theme

import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeChoicePickerTest {
    @Test
    fun pickerOffersFollowSystemLightAndDarkOnly() {
        assertEquals(
            listOf(
                ThemeChoiceOption(ThemeChoice.FollowSystem, "System"),
                ThemeChoiceOption(ThemeChoice.Light, "Light"),
                ThemeChoiceOption(ThemeChoice.Dark, "Dark"),
            ),
            themeChoiceOptions(),
        )
    }
}
