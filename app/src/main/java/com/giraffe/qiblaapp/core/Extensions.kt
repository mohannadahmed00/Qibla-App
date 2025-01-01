package com.giraffe.qiblaapp.core

import java.util.Locale


fun Float.justTwoDigits() = String.format(Locale.US, "%.2f", this).toFloat()
fun Double.justTwoDigits() = String.format(Locale.US, "%.2f", this).toFloat()
