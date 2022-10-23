package io.github.ivan8m8.courierhelper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.WindowCompat
import io.github.ivan8m8.courierhelper.databinding.ActivityMainBinding
import io.github.ivan8m8.courierhelper.ui.DeliveriesOsmMapFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.mainContainer, DeliveriesOsmMapFragment.newInstance())
                .commit()
        }
    }
}