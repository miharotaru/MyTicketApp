package com.example.myapplication.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.classes.Ticket
import com.example.myapplication.databinding.TicketItemBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class TicketViewHolder(private val binding: TicketItemBinding)
    : RecyclerView.ViewHolder(binding.root) {

    val cardTicketView = binding.cardTicket
    fun bind(item: Ticket){
        binding.tvNameTicket.text=item.title
        binding.tvDateTicket.text=setData(item.data)
        binding.tvLocationTicket.text="${item.location}, ${item.city}"
        setImage(item)

    }

    private fun setData(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val parsedDate = inputFormat.parse(date)
        return outputFormat.format(parsedDate)
    }

    private fun setImage(item: Ticket) {
        Picasso.get().load(item.urlToImage).into(binding.imageTicket)
    }
}