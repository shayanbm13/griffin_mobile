package com.example.griffinmobile.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.griffinmobile.mudels.Light
import com.example.griffinmobile.mudels.Thermostst

class Temperature_db private constructor(context: Context) : SQLiteOpenHelper(context, "Temperature_DB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_Temperature_DB_TABLE =
            "CREATE TABLE IF NOT EXISTS " + "Temperature_DB" + "(id INTEGER PRIMARY KEY,  room_name TEXT,  mood TEXT ,  temperature TEXT,  current_temperature TEXT ,  fan_status TEXT  ,  ip TEXT  ,  mac TEXT ,  on_off TEXT , subtype TEXT, name TEXT  )"





        db?.execSQL(CREATE_Temperature_DB_TABLE)
        Log.d("database message :", "Temperature_DB created")


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldversion: Int, newversion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Temperature_DB")
        onCreate(db)
    }
    fun updateTemperatureName(oldRoomName: String, newName: String) {
        val db = writableDatabase

        // بررسی وجود رکورد با room_name مشخص شده
        val cursor = db.query(
            "Temperature_DB",
            arrayOf("id"),
            "room_name = ?",
            arrayOf(oldRoomName),
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            // رکورد با room_name مشخص شده وجود دارد، انجام عملیات بروزرسانی
            val values = ContentValues().apply {
                put("room_name", newName)  // بروزرسانی name با newName
            }

            val rowsAffected = db.update(
                "Temperature_DB",
                values,
                "room_name = ?",
                arrayOf(oldRoomName)
            )

            if (rowsAffected > 0) {
                Log.d("database message:", "Temperature name updated successfully.")
            } else {
                Log.d("database message:", "Failed to update temperature name.")
            }
        } else {
            Log.d("database message:", "No record found with room_name: $oldRoomName")
        }

        cursor?.close()
        db.close()
    }
    fun removeDuplicates() {
        val db = this.writableDatabase

        // کوئری برای حذف رکوردهای تکراری
        val query = """
            DELETE FROM Temperature_DB
            WHERE id NOT IN (
                SELECT MIN(id)
                FROM Temperature_DB
                GROUP BY mac, subtype
            )
        """

        try {
            db.execSQL(query)
            Log.d("database message:", "Duplicate entries removed based on mac and subtype in Temperature_DB")
        } catch (e: Exception) {
            Log.e("database error:", "Error removing duplicates in Temperature_DB: ${e.message}")
        } finally {
            db.close()
        }
    }
    private val thermoststLiveData: MutableLiveData<Thermostst?> = MutableLiveData()
//    private val roomNamesLiveData = MutableLiveData<List<String>>()


    fun set_to_db_Temprature(Thermostst: Thermostst) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put("id", Thermostst.id)
        values.put("room_name", Thermostst.room_name)
        values.put("mood", Thermostst.mood)
        values.put("temperature", Thermostst.temperature)
        values.put("current_temperature", Thermostst.current_temperature)
        values.put("fan_status", Thermostst.fan_status)
        values.put("ip", Thermostst.ip)
        values.put("mac", Thermostst.mac)
        values.put("on_off", Thermostst.on_off)
        values.put("subtype", Thermostst.subtype)
        values.put("name", Thermostst.name)


        db.insert("Temperature_DB", null, values)
        db.close()
        Log.d("database message :", "Temperature_DB updated")
        thermoststLiveData.postValue(Thermostst)

    }


    fun delete_from_db_Temprature(id: Int?): Int {
        val db: SQLiteDatabase = writableDatabase
        return db.delete("Temperature_DB", "id=?", arrayOf(id.toString()))
    }
    fun getThermostatByName(name: String?): Thermostst? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "name = ?"
        val selectionArgs = arrayOf(name)
        val cursor = db.query("Temperature_DB", null, selection, selectionArgs, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            val thermostat = Thermostst()
            thermostat.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            thermostat.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            thermostat.mood = cursor.getStringOrNull(cursor.getColumnIndex("mood"))
            thermostat.temperature = cursor.getStringOrNull(cursor.getColumnIndex("temperature"))
            thermostat.current_temperature = cursor.getStringOrNull(cursor.getColumnIndex("current_temperature"))
            thermostat.fan_status = cursor.getStringOrNull(cursor.getColumnIndex("fan_status"))
            thermostat.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            thermostat.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            thermostat.on_off = cursor.getStringOrNull(cursor.getColumnIndex("on_off"))
            thermostat.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
            thermostat.name = cursor.getStringOrNull(cursor.getColumnIndex("name"))

            return thermostat
        }

        return null
    }

    fun get_from_db_Temprature(id: Int?): Thermostst? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "Temperature_DB",
            arrayOf("id", "room_name", "mood", "temperature", "current_temperature","mac", "fan_status","ip","subtype","on_off","name"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val Thermostst = Thermostst()
            Thermostst.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            Thermostst.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            Thermostst.mood = cursor.getStringOrNull(cursor.getColumnIndex("mood"))
            Thermostst.temperature = cursor.getStringOrNull(cursor.getColumnIndex("temperature"))
            Thermostst.current_temperature = cursor.getStringOrNull(cursor.getColumnIndex("current_temperature"))
            Thermostst.fan_status = cursor.getStringOrNull(cursor.getColumnIndex("fan_status"))
            Thermostst.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            Thermostst.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            Thermostst.on_off = cursor.getStringOrNull(cursor.getColumnIndex("on_off"))
            Thermostst.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
            Thermostst.name = cursor.getStringOrNull(cursor.getColumnIndex("name"))

            thermoststLiveData.postValue(Thermostst)
            return Thermostst
        }
        return null
    }

    fun getRoomByRoomNamePrefix(prefix: String): Thermostst? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_name LIKE ?"
        val selectionArgs = arrayOf("$prefix%")
        val cursor = db.query("Temperature_DB", null, selection, selectionArgs, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            val thermostst = Thermostst()
            thermostst.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            thermostst.mood = cursor.getStringOrNull(cursor.getColumnIndex("mood"))
            thermostst.temperature = cursor.getStringOrNull(cursor.getColumnIndex("temperature"))
            thermostst.fan_status = cursor.getStringOrNull(cursor.getColumnIndex("fan_status"))
            thermostst.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            thermostst.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            thermostst.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            thermostst.on_off = cursor.getStringOrNull(cursor.getColumnIndex("on_off"))
            thermostst.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
            thermostst.name = cursor.getStringOrNull(cursor.getColumnIndex("name"))
            return thermostst
        }

        return null
    }

    fun getAllThermostats(): List<Thermostst> {
        val db: SQLiteDatabase = readableDatabase
        val thermostatsList = mutableListOf<Thermostst>()

        val cursor = db.query(
            "Temperature_DB",
            null,
            null,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            while (cursor.moveToNext()) {
                val thermostat = Thermostst()
                thermostat.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
                thermostat.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
                thermostat.mood = cursor.getStringOrNull(cursor.getColumnIndex("mood"))
                thermostat.temperature = cursor.getStringOrNull(cursor.getColumnIndex("temperature"))
                thermostat.current_temperature = cursor.getStringOrNull(cursor.getColumnIndex("current_temperature"))
                thermostat.fan_status = cursor.getStringOrNull(cursor.getColumnIndex("fan_status"))
                thermostat.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
                thermostat.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
                thermostat.on_off = cursor.getStringOrNull(cursor.getColumnIndex("on_off"))
                thermostat.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
                thermostat.name = cursor.getStringOrNull(cursor.getColumnIndex("name"))

                thermostatsList.add(thermostat)
            }
        }

        return thermostatsList
    }




    fun getThermostatsByRoomName(roomName: String?): List<Thermostst> {
        val db: SQLiteDatabase = readableDatabase
        val thermostatsList = mutableListOf<Thermostst>()

        val selection = "room_name = ?"
        val selectionArgs = arrayOf(roomName)
        val cursor = db.query("Temperature_DB", null, selection, selectionArgs, null, null, null)

        cursor?.use {
            while (cursor.moveToNext()) {
                val thermostat = Thermostst()
                val id = it.getIntOrNull(it.getColumnIndex("id"))

                thermostat.id =
                    cursor.getIntOrNull(it.getColumnIndex("id"))
                thermostat.room_name =
                    cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
                thermostat.mood = cursor.getStringOrNull(cursor.getColumnIndex("mood"))
                thermostat.temperature =
                    cursor.getStringOrNull(cursor.getColumnIndex("temperature"))
                thermostat.current_temperature =
                    cursor.getStringOrNull(cursor.getColumnIndex("current_temperature"))
                thermostat.fan_status =
                    cursor.getStringOrNull(cursor.getColumnIndex("fan_status"))
                thermostat.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
                thermostat.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
                thermostat.on_off = cursor.getStringOrNull(cursor.getColumnIndex("on_off"))
                thermostat.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
                thermostat.name = cursor.getStringOrNull(cursor.getColumnIndex("name"))

                thermostatsList.add(thermostat)
            }
        }
        db.close()
        return thermostatsList
    }





    fun updatehermostatById(id: Int?, newTemperature: String?, current_temperature: String?,mood:String?,fan_status:String?,on_off:String?): Int {
        val db: SQLiteDatabase = writableDatabase
        try {
            val value = ContentValues()
            value.put("temperature", newTemperature)
            value.put("current_temperature", current_temperature)
            value.put("mood", mood)
            value.put("fan_status", fan_status)
            value.put("on_off", on_off)

            return db.update("Temperature_DB", value, "id=?", arrayOf(id.toString()))
        } catch (e: Exception) {
            println(e)
            return 0
        } finally {
            db.close()
        }
    }

    fun updatefullThermostatById(id: Int?, thermostat: Thermostst): Int {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put("room_name", thermostat.room_name)
        values.put("mood", thermostat.mood)
        values.put("temperature", thermostat.temperature)
        values.put("fan_status", thermostat.fan_status)
        values.put("ip", thermostat.ip)
        values.put("mac", thermostat.mac)
        values.put("on_off", thermostat.on_off)
        values.put("subtype", thermostat.subtype)
        values.put("name", thermostat.name)


        return db.update("Temperature_DB", values, "id=?", arrayOf(id.toString()))
    }


    fun deleteRowsWithNullOrEmptySubTtype() {
        val db: SQLiteDatabase = writableDatabase
        val whereClause = "subtype IS NULL OR subtype = ?"
        val whereArgs = arrayOf("")

        db.delete("Temperature_DB", whereClause, whereArgs)
        db.close()
    }


    fun updateNameById(id: Int?, name: String?): Int {
        val db: SQLiteDatabase = writableDatabase
        try {
            val value = ContentValues()
            value.put("name", name)


            return db.update("Temperature_DB", value, "id=?", arrayOf(id.toString()))
        } catch (e: Exception) {
            println(e)
            return 0
        } finally {
            db.close()
        }
    }
    fun updatehermostatAndIpById(id: Int?, newTemperature: String?, current_temperature: String?,mood:String?,fan_status:String?,on_off:String?,ip:String?): Int {
        val db: SQLiteDatabase = writableDatabase
        try {
            val value = ContentValues()
            value.put("temperature", newTemperature)
            value.put("current_temperature", current_temperature)
            value.put("mood", mood)
            value.put("fan_status", fan_status)
            value.put("on_off", on_off)
            value.put("ip", ip)

            return db.update("Temperature_DB", value, "id=?", arrayOf(id.toString()))
        } catch (e: Exception) {
            println(e)
            return 0
        } finally {
            db.close()
        }
    }

    fun getThermostatsWithNonEmptyMacByRoomName(roomName: String?): LiveData<List<Thermostst?>> {
        val thermostatListLiveData = MutableLiveData<List<Thermostst?>>()

        val db: SQLiteDatabase = readableDatabase
        val selection = "mac IS NOT NULL AND mac != '' AND room_name LIKE ?"
        val selectionArgs = arrayOf("$roomName%")
        val cursor = db.query(
            "Temperature_DB",
            arrayOf("id", "room_name", "mood", "temperature", "current_temperature", "fan_status", "ip", "mac", "on_off", "subtype", "name"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val thermostatList = mutableListOf<Thermostst?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val mood = it.getStringOrNull(it.getColumnIndex("mood"))
                val temperature = it.getStringOrNull(it.getColumnIndex("temperature"))
                val currentTemperature = it.getStringOrNull(it.getColumnIndex("current_temperature"))
                val fanStatus = it.getStringOrNull(it.getColumnIndex("fan_status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val onOff = it.getStringOrNull(it.getColumnIndex("on_off"))
                val subtype = it.getStringOrNull(it.getColumnIndex("subtype"))
                val name = it.getStringOrNull(it.getColumnIndex("name"))

                val thermostat = Thermostst()
                thermostat.id = id
                thermostat.room_name = roomName
                thermostat.mood = mood
                thermostat.temperature = temperature
                thermostat.current_temperature = currentTemperature
                thermostat.fan_status = fanStatus
                thermostat.ip = ip
                thermostat.mac = mac
                thermostat.on_off = onOff
                thermostat.subtype = subtype
                thermostat.name = name

                thermostatList.add(thermostat)
            }
        }

        thermostatListLiveData.postValue(thermostatList)
        return thermostatListLiveData
    }




    fun clear_db_Temprature() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("Temperature_DB", null, null)
        db.close()
        Log.d("database message :", "Temperature_DB cleared")
    }


    fun checkRoomNameExists(roomName: String): Boolean {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_name = ?"
        val selectionArgs = arrayOf(roomName)
        val cursor = db.query("Temperature_DB", null, selection, selectionArgs, null, null, null)
        val roomNameExists = cursor.count > 0
        cursor.close()
        return roomNameExists
    }
    fun getThermoststsBySameMacForPnames(Pnames: List<String>): List<List<Thermostst>> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "name IN (${Pnames.joinToString { "?" }})"
        val selectionArgs = Pnames.toTypedArray()
        val cursor = db.query(
            "Temperature_DB",
            arrayOf("id", "room_name", "mood", "temperature", "current_temperature", "fan_status", "ip", "mac", "on_off", "subtype", "name"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val ThermoststList = mutableListOf<Thermostst>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val mood = it.getStringOrNull(it.getColumnIndex("mood"))
                val temperature = it.getStringOrNull(it.getColumnIndex("temperature"))
                val roomName = it.getStringOrNull(it.getColumnIndex("roomName"))
                val currentTemperature = it.getStringOrNull(it.getColumnIndex("current_temperature"))
                val fanStatus = it.getStringOrNull(it.getColumnIndex("fan_status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val onOff = it.getStringOrNull(it.getColumnIndex("on_off"))
                val subtype = it.getStringOrNull(it.getColumnIndex("subtype"))
                val name = it.getStringOrNull(it.getColumnIndex("name"))

                val thermostat = Thermostst()
                thermostat.id = id
                thermostat.room_name = roomName
                thermostat.mood = mood
                thermostat.temperature = temperature
                thermostat.current_temperature = currentTemperature
                thermostat.fan_status = fanStatus
                thermostat.ip = ip
                thermostat.mac = mac
                thermostat.on_off = onOff
                thermostat.subtype = subtype
                thermostat.name = name

                ThermoststList.add(thermostat)
            }
        }

        // گروه‌بندی لامپ‌ها بر اساس Mac های مشابه
        val groupedLights = mutableMapOf<String, MutableList<Thermostst>>()

        for (Thermostst in ThermoststList) {
            val mac = Thermostst.mac ?: continue // تایید کردن اینکه Mac خالی نباشد
            if (!groupedLights.containsKey(mac)) {
                groupedLights[mac] = mutableListOf()
            }
            groupedLights[mac]?.add(Thermostst)
        }

        // تبدیل Map به لیست از لیست‌ها
        return groupedLights.values.toList()
    }





    fun get_from_db_Temprature_livedata(id: Int) {
        val db: SQLiteDatabase = readableDatabase
        val cursor = db.query(
            "Temperature_DB",
            arrayOf("id", "room_name", "mood", "temperature", "fan_status","ip","mac","subtype","on_off"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val Thermostst = Thermostst()
            Thermostst.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            Thermostst.mood = cursor.getStringOrNull(cursor.getColumnIndex("mood"))
            Thermostst.temperature = cursor.getStringOrNull(cursor.getColumnIndex("temperature"))
            Thermostst.fan_status = cursor.getStringOrNull(cursor.getColumnIndex("fan_status"))
            Thermostst.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            Thermostst.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            Thermostst.on_off = cursor.getStringOrNull(cursor.getColumnIndex("on_off"))
            Thermostst.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))


            thermoststLiveData.postValue(Thermostst)
        }
    }


    fun getThermostatsByMac(mac: String): List<Thermostst> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "mac = ?"
        val selectionArgs = arrayOf(mac)
        val cursor = db.query(
            "Temperature_DB",
            arrayOf("id", "room_name", "mood", "temperature", "current_temperature", "fan_status", "ip", "mac", "on_off", "subtype", "name"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val thermostatList = mutableListOf<Thermostst>()

        cursor?.use {
            while (it.moveToNext()) {
                val thermostat = Thermostst()
                thermostat.id = it.getIntOrNull(it.getColumnIndex("id"))
                thermostat.room_name = it.getStringOrNull(it.getColumnIndex("room_name"))
                thermostat.mood = it.getStringOrNull(it.getColumnIndex("mood"))
                thermostat.temperature = it.getStringOrNull(it.getColumnIndex("temperature"))
                thermostat.current_temperature = it.getStringOrNull(it.getColumnIndex("current_temperature"))
                thermostat.fan_status = it.getStringOrNull(it.getColumnIndex("fan_status"))
                thermostat.ip = it.getStringOrNull(it.getColumnIndex("ip"))
                thermostat.mac = it.getStringOrNull(it.getColumnIndex("mac"))
                thermostat.on_off = it.getStringOrNull(it.getColumnIndex("on_off"))
                thermostat.subtype = it.getStringOrNull(it.getColumnIndex("subtype"))
                thermostat.name = it.getStringOrNull(it.getColumnIndex("name"))

                thermostatList.add(thermostat)
            }
        }

        return thermostatList
    }

    fun getNumberOfItemsByLname(Lname: String): Int {
        val db: SQLiteDatabase = readableDatabase
        val selection = "name = ?"
        val selectionArgs = arrayOf(Lname)
        val cursor = db.query(
            "Temperature_DB",
            arrayOf("COUNT(*)"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var count = 0

        cursor?.use {
            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }

        return count
    }



    companion object {
        private var instance: Temperature_db? = null

        @Synchronized
        fun getInstance(context: Context): Temperature_db {
            if (instance == null) {
                instance = Temperature_db(context.applicationContext)
            }
            return instance!!
        }
    }







}