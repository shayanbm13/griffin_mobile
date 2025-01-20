package com.example.griffinmobile.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.lifecycle.MutableLiveData
import com.example.griffinmobile.modules.rooms_v2


class room_devices_db private constructor(context: Context) : SQLiteOpenHelper(context, "ROOMS_DEVICES_DB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_ROOMS_DEVICES_DB_TABLE =
            "CREATE TABLE IF NOT EXISTS " + "ROOMS_DEVICES_DB" + "(id INTEGER PRIMARY KEY,  room_id TEXT,  room_devices TEXT ,  homes TEXT  )"

        db?.execSQL(CREATE_ROOMS_DEVICES_DB_TABLE)
        Log.d("database message :", "ROOMS_DEVICES_DB created")


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldversion: Int, newversion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ROOMS_DEVICES_DB")
        onCreate(db)
    }
    fun removeDuplicates() {
        val db = this.writableDatabase

        // کوئری برای حذف رکوردهای تکراری
        val query = """
            DELETE FROM ROOMS_DEVICES_DB
            WHERE id NOT IN (
                SELECT MIN(id)
                FROM ROOMS_DEVICES_DB
                GROUP BY room_id, room_devices
            )
        """

        try {
            db.execSQL(query)
            Log.d("database message:", "Duplicate entries removed based on room_id and room_devices in ROOMS_DEVICES_DB")
        } catch (e: Exception) {
            Log.e("database error:", "Error removing duplicates in ROOMS_DEVICES_DB: ${e.message}")
        } finally {
            db.close()
        }
    }
    private val roomsLiveData: MutableLiveData<rooms_v2?> = MutableLiveData()
    private val roomNamesLiveData = MutableLiveData<List<String>>()


    fun set_to_db_rooms(rooms_v2: rooms_v2) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put("room_id", rooms_v2.room_id)
        values.put("room_devices", rooms_v2.room_devices)
        values.put("homes", rooms_v2.homes)


        db.insert("ROOMS_DEVICES_DB", null, values)
        db.close()
        Log.d("database message :", "ROOMS_DEVICES_DB updated")
        roomsLiveData.postValue(rooms_v2)

    }

    fun deleteRoomByRoomId(roomId: String): Int {
        val db: SQLiteDatabase = writableDatabase
        val deletedRows = db.delete("ROOMS_DEVICES_DB", "room_id=?", arrayOf(roomId))
        db.close()
        Log.d("database message :", "$deletedRows rows deleted with room_id = $roomId")
        return deletedRows
    }

    fun getAllRooms(): List<rooms_v2?> {
        val db: SQLiteDatabase = readableDatabase
        val cursor = db.query(
            "ROOMS_DEVICES_DB",
            arrayOf("id", "room_id", "room_devices", "homes"),
            null,
            null,
            null,
            null,
            null
        )

        val RoomList = mutableListOf<rooms_v2?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val roomName = it.getStringOrNull(it.getColumnIndex("room_id"))
                val room_devices = it.getStringOrNull(it.getColumnIndex("room_devices"))
                val homes = it.getStringOrNull(it.getColumnIndex("homes"))


                val room = id?.let { it1 ->
                    rooms_v2(
                        roomName ?: "",
                        room_devices ?: "",
                        homes ?: "",
                        it1,

                        )
                }
                RoomList.add(room)
            }
        }

        return RoomList.toList()
    }




    fun getAllRoomNames(): List<String> {
        val db: SQLiteDatabase = readableDatabase
        val cursor = db.query(
            "ROOMS_DEVICES_DB",
            arrayOf("id", "room_id", "room_devices", "homes"),
            null,
            null,
            null,
            null,
            null
        )

        val roomNamesList = mutableListOf<String>()

        cursor?.use {
            while (it.moveToNext()) {
                val roomName = it.getStringOrNull(it.getColumnIndex("room_id"))
                roomName?.let {
                    roomNamesList.add(roomName)
                }
            }
        }

        return roomNamesList.toList() // تبدیل لیست به لیست ثابت با استفاده از toList()
    }

    fun delete_from_db_rooms(id: Int?): Int {
        val db: SQLiteDatabase = writableDatabase
        return db.delete("ROOMS_DEVICES_DB", "id=?", arrayOf(id.toString()))
    }

    fun get_from_db_rooms(id: Int): rooms_v2? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "ROOMS_DEVICES_DB",
            arrayOf("id", "room_id", "room_devices", "homes"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val rooms_v2 = rooms_v2()
            rooms_v2.room_id = cursor.getStringOrNull(cursor.getColumnIndex("room_id"))
            rooms_v2.room_devices = cursor.getStringOrNull(cursor.getColumnIndex("room_devices"))
            rooms_v2.homes = cursor.getStringOrNull(cursor.getColumnIndex("homes"))

            roomsLiveData.postValue(rooms_v2)
            return rooms_v2
        }
        return null
    }


    fun getRoomByRoomIdAndHome(roomId: String, homes: String): rooms_v2? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_id = ? AND homes = ?"
        val selectionArgs = arrayOf(roomId, homes)

        val cursor = db.query(
            "ROOMS_DEVICES_DB",
            arrayOf("id", "room_id", "room_devices", "homes"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var room: rooms_v2? = null
        cursor?.use {
            if (it.moveToFirst()) {
                room = rooms_v2(
                    room_id = it.getStringOrNull(it.getColumnIndex("room_id")) ?: "",
                    room_devices = it.getStringOrNull(it.getColumnIndex("room_devices")) ?: "",
                    homes = it.getStringOrNull(it.getColumnIndex("homes")) ?: "",
                    id = it.getIntOrNull(it.getColumnIndex("id")) ?: 0
                )
            }
        }
        return room
    }

    fun getRoomByRoomNamePrefix(prefix: String): rooms_v2? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_id LIKE ?"
        val selectionArgs = arrayOf("$prefix%")
        val cursor = db.query("ROOMS_DEVICES_DB", null, selection, selectionArgs, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            val room = rooms_v2()
            room.room_id = cursor.getStringOrNull(cursor.getColumnIndex("room_id"))
            room.room_devices = cursor.getStringOrNull(cursor.getColumnIndex("room_devices"))
            room.homes = cursor.getStringOrNull(cursor.getColumnIndex("homes"))
            room.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            return room
        }

        return null
    }
    fun updateRoomById(id: Int?, newRoom: rooms_v2): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("room_id", newRoom.room_id)
        value.put("room_devices", newRoom.room_devices)
        value.put("homes", newRoom.homes)

        roomsLiveData.postValue(newRoom)

        return db.update("ROOMS_DEVICES_DB", value, "id=?", arrayOf(id.toString()))
    }


    fun clear_db_rooms() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("ROOMS_DEVICES_DB", null, null)
        db.close()
        Log.d("database message :", "ROOMS_DEVICES_DB cleared")
    }

    fun isEmptynetwork_tabale() :Boolean{

        val db: SQLiteDatabase =readableDatabase
        val countQuery="SELECT * FROM ROOMS_DEVICES_DB"
        val cursor: Cursor =db.rawQuery(countQuery,null)
        if (cursor.count==0){
            return true
        }else{
            return false
        }

    }

    fun checkRoomTypeExists(roomType: String): Boolean {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_devices = ?"
        val selectionArgs = arrayOf(roomType)
        val cursor = db.query("ROOMS_DEVICES_DB", null, selection, selectionArgs, null, null, null)
        val roomTypeExists = cursor.count > 0
        cursor.close()
        return roomTypeExists
    }
    fun checkRoomNameExists(roomName: String): Boolean {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_id = ?"
        val selectionArgs = arrayOf(roomName)
        val cursor = db.query("ROOMS_DEVICES_DB", null, selection, selectionArgs, null, null, null)
        val roomNameExists = cursor.count > 0
        cursor.close()
        return roomNameExists
    }




    fun getRoomTypeCount(startsWith: String): Int {
        val db: SQLiteDatabase = readableDatabase
        val countQuery = "SELECT COUNT(*) FROM ROOMS_DEVICES_DB WHERE room_devices LIKE '$startsWith%'"
        val cursor: Cursor = db.rawQuery(countQuery, null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        return if (count > 0) count else 0
    }
    fun getRoomsByHome(home: String): List<rooms_v2> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "homes = ?"
        val selectionArgs = arrayOf(home)
        val cursor = db.query(
            "ROOMS_DEVICES_DB",
            arrayOf("id", "room_id", "room_devices", "homes"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val roomsList = mutableListOf<rooms_v2>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id")) ?: 0
                val roomId = it.getStringOrNull(it.getColumnIndex("room_id")) ?: ""
                val roomDevices = it.getStringOrNull(it.getColumnIndex("room_devices")) ?: ""
                val homes = it.getStringOrNull(it.getColumnIndex("homes")) ?: ""

                roomsList.add(rooms_v2(room_id = roomId, room_devices = roomDevices, homes = homes, id = id))
            }
        }

        return roomsList
    }




    companion object {
        private var instance: room_devices_db? = null

        @Synchronized
        fun getInstance(context: Context): room_devices_db {
            if (instance == null) {
                instance = room_devices_db(context.applicationContext)
            }
            return instance!!
        }
    }

}

