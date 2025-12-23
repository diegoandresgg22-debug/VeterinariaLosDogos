package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity principal con BottomNavigation:
 * - Perfil (lista mascotas + IoT)
 * - Registrar Mascota
 * - Agendar Cita
 */
class MainActivity : AppCompatActivity() {
    private var bottomNav: BottomNavigationView? = null
    private var mAuth: FirebaseAuth? = null

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

            .also { bottomNav = it }<BottomNavigationView> findViewById (R.id.bottomNav)

        bottomNav.setOnNavigationItemSelectedListener({ item ->
            val fragment: Fragment?
            val id: Int = item.getItemId()
            if (id == R.id.menu_profile) {
                fragment = ProfileFragment()
            } else if (id == R.id.menu_register_pet) {
                fragment = RegisterPetFragment()
            } else if (id == R.id.menu_schedule) {
                fragment = ScheduleFragment()
            } else {
                return@setOnNavigationItemSelectedListener false
            }

            replaceFragment(fragment)
            true
        })

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.menu_profile)
            replaceFragment(ProfileFragment())
        }
    }

    protected override fun onStart() {
        super.onStart()
        val current: FirebaseUser? = mAuth.getCurrentUser()
        if (current == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
