package com.example.connectesp32

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.connectesp32.databinding.FragmentMainBinding

@RequiresApi(Build.VERSION_CODES.Q)
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var mainView: View
    private var serverConnection: ServerConnection = ServerConnection()
    private val handler = Handler(Looper.getMainLooper())
    private val connectivityManager: ConnectivityManager by lazy {
        requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    private val wifiName: String = "snake"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main, container, false
        )

        binding.start.setOnClickListener { view: View ->
            mainView = view
            if(checkWiFi()) tryToConnect()
        }

        return binding.root
    }

    private fun checkWiFi(): Boolean {
        if (isConnectedToWifi()) {
            return true
        }
        binding.info.visibility = View.VISIBLE
        return false
    }

    private fun tryToConnect() {
        serverConnection.sendGetRequest("test", mapOf()) { responseSuccessful: Boolean ->
            handleResponse(responseSuccessful)
        }
    }

    private fun handleResponse(responseSuccessful: Boolean) {
        handler.post {
            if (responseSuccessful) {
                val bundle = Bundle()
                bundle.putBoolean("control", true)
                mainView.findNavController().navigate(R.id.action_mainFragment_to_streamFragment, bundle)
            } else {
                binding.info.visibility = View.VISIBLE
                mainView.findNavController().navigate(R.id.action_mainFragment_to_streamFragment)
            }
        }
    }

    private fun isConnectedToWifi(): Boolean {
        val networkRequest = createNetworkRequest()
        val networkCallback = WifiNetworkCallback()
        connectivityManager.requestNetwork(networkRequest, networkCallback)
        return networkCallback.isConnectedToWifi
    }

    private fun createNetworkRequest(): NetworkRequest {
        return NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(WifiNetworkSpecifier.Builder().setSsid(wifiName).build())
            .build()
    }

    private inner class WifiNetworkCallback : ConnectivityManager.NetworkCallback() {
        var isConnectedToWifi = false

        override fun onAvailable(network: android.net.Network) {
            isConnectedToWifi = true
        }

        override fun onLost(network: android.net.Network) {
            isConnectedToWifi = false
        }
    }
}