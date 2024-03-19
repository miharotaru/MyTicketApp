package com.example.myapplication.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myapplication.AdminDashboardActivity
import com.example.myapplication.DashboardActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLogInBinding
import com.google.firebase.auth.FirebaseAuth

class LogInFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentLogInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {

                        //salvez email-ul in share preferences ca sa am acces la el in alte clase
                        // ca sa pot sa ma duc dupa in "user" si sa recuperez numele si prenumele

                        // Obține referința la SharedPreferences folosind contextul fragmentului
                        setEmailInSharePreferences(email)
                        if (email.equals("kiki@gmail.com")) {
                            val intent =
                                Intent(requireContext(), AdminDashboardActivity::class.java)
                            startActivity(intent)
                        } else {
                            val intent = Intent(requireContext(), DashboardActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
            } else {
                Toast.makeText(context, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT)
                    .show()

            }
        }
    }

    private fun setEmailInSharePreferences(email: String) {
        val sharedPreferences =
            requireContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email_user_key", email)
        editor.apply()
    }
}