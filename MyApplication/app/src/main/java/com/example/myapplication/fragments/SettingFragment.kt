package com.example.myapplication.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myapplication.classes.User
import com.example.myapplication.databinding.FragmentSettingBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot


class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var database: FirebaseFirestore

    private var userList: ArrayList<User>? = ArrayList()
    private var userIdList: ArrayList<UserId>? = ArrayList()

    var userPreferences: ArrayList<String>? = ArrayList()

    private var semnCheck: Int = 0
    private lateinit var emailUser: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseFirestore.getInstance()
        getUserFirebase()
    }

    private fun getUserFirebase() {
        val sharedPreferences: SharedPreferences
        if (isAdded) {
            sharedPreferences =
                requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            emailUser =
                sharedPreferences.getString("email_user_key", "valoare implicită").toString()
        }


// Adăugarea unui listener pentru schimbările din colecția "user"
        database.collection("user").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return
                }

                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {

                        val user = dc.document.toObject(User::class.java)

                        userList?.add(user)

                        userIdList?.add(
                            UserId(
                                userId = dc.document.id,
                                email = user.email
                            )
                        )
                    }
                }

                Log.d("Firestore Info", "Numărul de utilizatori în listă: ${userList?.size}")
                setFavoriteTicketsToSee()
            }
        })
    }

    private fun setFavoriteTicketsToSee() {

        var firstname = ""
        var lastname = ""
        var userIdFind = ""

        for (user in userList!!) {
            if (emailUser.equals(user.email)) {
                userPreferences = user.preferences
                firstname = user.firstname
                lastname = user.lastname
            }
        }

        for (userId in userIdList!!) {
            if (emailUser.equals(userId.email)) {
                userIdFind = userId.userId
            }
        }

        if (userPreferences != null) {
            val preferencesText = userPreferences?.joinToString(separator = ", ")

            binding.tvSettingsHeaderText.text =
                "Preferintele tale pana acum sunt acestea: " + preferencesText
        } else {
            binding.tvSettingsHeaderText.text =
                "Nu ai preferinte setate inca"
        }

        if (isAdded && activity != null) {

            binding.buttonSetarePreferinte.setOnClickListener {
                val mapUpdateUser = mapOf(
                    "firstname" to firstname,
                    "lastname" to lastname,
                    "email" to emailUser,
                    "preferences" to preferencesUserListOfPreferences()
                )

                if (semnCheck == 1) {
                    database.collection("user").document(userIdFind).update(mapUpdateUser)
                        .addOnSuccessListener {
                            Log.d("UpdateUser", "User successfully updated with email: $emailUser")
                        }
                        .addOnFailureListener {
                            Log.w("UpdateUser", "Error updating user with email: $emailUser")
                        }

                    binding.tvSettingsWarningText.visibility = View.INVISIBLE
                    Toast.makeText(
                        context,
                        "Preferintele tale au fost toate salvate cu succes!",
                        Toast.LENGTH_LONG
                    ).show()
                    semnCheck=0
                } else {
                    binding.tvSettingsWarningText.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun preferencesUserListOfPreferences(): java.util.ArrayList<String> {
        val checkBoxConcerts = binding.checkBoxConcerts
        val checkBoxFestival = binding.checkBoxFestival
        val checkBoxTheatre = binding.checkBoxTheatre
        val checkBoxBalet = binding.checkBoxBalet
        val checkBoxExpozitie = binding.checkBoxExpozitie
        val checkBoxComedie = binding.checkBoxComedie
        val checkBoxSport = binding.checkBoxSport
        val checkBoxStreetFood = binding.checkBoxStreetFood
        val checkBoxWorkshop = binding.checkBoxWorkshop
        val checkBoxTarg = binding.checkBoxTarg

        val selectedPreferences = java.util.ArrayList<String>()

        if (checkBoxConcerts.isChecked) {
            selectedPreferences.add(checkBoxConcerts.text.toString())
            semnCheck = 1
        }
        if (checkBoxFestival.isChecked) {
            selectedPreferences.add(checkBoxFestival.text.toString())
            semnCheck = 1
        }
        if (checkBoxTheatre.isChecked) {
            selectedPreferences.add(checkBoxTheatre.text.toString())
            semnCheck = 1
        }
        if (checkBoxBalet.isChecked) {
            selectedPreferences.add(checkBoxBalet.text.toString())
            semnCheck = 1
        }
        if (checkBoxExpozitie.isChecked) {
            selectedPreferences.add(checkBoxExpozitie.text.toString())
            semnCheck = 1
        }
        if (checkBoxComedie.isChecked) {
            selectedPreferences.add(checkBoxComedie.text.toString())
            semnCheck = 1
        }
        if (checkBoxSport.isChecked) {
            selectedPreferences.add(checkBoxSport.text.toString())
            semnCheck = 1
        }
        if (checkBoxStreetFood.isChecked) {
            selectedPreferences.add(checkBoxStreetFood.text.toString())
            semnCheck = 1
        }
        if (checkBoxWorkshop.isChecked) {
            selectedPreferences.add(checkBoxWorkshop.text.toString())
            semnCheck = 1
        }
        if (checkBoxTarg.isChecked) {
            selectedPreferences.add(checkBoxTheatre.text.toString())
            semnCheck = 1
        }
        return selectedPreferences
    }
}

data class UserId(
    var userId: String = "",
    var email: String = "",
)
