package com.example.connectesp32

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.connectesp32.databinding.FragmentMainBinding
import java.lang.Error

class MainFragment : ServerConnection() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var mainView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
        try {
            sendGetRequest("test", mapOf())
        } catch (e: Error){
            binding.info.visibility = View.VISIBLE
        }
    }

    override fun handleResponse(responseSuccessful: Boolean) {
        handler.post {
            if (responseSuccessful) {
                mainView.findNavController().navigate(R.id.action_mainFragment_to_streamFragment)
            } else {
                binding.info.visibility = View.VISIBLE
            }
        }
    }

}