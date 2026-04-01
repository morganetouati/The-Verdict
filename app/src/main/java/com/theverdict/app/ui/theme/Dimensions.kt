package com.theverdict.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Dimensions(
    val screenWidth: Dp,
    val screenHeight: Dp,
    val paddingSmall: Dp,
    val paddingMedium: Dp,
    val paddingLarge: Dp,
    val avatarSize: Dp,
    val avatarSizeSmall: Dp,
    val buttonHeight: Dp,
    val buttonHeightSmall: Dp,
    val titleSize: TextUnit,
    val subtitleSize: TextUnit,
    val bodySize: TextUnit,
    val iconSize: Dp,
    val cardElevation: Dp,
    val topSpacing: Dp
)

val LocalDimensions = compositionLocalOf { defaultDimensions() }

fun defaultDimensions() = Dimensions(
    screenWidth = 360.dp,
    screenHeight = 640.dp,
    paddingSmall = 8.dp,
    paddingMedium = 16.dp,
    paddingLarge = 24.dp,
    avatarSize = 280.dp,
    avatarSizeSmall = 56.dp,
    buttonHeight = 52.dp,
    buttonHeightSmall = 44.dp,
    titleSize = 32.sp,
    subtitleSize = 20.sp,
    bodySize = 14.sp,
    iconSize = 24.dp,
    cardElevation = 4.dp,
    topSpacing = 32.dp
)

@Composable
fun rememberDimensions(): Dimensions {
    val config = LocalConfiguration.current
    val w = config.screenWidthDp.dp
    val h = config.screenHeightDp.dp

    // Scale factor relative to a "base" 360x720 screen
    val widthFactor = (config.screenWidthDp / 360f).coerceIn(0.75f, 1.4f)
    val heightFactor = (config.screenHeightDp / 720f).coerceIn(0.75f, 1.4f)
    val factor = ((widthFactor + heightFactor) / 2f)

    return Dimensions(
        screenWidth = w,
        screenHeight = h,
        paddingSmall = (8 * factor).dp,
        paddingMedium = (16 * factor).dp,
        paddingLarge = (24 * factor).dp,
        avatarSize = (280 * widthFactor).coerceAtMost(h.value * 0.42f).dp,
        avatarSizeSmall = (56 * factor).dp,
        buttonHeight = (52 * heightFactor).coerceIn(44f, 60f).dp,
        buttonHeightSmall = (44 * heightFactor).coerceIn(38f, 52f).dp,
        titleSize = (32 * factor).coerceIn(24f, 42f).sp,
        subtitleSize = (20 * factor).coerceIn(16f, 26f).sp,
        bodySize = (14 * factor).coerceIn(12f, 18f).sp,
        iconSize = (24 * factor).dp,
        cardElevation = 4.dp,
        topSpacing = (32 * heightFactor).coerceIn(16f, 48f).dp
    )
}
