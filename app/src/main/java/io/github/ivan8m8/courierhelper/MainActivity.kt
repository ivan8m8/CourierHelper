package io.github.ivan8m8.courierhelper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.WindowCompat
import io.github.ivan8m8.courierhelper.databinding.ActivityMainBinding
import io.github.ivan8m8.courierhelper.navigation.MyNavigator
import io.github.ivan8m8.courierhelper.ui.fragments.MainFragment
import io.github.ivan8m8.courierhelper.navigation.holder.INavigatorHolder
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val navigatorHolder: INavigatorHolder by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigatorHolder.onCreate(MyNavigator(get(), supportFragmentManager, R.id.mainContainer)) {
            if (savedInstanceState == null) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.mainContainer, MainFragment.newInstance())
                    .commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.onResume()
    }

    override fun onPause() {
        navigatorHolder.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        navigatorHolder.onDestroy()
        super.onDestroy()
    }
}