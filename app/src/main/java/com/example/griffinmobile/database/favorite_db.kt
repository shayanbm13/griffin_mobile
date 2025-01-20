package com.example.griffinmobile.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.example.griffinmobile.mudels.Favorite

class favorite_db {



    class Favorite_db private constructor(context: Context) : SQLiteOpenHelper(context,"Favorite_db",null,1) {
        override fun onCreate(db: SQLiteDatabase?) {
            val CREATE_Favorite_db =
                "CREATE TABLE IF NOT EXISTS " + "Favorite_db" + "(id INTEGER PRIMARY KEY , type TEXT, name TEXT )"

            db!!.execSQL(CREATE_Favorite_db)
            Log.d("database message :", "Favorite_db created")
        }

        override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS Favorite_db")
            onCreate(db)
        }

        fun updateFavoriteNameById(id: Int?, FavoriteName: String?): Int {
            val db: SQLiteDatabase = writableDatabase
            try {
                val values = ContentValues()
                values.put("name", FavoriteName)

                return db.update("Favorite_db", values, "id=?", arrayOf(id.toString()))
            } catch (e: Exception) {
                println(e)
                return 0
            } finally {
                db.close()
            }
        }

        fun set_to_db_Favorite(Favorite: Favorite) {
            val db = writableDatabase
            val values = ContentValues()
            values.put("type", Favorite.type)
            values.put("name", Favorite.name)

            db.insert("Favorite_db", null, values)
            db.close()
            Log.d("database message :", "Favorite_db updated")

        }

        fun getFavoriteById(id: Int?): Favorite? {
            val db: SQLiteDatabase = readableDatabase
            var Favorite: Favorite? = null

            val cursor = db.query(
                "Favorite_db",
                null,
                "id=?",
                arrayOf(id.toString()),
                null,
                null,
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    Favorite = Favorite()
                    Favorite!!.id = it.getIntOrNull(it.getColumnIndex("id"))
                    Favorite!!.type = it.getStringOrNull(it.getColumnIndex("type"))
                    Favorite!!.name = it.getStringOrNull(it.getColumnIndex("name"))

                }
            }
                return Favorite
        }

        fun getFavoriteCount(): String {
            val db: SQLiteDatabase = readableDatabase
            val countQuery = "SELECT COUNT(*) FROM Favorite_db"
            val cursor = db.rawQuery(countQuery, null)
            var count = 0

            cursor?.use {
                if (it.moveToFirst()) {
                    count = it.getInt(0)
                }
            }

            return count.toString()
        }

        fun getAllFavorite(): List<Favorite> {
            val db: SQLiteDatabase = readableDatabase
            val FavoriteList = mutableListOf<Favorite>()

            val cursor = db.query(
                "Favorite_db",
                null,
                null,
                null,
                null,
                null,
                null
            )

            cursor?.use {
                while (cursor.moveToNext()) {
                    val Favorite = Favorite()
                    Favorite.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
                    Favorite.name = cursor.getStringOrNull(cursor.getColumnIndex("name"))
                    Favorite.type = cursor.getStringOrNull(cursor.getColumnIndex("type"))


                    FavoriteList.add(Favorite)
                }
            }

            return FavoriteList
        }


        fun delete_from_db_Favorite(id: Int?): Int {
            val db: SQLiteDatabase = writableDatabase
            return db.delete("Favorite_db", "id=?", arrayOf(id.toString()))

        }
        fun clear_db_favorite() {
            val db: SQLiteDatabase = writableDatabase
            db.delete("Favorite_db", null, null)
            db.close()
            Log.d("database message :", "FAVORITE_DB cleared")
        }



        fun getMaxId(): Int {
            val db: SQLiteDatabase = readableDatabase
            val cursor = db.rawQuery("SELECT MAX(id) FROM Favorite_db", null)
            var maxId = 0

            cursor?.use {
                if (it.moveToFirst()) {
                    maxId = it.getInt(0)
                }
            }

            return maxId
        }




        companion object {
            private var instance: Favorite_db? = null

            @Synchronized
            fun getInstance(context: Context): Favorite_db {
                if (instance == null) {
                    instance = Favorite_db(context.applicationContext)
                }
                return instance!!
            }
        }


    }
}