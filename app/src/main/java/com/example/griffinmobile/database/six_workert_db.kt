package com.example.griffinmobile.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.lifecycle.MutableLiveData
import com.example.griffinmobile.mudels.six_workert

class six_workert_db  private constructor(context: Context) : SQLiteOpenHelper(context, "six_workert_DB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_six_workert_DB_TABLE =
            "CREATE TABLE IF NOT EXISTS " + "six_workert_DB" + "(id INTEGER PRIMARY KEY,  type TEXT, pole_num TEXT,  sub_type TEXT ,  status TEXT  ,  ip TEXT  ,  mac TEXT,  name TEXT ,  work_name TEXT  )"





        db?.execSQL(CREATE_six_workert_DB_TABLE)
        Log.d("database message :", "six_workert_DB created")


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldversion: Int, newversion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS six_workert_DB")
        onCreate(db)
    }

    fun removeDuplicates() {
        val db = this.writableDatabase

        // کوئری برای حذف رکوردهای تکراری
        val query = """
            DELETE FROM six_workert_DB
            WHERE id NOT IN (
                SELECT MIN(id)
                FROM six_workert_DB
                GROUP BY mac, sub_type
            )
        """

        try {
            db.execSQL(query)
            Log.d("database message:", "Duplicate entries removed based on mac and sub_type in six_workert_DB")
        } catch (e: Exception) {
            Log.e("database error:", "Error removing duplicates in six_workert_DB: ${e.message}")
        } finally {
            db.close()
        }
    }
    private val six_workertLiveData: MutableLiveData<six_workert?> = MutableLiveData()
//    private val roomNamesLiveData = MutableLiveData<List<String>>()


    fun set_to_db_six_workert(six_workert: six_workert) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put("type", six_workert.type)
        values.put("sub_type", six_workert.sub_type)
        values.put("pole_num", six_workert.pole_num)
        values.put("status", six_workert.status)
        values.put("ip", six_workert.ip)
        values.put("mac", six_workert.mac)
        values.put("name", six_workert.name)
        values.put("work_name", six_workert.work_name)


        db.insert("six_workert_DB", null, values)
        db.close()
        Log.d("database message :", "six_workert_DB updated")
        six_workertLiveData.postValue(six_workert)

    }

    fun updateIpById(id: Int?, newIp: String) {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("ip", newIp)

        db.update("six_workert_DB", value, "id=?", arrayOf(id.toString()))
        db.close()
    }

    fun updateStatusById(id: Int?, newStatus: String) {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        try {
            value.put("status", newStatus)
            db.update("six_workert_DB", value, "id=?", arrayOf(id.toString()))

        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }




   




    fun delete_from_db_six_workert(id: Int?): Int {
        val db: SQLiteDatabase = writableDatabase
        return db.delete("six_workert_DB", "id=?", arrayOf(id.toString()))
    }

    fun get_from_db_six_workert(id: Int?): six_workert? {

        try {
            val db: SQLiteDatabase = writableDatabase
            val cursor = db.query(
                "six_workert_DB",
                arrayOf("id", "type","pole_num", "sub_type", "status","ip","mac","name","work_name"),
                "id=?",
                arrayOf(id.toString()),
                null,
                null,
                null
            )
            if (cursor != null) {
                cursor.moveToFirst()
                val six_workert = six_workert()
                six_workert.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
                six_workert.type = cursor.getStringOrNull(cursor.getColumnIndex("type"))
                six_workert.sub_type = cursor.getStringOrNull(cursor.getColumnIndex("sub_type"))
                six_workert.status = cursor.getStringOrNull(cursor.getColumnIndex("status"))
                six_workert.pole_num = cursor.getStringOrNull(cursor.getColumnIndex("pole_num"))
                six_workert.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
                six_workert.mac = cursor.getStringOrNull(cursor.getColumnIndex("mac"))
                six_workert.name = cursor.getStringOrNull(cursor.getColumnIndex("name"))
                six_workert.work_name = cursor.getStringOrNull(cursor.getColumnIndex("work_name"))

                six_workertLiveData.postValue(six_workert)
                return six_workert
            }
            db.close()
            return null


        }catch (e:Exception){
            return null

            println(e)
        }

    }

   fun getAllsix_workerts(): List<six_workert?> {
        val db: SQLiteDatabase = readableDatabase
        val cursor = db.query(
            "six_workert_DB",
            arrayOf("id", "type","pole_num", "sub_type", "status", "ip", "mac", "name", "work_name"),
            null,
            null,
            null,
            null,
            null
        )

        val six_workertList = mutableListOf<six_workert?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val roomName = it.getStringOrNull(it.getColumnIndex("type"))
                val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                val pole_num = it.getStringOrNull(it.getColumnIndex("pole_num"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val name = it.getStringOrNull(it.getColumnIndex("name"))
                val work_name = it.getStringOrNull(it.getColumnIndex("work_name"))

                val six_workert = six_workert(
                    roomName ?: "",
                    subType ?: "",
                    pole_num ?: "",
                    status ?: "",
                    ip ?: "",
                    mac ?: "",
                    id,
                    name ?: "",
                    work_name ?: ""
                )
                six_workertList.add(six_workert)
            }
        }

        return six_workertList.toList()
    }
    fun deleteRowsWithNullOrNewLocalName() {
        val db: SQLiteDatabase = writableDatabase
        val whereClause = "sub_type IS NULL OR sub_type = ?"
        val whereArgs = arrayOf("new_local")

        db.delete("six_workert_DB", whereClause, whereArgs)
        db.close()
    }









    fun getsix_workertsByMacAddress(mac: String?): List<six_workert?> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "mac = ?"
        val selectionArgs = arrayOf(mac)
        val cursor = db.query(
            "six_workert_DB",
            arrayOf("id", "type","pole_num", "sub_type", "status", "ip", "mac", "name", "work_name"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val six_workertList = mutableListOf<six_workert?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val roomName = it.getStringOrNull(it.getColumnIndex("type"))
                val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val pole_num = it.getStringOrNull(it.getColumnIndex("pole_num"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val macAddress = it.getStringOrNull(it.getColumnIndex("mac"))
                val name = it.getStringOrNull(it.getColumnIndex("name"))
                val work_name = it.getStringOrNull(it.getColumnIndex("work_name"))

                val six_workert = six_workert(
                    roomName ?: "",
                    subType ?: "",
                    pole_num ?: "",
                    status ?: "",
                    ip ?: "",
                    macAddress ?: "",
                    id,
                    name ?: "",
                    work_name ?: ""
                )
                six_workertList.add(six_workert)
            }
        }

        return six_workertList
    }




    fun getsix_workertsByname(name: String?): six_workert {
        try {
            val db: SQLiteDatabase = readableDatabase

            // دیگر کدهای شما اینجا قرار می‌گیرد...

            val selection = "name = ?"
            val selectionArgs = arrayOf(name)
            val cursor = db.query(
                "six_workert_DB",
                arrayOf("id", "type","pole_num", "sub_type", "status", "ip", "mac", "name", "work_name"),
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            var six_workert = six_workert()

            cursor?.use {
                while (it.moveToNext()) {
                    val id = it.getIntOrNull(it.getColumnIndex("id"))
                    val roomName = it.getStringOrNull(it.getColumnIndex("type"))
                    val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                    val pole_num = it.getStringOrNull(it.getColumnIndex("pole_num"))
                    val status = it.getStringOrNull(it.getColumnIndex("status"))
                    val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                    val macAddress = it.getStringOrNull(it.getColumnIndex("mac"))
                    val name = it.getStringOrNull(it.getColumnIndex("name"))
                    val work_name = it.getStringOrNull(it.getColumnIndex("work_name"))

                    six_workert = six_workert(
                        roomName ?: "",
                        subType ?: "",
                        pole_num ?: "",
                        status ?: "",
                        ip ?: "",
                        macAddress ?: "",
                        id,
                        name ?: "",
                        work_name ?: ""
                    )
                }
            }
            return six_workert
        } catch (e: Exception) {
            // مدیریت خطاها در اینجا
            e.printStackTrace()
            return six_workert() // یا مقدار پیش‌فرض دیگری
        }
    }
    fun getsix_workertsByid(id: Int?): six_workert {
        try {
            val db: SQLiteDatabase = readableDatabase

            // دیگر کدهای شما اینجا قرار می‌گیرد...

            val selection = "id = ?"
            val selectionArgs = arrayOf(id.toString())
            val cursor = db.query(
                "six_workert_DB",
                arrayOf("id", "type","pole_num", "sub_type", "status", "ip", "mac", "name", "work_name"),
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            var six_workert = six_workert()

            cursor?.use {
                while (it.moveToNext()) {
                    val id = it.getIntOrNull(it.getColumnIndex("id"))
                    val roomName = it.getStringOrNull(it.getColumnIndex("type"))
                    val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                    val pole_num = it.getStringOrNull(it.getColumnIndex("pole_num"))
                    val status = it.getStringOrNull(it.getColumnIndex("status"))
                    val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                    val macAddress = it.getStringOrNull(it.getColumnIndex("mac"))
                    val name = it.getStringOrNull(it.getColumnIndex("name"))
                    val work_name = it.getStringOrNull(it.getColumnIndex("work_name"))

                    six_workert = six_workert(
                        roomName ?: "",
                        subType ?: "",
                        pole_num ?: "",
                        status ?: "",
                        ip ?: "",
                        macAddress ?: "",
                        id,
                        name ?: "",
                        work_name ?: ""
                    )
                }
            }
            return six_workert
        } catch (e: Exception) {
            // مدیریت خطاها در اینجا
            e.printStackTrace()
            return six_workert() // یا مقدار پیش‌فرض دیگری
        }
    }


    fun getStatusById(id: Int?): String? {
        val db: SQLiteDatabase = readableDatabase
        var status: String? = null

        try {
            val selection = "id = ?"
            val selectionArgs = arrayOf(id.toString())
            val cursor = db.query(
                "six_workert_DB",
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


    fun getAllsix_workertsByRoomName(roomName: String?): List<six_workert?> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "type = ?"
        val selectionArgs = arrayOf(roomName)

        val cursor = db.query(
            "six_workert_DB",
            arrayOf("id", "type","pole_num", "sub_type", "status", "ip", "mac", "name", "work_name"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val six_workertList = mutableListOf<six_workert?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))
                val subType = it.getStringOrNull(it.getColumnIndex("sub_type"))
                val status = it.getStringOrNull(it.getColumnIndex("status"))
                val pole_num = it.getStringOrNull(it.getColumnIndex("pole_num"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val mac = it.getStringOrNull(it.getColumnIndex("mac"))
                val name = it.getStringOrNull(it.getColumnIndex("name"))
                val work_name = it.getStringOrNull(it.getColumnIndex("work_name"))

                val six_workert = roomName?.let { it1 ->
                    six_workert(
                        it1,
                        subType ?: "",
                        status ?: "",
                        pole_num ?: "",
                        ip ?: "",
                        mac ?: "",
                        id,
                        name ?: "",
                        work_name ?: ""
                    )
                }
                six_workertList.add(six_workert)
            }
        }

        return six_workertList.toList()
    }


    fun updatesix_workertById(id: Int?, six_workert: six_workert): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("type", six_workert.type)
        value.put("sub_type", six_workert.sub_type)
        value.put("status", six_workert.status)
        value.put("pole_num", six_workert.pole_num)
        value.put("ip", six_workert.ip)
        value.put("mac", six_workert.mac)
        value.put("name", six_workert.name)
        value.put("work_name", six_workert.work_name)

        six_workertLiveData.postValue(six_workert)
        println("six_workert_db updated" )

        return db.update("six_workert_DB", value, "id=?", arrayOf(id.toString()))
    }


    fun clear_db_six_workert() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("six_workert_DB", null, null)
        db.close()
        Log.d("database message :", "six_workert_DB cleared")
    }





    companion object {
        private var instance: six_workert_db? = null

        @Synchronized
        fun getInstance(context: Context): six_workert_db {
            if (instance == null) {
                instance = six_workert_db(context.applicationContext)
            }
            return instance!!
        }
    }







}