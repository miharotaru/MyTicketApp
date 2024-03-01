package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.classes.Ticket
import com.example.myapplication.databinding.FragmentDetailsTicketBinding
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.databinding.FragmentPaymentDetailsTicketBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class DetailsTicketFragment : Fragment() {

    private lateinit var binding: FragmentDetailsTicketBinding
    // Inițializat cu o valoare invalidă pentru a verifica dacă a fost setat corect
    private var ticketPos: Int = -1
    var ticketList: ArrayList<Ticket>? = ArrayList()
    private lateinit var database: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDetailsTicketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataFirebase()
        arguments?.let {
            ticketPos =
                it.getInt("ticketItem", -1) // -1 este valoarea implicită dacă nu se găsește cheia
        }

        binding.constrantLayoutDetailsTicket.setOnClickListener{
            val paymentDetailsFragment = PaymentDetailsTicketFragment()
            val bundle = Bundle()
            bundle.putInt("ticketItem", ticketPos)
            paymentDetailsFragment.arguments = bundle

            // Utilizează transaction pentru a înlocui fragmentul actual cu noul fragment
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container_main_drawer2, paymentDetailsFragment).commit()

        }

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

                setDetailsTicket()

            }
        }
        )
    }

    private fun setDetailsTicket() {
        if (ticketPos != -1) {
            Log.d("Fragment_ticketItem", ticketList.toString())
            ticketList?.get(ticketPos)?.let { ticket ->
                // Accesezi TextView-urile din binding și setezi textul
                binding.tvTitleDetailsTicket.text = ticket.title
                binding.tvTitleTicketDetailsTicketFromTicket.text = ticket.title
                binding.tvTextDetailsTicket.text = ticket.details

                binding.tvDateDownDetailsTicket.text=setTime(ticket.data)
                binding.tvDateUpDetailsTicket.text=setDataRo(ticket.data)
                binding.tvLocationCityFrDetails.text="${ticket.location}, ${ticket.city}"

                Picasso.get().load(ticket.urlToImage).into(binding.imageDetailsTicket)
            }

        } else {
            Log.d("Fragment_ticketItem", "Nu s-a transmis corect argumentul")
        }
    }

    private fun setTime(data: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val parsedDate = inputFormat.parse(data)
        return outputFormat.format(parsedDate)
    }

    private fun setDataRo(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMMM", Locale("ro"))

        val parsedDate = inputFormat.parse(date)
        val monthName = outputFormat.format(parsedDate)

        return monthName
    }

    private fun setData(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMMM", Locale.getDefault())

        val parsedDate = inputFormat.parse(date)
        return outputFormat.format(parsedDate)
    }
}