package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.classes.Ticket
import com.example.myapplication.databinding.TicketItemBinding
import com.example.myapplication.interfaces.OnClickListener

class TicketAdapter(
    private var tickets: ArrayList<Ticket>,
    private var onClickListener: OnClickListener) :
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
        holder.cardTicketView.setOnClickListener {
            onClickListener.onClickListenerDetails(position)
        }
    }
}