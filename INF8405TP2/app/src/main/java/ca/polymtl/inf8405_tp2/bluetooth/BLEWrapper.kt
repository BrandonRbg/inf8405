package ca.polymtl.inf8405_tp2.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager.*


class BLEWrapper(context: Context, val callback: (device: BluetoothDevice) -> Unit) {

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            print(intent)
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> handleActionFound(intent)
            }
        }
    }

    private val bluetoothAdapter: BluetoothAdapter

    init {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!bluetoothAdapter.isEnabled) {
            bluetoothAdapter.enable()
        }
        context.registerReceiver(broadcastReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
    }

    private fun handleActionFound(intent: Intent) {
        val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        callback(device)
        print(device)
    }

    fun scan(context: Context) {
        bluetoothAdapter.startDiscovery()
    }

    fun dispose(context: Context) {
        context.unregisterReceiver(broadcastReceiver)
    }
}
