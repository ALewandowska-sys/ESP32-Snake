package com.example.connectesp32

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.connectesp32.databinding.FragmentMainBinding
import java.lang.Error

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val serverConnection: ServerConnection = ServerConnection()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main, container, false
        )

        binding.start.setOnClickListener { view: View ->
            navigateIfServerReady(view)
        }

        return binding.root
    }

    private fun navigateIfServerReady(view: View) {
        try {
            serverConnection.sendGetRequest("test", mapOf()) { isServerReady ->
                Log.d("READY: ", isServerReady.toString())
                if (isServerReady) {
                    view.findNavController().navigate(R.id.action_mainFragment_to_streamFragment)
                } else {
                    binding.info.visibility = View.VISIBLE
                }
            }
        } catch (e: Error){
            binding.info.visibility = View.VISIBLE
        }
    }

}