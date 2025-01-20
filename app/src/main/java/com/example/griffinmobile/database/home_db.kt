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
import com.example.griffinmobile.modules.home

class home_db private constructor(context: Context) : SQLiteOpenHelper(context, "HOME_DB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_HOME_DB_TABLE =
            "CREATE TABLE IF NOT EXISTS " + "HOME_DB" + "(id INTEGER PRIMARY KEY,  tag TEXT,  home_name TEXT ,  location TEXT,  current_select TEXT  )"

        db?.execSQL(CREATE_HOME_DB_TABLE)
        Log.d("database message :", "HOME_DB created")


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldversion: Int, newversion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS HOME_DB")
        onCreate(db)
    }
   
    private val homeLiveData: MutableLiveData<home?> = MutableLiveData()
    private val tagsLiveData = MutableLiveData<List<String>>()

    fun getSelectedHome(): home? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "current_select = ?"
        val selectionArgs = arrayOf("true")

        val cursor = db.query(
            "HOME_DB",
            arrayOf("id", "tag", "home_name", "location", "current_select"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var selectedHome: home? = null
        cursor?.use {
            if (it.moveToFirst()) {
                selectedHome = home(
                    id = it.getIntOrNull(it.getColumnIndex("id")) ?: 0,
                    tag = it.getStringOrNull(it.getColumnIndex("tag")) ?: "",
                    home_name = it.getStringOrNull(it.getColumnIndex("home_name")) ?: "",
                    location = it.getStringOrNull(it.getColumnIndex("location")) ?: "",
                    current_select = it.getStringOrNull(it.getColumnIndex("current_select")) ?: ""
                )
            }
        }
        return selectedHome
    }
    fun set_to_db_home(home: home) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put("tag", home.tag)
        values.put("home_name", home.home_name)
        values.put("location", home.location)
        values.put("current_select", home.current_select)


        db.insert("HOME_DB", null, values)
        db.close()
        Log.d("database message :", "HOME_DB updated")
        homeLiveData.postValue(home)

    }

    fun updateCurrentSelectById(id: Int) {
        val db: SQLiteDatabase = writableDatabase
        db.beginTransaction()
        try {
            // Set current_select to false for all items
            val resetValues = ContentValues().apply {
                put("current_select", "false")
            }
            db.update("HOME_DB", resetValues, null, null)

            // Set current_select to true for the specified item
            val updateValues = ContentValues().apply {
                put("current_select", "true")
            }
            db.update("HOME_DB", updateValues, "id=?", arrayOf(id.toString()))

            db.setTransactionSuccessful()
            Log.d("database message :", "Current select updated for ID: $id")
        } catch (e: Exception) {
            Log.e("database error :", "Error updating current_select", e)
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun getAllhome(): List<home?> {
        val db: SQLiteDatabase = readableDatabase
        val cursor = db.query(
            "HOME_DB",
            arrayOf("id", "tag", "home_name", "location", "current_select"),
            null,
            null,
            null,
            null,
            null
        )

        val RoomList = mutableListOf<home?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val tag = it.getStringOrNull(it.getColumnIndex("tag"))
                val home_name = it.getStringOrNull(it.getColumnIndex("home_name"))
                val location = it.getStringOrNull(it.getColumnIndex("location"))
                val current_select = it.getStringOrNull(it.getColumnIndex("current_select"))


                val room = id?.let { it1 ->
                    home(
                        home_name ?: "",
                        tag ?: "",
                        location ?: "",
                        current_select ?: "",
                        it1,

                        )
                }
                RoomList.add(room)
            }
        }

        return RoomList.toList()
    }




    fun getAlltags(): List<String> {
        val db: SQLiteDatabase = readableDatabase
        val cursor = db.query(
            "HOME_DB",
            arrayOf("id", "tag", "home_name", "location", "current_select"),
            null,
            null,
            null,
            null,
            null
        )

        val tagsList = mutableListOf<String>()

        cursor?.use {
            while (it.moveToNext()) {
                val tag = it.getStringOrNull(it.getColumnIndex("tag"))
                tag?.let {
                    tagsList.add(tag)
                }
            }
        }

        return tagsList.toList() // تبدیل لیست به لیست ثابت با استفاده از toList()
    }

    fun delete_from_db_home(id: Int?): Int {
        val db: SQLiteDatabase = writableDatabase
        return db.delete("HOME_DB", "id=?", arrayOf(id.toString()))
    }

    fun get_from_db_home(id: Int): home? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "HOME_DB",
            arrayOf("id", "tag", "home_name", "location", "current_select"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val home = home()
            home.tag = cursor.getStringOrNull(cursor.getColumnIndex("tag"))
            home.home_name = cursor.getStringOrNull(cursor.getColumnIndex("home_name"))
            home.location = cursor.getStringOrNull(cursor.getColumnIndex("location"))
            home.current_select = cursor.getStringOrNull(cursor.getColumnIndex("current_select"))

            homeLiveData.postValue(home)
            return home
        }
        return null
    }


    fun getRoomByRoomIdAndHome(roomId: String, location: String): home? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "tag = ? AND location = ?"
        val selectionArgs = arrayOf(roomId, location)

        val cursor = db.query(
            "HOME_DB",
            arrayOf("id", "tag", "home_name", "location", "current_select"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var room: home? = null
        cursor?.use {
            if (it.moveToFirst()) {
                room = home(
                    tag = it.getStringOrNull(it.getColumnIndex("tag")) ?: "",
                    home_name = it.getStringOrNull(it.getColumnIndex("home_name")) ?: "",
                    location = it.getStringOrNull(it.getColumnIndex("location")) ?: "",
                    current_select = it.getStringOrNull(it.getColumnIndex("current_select")) ?: "",
                    id = it.getIntOrNull(it.getColumnIndex("id")) ?: 0
                )
            }
        }
        return room
    }

    fun getRoomBytagPrefix(prefix: String): home? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "tag LIKE ?"
        val selectionArgs = arrayOf("$prefix%")
        val cursor = db.query("HOME_DB", null, selection, selectionArgs, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            val room = home()
            room.tag = cursor.getStringOrNull(cursor.getColumnIndex("tag"))
            room.home_name = cursor.getStringOrNull(cursor.getColumnIndex("home_name"))
            room.location = cursor.getStringOrNull(cursor.getColumnIndex("location"))
            room.current_select = cursor.getStringOrNull(cursor.getColumnIndex("current_select"))
            room.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            return room
        }

        return null
    }
    fun updateRoomById(id: Int?, newRoom: home): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("tag", newRoom.tag)
        value.put("home_name", newRoom.home_name)
        value.put("location", newRoom.location)
        value.put("current_select", newRoom.current_select)

        homeLiveData.postValue(newRoom)

        return db.update("HOME_DB", value, "id=?", arrayOf(id.toString()))
    }
    fun getHomeCount(): Int {
        val db: SQLiteDatabase = readableDatabase
        val countQuery = "SELECT COUNT(*) FROM HOME_DB"
        val cursor: Cursor = db.rawQuery(countQuery, null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }

    fun clear_db_home() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("HOME_DB", null, null)
        db.close()
        Log.d("database message :", "HOME_DB cleared")
    }

    fun isEmptynetwork_tabale() :Boolean{

        val db: SQLiteDatabase =readableDatabase
        val countQuery="SELECT * FROM HOME_DB"
        val cursor: Cursor =db.rawQuery(countQuery,null)
        if (cursor.count==0){
            return true
        }else{
            return false
        }

    }

    fun checkRoomTypeExists(roomType: String): Boolean {
        val db: SQLiteDatabase = readableDatabase
        val selection = "home_name = ?"
        val selectionArgs = arrayOf(roomType)
        val cursor = db.query("HOME_DB", null, selection, selectionArgs, null, null, null)
        val roomTypeExists = cursor.count > 0
        cursor.close()
        return roomTypeExists
    }
    fun checktagExists(tag: String): Boolean {
        val db: SQLiteDatabase = readableDatabase
        val selection = "tag = ?"
        val selectionArgs = arrayOf(tag)
        val cursor = db.query("HOME_DB", null, selection, selectionArgs, null, null, null)
        val tagExists = cursor.count > 0
        cursor.close()
        return tagExists
    }




    fun getRoomTypeCount(startsWith: String): Int {
        val db: SQLiteDatabase = readableDatabase
        val countQuery = "SELECT COUNT(*) FROM HOME_DB WHERE home_name LIKE '$startsWith%'"
        val cursor: Cursor = db.rawQuery(countQuery, null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        return if (count > 0) count else 0
    }




    companion object {
        private var instance: home_db? = null

        @Synchronized
        fun getInstance(context: Context): home_db {
            if (instance == null) {
                instance = home_db(context.applicationContext)
            }
            return instance!!
        }
    }

}

