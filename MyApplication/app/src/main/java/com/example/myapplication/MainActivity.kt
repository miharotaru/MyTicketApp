package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.TicketAdapter
import com.example.myapplication.classes.Ticket
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class MainActivity : AppCompatActivity() {

    private var ticketList: ArrayList<Ticket>? = ArrayList()
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInformation()
        
    }

    private fun getDataFirebase() {
        database = FirebaseFirestore.getInstance()
        database.collection("tickets").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        ticketList?.add(dc.document.toObject(Ticket::class.java))
                    }
                }
                initAdapter()
            }
        }
        )
    }

    private fun getInformation() {

//        ticketList?.add(Ticket("fdsdafds22222", "fdsfds", "dsfdsf"))
//        ticketList?.add(Ticket("fdsdafds", "fdsfds", "dsfdsf"))
        getDataFirebase()
        initAdapter()
    }

    private fun initAdapter() {

        binding.recycleviewTicketItem.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ticketList?.let { TicketAdapter(it) }
        }
    }
}