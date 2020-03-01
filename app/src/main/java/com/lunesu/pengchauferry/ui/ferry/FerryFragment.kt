package com.lunesu.pengchauferry.ui.ferry

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.lunesu.pengchauferry.*
import org.joda.time.LocalTime
import org.joda.time.Seconds
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class FerryFragment : Fragment(), TabLayout.OnTabSelectedListener {

    companion object {
        fun newInstance() = FerryFragment()

        const val ARG_OBJECT = "object"
        val PIERS = setOf(
            FerryPier.Central,
            FerryPier.PengChau,
            FerryPier.DiscoveryBay,
            FerryPier.MuiWo
        )
    }

    private val viewModel by viewModels<FerryViewModel>({activity!!})
    private var adapter: FerryRecyclerViewAdapter? = null
    private val tabs = hashMapOf<FerryPier, TabLayout.Tab>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var tabLayoutFrom: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.ferry_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.state.observe(this, Observer {
            tabLayoutFrom.selectTab(tabs[it.from])
            adapter = FerryRecyclerViewAdapter(it.ferries)
            recyclerView.adapter = adapter
//            it.ferries.groupBy { it.to }.mapValues { it.value.find { it.time > now } }

            val now = viewModel.time.value?.toLocalTime()
            if (now != null) {
                var pos = it.ferries.indexOfFirst { it.time > now }
                if (pos == -1) pos = it.ferries.lastIndex
                adapter?.selected = pos
                val itemHeightPx = 50
                linearLayoutManager.scrollToPositionWithOffset(pos, itemHeightPx)
            }
        })

        viewModel.time.observe(this, Observer {
            val ferries = viewModel.state.value?.ferries
            if (ferries != null) {
                val now = it.toLocalTime()
                var pos = ferries.indexOfFirst { it.time > now }
                if (pos == -1) pos = ferries.lastIndex
                adapter?.selected = pos
            }
        })

        val from = savedInstanceState?.getString("from") ?: "PengChau"
        viewModel.switchPier(FerryPier.valueOf(from))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("from", viewModel.state.value?.from.toString())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.departures)
        linearLayoutManager = LinearLayoutManager(view.context)
        recyclerView.layoutManager = linearLayoutManager
        tabLayoutFrom = view.findViewById(R.id.tab_layout_from)
        PIERS.forEach {
            val tab = tabLayoutFrom.newTab().setText(STRINGS.getValue(it)).setTag(it)
            tabs[it] = tab
            tabLayoutFrom.addTab(tab)
        }
        tabLayoutFrom.addOnTabSelectedListener(this)
//        viewModel.switchPier(FerryPier.Central)

//        val tabLayoutTo = view.findViewById<TabLayout>(R.id.tab_layout_to)
//        STRINGS.forEach {
//            tabLayoutTo.addTab(tabLayoutTo.newTab().setText(it.value).setTag(it.key))
//        }

        super.onViewCreated(view, savedInstanceState)
    }

    class FerryRecyclerViewAdapter(private val ferries: List<Ferry>) : RecyclerView.Adapter<FerryRecyclerViewAdapter.ViewHolder>() {
        companion object {
            private fun now() = FerryViewModel.now().toLocalTime()
            private val uiFormatter: DateTimeFormatter = DateTimeFormat.shortTime()

            val COLORS = mapOf(
                FerryPier.Central to R.color.colorCentral,
                FerryPier.PengChau to R.color.colorPengChau,
                FerryPier.TrappistMonastery to R.color.colorTrappistMonastery,
                FerryPier.DiscoveryBay to R.color.colorDiscoveryBay,
                FerryPier.MuiWo to R.color.colorMuiWo
//                FerryPier.CheungChau to R.color.colorCheungChau,
//                FerryPier.ChiMaWan to R.color.colorChiMaWan,
//                FerryPier.HeiLingChau to R.color.colorHeiLingChau
            )
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val context: Context get() = itemView.context
            val textViewTime: TextView = itemView.findViewById(R.id.textView_time)
            val textViewDest: TextView = itemView.findViewById(R.id.textView_dest)
            val textViewWarn: TextView = itemView.findViewById(R.id.textView_warn)
            val imageViewWalk: ImageView = itemView.findViewById(R.id.imageView_walk)
            val textViewWalk: TextView = itemView.findViewById(R.id.textView_walk)
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

            if (position == selected) {
                val seconds = Seconds.secondsBetween(now, ferry.time).seconds
                holder.textViewWarn.text = holder.context.getString(R.string.nextTime, seconds/60)
                holder.itemView.setBackgroundColor(0x11223344)

                holder.imageViewWalk.visibility = View.VISIBLE
                holder.textViewWalk.text = "11 min" // TODO calc dist to pier
            } else {
                holder.textViewWarn.text = null
                holder.itemView.setBackgroundColor(0)
                holder.imageViewWalk.visibility = View.GONE
                holder.textViewWalk.text = null
            }

//            val start = millisToPx(ferry.time.millisOfDay)
//            val len = millisToPx(ferry.dur.millis.toInt())

            holder.textViewFare.text = holder.context.getString(R.string.ferry_fare, ferry.fare)
            holder.textViewTime.text = holder.context.getString(R.string.ferry_time,
                ferry.time.toString(uiFormatter),
                ferry.endTime.toString(uiFormatter))
            val ferryTo = holder.context.getString(STRINGS.getValue(ferry.to))
            holder.textViewDest.text = holder.context.getString(R.string.ferry_to, ferryTo)
            holder.textViewDest.setTextColor(ContextCompat.getColor(holder.context, COLORS.getValue(ferry.to)))
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        viewModel.switchPier(tab?.tag as FerryPier)
    }

}
