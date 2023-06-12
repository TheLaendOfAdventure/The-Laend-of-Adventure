package de.hdmstuttgart.thelaendofadventure.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.ActivityFullscreenBinding

/**
 * This class represents a fullscreen activity in the Android application.
 * It extends the AppCompatActivity class,
 * which is a base class for activities that use the support library action bar features.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.setProperty("hw.gpu.mode", "swiftshader_indirect")
        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("Main", "ContainerCreated!")

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        supportActionBar?.hide()
    }
}
