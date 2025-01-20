package com.example.griffinmobile

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelStore
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.griffinmobile.apdapters.RoomAdapter
import com.example.griffinmobile.database.Temperature_db
import com.example.griffinmobile.database.alarm_handeler_db
import com.example.griffinmobile.database.camera_db
import com.example.griffinmobile.database.curtain_db
import com.example.griffinmobile.database.door_db
import com.example.griffinmobile.database.fan_db
import com.example.griffinmobile.database.favorite_db
import com.example.griffinmobile.database.security_db
import com.example.griffinmobile.database.setting_simcard_accountsecurity_db
import com.example.griffinmobile.database.setting_simcard_security_db
import com.example.griffinmobile.database.setting_simcard_messageresponse_db
import com.example.griffinmobile.database.light_db
import com.example.griffinmobile.database.Elevator_db
import com.example.griffinmobile.database.Ir_db
import com.example.griffinmobile.database.home_db
import com.example.griffinmobile.database.plug_db
import com.example.griffinmobile.database.room_devices_db
import com.example.griffinmobile.database.rooms_db
import com.example.griffinmobile.database.scenario_db
import com.example.griffinmobile.database.setting_network_db
import com.example.griffinmobile.database.six_workert_db
import com.example.griffinmobile.database.valve_db

import com.example.griffinmobile.modules.getLocationFor45Seconds
import com.example.griffinmobile.modules.home
import com.example.griffinmobile.modules.rooms_v2
import com.example.griffinmobile.mudels.Light
import com.example.griffinmobile.mudels.rooms
import com.example.griffinmobile.mudels.Plug
import com.example.griffinmobile.mudels.SharedViewModel
import com.example.griffinmobile.mudels.sync_decoder
import com.example.griffinmobile.mudels.Thermostst
import com.example.griffinmobile.mudels.camera
import com.example.griffinmobile.mudels.checkIP
import com.example.griffinmobile.mudels.convertIpToBroadcast
import com.example.griffinmobile.mudels.curtain
import com.example.griffinmobile.mudels.fan
import com.example.griffinmobile.mudels.network_manual
import com.example.griffinmobile.mudels.receiveUdpMessage
import com.example.griffinmobile.mudels.scenario
import com.example.griffinmobile.mudels.six_workert
import com.example.griffinmobile.mudels.valve
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private var param1: String? = null
    private var param2: String? = null

    var  socket : DatagramSocket?=null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val sharedviewModel :SharedViewModel by viewModels()

    var rooms_counter=0
    var lights_counter=0
    var fans_counter=0
    var curtains_counter=0
    var valve_counter=0
    var temperature_counter=0
    var plugs_counter=0
    var cameras_counter=0
    var sixworker_counter=0
    var scenarios_counter=0
    var side = false


    var working = false


    fun findDuplicateIndices(list: List<String>): List<List<Int>> {
        val indexMap = mutableMapOf<String, MutableList<Int>>()

        // ذخیره ایندکس هر آیتم در دیکشنری
        list.forEachIndexed { index, item ->
            if (indexMap.containsKey(item)) {
                indexMap[item]?.add(index)
            } else {
                indexMap[item] = mutableListOf(index)
            }
        }

        // فیلتر کردن آیتم‌هایی که ایندکس‌های تکراری دارند
        return indexMap.values.filter { it.size > 1 }
    }

    fun filterStarsBeforeTilde(input: String): String {
        var result = input
        while (true) {
            // پیدا کردن موقعیت ~
            val tildeIndex = result.indexOf('~')

            // اگر ~ پیدا نشد، از حلقه خارج می‌شود
            if (tildeIndex == -1) break

            // حذف تمام * هایی که مستقیماً قبل از ~ قرار دارند
            val filteredPart = result.substring(0, tildeIndex).trimEnd('*')
            // ترکیب رشته بعد از ~ با بخش فیلتر شده
            result = filteredPart + result.substring(tildeIndex)
        }

        return result
    }

    fun substringBetween(input: String, startChar: Char, endChar: Char): String? {
        val startIndex = input.indexOf(startChar)
        val endIndex = input.indexOf(endChar, startIndex + 1)

        // بررسی اینکه هر دو کاراکتر پیدا شوند
        if (startIndex != -1 && endIndex != -1) {
            return input.substring(startIndex + 1, endIndex)
        }
        return null
    }

    fun mergeGroupsOfIndices(input: String, groupsOfIndices: List<List<Int>>): List<String> {
        // تقسیم ورودی بر اساس "***"
        val list = input.split("***").toMutableList()

        // ایجاد یک کپی از لیست اصلی برای نگهداری تغییرات
        val newList = list.toMutableList()

        // پیمایش گروه‌های ایندکس برای ادغام
        groupsOfIndices.forEach { indices ->
            if (indices.isEmpty()) return@forEach

            // دریافت طول رشته اولین ایندکس
            val length = list[indices[0]].length

            // ایجاد یک آرایه کاراکتری با طول رشته و مقدار اولیه '-'
            val combined = CharArray(length) { '-' }

            // ادغام رشته‌ها در ایندکس‌های مشخص شده
            indices.forEach { index ->
                list[index].forEachIndexed { charIndex, char ->
                    // اگر مقدار '0' یا '1' در هر رشته یافت شد، آن را جایگزین کنید
                    if (char == '0' || char == '1') {
                        combined[charIndex] = if (combined[charIndex] == '-') char else combined[charIndex] + char.toInt()
                    }
                }
            }

            // قرار دادن رشته ادغام‌شده در اولین ایندکس از گروه
            newList[indices[0]] = String(combined)

            // پاک‌سازی ایندکس‌های دیگر گروه
            indices.drop(1).forEach { index ->
                newList[index] = newList[index].replace(Regex("[01]"), "-")
            }
        }

        // حذف رشته‌های خالی که فقط شامل '-' هستند
        return newList.filter { it.contains(Regex("[01]")) }
    }
    fun removeDuplicates(strings: List<String>): List<String> {
        val seen = mutableSetOf<String>() // مجموعه برای پیگیری رشته‌های دیده‌شده
        val result = mutableListOf<String>() // لیست برای ذخیره رشته‌های بدون تکرار

        strings.forEach { str ->
            if (str !in seen) {
                seen.add(str) // اضافه کردن رشته به مجموعه
                result.add(str) // اضافه کردن رشته به لیست نتیجه
            }
        }

        return result
    }
    fun retainFirstIndexOnly(strings: List<String>, groupsOfIndices: List<List<Int>>): List<String> {
        // لیستی برای ذخیره ایندکس‌های که باید حذف شوند
        val indicesToRemove = mutableSetOf<Int>()

        // پیمایش گروه‌های ایندکس
        groupsOfIndices.forEach { indices ->
            if (indices.isEmpty()) return@forEach

            // فقط اولین ایندکس را حفظ می‌کنیم و بقیه را برای حذف آماده می‌کنیم
            indices.drop(1).forEach { index ->
                indicesToRemove.add(index)
            }
        }

        // ایجاد لیست جدید بر اساس ایندکس‌های باقی‌مانده
        val newList = strings.indices.filter { index -> index !in indicesToRemove }
            .map { index -> strings[index] }

        return newList
    }


    override fun attachBaseContext(newBase: Context?) {
        val config = Configuration(newBase?.resources?.configuration)
        config.fontScale = 1.0f // ثابت کردن اندازه فونت
        val customContext = newBase?.createConfigurationContext(config)
        super.attachBaseContext(customContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

    }

    override fun onResume() {
        super.onResume()
        val sms_switch = findViewById<SwitchMaterial>(R.id.sms_switch)
        val live_sms_switch = findViewById<SwitchMaterial>(R.id.live_sms_switch)
        val helper_text = findViewById<TextView>(R.id.helper_text)
        val textView_status = findViewById<TextView>(R.id.textView_status)
        val current_home_name = findViewById<TextView>(R.id.current_home_name)
        val sync_btn = findViewById<Button>(R.id.sync_btn)
        val set_btn = findViewById<Button>(R.id.set_btn)
        val get_ssid = findViewById<Button>(R.id.get_ssid)
        val get_ssidssid_input = findViewById<EditText>(R.id.ssid_input)
        val pasword_input = findViewById<EditText>(R.id.pasword_input)
        val api_key_input = findViewById<EditText>(R.id.api_key_input)
        val city_name = findViewById<EditText>(R.id.city_name)
        val home_style = findViewById<Button>(R.id.home_style)
        val change_Home = findViewById<Button>(R.id.change_Home)

        val homeDb=home_db.getInstance(this)
        val current = homeDb.getSelectedHome()
        if (current != null) {
            sharedviewModel.update_selected_home(current)
        }
        sharedviewModel.selected_home.observe(this, Observer { current_selected_home ->


            current_home_name.setText(current_selected_home.home_name)

            val databaseHelper = setting_network_db.getInstance(this)

            if (!databaseHelper.isDatabaseEmpty()) {

                val network_manual = databaseHelper.searchByHomes(current_selected_home.tag.toString())[0]
                get_ssidssid_input.setText(network_manual?.modem_ssid)
                pasword_input.setText(network_manual?.modem_password)
                api_key_input.setText(network_manual?.api_key)
                city_name.setText(network_manual?.city_name)

            }




            get_ssid.setOnClickListener {
                val wifiManager = this.applicationContext.getSystemService(
                    Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                get_ssidssid_input.setText(wifiInfo.ssid.replace("\"" ,""))
            }
            set_btn.setOnClickListener {
                if (get_ssidssid_input.text.toString().isNotEmpty() || get_ssidssid_input.text.toString() != "" ){

                    if (databaseHelper.isEmptynetwork_tabale()) {
                        var network_manual = network_manual()
                        network_manual.modem_ssid = get_ssidssid_input.text.toString()
                        network_manual.modem_password = pasword_input.text.toString()
                        network_manual.api_key = api_key_input.text.toString()
                        network_manual.city_name = city_name.text.toString()
                        network_manual.homes = current_selected_home.tag

                        databaseHelper.set_to_db_network_manual(network_manual)
                        Toast.makeText(this, "seted...", Toast.LENGTH_SHORT).show()

                    } else {
                        var network_manual = network_manual()
                        network_manual.modem_ssid = get_ssidssid_input.text.toString()
                        network_manual.modem_password = pasword_input.text.toString()
                        network_manual.api_key = api_key_input.text.toString()
                        network_manual.city_name = city_name.text.toString()

                        network_manual.id=1
                        databaseHelper.update_db_network_manual(network_manual)
                        Toast.makeText(this, "changes seted...", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            sync_btn.setOnClickListener {
                showPopup_receive_or_send()
            }


            if (sms_switch.isActivated) {
                live_sms_switch.isEnabled = true
                helper_text.visibility = View.INVISIBLE
                textView_status.isEnabled = false

            } else {
                live_sms_switch.isEnabled = false
                live_sms_switch.isChecked = false
                helper_text.visibility = View.VISIBLE
                textView_status.isEnabled = true
            }

            sms_switch.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    live_sms_switch.isEnabled = true
                    helper_text.visibility = View.INVISIBLE
                    textView_status.isEnabled = false

                } else {
                    live_sms_switch.isEnabled = false
                    live_sms_switch.isChecked = false
                    helper_text.visibility = View.VISIBLE
                    textView_status.isEnabled = true
                }

            }


            change_Home.setOnClickListener {
                val dialog =Dialog(this)

                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.window!!.setContentView(R.layout.change_home_view)
                val add_home_btn_3=dialog.findViewById<LottieAnimationView>(R.id.add_home_btn_3)
                val add_home_btn_2=dialog.findViewById<LottieAnimationView>(R.id.add_home_btn_2)
                val add_home_btn_1=dialog.findViewById<LottieAnimationView>(R.id.add_home_btn_1)

                val add_home_name_3=dialog.findViewById<TextView>(R.id.add_home_name_3)
                val add_home_name_2=dialog.findViewById<TextView>(R.id.add_home_name_2)
                val add_home_name_1=dialog.findViewById<TextView>(R.id.add_home_name_1)

                dialog.setCanceledOnTouchOutside(false)

//           Edit Popup
                val dialogedit = Dialog(this)
                dialogedit.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                dialogedit.setCanceledOnTouchOutside(false)
                dialogedit.setContentView(R.layout.home_edit)
                val home_name = dialogedit.findViewById<EditText>(R.id.home_name)
                val location_n = dialogedit.findViewById<TextView>(R.id.location_n)
                val location_e = dialogedit.findViewById<TextView>(R.id.location_e)
                val find_location_btn = dialogedit.findViewById<Button>(R.id.find_location_btn)
                val done_edit_btn = dialogedit.findViewById<Button>(R.id.done_edit_btn)
                val delete_home = dialogedit.findViewById<Button>(R.id.delete_home)
                val locating_page = dialogedit.findViewById<ConstraintLayout>(R.id.locating_page)
                val main_page = dialogedit.findViewById<ConstraintLayout>(R.id.main_page)


                fun locating_on(){
                    main_page.visibility=View.GONE

                    locating_page.alpha=0f
                    locating_page.visibility=View.VISIBLE
                    locating_page.animate()
                        .alpha(1f)
                        .setDuration(50).start()
                }
                fun locating_off(){
                    locating_page.visibility=View.GONE

                    main_page.alpha=0f
                    main_page.visibility=View.VISIBLE
                    main_page.animate()
                        .alpha(1f)
                        .setDuration(50).start()

                }
                fun startCountdown(textView: TextView) {
                    val countdownTimer = object : CountDownTimer(45000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            // نمایش زمان باقی‌مانده به فرمت "Xs"
                            val secondsLeft = millisUntilFinished / 1000
                            textView.text = "$secondsLeft s"
                        }

                        override fun onFinish() {
                            // وقتی شمارش معکوس تمام شد
                            textView.text = "0 s"
                        }
                    }

                    countdownTimer.start()  // شروع شمارش معکوس
                }
                find_location_btn.setOnClickListener {
                    locating_on()
                    val cuntdown = dialogedit.findViewById<TextView>(R.id.cuntdown)
                    startCountdown(cuntdown)
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                    // فراخوانی تابع getBestLocation برای دریافت بهترین موقعیت مکانی
                    getLocationFor45Seconds(applicationContext, this, fusedLocationClient) { location ->
                        location?.let {
                            val latitude = it.latitude
                            val longitude = it.longitude
                            println("Last Location: Latitude: $latitude, Longitude: $longitude")

                            location_n.setText(latitude.toString())
                            location_e.setText(longitude.toString())
                            locating_off()

                        } ?: run {
                            println("No location detected")
                            Toast.makeText(this, "Please turn on your GPS", Toast.LENGTH_SHORT).show()
                        }
                    }


                }



                val homeDb=home_db.getInstance(this)
                var home_count = homeDb.getHomeCount()
                if (home_count > 0){
                    var homes= homeDb.getAllhome()
                    when(home_count){
                        1 ->{
                            add_home_btn_1.setAnimation(R.raw.home_animation)
                            add_home_btn_1.playAnimation()
                            add_home_name_1.text = homes[0]!!.home_name
                            println(homes[0]!!.home_name)

                            add_home_btn_2.setAnimation(R.raw.add_animation)
                            add_home_btn_2.playAnimation()
                            add_home_btn_3.setAnimation(R.raw.add_animation)
                            add_home_btn_3.playAnimation()
                            add_home_name_2.setText("Add")
                            add_home_name_3.setText("Add")
                        }
                        2 ->{
                            add_home_btn_1.setAnimation(R.raw.home_animation)
                            add_home_btn_1.playAnimation()
                            add_home_name_1.setText(homes[0]!!.home_name)
                            add_home_btn_2.setAnimation(R.raw.home_animation)
                            add_home_btn_2.playAnimation()
                            add_home_name_2.setText(homes[1]!!.home_name)


                            add_home_btn_3.setAnimation(R.raw.add_animation)
                            add_home_btn_3.playAnimation()
                            add_home_name_3.setText("Add")

                        }
                        3 ->{
                            add_home_btn_1.setAnimation(R.raw.home_animation)
                            add_home_btn_1.playAnimation()
                            add_home_name_1.setText(homes[0]!!.home_name)
                            add_home_btn_2.setAnimation(R.raw.home_animation)
                            add_home_btn_2.playAnimation()
                            add_home_name_2.setText(homes[1]!!.home_name)
                            add_home_btn_3.setAnimation(R.raw.home_animation)
                            add_home_btn_3.playAnimation()
                            add_home_name_3.setText(homes[2]!!.home_name)

                        }

                    }

                    add_home_btn_1.setOnLongClickListener {
                        when(home_count) {
                            1 -> {

                                dialog.dismiss()
                                dialogedit.show()

                                home_name.setText(homes[0]!!.home_name)
                                location_n.text = homes[0]!!.location!!.split(",")[0]
                                location_e.text = homes[0]!!.location!!.split(",")[1]
                                done_edit_btn.setOnClickListener {

                                    if ((home_name.text.toString() != "") && (home_name.text.isNotEmpty()) && (location_n.text != "_") && (location_e.text != "_")) {
                                        homes[0]!!.home_name = home_name.text.toString()
                                        homes[0]!!.tag = homes[0]!!.tag
                                        homes[0]!!.location =
                                            "${location_n.text.trim()},${location_e.text.trim()}"
                                        homeDb.updateRoomById(homes[0]!!.id, homes[0]!!)
                                        homes = homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()

                                        when(home_count){
                                            1 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }
                                        dialog.show()

                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Please fill all fields",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }


                                }
                                delete_home.setOnClickListener {

                                    val networkManualDB =setting_network_db.getInstance(this)
                                    val current_network=networkManualDB.searchByHomes("h")
                                    val roomDevicesDb =room_devices_db.getInstance(this)
                                    val rooms=roomDevicesDb.getRoomsByHome("h")


                                    val delete_dialog = Dialog(this)
                                    delete_dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                                    // تنظیم لایه دلخواه برای دیالوگ
                                    delete_dialog.setContentView(R.layout.delete_all_or_pole)
                                    val text_msg = delete_dialog.findViewById<TextView>(R.id.text_msg)
                                    val yes_delete = delete_dialog.findViewById<Button>(R.id.yes_delete)
                                    val cancel_delete = delete_dialog.findViewById<Button>(R.id.cancel_delete)
                                    text_msg.setText("Are you sure you want to delete this room? all devices in this room will be deleted ")
                                    yes_delete.setText("Delete")
                                    cancel_delete.setText("Cancel")
                                    delete_dialog.setCanceledOnTouchOutside(false)
                                    delete_dialog.show()
                                    yes_delete.setOnClickListener {
                                        if (!networkManualDB.isDatabaseEmpty()){
                                            current_network[0].id?.let { it1 ->
                                                networkManualDB.deleteById(
                                                    it1
                                                )
                                            }

                                        }

                                        for(room_device in rooms){

                                            val rooms_db=rooms_db.getInstance(this)
                                            val room= rooms_db.get_from_db_rooms(room_device.room_id!!.toInt())
                                            val curtainDb= curtain_db.getInstance(this)
                                            val fanDb= fan_db.getInstance(this)
                                            val lightDb= light_db.getInstance(this)
                                            val plugtDb= plug_db.getInstance(this)
                                            val tempDb=Temperature_db.getInstance(this)
                                            val valveDb= valve_db.getInstance(this)
                                            val sixcDB= six_workert_db.getInstance(this)
                                            val lights = lightDb.getAllLightsByRoomName(room!!.room_name)
                                            val fans = fanDb.getfansByRoomName(room!!.room_name)
                                            val curtains = curtainDb.getAllcurtainsByRoomName(room.room_name)
                                            val plugs = plugtDb.getPlugsByRoomName(room.room_name)
                                            val valve = valveDb.getvalvesByRoomName(room.room_name)
                                            val temps  = tempDb.getThermostatsByRoomName(room.room_name)
                                            val sixc = sixcDB.getAllsix_workerts()

                                            for (light in lights){

                                                if (light != null) {
                                                    lightDb.delete_from_db_light(light.id)
                                                }
                                                for (six in sixc){
                                                    if (six!!.mac == light!!.mac){
                                                        sixcDB.delete_from_db_six_workert(six.id)
                                                    }
                                                }

                                            }
                                            for (fan in fans){
                                                if (fan != null) {
                                                    fanDb.delete_from_db_fan(fan.id)
                                                }
                                            }
                                            for(curtain in curtains){
                                                if (curtain != null) {
                                                    curtainDb.delete_from_db_curtain(curtain.id)
                                                }
                                            }
                                            for (plug in plugs){
                                                if (plug != null) {
                                                    plugtDb.delete_from_db_Plug(plug.id)
                                                }
                                            }

                                            for (valve in valve){
                                                if (valve != null) {
                                                    valveDb.delete_from_db_valve(valve.id)
                                                }
                                            }
                                            for (temp in temps){
                                                if (temp != null) {
                                                    tempDb.delete_from_db_Temprature(temp.id)
                                                }
                                            }
                                            val room_db = com.example.griffinmobile.database.rooms_db.getInstance(this)
                                            room_db.delete_from_db_rooms(room.id)

                                            room_devices_db.getInstance(this).deleteRoomByRoomId(room.id.toString())
                                            Toast.makeText(this, "Room and devices deleted", Toast.LENGTH_SHORT).show()

                                            delete_dialog.dismiss()



                                        }
                                        homeDb.delete_from_db_home(homes[0]!!.id)
                                        var home_count2 = homeDb.getHomeCount()
                                        if (home_count2==0){
                                            var home1=home()
                                            home1.home_name="home"
                                            home1.tag="h"
                                            home1.location="_,_"
                                            home1.current_select="true"
                                            homeDb.set_to_db_home(home1)

                                        }
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                homes[0]?.id?.let { it1 ->
                                                    homeDb.updateCurrentSelectById(
                                                        it1
                                                    )
                                                }
                                                homes[0]?.let { it1 ->
                                                    sharedviewModel.update_selected_home(
                                                        it1
                                                    )
                                                }
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                homes[1]?.id?.let { it1 ->
                                                    homeDb.updateCurrentSelectById(
                                                        it1
                                                    )
                                                }
                                                homes[1]?.let { it1 ->
                                                    sharedviewModel.update_selected_home(
                                                        it1
                                                    )
                                                }
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }

                                        dialog.show()
                                        delete_dialog.dismiss()
                                    }
                                    cancel_delete.setOnClickListener {
                                        Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()

                                        delete_dialog.dismiss()
                                        dialogedit.dismiss()
                                        dialog.dismiss()
                                    }




                                }


                            }

                            2 -> {
                                dialog.dismiss()
                                dialogedit.show()

                                home_name.setText(homes[0]!!.home_name)
                                location_n.text = homes[0]!!.location!!.split(",")[0]
                                location_e.text = homes[0]!!.location!!.split(",")[1]
                                done_edit_btn.setOnClickListener {

                                    if ((home_name.text.toString() != "") && (home_name.text.isNotEmpty()) && (location_n.text != "_") && (location_e.text != "_")) {
                                        homes[0]!!.home_name = home_name.text.toString()
                                        homes[0]!!.tag = homes[0]!!.tag
                                        homes[0]!!.location =
                                            "${location_n.text.trim()},${location_e.text.trim()}"
                                        homeDb.updateRoomById(homes[0]!!.id, homes[0]!!)
                                        homes = homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }
                                        dialog.show()

                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Please fill all fields",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }


                                }
                                delete_home.setOnClickListener {

                                    val networkManualDB =setting_network_db.getInstance(this)
                                    val current_network=networkManualDB.searchByHomes("h")
                                    val roomDevicesDb =room_devices_db.getInstance(this)
                                    val rooms=roomDevicesDb.getRoomsByHome("h")


                                    val delete_dialog = Dialog(this)
                                    delete_dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                                    // تنظیم لایه دلخواه برای دیالوگ
                                    delete_dialog.setContentView(R.layout.delete_all_or_pole)
                                    val text_msg = delete_dialog.findViewById<TextView>(R.id.text_msg)
                                    val yes_delete = delete_dialog.findViewById<Button>(R.id.yes_delete)
                                    val cancel_delete = delete_dialog.findViewById<Button>(R.id.cancel_delete)
                                    text_msg.setText("Are you sure you want to delete this room? all devices in this room will be deleted ")
                                    yes_delete.setText("Delete")
                                    cancel_delete.setText("Cancel")
                                    delete_dialog.setCanceledOnTouchOutside(false)
                                    delete_dialog.show()
                                    yes_delete.setOnClickListener {
                                        if (!networkManualDB.isDatabaseEmpty()){
                                            current_network[0].id?.let { it1 ->
                                                networkManualDB.deleteById(
                                                    it1
                                                )
                                            }

                                        }

                                        for(room_device in rooms){

                                            val rooms_db=rooms_db.getInstance(this)
                                            val room= rooms_db.get_from_db_rooms(room_device.room_id!!.toInt())
                                            val curtainDb= curtain_db.getInstance(this)
                                            val fanDb= fan_db.getInstance(this)
                                            val lightDb= light_db.getInstance(this)
                                            val plugtDb= plug_db.getInstance(this)
                                            val tempDb=Temperature_db.getInstance(this)
                                            val valveDb= valve_db.getInstance(this)
                                            val sixcDB= six_workert_db.getInstance(this)
                                            val fans = fanDb.getfansByRoomName(room!!.room_name)
                                            val lights = lightDb.getAllLightsByRoomName(room!!.room_name)
                                            val curtains = curtainDb.getAllcurtainsByRoomName(room.room_name)
                                            val plugs = plugtDb.getPlugsByRoomName(room.room_name)
                                            val valve = valveDb.getvalvesByRoomName(room.room_name)
                                            val temps  = tempDb.getThermostatsByRoomName(room.room_name)
                                            val sixc = sixcDB.getAllsix_workerts()

                                            for (light in lights){

                                                if (light != null) {
                                                    lightDb.delete_from_db_light(light.id)
                                                }
                                                for (six in sixc){
                                                    if (six!!.mac == light!!.mac){
                                                        sixcDB.delete_from_db_six_workert(six.id)
                                                    }
                                                }

                                            }
                                            for(curtain in curtains){
                                                if (curtain != null) {
                                                    curtainDb.delete_from_db_curtain(curtain.id)
                                                }
                                            }
                                            for (fan in fans){
                                                if (fan != null) {
                                                    fanDb.delete_from_db_fan(fan.id)
                                                }
                                            }
                                            for (plug in plugs){
                                                if (plug != null) {
                                                    plugtDb.delete_from_db_Plug(plug.id)
                                                }
                                            }

                                            for (valve in valve){
                                                if (valve != null) {
                                                    valveDb.delete_from_db_valve(valve.id)
                                                }
                                            }
                                            for (temp in temps){
                                                if (temp != null) {
                                                    tempDb.delete_from_db_Temprature(temp.id)
                                                }
                                            }
                                            val room_db = com.example.griffinmobile.database.rooms_db.getInstance(this)
                                            room_db.delete_from_db_rooms(room.id)

                                            room_devices_db.getInstance(this).deleteRoomByRoomId(room.id.toString())
                                            Toast.makeText(this, "Room and devices deleted", Toast.LENGTH_SHORT).show()

                                            delete_dialog.dismiss()



                                        }
                                        homeDb.delete_from_db_home(homes[0]!!.id)
                                        var home_count2 = homeDb.getHomeCount()
                                        if (home_count2==0){
                                            var home1=home()
                                            home1.home_name="home"
                                            home1.tag="h"
                                            home1.location="_,_"
                                            home1.current_select="true"
                                            homeDb.set_to_db_home(home1)

                                        }
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                homes[0]?.id?.let { it1 ->
                                                    homeDb.updateCurrentSelectById(
                                                        it1
                                                    )
                                                }
                                                homes[0]?.let { it1 ->
                                                    sharedviewModel.update_selected_home(
                                                        it1
                                                    )
                                                }
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                homes[1]?.id?.let { it1 ->
                                                    homeDb.updateCurrentSelectById(
                                                        it1
                                                    )
                                                }
                                                homes[1]?.let { it1 ->
                                                    sharedviewModel.update_selected_home(
                                                        it1
                                                    )
                                                }
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }

                                        dialog.show()
                                        delete_dialog.dismiss()
                                    }
                                    cancel_delete.setOnClickListener {
                                        Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()

                                        delete_dialog.dismiss()
                                        dialogedit.dismiss()
                                        dialog.dismiss()
                                    }




                                }
                            }
                            3 ->{


                                dialog.dismiss()
                                dialogedit.show()

                                home_name.setText(homes[0]!!.home_name)
                                location_n.text = homes[0]!!.location!!.split(",")[0]
                                location_e.text = homes[0]!!.location!!.split(",")[1]
                                done_edit_btn.setOnClickListener {

                                    if ((home_name.text.toString() != "") && (home_name.text.isNotEmpty()) && (location_n.text != "_") && (location_e.text != "_")) {
                                        homes[0]!!.home_name = home_name.text.toString()
                                        homes[0]!!.tag = homes[0]!!.tag
                                        homes[0]!!.location =
                                            "${location_n.text.trim()},${location_e.text.trim()}"
                                        homeDb.updateRoomById(homes[0]!!.id, homes[0]!!)
                                        homes = homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }
                                        dialog.show()

                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Please fill all fields",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }


                                }
                                delete_home.setOnClickListener {

                                    val networkManualDB =setting_network_db.getInstance(this)
                                    val current_network=networkManualDB.searchByHomes("h")
                                    val roomDevicesDb =room_devices_db.getInstance(this)
                                    val rooms=roomDevicesDb.getRoomsByHome("h")


                                    val delete_dialog = Dialog(this)
                                    delete_dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                                    // تنظیم لایه دلخواه برای دیالوگ
                                    delete_dialog.setContentView(R.layout.delete_all_or_pole)
                                    val text_msg = delete_dialog.findViewById<TextView>(R.id.text_msg)
                                    val yes_delete = delete_dialog.findViewById<Button>(R.id.yes_delete)
                                    val cancel_delete = delete_dialog.findViewById<Button>(R.id.cancel_delete)
                                    text_msg.setText("Are you sure you want to delete this room? all devices in this room will be deleted ")
                                    yes_delete.setText("Delete")
                                    cancel_delete.setText("Cancel")
                                    delete_dialog.setCanceledOnTouchOutside(false)
                                    delete_dialog.show()
                                    yes_delete.setOnClickListener {
                                        if (!networkManualDB.isDatabaseEmpty()){
                                            current_network[0].id?.let { it1 ->
                                                networkManualDB.deleteById(
                                                    it1
                                                )
                                            }

                                        }

                                        for(room_device in rooms){

                                            val rooms_db=rooms_db.getInstance(this)
                                            val room= rooms_db.get_from_db_rooms(room_device.room_id!!.toInt())
                                            val curtainDb= curtain_db.getInstance(this)
                                            val fanDb= fan_db.getInstance(this)
                                            val lightDb= light_db.getInstance(this)
                                            val plugtDb= plug_db.getInstance(this)
                                            val tempDb=Temperature_db.getInstance(this)
                                            val valveDb= valve_db.getInstance(this)
                                            val sixcDB= six_workert_db.getInstance(this)
                                            val lights = lightDb.getAllLightsByRoomName(room!!.room_name)
                                            val curtains = curtainDb.getAllcurtainsByRoomName(room.room_name)
                                            val fans = fanDb.getfansByRoomName(room!!.room_name)
                                            val plugs = plugtDb.getPlugsByRoomName(room.room_name)
                                            val valve = valveDb.getvalvesByRoomName(room.room_name)
                                            val temps  = tempDb.getThermostatsByRoomName(room.room_name)
                                            val sixc = sixcDB.getAllsix_workerts()

                                            for (light in lights){

                                                if (light != null) {
                                                    lightDb.delete_from_db_light(light.id)
                                                }
                                                for (six in sixc){
                                                    if (six!!.mac == light!!.mac){
                                                        sixcDB.delete_from_db_six_workert(six.id)
                                                    }
                                                }

                                            }
                                            for (fan in fans){
                                                if (fan != null) {
                                                    fanDb.delete_from_db_fan(fan.id)
                                                }
                                            }
                                            for(curtain in curtains){
                                                if (curtain != null) {
                                                    curtainDb.delete_from_db_curtain(curtain.id)
                                                }
                                            }
                                            for (plug in plugs){
                                                if (plug != null) {
                                                    plugtDb.delete_from_db_Plug(plug.id)
                                                }
                                            }

                                            for (valve in valve){
                                                if (valve != null) {
                                                    valveDb.delete_from_db_valve(valve.id)
                                                }
                                            }
                                            for (temp in temps){
                                                if (temp != null) {
                                                    tempDb.delete_from_db_Temprature(temp.id)
                                                }
                                            }
                                            val room_db = com.example.griffinmobile.database.rooms_db.getInstance(this)
                                            room_db.delete_from_db_rooms(room.id)

                                            room_devices_db.getInstance(this).deleteRoomByRoomId(room.id.toString())
                                            Toast.makeText(this, "Room and devices deleted", Toast.LENGTH_SHORT).show()

                                            delete_dialog.dismiss()



                                        }
                                        homeDb.delete_from_db_home(homes[0]!!.id)
                                        var home_count2 = homeDb.getHomeCount()
                                        if (home_count2==0){
                                            var home1=home()
                                            home1.home_name="home"
                                            home1.tag="h"
                                            home1.location="_,_"
                                            home1.current_select="true"
                                            homeDb.set_to_db_home(home1)

                                        }
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                homes[0]?.id?.let { it1 ->
                                                    homeDb.updateCurrentSelectById(
                                                        it1
                                                    )
                                                }
                                                homes[0]?.let { it1 ->
                                                    sharedviewModel.update_selected_home(
                                                        it1
                                                    )
                                                }
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                homes[1]?.id?.let { it1 ->
                                                    homeDb.updateCurrentSelectById(
                                                        it1
                                                    )
                                                }
                                                homes[1]?.let { it1 ->
                                                    sharedviewModel.update_selected_home(
                                                        it1
                                                    )
                                                }
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }

                                        dialog.show()
                                        delete_dialog.dismiss()
                                    }
                                    cancel_delete.setOnClickListener {
                                        Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()

                                        delete_dialog.dismiss()
                                        dialogedit.dismiss()
                                        dialog.dismiss()
                                    }




                                }



                            }else->{
                            dialog.dismiss()
                            dialogedit.show()

                            home_name.setText("")
                            location_n.text ="_"
                            location_e.text ="_"
                            done_edit_btn.setOnClickListener {

                                if ((home_name.text.toString() !="") && (home_name.text.isNotEmpty()) &&(location_n.text !="_")&&(location_e.text !="_")  ){
                                    val new_home = home()
                                    new_home!!.home_name = home_name.text.toString()
                                    new_home!!.tag = "v1"
                                    new_home!!.location = "${location_n.text.trim()},${location_e.text.trim()}"
                                    homeDb.set_to_db_home(new_home)
                                    homes= homeDb.getAllhome()
                                    home_count = homeDb.getHomeCount()
                                    dialogedit.dismiss()
                                    when(home_count){
                                        1 ->{
                                            add_home_btn_1.setAnimation(R.raw.home_animation)
                                            add_home_btn_1.playAnimation()
                                            add_home_name_1.text = homes[0]!!.home_name
                                            println(homes[0]!!.home_name)

                                            add_home_btn_2.setAnimation(R.raw.add_animation)
                                            add_home_btn_2.playAnimation()
                                            add_home_btn_3.setAnimation(R.raw.add_animation)
                                            add_home_btn_3.playAnimation()
                                            add_home_name_2.setText("Add")
                                            add_home_name_3.setText("Add")
                                        }
                                        2 ->{
                                            add_home_btn_1.setAnimation(R.raw.home_animation)
                                            add_home_btn_1.playAnimation()
                                            add_home_name_1.setText(homes[0]!!.home_name)
                                            add_home_btn_2.setAnimation(R.raw.home_animation)
                                            add_home_btn_2.playAnimation()
                                            add_home_name_2.setText(homes[1]!!.home_name)


                                            add_home_btn_3.setAnimation(R.raw.add_animation)
                                            add_home_btn_3.playAnimation()
                                            add_home_name_3.setText("Add")

                                        }
                                        3 ->{
                                            add_home_btn_1.setAnimation(R.raw.home_animation)
                                            add_home_btn_1.playAnimation()
                                            add_home_name_1.setText(homes[0]!!.home_name)
                                            add_home_btn_2.setAnimation(R.raw.home_animation)
                                            add_home_btn_2.playAnimation()
                                            add_home_name_2.setText(homes[1]!!.home_name)
                                            add_home_btn_3.setAnimation(R.raw.home_animation)
                                            add_home_btn_3.playAnimation()
                                            add_home_name_3.setText(homes[2]!!.home_name)

                                        }

                                    }
                                    dialog.show()

                                }else{
                                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                }



                            }
                            delete_home.setOnClickListener {

                                dialog.dismiss()
                                dialogedit.dismiss()



                            }

                        }
                        }




                        true
                    }

                    add_home_btn_2.setOnLongClickListener {
                        when(home_count){
                            1->{
                                dialog.dismiss()
                                dialogedit.show()

                                home_name.setText("")
                                location_n.text ="_"
                                location_e.text ="_"
                                done_edit_btn.setOnClickListener {

                                    if ((home_name.text.toString() !="") && (home_name.text.isNotEmpty()) &&(location_n.text !="_")&&(location_e.text !="_")  ){
                                        val new_home = home()
                                        new_home!!.home_name = home_name.text.toString()
                                        new_home!!.tag = "v1"
                                        new_home!!.location = "${location_n.text.trim()},${location_e.text.trim()}"
                                        homeDb.set_to_db_home(new_home)
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }
                                        dialog.show()

                                    }else{
                                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }



                                }
                                delete_home.setOnClickListener {

                                    dialog.dismiss()
                                    dialogedit.dismiss()


                                }

                            }
                            2->{
                                dialog.dismiss()
                                dialogedit.show()

                                home_name.setText(homes[1]!!.home_name)
                                location_n.text = homes[1]!!.location!!.split(",")[0]
                                location_e.text = homes[1]!!.location!!.split(",")[1]
                                done_edit_btn.setOnClickListener {

                                    if ((home_name.text.toString() !="") && (home_name.text.isNotEmpty()) &&(location_n.text !="_")&&(location_e.text !="_")  ){
                                        homes[1]!!.home_name = home_name.text.toString()
                                        homes[1]!!.tag = homes[1]!!.tag
                                        homes[1]!!.location = "${location_n.text.trim()},${location_e.text.trim()}"
                                        homeDb.updateRoomById(homes[1]!!.id, homes[1]!!)
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }
                                        dialog.show()

                                    }else{
                                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }



                                }
                                delete_home.setOnClickListener {

                                    val networkManualDB =setting_network_db.getInstance(this)
                                    val current_network=networkManualDB.searchByHomes("v1")
                                    val roomDevicesDb =room_devices_db.getInstance(this)
                                    val rooms=roomDevicesDb.getRoomsByHome("v1")


                                    val delete_dialog = Dialog(this)
                                    delete_dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                                    // تنظیم لایه دلخواه برای دیالوگ
                                    delete_dialog.setContentView(R.layout.delete_all_or_pole)
                                    val text_msg = delete_dialog.findViewById<TextView>(R.id.text_msg)
                                    val yes_delete = delete_dialog.findViewById<Button>(R.id.yes_delete)
                                    val cancel_delete = delete_dialog.findViewById<Button>(R.id.cancel_delete)
                                    text_msg.setText("Are you sure you want to delete this room? all devices in this room will be deleted ")
                                    yes_delete.setText("Delete")
                                    cancel_delete.setText("Cancel")
                                    delete_dialog.setCanceledOnTouchOutside(false)
                                    delete_dialog.show()
                                    yes_delete.setOnClickListener {
                                        if (!networkManualDB.isDatabaseEmpty()){
                                            current_network[0].id?.let { it1 ->
                                                networkManualDB.deleteById(
                                                    it1
                                                )
                                            }

                                        }


                                        for(room_device in rooms){

                                            val rooms_db=rooms_db.getInstance(this)
                                            val room= rooms_db.get_from_db_rooms(room_device.room_id!!.toInt())
                                            val curtainDb= curtain_db.getInstance(this)
                                            val fanDb= fan_db.getInstance(this)
                                            val lightDb= light_db.getInstance(this)
                                            val plugtDb= plug_db.getInstance(this)
                                            val tempDb=Temperature_db.getInstance(this)
                                            val valveDb= valve_db.getInstance(this)
                                            val sixcDB= six_workert_db.getInstance(this)
                                            val lights = lightDb.getAllLightsByRoomName(room!!.room_name)
                                            val curtains = curtainDb.getAllcurtainsByRoomName(room.room_name)
                                            val fans = fanDb.getfansByRoomName(room!!.room_name)
                                            val plugs = plugtDb.getPlugsByRoomName(room.room_name)
                                            val valve = valveDb.getvalvesByRoomName(room.room_name)
                                            val temps  = tempDb.getThermostatsByRoomName(room.room_name)
                                            val sixc = sixcDB.getAllsix_workerts()

                                            for (light in lights){

                                                if (light != null) {
                                                    lightDb.delete_from_db_light(light.id)
                                                }
                                                for (six in sixc){
                                                    if (six!!.mac == light!!.mac){
                                                        sixcDB.delete_from_db_six_workert(six.id)
                                                    }
                                                }

                                            }
                                            for (fan in fans){
                                                if (fan != null) {
                                                    fanDb.delete_from_db_fan(fan.id)
                                                }
                                            }
                                            for(curtain in curtains){
                                                if (curtain != null) {
                                                    curtainDb.delete_from_db_curtain(curtain.id)
                                                }
                                            }
                                            for (plug in plugs){
                                                if (plug != null) {
                                                    plugtDb.delete_from_db_Plug(plug.id)
                                                }
                                            }

                                            for (valve in valve){
                                                if (valve != null) {
                                                    valveDb.delete_from_db_valve(valve.id)
                                                }
                                            }
                                            for (temp in temps){
                                                if (temp != null) {
                                                    tempDb.delete_from_db_Temprature(temp.id)
                                                }
                                            }
                                            val room_db = com.example.griffinmobile.database.rooms_db.getInstance(this)
                                            room_db.delete_from_db_rooms(room.id)

                                            room_devices_db.getInstance(this).deleteRoomByRoomId(room.id.toString())
                                            Toast.makeText(this, "Room and devices deleted", Toast.LENGTH_SHORT).show()

                                            delete_dialog.dismiss()



                                        }
                                        homeDb.delete_from_db_home(homes[1]!!.id)
                                        var home_count2 = homeDb.getHomeCount()
                                        if (home_count2==0){
                                            var home1=home()
                                            home1.home_name="home"
                                            home1.tag="h"
                                            home1.location="_,_"
                                            home1.current_select="true"
                                            homeDb.set_to_db_home(home1)

                                        }
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                homes[0]?.id?.let { it1 ->
                                                    homeDb.updateCurrentSelectById(
                                                        it1
                                                    )
                                                }
                                                homes[0]?.let { it1 ->
                                                    sharedviewModel.update_selected_home(
                                                        it1
                                                    )
                                                }
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                homes[1]?.id?.let { it1 ->
                                                    homeDb.updateCurrentSelectById(
                                                        it1
                                                    )
                                                }
                                                homes[1]?.let { it1 ->
                                                    sharedviewModel.update_selected_home(
                                                        it1
                                                    )
                                                }
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }

                                        delete_dialog.dismiss()
                                        dialog.show()
                                        delete_dialog.dismiss()
                                    }
                                    cancel_delete.setOnClickListener {
                                        Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()

                                        delete_dialog.dismiss()
                                        dialogedit.dismiss()
                                        dialog.dismiss()
                                    }




                                }

                            }
                            3->{
                                dialog.dismiss()
                                dialogedit.show()

                                home_name.setText(homes[1]!!.home_name)
                                location_n.text = homes[1]!!.location!!.split(",")[0]
                                location_e.text = homes[1]!!.location!!.split(",")[1]
                                done_edit_btn.setOnClickListener {

                                    if ((home_name.text.toString() !="") && (home_name.text.isNotEmpty()) &&(location_n.text !="_")&&(location_e.text !="_")  ){
                                        homes[1]!!.home_name = home_name.text.toString()
                                        homes[1]!!.tag = homes[1]!!.tag
                                        homes[1]!!.location = "${location_n.text.trim()},${location_e.text.trim()}"
                                        homeDb.updateRoomById(homes[1]!!.id, homes[1]!!)
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }
                                        dialog.show()

                                    }else{
                                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }



                                }
                                delete_home.setOnClickListener {

                                    val networkManualDB =setting_network_db.getInstance(this)
                                    val current_network=networkManualDB.searchByHomes("v1")
                                    val roomDevicesDb =room_devices_db.getInstance(this)
                                    val rooms=roomDevicesDb.getRoomsByHome("v1")


                                    val delete_dialog = Dialog(this)
                                    delete_dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                                    // تنظیم لایه دلخواه برای دیالوگ
                                    delete_dialog.setContentView(R.layout.delete_all_or_pole)
                                    val text_msg = delete_dialog.findViewById<TextView>(R.id.text_msg)
                                    val yes_delete = delete_dialog.findViewById<Button>(R.id.yes_delete)
                                    val cancel_delete = delete_dialog.findViewById<Button>(R.id.cancel_delete)
                                    text_msg.setText("Are you sure you want to delete this room? all devices in this room will be deleted ")
                                    yes_delete.setText("Delete")
                                    cancel_delete.setText("Cancel")
                                    delete_dialog.setCanceledOnTouchOutside(false)
                                    delete_dialog.show()
                                    yes_delete.setOnClickListener {
                                        if (!networkManualDB.isDatabaseEmpty()){
                                            current_network[0].id?.let { it1 ->
                                                networkManualDB.deleteById(
                                                    it1
                                                )
                                            }

                                        }
                                        for(room_device in rooms){

                                            val rooms_db=rooms_db.getInstance(this)
                                            val room= rooms_db.get_from_db_rooms(room_device.room_id!!.toInt())
                                            val curtainDb= curtain_db.getInstance(this)
                                            val fanDb= fan_db.getInstance(this)
                                            val lightDb= light_db.getInstance(this)
                                            val plugtDb= plug_db.getInstance(this)
                                            val tempDb=Temperature_db.getInstance(this)
                                            val valveDb= valve_db.getInstance(this)
                                            val sixcDB= six_workert_db.getInstance(this)
                                            val lights = lightDb.getAllLightsByRoomName(room!!.room_name)
                                            val fans = fanDb.getfansByRoomName(room!!.room_name)
                                            val curtains = curtainDb.getAllcurtainsByRoomName(room.room_name)
                                            val plugs = plugtDb.getPlugsByRoomName(room.room_name)
                                            val valve = valveDb.getvalvesByRoomName(room.room_name)
                                            val temps  = tempDb.getThermostatsByRoomName(room.room_name)
                                            val sixc = sixcDB.getAllsix_workerts()

                                            for (light in lights){

                                                if (light != null) {
                                                    lightDb.delete_from_db_light(light.id)
                                                }
                                                for (six in sixc){
                                                    if (six!!.mac == light!!.mac){
                                                        sixcDB.delete_from_db_six_workert(six.id)
                                                    }
                                                }

                                            }
                                            for(curtain in curtains){
                                                if (curtain != null) {
                                                    curtainDb.delete_from_db_curtain(curtain.id)
                                                }
                                            }
                                            for (plug in plugs){
                                                if (plug != null) {
                                                    plugtDb.delete_from_db_Plug(plug.id)
                                                }
                                            }
                                            for (fan in fans){
                                                if (fan != null) {
                                                    fanDb.delete_from_db_fan(fan.id)
                                                }
                                            }
                                            for (valve in valve){
                                                if (valve != null) {
                                                    valveDb.delete_from_db_valve(valve.id)
                                                }
                                            }
                                            for (temp in temps){
                                                if (temp != null) {
                                                    tempDb.delete_from_db_Temprature(temp.id)
                                                }
                                            }
                                            val room_db = com.example.griffinmobile.database.rooms_db.getInstance(this)
                                            room_db.delete_from_db_rooms(room.id)

                                            room_devices_db.getInstance(this).deleteRoomByRoomId(room.id.toString())
                                            Toast.makeText(this, "Room and devices deleted", Toast.LENGTH_SHORT).show()

                                            delete_dialog.dismiss()



                                        }
                                        homeDb.delete_from_db_home(homes[1]!!.id)
                                        var home_count2 = homeDb.getHomeCount()
                                        if (home_count2==0){
                                            var home1=home()
                                            home1.home_name="home"
                                            home1.tag="h"
                                            home1.location="_,_"
                                            home1.current_select="true"
                                            homeDb.set_to_db_home(home1)

                                        }
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                homes[0]?.id?.let { it1 ->
                                                    homeDb.updateCurrentSelectById(
                                                        it1
                                                    )
                                                }
                                                homes[0]?.let { it1 ->
                                                    sharedviewModel.update_selected_home(
                                                        it1
                                                    )
                                                }
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                homes[1]?.id?.let { it1 ->
                                                    homeDb.updateCurrentSelectById(
                                                        it1
                                                    )
                                                }
                                                homes[1]?.let { it1 ->
                                                    sharedviewModel.update_selected_home(
                                                        it1
                                                    )
                                                }
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }

                                        dialog.show()
                                        delete_dialog.dismiss()
                                    }
                                    cancel_delete.setOnClickListener {
                                        Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()

                                        delete_dialog.dismiss()
                                        dialogedit.dismiss()
                                        dialog.dismiss()
                                    }




                                }

                            }
                            else->{
                                dialog.dismiss()
                                dialogedit.show()

                                home_name.setText("")
                                location_n.text ="_"
                                location_e.text ="_"
                                done_edit_btn.setOnClickListener {

                                    if ((home_name.text.toString() !="") && (home_name.text.isNotEmpty()) &&(location_n.text !="_")&&(location_e.text !="_")  ){
                                        val new_home = home()
                                        new_home!!.home_name = home_name.text.toString()
                                        new_home!!.tag = "v2"
                                        new_home!!.location = "${location_n.text.trim()},${location_e.text.trim()}"
                                        homeDb.set_to_db_home(new_home)
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }
                                        dialog.show()

                                    }else{
                                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }



                                }
                                delete_home.setOnClickListener {

                                    dialog.dismiss()
                                    dialogedit.dismiss()


                                }

                            }




                        }
                        true
                    }

                    add_home_btn_3.setOnLongClickListener {
                        when(home_count){
                            1->{
                                dialog.dismiss()
                                dialogedit.show()

                                home_name.setText("")
                                location_n.text ="_"
                                location_e.text ="_"
                                done_edit_btn.setOnClickListener {

                                    if ((home_name.text.toString() !="") && (home_name.text.isNotEmpty()) &&(location_n.text !="_")&&(location_e.text !="_")  ){
                                        val new_home = home()
                                        new_home!!.home_name = home_name.text.toString()
                                        new_home!!.tag = "v2"
                                        new_home!!.location = "${location_n.text.trim()},${location_e.text.trim()}"
                                        homeDb.set_to_db_home(new_home)
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }
                                        dialog.show()

                                    }else{
                                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }



                                }
                                delete_home.setOnClickListener {

                                    dialog.dismiss()
                                    dialogedit.dismiss()


                                }

                            }
                            2->{
                                dialog.dismiss()
                                dialogedit.show()

                                home_name.setText("")
                                location_n.text ="_"
                                location_e.text ="_"
                                done_edit_btn.setOnClickListener {

                                    if ((home_name.text.toString() !="") && (home_name.text.isNotEmpty()) &&(location_n.text !="_")&&(location_e.text !="_")  ){
                                        val new_home = home()
                                        new_home!!.home_name = home_name.text.toString()
                                        new_home!!.tag = "v2"
                                        new_home!!.location = "${location_n.text.trim()},${location_e.text.trim()}"
                                        homeDb.set_to_db_home(new_home)
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }
                                        dialog.show()

                                    }else{
                                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }



                                }
                                delete_home.setOnClickListener {

                                    dialog.dismiss()
                                    dialogedit.dismiss()


                                }

                            }
                            3->{
                                dialog.dismiss()
                                dialogedit.show()

                                home_name.setText(homes[2]!!.home_name)
                                location_n.text = homes[2]!!.location!!.split(",")[0]
                                location_e.text = homes[2]!!.location!!.split(",")[1]
                                done_edit_btn.setOnClickListener {

                                    if ((home_name.text.toString() !="") && (home_name.text.isNotEmpty()) &&(location_n.text !="_")&&(location_e.text !="_")  ){
                                        homes[2]!!.home_name = home_name.text.toString()
                                        homes[2]!!.tag = homes[2]!!.tag
                                        homes[2]!!.location = "${location_n.text.trim()},${location_e.text.trim()}"
                                        homeDb.updateRoomById(homes[2]!!.id, homes[2]!!)
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }
                                        dialog.show()

                                    }else{
                                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }



                                }
                                delete_home.setOnClickListener {

                                    val networkManualDB =setting_network_db.getInstance(this)
                                    val current_network=networkManualDB.searchByHomes("v2")
                                    val roomDevicesDb =room_devices_db.getInstance(this)
                                    val rooms=roomDevicesDb.getRoomsByHome("v2")


                                    val delete_dialog = Dialog(this)
                                    delete_dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                                    // تنظیم لایه دلخواه برای دیالوگ
                                    delete_dialog.setContentView(R.layout.delete_all_or_pole)
                                    val text_msg = delete_dialog.findViewById<TextView>(R.id.text_msg)
                                    val yes_delete = delete_dialog.findViewById<Button>(R.id.yes_delete)
                                    val cancel_delete = delete_dialog.findViewById<Button>(R.id.cancel_delete)
                                    text_msg.setText("Are you sure you want to delete this room? all devices in this room will be deleted ")
                                    yes_delete.setText("Delete")
                                    cancel_delete.setText("Cancel")
                                    delete_dialog.setCanceledOnTouchOutside(false)
                                    delete_dialog.show()
                                    yes_delete.setOnClickListener {

                                        if (!networkManualDB.isDatabaseEmpty()){
                                            current_network[0].id?.let { it1 ->
                                                networkManualDB.deleteById(
                                                    it1
                                                )
                                            }

                                        }

                                        for(room_device in rooms){

                                            val rooms_db=rooms_db.getInstance(this)
                                            val room= rooms_db.get_from_db_rooms(room_device.room_id!!.toInt())
                                            val curtainDb= curtain_db.getInstance(this)
                                            val fanDb= fan_db.getInstance(this)
                                            val lightDb= light_db.getInstance(this)
                                            val plugtDb= plug_db.getInstance(this)
                                            val tempDb=Temperature_db.getInstance(this)
                                            val valveDb= valve_db.getInstance(this)
                                            val fans = fanDb.getfansByRoomName(room!!.room_name)
                                            val sixcDB= six_workert_db.getInstance(this)
                                            val lights = lightDb.getAllLightsByRoomName(room!!.room_name)
                                            val curtains = curtainDb.getAllcurtainsByRoomName(room.room_name)
                                            val plugs = plugtDb.getPlugsByRoomName(room.room_name)
                                            val valve = valveDb.getvalvesByRoomName(room.room_name)
                                            val temps  = tempDb.getThermostatsByRoomName(room.room_name)
                                            val sixc = sixcDB.getAllsix_workerts()

                                            for (light in lights){

                                                if (light != null) {
                                                    lightDb.delete_from_db_light(light.id)
                                                }
                                                for (six in sixc){
                                                    if (six!!.mac == light!!.mac){
                                                        sixcDB.delete_from_db_six_workert(six.id)
                                                    }
                                                }

                                            }
                                            for (fan in fans){
                                                if (fan != null) {
                                                    fanDb.delete_from_db_fan(fan.id)
                                                }
                                            }
                                            for(curtain in curtains){
                                                if (curtain != null) {
                                                    curtainDb.delete_from_db_curtain(curtain.id)
                                                }
                                            }
                                            for (plug in plugs){
                                                if (plug != null) {
                                                    plugtDb.delete_from_db_Plug(plug.id)
                                                }
                                            }

                                            for (valve in valve){
                                                if (valve != null) {
                                                    valveDb.delete_from_db_valve(valve.id)
                                                }
                                            }
                                            for (temp in temps){
                                                if (temp != null) {
                                                    tempDb.delete_from_db_Temprature(temp.id)
                                                }
                                            }
                                            val room_db = com.example.griffinmobile.database.rooms_db.getInstance(this)
                                            room_db.delete_from_db_rooms(room.id)

                                            room_devices_db.getInstance(this).deleteRoomByRoomId(room.id.toString())
                                            Toast.makeText(this, "Room and devices deleted", Toast.LENGTH_SHORT).show()

                                            delete_dialog.dismiss()



                                        }
                                        homeDb.delete_from_db_home(homes[2]!!.id)
                                        var home_count2 = homeDb.getHomeCount()
                                        if (home_count2==0){
                                            var home1=home()
                                            home1.home_name="home"
                                            home1.tag="h"
                                            home1.location="_,_"
                                            home1.current_select="true"
                                            homeDb.set_to_db_home(home1)

                                        }
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                homes[0]?.id?.let { it1 ->
                                                    homeDb.updateCurrentSelectById(
                                                        it1
                                                    )
                                                }
                                                homes[0]?.let { it1 ->
                                                    sharedviewModel.update_selected_home(
                                                        it1
                                                    )
                                                }
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                homes[1]?.id?.let { it1 ->
                                                    homeDb.updateCurrentSelectById(
                                                        it1
                                                    )
                                                }
                                                homes[1]?.let { it1 ->
                                                    sharedviewModel.update_selected_home(
                                                        it1
                                                    )
                                                }
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }

                                        dialog.show()
                                        delete_dialog.dismiss()
                                    }
                                    cancel_delete.setOnClickListener {
                                        Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()

                                        delete_dialog.dismiss()
                                        dialogedit.dismiss()
                                        dialog.dismiss()
                                    }




                                }

                            }
                            else->{
                                dialog.dismiss()
                                dialogedit.show()

                                home_name.setText("")
                                location_n.text ="_"
                                location_e.text ="_"
                                done_edit_btn.setOnClickListener {

                                    if ((home_name.text.toString() !="") && (home_name.text.isNotEmpty()) &&(location_n.text !="_")&&(location_e.text !="_")  ){
                                        val new_home = home()
                                        new_home!!.home_name = home_name.text.toString()
                                        new_home!!.tag = "v2"
                                        new_home!!.location = "${location_n.text.trim()},${location_e.text.trim()}"
                                        homeDb.set_to_db_home(new_home)
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }
                                        dialog.show()

                                    }else{
                                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }



                                }
                                delete_home.setOnClickListener {

                                    dialog.dismiss()
                                    dialogedit.dismiss()


                                }

                            }





                        }
                        true
                    }



                    //  click to add



                    add_home_btn_1.setOnClickListener {
                        println(home_count)
                        when(home_count) {
                            0 -> {

                                dialog.dismiss()
                                dialogedit.show()

                                home_name.setText("")
                                location_n.text ="_"
                                location_e.text ="_"
                                done_edit_btn.setOnClickListener {

                                    if ((home_name.text.toString() !="") && (home_name.text.isNotEmpty()) &&(location_n.text !="_")&&(location_e.text !="_")  ){
                                        val new_home = home()
                                        new_home!!.home_name = home_name.text.toString()
                                        new_home!!.tag = "h"
                                        new_home!!.location = "${location_n.text.trim()},${location_e.text.trim()}"
                                        homeDb.set_to_db_home(new_home)
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        homes[0]!!.id?.let { it1 -> homeDb.updateCurrentSelectById(it1) }
                                        homes[0]?.let { it1 ->
                                            sharedviewModel.update_selected_home(
                                                it1
                                            )
                                        }
                                        Toast.makeText(this, "Current Home : ${homes[0]!!.home_name}", Toast.LENGTH_SHORT).show()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }
                                        dialog.show()

                                    }else{
                                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }



                                }
                                delete_home.setOnClickListener {

                                    dialogedit.dismiss()
                                    dialog.dismiss()
                                }

                            }
                            else ->{

                                homes[0]!!.id?.let { it1 -> homeDb.updateCurrentSelectById(it1) }
                                homes[0]?.let { it1 ->
                                    sharedviewModel.update_selected_home(
                                        it1
                                    )
                                }
                                Toast.makeText(this, "Current Home : ${homes[0]!!.home_name}", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }




                        }

                    }

                    add_home_btn_2.setOnClickListener {
                        when(home_count){
                            0->{
                                dialog.dismiss()
                                dialogedit.show()

                                home_name.setText("")
                                location_n.text ="_"
                                location_e.text ="_"
                                done_edit_btn.setOnClickListener {

                                    if ((home_name.text.toString() !="") && (home_name.text.isNotEmpty()) &&(location_n.text !="_")&&(location_e.text !="_")  ){
                                        val new_home = home()
                                        new_home!!.home_name = home_name.text.toString()
                                        new_home!!.tag = "v1"
                                        new_home!!.location = "${location_n.text.trim()},${location_e.text.trim()}"
                                        homeDb.set_to_db_home(new_home)
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }
                                        dialog.show()

                                    }else{
                                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }



                                }
                                delete_home.setOnClickListener {

                                    dialogedit.dismiss()
                                    dialog.dismiss()
                                }

                            }
                            1->{
                                dialog.dismiss()
                                dialogedit.show()

                                home_name.setText("")
                                location_n.text ="_"
                                location_e.text ="_"
                                done_edit_btn.setOnClickListener {

                                    if ((home_name.text.toString() !="") && (home_name.text.isNotEmpty()) &&(location_n.text !="_")&&(location_e.text !="_")  ){
                                        val new_home = home()
                                        new_home!!.home_name = home_name.text.toString()
                                        new_home!!.tag = "v1"
                                        new_home!!.location = "${location_n.text.trim()},${location_e.text.trim()}"
                                        homeDb.set_to_db_home(new_home)
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        homes[1]!!.id?.let { it1 -> homeDb.updateCurrentSelectById(it1) }
                                        homes[1]?.let { it1 ->
                                            sharedviewModel.update_selected_home(
                                                it1
                                            )
                                        }
                                        Toast.makeText(this, "Current Home : ${homes[1]!!.home_name}", Toast.LENGTH_SHORT).show()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }
                                        dialog.show()

                                    }else{
                                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }



                                }
                                delete_home.setOnClickListener {

                                    dialogedit.dismiss()
                                    dialog.dismiss()
                                }
                            }
                            else->{
                                homes[1]!!.id?.let { it1 -> homeDb.updateCurrentSelectById(it1) }
                                homes[1]?.let { it1 ->
                                    sharedviewModel.update_selected_home(
                                        it1
                                    )
                                }
                                Toast.makeText(this, "Current Home : ${homes[1]!!.home_name}", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }





                        }

                    }


                    add_home_btn_3.setOnClickListener {

                        when(home_count){
                            0->{
                                dialog.dismiss()
                                dialogedit.show()

                                home_name.setText("")
                                location_n.text ="_"
                                location_e.text ="_"
                                done_edit_btn.setOnClickListener {

                                    if ((home_name.text.toString() !="") && (home_name.text.isNotEmpty()) &&(location_n.text !="_")&&(location_e.text !="_")  ){
                                        val new_home = home()
                                        new_home!!.home_name = home_name.text.toString()
                                        new_home!!.tag = "v2"
                                        new_home!!.location = "${location_n.text.trim()},${location_e.text.trim()}"
                                        homeDb.set_to_db_home(new_home)
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }
                                        dialog.show()

                                    }else{
                                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }



                                }
                                delete_home.setOnClickListener {

                                    dialogedit.dismiss()
                                    dialog.dismiss()
                                }
                            }
                            1->{
                                dialog.dismiss()
                                dialogedit.show()

                                home_name.setText("")
                                location_n.text ="_"
                                location_e.text ="_"
                                done_edit_btn.setOnClickListener {

                                    if ((home_name.text.toString() !="") && (home_name.text.isNotEmpty()) &&(location_n.text !="_")&&(location_e.text !="_")  ){
                                        val new_home = home()
                                        new_home!!.home_name = home_name.text.toString()
                                        new_home!!.tag = "v2"
                                        new_home!!.location = "${location_n.text.trim()},${location_e.text.trim()}"
                                        homeDb.set_to_db_home(new_home)
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }
                                        dialog.show()

                                    }else{
                                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }



                                }
                                delete_home.setOnClickListener {

                                    dialogedit.dismiss()
                                    dialog.dismiss()
                                }
                            }
                            2->{
                                dialog.dismiss()
                                dialogedit.show()

                                home_name.setText("")
                                location_n.text ="_"
                                location_e.text ="_"
                                done_edit_btn.setOnClickListener {

                                    if ((home_name.text.toString() !="") && (home_name.text.isNotEmpty()) &&(location_n.text !="_")&&(location_e.text !="_")  ){
                                        val new_home = home()
                                        new_home!!.home_name = home_name.text.toString()
                                        new_home!!.tag = "v2"
                                        new_home!!.location = "${location_n.text.trim()},${location_e.text.trim()}"
                                        homeDb.set_to_db_home(new_home)
                                        homes= homeDb.getAllhome()
                                        home_count = homeDb.getHomeCount()
                                        homes[2]!!.id?.let { it1 -> homeDb.updateCurrentSelectById(it1) }
                                        homes[2]?.let { it1 ->
                                            sharedviewModel.update_selected_home(
                                                it1
                                            )
                                        }
                                        Toast.makeText(this, "Current Home : ${homes[2]!!.home_name}", Toast.LENGTH_SHORT).show()
                                        dialogedit.dismiss()
                                        when(home_count){
                                            1 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.text = homes[0]!!.home_name
                                                println(homes[0]!!.home_name)

                                                add_home_btn_2.setAnimation(R.raw.add_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_2.setText("Add")
                                                add_home_name_3.setText("Add")
                                            }
                                            2 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)


                                                add_home_btn_3.setAnimation(R.raw.add_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText("Add")

                                            }
                                            3 ->{
                                                add_home_btn_1.setAnimation(R.raw.home_animation)
                                                add_home_btn_1.playAnimation()
                                                add_home_name_1.setText(homes[0]!!.home_name)
                                                add_home_btn_2.setAnimation(R.raw.home_animation)
                                                add_home_btn_2.playAnimation()
                                                add_home_name_2.setText(homes[1]!!.home_name)
                                                add_home_btn_3.setAnimation(R.raw.home_animation)
                                                add_home_btn_3.playAnimation()
                                                add_home_name_3.setText(homes[2]!!.home_name)

                                            }

                                        }
                                        dialog.show()

                                    }else{
                                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }



                                }
                                delete_home.setOnClickListener {

                                    dialogedit.dismiss()
                                    dialog.dismiss()
                                }
                            }
                            else ->{

                                homes[2]!!.id?.let { it1 -> homeDb.updateCurrentSelectById(it1) }
                                homes[2]?.let { it1 ->
                                    sharedviewModel.update_selected_home(
                                        it1
                                    )
                                }
                                Toast.makeText(this, "Current Home : ${homes[2]!!.home_name}", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }





                        }

                    }



                }




                dialog.show()
            }


            home_style.setOnClickListener {


                val intent = Intent(this,com.example.griffinmobile.rooms::class.java)
                startActivity(intent)

//            val dialog = Dialog(this)
//
//            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
//            dialog.window!!.setContentView(R.layout.home_style)
//            dialog.setCanceledOnTouchOutside(false)
//            val rooms_recyceler_view = dialog.findViewById<RecyclerView>(R.id.rooms_recyceler_view)
//            val disable_layout = dialog.findViewById<ConstraintLayout>(R.id.disable_layout)
//            val enable_layout = dialog.findViewById<ConstraintLayout>(R.id.enable_layout)
//            val editText = dialog.findViewById<EditText>(R.id.editText)
//            val ok_btn = dialog.findViewById<Button>(R.id.ok_btn)
//            val add_button = dialog.findViewById<Button>(R.id.add_button)
//            val style_name = dialog.findViewById<TextView>(R.id.style_name)
//            val room_name = dialog.findViewById<TextView>(R.id.room_name)
//
//            var current_mood ="nothing"
//            val my_rooms_db = rooms_db.getInstance(this)
//            var my_rooms = my_rooms_db.getAllRooms()
//            val all_names = mutableListOf<String>()
//
//            var adapter : RoomAdapter?=null
//            var selected_image = ""
//
//            for(room in my_rooms){
//                all_names.add(room!!.room_name!!)
//            }
//
//            fun active_edit(){
//                enable_layout.visibility=View.VISIBLE
//                enable_layout.alpha=1f
//                disable_layout.visibility=View.INVISIBLE
//                editText.isClickable=true
//                editText.isEnabled=true
//                ok_btn.isEnabled=true
//                ok_btn.text="Ok"
//
//                current_mood="edit"
//
//            }
//
//            fun active_add(){
//                enable_layout.visibility=View.VISIBLE
//                enable_layout.alpha=1f
//                disable_layout.visibility=View.INVISIBLE
//                editText.isClickable=true
//                editText.isEnabled=true
//                ok_btn.isEnabled=true
//                ok_btn.text="Set"
//                editText.setText("Enter Room Name")
//                current_mood="add"
//
//            }
//            fun deactive_add_edit(){
//
//                enable_layout.visibility=View.INVISIBLE
//                enable_layout.alpha=0.15f
//                disable_layout.visibility=View.VISIBLE
//                editText.isClickable=false
//                editText.isEnabled=false
//                ok_btn.isEnabled=false
//                ok_btn.text="Ok"
//                current_mood="nothing"
//
//            }
//
//            dialog.show()
//
//
//
//            add_button.setOnClickListener {
//                active_add()
//
//
//            }
//
//
//
//
//
//            if (my_rooms.isNotEmpty()){
//                adapter = RoomAdapter(my_rooms) { room ->
//
//
////                    Toast.makeText(this, "Room Name: $roomName", Toast.LENGTH_SHORT).show()
//                    active_edit()
//                    editText.setText(room.room_name)
//
//                }
//
//                rooms_recyceler_view.layoutManager = LinearLayoutManager(this)
//                rooms_recyceler_view.adapter = adapter
//
//
//
//            }else{
//                deactive_add_edit()
//
//
//            }
//
//            ok_btn.setOnClickListener {
//                if (current_mood == "add"){
//                    if (editText.text.toString().trim() !="Enter Room Name" && selected_image !="" && editText.text.toString().trim() !="" && !(all_names.contains(editText.text.toString().trim()))){
//                        val my_room =rooms()
//                        my_room.room_type = style_name.text.toString().trim()
//                        my_room.room_name = editText.text.toString().trim()
//                        my_room.room_image=selected_image
//                        my_rooms_db.set_to_db_rooms(my_room)
//                        Toast.makeText(this, "Room Added", Toast.LENGTH_SHORT).show()
//                        deactive_add_edit()
//                        my_rooms=my_rooms_db.getAllRooms()
//                        if (adapter!= null)
//                        adapter.notifyDataSetChanged()
//
//                    }
//
//
//
//                }
//
//            }
//


            }





        })






    }

    override fun onPause() {
        super.onPause()
        try {
            println("sssssssssssssssssssssssssssssssssssssssssssssdd")
            socket?.close()
            socket=null
            working=false
//            val ok_receive =  findViewById<Button>(R.id.ok_receive)
//            val cance_receive =  findViewById<Button>(R.id.cance_receive)
//            val sender_ip = findViewById<TextView>(R.id.sender_ip)
//            val receive_steps =  findViewById<TextView>(R.id.receive_steps)
//            val received_status = findViewById<TextView>(R.id.received_status)
//            val connect_status =  findViewById<TextView>(R.id.connect_status)
//            val textView9 = findViewById<TextView>(R.id.textView9)
//            ok_receive.isSelected=false
//            try {
//
//                textView9.setText("Current Database Will \n Be Cleared , Proceed?")
//                sender_ip.setText("")
//                receive_steps.setText("")
//                received_status.setText("Canceled")
//                connect_status.setText("Disconnecteded")
//            }catch (e:Exception){
//                println(e)
//            }
        }catch (e:Exception){
            println(e)
        }


    }
    override fun onBackPressed() {
        // انجام یک کار خاص هنگام فشردن دکمه Back
        val indent = Intent(this,MainActivity::class.java)
        startActivity(indent)

        // اگر می‌خواهید اکتیویتی بسته شود:
        super.onBackPressed()
    }
    override fun onDestroy() {
        super.onDestroy()
        println("sdadasdasdasdasdadsd")

    }



    private fun showPopup_receive() {
        // ساخت دیالوگ
        val dialog = Dialog(this)

        // حذف گوشه‌ها و پس‌زمینه دیالوگ
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // تنظیم لایه دلخواه برای دیالوگ
        dialog.setContentView(R.layout.sync_receive_popup)


        dialog.setCanceledOnTouchOutside(false)



        val ok_receive =  dialog.findViewById<Button>(R.id.ok_receive)
        val cance_receive =  dialog.findViewById<Button>(R.id.cance_receive)
        val sender_ip = dialog.findViewById<TextView>(R.id.sender_ip)
        val receive_steps =  dialog.findViewById<TextView>(R.id.receive_steps)
        val received_status = dialog.findViewById<TextView>(R.id.received_status)
        val connect_status =  dialog.findViewById<TextView>(R.id.connect_status)
        val textView9 = dialog.findViewById<TextView>(R.id.textView9)



        var light_db = light_db.getInstance(this)
        var fan_db = fan_db.getInstance(this)
        var plug_db = plug_db.getInstance(this)
        var curtain_db = curtain_db.getInstance(this)
        var valve_db = valve_db.getInstance(this)
        var Temperature_db = Temperature_db.getInstance(this)
        var sixWorkertDb_db = six_workert_db.getInstance(this)
        var room_db = rooms_db.getInstance(this)
        var camera_db = camera_db.getInstance(this)
        var scenario_db = scenario_db.Scenario_db.getInstance(this)
        val room_devices_databaseHelper=room_devices_db.getInstance(this@SettingsActivity)

        try {

            connect_status.setText("Disconnected")
        }catch (e:Exception){
            println(e)
        }



        var working = false


        cance_receive.setOnClickListener {



            socket?.close()
            socket=null
            working=false
            ok_receive.isSelected=false
            try {

                textView9.setText("Current Database Will \n Be Cleared , Proceed?")
                sender_ip.setText("")
                receive_steps.setText("")
                received_status.setText("Canceled")
                connect_status.setText("Disconnecteded")
            }catch (e:Exception){
                println(e)
            }





        }




        fun isConnectedToWifi(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            return networkInfo?.type == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected
        }



        ok_receive.setOnClickListener {


            try {

                receive_steps.setText("")
            }catch (e:Exception){
                println(e)
            }
            val wifiManager = this.applicationContext.getSystemService(
                Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val ssid = wifiInfo.ssid

            if (!setting_network_db.getInstance(this).isDatabaseEmpty()){

                val db_ssid = setting_network_db.getInstance(this).get_from_db_network_manual(1)?.modem_ssid

                println(ssid)
                println(isConnectedToWifi(this))

                if ( ssid.replace("\"", "").trim() ==db_ssid!!.trim() ){

                    working=true

                    try {
                        connect_status.setText("Connecting...")

                    }catch (e:Exception){
                        println(e)
                    }
//                    rooms_db.getInstance( this).clear_db_rooms()
//                    Temperature_db.clear_db_Temprature()
//                    light_db.clear_db_light()
//                    fan_db.clear_db_fan()
//                    curtain_db.clear_db_curtain()
//                    plug_db.clear_db_plug()
//                    valve_db.clear_db_valve()
//                    rooms_db.getInstance( this).clear_db_rooms()
//                    camera_db.clear_db_camera()
//                    scenario_db.clear_db_scenario()
//                    favorite_db.Favorite_db.getInstance(this).clear_db_favorite()
//                    alarm_handeler_db.getInstance(this).clear_db_alarm()
//                    door_db.getInstance(this).clear_db_DOOR()
//                    Elevator_db.getInstance(this).clear_db()
//                    security_db.getInstance(this).clearsecurityTable()
////                setting_network_db.getInstance(requireContext()).clearNetworkTable()
//                    setting_simcard_accountsecurity_db.getInstance(this).clear_db()
//                    setting_simcard_messageresponse_db.getInstance(this).clear_db()
//                    setting_simcard_security_db.getInstance(this).clear_db()
//                    six_workert_db.getInstance( this).clear_db_six_workert()
//                    Ir_db.getInstance(this).clear_db_Ir()
//                    room_devices_databaseHelper.clear_db_rooms()

                    sharedviewModel.selected_home.observe(this@SettingsActivity,Observer { current_home ->

                        val roomDevicesDb =room_devices_db.getInstance(this)
                        val rooms=roomDevicesDb.getRoomsByHome(current_home.tag.toString())

                        for(room_device in rooms){

                            val rooms_db=rooms_db.getInstance(this)
                            val room= rooms_db.get_from_db_rooms(room_device.room_id!!.toInt())
                            val curtainDb= com.example.griffinmobile.database.curtain_db.getInstance(this)
                            val fanDb= com.example.griffinmobile.database.fan_db.getInstance(this)
                            val lightDb= com.example.griffinmobile.database.light_db.getInstance(this)
                            val plugtDb= com.example.griffinmobile.database.plug_db.getInstance(this)
                            val tempDb= com.example.griffinmobile.database.Temperature_db.getInstance(this)
                            val valveDb= com.example.griffinmobile.database.valve_db.getInstance(this)
                            val sixcDB= six_workert_db.getInstance(this)
                            val fans = fanDb.getfansByRoomName(room!!.room_name)
                            val lights = lightDb.getAllLightsByRoomName(room!!.room_name)
                            val curtains = curtainDb.getAllcurtainsByRoomName(room.room_name)
                            val plugs = plugtDb.getPlugsByRoomName(room.room_name)
                            val valve = valveDb.getvalvesByRoomName(room.room_name)
                            val temps  = tempDb.getThermostatsByRoomName(room.room_name)
                            val sixc = sixcDB.getAllsix_workerts()


                            for (light in lights){

                                if (light != null) {
                                    lightDb.delete_from_db_light(light.id)
                                }
                                for (six in sixc){
                                    if (six!!.mac == light!!.mac){
                                        sixcDB.delete_from_db_six_workert(six.id)
                                    }
                                }

                            }
                            for(curtain in curtains){
                                if (curtain != null) {
                                    curtainDb.delete_from_db_curtain(curtain.id)
                                }
                            }
                            for (plug in plugs){
                                if (plug != null) {
                                    plugtDb.delete_from_db_Plug(plug.id)
                                }
                            }

                            for (valve in valve){
                                if (valve != null) {
                                    valveDb.delete_from_db_valve(valve.id)
                                }
                            }
                            for (fan in fans){
                                if (fan != null) {
                                    fanDb.delete_from_db_fan(fan.id)
                                }
                            }
                            for (temp in temps){
                                if (temp != null) {
                                    tempDb.delete_from_db_Temprature(temp.id)
                                }
                            }
                            val room_db = com.example.griffinmobile.database.rooms_db.getInstance(this)
                            room_db.delete_from_db_rooms(room.id)

                            room_devices_db.getInstance(this).deleteRoomByRoomId(room.id.toString())






                        }

                    })


                    ok_receive.isSelected=true
                    try {

                        textView9.setText("Press Cancel To Stop")
                    }catch (e:Exception){
                        println(e)
                    }

                    GlobalScope.launch(Dispatchers.IO) {
                        val bufferSize = 2048
                        val receiveData = ByteArray(bufferSize)


                        var previus_data =""

                        try {

                            while (working) {
                                try {
                                    if (socket == null || socket!!.isClosed) {
                                        socket = DatagramSocket(8089)
                                        println("Listenning to 8089..")
                                    }
                                }catch (e:Exception){
                                    println(e)
                                    println("111111111")
//                            socket?.close()
//                            socket=null
//                            working=false
//                            ok_receive.isSelected=false
//                            textView9.setText("Current Database Will \n Be Cleared , Proceed?")
//                            sender_ip.setText("")
//                            receive_steps.setText("")
//                            received_status.setText("Failed")
//                            connect_status.setText("Disconnecteded")

                                }



                                val receivePacket = DatagramPacket(receiveData, receiveData.size)
                                socket!!.receive(receivePacket)

                                val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                                println("received messagefrom 8089 : $receivedMessage")

//                        println(receivedMessage)


                                try {
                                    connect_status.setText("Connected")

                                }catch (e:Exception){
                                    println(e)
                                }
                                val msg=receivedMessage.split("~>")
                                val target_ip = msg[(msg.count())-1 ]
                                var akid = msg[(msg.count())-2 ]
                                val my_ip= checkIP( this@SettingsActivity)

                                val sy = msg[0]
                                if (msg[0]=="sygi"){
                                    akid="#"
                                    try {

                                        sender_ip.setText(msg[1])
                                    }catch (e:Exception){
                                        println(e)
                                    }

                                }else if (msg[0] == "sydn"){
                                    println("dnnnnnnnnnnnnnnnnnnnnnnnnnnnn")
                                    working=false

                                    val msg=receivedMessage.split("~>")
                                    val target_ip = msg[(msg.count())-1 ]
                                    var akid = msg[(msg.count())-2 ]
                                    try {

                                        connect_status.setText("Disconnected...")
                                    }catch (e:Exception){
                                        println(e)
                                    }

                                    val message = "syac~>$akid"
                                    try {
                                        receive_steps.setText("")
                                        received_status.setText("Completed")
                                        textView9.setText("Current Database Will \n Be Cleared , Proceed?")

                                        val lightDatabase = light_db
                                        lightDatabase.removeDuplicates()
                                        val curtainDatabase = curtain_db
                                        curtainDatabase.removeDuplicates()
                                        val fanDatabase = fan_db
                                        fanDatabase.removeDuplicates()
                                        val plugDatabase = plug_db
                                        plugDatabase.removeDuplicates()
                                        val roomsDatabase = rooms_db.getInstance( this@SettingsActivity)
                                        roomsDatabase.removeDuplicates()
                                        val sixWorkertDatabase = six_workert_db.getInstance( this@SettingsActivity)
                                        sixWorkertDatabase.removeDuplicates()
                                        val temperatureDatabase = Temperature_db
                                        temperatureDatabase.removeDuplicates()
                                        val valveDatabase = valve_db
                                        valveDatabase.removeDuplicates()

                                    }catch (e:Exception){
                                        println(e)
                                    }


                                    sharedviewModel.selected_home.observe(this@SettingsActivity,Observer { current_home ->

                                        val room_devices = current_home.tag?.let { it1 ->
                                            room_devices_databaseHelper.getRoomsByHome(
                                                it1
                                            )
                                        }
                                        if (room_devices != null) {
                                            for (room_device in  room_devices){
                                                val room = room_db.get_from_db_rooms(room_device.room_id!!.toInt())
                                                val all_lights =light_db.getAllLightsByRoomName(room!!.room_name)
                                                val all_fans =fan_db.getfansByRoomName(room!!.room_name)
                                                val all_plug =plug_db.getPlugsByRoomName(room!!.room_name)
                                                val all_curtain =curtain_db.getAllcurtainsByRoomName(room!!.room_name)
                                                val all_valve =valve_db.getvalvesByRoomName(room!!.room_name)
                                                val all_termostat =Temperature_db.getThermostatsByRoomName(room!!.room_name)

                                                val current_rooms_v2= rooms_v2()
                                                current_rooms_v2.room_id=room!!.id.toString()
                                                var my_devices = arrayListOf<String>()
                                                for (item in all_lights ){
                                                    my_devices.add("L:${item!!.id}")


                                                }
                                                for (item in all_fans ){
                                                    my_devices.add("F:${item!!.id}")


                                                }
                                                for (item in all_plug ){
                                                    my_devices.add("P:${item!!.id}")


                                                }
                                                for (item in all_curtain ){
                                                    my_devices.add("C:${item!!.id}")


                                                }
                                                for (item in all_valve ){
                                                    my_devices.add("V:${item!!.id}")


                                                }
                                                for (item in all_termostat ){
                                                    my_devices.add("T:${item!!.id}")


                                                }


                                                current_rooms_v2.room_devices=my_devices.joinToString(separator = ",")
                                                current_rooms_v2.homes=current_home.tag
                                                room_devices_databaseHelper.set_to_db_rooms(current_rooms_v2)
                                                println(my_devices)
                                            }
                                        }


                                    })



                                    println(target_ip)
                                    Thread.sleep(2000)
                                    dialog.dismiss()
//                            val serverAddress = InetAddress.getByName(target_ip)
//                            val serverPort = 8089
//                            val sendData = message.toByteArray()
//                            val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
//                            socket!!.send(sendPacket)
//                            socket!!.close()
                                    // باید بگه به ال سی ئی که تموم شد بعد سوکت رو ببنده





                                }else{
                                    try {

                                        if (msg[0]=="syro"){
                                            receive_steps.setText("Rooms")
                                        }else if (msg[0]=="symo"){
                                            receive_steps.setText("Modules")
                                        }else if (msg[0]=="sysp"){
                                            receive_steps.setText("spilet")
                                        }else if (msg[0]=="sysx"){
                                            receive_steps.setText("Modules")
                                        }else if (msg[0]=="sysc"){
                                            receive_steps.setText("Scenario")
                                        }
                                    }catch (e:Exception){
                                        println(e)
                                    }

                                    println(previus_data)
                                    println(receivedMessage)
                                    if (receivedMessage != previus_data){
                                        sharedviewModel.selected_home.observe(this@SettingsActivity,Observer { current_home ->

                                            current_home.tag?.let { it1 ->
                                                sync_decoder( this@SettingsActivity,receivedMessage,
                                                    it1
                                                )
                                            }
                                            previus_data= receivedMessage

                                        })


                                    }

                                }



                                var message = "sysi~>$my_ip"
                                if (akid =="#"){
                                    message = "sysi~>$my_ip"


                                }else{
                                    message = "syac~>$akid"

                                }

                                println(message)
                                println(target_ip)
                                val serverAddress = InetAddress.getByName(target_ip)
                                val serverPort = 8089


                                val sendData = message.toByteArray()
                                val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
                                socket!!.send(sendPacket)
//                            sync_fidback( this,sy,akid,target_ip)
                                if (msg[0] == "sydn"){
//                            socket!!.close()
                                    try {

                                        connect_status.setText("Disconnected...")


                                        receive_steps.setText("")
                                        received_status.setText("Completed")
                                        textView9.setText("Current Database Will \n Be Cleared , Proceed?")
                                        val lightDatabase = light_db
                                        lightDatabase.removeDuplicates()
                                        val curtainDatabase = curtain_db
                                        curtainDatabase.removeDuplicates()
                                        val fanDatabase = fan_db
                                        fanDatabase.removeDuplicates()
                                        val plugDatabase = plug_db
                                        plugDatabase.removeDuplicates()
                                        val roomsDatabase = rooms_db.getInstance( this@SettingsActivity)
                                        roomsDatabase.removeDuplicates()
                                        val sixWorkertDatabase = six_workert_db.getInstance( this@SettingsActivity)
                                        sixWorkertDatabase.removeDuplicates()
                                        val temperatureDatabase = Temperature_db
                                        temperatureDatabase.removeDuplicates()
                                        val valveDatabase = valve_db
                                        valveDatabase.removeDuplicates()



                                    }catch (e:Exception){
                                        println(e)
                                    }
                                    var db =light_db
                                    val a = db.getLightsWithSubType0000()

                                    val list = arrayListOf<List<Light?>>()
                                    for (item in a ){
                                        if (item != null) {
                                            list.add(db.getLightsByMacAddress(item.mac))
                                        }
                                    }

                                    socket!!.close()

                                }





                            }


                        }catch (e:Exception){
                            println("issssssssssss")
                            println(e)
                            socket?.close()

                            socket=null
                            working=false
                            ok_receive.isSelected=false
                            try {
                                textView9.setText("Current Database Will \n Be Cleared , Proceed?")
                                sender_ip.setText("")
                                receive_steps.setText("")
                                received_status.setText("Failed")
                                connect_status.setText("Disconnecteded")

                            }catch (e:Exception){
                                println(e)
                            }






                        }

                    }



                }else{
                    Toast.makeText( this, "Connect to a Griffin Network", Toast.LENGTH_SHORT).show()
                }










            }else{
                Toast.makeText(this,"Enter SSID First",Toast.LENGTH_SHORT).show()

            }

        }




        dialog.show()
    }
    private fun showPopup_send() {
        // ساخت دیالوگ
        val dialog = Dialog(this)

        // حذف گوشه‌ها و پس‌زمینه دیالوگ
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // تنظیم لایه دلخواه برای دیالوگ
        dialog.setContentView(R.layout.sync_send_popup)


        dialog.setCanceledOnTouchOutside(false)




        var start_send_db = dialog.findViewById<Button>(R.id.start_send_db)
        var connection_status_send = dialog.findViewById<TextView>(R.id.connection_status_send)
        var target_ip_view = dialog.findViewById<TextView>(R.id.target_ip)
        var sending_steps = dialog.findViewById<TextView>(R.id.sending_steps)
        var learn_camera_ipadsddress = dialog.findViewById<TextView>(R.id.learn_camera_ipadsddress)


        sharedviewModel.selected_home.observe(this@SettingsActivity,Observer { current_home ->

            val room_db =rooms_db.getInstance(this)
            val roomDevicesDb =room_devices_db.getInstance(this)
            val room_devices=roomDevicesDb.getRoomsByHome(current_home.tag.toString())

            var light_db = light_db.getInstance(this)
            var fan_db = fan_db.getInstance(this)
            var plug_db = plug_db.getInstance(this)
            var curtain_db = curtain_db.getInstance(this)
            var valve_db = valve_db.getInstance(this)
            var Temperature_db = Temperature_db.getInstance(this)
            var sixWorkertDb_db = six_workert_db.getInstance(this)

            var camera_db = camera_db.getInstance(this)
            var scenario_db = scenario_db.Scenario_db.getInstance(this)

            var lights = light_db.getAllLights().sortedBy { it?.room_name }


            var fans = mutableListOf<fan>()
            var plugs =mutableListOf<Plug>()
            var curtains = mutableListOf<curtain>()
            var valve = mutableListOf<valve>()
            var temperature = mutableListOf<Thermostst>()
            var sixworker = mutableListOf<six_workert>()
            var rooms = mutableListOf<rooms>()
            var room = rooms()
            room.room_name = "Multifunctional"
            room.room_type = "Security"
            rooms.add(room)
            var cameras = mutableListOf<camera>()
            var scenarios = mutableListOf<scenario>()

            for(room_device in room_devices){
                val room =room_db.get_from_db_rooms(room_device.room_id!!.toInt())
                val room_fans=fan_db.getfansByRoomName(room!!.room_name)
                val room_plugs=plug_db.getPlugsByRoomName(room!!.room_name)
                val room_curtain=curtain_db.getAllcurtainsByRoomName(room!!.room_name)
                val room_valve=valve_db.getvalvesByRoomName(room!!.room_name)
                val room_temperaturee=Temperature_db.getThermostatsByRoomName(room!!.room_name)
                val room_sixworker=sixWorkertDb_db.getAllsix_workertsByRoomName(room!!.room_name)
                val room_cameras= current_home.tag?.let { camera_db.getCamerasByTag(it) }
                val room_scenario= current_home.tag?.let { scenario_db.getScenariosByTag(it) }



                rooms.add(room)

                for (fan in room_fans){
                    fans.add(fan)
                }
                for (device in room_plugs){
                    plugs.add(device)
                }
                for (device in room_curtain){
                    if (device != null) {
                        curtains.add(device)
                    }
                }
                for (device in room_valve){
                    valve.add(device)
                }
                for (device in room_temperaturee){
                    temperature.add(device)
                }
                for (device in room_sixworker){
                    if (device != null) {
                        sixworker.add(device)
                    }
                }
                if (room_scenario != null) {
                    for (device in room_scenario){
                        if (device != null) {
                            scenarios.add(device)
                        }
                    }
                }
                if (room_cameras != null) {
                    for (device in room_cameras){
                        if (device != null) {
                            cameras.add(device)
                        }
                    }
                }

            }


            fans = fans.sortedBy { it.room_name }.toMutableList()
            plugs =plugs.sortedBy { it.room_name }.toMutableList()
            curtains =curtains.sortedBy { it.room_name }.toMutableList()
            valve = valve.sortedBy { it.room_name }.toMutableList()
            temperature =temperature.sortedBy { it.room_name }.toMutableList()









            fun room_encoder(room: rooms, akid: Int, ip: String): String {
                var room_type = room.room_type
                when {
                    room_type!!.startsWith("living") -> {
                        room_type = "Hall"
                    }

                    room_type.startsWith("bathroom") -> {
                        room_type = "Bathroom"
                    }

                    room_type.startsWith("dining") -> {
                        room_type = "Hall"
                    }

                    room_type.startsWith("gust") -> {
                        room_type = "Bedroom"
                    }

                    room_type.startsWith("kid") -> {
                        room_type = "Bedroom"
                    }

                    room_type.startsWith("kitchen") -> {
                        room_type = "Kitchen"
                    }

                    room_type.startsWith("maste") -> {
                        room_type = "Bedroom"
                    }

                    room_type.startsWith("tv") -> {
                        room_type = "Hall"
                    }

                    room_type.startsWith("hayat") -> {
                        room_type = "Yard"
                    }

                    room_type.startsWith("room") -> {
                        room_type = "Bedroom"
                    }
                }
                val msg = "syro~>Griffin~>${room.room_name}~>$room_type~>$akid~>$ip"


                return msg
            }

            fun decreaseString(inputString: String): String? {

                var num = inputString.toInt()
                // کاهش مقدار عددی در یک واحد
                num -= 1
                if (num == -1) {
                    num = 0
                }
                // تبدیل عدد به رشته و اضافه کردن صفرهای لازم در ابتدا
                return num.toString().padStart(inputString.length, '0')
            }

            fun state_coder(pole_num: String?, pole_state: String): String {

                val state = arrayListOf<String>(
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-"
                )
                if (pole_state == "on" || pole_state == "1") {
                    state[pole_num?.toInt()!!] = "1"


                } else if (pole_state == "off" || pole_state == "0") {

                    state[pole_num?.toInt()!!] = "0"
                }


                return state.joinToString(separator = "")

            }


            fun light_encoder(light: Light, akid: Int, ip: String): String {

                val first = light_db.getLightsByMacAddress(light.mac).sortedBy { it!!.sub_type }[0]
                if (first!!.sub_type == "0001") {
                    println(light!!.sub_type)
                    println(light!!.Lname)
                    println(light!!.room_name)

                    light.sub_type = decreaseString(light.sub_type.toString())
                    println(light.sub_type)
                    println(light.Lname)
                }
                if (light.ip?.contains("~>") == true) {
                    val splited_light = light.ip!!.split("~>")
                    light.ip = splited_light[1]

                }


                val msg =
                    "symo~>Griffin~>${light.room_name}~>${light.Lname}~>Lght~>${light.sub_type}~>${"0000000000000000"}~>${light.mac}~>${light.ip}~>~>~>~>$akid~>$ip"

                println(msg)
                return msg
            }


            fun fan_encoder(fan: fan, akid: Int, ip: String): String {

                val first = fan_db.getfansByMacAddress(fan.mac).sortedBy { it!!.subtype }[0]
                if (first!!.subtype == "0001") {

                    fan.subtype = decreaseString(fan.subtype.toString())
                }

                val msg =
                    "symo~>Griffin~>${fan.room_name}~>${fan.Fname}~>Fano~>${fan.subtype}~>${"0000000000000000"}~>${fan.mac}~>${fan.ip}~>~>~>~>$akid~>$ip"

                return msg
            }

            fun plug_encoder(plug: Plug, akid: Int, ip: String): String {

                val first = plug_db.getPlugsByMacAddress(plug.mac).sortedBy { it?.subtype }[0]
                if (first != null) {
                    if (first.subtype == "0001") {

                        plug.subtype = decreaseString(plug.subtype.toString())
                    }
                }
//            when(plug.room_name) {
//                "livingroom" -> {
//                    plug.room_name = "Hall"
//                }
//                "bathroom1" -> {
//                    plug.room_name = "Bathroom"
//                }
//                "bathroom" -> {
//                    plug.room_name = "Bathroom"
//                }
//                "dining" -> {
//                    plug.room_name = "Hall"
//                }
//                "gust_room" -> {
//                    plug.room_name = "Bedroom"
//                }
//                "kids_room" -> {
//                    plug.room_name = "Bedroom"
//                }
//                "kitchen" -> {
//                    plug.room_name = "Kitchen"
//                }
//                "living_room" -> {
//                    plug.room_name = "Hall"
//                }
//                "master_room" -> {
//                    plug.room_name = "Bedroom"
//                }
//                "tv_room" -> {
//                    plug.room_name = "Hall"
//                }
//                "yard" -> {
//                    plug.room_name = "Yard"
//                }
//                "room" -> {
//                    plug.room_name = "Bedroom"
//                }
//            }
                println("vvvvvvvvvvvvvv ${plug.subtype}")
                val msg =
                    "symo~>Griffin~>${plug.room_name}~>${plug.Pname}~>Plug~>${plug.subtype}~>${"0000000000000000"}~>${plug.mac}~>${plug.ip}~>~>~>~>$akid~>$ip"

                return msg
            }

            fun valve_encoder(valve: valve, akid: Int, ip: String): String {

                val first = valve_db.getvalvesByMacAddress(valve.mac).sortedBy { it?.subtype }[0]
                if (first != null) {
                    if (first.subtype == "0001") {

                        valve.subtype = decreaseString(valve.subtype.toString())
                    }
                }

//            when(valve.room_name) {
//                "livingroom" -> {
//                    valve.room_name = "Hall"
//                }
//                "bathroom1" -> {
//                    valve.room_name = "Bathroom"
//                }
//                "bathroom" -> {
//                    valve.room_name = "Bathroom"
//                }
//                "dining" -> {
//                    valve.room_name = "Hall"
//                }
//                "gust_room" -> {
//                    valve.room_name = "Bedroom"
//                }
//                "kids_room" -> {
//                    valve.room_name = "Bedroom"
//                }
//                "kitchen" -> {
//                    valve.room_name = "Kitchen"
//                }
//                "living_room" -> {
//                    valve.room_name = "Hall"
//                }
//                "master_room" -> {
//                    valve.room_name = "Bedroom"
//                }
//                "tv_room" -> {
//                    valve.room_name = "Hall"
//                }
//                "yard" -> {
//                    valve.room_name = "Yard"
//                }
//                "room" -> {
//                    valve.room_name = "Bedroom"
//                }
//            }
                val msg =
                    "symo~>Griffin~>${valve.room_name}~>${valve.Vname}~>ElVa~>${valve.subtype}~>${"0000000000000000"}~>${valve.mac}~>${valve.ip}~>~>~>~>$akid~>$ip"

                return msg
            }

            fun curtain_encoder(curtain: curtain, akid: Int, ip: String): String {


                val first =
                    curtain_db.getcurtainsByMacAddress(curtain.mac).sortedBy { it!!.sub_type }[0]
                if (first!!.sub_type == "0001") {

                    curtain.sub_type = decreaseString(curtain.sub_type.toString())
                }

//            when(curtain.room_name) {
//                "livingroom" -> {
//                    curtain.room_name = "Hall"
//                }
//                "bathroom1" -> {
//                    curtain.room_name = "Bathroom"
//                }
//                "bathroom" -> {
//                    curtain.room_name = "Bathroom"
//                }
//                "dining" -> {
//                    curtain.room_name = "Hall"
//                }
//                "gust_room" -> {
//                    curtain.room_name = "Bedroom"
//                }
//                "kids_room" -> {
//                    curtain.room_name = "Bedroom"
//                }
//                "kitchen" -> {
//                    curtain.room_name = "Kitchen"
//                }
//                "living_room" -> {
//                    curtain.room_name = "Hall"
//                }
//                "master_room" -> {
//                    curtain.room_name = "Bedroom"
//                }
//                "tv_room" -> {
//                    curtain.room_name = "Hall"
//                }
//                "yard" -> {
//                    curtain.room_name = "Yard"
//                }
//                "room" -> {
//                    curtain.room_name = "Bedroom"
//                }
//            }
                val msg =
                    "symo~>Griffin~>${curtain.room_name}~>${curtain.Cname}~>Crtn~>${curtain.sub_type}~>${"0000000000000000"}~>${curtain.mac}~>${curtain.ip}~>~>~>~>$akid~>$ip"

                return msg
            }

            fun thermostat_encoder(thermostst: Thermostst, akid: Int, ip: String): String {

                val first = thermostst.mac?.let {
                    Temperature_db.getThermostatsByMac(it).sortedBy { it.subtype }
                }
                    ?.get(0)
                if (first != null) {
                    if (first.subtype == "0001") {

                        thermostst.subtype = decreaseString(thermostst.subtype.toString())
                    }
                }


                val t = "25"
                val on_off = thermostst.on_off
                val temperature = thermostst.temperature
                val fan_status = thermostst.fan_status
                val mood = thermostst.mood
                val statuss = "$on_off$t$temperature$fan_status${mood}000000000"
                val msg =
                    "symo~>Griffin~>${thermostst.room_name}~>${thermostst.name}~>Tmpr~>${thermostst.subtype}~>${statuss}~>${thermostst.mac}~>${thermostst.ip}~>~>~>~>$akid~>$ip"

                return msg
            }


            fun sixworker_encoder(sixworker: six_workert, akid: Int, ip: String): String {


                val empty = "----"


                val status =
                    sixworker.type + "!" + sixworker.pole_num + "!" + sixworker.status + "!" + sixworker.work_name
                val msg =
                    "symo~>Griffin~>${empty}~>${sixworker.name}~>SixC~>${sixworker.sub_type}~>${status}~>${sixworker.mac}~>${sixworker.ip}~>~>~>~>$akid~>$ip"

                return msg
            }

            fun camera_encoder(camera: camera, akid: Int, ip: String): String {


                val msg =
                    "symo~>Griffin~>Multifunctional~>${camera.CAMname}~>Camr~>0000~>0000000000000000~>~>~>rtsp://${camera.user}:${camera.pass}@${camera.ip}:${camera.port}/cam/realmonitor?channel=${camera.chanel}&subtype=${camera.subtype}~>~>~>$akid~>$ip"

                return msg
            }


            fun scenario_encoder(scenario: scenario, akid: Int, ip: String): String {

                val scenario_mac_lights = arrayListOf<String>()
                val scenario_status_lights = arrayListOf<String>()
                val scenario_room_name_lights = arrayListOf<String>()
                val scenario_mac_plugs = arrayListOf<String>()
                val scenario_status_plugs = arrayListOf<String>()
                val scenario_room_name_plugs = arrayListOf<String>()
                val scenario_mac_termostat = arrayListOf<String>()
                val scenario_status_termostat = arrayListOf<String>()
                val scenario_room_name_termostat = arrayListOf<String>()
                val scenario_mac_curtain = arrayListOf<String>()
                val scenario_status_curtain = arrayListOf<String>()
                val scenario_room_name_curtain = arrayListOf<String>()
                val scenario_mac_valve = arrayListOf<String>()
                val scenario_status_valve = arrayListOf<String>()
                val scenario_room_name_valve = arrayListOf<String>()
                val scenario_mac_fan = arrayListOf<String>()
                val scenario_status_fan = arrayListOf<String>()
                val scenario_room_name_fan = arrayListOf<String>()

                val scenario_lights = scenario.light?.split(",")
                val scenario_plugs = scenario.plug?.split(",")
                val scenario_termostat = scenario.thermostat?.split(",")
                println("##############" + scenario.thermostat)
                println("##############" + scenario_termostat)
                val scenario_curtain = scenario.curtain?.split(",")
                val scenario_valve = scenario.valve?.split(",")
                val scenario_fan = scenario.fan?.split(",")

                if (scenario_lights != null && (scenario.light != "" && scenario.light != null)) {


                    for (light in scenario_lights) {
                        val new_light = light_db.getLightsByLname(light.split("#")[0])
                        println(new_light.Lname)
                        val lightss =
                            light_db.getLightsByMacAddress(new_light.mac).sortedBy { it!!.sub_type }[0]
                        if (lightss!!.sub_type == "0001") {
                            val new_light_status = state_coder(new_light.sub_type?.let {
                                decreaseString(
                                    it
                                )
                            }, light.split("#")[1])
                            new_light.mac?.let { scenario_mac_lights.add(it) }
                            new_light.room_name?.let { scenario_room_name_lights.add(it) }
                            scenario_status_lights.add(new_light_status)
                        } else {
                            val new_light_status = state_coder(new_light.sub_type, light.split("#")[1])
                            new_light.mac?.let { scenario_mac_lights.add(it) }
                            new_light.room_name?.let { scenario_room_name_lights.add(it) }
                            scenario_status_lights.add(new_light_status)
                        }
                    }
                }
                if (scenario_plugs != null && (scenario.plug != "" && scenario.plug != null)) {
                    for (plug in scenario_plugs) {
                        val new_plug = plug_db.getPlugByCname(plug.split("#")[0])
                        val plugss =
                            plug_db.getPlugsByMacAddress(new_plug!!.mac).sortedBy { it!!.subtype }[0]
                        if (plugss!!.subtype == "0001") {
                            val new_plug_status = state_coder(
                                new_plug?.subtype?.let { decreaseString(it) },
                                plug.split("#")[1]
                            )
                            new_plug?.mac?.let { scenario_mac_plugs.add(it) }
                            new_plug?.room_name?.let { scenario_room_name_plugs.add(it) }
                            scenario_status_plugs.add(new_plug_status)

                        } else {

                            val new_plug_status = state_coder(new_plug?.subtype, plug.split("#")[1])
                            new_plug?.mac?.let { scenario_mac_plugs.add(it) }
                            new_plug?.room_name?.let { scenario_room_name_plugs.add(it) }
                            scenario_status_plugs.add(new_plug_status)

                        }

                    }
                }
                if (scenario_termostat != null && (scenario.thermostat != "" && scenario.thermostat != null)) {
                    println("+++++++")
                    for (termostat in scenario_termostat) {
                        println(termostat.split("#")[0])
                        val new_termostat = Temperature_db.getThermostatByName(termostat.split("#")[0])
//                    val new_termostat_status = termostat.split("#")[1].split(Regex("[^0-9]")).joinToString(separator = "").padEnd(16, '0')

                        val status = termostat.split("#")[1].substringBefore("!")
                        val temperature = substringBetween(termostat.split("#")[1], '!', '$')
                        val mood = substringBetween(termostat.split("#")[1], '$', '@')
                        val fanStatus = termostat.split("#")[1].substringAfter("@")

                        val new_termostat_status =
                            "${status}--${temperature}${mood}${fanStatus}000000000"

                        new_termostat?.mac?.let { scenario_mac_termostat.add(it) }
                        scenario_status_termostat.add(new_termostat_status)
                        new_termostat?.room_name?.let { scenario_room_name_termostat.add(it) }
                    }
                }
                if (scenario_curtain != null && (scenario.curtain != "" && scenario.curtain != null)) {
                    for (curtain in scenario_curtain) {
                        val new_curtain = curtain_db.getCurtainByCname(curtain.split("#")[0])
                        val newcurtain_status = curtain.split("#")[1]
                        new_curtain?.mac?.let { scenario_mac_curtain.add(it) }
                        new_curtain?.room_name?.let { scenario_room_name_curtain.add(it) }
                        scenario_status_curtain.add(newcurtain_status)
                    }
                }
                if (scenario_valve != null && (scenario.curtain != "" && scenario.curtain != null)) {
                    for (valve in scenario_valve) {
                        val new_valve = valve_db.getvalveByCname(valve.split("#")[0])
                        val valvess =
                            valve_db.getvalvesByMacAddress(new_valve!!.mac).sortedBy { it!!.subtype }[0]
                        if (valvess!!.subtype == "0001") {
                            val new_valve_status = state_coder(
                                new_valve?.subtype?.let { decreaseString(it) },
                                valve.split("#")[1]
                            )
                            new_valve?.mac?.let { scenario_mac_valve.add(it) }
                            new_valve?.room_name?.let { scenario_room_name_valve.add(it) }
                            scenario_status_valve.add(new_valve_status)
                        } else {

                            val new_valve_status = state_coder(new_valve?.subtype, valve.split("#")[1])
                            new_valve?.mac?.let { scenario_mac_valve.add(it) }
                            new_valve?.room_name?.let { scenario_room_name_valve.add(it) }
                            scenario_status_valve.add(new_valve_status)
                        }

                    }
                }
                if (scenario_fan != null && (scenario.fan != "" && scenario.fan != null)) {
                    for (fan in scenario_fan) {
                        val new_fan = fan_db.get_from_db_fan_By_name(fan.split("#")[0])
                        val fanss =
                            fan_db.getfansByMacAddress(new_fan!!.mac).sortedBy { it!!.subtype }[0]
                        val is_n = light_db.getLightsByMacAddress(new_fan.mac)
                        if (fanss!!.subtype == "0001" && is_n.count() == 0) {
                            val new_fan_status = state_coder(
                                new_fan?.subtype?.let { decreaseString(it) },
                                fan.split("#")[1]
                            )

                            new_fan?.mac?.let { scenario_mac_fan.add(it) }
                            new_fan?.room_name?.let { scenario_room_name_fan.add(it) }
                            scenario_status_fan.add(new_fan_status)
                        } else {
                            val new_fan_status = state_coder(new_fan?.subtype, fan.split("#")[1])

                            new_fan?.mac?.let { scenario_mac_fan.add(it) }
                            new_fan?.room_name?.let { scenario_room_name_fan.add(it) }
                            scenario_status_fan.add(new_fan_status)

                        }
                    }
                }
                val my_senario_list2 = mutableListOf<String>()
                my_senario_list2.add(scenario_room_name_lights.joinToString(separator = "***"))
                my_senario_list2.add(scenario_room_name_termostat.joinToString(separator = "***"))
                my_senario_list2.add(scenario_room_name_curtain.joinToString(separator = "***"))
                my_senario_list2.add(scenario_room_name_valve.joinToString(separator = "***"))
                my_senario_list2.add(scenario_room_name_fan.joinToString(separator = "***"))
                my_senario_list2.add(scenario_room_name_plugs.joinToString(separator = "***"))
                var final_room_names = my_senario_list2.joinToString(separator = "***").trimEnd('*')

                val my_senario_list_mac = mutableListOf<String>()
                my_senario_list_mac.add(scenario_mac_lights.joinToString(separator = "***"))
                my_senario_list_mac.add(scenario_mac_termostat.joinToString(separator = "***"))
                my_senario_list_mac.add(scenario_mac_curtain.joinToString(separator = "***"))
                my_senario_list_mac.add(scenario_mac_valve.joinToString(separator = "***"))
                my_senario_list_mac.add(scenario_mac_fan.joinToString(separator = "***"))
                my_senario_list_mac.add(scenario_mac_plugs.joinToString(separator = "***"))
                var final_mac = my_senario_list_mac.joinToString(separator = "***").trimEnd('*')
                println("final_mac : " + final_mac)

                val mysenario_list_status = mutableListOf<String>()
                mysenario_list_status.add(scenario_status_lights.joinToString(separator = "***"))
                mysenario_list_status.add(scenario_status_termostat.joinToString(separator = "***"))
                mysenario_list_status.add(scenario_status_curtain.joinToString(separator = "***"))
                my_senario_list_mac.add(scenario_mac_valve.joinToString(separator = "***"))
                mysenario_list_status.add(scenario_status_fan.joinToString(separator = "***"))
                mysenario_list_status.add(scenario_status_plugs.joinToString(separator = "***"))
                var final_status = mysenario_list_status.joinToString(separator = "***").trimEnd('*')

                println("sysc~>Griffin~>${scenario.scenario_name}~>${scenario.id}~>$final_room_names~>$final_mac~>$final_status~>~>~>~>~>$akid~>$ip")
                println("----------------------")
                val final_mac_dublicates = findDuplicateIndices(final_mac.split("***"))
                println(final_mac_dublicates)
                val new_status = mergeGroupsOfIndices(final_status, final_mac_dublicates)

                final_status = new_status.joinToString(separator = "***")

                final_mac = removeDuplicates(final_mac.split("***")).joinToString(separator = "***")

                final_room_names = retainFirstIndexOnly(
                    final_room_names.split("***"),
                    final_mac_dublicates
                ).joinToString(separator = "***")


                var msg =
                    "sysc~>Griffin~>${scenario.scenario_name}~>${scenario.id}~>$final_room_names~>$final_mac~>$final_status~>~>~>~>~>$akid~>$ip"




                println(msg)
                return msg
            }


            fun isConnectedToWifi(context: Context): Boolean {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
                return networkInfo?.type == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected
            }

            start_send_db?.setOnClickListener {


                for(room_device in room_devices){
                    val room =room_db.get_from_db_rooms(room_device.room_id!!.toInt())
                    val room_fans=fan_db.getfansByRoomName(room!!.room_name)
                    val room_plugs=plug_db.getPlugsByRoomName(room!!.room_name)
                    val room_curtain=curtain_db.getAllcurtainsByRoomName(room!!.room_name)
                    val room_valve=valve_db.getvalvesByRoomName(room!!.room_name)
                    val room_temperaturee=Temperature_db.getThermostatsByRoomName(room!!.room_name)
                    val room_sixworker=sixWorkertDb_db.getAllsix_workertsByRoomName(room!!.room_name)
                    val room_cameras= current_home.tag?.let { camera_db.getCamerasByTag(it) }
                    val room_scenario= current_home.tag?.let { scenario_db.getScenariosByTag(it) }



                    rooms.add(room)

                    for (fan in room_fans){
                        fans.add(fan)
                    }
                    for (device in room_plugs){
                        plugs.add(device)
                    }
                    for (device in room_curtain){
                        if (device != null) {
                            curtains.add(device)
                        }
                    }
                    for (device in room_valve){
                        valve.add(device)
                    }
                    for (device in room_temperaturee){
                        temperature.add(device)
                    }
                    for (device in room_sixworker){
                        if (device != null) {
                            sixworker.add(device)
                        }
                    }
                    if (room_scenario != null) {
                        for (device in room_scenario){
                            if (device != null) {
                                scenarios.add(device)
                            }
                        }
                    }
                    if (room_cameras != null) {
                        for (device in room_cameras){
                            if (device != null) {
                                cameras.add(device)
                            }
                        }
                    }

                }


                fans = fans.sortedBy { it.room_name }.toMutableList()
                plugs =plugs.sortedBy { it.room_name }.toMutableList()
                curtains =curtains.sortedBy { it.room_name }.toMutableList()
                valve = valve.sortedBy { it.room_name }.toMutableList()
                temperature =temperature.sortedBy { it.room_name }.toMutableList()











                for (light in lights) {
                    println(light!!.Lname + "           " + light!!.sub_type)
                }


                val wifiManager = this.applicationContext.getSystemService(
                    Context.WIFI_SERVICE
                ) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                val ssid = wifiInfo.ssid
                if (!setting_network_db.getInstance(this).isDatabaseEmpty()){

                    val db_ssid =
                        setting_network_db.getInstance(this).get_from_db_network_manual(1)?.modem_ssid

                    if (isConnectedToWifi(this) && ssid.replace("\"", "") == db_ssid) {


                        if (side) {
                            println("in")
                            socket?.close()
                            socket = null

                            try {

                                start_send_db.setText("Start")
                                target_ip_view!!.setText("")
                                learn_camera_ipadsddress!!.setText("")
                                sending_steps!!.setText("")

                                connection_status_send!!.setText("Disconnected")
                            } catch (e: Exception) {
                                println(e)
                            }
                            side = false

                        } else {
                            println("ese")
                            side = true

                            try {
                                try {

                                    start_send_db.text = "Please Wait..."
                                } catch (e: Exception) {
                                    println(e)
                                }
                                try {

                                    connection_status_send?.setText("Connecting")
                                } catch (e: Exception) {
                                    println(e)
                                }

                                GlobalScope.launch(Dispatchers.IO) {

                                    val bufferSize = 1024
                                    val receiveData = ByteArray(bufferSize)


                                    var is_finished = false
                                    var target_ip = convertIpToBroadcast(this@SettingsActivity)
                                    var conection_status = false
                                    var ak_id = 1
                                    var send_level = 1
                                    val received_akid = 1



                                    while (side) {

                                        println(conection_status)

                                        try {
                                            if (socket == null || socket?.isClosed == true || socket?.isBound == true) {

                                                socket = DatagramSocket(8089)
                                                println("Listenning to 8089..")
                                            }
                                            if (is_finished || side == false) {
                                                break
                                            }
                                        } catch (e: Exception) {

                                            socket?.close()
                                            socket = null

                                            try {

                                                start_send_db.setText("Start")
                                                target_ip_view!!.setText("")
                                                learn_camera_ipadsddress!!.setText("")
                                                sending_steps!!.setText("")
                                                conection_status = false
                                                connection_status_send!!.setText("Disconnected")
                                            } catch (e: Exception) {
                                                println(e)
                                            }
                                            side = false

                                            break
                                        }


                                        var serverAddress = InetAddress.getByName(target_ip)
                                        val serverPort = 8089

                                        if (!conection_status) {
                                            println("connection test")

                                            val message = "sygi~>${checkIP(this@SettingsActivity)}"
                                            val sendData = message.toByteArray()
                                            val sendPacket = DatagramPacket(
                                                sendData,
                                                sendData.size,
                                                serverAddress,
                                                serverPort
                                            )
                                            socket?.send(sendPacket)
                                            socket?.close()
                                            receiveUdpMessage({ receivedMessage ->
                                                println(receivedMessage)
                                                val listed_msg = receivedMessage.split("~>")

                                                if (listed_msg[0] == "sysi") {
                                                    try {

                                                        connection_status_send?.setText("Connected")
                                                    } catch (e: Exception) {
                                                        println(e)
                                                    }
                                                    try {

                                                        target_ip_view.setText(listed_msg[1])
                                                    } catch (e: Exception) {
                                                        println(e)
                                                    }
                                                    println("done")
                                                    conection_status = true
                                                    target_ip = listed_msg.reversed()[0]
                                                    send_level = 2

//                               return@receiveUdpMessagebreak
                                                }


                                            }, 8089, 1000)


                                        } else {
                                            println(ak_id)
                                            println(rooms_counter)
                                            println(rooms.count())

                                            if (rooms_counter < rooms.count()) {
                                                serverAddress = InetAddress.getByName(target_ip)
                                                try {

                                                    sending_steps?.setText("Rooms")
                                                } catch (e: Exception) {
                                                    println(e)
                                                }
                                                val messagee = rooms[rooms_counter]?.let { it1 ->
                                                    room_encoder(
                                                        it1, ak_id,
                                                        checkIP(this@SettingsActivity)
                                                    )
                                                }
                                                val sendData = messagee?.toByteArray()
                                                val sendPacket = DatagramPacket(
                                                    sendData,
                                                    sendData!!.size,
                                                    serverAddress,
                                                    serverPort
                                                )
                                                socket?.send(sendPacket)
                                                socket?.close()

                                                receiveUdpMessage({ receivedMessage ->
                                                    val listed_receivedMessage = receivedMessage.split("~>")
                                                    if (listed_receivedMessage[0] == "syac") {
                                                        if (listed_receivedMessage[1] == ak_id.toString()) {
                                                            rooms_counter += 1
                                                            ak_id += 1

                                                        }
                                                    }
                                                }, 8089, 1000)

                                            } else if (lights_counter < lights.count()) {
                                                serverAddress = InetAddress.getByName(target_ip)
                                                try {

                                                    sending_steps?.setText("Lights")
                                                } catch (e: Exception) {
                                                    println(e)
                                                }
                                                val messagee =
                                                    lights[lights_counter]?.let { it1 ->
                                                        light_encoder(
                                                            it1,
                                                            ak_id,
                                                            checkIP(this@SettingsActivity)
                                                        )
                                                    }
                                                println(lights[lights_counter]!!.Lname + "       " + lights[lights_counter]!!.sub_type)
                                                val sendData = messagee?.toByteArray()
                                                val sendPacket = DatagramPacket(
                                                    sendData,
                                                    sendData!!.size,
                                                    serverAddress,
                                                    serverPort
                                                )
                                                socket?.send(sendPacket)
                                                socket?.close()
                                                receiveUdpMessage({ receivedMessage ->
                                                    val listed_receivedMessage = receivedMessage.split("~>")
                                                    if (listed_receivedMessage[0] == "syac") {
                                                        if (listed_receivedMessage[1] == ak_id.toString()) {
                                                            lights_counter += 1
                                                            ak_id += 1

                                                        }
                                                    }
                                                }, 8089, 1000)


                                            } else if (fans_counter < fans.count()) {
                                                serverAddress = InetAddress.getByName(target_ip)
                                                try {

                                                    sending_steps?.setText("Fans")
                                                } catch (e: Exception) {
                                                    println(e)
                                                }
                                                val messagee =
                                                    fans[fans_counter]?.let { it1 ->
                                                        fan_encoder(
                                                            it1,
                                                            ak_id,
                                                            checkIP(this@SettingsActivity)
                                                        )
                                                    }
                                                val sendData = messagee?.toByteArray()
                                                val sendPacket = DatagramPacket(
                                                    sendData,
                                                    sendData!!.size,
                                                    serverAddress,
                                                    serverPort
                                                )
                                                socket?.send(sendPacket)
                                                socket?.close()
                                                receiveUdpMessage({ receivedMessage ->
                                                    val listed_receivedMessage = receivedMessage.split("~>")
                                                    if (listed_receivedMessage[0] == "syac") {
                                                        if (listed_receivedMessage[1] == ak_id.toString()) {
                                                            fans_counter += 1
                                                            ak_id += 1

                                                        }
                                                    }
                                                }, 8089, 1000)


                                            } else if (plugs_counter < plugs.count()) {
                                                serverAddress = InetAddress.getByName(target_ip)
                                                try {

                                                    sending_steps?.setText("Plugs")
                                                } catch (e: Exception) {
                                                    println(e)
                                                }
                                                val messagee =
                                                    plug_encoder(plugs[plugs_counter], ak_id, checkIP(this@SettingsActivity))
                                                val sendData = messagee?.toByteArray()
                                                val sendPacket = DatagramPacket(
                                                    sendData,
                                                    sendData!!.size,
                                                    serverAddress,
                                                    serverPort
                                                )
                                                socket?.send(sendPacket)
                                                socket?.close()
                                                receiveUdpMessage({ receivedMessage ->
                                                    val listed_receivedMessage = receivedMessage.split("~>")
                                                    if (listed_receivedMessage[0] == "syac") {
                                                        if (listed_receivedMessage[1] == ak_id.toString()) {
                                                            plugs_counter += 1
                                                            ak_id += 1

                                                        }
                                                    }
                                                }, 8089, 1000)


                                            } else if (valve_counter < valve.count()) {
                                                serverAddress = InetAddress.getByName(target_ip)
                                                try {

                                                    sending_steps?.setText("Valve")
                                                } catch (e: Exception) {
                                                    println(e)
                                                }
                                                val messagee = valve_encoder(
                                                    valve[valve_counter],
                                                    ak_id,
                                                    checkIP(this@SettingsActivity)
                                                )
                                                val sendData = messagee?.toByteArray()
                                                val sendPacket = DatagramPacket(
                                                    sendData,
                                                    sendData!!.size,
                                                    serverAddress,
                                                    serverPort
                                                )
                                                socket?.send(sendPacket)
                                                socket?.close()
                                                receiveUdpMessage({ receivedMessage ->
                                                    val listed_receivedMessage = receivedMessage.split("~>")
                                                    if (listed_receivedMessage[0] == "syac") {
                                                        if (listed_receivedMessage[1] == ak_id.toString()) {
                                                            valve_counter += 1
                                                            ak_id += 1

                                                        }
                                                    }
                                                }, 8089, 1000)


                                            } else if (curtains_counter < curtains.count()) {
                                                serverAddress = InetAddress.getByName(target_ip)
                                                try {

                                                    sending_steps?.setText("Curtain")
                                                } catch (e: Exception) {
                                                    println(e)
                                                }
                                                val messagee = curtains[curtains_counter]?.let { it1 ->
                                                    curtain_encoder(
                                                        it1, ak_id, checkIP(this@SettingsActivity)
                                                    )
                                                }
                                                val sendData = messagee?.toByteArray()
                                                val sendPacket = DatagramPacket(
                                                    sendData,
                                                    sendData!!.size,
                                                    serverAddress,
                                                    serverPort
                                                )
                                                socket?.send(sendPacket)
                                                socket?.close()
                                                receiveUdpMessage({ receivedMessage ->
                                                    val listed_receivedMessage = receivedMessage.split("~>")
                                                    if (listed_receivedMessage[0] == "syac") {
                                                        if (listed_receivedMessage[1] == ak_id.toString()) {
                                                            curtains_counter += 1
                                                            ak_id += 1

                                                        }
                                                    }
                                                }, 8089, 1000)


                                            } else if (temperature_counter < temperature.count()) {
                                                serverAddress = InetAddress.getByName(target_ip)
                                                try {

                                                    sending_steps?.setText("Thermostat")
                                                } catch (e: Exception) {
                                                    println(e)
                                                }
                                                val messagee =
                                                    temperature[temperature_counter]?.let { it1 ->
                                                        thermostat_encoder(
                                                            it1, ak_id, checkIP(this@SettingsActivity)
                                                        )
                                                    }
                                                val sendData = messagee?.toByteArray()
                                                val sendPacket = DatagramPacket(
                                                    sendData,
                                                    sendData!!.size,
                                                    serverAddress,
                                                    serverPort
                                                )
                                                socket?.send(sendPacket)
                                                socket?.close()
                                                receiveUdpMessage({ receivedMessage ->
                                                    val listed_receivedMessage = receivedMessage.split("~>")
                                                    if (listed_receivedMessage[0] == "syac") {
                                                        if (listed_receivedMessage[1] == ak_id.toString()) {
                                                            temperature_counter += 1
                                                            ak_id += 1

                                                        }
                                                    }
                                                }, 8089, 1000)


                                            } else if (cameras_counter < cameras.count()) {
                                                serverAddress = InetAddress.getByName(target_ip)
                                                try {

                                                    sending_steps?.setText("Camera")
                                                } catch (e: Exception) {
                                                    println(e)
                                                }
                                                val messagee = cameras[cameras_counter]?.let { it1 ->
                                                    camera_encoder(
                                                        it1, ak_id, checkIP(this@SettingsActivity)
                                                    )
                                                }
                                                val sendData = messagee?.toByteArray()
                                                val sendPacket = DatagramPacket(
                                                    sendData,
                                                    sendData!!.size,
                                                    serverAddress,
                                                    serverPort
                                                )
                                                socket?.send(sendPacket)
                                                socket?.close()
                                                receiveUdpMessage({ receivedMessage ->
                                                    val listed_receivedMessage = receivedMessage.split("~>")
                                                    if (listed_receivedMessage[0] == "syac") {
                                                        if (listed_receivedMessage[1] == ak_id.toString()) {
                                                            cameras_counter += 1
                                                            ak_id += 1

                                                        }
                                                    }
                                                }, 8089, 1000)



                                                println("aaaaaaaaaaaaaaaaaaaaaaaaa  " + scenarios_counter + "        " + scenarios.count())
                                            } else if (sixworker_counter < sixworker.count()) {
                                                serverAddress = InetAddress.getByName(target_ip)
                                                try {

                                                    sending_steps?.setText("Six worker")
                                                } catch (e: Exception) {
                                                    println(e)
                                                }
                                                val messagee = sixworker[sixworker_counter]?.let { it1 ->
                                                    sixworker_encoder(
                                                        it1, ak_id, checkIP(this@SettingsActivity)
                                                    )
                                                }

                                                val sendData = messagee?.toByteArray()
                                                val sendPacket = DatagramPacket(
                                                    sendData,
                                                    sendData!!.size,
                                                    serverAddress,
                                                    serverPort
                                                )
                                                socket?.send(sendPacket)
                                                socket?.close()
                                                receiveUdpMessage({ receivedMessage ->
                                                    val listed_receivedMessage = receivedMessage.split("~>")
                                                    if (listed_receivedMessage[0] == "syac") {
                                                        if (listed_receivedMessage[1] == ak_id.toString()) {
                                                            sixworker_counter += 1
                                                            ak_id += 1

                                                        }
                                                    }
                                                }, 8089, 1000)


                                            } else if (scenarios_counter < scenarios.count()) {

                                                println("                    " + scenarios.count())
                                                serverAddress = InetAddress.getByName(target_ip)
                                                try {

                                                    sending_steps?.setText("Scenarios")
                                                } catch (e: Exception) {
                                                    println(e)
                                                }

                                                try {

                                                    val messagee = scenarios[scenarios_counter].let { it1 ->
                                                        scenario_encoder(
                                                            it1, ak_id, checkIP(this@SettingsActivity)
                                                        )
                                                    }
                                                    println(messagee)
                                                    val sendData = messagee?.toByteArray()
                                                    val sendPacket = DatagramPacket(
                                                        sendData,
                                                        sendData!!.size,
                                                        serverAddress,
                                                        serverPort
                                                    )
                                                    socket?.send(sendPacket)
                                                    socket?.close()
                                                    receiveUdpMessage({ receivedMessage ->
                                                        val listed_receivedMessage =
                                                            receivedMessage.split("~>")
                                                        if (listed_receivedMessage[0] == "syac") {
                                                            if (listed_receivedMessage[1] == ak_id.toString()) {
                                                                scenarios_counter += 1
                                                                ak_id += 1

                                                            }
                                                        }
                                                    }, 8089, 1000)


                                                } catch (e: Exception) {
                                                    println(e)
                                                    scenarios_counter += 1
                                                    socket?.close()
                                                }


                                            } else {
                                                println(serverAddress)
                                                val messagee =
                                                    "sydn~>$ak_id~>${checkIP(this@SettingsActivity)}"
                                                val sendData = messagee.toByteArray()
                                                val sendPackett = DatagramPacket(
                                                    sendData,
                                                    sendData.size,
                                                    serverAddress,
                                                    serverPort
                                                )
                                                socket?.send(sendPackett)
                                                socket?.close()
                                                socket?.close()

                                                receiveUdpMessage({ receivedMessage ->
                                                    val listed_receivedMessage = receivedMessage.split("~>")
                                                    if (listed_receivedMessage[0] == "syac" && (listed_receivedMessage[1] == ak_id.toString())) {
                                                        is_finished = true
                                                        this@SettingsActivity.runOnUiThread {
                                                            try {
                                                                start_send_db.setText("Start")
                                                                sending_steps?.setText("")
                                                                learn_camera_ipadsddress!!.setText("Finished")
                                                                connection_status_send?.setText("Disconect")

                                                            } catch (e: Exception) {
                                                                println(e)
                                                            }

                                                            rooms_counter = 0
                                                            scenarios_counter = 0
                                                            cameras_counter = 0
                                                            sixworker_counter = 0
                                                            temperature_counter = 0
                                                            curtains_counter = 0
                                                            valve_counter = 0
                                                            plugs_counter = 0
                                                            lights_counter = 0
                                                            fans_counter = 0


                                                        }

                                                    }
                                                }, 8089, 1000)

                                            }


                                        }

                                        Thread.sleep(800)
                                    }
                                    socket?.close()

                                    side = false
                                }

                                rooms_counter = 0
                                scenarios_counter = 0
                                cameras_counter = 0
                                sixworker_counter = 0
                                temperature_counter = 0
                                curtains_counter = 0
                                valve_counter = 0
                                plugs_counter = 0
                                lights_counter = 0
                                fans_counter = 0

                            } catch (e: Exception) {
                                socket?.close()
                                socket = null


                                try {

                                    start_send_db.setText("Start")
                                    target_ip_view!!.setText("")
                                    learn_camera_ipadsddress!!.setText("")
                                    sending_steps!!.setText("")
                                    connection_status_send!!.setText("Disconnected")
                                } catch (e: Exception) {
                                    println(e)
                                }
                                side = false
                                rooms_counter = 0
                                scenarios_counter = 0
                                cameras_counter = 0
                                sixworker_counter = 0
                                temperature_counter = 0
                                curtains_counter = 0
                                valve_counter = 0
                                plugs_counter = 0
                                lights_counter = 0
                                fans_counter = 0
                                socket?.close()
                                println(e)


                            }

                        }


                    } else {
                        Toast.makeText(this, "Connect to a Griffin Network", Toast.LENGTH_SHORT).show()
                    }




                }else{
                    Toast.makeText(this, "Enter SSID First", Toast.LENGTH_SHORT).show()

                }

            }




        })








        dialog.show()
    }

    private fun showPopup_receive_or_send() {
        // ساخت دیالوگ
        val dialog = Dialog(this)

        // حذف گوشه‌ها و پس‌زمینه دیالوگ
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // تنظیم لایه دلخواه برای دیالوگ
        dialog.setContentView(R.layout.send_or_receive_popup)
        val select_send_btn = dialog.findViewById<Button>(R.id.select_send_btn)
        val select_receive_btn = dialog.findViewById<Button>(R.id.select_receive_btn)
        select_send_btn.setOnClickListener {
            showPopup_send()
            dialog.dismiss()
        }
        select_receive_btn.setOnClickListener {
            dialog.dismiss()
            showPopup_receive()
        }


        dialog.setCanceledOnTouchOutside(false)

//        val closeButton = dialog.findViewById<Button>(R.id.closeButton)
//        closeButton.setOnClickListener {
//            dialog.dismiss()
//        }

        dialog.show()
    }



}