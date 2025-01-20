package com.example.griffinmobile.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.griffinmobile.mudels.Light
import com.example.griffinmobile.mudels.rooms

class rooms_db private constructor(context: Context) : SQLiteOpenHelper(context, "ROOMS_DB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_ROOMS_DB_TABLE =
            "CREATE TABLE IF NOT EXISTS " + "ROOMS_DB" + "(id INTEGER PRIMARY KEY,  room_name TEXT,  room_type TEXT ,  room_image TEXT  )"

        db?.execSQL(CREATE_ROOMS_DB_TABLE)
        Log.d("database message :", "ROOMS_DB created")


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldversion: Int, newversion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ROOMS_DB")
        onCreate(db)
    }
    fun removeDuplicates() {
        val db = this.writableDatabase

        // کوئری برای حذف رکوردهای تکراری
        val query = """
            DELETE FROM ROOMS_DB
            WHERE id NOT IN (
                SELECT MIN(id)
                FROM ROOMS_DB
                GROUP BY room_name, room_type
            )
        """

        try {
            db.execSQL(query)
            Log.d("database message:", "Duplicate entries removed based on room_name and room_type in ROOMS_DB")
        } catch (e: Exception) {
            Log.e("database error:", "Error removing duplicates in ROOMS_DB: ${e.message}")
        } finally {
            db.close()
        }
    }
    private val roomsLiveData: MutableLiveData<rooms?> = MutableLiveData()
    private val roomNamesLiveData = MutableLiveData<List<String>>()


    fun set_to_db_rooms(rooms: rooms) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put("room_name", rooms.room_name)
        values.put("room_type", rooms.room_type)
        values.put("room_image", rooms.room_image)


        db.insert("ROOMS_DB", null, values)
        db.close()
        Log.d("database message :", "ROOMS_DB updated")
        roomsLiveData.postValue(rooms)

    }


    fun getAllRooms(): List<rooms?> {
        val db: SQLiteDatabase = readableDatabase
        val cursor = db.query(
            "ROOMS_DB",
            arrayOf("id", "room_name", "room_type", "room_image"),
            null,
            null,
            null,
            null,
            null
        )

        val RoomList = mutableListOf<rooms?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val roomName = it.getStringOrNull(it.getColumnIndex("room_name"))
                val room_type = it.getStringOrNull(it.getColumnIndex("room_type"))
                val room_image = it.getStringOrNull(it.getColumnIndex("room_image"))


                val room = id?.let { it1 ->
                    rooms(
                        roomName ?: "",
                        room_type ?: "",
                        room_image ?: "",
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
            "ROOMS_DB",
            arrayOf("id", "room_name", "room_type", "room_image"),
            null,
            null,
            null,
            null,
            null
        )

        val roomNamesList = mutableListOf<String>()

        cursor?.use {
            while (it.moveToNext()) {
                val roomName = it.getStringOrNull(it.getColumnIndex("room_name"))
                roomName?.let {
                    roomNamesList.add(roomName)
                }
            }
        }

        return roomNamesList.toList() // تبدیل لیست به لیست ثابت با استفاده از toList()
    }

    fun delete_from_db_rooms(id: Int?): Int {
        val db: SQLiteDatabase = writableDatabase
        return db.delete("ROOMS_DB", "id=?", arrayOf(id.toString()))
    }

    fun get_from_db_rooms(id: Int): rooms? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "ROOMS_DB",
            arrayOf("id", "room_name", "room_type", "room_image"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val rooms = rooms()
            rooms.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            rooms.room_type = cursor.getStringOrNull(cursor.getColumnIndex("room_type"))
            rooms.room_image = cursor.getStringOrNull(cursor.getColumnIndex("room_image"))

            roomsLiveData.postValue(rooms)
            return rooms
        }
        return null
    }

    fun getRoomByRoomNamePrefix(prefix: String): rooms? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_name LIKE ?"
        val selectionArgs = arrayOf("$prefix%")
        val cursor = db.query("ROOMS_DB", null, selection, selectionArgs, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            val room = rooms()
            room.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            room.room_type = cursor.getStringOrNull(cursor.getColumnIndex("room_type"))
            room.room_image = cursor.getStringOrNull(cursor.getColumnIndex("room_image"))
            room.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            return room
        }

        return null
    }
    fun updateRoomById(id: Int?, newRoom: rooms): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("room_name", newRoom.room_name)
        value.put("room_type", newRoom.room_type)
        value.put("room_image", newRoom.room_image)

        roomsLiveData.postValue(newRoom)

        return db.update("ROOMS_DB", value, "id=?", arrayOf(id.toString()))
    }


    fun clear_db_rooms() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("ROOMS_DB", null, null)
        db.close()
        Log.d("database message :", "ROOMS_DB cleared")
    }

    fun isEmptynetwork_tabale() :Boolean{

        val db:SQLiteDatabase=readableDatabase
        val countQuery="SELECT * FROM ROOMS_DB"
        val cursor: Cursor =db.rawQuery(countQuery,null)
        if (cursor.count==0){
            return true
        }else{
            return false
        }

    }

    fun checkRoomTypeExists(roomType: String): Boolean {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_type = ?"
        val selectionArgs = arrayOf(roomType)
        val cursor = db.query("ROOMS_DB", null, selection, selectionArgs, null, null, null)
        val roomTypeExists = cursor.count > 0
        cursor.close()
        return roomTypeExists
    }
    fun checkRoomNameExists(roomName: String): Boolean {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_name = ?"
        val selectionArgs = arrayOf(roomName)
        val cursor = db.query("ROOMS_DB", null, selection, selectionArgs, null, null, null)
        val roomNameExists = cursor.count > 0
        cursor.close()
        return roomNameExists
    }




    fun getRoomTypeCount(startsWith: String): Int {
        val db: SQLiteDatabase = readableDatabase
        val countQuery = "SELECT COUNT(*) FROM ROOMS_DB WHERE room_type LIKE '$startsWith%'"
        val cursor: Cursor = db.rawQuery(countQuery, null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        return if (count > 0) count else 0
    }




    companion object {
        private var instance: rooms_db? = null

        @Synchronized
        fun getInstance(context: Context): rooms_db {
            if (instance == null) {
                instance = rooms_db(context.applicationContext)
            }
            return instance!!
        }
    }

}

