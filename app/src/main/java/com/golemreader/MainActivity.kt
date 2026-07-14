package com.golemreader

import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.golemreader.bootstrap.BookBootstrap
import com.golemreader.storage.GolemStorageSubstrate
import com.golemreader.theme.ThemeChoice
import com.golemreader.theme.ThemeSettingsRepository
import com.golemreader.theme.TextScaleStep
import com.golemreader.theme.effectiveReducedMotion
import com.golemreader.ui.GolemReaderApp
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow

object AppInfo {
    const val name = "Golem Reader"
}

class MainActivity : ComponentActivity() {
    private var storage: GolemStorageSubstrate? = null
    private val osRemoveAnimations = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeRepository = initializeThemeRepository()
        val bootstrap = BookBootstrap(this).start()
        refreshOsRemoveAnimations()
        setContent {
            val themeChoice by themeRepository.choiceFlow()
                .collectAsState(initial = ThemeChoice.FollowSystem)
            val highContrast by themeRepository.highContrastFlow()
                .collectAsState(initial = false)
            val textScale by themeRepository.textScaleFlow()
                .collectAsState(initial = TextScaleStep.Default)
            val inAppReducedMotion by themeRepository.reducedMotionFlow()
                .collectAsState(initial = false)
            val osReducedMotion by osRemoveAnimations.collectAsState()
            val themeWriteScope = rememberCoroutineScope()
            GolemReaderApp(
                bookTitle = bootstrap.bookTitle,
                sentences = bootstrap.sentences,
                highlightEmitter = bootstrap.highlightEmitter,
                starvationState = bootstrap.starvationState,
                themeChoice = themeChoice,
                highContrast = highContrast,
                textScale = textScale,
                reducedMotion = effectiveReducedMotion(osReducedMotion, inAppReducedMotion),
                inAppReducedMotion = inAppReducedMotion,
                onThemeChoiceSelected = { choice ->
                    themeWriteScope.launch { themeRepository.setChoice(choice) }
                },
                onHighContrastToggled = { enabled ->
                    themeWriteScope.launch { themeRepository.setHighContrast(enabled) }
                },
                onTextScaleChanged = { step ->
                    themeWriteScope.launch { themeRepository.setTextScale(step) }
                },
                onReducedMotionToggled = { enabled ->
                    themeWriteScope.launch { themeRepository.setReducedMotion(enabled) }
                },
                transportControls = bootstrap.transportControls,
            )
        }
    }

    override fun onResume() {
        super.onResume()
        refreshOsRemoveAnimations()
    }

    override fun onDestroy() {
        storage?.preciousDatabase?.close()
        storage = null
        super.onDestroy()
    }

    private fun initializeThemeRepository(): ThemeSettingsRepository {
        val initialized = GolemStorageSubstrate.initialize(this)
        storage = initialized
        return initialized.preciousDatabase.themeSettingsRepository()
    }

    private fun refreshOsRemoveAnimations() {
        osRemoveAnimations.value = Settings.Global.getFloat(
            contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        ) == 0f
    }
}
