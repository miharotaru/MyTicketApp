package com.example.myapplication.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.TicketAdapter
import com.example.myapplication.classes.Ticket
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.interfaces.OnClickListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot



class HomeFragment : Fragment(), OnClickListener {
    private var ticketList: ArrayList<Ticket>? = ArrayList()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseFirestore
    private lateinit var adapter: TicketAdapter
        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataFirebase()
    }
//luam datele din firebase si le punem in lista de tichete
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

    //aici se intampla magia cand se se apasa pe un ticket propriu zis si se transfera datele
    // de la un fragment la altul
    override fun onClickListenerDetails(ticketPos: Int) {
       // Toast.makeText(context, "Hello"+ticketItem.toString()+" ceva", Toast.LENGTH_SHORT).show()
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
