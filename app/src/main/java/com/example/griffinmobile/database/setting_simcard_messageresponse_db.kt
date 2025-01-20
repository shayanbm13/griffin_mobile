package com.example.griffinmobile.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.example.griffinmobile.mudels.simcard_messageresponse

class setting_simcard_messageresponse_db private constructor(context: Context) : SQLiteOpenHelper(context, "SIMCARD_MESSAGERESPONSE_DB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_SIMCARD_MESSAGERESPONSE_DB = "CREATE TABLE SIMCARD_MESSAGERESPONSE_DB " +
                "(id INTEGER PRIMARY KEY, scenario_r TEXT, module_r TEXT, sensor_r TEXT, security_r TEXT)"



        db?.execSQL(CREATE_SIMCARD_MESSAGERESPONSE_DB)
        Log.d("database message :", "SIMCARD_MESSAGERESPONSE_DB created")




    }

    override fun onUpgrade(db: SQLiteDatabase?, oldversion: Int, newversion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS SIMCARD_MESSAGERESPONSE_DB")
        onCreate(db)
    }

    fun set_to_db_simcard_message_response(simcard_messageresponse: simcard_messageresponse) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put("scenario_r", simcard_messageresponse.scenario_r)
        values.put("module_r", simcard_messageresponse.module_r)
        values.put("sensor_r", simcard_messageresponse.sensor_r)
        values.put("security_r", simcard_messageresponse.security_r)
        values.put("id", simcard_messageresponse.id)

        db.insert("SIMCARD_MESSAGERESPONSE_DB", null, values)
        db.close()
        Log.d("database message:", "SIMCARD_MESSAGERESPONSE_DB updated")
        println("SIMCARD_MESSAGERESPONSE_DB updated")
    }

    fun get_from_db_simcard_message_response(id: Int): simcard_messageresponse? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "SIMCARD_MESSAGERESPONSE_DB",
            arrayOf("id", "scenario_r", "module_r","sensor_r" ,"security_r"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val simcard_messageresponse = simcard_messageresponse()
            simcard_messageresponse.scenario_r = cursor.getStringOrNull(cursor.getColumnIndex("scenario_r"))
            simcard_messageresponse.module_r = cursor.getStringOrNull(cursor.getColumnIndex("module_r"))
            simcard_messageresponse.sensor_r = cursor.getStringOrNull(cursor.getColumnIndex("sensor_r"))
            simcard_messageresponse.security_r = cursor.getStringOrNull(cursor.getColumnIndex("security_r"))
            simcard_messageresponse.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))

            return simcard_messageresponse
        }
        return null
    }

    fun update_db_simcard_message_response(simcard_messageresponse: simcard_messageresponse): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("scenario_r", simcard_messageresponse.scenario_r)
        value.put("module_r", simcard_messageresponse.module_r)
        value.put("sensor_r", simcard_messageresponse.sensor_r)
        value.put("security_r", simcard_messageresponse.security_r)
        value.put("id", simcard_messageresponse.id)

        return db.update("SIMCARD_MESSAGERESPONSE_DB", value, "id=?", arrayOf(simcard_messageresponse.id.toString()))
    }

    fun delete_from_db_simcard_message_response(id: Int): Int {
        val db: SQLiteDatabase = writableDatabase
        return db.delete("SIMCARD_MESSAGERESPONSE_DB", "id=?", arrayOf(id.toString()))
    }
    fun isEmpty_simcard_message_response_tabale() :Boolean {

        val db: SQLiteDatabase = readableDatabase
        val countQuery = "SELECT * FROM SIMCARD_MESSAGERESPONSE_DB"
        val cursor: Cursor = db.rawQuery(countQuery, null)
        if (cursor.count == 0) {
            return true
        } else {
            return false
        }
    }

    fun clear_db() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("SIMCARD_MESSAGERESPONSE_DB", null, null)
        db.close()
        Log.d("database message :", "SIMCARD_MESSAGERESPONSE_DB cleared")
    }
    fun isDatabaseValid(context: Context): Boolean {
        // مسیر پیش‌فرض دیتابیس‌های اپلیکیشن
        val dbFile = context.getDatabasePath("SIMCARD_MESSAGERESPONSE_DB")

        // بررسی وجود فایل دیتابیس
        if (!dbFile.exists()) {
            Log.d("database check", "Database does not exist.")
            return false
        }

        // بررسی خالی بودن جدول
        val db: SQLiteDatabase = SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READONLY)
        val countQuery = "SELECT COUNT(*) FROM SIMCARD_MESSAGERESPONSE_DB"
        val cursor = db.rawQuery(countQuery, null)
        var isValid = false

        if (cursor.moveToFirst()) {
            // چک کردن تعداد ردیف‌ها، اگر بیش از 0 باشد، دیتابیس معتبر است
            isValid = cursor.getInt(0) > 0
        }

        cursor.close()
        db.close()

        if (!isValid) {
            Log.d("database check", "Database exists but is empty.")
        }

        return isValid
    }

    companion object {
        private var instance: setting_simcard_messageresponse_db? = null

        @Synchronized
        fun getInstance(context: Context): setting_simcard_messageresponse_db {
            if (instance == null) {
                instance = setting_simcard_messageresponse_db(context.applicationContext)
            }
            return instance!!
        }
    }
}