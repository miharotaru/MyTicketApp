package com.example.myapplication.fragments.admin

import android.annotation.SuppressLint
import android.os.Binder
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
import com.example.myapplication.databinding.FragmentAdminHomeBinding
import com.example.myapplication.interfaces.OnClickListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot


class AdminHomeFragment : Fragment(), OnClickListener {

    private lateinit var binding: FragmentAdminHomeBinding
    private lateinit var database: FirebaseFirestore
    private lateinit var adapter: TicketAdapter
    private var ticketList: ArrayList<Ticket>? = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminHomeBinding.inflate(inflater, container, false)

        val searchButton = binding.searchButton
        val searchEditText = binding.searchEditText
        val ticketsListView = binding.recycleviewTicketItemFragmentHome

        searchButton.setOnClickListener {
            val searchQuery = searchEditText.text.toString()
            searchTickets(searchQuery)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // getDataFirebase()
    }


    fun searchTickets(query: String) {
        database = FirebaseFirestore.getInstance()
        database.collection("tickets")
            .whereEqualTo("title", query)  // Ajustează acest câmp conform structurii tale de date
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return@addSnapshotListener
                }
                ticketList?.clear()  // Golește lista pentru a afișa doar rezultatele căutării

                for (doc in value!!) {
                    val ticket = doc.toObject(Ticket::class.java).apply {
                        id = doc.id  // Setează ID-ul aici
                    }
                    ticketList?.add(ticket)
                }
                initAdapter()  // Reînnoiește adapterul cu rezultatele filtrate
            }
    }

    private fun getTickets() {
        database = FirebaseFirestore.getInstance()
        database.collection("tickets").addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("Firestore error", error.message.toString())
                return@addSnapshotListener
            }
            ticketList?.clear()  // Golește lista la fiecare actualizare pentru a evita duplicatele

            value?.documentChanges?.forEach { dc ->
                val ticket = dc.document.toObject(Ticket::class.java).apply {
                    id = dc.document.id  // Setează ID-ul aici
                }
                when (dc.type) {
                    DocumentChange.Type.ADDED -> ticketList?.add(ticket)
                    DocumentChange.Type.MODIFIED -> {
                        val index = ticketList?.indexOfFirst { it.id == ticket.id }
                        if (index != null && index >= 0) {
                            ticketList?.set(index, ticket)
                        }
                    }

                    DocumentChange.Type.REMOVED -> {
                        ticketList?.removeIf { it.id == ticket.id }
                    }
                }
            }
        }
    }

    private fun getDataFirebase() {
        database = FirebaseFirestore.getInstance()
        database.collection("tickets").addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("Firestore error", error.message.toString())
                return@addSnapshotListener
            }
            ticketList?.clear()  // Golește lista la fiecare actualizare pentru a evita duplicatele

            value?.documentChanges?.forEach { dc ->
                val ticket = dc.document.toObject(Ticket::class.java).apply {
                    id = dc.document.id  // Setează ID-ul aici
                }
                when (dc.type) {
                    DocumentChange.Type.ADDED -> ticketList?.add(ticket)
                    DocumentChange.Type.MODIFIED -> {
                        val index = ticketList?.indexOfFirst { it.id == ticket.id }
                        if (index != null && index >= 0) {
                            ticketList?.set(index, ticket)
                        }
                    }

                    DocumentChange.Type.REMOVED -> {
                        ticketList?.removeIf { it.id == ticket.id }
                    }
                }
            }

            initAdapter()  // Inițializează adapterul după actualizarea listei
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initAdapter() {
        binding.recycleviewTicketItemFragmentHome.layoutManager =
            LinearLayoutManager(requireContext())
        if (!::adapter.isInitialized) {
            adapter = TicketAdapter(ticketList ?: ArrayList(), this)
            binding.recycleviewTicketItemFragmentHome.adapter = adapter
        }
        adapter.notifyDataSetChanged()
    }


    override fun onClickListenerDetails(ticketPos: Int) {
        //TODO("Not yet implemented")
    }

}