package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import android.content.SharedPreferences

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    //aceast override este pentru a face ca atunci cand dai back
    //chiar sa faca back si sa nu se intercaleze fragmentele
    override fun onSupportNavigateUp(): Boolean {
        navController=findNavController(R.id.fragmentContainerView)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    //aceasta e functia care te tine logata pe aplicatie
    //si nu trebuie sa se autentifici de fiecare data cand deschizi aplicatia
    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser != null){
            //iau email-ul din share preferences
            val sharedPreferences = getSharedPreferences(Utils.NAME_FOLDER_PREFERENCES, Context.MODE_PRIVATE)
            val emailUser = sharedPreferences.getString(Utils.EMAIL_KEY, null)
            if(emailUser.equals(Utils.EMAIL_ADDRESS_ADMIN)){
                val intent = Intent(this, AdminDashboardActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            }
        }
    }
}