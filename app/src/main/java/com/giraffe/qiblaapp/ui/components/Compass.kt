package com.giraffe.qiblaapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.giraffe.qiblaapp.R

@Composable
fun Compass(modifier: Modifier = Modifier, rotationAngle: Float = 0f, qiblaAngle: Float = 0f) {
    val northAngle = remember(rotationAngle) {
        if (rotationAngle < 0) (360 - rotationAngle) else -rotationAngle
    }

    Box(
        modifier = modifier
            .aspectRatio(1.0f)
            .graphicsLayer(
                rotationZ = northAngle + qiblaAngle
            )
            .clip(CircleShape),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.compass),
            contentDescription = "compass",
            modifier = Modifier
                .padding(50.dp)
                .aspectRatio(1.0f)
                .graphicsLayer(
                    rotationZ = -qiblaAngle
                )
                .clip(CircleShape)
                .background(Color.White),
            contentScale = ContentScale.Fit
        )
        Image(
            modifier = Modifier.size(80.dp),
            painter = painterResource(id = R.drawable.kaaba),
            contentDescription = "kaaba",
        )
    }

}