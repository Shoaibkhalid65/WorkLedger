package com.gshoaib998.progressly.ui.settings

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.gshoaib998.progressly.ui.components.SchemeDonut
import com.gshoaib998.progressly.ui.taskcreation.SectionCard
import com.gshoaib998.progressly.ui.theme.AppColorScheme
import com.gshoaib998.progressly.ui.theme.AppThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSettingsScreen(
    navController: NavHostController,
    viewModel: AppPreferencesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val prefs = (uiState as? AppPreferencesState.Ready) ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Appearance",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── App Theme ─────────────────────────────────────────────
            SectionCard(title = "App Theme") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ThemeModeOption(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.SettingsBrightness,
                        label = "System",
                        isSelected = prefs.themeMode == AppThemeMode.SYSTEM,
                        onClick = { viewModel.setThemeMode(AppThemeMode.SYSTEM) }
                    )
                    ThemeModeOption(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.LightMode,
                        label = "Light",
                        isSelected = prefs.themeMode == AppThemeMode.LIGHT,
                        onClick = { viewModel.setThemeMode(AppThemeMode.LIGHT) }
                    )
                    ThemeModeOption(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.DarkMode,
                        label = "Dark",
                        isSelected = prefs.themeMode == AppThemeMode.DARK,
                        onClick = { viewModel.setThemeMode(AppThemeMode.DARK) }
                    )
                }
            }

            // ── Dynamic Color (Android 12+ only) ──────────────────────
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                SectionCard(title = "Dynamic Color") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Use System Colors",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Picks colors from your device wallpaper",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = prefs.useDynamicColor,
                            onCheckedChange = { viewModel.setUseDynamicColor(it) }
                        )
                    }
                }
            }

            // ── Color Scheme ──────────────────────────────────────────
            val colorSectionAlpha = if (prefs.useDynamicColor) 0.38f else 1f

            SectionCard(title = "Color Scheme") {
                Column(
                    modifier = Modifier.alpha(colorSectionAlpha),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppColorScheme.entries.forEach { scheme ->
                        ColorSchemeOption(
                            scheme = scheme,
                            isSelected = prefs.colorScheme == scheme,
                            onClick = {
                                if (!prefs.useDynamicColor) {
                                    viewModel.setColorScheme(scheme)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Theme Mode Option ─────────────────────────────────────────────────────────

@Composable
fun ThemeModeOption(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.outlineVariant

    val containerColor = if (isSelected)
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    else
        MaterialTheme.colorScheme.surface

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 0.5.dp,
            color = borderColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Color Scheme Option Row ───────────────────────────────────────────────────
@Composable
fun ColorSchemeOption(
    scheme: AppColorScheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.0f else 0.97f,
        animationSpec = tween(200),
        label = "scheme_scale"
    )

    val borderColor = if (isSelected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.outlineVariant

    val containerColor = if (isSelected)
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    else
        MaterialTheme.colorScheme.surface

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 0.5.dp,
            color = borderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Donut with selection ring
            SchemeDonut(
                colors = scheme.paletteColors,
                size = 46.dp,
                strokeWidth = 13.dp,
                isSelected = isSelected,
                selectedRingColor = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = scheme.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                // Subtitle hint of the palette mood
                val mood = when (scheme) {
                    AppColorScheme.TERRACOTTA -> "Warm earthy tones"
                    AppColorScheme.OCEAN -> "Cool deep blues"
                    AppColorScheme.FOREST -> "Natural greens"
                    AppColorScheme.VIOLET -> "Rich purples"
                    AppColorScheme.SLATE -> "Neutral blue-grey"
                }
                Text(
                    text = mood,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}