package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.myapplication.classes.Ticket
import com.example.myapplication.classes.User
import com.example.myapplication.fragments.CalendarFragment
import com.example.myapplication.fragments.FavoriteTicketsFragment
import com.example.myapplication.fragments.HomeFragment
import com.example.myapplication.fragments.SettingFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot


class DashboardActivity : AppCompatActivity() ,NavigationView.OnNavigationItemSelectedListener{

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var navigationView: NavigationView
    private var userList: ArrayList<User>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth= Firebase.auth

        drawerLayout=findViewById(R.id.drawer_spercamerge)

        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        navigationView=findViewById<NavigationView>(R.id.nav_view)


        val toggle= ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if(savedInstanceState==null)
        {
            replaceFragment(HomeFragment())
        }

        navigationView.setNavigationItemSelectedListener(this)

        getDataFirebase()
    }

    private fun getDataFirebase() {
        database = FirebaseFirestore.getInstance()
        database.collection("user").addSnapshotListener(object : EventListener<QuerySnapshot> {

            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        userList?.add(dc.document.toObject(User::class.java))
                    }
                    Log.d("Firestore error", userList?.size.toString())
                }
                setNameAndEmailUser()
            }
        }

        )
    }

    private fun setNameAndEmailUser() {
      val headerView: View = navigationView.getHeaderView(0) // index 0 pentru primul header (dacă ai mai multe)
        val emailTextView: TextView = headerView.findViewById(R.id.emailTextViewId) // înlocuiește cu id-ul real
        val nameTextView: TextView = headerView.findViewById(R.id.nameTextViewId) // înlocuiește cu id-ul real
        val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        // Setează textul pentru TextView-urile de email și nume
        emailTextView.setText(sharedPreferences.getString("email_user_key", "valoare_default"))

        for (user in userList!!) {
            if (user.email == emailTextView.text) {
                nameTextView.text ="${user.firstname} ${user.lastname}"
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction: FragmentTransaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container_main_drawer2,fragment)
        transaction.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home-> replaceFragment(HomeFragment())
            R.id.nav_setting->replaceFragment(SettingFragment())
            R.id.nav_popular->replaceFragment(FavoriteTicketsFragment())
            R.id.nav_calendar->replaceFragment(CalendarFragment())
            R.id.nav_log_out->{
                auth.signOut()
                startActivity(Intent(this,LoginActivity::class.java))
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START )
        return true
    }
}