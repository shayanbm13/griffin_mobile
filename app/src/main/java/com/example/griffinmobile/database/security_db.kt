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
import com.example.griffinmobile.mudels.security

class security_db private constructor(context: Context) : SQLiteOpenHelper(context, "SECURITY_DB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_security_DB_TABLE = "CREATE TABLE IF NOT EXISTS " + "SECURITY_DB" + "(id INTEGER PRIMARY KEY, alarm_duration TEXT , arm_active_deley TEXT , alarm_triger_deley TEXT , active_scenario TEXT , password_security TEXT )"





        db?.execSQL(CREATE_security_DB_TABLE)
        Log.d("database message :", "SECURITY_DB created")




    }

    override fun onUpgrade(db: SQLiteDatabase?, oldversion: Int, newversion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS SECURITY_DB")
        onCreate(db)
    }

    private val securityLiveData: MutableLiveData<security?> = MutableLiveData()




//####  security database  #########################################################################################

    fun set_to_db_security(security: security) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put("alarm_duration", security.alarm_duration)
        values.put("arm_active_deley", security.arm_active_deley)
        values.put("alarm_triger_deley", security.alarm_triger_deley)
        values.put("active_scenario", security.active_scenario)
        values.put("password_security", security.password_security)

        db.insert("SECURITY_DB", null, values)
        db.close()
        Log.d("database message :", "SECURITY_DB updated")
        securityLiveData.postValue(security)

    }

    fun get_from_db_security(id: Int): security? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "SECURITY_DB",
            arrayOf("id", "alarm_duration", "arm_active_deley", "alarm_triger_deley", "active_scenario", "password_security"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val security = security()
            security.alarm_duration = cursor.getStringOrNull(cursor.getColumnIndex("alarm_duration"))
            security.arm_active_deley = cursor.getStringOrNull(cursor.getColumnIndex("arm_active_deley"))
            security.alarm_triger_deley = cursor.getStringOrNull(cursor.getColumnIndex("alarm_triger_deley"))
            security.active_scenario = cursor.getStringOrNull(cursor.getColumnIndex("active_scenario"))
            security.password_security = cursor.getStringOrNull(cursor.getColumnIndex("password_security"))
            security.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            securityLiveData.postValue(security)
            return security
        }
        return null
    }

    fun update_db_security(security: security): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("alarm_duration", security.alarm_duration)
        value.put("arm_active_deley", security.arm_active_deley)
        value.put("alarm_triger_deley", security.alarm_triger_deley)
        value.put("active_scenario", security.active_scenario)
        value.put("password_security", security.password_security)
        securityLiveData.postValue(security)
        println(security.alarm_duration)

        return db.update("SECURITY_DB", value, "id=?", arrayOf(security.id.toString()))
    }

    fun clearsecurityTable() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("SECURITY_DB", null, null)
        db.close()
        Log.d("database message :", "SECURITY_DB cleared")
    }

    fun isEmptysecurity_tabale() :Boolean{

        val db: SQLiteDatabase =readableDatabase
        val countQuery="SELECT * FROM SECURITY_DB"
        val cursor: Cursor =db.rawQuery(countQuery,null)
        if (cursor.count==0){
            return true
        }else{
            return false
        }

    }




    fun getsecurityLiveData(): LiveData<security?> {
        return securityLiveData
    }

    fun get_from_db_security_livedata(id: Int) {
        val db: SQLiteDatabase = readableDatabase
        val cursor = db.query(
            "SECURITY_DB",
            arrayOf("id", "alarm_duration", "arm_active_deley", "alarm_triger_deley", "active_scenario", "password_security"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val security = security()
            security.alarm_duration = cursor.getStringOrNull(cursor.getColumnIndex("alarm_duration"))
            security.arm_active_deley = cursor.getStringOrNull(cursor.getColumnIndex("arm_active_deley"))
            security.alarm_triger_deley = cursor.getStringOrNull(cursor.getColumnIndex("alarm_triger_deley"))
            security.active_scenario = cursor.getStringOrNull(cursor.getColumnIndex("active_scenario"))
            security.password_security = cursor.getStringOrNull(cursor.getColumnIndex("password_security"))

            // مقدار securityLiveData را به‌روز کنید
            securityLiveData.postValue(security)
        }
    }









    companion object {
        private var instance: security_db? = null

        @Synchronized
        fun getInstance(context: Context): security_db {
            if (instance == null) {
                instance = security_db(context.applicationContext)
            }
            return instance!!
        }
    }
}
