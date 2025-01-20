package com.example.griffinmobile.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.example.griffinmobile.mudels.Plug
import com.example.griffinmobile.mudels.fan
import com.example.griffinmobile.mudels.scenario

class scenario_db {



    class Scenario_db private constructor(context: Context) : SQLiteOpenHelper(context,"Scenario_db",null,1) {
        override fun onCreate(db: SQLiteDatabase?) {
            val CREATE_Scenario_db =
                "CREATE TABLE IF NOT EXISTS " + "Scenario_db" + "(id INTEGER PRIMARY KEY , light TEXT, thermostat TEXT, curtain TEXT, valve TEXT, fan TEXT, plug TEXT, scenario_name TEXT, music TEXT, in_ex TEXT, tag TEXT )"

            db!!.execSQL(CREATE_Scenario_db)
            Log.d("database message :", "Scenario_db created")
        }

        override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS Scenario_db")
            onCreate(db)
        }
        fun updateScenarioNameById(id: Int?, scenarioName: String?): Int {
            val db: SQLiteDatabase = writableDatabase
            try {
                val values = ContentValues()
                values.put("scenario_name", scenarioName)

                return db.update("Scenario_db", values, "id=?", arrayOf(id.toString()))
            } catch (e: Exception) {
                println(e)
                return 0
            } finally {
                db.close()
            }
        }

        fun getScenariosByTag(tag: String): List<scenario> {
            val scenarios = mutableListOf<scenario>()
            val db: SQLiteDatabase = readableDatabase
            val cursor = db.query(
                "Scenario_db",  // نام جدول
                null,           // ستون‌ها (null برای دریافت همه ستون‌ها)
                "tag=?",        // شرط جستجو
                arrayOf(tag),   // مقدار شرط
                null,           // Group by
                null,           // Having
                null            // Order by
            )

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val light = cursor.getString(cursor.getColumnIndexOrThrow("light"))
                    val thermostat = cursor.getString(cursor.getColumnIndexOrThrow("thermostat"))
                    val curtain = cursor.getString(cursor.getColumnIndexOrThrow("curtain"))
                    val valve = cursor.getString(cursor.getColumnIndexOrThrow("valve"))
                    val fan = cursor.getString(cursor.getColumnIndexOrThrow("fan"))
                    val plug = cursor.getString(cursor.getColumnIndexOrThrow("plug"))
                    val scenarioName = cursor.getString(cursor.getColumnIndexOrThrow("scenario_name"))
                    val music = cursor.getString(cursor.getColumnIndexOrThrow("music"))
                    val inEx = cursor.getString(cursor.getColumnIndexOrThrow("in_ex"))
                    val tagValue = cursor.getString(cursor.getColumnIndexOrThrow("tag"))

                    // ساخت یک شیء Scenario با داده‌های استخراج شده
                    val scenario = scenario( light, thermostat, curtain, valve, fan, plug, scenarioName,id, music, inEx, tagValue)
                    scenarios.add(scenario)
                }
                cursor.close()
            }
            db.close()
            return scenarios
        }
        fun set_to_db_Scenario(scenario: scenario) {
            val db = writableDatabase
            val values = ContentValues()
            values.put("light", scenario.light)
            values.put("thermostat", scenario.thermostat)
            values.put("curtain", scenario.curtain)
            values.put("valve", scenario.valve)
            values.put("fan", scenario.fan)
            values.put("plug", scenario.plug)
            values.put("music", scenario.music)
            values.put("scenario_name", scenario.scenario_name)
            values.put("in_ex", scenario.in_ex)
            values.put("tag", scenario.tag)
            db.insert("Scenario_db", null, values)
            db.close()
            Log.d("database message :", "Scenario_db updated")

        }
        fun getScenarioById(id: Int?): scenario? {
            val db: SQLiteDatabase = readableDatabase
            var scenario: scenario? = null

            val cursor = db.query(
                "Scenario_db",
                null,
                "id=?",
                arrayOf(id.toString()),
                null,
                null,
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    scenario = scenario()
                    scenario!!.id = it.getIntOrNull(it.getColumnIndex("id"))
                    scenario!!.light = it.getStringOrNull(it.getColumnIndex("light"))
                    scenario!!.thermostat = it.getStringOrNull(it.getColumnIndex("thermostat"))
                    scenario!!.curtain = it.getStringOrNull(it.getColumnIndex("curtain"))
                    scenario!!.valve = it.getStringOrNull(it.getColumnIndex("valve"))
                    scenario!!.fan = it.getStringOrNull(it.getColumnIndex("fan"))
                    scenario!!.plug = it.getStringOrNull(it.getColumnIndex("plug"))
                    scenario!!.in_ex = it.getStringOrNull(it.getColumnIndex("in_ex"))
                    scenario!!.music = it.getStringOrNull(it.getColumnIndex("music"))
                    scenario!!.scenario_name = it.getStringOrNull(it.getColumnIndex("scenario_name"))
                    scenario!!.tag = it.getStringOrNull(it.getColumnIndex("tag"))
                }
            }

            return scenario
        }
        fun getScenarioCount(): String {
            val db: SQLiteDatabase = readableDatabase
            val countQuery = "SELECT COUNT(*) FROM Scenario_db"
            val cursor = db.rawQuery(countQuery, null)
            var count = 0

            cursor?.use {
                if (it.moveToFirst()) {
                    count = it.getInt(0)
                }
            }

            return count.toString()
        }

        fun getAllScenario(): List<scenario> {
            val db: SQLiteDatabase = readableDatabase
            val scenarioList = mutableListOf<scenario>()

            val cursor = db.query(
                "Scenario_db",
                null,
                null,
                null,
                null,
                null,
                null
            )

            cursor?.use {
                while (cursor.moveToNext()) {
                    val scenario = scenario()
                    scenario.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
                    scenario.light = cursor.getStringOrNull(cursor.getColumnIndex("light"))
                    scenario.thermostat = cursor.getStringOrNull(cursor.getColumnIndex("thermostat"))
                    scenario.curtain = cursor.getStringOrNull(cursor.getColumnIndex("curtain"))
                    scenario.valve = cursor.getStringOrNull(cursor.getColumnIndex("valve"))
                    scenario.fan = cursor.getStringOrNull(cursor.getColumnIndex("fan"))
                    scenario.plug = cursor.getStringOrNull(cursor.getColumnIndex("plug"))
                    scenario.music = cursor.getStringOrNull(cursor.getColumnIndex("music"))
                    scenario.in_ex = cursor.getStringOrNull(cursor.getColumnIndex("in_ex"))
                    scenario.tag = cursor.getStringOrNull(cursor.getColumnIndex("tag"))
                    scenario.scenario_name = cursor.getStringOrNull(cursor.getColumnIndex("scenario_name"))

                    scenarioList.add(scenario)
                }
            }

            return scenarioList
        }


        fun delete_from_db_Scenariog(id: Int?): Int {
            val db: SQLiteDatabase = writableDatabase
            return db.delete("Scenario_db", "id=?", arrayOf(id.toString()))

        }


        fun updateLightById(id: Int?, light: String?): Int {
            val db: SQLiteDatabase = writableDatabase
            try {
                val value = ContentValues()
                value.put("light", light)


                return db.update("Scenario_db", value, "id=?", arrayOf(id.toString()))
            } catch (e: Exception) {
                println(e)
                return 0
            } finally {
                db.close()
            }
        }
        fun updateThermostatById(id: Int?, thermostat: String?): Int {
            val db: SQLiteDatabase = writableDatabase
            try {
                val value = ContentValues()
                value.put("thermostat", thermostat)


                return db.update("Scenario_db", value, "id=?", arrayOf(id.toString()))
            } catch (e: Exception) {
                println(e)
                return 0
            } finally {
                db.close()
            }
        }

        fun getMaxId(): Int {
            val db: SQLiteDatabase = readableDatabase
            val cursor = db.rawQuery("SELECT MAX(id) FROM Scenario_db", null)
            var maxId = 0

            cursor?.use {
                if (it.moveToFirst()) {
                    maxId = it.getInt(0)
                }
            }

            return maxId
        }


        fun updateCurtainById(id: Int?, curtain: String?): Int {
            val db: SQLiteDatabase = writableDatabase
            try {
                val value = ContentValues()
                value.put("curtain", curtain)


                return db.update("Scenario_db", value, "id=?", arrayOf(id.toString()))
            } catch (e: Exception) {
                println(e)
                return 0
            } finally {
                db.close()
            }
        }
        fun getScenarioByScenarioName(scenarioName: String?): scenario? {
            val db: SQLiteDatabase = readableDatabase
            var scenario: scenario? = null

            val cursor = db.query(
                "Scenario_db",
                null,
                "scenario_name=?",
                arrayOf(scenarioName),
                null,
                null,
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    scenario = scenario().apply {
                        id = it.getIntOrNull(it.getColumnIndex("id"))
                        light = it.getStringOrNull(it.getColumnIndex("light"))
                        thermostat = it.getStringOrNull(it.getColumnIndex("thermostat"))
                        curtain = it.getStringOrNull(it.getColumnIndex("curtain"))
                        valve = it.getStringOrNull(it.getColumnIndex("valve"))
                        fan = it.getStringOrNull(it.getColumnIndex("fan"))
                        plug = it.getStringOrNull(it.getColumnIndex("plug"))
                        music = it.getStringOrNull(it.getColumnIndex("music"))
                        in_ex = it.getStringOrNull(it.getColumnIndex("in_ex"))
                        tag = it.getStringOrNull(it.getColumnIndex("tag"))
                        scenario_name = it.getStringOrNull(it.getColumnIndex("scenario_name"))
                    }
                }
            }

            return scenario
        }
        fun updateValveById(id: Int?, valve: String?): Int {
            val db: SQLiteDatabase = writableDatabase
            try {
                val value = ContentValues()
                value.put("valve", valve)


                return db.update("Scenario_db", value, "id=?", arrayOf(id.toString()))
            } catch (e: Exception) {
                println(e)
                return 0
            } finally {
                db.close()
            }
        }
        fun updateFanById(id: Int?, fan: String?): Int {
            val db: SQLiteDatabase = writableDatabase
            try {
                val value = ContentValues()
                value.put("fan", fan)


                return db.update("Scenario_db", value, "id=?", arrayOf(id.toString()))
            } catch (e: Exception) {
                println(e)
                return 0
            } finally {
                db.close()
            }
        }
        fun updatemusicById(id: Int?, fan: String?): Int {
            val db: SQLiteDatabase = writableDatabase
            try {
                val value = ContentValues()
                value.put("music", fan)


                return db.update("Scenario_db", value, "id=?", arrayOf(id.toString()))
            } catch (e: Exception) {
                println(e)
                return 0
            } finally {
                db.close()
            }
        }

        fun get_from_db_scenario_By_name(name: String?): scenario? {
            val db: SQLiteDatabase = writableDatabase
            val cursor = db.query(
                "Scenario_db",
                arrayOf("id" , "light", "thermostat", "curtain", "valve" , "fan" , "plug" , "scenario_name", "music", "in_ex", "tag"),
                "scenario_name=?",
                arrayOf(name.toString()),
                null,
                null,
                null
            )
            var scenario: scenario? = null
            if (cursor != null) {
                cursor.use {
                    if (it.moveToFirst()) {
                        scenario = scenario()
                        scenario!!.id = it.getIntOrNull(it.getColumnIndex("id"))
                        scenario!!.light = it.getStringOrNull(it.getColumnIndex("light"))
                        scenario!!.thermostat = it.getStringOrNull(it.getColumnIndex("thermostat"))
                        scenario!!.curtain = it.getStringOrNull(it.getColumnIndex("curtain"))
                        scenario!!.valve = it.getStringOrNull(it.getColumnIndex("valve"))
                        scenario!!.fan = it.getStringOrNull(it.getColumnIndex("fan"))
                        scenario!!.plug = it.getStringOrNull(it.getColumnIndex("plug"))
                        scenario!!.music = it.getStringOrNull(it.getColumnIndex("music"))
                        scenario!!.in_ex = it.getStringOrNull(it.getColumnIndex("in_ex"))
                        scenario!!.tag = it.getStringOrNull(it.getColumnIndex("tag"))
                        scenario!!.scenario_name = it.getStringOrNull(it.getColumnIndex("scenario_name"))
                    }
                }

                return scenario
            }
            return null
        }
        fun getItemsWithInExIn(): List<scenario> {
            val db: SQLiteDatabase = readableDatabase
            val scenarioList = mutableListOf<scenario>()

            val cursor = db.query(
                "Scenario_db",
                null,
                "in_ex=?",
                arrayOf("in"),
                null,
                null,
                null
            )

            cursor?.use {
                while (cursor.moveToNext()) {
                    val scenario = scenario()
                    scenario.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
                    scenario.light = cursor.getStringOrNull(cursor.getColumnIndex("light"))
                    scenario.thermostat = cursor.getStringOrNull(cursor.getColumnIndex("thermostat"))
                    scenario.curtain = cursor.getStringOrNull(cursor.getColumnIndex("curtain"))
                    scenario.valve = cursor.getStringOrNull(cursor.getColumnIndex("valve"))
                    scenario.fan = cursor.getStringOrNull(cursor.getColumnIndex("fan"))
                    scenario.plug = cursor.getStringOrNull(cursor.getColumnIndex("plug"))
                    scenario.music = cursor.getStringOrNull(cursor.getColumnIndex("music"))
                    scenario.in_ex = cursor.getStringOrNull(cursor.getColumnIndex("in_ex"))
                    scenario.tag = cursor.getStringOrNull(cursor.getColumnIndex("tag"))
                    scenario.scenario_name = cursor.getStringOrNull(cursor.getColumnIndex("scenario_name"))

                    scenarioList.add(scenario)
                }
            }

            return scenarioList
        }
        fun getItemsWithInExex(): List<scenario> {
            val db: SQLiteDatabase = readableDatabase
            val scenarioList = mutableListOf<scenario>()

            val cursor = db.query(
                "Scenario_db",
                null,
                "in_ex=?",
                arrayOf("ex"),
                null,
                null,
                null
            )

            cursor?.use {
                while (cursor.moveToNext()) {
                    val scenario = scenario()
                    scenario.id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
                    scenario.light = cursor.getStringOrNull(cursor.getColumnIndex("light"))
                    scenario.thermostat = cursor.getStringOrNull(cursor.getColumnIndex("thermostat"))
                    scenario.curtain = cursor.getStringOrNull(cursor.getColumnIndex("curtain"))
                    scenario.valve = cursor.getStringOrNull(cursor.getColumnIndex("valve"))
                    scenario.fan = cursor.getStringOrNull(cursor.getColumnIndex("fan"))
                    scenario.plug = cursor.getStringOrNull(cursor.getColumnIndex("plug"))
                    scenario.music = cursor.getStringOrNull(cursor.getColumnIndex("music"))
                    scenario.in_ex = cursor.getStringOrNull(cursor.getColumnIndex("in_ex"))
                    scenario.tag = cursor.getStringOrNull(cursor.getColumnIndex("tag"))
                    scenario.scenario_name = cursor.getStringOrNull(cursor.getColumnIndex("scenario_name"))

                    scenarioList.add(scenario)
                }
            }

            return scenarioList
        }
        fun updateScenarioById(id: Int?, scenario: scenario): Int {
            val db: SQLiteDatabase = writableDatabase
            try {
                val values = ContentValues()
                values.put("id", scenario.id)
                values.put("light", scenario.light)
                values.put("thermostat", scenario.thermostat)
                values.put("curtain", scenario.curtain)
                values.put("valve", scenario.valve)
                values.put("fan", scenario.fan)
                values.put("plug", scenario.plug)
                values.put("scenario_name", scenario.scenario_name)
                values.put("music", scenario.music)
                values.put("in_ex", scenario.in_ex)
                values.put("tag", scenario.tag)

                return db.update("Scenario_db", values, "id=?", arrayOf(id.toString()))
                println("scenario_db Updated")
            } catch (e: Exception) {
                println(e)
                return 0
            } finally {
                db.close()
            }
        }
        fun deleteEmptyScenarios() {
            val db = writableDatabase
            val whereClause = "light=? AND thermostat=? AND curtain=? AND valve=? AND fan=? AND plug=? AND music=?"
            val whereArgs = arrayOf("", "", "", "", "", "", "")

            try {
                val deletedRows = db.delete("Scenario_db", whereClause, whereArgs)
                Log.d("DeleteEmptyScenarios", "Deleted $deletedRows rows")
            } catch (e: Exception) {
                Log.e("DeleteEmptyScenarios", "Error deleting empty scenarios", e)
            } finally {
                db.close()
            }
        }
        fun clear_db_scenario() {
            val db: SQLiteDatabase = writableDatabase
            db.delete("Scenario_db", null, null)
            db.close()
            Log.d("database message :", "SCENARIO_DB cleared")
        }

        fun updatePlugById(id: Int?, plug: String?): Int {
            val db: SQLiteDatabase = writableDatabase
            try {
                val value = ContentValues()
                value.put("plug", plug)


                return db.update("Scenario_db", value, "id=?", arrayOf(id.toString()))
            } catch (e: Exception) {
                println(e)
                return 0
            } finally {
                db.close()
            }

        }


        companion object {
            private var instance: Scenario_db? = null

            @Synchronized
            fun getInstance(context: Context): Scenario_db {
                if (instance == null) {
                    instance = Scenario_db(context.applicationContext)
                }
                return instance!!
            }
        }
    }
    
}