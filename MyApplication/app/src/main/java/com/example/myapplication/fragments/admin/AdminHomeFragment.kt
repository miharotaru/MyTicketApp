package com.example.myapplication.fragments.admin

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


class AdminHomeFragment : Fragment(),OnClickListener {

    private lateinit var binding:FragmentAdminHomeBinding
    private lateinit var database: FirebaseFirestore
    private lateinit var adapter: TicketAdapter
    private var ticketList: ArrayList<Ticket>? = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentAdminHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataFirebase()
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
                    Log.d("Firestore error", ticketList?.size.toString())
                }
                initAdapter()
                //nu functioneaza dar il las aici
                adapter.notifyDataSetChanged()
            }

        }

        )
    }

    private fun initAdapter() {

        binding.recycleviewTicketItemFragmentHome.layoutManager = LinearLayoutManager(requireContext())
        adapter = TicketAdapter(ticketList as ArrayList<Ticket>,this)
        binding.recycleviewTicketItemFragmentHome.adapter = adapter
        adapter.notifyDataSetChanged()
        Log.d("init adaper", "initadapter ticketList?.size.toString()")
        Log.d("init adaper", ticketList?.size.toString())

    }

    override fun onClickListenerDetails(ticketPos: Int) {
        //TODO("Not yet implemented")
    }

}