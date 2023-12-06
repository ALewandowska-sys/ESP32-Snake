package com.example.connectesp32

import android.util.Log
import kotlin.math.cos

class EngineSetUp {

    private var ready = true
    private val pathForEngine = "engine"
    private var serverConnection: ServerConnection = ServerConnection()

    fun handleJoystick(newAngle: Int, newPower: Int) {
        if (ready) {
            ready = false
            val percentOfPower = newPower.toFloat() / 100
            val valuesForEngines = calculatePowerForEngine(newAngle, percentOfPower)
            Log.d("VALUE: ", valuesForEngines.toString())
            sendRequestToServer(valuesForEngines.first, valuesForEngines.second)
        }
    }

    private fun calculatePowerForEngine(angle: Int, percentOfPower: Float): Pair<Int, Int> {
        val normalizedAngle = (angle % 360 + 360) % 360
        val half = 255 / 2
        var x = when {
            normalizedAngle <= 90 -> half + half * cos(Math.toRadians(normalizedAngle.toDouble()))
            normalizedAngle <= 180 -> half - half * cos(Math.toRadians(normalizedAngle.toDouble() - 90))
            normalizedAngle <= 270 -> half - half * cos(Math.toRadians(normalizedAngle.toDouble() - 180))
            else -> half + half * cos(Math.toRadians(normalizedAngle.toDouble() - 270))
        }.toFloat()
        x *= percentOfPower
        val y = 255 * percentOfPower - x

        return Pair(x.toInt(), y.toInt())
    }

    private fun sendRequestToServer(leftPower: Int, rightPower: Int) {
        val paramL = mapOf("left" to leftPower.toString())
        val paramR = mapOf("right" to rightPower.toString())
        Log.d("SEND ENGINE:", "left - $paramL,right - $paramR")
        serverConnection.sendGetRequest(pathForEngine, paramL + paramR) { responseSuccessful: Boolean ->
            ready = responseSuccessful
        }
    }
}