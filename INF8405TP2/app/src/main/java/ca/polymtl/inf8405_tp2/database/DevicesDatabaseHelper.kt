package ca.polymtl.inf8405_tp2.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class DevicesDatabaseHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "Devices") {
    companion object {
        private var instance: DevicesDatabaseHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): DevicesDatabaseHelper {
            if (instance == null) {
                instance = DevicesDatabaseHelper(ctx.getApplicationContext())
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("devices", true,
            "id" to INTEGER + PRIMARY_KEY + UNIQUE,
            "latitude" to REAL,
            "longitude" to REAL,
            "name" to TEXT,
            "address" to TEXT,
            "deviceClass" to TEXT,
            "type" to TEXT,
            "starred" to INTEGER
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable("devices", true)

        onCreate(db)
    }
}

val Context.database: DevicesDatabaseHelper
    get() = DevicesDatabaseHelper.getInstance(getApplicationContext())