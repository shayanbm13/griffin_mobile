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
import com.example.griffinmobile.mudels.camera

class camera_db private constructor(context: Context) : SQLiteOpenHelper(context, "camera_db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_camera_db_TABLE =
            "CREATE TABLE IF NOT EXISTS " + "camera_db" + "(id INTEGER PRIMARY KEY,  user TEXT,  subtype TEXT ,  pass TEXT  ,  ip TEXT  ,  chanel TEXT,  CAMname TEXT,  port TEXT ,  tag TEXT  )"





        db?.execSQL(CREATE_camera_db_TABLE)
        Log.d("database message :", "camera_db created")


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldversion: Int, newversion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS camera_db")
        onCreate(db)
    }

    private val cameraLiveData: MutableLiveData<camera?> = MutableLiveData()



    fun set_to_db_camera(camera: camera?) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put("user", camera!!.user)
        values.put("subtype", camera.subtype)
        values.put("pass", camera.pass)
        values.put("ip", camera.ip)
        values.put("chanel", camera.chanel)
        values.put("CAMname", camera.CAMname)
        values.put("port", camera.port)
        values.put("chanel", camera.chanel)
        values.put("tag", camera.tag)

        db.insert("camera_db", null, values)
        db.close()
        Log.d("database message :", "camera_db updated")
        cameraLiveData.postValue(camera)

    }

    fun updateIpById(id: Int?, newIp: String) {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("ip", newIp)

        db.update("camera_db", value, "id=?", arrayOf(id.toString()))
        db.close()
    }


    fun getCamerasByTag(tag: String): List<camera> {
        val cameras = mutableListOf<camera>()
        val db: SQLiteDatabase = readableDatabase
        val cursor = db.query(
            "camera_db",  // Table name
            null,  // Columns (null برای دریافت همه ستون‌ها)
            "tag=?",  // Selection (شرط)
            arrayOf(tag),  // Selection args (مقدار شرط)
            null,  // Group by
            null,  // Having
            null   // Order by
        )

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val user = cursor.getString(cursor.getColumnIndexOrThrow("user"))
                val subtype = cursor.getString(cursor.getColumnIndexOrThrow("subtype"))
                val pass = cursor.getString(cursor.getColumnIndexOrThrow("pass"))
                val ip = cursor.getString(cursor.getColumnIndexOrThrow("ip"))
                val chanel = cursor.getString(cursor.getColumnIndexOrThrow("chanel"))
                val camName = cursor.getString(cursor.getColumnIndexOrThrow("CAMname"))
                val port = cursor.getString(cursor.getColumnIndexOrThrow("port"))
                val tagValue = cursor.getString(cursor.getColumnIndexOrThrow("tag"))

                val camera = camera( user, subtype, pass, ip, chanel, camName, port, tagValue,id)
                cameras.add(camera)
            }
            cursor.close()
        }
        db.close()
        return cameras
    }


    fun updatepassById(id: Int?, newpass: String?) {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        try {
            value.put("pass", newpass)
            db.update("camera_db", value, "id=?", arrayOf(id.toString()))

        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }

    fun updatepassAndIpById(id: Int?, newpass: String?,newIp: String) {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        try {
            value.put("pass", newpass)
            value.put("ip", newIp)
            db.update("camera_db", value, "id=?", arrayOf(id.toString()))

        }catch (e:Exception){
            println(e)
        }finally {
            db.close()
        }

    }

    fun delete_from_db_camera(id: Int?): Int {
        val db: SQLiteDatabase = writableDatabase
        return db.delete("camera_db", "id=?", arrayOf(id.toString()))
    }

    fun get_from_db_camera(id: Int?): camera? {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            "camera_db",
            arrayOf("id", "user", "subtype", "pass","ip","CAMname","port","chanel","tag"),
            "id=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val camera = camera()
            camera.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            camera.user = cursor.getStringOrNull(cursor.getColumnIndex("user"))
            camera.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
            camera.pass = cursor.getStringOrNull(cursor.getColumnIndex("pass"))
            camera.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            camera.chanel = cursor.getStringOrNull(cursor.getColumnIndex("chanel"))
            camera.CAMname = cursor.getStringOrNull(cursor.getColumnIndex("CAMname"))
            camera.port = cursor.getStringOrNull(cursor.getColumnIndex("port"))
            camera.chanel = cursor.getStringOrNull(cursor.getColumnIndex("chanel"))
            camera.tag = cursor.getStringOrNull(cursor.getColumnIndex("tag"))

            cameraLiveData.postValue(camera)
            return camera
        }
        return null
    }

    fun get_id_by_same_chanels(){


    }
    fun getAllcameras(): List<camera?> {
        val db: SQLiteDatabase = readableDatabase
        val cursor = db.query(
            "camera_db",
            arrayOf("id", "user", "subtype", "pass", "ip", "chanel", "CAMname", "port", "chanel", "tag"),
            null,
            null,
            null,
            null,
            null
        )

        val cameraList = mutableListOf<camera?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))

                val subType = it.getStringOrNull(it.getColumnIndex("subtype"))
                val pass = it.getStringOrNull(it.getColumnIndex("pass"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val chanel = it.getStringOrNull(it.getColumnIndex("chanel"))
                val CAMname = it.getStringOrNull(it.getColumnIndex("CAMname"))
                val port = it.getStringOrNull(it.getColumnIndex("port"))
                val user = it.getStringOrNull(it.getColumnIndex("user"))
                val tag = it.getStringOrNull(it.getColumnIndex("tag"))

                val camera = camera(
                    CAMname ?: "",
                    user ?: "",
                    pass ?: "",
                    port ?: "",
                    chanel ?: "",
                    subType ?: "",
                    ip ?:"",
                    tag ?:"",
                    id

                )
                cameraList.add(camera)
            }
        }

        return cameraList.toList()
    }
    fun deleteRowsWithNullOrEmptySubTtype() {
        val db: SQLiteDatabase = writableDatabase
        val whereClause = "subtype IS NULL OR subtype = ?"
        val whereArgs = arrayOf("")

        db.delete("camera_db", whereClause, whereArgs)
        db.close()
    }





    fun getcamerasBychanelAddress(chanel: String?): List<camera?> {
        val db: SQLiteDatabase = readableDatabase
        val selection = "chanel = ?"
        val selectionArgs = arrayOf(chanel)
        val cursor = db.query(
            "camera_db",
            arrayOf("id", "user", "subtype", "pass", "ip", "chanel", "CAMname", "port", "tag"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val cameraList = mutableListOf<camera?>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getIntOrNull(it.getColumnIndex("id"))

                val subType = it.getStringOrNull(it.getColumnIndex("subtype"))
                val pass = it.getStringOrNull(it.getColumnIndex("pass"))
                val ip = it.getStringOrNull(it.getColumnIndex("ip"))
                val chanel = it.getStringOrNull(it.getColumnIndex("chanel"))
                val CAMname = it.getStringOrNull(it.getColumnIndex("CAMname"))
                val port = it.getStringOrNull(it.getColumnIndex("port"))
                val user = it.getStringOrNull(it.getColumnIndex("user"))
                val tag = it.getStringOrNull(it.getColumnIndex("tag"))

                val camera = camera(
                    CAMname ?: "",
                    user ?: "",
                    pass ?: "",
                    port ?: "",
                    chanel ?: "",
                    subType ?: "",
                    ip ?:"",
                    tag ?:"",
                    id

                )
                cameraList.add(camera)
            }
        }

        return cameraList
    }


    fun getpassById(id: Int?): String? {
        val db: SQLiteDatabase = readableDatabase
        var pass: String? = null

        try {
            val selection = "id = ?"
            val selectionArgs = arrayOf(id.toString())
            val cursor = db.query(
                "camera_db",
                arrayOf("pass"),
                selection,
                selectionArgs,
                null,
                null,
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    pass = it.getStringOrNull(it.getColumnIndex("pass"))
                }
            }
        } catch (e: Exception) {
            // Handle the exception appropriately, e.g., log it.
        } finally {
            db.close()
        }

        return pass
    }



    fun updatecameraById(id: Int?, camera: camera?): Int {
        val db: SQLiteDatabase = writableDatabase
        val value = ContentValues()
        value.put("user", camera!!.user)
        value.put("subtype", camera.subtype)
        value.put("pass", camera.pass)
        value.put("ip", camera.ip)
        value.put("chanel", camera.chanel)
        value.put("CAMname", camera.CAMname)
        value.put("port", camera.port)
        value.put("tag", camera.tag)

        cameraLiveData.postValue(camera)
        println("camera_db updated" )

        return db.update("camera_db", value, "id=?", arrayOf(id.toString()))
    }




    fun clear_db_camera() {
        val db: SQLiteDatabase = writableDatabase
        db.delete("camera_db", null, null)
        db.close()
        Log.d("database message :", "camera_db cleared")
    }

    



    fun get_db_camera_LiveData(): LiveData<camera?> {
        return cameraLiveData
    }

//    fun get_from_db_camera_livedata(id: Int) {
//        val db: SQLiteDatabase = readableDatabase
//        val cursor = db.query(
//            "camera_db",
//            arrayOf("id", "user", "subtype", "pass","ip","chanel"),
//            "id=?",
//            arrayOf(id.toString()),
//            null,
//            null,
//            null
//        )
//        if (cursor != null && cursor.moveToFirst()) {
//            val camera = camera()
//            camera.user = cursor.getStringOrNull(cursor.getColumnIndex("user"))
//            camera.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
//            camera.pass = cursor.getStringOrNull(cursor.getColumnIndex("pass"))
//            camera.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
//            camera.chanel = cursor.getStringOrNull(cursor.getColumnIndex("chanel"))
//
//
//            cameraLiveData.postValue(camera)
//        }
//    }
//    fun getRoomTypeCount(startsWith: String): Int {
//        val db: SQLiteDatabase = readableDatabase
//        val countQuery = "SELECT COUNT(*) FROM camera_db WHERE subtype LIKE '$startsWith%'"
//        val cursor: Cursor = db.rawQuery(countQuery, null)
//        cursor.moveToFirst()
//        val count = cursor.getInt(0)
//        cursor.close()
//
//        return if (count > 0) count else 0
//    }

    fun getcameraByCAMname(CAMname: String): camera? {
        val db: SQLiteDatabase = readableDatabase
        val selection = "CAMname = ?"
        val selectionArgs = arrayOf(CAMname)
        val cursor = db.query(
            "camera_db",
            arrayOf("id", "user", "subtype", "pass", "ip", "chanel", "CAMname", "port", "tag"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val camera = camera()
            camera.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
            camera.user = cursor.getStringOrNull(cursor.getColumnIndex("user"))
            camera.subtype = cursor.getStringOrNull(cursor.getColumnIndex("subtype"))
            camera.pass = cursor.getStringOrNull(cursor.getColumnIndex("pass"))
            camera.ip = cursor.getStringOrNull(cursor.getColumnIndex("ip"))
            camera.chanel = cursor.getStringOrNull(cursor.getColumnIndex("chanel"))
            camera.CAMname = cursor.getStringOrNull(cursor.getColumnIndex("CAMname"))
            camera.port = cursor.getStringOrNull(cursor.getColumnIndex("port"))
            camera.tag = cursor.getStringOrNull(cursor.getColumnIndex("tag"))

            return camera
        }

        return null
    }



    companion object {
        private var instance: camera_db? = null

        @Synchronized
        fun getInstance(context: Context): camera_db {
            if (instance == null) {
                instance = camera_db(context.applicationContext)
            }
            return instance!!
        }
    }







}