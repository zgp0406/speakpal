package com.zgp.speakpal.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.zgp.speakpal.ui.navigation.SpeakPalNavHost

@Composable
fun SpeakPalApp() {
    val navController = rememberNavController()
    SpeakPalNavHost(navController = navController)
}
