package ca.polymtl.inf8405.Main.StatsView


import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ca.polymtl.inf8405.R
import kotlinx.android.synthetic.main.fragment_stats_view.view.*
import java.util.*

class StatsViewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stats_view, container, false)
        updateBatteryView(view)
        Timer().scheduleAtFixedRate(object: TimerTask(){
            override fun run() {
                updateBatteryView(view)
            }
        }, 0, 1000)
        return view
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
        var batteryLevel = (batteryPct * 100).toInt()
        view.batteryMeterView.chargeLevel = batteryLevel
        view.batteryLevelText.text = "$batteryLevel%"
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        view.batteryMeterView.isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL

    }


}
