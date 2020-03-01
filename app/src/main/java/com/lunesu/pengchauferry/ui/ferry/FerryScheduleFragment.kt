package com.lunesu.pengchauferry.ui.ferry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lunesu.pengchauferry.*
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class FerryScheduleFragment : Fragment(), AdapterView.OnItemSelectedListener {

    companion object {
        fun newInstance() = FerryScheduleFragment()

        val STRINGS = mapOf(
            FerryPier.Central to R.string.Central,
            FerryPier.PengChau to R.string.PengChau,
//            FerryPier.TrappistMonastery to R.string.TrappistMonastery,
            FerryPier.DiscoveryBay to R.string.DiscoveryBay,
            FerryPier.MuiWo to R.string.MuiWo
//                FerryPier.CheungChau to R.string.CheungChau,
//                FerryPier.ChiMaWan to R.string.ChiMaWan,
//                FerryPier.HeiLingChau to R.string.HeiLingChau
        )
    }

    private val strings by lazy { STRINGS.mapValues { getString(it.value) } }
    private val viewModel by viewModels<FerryViewModel>()
    private lateinit var spinner: Spinner
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.schedule_fragment, container, false)
    }

    private val now: LocalTime? get() = viewModel.time.value?.toLocalTime()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        viewModel.state.observe(this, Observer {
            recyclerView.adapter = FerryRecyclerViewAdapter(it.ferries)
//            it.ferries.groupBy { it.to }.mapValues { it.value.find { it.time > now } }
        })
        viewModel.time.observe(this, Observer {
            // TODO: show time
        })
        super.onActivityCreated(savedInstanceState)
    }

    inner class SpinnerItem(val ferryPier: FerryPier) {
        override fun toString() = strings.getValue(ferryPier)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val iv = view.findViewById<ImageView>(R.id.grid)
//        iv.setImageDrawable(TilingDrawable(ContextCompat.getDrawable(view.context, R.drawable.gridline)!!))

        val items = strings.keys.map{ SpinnerItem(it) }

        recyclerView = view.findViewById(R.id.ferries)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        spinner = view.findViewById(R.id.spinner)
        spinner.adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.onItemSelectedListener = this
        super.onViewCreated(view, savedInstanceState)
    }

    //region AdapterView.OnItemSelectedListener

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // NOP
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val pier = parent?.getItemAtPosition(position) as? SpinnerItem
        pier?.let {
            viewModel.switchPier(it.ferryPier)
        }
    }
    //endregion

    class FerryRecyclerViewAdapter(private val ferries: List<Ferry>) : RecyclerView.Adapter<FerryRecyclerViewAdapter.ViewHolder>() {
        companion object {
            val uiFormatter: DateTimeFormatter = DateTimeFormat.shortTime()
//            const val SECONDS_IN_DAY = 60*60*24
            private const val scaling = 0.0002 // FIXME: react to pinch/zoom gestures

            val backgrounds = mapOf(
                FerryPier.Central to R.color.colorCentral,
                FerryPier.PengChau to R.color.colorHKKF,
                FerryPier.TrappistMonastery to R.color.colorKaito,
                FerryPier.DiscoveryBay to R.color.colorDiscoveryBay,
                FerryPier.MuiWo to R.color.colorNWFF
//                FerryPier.CheungChau to R.color.colorCheungChau,
//                FerryPier.ChiMaWan to R.color.colorChiMaWan,
//                FerryPier.HeiLingChau to R.color.colorHeiLingChau
            )

            fun millisToPx(millis: Int) = (millis * scaling).roundToInt()
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater
                .from(parent.context)
                .inflate(viewType, parent, false)
            return ViewHolder(view)
        }

        override fun getItemViewType(position: Int): Int = R.layout.ferry_bar

        override fun getItemCount(): Int = ferries.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val textView = holder.itemView.findViewById<TextView>(R.id.textView)
            val relView: RelativeLayout = holder.itemView as RelativeLayout
            val ferry = ferries[position]

            val start = millisToPx(ferry.time.millisOfDay)
            val len = millisToPx(ferry.dur.millis.toInt())

            relView.setPadding(start, 10, 0, 0)
            textView.text = textView.context.getString(R.string.ferry_info,
                ferry.time.toString(uiFormatter),
//                ferry.endTime.toString(uiFormatter),
                ferry.to)
            textView.layoutParams.width = len
            textView.setBackgroundResource(backgrounds.getValue(ferry.to))
//            relView.layoutParams.height = ((secondsBefore+seconds+secondsAfter)* scaling).roundToInt()
        }
    }

}
