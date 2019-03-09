package ca.polymtl.inf8405_tp2

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.content.LocalBroadcastManager.*


class BLEWrapper(context: Context) {


    private val broadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> handleActionFound(intent)
            }
        }
    }

    private val bluetoothAdapter: BluetoothAdapter

    private val _devices = HashMap<String, BluetoothDevice>()
    val devices: HashMap<String, BluetoothDevice>
        get() {
            return _devices
        }

    init {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(!bluetoothAdapter.isEnabled) {
            bluetoothAdapter.enable()
        }
        getInstance(context).registerReceiver(broadcastReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
    }

    private fun handleActionFound(intent: Intent) {
        val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        if(device != null) {
            _devices.putIfAbsent(device.address, device)
        }
    }

    fun scan(context: Context) {
        bluetoothAdapter.startDiscovery()
    }

    fun dispose(context: Context) {
        getInstance(context).unregisterReceiver(broadcastReceiver)
    }
}
