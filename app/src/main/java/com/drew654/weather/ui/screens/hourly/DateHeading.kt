package com.drew654.weather.ui.screens.hourly

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drew654.weather.utils.capitalizeWord
import java.time.LocalDateTime

@Composable
fun DateHeading(
    index: Int,
    hours: List<LocalDateTime>
) {
    val dayOfWeek = hours[index].dayOfWeek.toString()
        .capitalizeWord()
    val month =
        hours[index].month.toString().capitalizeWord()
    val day = hours[index].dayOfMonth.toString().capitalizeWord()
    Text(
        text = "$dayOfWeek, $month $day",
        fontSize = 20.sp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}
