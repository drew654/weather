package com.drew654.weather.ui.screens.settings

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TappableText(
    navController: NavController,
    text: String,
    route: String,
    style: TextStyle = LocalTextStyle.current,
    color: Color = TopAppBarDefaults.topAppBarColors().titleContentColor
) {
    val tapCount = remember { mutableIntStateOf(0) }
    val lastTapTime = remember { mutableLongStateOf(0L) }
    val tapThreshold = 300L
    val requiredTaps = 7

    LaunchedEffect(tapCount) {
        if (tapCount.intValue > 0) {
            delay(tapThreshold)
            tapCount.intValue = 0
            lastTapTime.longValue = 0L
        }
    }

    Text(
        text = text,
        style = style,
        color = color,
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastTapTime.longValue < tapThreshold) {
                        tapCount.intValue++
                        if (tapCount.intValue == requiredTaps) {
                            navController.navigate(route)
                        }
                    } else {
                        tapCount.intValue = 1
                    }
                    lastTapTime.longValue = currentTime
                }
            }
    )
}
