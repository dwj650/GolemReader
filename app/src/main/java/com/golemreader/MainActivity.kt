package com.golemreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.golemreader.ui.GolemReaderApp

object AppInfo {
    const val name = "Golem Reader"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GolemReaderApp()
        }
    }
}
