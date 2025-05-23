package com.drew654.weather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun VerticalProgressBar(
    progress: Int
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .height(80.dp)
            .width(32.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.onBackground,
                    RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                )
                .width(32.dp)
                .height((progress.toDouble() / 100 * 80).dp)
        )
    }
}
