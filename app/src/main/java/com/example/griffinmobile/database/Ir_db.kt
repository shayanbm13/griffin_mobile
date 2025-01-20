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
import com.example.griffinmobile.mudels.IR


class Ir_db private constructor(context: Context) : SQLiteOpenHelper(context, "Ir_db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_Ir_db_TABLE =
            "CREATE TABLE IF NOT EXISTS " + "Ir_db" + "(id INTEGER PRIMARY KEY,   sub_type TEXT ,  cmd TEXT  ,  ip TEXT  ,  mac TEXT ,name TEXT,type TEXT )"

        db?.execSQL(CREATE_Ir_db_TABLE)
        Log.d("database message :", "Ir_db created")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldversion: Int, newversion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Ir_db")
        onCreate(db)
    }

   
   
    
   
    fun set_to_db_Ir(Ir: IR) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        
        values.put("sub_type", Ir.sub_type)
        values.put("cmd", Ir.cmd)
        values.put("ip", Ir.ip)
        values.put("mac", Ir.mac)

        values.put("name", Ir.name)
        values.put("type", Ir.type)


        db.insert("Ir_db", null, values)
        db.close()
        Log.d("database message :", "Ir_db updated")


    }

    fun updateIpById(id: Int?, newIp: String) {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("ip", newIp)

        db.update("Ir_db", value, "id=?", arrayOf(id.toString()))
        db.close()
    }

    fun updatecmdById(id: Int?, newcmd: String) {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        try {
            value.put("cmd", newcmd)
            db.update("Ir_db", value, "id=?", arrayOf(id.toString()))
            println("db updated")

        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }


    

   

    fun updatecmdAndIpById(id: Int?,newcmd: String?,newIp:String?){
        val db=writableDatabase
        val values= ContentValues()
        try {
            values.put("cmd",newcmd)
            values.put("ip",newIp)
            db.update("Ir_db",values,"id=?", arrayOf(id.toString()))
        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }

    fun delete_from_db_Ir(id: Int?): Int {
        val db: SQLiteDatabase = writableDatabase
        return db.delete("Ir_db", "id=?", arrayOf(id.toString()))
    }

    fun get_from_db_Ir(id: Int?): IR? {

        try {
            val db: SQLiteDatabase = writableDatabase
            val cursor = db.query(
                "Ir_db",
                arrayOf("id", "sub_type", "cmd","ip","mac","name","type"),
                "id=?",
                arrayOf(id.toString()),
                null,
                null,
                null
            )
            if (cursor != null) {
                cursor.moveToFirst()
                val Ir = IR()
        
                Ir.sub_type = cursor.getStringOrNull(cursor.getColumnIndex("sub_type"))
                Ir.cmd = cursor.getStringOrNull(cursor.getColumnIndex("cmd"))
                Ir.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
                Ir.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))

                Ir.name = cursor.getStringOrNull(cursor.getColumnIndex("name"))
                Ir.type = cursor.getStringOrNull(cursor.getColumnIndex("type"))
                Ir.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))


                return Ir
            }
            db.close()
            return null


        }catch (e:Exception){
            return null

            println(e)
        }

    }

    
    fun getAllIrs(): List<IR?> {
        val db: SQLiteDatabase = readableDatabase
        val cursor = db.query(
            "Ir_db",
            arrayOf("id", "sub_type", "cmd", "ip", "mac", "name", "type"),
            null,
            null,
            null,
            null,
            null
        )

        val IrList = mutableListOf<IR?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
               
                val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                val cmd = it.getStringOrNull(it.getColumnIndex("cmd"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))

                val name = it.getStringOrNull(it.getColumnIndex("name"))
                val type = it.getStringOrNull(it.getColumnIndex("type"))

                val Ir = IR(

                    subType ?: "",
                    mac ?: "",
                    ip ?: "",
                    cmd ?: "",
                    name ?: "",type ?: "",
                    id
                )
                IrList.add(Ir)
            }
        }

        return IrList.toList()
    }
   



   
    fun getIrsByMacAddress(mac: String?): List<IR?> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "mac = ?"
        val selectionArgs = arrayOf(mac)
        val cursor = db.query(
            "Ir_db",
            arrayOf("id", "sub_type", "cmd", "ip", "mac", "name", "type"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val IrList = mutableListOf<IR?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
            
                val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                val cmd = it.getStringOrNull(it.getColumnIndex("cmd"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val macAddress = it.getStringOrNull(it.getColumnIndex("mac"))

                val name = it.getStringOrNull(it.getColumnIndex("name"))
                val type = it.getStringOrNull(it.getColumnIndex("type"))

                val Ir = IR(

                    subType ?: "",
                    cmd ?: "",
                    ip ?: "",
                    macAddress ?: "",

                    name ?: "",
                    type ?: "",
                    id
                )
                IrList.add(Ir)
            }
        }

        return IrList
    }




    
    fun getIrsByid(id: String?): IR {
        try {
            val db: SQLiteDatabase = readableDatabase

            // دیگر کدهای شما اینجا قرار می‌گیرد...

            val selection = "id = ?"
            val selectionArgs = arrayOf(id)
            val cursor = db.query(
                "Ir_db",
                arrayOf("id", "sub_type", "cmd", "ip", "mac", "name", "type"),
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            var Ir = IR()

            cursor?.use {
                while (it.moveToNext()) {
                    val id = it.getIntOrNull(it.getColumnIndex("id"))
                    
                    val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                    val cmd = it.getStringOrNull(it.getColumnIndex("cmd"))
                    val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                    val macAddress = it.getStringOrNull(it.getColumnIndex("mac"))

                    val name = it.getStringOrNull(it.getColumnIndex("name"))
                    val type = it.getStringOrNull(it.getColumnIndex("type"))

                    Ir = IR(

                        subType ?: "",
                        cmd ?: "",
                        ip ?: "",
                        macAddress ?: "",

                        name ?: "",
                        type ?: "",
                        id
                    )
                }
            }
            return Ir
        } catch (e: Exception) {
            // مدیریت خطاها در اینجا
            e.printStackTrace()
            return IR() // یا مقدار پیش‌فرض دیگری
        }
    }


    fun getcmdById(id: Int?): String? {
        val db: SQLiteDatabase = readableDatabase
        var cmd: String? = null

        try {
            val selection = "id = ?"
            val selectionArgs = arrayOf(id.toString())
            val cursor = db.query(
                "Ir_db",
                arrayOf("cmd"),
                selection,
                selectionArgs,
                null,
                null,
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    cmd = it.getStringOrNull(it.getColumnIndex("cmd"))
                }
            }
        } catch (e: Exception) {
            // Handle the exception appropriately, e.g., log it.
        } finally {
            db.close()
        }

        return cmd
    }


    
    
    fun updateIrById(id: Int?, Ir: IR): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
      
        value.put("sub_type", Ir.sub_type)
        value.put("cmd", Ir.cmd)
        value.put("ip", Ir.ip)
        value.put("mac", Ir.mac)

        value.put("name", Ir.name)
        value.put("type", Ir.type)

        println("Ir_db updated" )

        return db.update("Ir_db", value, "id=?", arrayOf(id.toString()))
    }


    fun clear_db_Ir() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("Ir_db", null, null)
        db.close()
        Log.d("database message :", "Ir_db cleared")
    }

   
    
 
    companion object {
        private var instance: Ir_db? = null

        @Synchronized
        fun getInstance(context: Context): Ir_db {
            if (instance == null) {
                instance = Ir_db(context.applicationContext)
            }
            return instance!!
        }
    }

}