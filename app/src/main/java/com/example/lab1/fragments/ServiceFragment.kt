package com.example.lab1.fragments

import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.lab1.databinding.FragmentServiceBinding
import com.example.lab1.service.MusicService

class ServiceFragment : Fragment() {
    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!
//    private val STARTFOREGROUND_ACTION = "com.example.musicplayer.action.START_FOREGROUND"
//    private val STOPFOREGROUND_ACTION = "com.example.musicplayer.action.STOP_FOREGROUND"
    private val NOTIFICATION_CHANNEL_ID = "musicplayer_channel"
//    private val PAUSE_ACTION = "com.example.musicplayer.action.PAUSE"
//    private val PLAY_ACTION = "com.example.musicplayer.action.PLAY"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateButtonStates()

        binding.btnStart.setOnClickListener {
            Intent(requireContext(), MusicService::class.java).also { intent ->
                intent.action = MusicService.ACTION_START
                requireContext().startService(intent)
                updateButtonStates()
            }
        }

        binding.btnPause.setOnClickListener {
            Intent(requireContext(), MusicService::class.java).also { intent ->
                if (MusicService.isPlaying) {
                    intent.action = MusicService.ACTION_PAUSE
                } else {
                    intent.action = MusicService.ACTION_RESUME
                }
                requireContext().startService(intent)
                updateButtonStates()
            }
        }

        binding.btnStop.setOnClickListener {
            Intent(requireContext(), MusicService::class.java).also { intent ->
                intent.action = MusicService.ACTION_STOP
                requireContext().startService(intent)
                updateButtonStates()
            }
        }
    }

    private fun updateButtonStates() {
        val isServiceRunning = MusicService.isPlaying
        binding.btnStart.isEnabled = !isServiceRunning
        binding.btnPause.isEnabled = isServiceRunning
        binding.btnPause.text = if (isServiceRunning) "Pause" else "Resume"
        binding.btnStop.isEnabled = isServiceRunning
    }

    override fun onResume() {
        super.onResume()
        updateButtonStates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}