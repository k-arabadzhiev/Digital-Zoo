package com.pollux.digitalzoo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.*
import com.pollux.digitalzoo.databinding.ActivityMainBinding
import com.pollux.digitalzoo.util.C
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val params = binding.collapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams
            if (destination.id == R.id.animalsFragment) {
                binding.bottomNavigation.visibility = View.VISIBLE
                binding.appBar.setExpanded(true, true)
                binding.collapsingToolbarLayout.title = C.COLLAPSING_TOOLBAR_TITLE
            } else {
                binding.bottomNavigation.visibility = View.GONE
                binding.appBar.setExpanded(false, true)
                binding.collapsingToolbarLayout.title = C.EMPTY_TITLE
            }
            if (destination.id == R.id.animalSearchFragment || destination.id == R.id.addEditAnimalFragment) {
                params.scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_EXIT_UNTIL_COLLAPSED or
                        SCROLL_FLAG_SNAP
            } else {
                params.scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_SNAP or
                        SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED
            }
        }

        disableDrag()

        appBarConfiguration = AppBarConfiguration(
            //navController.graph
            setOf(
                R.id.animalsFragment,
                R.id.addEditAnimalFragment,
                R.id.loginFragment
            )
        )

        //setSupportActionBar(binding.toolbar)
        setSupportActionBar(binding.toolbarHome)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.setOnNavigationItemReselectedListener { } // quick & dirty
    }

    private fun disableDrag() {
        val params = binding.appBar.layoutParams as CoordinatorLayout.LayoutParams
        if (params.behavior == null)
            params.behavior = AppBarLayout.Behavior()
        val behaviour = params.behavior as AppBarLayout.Behavior
        behaviour.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return false
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}