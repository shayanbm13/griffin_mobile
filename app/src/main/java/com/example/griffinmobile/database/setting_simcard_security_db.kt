package com.example.griffinmobile.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.getStringOrNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.griffinmobile.mudels.network_manual
import com.example.griffinmobile.mudels.simcard_security

class setting_simcard_security_db private constructor(context: Context) : SQLiteOpenHelper(context, "SIMCARD_SECURITY_DB", null, 1) {


    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_SIMCARD_SECURITY_DB = "CREATE TABLE SIMCARD_SECURITY_DB " +
                "(id INTEGER PRIMARY KEY, username TEXT, password TEXT)"



        db?.execSQL(CREATE_SIMCARD_SECURITY_DB)
        Log.d("database message :", "SIMCARD_SECURITY_DB created")




    }

    override fun onUpgrade(db: SQLiteDatabase?, oldversion: Int, newversion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS SIMCARD_SECURITY_DB")
        onCreate(db)
    }

    private val simcard_securityLiveData: MutableLiveData<simcard_security?> = MutableLiveData()






    fun set_to_db_simcard_security(simcard_security: simcard_security) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put("username", simcard_security.username)
        values.put("password", simcard_security.password)

        db.insert("SIMCARD_SECURITY_DB", null, values)
        db.close()
        Log.d("database message:", "SIMCARD_SECURITY_DB updated")
        simcard_securityLiveData.postValue(simcard_security)

    }

    fun get_from_db_simcard_security(id: Int): simcard_security? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "SIMCARD_SECURITY_DB",
            arrayOf("id", "username", "password"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var simcard_security: simcard_security? = null

        cursor.use {
            if (it.moveToFirst()) { // moveToFirst() returns false if cursor is empty
                simcard_security = simcard_security().apply {
                    username = it.getStringOrNull(it.getColumnIndexOrThrow("username"))
                    password = it.getStringOrNull(it.getColumnIndexOrThrow("password"))
                }
            }
        }

        return simcard_security
    }    fun update_db_simcard_security(simcard_security: simcard_security): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("username", simcard_security.username)
        value.put("password", simcard_security.password)
        simcard_securityLiveData.postValue(simcard_security)

        return db.update("SIMCARD_SECURITY_DB", value, "id=?", arrayOf(simcard_security.id.toString()))
    }

    fun delete_from_db_simcard_security(id: Int): Int {
        val db: SQLiteDatabase = writableDatabase
        return db.delete("SIMCARD_SECURITY_DB", "id=?", arrayOf(id.toString()))
    }
    fun isEmpty_simcard_security_tabale() :Boolean {

        val db: SQLiteDatabase = readableDatabase
        val countQuery = "SELECT * FROM SIMCARD_SECURITY_DB"
        val cursor: Cursor = db.rawQuery(countQuery, null)
        if (cursor.count == 0) {
            return true
        } else {
            return false
        }
    }

    fun getsimcard_LiveData(): LiveData<simcard_security?> {
        return simcard_securityLiveData
    }

    fun clear_db() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("SIMCARD_SECURITY_DB", null, null)
        db.close()
        Log.d("database message :", "SIMCARD_ACCOUNTSECURITY_DB cleared")
    }

    companion object {
        private var instance: setting_simcard_security_db? = null

        @Synchronized
        fun getInstance(context: Context): setting_simcard_security_db {
            if (instance == null) {
                instance = setting_simcard_security_db(context.applicationContext)
            }
            return instance!!
        }
    }
}
