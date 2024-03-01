package com.example.myapplication.fragments

import android.content.Context
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
import com.example.myapplication.classes.User
import com.example.myapplication.databinding.FragmentFavoriteTicketsBinding
import com.example.myapplication.interfaces.OnClickListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class FavoriteTicketsFragment : Fragment(), OnClickListener {


    private lateinit var binding: FragmentFavoriteTicketsBinding
    private lateinit var database: FirebaseFirestore
    private var userList: ArrayList<User>? = ArrayList()
    var ticketList: ArrayList<Ticket>? = ArrayList()
    var userTicketList: ArrayList<Ticket>? = ArrayList()
    var userPreferences: ArrayList<String>? = ArrayList()
    var dummy: ArrayList<String>? = ArrayList()
    private lateinit var adapter: TicketAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteTicketsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTicketFirebase()

    }

    private fun getTicketFirebase() {
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

                getUserFirebase()
            }
        }
        )
    }


    private fun getUserFirebase() {
        database = FirebaseFirestore.getInstance()
        database.collection("user").addSnapshotListener(object : EventListener<QuerySnapshot> {

            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        userList?.add(dc.document.toObject(User::class.java))
                    }
                    Log.d("Firestore error", userList?.size.toString())
                }

                setFavoriteTicketsToSee()

            }

        }

        )
    }

    private fun setFavoriteTicketsToSee() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val emailUser = sharedPreferences.getString("email_user_key", "valoare implicită")

        for (user in userList!!) {
            if (emailUser.equals(user.email)) {
                userPreferences = user.preferences
            }
        }

        for (ticket in ticketList.orEmpty()) {
            if (ticket.category in userPreferences.orEmpty()) {
                userTicketList?.add(ticket)

            }
        }

        //userTicketList=null
        if(userTicketList.isNullOrEmpty()){
            binding.tvFrontTextFavorite.visibility = View.VISIBLE
        }else{
            initializareAdapterFavorite()
        }
    }

    private fun initializareAdapterFavorite() {

        binding.recycleviewTicketItemFragmentFavorite.layoutManager =
            LinearLayoutManager(requireContext())
        adapter = TicketAdapter(userTicketList as ArrayList<Ticket>, this)
        binding.recycleviewTicketItemFragmentFavorite.adapter = adapter

    }

    override fun onClickListenerDetails(ticketPos: Int) {
        val detailsFragment = DetailsTicketFragment()
        val bundle = Bundle()
        Log.d("ticketmeu", ticketList?.get(ticketPos).toString())
        bundle.putInt("ticketItem", ticketPos)
        detailsFragment.arguments = bundle

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container_main_drawer2, detailsFragment)
        transaction.addToBackStack(null)

        transaction.commit()
        //poate pot sa rezolv bug-ul cu aceasta metoda
        //requireActivity().supportFragmentManager.popBackStack()
    }


}