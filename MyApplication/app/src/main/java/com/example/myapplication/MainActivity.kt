package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.TicketAdapter
import com.example.myapplication.classes.Ticket
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var ticketList: ArrayList<Ticket>? = ArrayList()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInformation()
        //recycleview_dashboard_list_user.adapter?.notifyItemChanged(TransactionManager.getTransactionsList().size)


    }

    private fun getInformation() {

        ticketList?.add(Ticket("fdsdafds22222", "fdsfds", "dsfdsf"))
        ticketList?.add(Ticket("fdsdafds", "fdsfds", "dsfdsf"))
        initAdapter()
    }

    private fun initAdapter() {

        binding.recycleviewTicketItem.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter= ticketList?.let { TicketAdapter(it) }
        }
    }
}