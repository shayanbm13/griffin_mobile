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
import com.example.griffinmobile.mudels.simcard_accountsecurity
import com.example.griffinmobile.mudels.simcard_security

class setting_simcard_accountsecurity_db private constructor(context: Context) : SQLiteOpenHelper(context, "SIMCARD_ACCOUNTSECURITY_DB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_SIMCARD_ACCOUNTSECURITY_DB = "CREATE TABLE SIMCARD_ACCOUNTSECURITY_DB " +
                "(id INTEGER PRIMARY KEY, smsanswer_on_off TEXT, admin_number TEXT, backup_number1 TEXT, backup_number2 TEXT, backup_number3 TEXT, backup_number4 TEXT)"

        db?.execSQL(CREATE_SIMCARD_ACCOUNTSECURITY_DB)
        Log.d("database message :", "SIMCARD_ACCOUNTSECURITY_DB created")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldversion: Int, newversion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS SIMCARD_ACCOUNTSECURITY_DB")
        onCreate(db)
    }

    private val simcard_accountsecurityLiveData: MutableLiveData<simcard_accountsecurity?> = MutableLiveData()


    fun set_to_db_simcard_accountsecurity(simcard_accountsecurity: simcard_accountsecurity) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put("smsanswer_on_off", simcard_accountsecurity.smsanswer_on_off)
        values.put("admin_number", simcard_accountsecurity.admin_number)
        values.put("backup_number1", simcard_accountsecurity.backup_number1)
        values.put("backup_number2", simcard_accountsecurity.backup_number2)
        values.put("backup_number3", simcard_accountsecurity.backup_number3)
        values.put("backup_number4", simcard_accountsecurity.backup_number4)

        db.insert("SIMCARD_ACCOUNTSECURITY_DB", null, values)
        db.close()
        Log.d("database message:", "SIMCARD_ACCOUNTSECURITY_DB updated")
        simcard_accountsecurityLiveData.postValue(simcard_accountsecurity)
    }

    fun get_from_db_simcard_accountsecurity(id: Int): simcard_accountsecurity? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "SIMCARD_ACCOUNTSECURITY_DB",
            arrayOf("id", "smsanswer_on_off", "admin_number", "backup_number1", "backup_number2", "backup_number3", "backup_number4"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var simcard_accountsecurity: simcard_accountsecurity? = null

        cursor.use {
            if (it.moveToFirst()) { // moveToFirst() returns false if the cursor is empty
                simcard_accountsecurity = simcard_accountsecurity().apply {
                    smsanswer_on_off = it.getStringOrNull(it.getColumnIndexOrThrow("smsanswer_on_off"))
                    admin_number = it.getStringOrNull(it.getColumnIndexOrThrow("admin_number"))
                    backup_number1 = it.getStringOrNull(it.getColumnIndexOrThrow("backup_number1"))
                    backup_number2 = it.getStringOrNull(it.getColumnIndexOrThrow("backup_number2"))
                    backup_number3 = it.getStringOrNull(it.getColumnIndexOrThrow("backup_number3"))
                    backup_number4 = it.getStringOrNull(it.getColumnIndexOrThrow("backup_number4"))
                }
                simcard_accountsecurityLiveData.postValue(simcard_accountsecurity)
            }
        }

        return simcard_accountsecurity
    }
    fun update_db_simcard_accountsecurity(simcard_accountsecurity: simcard_accountsecurity): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("smsanswer_on_off", simcard_accountsecurity.smsanswer_on_off)
        value.put("admin_number", simcard_accountsecurity.admin_number)
        value.put("backup_number1", simcard_accountsecurity.backup_number1)
        value.put("backup_number2", simcard_accountsecurity.backup_number2)
        value.put("backup_number3", simcard_accountsecurity.backup_number3)
        value.put("backup_number4", simcard_accountsecurity.backup_number4)
        simcard_accountsecurityLiveData.postValue(simcard_accountsecurity)
        return db.update("SIMCARD_ACCOUNTSECURITY_DB", value, "id=?", arrayOf(simcard_accountsecurity.id.toString()))


    }

    fun delete_from_db_simcard_accountsecurity(id: Int): Int {
        val db: SQLiteDatabase = writableDatabase
        return db.delete("SIMCARD_ACCOUNTSECURITY_DB", "id=?", arrayOf(id.toString()))
    }
    fun isEmpty_simcard_accountsecurity_tabale() :Boolean {

        val db: SQLiteDatabase = readableDatabase
        val countQuery = "SELECT * FROM SIMCARD_ACCOUNTSECURITY_DB"
        val cursor: Cursor = db.rawQuery(countQuery, null)

        if (cursor.count == 0) {
            return true
        } else {
            return false
        }
    }

    fun getsimcard_accountsecurity_LiveData(): LiveData<simcard_accountsecurity?> {
        return simcard_accountsecurityLiveData
    }

    fun clear_db() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("SIMCARD_ACCOUNTSECURITY_DB", null, null)
        db.close()
        Log.d("database message :", "SIMCARD_ACCOUNTSECURITY_DB cleared")
    }

    companion object {
        private var instance: setting_simcard_accountsecurity_db? = null

        @Synchronized
        fun getInstance(context: Context): setting_simcard_accountsecurity_db {
            if (instance == null) {
                instance = setting_simcard_accountsecurity_db(context.applicationContext)
            }
            return instance!!
        }
    }
}