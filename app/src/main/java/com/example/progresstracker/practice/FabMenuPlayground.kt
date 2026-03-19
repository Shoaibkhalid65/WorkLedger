package com.example.progresstracker.practice

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FabMenuPlaygroundScreen() {
    var fabExpanded by remember { mutableStateOf(false) }
    val fabMenus =
        remember { listOf(Icons.Default.CameraAlt, Icons.Default.Image, Icons.Default.BrokenImage) }
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButtonMenu(
                expanded = fabExpanded,
                button = {
                    ToggleFloatingActionButton(
                        checked = fabExpanded,
                        onCheckedChange = {
                            fabExpanded = it
                        },
                        containerCornerRadius = { 100.dp }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier
                                .graphicsLayer {
                                    rotationZ = checkedProgress * 135f
                                }
                        )
                    }
                }
            ) {
                fabMenus.forEach { icon ->
                    AnimatedVisibility(
                        visible = fabExpanded,
                        enter = scaleIn(animationSpec = tween(300)),
                        exit = scaleOut(animationSpec = tween(300))
                    ) {
                        SmallFloatingActionButton(
                            onClick = {
                                Toast.makeText(context, "${icon.name} Clicked!", Toast.LENGTH_SHORT)
                                    .show()
                            },
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Main Content"
            )
        }
    }
}


@Preview
@Composable
fun FabMenuPlaygroundScreen1() {
    var fabExpanded by remember { mutableStateOf(false) }

    val fabItems = listOf(
        Pair(Icons.Default.CameraAlt, "Camera"),
        Pair(Icons.Default.Image, "Gallery"),
        Pair(Icons.Default.PhotoLibrary, "Custom Gallery")
    )

    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fabItems.forEach { (icon, label) ->
                    AnimatedVisibility(
                        visible = fabExpanded,
                        enter = scaleIn(animationSpec = tween(300)),
                        exit = scaleOut(animationSpec = tween(300))
                    ) {
                        SmallFloatingActionButton(
                            onClick = {
                                Toast.makeText(context, "$label Clicked!", Toast.LENGTH_SHORT).show()
                                fabExpanded = false
                            },
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label
                            )
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { fabExpanded = !fabExpanded },
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (fabExpanded) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = if (fabExpanded) "Close" else "Add",
                        modifier = Modifier.rotate(if (fabExpanded) 135f else 0f)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Main Content")
        }
    }
}
