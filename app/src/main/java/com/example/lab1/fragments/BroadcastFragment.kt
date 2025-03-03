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

    private val airplaneModeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
                val isAirplaneModeOn = Settings.Global.getInt(
                    context?.contentResolver,
                    Settings.Global.AIRPLANE_MODE_ON, 0
                ) != 0

                val status = if (isAirplaneModeOn) "ON" else "OFF"
                binding.textViewStatus.text = "Airplane Mode: $status"

                Toast.makeText(
                    context,
                    "Airplane Mode is now $status",
                    Toast.LENGTH_SHORT
                ).show()
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

        // Initialize status
        val isAirplaneModeOn = Settings.Global.getInt(
            requireContext().contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0
        ) != 0

        val status = if (isAirplaneModeOn) "ON" else "OFF"
        binding.textViewStatus.text = "Airplane Mode: $status"

        binding.btnOpenSettings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS))
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