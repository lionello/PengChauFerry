package com.lunesu.pengchauferry

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import android.view.*
import android.widget.RelativeLayout
import android.widget.TextView
import org.joda.time.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.lang.IllegalArgumentException
import kotlin.math.roundToInt
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.*
import android.location.LocationManager.NETWORK_PROVIDER
import android.os.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearSmoothScroller.SNAP_TO_END
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.observe


class MainActivity : AppCompatActivity() {

    companion object {
        private const val MY_PERMISSION_REQUEST_CODE = 123
        private const val PALM = "\uD83C\uDF34"
    }

    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var locationManager: LocationManager
    private var provider: String? = null
    private var canRequestLocationUpdate = false
    private val viewModel by viewModels<FerryViewModel>()

    class CustomSnapScroller(ctx: Context, private val snap: Int): androidx.recyclerview.widget.LinearSmoothScroller(ctx) {
        override fun getVerticalSnapPreference() = snap
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.times)
        recyclerView.layoutManager = StaggeredGridLayoutManager(3, VERTICAL)
        //recyclerView.setHasFixedSize(true)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        viewModel.state.observe(this) {
            this.title = getString(R.string.main_title, it.from) + (if (it.isHoliday) PALM else "")
            recyclerView.adapter = MyAdapter(it.ferries)
            scrollToNow()
        }
        viewModel.time.observe(this) {
            recyclerView.adapter?.notifyItemChanged(0)
        }

        val from = savedInstanceState?.getString("from") ?: "PengChau"
        viewModel.switchPier(FerryPier.valueOf(from))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("from", viewModel.state.value?.from.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()

        val criteria = Criteria()
        val permission: String
        if (Utils.isEmulator) {
            // The Android emulator only supports FINE/GPS location provider
            permission = Manifest.permission.ACCESS_FINE_LOCATION
            provider = locationManager.getBestProvider(criteria, true)
        } else {
            criteria.accuracy = Criteria.ACCURACY_COARSE
            permission = Manifest.permission.ACCESS_COARSE_LOCATION
            provider = NETWORK_PROVIDER
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED) {
            val lastLocation = locationManager.getLastKnownLocation(provider)
            if (lastLocation != null) {
                viewModel.switchPier(FerryPier.findNearest(lastLocation.longitude, lastLocation.latitude))
            }

            requestLocationUpdate()
        } else {
            // Request permission now (can be used next time)
            ActivityCompat.requestPermissions(this, arrayOf(permission), MY_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                requestLocationUpdate()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdate() {
        canRequestLocationUpdate = true
        locationManager.requestSingleUpdate(provider, object: LocationListener {
            override fun onLocationChanged(location: Location?) {
                if (location != null) {
                    val nowPier = FerryPier.findNearest(location.longitude, location.latitude)
                    if (nowPier != viewModel.state.value?.from) {
                        val text = getString(R.string.wrong_location, nowPier.toString())
                        Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderDisabled(provider: String?) {}
            override fun onProviderEnabled(provider: String?) {}
        }, this.mainLooper)
    }

    override fun onResume() {
        super.onResume()

        if (canRequestLocationUpdate) {
            requestLocationUpdate()
        }
    }

    private fun scrollToNow() {
        val now = FerryViewModel.now()
        val myAdapter = recyclerView.adapter as MyAdapter
        val index = myAdapter.list.indexOfFirst { it.time > now }
        val position = index + 1
        val smoothScroller = CustomSnapScroller(this, SNAP_TO_END)
        smoothScroller.targetPosition = position
        recyclerView.layoutManager!!.startSmoothScroll(smoothScroller)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.app_bar_switch)?.isChecked = viewModel.state.value?.isHoliday == true
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.app_bar_flip -> {
                val fromPier = viewModel.state.value?.from
                viewModel.switchPier(if (fromPier == FerryPier.Central) FerryPier.PengChau else FerryPier.Central)
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    class MyAdapter(val list: List<Ferry>) : androidx.recyclerview.widget.RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        private fun now() = FerryViewModel.now()
        private val startTime: LocalTime = if (!list.isEmpty() && list[0].time.isBefore(now())) list[0].time else now()

        companion object {
            val uiFormatter: DateTimeFormatter = DateTimeFormat.shortTime()
            const val SECONDS_IN_DAY = 60*60*24
        }

        class MyViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

        var scaling = 0.1 // FIXME: react to pinch/zoom gestures

        override fun onBindViewHolder(holder: MyViewHolder, pos: Int) {
            val textView = holder.itemView.findViewById<TextView>(R.id.textView)
            val relView: RelativeLayout = holder.itemView as RelativeLayout

            if (pos == 0) {
                val now = now()
                val secondsBefore = Seconds.secondsBetween(this.startTime, now()).seconds
                val nextFerry = this.list.find { it.time > now }?: this.list[0]
                val seconds = Seconds.secondsBetween(now, nextFerry.time).seconds
                val nextTimeView = holder.itemView.findViewById<TextView>(R.id.nextTime)
                val secondsAfter = Seconds.secondsBetween(now, LocalTime.MIDNIGHT).seconds + SECONDS_IN_DAY

                holder.itemView.setPadding(0, (secondsBefore*scaling).roundToInt(),0,0)
                textView.text = textView.context.getString(R.string.now, now.toString(uiFormatter))
                textView.height = (seconds*scaling).roundToInt()
                nextTimeView.text = nextTimeView.context.getString(R.string.nextTime, seconds/60)
                relView.layoutParams.height = ((secondsBefore+secondsAfter)*scaling).roundToInt()
            } else {
                val position = pos - 1
                val thisFerry = list[position]
                val endTime = thisFerry.endTime
                val seconds = thisFerry.dur.standardSeconds

                val prevTime = if (position <= 1) this.startTime else thisFerry.time
                val nextTime = if (position + 2 < list.size) list[position+2].time else if (position + 1 < list.size) list[position+1].time else endTime

                val secondsBefore = Seconds.secondsBetween(prevTime, thisFerry.time).seconds
                val secondsAfter = Seconds.secondsBetween(endTime, nextTime).seconds

                holder.itemView.setPadding(0, (secondsBefore*scaling).roundToInt(), 0, (secondsAfter*scaling).roundToInt())
                textView.text = textView.context.getString(R.string.ferry_info,
                    thisFerry.time.toString(uiFormatter),
                    endTime.toString(uiFormatter),
                    thisFerry.to)
                textView.height = (seconds*scaling).roundToInt()
                relView.layoutParams.height = ((secondsBefore+seconds+secondsAfter)*scaling).roundToInt()
                textView.setBackgroundResource(
                    when {
                        seconds <= 600 -> R.color.colorFaster
                        seconds <= 1800 -> R.color.colorFast
                        else -> R.color.colorSlow
                    }
                )
            }
        }

        override fun getItemViewType(position: Int) = if (position == 0) R.layout.ferry_now else R.layout.ferry_row

        override fun getItemCount() = list.size+1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater
                .from(parent.context)
                .inflate(viewType, parent, false)
            return MyViewHolder(view)
        }

    }

}
