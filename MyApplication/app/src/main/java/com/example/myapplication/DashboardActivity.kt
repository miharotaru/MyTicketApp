package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.myapplication.fragments.CalendarFragment
import com.example.myapplication.fragments.HomeFragment
import com.example.myapplication.fragments.SettingFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class DashboardActivity : AppCompatActivity() ,NavigationView.OnNavigationItemSelectedListener{

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var navigationView: NavigationView
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

//        navigationView.menu.clear()
//        // Inflarea layout-ului my_textview_layout.xml
//        val inflater = LayoutInflater.from(this)
//        val navHeader = inflater.inflate(R.layout.nav_header, null)
//
//
//        val layout = navHeader.findViewById<Layout>(R.id.nav_view)
//
//        // Setarea TextView-ului ca conținut al activității curente
//        val fullNameUser = navHeader.findViewById<TextView>(R.id.nav_header_profilname)
//        val email = navHeader.findViewById<TextView>(R.id.nav_header_profilemail)
//
//        val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
//
//        email.setText(sharedPreferences.getString("email_user_key", "valoare_default"))
//        //navigationView.removeHeaderView(navHeader)
//        //navigationView.addHeaderView(navHeader)



    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction: FragmentTransaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container_main_drawer2,fragment)
        transaction.commit()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home-> {
                replaceFragment(HomeFragment())
            }
            R.id.nav_setting->replaceFragment(SettingFragment())
            //R.id.nav_popular->replaceFragment()
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