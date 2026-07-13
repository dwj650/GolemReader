package com.golemreader.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.golemreader.R
import com.golemreader.theme.GolemTheme

@Composable
fun GolemBottomNavigation(
    selectedTab: GolemTab,
    onTabSelected: (GolemTab) -> Unit,
) {
    val tokens = GolemTheme.tokens
    NavigationBar(containerColor = tokens.colors.navigationBarBackground) {
        bottomNavigationTabs().forEach { tab ->
            NavigationBarItem(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                modifier = Modifier.testTag("bottom-nav-${tab.name.lowercase()}"),
                icon = {
                    Icon(
                        painter = painterResource(
                            when (tab) {
                                GolemTab.NowPlaying -> R.drawable.ic_now_playing
                                GolemTab.Settings -> R.drawable.ic_settings
                            },
                        ),
                        contentDescription = null,
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        style = tokens.typography.label,
                    )
                },
            )
        }
    }
}
