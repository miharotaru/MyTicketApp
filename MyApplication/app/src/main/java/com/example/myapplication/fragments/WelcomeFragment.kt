package com.example.myapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentWelcomeBinding.inflate(inflater, container, false)
        binding.btToLoginFragment.setOnClickListener {
            it.findNavController().navigate(R.id.action_welcomeFragment_to_logInFragment)
        }

        binding.btToRegisterFragment.setOnClickListener {
            it.findNavController().navigate(R.id.action_welcomeFragment_to_registerFragment)
        }

        return binding.root
    }

}