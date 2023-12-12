package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.classes.Ticket
import com.example.myapplication.databinding.TicketItemBinding

class TicketAdapter(private var tickets: ArrayList<Ticket>) :
    RecyclerView.Adapter<TicketViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val binding = TicketItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TicketViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tickets.size
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bind(tickets[position])
        //todo implement onclickListener for click
//        holder.itemView.card_ticket.setOnClickListener {
//            onClickListener.onClickListenerDetails(position)
//        }
    }

}