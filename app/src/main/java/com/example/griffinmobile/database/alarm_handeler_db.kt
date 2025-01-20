package com.example.griffinmobile.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.lifecycle.MutableLiveData
import com.example.griffinmobile.mudels.alarm

class alarm_handeler_db private constructor(context: Context) : SQLiteOpenHelper(context, "alarm_DB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_alarm_DB_TABLE =
            "CREATE TABLE IF NOT EXISTS " + "alarm_DB" + "(id INTEGER PRIMARY KEY,  next_status TEXT  ,  alarm_name TEXT,  device_name TEXT  ,  alarm_day TEXT,  alarm_tyme TEXT,  grooup TEXT  )"





        db?.execSQL(CREATE_alarm_DB_TABLE)
        Log.d("database message :", "alarm_DB created")


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldversion: Int, newversion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS alarm_DB")
        onCreate(db)
    }

    private val alarmLiveData: MutableLiveData<alarm?> = MutableLiveData()
//    private val roomNamesLiveData = MutableLiveData<List<String>>()


    fun set_to_db_alarm(alarm: alarm) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()

        values.put("next_status", alarm.next_status)

        values.put("alarm_name", alarm.alarm_name)
        values.put("device_name", alarm.device_name)
        values.put("alarm_day", alarm.alarm_day)
        values.put("alarm_tyme", alarm.alarm_tyme)
        values.put("grooup", alarm.grooup)

        db.insert("alarm_DB", null, values)
        db.close()
        Log.d("database message :", "alarm_DB updated")
        alarmLiveData.postValue(alarm)

    }

    fun clear_db_alarm() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("alarm_DB", null, null)
        db.close()
        Log.d("database message :", "SCENARIO_DB cleared")
    }
    fun updateNameById(id: Int?, newName: String) {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("device_name", newName)

        db.update("alarm_DB", value, "id=?", arrayOf(id.toString()))
        db.close()
    }

    fun updatenext_statusById(id: Int?, newnext_status: String?) {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        try {
            value.put("next_status", newnext_status)
            db.update("alarm_DB", value, "id=?", arrayOf(id.toString()))

        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }


//    fun getAlarmsGroupedByDeviceAndTime(deviceName: String?): List<List<alarm>> {
//        val db: SQLiteDatabase = readableDatabase
//        val cursor = db.query(
//            "alarm_DB",
//            arrayOf("id", "next_status", "alarm_name", "device_name", "alarm_day", "alarm_tyme", "grooup"),
//            "device_name=?",
//            arrayOf(deviceName),
//            null,
//            null,
//            null
//        )
//
//        val alarmGroups = mutableMapOf<String, MutableList<alarm>>()
//
//        cursor?.use {
//            while (it.moveToNext()) {
//                val alarmTime = it.getStringOrNull(it.getColumnIndex("alarm_tyme"))
//                val id = it.getIntOrNull(it.getColumnIndex("id"))
//                val next_status = it.getStringOrNull(it.getColumnIndex("next_status"))
//                val alarm_name = it.getStringOrNull(it.getColumnIndex("alarm_name"))
//                val device_name = it.getStringOrNull(it.getColumnIndex("device_name"))
//                val alarm_day = it.getStringOrNull(it.getColumnIndex("alarm_day"))
//                val alarm_tyme = it.getStringOrNull(it.getColumnIndex("alarm_tyme"))
//                val grooup = it.getStringOrNull(it.getColumnIndex("grooup"))
//
//                val alarm = alarm(
//                    next_status ?: "",
//                    id,
//                    alarm_name ?: "",
//                    device_name ?: "",
//                    alarm_day ?: "",
//                    alarm_tyme ?: "",
//                    grooup ?: ""
//                )
//
//                if (alarmTime != null) {
//                    val key = "$deviceName-$alarmTime"
//                    if (!alarmGroups.containsKey(key)) {
//                        alarmGroups[key] = mutableListOf()
//                    }
//                    alarmGroups[key]?.add(alarm)
//                }
//            }
//        }
//
//        return alarmGroups.values.toList()
//    }

    fun getAlarmsGroupedByDeviceName(deviceName: String?): List<List<alarm>> {
        val db: SQLiteDatabase = readableDatabase
        val cursor = db.query(
            "alarm_DB",
            arrayOf("id", "next_status", "alarm_name", "device_name", "alarm_day", "alarm_tyme", "grooup"),
            "device_name=?",
            arrayOf(deviceName),
            null,
            null,
            null
        )

        val alarmGroups = mutableMapOf<String, MutableList<alarm>>()

        cursor?.use {
            while (it.moveToNext()) {
                val grooup = it.getStringOrNull(it.getColumnIndex("grooup"))
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val next_status = it.getStringOrNull(it.getColumnIndex("next_status"))
                val alarm_name = it.getStringOrNull(it.getColumnIndex("alarm_name"))
                val device_name = it.getStringOrNull(it.getColumnIndex("device_name"))
                val alarm_day = it.getStringOrNull(it.getColumnIndex("alarm_day"))
                val alarm_tyme = it.getStringOrNull(it.getColumnIndex("alarm_tyme"))

                val alarm = alarm(
                    next_status ?: "",
                    id,
                    alarm_name ?: "",
                    device_name ?: "",
                    alarm_day ?: "",
                    alarm_tyme ?: "",
                    grooup ?: ""
                )

                if (grooup != null) {
                    if (!alarmGroups.containsKey(grooup)) {
                        alarmGroups[grooup] = mutableListOf()
                    }
                    alarmGroups[grooup]?.add(alarm)
                }
            }
        }

        return alarmGroups.values.toList()
    }




    fun delete_from_db_alarm(id: Int?): Int? {
        try {
            val db: SQLiteDatabase = writableDatabase
            return db.delete("alarm_DB", "id=?", arrayOf(id.toString()))
        }catch (e:Exception){
            println(e)
            return null
        }

    }

    fun get_from_db_alarm(name: String?): alarm? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "alarm_DB",
            arrayOf("id", "next_status", "alarm_name", "device_name", "alarm_day", "alarm_tyme", "grooup"),
            "alarm_name=?",
            arrayOf(name.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val alarm = alarm()
            alarm.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))

            alarm.next_status = cursor.getStringOrNull(cursor.getColumnIndex("next_status"))

            alarm.alarm_name = cursor.getStringOrNull(cursor.getColumnIndex("alarm_name"))
            alarm.device_name = cursor.getStringOrNull(cursor.getColumnIndex("device_name"))
            alarm.alarm_day = cursor.getStringOrNull(cursor.getColumnIndex("alarm_day"))
            alarm.alarm_tyme = cursor.getStringOrNull(cursor.getColumnIndex("alarm_tyme"))
            alarm.grooup = cursor.getStringOrNull(cursor.getColumnIndex("grooup"))

            alarmLiveData.postValue(alarm)
            return alarm
        }
        return null
    }

    fun getAlarmsWithSameDeviceName(name: String?): List<alarm> {
        val db: SQLiteDatabase = readableDatabase
        val cursor = db.query(
            "alarm_DB",
            arrayOf("id", "next_status", "alarm_name", "device_name", "alarm_day", "alarm_tyme", "grooup"),
            "device_name=?",
            arrayOf(name),
            null,
            null,
            null
        )

        val alarmList = mutableListOf<alarm>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val next_status = it.getStringOrNull(it.getColumnIndex("next_status"))
                val alarm_name = it.getStringOrNull(it.getColumnIndex("alarm_name"))
                val device_name = it.getStringOrNull(it.getColumnIndex("device_name"))
                val alarm_day = it.getStringOrNull(it.getColumnIndex("alarm_day"))
                val alarm_tyme = it.getStringOrNull(it.getColumnIndex("alarm_tyme"))
                val grooup = it.getStringOrNull(it.getColumnIndex("grooup"))

                val alarm = alarm(
                    next_status ?: "",
                    id,
                    alarm_name ?: "",
                    device_name ?: "",
                    alarm_day ?: "",
                    alarm_tyme ?: "",
                    grooup ?: ""
                )
                alarmList.add(alarm)
            }
        }

        return alarmList.toList()
    }


    fun get_from_db_alarm_by_deviceName(name: String?): alarm? {
        val db: SQLiteDatabase = writableDatabase
        try {
            val cursor = db.query(
                "alarm_DB",
                arrayOf("id", "next_status", "alarm_name", "device_name", "alarm_day", "alarm_tyme", "grooup"),
                "device_name=?",
                arrayOf(name.toString()),
                null,
                null,
                null
            )
            if (cursor != null) {
                cursor.moveToFirst()
                val alarm = alarm()
                alarm.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))

                alarm.next_status = cursor.getStringOrNull(cursor.getColumnIndex("next_status"))

                alarm.alarm_name = cursor.getStringOrNull(cursor.getColumnIndex("alarm_name"))
                alarm.device_name = cursor.getStringOrNull(cursor.getColumnIndex("device_name"))
                alarm.alarm_day = cursor.getStringOrNull(cursor.getColumnIndex("alarm_day"))
                alarm.alarm_tyme = cursor.getStringOrNull(cursor.getColumnIndex("alarm_tyme"))
                alarm.grooup = cursor.getStringOrNull(cursor.getColumnIndex("grooup"))

                alarmLiveData.postValue(alarm)
                return alarm
            }
        }catch (e:Exception){
            println(e)
        }

        return null
    }

    fun get_id_by_same_alarm_names(){


    }
    fun getAllalarms(): List<alarm?> {
        val db: SQLiteDatabase = readableDatabase
        val cursor = db.query(
            "alarm_DB",
            arrayOf("id", "next_status", "alarm_name", "device_name", "alarm_day", "alarm_tyme", "grooup"),
            null,
            null,
            null,
            null,
            null
        )

        val alarmList = mutableListOf<alarm?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))


                val next_status = it.getStringOrNull(it.getColumnIndex("next_status"))

                val alarm_name = it.getStringOrNull(it.getColumnIndex("alarm_name"))
                val device_name = it.getStringOrNull(it.getColumnIndex("device_name"))
                val alarm_day = it.getStringOrNull(it.getColumnIndex("alarm_day"))
                val alarm_tyme = it.getStringOrNull(it.getColumnIndex("alarm_tyme"))
                val grooup = it.getStringOrNull(it.getColumnIndex("grooup"))

                val alarm = alarm(
                    next_status ?: "",
                    id ,
                    alarm_name ?: "",
                    device_name ?: "",
                    alarm_day ?: "",
                    alarm_tyme ?: "",
                    grooup ?: ""

                )
                alarmList.add(alarm)
            }
        }

        return alarmList.toList()
    }




    companion object {
        private var instance: alarm_handeler_db? = null

        @Synchronized
        fun getInstance(context: Context): alarm_handeler_db {
            if (instance == null) {
                instance = alarm_handeler_db(context.applicationContext)
            }
            return instance!!
        }
    }



}