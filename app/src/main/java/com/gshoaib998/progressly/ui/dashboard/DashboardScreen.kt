package com.gshoaib998.progressly.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.gshoaib998.progressly.navigation.Screen
import com.gshoaib998.progressly.ui.dashboard.charts.BarChart
import com.gshoaib998.progressly.ui.dashboard.charts.BarChartData
import com.gshoaib998.progressly.ui.dashboard.charts.LineChart
import com.gshoaib998.progressly.ui.dashboard.charts.LineChartData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // Fade-in the whole screen once loading is done
    val screenAlpha by animateFloatAsState(
        targetValue = if (uiState.isLoading) 0f else 1f,
        animationSpec = tween(400),
        label = "screen_fade"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Dashboard",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.AppearanceSettingsScreen.route) }) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Appearance settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                windowInsets = WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        contentWindowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .alpha(screenAlpha)
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // ── Today Summary ─────────────────────────────────────────
            DashboardSection(
                title = "Today's Overview",
                icon = {
                    Icon(
                        Icons.Outlined.SentimentSatisfied,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryCard(
                        label = "Hours worked",
                        value = "%.1f h".format(uiState.todaySummary.totalHours),
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    SummaryCard(
                        label = "Sessions",
                        value = "${uiState.todaySummary.tasksDone}",
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    SummaryCard(
                        label = "Avg satisfaction",
                        value = "%.0f%%".format(uiState.todaySummary.avgSatisfaction),
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            // ── Weekly Bar Chart ──────────────────────────────────────
            DashboardSection(
                title = "This Week — Work Hours",
                icon = {
                    Icon(
                        Icons.Default.WorkHistory,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                if (uiState.weeklyDurations.isEmpty() || uiState.weeklyDurations.all { it.totalMillis == 0L }) {
                    EmptyChartPlaceholder("No duration data yet")
                } else {
                    DashboardChartCard {
                        BarChart(
                            data = uiState.weeklyDurations.map { entry ->
                                BarChartData(
                                    label = viewModel.dayLabel(entry.dateEpoch),
                                    value = entry.totalMillis / 3_600_000f,
                                    unit = "h"
                                )
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                            barColor = MaterialTheme.colorScheme.primary,
                            selectedBarColor = MaterialTheme.colorScheme.tertiary,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            gridColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }

            // ── Satisfaction Trend ────────────────────────────────────
            DashboardSection(
                title = "30-Day Satisfaction Trend",
                icon = {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                if (uiState.satisfactionTrend.isEmpty()) {
                    EmptyChartPlaceholder("No satisfaction data yet")
                } else {
                    DashboardChartCard {
                        LineChart(
                            data = uiState.satisfactionTrend.map { entry ->
                                LineChartData(
                                    label = viewModel.dayLabel(entry.dateEpoch),
                                    value = entry.avgPercent
                                )
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                            lineColor = MaterialTheme.colorScheme.primary,
                            fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            gridColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Section with icon + title ─────────────────────────────────────────────────

@Composable
fun DashboardSection(
    title: String,
    icon: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            icon?.invoke()
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        content()
    }
}

// ── Chart Card ────────────────────────────────────────────────────────────────

@Composable
fun DashboardChartCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        content()
    }
}

// ── Summary Card — now color-coded per slot ───────────────────────────────────

@Composable
fun SummaryCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surfaceVariant.copy(
        alpha = 0.6f
    ),
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = contentColor.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

// ── Empty State ───────────────────────────────────────────────────────────────

@Composable
fun EmptyChartPlaceholder(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}