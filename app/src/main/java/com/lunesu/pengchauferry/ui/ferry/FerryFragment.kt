package com.lunesu.pengchauferry.ui.ferry

import android.content.Context
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
import org.joda.time.Minutes
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import kotlin.math.ceil

class FerryFragment : Fragment(), AdapterView.OnItemSelectedListener {

    companion object {
        fun newInstance() = FerryFragment()

        const val ARG_OBJECT = "object"
        val PIERS = arrayOf(
            FerryPier.Central,
            FerryPier.PengChau,
            FerryPier.DiscoveryBay,
            FerryPier.MuiWo
        )
    }

    private val viewModel by viewModels<FerryViewModel>({requireActivity()})
    private val locationViewModel by viewModels<LocationViewModel>({requireActivity()})
    private var adapter: FerryRecyclerViewAdapter? = null
    private var walkingTime = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var spinnerFrom: Spinner
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.ferry_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.state.observe(this, Observer {
            spinnerFrom.setSelection(PIERS.indexOf(it.from))
            adapter = FerryRecyclerViewAdapter(it.ferries)
            recyclerView.adapter = adapter
//            it.ferries.groupBy { it.to }.mapValues { it.value.find { it.time > now } }

            val loc = locationViewModel.location.value
            if (loc != null) {
                updateWalkingTime(loc, it.from)
            }

            val now = viewModel.time.value?.toLocalTime()?.plusMinutes(walkingTime)
            if (now != null) {
                var pos = it.ferries.indexOfFirst { it.time > now }
                if (pos == -1) pos = it.ferries.lastIndex
                adapter?.selected = pos
//                val itemHeightPx = 150
//                linearLayoutManager.scrollToPositionWithOffset(pos, itemHeightPx)
            }

        })

        viewModel.time.observe(this, Observer {
            val ferries = viewModel.state.value?.ferries
            if (ferries != null) {
                val now = it.toLocalTime().plusMinutes(walkingTime)
                var pos = ferries.indexOfFirst { it.time > now }
                if (pos == -1) pos = ferries.lastIndex
                adapter?.selected = pos

//                val today = it.toLocalDate()
//                if (viewModel.getDay(today) != viewModel.state.value?.day) {
//                }
            }
        })

        locationViewModel.location.observe(this, Observer {
            val from = viewModel.state.value?.from
            if (from != null) {
                updateWalkingTime(it, from)
            }
        })

        val from = savedInstanceState?.getString("from") ?: "PengChau"
        viewModel.switchPier(FerryPier.valueOf(from))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("from", viewModel.state.value?.from.toString())
    }

    private fun updateWalkingTime(to: Location, from: FerryPier) {
        val minutes = ceil(from.distance(to.latitude, to.longitude) * 0.018).toInt()
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
        val items = PIERS.map { getString(STRINGS.getValue(it)) }
        ArrayAdapter(requireActivity(), R.layout.spinner_item_selected, R.id.text1, items).let {
//            it.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            spinnerFrom.adapter = it
        }
        spinnerFrom.onItemSelectedListener = this

        super.onViewCreated(view, savedInstanceState)
    }

    class FerryRecyclerViewAdapter(private val ferries: List<Ferry>) : RecyclerView.Adapter<FerryRecyclerViewAdapter.ViewHolder>() {
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

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val context: Context get() = itemView.context
            val textViewTime: TextView = itemView.findViewById(R.id.textView_time)
            val textViewDest: TextView = itemView.findViewById(R.id.textView_dest)
            val textViewWarn: TextView = itemView.findViewById(R.id.textView_warn)
//            val imageViewWalk: ImageView = itemView.findViewById(R.id.imageView_walk)
//            val textViewWalk: TextView = itemView.findViewById(R.id.textView_walk)
            val textViewFare: TextView = itemView.findViewById(R.id.textView_fare)
        }

        var selected: Int = -1
            set(value) {
                if (field != value) {
                    this.notifyItemChanged(field)
                    this.notifyItemChanged(value)
                    field = value
                }
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater
                .from(parent.context)
                .inflate(viewType, parent, false)
            return ViewHolder(view)
        }

        override fun getItemViewType(position: Int): Int = R.layout.departure

        override fun getItemCount(): Int = ferries.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val ferry = ferries[position]
            val now = now()

            val minutes = Minutes.minutesBetween(now, ferry.time).minutes
            if (minutes in 0..119) {
                holder.textViewWarn.text = holder.context.getString(R.string.nextTime, minutes)
            } else {
                holder.textViewWarn.text = null
            }

            if (position == selected) {
                holder.itemView.setBackgroundColor(0x11223344)
            } else {
                holder.itemView.setBackgroundColor(0)
            }

            holder.textViewFare.text = holder.context.getString(R.string.ferry_fare, ferry.fare)
            holder.textViewTime.text = holder.context.getString(R.string.ferry_time,
                ferry.time.toString(uiFormatter),
                ferry.endTime.toString(uiFormatter))
            val ferryTo = holder.context.getString(STRINGS.getValue(ferry.to))
            if (ferry.via != null) {
                val ferryVia = holder.context.getString(STRINGS.getValue(ferry.via))
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
