package com.lunesu.pengchauferry.ui.ferry

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lunesu.pengchauferry.Ferry
import com.lunesu.pengchauferry.FerryPier
import com.lunesu.pengchauferry.R
import kotlin.math.ceil
import org.joda.time.LocalTime
import org.joda.time.Minutes
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class FerryFragment : Fragment(), AdapterView.OnItemSelectedListener {

    companion object {
        fun newInstance() = FerryFragment()

        const val WALKING_SPEED = 0.018

        val PIERS = arrayOf(
            FerryPier.Central,
            FerryPier.PengChau,
            FerryPier.DiscoveryBay,
            FerryPier.MuiWo
        )
    }

    private val viewModel by viewModels<FerryViewModel>({ requireActivity() })
    private val locationViewModel by viewModels<LocationViewModel>({ requireActivity() })
    private var adapter: FerryRecyclerViewAdapter? = null
    private var walkingTime = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var spinnerFrom: Spinner
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.ferry_fragment, container, false)
    }

    private fun updateSelected(ferries: List<Ferry>?, now: LocalTime) {
        if (ferries != null) {
            val then = now.plusMinutes(walkingTime)
            var pos = ferries.indexOfFirst { it.time > then }
            if (pos == -1) pos = ferries.lastIndex
            adapter?.selected = pos
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.state.observe(viewLifecycleOwner, Observer {
            spinnerFrom.setSelection(PIERS.indexOf(it.from))
            adapter = FerryRecyclerViewAdapter(it.ferries)
            recyclerView.adapter = adapter
//            it.ferries.groupBy { it.to }.mapValues { it.value.find { it.time > now } }

            val loc = locationViewModel.location.value
            if (loc != null) {
                this.updateWalkingTime(loc, it.from)
            }

            val now = viewModel.time.value
            if (now != null) {
                this.updateSelected(it.ferries, now.toLocalTime())
//                val itemHeightPx = 150
//                linearLayoutManager.scrollToPositionWithOffset(pos, itemHeightPx)
            }
        })

        viewModel.time.observe(viewLifecycleOwner, Observer {
            this.updateSelected(viewModel.state.value?.ferries, it.toLocalTime())
//                val today = it.toLocalDate()
//                if (viewModel.getDay(today) != viewModel.state.value?.day) {
//                }
        })

        locationViewModel.location.observe(viewLifecycleOwner, Observer {
            val from = this.viewModel.state.value?.from
            if (from != null) {
                this.updateWalkingTime(it, from)
            }
        })

        val from = savedInstanceState?.getString("from") ?: "PengChau"
        viewModel.switchPier(FerryPier.valueOf(from))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("from", viewModel.state.value?.from.toString())
    }

    private fun updateWalkingTime(from: Location, to: FerryPier) {
        val minutes = ceil(to.coordinate.distance(from.latitude, from.longitude) * WALKING_SPEED).toInt()
        val tv = spinnerFrom.findViewById<TextView>(R.id.textView_walk)
        val iv = spinnerFrom.findViewById<ImageView>(R.id.imageView_walk)
        if (tv != null && minutes < 60) {
            tv.text = getString(R.string.x_min, minutes)
            iv?.visibility = View.VISIBLE
            tv.visibility = View.VISIBLE
            walkingTime = minutes
        } else {
            iv?.visibility = View.INVISIBLE
            tv?.visibility = View.INVISIBLE
            walkingTime = 0
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swipeRefreshLayout = view.findViewById(R.id.swipeContainer)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchAll()
            swipeRefreshLayout.isRefreshing = false
        }

        recyclerView = view.findViewById(R.id.departures)
        linearLayoutManager = LinearLayoutManager(view.context)
        recyclerView.layoutManager = linearLayoutManager
        spinnerFrom = view.findViewById(R.id.spinner_from)
        val items = PIERS.map { resources.getString(Strings.PIERS_DUAL.getValue(it)) }
        ArrayAdapter(requireActivity(), R.layout.spinner_item_selected, R.id.text1, items).let {
//            it.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            spinnerFrom.adapter = it
        }
        spinnerFrom.onItemSelectedListener = this

        super.onViewCreated(view, savedInstanceState)
    }

    class FerryRecyclerViewAdapter(private val ferries: List<Ferry>) : RecyclerView.Adapter<FerryRow>() {
        companion object {
            private fun now() = FerryViewModel.now().toLocalTime()
            private val uiFormatter: DateTimeFormatter = DateTimeFormat.shortTime()

            val COLORS = mapOf(
                FerryPier.Central to R.color.colorHKKF,
//                FerryPier.PengChau to R.color.colorHKKF,
                FerryPier.TrappistMonastery to R.color.colorKaito,
                FerryPier.DiscoveryBay to R.color.colorKaito,
                FerryPier.MuiWo to R.color.colorNWFF,
                FerryPier.CheungChau to R.color.colorNWFF,
                FerryPier.ChiMaWan to R.color.colorNWFF,
                FerryPier.HeiLingChau to R.color.colorHKKF
            )
        }

        var selected: Int = -1
            set(value) {
                if (field != value) {
                    this.notifyItemChanged(field)
                    this.notifyItemChanged(value)
                    field = value
                }
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FerryRow {
            val view = LayoutInflater
                .from(parent.context)
                .inflate(viewType, parent, false)
            return FerryRow(view)
        }

        override fun getItemViewType(position: Int): Int = R.layout.ferry_row

        override fun getItemCount(): Int = ferries.size

        override fun onBindViewHolder(holder: FerryRow, position: Int) {
            val ferry = ferries[position]
            val now = now()

            val minutes = Minutes.minutesBetween(now, ferry.time).minutes
            if (minutes in 0..59 || position == selected) {
                holder.textViewWarn.text = holder.context.getString(R.string.nextTime, minutes)
            } else {
                holder.textViewWarn.text = null
            }

            if (position == selected) {
                holder.itemView.setBackgroundResource(R.color.colorAccent)
            } else {
                holder.itemView.setBackgroundColor(0)
            }

            holder.textViewFare.text = holder.context.getString(R.string.ferry_fare, ferry.fare)
            holder.textViewTime.text = holder.context.getString(R.string.ferry_time,
                ferry.time.toString(uiFormatter),
                ferry.endTime.toString(uiFormatter))
            val ferryTo = Strings.localized(ferry.to, holder.context.resources)
            if (ferry.via != null) {
                val ferryVia = Strings.localized(ferry.via, holder.context.resources)
                holder.textViewDest.text = holder.context.getString(R.string.ferry_to_via, ferryTo, ferryVia)
            } else {
                holder.textViewDest.text = holder.context.getString(R.string.ferry_to, ferryTo)
            }
            val company = if (ferry.to != FerryPier.PengChau) ferry.to else ferry.from
            holder.textViewDest.setTextColor(ContextCompat.getColor(holder.context, COLORS.getValue(company)))
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // NOP
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.switchPier(PIERS[position])
    }
}
