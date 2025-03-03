package com.example.lab1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lab1.databinding.FragmentMainBinding
import com.example.lab1.R

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnIntent.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_intent)
        }

        binding.btnService.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_service)
        }

        binding.btnBroadcast.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_broadcast)
        }

        binding.btnContent.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_content)
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}