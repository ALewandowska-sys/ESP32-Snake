package com.example.connectesp32

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.connectesp32.databinding.FragmentStreamBinding
import io.github.controlwear.virtual.joystick.android.JoystickView

class StreamFragment : Fragment() {

    private lateinit var binding: FragmentStreamBinding
    private var ledState: Boolean = false
    private var engineSetUp: EngineSetUp = EngineSetUp()
    private var serverConnection: ServerConnection = ServerConnection()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_stream, container, false
        )

        setupWebView()
        handleReloadButton()
        setControl()

        return binding.root
    }

    private fun setControl() {
        val control = arguments?.getBoolean("control", false)
        setControlVisibility(control == true)
        if (control == true) {
            handleJoystick()
            handleLedButton()
        }
    }

    private fun setControlVisibility(visible: Boolean) {
        binding.errorTest.visibility = if (visible) View.INVISIBLE else View.VISIBLE
        binding.controlPanel.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    private fun handleReloadButton() {
        binding.reloadButton.setOnClickListener { loadWebView() }
    }

    private fun loadWebView() {
        Log.d("URL: ", serverConnection.getUrlAddress())
        binding.webView.loadUrl(serverConnection.getUrlAddress())
    }

    private fun handleJoystick() {
        val joystick: JoystickView = binding.joystickView
        joystick.setOnMoveListener { angle, strength ->
            engineSetUp.handleJoystick(angle, strength)
        }
    }

    private fun handleLedButton() {
        var power: Int
        binding.led.setOnClickListener {
            power = if (ledState) 0 else 255
            ledState = !ledState

            val params = mapOf("value" to power.toString())
            serverConnection.sendGetRequest("dioda", params) { responseSuccessful: Boolean ->
                Log.d("RESPONSE: ", "for value $power is: $responseSuccessful")
                handleResponse(responseSuccessful)
            }
            binding.led.text = if (ledState) getString(R.string.ledOff) else getString(R.string.ledOn)
        }
    }

    private fun handleResponse(responseSuccessful: Boolean) {
        handler.post {
            if (responseSuccessful) {
                binding.led.text = if (ledState) getString(R.string.ledOff) else getString(R.string.ledOn)
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

        loadWebView()
    }

}