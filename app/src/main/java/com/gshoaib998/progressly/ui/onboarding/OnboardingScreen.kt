package com.gshoaib998.progressly.ui.onboarding

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val onPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer
    val surface = MaterialTheme.colorScheme.surface
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> TaskTrackingPage(
                    primary = primary,
                    primaryContainer = primaryContainer,
                    onPrimaryContainer = onPrimaryContainer,
                    surface = surface,
                    onSurface = onSurface,
                    onSurfaceVariant = onSurfaceVariant,
                    outlineVariant = outlineVariant
                )
                1 -> TimerPage(
                    primary = primary,
                    secondary = secondary,
                    primaryContainer = primaryContainer,
                    surface = surface,
                    onSurface = onSurface,
                    onSurfaceVariant = onSurfaceVariant,
                    outlineVariant = outlineVariant
                )
                2 -> GoalPage(
                    primary = primary,
                    secondary = secondary,
                    tertiary = tertiary,
                    primaryContainer = primaryContainer,
                    surface = surface,
                    onSurface = onSurface,
                    onSurfaceVariant = onSurfaceVariant
                )
            }
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Page indicator
            PagerIndicator(
                pageCount = 3,
                currentPage = pagerState.currentPage,
                activeColor = MaterialTheme.colorScheme.primary,
                inactiveColor = MaterialTheme.colorScheme.outlineVariant
            )

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skip — only visible before last page
                if (pagerState.currentPage < 2) {
                    TextButton(onClick = {
                        viewModel.completeOnboarding()
                        onFinished()
                    }) {
                        Text(
                            "Skip",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Spacer(Modifier.width(72.dp))
                }

                // Next / Get Started
                Button(
                    onClick = {
                        if (pagerState.currentPage < 2) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            viewModel.completeOnboarding()
                            onFinished()
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = if (pagerState.currentPage < 2) "Next" else "Get Started",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

// ── Page 1: Task Tracking ─────────────────────────────────────────────────────

@Composable
private fun TaskTrackingPage(
    primary: Color,
    primaryContainer: Color,
    onPrimaryContainer: Color,
    surface: Color,
    onSurface: Color,
    onSurfaceVariant: Color,
    outlineVariant: Color
) {
    // Animate the satisfaction bar filling up
    val infiniteTransition = rememberInfiniteTransition(label = "task_anim")
    val barProgress by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar_progress"
    )

    OnboardingPageLayout(
        illustration = {
            TaskIllustration(
                primary = primary,
                primaryContainer = primaryContainer,
                onPrimaryContainer = onPrimaryContainer,
                surface = surface,
                onSurface = onSurface,
                onSurfaceVariant = onSurfaceVariant,
                outlineVariant = outlineVariant,
                barProgress = barProgress
            )
        },
        title = "Log Your Daily Work",
        subtitle = "Capture every task with a title, notes, and your satisfaction level. Look back on what you actually accomplished each day.",
        onSurface = onSurface,
        onSurfaceVariant = onSurfaceVariant,
        primary = primary
    )
}

@Composable
private fun TaskIllustration(
    primary: Color,
    primaryContainer: Color,
    onPrimaryContainer: Color,
    surface: Color,
    onSurface: Color,
    onSurfaceVariant: Color,
    outlineVariant: Color,
    barProgress: Float
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(horizontal = 24.dp)
    ) {
        val w = size.width
        val h = size.height
        val cardW = w * 0.88f
        val cardH = h * 0.72f
        val cardLeft = (w - cardW) / 2f
        val cardTop = (h - cardH) / 2f
        val r = 20f

        // Card shadow-like background
        drawRoundRect(
            color = outlineVariant.copy(alpha = 0.3f),
            topLeft = Offset(cardLeft + 6f, cardTop + 6f),
            size = Size(cardW, cardH),
            cornerRadius = CornerRadius(r)
        )
        // Card surface
        drawRoundRect(
            color = surface,
            topLeft = Offset(cardLeft, cardTop),
            size = Size(cardW, cardH),
            cornerRadius = CornerRadius(r)
        )
        // Left accent bar (like your DailyTaskItem)
        drawRoundRect(
            color = primary,
            topLeft = Offset(cardLeft, cardTop + 16f),
            size = Size(5f, cardH - 32f),
            cornerRadius = CornerRadius(3f)
        )

        val contentLeft = cardLeft + 24f
        val contentRight = cardLeft + cardW - 24f
        val contentW = contentRight - contentLeft

        // Badge pill at top
        drawRoundRect(
            color = primaryContainer,
            topLeft = Offset(contentLeft, cardTop + 20f),
            size = Size(contentW * 0.38f, 22f),
            cornerRadius = CornerRadius(6f)
        )
        // Badge text line (simulated)
        drawRoundRect(
            color = onPrimaryContainer.copy(alpha = 0.6f),
            topLeft = Offset(contentLeft + 8f, cardTop + 27f),
            size = Size(contentW * 0.22f, 8f),
            cornerRadius = CornerRadius(4f)
        )

        // Title text line
        drawRoundRect(
            color = onSurface,
            topLeft = Offset(contentLeft, cardTop + 56f),
            size = Size(contentW * 0.7f, 11f),
            cornerRadius = CornerRadius(5f)
        )
        // Subtitle line 1
        drawRoundRect(
            color = onSurfaceVariant.copy(alpha = 0.5f),
            topLeft = Offset(contentLeft, cardTop + 76f),
            size = Size(contentW * 0.9f, 8f),
            cornerRadius = CornerRadius(4f)
        )
        // Subtitle line 2
        drawRoundRect(
            color = onSurfaceVariant.copy(alpha = 0.4f),
            topLeft = Offset(contentLeft, cardTop + 90f),
            size = Size(contentW * 0.65f, 8f),
            cornerRadius = CornerRadius(4f)
        )

        // Satisfaction label
        drawRoundRect(
            color = onSurfaceVariant.copy(alpha = 0.4f),
            topLeft = Offset(contentLeft, cardTop + 118f),
            size = Size(contentW * 0.32f, 7f),
            cornerRadius = CornerRadius(3f)
        )
        // Percentage label on right
        drawRoundRect(
            color = primary.copy(alpha = 0.7f),
            topLeft = Offset(contentLeft + contentW * 0.78f, cardTop + 118f),
            size = Size(contentW * 0.18f, 7f),
            cornerRadius = CornerRadius(3f)
        )
        // Progress track
        drawRoundRect(
            color = primary.copy(alpha = 0.12f),
            topLeft = Offset(contentLeft, cardTop + 132f),
            size = Size(contentW, 7f),
            cornerRadius = CornerRadius(4f)
        )
        // Progress fill — animated
        drawRoundRect(
            color = primary,
            topLeft = Offset(contentLeft, cardTop + 132f),
            size = Size(contentW * barProgress, 7f),
            cornerRadius = CornerRadius(4f)
        )

        // Clock chip at bottom
        drawRoundRect(
            color = primaryContainer.copy(alpha = 0.5f),
            topLeft = Offset(contentLeft, cardTop + 152f),
            size = Size(contentW * 0.32f, 26f),
            cornerRadius = CornerRadius(8f)
        )
        // Clock icon circle
        drawCircle(
            color = primary,
            radius = 6f,
            center = Offset(contentLeft + 16f, cardTop + 165f)
        )
        // Time text line
        drawRoundRect(
            color = primary,
            topLeft = Offset(contentLeft + 28f, cardTop + 160f),
            size = Size(contentW * 0.16f, 8f),
            cornerRadius = CornerRadius(4f)
        )

        // Divider
        drawLine(
            color = outlineVariant,
            start = Offset(contentLeft, cardTop + cardH - 52f),
            end = Offset(contentLeft + contentW, cardTop + cardH - 52f),
            strokeWidth = 0.8f
        )
        // Date line
        drawRoundRect(
            color = onSurfaceVariant.copy(alpha = 0.35f),
            topLeft = Offset(contentLeft, cardTop + cardH - 38f),
            size = Size(contentW * 0.4f, 7f),
            cornerRadius = CornerRadius(3f)
        )
        // Islamic date
        drawRoundRect(
            color = onSurfaceVariant.copy(alpha = 0.25f),
            topLeft = Offset(contentLeft + contentW * 0.58f, cardTop + cardH - 38f),
            size = Size(contentW * 0.38f, 7f),
            cornerRadius = CornerRadius(3f)
        )
    }
}

// ── Page 2: Timer ─────────────────────────────────────────────────────────────

@Composable
private fun TimerPage(
    primary: Color,
    secondary: Color,
    primaryContainer: Color,
    surface: Color,
    onSurface: Color,
    onSurfaceVariant: Color,
    outlineVariant: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "timer_anim")
    val arcSweep by infiniteTransition.animateFloat(
        initialValue = 30f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arc_sweep"
    )
    val dotRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dot_rotation"
    )

    OnboardingPageLayout(
        illustration = {
            TimerIllustration(
                primary = primary,
                secondary = secondary,
                primaryContainer = primaryContainer,
                surface = surface,
                onSurface = onSurface,
                onSurfaceVariant = onSurfaceVariant,
                outlineVariant = outlineVariant,
                arcSweep = arcSweep,
                dotRotation = dotRotation
            )
        },
        title = "Time Every Session",
        subtitle = "Start a live timer from the notification or log durations manually. Your hours accumulate automatically across sessions.",
        onSurface = onSurface,
        onSurfaceVariant = onSurfaceVariant,
        primary = primary
    )
}

@Composable
private fun TimerIllustration(
    primary: Color,
    secondary: Color,
    primaryContainer: Color,
    surface: Color,
    onSurface: Color,
    onSurfaceVariant: Color,
    outlineVariant: Color,
    arcSweep: Float,
    dotRotation: Float
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val outerRadius = size.minDimension * 0.36f
        val strokeW = outerRadius * 0.18f

        // Outer ring track
        drawCircle(
            color = outlineVariant.copy(alpha = 0.3f),
            radius = outerRadius,
            center = Offset(cx, cy),
            style = Stroke(width = strokeW)
        )
        // Animated arc
        drawArc(
            color = primary,
            startAngle = -90f,
            sweepAngle = arcSweep,
            useCenter = false,
            topLeft = Offset(cx - outerRadius, cy - outerRadius),
            size = Size(outerRadius * 2, outerRadius * 2),
            style = Stroke(width = strokeW, cap = StrokeCap.Round)
        )
        // Moving dot at arc end
        val dotAngleRad = Math.toRadians((dotRotation - 90.0))
        val dotX = cx + outerRadius * cos(dotAngleRad).toFloat()
        val dotY = cy + outerRadius * sin(dotAngleRad).toFloat()
        drawCircle(
            color = surface,
            radius = strokeW * 0.55f,
            center = Offset(dotX, dotY)
        )
        drawCircle(
            color = primary,
            radius = strokeW * 0.38f,
            center = Offset(dotX, dotY)
        )

        // Center card
        val cardW = outerRadius * 1.1f
        val cardH = outerRadius * 0.72f
        drawRoundRect(
            color = primaryContainer.copy(alpha = 0.6f),
            topLeft = Offset(cx - cardW / 2f, cy - cardH / 2f),
            size = Size(cardW, cardH),
            cornerRadius = CornerRadius(16f)
        )
        // Elapsed label
        drawRoundRect(
            color = onSurfaceVariant.copy(alpha = 0.4f),
            topLeft = Offset(cx - cardW * 0.3f, cy - cardH * 0.3f),
            size = Size(cardW * 0.6f, 7f),
            cornerRadius = CornerRadius(4f)
        )
        // Time digits simulation
        val digitY = cy + cardH * 0.08f
        val digitH = 14f
        listOf(-0.32f, -0.14f, 0.0f, 0.14f, 0.32f).forEachIndexed { i, xOff ->
            if (i == 2) {
                // Colon dots
                drawCircle(color = primary, radius = 3f, center = Offset(cx + cardW * xOff, digitY - 4f))
                drawCircle(color = primary, radius = 3f, center = Offset(cx + cardW * xOff, digitY + 4f))
            } else {
                drawRoundRect(
                    color = primary,
                    topLeft = Offset(cx + cardW * xOff - 10f, digitY - digitH / 2f),
                    size = Size(20f, digitH),
                    cornerRadius = CornerRadius(4f)
                )
            }
        }

        // Notification pill at bottom
        val pillW = outerRadius * 1.4f
        val pillTop = cy + outerRadius * 0.72f
        drawRoundRect(
            color = surface,
            topLeft = Offset(cx - pillW / 2f, pillTop),
            size = Size(pillW, 36f),
            cornerRadius = CornerRadius(12f)
        )
        drawRoundRect(
            color = outlineVariant.copy(alpha = 0.5f),
            topLeft = Offset(cx - pillW / 2f, pillTop),
            size = Size(pillW, 36f),
            cornerRadius = CornerRadius(12f),
            style = Stroke(width = 0.8f)
        )
        // Notification icon
        drawCircle(
            color = primary.copy(alpha = 0.8f),
            radius = 8f,
            center = Offset(cx - pillW / 2f + 20f, pillTop + 18f)
        )
        // Text lines inside notification
        drawRoundRect(
            color = onSurface.copy(alpha = 0.7f),
            topLeft = Offset(cx - pillW / 2f + 36f, pillTop + 10f),
            size = Size(pillW * 0.38f, 7f),
            cornerRadius = CornerRadius(3f)
        )
        drawRoundRect(
            color = onSurfaceVariant.copy(alpha = 0.4f),
            topLeft = Offset(cx - pillW / 2f + 36f, pillTop + 22f),
            size = Size(pillW * 0.28f, 6f),
            cornerRadius = CornerRadius(3f)
        )
        // Discard / Save buttons in notification
        drawRoundRect(
            color = outlineVariant,
            topLeft = Offset(cx + pillW * 0.14f, pillTop + 10f),
            size = Size(pillW * 0.18f, 16f),
            cornerRadius = CornerRadius(5f)
        )
        drawRoundRect(
            color = primary.copy(alpha = 0.7f),
            topLeft = Offset(cx + pillW * 0.35f, pillTop + 10f),
            size = Size(pillW * 0.12f, 16f),
            cornerRadius = CornerRadius(5f)
        )
    }
}

// ── Page 3: Goals ─────────────────────────────────────────────────────────────

@Composable
private fun GoalPage(
    primary: Color,
    secondary: Color,
    tertiary: Color,
    primaryContainer: Color,
    surface: Color,
    onSurface: Color,
    onSurfaceVariant: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "goal_anim")
    val checkScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "check_scale"
    )

    OnboardingPageLayout(
        illustration = {
            GoalIllustration(
                primary = primary,
                secondary = secondary,
                tertiary = tertiary,
                primaryContainer = primaryContainer,
                surface = surface,
                onSurface = onSurface,
                onSurfaceVariant = onSurfaceVariant,
                checkScale = checkScale
            )
        },
        title = "Set Goals, Stay Focused",
        subtitle = "Define goals with urgency, importance, and difficulty. Track pending vs completed goals and never lose sight of what matters.",
        onSurface = onSurface,
        onSurfaceVariant = onSurfaceVariant,
        primary = primary
    )
}

@Composable
private fun GoalIllustration(
    primary: Color,
    secondary: Color,
    tertiary: Color,
    primaryContainer: Color,
    surface: Color,
    onSurface: Color,
    onSurfaceVariant: Color,
    checkScale: Float
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(horizontal = 24.dp)
    ) {
        val w = size.width
        val h = size.height

        // Draw two goal cards — one pending, one completed
        drawGoalCard(
            topLeft = Offset(w * 0.04f, h * 0.04f),
            width = w * 0.88f,
            height = h * 0.38f,
            accentColor = tertiary,
            surface = surface,
            onSurface = onSurface,
            onSurfaceVariant = onSurfaceVariant,
            primaryContainer = primaryContainer,
            badgeColors = listOf(Color(0xFFE24B4A), Color(0xFFEF9F27), Color(0xFF639922)),
            isCompleted = false,
            outlineVariant = onSurfaceVariant.copy(alpha = 0.2f)
        )

        drawGoalCard(
            topLeft = Offset(w * 0.04f, h * 0.48f),
            width = w * 0.88f,
            height = h * 0.38f,
            accentColor = primary,
            surface = surface,
            onSurface = onSurface,
            onSurfaceVariant = onSurfaceVariant,
            primaryContainer = primaryContainer,
            badgeColors = listOf(Color(0xFF639922), Color(0xFF639922), Color(0xFF378ADD)),
            isCompleted = true,
            outlineVariant = onSurfaceVariant.copy(alpha = 0.2f),
            checkScale = checkScale
        )
    }
}

private fun DrawScope.drawGoalCard(
    topLeft: Offset,
    width: Float,
    height: Float,
    accentColor: Color,
    surface: Color,
    onSurface: Color,
    onSurfaceVariant: Color,
    primaryContainer: Color,
    badgeColors: List<Color>,
    isCompleted: Boolean,
    outlineVariant: Color,
    checkScale: Float = 1f
) {
    // Card
    drawRoundRect(
        color = surface,
        topLeft = topLeft,
        size = Size(width, height),
        cornerRadius = CornerRadius(18f)
    )
    drawRoundRect(
        color = outlineVariant,
        topLeft = topLeft,
        size = Size(width, height),
        cornerRadius = CornerRadius(18f),
        style = Stroke(width = 0.8f)
    )
    // Left accent
    drawRoundRect(
        color = accentColor,
        topLeft = Offset(topLeft.x, topLeft.y + 12f),
        size = Size(5f, height - 24f),
        cornerRadius = CornerRadius(3f)
    )

    val cL = topLeft.x + 20f
    val cTop = topLeft.y

    // Status badge
    val badgeColor = if (isCompleted) Color(0xFF639922) else Color(0xFFEF9F27)
    val badgeContainerColor = badgeColor.copy(alpha = 0.15f)
    drawRoundRect(
        color = badgeContainerColor,
        topLeft = Offset(cL, cTop + 14f),
        size = Size(width * 0.28f, 18f),
        cornerRadius = CornerRadius(5f)
    )
    drawRoundRect(
        color = badgeColor,
        topLeft = Offset(cL + 6f, cTop + 19f),
        size = Size(width * 0.16f, 6f),
        cornerRadius = CornerRadius(3f)
    )

    // Title line
    drawRoundRect(
        color = onSurface.copy(alpha = 0.85f),
        topLeft = Offset(cL, cTop + 38f),
        size = Size(width * 0.55f, 9f),
        cornerRadius = CornerRadius(4f)
    )

    // 3 priority badges
    val badgeXStart = cL
    val badgeY = cTop + 56f
    badgeColors.forEachIndexed { i, color ->
        drawRoundRect(
            color = color.copy(alpha = 0.15f),
            topLeft = Offset(badgeXStart + i * (width * 0.22f + 6f), badgeY),
            size = Size(width * 0.22f, 16f),
            cornerRadius = CornerRadius(5f)
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(badgeXStart + i * (width * 0.22f + 6f) + 6f, badgeY + 4f),
            size = Size(width * 0.12f, 7f),
            cornerRadius = CornerRadius(3f)
        )
    }

    // Toggle switch on bottom right (for completed state)
    val switchX = topLeft.x + width - 54f
    val switchY = cTop + height - 28f
    drawRoundRect(
        color = if (isCompleted) accentColor.copy(alpha = 0.7f) else onSurfaceVariant.copy(alpha = 0.2f),
        topLeft = Offset(switchX, switchY),
        size = Size(32f, 18f),
        cornerRadius = CornerRadius(9f)
    )
    drawCircle(
        color = surface,
        radius = 7f,
        center = if (isCompleted) Offset(switchX + 24f, switchY + 9f)
        else Offset(switchX + 8f, switchY + 9f)
    )

    // Animated checkmark circle on completed card
    if (isCompleted) {
        val cx = topLeft.x + width - 24f
        val cy = cTop + 20f
        drawCircle(
            color = accentColor.copy(alpha = 0.2f),
            radius = 11f * checkScale,
            center = Offset(cx, cy)
        )
        drawCircle(
            color = accentColor,
            radius = 8f * checkScale,
            center = Offset(cx, cy)
        )
        // Checkmark path
        val path = Path().apply {
            moveTo(cx - 4f * checkScale, cy)
            lineTo(cx - 1f * checkScale, cy + 3f * checkScale)
            lineTo(cx + 4f * checkScale, cy - 3f * checkScale)
        }
        drawPath(
            path = path,
            color = surface,
            style = Stroke(width = 2.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

// ── Shared layout ─────────────────────────────────────────────────────────────

@Composable
private fun OnboardingPageLayout(
    illustration: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onSurface: Color,
    onSurfaceVariant: Color,
    primary: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 140.dp),  // space for the bottom controls
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        illustration()

        Spacer(Modifier.height(36.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp,
            modifier = Modifier.padding(horizontal = 36.dp)
        )
    }
}

// ── Page indicator ────────────────────────────────────────────────────────────

@Composable
private fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    activeColor: Color,
    inactiveColor: Color
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(pageCount) { i ->
            val isActive = i == currentPage
            val width by animateDpAsState(
                targetValue = if (isActive) 24.dp else 8.dp,
                animationSpec = tween(300),
                label = "indicator_width"
            )
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(if (isActive) activeColor else inactiveColor)
            )
        }
    }
}

