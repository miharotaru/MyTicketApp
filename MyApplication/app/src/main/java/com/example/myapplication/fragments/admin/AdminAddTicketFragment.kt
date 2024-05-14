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
import android.widget.AdapterView
import android.widget.ArrayAdapter
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


class AdminAddTicketFragment : Fragment() {


    lateinit var pickDateBtn: ImageView
    lateinit var selectedDateTV: TextView
    lateinit var pickTimeBtn: ImageView
    lateinit var selectedTimeTV: TextView
    lateinit var addTicket: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentAdminAddTicketBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAdminAddTicketBinding.inflate(inflater, container, false)
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

        var category = ""

        val items2: ArrayList<String> = ArrayList()
        items2.addAll(
            listOf(
                "Concert",
                "Festival",
                "Teatru",
                "Balet/Dans",
                "Expozitie",
                "Comedie",
                "Sport",
                "Street food",
                "Workshop",
                "Targ"
            )
        )


        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.list_favorite_item,
            items2
        )

        val autoCompleteTextView = binding.idEdtCategory

        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) autoCompleteTextView.showDropDown()
        }
        autoCompleteTextView.setOnClickListener {
            autoCompleteTextView.showDropDown()
        }

        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { adapter, view, i, l ->
                category = adapter.getItemAtPosition(i).toString()
            }

        addTicket = binding.idBtnAddTicket
        addTicket.setOnClickListener {
            //val category = binding.idEdtCategory.text.toString().trim()

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
                val isoDateTime =
                    dateTime.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)

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

            val ticketCeva = Ticket(
                title,
                location,
                city,
                finalDate,
                details,
                numberTicket,
                priceCategoryOne,
                priceCategoryTwo,
                priceCategoryThree,
                priceCategoryVIP,
                category,
                imageUrl
            )

            Log.d("AddTicket", "Attempting to add ticket with data: $ticketCeva")
            db.collection("tickets")
                .add(ticketCeva)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        "AddTicketSuccess",
                        "DocumentSnapshot added with ID: ${documentReference.id}"
                    )
                    Toast.makeText(context, "Ticket added successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("AddTicketFailure", "Error adding document", e)
                    Toast.makeText(
                        requireContext(),
                        "Error adding ticket: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            clearFragment()
        }
    }

    private fun clearFragment() {
        binding.idEdtCity.text?.clear()
        binding.idEdtDetails.text?.clear()
        binding.idEdtLocation.text?.clear()
        binding.idEdtNumberTicket.text?.clear()
        binding.idEdtPriceCategoryOne.text?.clear()
        binding.idEdtPriceCategoryTwo.text?.clear()
        binding.idEdtPriceCategoryThree.text?.clear()
        binding.idEdtPriceCategoryVIP.text?.clear()
        binding.idEdtTitle.text?.clear()
        binding.idEdtImageUrl.text?.clear()
        binding.idTVSelectedTime.text="Selecteaza ora"
        binding.idTVSelectedDate.text="Selecteaza data"

    }

    private fun getHour() {
        pickTimeBtn = binding.idBtnPickTime
        selectedTimeTV = binding.idTVSelectedTime

        pickTimeBtn.setOnClickListener {

            val c = Calendar.getInstance()

            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                context,
                { view, hourOfDay, minute ->
                    selectedTimeTV.setText("$hourOfDay:$minute")
                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }
    }

    private fun getData() {
        pickDateBtn = binding.idBtnPickDate
        selectedDateTV = binding.idTVSelectedDate

        pickDateBtn.setOnClickListener {
            val c = Calendar.getInstance()

            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = context?.let { it1 ->
                DatePickerDialog(
                    it1,
                    { view, year, monthOfYear, dayOfMonth ->
                        selectedDateTV.text =
                            (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    },
                    year,
                    month,
                    day
                )
            }
            datePickerDialog?.show()
        }
    }
}