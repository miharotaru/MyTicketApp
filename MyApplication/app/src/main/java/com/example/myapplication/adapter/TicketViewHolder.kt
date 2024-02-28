package com.example.myapplication.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.classes.Ticket
import com.example.myapplication.databinding.TicketItemBinding
import com.squareup.picasso.Picasso

class TicketViewHolder(private val binding: TicketItemBinding)
    : RecyclerView.ViewHolder(binding.root) {

    val cardTicketView = binding.cardTicket
    fun bind(item: Ticket){
        binding.tvNameTicket.text=item.title
        binding.tvDateTicket.text=item.data
        binding.tvLocationTicket.text=item.location
        setImage(item)

    }

    private fun setImage(item: Ticket) {
        Picasso.get().load(item.urlToImage).into(binding.imageTicket)
    }
}