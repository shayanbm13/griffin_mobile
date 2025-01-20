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
import com.example.griffinmobile.mudels.Master_slave

class Master_slave_db private constructor(context: Context) : SQLiteOpenHelper(context,"Master_slave_db",null,1){
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_MASTER_SLAVE_DB="CREATE TABLE IF NOT EXISTS "+ "Master_slave_db"+"(id INTEGER PRIMARY KEY ,  status TEXT )"

        db!!.execSQL(CREATE_MASTER_SLAVE_DB)
        Log.d("database message :" ,"Master_slave_db created")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS Master_slave_db")
        onCreate(db)
    }

    fun set_to_db_Master_slave(Master_slave: Master_slave){
        val db=writableDatabase
        val values= ContentValues()


        values.put("status",Master_slave.status)

        db.insert("Master_slave_db",null,values)
        db.close()
        Log.d("database message :", "Master_slave_db updated")

    }

    fun updateStatusbyId(id:Int?,newStatus:String){
        val db=writableDatabase
        val values= ContentValues()
        try {
            values.put("status",newStatus)
            db.update("Master_slave_db",values, "id=?", arrayOf(id.toString()))


        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }
    fun updateMaster_slavebyId(id:Int?,ip:String){
        val db=writableDatabase
        val values= ContentValues()
        try {
            values.put("ip",ip)
            db.update("Master_slave_db",values, "id=?", arrayOf(id.toString()))


        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }
    fun updateStatusAndIpById(id: Int?,newStatus: String?){
        val db=writableDatabase
        val values= ContentValues()
        try {
            values.put("status",newStatus)

            db.update("Master_slave_db",values,"id=?", arrayOf(id.toString()))
        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }


    fun getMaster_slaveByCname(Fname: String?): Master_slave? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "Fname = ?"
        val selectionArgs = arrayOf(Fname)
        val cursor = db.query(
            "Master_slave_db",
            arrayOf("id",  "status"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val Master_slave = Master_slave()
            Master_slave.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))


            Master_slave.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))



            return Master_slave
        }

        return null
    }









    fun get_from_db_Master_slave(id: Int?): Master_slave? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "Master_slave_db",
            arrayOf("id" , "status"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val Master_slave = Master_slave()
            Master_slave.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            Master_slave.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
//            thermoststLiveData.postValue(Master_slave)
            return Master_slave
        }
        db.close()
        return null
    }




    fun getAllMaster_slaves(): List<Master_slave> {
        val db: SQLiteDatabase = readableDatabase
        val Master_slaveList = mutableListOf<Master_slave>()

        val cursor = db.query(
            "Master_slave_db",
            null,
            null,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            while (cursor.moveToNext()) {
                val Master_slave = Master_slave()
                Master_slave.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
                Master_slave.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
                Master_slaveList.add(Master_slave)
            }
        }

        return Master_slaveList
    }





    fun delete_from_db_Master_slave(id: Int?): Int {
        val db: SQLiteDatabase = writableDatabase
        return db.delete("Master_slave_db", "id=?", arrayOf(id.toString()))
    }

    fun updateMaster_slaveById(id: Int?, Master_slave: Master_slave): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("status", Master_slave.status)
        println("Master_slave_db updated" )
        return db.update("Master_slave_db", value, "id=?", arrayOf(id.toString()))
    }



    fun getStatusById(id: Int?): String? {
        val db: SQLiteDatabase = readableDatabase
        var status: String? = null

        try {
            val selection = "id = ?"
            val selectionArgs = arrayOf(id.toString())
            val cursor = db.query(
                "Master_slave_db",
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



    companion object {
        private var instance: Master_slave_db? = null

        @Synchronized
        fun getInstance(context: Context): Master_slave_db {
            if (instance == null) {
                instance = Master_slave_db(context.applicationContext)
            }
            return instance!!
        }
    }

}