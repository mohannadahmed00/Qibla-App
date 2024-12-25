package com.giraffe.qiblaapp.common

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


fun calculateBearing(
    firstLocation: Pair<Double, Double>,
    secondLocation: Pair<Double, Double>,
): Float {
    val lat1Rad = Math.toRadians(firstLocation.first)
    val lon1Rad = Math.toRadians(firstLocation.second)
    val lat2Rad = Math.toRadians(secondLocation.first)
    val lon2Rad = Math.toRadians(secondLocation.second)

    val deltaLon = lon2Rad - lon1Rad

    val y = sin(deltaLon) * cos(lat2Rad)
    val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(deltaLon)
    var bearing = atan2(y, x)

    bearing = Math.toDegrees(bearing)

    if (bearing < 0) {
        bearing += 360
    }

    return bearing.toFloat()
}
