package com.example.progresstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.progresstracker.navigation.AppNavGraph
import com.example.progresstracker.navigation.BottomBarDestination
import com.example.progresstracker.practice.SearchSortPracticeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavGraph()
        }
    }
}


@Composable
fun AppCustomBottomBar(navHostController: NavHostController) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BottomBarDestination.entries.forEachIndexed { index, destination ->
            CustomNavigationBarItem(
                selected = selectedIndex == index,
                onClick = {
                    navHostController.navigate(destination.route)
                    selectedIndex = index
                },
                icon = {
                    Icon(
                        imageVector = if (selectedIndex == index) destination.selectedIcon else destination.unSelectedIcon,
                        contentDescription = "bottom bar item icon",
                    )
                }
            )
        }
    }
}

@Composable
fun CustomNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    text: @Composable (() -> Unit)? = null,
    alwaysShowText: Boolean = false
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.clickable(
            interactionSource = null,
            indication = null,
            onClick = onClick
        )
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)) {
            icon()
            text?.let {
                if (alwaysShowText || selected) {
                    it()
                }
            }

        }
        Box(
            modifier = Modifier
                .size(60.dp, 40.dp)
                .background(
                    if (selected) MaterialTheme.colorScheme.primary.copy(0.2f) else Color.Transparent,
                    CircleShape
                )
        )
    }
}

