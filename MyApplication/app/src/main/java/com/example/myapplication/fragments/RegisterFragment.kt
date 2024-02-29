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
import com.example.myapplication.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
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
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            Log.e("Firestore error", "01")
            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                Log.e("Firestore error", "11")
                if (pass == confirmPass) {
                    Log.e("Firestore error", "12")
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.e("Firestore error", "13")
//                            val intent = Intent(this, SignInActivity::class.java)
//                            startActivity(intent)
                            Toast.makeText(context, "User registered successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e("Firestore error", it.exception.toString())
                            Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT)
                                .show()

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
}
