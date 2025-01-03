package com.giraffe.qiblaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.giraffe.qiblaapp.ui.screens.qibla.QiblaScreen
import com.giraffe.qiblaapp.ui.theme.QiblaAppTheme


val LocalActivity = staticCompositionLocalOf<ComponentActivity> {
    error("LocalContext")
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QiblaAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CompositionLocalProvider(LocalActivity provides this) {
                        QiblaScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}