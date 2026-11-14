package com.caresync.app.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val CareSyncCardShape = RoundedCornerShape(18.dp)
val CareSyncButtonShape = RoundedCornerShape(14.dp)
val CareSyncChipShape = RoundedCornerShape(12.dp)

val CareSyncShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = CareSyncButtonShape,
    large = CareSyncCardShape,
    extraLarge = RoundedCornerShape(24.dp)
)
