package com.example.lab1.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lab1.databinding.FragmentServiceBinding
import com.example.lab1.service.MusicService

class ServiceFragment : Fragment() {
    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val REQUEST_CODE_NOTIFICATIONS = 1001
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        _binding = FragmentServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateButtonStates()

        binding.btnStart.setOnClickListener {
            // Check notification permission on Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (requireContext().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        REQUEST_CODE_NOTIFICATIONS
                    )
                } else {
                    startMusicService()
                }
            } else {
                startMusicService()
            }
        }

        binding.btnPause.setOnClickListener {
            Intent(requireContext(), MusicService::class.java).also { intent ->
                intent.action = MusicService.ACTION_PAUSE
                requireContext().startService(intent)
                updateButtonStates()
            }
        }
    }

    private fun startMusicService() {
        Intent(requireContext(), MusicService::class.java).also { intent ->
            // Using ACTION_RESUME to start/resume music playback.
            intent.action = MusicService.ACTION_RESUME
            requireContext().startService(intent)
            updateButtonStates()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_NOTIFICATIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMusicService()
            } else {
                Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateButtonStates() {
        // If music is playing, enable Pause and disable Resume; if not, enable Resume.
        val isPlaying = MusicService.isPlaying
        binding.btnStart.isEnabled = !isPlaying
        binding.btnPause.isEnabled = isPlaying
        binding.btnStart.text = "Resume"
        binding.btnPause.text = "Pause"
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
