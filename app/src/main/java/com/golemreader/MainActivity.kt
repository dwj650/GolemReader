package com.golemreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.golemreader.bootstrap.BookBootstrap
import com.golemreader.storage.GolemStorageSubstrate
import com.golemreader.theme.ThemeChoice
import com.golemreader.theme.ThemeSettingsRepository
import com.golemreader.ui.GolemReaderApp
import kotlinx.coroutines.launch

object AppInfo {
    const val name = "Golem Reader"
}

class MainActivity : ComponentActivity() {
    private var storage: GolemStorageSubstrate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeRepository = initializeThemeRepository()
        val bootstrap = BookBootstrap(this).start()
        setContent {
            val themeChoice by themeRepository.choiceFlow()
                .collectAsState(initial = ThemeChoice.FollowSystem)
            val themeWriteScope = rememberCoroutineScope()
            GolemReaderApp(
                bookTitle = bootstrap.bookTitle,
                sentences = bootstrap.sentences,
                highlightEmitter = bootstrap.highlightEmitter,
                starvationState = bootstrap.starvationState,
                themeChoice = themeChoice,
                onThemeChoiceSelected = { choice ->
                    themeWriteScope.launch { themeRepository.setChoice(choice) }
                },
                transportControls = bootstrap.transportControls,
            )
        }
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
}
