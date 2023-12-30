package com.example.connectesp32

import android.os.Bundle
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
    private var engineSetUp: EngineSetUp = EngineSetUp()
    private var serverConnection: ServerConnection = ServerConnection()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_stream, container, false
        )

        setupWebView()
        handleReloadButton()
        handleJoystick()

        return binding.root
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