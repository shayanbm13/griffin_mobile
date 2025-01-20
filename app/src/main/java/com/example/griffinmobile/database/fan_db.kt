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
import com.example.griffinmobile.mudels.fan

class fan_db private constructor(context: Context) : SQLiteOpenHelper(context,"fan_DB",null,1){
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_fan_DB="CREATE TABLE IF NOT EXISTS "+ "fan_DB"+"(id INTEGER PRIMARY KEY , room_name TEXT, status TEXT, subtype TEXT, ip TEXT, mac TEXT, Fname TEXT )"

        db!!.execSQL(CREATE_fan_DB)
        Log.d("database message :" ,"fan_db created")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS fan_db")
        onCreate(db)
    }

    fun updateFanName(oldRoomName: String, newName: String) {
        val db = writableDatabase

        // بررسی وجود رکورد با room_name مشخص شده
        val cursor = db.query(
            "fan_DB",
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
                put("room_name", newName)  // بروزرسانی Fname با newName
            }

            val rowsAffected = db.update(
                "fan_DB",
                values,
                "room_name = ?",
                arrayOf(oldRoomName)
            )

            if (rowsAffected > 0) {
                Log.d("database message:", "Fan name updated successfully.")
            } else {
                Log.d("database message:", "Failed to update fan name.")
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
            DELETE FROM fan_DB
            WHERE id NOT IN (
                SELECT MIN(id)
                FROM fan_DB
                GROUP BY mac, subtype
            )
        """

        try {
            db.execSQL(query)
            Log.d("database message:", "Duplicate entries removed based on mac and subtype in fan_DB")
        } catch (e: Exception) {
            Log.e("database error:", "Error removing duplicates in fan_DB: ${e.message}")
        } finally {
            db.close()
        }
    }

    fun set_to_db_fan(fan: fan){
        val db=writableDatabase
        val values= ContentValues()
        values.put("room_name",fan.room_name)
        values.put("status",fan.status)
        values.put("subtype",fan.subtype)
        values.put("ip",fan.ip)
        values.put("mac",fan.mac)
        values.put("Fname",fan.Fname)
        db.insert("fan_db",null,values)
        db.close()
        Log.d("database message :", "fan_db updated")

    }

    fun updateStatusbyId(id:Int?,newStatus:String){
        val db=writableDatabase
        val values= ContentValues()
        try {
            values.put("status",newStatus)
            db.update("fan_DB",values, "id=?", arrayOf(id.toString()))


        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }
    fun updatefanbyId(id:Int?,ip:String){
        val db=writableDatabase
        val values= ContentValues()
        try {
            values.put("ip",ip)
            db.update("fan_DB",values, "id=?", arrayOf(id.toString()))


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
            db.update("fan_DB",values,"id=?", arrayOf(id.toString()))
        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }


    fun getfanByCname(Fname: String?): fan? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "Fname = ?"
        val selectionArgs = arrayOf(Fname)
        val cursor = db.query(
            "fan_DB",
            arrayOf("id", "room_name", "status", "subtype", "ip", "mac", "Fname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val fan = fan()
            fan.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            fan.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            fan.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
            fan.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
            fan.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            fan.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            fan.Fname = cursor.getStringOrNull(cursor.getColumnIndex("Fname"))

            return fan
        }

        return null
    }


    fun getfansByMacAddress(mac: String?): List<fan?> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "mac = ?"
        val selectionArgs = arrayOf(mac)
        val cursor = db.query(
            "fan_DB",
            arrayOf("id", "room_name", "subtype", "status", "ip", "mac", "Fname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val fanList = mutableListOf<fan?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val roomName = it.getStringOrNull(it.getColumnIndex("room_name"))
                val subType = it.getStringOrNull(it.getColumnIndex("subtype"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val macAddress = it.getStringOrNull(it.getColumnIndex("mac"))
                val Fname = it.getStringOrNull(it.getColumnIndex("Fname"))

                val fan = fan(
                    roomName ?: "",
                    status ?: "",
                    subType ?: "",
                    ip ?: "",
                    macAddress ?: "",
                    Fname ?: "",
                    id
                )
                fanList.add(fan)
            }
        }

        return fanList
    }


    fun getfansWithNonEmptyMacByRoomName(roomName: String?): LiveData<List<fan?>> {
        val fanListLiveData = MutableLiveData<List<fan?>>()

        val db: SQLiteDatabase = readableDatabase
        val selection = "mac IS NOT NULL AND mac != '' AND room_name LIKE ?"
        val selectionArgs = arrayOf("$roomName%")
        val cursor = db.query(
            "fan_DB",
            arrayOf("id", "room_name", "subtype", "status", "ip", "mac", "Fname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val fanList = mutableListOf<fan?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val subType = it.getStringOrNull(it.getColumnIndex("subtype"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Fname = it.getStringOrNull(it.getColumnIndex("Fname"))

                val fan = fan(
                    roomName ?: "",
                    status ?: "",
                    subType ?: "",
                    ip ?: "",
                    mac ?: "",
                    Fname ?: "",
                    id
                )
                fanList.add(fan)
            }
        }

        fanListLiveData.postValue(fanList)
        return fanListLiveData
    }


    fun getfansWithNonEmptyMacByRoomName2(roomName: String?): List<List<fan?>> {
        val fanListLiveData = mutableListOf<List<fan?>>()

        val db: SQLiteDatabase = readableDatabase
        val selection = "mac IS NOT NULL AND mac != '' AND room_name LIKE ?"
        val selectionArgs = arrayOf("$roomName%")
        val cursor = db.query(
            "fan_DB",
            arrayOf("id", "room_name", "subtype", "status", "ip", "mac", "Fname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val fanList = mutableListOf<fan?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val subType = it.getStringOrNull(it.getColumnIndex("subtype"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Fname = it.getStringOrNull(it.getColumnIndex("Fname"))

                val fan = fan(
                    roomName ?: "",
                    status ?: "",
                    subType ?: "",
                    ip ?: "",
                    mac ?: "",
                    Fname ?: "",
                    id
                )
                fanList.add(fan)
            }
        }

        fanListLiveData.add(fanList)
        return fanListLiveData
    }



    fun get_from_db_fan(id: Int?): fan? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "fan_DB",
            arrayOf("id", "room_name", "status", "subtype", "ip", "mac", "Fname"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val fan = fan()
            fan.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            fan.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            fan.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            fan.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            fan.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
            fan.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
            fan.Fname = cursor.getStringOrNull(cursor.getColumnIndex("Fname"))

//            thermoststLiveData.postValue(fan)
            return fan
        }
        db.close()
        return null
    }


    fun get_from_db_fan_By_name(name: String?): fan? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "fan_DB",
            arrayOf("id", "room_name", "status", "subtype", "ip", "mac", "Fname"),
            "Fname=?",
            arrayOf(name.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val fan = fan()
            fan.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            fan.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            fan.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            fan.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            fan.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
            fan.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
            fan.Fname = cursor.getStringOrNull(cursor.getColumnIndex("Fname"))

//            thermoststLiveData.postValue(fan)
            return fan
        }
        return null
    }

    fun updateNameById(id: Int?, Fname: String?): Int {
        val db: SQLiteDatabase = writableDatabase
        try {
            val value = ContentValues()
            value.put("Fname", Fname)


            return db.update("fan_DB", value, "id=?", arrayOf(id.toString()))
        } catch (e: Exception) {
            println(e)
            return 0
        } finally {
            db.close()
        }
    }

    fun getAllfans(): List<fan> {
        val db: SQLiteDatabase = readableDatabase
        val fanList = mutableListOf<fan>()

        val cursor = db.query(
            "fan_DB",
            null,
            null,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            while (cursor.moveToNext()) {
                val fan = fan()
                fan.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
                fan.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
                fan.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
                fan.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
                fan.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
                fan.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
                fan.Fname = cursor.getStringOrNull(cursor.getColumnIndex("Fname"))

                fanList.add(fan)
            }
        }

        return fanList
    }


    fun getfanByRoomName(room_name: String?): fan? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_name = ?"
        val selectionArgs = arrayOf(room_name)
        val cursor = db.query(
            "fan_DB",
            arrayOf("id", "room_name", "status", "subtype", "ip", "mac", "Fname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val fan = fan()
            fan.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            fan.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            fan.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
            fan.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
            fan.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            fan.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            fan.Fname = cursor.getStringOrNull(cursor.getColumnIndex("Fname"))

            return fan
        }

        return null
    }

    fun getfansByRoomName(roomName: String?): List<fan> {
        val db: SQLiteDatabase = readableDatabase
        val fanList = mutableListOf<fan>()

        val selection = "room_name = ?"
        val selectionArgs = arrayOf(roomName)
        val cursor = db.query("fan_DB", null, selection, selectionArgs, null, null, null)

        cursor?.use {
            while (cursor.moveToNext()) {
                val fan = fan()


                fan.id = cursor.getIntOrNull(it.getColumnIndex("id"))
                fan.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
                fan.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
                fan.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
                fan.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
                fan.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
                fan.Fname = cursor.getStringOrNull(cursor.getColumnIndex("Fname"))

                fanList.add(fan)
            }
        }
        db.close()
        return fanList
    }
    fun deleteRowsWithNullOrEmptySubTtype() {
        val db: SQLiteDatabase = writableDatabase
        val whereClause = "subtype IS NULL OR subtype = ?"
        val whereArgs = arrayOf("")

        db.delete("fan_DB", whereClause, whereArgs)
        db.close()
    }


    fun delete_from_db_fan(id: Int?): Int {
        val db: SQLiteDatabase = writableDatabase
        return db.delete("fan_DB", "id=?", arrayOf(id.toString()))
    }

    fun updatefanById(id: Int?, fan: fan): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("room_name", fan.room_name)
        value.put("subtype", fan.subtype)
        value.put("status", fan.status)
        value.put("ip", fan.ip)
        value.put("mac", fan.mac)
        value.put("Fname", fan.Fname)

//        LightLiveData.postValue(light)
        println("fan_db updated" )

        return db.update("fan_DB", value, "id=?", arrayOf(id.toString()))
    }

    fun getfansBySameMacForLnames(Lnames: List<String>): List<List<fan>> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "Fname IN (${Lnames.joinToString { "?" }})"
        val selectionArgs = Lnames.toTypedArray()
        val cursor = db.query(
            "fan_DB",
            arrayOf("id", "room_name", "subtype", "status", "ip", "mac", "Fname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val fanList = mutableListOf<fan>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val subType = it.getStringOrNull(it.getColumnIndex("subtype"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Fname = it.getStringOrNull(it.getColumnIndex("Fname"))
                val room_name = it.getStringOrNull(it.getColumnIndex("room_name"))

                val fan = fan(
                    room_name ?: "",
                    subType ?: "",
                    status ?: "",
                    ip ?: "",
                    mac ?: "",
                    Fname ?: "",
                    id,

                )
                fanList.add(fan)
            }
        }

        // گروه‌بندی لامپ‌ها بر اساس Mac های مشابه
        val groupedfans = mutableMapOf<String, MutableList<fan>>()

        for (fan in fanList) {
            val mac = fan.mac ?: continue // تایید کردن اینکه Mac خالی نباشد
            if (!groupedfans.containsKey(mac)) {
                groupedfans[mac] = mutableListOf()
            }
            groupedfans[mac]?.add(fan)
        }

        // تبدیل Map به لیست از لیست‌ها
        return groupedfans.values.toList()
    }


    fun getStatusById(id: Int?): String? {
        val db: SQLiteDatabase = readableDatabase
        var status: String? = null

        try {
            val selection = "id = ?"
            val selectionArgs = arrayOf(id.toString())
            val cursor = db.query(
                "fan_DB",
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
        val selection = "Fname = ?"
        val selectionArgs = arrayOf(Lname)
        val cursor = db.query(
            "fan_DB",
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
    fun clear_db_fan() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("fan_DB", null, null)
        db.close()
        Log.d("database message :", "fan_DB cleared")
    }

    companion object {
        private var instance: fan_db? = null

        @Synchronized
        fun getInstance(context: Context): fan_db {
            if (instance == null) {
                instance = fan_db(context.applicationContext)
            }
            return instance!!
        }
    }

}