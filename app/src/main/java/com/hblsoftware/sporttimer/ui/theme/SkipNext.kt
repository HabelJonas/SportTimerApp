package com.hblsoftware.sporttimer.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MaterialIconsSkip_next: ImageVector
    get() {
        if (_MaterialIconsSkip_next != null) return _MaterialIconsSkip_next!!

        _MaterialIconsSkip_next = ImageVector.Builder(
            name = "skip_next",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Transparent)
            ) {
                moveTo(0f, 0f)
                horizontalLineToRelative(24f)
                verticalLineToRelative(24f)
                horizontalLineTo(0f)
                verticalLineTo(0f)
                close()
            }
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(6f, 18f)
                lineToRelative(8.5f, -6f)
                lineTo(6f, 6f)
                verticalLineToRelative(12f)
                close()
                moveToRelative(2f, -8.14f)
                lineTo(11.03f, 12f)
                lineTo(8f, 14.14f)
                verticalLineTo(9.86f)
                close()
                moveTo(16f, 6f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(12f)
                horizontalLineToRelative(-2f)
                close()
            }
        }.build()

        return _MaterialIconsSkip_next!!
    }

private var _MaterialIconsSkip_next: ImageVector? = null
