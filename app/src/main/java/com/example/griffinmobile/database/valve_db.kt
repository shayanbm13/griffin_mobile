package com.example.griffinmobile.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.griffinmobile.mudels.Plug
import com.example.griffinmobile.mudels.valve


class valve_db private constructor(context: Context) : SQLiteOpenHelper(context,"valve_DB",null,1){
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_valve_DB="CREATE TABLE IF NOT EXISTS "+ "valve_DB"+"(id INTEGER PRIMARY KEY , room_name TEXT, status TEXT, subtype TEXT, ip TEXT, mac TEXT, Vname TEXT )"

        db!!.execSQL(CREATE_valve_DB)
        Log.d("database message :" ,"valve_db created")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS valve_db")
        onCreate(db)
    }
    fun updateValveName(oldRoomName: String, newName: String) {
        val db = writableDatabase

        // بررسی وجود رکورد با room_name مشخص شده
        val cursor = db.query(
            "valve_DB",
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
                put("room_name", newName)  // بروزرسانی Vname با newName
            }

            val rowsAffected = db.update(
                "valve_DB",
                values,
                "room_name = ?",
                arrayOf(oldRoomName)
            )

            if (rowsAffected > 0) {
                Log.d("database message:", "Valve name updated successfully.")
            } else {
                Log.d("database message:", "Failed to update valve name.")
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
            DELETE FROM valve_DB
            WHERE id NOT IN (
                SELECT MIN(id)
                FROM valve_DB
                GROUP BY mac, subtype
            )
        """

        try {
            db.execSQL(query)
            Log.d("database message:", "Duplicate entries removed based on mac and subtype in valve_DB")
        } catch (e: Exception) {
            Log.e("database error:", "Error removing duplicates in valve_DB: ${e.message}")
        } finally {
            db.close()
        }
    }

    fun set_to_db_valve(valve: valve){
        val db=writableDatabase
        val values= ContentValues()
        values.put("room_name",valve.room_name)
        values.put("status",valve.status)
        values.put("subtype",valve.subtype)
        values.put("ip",valve.ip)
        values.put("mac",valve.mac)
        values.put("Vname",valve.Vname)
        db.insert("valve_db",null,values)
        db.close()
        Log.d("database message :", "valve_db updated")

    }

    fun updateStatusbyId(id:Int?,newStatus:String){
        val db=writableDatabase
        val values= ContentValues()
        try {
            values.put("status",newStatus)
            db.update("valve_DB",values, "id=?", arrayOf(id.toString()))


        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }
    fun updatevalvebyId(id:Int?,ip:String){
        val db=writableDatabase
        val values= ContentValues()
        try {
            values.put("ip",ip)
            db.update("valve_DB",values, "id=?", arrayOf(id.toString()))


        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }
    fun updateStatusAndIpById(id: Int?,newStatus: String?,newIp:String?){
        val db=writableDatabase
        val values= ContentValues()
        try {
            values.put("status",newStatus)
            values.put("ip",newIp)
            db.update("valve_DB",values,"id=?", arrayOf(id.toString()))
        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }


    fun getvalveByCname(Vname: String?): valve? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "Vname = ?"
        val selectionArgs = arrayOf(Vname)
        val cursor = db.query(
            "valve_DB",
            arrayOf("id", "room_name", "status", "subtype", "ip", "mac", "Vname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val valve = valve()
            valve.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            valve.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            valve.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
            valve.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
            valve.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            valve.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            valve.Vname = cursor.getStringOrNull(cursor.getColumnIndex("Vname"))

            return valve
        }

        return null
    }


    fun getvalvesByMacAddress(mac: String?): List<valve?> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "mac = ?"
        val selectionArgs = arrayOf(mac)
        val cursor = db.query(
            "valve_DB",
            arrayOf("id", "room_name", "subtype", "status", "ip", "mac", "Vname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val valveList = mutableListOf<valve?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val roomName = it.getStringOrNull(it.getColumnIndex("room_name"))
                val subType = it.getStringOrNull(it.getColumnIndex("subtype"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val macAddress = it.getStringOrNull(it.getColumnIndex("mac"))
                val Vname = it.getStringOrNull(it.getColumnIndex("Vname"))

                val valve = valve(
                    roomName ?: "",
                    status ?: "",
                    subType ?: "",
                    ip ?: "",
                    macAddress ?: "",
                    Vname ?: "",
                    id
                )
                valveList.add(valve)
            }
        }

        return valveList
    }


    fun getvalvesWithNonEmptyMacByRoomName(roomName: String?): LiveData<List<valve?>> {
        val valveListLiveData = MutableLiveData<List<valve?>>()

        val db: SQLiteDatabase = readableDatabase
        val selection = "mac IS NOT NULL AND mac != '' AND room_name LIKE ?"
        val selectionArgs = arrayOf("$roomName%")
        val cursor = db.query(
            "valve_DB",
            arrayOf("id", "room_name", "subtype", "status", "ip", "mac", "Vname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val valveList = mutableListOf<valve?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val subType = it.getStringOrNull(it.getColumnIndex("subtype"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Vname = it.getStringOrNull(it.getColumnIndex("Vname"))

                val valve = valve(
                    roomName ?: "",
                    status ?: "",
                    subType ?: "",
                    ip ?: "",
                    mac ?: "",
                    Vname ?: "",
                    id
                )
                valveList.add(valve)
            }
        }

        valveListLiveData.postValue(valveList)
        return valveListLiveData
    }


    fun getvalvesWithNonEmptyMacByRoomName2(roomName: String?): List<List<valve?>> {
        val valveListLiveData = mutableListOf<List<valve?>>()

        val db: SQLiteDatabase = readableDatabase
        val selection = "mac IS NOT NULL AND mac != '' AND room_name LIKE ?"
        val selectionArgs = arrayOf("$roomName%")
        val cursor = db.query(
            "valve_DB",
            arrayOf("id", "room_name", "subtype", "status", "ip", "mac", "Vname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val valveList = mutableListOf<valve?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val subType = it.getStringOrNull(it.getColumnIndex("subtype"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Vname = it.getStringOrNull(it.getColumnIndex("Vname"))

                val valve = valve(
                    roomName ?: "",
                    status ?: "",
                    subType ?: "",
                    ip ?: "",
                    mac ?: "",
                    Vname ?: "",
                    id
                )
                valveList.add(valve)
            }
        }

        valveListLiveData.add(valveList)
        return valveListLiveData
    }



    fun get_from_db_valve(id: Int?): valve? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "valve_DB",
            arrayOf("id", "room_name", "status", "subtype", "ip", "mac", "Vname"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val valve = valve()
            valve.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            valve.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            valve.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            valve.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            valve.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
            valve.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
            valve.Vname = cursor.getStringOrNull(cursor.getColumnIndex("Vname"))

//            thermoststLiveData.postValue(valve)
            return valve
        }
        return null
    }


    fun get_from_db_valve_By_name(name: String?): valve? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "valve_DB",
            arrayOf("id", "room_name", "status", "subtype", "ip", "mac", "Vname"),
            "Vname=?",
            arrayOf(name.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val valve = valve()
            valve.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            valve.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            valve.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            valve.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            valve.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
            valve.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
            valve.Vname = cursor.getStringOrNull(cursor.getColumnIndex("Vname"))

//            thermoststLiveData.postValue(valve)
            return valve
        }
        return null
    }

    fun updateNameById(id: Int?, Vname: String?): Int {
        val db: SQLiteDatabase = writableDatabase
        try {
            val value = ContentValues()
            value.put("Vname", Vname)


            return db.update("valve_DB", value, "id=?", arrayOf(id.toString()))
        } catch (e: Exception) {
            println(e)
            return 0
        } finally {
            db.close()
        }
    }

    fun getAllvalves(): List<valve> {
        val db: SQLiteDatabase = readableDatabase
        val valveList = mutableListOf<valve>()

        val cursor = db.query(
            "valve_DB",
            null,
            null,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            while (cursor.moveToNext()) {
                val valve = valve()
                valve.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
                valve.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
                valve.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
                valve.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
                valve.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
                valve.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
                valve.Vname = cursor.getStringOrNull(cursor.getColumnIndex("Vname"))

                valveList.add(valve)
            }
        }

        return valveList
    }


    fun getvalveByRoomName(room_name: String?): valve? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_name = ?"
        val selectionArgs = arrayOf(room_name)
        val cursor = db.query(
            "valve_DB",
            arrayOf("id", "room_name", "status", "subtype", "ip", "mac", "Vname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val valve = valve()
            valve.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            valve.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            valve.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
            valve.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
            valve.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            valve.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            valve.Vname = cursor.getStringOrNull(cursor.getColumnIndex("Vname"))

            return valve
        }

        return null
    }

    fun getvalvesByRoomName(roomName: String?): List<valve> {
        val db: SQLiteDatabase = readableDatabase
        val valveList = mutableListOf<valve>()

        val selection = "room_name = ?"
        val selectionArgs = arrayOf(roomName)
        val cursor = db.query("valve_DB", null, selection, selectionArgs, null, null, null)

        cursor?.use {
            while (cursor.moveToNext()) {
                val valve = valve()


                valve.id = cursor.getIntOrNull(it.getColumnIndex("id"))
                valve.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
                valve.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
                valve.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
                valve.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
                valve.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
                valve.Vname = cursor.getStringOrNull(cursor.getColumnIndex("Vname"))

                valveList.add(valve)
            }
        }
        db.close()
        return valveList
    }
    fun deleteRowsWithNullOrEmptySubTtype() {
        val db: SQLiteDatabase = writableDatabase
        val whereClause = "subtype IS NULL OR subtype = ?"
        val whereArgs = arrayOf("")

        db.delete("valve_DB", whereClause, whereArgs)
        db.close()
    }


    fun delete_from_db_valve(id: Int?): Int {
        val db: SQLiteDatabase = writableDatabase
        return db.delete("valve_DB", "id=?", arrayOf(id.toString()))
    }

    fun updatevalveById(id: Int?, valve: valve): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("room_name", valve.room_name)
        value.put("subtype", valve.subtype)
        value.put("status", valve.status)
        value.put("ip", valve.ip)
        value.put("mac", valve.mac)
        value.put("Vname", valve.Vname)

//        LightLiveData.postValue(light)
        println("valve_db updated" )

        return db.update("valve_DB", value, "id=?", arrayOf(id.toString()))
    }
    fun getValvesBySameMacForPnames(Vnames: List<String>): List<List<valve>> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "Vname IN (${Vnames.joinToString { "?" }})"
        val selectionArgs = Vnames.toTypedArray()
        val cursor = db.query(
            "valve_DB",
            arrayOf("id", "room_name", "subtype", "status", "ip", "mac", "Vname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val valveList = mutableListOf<valve>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val subType = it.getStringOrNull(it.getColumnIndex("subtype"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Vname = it.getStringOrNull(it.getColumnIndex("Vname"))
                val room_name = it.getStringOrNull(it.getColumnIndex("room_name"))

                val valve = valve(
                    room_name ?: "",
                    subType ?: "",
                    status ?: "",
                    ip ?: "",
                    mac ?: "",
                    Vname ?: "",
                    id,
                )
                valveList.add(valve)
            }
        }

        // گروه‌بندی لامپ‌ها بر اساس Mac های مشابه
        val groupedLights = mutableMapOf<String, MutableList<valve>>()

        for (valve in valveList) {
            val mac = valve.mac ?: continue // تایید کردن اینکه Mac خالی نباشد
            if (!groupedLights.containsKey(mac)) {
                groupedLights[mac] = mutableListOf()
            }
            groupedLights[mac]?.add(valve)
        }

        // تبدیل Map به لیست از لیست‌ها
        return groupedLights.values.toList()
    }

    fun clear_db_valve() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("valve_DB", null, null)
        db.close()
        Log.d("database message :", "valve_DB cleared")
    }


    fun getStatusById(id: Int?): String? {
        val db: SQLiteDatabase = readableDatabase
        var status: String? = null

        try {
            val selection = "id = ?"
            val selectionArgs = arrayOf(id.toString())
            val cursor = db.query(
                "valve_DB",
                arrayOf("status"),
                selection,
                selectionArgs,
                null,
                null,
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    status = it.getStringOrNull(it.getColumnIndex("status"))
                }
            }
        } catch (e: Exception) {
            // Handle the exception appropriately, e.g., log it.
        } finally {
            db.close()
        }

        return status
    }
    fun getNumberOfItemsByLname(Lname: String): Int {
        val db: SQLiteDatabase = readableDatabase
        val selection = "Vname = ?"
        val selectionArgs = arrayOf(Lname)
        val cursor = db.query(
            "valve_DB",
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
        private var instance: valve_db? = null

        @Synchronized
        fun getInstance(context: Context): valve_db {
            if (instance == null) {
                instance = valve_db(context.applicationContext)
            }
            return instance!!
        }
    }

}