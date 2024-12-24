package com.giraffe.qiblaapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.giraffe.qiblaapp.R
import kotlin.math.floor

@Composable
fun Compass(modifier: Modifier = Modifier, rotationAngle: Float = 0f) {
    Image(
        painter = painterResource(id = R.drawable.compass),
        contentDescription = "Compass",
        modifier = modifier
            .aspectRatio(1.0f)
            .graphicsLayer(
                rotationZ = if (rotationAngle < 0) floor(360 - rotationAngle) else -floor(
                    rotationAngle
                )
            )
            .clip(CircleShape)
            .background(Color.White),
        contentScale = ContentScale.Fit
    )
}