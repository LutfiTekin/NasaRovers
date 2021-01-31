package tekin.lutfi.nasa.rest.api.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import tekin.lutfi.nasa.rest.api.R
import tekin.lutfi.nasa.rest.api.ui.viewmodel.MODE_GRID
import tekin.lutfi.nasa.rest.api.ui.viewmodel.MODE_LIST
import tekin.lutfi.nasa.rest.api.ui.viewmodel.RoverViewModel

class Home : AppCompatActivity() {

    private val roverViewModel: RoverViewModel by lazy {
        ViewModelProvider(this).get(RoverViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Firebase.remoteConfig.fetchAndActivate()
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_curiosity, R.id.navigation_opportunity, R.id.navigation_spirit
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_view_mode) {
            val current = roverViewModel.recyclerViewMode.value ?: MODE_LIST
            roverViewModel.recyclerViewMode.value =
                if (current == MODE_GRID) MODE_LIST else MODE_GRID
            val icon =
                if (current == MODE_GRID) R.drawable.ic_baseline_grid_on_24 else R.drawable.ic_baseline_view_list_24
            item.setIcon(icon)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

}