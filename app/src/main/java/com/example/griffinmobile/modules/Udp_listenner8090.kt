package com.example.griffinmobile.mudels

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.KeyguardManager
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import com.example.griffinmobile.R
//import com.example.griffinmobile.dashboard
import com.example.griffinmobile.database.*
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException
import java.security.AccessController.getContext

@SuppressLint("StaticFieldLeak")
object UdpListener8090 {
    private const val LISTEN_PORT = 8090
    private var socket: DatagramSocket? = null
    private var receivePacket: DatagramPacket? = null
    private var running: Boolean = false
    private var paused: Boolean = false
    private var contextListener: WeakReference<ContextListener>? = null
    var activity: Activity?=null
    private var activityRef: WeakReference<Activity>? = null
    lateinit var sharedViewModel: SharedViewModel

    fun setContextListener(listener: ContextListener) {
        contextListener = WeakReference(listener)
    }

    val context: Context
        get() = contextListener?.get()?.getContext() ?: throw IllegalStateException("ContextListener is not set")

    fun init(activity: Activity) {
        this.activity = activity
        activityRef = WeakReference(activity)
        sharedViewModel = ViewModelProvider(activity as AppCompatActivity)[SharedViewModel::class.java]

    }

    private fun initializeSocket() {
        try {
            socket = DatagramSocket(LISTEN_PORT , InetAddress.getByName("0.0.0.0")).apply {
                reuseAddress = true
            }
            socket!!.broadcast=true

            val bufferSize = 1024
            val receiveData = ByteArray(bufferSize)
            receivePacket = DatagramPacket(receiveData, receiveData.size)
            paused = false
            println("Socket initialized on port: $LISTEN_PORT")
        } catch (e: SocketException) {
            println("Socket could not be opened, or could not bind to the port: $LISTEN_PORT. Error: ${e.message}")
        }
    }

//    fun security_popup(context: Activity){
//
//
//        val security_db_handler = security_db.getInstance(context)
//        if (!security_db_handler.isEmptysecurity_tabale()) {
//            val current_db = security_db_handler.get_from_db_security(1)
//
//            val time = (current_db!!.arm_active_deley?.toInt())?.times(1000)
//            val pass = current_db.password_security
//
//
//            val inflater5 = LayoutInflater.from(context)
//            val popupView5: View = inflater5.inflate(R.layout.popup_layout, null)
//            val wrong_password_security =
//                popupView5.findViewById<TextView>(R.id.wrong_password_security)
//            wrong_password_security.visibility = View.GONE
//            val ok = popupView5.findViewById<Button>(R.id.ok_btn)
//            val enterd_password = popupView5.findViewById<EditText>(R.id.intered_pass)
//
//
//            val alertDialogBuilder5 = AlertDialog.Builder(context)
//            alertDialogBuilder5.setView(popupView5)
//
//            val tvCountdown = popupView5.findViewById<TextView>(R.id.tvCountdown)
//
//            var is_ok = false
//
//            val horn = CarHornSoundPlayer.getInstance()
//            val alertDialog5 = alertDialogBuilder5.create()
//            alertDialog5.show()
//            alertDialog5.setCanceledOnTouchOutside(false)
//            alertDialog5.setCancelable(false)
//            ok.setOnClickListener {
//                if (enterd_password.text.toString() == pass.toString()) {
//                    alertDialog5.dismiss()
//                    horn.stopHornSound()
//                    Toast.makeText(context, "Disarmed...", Toast.LENGTH_LONG).show()
//                    is_ok = true
//                    sharedViewModel.countdownLiveData.postValue(999999)
//                } else {
//                    wrong_password_security.visibility = View.VISIBLE
//                }
//            }
//            // حذف بک‌گراند دیالوگ الرت
//            alertDialog5.window?.setBackgroundDrawableResource(android.R.color.transparent)
//
//            val countDownTimer = object :
//                CountDownTimer(time!!.toLong(), 1000) { // 10000 میلی‌ثانیه معادل 10 ثانیه است
//                override fun onTick(millisUntilFinished: Long) {
//                    val seconds = millisUntilFinished / 1000
//                    tvCountdown.text = seconds.toString()
//                    sharedViewModel.countdownLiveData.postValue(seconds)
//                }
//
//                override fun onFinish() {
//                    sharedViewModel.countdownLiveData.postValue(999999)
//                    tvCountdown.text = "0"
//                    if (!is_ok) {
//                        println("bang")
//                        tvCountdown.setTextColor(Color.RED)
//                        horn.startHornSound()
//
//                    } else {
//                        alertDialog5.dismiss()
//                        horn.stopHornSound()
//                        println("disarmed")
//
//                    }
//                    // اینجا می‌توانید کدی برای اجرا پس از پایان شمارنده قرار دهید
//                    //                    println("finish")
//                }
//            }
//            countDownTimer.start()
//
//
//            //        }
//        }
//    }



    @SuppressLint("ServiceCast")
    fun startListening() {

        println("starting")
//        if (running) return
//        running = true

        Thread {
            println("thered")
            while (running) {
                if (!paused) {
                    initializeSocket()
                    try {
                        socket!!.receive(receivePacket)
                        val receivedMessage = receivePacket?.let { String(it.data, 0, receivePacket!!.length) }
                        println("received message from 8090 : $receivedMessage")

                        val masterSlaveDb= Master_slave_db.getInstance(context)
                        if (masterSlaveDb.getStatusById(1)== "master"){
                            if (receivedMessage.toString()== "15"){
//                                activity?.runOnUiThread{
//                                    val keyguardManager = context.applicationContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                        keyguardManager.requestDismissKeyguard(activity!!, null)
//                                    }
//
//                                    activity!!.runOnUiThread {
//
//                                        var wakeLock: PowerManager.WakeLock? = null
//                                        val powerManager = context.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
//                                        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp::MyWakelockTag")
//                                        wakeLock?.acquire()
//                                        val intent = Intent(activity, dashboard::class.java)
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                        context.startActivity(intent)
//
//
//                                        security_popup(activity!!)
//                                    }
//                                }

                            }else if (receivedMessage != null) {
                                if(receivedMessage.split("~>")[0] =="sixc" ){
                                    val listed_response=receivedMessage.split("~>")
                                    val six_worker_db = six_workert_db.getInstance(activity!!)
                                    val current_six_worker = six_worker_db.getsix_workertsByMacAddress(listed_response[1].toString())[0]
                                    val scenario_db = scenario_db.Scenario_db.getInstance(activity!!)
                                    val current_pole=listed_response[2].toInt()
                                    var scenarios_id= try {
                                        current_six_worker!!.sub_type!!.split(",").toMutableList()


                                    }catch (e:Exception){

                                        null
                                    }
                                    println("scenarios_id   "+scenarios_id)
                                    if (scenarios_id?.get(current_pole-1)   != null || scenarios_id?.get(current_pole-1)  != "" ){

                        //                                val inflater = LayoutInflater.from(activity)
                        //
                        ////        val customPopupView: View = inflater.inflate(R.layout.scenario_loading, null)
                        //                                val popupView: View = inflater.inflate(R.layout.scenario_loading, null)
                        //                                val popupWidth = 530
                        //                                val popupHeight = 530
                        //                                // ایجاد لایه‌ی کاستوم
                        //                                // ایجاد PopupWindow با استفاده از لایه‌ی کاستوم
                        ////        val loadingView=popupView.findViewById<CustomLoadingCircle>(R.id.custom_loading_view)
                        //                                val popupWindow = PopupWindow(popupView, popupWidth, popupHeight, true)
                        //                                val alertDialogBuilder = AlertDialog.Builder(activity)
                        //                                alertDialogBuilder.setView(popupView)
                        //                                val alertDialog = alertDialogBuilder.create()
                        //                                val loading_view=popupView.findViewById<CustomLoadingLine>(R.id.loadingAnim)


                                        val target_scenario=scenario_db.getScenarioById(scenarios_id?.get(current_pole-1)?.toInt())

                                        try {
                                            val status = sharedViewModel.is_doing.value

                                            println(status)
                                            var scenario_side="user"

                                            val selectedItem = target_scenario

                                            if (scenario_side=="user"){

                                                println("userrrr")




                                                if ((status != "bussy") || (status==null)){
                                                    if (selectedItem != null) {
                                                        activity!!.runOnUiThread {

                                                            Toast.makeText(activity, selectedItem.scenario_name, Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                        //                                            popupWindow.showAtLocation(requireViewById(R.layout), Gravity.CENTER, 0, 0)


                                                    Thread{
                                                        activity!!.runOnUiThread {
                                                            sharedViewModel.update_is_doing("bussy")
                                                        }

                        //                                                loading_view.increaseProgress(0.0)

                                                        val light_database_handler= light_db.getInstance(activity!!)
                                                        val thermostat_database_handler= Temperature_db.getInstance(activity!!)
                                                        val curtain_database_handler= curtain_db.getInstance(activity!!)
                                                        val valve_database_handler= valve_db.getInstance(activity!!)
                                                        val fan_database_handler= fan_db.getInstance(activity!!)
                                                        val plug_database_handler= plug_db.getInstance(activity!!)

                                                        var next =true

                                                        var all_scenario_to_do= ""
                                                        var scenario_done= listOf<Any>()
                                                        var light_scenario_done= ArrayList<String?>()
                                                        var thermostat_scenario_done= ArrayList<String?>()
                                                        var curtain_scenario_done= ArrayList<String?>()
                                                        var valve_scenario_done= ArrayList<String?>()
                                                        var fan_scenario_done= ArrayList<String?>()
                                                        var plug_scenario_done= ArrayList<String?>()
                                                        var music_scenario_done= ArrayList<String?>()


                                                        val scenario_database_handler= com.example.griffinmobile.database.scenario_db.Scenario_db.getInstance(activity!!)
                                                        var light_scenario= emptyList<String>()
                                                        if (selectedItem?.light?.isEmpty() == false) {
                                                            if (all_scenario_to_do!=""){
                                                                all_scenario_to_do+=selectedItem?.light
                                                            }else{
                                                                all_scenario_to_do+=","+selectedItem?.light
                                                            }

                                                            light_scenario= selectedItem?.light!!.split(",")


                                                        }

                                                        var thermostat_scenario=emptyList<String>()
                                                        if (selectedItem?.thermostat?.isEmpty() == false) {
                                                            thermostat_scenario=selectedItem?.thermostat!!.split(",")
                                                            if (all_scenario_to_do!=""){
                                                                all_scenario_to_do+=selectedItem?.thermostat
                                                            }else{
                                                                all_scenario_to_do+=","+selectedItem?.thermostat
                                                            }
                                                        }


                                                        var curtain_scenario= emptyList<String>()
                                                        if (selectedItem?.curtain?.isEmpty() == false) {
                                                            curtain_scenario=selectedItem?.curtain!!.split(",")
                                                            if (all_scenario_to_do!=""){
                                                                all_scenario_to_do+=selectedItem?.curtain
                                                            }else{
                                                                all_scenario_to_do+=","+selectedItem?.curtain
                                                            }
                                                        }


                                                        var valve_scenario= emptyList<String>()
                                                        if (selectedItem?.valve?.isEmpty() == false) {
                                                            valve_scenario=selectedItem?.valve!!.split(",")
                                                            if (all_scenario_to_do!=""){
                                                                all_scenario_to_do+=selectedItem?.valve
                                                            }else{
                                                                all_scenario_to_do+=","+selectedItem?.valve
                                                            }
                                                        }


                                                        var fan_scenario= emptyList<String>()
                                                        if (selectedItem?.fan?.isEmpty() == false) {
                                                            fan_scenario=selectedItem?.fan!!.split(",")
                                                            if (all_scenario_to_do!=""){
                                                                all_scenario_to_do+=selectedItem?.fan
                                                            }else{
                                                                all_scenario_to_do+=","+selectedItem?.fan
                                                            }
                                                        }
                                                        var plug_scenario =  emptyList<String>()
                                                        if (selectedItem?.plug?.isEmpty() == false) {
                                                            plug_scenario=selectedItem?.plug!!.split(",")
                                                            if (all_scenario_to_do!=""){
                                                                all_scenario_to_do+=selectedItem?.plug
                                                            }else{
                                                                all_scenario_to_do+=","+selectedItem?.plug
                                                            }
                                                        }

                                                        var music_scenario =  emptyList<String>()
                                                        if (selectedItem?.music?.isEmpty() == false) {
                                                            music_scenario=selectedItem?.music!!.split(",")
                                                            if (all_scenario_to_do!=""){
                                                                all_scenario_to_do+=selectedItem?.music
                                                            }else{
                                                                all_scenario_to_do+=","+selectedItem?.music
                                                            }
                                                        }


                                                        val all_scenario_count=plug_scenario.count()+fan_scenario.count()+valve_scenario.count()+curtain_scenario.count()+thermostat_scenario.count()+light_scenario.count()+music_scenario.count()




                                                        if (music_scenario.size !=0 ){

                                                            val playlist= mutableListOf<MusicModel>()

                                                            val urls = mutableListOf<String>()
                                                            for (music in music_scenario){
                                                                urls.add(music)

                                                            }
                                                            for ( music in musicList2){
                                                                if (music.audioUrl in urls ){
                                                                    playlist.add(music)


                                                                }else{
                                                                    println(music.audioUrl)
                                                                }
                                                            }

                                                            playlist[0].isplaying="true"
                                                            musicList=playlist

                        //                        println(final_light_to_do)
                                                            println(music_scenario)


                                                            val musicPlayer = Music_player.getInstance(activity!!)
                                                            musicPlayer.playMusic(musicList[0].audioUrl)






                                                            Handler(Looper.getMainLooper()).post {
                        //                                                        loading_view.increaseProgress((taghsim(1.0 ,all_scenario_count.toDouble() ))* 100)
                        //
                                                            }


                                                            music_scenario_done.add("music")


                                                        }






                                                        if (light_scenario.size !=0 ){
                        //                        Handler(Looper.getMainLooper()).post {
                        ////                            loadingIndicator =view.findViewById(R.id.loadingIndicator)
                        //                            loadPercentage.addPercentage(20) // for example
                        //                            updateLoadingIndicator()
                        //                        }

                                                            var current_light_in_scenario= ArrayList<String>()
                                                            for (light in light_scenario){
                                                                current_light_in_scenario.add(light.split("#")[0])


                                                            }
                                                            println(current_light_in_scenario)
                                                            println(light_scenario)
                                                            val same_macs=light_database_handler.getLightsBySameMacForLnames(current_light_in_scenario)
                                                            val final_same_mac= ArrayList<Light>()
                                                            for (light in same_macs){
                                                                final_same_mac.add(light[0])

                                                            }
                        //                        println(same_macs)
                        //                        val refreshed_lights=refresh_light_for_scenario(this,final_same_mac)



                        //                        println("lights refreshed...")

                                                            val current_light_to_change= arrayListOf<Light>()
                                                            for (light in light_scenario){


                                                                val current_light_in_scenario=light.split("#")
                                                                val current_light_in_db=light_database_handler.getLightsByLname(current_light_in_scenario[0])
                                                                current_light_to_change.add(current_light_in_db)


                                                            }
                                                            val sorted_lights = getLightsGroupedBySameMac(current_light_to_change)
                                                            val final_light_to_do= arrayListOf<List<Light>>()
                                                            for (same_mac in sorted_lights){
                                                                val listt = same_mac.map { Light ->
                                                                    val status = light_scenario.find { it.startsWith("${Light.Lname}#") }?.substringAfter("#") ?: ""
                                                                    Light.status = status
                                                                    Light

                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_light_to_do.add(listt)

                                                            }
                        //                        println(final_light_to_do)

                                                            if (next){
                                                                next=false
                                                                for (same in final_light_to_do){

                                                                    try {
//                                                                        println((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)

                                                                        val send=udp_light_scenario(activity!!,same)


                                                                        if (send){
                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }

                                                                            for (light in same){

                                                                                light_scenario_done.add(light.Lname)
                                                                                println(same.count())
                                                                                println(all_scenario_count)


                                                                            }


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }
                                                        if (plug_scenario.size !=0 ){
                        //                        Handler(Looper.getMainLooper()).post {
                        ////                            loadingIndicator =view.findViewById(R.id.loadingIndicator)
                        //                            loadPercentage.addPercentage(20) // for example
                        //                            updateLoadingIndicator()
                        //                        }

                                                            var current_plug_in_scenario= ArrayList<String>()
                                                            for (plug in plug_scenario){
                                                                current_plug_in_scenario.add(plug.split("#")[0])


                                                            }
                                                            println(plug_scenario)
                                                            val same_macs=plug_database_handler.getPlugsBySameMacForPnames(current_plug_in_scenario)
                                                            val final_same_mac= ArrayList<Plug>()
                                                            for (plug in same_macs){
                                                                final_same_mac.add(plug[0])

                                                            }
                        //                        println(same_macs)
                        //                        val refreshed_plugs=refresh_plug_for_scenario(this,final_same_mac)
                        //
                        //
                        //
                        //                        println("plugs refreshed...")

                                                            val current_plug_to_change= arrayListOf<Plug>()
                                                            for (plug in plug_scenario){


                                                                val current_plug_in_scenario=plug.split("#")
                                                                val current_plug_in_db=plug_database_handler.getPlugByCname(current_plug_in_scenario[0])
                                                                if (current_plug_in_db != null) {
                                                                    current_plug_to_change.add(current_plug_in_db)
                                                                }


                                                            }

                                                            val sorted_plugs = getPlugsGroupedBySameMac(current_plug_to_change)


                                                            val final_plug_to_do= arrayListOf<List<Plug>>()
                                                            for (same_mac in sorted_plugs){
                                                                val listt = same_mac.map { Plug ->
                                                                    val status = plug_scenario.find { it.startsWith("${Plug.Pname}#") }?.substringAfter("#") ?: ""
                                                                    Plug.status = status
                                                                    Plug

                                                                }
                                                                println(current_plug_in_scenario)
                                                                println(listt[0].Pname)
                                                                println(listt[0].status)


                                                                final_plug_to_do.add(listt)

                                                            }

                                                            println(sorted_plugs)
                                                            next=true

                                                            if (next){
                                                                next=false
                                                                for (same in final_plug_to_do){

                                                                    try {
//                                                                        println((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)

                                                                        val send=udp_plug_scenario(activity!!,same)


                                                                        if (send){
                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }
                                                                            for (plug in same){
                                                                                plug_scenario_done.add(plug.Pname)
                                                                                println(same.count())
                                                                                println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            }


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }
                                                        if (valve_scenario.size !=0 ){
                        //                        Handler(Looper.getMainLooper()).post {
                        ////                            loadingIndicator =view.findViewById(R.id.loadingIndicator)
                        //                            loadPercentage.addPercentage(20) // for example
                        //                            updateLoadingIndicator()
                        //                        }

                                                            var current_valve_in_scenario= ArrayList<String>()
                                                            for (valve in valve_scenario){
                                                                current_valve_in_scenario.add(valve.split("#")[0])


                                                            }
                                                            println(valve_scenario)
                                                            val same_macs=valve_database_handler.getValvesBySameMacForPnames(current_valve_in_scenario)
                                                            val final_same_mac= ArrayList<valve>()
                                                            for (valve in same_macs){
                                                                final_same_mac.add(valve[0])

                                                            }
                        //                        println(same_macs)

                                                            val current_valve_to_change= arrayListOf<valve>()
                                                            for (valve in valve_scenario){


                                                                println(valve)
                                                                val current_valve_in_scenario=valve.split("#")
                                                                val current_valve_in_db=valve_database_handler.getvalveByCname(current_valve_in_scenario[0])
                                                                if (current_valve_in_db != null) {
                                                                    current_valve_to_change.add(current_valve_in_db)
                                                                }


                                                            }
                                                            val sorted_valve = getvalvesGroupedBySameMac(current_valve_to_change)
                                                            val final_valve_to_do= arrayListOf<List<valve>>()
                                                            for (same_mac in sorted_valve){
                                                                val listt = same_mac.map { valve ->
                                                                    val status = valve_scenario.find { it.startsWith("${valve.Vname}#") }?.substringAfter("#") ?: ""
                                                                    valve.status = status
                                                                    valve

                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_valve_to_do.add(listt)

                                                            }
                                                            next=true

                                                            if (next){
                                                                next=false
                                                                for (same in final_valve_to_do){

                                                                    try {
//                                                                        println((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)

                                                                        val send=udp_valve_for_scenario(activity!!,same)


                                                                        if (send){

                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }
                                                                            for (valve in same){
                                                                                valve_scenario_done.add(valve.Vname)
                                                                                println(same.count())
                                                                                println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            }


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }

                                                        if (curtain_scenario.size !=0 ){
                        //                        Handler(Looper.getMainLooper()).post {
                        ////                            loadingIndicator =view.findViewById(R.id.loadingIndicator)
                        //                            loadPercentage.addPercentage(20) // for example
                        //                            updateLoadingIndicator()
                        //                        }

                                                            var current_curtain_in_scenario= ArrayList<String>()
                                                            for (curtain in curtain_scenario){
                                                                current_curtain_in_scenario.add(curtain.split("#")[0])


                                                            }
                                                            println(curtain_scenario)
                                                            val same_macs=curtain_database_handler.getcurtainsBySameMacForPnames(current_curtain_in_scenario)
                                                            val final_same_mac= ArrayList<curtain>()
                                                            for (curtain in same_macs){
                                                                final_same_mac.add(curtain[0])

                                                            }
                        //                        println(same_macs)
                        //                        val refreshed_curtain= refresh_curtain_for_scenario(this,final_same_mac)
                        //
                        //
                        //
                        //                        println("curtains refreshed...")

                                                            val current_curtain_to_change= arrayListOf<curtain>()
                                                            for (curtain in curtain_scenario){


                                                                println(curtain)
                                                                val current_curtain_in_scenario=curtain.split("#")
                                                                val current_curtain_in_db=curtain_database_handler.getCurtainByCname(current_curtain_in_scenario[0])
                                                                if (current_curtain_in_db != null) {
                                                                    current_curtain_to_change.add(current_curtain_in_db)
                                                                }


                                                            }
                                                            val sorted_curtain = getcurtainGroupedBySameMac(current_curtain_to_change)

                                                            val final_curtain_to_do= arrayListOf<List<curtain>>()
                                                            for (same_mac in sorted_curtain){
                                                                val listt = same_mac.map { curtain ->
                                                                    val status = curtain_scenario.find { it.startsWith("${curtain.Cname}#") }?.substringAfter("#") ?: ""
                                                                    curtain.status = status
                                                                    curtain

                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_curtain_to_do.add(listt)

                                                            }
                                                            next=true

                                                            if (next){
                                                                next=false
                                                                for (same in final_curtain_to_do){

                                                                    try {
                        //                                    println((taghsim(1.0 ,all_scenario_count.toDouble() ))* 100)

                                                                        val send= udp_curtain_for_scenario(activity!!,same[0],same[0].status)


                                                                        if (send){
                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(1.0 ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }

                                                                            curtain_scenario_done.add(same[0].Cname)
                        //                                        println(same.count())
                        //                                        println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }
                                                        if (thermostat_scenario.size !=0 ){
                        //                        Handler(Looper.getMainLooper()).post {
                        ////                            loadingIndicator =view.findViewById(R.id.loadingIndicator)
                        //                            loadPercentage.addPercentage(20) // for example
                        //                            updateLoadingIndicator()
                        //                        }

                                                            var current_Thermostst_in_scenario= ArrayList<String>()
                                                            for (Thermostst in thermostat_scenario){
                                                                current_Thermostst_in_scenario.add(Thermostst.split("#")[0])


                                                            }
                                                            println(thermostat_scenario)
                                                            val same_macs=thermostat_database_handler.getThermoststsBySameMacForPnames(current_Thermostst_in_scenario)
                                                            val final_same_mac= ArrayList<Thermostst>()
                                                            for (Thermostst in same_macs){
                                                                final_same_mac.add(Thermostst[0])

                                                            }
                        //                        println(same_macs)
                        //                        val refreshed_Thermostst= refresh_thermostat_for_scenario(this,final_same_mac)
                        //
                        //
                        //
                        //                        println("Thermoststs refreshed...")

                                                            val current_Thermostst_to_change= arrayListOf<Thermostst>()
                                                            for (Thermostst in thermostat_scenario){


                                                                println(Thermostst)
                                                                val current_Thermostst_in_scenario=Thermostst.split("#")
                                                                val current_Thermostst_in_db=thermostat_database_handler.getThermostatByName(current_Thermostst_in_scenario[0])
                                                                if (current_Thermostst_in_db != null) {
                                                                    current_Thermostst_to_change.add(current_Thermostst_in_db)
                                                                }


                                                            }
                                                            val sorted_Thermostst = arrayListOf<List<Thermostst>>()

                                                            for (light in current_Thermostst_to_change){
                                                                val a= arrayListOf <Thermostst>()
                                                                a.add(light)
                                                                sorted_Thermostst.add(a)

                                                            }
                                                            val final_thermostat_to_do= arrayListOf<List<Thermostst>>()
                                                            for (same_mac in sorted_Thermostst){

                                                                val listt = same_mac.map { Thermostat ->

                                                                    val on_off =( thermostat_scenario.find { it.startsWith("${Thermostat.name}#") }?.substringAfter("#") ?: "").substringBefore("!")
                                                                    val themp = (thermostat_scenario.find { it.startsWith("${Thermostat.name}#") }?.substringAfter("!") ?: "").substringBefore("$")
                                                                    val mod = (thermostat_scenario.find { it.startsWith("${Thermostat.name}#") }?.substringAfter("$") ?: "").substringBefore("@")
                                                                    val fan = thermostat_scenario.find { it.startsWith("${Thermostat.name}#") }?.substringAfter("@") ?: ""
                                                                    Thermostat.mood = mod
                                                                    Thermostat.on_off = on_off
                                                                    Thermostat.fan_status = fan
                                                                    Thermostat.temperature = themp

                                                                    println(Thermostat.on_off.toString() +Thermostat.temperature.toString()+ Thermostat.fan_status.toString()+Thermostat.mood.toString())

                                                                    Thermostat


                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_thermostat_to_do.add(listt)

                                                            }
                        //                        println(sorted_Thermostst)
                                                            next=true

                                                            if (next){
                                                                next=false
                                                                for (same in final_thermostat_to_do){

                                                                    try {
                        //                                    println((taghsim(1.0 ,all_scenario_count.toDouble() ))* 100)

                                                                        val send= udp_thermostat_for_scenario(activity!!,same[0],same[0].mac,same[0].mood,same[0].temperature,same[0].fan_status,same[0].on_off,same[0].ip)


                                                                        if (send){
                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(1.0 ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }


                                                                            thermostat_scenario_done.add(same[0].name)
                        //                                        println(same.count())
                        //                                        println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }

                                                        if (fan_scenario.size !=0 ){
                        //                        Handler(Looper.getMainLooper()).post {
                        ////                            loadingIndicator =view.findViewById(R.id.loadingIndicator)
                        //                            loadPercentage.addPercentage(20) // for example
                        //                            updateLoadingIndicator()
                        //                        }

                                                            var current_fan_in_scenario= ArrayList<String>()
                                                            for (fan in fan_scenario){
                                                                current_fan_in_scenario.add(fan.split("#")[0])


                                                            }
                                                            println(fan_scenario)
                                                            val same_macs=fan_database_handler.getfansBySameMacForLnames(current_fan_in_scenario)
                                                            val final_same_mac= ArrayList<fan>()
                                                            for (fan in same_macs){
                                                                final_same_mac.add(fan[0])

                                                            }
                        //                        println(same_macs)
                        //                        val refreshed_fans= refresh_fan_for_scenario(this,final_same_mac)
                        //
                        //
                        //
                        //                        println("fans refreshed...")

                                                            val current_fan_to_change= arrayListOf<fan>()
                                                            for (fan in fan_scenario){


                                                                val current_fan_in_scenario=fan.split("#")
                                                                val current_fan_in_db=fan_database_handler.getfanByCname(current_fan_in_scenario[0])
                                                                if (current_fan_in_db != null) {
                                                                    current_fan_to_change.add(current_fan_in_db)
                                                                }


                                                            }
                                                            val sorted_fan = getfansGroupedBySameMac(current_fan_to_change)
                                                            val final_fan_to_do= arrayListOf<List<fan>>()
                                                            for (same_mac in sorted_fan){
                                                                val listt = same_mac.map { fan ->
                                                                    val status = fan_scenario.find { it.startsWith("${fan.Fname}#") }?.substringAfter("#") ?: ""
                                                                    fan.status = status
                                                                    fan

                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_fan_to_do.add(listt)

                                                            }
                                                            next=true
                                                            if (next){
                                                                next=false
                                                                for (same in final_fan_to_do){

                                                                    try {
//                                                                        println((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)

                                                                        val send= udp_fan_for_scenario(activity!!,same)


                                                                        if (send){
                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }

                                                                            for (fan in same){
                                                                                fan_scenario_done.add(fan.Fname)
                                                                                println(same.count())
                                                                                println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            }


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }
                        //                    println(light_scenario_done)
                        //                    println(light_scenario)


                                                        if (light_scenario_done.count()< light_scenario.count()){
                                                            println("seciond light scenario check")

                                                            var light_to_do= arrayListOf<String>()
                                                            for (light in light_scenario){
                                                                if (light.split("#")[0] !in light_scenario_done){
                                                                    light_to_do.add(light)
                                                                }
                                                            }
                                                            println(light_to_do)


                                                            var current_light_in_scenario= ArrayList<String>()
                                                            for (light in light_to_do){
                                                                current_light_in_scenario.add(light.split("#")[0])


                                                            }

                                                            val same_macs=light_database_handler.getLightsBySameMacForLnames(current_light_in_scenario)
                                                            val final_same_mac= ArrayList<Light>()
                                                            for (light in same_macs){
                                                                final_same_mac.add(light[0])

                                                            }
                        //                        println(same_macs)


                                                            val current_light_to_change1= arrayListOf<Light>()

                                                            for (light in light_to_do){

                                                                val current_light_in_scenario=light.split("#")
                                                                val current_light_in_db=light_database_handler.getLightsByLname(current_light_in_scenario[0])
                                                                current_light_to_change1.add(current_light_in_db)

                                                            }
                                                            val sorted_lights = getLightsGroupedBySameMac(current_light_to_change1)

                                                            val final_light_to_do= arrayListOf<List<Light>>()
                                                            for (same_mac in sorted_lights){
                                                                val listt = same_mac.map { Light ->
                                                                    val status = light_to_do.find { it.startsWith("${Light.Lname}#") }?.substringAfter("#") ?: ""
                                                                    Light.status = status
                                                                    Light

                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_light_to_do.add(listt)

                                                            }
                                                            next=true
                                                            if (next){
                                                                next=false
                                                                for (same in final_light_to_do){

                                                                    try {
//                                                                        println((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)

                                                                        val send=udp_light_scenario(activity!!,same)


                                                                        if (send){
                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }

                                                                            for (light in same){

                                                                                light_scenario_done.add(light.Lname)
                                                                                println(same.count())
                                                                                println(all_scenario_count)

                        //


                                                                            }


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }
                                                        if (thermostat_scenario_done.count()< thermostat_scenario.count()){
                                                            println("thermostat second")
                                                            var thermostat_to_do= arrayListOf<String>()
                                                            for (thermostat in thermostat_scenario){
                                                                if (thermostat.split("#")[0] !in thermostat_scenario_done){
                                                                    thermostat_to_do.add(thermostat)
                                                                }
                                                            }

                                                            var current_Thermostst_in_scenario= ArrayList<String>()
                                                            for (Thermostst in thermostat_to_do){
                                                                current_Thermostst_in_scenario.add(Thermostst.split("#")[0])


                                                            }
                                                            println(thermostat_to_do)
                                                            val same_macs=thermostat_database_handler.getThermoststsBySameMacForPnames(current_Thermostst_in_scenario)
                                                            val final_same_mac= ArrayList<Thermostst>()
                                                            for (Thermostst in same_macs){
                                                                final_same_mac.add(Thermostst[0])

                                                            }
                        //                        println(same_macs)
                        //                        val refreshed_Thermostst= refresh_thermostat_for_scenario(this,final_same_mac)
                        //
                        //
                        //
                        //                        println("Thermoststs refreshed...")

                                                            val current_Thermostst_to_change= arrayListOf<Thermostst>()
                                                            for (Thermostst in thermostat_to_do){


                                                                println(Thermostst)
                                                                val current_Thermostst_in_scenario=
                                                                    Thermostst.split("#")
                                                                val current_Thermostst_in_db=thermostat_database_handler.getThermostatByName(current_Thermostst_in_scenario[0])
                                                                if (current_Thermostst_in_db != null) {
                                                                    current_Thermostst_to_change.add(current_Thermostst_in_db)
                                                                }


                                                            }
                                                            val sorted_Thermostst = arrayListOf<List<Thermostst>>()

                                                            for (light in current_Thermostst_to_change){
                                                                val a= arrayListOf <Thermostst>()
                                                                a.add(light)
                                                                sorted_Thermostst.add(a)

                                                            }
                                                            val final_thermostat_to_do= arrayListOf<List<Thermostst>>()
                                                            for (same_mac in sorted_Thermostst){

                                                                val listt = same_mac.map { Thermostat ->

                                                                    val on_off =( thermostat_to_do.find { it.startsWith("${Thermostat.name}#") }?.substringAfter("#") ?: "").substringBefore("!")
                                                                    val themp = (thermostat_to_do.find { it.startsWith("${Thermostat.name}#") }?.substringAfter("!") ?: "").substringBefore("$")
                                                                    val mod = (thermostat_to_do.find { it.startsWith("${Thermostat.name}#") }?.substringAfter("$") ?: "").substringBefore("@")
                                                                    val fan = thermostat_to_do.find { it.startsWith("${Thermostat.name}#") }?.substringAfter("@") ?: ""
                                                                    Thermostat.mood = mod
                                                                    Thermostat.on_off = on_off
                                                                    Thermostat.fan_status = fan
                                                                    Thermostat.temperature = themp

                                                                    println(Thermostat.on_off.toString() +Thermostat.temperature.toString()+ Thermostat.fan_status.toString()+Thermostat.mood.toString())

                                                                    Thermostat


                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_thermostat_to_do.add(listt)

                                                            }
                        //                        println(sorted_Thermostst)
                                                            next=true

                                                            if (next){
                                                                next=false
                                                                for (same in final_thermostat_to_do){

                                                                    try {
                        //                                    println((taghsim(1.0 ,all_scenario_count.toDouble() ))* 100)


                                                                        val send= udp_thermostat_for_scenario(activity!!,same[0],same[0].mac,same[0].mood,same[0].temperature,same[0].fan_status,same[0].on_off,same[0].ip)


                                                                        if (send){
                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(1.0 ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }


                                                                            thermostat_scenario_done.add(same[0].name)
                        //                                        println(same.count())
                        //                                        println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }
                                                        if (curtain_scenario_done.count()< curtain_scenario.count()){
                                                            println("curtain second")
                                                            var curtain_to_do= arrayListOf<String>()
                                                            for (curtain in curtain_scenario){
                                                                if (curtain.split("#")[0]  !in curtain_scenario_done){
                                                                    curtain_to_do.add(curtain)
                                                                }
                                                            }


                                                            var current_curtain_in_scenario = ArrayList<String>()
                                                            for (curtain in curtain_to_do){
                                                                current_curtain_in_scenario.add(curtain.split("#")[0])


                                                            }
                                                            println(curtain_to_do)
                                                            val same_macs = curtain_database_handler.getcurtainsBySameMacForPnames(current_curtain_in_scenario)
                                                            val final_same_mac = ArrayList<curtain>()
                                                            for (curtain in same_macs){
                                                                final_same_mac.add(curtain[0])

                                                            }
                        //                        println(same_macs)
                        //                        val refreshed_curtain= refresh_curtain_for_scenario(this,final_same_mac)
                        //
                        //
                        //
                        //                        println("curtains refreshed...")

                                                            val current_curtain_to_change = arrayListOf<curtain>()
                                                            for (curtain in curtain_to_do){


                                                                println(curtain)
                                                                val current_curtain_in_scenario = curtain.split("#")
                                                                val current_curtain_in_db =
                                                                    curtain_database_handler.getCurtainByCname(current_curtain_in_scenario[0])
                                                                if (current_curtain_in_db != null) {
                                                                    current_curtain_to_change.add(current_curtain_in_db)
                                                                }


                                                            }

                                                            val sorted_curtain = getcurtainGroupedBySameMac(current_curtain_to_change)

                                                            val final_curtain_to_do = arrayListOf<List<curtain>>()
                                                            for (same_mac in sorted_curtain){
                                                                val listt = same_mac.map { curtain ->
                                                                    val status =
                                                                        curtain_to_do.find { it.startsWith("${curtain.Cname}#") }?.substringAfter("#") ?: ""
                                                                    curtain.status = status
                                                                    curtain

                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_curtain_to_do.add(listt)

                                                            }
                                                            next=true

                                                            if (next){
                                                                next = false
                                                                for (same in final_curtain_to_do) {

                                                                    try {
                        //                                    println((taghsim(1.0 ,all_scenario_count.toDouble() ))* 100)

                                                                        val send = udp_curtain_for_scenario(activity!!, same[0], same[0].status)


                                                                        if (send) {
                        //                                                                    Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress(
                        //                                                                            (taghsim(
                        //                                                                                1.0,
                        //                                                                                all_scenario_count.toDouble()
                        //                                                                            )) * 100
                        //                                                                        )
                        //
                        //                                                                    }

                                                                            curtain_scenario_done.add(same[0].Cname)
                        //                                        println(same.count())
                        //                                        println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            next = true
                                                                        }
                                                                    } catch (e: Exception) {
                                                                        next = true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }

                                                        if (plug_scenario_done.count()< plug_scenario.count()){
                                                            println("plug second")
                                                            var plug_to_do= arrayListOf<String>()
                                                            for (plug in plug_scenario){
                                                                if (plug.split("#")[0] !in plug_scenario_done){
                                                                    plug_to_do.add(plug)
                                                                }
                                                            }


                                                            var current_plug_in_scenario= ArrayList<String>()
                                                            for (plug in plug_to_do){
                                                                current_plug_in_scenario.add(plug.split("#")[0])


                                                            }
                                                            println(plug_scenario)
                                                            val same_macs=plug_database_handler.getPlugsBySameMacForPnames(current_plug_in_scenario)
                                                            val final_same_mac= ArrayList<Plug>()
                                                            for (plug in same_macs){
                                                                final_same_mac.add(plug[0])

                                                            }
                        //                        println(same_macs)
                        //                        val refreshed_plugs=refresh_plug_for_scenario(this,final_same_mac)
                        //
                        //
                        //
                        //                        println("plugs refreshed...")

                                                            val current_plug_to_change= arrayListOf<Plug>()
                                                            for (plug in plug_to_do){


                                                                val current_plug_in_scenario=plug.split("#")
                                                                val current_plug_in_db=plug_database_handler.getPlugByCname(current_plug_in_scenario[0])
                                                                if (current_plug_in_db != null) {
                                                                    current_plug_to_change.add(current_plug_in_db)
                                                                }


                                                            }

                                                            val sorted_plugs = getPlugsGroupedBySameMac(current_plug_to_change)


                                                            val final_plug_to_do= arrayListOf<List<Plug>>()
                                                            for (same_mac in sorted_plugs){
                                                                val listt = same_mac.map { Plug ->
                                                                    val status = plug_to_do.find { it.startsWith("${Plug.Pname}#") }?.substringAfter("#") ?: ""
                                                                    Plug.status = status
                                                                    Plug

                                                                }
                                                                println(current_plug_in_scenario)
                                                                println(listt[0].Pname)
                                                                println(listt[0].status)


                                                                final_plug_to_do.add(listt)

                                                            }

                                                            println(sorted_plugs)
                                                            next=true

                                                            if (next){
                                                                next=false
                                                                for (same in final_plug_to_do){

                                                                    try {
//                                                                        println((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)

                                                                        val send=udp_plug_scenario(activity!!,same)


                                                                        if (send){
                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }
                                                                            for (plug in same){
                                                                                plug_scenario_done.add(plug.Pname)
                                                                                println(same.count())
                                                                                println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            }


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }
                                                        if (valve_scenario_done.count()< valve_scenario.count()){
                                                            println("valve second")
                                                            var valve_to_do= arrayListOf<String>()
                                                            for (valve in valve_scenario){
                                                                if (valve.split("#")[0]  !in valve_scenario_done){
                                                                    valve_to_do.add(valve)
                                                                }
                                                            }
                                                            var current_valve_in_scenario= ArrayList<String>()
                                                            for (valve in valve_to_do){
                                                                current_valve_in_scenario.add(valve.split("#")[0])


                                                            }
                                                            println(valve_to_do)
                                                            val same_macs=valve_database_handler.getValvesBySameMacForPnames(current_valve_in_scenario)
                                                            val final_same_mac= ArrayList<valve>()
                                                            for (valve in same_macs){
                                                                final_same_mac.add(valve[0])

                                                            }
                        //                        println(same_macs)

                                                            val current_valve_to_change= arrayListOf<valve>()
                                                            for (valve in valve_to_do){


                                                                println(valve)
                                                                val current_valve_in_scenario= valve.split("#")
                                                                val current_valve_in_db=valve_database_handler.getvalveByCname(current_valve_in_scenario[0])
                                                                if (current_valve_in_db != null) {
                                                                    current_valve_to_change.add(current_valve_in_db)
                                                                }


                                                            }
                                                            val sorted_valve = getvalvesGroupedBySameMac(current_valve_to_change)
                                                            val final_valve_to_do= arrayListOf<List<valve>>()
                                                            for (same_mac in sorted_valve){
                                                                val listt = same_mac.map { valve ->
                                                                    val status = valve_to_do.find { it.startsWith("${valve.Vname}#") }?.substringAfter("#") ?: ""
                                                                    valve.status = status
                                                                    valve

                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_valve_to_do.add(listt)

                                                            }
                                                            next=true

                                                            if (next){
                                                                next=false
                                                                for (same in final_valve_to_do){

                                                                    try {
//                                                                        println((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)

                                                                        val send= udp_valve_for_scenario(activity!!,same)


                                                                        if (send){

                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }
                                                                            for (valve in same){
                                                                                valve_scenario_done.add(valve.Vname)
                                                                                println(same.count())
                                                                                println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            }


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }
                                                        if (fan_scenario_done.count()< fan_scenario.count()){
                                                            println("fan second")
                                                            var fan_to_do= arrayListOf<String>()
                                                            for (fan in fan_scenario){
                                                                if (fan.split("#")[0]  !in fan_scenario_done){
                                                                    fan_to_do.add(fan)
                                                                }
                                                            }
                                                            var current_fan_in_scenario= ArrayList<String>()
                                                            for (fan in fan_to_do){
                                                                current_fan_in_scenario.add(fan.split("#")[0])


                                                            }
                                                            println(fan_to_do)
                                                            val same_macs=fan_database_handler.getfansBySameMacForLnames(current_fan_in_scenario)
                                                            val final_same_mac= ArrayList<fan>()
                                                            for (fan in same_macs){
                                                                final_same_mac.add(fan[0])

                                                            }
                        //                        println(same_macs)
                        //                        val refreshed_fans= refresh_fan_for_scenario(this,final_same_mac)
                        //
                        //
                        //
                        //                        println("fans refreshed...")

                                                            val current_fan_to_change= arrayListOf<fan>()
                                                            for (fan in fan_to_do){


                                                                val current_fan_in_scenario= fan.split("#")
                                                                val current_fan_in_db=fan_database_handler.getfanByCname(current_fan_in_scenario[0])
                                                                if (current_fan_in_db != null) {
                                                                    current_fan_to_change.add(current_fan_in_db)
                                                                }


                                                            }
                                                            val sorted_fan = getfansGroupedBySameMac(current_fan_to_change)
                                                            val final_fan_to_do= arrayListOf<List<fan>>()
                                                            for (same_mac in sorted_fan){
                                                                val listt = same_mac.map { fan ->
                                                                    val status = fan_to_do.find { it.startsWith("${fan.Fname}#") }?.substringAfter("#") ?: ""
                                                                    fan.status = status
                                                                    fan

                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_fan_to_do.add(listt)

                                                            }
                                                            next=true
                                                            if (next){
                                                                next=false
                                                                for (same in final_fan_to_do){

                                                                    try {
//                                                                        println((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)

                                                                        val send= udp_fan_for_scenario(activity!!,same)


                                                                        if (send){
                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }

                                                                            for (fan in same){
                                                                                fan_scenario_done.add(fan.Fname)
                                                                                println(same.count())
                                                                                println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            }


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }







                                                        activity!!.runOnUiThread {
                                                            sharedViewModel.update_is_doing("not bussy")
                                                        }


                                                        var fan_to_do = arrayListOf<String>()
                                                        var valve_to_do= arrayListOf<String>()
                                                        var plug_to_do= arrayListOf<String>()
                                                        var curtain_to_do = arrayListOf<String>()
                                                        var thermostat_to_do= arrayListOf<String>()
                                                        var light_to_do= arrayListOf<String>()


                                                        if (fan_scenario_done.count()< fan_scenario.count()) {


                                                            for (fan in fan_scenario) {
                                                                if (fan.split("#")[0] !in fan_scenario_done) {
                                                                    fan_to_do.add(fan)
                                                                }
                                                            }

                                                        }
                                                        if (valve_scenario_done.count()< valve_scenario.count()) {


                                                            for (valve in valve_scenario) {
                                                                if (valve.split("#")[0] !in valve_scenario_done) {
                                                                    valve_to_do.add(valve)
                                                                }
                                                            }


                                                        }
                                                        if (plug_scenario_done.count()< plug_scenario.count()) {


                                                            for (plug in plug_scenario) {
                                                                if (plug.split("#")[0] !in plug_scenario_done) {
                                                                    plug_to_do.add(plug)
                                                                }
                                                            }
                                                        }

                                                        if (curtain_scenario_done.count()< curtain_scenario.count()) {


                                                            for (curtain in curtain_scenario) {
                                                                if (curtain.split("#")[0] !in curtain_scenario_done) {
                                                                    curtain_to_do.add(curtain)
                                                                }
                                                            }
                                                        }

                                                        if (thermostat_scenario_done.count()< thermostat_scenario.count()) {


                                                            for (thermostat in thermostat_scenario) {
                                                                if (thermostat.split("#")[0] !in thermostat_scenario_done) {
                                                                    thermostat_to_do.add(thermostat)
                                                                }
                                                            }
                                                        }
                                                        if (light_scenario_done.count()< light_scenario.count()) {


                                                            for (light in light_scenario) {
                                                                if (light.split("#")[0] !in light_scenario_done) {
                                                                    light_to_do.add(light)
                                                                }
                                                            }
                                                        }
                                                        activity!!.runOnUiThread{
                        //                                                    popupWindow.dismiss()
                                                        }

                                                        println("done")
                                                        println(all_scenario_count)
                                                        println(fan_to_do.count()+valve_to_do.count()+plug_to_do.count()+curtain_to_do.count()+thermostat_to_do.count()+light_to_do.count())
                                                        if (0==fan_to_do.count()+valve_to_do.count()+plug_to_do.count()+curtain_to_do.count()+thermostat_to_do.count()+light_to_do.count()){
                                                            activity!!.runOnUiThread{
                                                                Toast.makeText(activity, "Scenario Done", Toast.LENGTH_SHORT).show()
                                                            }


                                                        }else if (all_scenario_count>fan_to_do.count()+valve_to_do.count()+plug_to_do.count()+curtain_to_do.count()+thermostat_to_do.count()+light_to_do.count()){
                                                            val final_list_to_show= arrayListOf<String>()
                                                            println(fan_to_do)
                                                            println(curtain_to_do)
                                                            println(thermostat_to_do)
                                                            println(light_to_do)

                                                            if (music_scenario_done.count() < music_scenario.count()){
                                                                final_list_to_show.add("Music")

                                                            }


                                                            if (fan_to_do.count()>0){
                                                                for (item in fan_to_do){
                                                                    final_list_to_show.add("${item.split("#")[0]}  (Fan)" )
                                                                }


                                                            }
                                                            if (valve_to_do.count()>0){
                                                                for (item in valve_to_do){
                                                                    final_list_to_show.add("${item.split("#")[0]}  (Valve)" )
                                                                }


                                                            }else if (plug_to_do.count()>0){
                                                                for (item in plug_to_do){
                                                                    final_list_to_show.add("${item.split("#")[0]}  (Plug)" )
                                                                }


                                                            }
                                                            if (curtain_to_do.count()>0){
                                                                for (item in curtain_to_do){
                                                                    final_list_to_show.add("${item.split("#")[0]}  (Curtain)" )
                                                                }


                                                            }
                                                            if (thermostat_to_do.count()>0){
                                                                for (item in thermostat_to_do){
                                                                    final_list_to_show.add("${item.split("#")[0]}  (Thermostat)" )
                                                                }


                                                            }
                                                            if (light_to_do.count()>0){
                                                                for (item in light_to_do){
                                                                    final_list_to_show.add("${item.split("#")[0]}  (Light)" )
                                                                }


                                                            }
                                                            activity!!.runOnUiThread {

                        //                                                        popupWindow.dismiss()
                                                            }
                                                            println(final_list_to_show)


                                                        }


                                                    }.start()


                                                }else{

                                                    Toast.makeText(activity, "please Wait...", Toast.LENGTH_SHORT).show()
                                                }


                                            }


                                        }catch (e:Exception){
                                            println(e)
                                            UdpListener8090.running = true
                                            UdpListener8090.paused =false
                                        }


                                    }
                                    UdpListener8090.running = true
                                    UdpListener8090.paused =false

                                }else if(receivedMessage.split("~>")[0]=="rnsc"){

                                    if (true ){


                                        val target_scenario=
                                            scenario_db.Scenario_db.getInstance(activity!!).getScenarioById(receivedMessage.split("~>")[2].toInt())

                                        try {
                                            val status = sharedViewModel.is_doing.value

                                            println(status)
                                            var scenario_side="user"

                                            val selectedItem = target_scenario

                                            if (scenario_side=="user"){

                                                println("userrrr")




                                                if ((status != "bussy") || (status==null)){
                                                    if (selectedItem != null) {
                                                        activity!!.runOnUiThread {

                                                            Toast.makeText(activity, selectedItem.scenario_name, Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                        //                                            popupWindow.showAtLocation(requireViewById(R.layout), Gravity.CENTER, 0, 0)


                                                    Thread{
                                                        activity!!.runOnUiThread {
                                                            sharedViewModel.update_is_doing("bussy")
                                                        }

                        //                                                loading_view.increaseProgress(0.0)

                                                        val light_database_handler=light_db.getInstance(activity!!)
                                                        val thermostat_database_handler= Temperature_db.getInstance(activity!!)
                                                        val curtain_database_handler= curtain_db.getInstance(activity!!)
                                                        val valve_database_handler= valve_db.getInstance(activity!!)
                                                        val fan_database_handler= fan_db.getInstance(activity!!)
                                                        val plug_database_handler= plug_db.getInstance(activity!!)

                                                        var next =true

                                                        var all_scenario_to_do= ""
                                                        var scenario_done= listOf<Any>()
                                                        var light_scenario_done= ArrayList<String?>()
                                                        var thermostat_scenario_done= ArrayList<String?>()
                                                        var curtain_scenario_done= ArrayList<String?>()
                                                        var valve_scenario_done= ArrayList<String?>()
                                                        var fan_scenario_done= ArrayList<String?>()
                                                        var plug_scenario_done= ArrayList<String?>()
                                                        var music_scenario_done= ArrayList<String?>()


                                                        val scenario_database_handler= com.example.griffinmobile.database.scenario_db.Scenario_db.getInstance(activity!!)
                                                        var light_scenario= emptyList<String>()
                                                        if (selectedItem?.light?.isEmpty() == false) {
                                                            if (all_scenario_to_do!=""){
                                                                all_scenario_to_do+=selectedItem?.light
                                                            }else{
                                                                all_scenario_to_do+=","+selectedItem?.light
                                                            }

                                                            light_scenario= selectedItem?.light!!.split(",")


                                                        }

                                                        var thermostat_scenario=emptyList<String>()
                                                        if (selectedItem?.thermostat?.isEmpty() == false) {
                                                            thermostat_scenario=selectedItem?.thermostat!!.split(",")
                                                            if (all_scenario_to_do!=""){
                                                                all_scenario_to_do+=selectedItem?.thermostat
                                                            }else{
                                                                all_scenario_to_do+=","+selectedItem?.thermostat
                                                            }
                                                        }


                                                        var curtain_scenario= emptyList<String>()
                                                        if (selectedItem?.curtain?.isEmpty() == false) {
                                                            curtain_scenario=selectedItem?.curtain!!.split(",")
                                                            if (all_scenario_to_do!=""){
                                                                all_scenario_to_do+=selectedItem?.curtain
                                                            }else{
                                                                all_scenario_to_do+=","+selectedItem?.curtain
                                                            }
                                                        }


                                                        var valve_scenario= emptyList<String>()
                                                        if (selectedItem?.valve?.isEmpty() == false) {
                                                            valve_scenario=selectedItem?.valve!!.split(",")
                                                            if (all_scenario_to_do!=""){
                                                                all_scenario_to_do+=selectedItem?.valve
                                                            }else{
                                                                all_scenario_to_do+=","+selectedItem?.valve
                                                            }
                                                        }


                                                        var fan_scenario= emptyList<String>()
                                                        if (selectedItem?.fan?.isEmpty() == false) {
                                                            fan_scenario=selectedItem?.fan!!.split(",")
                                                            if (all_scenario_to_do!=""){
                                                                all_scenario_to_do+=selectedItem?.fan
                                                            }else{
                                                                all_scenario_to_do+=","+selectedItem?.fan
                                                            }
                                                        }
                                                        var plug_scenario =  emptyList<String>()
                                                        if (selectedItem?.plug?.isEmpty() == false) {
                                                            plug_scenario=selectedItem?.plug!!.split(",")
                                                            if (all_scenario_to_do!=""){
                                                                all_scenario_to_do+=selectedItem?.plug
                                                            }else{
                                                                all_scenario_to_do+=","+selectedItem?.plug
                                                            }
                                                        }

                                                        var music_scenario =  emptyList<String>()
                                                        if (selectedItem?.music?.isEmpty() == false) {
                                                            music_scenario=selectedItem?.music!!.split(",")
                                                            if (all_scenario_to_do!=""){
                                                                all_scenario_to_do+=selectedItem?.music
                                                            }else{
                                                                all_scenario_to_do+=","+selectedItem?.music
                                                            }
                                                        }


                                                        val all_scenario_count=plug_scenario.count()+fan_scenario.count()+valve_scenario.count()+curtain_scenario.count()+thermostat_scenario.count()+light_scenario.count()+music_scenario.count()




                                                        if (music_scenario.size !=0 ){

                                                            val playlist= mutableListOf<MusicModel>()

                                                            val urls = mutableListOf<String>()
                                                            for (music in music_scenario){
                                                                urls.add(music)

                                                            }
                                                            for ( music in musicList2){
                                                                if (music.audioUrl in urls ){
                                                                    playlist.add(music)


                                                                }else{
                                                                    println(music.audioUrl)
                                                                }
                                                            }

                                                            playlist[0].isplaying="true"
                                                            musicList=playlist

                        //                        println(final_light_to_do)
                                                            println(music_scenario)


                                                            val musicPlayer = Music_player.getInstance(activity!!)
                                                            musicPlayer.playMusic(musicList[0].audioUrl)






                                                            Handler(Looper.getMainLooper()).post {
                        //                                                        loading_view.increaseProgress((taghsim(1.0 ,all_scenario_count.toDouble() ))* 100)
                        //
                                                            }


                                                            music_scenario_done.add("music")


                                                        }






                                                        if (light_scenario.size !=0 ){
                        //                        Handler(Looper.getMainLooper()).post {
                        ////                            loadingIndicator =view.findViewById(R.id.loadingIndicator)
                        //                            loadPercentage.addPercentage(20) // for example
                        //                            updateLoadingIndicator()
                        //                        }

                                                            var current_light_in_scenario= ArrayList<String>()
                                                            for (light in light_scenario){
                                                                current_light_in_scenario.add(light.split("#")[0])


                                                            }
                                                            println(current_light_in_scenario)
                                                            println(light_scenario)
                                                            val same_macs=light_database_handler.getLightsBySameMacForLnames(current_light_in_scenario)
                                                            val final_same_mac= ArrayList<Light>()
                                                            for (light in same_macs){
                                                                final_same_mac.add(light[0])

                                                            }
                        //                        println(same_macs)
                        //                        val refreshed_lights=refresh_light_for_scenario(this,final_same_mac)



                        //                        println("lights refreshed...")

                                                            val current_light_to_change= arrayListOf<Light>()
                                                            for (light in light_scenario){


                                                                val current_light_in_scenario=light.split("#")
                                                                val current_light_in_db=light_database_handler.getLightsByLname(current_light_in_scenario[0])
                                                                current_light_to_change.add(current_light_in_db)


                                                            }
                                                            val sorted_lights = getLightsGroupedBySameMac(current_light_to_change)
                                                            val final_light_to_do= arrayListOf<List<Light>>()
                                                            for (same_mac in sorted_lights){
                                                                val listt = same_mac.map { Light ->
                                                                    val status = light_scenario.find { it.startsWith("${Light.Lname}#") }?.substringAfter("#") ?: ""
                                                                    Light.status = status
                                                                    Light

                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_light_to_do.add(listt)

                                                            }
                        //                        println(final_light_to_do)

                                                            if (next){
                                                                next=false
                                                                for (same in final_light_to_do){

                                                                    try {
//                                                                        println((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)

                                                                        val send=udp_light_scenario(activity!!,same)


                                                                        if (send){
                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }

                                                                            for (light in same){

                                                                                light_scenario_done.add(light.Lname)
                                                                                println(same.count())
                                                                                println(all_scenario_count)


                                                                            }


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }
                                                        if (plug_scenario.size !=0 ){
                        //                        Handler(Looper.getMainLooper()).post {
                        ////                            loadingIndicator =view.findViewById(R.id.loadingIndicator)
                        //                            loadPercentage.addPercentage(20) // for example
                        //                            updateLoadingIndicator()
                        //                        }

                                                            var current_plug_in_scenario= ArrayList<String>()
                                                            for (plug in plug_scenario){
                                                                current_plug_in_scenario.add(plug.split("#")[0])


                                                            }
                                                            println(plug_scenario)
                                                            val same_macs=plug_database_handler.getPlugsBySameMacForPnames(current_plug_in_scenario)
                                                            val final_same_mac= ArrayList<Plug>()
                                                            for (plug in same_macs){
                                                                final_same_mac.add(plug[0])

                                                            }
                        //                        println(same_macs)
                        //                        val refreshed_plugs=refresh_plug_for_scenario(this,final_same_mac)
                        //
                        //
                        //
                        //                        println("plugs refreshed...")

                                                            val current_plug_to_change= arrayListOf<Plug>()
                                                            for (plug in plug_scenario){


                                                                val current_plug_in_scenario=plug.split("#")
                                                                val current_plug_in_db=plug_database_handler.getPlugByCname(current_plug_in_scenario[0])
                                                                if (current_plug_in_db != null) {
                                                                    current_plug_to_change.add(current_plug_in_db)
                                                                }


                                                            }

                                                            val sorted_plugs = getPlugsGroupedBySameMac(current_plug_to_change)


                                                            val final_plug_to_do= arrayListOf<List<Plug>>()
                                                            for (same_mac in sorted_plugs){
                                                                val listt = same_mac.map { Plug ->
                                                                    val status = plug_scenario.find { it.startsWith("${Plug.Pname}#") }?.substringAfter("#") ?: ""
                                                                    Plug.status = status
                                                                    Plug

                                                                }
                                                                println(current_plug_in_scenario)
                                                                println(listt[0].Pname)
                                                                println(listt[0].status)


                                                                final_plug_to_do.add(listt)

                                                            }

                                                            println(sorted_plugs)
                                                            next=true

                                                            if (next){
                                                                next=false
                                                                for (same in final_plug_to_do){

                                                                    try {
//                                                                        println((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)

                                                                        val send=udp_plug_scenario(activity!!,same)


                                                                        if (send){
                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }
                                                                            for (plug in same){
                                                                                plug_scenario_done.add(plug.Pname)
                                                                                println(same.count())
                                                                                println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            }


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }
                                                        if (valve_scenario.size !=0 ){
                        //                        Handler(Looper.getMainLooper()).post {
                        ////                            loadingIndicator =view.findViewById(R.id.loadingIndicator)
                        //                            loadPercentage.addPercentage(20) // for example
                        //                            updateLoadingIndicator()
                        //                        }

                                                            var current_valve_in_scenario= ArrayList<String>()
                                                            for (valve in valve_scenario){
                                                                current_valve_in_scenario.add(valve.split("#")[0])


                                                            }
                                                            println(valve_scenario)
                                                            val same_macs=valve_database_handler.getValvesBySameMacForPnames(current_valve_in_scenario)
                                                            val final_same_mac= ArrayList<valve>()
                                                            for (valve in same_macs){
                                                                final_same_mac.add(valve[0])

                                                            }
                        //                        println(same_macs)

                                                            val current_valve_to_change= arrayListOf<valve>()
                                                            for (valve in valve_scenario){


                                                                println(valve)
                                                                val current_valve_in_scenario=valve.split("#")
                                                                val current_valve_in_db=valve_database_handler.getvalveByCname(current_valve_in_scenario[0])
                                                                if (current_valve_in_db != null) {
                                                                    current_valve_to_change.add(current_valve_in_db)
                                                                }


                                                            }
                                                            val sorted_valve = getvalvesGroupedBySameMac(current_valve_to_change)
                                                            val final_valve_to_do= arrayListOf<List<valve>>()
                                                            for (same_mac in sorted_valve){
                                                                val listt = same_mac.map { valve ->
                                                                    val status = valve_scenario.find { it.startsWith("${valve.Vname}#") }?.substringAfter("#") ?: ""
                                                                    valve.status = status
                                                                    valve

                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_valve_to_do.add(listt)

                                                            }
                                                            next=true

                                                            if (next){
                                                                next=false
                                                                for (same in final_valve_to_do){

                                                                    try {
//                                                                        println((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)

                                                                        val send=udp_valve_for_scenario(activity!!,same)


                                                                        if (send){

                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }
                                                                            for (valve in same){
                                                                                valve_scenario_done.add(valve.Vname)
                                                                                println(same.count())
                                                                                println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            }


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }

                                                        if (curtain_scenario.size !=0 ){
                        //                        Handler(Looper.getMainLooper()).post {
                        ////                            loadingIndicator =view.findViewById(R.id.loadingIndicator)
                        //                            loadPercentage.addPercentage(20) // for example
                        //                            updateLoadingIndicator()
                        //                        }

                                                            var current_curtain_in_scenario= ArrayList<String>()
                                                            for (curtain in curtain_scenario){
                                                                current_curtain_in_scenario.add(curtain.split("#")[0])


                                                            }
                                                            println(curtain_scenario)
                                                            val same_macs=curtain_database_handler.getcurtainsBySameMacForPnames(current_curtain_in_scenario)
                                                            val final_same_mac= ArrayList<curtain>()
                                                            for (curtain in same_macs){
                                                                final_same_mac.add(curtain[0])

                                                            }
                        //                        println(same_macs)
                        //                        val refreshed_curtain= refresh_curtain_for_scenario(this,final_same_mac)
                        //
                        //
                        //
                        //                        println("curtains refreshed...")

                                                            val current_curtain_to_change= arrayListOf<curtain>()
                                                            for (curtain in curtain_scenario){


                                                                println(curtain)
                                                                val current_curtain_in_scenario=curtain.split("#")
                                                                val current_curtain_in_db=curtain_database_handler.getCurtainByCname(current_curtain_in_scenario[0])
                                                                if (current_curtain_in_db != null) {
                                                                    current_curtain_to_change.add(current_curtain_in_db)
                                                                }


                                                            }
                                                            val sorted_curtain = getcurtainGroupedBySameMac(current_curtain_to_change)

                                                            val final_curtain_to_do= arrayListOf<List<curtain>>()
                                                            for (same_mac in sorted_curtain){
                                                                val listt = same_mac.map { curtain ->
                                                                    val status = curtain_scenario.find { it.startsWith("${curtain.Cname}#") }?.substringAfter("#") ?: ""
                                                                    curtain.status = status
                                                                    curtain

                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_curtain_to_do.add(listt)

                                                            }
                                                            next=true

                                                            if (next){
                                                                next=false
                                                                for (same in final_curtain_to_do){

                                                                    try {
                        //                                    println((taghsim(1.0 ,all_scenario_count.toDouble() ))* 100)

                                                                        val send= udp_curtain_for_scenario(activity!!,same[0],same[0].status)


                                                                        if (send){
                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(1.0 ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }

                                                                            curtain_scenario_done.add(same[0].Cname)
                        //                                        println(same.count())
                        //                                        println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }
                                                        if (thermostat_scenario.size !=0 ){
                        //                        Handler(Looper.getMainLooper()).post {
                        ////                            loadingIndicator =view.findViewById(R.id.loadingIndicator)
                        //                            loadPercentage.addPercentage(20) // for example
                        //                            updateLoadingIndicator()
                        //                        }

                                                            var current_Thermostst_in_scenario= ArrayList<String>()
                                                            for (Thermostst in thermostat_scenario){
                                                                current_Thermostst_in_scenario.add(Thermostst.split("#")[0])


                                                            }
                                                            println(thermostat_scenario)
                                                            val same_macs=thermostat_database_handler.getThermoststsBySameMacForPnames(current_Thermostst_in_scenario)
                                                            val final_same_mac= ArrayList<Thermostst>()
                                                            for (Thermostst in same_macs){
                                                                final_same_mac.add(Thermostst[0])

                                                            }
                        //                        println(same_macs)
                        //                        val refreshed_Thermostst= refresh_thermostat_for_scenario(this,final_same_mac)
                        //
                        //
                        //
                        //                        println("Thermoststs refreshed...")

                                                            val current_Thermostst_to_change= arrayListOf<Thermostst>()
                                                            for (Thermostst in thermostat_scenario){


                                                                println(Thermostst)
                                                                val current_Thermostst_in_scenario=Thermostst.split("#")
                                                                val current_Thermostst_in_db=thermostat_database_handler.getThermostatByName(current_Thermostst_in_scenario[0])
                                                                if (current_Thermostst_in_db != null) {
                                                                    current_Thermostst_to_change.add(current_Thermostst_in_db)
                                                                }


                                                            }
                                                            val sorted_Thermostst = arrayListOf<List<Thermostst>>()

                                                            for (light in current_Thermostst_to_change){
                                                                val a= arrayListOf <Thermostst>()
                                                                a.add(light)
                                                                sorted_Thermostst.add(a)

                                                            }
                                                            val final_thermostat_to_do= arrayListOf<List<Thermostst>>()
                                                            for (same_mac in sorted_Thermostst){

                                                                val listt = same_mac.map { Thermostat ->

                                                                    val on_off =( thermostat_scenario.find { it.startsWith("${Thermostat.name}#") }?.substringAfter("#") ?: "").substringBefore("!")
                                                                    val themp = (thermostat_scenario.find { it.startsWith("${Thermostat.name}#") }?.substringAfter("!") ?: "").substringBefore("$")
                                                                    val mod = (thermostat_scenario.find { it.startsWith("${Thermostat.name}#") }?.substringAfter("$") ?: "").substringBefore("@")
                                                                    val fan = thermostat_scenario.find { it.startsWith("${Thermostat.name}#") }?.substringAfter("@") ?: ""
                                                                    Thermostat.mood = mod
                                                                    Thermostat.on_off = on_off
                                                                    Thermostat.fan_status = fan
                                                                    Thermostat.temperature = themp

                                                                    println(Thermostat.on_off.toString() +Thermostat.temperature.toString()+ Thermostat.fan_status.toString()+Thermostat.mood.toString())

                                                                    Thermostat


                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_thermostat_to_do.add(listt)

                                                            }
                        //                        println(sorted_Thermostst)
                                                            next=true

                                                            if (next){
                                                                next=false
                                                                for (same in final_thermostat_to_do){

                                                                    try {
                        //                                    println((taghsim(1.0 ,all_scenario_count.toDouble() ))* 100)

                                                                        val send= udp_thermostat_for_scenario(activity!!,same[0],same[0].mac,same[0].mood,same[0].temperature,same[0].fan_status,same[0].on_off,same[0].ip)


                                                                        if (send){
                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(1.0 ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }


                                                                            thermostat_scenario_done.add(same[0].name)
                        //                                        println(same.count())
                        //                                        println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }

                                                        if (fan_scenario.size !=0 ){
                        //                        Handler(Looper.getMainLooper()).post {
                        ////                            loadingIndicator =view.findViewById(R.id.loadingIndicator)
                        //                            loadPercentage.addPercentage(20) // for example
                        //                            updateLoadingIndicator()
                        //                        }

                                                            var current_fan_in_scenario= ArrayList<String>()
                                                            for (fan in fan_scenario){
                                                                current_fan_in_scenario.add(fan.split("#")[0])


                                                            }
                                                            println(fan_scenario)
                                                            val same_macs=fan_database_handler.getfansBySameMacForLnames(current_fan_in_scenario)
                                                            val final_same_mac= ArrayList<fan>()
                                                            for (fan in same_macs){
                                                                final_same_mac.add(fan[0])

                                                            }
                        //                        println(same_macs)
                        //                        val refreshed_fans= refresh_fan_for_scenario(this,final_same_mac)
                        //
                        //
                        //
                        //                        println("fans refreshed...")

                                                            val current_fan_to_change= arrayListOf<fan>()
                                                            for (fan in fan_scenario){


                                                                val current_fan_in_scenario=fan.split("#")
                                                                val current_fan_in_db=fan_database_handler.getfanByCname(current_fan_in_scenario[0])
                                                                if (current_fan_in_db != null) {
                                                                    current_fan_to_change.add(current_fan_in_db)
                                                                }


                                                            }
                                                            val sorted_fan = getfansGroupedBySameMac(current_fan_to_change)
                                                            val final_fan_to_do= arrayListOf<List<fan>>()
                                                            for (same_mac in sorted_fan){
                                                                val listt = same_mac.map { fan ->
                                                                    val status = fan_scenario.find { it.startsWith("${fan.Fname}#") }?.substringAfter("#") ?: ""
                                                                    fan.status = status
                                                                    fan

                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_fan_to_do.add(listt)

                                                            }
                                                            next=true
                                                            if (next){
                                                                next=false
                                                                for (same in final_fan_to_do){

                                                                    try {
//                                                                        println((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)

                                                                        val send= udp_fan_for_scenario(activity!!,same)


                                                                        if (send){
                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }

                                                                            for (fan in same){
                                                                                fan_scenario_done.add(fan.Fname)
                                                                                println(same.count())
                                                                                println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            }


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }
                        //                    println(light_scenario_done)
                        //                    println(light_scenario)


                                                        if (light_scenario_done.count()< light_scenario.count()){
                                                            println("seciond light scenario check")

                                                            var light_to_do= arrayListOf<String>()
                                                            for (light in light_scenario){
                                                                if (light.split("#")[0] !in light_scenario_done){
                                                                    light_to_do.add(light)
                                                                }
                                                            }
                                                            println(light_to_do)


                                                            var current_light_in_scenario= ArrayList<String>()
                                                            for (light in light_to_do){
                                                                current_light_in_scenario.add(light.split("#")[0])


                                                            }

                                                            val same_macs=light_database_handler.getLightsBySameMacForLnames(current_light_in_scenario)
                                                            val final_same_mac= ArrayList<Light>()
                                                            for (light in same_macs){
                                                                final_same_mac.add(light[0])

                                                            }
                        //                        println(same_macs)


                                                            val current_light_to_change1= arrayListOf<Light>()

                                                            for (light in light_to_do){


                                                                val current_light_in_scenario=light.split("#")
                                                                val current_light_in_db=light_database_handler.getLightsByLname(current_light_in_scenario[0])
                                                                current_light_to_change1.add(current_light_in_db)


                                                            }
                                                            val sorted_lights = getLightsGroupedBySameMac(current_light_to_change1)
                                                            sorted_lights[0]
                                                            val final_light_to_do= arrayListOf<List<Light>>()
                                                            for (same_mac in sorted_lights){
                                                                val listt = same_mac.map { Light ->
                                                                    val status = light_to_do.find { it.startsWith("${Light.Lname}#") }?.substringAfter("#") ?: ""
                                                                    Light.status = status
                                                                    Light

                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_light_to_do.add(listt)

                                                            }
                                                            next=true
                                                            if (next){
                                                                next=false
                                                                for (same in final_light_to_do){

                                                                    try {
//                                                                        println((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)

                                                                        val send=udp_light_scenario(activity!!,same)


                                                                        if (send){
                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }

                                                                            for (light in same){

                                                                                light_scenario_done.add(light.Lname)
                                                                                println(same.count())
                                                                                println(all_scenario_count)

                        //


                                                                            }


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }
                                                        if (thermostat_scenario_done.count()< thermostat_scenario.count()){
                                                            println("thermostat second")
                                                            var thermostat_to_do= arrayListOf<String>()
                                                            for (thermostat in thermostat_scenario){
                                                                if (thermostat.split("#")[0] !in thermostat_scenario_done){
                                                                    thermostat_to_do.add(thermostat)
                                                                }
                                                            }

                                                            var current_Thermostst_in_scenario= ArrayList<String>()
                                                            for (Thermostst in thermostat_to_do){
                                                                current_Thermostst_in_scenario.add(Thermostst.split("#")[0])


                                                            }
                                                            println(thermostat_to_do)
                                                            val same_macs=thermostat_database_handler.getThermoststsBySameMacForPnames(current_Thermostst_in_scenario)
                                                            val final_same_mac= ArrayList<Thermostst>()
                                                            for (Thermostst in same_macs){
                                                                final_same_mac.add(Thermostst[0])

                                                            }
                        //                        println(same_macs)
                        //                        val refreshed_Thermostst= refresh_thermostat_for_scenario(this,final_same_mac)
                        //
                        //
                        //
                        //                        println("Thermoststs refreshed...")

                                                            val current_Thermostst_to_change= arrayListOf<Thermostst>()
                                                            for (Thermostst in thermostat_to_do){


                                                                println(Thermostst)
                                                                val current_Thermostst_in_scenario=
                                                                    Thermostst.split("#")
                                                                val current_Thermostst_in_db=thermostat_database_handler.getThermostatByName(current_Thermostst_in_scenario[0])
                                                                if (current_Thermostst_in_db != null) {
                                                                    current_Thermostst_to_change.add(current_Thermostst_in_db)
                                                                }


                                                            }
                                                            val sorted_Thermostst = arrayListOf<List<Thermostst>>()

                                                            for (light in current_Thermostst_to_change){
                                                                val a= arrayListOf <Thermostst>()
                                                                a.add(light)
                                                                sorted_Thermostst.add(a)

                                                            }
                                                            val final_thermostat_to_do= arrayListOf<List<Thermostst>>()
                                                            for (same_mac in sorted_Thermostst){

                                                                val listt = same_mac.map { Thermostat ->

                                                                    val on_off =( thermostat_to_do.find { it.startsWith("${Thermostat.name}#") }?.substringAfter("#") ?: "").substringBefore("!")
                                                                    val themp = (thermostat_to_do.find { it.startsWith("${Thermostat.name}#") }?.substringAfter("!") ?: "").substringBefore("$")
                                                                    val mod = (thermostat_to_do.find { it.startsWith("${Thermostat.name}#") }?.substringAfter("$") ?: "").substringBefore("@")
                                                                    val fan = thermostat_to_do.find { it.startsWith("${Thermostat.name}#") }?.substringAfter("@") ?: ""
                                                                    Thermostat.mood = mod
                                                                    Thermostat.on_off = on_off
                                                                    Thermostat.fan_status = fan
                                                                    Thermostat.temperature = themp

                                                                    println(Thermostat.on_off.toString() +Thermostat.temperature.toString()+ Thermostat.fan_status.toString()+Thermostat.mood.toString())

                                                                    Thermostat


                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_thermostat_to_do.add(listt)

                                                            }
                        //                        println(sorted_Thermostst)
                                                            next=true

                                                            if (next){
                                                                next=false
                                                                for (same in final_thermostat_to_do){

                                                                    try {
                        //                                    println((taghsim(1.0 ,all_scenario_count.toDouble() ))* 100)


                                                                        val send= udp_thermostat_for_scenario(activity!!,same[0],same[0].mac,same[0].mood,same[0].temperature,same[0].fan_status,same[0].on_off,same[0].ip)


                                                                        if (send){
                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(1.0 ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }


                                                                            thermostat_scenario_done.add(same[0].name)
                        //                                        println(same.count())
                        //                                        println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }
                                                        if (curtain_scenario_done.count()< curtain_scenario.count()){
                                                            println("curtain second")
                                                            var curtain_to_do= arrayListOf<String>()
                                                            for (curtain in curtain_scenario){
                                                                if (curtain.split("#")[0]  !in curtain_scenario_done){
                                                                    curtain_to_do.add(curtain)
                                                                }
                                                            }


                                                            var current_curtain_in_scenario = ArrayList<String>()
                                                            for (curtain in curtain_to_do){
                                                                current_curtain_in_scenario.add(curtain.split("#")[0])


                                                            }
                                                            println(curtain_to_do)
                                                            val same_macs = curtain_database_handler.getcurtainsBySameMacForPnames(current_curtain_in_scenario)
                                                            val final_same_mac = ArrayList<curtain>()
                                                            for (curtain in same_macs){
                                                                final_same_mac.add(curtain[0])

                                                            }
                        //                        println(same_macs)
                        //                        val refreshed_curtain= refresh_curtain_for_scenario(this,final_same_mac)
                        //
                        //
                        //
                        //                        println("curtains refreshed...")

                                                            val current_curtain_to_change = arrayListOf<curtain>()
                                                            for (curtain in curtain_to_do){


                                                                println(curtain)
                                                                val current_curtain_in_scenario = curtain.split("#")
                                                                val current_curtain_in_db =
                                                                    curtain_database_handler.getCurtainByCname(current_curtain_in_scenario[0])
                                                                if (current_curtain_in_db != null) {
                                                                    current_curtain_to_change.add(current_curtain_in_db)
                                                                }


                                                            }

                                                            val sorted_curtain = getcurtainGroupedBySameMac(current_curtain_to_change)

                                                            val final_curtain_to_do = arrayListOf<List<curtain>>()
                                                            for (same_mac in sorted_curtain){
                                                                val listt = same_mac.map { curtain ->
                                                                    val status =
                                                                        curtain_to_do.find { it.startsWith("${curtain.Cname}#") }?.substringAfter("#") ?: ""
                                                                    curtain.status = status
                                                                    curtain

                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_curtain_to_do.add(listt)

                                                            }
                                                            next=true

                                                            if (next){
                                                                next = false
                                                                for (same in final_curtain_to_do) {

                                                                    try {
                        //                                    println((taghsim(1.0 ,all_scenario_count.toDouble() ))* 100)

                                                                        val send = udp_curtain_for_scenario(activity!!, same[0], same[0].status)


                                                                        if (send) {
                        //                                                                    Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress(
                        //                                                                            (taghsim(
                        //                                                                                1.0,
                        //                                                                                all_scenario_count.toDouble()
                        //                                                                            )) * 100
                        //                                                                        )
                        //
                        //                                                                    }

                                                                            curtain_scenario_done.add(same[0].Cname)
                        //                                        println(same.count())
                        //                                        println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            next = true
                                                                        }
                                                                    } catch (e: Exception) {
                                                                        next = true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }

                                                        if (plug_scenario_done.count()< plug_scenario.count()){
                                                            println("plug second")
                                                            var plug_to_do= arrayListOf<String>()
                                                            for (plug in plug_scenario){
                                                                if (plug.split("#")[0] !in plug_scenario_done){
                                                                    plug_to_do.add(plug)
                                                                }
                                                            }


                                                            var current_plug_in_scenario= ArrayList<String>()
                                                            for (plug in plug_to_do){
                                                                current_plug_in_scenario.add(plug.split("#")[0])


                                                            }
                                                            println(plug_scenario)
                                                            val same_macs=plug_database_handler.getPlugsBySameMacForPnames(current_plug_in_scenario)
                                                            val final_same_mac= ArrayList<Plug>()
                                                            for (plug in same_macs){
                                                                final_same_mac.add(plug[0])

                                                            }
                        //                        println(same_macs)
                        //                        val refreshed_plugs=refresh_plug_for_scenario(this,final_same_mac)
                        //
                        //
                        //
                        //                        println("plugs refreshed...")

                                                            val current_plug_to_change= arrayListOf<Plug>()
                                                            for (plug in plug_to_do){


                                                                val current_plug_in_scenario=plug.split("#")
                                                                val current_plug_in_db=plug_database_handler.getPlugByCname(current_plug_in_scenario[0])
                                                                if (current_plug_in_db != null) {
                                                                    current_plug_to_change.add(current_plug_in_db)
                                                                }


                                                            }

                                                            val sorted_plugs = getPlugsGroupedBySameMac(current_plug_to_change)


                                                            val final_plug_to_do= arrayListOf<List<Plug>>()
                                                            for (same_mac in sorted_plugs){
                                                                val listt = same_mac.map { Plug ->
                                                                    val status = plug_to_do.find { it.startsWith("${Plug.Pname}#") }?.substringAfter("#") ?: ""
                                                                    Plug.status = status
                                                                    Plug

                                                                }
                                                                println(current_plug_in_scenario)
                                                                println(listt[0].Pname)
                                                                println(listt[0].status)


                                                                final_plug_to_do.add(listt)

                                                            }

                                                            println(sorted_plugs)
                                                            next=true

                                                            if (next){
                                                                next=false
                                                                for (same in final_plug_to_do){

                                                                    try {
//                                                                        println((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)

                                                                        val send=udp_plug_scenario(activity!!,same)


                                                                        if (send){
                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }
                                                                            for (plug in same){
                                                                                plug_scenario_done.add(plug.Pname)
                                                                                println(same.count())
                                                                                println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            }


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }
                                                        if (valve_scenario_done.count()< valve_scenario.count()){
                                                            println("valve second")
                                                            var valve_to_do= arrayListOf<String>()
                                                            for (valve in valve_scenario){
                                                                if (valve.split("#")[0]  !in valve_scenario_done){
                                                                    valve_to_do.add(valve)
                                                                }
                                                            }
                                                            var current_valve_in_scenario= ArrayList<String>()
                                                            for (valve in valve_to_do){
                                                                current_valve_in_scenario.add(valve.split("#")[0])


                                                            }
                                                            println(valve_to_do)
                                                            val same_macs=valve_database_handler.getValvesBySameMacForPnames(current_valve_in_scenario)
                                                            val final_same_mac= ArrayList<valve>()
                                                            for (valve in same_macs){
                                                                final_same_mac.add(valve[0])

                                                            }
                        //                        println(same_macs)

                                                            val current_valve_to_change= arrayListOf<valve>()
                                                            for (valve in valve_to_do){


                                                                println(valve)
                                                                val current_valve_in_scenario= valve.split("#")
                                                                val current_valve_in_db=valve_database_handler.getvalveByCname(current_valve_in_scenario[0])
                                                                if (current_valve_in_db != null) {
                                                                    current_valve_to_change.add(current_valve_in_db)
                                                                }


                                                            }
                                                            val sorted_valve = getvalvesGroupedBySameMac(current_valve_to_change)
                                                            val final_valve_to_do= arrayListOf<List<valve>>()
                                                            for (same_mac in sorted_valve){
                                                                val listt = same_mac.map { valve ->
                                                                    val status = valve_to_do.find { it.startsWith("${valve.Vname}#") }?.substringAfter("#") ?: ""
                                                                    valve.status = status
                                                                    valve

                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_valve_to_do.add(listt)

                                                            }
                                                            next=true

                                                            if (next){
                                                                next=false
                                                                for (same in final_valve_to_do){

                                                                    try {
//                                                                        println((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)

                                                                        val send= udp_valve_for_scenario(activity!!,same)


                                                                        if (send){

                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }
                                                                            for (valve in same){
                                                                                valve_scenario_done.add(valve.Vname)
                                                                                println(same.count())
                                                                                println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            }


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }
                                                        if (fan_scenario_done.count()< fan_scenario.count()){
                                                            println("fan second")
                                                            var fan_to_do= arrayListOf<String>()
                                                            for (fan in fan_scenario){
                                                                if (fan.split("#")[0]  !in fan_scenario_done){
                                                                    fan_to_do.add(fan)
                                                                }
                                                            }
                                                            var current_fan_in_scenario= ArrayList<String>()
                                                            for (fan in fan_to_do){
                                                                current_fan_in_scenario.add(fan.split("#")[0])


                                                            }
                                                            println(fan_to_do)
                                                            val same_macs=fan_database_handler.getfansBySameMacForLnames(current_fan_in_scenario)
                                                            val final_same_mac= ArrayList<fan>()
                                                            for (fan in same_macs){
                                                                final_same_mac.add(fan[0])

                                                            }
                        //                        println(same_macs)
                        //                        val refreshed_fans= refresh_fan_for_scenario(this,final_same_mac)
                        //
                        //
                        //
                        //                        println("fans refreshed...")

                                                            val current_fan_to_change= arrayListOf<fan>()
                                                            for (fan in fan_to_do){


                                                                val current_fan_in_scenario= fan.split("#")
                                                                val current_fan_in_db=fan_database_handler.getfanByCname(current_fan_in_scenario[0])
                                                                if (current_fan_in_db != null) {
                                                                    current_fan_to_change.add(current_fan_in_db)
                                                                }


                                                            }
                                                            val sorted_fan = getfansGroupedBySameMac(current_fan_to_change)
                                                            val final_fan_to_do= arrayListOf<List<fan>>()
                                                            for (same_mac in sorted_fan){
                                                                val listt = same_mac.map { fan ->
                                                                    val status = fan_to_do.find { it.startsWith("${fan.Fname}#") }?.substringAfter("#") ?: ""
                                                                    fan.status = status
                                                                    fan

                                                                }
                        //                                println(current_light_in_scenario)
                        //                                println(listt[0].Lname)
                        //                                println(listt[0].status)


                                                                final_fan_to_do.add(listt)

                                                            }
                                                            next=true
                                                            if (next){
                                                                next=false
                                                                for (same in final_fan_to_do){

                                                                    try {
//                                                                        println((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)

                                                                        val send= udp_fan_for_scenario(activity!!,same)


                                                                        if (send){
                                                                            Handler(Looper.getMainLooper()).post {
                        //                                                                        loading_view.increaseProgress((taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))* 100)
                        //
                                                                            }

                                                                            for (fan in same){
                                                                                fan_scenario_done.add(fan.Fname)
                                                                                println(same.count())
                                                                                println(all_scenario_count)

                        //                                            println(taghsim(same.count().toDouble() ,all_scenario_count.toDouble() ))

                        //                                            val a =12
                        //                                            println("dddddddddddddddddddddddddddddddd $a")


                                                                            }


                                                                            next=true
                                                                        }
                                                                    }catch (e:Exception){
                                                                        next=true
                                                                        println(e)
                                                                        //
                                                                    }

                                                                }

                                                            }


                                                        }







                                                        activity!!.runOnUiThread {
                                                            sharedViewModel.update_is_doing("not bussy")
                                                        }


                                                        var fan_to_do = arrayListOf<String>()
                                                        var valve_to_do= arrayListOf<String>()
                                                        var plug_to_do= arrayListOf<String>()
                                                        var curtain_to_do = arrayListOf<String>()
                                                        var thermostat_to_do= arrayListOf<String>()
                                                        var light_to_do= arrayListOf<String>()


                                                        if (fan_scenario_done.count()< fan_scenario.count()) {


                                                            for (fan in fan_scenario) {
                                                                if (fan.split("#")[0] !in fan_scenario_done) {
                                                                    fan_to_do.add(fan)
                                                                }
                                                            }

                                                        }
                                                        if (valve_scenario_done.count()< valve_scenario.count()) {


                                                            for (valve in valve_scenario) {
                                                                if (valve.split("#")[0] !in valve_scenario_done) {
                                                                    valve_to_do.add(valve)
                                                                }
                                                            }


                                                        }
                                                        if (plug_scenario_done.count()< plug_scenario.count()) {


                                                            for (plug in plug_scenario) {
                                                                if (plug.split("#")[0] !in plug_scenario_done) {
                                                                    plug_to_do.add(plug)
                                                                }
                                                            }
                                                        }

                                                        if (curtain_scenario_done.count()< curtain_scenario.count()) {


                                                            for (curtain in curtain_scenario) {
                                                                if (curtain.split("#")[0] !in curtain_scenario_done) {
                                                                    curtain_to_do.add(curtain)
                                                                }
                                                            }
                                                        }

                                                        if (thermostat_scenario_done.count()< thermostat_scenario.count()) {


                                                            for (thermostat in thermostat_scenario) {
                                                                if (thermostat.split("#")[0] !in thermostat_scenario_done) {
                                                                    thermostat_to_do.add(thermostat)
                                                                }
                                                            }
                                                        }
                                                        if (light_scenario_done.count()< light_scenario.count()) {


                                                            for (light in light_scenario) {
                                                                if (light.split("#")[0] !in light_scenario_done) {
                                                                    light_to_do.add(light)
                                                                }
                                                            }
                                                        }
                                                        activity!!.runOnUiThread{
                        //                                                    popupWindow.dismiss()
                                                        }

                                                        println("done")
                                                        println(all_scenario_count)
                                                        println(fan_to_do.count()+valve_to_do.count()+plug_to_do.count()+curtain_to_do.count()+thermostat_to_do.count()+light_to_do.count())
                                                        if (0==fan_to_do.count()+valve_to_do.count()+plug_to_do.count()+curtain_to_do.count()+thermostat_to_do.count()+light_to_do.count()){
                                                            activity!!.runOnUiThread{
                                                                Toast.makeText(activity, "Scenario Done", Toast.LENGTH_SHORT).show()
                                                            }


                                                        }else if (all_scenario_count>fan_to_do.count()+valve_to_do.count()+plug_to_do.count()+curtain_to_do.count()+thermostat_to_do.count()+light_to_do.count()){
                                                            val final_list_to_show= arrayListOf<String>()
                                                            println(fan_to_do)
                                                            println(curtain_to_do)
                                                            println(thermostat_to_do)
                                                            println(light_to_do)

                                                            if (music_scenario_done.count() < music_scenario.count()){
                                                                final_list_to_show.add("Music")

                                                            }


                                                            if (fan_to_do.count()>0){
                                                                for (item in fan_to_do){
                                                                    final_list_to_show.add("${item.split("#")[0]}  (Fan)" )
                                                                }


                                                            }
                                                            if (valve_to_do.count()>0){
                                                                for (item in valve_to_do){
                                                                    final_list_to_show.add("${item.split("#")[0]}  (Valve)" )
                                                                }


                                                            }else if (plug_to_do.count()>0){
                                                                for (item in plug_to_do){
                                                                    final_list_to_show.add("${item.split("#")[0]}  (Plug)" )
                                                                }


                                                            }
                                                            if (curtain_to_do.count()>0){
                                                                for (item in curtain_to_do){
                                                                    final_list_to_show.add("${item.split("#")[0]}  (Curtain)" )
                                                                }


                                                            }
                                                            if (thermostat_to_do.count()>0){
                                                                for (item in thermostat_to_do){
                                                                    final_list_to_show.add("${item.split("#")[0]}  (Thermostat)" )
                                                                }


                                                            }
                                                            if (light_to_do.count()>0){
                                                                for (item in light_to_do){
                                                                    final_list_to_show.add("${item.split("#")[0]}  (Light)" )
                                                                }


                                                            }
                                                            activity!!.runOnUiThread {

                        //                                                        popupWindow.dismiss()
                                                            }
                                                            println(final_list_to_show)


                                                        }


                                                    }.start()


                                                }else{

                                                    Toast.makeText(context, "please Wait...", Toast.LENGTH_SHORT).show()
                                                }


                                            }


                                        }catch (e:Exception){
                                            println(e)
                                            UdpListener8090.running = true
                                            UdpListener8090.paused =false
                                        }
                                        UdpListener8090.running = true
                                        UdpListener8090.paused =false


                                    }


                                }
                            }


                        }
                    } catch (e: IOException) {
                        println("Error in receiving UDP packet. Error: ${e.message}")
                        if (UdpListener8090.running && !UdpListener8090.paused) {
                            println("Reconnecting...")
                            UdpListener8090.reconnect()
                        }
                    }

                }
            }
        }.start()
    }
    fun aut_recconect() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                // کدی که باید هر 5 ثانیه یک بار اجرا شود
//                Task()

                println("P : " +paused)
                println("R : " +running)
                reconnect()
                // اجرای مجدد کد بعد از 5 ثانیه
                handler.postDelayed(this, 5000)
            }
        }

        // شروع اجرای کد
        handler.post(runnable)
    }

    fun Task() {
        // کدی که باید اجرا شود
        println("Task executed")
    }

    fun start() {
        paused = false
        if (!running) {
            running=true
            startListening()
        }
    }

    fun pause() {
        running = false
        paused = true
        socket?.close()
        println("Paused")
    }

    fun resume() {
        if (!running) {
            paused = false
            running = true
            startListening()
            println("Resumed")
        }
    }

    fun stop() {
        running = false
        socket?.close()
        println("Stopped")
    }

    private fun reconnect() {
        println("reconnecting..........")
        Thread.sleep(4000)
        if (running && !paused) {
            while (running && !paused) {
                if (!paused){

                    try {
                        initializeSocket()
                        println("Reconnected to the port: $LISTEN_PORT")
                        break
                    } catch (e: SocketException) {
                        println("Attempting to reconnect to the port: $LISTEN_PORT. Error: ${e.message}")
                        Thread.sleep(5000)
                    }

                }else{
                    Thread.sleep(5000)
                    println("Reconnect delayed ...")
                }
            }
        } else {
            println("Reconnect delayed ...")
        }
    }

    // Grouping functions
    fun getcurtainGroupedBySameMac(curtains: List<curtain>): List<List<curtain>> {
        val groupedLights = mutableMapOf<String, MutableList<curtain>>()

        for (curtain in curtains) {
            val mac = curtain.mac ?: continue // تایید کردن اینکه Mac خالی نباشد
            if (!groupedLights.containsKey(mac)) {
                groupedLights[mac] = mutableListOf()
            }
            groupedLights[mac]?.add(curtain)
        }

        // تبدیل Map به لیست از لیست‌ها
        return groupedLights.values.toList()
    }

    fun getPlugsGroupedBySameMac(plugs: List<Plug>): List<List<Plug>> {
        val groupedLights = mutableMapOf<String, MutableList<Plug>>()

        for (plugg in plugs) {
            val mac = plugg.mac ?: continue // تایید کردن اینکه Mac خالی نباشد
            if (!groupedLights.containsKey(mac)) {
                groupedLights[mac] = mutableListOf()
            }
            groupedLights[mac]?.add(plugg)
        }

        // تبدیل Map به لیست از لیست‌ها
        return groupedLights.values.toList()
    }


    fun getLightsGroupedBySameMac(lights: List<Light>): List<List<Light>> {
        val groupedLights = mutableMapOf<String, MutableList<Light>>()

        for (light in lights) {
            val mac = light.mac ?: continue // تایید کردن اینکه Mac خالی نباشد
            if (!groupedLights.containsKey(mac)) {
                groupedLights[mac] = mutableListOf()
            }
            groupedLights[mac]?.add(light)
        }

        // تبدیل Map به لیست از لیست‌ها
        return groupedLights.values.toList()
    }

    fun getfansGroupedBySameMac(lights: List<fan>): List<List<fan>> {
        val groupedLights = mutableMapOf<String, MutableList<fan>>()

        for (light in lights) {
            val mac = light.mac ?: continue // تایید کردن اینکه Mac خالی نباشد
            if (!groupedLights.containsKey(mac)) {
                groupedLights[mac] = mutableListOf()
            }
            groupedLights[mac]?.add(light)
        }

        // تبدیل Map به لیست از لیست‌ها
        return groupedLights.values.toList()
    }

    fun getvalvesGroupedBySameMac(valve: List<valve>): List<List<valve>> {
        val groupedLights = mutableMapOf<String, MutableList<valve>>()

        for (valvee in valve) {
            val mac = valvee.mac ?: continue // تایید کردن اینکه Mac خالی نباشد
            if (!groupedLights.containsKey(mac)) {
                groupedLights[mac] = mutableListOf()
            }
            groupedLights[mac]?.add(valvee)
        }

        // تبدیل Map به لیست از لیست‌ها
        return groupedLights.values.toList()
    }
}