package com.example.connectesp32

import android.util.Log
import kotlin.math.abs
import kotlin.math.cos

class EngineSetUp {

    private var angle: Int = -1
    private var power: Int = -1
    private val threshold = 10
    private val cooldownMillis = 1000 // 1 second cooldown
    private var lastExecutionTime: Long = 0
    private val pathForEngine = "engine"
    private var serverConnection: ServerConnection = ServerConnection()

    fun handleJoystick(newAngle: Int, newPower: Int) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastExecutionTime >= cooldownMillis) {
            if (abs(newAngle - angle) >= threshold || abs(newPower - power) >= threshold) {
                val percentOfPower = newPower.toFloat() / 100
                val valuesForEngines = calculatePowerForEngine(newAngle, percentOfPower)
                Log.d("VALUE: ", valuesForEngines.toString())
                sendRequestToServer(valuesForEngines.first, valuesForEngines.second)
                angle = newAngle
                power = newPower
                lastExecutionTime = currentTime
            }
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
        serverConnection.sendGetRequest(pathForEngine, paramL + paramR) { }
    }
}