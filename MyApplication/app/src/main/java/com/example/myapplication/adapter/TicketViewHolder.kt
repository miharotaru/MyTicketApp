package com.example.myapplication.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.classes.Ticket
import com.example.myapplication.databinding.TicketItemBinding

class TicketViewHolder(private val binding: TicketItemBinding)
    : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Ticket){
        binding.tvNameTicket.text=item.title
        binding.tvDateTicket.text=item.data
        binding.tvLocationTicket.text=item.location

        //todo
        //implement image for events
    }
}