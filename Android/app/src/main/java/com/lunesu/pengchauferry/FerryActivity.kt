package com.lunesu.pengchauferry

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.lunesu.pengchauferry.ui.ferry.FerryFragment
import com.lunesu.pengchauferry.ui.ferry.FerryViewModel
import com.lunesu.pengchauferry.ui.ferry.LocationViewModel
import com.lunesu.pengchauferry.ui.ferry.Strings
import java.util.*

class FerryActivity : AppCompatActivity() {

    companion object {
        private const val MY_PERMISSION_REQUEST_CODE = 123
    }

    private val viewModel by viewModels<FerryViewModel>()
    private val locationViewModel by viewModels<LocationViewModel>()

    private var shouldSwitchPier = true
    private var overrideResources: Resources? = null

    override fun getResources(): Resources {
        if (overrideResources == null) {
            val config = super.getResources().configuration
            config.setLocale(Locale.getDefault())
            overrideResources =
                createConfigurationContext(config).resources
        }
        return overrideResources!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Preferences(this).language?.let {
            Locale.setDefault(Locale(it))
        }

        setContentView(R.layout.ferry_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FerryFragment.newInstance())
                .commitNow()
        }

        viewModel.state.observe(this, Observer {
            this.title = getString(R.string.title, Strings.localized(it.day, resources))
        })

        locationViewModel.location.observe(this, Observer {
            val nowPier = FerryPier.findNearest(it.latitude, it.longitude, FerryFragment.PIERS)
            if (nowPier != null) {
                if (nowPier != viewModel.state.value?.from) {
                    val text = getString(R.string.wrong_location, Strings.localized(nowPier, resources))
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                }
                if (shouldSwitchPier) {
                    shouldSwitchPier = false
                    viewModel.switchPier(nowPier)
                }
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                locationViewModel.refresh()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()

        val permission = Manifest.permission.ACCESS_FINE_LOCATION

        shouldSwitchPier = true
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // Request permission now (can be used next time)
            ActivityCompat.requestPermissions(this, arrayOf(permission),
                MY_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onResume() {
        super.onResume()
        locationViewModel.refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.app_bar_switch)?.isChecked = viewModel.state.value?.day == FerryDay.Holiday
        when (Locale.getDefault().language) {
            "en" ->
                menu?.findItem(R.id.app_bar_en)?.isChecked = true
            "zh" ->
                menu?.findItem(R.id.app_bar_zh)?.isChecked = true
        }
        return super.onPrepareOptionsMenu(menu)
    }

    @SuppressLint("MissingPermission")
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
//            R.id.app_bar_flip -> {
//                val fromPier = viewModel.state.value?.from
//                val autoPier = FerryPier.Central // TODO detect from location
//                viewModel.switchPier(if (fromPier == FerryPier.PengChau) autoPier else FerryPier.PengChau)
//                true
//            }
            R.id.app_bar_location -> {
                shouldSwitchPier = true
                locationViewModel.refresh()
                true
            }
            R.id.app_bar_switch -> {
                item.isChecked = viewModel.toggleHoliday()
                true
            }
            R.id.app_bar_refresh -> {
                viewModel.refresh()
                true
            }
            R.id.app_bar_store -> {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(
                        "https://play.google.com/store/apps/details?id=com.lunesu.pengchauferry")
                    setPackage("com.android.vending")
                }
                startActivity(intent)
                true
            }
            R.id.app_bar_en -> {
                Preferences(this).language = "en"
                recreate()
                true
            }
            R.id.app_bar_zh -> {
                Preferences(this).language = "zh"
                recreate()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
