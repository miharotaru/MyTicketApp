package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.classes.User
import com.example.myapplication.databinding.FragmentRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.ArrayList

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var user:User
    private var db= Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passET.text.toString().trim()
            val confirmPass = binding.confirmPassEt.text.toString().trim()
            val firstname=binding.firstnameEt.text.toString().trim()
            val lastname=binding.lastnameEt.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()
                && firstname.isNotEmpty()&& lastname.isNotEmpty()) {
                if (pass == confirmPass) {
                    //aici se adauga noul user in firebase auth
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
//                            val intent = Intent(this, SignInActivity::class.java)
//                            startActivity(intent)


                            //user.preferences=preferencesUserListOfPreferences()
                            user=User(firstname,lastname,email,preferencesUserListOfPreferences())

                            saveUserInFirestore(user)

                            //Toast.makeText(context, "User registered successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT)
                    .show()

            }
        }
    }

    private fun preferencesUserListOfPreferences(): ArrayList<String> {
        val checkBoxConcerts = binding.checkBoxConcerts
        val checkBoxTheatre = binding.checkBoxTheatre
        val checkBoxOutdoor = binding.checkBoxOutdoor

        val selectedPreferences = ArrayList<String>()

        if (checkBoxConcerts.isChecked) {
            selectedPreferences.add(checkBoxConcerts.text.toString())
        }
        if (checkBoxTheatre.isChecked) {
            selectedPreferences.add(checkBoxTheatre.text.toString())
        }
        if (checkBoxOutdoor.isChecked) {
            selectedPreferences.add(checkBoxOutdoor.text.toString())
        }

       return selectedPreferences
    }


    private fun saveUserInFirestore(user: User) {
        db=Firebase.firestore
        db.collection("user").document().set(user).addOnSuccessListener {
            Toast.makeText(context, "User registered successfully!", Toast.LENGTH_SHORT).show()
             binding.emailEt.text?.clear()
             binding.passET.text?.clear()
             binding.confirmPassEt.text?.clear()
            binding.firstnameEt.text?.clear()
            binding.lastnameEt.text?.clear()
        }
            .addOnFailureListener {
                Toast.makeText(context, "Fail :(((", Toast.LENGTH_SHORT).show()
            }
    }
}
