package com.example.myapplication.fragments

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import android.graphics.Color
import com.example.myapplication.R
import com.example.myapplication.classes.Ticket
import com.example.myapplication.databinding.FragmentPaymentDetailsTicketBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot


class PaymentDetailsTicketFragment : Fragment() {

private lateinit var binding: FragmentPaymentDetailsTicketBinding
    private var ticketPos: Int = -1
    var ticketList: ArrayList<Ticket>? = ArrayList()
    private lateinit var database: FirebaseFirestore
    private lateinit var ticketItem: Ticket
    private lateinit var alertDialog: AlertDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentPaymentDetailsTicketBinding.inflate(inflater, container,false)
        alertDialog = AlertDialog.Builder(requireContext()).create()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataFirebase()
        arguments?.let {
            ticketPos =
                it.getInt("ticketItem", -1) // -1 este valoarea implicită dacă nu se găsește cheia
        }

        setButtonsForNumberOfTicket()
        setSumOfTicketRadioButton()

    }

    private fun setSumOfTicketRadioButton() {
        binding.radioGroupLeft.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = binding.root.findViewById<RadioButton>(checkedId)
            val selectedNumber = selectedRadioButton.text.toString().toInt()
            val currentNumberOfTickets = binding.tvShowNumberOfTickets.text.toString().toInt()
            val totalSum = selectedNumber * currentNumberOfTickets
            binding.tvSumaTotalaTicket.text = totalSum.toString()
        }
    }


    private fun setButtonsForNumberOfTicket() {
        binding.btPlusTicket.setOnClickListener {
            val currentNumberOfTickets = binding.tvShowNumberOfTickets.text.toString().toInt()
            if (currentNumberOfTickets < 10) {
                val newNumberOfTickets = currentNumberOfTickets + 1
                binding.tvShowNumberOfTickets.text = newNumberOfTickets.toString()
            }
            updateButtonStates()
        }

        binding.btMinusTicket.setOnClickListener {
            val currentNumberOfTickets = binding.tvShowNumberOfTickets.text.toString().toInt()
            if (currentNumberOfTickets > 1) {
                val newNumberOfTickets = currentNumberOfTickets - 1
                binding.tvShowNumberOfTickets.text = newNumberOfTickets.toString()
            }
            updateButtonStates()
        }
    }

    private fun updateButtonStates() {


        val currentNumberOfTickets = binding.tvShowNumberOfTickets.text.toString().toInt()
        binding.btPlusTicket.isEnabled = currentNumberOfTickets < 10
        binding.btMinusTicket.isEnabled = currentNumberOfTickets > 1
        if(currentNumberOfTickets >= ticketItem.numberTickets){
            binding.btPlusTicket.isEnabled=currentNumberOfTickets < ticketItem.numberTickets
           // Toast.makeText(context,"Numar insuficient de bilete",Toast.LENGTH_LONG).show()

            showInsufficientTicketsDialog()
        }


        //fac update la text cand se schimba valoarea nr de tickete
        val selectedRadioButtonId= binding.radioGroupLeft.checkedRadioButtonId
        if (selectedRadioButtonId != -1) {
            val selectedRadioButton = binding.root.findViewById<RadioButton>(selectedRadioButtonId)
            val selectedNumber = selectedRadioButton.text.toString().toInt()
            val totalSum = selectedNumber * currentNumberOfTickets
            binding.tvSumaTotalaTicket.text = totalSum.toString()
        }
    }
    private fun showInsufficientTicketsDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null)
//        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
//        dialogMessage.text = "Numar insuficient de bilete"

//        val okButton = dialogView.findViewById<Button>(R.id.ok_button)
//        okButton.setOnClickListener {
//           alertDialog.dismiss()
//        }

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        builder.setCancelable(true)

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog = builder.create()
        alertDialog.show()
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
                //se seteaza datele pentru view aici ca sa apara ce e in tickete
                setDetailsTicket()
            }
        }
        )
    }

    private fun setDetailsTicket() {
        if (ticketPos != -1) {
            Log.d("Fragment_ticketItem", ticketList.toString())
            ticketList?.get(ticketPos)?.let { ticket ->
                //
                ticketItem=ticket

                // Accesezi TextView-urile din binding și setezi textul
                binding.tvNameTicketPayment.text = ticket.title
                binding.tvLocationTicketPayment.text = ticket.location
                binding.tvDateTicketPayment.text = ticket.data
                binding.tvShowNumberOfTickets.text = "1"
                binding.tvSumaTotalaTicket.text="-"

                binding.radioButton1.text = ticket.priceCategoryOne.toString()
                binding.radioButton2.text = ticket.priceCategoryTwo.toString()
                binding.radioButton3.text = ticket.priceCategoryThree.toString()
                binding.radioButton4.text = ticket.priceCategoryVIP.toString()
            }

        } else {
            Log.d("Fragment_ticketItem", "Nu s-a transmis corect argumentul")
        }
    }

}