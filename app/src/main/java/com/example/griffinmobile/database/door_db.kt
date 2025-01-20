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
import com.example.griffinmobile.mudels.DOOR


class door_db private constructor(context: Context) : SQLiteOpenHelper(context, "DOOR_DB", null, 1) {


    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_DOOR_DB = "CREATE TABLE DOOR_DB " +
                "(id INTEGER PRIMARY KEY, type TEXT, sub_type TEXT , URL TEXT, name TEXT, status TEXT, mac TEXT, ip TEXT)"



        db?.execSQL(CREATE_DOOR_DB)
        Log.d("database message :", "DOOR_DB created")




    }


    override fun onUpgrade(db: SQLiteDatabase?, oldversion: Int, newversion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS DOOR_DB")
        onCreate(db)
    }

    private val DOORLiveData: MutableLiveData<DOOR?> = MutableLiveData()


    fun clear_db_DOOR() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("DOOR_DB", null, null)
        db.close()
        Log.d("database message :", "door_DB cleared")
    }



    fun set_to_db_DOOR(DOOR: DOOR) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put("type", DOOR.type)
        values.put("sub_type", DOOR.sub_type)
        values.put("URL", DOOR.URL)
        values.put("name", DOOR.name)
        values.put("ip", DOOR.ip)
        values.put("mac", DOOR.mac)
        values.put("status", DOOR.status)

        db.insert("DOOR_DB", null, values)
        db.close()
        Log.d("database message:", "DOOR_DB updated")
        DOORLiveData.postValue(DOOR)

    }

    fun get_from_db_DOOR(id: Int): DOOR? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "DOOR_DB",
            arrayOf("id", "type", "sub_type" , "URL", "name", "status", "mac", "ip"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val DOOR = DOOR()
            DOOR.type = cursor.getStringOrNull(cursor.getColumnIndex("type"))
            DOOR.sub_type = cursor.getStringOrNull(cursor.getColumnIndex("sub_type"))
            DOOR.URL = cursor.getStringOrNull(cursor.getColumnIndex("URL"))
            DOOR.name = cursor.getStringOrNull(cursor.getColumnIndex("name"))
            DOOR.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
            DOOR.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            DOOR.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            DOORLiveData.postValue(DOOR)

            return DOOR
        }
        return null
    }

    fun update_db_DOOR(DOOR: DOOR): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("type", DOOR.type)
        value.put("sub_type", DOOR.sub_type)
        value.put("URL", DOOR.URL)
        value.put("name", DOOR.name)
        value.put("status", DOOR.status)
        value.put("mac", DOOR.mac)
        value.put("ip", DOOR.ip)
        DOORLiveData.postValue(DOOR)

        return db.update("DOOR_DB", value, "id=?", arrayOf(DOOR.id.toString()))
    }

    fun delete_from_db_DOOR(id: Int): Int {
        val db: SQLiteDatabase = writableDatabase
        return db.delete("DOOR_DB", "id=?", arrayOf(id.toString()))
    }

    fun getDoorsByMacAddress(mac: String?): List<DOOR?> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "mac = ?"
        val selectionArgs = arrayOf(mac)
        val cursor = db.query(
            "DOOR_DB",
            arrayOf("id", "type", "sub_type", "URL", "name", "status", "mac", "ip"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val doorList = mutableListOf<DOOR?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val type = it.getStringOrNull(it.getColumnIndex("type"))
                val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                val URL = it.getStringOrNull(it.getColumnIndex("URL"))
                val name = it.getStringOrNull(it.getColumnIndex("name"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val macAddress = it.getStringOrNull(it.getColumnIndex("mac"))

                val door = DOOR(
                    type ?: "",
                    subType?: "",
                    ip?: "",
                     macAddress?: "",
                    status?: "",
                    URL?: "",
                    name?: "",
                     id
                )
                doorList.add(door)
            }
        }

        return doorList
    }

    fun isEmpty_DOOR_tabale() :Boolean {

        val db: SQLiteDatabase = readableDatabase
        val countQuery = "SELECT * FROM DOOR_DB"
        val cursor: Cursor = db.rawQuery(countQuery, null)
        if (cursor.count == 0) {
            return true
        } else {
            return false
        }
    }

    fun getDOOR_LiveData(): LiveData<DOOR?> {
        return DOORLiveData
    }



    companion object {
        private var instance: door_db? = null

        @Synchronized
        fun getInstance(context: Context): door_db {
            if (instance == null) {
                instance = door_db(context.applicationContext)
            }
            return instance!!
        }
    }
}
