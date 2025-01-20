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
import com.example.griffinmobile.mudels.curtain

class curtain_db private constructor(context: Context) : SQLiteOpenHelper(context, "curtain_DB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CURTAIN_DB_TABLE =
            "CREATE TABLE IF NOT EXISTS " + "curtain_DB" + "(id INTEGER PRIMARY KEY,  room_name TEXT,  sub_type TEXT ,  status TEXT  ,  ip TEXT  ,  mac TEXT,  Cname TEXT  )"





        db?.execSQL(CREATE_CURTAIN_DB_TABLE)
        Log.d("database message :", "curtain_DB created")


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldversion: Int, newversion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS curtain_DB")
        onCreate(db)
    }

    fun updateCurtainName(oldRoomName: String, newName: String) {
        val db = writableDatabase

        // بررسی وجود رکورد با room_name مشخص شده
        val cursor = db.query(
            "curtain_DB",
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
                put("room_name", newName)
            }

            val rowsAffected = db.update(
                "curtain_DB",
                values,
                "room_name = ?",
                arrayOf(oldRoomName)
            )

            if (rowsAffected > 0) {
                Log.d("database message:", "Room name updated successfully.")
            } else {
                Log.d("database message:", "Failed to update room name.")
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
            DELETE FROM curtain_DB
            WHERE id NOT IN (
                SELECT MIN(id)
                FROM curtain_DB
                GROUP BY mac, sub_type
            )
        """

        try {
            db.execSQL(query)
            Log.d("database message:", "Duplicate entries removed based on mac and sub_type in curtain_DB")
        } catch (e: Exception) {
            Log.e("database error:", "Error removing duplicates in curtain_DB: ${e.message}")
        } finally {
            db.close()
        }
    }
    private val curtainLiveData: MutableLiveData<curtain?> = MutableLiveData()
//    private val roomNamesLiveData = MutableLiveData<List<String>>()


    fun set_to_db_curtain(curtain: curtain) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put("room_name", curtain.room_name)
        values.put("sub_type", curtain.sub_type)
        values.put("status", curtain.status)
        values.put("ip", curtain.ip)
        values.put("mac", curtain.mac)
        values.put("Cname", curtain.Cname)

        db.insert("curtain_DB", null, values)
        db.close()
        Log.d("database message :", "curtain_DB updated")
        curtainLiveData.postValue(curtain)

    }

    fun updateIpById(id: Int?, newIp: String) {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("ip", newIp)

        db.update("curtain_DB", value, "id=?", arrayOf(id.toString()))
        db.close()
    }

    fun updateStatusById(id: Int?, newStatus: String?) {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        try {
            value.put("status", newStatus)
            db.update("curtain_DB", value, "id=?", arrayOf(id.toString()))

        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }

    fun updateStatusAndIpById(id: Int?, newStatus: String?,newIp: String) {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        try {
            value.put("status", newStatus)
            value.put("ip", newIp)
            db.update("curtain_DB", value, "id=?", arrayOf(id.toString()))

        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }

    fun delete_from_db_curtain(id: Int?): Int {
        val db: SQLiteDatabase = writableDatabase
        return db.delete("curtain_DB", "id=?", arrayOf(id.toString()))
    }

    fun get_from_db_curtain(id: Int?): curtain? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "curtain_DB",
            arrayOf("id", "room_name", "sub_type", "status","ip","mac","Cname"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val curtain = curtain()
            curtain.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            curtain.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            curtain.sub_type = cursor.getStringOrNull(cursor.getColumnIndex("sub_type"))
            curtain.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
            curtain.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            curtain.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            curtain.Cname = cursor.getStringOrNull(cursor.getColumnIndex("Cname"))

            curtainLiveData.postValue(curtain)
            return curtain
        }
        return null
    }

    fun get_id_by_same_macs(){


    }
    fun getAllcurtains(): List<curtain?> {
        val db: SQLiteDatabase = readableDatabase
        val cursor = db.query(
            "curtain_DB",
            arrayOf("id", "room_name", "sub_type", "status", "ip", "mac", "Cname"),
            null,
            null,
            null,
            null,
            null
        )

        val curtainList = mutableListOf<curtain?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val roomName = it.getStringOrNull(it.getColumnIndex("room_name"))
                val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Cname = it.getStringOrNull(it.getColumnIndex("Cname"))

                val curtain = curtain(
                    roomName ?: "",
                    subType ?: "",
                    status ?: "",
                    ip ?: "",
                    mac ?: "",
                    id,
                    Cname ?: ""
                )
                curtainList.add(curtain)
            }
        }

        return curtainList.toList()
    }
    fun deleteRowsWithNullOrEmptySubTtype() {
        val db: SQLiteDatabase = writableDatabase
        val whereClause = "sub_type IS NULL OR sub_type = ?"
        val whereArgs = arrayOf("")

        db.delete("curtain_DB", whereClause, whereArgs)
        db.close()
    }



    fun getcurtainsWithNonEmptyMacByRoomName(roomName: String?): LiveData<List<curtain?>> {
        val curtainListLiveData = MutableLiveData<List<curtain?>>()

        val db: SQLiteDatabase = readableDatabase
        val selection = "mac IS NOT NULL AND mac != '' AND room_name LIKE ?"
        val selectionArgs = arrayOf("$roomName%")
        val cursor = db.query(
            "curtain_DB",
            arrayOf("id", "room_name", "sub_type", "status", "ip", "mac", "Cname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val curtainList = mutableListOf<curtain?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Cname = it.getStringOrNull(it.getColumnIndex("Cname"))

                val curtain = curtain(
                    roomName ?: "",
                    subType ?: "",
                    status ?: "",
                    ip ?: "",
                    mac ?: "",
                    id,
                    Cname ?: ""
                )
                curtainList.add(curtain)
            }
        }

        curtainListLiveData.postValue(curtainList)
        return curtainListLiveData
    }


    fun getcurtainsByMacAddress(mac: String?): List<curtain?> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "mac = ?"
        val selectionArgs = arrayOf(mac)
        val cursor = db.query(
            "curtain_DB",
            arrayOf("id", "room_name", "sub_type", "status", "ip", "mac", "Cname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val curtainList = mutableListOf<curtain?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val roomName = it.getStringOrNull(it.getColumnIndex("room_name"))
                val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val macAddress = it.getStringOrNull(it.getColumnIndex("mac"))
                val Cname = it.getStringOrNull(it.getColumnIndex("Cname"))

                val curtain = curtain(
                    roomName ?: "",
                    subType ?: "",
                    status ?: "",
                    ip ?: "",
                    macAddress ?: "",
                    id,
                    Cname ?: ""
                )
                curtainList.add(curtain)
            }
        }

        return curtainList
    }


    fun getStatusById(id: Int?): String? {
        val db: SQLiteDatabase = readableDatabase
        var status: String? = null

        try {
            val selection = "id = ?"
            val selectionArgs = arrayOf(id.toString())
            val cursor = db.query(
                "curtain_DB",
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


    fun getAllcurtainsByRoomName(roomName: String?): List<curtain?> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_name = ?"
        val selectionArgs = arrayOf(roomName)

        val cursor = db.query(
            "curtain_DB",
            arrayOf("id", "room_name", "sub_type", "status", "ip", "mac", "Cname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val curtainList = mutableListOf<curtain?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Cname = it.getStringOrNull(it.getColumnIndex("Cname"))

                val curtain = roomName?.let { it1 ->
                    curtain(
                        it1,
                        subType ?: "",
                        status ?: "",
                        ip ?: "",
                        mac ?: "",
                        id,
                        Cname ?: ""
                    )
                }
                curtainList.add(curtain)
            }
        }

        return curtainList.toList()
    }

    fun getRoomByRoomNamePrefix(prefix: String): curtain? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_name LIKE ?"
        val selectionArgs = arrayOf("$prefix%")
        val cursor = db.query("curtain_DB", null, selection, selectionArgs, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            val room = curtain()
            room.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            room.sub_type = cursor.getStringOrNull(cursor.getColumnIndex("sub_type"))
            room.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
            room.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            room.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            room.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            return room
        }

        return null
    }
    fun updatecurtainById(id: Int?, curtain: curtain): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("room_name", curtain.room_name)
        value.put("sub_type", curtain.sub_type)
        value.put("status", curtain.status)
        value.put("ip", curtain.ip)
        value.put("mac", curtain.mac)
        value.put("Cname", curtain.Cname)

        curtainLiveData.postValue(curtain)
        println("curtain_db updated" )

        return db.update("curtain_DB", value, "id=?", arrayOf(id.toString()))
    }




    fun clear_db_curtain() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("curtain_DB", null, null)
        db.close()
        Log.d("database message :", "curtain_DB cleared")
    }

//    fun isEmptynetwork_tabale() :Boolean{
//
//        val db: SQLiteDatabase =readableDatabase
//        val countQuery="SELECT * FROM curtain_DB"
//        val cursor: Cursor =db.rawQuery(countQuery,null)
//        if (cursor.count==0){
//            return true
//        }else{
//            return false
//        }
//
//    }

    //    fun checkRoomTypeExists(roomType: String): Boolean {
//        val db: SQLiteDatabase = readableDatabase
//        val selection = "sub_type = ?"
//        val selectionArgs = arrayOf(roomType)
//        val cursor = db.query("curtain_DB", null, selection, selectionArgs, null, null, null)
//        val roomTypeExists = cursor.count > 0
//        cursor.close()
//        return roomTypeExists
//    }
    fun checkRoomNameExists(roomName: String): Boolean {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_name = ?"
        val selectionArgs = arrayOf(roomName)
        val cursor = db.query("curtain_DB", null, selection, selectionArgs, null, null, null)
        val roomNameExists = cursor.count > 0
        cursor.close()
        return roomNameExists
    }



    fun get_db_curtain_LiveData(): LiveData<curtain?> {
        return curtainLiveData
    }

//    fun get_from_db_curtain_livedata(id: Int) {
//        val db: SQLiteDatabase = readableDatabase
//        val cursor = db.query(
//            "curtain_DB",
//            arrayOf("id", "room_name", "sub_type", "status","ip","mac"),
//            "id=?",
//            arrayOf(id.toString()),
//            null,
//            null,
//            null
//        )
//        if (cursor != null && cursor.moveToFirst()) {
//            val curtain = curtain()
//            curtain.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
//            curtain.sub_type = cursor.getStringOrNull(cursor.getColumnIndex("sub_type"))
//            curtain.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
//            curtain.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
//            curtain.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
//
//
//            curtainLiveData.postValue(curtain)
//        }
//    }
//    fun getRoomTypeCount(startsWith: String): Int {
//        val db: SQLiteDatabase = readableDatabase
//        val countQuery = "SELECT COUNT(*) FROM curtain_DB WHERE sub_type LIKE '$startsWith%'"
//        val cursor: Cursor = db.rawQuery(countQuery, null)
//        cursor.moveToFirst()
//        val count = cursor.getInt(0)
//        cursor.close()
//
//        return if (count > 0) count else 0
//    }


    fun getcurtainsBySameMacForPnames(Pnames: List<String>): List<List<curtain>> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "Cname IN (${Pnames.joinToString { "?" }})"
        val selectionArgs = Pnames.toTypedArray()
        val cursor = db.query(
            "curtain_DB",
            arrayOf("id", "room_name", "sub_type", "status", "ip", "mac", "Cname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val curtainList = mutableListOf<curtain>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Cname = it.getStringOrNull(it.getColumnIndex("Cname"))
                val room_name = it.getStringOrNull(it.getColumnIndex("room_name"))

                val curtain = curtain(
                    room_name ?: "",
                    subType ?: "",
                    status ?: "",
                    ip ?: "",
                    mac ?: "",
                    id,
                    Cname ?: "",
                )
                curtainList.add(curtain)
            }
        }

        // گروه‌بندی لامپ‌ها بر اساس Mac های مشابه
        val groupedLights = mutableMapOf<String, MutableList<curtain>>()

        for (curtain in curtainList) {
            val mac = curtain.mac ?: continue // تایید کردن اینکه Mac خالی نباشد
            if (!groupedLights.containsKey(mac)) {
                groupedLights[mac] = mutableListOf()
            }
            groupedLights[mac]?.add(curtain)
        }

        // تبدیل Map به لیست از لیست‌ها
        return groupedLights.values.toList()
    }

    fun getCurtainByCname(Cname: String): curtain? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "Cname = ?"
        val selectionArgs = arrayOf(Cname)
        val cursor = db.query(
            "curtain_DB",
            arrayOf("id", "room_name", "sub_type", "status", "ip", "mac", "Cname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val curtain = curtain()
            curtain.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            curtain.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            curtain.sub_type = cursor.getStringOrNull(cursor.getColumnIndex("sub_type"))
            curtain.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
            curtain.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            curtain.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            curtain.Cname = cursor.getStringOrNull(cursor.getColumnIndex("Cname"))

            return curtain
        }

        return null
    }


    fun getNumberOfItemsByLname(Lname: String): Int {
        val db: SQLiteDatabase = readableDatabase
        val selection = "Cname = ?"
        val selectionArgs = arrayOf(Lname)
        val cursor = db.query(
            "curtain_DB",
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
        private var instance: curtain_db? = null

        @Synchronized
        fun getInstance(context: Context): curtain_db {
            if (instance == null) {
                instance = curtain_db(context.applicationContext)
            }
            return instance!!
        }
    }







}