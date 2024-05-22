package com.example.myapplication.fragments.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.classes.Ticket
import com.example.myapplication.databinding.FragmentAdminEditTicketBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AdminEditTicketFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentAdminEditTicketBinding
    private lateinit var ticket: Ticket
    private lateinit var category: String
    private lateinit var idTicket: String
    private lateinit var pickDateBtn: ImageView
    private lateinit var selectedDateTV: TextView
    private lateinit var pickTimeBtn: ImageView
    private lateinit var selectedTimeTV: TextView
    private var isTicketDeleted: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminEditTicketBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTicketFromPreferences()
        setDetailsTicket()
        setDropDownMenuCategory()
        getData()
        getHour()
        editTicket()
        deleteTicket()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun editTicket() {
        binding.idBtnEditareDate.setOnClickListener {

            if (!isTicketDeleted) {

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
                val category2 = binding.idEdtCategory.text.toString()

                var finalDate = ""

                try {
                    val date = LocalDate.parse(date1, DateTimeFormatter.ofPattern("d-M-yyyy"))
                    val time = LocalTime.parse(time1, DateTimeFormatter.ofPattern("H:m"))
                    val dateTime = LocalDateTime.of(date, time)
                    finalDate =
                        dateTime.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)
                } catch (e: Exception) {
                    Log.e("DateParseError", "Error parsing date or time: ${e.message}")
                }

                if (imageUrl.isEmpty()) {
                    imageUrl =
                        "https://teatrulioncreanga.ro/wp-content/uploads/2022/11/TILL_Site-600-x450.png"
                }

                if (city.isEmpty() || details.isEmpty() || location.isEmpty() || title.isEmpty()
                    || priceCategoryOne.isEmpty() || priceCategoryTwo.isEmpty()
                    || priceCategoryThree.isEmpty() || priceCategoryVIP.isEmpty()
                ) {
                    Toast.makeText(
                        context,
                        "Ai campuri necompletate!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val ticket5 = Ticket(
                        title,
                        location,
                        city,
                        finalDate,
                        details,
                        numberTicket.toIntOrNull() ?: 0,
                        priceCategoryOne.toIntOrNull() ?: 0,
                        priceCategoryTwo.toIntOrNull() ?: 0,
                        priceCategoryThree.toIntOrNull() ?: 0,
                        priceCategoryVIP.toIntOrNull() ?: 0,
                        category2,
                        imageUrl
                    )

                    val ticketEdit = mapOf(
                        "category" to ticket5.category,
                        "city" to ticket5.city,
                        "data" to ticket5.data,
                        "details" to ticket5.details,
                        "location" to ticket5.location,
                        "numberTickets" to ticket5.numberTickets,
                        "priceCategoryOne" to ticket5.priceCategoryOne,
                        "priceCategoryTwo" to ticket5.priceCategoryTwo,
                        "priceCategoryThree" to ticket5.priceCategoryThree,
                        "priceCategoryVIP" to ticket5.priceCategoryVIP,
                        "title" to ticket5.title,
                        "urlToImage" to ticket5.urlToImage
                    )

                    db.collection("tickets").document(idTicket).update(ticketEdit)
                        .addOnSuccessListener {
                            Log.d("UpdateUser", "User successfully updated")
                            Toast.makeText(context, "A fost editat", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Log.w("UpdateUser", "Error updating", it)
                        }
                }
            } else {
                Toast.makeText(context, "Ticket-ul a fost șters", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setDropDownMenuCategory() {
        val items2: ArrayList<String> = arrayListOf(
            "Concert", "Festival", "Teatru", "Balet/Dans", "Expozitie",
            "Comedie", "Sport", "Street food", "Workshop", "Targ"
        )
        val adapter = ArrayAdapter(requireContext(), R.layout.list_favorite_item, items2)
        val autoCompleteTextView = binding.idEdtCategory

        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) autoCompleteTextView.showDropDown()
        }
        autoCompleteTextView.setOnClickListener {
            autoCompleteTextView.showDropDown()
        }

        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { adapter, _, i, _ ->
                category = adapter.getItemAtPosition(i).toString()
            }
    }

    private fun setDetailsTicket() {
        binding.idEdtCity.setText(ticket.city)
        binding.idEdtDetails.setText(ticket.details)
        binding.idEdtLocation.setText(ticket.location)
        binding.idEdtNumberTicket.setText(ticket.numberTickets.toString())
        binding.idEdtPriceCategoryOne.setText(ticket.priceCategoryOne.toString())
        binding.idEdtPriceCategoryTwo.setText(ticket.priceCategoryTwo.toString())
        binding.idEdtPriceCategoryThree.setText(ticket.priceCategoryThree.toString())
        binding.idEdtPriceCategoryVIP.setText(ticket.priceCategoryVIP.toString())
        binding.idEdtCategory.setText(ticket.category)
        binding.idEdtTitle.setText(ticket.title)
        binding.idTVSelectedTime.text = ticket.data.substring(11, 16)
        binding.idEdtImageUrl.setText(ticket.urlToImage)


        val newData = ticket.data.substring(0, 10)
        ticket.data.substring(0, 10)
        val components = newData.split("-")
        val year = components[0]
        val month = components[1].padStart(2, '0')
        val day = components[2].padStart(2, '0')
        binding.idTVSelectedDate.text = "$day-$month-$year"
    }

    private fun getTicketFromPreferences() {
        context?.let { ctx ->
            val sharedPreferences: SharedPreferences =
                ctx.getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE)
            val ticketJson = sharedPreferences.getString("ticketCeva", null)
            idTicket = sharedPreferences.getString("idValue", null).toString()
            Log.d("ceva", idTicket)
            val gson = Gson()
            ticket = gson.fromJson(ticketJson, Ticket::class.java)
        }
    }

    private fun getHour() {
        pickTimeBtn = binding.idBtnPickTime
        selectedTimeTV = binding.idTVSelectedTime
        pickTimeBtn.setOnClickListener {
            if (!isTicketDeleted) {
                val c = Calendar.getInstance()
                val hour = c.get(Calendar.HOUR_OF_DAY)
                val minute = c.get(Calendar.MINUTE)
                val timePickerDialog = TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        selectedTimeTV.text = "$hourOfDay:$minute"
                    },
                    hour,
                    minute,
                    false
                )
                timePickerDialog.show()

            } else {
                Toast.makeText(context, "Ticket-ul a fost șters", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getData() {
        pickDateBtn = binding.idBtnPickDate
        selectedDateTV = binding.idTVSelectedDate

        pickDateBtn.setOnClickListener {
            if (!isTicketDeleted) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, year, monthOfYear, dayOfMonth ->
                        selectedDateTV.text = "$dayOfMonth-${monthOfYear + 1}-$year"
                    },
                    year,
                    month,
                    day
                )
                datePickerDialog.datePicker.minDate = c.timeInMillis
                datePickerDialog.show()
            } else {
                Toast.makeText(context, "Ticket-ul a fost șters", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteTicket() {

        binding.idBtnDeleteDate.setOnClickListener {
            db.collection("tickets").document(idTicket).delete()
                .addOnSuccessListener {
                    isTicketDeleted = true
                    cleanAll()
                    Toast.makeText(context, "Ticket a fost șters", Toast.LENGTH_SHORT).show()
                    Log.d("DeleteUser", "User successfully deleted")
                }
                .addOnFailureListener {
                    Log.w("DeleteUser", "Error deleting user", it)
                }
        }
    }

    private fun cleanAll() {
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
        binding.idEdtCategory.text?.clear()
        binding.idTVSelectedTime.text = getString(R.string.selecteaza_ora_delet)
        binding.idTVSelectedDate.text = getString(R.string.selecteaza_data_delet)
    }
}
