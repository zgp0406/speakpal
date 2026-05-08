package com.zgp.speakpal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.zgp.speakpal.ui.SpeakPalApp
import com.zgp.speakpal.ui.theme.SpeakPalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpeakPalTheme {
                SpeakPalApp()
            }
        }
    }
}
