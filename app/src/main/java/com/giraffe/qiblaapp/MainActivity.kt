package com.giraffe.qiblaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.giraffe.qiblaapp.ui.screens.QiblaScreen
import com.giraffe.qiblaapp.ui.theme.QiblaAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QiblaAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QiblaScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}