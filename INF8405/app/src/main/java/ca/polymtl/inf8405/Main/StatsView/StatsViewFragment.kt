package ca.polymtl.inf8405.Main.StatsView


import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.polymtl.inf8405.Domain.BatteryLevel
import ca.polymtl.inf8405.R
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_stats_view.view.*
import java.util.*

class StatsViewFragment : Fragment() {

    val batteryLevels = mutableListOf<BatteryLevel>()

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
        batteryLevels.add(BatteryLevel(batteryLevel, Date().time))
        view.batteryLevelChart.xAxis.axisMinimum = batteryLevels.first().timestamp.toFloat()
        view.batteryLevelChart.xAxis.axisMaximum = batteryLevels.last().timestamp.toFloat()
        view.batteryLevelChart.data =
            LineData(
                LineDataSet(
                    batteryLevels.map { Entry(it.timestamp.toFloat(), it.level.toFloat()) },
                    "Battery Level"
                ).apply {
                    axisDependency = YAxis.AxisDependency.LEFT
                }
            )
        view.batteryLevelChart.invalidate()

        view.batteryMeterView.chargeLevel = batteryLevel
        view.batteryLevelText.text = "$batteryLevel%"
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        view.batteryMeterView.isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL

    }
}
