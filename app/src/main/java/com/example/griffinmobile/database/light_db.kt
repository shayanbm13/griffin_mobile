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
import com.example.griffinmobile.mudels.Light

class light_db private constructor(context: Context) : SQLiteOpenHelper(context, "light_DB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_light_DB_TABLE =
            "CREATE TABLE IF NOT EXISTS " + "light_DB" + "(id INTEGER PRIMARY KEY,  room_name TEXT,  sub_type TEXT ,  status TEXT  ,  ip TEXT  ,  mac TEXT,  Lname TEXT  )"

        db?.execSQL(CREATE_light_DB_TABLE)
        Log.d("database message :", "light_DB created")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldversion: Int, newversion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS light_DB")
        onCreate(db)
    }

    private val LightLiveData: MutableLiveData<Light?> = MutableLiveData()
//    private val roomNamesLiveData = MutableLiveData<List<String>>()
fun updateLightName(oldRoomName: String, newName: String) {
    val db = writableDatabase

    // بررسی وجود رکورد با room_name مشخص شده
    val cursor = db.query(
        "light_DB",
        arrayOf("id"),
        "room_name = ?",
        arrayOf(oldRoomName),
        null,
        null,
        null
    )

    if (cursor != null && cursor.moveToFirst()) {
        // رکورد با room_name مشخص شده وجود دارد، انجام عملیات بروزرسانی
        val values = ContentValues().apply {
            put("room_name", newName)  // بروزرسانی Lname با newName
        }

        val rowsAffected = db.update(
            "light_DB",
            values,
            "room_name = ?",
            arrayOf(oldRoomName)
        )

        if (rowsAffected > 0) {
            Log.d("database message:", "Light name updated successfully.")
        } else {
            Log.d("database message:", "Failed to update light name.")
        }
    } else {
        Log.d("database message:", "No record found with room_name: $oldRoomName")
    }

    cursor?.close()
    db.close()
}
    fun removeDuplicates() {
        val db = this.writableDatabase

        // کوئری برای انتخاب ردیف‌های تکراری بر اساس mac و sub_type، به جز یک نمونه از هر کدام
        val query = """
        DELETE FROM light_DB
        WHERE id NOT IN (
            SELECT MIN(id)
            FROM light_DB
            GROUP BY mac, sub_type
        )
    """

        try {
            db.execSQL(query)
            Log.d("database message:", "Duplicate entries removed based on mac and sub_type")
        } catch (e: Exception) {
            Log.e("database error:", "Error removing duplicates: ${e.message}")
        } finally {
            db.close()
        }
    }
    fun set_to_db_light(Light: Light) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put("room_name", Light.room_name)
        values.put("sub_type", Light.sub_type)
        values.put("status", Light.status)
        values.put("ip", Light.ip)
        values.put("mac", Light.mac)
        values.put("Lname", Light.Lname)


        db.insert("light_DB", null, values)
        db.close()
        Log.d("database message :", "light_DB updated")
        LightLiveData.postValue(Light)

    }

    fun updateIpById(id: Int?, newIp: String) {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("ip", newIp)

        db.update("light_DB", value, "id=?", arrayOf(id.toString()))
        db.close()
    }

    fun updateStatusById(id: Int?, newStatus: String) {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        try {
            value.put("status", newStatus)
            db.update("light_DB", value, "id=?", arrayOf(id.toString()))
            println("db updated")

        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }


    fun getLightsBySubType(subType: String): List<Light?> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "sub_type = ?"
        val selectionArgs = arrayOf(subType)
        val cursor = db.query(
            "light_DB",
            arrayOf("id", "room_name", "sub_type", "status", "ip", "mac", "Lname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val lightList = mutableListOf<Light?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val roomName = it.getStringOrNull(it.getColumnIndex("room_name"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Lname = it.getStringOrNull(it.getColumnIndex("Lname"))

                val light = Light(
                    roomName ?: "",
                    subType,
                    status ?: "",
                    ip ?: "",
                    mac ?: "",
                    id,
                    Lname ?: ""
                )
                lightList.add(light)
            }
        }

        return lightList.toList()
    }

    fun getLightsWithSubType0000(): List<Light?> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "sub_type = ?"
        val selectionArgs = arrayOf("0000")
        val cursor = db.query(
            "light_DB",
            arrayOf("id", "room_name", "sub_type", "status", "ip", "mac", "Lname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val lightList = mutableListOf<Light?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val roomName = it.getStringOrNull(it.getColumnIndex("room_name"))
                val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Lname = it.getStringOrNull(it.getColumnIndex("Lname"))

                val light = Light(
                    roomName ?: "",
                    subType ?: "",
                    status ?: "",
                    ip ?: "",
                    mac ?: "",
                    id,
                    Lname ?: ""
                )
                lightList.add(light)
            }
        }

        return lightList.toList()
    }

    fun updateStatusAndIpById(id: Int?,newStatus: String?,newIp:String?){
        val db=writableDatabase
        val values=ContentValues()
        try {
            values.put("status",newStatus)
            values.put("ip",newIp)
            db.update("light_DB",values,"id=?", arrayOf(id.toString()))
        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }

    fun delete_from_db_light(id: Int?): Int {
        val db: SQLiteDatabase = writableDatabase
        return db.delete("light_DB", "id=?", arrayOf(id.toString()))
    }

    fun get_from_db_light(id: Int?): Light? {

        try {
            val db: SQLiteDatabase = writableDatabase
            val cursor = db.query(
                "light_DB",
                arrayOf("id", "room_name", "sub_type", "status","ip","mac","Lname"),
                "id=?",
                arrayOf(id.toString()),
                null,
                null,
                null
            )
            if (cursor != null) {
                cursor.moveToFirst()
                val Light = Light()
                Light.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
                Light.sub_type = cursor.getStringOrNull(cursor.getColumnIndex("sub_type"))
                Light.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
                Light.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
                Light.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
                Light.Lname = cursor.getStringOrNull(cursor.getColumnIndex("Lname"))
                Light.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))

                LightLiveData.postValue(Light)
                return Light
            }
            db.close()
            return null


        }catch (e:Exception){
            return null

            println(e)
        }

    }

    fun get_id_by_same_macs(){


    }
    fun getAllLights(): List<Light?> {
        val db: SQLiteDatabase = readableDatabase
        val cursor = db.query(
            "light_DB",
            arrayOf("id", "room_name", "sub_type", "status", "ip", "mac", "Lname"),
            null,
            null,
            null,
            null,
            null
        )

        val lightList = mutableListOf<Light?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val roomName = it.getStringOrNull(it.getColumnIndex("room_name"))
                val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Lname = it.getStringOrNull(it.getColumnIndex("Lname"))

                val light = Light(
                    roomName ?: "",
                    subType ?: "",
                    status ?: "",
                    ip ?: "",
                    mac ?: "",
                    id,
                    Lname ?: ""
                )
                lightList.add(light)
            }
        }

        return lightList.toList()
    }
    fun deleteRowsWithNullOrEmptySubTtype() {
        val db: SQLiteDatabase = writableDatabase
        val whereClause = "sub_type IS NULL OR sub_type = ?"
        val whereArgs = arrayOf("")

        db.delete("light_DB", whereClause, whereArgs)
        db.close()
    }



    fun getLightsWithNonEmptyMacByRoomName(roomName: String?): LiveData<List<Light?>> {
        val lightListLiveData = MutableLiveData<List<Light?>>()

        val db: SQLiteDatabase = readableDatabase
        val selection = "mac IS NOT NULL AND mac != '' AND room_name LIKE ?"
        val selectionArgs = arrayOf("$roomName%")
        val cursor = db.query(
            "light_DB",
            arrayOf("id", "room_name", "sub_type", "status", "ip", "mac", "Lname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val lightList = mutableListOf<Light?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Lname = it.getStringOrNull(it.getColumnIndex("Lname"))

                val light = Light(
                    roomName ?: "",
                    subType ?: "",
                    status ?: "",
                    ip ?: "",
                    mac ?: "",
                    id,
                    Lname ?: ""
                )
                lightList.add(light)
            }
        }

        lightListLiveData.postValue(lightList)
        return lightListLiveData
    }


    fun getLightsWithSameMacByRoomName(roomName: String?): List<List<Light>> {

        val db: SQLiteDatabase = readableDatabase
        val selection = "room_name = ?"
        val selectionArgs = arrayOf(roomName)
        val cursor = db.query(
            "light_DB",
            arrayOf("id", "room_name", "sub_type", "status", "ip", "mac", "Lname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val lightList = mutableListOf<Light>()

        try {
            cursor?.use {
                while (it.moveToNext()) {
                    val id = it.getIntOrNull(it.getColumnIndex("id"))
                    val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                    val status = it.getStringOrNull(it.getColumnIndex("status"))
                    val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                    val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                    val Lname = it.getStringOrNull(it.getColumnIndex("Lname"))

                    val light = Light(
                        roomName ?: "",
                        subType ?: "",
                        status ?: "",
                        ip ?: "",
                        mac ?: "",
                        id,
                        Lname ?: ""
                    )
                    lightList.add(light)
                }
            }
            cursor.close()
        }catch (e:Exception){
            println(e)
        }


        // گروه‌بندی لامپ‌ها بر اساس Mac های مشابه
        val groupedLights = mutableMapOf<String, MutableList<Light>>()

        for (light in lightList) {
            val mac = light.mac ?: continue // تایید کردن اینکه Mac خالی نباشد
            if (!groupedLights.containsKey(mac)) {
                groupedLights[mac] = mutableListOf()
            }
            groupedLights[mac]?.add(light)
        }

        // تبدیل Map به لیست از لیست‌ها
        db.close()
        return groupedLights.values.toList()
    }



    fun getLightsBySameMacForLnames(Lnames: List<String>): List<List<Light>> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "Lname IN (${Lnames.joinToString { "?" }})"
        val selectionArgs = Lnames.toTypedArray()
        val cursor = db.query(
            "light_DB",
            arrayOf("id", "room_name", "sub_type", "status", "ip", "mac", "Lname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val lightList = mutableListOf<Light>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Lname = it.getStringOrNull(it.getColumnIndex("Lname"))
                val room_name = it.getStringOrNull(it.getColumnIndex("room_name"))

                val light = Light(
                    room_name ?: "",
                    subType ?: "",
                    status ?: "",
                    ip ?: "",
                    mac ?: "",
                    id,
                    Lname ?: ""
                )
                lightList.add(light)
            }
        }

        // گروه‌بندی لامپ‌ها بر اساس Mac های مشابه
        val groupedLights = mutableMapOf<String, MutableList<Light>>()

        for (light in lightList) {
            val mac = light.mac ?: continue // تایید کردن اینکه Mac خالی نباشد
            if (!groupedLights.containsKey(mac)) {
                groupedLights[mac] = mutableListOf()
            }
            groupedLights[mac]?.add(light)
        }

        // تبدیل Map به لیست از لیست‌ها
        return groupedLights.values.toList()
    }


    fun getLightsByMacAddress(mac: String?): List<Light?> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "mac = ?"
        val selectionArgs = arrayOf(mac)
        val cursor = db.query(
            "light_DB",
            arrayOf("id", "room_name", "sub_type", "status", "ip", "mac", "Lname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val lightList = mutableListOf<Light?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val roomName = it.getStringOrNull(it.getColumnIndex("room_name"))
                val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val macAddress = it.getStringOrNull(it.getColumnIndex("mac"))
                val Lname = it.getStringOrNull(it.getColumnIndex("Lname"))

                val light = Light(
                    roomName ?: "",
                    subType ?: "",
                    status ?: "",
                    ip ?: "",
                    macAddress ?: "",
                    id,
                    Lname ?: ""
                )
                lightList.add(light)
            }
        }

        return lightList
    }




    fun getLightsByLname(Lname: String?): Light {
        try {
            val db: SQLiteDatabase = readableDatabase

            // دیگر کدهای شما اینجا قرار می‌گیرد...

            val selection = "Lname = ?"
            val selectionArgs = arrayOf(Lname)
            val cursor = db.query(
                "light_DB",
                arrayOf("id", "room_name", "sub_type", "status", "ip", "mac", "Lname"),
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            var light = Light()

            cursor?.use {
                while (it.moveToNext()) {
                    val id = it.getIntOrNull(it.getColumnIndex("id"))
                    val roomName = it.getStringOrNull(it.getColumnIndex("room_name"))
                    val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                    val status = it.getStringOrNull(it.getColumnIndex("status"))
                    val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                    val macAddress = it.getStringOrNull(it.getColumnIndex("mac"))
                    val Lname = it.getStringOrNull(it.getColumnIndex("Lname"))

                    light = Light(
                        roomName ?: "",
                        subType ?: "",
                        status ?: "",
                        ip ?: "",
                        macAddress ?: "",
                        id,
                        Lname ?: ""
                    )
                }
            }
            return light
        } catch (e: Exception) {
            // مدیریت خطاها در اینجا
            e.printStackTrace()
            return Light() // یا مقدار پیش‌فرض دیگری
        }
    }
    fun getLightsByid(id: String?): Light {
        try {
            val db: SQLiteDatabase = readableDatabase

            // دیگر کدهای شما اینجا قرار می‌گیرد...

            val selection = "id = ?"
            val selectionArgs = arrayOf(id)
            val cursor = db.query(
                "light_DB",
                arrayOf("id", "room_name", "sub_type", "status", "ip", "mac", "Lname"),
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            var light = Light()

            cursor?.use {
                while (it.moveToNext()) {
                    val id = it.getIntOrNull(it.getColumnIndex("id"))
                    val roomName = it.getStringOrNull(it.getColumnIndex("room_name"))
                    val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                    val status = it.getStringOrNull(it.getColumnIndex("status"))
                    val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                    val macAddress = it.getStringOrNull(it.getColumnIndex("mac"))
                    val Lname = it.getStringOrNull(it.getColumnIndex("Lname"))

                    light = Light(
                        roomName ?: "",
                        subType ?: "",
                        status ?: "",
                        ip ?: "",
                        macAddress ?: "",
                        id,
                        Lname ?: ""
                    )
                }
            }
            return light
        } catch (e: Exception) {
            // مدیریت خطاها در اینجا
            e.printStackTrace()
            return Light() // یا مقدار پیش‌فرض دیگری
        }
    }


    fun getStatusById(id: Int?): String? {
        val db: SQLiteDatabase = readableDatabase
        var status: String? = null

        try {
            val selection = "id = ?"
            val selectionArgs = arrayOf(id.toString())
            val cursor = db.query(
                "light_DB",
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


    fun getAllLightsByRoomName(roomName: String?): List<Light?> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_name = ?"
        val selectionArgs = arrayOf(roomName)

        val cursor = db.query(
            "light_DB",
            arrayOf("id", "room_name", "sub_type", "status", "ip", "mac", "Lname"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val lightList = mutableListOf<Light?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val Lname = it.getStringOrNull(it.getColumnIndex("Lname"))

                val light = roomName?.let { it1 ->
                    Light(
                        it1,
                        subType ?: "",
                        status ?: "",
                        ip ?: "",
                        mac ?: "",
                        id,
                        Lname ?: ""
                    )
                }
                lightList.add(light)
            }
        }

        return lightList.toList()
    }

    fun getRoomByRoomNamePrefix(prefix: String): Light? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_name LIKE ?"
        val selectionArgs = arrayOf("$prefix%")
        val cursor = db.query("light_DB", null, selection, selectionArgs, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            val room = Light()
            room.room_name = cursor.getStringOrNull(cursor.getColumnIndex("room_name"))
            room.sub_type = cursor.getStringOrNull(cursor.getColumnIndex("sub_type"))
            room.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
            room.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            room.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            room.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
            return room
        }

        return null
    }
    fun updateLightById(id: Int?, light: Light): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("room_name", light.room_name)
        value.put("sub_type", light.sub_type)
        value.put("status", light.status)
        value.put("ip", light.ip)
        value.put("mac", light.mac)
        value.put("Lname", light.Lname)

        LightLiveData.postValue(light)
        println("light_db updated" )

        return db.update("light_DB", value, "id=?", arrayOf(id.toString()))
    }


    fun clear_db_light() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("light_DB", null, null)
        db.close()
        Log.d("database message :", "light_DB cleared")
    }

//    fun isEmptynetwork_tabale() :Boolean{
//
//        val db: SQLiteDatabase =readableDatabase
//        val countQuery="SELECT * FROM light_DB"
//        val cursor: Cursor =db.rawQuery(countQuery,null)
//        if (cursor.count==0){
//            return true
//        }else{
//            return false
//        }
//
//    }

//    fun checkRoomTypeExists(roomType: String): Boolean {
//        val db: SQLiteDatabase = readableDatabase
//        val selection = "sub_type = ?"
//        val selectionArgs = arrayOf(roomType)
//        val cursor = db.query("light_DB", null, selection, selectionArgs, null, null, null)
//        val roomTypeExists = cursor.count > 0
//        cursor.close()
//        return roomTypeExists
//    }
    fun checkRoomNameExists(roomName: String): Boolean {
        val db: SQLiteDatabase = readableDatabase
        val selection = "room_name = ?"
        val selectionArgs = arrayOf(roomName)
        val cursor = db.query("light_DB", null, selection, selectionArgs, null, null, null)
        val roomNameExists = cursor.count > 0
        cursor.close()
        return roomNameExists
    }
    fun getNumberOfItemsByLname(Lname: String): Int {
        val db: SQLiteDatabase = readableDatabase
        val selection = "Lname = ?"
        val selectionArgs = arrayOf(Lname)
        val cursor = db.query(
            "light_DB",
            arrayOf("COUNT(*)"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var count = 0

        cursor?.use {
            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }

        return count
    }
    fun get_db_light_LiveData(): LiveData<Light?> {
        return LightLiveData
    }
    companion object {
        private var instance: light_db? = null

        @Synchronized
        fun getInstance(context: Context): light_db {
            if (instance == null) {
                instance = light_db(context.applicationContext)
            }
            return instance!!
        }
    }

}