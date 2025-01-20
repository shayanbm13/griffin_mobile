package com.example.griffinmobile.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.database.getStringOrNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.griffinmobile.mudels.network_manual
import com.example.griffinmobile.mudels.simcard_security

class setting_network_db private constructor(context: Context) : SQLiteOpenHelper(context, "SETTING_NETWORK_DB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_NETWORK_DB_TABLE = "CREATE TABLE IF NOT EXISTS " + "SETTING_NETWORK_DB" + "(id INTEGER PRIMARY KEY, modem_ssid TEXT , modem_password TEXT , api_key TEXT , city_name TEXT, homes TEXT )"





        db?.execSQL(CREATE_NETWORK_DB_TABLE)
        Log.d("database message :", "SETTING_NETWORK_DB created")




    }

    override fun onUpgrade(db: SQLiteDatabase?, oldversion: Int, newversion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS SETTING_NETWORK_DB")
        onCreate(db)
    }

    private val networkManualLiveData: MutableLiveData<network_manual?> = MutableLiveData()




//####  network database  #########################################################################################

    fun set_to_db_network_manual(networkManual: network_manual) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put("modem_ssid", networkManual.modem_ssid)
        values.put("modem_password", networkManual.modem_password)
        values.put("api_key", networkManual.api_key)
        values.put("city_name", networkManual.city_name)
        values.put("homes", networkManual.homes)

        db.insert("SETTING_NETWORK_DB", null, values)
        db.close()
        Log.d("database message :", "SETTING_NETWORK_DB updated")
        networkManualLiveData.postValue(networkManual)

    }

    fun get_from_db_network_manual(id: Int): network_manual? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "SETTING_NETWORK_DB",
            arrayOf("id", "modem_ssid", "modem_password", "api_key", "city_name", "homes"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val network_manual = network_manual()
            network_manual.modem_ssid = cursor.getStringOrNull(cursor.getColumnIndex("modem_ssid"))
            network_manual.modem_password = cursor.getStringOrNull(cursor.getColumnIndex("modem_password"))
            network_manual.api_key = cursor.getStringOrNull(cursor.getColumnIndex("api_key"))
            network_manual.city_name = cursor.getStringOrNull(cursor.getColumnIndex("city_name"))
            network_manual.homes = cursor.getStringOrNull(cursor.getColumnIndex("homes"))
            networkManualLiveData.postValue(network_manual)
            return network_manual
        }
        return null
    }
    fun isDatabaseEmpty(): Boolean {
        val db: SQLiteDatabase = readableDatabase
        val countQuery = "SELECT COUNT(*) FROM SETTING_NETWORK_DB"
        val cursor: Cursor = db.rawQuery(countQuery, null)
        var isEmpty = true
        if (cursor.moveToFirst()) {
            val count = cursor.getInt(0)
            isEmpty = count == 0
        }
        cursor.close()
        db.close()
        return isEmpty
    }
    fun update_db_network_manual(networkManual: network_manual): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("modem_ssid", networkManual.modem_ssid)
        value.put("modem_password", networkManual.modem_password)
        value.put("api_key", networkManual.api_key)
        value.put("city_name", networkManual.city_name)
        value.put("homes", networkManual.homes)
        networkManualLiveData.postValue(networkManual)

            return db.update("SETTING_NETWORK_DB", value, "id=?", arrayOf(networkManual.id.toString()))
    }
    fun searchByHomes(homesValue: String): List<network_manual> {
        val db: SQLiteDatabase = readableDatabase
        val resultList = mutableListOf<network_manual>()

        val cursor = db.query(
            "SETTING_NETWORK_DB",
            arrayOf("id", "modem_ssid", "modem_password", "api_key", "city_name", "homes"),
            "homes = ?",
            arrayOf(homesValue),
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val networkManual = network_manual().apply {
                    modem_ssid = cursor.getStringOrNull(cursor.getColumnIndex("modem_ssid"))
                    modem_password = cursor.getStringOrNull(cursor.getColumnIndex("modem_password"))
                    api_key = cursor.getStringOrNull(cursor.getColumnIndex("api_key"))
                    city_name = cursor.getStringOrNull(cursor.getColumnIndex("city_name"))
                    homes = cursor.getStringOrNull(cursor.getColumnIndex("homes"))
                }
                resultList.add(networkManual)
            } while (cursor.moveToNext())
        }
        cursor?.close()
        db.close()

        return resultList
    }
    fun clearNetworkTable() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("SETTING_NETWORK_DB", null, null)
        db.close()
        Log.d("database message :", "SETTING_NETWORK_DB cleared")
    }

    fun isEmptynetwork_tabale() :Boolean{

        val db:SQLiteDatabase=readableDatabase
        val countQuery="SELECT * FROM SETTING_NETWORK_DB"
        val cursor:Cursor=db.rawQuery(countQuery,null)
        if (cursor.count==0){
            return true
        }else{
            return false
        }

    }




    fun getNetworkManualLiveData(): LiveData<network_manual?> {
        return networkManualLiveData
    }


    fun deleteById(id: Int): Boolean {
        val db: SQLiteDatabase = writableDatabase
        val rowsDeleted = db.delete("SETTING_NETWORK_DB", "id = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted > 0
    }
    fun get_from_db_network_manual_livedata(id: Int) {
        val db: SQLiteDatabase = readableDatabase
        val cursor = db.query(
            "SETTING_NETWORK_DB",
            arrayOf("id", "modem_ssid", "modem_password", "api_key", "city_name", "homes"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val network_manual = network_manual()
            network_manual.modem_ssid = cursor.getStringOrNull(cursor.getColumnIndex("modem_ssid"))
            network_manual.modem_password = cursor.getStringOrNull(cursor.getColumnIndex("modem_password"))
            network_manual.api_key = cursor.getStringOrNull(cursor.getColumnIndex("api_key"))
            network_manual.city_name = cursor.getStringOrNull(cursor.getColumnIndex("city_name"))
            network_manual.homes = cursor.getStringOrNull(cursor.getColumnIndex("homes"))

            // مقدار networkManualLiveData را به‌روز کنید
            networkManualLiveData.postValue(network_manual)
        }
    }









        companion object {
        private var instance: setting_network_db? = null

        @Synchronized
        fun getInstance(context: Context): setting_network_db {
            if (instance == null) {
                instance = setting_network_db(context.applicationContext)
            }
            return instance!!
        }
    }
}
