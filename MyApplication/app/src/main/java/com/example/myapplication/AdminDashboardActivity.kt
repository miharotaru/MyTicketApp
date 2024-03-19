package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.myapplication.classes.User
import com.example.myapplication.fragments.CalendarFragment
import com.example.myapplication.fragments.FavoriteTicketsFragment
import com.example.myapplication.fragments.HomeFragment
import com.example.myapplication.fragments.SettingFragment
import com.example.myapplication.fragments.admin.AdminAddTicketFragment
import com.example.myapplication.fragments.admin.AdminCalendarFragment
import com.example.myapplication.fragments.admin.AdminHomeFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class AdminDashboardActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private var userList: ArrayList<User>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)
        auth= Firebase.auth

        drawerLayout=findViewById(R.id.drawer_spercamerge2)

        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        navigationView=findViewById<NavigationView>(R.id.nav_view_admin)


        val toggle= ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if(savedInstanceState==null)
        {
            replaceFragment(AdminHomeFragment())
        }

        navigationView.setNavigationItemSelectedListener(this)
        getUserFirebase()
    }
    private fun getUserFirebase() {
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
        val sharedPreferences = getSharedPreferences(Utils.NAME_FOLDER_PREFERENCES, Context.MODE_PRIVATE)
        // Setează textul pentru TextView-urile de email și nume
        emailTextView.setText(sharedPreferences.getString(Utils.EMAIL_KEY, "valoare_default"))

        for (user in userList!!) {
            if (user.email == emailTextView.text) {
                nameTextView.text ="${user.firstname} ${user.lastname}"
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction: FragmentTransaction =supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container_main_drawer3,fragment)
        transaction.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home_admin-> replaceFragment(AdminHomeFragment())
            R.id.nav_calendar_admin-> replaceFragment(AdminCalendarFragment())
            R.id.nav_add_ticket->replaceFragment(AdminAddTicketFragment())
            R.id.nav_log_out_admin->{
                auth.signOut()
                startActivity(Intent(this,LoginActivity::class.java))
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START )
        return true
    }
}