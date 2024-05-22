package com.example.myapplication.fragments.admin

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.TicketAdapter
import com.example.myapplication.classes.Ticket
import com.example.myapplication.databinding.FragmentAdminHomeBinding
import com.example.myapplication.interfaces.OnClickListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson


class AdminHomeFragment : Fragment(), OnClickListener {

    private lateinit var binding: FragmentAdminHomeBinding
    private lateinit var database: FirebaseFirestore
    private lateinit var adapter: TicketAdapter
    private var ticketList: ArrayList<Ticket>? = ArrayList()
    private var ticketPostIdList: ArrayList<TicketId>? = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminHomeBinding.inflate(inflater, container, false)

        val searchButton = binding.searchButton
        val searchEditText = binding.searchEditText

        searchButton.setOnClickListener {
            val searchQuery = searchEditText.text.toString()
            searchTickets(searchQuery)
        }
        binding.imageSearchHomeAllObject.setOnClickListener{
            getDataFirebase()
        }
        return binding.root
    }

    private fun searchTickets(query: String) {
        database = FirebaseFirestore.getInstance()
        database.collection("tickets")
            .whereEqualTo("title", query)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return@addSnapshotListener
                }
                ticketList?.clear()

                for (doc in value!!) {
                    val ticket = doc.toObject(Ticket::class.java).apply {
                        id = doc.id
                    }
                    ticketList?.add(ticket)
                }
                Log.d("Search", "Found ${ticketList?.size} tickets matching the query")

                activity?.runOnUiThread {
                    initAdapter()  // Asigură-te că se rulează pe thread-ul principal
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

            var posTicket=0
            value?.documentChanges?.forEach { dc ->
                val ticket = dc.document.toObject(Ticket::class.java).apply {
                    id = dc.document.id  // Setează ID-ul aici
                }
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        ticketList?.add(ticket)
                        ticketPostIdList?.add(TicketId(dc.document.id,posTicket))
                        posTicket += 1
                    }
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
        val idValue = ticketPostIdList?.get(ticketPos)?.ticketId

        context?.let { ctx ->
            val sharedPreferences: SharedPreferences =
                ctx.getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE)

            val ticket= ticketList?.get(ticketPos)
            val gson = Gson()
            val ticketJson = gson.toJson(ticket)

            val editor = sharedPreferences.edit()
            editor.putString("ticketCeva", ticketJson)
            editor.putString("idValue", idValue)
            editor.apply()
        }

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container_main_drawer3, AdminEditTicketFragment())
        transaction.addToBackStack(null)

        transaction.commit()
    }

}

data class TicketId(
    var ticketId: String = "",
    var posTicket: Int = 0,
)
