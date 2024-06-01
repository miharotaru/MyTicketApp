package com.example.myapplication.fragments.admin


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
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
import com.google.gson.Gson
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AdminAddTicketFragment : Fragment() {
    private lateinit var pickDateBtn: ImageView
    private lateinit var selectedDateTV: TextView
    private lateinit var pickTimeBtn: ImageView
    private lateinit var selectedTimeTV: TextView
    private lateinit var addTicket: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentAdminAddTicketBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAdminAddTicketBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
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
        var category = ""
        val items2: ArrayList<String> = ArrayList()
        items2.addAll(
            listOf(
                "Concert", "Festival", "Teatru", "Balet/Dans", "Expozitie",
                "Comedie", "Sport", "Street food", "Workshop", "Targ"
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
//------ AICI SE SETEAZA CATEGORIA

        addTicket = binding.idBtnAddTicket
        addTicket.setOnClickListener {
            val city = binding.idEdtCity.text.toString()
            val details = binding.idEdtDetails.text.toString()
            val location = binding.idEdtLocation.text.toString()
            val numberTicket = binding.idEdtNumberTicket.text.toString()
            val priceCategoryOne = binding.idEdtPriceCategoryOne.text.toString()
            val priceCategoryTwo = binding.idEdtPriceCategoryTwo.text.toString()
            val priceCategoryThree = binding.idEdtPriceCategoryThree.text.toString()
            val priceCategoryVIP = binding.idEdtPriceCategoryVIP.text.toString()
            val title = binding.idEdtTitle.text.toString()
            val time1 = binding.idTVSelectedTime.text.toString()
            val date1 = binding.idTVSelectedDate.text.toString()
            var imageUrl = binding.idEdtImageUrl.text.toString()

            if (city.isEmpty() || details.isEmpty() || location.isEmpty() || title.isEmpty()
                || time1 == getString(R.string.selecteaza_ora) || date1 == getString(R.string.selecteaza_data)
                || category.isEmpty() || priceCategoryOne.isEmpty() || priceCategoryTwo.isEmpty()
                || priceCategoryThree.isEmpty() || priceCategoryVIP.isEmpty()) {
                Toast.makeText(context, "Ai campuri necompletate", Toast.LENGTH_LONG).show()
            } else {
                var finalDate = ""

                try {
                    val date = LocalDate.parse(date1, DateTimeFormatter.ofPattern("d-M-yyyy"))
                    val time = LocalTime.parse(time1, DateTimeFormatter.ofPattern("H:m"))
                    val dateTime = LocalDateTime.of(date, time)
                    finalDate = "$dateTime.321Z"
                } catch (e: Exception) {
                    println("Eroare la parsarea datei sau a timpului: ${e.message}")
                }

                if (imageUrl.isEmpty()) {
                    imageUrl = "https://teatrulioncreanga.ro/wp-content/uploads/2022/11/TILL_Site-600-x450.png"
                }

                val ticketCeva = Ticket(
                    title, location, city, finalDate, details, numberTicket.toInt(),
                    priceCategoryOne.toInt(), priceCategoryTwo.toInt(),
                    priceCategoryThree.toInt(), priceCategoryVIP.toInt(), category, imageUrl
                )

                Log.d("AddTicket", "Attempting to add ticket with data: $ticketCeva")
                db.collection("tickets")
                    .add(ticketCeva)
                    .addOnSuccessListener { documentReference ->
                        Log.d("AddTicketSuccess", "DocumentSnapshot added with ID: ${documentReference.id}")
                        Toast.makeText(context, "Ticket added successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("AddTicketFailure", "Error adding document", e)
                        Toast.makeText(requireContext(), "Error adding ticket: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                cleanAll2()
                isNotificationSetOn(ticketCeva)
            }
        }
    }

    private fun isNotificationSetOn(ticket: Ticket) {
        val checkBox = binding.checkBoxNotification

        if (checkBox.isChecked) {
            val someIntValue = 1

            context?.let { ctx ->
                val sharedPreferences: SharedPreferences =
                    ctx.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

                val gson = Gson()
                val ticketJson = gson.toJson(ticket)

                val editor = sharedPreferences.edit()
                editor.putString("ticketCeva", ticketJson)
                editor.putInt("someIntValue", someIntValue)
                editor.apply()
            }
        }
    }

    private fun cleanAll2() {
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
        binding.idTVSelectedTime.text = getString(R.string.selecteaza_ora)
        binding.idTVSelectedDate.text = getString(R.string.selecteaza_data)
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
                        selectedDateTV.text = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    },
                    year,
                    month,
                    day
                )
            }
            // Set the minimum date to the current date
            datePickerDialog?.datePicker?.minDate = c.timeInMillis

            datePickerDialog?.show()
        }
    }
}
