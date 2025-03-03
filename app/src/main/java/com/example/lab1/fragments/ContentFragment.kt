package com.example.lab1.fragments

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab1.adapter.CalendarAdapter
import com.example.lab1.databinding.FragmentContentBinding
import com.example.lab1.model.CalendarEvent
import java.util.*

class ContentFragment : Fragment() {
    private var _binding: FragmentContentBinding? = null
    private val binding get() = _binding!!

    private val calendarAdapter = CalendarAdapter()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadCalendarEvents()
        } else {
            Toast.makeText(
                requireContext(),
                "Calendar permission is required to view events",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = calendarAdapter
        }

        binding.btnLoadEvents.setOnClickListener {
            checkPermissionAndLoadEvents()
        }
    }

    private fun checkPermissionAndLoadEvents() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CALENDAR
            ) == PackageManager.PERMISSION_GRANTED -> {
                loadCalendarEvents()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CALENDAR) -> {
                Toast.makeText(
                    requireContext(),
                    "Calendar permission is needed to show your events",
                    Toast.LENGTH_LONG
                ).show()
                requestPermissionLauncher.launch(Manifest.permission.READ_CALENDAR)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_CALENDAR)
            }
        }
    }

    private fun loadCalendarEvents() {
        binding.progressBar.visibility = View.VISIBLE

        val events = mutableListOf<CalendarEvent>()

        // Set the time range - get events for the next month
        val now = Calendar.getInstance()
        val end = Calendar.getInstance()
        end.add(Calendar.MONTH, 1)

        // The projection defines which columns from the database will be queried
        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND
        )

        // Define the selection criteria
        val selection = "${CalendarContract.Events.DTSTART} >= ? AND " +
                "${CalendarContract.Events.DTSTART} <= ?"
        val selectionArgs = arrayOf(
            now.timeInMillis.toString(),
            end.timeInMillis.toString()
        )

        // Query the calendar content provider
        val uri: Uri = CalendarContract.Events.CONTENT_URI
        var cursor: Cursor? = null

        try {
            cursor = requireContext().contentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                "${CalendarContract.Events.DTSTART} ASC" // Sort by start time
            )

            cursor?.let {
                val idIndex = it.getColumnIndex(CalendarContract.Events._ID)
                val titleIndex = it.getColumnIndex(CalendarContract.Events.TITLE)
                val descIndex = it.getColumnIndex(CalendarContract.Events.DESCRIPTION)
                val locIndex = it.getColumnIndex(CalendarContract.Events.EVENT_LOCATION)
                val startIndex = it.getColumnIndex(CalendarContract.Events.DTSTART)
                val endIndex = it.getColumnIndex(CalendarContract.Events.DTEND)

                while (it.moveToNext()) {
                    val id = it.getLong(idIndex)
                    val title = it.getString(titleIndex) ?: "No Title"
                    val description = it.getString(descIndex)
                    val location = it.getString(locIndex)
                    val start = it.getLong(startIndex)
                    val end = it.getLong(endIndex)

                    events.add(
                        CalendarEvent(
                            id = id,
                            title = title,
                            description = description,
                            location = location,
                            startTime = start,
                            endTime = end
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error loading calendar events: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        } finally {
            cursor?.close()
            binding.progressBar.visibility = View.GONE

            if (events.isEmpty()) {
                binding.textViewNoEvents.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.textViewNoEvents.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                calendarAdapter.setEvents(events)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}