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
import com.example.griffinmobile.mudels.Elevator

class Elevator_db private constructor(context: Context) : SQLiteOpenHelper(context,"Elevator_DB",null,1){
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_Elevator_DB="CREATE TABLE IF NOT EXISTS "+ "Elevator_DB"+"(id INTEGER PRIMARY KEY ,  status TEXT, ip TEXT, mac TEXT )"

        db!!.execSQL(CREATE_Elevator_DB)
        Log.d("database message :" ,"Elevator_db created")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS Elevator_db")
        onCreate(db)
    }

    fun set_to_db_Elevator(Elevator: Elevator){
        val db=writableDatabase
        val values= ContentValues()

        values.put("ip",Elevator.ip)
        values.put("mac",Elevator.mac)
        values.put("status",Elevator.status)

        db.insert("Elevator_db",null,values)
        db.close()
        Log.d("database message :", "Elevator_db updated")

    }

    fun updateStatusbyId(id:Int?,newStatus:String){
        val db=writableDatabase
        val values= ContentValues()
        try {
            values.put("status",newStatus)
            db.update("Elevator_DB",values, "id=?", arrayOf(id.toString()))


        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }
    fun updateElevatorbyId(id:Int?,ip:String){
        val db=writableDatabase
        val values= ContentValues()
        try {
            values.put("ip",ip)
            db.update("Elevator_DB",values, "id=?", arrayOf(id.toString()))


        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }
    fun updateStatusAndIpById(id: Int?,newStatus: String?,newIp:String?){
        val db=writableDatabase
        val values= ContentValues()
        try {
            values.put("status",newStatus)
            values.put("ip",newIp)
            db.update("Elevator_DB",values,"id=?", arrayOf(id.toString()))
        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }


    fun getElevatorByCname(Fname: String?): Elevator? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "Fname = ?"
        val selectionArgs = arrayOf(Fname)
        val cursor = db.query(
            "Elevator_DB",
            arrayOf("id",  "status", "ip", "mac"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val Elevator = Elevator()
            Elevator.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))

            Elevator.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            Elevator.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
            Elevator.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))


            return Elevator
        }

        return null
    }


    fun getElevatorsByMacAddress(mac: String?): List<Elevator?> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "mac = ?"
        val selectionArgs = arrayOf(mac)
        val cursor = db.query(
            "Elevator_DB",
            arrayOf("id", "status", "ip", "mac"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val ElevatorList = mutableListOf<Elevator?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))

                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val macAddress = it.getStringOrNull(it.getColumnIndex("mac"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))


                val Elevator = Elevator(



                    ip ?: "",
                    macAddress ?: "",
                    status ?: "",

                    id
                )
                ElevatorList.add(Elevator)
            }
        }

        return ElevatorList
    }


    fun getElevatorsWithNonEmptyMacByRoomName(roomName: String?): LiveData<List<Elevator?>> {
        val ElevatorListLiveData = MutableLiveData<List<Elevator?>>()

        val db: SQLiteDatabase = readableDatabase
        val selection = "mac IS NOT NULL AND mac != '' AND room_name LIKE ?"
        val selectionArgs = arrayOf("$roomName%")
        val cursor = db.query(
            "Elevator_DB",
            arrayOf("id", "ip", "status", "mac"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val ElevatorList = mutableListOf<Elevator?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))

                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))


                val Elevator = Elevator(

                    ip ?: "",
                    mac ?: "",
                    status ?: "",

                    id
                )
                ElevatorList.add(Elevator)
            }
        }

        ElevatorListLiveData.postValue(ElevatorList)
        return ElevatorListLiveData
    }


    fun getElevatorsWithNonEmptyMacByRoomName2(roomName: String?): List<List<Elevator?>> {
        val ElevatorListLiveData = mutableListOf<List<Elevator?>>()

        val db: SQLiteDatabase = readableDatabase
        val selection = "mac IS NOT NULL AND mac != '' AND room_name LIKE ?"
        val selectionArgs = arrayOf("$roomName%")
        val cursor = db.query(
            "Elevator_DB",
            arrayOf("id", "ip", "mac", "status"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val ElevatorList = mutableListOf<Elevator?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))

                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))


                val Elevator = Elevator(

                    ip ?: "",
                    mac ?: "",
                    status ?: "",

                    id
                )
                ElevatorList.add(Elevator)
            }
        }

        ElevatorListLiveData.add(ElevatorList)
        return ElevatorListLiveData
    }



    fun get_from_db_Elevator(id: Int?): Elevator? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "Elevator_DB",
            arrayOf("id" ,"ip", "mac", "status"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val Elevator = Elevator()
            Elevator.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))

            Elevator.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            Elevator.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            Elevator.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))


//            thermoststLiveData.postValue(Elevator)
            return Elevator
        }
        db.close()
        return null
    }


    fun get_from_db_Elevator_By_name(name: String?): Elevator? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "Elevator_DB",
            arrayOf("id", "ip", "mac", "status"),
            "Fname=?",
            arrayOf(name.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val Elevator = Elevator()
            Elevator.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            Elevator.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            Elevator.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            Elevator.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))


//            thermoststLiveData.postValue(Elevator)
            return Elevator
        }
        return null
    }

    fun updateNameById(id: Int?, Fname: String?): Int {
        val db: SQLiteDatabase = writableDatabase
        try {
            val value = ContentValues()
            value.put("Fname", Fname)


            return db.update("Elevator_DB", value, "id=?", arrayOf(id.toString()))
        } catch (e: Exception) {
            println(e)
            return 0
        } finally {
            db.close()
        }
    }

    fun getAllElevators(): List<Elevator> {
        val db: SQLiteDatabase = readableDatabase
        val ElevatorList = mutableListOf<Elevator>()

        val cursor = db.query(
            "Elevator_DB",
            null,
            null,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            while (cursor.moveToNext()) {
                val Elevator = Elevator()
                Elevator.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))

                Elevator.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
                Elevator.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
                Elevator.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))


                ElevatorList.add(Elevator)
            }
        }

        return ElevatorList
    }


    fun getElevatorByRoomName(room_name: String?): Elevator? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_name = ?"
        val selectionArgs = arrayOf(room_name)
        val cursor = db.query(
            "Elevator_DB",
            arrayOf("id",  "ip", "mac", "status"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val Elevator = Elevator()
            Elevator.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))

            Elevator.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            Elevator.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            Elevator.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))


            return Elevator
        }

        return null
    }

    fun getElevatorsByRoomName(roomName: String?): List<Elevator> {
        val db: SQLiteDatabase = readableDatabase
        val ElevatorList = mutableListOf<Elevator>()

        val selection = "room_name = ?"
        val selectionArgs = arrayOf(roomName)
        val cursor = db.query("Elevator_DB", null, selection, selectionArgs, null, null, null)

        cursor?.use {
            while (cursor.moveToNext()) {
                val Elevator = Elevator()


                Elevator.id = cursor.getIntOrNull(it.getColumnIndex("id"))

                Elevator.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))

                Elevator.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
                Elevator.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))


                ElevatorList.add(Elevator)
            }
        }
        db.close()
        return ElevatorList
    }
    fun deleteRowsWithNullOrEmptySubTtype() {
        val db: SQLiteDatabase = writableDatabase
        val whereClause = "status IS NULL OR subtype = ?"
        val whereArgs = arrayOf("")

        db.delete("Elevator_DB", whereClause, whereArgs)
        db.close()
    }


    fun delete_from_db_Elevator(id: Int?): Int {
        val db: SQLiteDatabase = writableDatabase
        return db.delete("Elevator_DB", "id=?", arrayOf(id.toString()))
    }

    fun updateElevatorById(id: Int?, Elevator: Elevator): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()

        value.put("ip", Elevator.ip)
        value.put("mac", Elevator.mac)
        value.put("status", Elevator.status)


//        LightLiveData.postValue(light)
        println("Elevator_db updated" )

        return db.update("Elevator_DB", value, "id=?", arrayOf(id.toString()))
    }

    fun getElevatorsBySameMacForLnames(Lnames: List<String>): List<List<Elevator>> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "Fname IN (${Lnames.joinToString { "?" }})"
        val selectionArgs = Lnames.toTypedArray()
        val cursor = db.query(
            "Elevator_DB",
            arrayOf("id", "ip", "mac", "status"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val ElevatorList = mutableListOf<Elevator>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))

                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))


                val Elevator = Elevator(

                    ip ?: "",
                    mac ?: "",
                    status ?: "",

                    id,

                    )
                ElevatorList.add(Elevator)
            }
        }

        // گروه‌بندی لامپ‌ها بر اساس Mac های مشابه
        val groupedElevators = mutableMapOf<String, MutableList<Elevator>>()

        for (Elevator in ElevatorList) {
            val mac = Elevator.mac ?: continue // تایید کردن اینکه Mac خالی نباشد
            if (!groupedElevators.containsKey(mac)) {
                groupedElevators[mac] = mutableListOf()
            }
            groupedElevators[mac]?.add(Elevator)
        }

        // تبدیل Map به لیست از لیست‌ها
        return groupedElevators.values.toList()
    }


    fun getStatusById(id: Int?): String? {
        val db: SQLiteDatabase = readableDatabase
        var status: String? = null

        try {
            val selection = "id = ?"
            val selectionArgs = arrayOf(id.toString())
            val cursor = db.query(
                "Elevator_DB",
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

    fun clear_db() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("Elevator_DB", null, null)
        db.close()
        Log.d("database message :", "Elevator_DBcleared")
    }

    companion object {
        private var instance: Elevator_db? = null

        @Synchronized
        fun getInstance(context: Context): Elevator_db {
            if (instance == null) {
                instance = Elevator_db(context.applicationContext)
            }
            return instance!!
        }
    }

}