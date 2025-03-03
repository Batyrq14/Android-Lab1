package com.example.lab1.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.lab1.databinding.FragmentIntentBinding
import java.io.File

class IntentFragment : Fragment() {
    private var _binding: FragmentIntentBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = uri
            binding.imageView.setImageURI(uri)
            binding.btnShare.isEnabled = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPickImage.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.btnShare.setOnClickListener {
            shareToInstagramStories()
        }

        // Initially disable share button until image is selected
        binding.btnShare.isEnabled = false
    }

    private fun shareToInstagramStories() {
        imageUri?.let { uri ->
            // Create a new Intent to share to Instagram Stories
            val intent = Intent("com.instagram.share.ADD_TO_STORY")
            intent.putExtra("source_application", requireContext().packageName)
            intent.setDataAndType(uri, "image/*")
            intent.putExtra("interactive_asset_uri", imageUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            try {
                // Check if Instagram is installed
                requireContext().packageManager.resolveActivity(intent, 0)?.let {
                    startActivity(intent)
                } ?: run {
                    Toast.makeText(requireContext(), "Instagram app is not installed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(requireContext(), "Instagram app is not installed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}