package com.example.connectesp32

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import androidx.databinding.DataBindingUtil
import com.example.connectesp32.databinding.FragmentStreamBinding
import io.github.controlwear.virtual.joystick.android.JoystickView

class StreamFragment : ServerConnection() {

    private lateinit var binding: FragmentStreamBinding
    private var ledState: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_stream, container, false
        )
        changeEnginePower()
        changeLedPower()

        setupWebView()
        loadWebView()

        return binding.root
    }

    private fun loadWebView() {
        binding.webView.loadUrl(getUrlAddress())
    }

    private fun changeEnginePower() {
        val joystick: JoystickView = binding.joystickView
        joystick.setOnMoveListener { angle, strength ->
            binding.textViewAngle.text = "$angle°"
            binding.textViewStrength.text = "$strength%"
        }
    }

    private fun changeLedPower() {
        var power: Int
        binding.led.setOnClickListener {
            power = if (ledState) 0 else 255
            ledState = !ledState

            val params = mapOf("value" to power.toString())

            sendGetRequest("dioda", params)
        }

    }

    override fun handleResponse(responseSuccessful: Boolean) {
        handler.post {
            if (responseSuccessful) {
                binding.led.text = if (ledState) getString(R.string.ledOn) else getString(R.string.ledOff)
            } else {
                ledState = !ledState
            }
        }
    }

    private fun setupWebView() {
        val webSettings: WebSettings = binding.webView.settings
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        binding.webView.webChromeClient = WebChromeClient()
    }

}