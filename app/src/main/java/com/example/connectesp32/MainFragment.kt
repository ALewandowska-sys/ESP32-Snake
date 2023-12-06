package com.example.connectesp32

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.connectesp32.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var mainView: View
    private var serverConnection: ServerConnection = ServerConnection()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main, container, false
        )

        binding.start.setOnClickListener { view: View ->
            mainView = view
            tryToConnect()
        }

        return binding.root
    }

    private fun tryToConnect() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.start.isEnabled = false
        serverConnection.sendGetRequest("test", mapOf()) { responseSuccessful: Boolean ->
            handleResponse(responseSuccessful)
        }
    }

    private fun handleResponse(responseSuccessful: Boolean) {
        handler.post {
            binding.loadingIndicator.visibility = View.GONE
            binding.start.isEnabled = true
            if (responseSuccessful) {
                val bundle = Bundle()
                bundle.putBoolean("control", serverConnection.responseMessage == "true")
                mainView.findNavController().navigate(R.id.action_mainFragment_to_streamFragment, bundle)
            } else {
                binding.info.visibility = View.VISIBLE
            }
        }
    }
}