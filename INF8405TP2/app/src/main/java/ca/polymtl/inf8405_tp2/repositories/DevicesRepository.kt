package ca.polymtl.inf8405_tp2.repositories

import android.content.ContentValues
import android.content.Context
import ca.polymtl.inf8405_tp2.database.database
import ca.polymtl.inf8405_tp2.domain.Device
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.select

class DevicesRepository(val context: Context) {
    fun findAll(): List<Device> = context.database.use {
        select("devices")
            .parseList(object : MapRowParser<Device> {
                override fun parseRow(columns: Map<String, Any?>): Device {
                    println(columns["starred"].toString().toInt())
                    return Device(
                        columns["latitude"].toString().toDouble(),
                        columns["longitude"].toString().toDouble(),
                        columns["name"].toString(),
                        columns["address"].toString(),
                        columns["deviceClass"].toString(),
                        columns["type"].toString(),
                        columns["starred"].toString().toInt() > 0
                    )
                }
            })
    }

    fun create(device: Device) = context.database.use {
        val values = ContentValues()
        values.put("latitude", device.latitude)
        values.put("longitude", device.longitude)
        values.put("name", device.name)
        values.put("address", device.address)
        values.put("deviceClass", device.deviceClass)
        values.put("type", device.type)
        values.put("starred", if (device.starred) 1 else 0)
        insert("devices", null, values)
    }

    fun setStarred(device: Device) = context.database.use {
        val values = ContentValues()
        values.put("starred", 1)
        update("devices", values, "address = ?", arrayOf(device.address))
    }
}