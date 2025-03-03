package com.example.lab1.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lab1.databinding.ItemCalendarEventBinding
import com.example.lab1.model.CalendarEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.EventViewHolder>() {

    private val events = mutableListOf<CalendarEvent>()

    fun setEvents(newEvents: List<CalendarEvent>) {
        events.clear()
        events.addAll(newEvents)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemCalendarEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    class EventViewHolder(private val binding: ItemCalendarEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: CalendarEvent) {
            binding.textViewTitle.text = event.title
            binding.textViewStart.text = "Starts: ${event.getFormattedStartDate()}"
            binding.textViewEnd.text = "Ends: ${event.getFormattedEndDate()}"
            binding.textViewDuration.text = "Duration: ${event.getDuration()}"

            binding.textViewLocation.text = if (!event.location.isNullOrEmpty()) {
                "Location: ${event.location}"
            } else {
                "No location specified"
            }

            binding.textViewDescription.text = if (!event.description.isNullOrEmpty()) {
                "Description: ${event.description}"
            } else {
                "No description"
            }
        }
    }
}