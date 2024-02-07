package com.example.connectesp32

import android.util.Log
import kotlin.math.abs
import kotlin.math.cos

class EngineSetUp {

    private val pathForEngine = "engine"
    private var serverConnection: ServerConnection = ServerConnection()
    private val valueThreshold = 10
    private var lastLeftEngineValue = 0
    private var lastRightEngineValue = 0

    fun handleJoystick(newAngle: Int, newPower: Int): Pair<Int, Int> {
        val percentOfPower = newPower.toFloat() / 100
        val valuesForEngines = calculatePowerForEngines(newAngle, percentOfPower)
        if (shouldSendRequest(newAngle, newPower)) {
            sendRequestToServer(valuesForEngines.first, valuesForEngines.second)
        }
        return valuesForEngines
    }

    private fun shouldSendRequest(leftEngineValue: Int, rightEngineValue: Int): Boolean {
        if (abs(leftEngineValue - lastLeftEngineValue) > valueThreshold ||
            abs(rightEngineValue - lastRightEngineValue) > valueThreshold) {
            lastLeftEngineValue = leftEngineValue
            lastRightEngineValue = rightEngineValue
            return true
        }
        return false
    }

    private fun calculatePowerForEngines(angle: Int, percentOfPower: Float): Pair<Int, Int> {
        val halfOfMaximum = 255 / 2
        val preparedAngle = Math.toRadians(angle.toDouble()/2)
        var leftEngineValue = halfOfMaximum + halfOfMaximum * cos(preparedAngle)
        leftEngineValue *= percentOfPower
        val rightEngineValue = 255 * percentOfPower - leftEngineValue
        val finalLeft = leftEngineValue.toInt().takeIf { it >= 30 } ?: 0
        val finalRight = rightEngineValue.toInt().takeIf { it >= 30 } ?: 0
        return Pair(finalLeft, finalRight)
    }

    private fun sendRequestToServer(leftPower: Int, rightPower: Int) {
        val paramL = mapOf("left" to leftPower.toString())
        val paramR = mapOf("right" to rightPower.toString())
        Log.d("SEND ENGINE:", "$paramL, $paramR")
        serverConnection.sendGetRequest(pathForEngine, paramL + paramR) {}
    }
}