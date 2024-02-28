package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.myapplication.fragments.HomeFragment
import com.example.myapplication.fragments.SettingFragment
import com.google.android.material.navigation.NavigationView

class DashboardActivity : AppCompatActivity() ,NavigationView.OnNavigationItemSelectedListener{

    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        drawerLayout=findViewById(R.id.drawer_spercamerge)

        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView=findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle= ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if(savedInstanceState==null)
        {
            replaceFragment(HomeFragment())
        }

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
                Toast.makeText(this,"homeee", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_setting->replaceFragment(SettingFragment())
            //R.id.nav_popular->replaceFragment()
        }
        drawerLayout.closeDrawer(GravityCompat.START )
        return true
    }
}