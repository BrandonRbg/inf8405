package ca.polymtl.inf8405.Main.StatsView


import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.BatteryManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.polymtl.inf8405.R
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_stats_view.view.*
import java.util.*


class StatsViewFragment : Fragment() {

    private val firstTimestamp = Date().time

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats_view, container, false)
        setupBatteryLevelChart(view)
        updateBatteryView(view)
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                updateBatteryView(view)
            }
        }, 0, 1_000)
        return view
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    fun setupBatteryLevelChart(view: View) {
        view.batteryLevelChart.setDrawGridBackground(false)
        view.batteryLevelChart.legend.isEnabled = false
        view.batteryLevelChart.description.isEnabled = false
        view.batteryLevelChart.getAxisLeft().setEnabled(true)
        view.batteryLevelChart.getAxisLeft().axisMinimum = 0f
        view.batteryLevelChart.getAxisLeft().axisMaximum = 100f
        view.batteryLevelChart.getAxisLeft().setSpaceTop(40f)
        view.batteryLevelChart.getAxisLeft().setSpaceBottom(40f)
        view.batteryLevelChart.getAxisRight().setEnabled(false)
        view.batteryLevelChart.getXAxis().setEnabled(false)
        view.batteryLevelChart.animateX(2500)
        view.batteryLevelChart.data =
            LineData(
                LineDataSet(
                    mutableListOf(),
                    "Battery Level"
                ).apply {
                    setMode(LineDataSet.Mode.LINEAR)
                    setCubicIntensity(0.2f)
                    setDrawFilled(true)
                    setDrawCircles(false)
                    setDrawValues(false)
                    setLineWidth(1.8f)
                    setCircleRadius(4f)
                    setCircleColor(Color.WHITE)
                    setHighLightColor(Color.rgb(244, 117, 117))
                    setColor(Color.WHITE)
                    setFillColor(Color.rgb(104, 241, 175))
                    setFillAlpha(100)
                    setDrawHorizontalHighlightIndicator(false)
                    setFillFormatter { dataSet, dataProvider ->
                        view.batteryLevelChart.getAxisLeft().getAxisMinimum()
                    }

                }
            )
    }

    fun updateBatteryView(view: View) {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context?.registerReceiver(null, ifilter)
        }
        val batteryPct: Float = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level / scale.toFloat()
        } ?: 0.0f
        val batteryLevel = (batteryPct * 100).toInt()
        view.batteryLevelChart.data.addEntry(Entry((Date().time - firstTimestamp).toFloat(), batteryLevel.toFloat()), 0)
        view.batteryLevelChart.data.notifyDataChanged()
        view.batteryLevelChart.notifyDataSetChanged()
        view.batteryLevelChart.invalidate()

        view.batteryMeterView.chargeLevel = batteryLevel
        view.batteryLevelText.text = "$batteryLevel%"
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        view.batteryMeterView.isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL

    }
}
