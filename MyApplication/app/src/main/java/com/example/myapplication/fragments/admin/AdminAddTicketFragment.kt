package com.example.myapplication.fragments.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.myapplication.R
import com.example.myapplication.classes.Ticket
import com.example.myapplication.databinding.FragmentAdminAddTicketBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar

//private const val ARG_PARAM2 = "param2"


class AdminAddTicketFragment : Fragment() {


    lateinit var pickDateBtn: ImageView
    lateinit var selectedDateTV: TextView
    lateinit var pickTimeBtn: ImageView
    lateinit var selectedTimeTV: TextView
    lateinit var addTicket: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var binding:FragmentAdminAddTicketBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
       binding=FragmentAdminAddTicketBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()
        getHour()
        setDataBase()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDataBase() {
        db = FirebaseFirestore.getInstance()


        addTicket = binding.idBtnAddTicket
        addTicket.setOnClickListener {
            val category = binding.idEdtCategory.text.toString().trim()
            val city = binding.idEdtCity.text.toString().trim()
            val details = binding.idEdtDetails.text.toString().trim()
            val location = binding.idEdtLocation.text.toString().trim()
            val numberTicket = binding.idEdtNumberTicket.text.toString().toInt() ?: 0
            val priceCategoryOne = binding.idEdtPriceCategoryOne.text.toString().toInt() ?: 0
            val priceCategoryTwo = binding.idEdtPriceCategoryTwo.text.toString().toInt()
            val priceCategoryThree = binding.idEdtPriceCategoryThree.text.toString().toInt()
            val priceCategoryVIP = binding.idEdtPriceCategoryVIP.text.toString().toInt()
            val title = binding.idEdtTitle.text.toString().trim()
            val imageUrl = binding.idEdtImageUrl.text.toString().trim()


            val time1 = binding.idTVSelectedTime.text.toString()
            val date1 = binding.idTVSelectedDate.text.toString()
            var finalDate = ""

            try {
                val date = LocalDate.parse(date1, DateTimeFormatter.ofPattern("d-M-yyyy"))
                val time = LocalTime.parse(time1, DateTimeFormatter.ofPattern("H:m"))


                val dateTime = LocalDateTime.of(date, time)
                val isoDateTime = dateTime.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)

                finalDate = "$dateTime.321Z"

            } catch (e: Exception) {
                println("Eroare la parsarea datei sau a timpului: ${e.message}")
            }

            // if (!isAdded) return@setOnClickListener

            val ticket = hashMapOf(
                "category" to category,
                "city" to city,
                "data" to finalDate,
                "details" to details,
                "location" to location,
                "numberTickets" to numberTicket,
                "priceCategoryOne" to priceCategoryOne,
                "priceCategoryThree" to priceCategoryThree,
                "priceCategoryTwo" to priceCategoryTwo,
                "priceCategoryVIP" to priceCategoryVIP,
                "title" to title,
                "urlToImage" to imageUrl
            )

            val ticketCeva= Ticket(title,location,city,finalDate,details,numberTicket,priceCategoryOne, priceCategoryTwo
                , priceCategoryThree, priceCategoryVIP,category, imageUrl)

            Log.d("AddTicket", "Attempting to add ticket with data: $ticketCeva")
            db.collection("tickets")
                .add(ticketCeva)
                .addOnSuccessListener {
                        documentReference ->
                    Log.d("AddTicketSuccess", "DocumentSnapshot added with ID: ${documentReference.id}")
                    Toast.makeText(context, "Ticket added successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("AddTicketFailure", "Error adding document", e)
                    Toast.makeText(requireContext(), "Error adding ticket: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun getHour() {
        pickTimeBtn = binding.idBtnPickTime
        selectedTimeTV = binding.idTVSelectedTime

        pickTimeBtn.setOnClickListener {
            // on below line we are getting
            // the instance of our calendar.
            val c = Calendar.getInstance()

            // on below line we are getting our hour, minute.
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            // on below line we are initializing
            // our Time Picker Dialog
            val timePickerDialog = TimePickerDialog(
                context,
                { view, hourOfDay, minute ->
                    // on below line we are setting selected
                    // time in our text view.
                    selectedTimeTV.setText("$hourOfDay:$minute")
                },
                hour,
                minute,
                false
            )
            // at last we are calling show to
            // display our time picker dialog.
            timePickerDialog.show()
        }
    }

    private fun getData() {
        pickDateBtn = binding.idBtnPickDate
        selectedDateTV = binding.idTVSelectedDate

        pickDateBtn.setOnClickListener {
            // on below line we are getting
            // the instance of our calendar.
            val c = Calendar.getInstance()

            // on below line we are getting
            // our day, month and year.
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // on below line we are creating a
            // variable for date picker dialog.
            val datePickerDialog = context?.let { it1 ->
                DatePickerDialog(
                    // on below line we are passing context.
                    it1,
                    { view, year, monthOfYear, dayOfMonth ->
                        // on below line we are setting
                        // date to our text view.
                        selectedDateTV.text =
                            (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    },
                    // on below line we are passing year, month
                    // and day for the selected date in our date picker.
                    year,
                    month,
                    day
                )
            }
            // at last we are calling show
            // to display our date picker dialog.
            datePickerDialog?.show()
        }
    }

}