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
import com.example.griffinmobile.mudels.Light
import com.example.griffinmobile.mudels.Plug
import com.example.griffinmobile.mudels.Thermostst
import com.example.griffinmobile.mudels.curtain

class plug_db private constructor(context: Context) :SQLiteOpenHelper(context,"plug_DB",null,1){
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_PLUG_DB="CREATE TABLE IF NOT EXISTS "+ "plug_DB"+"(id INTEGER PRIMARY KEY , room_name TEXT, status TEXT, subtype TEXT, ip TEXT, mac TEXT, Pname TEXT )"

        db!!.execSQL(CREATE_PLUG_DB)
        Log.d("database message :" ,"plug_db created")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS plug_db")
        onCreate(db)
    }

    fun updatePlugName(oldRoomName: String, newName: String) {
        val db = writableDatabase

        // بررسی وجود رکورد با room_name مشخص شده
        val cursor = db.query(
            "plug_DB",
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
                put("room_name", newName)  // بروزرسانی Pname با newName
            }

            val rowsAffected = db.update(
                "plug_DB",
                values,
                "room_name = ?",
                arrayOf(oldRoomName)
            )

            if (rowsAffected > 0) {
                Log.d("database message:", "Plug name updated successfully.")
            } else {
                Log.d("database message:", "Failed to update plug name.")
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
            DELETE FROM plug_DB
            WHERE id NOT IN (
                SELECT MIN(id)
                FROM plug_DB
                GROUP BY mac, subtype
            )
        """

        try {
            db.execSQL(query)
            Log.d("database message:", "Duplicate entries removed based on mac and subtype in plug_DB")
        } catch (e: Exception) {
            Log.e("database error:", "Error removing duplicates in plug_DB: ${e.message}")
        } finally {
            db.close()
        }
    }
    fun set_to_db_plug(plug: Plug){
        val db=writableDatabase
        val values= ContentValues()
        values.put("room_name",plug.room_name)
        values.put("status",plug.status)
        values.put("subtype",plug.subtype)
        values.put("ip",plug.ip)
        values.put("mac",plug.mac)
        values.put("Pname",plug.Pname)
        db.insert("plug_db",null,values)
        db.close()
        Log.d("database message :", "plug_db updated")

    }

    fun updateStatusbyId(id:Int?,newStatus:String){
        val db=writableDatabase
        val values=ContentValues()
        try {
            values.put("status",newStatus)
            db.update("plug_DB",values, "id=?", arrayOf(id.toString()))


        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }
    fun updatePlugbyId(id:Int?,ip:String){
        val db=writableDatabase
        val values=ContentValues()
        try {
            values.put("ip",ip)
            db.update("plug_DB",values, "id=?", arrayOf(id.toString()))


        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }
    fun updateStatusAndIpById(id: Int?,newStatus: String?,newIp:String?){
        val db=writableDatabase
        val values=ContentValues()
        try {
            values.put("status",newStatus)
            values.put("ip",newIp)
            db.update("plug_DB",values,"id=?", arrayOf(id.toString()))
        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }


    fun getPlugByCname(Pname: String?): Plug? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "Pname = ?"
        val selectionArgs = arrayOf(Pname)
        val cursor = db.query(
            "plug_DB",
            arrayOf("id", "room_name", "status", "subtype", "ip", "mac", "Pname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val plug = Plug()
            plug.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            plug.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            plug.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
            plug.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
            plug.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            plug.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            plug.Pname = cursor.getStringOrNull(cursor.getColumnIndex("Pname"))

            return plug
        }
        db.close()

        return null
    }


    fun getPlugsByMacAddress(mac: String?): List<Plug?> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "mac = ?"
        val selectionArgs = arrayOf(mac)
        val cursor = db.query(
            "plug_DB",
            arrayOf("id", "room_name", "subtype", "status", "ip", "mac", "Pname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val plugList = mutableListOf<Plug?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val roomName = it.getStringOrNull(it.getColumnIndex("room_name"))
                val subType = it.getStringOrNull(it.getColumnIndex("subtype"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val macAddress = it.getStringOrNull(it.getColumnIndex("mac"))
                val Pname = it.getStringOrNull(it.getColumnIndex("Pname"))

                val plug = Plug(
                    roomName ?: "",
                    status ?: "",
                    subType ?: "",
                    ip ?: "",
                    macAddress ?: "",
                    Pname ?: "",
                    id
                )
                plugList.add(plug)
            }
        }

        return plugList
    }


    fun getplugsWithNonEmptyMacByRoomName(roomName: String?): LiveData<List<Plug?>> {
        val plugListLiveData = MutableLiveData<List<Plug?>>()

        val db: SQLiteDatabase = readableDatabase
        val selection = "mac IS NOT NULL AND mac != '' AND room_name LIKE ?"
        val selectionArgs = arrayOf("$roomName%")
        val cursor = db.query(
            "plug_DB",
            arrayOf("id", "room_name", "subtype", "status", "ip", "mac", "Pname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val plugList = mutableListOf<Plug?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val subType = it.getStringOrNull(it.getColumnIndex("subtype"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Pname = it.getStringOrNull(it.getColumnIndex("Pname"))

                val plug = Plug(
                    roomName ?: "",
                    status ?: "",
                    subType ?: "",
                    ip ?: "",
                    mac ?: "",
                    Pname ?: "",
                    id
                )
                plugList.add(plug)
            }
        }

        plugListLiveData.postValue(plugList)
        return plugListLiveData
    }


    fun getplugsWithNonEmptyMacByRoomName2(roomName: String?): List<List<Plug?>> {
        val plugListLiveData = mutableListOf<List<Plug?>>()

        val db: SQLiteDatabase = readableDatabase
        val selection = "mac IS NOT NULL AND mac != '' AND room_name LIKE ?"
        val selectionArgs = arrayOf("$roomName%")
        val cursor = db.query(
            "plug_DB",
            arrayOf("id", "room_name", "subtype", "status", "ip", "mac", "Pname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val plugList = mutableListOf<Plug?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val subType = it.getStringOrNull(it.getColumnIndex("subtype"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Pname = it.getStringOrNull(it.getColumnIndex("Pname"))

                val plug = Plug(
                    roomName ?: "",
                    status ?: "",
                    subType ?: "",
                    ip ?: "",
                    mac ?: "",
                    Pname ?: "",
                    id
                )
                plugList.add(plug)
            }
        }

        plugListLiveData.add(plugList)
        return plugListLiveData
    }



    fun get_from_db_Plug(id: Int?): Plug? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "plug_DB",
            arrayOf("id", "room_name", "status", "subtype", "ip", "mac", "Pname"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val plug = Plug()
            plug.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            plug.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            plug.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            plug.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            plug.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
            plug.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
            plug.Pname = cursor.getStringOrNull(cursor.getColumnIndex("Pname"))

//            thermoststLiveData.postValue(plug)
            return plug
        }
        return null
    }


    fun get_from_db_Plug_By_name(name: String?): Plug? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "plug_DB",
            arrayOf("id", "room_name", "status", "subtype", "ip", "mac", "Pname"),
            "Pname=?",
            arrayOf(name.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val plug = Plug()
            plug.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            plug.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            plug.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            plug.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            plug.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
            plug.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
            plug.Pname = cursor.getStringOrNull(cursor.getColumnIndex("Pname"))

//            thermoststLiveData.postValue(plug)
            return plug
        }
        return null
    }

    fun updateNameById(id: Int?, Pname: String?): Int {
        val db: SQLiteDatabase = writableDatabase
        try {
            val value = ContentValues()
            value.put("Pname", Pname)


            return db.update("plug_DB", value, "id=?", arrayOf(id.toString()))
        } catch (e: Exception) {
            println(e)
            return 0
        } finally {
            db.close()
        }
    }

    fun getAllPlugs(): List<Plug> {
        val db: SQLiteDatabase = readableDatabase
        val plugList = mutableListOf<Plug>()

        val cursor = db.query(
            "plug_DB",
            null,
            null,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            while (cursor.moveToNext()) {
                val plug = Plug()
                plug.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
                plug.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
                plug.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
                plug.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
                plug.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
                plug.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
                plug.Pname = cursor.getStringOrNull(cursor.getColumnIndex("Pname"))

                plugList.add(plug)
            }
        }

        return plugList
    }


    fun getPlugByRoomName(room_name: String?): Plug? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_name = ?"
        val selectionArgs = arrayOf(room_name)
        val cursor = db.query(
            "plug_DB",
            arrayOf("id", "room_name", "status", "subtype", "ip", "mac", "Pname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val plug = Plug()
            plug.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            plug.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            plug.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
            plug.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
            plug.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            plug.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            plug.Pname = cursor.getStringOrNull(cursor.getColumnIndex("Pname"))

            return plug
        }

        return null
    }

    fun getPlugsByRoomName(roomName: String?): List<Plug> {
        val db: SQLiteDatabase = readableDatabase
        val plugList = mutableListOf<Plug>()

        val selection = "room_name = ?"
        val selectionArgs = arrayOf(roomName)
        val cursor = db.query("plug_DB", null, selection, selectionArgs, null, null, null)

        cursor?.use {
            while (cursor.moveToNext()) {
                val plug = Plug()


                plug.id = cursor.getIntOrNull(it.getColumnIndex("id"))
                plug.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
                plug.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
                plug.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
                plug.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
                plug.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
                plug.Pname = cursor.getStringOrNull(cursor.getColumnIndex("Pname"))

                plugList.add(plug)
            }
        }
        db.close()
        return plugList
    }
    fun deleteRowsWithNullOrEmptySubTtype() {
        val db: SQLiteDatabase = writableDatabase
        val whereClause = "subtype IS NULL OR subtype = ?"
        val whereArgs = arrayOf("")

        db.delete("plug_DB", whereClause, whereArgs)
        db.close()
    }


    fun delete_from_db_Plug(id: Int?): Int {
        val db: SQLiteDatabase = writableDatabase
        return db.delete("plug_DB", "id=?", arrayOf(id.toString()))

    }

    fun updatePlugById(id: Int?, plug: Plug): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("room_name", plug.room_name)
        value.put("subtype", plug.subtype)
        value.put("status", plug.status)
        value.put("ip", plug.ip)
        value.put("mac", plug.mac)
        value.put("Pname", plug.Pname)

//        LightLiveData.postValue(light)
        println("plug_db updated" )

        return db.update("plug_DB", value, "id=?", arrayOf(id.toString()))
    }
    fun getPlugsBySameMacForPnames(Pnames: List<String>): List<List<Plug>> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "Pname IN (${Pnames.joinToString { "?" }})"
        val selectionArgs = Pnames.toTypedArray()
        val cursor = db.query(
            "plug_DB",
            arrayOf("id", "room_name", "subtype", "status", "ip", "mac", "Pname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val plugList = mutableListOf<Plug>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val subType = it.getStringOrNull(it.getColumnIndex("subtype"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Pname = it.getStringOrNull(it.getColumnIndex("Pname"))
                val room_name = it.getStringOrNull(it.getColumnIndex("room_name"))

                val plug = Plug(
                    room_name ?: "",
                    subType ?: "",
                    status ?: "",
                    ip ?: "",
                    mac ?: "",
                    Pname ?: "",
                    id,
                )
                plugList.add(plug)
            }
        }

        // گروه‌بندی لامپ‌ها بر اساس Mac های مشابه
        val groupedLights = mutableMapOf<String, MutableList<Plug>>()

        for (plug in plugList) {
            val mac = plug.mac ?: continue // تایید کردن اینکه Mac خالی نباشد
            if (!groupedLights.containsKey(mac)) {
                groupedLights[mac] = mutableListOf()
            }
            groupedLights[mac]?.add(plug)
        }

        // تبدیل Map به لیست از لیست‌ها
        return groupedLights.values.toList()
    }

    fun clear_db_plug() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("plug_DB", null, null)
        db.close()
        Log.d("database message :", "plug_DB cleared")
    }


    fun getStatusById(id: Int?): String? {
        val db: SQLiteDatabase = readableDatabase
        var status: String? = null

        try {
            val selection = "id = ?"
            val selectionArgs = arrayOf(id.toString())
            val cursor = db.query(
                "plug_DB",
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
        val selection = "Pname = ?"
        val selectionArgs = arrayOf(Lname)
        val cursor = db.query(
            "plug_DB",
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
        private var instance: plug_db? = null

        @Synchronized
        fun getInstance(context: Context): plug_db {
            if (instance == null) {
                instance = plug_db(context.applicationContext)
            }
            return instance!!
        }
    }

}