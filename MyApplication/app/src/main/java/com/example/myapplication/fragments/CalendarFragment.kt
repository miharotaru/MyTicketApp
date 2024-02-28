package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.TicketAdapter
import com.example.myapplication.classes.Ticket
import com.example.myapplication.databinding.FragmentCalendarBinding
import com.example.myapplication.databinding.FragmentSettingBinding
import com.example.myapplication.interfaces.OnClickListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.util.Calendar


class CalendarFragment : Fragment() , OnClickListener{

    private lateinit var binding: FragmentCalendarBinding
    var ticketList: ArrayList<Ticket>? = ArrayList()
    private lateinit var database: FirebaseFirestore
    private lateinit var adapter: TicketAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
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

                setCalendar()

            }
        }
        )
    }

    private fun setCalendar() {
        val calendar = Calendar.getInstance()
        binding.calendarView.minDate = calendar.timeInMillis
        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)

            if (selectedCalendar.before(calendar)) {
                // Dacă data selectată este în trecut, afișează un mesaj și setează data înapoi la cea curentă
                Toast.makeText(requireContext(), "Nu puteți selecta zilele din trecut", Toast.LENGTH_SHORT).show()
                binding.calendarView.date = calendar.timeInMillis
            } else {
                // Altfel, poți face orice altă acțiune dorită, de exemplu, actualizarea altor informații în funcție de data selectată
                //pe acesta il foloseam ca sa vad fix data selectata si sa o afisez in toast
                val selectedDate = "${dayOfMonth}/${month + 1}/${year}" // +1 la month pentru că este indexat de la 0

                val newTicketList = filterTicketsByDate(selectedCalendar)
                Log.d("Calendar List", newTicketList.toString())

                binding.recycleviewTicketItemFragmentCalendar.layoutManager=LinearLayoutManager(requireContext())
                adapter = TicketAdapter(newTicketList as ArrayList<Ticket>,this)
                binding.recycleviewTicketItemFragmentCalendar.adapter = adapter

            }
        }
    }


    fun filterTicketsByDate(date: Calendar): List<Ticket> {
        val filteredList = mutableListOf<Ticket>()

        for (ticket in ticketList!!) {
            val ticketDate = ticket.data.substring(0, 10) // Extrage doar partea de data din String-ul de data

            if (ticketDate == formatDate(date)) {
                filteredList.add(ticket)
            }
        }

        return filteredList
    }

    private fun formatDate(date: Calendar): String {
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH) + 1 // Calendar.MONTH începe de la 0
        val dayOfMonth = date.get(Calendar.DAY_OF_MONTH)

        return String.format("%d-%02d-%02d", year, month, dayOfMonth)
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