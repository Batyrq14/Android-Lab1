package com.example.lab1.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lab1.databinding.FragmentBroadcastBinding

class BroadcastFragment : Fragment() {
    private var _binding: FragmentBroadcastBinding? = null
    private val binding get() = _binding!!

    // Initial status text
    private var statusText: String = "Airplane Mode: OFF"

    // BroadcastReceiver that listens for airplane mode changes and updates statusText
    private val airplaneModeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
                val isAirplaneModeOn = intent.getBooleanExtra("state", false)
                statusText = if (isAirplaneModeOn) "Airplane Mode: ON" else "Airplane Mode: OFF"
                // Optionally, show a toast when the state changes
                Toast.makeText(context, statusText, Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBroadcastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check current airplane mode state and update the statusText and TextView
        val isAirplaneModeOn = Settings.Global.getInt(
            requireContext().contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0
        ) != 0
        statusText = if (isAirplaneModeOn) "Airplane Mode: ON" else "Airplane Mode: OFF"
        binding.textViewStatus.text = statusText

        // When the button is clicked, refresh the TextView with the latest statusText.
        binding.btnRefresh.setOnClickListener {
            binding.textViewStatus.text = statusText
        }
    }

    override fun onResume() {
        super.onResume()
        requireContext().registerReceiver(
            airplaneModeReceiver,
            IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        )
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(airplaneModeReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}