package com.example.griffinmobile.fragments

import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.griffinmobile.R
import com.example.griffinmobile.apdapters.moving_adapter
import com.example.griffinmobile.database.Temperature_db
import com.example.griffinmobile.database.curtain_db
import com.example.griffinmobile.database.fan_db
import com.example.griffinmobile.database.favorite_db
import com.example.griffinmobile.database.light_db
import com.example.griffinmobile.database.plug_db
import com.example.griffinmobile.database.room_devices_db
import com.example.griffinmobile.database.rooms_db
import com.example.griffinmobile.database.scenario_db
import com.example.griffinmobile.database.setting_network_db
import com.example.griffinmobile.database.six_workert_db
import com.example.griffinmobile.database.valve_db
import com.example.griffinmobile.modules.connectToWiFiAndPerformAction
import com.example.griffinmobile.mudels.CircularSeekBar
import com.example.griffinmobile.mudels.Light
import com.example.griffinmobile.mudels.SharedViewModel
import com.example.griffinmobile.mudels.checkIP
import com.example.griffinmobile.mudels.extract_response
import com.example.griffinmobile.mudels.refresh_light
import com.example.griffinmobile.mudels.rooms
import com.example.griffinmobile.mudels.udp_light
import com.example.griffinmobile.mudels.udp_thermostat
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.LinkedList
import java.util.Queue
import kotlin.concurrent.thread

data class Quadruple<T1, T2, T3, T4>(
    val first: T1,
    val second: T2,
    val third: T3,
    val fourth: T4
)


class room_fragment : Fragment() {

    val sharedViewModel:SharedViewModel by activityViewModels()
    private lateinit var gestureDetector: GestureDetector
    private val requestQueue: Queue<Quadruple<ConstraintLayout, String, String, String>> = LinkedList()
    private var isProcessing = false
    private val handler = Handler(Looper.getMainLooper())
    private val delayMillis: Long = 400

    var adapter: moving_adapter? =null




    fun rename_item_in_scenario(e_d:String , old_item_name:String,new_item_name:String,type:String){
        var scenario_db_handler = scenario_db.Scenario_db.getInstance(requireContext())
        val favorite_db_handler = favorite_db.Favorite_db.getInstance(requireContext())
        val all_favorite = favorite_db_handler.getAllFavorite()

        var all_scenario = scenario_db_handler.getAllScenario()
        if (e_d=="e"){
            if (type=="light"){
                for (scenario in all_scenario){
                    println(scenario.light)
                    println(old_item_name)
                    if (scenario.light!!.contains(old_item_name) ){
                        println("bood")
                        println(scenario.light)
                        scenario.light=scenario.light!!.replace(old_item_name,new_item_name)
                        println(scenario.id)
                        scenario_db_handler.updateScenarioById(scenario.id,scenario)
                    }
                }
                for (favorite in all_favorite){

                    if (favorite.name==old_item_name && favorite.type=="light"){
                        favorite.name=new_item_name
                        favorite_db_handler.updateFavoriteNameById(favorite.id,favorite.name)

                    }
                }


            }else if (type=="curtain"){
                for (scenario in all_scenario){
                    if (scenario.curtain!!.contains(old_item_name) ){
                        scenario.curtain=scenario.curtain!!.replace(old_item_name,new_item_name)
                        scenario_db_handler.updateScenarioById(scenario.id,scenario)
                    }
                }
                for (favorite in all_favorite){

                    if (favorite.name==old_item_name && favorite.type=="curtain"){
                        favorite.name=new_item_name
                        favorite_db_handler.updateFavoriteNameById(favorite.id,favorite.name)

                    }
                }

            }else if (type=="fan"){
                for (scenario in all_scenario){
                    if (scenario.fan!!.contains(old_item_name) ){
                        scenario.fan=scenario.fan!!.replace(old_item_name,new_item_name)
                        scenario_db_handler.updateScenarioById(scenario.id,scenario)
                    }
                }
                for (favorite in all_favorite){



                    if (favorite.name==old_item_name && favorite.type=="fan"){
                        favorite.name=new_item_name
                        favorite_db_handler.updateFavoriteNameById(favorite.id,favorite.name)

                    }
                }

            }else if (type=="plug"){
                for (scenario in all_scenario){
                    if (scenario.plug!!.contains(old_item_name) ){
                        scenario.plug=scenario.plug!!.replace(old_item_name,new_item_name)
                        scenario_db_handler.updateScenarioById(scenario.id,scenario)
                    }
                }

            }else if (type=="temp"){
                for (scenario in all_scenario){
                    if (scenario.thermostat!!.contains(old_item_name) ){
                        scenario.thermostat=scenario.thermostat!!.replace(old_item_name,new_item_name)
                        scenario_db_handler.updateScenarioById(scenario.id,scenario)
                    }
                }
                for (favorite in all_favorite){

                    if (favorite.name==old_item_name && favorite.type=="thermostat"){
                        favorite.name=new_item_name
                        favorite_db_handler.updateFavoriteNameById(favorite.id,favorite.name)

                    }
                }

            }else if (type=="valve"){
                for (scenario in all_scenario){
                    if (scenario.valve!!.contains(old_item_name) ){
                        scenario.valve=scenario.valve!!.replace(old_item_name,new_item_name)
                        scenario_db_handler.updateScenarioById(scenario.id,scenario)
                    }
                }
                for (favorite in all_favorite){

                    if (favorite.name==old_item_name && favorite.type=="valve"){
                        favorite.name=new_item_name
                        favorite_db_handler.updateFavoriteNameById(favorite.id,favorite.name)

                    }
                }

            }

        }else{
            if (type=="light"){
                for (scenario in all_scenario){
                    if (scenario.light!!.contains(old_item_name) ){
                        var my_list = scenario.light!!.split(",").toMutableList()
                        for (item in my_list){
                            if (item.contains(old_item_name)){
                                val my_index = my_list.indexOf(item)
                                my_list.removeAt(my_index)
                                break


                            }

                        }

                        scenario.light=my_list.joinToString(",")

                        scenario_db_handler.updateScenarioById(scenario.id,scenario)
                    }
                }
                for (favorite in all_favorite){

                    if (favorite.name==old_item_name && favorite.type=="light"){
                        favorite.name=new_item_name
                        favorite_db_handler.delete_from_db_Favorite(favorite.id)

                    }
                }
            }else if (type=="curtain"){
                for (scenario in all_scenario){
                    if (scenario.curtain!!.contains(old_item_name) ){
                        var my_list = scenario.curtain!!.split(",").toMutableList()
                        for (item in my_list){
                            if (item.contains(old_item_name)){
                                val my_index = my_list.indexOf(item)
                                my_list.removeAt(my_index)
                                break


                            }

                        }

                        scenario.curtain=my_list.joinToString(",")

                        scenario_db_handler.updateScenarioById(scenario.id,scenario)
                    }
                }
                for (favorite in all_favorite){

                    if (favorite.name==old_item_name && favorite.type=="curtain"){
                        favorite.name=new_item_name
                        favorite_db_handler.delete_from_db_Favorite(favorite.id)

                    }
                }
            }else if (type=="fan"){
                for (scenario in all_scenario){
                    if (scenario.fan!!.contains(old_item_name) ){
                        var my_list = scenario.fan!!.split(",").toMutableList()
                        for (item in my_list){
                            if (item.contains(old_item_name)){
                                val my_index = my_list.indexOf(item)
                                my_list.removeAt(my_index)
                                break


                            }

                        }

                        scenario.fan=my_list.joinToString(",")

                        scenario_db_handler.updateScenarioById(scenario.id,scenario)
                    }
                }
                for (favorite in all_favorite){

                    if (favorite.name==old_item_name && favorite.type=="fan"){
                        favorite.name=new_item_name
                        favorite_db_handler.delete_from_db_Favorite(favorite.id)

                    }
                }
            }else if (type=="plug"){
                for (scenario in all_scenario){
                    if (scenario.plug!!.contains(old_item_name) ){
                        var my_list = scenario.plug!!.split(",").toMutableList()
                        for (item in my_list){
                            if (item.contains(old_item_name)){
                                val my_index = my_list.indexOf(item)
                                my_list.removeAt(my_index)
                                break


                            }

                        }

                        scenario.plug=my_list.joinToString(",")

                        scenario_db_handler.updateScenarioById(scenario.id,scenario)
                    }
                }
                for (favorite in all_favorite){

                    if (favorite.name==old_item_name && favorite.type=="plug"){
                        favorite.name=new_item_name
                        favorite_db_handler.delete_from_db_Favorite(favorite.id)

                    }
                }
            }else if (type=="temp"){
                for (scenario in all_scenario){
                    if (scenario.thermostat!!.contains(old_item_name) ){
                        var my_list = scenario.thermostat!!.split(",").toMutableList()
                        for (item in my_list){
                            if (item.contains(old_item_name)){
                                val my_index = my_list.indexOf(item)
                                my_list.removeAt(my_index)
                                break


                            }

                        }

                        scenario.thermostat=my_list.joinToString(",")

                        scenario_db_handler.updateScenarioById(scenario.id,scenario)
                    }
                }
                for (favorite in all_favorite){

                    if (favorite.name==old_item_name && favorite.type=="thermostat"){
                        favorite.name=new_item_name
                        favorite_db_handler.delete_from_db_Favorite(favorite.id)

                    }
                }
            }else if (type=="valve"){
                for (scenario in all_scenario){
                    if (scenario.valve!!.contains(old_item_name) ){
                        var my_list = scenario.valve!!.split(",").toMutableList()
                        for (item in my_list){
                            if (item.contains(old_item_name)){
                                val my_index = my_list.indexOf(item)
                                my_list.removeAt(my_index)
                                break


                            }

                        }

                        scenario.valve=my_list.joinToString(",")

                        scenario_db_handler.updateScenarioById(scenario.id,scenario)
                    }
                }
                for (favorite in all_favorite){

                    if (favorite.name==old_item_name && favorite.type=="thermostat"){
                        favorite.name=new_item_name
                        favorite_db_handler.delete_from_db_Favorite(favorite.id)

                    }
                }
            }


        }



    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_room_fragment, container, false)
        val imageView = view.findViewById<ImageView>(R.id.imageView4)



        return view
    }

    private fun isTouchInsideView(x: Float, y: Float, view: View): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val viewX = location[0]
        val viewY = location[1]

        // مختصات ConstraintLayout
        val left = viewX
        val right = left + view.width
        val top = viewY
        val bottom = top + view.height

        // بررسی آیا موقعیت لمس داخل ConstraintLayout است یا خیر
        return x >= left && x <= right && y >= top && y <= bottom
    }
    private fun onSwipeLeft() {
        Toast.makeText(requireContext(), "Swipe Left", Toast.LENGTH_SHORT).show()
    }

    private fun onSwipeRight() {
        Toast.makeText(requireContext(), "Swipe Right", Toast.LENGTH_SHORT).show()
    }

    private fun processQueue() {
        val light_helper = light_db.getInstance(requireContext())
        val fan_db_helper = fan_db.getInstance(requireContext())
        val plug_db_helper = plug_db.getInstance(requireContext())
        val curtain_db_helper = curtain_db.getInstance(requireContext())
        val valve_db_helper = valve_db.getInstance(requireContext())
        val termostat_db_helper = Temperature_db.getInstance(requireContext())

        try {
            if (requestQueue.isEmpty()) {
                isProcessing = false
                return
            }

            isProcessing = true

            val (background, status, type,id) = requestQueue.poll()

            if (type == "L"){
                val current_light = light_helper.get_from_db_light(id.toInt())
                val previousStatus = status

//        println(switch.isChecked)
                if (status=="1") {
                    light_helper.updateStatusById(id.toInt(), "off")
                } else {
                    light_helper.updateStatusById(id.toInt(), "on")
                }

                Thread {
                    try {
                        println("sended")
                        val res = current_light?.let { udp_light(requireContext(), it) }
                        if (!res!!){
                            background.setBackgroundResource(R.drawable.light_background_off)
                        }
                        println("2")
                        println(res)


                    } catch (e: Exception) {
                        println(e)
                        print("lights page")
                    } finally {
                        handler.postDelayed({
                            processQueue()
                        }, delayMillis)
                    }
                }.start()


            }

        }catch (e:Exception){
            println(e)

        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val add_button = view.findViewById<Button>(R.id.add_btn)



        fun showPopup_termostat(termostat_id :String, selected_item: ConstraintLayout) {
            // ساخت دیالوگ
            val dialog = Dialog(requireContext())

            // حذف گوشه‌ها و پس‌زمینه دیالوگ
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            // تنظیم لایه دلخواه برای دیالوگ
            dialog.setContentView(R.layout.thermostat_view)


            dialog.setCanceledOnTouchOutside(false)





            val delayMillis2: Long = 2000
            try {
                var temp_layout = dialog.findViewById<ConstraintLayout>(R.id.temp_layout)

                val temp_db = Temperature_db.getInstance(requireContext())


//                var termostats_in_room=temp_db.getThermostatsByRoomName(room!!.room_name)

                val coler_on_off = dialog.findViewById<CheckBox>(R.id.coler_on_off)
                var current_temperature_textViwe=dialog.findViewById<TextView>(R.id.current_temperature)

                val change_fan_status = dialog.findViewById<Button>(R.id.change_fan_status)
                var fanstatus_num=0
                val status1 = dialog.findViewById<ImageView>(R.id.status1)
                val status2 = dialog.findViewById<ImageView>(R.id.status2)
                val status3 = dialog.findViewById<ImageView>(R.id.status3)
                val status4 = dialog.findViewById<TextView>(R.id.status4)
                val circularSeekBar=dialog.findViewById<CircularSeekBar>(R.id.circularSeekBar)

                val radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup)

                val winterside = dialog.findViewById<ImageButton>(R.id.radioOption1)
                val summerside = dialog.findViewById<ImageButton>(R.id.radioOption2)
//                val temp_disconnected = view.findViewById<TextView>(R.id.temp_disconnected)

                var current_thermostat=temp_db.get_from_db_Temprature(termostat_id.toInt())
                var current_seekbar_temp=current_thermostat!!.temperature
                var on_off_status=current_thermostat.on_off
                var current_temperature=current_thermostat.current_temperature
                var fanstatus=current_thermostat.fan_status
                var mood=current_thermostat.mood

                var isRefreshing = false




                var handler= Handler(Looper.getMainLooper())









                fun fanstatus1(){
                    fanstatus="1"
                    if (status1.isVisible){
                        null

                    }else{
                        status1.alpha = 0f
                        status1.visibility = View.VISIBLE

                        status1.animate()
                            .alpha(1f)
                            .setDuration(1000)
                            .setListener(null)

                    }
                    if (status2.isVisible){
                        status2.alpha = 1f
                        status2.visibility = View.GONE

                        status2.animate()
                            .alpha(0f)
                            .setDuration(1000)
                            .setListener(null)

                    }

                    if (status3.isVisible){

                        status3.alpha = 1f
                        status3.visibility = View.GONE

                        status4.animate()
                            .alpha(0f)
                            .setDuration(1000)
                            .setListener(null)
                    }

                    if (status4.isVisible){
                        status4.alpha = 1f
                        status4.visibility = View.GONE

                        status4.animate()
                            .alpha(0f)
                            .setDuration(1000)
                            .setListener(null)

                    }

                }
                fun fanstatus2() {
                    fanstatus="2"
                    if (status1.isVisible){
                        null

                    }else{
                        status1.alpha = 0f
                        status1.visibility = View.VISIBLE

                        status1.animate()
                            .alpha(1f)
                            .setDuration(1000)
                            .setListener(null)

                    }
                    if (status2.isVisible){
                        null
                    }else{

                        status2.alpha = 0f
                        status2.visibility = View.VISIBLE

                        status2.animate()
                            .alpha(1f)
                            .setDuration(1000)
                            .setListener(null)
                    }

                    if (status3.isVisible){
                        status3.alpha = 1f
                        status3.visibility = View.GONE

                        status3.animate()
                            .alpha(0f)
                            .setDuration(1000)
                            .setListener(null)
                    }

                    if (status4.isVisible){
                        status4.alpha = 1f
                        status4.visibility = View.GONE

                        status4.animate()
                            .alpha(0f)
                            .setDuration(1000)
                            .setListener(null)
                    }

                }
                fun fanstatus3(){
                    fanstatus="3"
                    if (status1.isVisible){
                        null

                    }else{
                        status1.alpha = 0f
                        status1.visibility = View.VISIBLE

                        status1.animate()
                            .alpha(1f)
                            .setDuration(1000)
                            .setListener(null)

                    }
                    if (status2.isVisible){
                        null
                    }else{

                        status2.alpha = 0f
                        status2.visibility = View.VISIBLE

                        status2.animate()
                            .alpha(1f)
                            .setDuration(1000)
                            .setListener(null)
                    }
                    if (status3.isVisible){
                        null
                    }else{

                        status3.alpha = 0f
                        status3.visibility = View.VISIBLE

                        status3.animate()
                            .alpha(1f)
                            .setDuration(1000)
                            .setListener(null)

                    }

                    if (status4.isVisible){
                        status4.alpha = 1f
                        status4.visibility = View.GONE

                        status4.animate()
                            .alpha(0f)
                            .setDuration(1000)
                            .setListener(null)
                    }
                }
                fun fanstatus4(){
                    fanstatus="0"
                    if (status1.isVisible){
                        status1.alpha = 1f
                        status1.visibility = View.GONE

                        status1.animate()
                            .alpha(0f)
                            .setDuration(1000)
                            .setListener(null)
                    }
                    if (status2.isVisible){
                        status2.alpha = 1f
                        status2.visibility = View.GONE

                        status2.animate()
                            .alpha(0f)
                            .setDuration(1000)
                            .setListener(null)


                    }

                    if (status3.isVisible){

                        status3.alpha = 1f
                        status3.visibility = View.GONE

                        status3.animate()
                            .alpha(0f)
                            .setDuration(1000)
                            .setListener(null)

                    }


                    if (status4.isVisible){
                        null
                    }else{
                        status4.alpha = 0f
                        status4.visibility = View.VISIBLE

                        status4.animate()
                            .alpha(1f)
                            .setDuration(1000)
                            .setListener(null)

                    }

                }
                fun update_statuses_ui(){
                    try {
                        var new_db=Temperature_db.getInstance(requireContext())
                        var new_s = new_db.get_from_db_Temprature(termostat_id.toInt())
                        on_off_status=new_s!!.on_off
                        if (on_off_status=="1"){
                            requireActivity().runOnUiThread{

                                selected_item.setBackgroundResource(R.drawable.termostat_background_on)
                            }

                        }else{
                            requireActivity().runOnUiThread{
                                selected_item.setBackgroundResource(R.drawable.termostat_background_off)

                            }
                        }
                        println(new_s!!.on_off)
                        fanstatus=new_s!!.fan_status
                        current_temperature=new_s!!.current_temperature!!.toInt().toString()
                        mood=new_s!!.mood
                        current_seekbar_temp=new_s!!.temperature

                        circularSeekBar.setProgress(new_s!!.temperature!!.toInt())

                        current_temperature_textViwe.setText(current_temperature)

                        if (mood=="0"){
                            radioGroup.check(R.id.radioOption1)
                            winterside.setBackgroundResource(R.drawable.winter_side_on);
                            summerside.setBackgroundResource(R.drawable.summer_side_off);

                        }else{
                            radioGroup.check(R.id.radioOption2)
                            winterside.setBackgroundResource(R.drawable.winter_side_off);
                            summerside.setBackgroundResource(R.drawable.summer_side_on);
                        }
                        if (fanstatus=="0"){
                            fanstatus4()
                            fanstatus_num=0
                        }else if (fanstatus=="1"){
                            fanstatus1()
                            fanstatus_num=1

                        }else if (fanstatus=="2"){
                            fanstatus2()
                            fanstatus_num=2

                        }else if (fanstatus=="3"){
                            fanstatus3()
                            fanstatus_num=3

                        }

                        if (on_off_status=="0"){
                            coler_on_off.isChecked=false
                            coler_on_off.setBackgroundResource(R.drawable.coler_off)
                        }else if (on_off_status=="1"){
                            coler_on_off.isChecked=true
                            coler_on_off.setBackgroundResource(R.drawable.coler_on)
                        }


                    }catch (e:Exception){
                        println(e)

                    }


                }




                if (!isRefreshing) {
//                    isRefreshing = true
                    thread {

                        try {
                            requireActivity().runOnUiThread {
                                update_statuses_ui()
                            }

//                            fun isConnectedToWifi(context: Context): Boolean {
//                                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//                                val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
//                                return networkInfo?.type == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected
//                            }
//                            val wifiManager = requireContext().applicationContext.getSystemService(
//                                Context.WIFI_SERVICE) as WifiManager
//                            val wifiInfo = wifiManager.connectionInfo
//                            val ssid = wifiInfo.ssid
//                            val db_ssid = setting_network_db.getInstance(requireContext()).get_from_db_network_manual(1)?.modem_ssid
//
//                            if (isConnectedToWifi(requireContext()) && ssid.replace("\"", "") ==db_ssid ){
//                                var is_ok =true
//
//                                println(is_ok)
//                                isRefreshing = false
//                                handler.post{
//                                    if(is_ok){
//                                        requireActivity().runOnUiThread {
//                                            update_statuses_ui()
//                                        }
//
////                                                    Thread{
////                                                        update_statuses_ui()
////
////                                                    }
////                                        temp_disconnected.visibility=View.GONE
//
//                                        temp_layout.alpha = 0f
//                                        temp_layout.visibility = View.VISIBLE
//
//                                        temp_layout.animate()
//                                            .alpha(1f)
//                                            .setDuration(1200)
//                                            .setListener(null)
//                                    }else{
//
////                                        temp_disconnected.alpha = 0f
////                                        temp_disconnected.visibility = View.VISIBLE
//
////                                        temp_disconnected.animate()
////                                            .alpha(1f)
////                                            .setDuration(1200)
////                                            .setListener(null)
//
//
//                                    }
////
//
//                                }
//
//
//                            }else{
//                                requireActivity().runOnUiThread{
//
//                                    Toast.makeText(requireContext(), "Connect to a Griffin Network", Toast.LENGTH_SHORT).show()
//                                }
//                            }


                        }catch (e:Exception){
                            println(e)
                        }

//                            update_statuses_ui()
                    }
                }







                update_statuses_ui()

                fun set_all_status(newMood:String?,newFanStatus:String?,newOnOffStatus:String?,newCurrentSeekbarTemp:String?){
                    newMood?.let { mood = it }
                    newFanStatus?.let { fanstatus = it }
                    newOnOffStatus?.let { on_off_status = it }
                    newCurrentSeekbarTemp?.let { current_seekbar_temp = it }

                }

                //                mood= termostats_in_room[0].mood.toString()
                //                fanstatus= termostats_in_room[0].fan_status.toString()
                //                on_off_status= termostats_in_room[0].on_off.toString()









                current_seekbar_temp=circularSeekBar.currentSelectedIndex






                fun SeekBarStatus():String{
                    return circularSeekBar.currentSelectedIndex
                }







                val timerRunnable = Runnable {
                    // اینجا کدی را اجرا کنید که می‌خواهید پس از 6 ثانیه از آخرین تغییر متغیرها انجام شود
                    // مثلا:

                    println(SeekBarStatus())
                    println(on_off_status)
                    println(fanstatus)
                    println(mood)

                    Thread{
                        try {
                            udp_thermostat(this,current_thermostat,current_thermostat.mac,mood,SeekBarStatus(),fanstatus,on_off_status,current_thermostat.ip)

                        }catch (e:Exception){
                            println()
                        }finally {

                            requireActivity().runOnUiThread {

                                update_statuses_ui()
                            }
//                                        handler.post{
////                                    Thread.sleep(2000)
//
//                                            update_statuses_ui()
//                                        }
//                            is_doing = false


                        }
                    }.start()



//                        update_statuses_ui()


                }
                fun resetTimer() {

                    // حذف هر تایمر قبلی اگر وجود داشته باشد
//                    is_doing=true
                    handler.removeCallbacks(timerRunnable)

                    // اضافه کردن تایمر جدید برای اجرای کد پس از 6 ثانیه
                    handler.postDelayed(timerRunnable, delayMillis2.toLong())
                }









                winterside.setOnClickListener{
                    println("mod  changed 0 ")
                    // اگر گزینه 1 انتخاب شد

                    // تغییر تصویر زمینه به تصویر مرتبط با گزینه 1
                    winterside.setBackgroundResource(R.drawable.winter_side_on);
                    summerside.setBackgroundResource(R.drawable.summer_side_off);
                    mood="0"
                    resetTimer()
                    println("A")

                }
                summerside.setOnClickListener{
                    println("mod  changed 1 ")
                    // اگر گزینه 2 انتخاب شد
                    // تغییر تصویر زمینه به تصویر مرتبط با گزینه 2
                    mood="1"
                    resetTimer()
                    println("B")
                    summerside.setBackgroundResource(R.drawable.summer_side_on);
                    winterside.setBackgroundResource(R.drawable.winter_side_off);
                }


                // تعیین عملکرد برای رادیو باتن‌ها
//                            radioGroup.setOnCheckedChangeListener { group, checkedId ->
//                                if (checkedId == R.id.radioOption1) {
//                                    println("mod  changed 0 ")
//                                    // اگر گزینه 1 انتخاب شد
//
//                                    // تغییر تصویر زمینه به تصویر مرتبط با گزینه 1
//                                    winterside.setBackgroundResource(R.drawable.winter_side_on);
//                                    summerside.setBackgroundResource(R.drawable.summer_side_off);
//                                    mood="0"
//                                    resetTimer()
//                                    println("A")
//                                } else if (checkedId == R.id.radioOption2) {
//                                    println("mod  changed 1 ")
//                                    // اگر گزینه 2 انتخاب شد
//                                    // تغییر تصویر زمینه به تصویر مرتبط با گزینه 2
//                                    mood="1"
//                                    resetTimer()
//                                    println("B")
//                                    summerside.setBackgroundResource(R.drawable.summer_side_on);
//                                    winterside.setBackgroundResource(R.drawable.winter_side_off);
//                                }
//                            }
                coler_on_off.setOnClickListener {
                    if (coler_on_off.isChecked){
                        coler_on_off.setBackgroundResource(R.drawable.coler_on)
                        on_off_status="1"
                        resetTimer()
                        println("C")

                    }else{
                        coler_on_off.setBackgroundResource(R.drawable.coler_off)
                        on_off_status="0"
                        resetTimer()
                        println("D")


                    }
                }




                change_fan_status.setOnClickListener {

                    when(fanstatus_num ){
                        0-> {
                            fanstatus1()
                            resetTimer()
                            println("E")
                            fanstatus_num+=1
                        }
                        1 ->{
                            fanstatus2()
                            resetTimer()
                            println("F")
                            fanstatus_num+=1
                        }
                        2->{
                            fanstatus3()
                            resetTimer()
                            println("G")
                            fanstatus_num+=1
                        }
                        3->{
                            fanstatus4()
                            resetTimer()
                            println("H")
                            fanstatus_num=0
                        }
                    }
                }



                circularSeekBar.setOnCircularSeekBarChangeListener(object :CircularSeekBar.OnCircularSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: CircularSeekBar?, progress: Int, fromUser: Boolean) {
                        // اینجا کدی که می‌خواهید هنگام تغییر مقدار سیکبار اجرا شود را قرار دهید.
                        // مثلاً اگر می‌خواهید مقدار جدید را نمایش دهید:


                        // اگر می‌خواهید کدی اجرا کنید که به وسایل هوشمند ارتباط برقرار کند:
                        // تابعی را اینجا فراخوانی کنید.
                        // به عنوان مثال: sendToSmartDevices(progress)
                    }

                    override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {
                        // وقتی کاربر شروع به جابجایی سیکبار می‌کند، این متد فراخوانی می‌شود.
                    }

                    override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {
                        // وقتی کاربر پایان دادن به جابجایی سیکبار می‌دهد، این متد فراخوانی می‌شود.
                        current_seekbar_temp=circularSeekBar.currentSelectedIndex
                        resetTimer()
                        println("F")

                    }
                })







            }catch (e:Exception){
                println(e)
            }



            dialog.show()
        }



        val room_db_helper = rooms_db.getInstance(requireContext())
        val light_helper = light_db.getInstance(requireContext())
        val fan_db_helper = fan_db.getInstance(requireContext())
        val plug_db_helper = plug_db.getInstance(requireContext())
        val curtain_db_helper = curtain_db.getInstance(requireContext())
        val valve_db_helper = valve_db.getInstance(requireContext())
        val termostat_db_helper = Temperature_db.getInstance(requireContext())



        val all_rooms = room_db_helper.getAllRooms()
        if (all_rooms.isNotEmpty()){
            var current_index = 0
            var current_room = all_rooms[0]
            var rooms_count= all_rooms.count()





            add_button.setOnClickListener {

                val dialog = Dialog(requireContext())

                // حذف گوشه‌ها و پس‌زمینه دیالوگ
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                // تنظیم لایه دلخواه برای دیالوگ
                dialog.setContentView(R.layout.laerning_device_connecting)


                dialog.setCanceledOnTouchOutside(false)


                val connectingText = dialog.findViewById<TextView>(R.id.connecting_text)
                val lottieAnimationView = dialog.findViewById<LottieAnimationView>(R.id.lottieAnimationView)

// نقاط را به تدریج اضافه و حذف کنید
                val handler = Handler(Looper.getMainLooper())
                var dotCount = 0

// حلقه برای مدیریت انیمیشن
                val runnable = object : Runnable {
                    override fun run() {
                        // تعداد نقاط را تغییر دهید
                        dotCount = (dotCount + 1) % 4 // 0 تا 3 نقاط
                        val dots = ".".repeat(dotCount)
                        connectingText.text = "Connecting$dots"
                        handler.postDelayed(this, 550) // هر 500 میلی‌ثانیه
                    }
                }

// انیمیشن را شروع کنید
                handler.post(runnable)


                dialog.show()

                connectToWiFiAndPerformAction(
                    context = requireContext(),
                    ssid = "Griffin_V3",
                    password = null,
                    onConnected = {
                        requireActivity().runOnUiThread {

//                            Toast.makeText(requireContext(), "Connected to Wi-Fi!", Toast.LENGTH_SHORT).show()


                            Thread.sleep(400)



                            try {

                                println("1111111111111111111111111")

                                if (true ){
                                    Thread{
                                        try {
//                                        UdpListener8089.pause()
                                            // اتصال به سرور
                                            val ip ="192.168.4.1"
                                            val port = 80
                                            val network_db=setting_network_db.getInstance(requireContext())
                                            val network=network_db.get_from_db_network_manual(1)
                                            var step = 0

                                            var myip= checkIP(requireContext())
                                            println("22222222222222")


                                            val socket = Socket(ip, port)
                                            println("Connected to server.")

                                            val timeout = 5000 // 5 ثانیه زمان انتظار برای دریافت پاسخ
                                            val retryDelay = 1000L // 1 ثانیه تأخیر برای تلاش مجدد

                                            try {
                                                // تنظیم timeout برای انتظار دریافت پاسخ
                                                socket.soTimeout = timeout

                                                // ارسال پیام به سرور
                                                val output = OutputStreamWriter(socket.getOutputStream())
                                                val message = "ssid:${network!!.modem_ssid}~>pswd:${network!!.modem_password}~>$myip" // پیام خود را وارد کن
                                                Thread.sleep(300)
                                                output.write("$message\n")
                                                output.flush()
                                                println("Message sent: $message")

                                                // گوش دادن برای دریافت پاسخ
                                                val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                                                val response = input.readLine()
                                                println(response)

                                                if (response != null) {
                                                    println("Received response: $response")
                                                    socket.close()

                                                    val datbaseHelper=light_db.getInstance(requireContext())
                                                    var current_items=datbaseHelper.getAllLights()
                                                    var current_items_number=datbaseHelper.getAllLights()
                                                    val uniqueMacModels = current_items_number.mapNotNull { it?.mac }.toSet()

                                                    // تعداد مدل‌های منحصر به فرد
                                                    val numberOfUniqueMacModels = uniqueMacModels.count() +1

                                                    //  main code
                                                    requireActivity().runOnUiThread {

                                                        var receivedmessage_decoded=extract_response(response)
                                                        var macip= receivedmessage_decoded[0]
                                                        var type=receivedmessage_decoded[1]
                                                        var pole_num=receivedmessage_decoded[2]

//                                                Toast.makeText(requireContext(), macip, Toast.LENGTH_SHORT).show()
//                                                Toast.makeText(requireContext(), type, Toast.LENGTH_SHORT).show()
//                                                Toast.makeText(requireContext(), pole_num, Toast.LENGTH_SHORT).show()
                                                        println(type)
                                                        println(pole_num)

                                                        if (type=="Lght"){
                                                            val current_pole = 1

                                                            val room_devices_dbHandler = room_devices_db.getInstance(requireContext())
                                                            val current_devices = room_devices_dbHandler.getRoomByRoomIdAndHome(current_room!!.id.toString(), "home")
                                                            val current_room_devices = current_devices!!.room_devices!!.split(",").toMutableList()
                                                            for (i in 1..pole_num.toInt()){

                                                                val updated_light=Light()
                                                                updated_light.ip="192.168.1.1"
                                                                updated_light.mac=macip

                                                                updated_light.status="off"
                                                                println(i)
                                                                if (i<9){

                                                                    updated_light.sub_type="000${i}"
                                                                }else if (i==9 ){
                                                                    updated_light.sub_type="0010"

                                                                }else if (i>9 ){
                                                                    updated_light.sub_type="00${i}"

                                                                }

                                                                updated_light.Lname="$numberOfUniqueMacModels: ${pole_num.toInt().toString()} pole: $i"
                                                                datbaseHelper.set_to_db_light(updated_light)
//                                                            counted=counted+1
                                                                println(updated_light.Lname)
                                                                println(updated_light.sub_type)


                                                                current_room_devices.add("L:${light_helper.getLightsByLname(updated_light.Lname).id}")

                                                            }

                                                            current_devices.room_devices=current_room_devices.joinToString (separator = ",")
                                                            room_devices_dbHandler.updateRoomById(current_devices.id,current_devices)




                                                            val current_devices2 = room_devices_dbHandler.getRoomByRoomIdAndHome(
                                                                current_room!!.id.toString(), "home")
                                                            sharedViewModel.update_current_devices(current_devices2!!.room_devices)
                                                            requireActivity().runOnUiThread {

                                                                lottieAnimationView.loop(false) // غیرفعال کردن تکرار انیمیشن
                                                                lottieAnimationView.setAnimation(R.raw.succes_learn) // تنظیم انیمیشن جدید
                                                                lottieAnimationView.playAnimation() // شروع اجرای انیمیشن

                                                                connectingText.text = "Success" // تنظیم متن

                                                                handler.removeCallbacks(runnable) // حذف کال‌بک‌های قبلی

                                                                handler.postDelayed({
                                                                    dialog.dismiss() // بستن دیالوگ بعد از 3 ثانیه
                                                                    println("This runs after 3 seconds!")
                                                                    val network_db_handler = setting_network_db.getInstance(requireContext()).get_from_db_network_manual(1)
                                                                    if (network_db_handler != null) {
                                                                        network_db_handler.modem_ssid?.let { it1 ->
                                                                            connectToWiFiAndPerformAction(
                                                                                context = requireContext(),
                                                                                ssid = it1,
                                                                                password = network_db_handler.modem_password,
                                                                                onConnected = {
                                                                                    Toast.makeText(
                                                                                        requireContext(),
                                                                                        "Connected to Main network",
                                                                                        Toast.LENGTH_SHORT
                                                                                    ).show()

                                                                                },
                                                                                onTimeout = {

                                                                                }
                                                                            )

                                                                        }


                                                                    }
                                                                }, 4000) // زمان 3 ثانیه

                                                            }


                                                        }
//                                                    else if (type=="Lsix"){

//
//                                                        val same_mac_to_learn= selectedItem.mac?.let { it1 ->
//                                                            database.getLightsByMacAddress(
//                                                                it1
//                                                            )
//                                                        }
//                                                        val same_mac_count=same_mac_to_learn!!.count()
//
//                                                        if (same_mac_to_learn != null) {
//                                                            var counted=0
//                                                            for (light in same_mac_to_learn.subList(0,pole_num.toInt()) ){
//
//
//                                                                val updated_light=Light()
//                                                                updated_light.ip=light!!.ip
//                                                                updated_light.mac=macip
//                                                                updated_light.room_name=selectedItem.room_name
//                                                                updated_light.status="off"
//                                                                if (counted<9){
//
//                                                                    updated_light.sub_type="000${counted+1}"
//                                                                }else if (counted==9 ){
//                                                                    updated_light.sub_type="0010"
//
//                                                                }else if (counted>9 ){
//                                                                    updated_light.sub_type="00${counted+1}"
//
//                                                                }
//
//                                                                updated_light.Lname=light.Lname
//                                                                database.updateLightById(light!!.id,updated_light)
//                                                                counted=counted+1
//                                                            }
//                                                            val six_workert_db= six_workert_db.getInstance(requireContext())
//                                                            six_workert_db.deleteRowsWithNullOrNewLocalName()
//                                                            val added_six_worker =six_workert()
//                                                            val six_worker_count = six_workert_db.getAllsix_workerts().count()
//                                                            added_six_worker.name="new Local " + ((six_worker_count)+1)
//                                                            added_six_worker.pole_num=pole_num.toInt().toString()
//                                                            added_six_worker.sub_type=",,,,,"
//                                                            added_six_worker.type=",,,,,"
//                                                            added_six_worker.status=",,,,,"
//                                                            added_six_worker.ip="192.168.1.1"
//                                                            added_six_worker.mac=macip
//
//                                                            added_six_worker.work_name=",,,,,"
//
//                                                            six_workert_db.set_to_db_six_workert(added_six_worker)
//                                                            SharedViewModel.update_six_worker_list(six_workert_db.getAllsix_workerts())
//
//                                                            requireActivity().runOnUiThread{
//                                                                Toast.makeText(requireContext(), "Learned", Toast.LENGTH_LONG).show()
//                                                                val learn_light_db=light_db.getInstance(requireContext())
//                                                                SharedViewModel.update_light_to_learn_list( learn_light_db.getAllLightsByRoomName(room!!.room_name))
//                                                                on_off_test_light_pupop.isEnabled = true
//                                                                selectedItem.mac=macip
//                                                                on_off_test_light_pupop.alpha=1f
//                                                                popupWindow.dismiss()
//                                                            }
//
//
//
//
//                                                        }
//
//
//
//                                                    }else if (type == "Fano") {
//                                                        if (pole_num.toInt() == 2){
//                                                            val same_mac_to_learn= selectedItem.mac?.let { it1 ->
//                                                                database.getLightsByMacAddress(
//                                                                    it1
//                                                                )
//                                                            }
//                                                            val same_mac_count=same_mac_to_learn!!.count()
//
//                                                            val updated_light=Light()
//                                                            updated_light.ip=same_mac_to_learn[0]!!.ip
//                                                            updated_light.mac=macip
//                                                            updated_light.room_name=selectedItem.room_name
//                                                            updated_light.status="off"
//                                                            updated_light.sub_type="0001"
//                                                            updated_light.Lname= same_mac_to_learn[0]?.Lname
//                                                            database.updateLightById(same_mac_to_learn[0]!!.id,updated_light)
//
//                                                            val database2= fan_db.getInstance(requireContext())
//                                                            val updated_fan= fan()
//                                                            updated_fan.ip=same_mac_to_learn[1]!!.ip
//                                                            updated_fan.mac=macip
//                                                            updated_fan.room_name=selectedItem.room_name
//                                                            updated_fan.status="0"
//                                                            updated_fan.subtype="0002"
//
//                                                            updated_fan.Fname= same_mac_to_learn[1]?.Lname
//                                                            database2.set_to_db_fan(updated_fan)
//
//
//                                                        }else if (pole_num.toInt() == 3){
//                                                            val same_mac_to_learn= selectedItem.mac?.let { it1 ->
//                                                                database.getLightsByMacAddress(
//                                                                    it1
//                                                                )
//                                                            }
//                                                            val same_mac_count=same_mac_to_learn!!.count()
//
//
//
//                                                            val updated_light=Light()
//                                                            updated_light.ip=same_mac_to_learn[0]!!.ip
//                                                            updated_light.mac=macip
//                                                            updated_light.room_name=selectedItem.room_name
//                                                            updated_light.status="off"
//                                                            updated_light.sub_type="0001"
//                                                            updated_light.Lname= same_mac_to_learn[0]?.Lname
//                                                            database.updateLightById(same_mac_to_learn[0]!!.id,updated_light)
//
//                                                            val updated_light2=Light()
//                                                            updated_light2.ip=same_mac_to_learn[1]!!.ip
//                                                            updated_light2.mac=macip
//                                                            updated_light2.room_name=selectedItem.room_name
//                                                            updated_light2.status="off"
//                                                            updated_light2.sub_type="0002"
//                                                            updated_light2.Lname= same_mac_to_learn[1]?.Lname
//                                                            database.updateLightById(same_mac_to_learn[1]!!.id,updated_light2)
//
//
//                                                            val database2= fan_db.getInstance(requireContext())
//                                                            val updated_fan= fan()
//                                                            updated_fan.ip=same_mac_to_learn[2]!!.ip
//                                                            updated_fan.mac=macip
//                                                            updated_fan.room_name=selectedItem.room_name
//                                                            updated_fan.status="0"
//                                                            updated_fan.subtype="0003"
//
//                                                            updated_fan.Fname= same_mac_to_learn[2]?.Lname
//                                                            database2.set_to_db_fan(updated_fan)
//
//
//                                                        }else if (pole_num.toInt() == 4){
//                                                            val same_mac_to_learn= selectedItem.mac?.let { it1 ->
//                                                                database.getLightsByMacAddress(
//                                                                    it1
//                                                                )
//                                                            }
//                                                            val same_mac_count=same_mac_to_learn!!.count()
//
//
//
//                                                            val updated_light=Light()
//                                                            updated_light.ip=same_mac_to_learn[0]!!.ip
//                                                            updated_light.mac=macip
//                                                            updated_light.room_name=selectedItem.room_name
//                                                            updated_light.status="off"
//                                                            updated_light.sub_type="0001"
//                                                            updated_light.Lname= same_mac_to_learn[0]?.Lname
//                                                            database.updateLightById(same_mac_to_learn[0]!!.id,updated_light)
//
//                                                            val updated_light2=Light()
//                                                            updated_light2.ip=same_mac_to_learn[1]!!.ip
//                                                            updated_light2.mac=macip
//                                                            updated_light2.room_name=selectedItem.room_name
//                                                            updated_light2.status="off"
//                                                            updated_light2.sub_type="0002"
//                                                            updated_light2.Lname= same_mac_to_learn[1]?.Lname
//                                                            database.updateLightById(same_mac_to_learn[1]!!.id,updated_light2)
//
//                                                            val updated_light3=Light()
//                                                            updated_light3.ip=same_mac_to_learn[2]!!.ip
//                                                            updated_light3.mac=macip
//                                                            updated_light3.room_name=selectedItem.room_name
//                                                            updated_light3.status="off"
//                                                            updated_light3.sub_type="0003"
//                                                            updated_light3.Lname= same_mac_to_learn[2]?.Lname
//                                                            database.updateLightById(same_mac_to_learn[2]!!.id,updated_light3)
//
//
//                                                            val database2= fan_db.getInstance(requireContext())
//                                                            val updated_fan= fan()
//                                                            updated_fan.ip=same_mac_to_learn[3]!!.ip
//                                                            updated_fan.mac=macip
//                                                            updated_fan.room_name=selectedItem.room_name
//                                                            updated_fan.status="0"
//                                                            updated_fan.subtype="0004"
//
//                                                            updated_fan.Fname= same_mac_to_learn[3]?.Lname
//                                                            database2.set_to_db_fan(updated_fan)
//
//
//                                                        }
//
//
//                                                        requireActivity().runOnUiThread{
//
//                                                            Toast.makeText(requireContext(), "Learned.. ", Toast.LENGTH_SHORT).show()
//                                                            val learn_light_db=light_db.getInstance(requireContext())
//                                                            SharedViewModel.update_light_to_learn_list( learn_light_db.getAllLightsByRoomName(room!!.room_name))
//                                                            on_off_test_light_pupop.isEnabled = true
//                                                            selectedItem.mac=macip
//                                                            on_off_test_light_pupop.alpha=1f
//                                                            popupWindow.dismiss()
//                                                        }
//
//                                                    }

                                                    }


//                                                UdpListener8089.resume()


                                                }else{
                                                    requireActivity().runOnUiThread {
//                                                    UdpListener8089.resume()
                                                        lottieAnimationView.loop(false)
                                                        lottieAnimationView.setAnimation(R.raw.failed_learn)
                                                        lottieAnimationView.playAnimation()
                                                        connectingText.setText("Failed")
                                                        handler.removeCallbacks(runnable)

                                                        handler.postDelayed({
                                                            // کاری که می‌خواهید انجام شود
                                                            dialog.dismiss()
                                                            println("This runs after 3 seconds!")
                                                            val network_db_handler = setting_network_db.getInstance(requireContext()).get_from_db_network_manual(1)
                                                            if (network_db_handler != null) {
                                                                network_db_handler.modem_ssid?.let { it1 ->
                                                                    connectToWiFiAndPerformAction(
                                                                        context = requireContext(),
                                                                        ssid = it1,
                                                                        password = network_db_handler.modem_password,
                                                                        onConnected = {
                                                                            Toast.makeText(
                                                                                requireContext(),
                                                                                "Connected to Main network",
                                                                                Toast.LENGTH_SHORT
                                                                            ).show()

                                                                        },
                                                                        onTimeout = {

                                                                        }
                                                                    )

                                                                }


                                                            }
                                                        }, 4000) // زمان بر حسب میلی‌ثانیه (3 ثانیه)
                                                    }

                                                }

                                            } catch (e: SocketTimeoutException) {
                                                println("Timeout reached, no response received for message.")
                                                requireActivity().runOnUiThread{

//                                                Toast.makeText(requireContext(), "Failed.. ", Toast.LENGTH_SHORT).show()

                                                    lottieAnimationView.loop(false)
                                                    lottieAnimationView.setAnimation(R.raw.failed_learn)
                                                    lottieAnimationView.playAnimation()
                                                    connectingText.setText("Failed")
                                                    handler.removeCallbacks(runnable)

                                                    handler.postDelayed({
                                                        // کاری که می‌خواهید انجام شود
                                                        dialog.dismiss()
                                                        println("This runs after 3 seconds!")
                                                        val network_db_handler = setting_network_db.getInstance(requireContext()).get_from_db_network_manual(1)
                                                        if (network_db_handler != null) {
                                                            network_db_handler.modem_ssid?.let { it1 ->
                                                                connectToWiFiAndPerformAction(
                                                                    context = requireContext(),
                                                                    ssid = it1,
                                                                    password = network_db_handler.modem_password,
                                                                    onConnected = {
                                                                        Toast.makeText(
                                                                            requireContext(),
                                                                            "Connected to Main network",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()

                                                                    },
                                                                    onTimeout = {

                                                                    }
                                                                )

                                                            }


                                                        }
                                                    }, 4000) // زمان بر حسب میلی‌ثانیه (3 ثانیه)

                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                requireActivity().runOnUiThread{

                                                    lottieAnimationView.loop(false)
                                                    lottieAnimationView.setAnimation(R.raw.failed_learn)
                                                    lottieAnimationView.playAnimation()
                                                    connectingText.setText("Failed")
                                                    handler.removeCallbacks(runnable)

                                                    handler.postDelayed({
                                                        // کاری که می‌خواهید انجام شود
                                                        dialog.dismiss()
                                                        println("This runs after 3 seconds!")
                                                        val network_db_handler = setting_network_db.getInstance(requireContext()).get_from_db_network_manual(1)
                                                        if (network_db_handler != null) {
                                                            network_db_handler.modem_ssid?.let { it1 ->
                                                                connectToWiFiAndPerformAction(
                                                                    context = requireContext(),
                                                                    ssid = it1,
                                                                    password = network_db_handler.modem_password,
                                                                    onConnected = {
                                                                        Toast.makeText(
                                                                            requireContext(),
                                                                            "Connected to Main network",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()

                                                                    },
                                                                    onTimeout = {

                                                                    }
                                                                )

                                                            }


                                                        }
                                                    }, 4000) // زمان بر حسب میلی‌ثانیه (3 ثانیه)
                                                }
                                            }
                                            // بستن ارتباط
                                            socket.close()
//                                        UdpListener8089.resume()
                                        }catch (e:Exception){
                                            println(e)
//                                        UdpListener8089.resume()
                                        }

                                    }.start()


                                }



                            } catch (e: Exception) {
                                e.printStackTrace()
                                println(e)
                                lottieAnimationView.loop(false)
                                lottieAnimationView.setAnimation(R.raw.failed_learn)
                                lottieAnimationView.playAnimation()
                                connectingText.setText("Failed")
                                handler.removeCallbacks(runnable)

                                handler.postDelayed({
                                    // کاری که می‌خواهید انجام شود
                                    dialog.dismiss()
                                    println("This runs after 3 seconds!")
                                    val network_db_handler = setting_network_db.getInstance(requireContext()).get_from_db_network_manual(1)
                                    if (network_db_handler != null) {
                                        network_db_handler.modem_ssid?.let { it1 ->
                                            connectToWiFiAndPerformAction(
                                                context = requireContext(),
                                                ssid = it1,
                                                password = network_db_handler.modem_password,
                                                onConnected = {
                                                    Toast.makeText(
                                                        requireContext(),
                                                        "Connected to Main network",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                },
                                                onTimeout = {

                                                }
                                            )

                                        }


                                    }
                                }, 4000) // زمان بر حسب میلی‌ثانیه (3 ثانیه)
//                            UdpListener8089.resume()
                            }






                            // ادامه عملیات دلخواه
                        }



                    },
                    onTimeout = {
                        lottieAnimationView.loop(false)
                        lottieAnimationView.setAnimation(R.raw.failed_learn)
                        lottieAnimationView.playAnimation()
                        connectingText.setText("Failed")
                        handler.removeCallbacks(runnable)

                        handler.postDelayed({
                            // کاری که می‌خواهید انجام شود
                            dialog.dismiss()
                            println("This runs after 3 seconds!")
                            val network_db_handler = setting_network_db.getInstance(requireContext()).get_from_db_network_manual(1)
                            if (network_db_handler != null) {
                                network_db_handler.modem_ssid?.let { it1 ->
                                    connectToWiFiAndPerformAction(
                                        context = requireContext(),
                                        ssid = it1,
                                        password = network_db_handler.modem_password,
                                        onConnected = {
                                            Toast.makeText(
                                                requireContext(),
                                                "Connected to Main network",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        },
                                        onTimeout = {

                                        }
                                    )

                                }


                            }
                        }, 4000) // زمان بر حسب میلی‌ثانیه (3 ثانیه)
                    }
                )





            }




            fun next_room_func(){

                sharedViewModel.update_light_ref_status("loading" )
                println("nexttttttttttttttttttttttttttttt")
                adapter=null

                if (current_index < rooms_count-1){

//                    val imageName=rooms[current_index+1]!!.room_image+"3"
//                    imageName?.let {
//                        val imageResource = resources.getIdentifier(it, "drawable", this.packageName)
//                        room_image_home.setBackgroundResource(imageResource)
//
//
//                    }
                    if (current_index < rooms_count){
                        current_index += 1
                    }else{
                        current_index = 0
                    }

                    current_room=all_rooms[current_index]
                    sharedViewModel.update_current_room(current_room)
//                    room_name_home.text=current_room!!.room_name

                }else{


//                    val imageName=rooms[0]!!.room_image+"3"
//                    imageName?.let {
//                        val imageResource = resources.getIdentifier(it, "drawable", this.packageName)
//                        room_image_home.setBackgroundResource(imageResource)
//
//
//                    }

                    current_index = 0

                    current_room=all_rooms[current_index]
                    sharedViewModel.update_current_room(current_room)
//                    room_name_home.text=current_room!!.room_name
                }





            }
            fun previous_room_func(){
                adapter=null
                sharedViewModel.update_light_ref_status("loading" )
                if (current_index > 0){

//                    val imageName=rooms[current_index-1]!!.room_image+"3"
//                    imageName?.let {
//                        val imageResource = resources.getIdentifier(it, "drawable", this.packageName)
//                        room_image_home.setBackgroundResource(imageResource)
//
//                    }

                    if (0 < current_index){
                        current_index -= 1
                    }else{
                        current_index = rooms_count
                    }
                    current_room=all_rooms[current_index]
                    sharedViewModel.update_current_room(current_room)
//                    room_name_home.text=current_room!!.room_name

                }else{


//                    val imageName=rooms[rooms_count-1]!!.room_image+"3"
//                    imageName?.let {
//                        val imageResource = resources.getIdentifier(it, "drawable", this.packageName)
//                        room_image_home.setBackgroundResource(imageResource)
//
//
//                    }
                    current_index = rooms_count-1

                    current_room=all_rooms[current_index]
                    sharedViewModel.update_current_room(current_room)
//                    room_name_home.text=current_room!!.room_name
                }



            }

            val roomImageHome = view.findViewById<ImageView>(R.id.imageView4)

// تعریف GestureDetector
            val gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
                private val SWIPE_THRESHOLD = 100  // کاهش مقدار به 100 برای حساسیت بیشتر
                private val SWIPE_VELOCITY_THRESHOLD = 100  // حساسیت برای سرعت بیشتر

                override fun onDown(e: MotionEvent): Boolean {
                    // اطمینان از اینکه حرکات شبیه به سوایپ شروع می‌شود
                    return true
                }

                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    if (e1 != null) {
                        if (Math.abs(velocityX) > Math.abs(velocityY) && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            // تشخیص حرکت به چپ یا راست بر اساس مقدار velocityX
                            if (e2.x - e1.x > SWIPE_THRESHOLD) {
                                previous_room_func()
                                return true
                            } else if (e1.x - e2.x > SWIPE_THRESHOLD) {
                                next_room_func()
                                return true
                            }
                        }
                    }
                    return false
                }
            })

// تنظیم OnTouchListener روی ImageView
            roomImageHome.setOnTouchListener { v, event ->
                if (event != null) {
                    // بررسی آیا لمس داخل ImageView انجام شده است
                    val isTouchInsideImageView = isTouchInsideView(event.x, event.y, roomImageHome)

                    if (isTouchInsideImageView) {
                        try {
                            gestureDetector.onTouchEvent(event)
                        } catch (e: Exception) {
                            println(e)
                        }
                    }
                }
                // برگرداندن true برای جلوگیری از ارسال TouchEvent به سایر ویوها
                true
            }


            val failedLoad = requireView().findViewById<TextView>(R.id.failed_refresh)


            sharedViewModel.update_current_room(current_room)
            val recyclerView: RecyclerView = requireView().findViewById(R.id.devices_recyceler)
            val gridLayoutManager = GridLayoutManager(requireContext(), 4) // تعداد ستون‌ها
            recyclerView.layoutManager = gridLayoutManager
            val prigress_loading = requireView().findViewById<ProgressBar>(R.id.prigress_loading)
            sharedViewModel.current_room.observe(viewLifecycleOwner, Observer { current_room ->

                println("sss")

                // بررسی اینکه current_room مقدار null نباشد
                if (current_room != null) {
                    val room_devices_dbHandler = room_devices_db.getInstance(requireContext())
                    val current_devices = room_devices_dbHandler.getRoomByRoomIdAndHome(current_room.id.toString(), "home")



                    // نمایش loading
                    prigress_loading.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE

                    Thread {

                        val ref = refresh_light(requireContext(), current_room)

                        handler.post {
                            sharedViewModel.update_light_ref_status("loading" )
                            if(ref){
                                sharedViewModel.update_light_ref_status("ok" )

                            }else{
                                sharedViewModel.update_light_ref_status("failed" )
                            }
                            // اجرای تغییرات در ترد اصلی (UI Thread)

//                            sharedViewModel.update_light_to_learn_list( learn_light_db.getAllLightsByRoomName(curent_room!!.room_name))

                            sharedViewModel.update_current_devices(current_devices!!.room_devices)

                        }

                    }.start()


                    sharedViewModel.light_ref_status.observe(viewLifecycleOwner, Observer { ref_status ->
                        when (ref_status) {
                            "ok" -> {
                                prigress_loading.visibility = View.GONE
                                recyclerView.visibility = View.VISIBLE
                                failedLoad.visibility = View.GONE
//                                learnLightDb = light_db.getInstance(requireContext())
//                                sharedViewModel.update_light_to_learn_list(learnLightDb.getAllLightsByRoomName(room!!.room_name))
                            }
                            "failed" -> {
                                failedLoad.visibility = View.VISIBLE
                                prigress_loading.visibility = View.GONE
                                Handler(Looper.getMainLooper()).postDelayed({
                                    failedLoad.visibility = View.GONE

                                    prigress_loading.visibility = View.GONE
                                    recyclerView.visibility = View.VISIBLE
                                    failedLoad.visibility = View.GONE
//                                    learnLightDb = light_db.getInstance(requireContext())
//                                    SharedViewModel.update_light_to_learn_list(learnLightDb.getAllLightsByRoomName(room!!.room_name))

                                }, 2000) 
                            }
                            "loading" -> {
                                prigress_loading.visibility = View.VISIBLE
                                recyclerView.visibility = View.GONE
                                failedLoad.visibility = View.GONE
                            }
                        }




                    })
                    sharedViewModel.current_devices.observe(viewLifecycleOwner , Observer { current_list ->

//                        recyclerView.visibility = View.VISIBLE
//                        prigress_loading.visibility = View.GONE

                        // حذف آداپتر قبلی

                        recyclerView.adapter = null

                        // مقداردهی آداپتر جدید
                        println( current_devices!!.room_devices!!.split(",").toMutableList())
                        adapter = current_list!!.split(",").toMutableList()?.let {
                            moving_adapter(
                                it.toMutableList(),requireContext()) { type, dbId, selected_item, status ->
                                Log.d("ClickedItem", "Type: $type, DB ID: $dbId")
                                println(status + "dsfsfsfsf")

                                if (adapter!!.isEditMode){
                                    println("Ediiiiiiiit")

                                    val dialog = Dialog(requireContext())
                                    val dialog2 = Dialog(requireContext())

                                    // حذف گوشه‌ها و پس‌زمینه دیالوگ
                                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                                    dialog2.window?.setBackgroundDrawableResource(android.R.color.transparent)

                                    // تنظیم لایه دلخواه برای دیالوگ
                                    dialog.setContentView(R.layout.edit_device)
                                    dialog2.setContentView(R.layout.delete_all_or_pole)


                                    val edit_name_pupop = dialog.findViewById<EditText>(R.id.edit_name_pupop)
                                    val ok_name_pupop = dialog.findViewById<Button>(R.id.ok_name_pupop)
                                    val delete_light_pupop = dialog.findViewById<Button>(R.id.delete_light_pupop)


                                    val delete_all = dialog2.findViewById<Button>(R.id.yes_delete)
                                    val delete_pole = dialog2.findViewById<Button>(R.id.cancel_delete)



                                    dialog.show()

                                    when (type) {
                                        "L" -> {
                                            val old_name =edit_name_pupop.text.toString()
                                            val current_device = light_helper.getLightsByid(dbId.toString())
                                            edit_name_pupop.setText(current_device.Lname)
                                            ok_name_pupop.setOnClickListener {
                                                current_device.Lname=edit_name_pupop.text.toString()
                                                if(old_name.trim() != edit_name_pupop.text.toString().trim()){



                                                    val light=Light()

                                                    val same_counter = light_helper.getNumberOfItemsByLname(edit_name_pupop.text.toString().trim()).toInt()
                                                    if ( same_counter!= 0 ){


                                                        light.Lname=edit_name_pupop.text.toString().trim()+ " (${same_counter +1})"


                                                    }else{
                                                        light.Lname=edit_name_pupop.text.toString().trim()
                                                    }

                                                    light.mac=current_device.mac
                                                    light.status=current_device.status
                                                    light.room_name=current_device.room_name
                                                    light.sub_type=current_device.sub_type
                                                    light.ip=current_device.ip
                                                    light_helper.updateLightById(current_device.id,light)
                                                    if (old_name != null) {
                                                        println("renameeeddd")
                                                        rename_item_in_scenario("e",old_name, light.Lname!!,"light")
                                                    }
                                                    val room_devices_dbHandler = room_devices_db.getInstance(requireContext())
                                                    val current_devices = room_devices_dbHandler.getRoomByRoomIdAndHome(current_room.id.toString(), "home")
                                                    sharedViewModel.update_current_devices(current_devices!!.room_devices)

                                                }
                                            }

                                            delete_light_pupop.setOnClickListener {
                                                dialog.dismiss()
                                                dialog2.show()


                                            }
                                            delete_pole.setOnClickListener {


                                                val same_mac_count = light_helper.getLightsByMacAddress(current_device.mac).count()
                                                if (same_mac_count ==1){
                                                    light_helper.delete_from_db_light(current_device.id)

                                                    rename_item_in_scenario("d",old_name,old_name,"light")
                                                    val room_devices_dbHandler = room_devices_db.getInstance(requireContext())
                                                    val current_devices = room_devices_dbHandler.getRoomByRoomIdAndHome(current_room.id.toString(), "home")
                                                    val new = current_devices!!.room_devices!!.split(",").toMutableList()
                                                    new.remove("L:$dbId")
                                                    current_devices.room_devices=new.joinToString(separator = ",")
                                                    room_devices_dbHandler.updateRoomById(current_devices.id,current_devices)
                                                    sharedViewModel.update_current_devices(current_devices!!.room_devices)

                                                    val sixworkerdb = six_workert_db.getInstance(requireContext())
                                                    val sixworker_list = sixworkerdb.getsix_workertsByMacAddress(current_device.mac)
                                                    if (sixworker_list.count() >0 ){
                                                        sixworkerdb.delete_from_db_six_workert(sixworker_list[0]?.id)
                                                    }

                                                }else{
                                                    light_helper.delete_from_db_light(current_device.id)

                                                    rename_item_in_scenario("d",old_name,old_name,"light")
                                                    val room_devices_dbHandler = room_devices_db.getInstance(requireContext())
                                                    val current_devices = room_devices_dbHandler.getRoomByRoomIdAndHome(current_room.id.toString(), "home")
                                                    val new = current_devices!!.room_devices!!.split(",").toMutableList()
                                                    new.remove("L:$dbId")
                                                    current_devices.room_devices=new.joinToString(separator = ",")
                                                    room_devices_dbHandler.updateRoomById(current_devices.id,current_devices)
                                                    sharedViewModel.update_current_devices(current_devices!!.room_devices)
                                                    val sixworkerdb = six_workert_db.getInstance(requireContext())
                                                    val sixworker_list = sixworkerdb.getsix_workertsByMacAddress(current_device.mac)
                                                    if (sixworker_list.count() >0 ){
                                                        sixworkerdb.delete_from_db_six_workert(sixworker_list[0]?.id)
                                                    }
//

                                                }
                                                dialog2.dismiss()
                                                requireActivity().runOnUiThread{
                                                    Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            delete_all.setOnClickListener {

                                                val room_devices_dbHandler = room_devices_db.getInstance(requireContext())
                                                val current_devices = room_devices_dbHandler.getRoomByRoomIdAndHome(current_room.id.toString(), "home")
                                                val new = current_devices!!.room_devices!!.split(",").toMutableList()

                                                val same_mac_lights = light_helper.getLightsByMacAddress(current_device.mac)
                                                for (light in same_mac_lights){
                                                    light_helper.delete_from_db_light(light!!.id)
                                                    if (light != null) {
                                                        new.remove("L:${light.id}")
                                                        println(light.Lname)
                                                        light.Lname?.let { it1 ->
                                                            rename_item_in_scenario("d",
                                                                it1,it1,"light")
                                                        }
                                                    }

                                                }
                                                val sixworkerdb = six_workert_db.getInstance(requireContext())
                                                val sixworker_list = sixworkerdb.getsix_workertsByMacAddress(current_device.mac)
                                                if (sixworker_list.count() >0 ){
                                                    sixworkerdb.delete_from_db_six_workert(sixworker_list[0]?.id)
                                                }

                                                current_devices.room_devices=new.joinToString(separator = ",")
                                                room_devices_dbHandler.updateRoomById(current_devices.id,current_devices)
                                                sharedViewModel.update_current_devices(current_devices!!.room_devices)
                                                dialog2.dismiss()
                                                requireActivity().runOnUiThread{
                                                    Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show()
                                                }

                                            }


                                        }

                                        "F" -> {

                                        }

                                        "P" -> {

                                        }

                                        "V" -> {

                                        }
                                        "T" -> {

                                        }

                                        else -> {}
                                    }

                                    dialog.setCanceledOnTouchOutside(false)

                                }else{


                                    when (type) {
                                        "L" -> {
                                            if (status == "1") {
                                                selected_item.setBackgroundResource(R.drawable.light_background_on)
                                            } else {
                                                selected_item.setBackgroundResource(R.drawable.light_background_off)
                                            }
                                            requestQueue.add(Quadruple(selected_item, status, type, dbId.toString()))
                                            if (!isProcessing) {
                                                processQueue()
                                            }
                                        }

                                        "F" -> {
                                            if (status == "0") {
                                                selected_item.setBackgroundResource(R.drawable.fan_background_on)
                                            } else {
                                                selected_item.setBackgroundResource(R.drawable.fan_background_off)
                                            }
                                        }

                                        "P" -> {
                                            if (status == "0") {
                                                selected_item.setBackgroundResource(R.drawable.plug_background_on)
                                            } else {
                                                selected_item.setBackgroundResource(R.drawable.plug_background_off)
                                            }
                                        }

                                        "V" -> {
                                            if (status == "0") {
                                                selected_item.setBackgroundResource(R.drawable.valve_background_on)
                                            } else {
                                                selected_item.setBackgroundResource(R.drawable.valve_background_off)
                                            }
                                        }
                                        "T" -> {
                                            println(dbId)
                                            showPopup_termostat(dbId.toString(), selected_item)
//                                        if (status == "0") {
//                                            selected_item.setBackgroundResource(R.drawable.valve_background_on)
//                                        } else {
//                                            selected_item.setBackgroundResource(R.drawable.valve_background_off)
//                                        }
                                        }

                                        else -> {}
                                    }

                                }




                            }
                        }

                        // تنظیم آداپتر جدید
                        recyclerView.adapter = adapter



                    })


                    val editButton: Button = requireView().findViewById(R.id.edit_btn)

                    editButton.setOnClickListener {
                        if (adapter != null) {
                            adapter!!.isEditMode = !adapter!!.isEditMode
                            if (!adapter!!.isEditMode){

                                val new_sort = adapter!!.getItemsOrder()

                                if (sharedViewModel.current_devices.value != new_sort ){
                                    if (current_devices != null) {
                                        current_devices.room_devices=new_sort
                                    }
                                    if (current_devices != null) {
                                        room_devices_dbHandler.updateRoomById(current_devices.id,current_devices)
                                    }



                                }





                                sharedViewModel.update_current_devices(current_devices!!.room_devices)
                            }

                        }

                        adapter?.notifyDataSetChanged()
                    }



//                    Thread {
//                        val ref = refresh_light(requireContext(), current_room)
//
//                        if (ref) {
//                            requireActivity().runOnUiThread {
//                                // مخفی کردن لودینگ و نمایش RecyclerView
//
//
//                                // دکمه Edit
//
//                            }
//                        }
//                    }.start()
                }
            })


// تعریف ItemTouchHelper.Callback
            val itemTouchHelperCallback = object : ItemTouchHelper.Callback() {
                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    val adapter = recyclerView.adapter as moving_adapter
                    return if (adapter.isEditMode) {
                        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                        val swipeFlags = 0
                        makeMovementFlags(dragFlags, swipeFlags)
                    } else {
                        makeMovementFlags(0, 0)
                    }
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition
                    (recyclerView.adapter as moving_adapter).onItemMove(fromPosition, toPosition)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // Swipe غیرفعال است
                }

                override fun isLongPressDragEnabled(): Boolean {
                    println("presssss")
                    return (recyclerView.adapter as moving_adapter).isEditMode
                }
            }

            // اضافه کردن ItemTouchHelper به RecyclerView
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(recyclerView)










        }else{


        }


    }

}

