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
        binding.tvDateTicket.text= firstTenCaracters(item.data)
        binding.tvLocationTicket.text="${item.location}, ${item.city}"
        setImage(item)

    }

    private fun firstTenCaracters(date:String):String{
        return date.substring(0,10)
    }

    private fun setImage(item: Ticket) {
        Picasso.get().load(item.urlToImage).into(binding.imageTicket)
    }
}