package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.TicketAdapter
import com.example.myapplication.classes.Ticket
import com.example.myapplication.databinding.FragmentHomeBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class HomeFragment : Fragment() {
    private var ticketList: ArrayList<Ticket>? = ArrayList()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataFirebase()
    }
//luam datele din firebase si le punem in lista de tichetel
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
                    Log.d("Firestore error", ticketList?.size.toString())

                }
                initAdapter()
            }
        }
        )
    }

    private fun initAdapter() {

        binding.recycleviewTicketItemFragmentHome.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleviewTicketItemFragmentHome.adapter = TicketAdapter(ticketList as ArrayList<Ticket>)
        Log.d("init adaper", "initadapter ticketList?.size.toString()")
        Log.d("init adaper", ticketList?.size.toString())

    }

}