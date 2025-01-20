package com.example.griffinmobile.mudels
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import com.example.griffinmobile.database.*
//import com.example.griffinmobile.fragment.setting_setting.setting_admin
import java.util.*
import kotlin.collections.ArrayList

class SmsReceiver : BroadcastReceiver() {
    private lateinit var sharedViewModel: SharedViewModel

    private val requestQueue: Queue<Pair<Any, String>> = LinkedList()
    private var isProcessing = false
    private val handler = Handler(Looper.getMainLooper())
    private val delayMillis: Long = 400

    fun calculatePercentage(part: Double, whole: Double): Double {
        // چک کردن اینکه کله صفر نباشد
        if (whole == 0.0) {
            throw IllegalArgumentException("عدد کله نمی‌تواند صفر باشد.")
        }
        // محاسبه درصد
        return (part / whole) * 100
    }
    fun sendSMS(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            println("پیام ارسال شد")
        } catch (e: Exception) {
            e.printStackTrace()
            println("خطا در ارسال پیام: ${e.message}")
        }
    }

    private fun processQueue(context: Context) {
        if (requestQueue.isEmpty()) {
            isProcessing = false
            return
        }

        val list = requestQueue.toList().toMutableList()

        if (list.size > 1) {
            // حذف همه آیتم‌ها به جز آخری
            list.subList(0, list.size - 1).clear()
        }

// پاک کردن صف و اضافه کردن آیتم باقی‌مانده از لیست
        requestQueue.clear()
        requestQueue.addAll(list)


        isProcessing = true
        val (device, status) = requestQueue.poll()
        var databasehelper=setting_simcard_accountsecurity_db.getInstance(context)
        val current_db=databasehelper.get_from_db_simcard_accountsecurity(1)

        val simcart_number_db = setting_simcard_accountsecurity_db.getInstance(context)
        val backup_numbers = simcart_number_db.get_from_db_simcard_accountsecurity(1)
        val backup_numbers_list = listOf(backup_numbers?.backup_number1.toString(),
            backup_numbers?.backup_number2.toString(),
            backup_numbers?.backup_number3.toString(), backup_numbers?.backup_number4.toString(), backup_numbers?.admin_number.toString()).filterNot { it.isNullOrBlank() }

        val sms_response_db = setting_simcard_messageresponse_db.getInstance(context)

        val sms_response = sms_response_db.get_from_db_simcard_message_response(1)

        Thread {
            try {
                when (device) {
                    is valve -> {
                       val is_pass =  udp_valve_sms(context,device,status)

                            if (current_db!!.smsanswer_on_off == "on"){
                                if (is_pass){
                                    for (phone_number in backup_numbers_list)
                                        if (sms_response != null) {
                                            sms_response.module_r?.let { sendSMS(phone_number, it) }
                                        }
                                }else{

                                }
                            }

                    }
                    is Plug -> {
                        val is_pass= udp_plug_sms(context,device,status)
                        if (current_db!!.smsanswer_on_off == "on"){
                            if (is_pass){
                                for (phone_number in backup_numbers_list)
                                    if (sms_response != null) {
                                        sms_response.module_r?.let { sendSMS(phone_number, it) }
                                    }
                            }else{

                            }
                        }

                    }
                    is fan ->{
                        val is_pass = udp_fan_sms(context,device,status)

                        if (current_db!!.smsanswer_on_off == "on"){
                            if (is_pass){
                                for (phone_number in backup_numbers_list)
                                    if (sms_response != null) {
                                        sms_response.module_r?.let { sendSMS(phone_number, it) }
                                    }
                            }else{

                            }
                        }
                    }
                    is Thermostst ->{
                        val is_pass = udp_thermostat_sms(context,device,status)
                        if (current_db!!.smsanswer_on_off == "on"){
                            if (is_pass){
                                for (phone_number in backup_numbers_list)
                                    if (sms_response != null) {
                                        sms_response.module_r?.let { sendSMS(phone_number, it) }
                                    }
                            }else{

                            }
                        }


                    }
                    is curtain ->{

                        val is_pass = udp_curtain(context,device,status)
                        if (current_db!!.smsanswer_on_off == "on"){
                            if (is_pass){
                                for (phone_number in backup_numbers_list)
                                    if (sms_response != null) {
                                        sms_response.module_r?.let { sendSMS(phone_number, it) }
                                    }
                            }else{

                            }
                        }
                    }
                    is Light ->{
                        val is_pass = udp_light_sms(context,device,status)
                        if (current_db!!.smsanswer_on_off == "on"){
                            if (is_pass){
                                for (phone_number in backup_numbers_list)
                                    if (sms_response != null) {
                                        sms_response.module_r?.let { sendSMS(phone_number, it) }
                                    }
                            }else{

                            }
                        }

                    }
                    is scenario ->{

                        try {




                            println(status)
                            var scenario_side="user"

                            val selectedItem = device

                            if (scenario_side=="user"){

                                println("userrrr")




                                if ((status != "bussy") || (status==null)){
                                    if (selectedItem != null) {

                                    }
                                    //                                            popupWindow.showAtLocation(requireViewById(R.layout), Gravity.CENTER, 0, 0)


                                    Thread{
                                        UdpListener8089.activity.runOnUiThread {
                                            UdpListener8089.sharedViewModel.update_is_doing("bussy")
                                        }

                                        //                                                loading_view.increaseProgress(0.0)

                                        val light_database_handler=light_db.getInstance(
                                            UdpListener8089.context
                                        )
                                        val thermostat_database_handler= Temperature_db.getInstance(
                                            UdpListener8089.context
                                        )
                                        val curtain_database_handler= curtain_db.getInstance(
                                            UdpListener8089.context
                                        )
                                        val valve_database_handler= valve_db.getInstance(
                                            UdpListener8089.context
                                        )
                                        val fan_database_handler= fan_db.getInstance(
                                            UdpListener8089.context
                                        )
                                        val plug_database_handler= plug_db.getInstance(
                                            UdpListener8089.context
                                        )

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


                                        val scenario_database_handler= com.example.griffinmobile.database.scenario_db.Scenario_db.getInstance(
                                            UdpListener8089.context
                                        )
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


                                            val musicPlayer = Music_player.getInstance(
                                                UdpListener8089.context
                                            )
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
                                            val sorted_lights =
                                                UdpListener8089.getLightsGroupedBySameMac(
                                                    current_light_to_change
                                                )
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

                                                        val send=udp_light_scenario(
                                                            UdpListener8089.context!!,same)


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

                                            val sorted_plugs =
                                                UdpListener8089.getPlugsGroupedBySameMac(
                                                    current_plug_to_change
                                                )


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

                                                        val send=udp_plug_scenario(
                                                            UdpListener8089.context,same)


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
                                            val sorted_valve =
                                                UdpListener8089.getvalvesGroupedBySameMac(
                                                    current_valve_to_change
                                                )
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

                                                        val send=udp_valve_for_scenario(
                                                            UdpListener8089.context,same)


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
                                            val sorted_curtain =
                                                UdpListener8089.getcurtainGroupedBySameMac(
                                                    current_curtain_to_change
                                                )

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

                                                        val send= udp_curtain_for_scenario(
                                                            UdpListener8089.context!!,same[0],same[0].status)


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

                                                        val send= udp_thermostat_for_scenario(
                                                            UdpListener8089.context!!,same[0],same[0].mac,same[0].mood,same[0].temperature,same[0].fan_status,same[0].on_off,same[0].ip)


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
                                            val sorted_fan =
                                                UdpListener8089.getfansGroupedBySameMac(
                                                    current_fan_to_change
                                                )
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

                                                        val send= udp_fan_for_scenario(
                                                            UdpListener8089.context,same)


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
                                            val sorted_lights =
                                                UdpListener8089.getLightsGroupedBySameMac(
                                                    current_light_to_change1
                                                )
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

                                                        val send=udp_light_scenario(
                                                            UdpListener8089.context!!,same)


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


                                                        val send= udp_thermostat_for_scenario(
                                                            UdpListener8089.context!!,same[0],same[0].mac,same[0].mood,same[0].temperature,same[0].fan_status,same[0].on_off,same[0].ip)


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

                                            val sorted_curtain =
                                                UdpListener8089.getcurtainGroupedBySameMac(
                                                    current_curtain_to_change
                                                )

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

                                                        val send = udp_curtain_for_scenario(
                                                            UdpListener8089.context!!, same[0], same[0].status)


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

                                            val sorted_plugs =
                                                UdpListener8089.getPlugsGroupedBySameMac(
                                                    current_plug_to_change
                                                )


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

                                                        val send=udp_plug_scenario(
                                                            UdpListener8089.context,same)


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
                                            val sorted_valve =
                                                UdpListener8089.getvalvesGroupedBySameMac(
                                                    current_valve_to_change
                                                )
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

                                                        val send= udp_valve_for_scenario(
                                                            UdpListener8089.context,same)


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
                                            val sorted_fan =
                                                UdpListener8089.getfansGroupedBySameMac(
                                                    current_fan_to_change
                                                )
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

                                                        val send= udp_fan_for_scenario(
                                                            UdpListener8089.context,same)


                                                        if (send){
                                                            Handler(Looper.getMainLooper()).post {

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







                                        UdpListener8089.activity.runOnUiThread {
                                            UdpListener8089.sharedViewModel.update_is_doing("not bussy")
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

                                        println("done")
                                        println(all_scenario_count)
                                        println(fan_to_do.count()+valve_to_do.count()+plug_to_do.count()+curtain_to_do.count()+thermostat_to_do.count()+light_to_do.count())
                                        if (0==fan_to_do.count()+valve_to_do.count()+plug_to_do.count()+curtain_to_do.count()+thermostat_to_do.count()+light_to_do.count()){


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
                                            println(final_list_to_show)


                                        }


                                        val percent_done = light_scenario_done.count() + thermostat_scenario_done.count() +  curtain_scenario_done.count() + valve_scenario_done.count()+fan_scenario_done.count() + plug_scenario_done.count() + music_scenario_done.count()




                                        if (current_db!!.smsanswer_on_off == "on"){
                                            for (phone_number in backup_numbers_list)
                                                if (sms_response != null) {
                                                    sendSMS(phone_number,sms_response.scenario_r+" "+ calculatePercentage(percent_done.toDouble(), all_scenario_count.toDouble())+"%" )
                                                }

                                        }

                                    }.start()


                                }else{

                                    Toast.makeText(UdpListener8089.context, "please Wait...", Toast.LENGTH_SHORT).show()
                                }



                            }









                        }catch (e:Exception){
                            println(e)
                        }




                    }
                    else ->{}
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                handler.postDelayed({
                    processQueue(context)
                }, delayMillis)
            }
        }.start()
    }



    override fun onReceive(context: Context, intent: Intent) {
        // بررسی اینکه آیا پیام از نوع SMS است
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras
            if (bundle != null) {
                val pdus = bundle["pdus"] as Array<*>
                for (pdu in pdus) {
                    val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                    var sender = smsMessage.displayOriginatingAddress
                    sender = sender
                    if (sender.startsWith("+98")){
                        sender = sender.toString().replace("+98","0")

                    }
                    try {
                        val messageBody = smsMessage.messageBody

                        // پردازش پیام SMS
                        println( "received from: $sender")
                        println("Message: $messageBody")

                        val itrator = listOf<Int>(4,1,3,2,8,7,5,6)
                        var current_itrate = 0
                        var  current_state =0
                        val all_count = 8
                        var smsMessage_scii = messageBody.map{it.code}.toMutableList()

                        println(smsMessage_scii.count())
                        for (scii in smsMessage_scii){
                            if (current_state<smsMessage_scii.count()){

                                smsMessage_scii[current_state]=(scii-itrator[current_itrate])
                                current_state+=1
                                current_itrate+=1
                                if (current_itrate==8){
                                    current_itrate=0
                                }
                            }

                        }

                        val final_cmd = smsMessage_scii.map { it.toChar() }.joinToString("")

                        println(final_cmd)
///////////////////////////////  decoded message  //////////////////////////
                        val final_cmd_splited_1=final_cmd.split("***")
                        val recived_date = final_cmd_splited_1[0].toInt()
                        val recived_hour = final_cmd_splited_1[1].toInt()
                        val recived_minute = final_cmd_splited_1[2].toInt()
                        val recived_pass = final_cmd_splited_1[3]
                        val recived_cmd = final_cmd_splited_1[4]


                        val recived_cmd_splited = final_cmd_splited_1[4].split("~>")
                        val cmd_type = recived_cmd_splited[0]
                        val cmd_mac = recived_cmd_splited[1]

                        var cmd_status:String? = null
                        if (recived_cmd_splited.count()>2){
                            cmd_status = recived_cmd_splited[2]
                        }


                        //////////////  date  ////////////////////
                        val calendar = Calendar.getInstance()
                        val hour = calendar.get(Calendar.HOUR_OF_DAY) // ساعت در قالب 24 ساعته
                        val minute = calendar.get(Calendar.MINUTE)
                        val day = calendar.get(Calendar.DAY_OF_MONTH)


                        //////////////////// user pass //////////////////////////
                        val sim_security = setting_simcard_security_db.getInstance(context)
                        val username = sim_security.get_from_db_simcard_security(1)?.username
                        val pass = sim_security.get_from_db_simcard_security(1)?.password
                        //////////////////  phone Numbers  ///////////////////
                        val simcart_number_db = setting_simcard_accountsecurity_db.getInstance(context)
                        val backup_numbers = simcart_number_db.get_from_db_simcard_accountsecurity(1)

                        val backup_numbers_list = listOf(backup_numbers?.backup_number1.toString(),
                            backup_numbers?.backup_number2.toString(),
                            backup_numbers?.backup_number3.toString(), backup_numbers?.backup_number4.toString(), backup_numbers?.admin_number.toString())

                        //////////////   module databases  ////////////////////

                        val curtain_db_handler=curtain_db.getInstance(context)
                        val door_db_handler=door_db.getInstance(context)
                        val fan_db_handler=fan_db.getInstance(context)
                        val light_db_handler=light_db.getInstance(context)
                        val plug_db_handler=plug_db.getInstance(context)
                        val scenario_db_handler=scenario_db.Scenario_db.getInstance(context)
                        val security_db_handler_handler=security_db.getInstance(context)

                        val temperature_db_handler=Temperature_db.getInstance(context)
                        val valve_db_handler=valve_db.getInstance(context)


                        println(cmd_status)
                        if (cmd_status!=null){


                            val curtain=curtain_db_handler.getcurtainsByMacAddress(cmd_mac)
                            val door= door_db_handler.getDoorsByMacAddress(cmd_mac)
                            val fan = fan_db_handler.getfansByMacAddress(cmd_mac)
                            val light = light_db_handler.getLightsByMacAddress(cmd_mac)
                            val plug = plug_db_handler.getPlugsByMacAddress(cmd_mac)
                            val temperature = temperature_db_handler.getThermostatsByMac(cmd_mac)
                            val valve = valve_db_handler.getvalvesByMacAddress(cmd_mac)

                            println(curtain)
                            println(door)
                            println(fan)
                            println(light)
                            println(plug)




                            println("          "+recived_pass)
                            println("          "+sender)
                            println("          "+backup_numbers_list)
                            println("          "+pass)
                            if ((backup_numbers_list.contains(sender)) && (recived_pass == "admin")){
                                println("pass")

                                if(curtain.isNotEmpty()){
                                    val target_curtain = curtain[0]
                                    if (target_curtain != null) {


                                        try {
                                            requestQueue.add(Pair(target_curtain, cmd_status))
                                            if (!isProcessing) {
                                                processQueue(context)
                                            }
                                        }catch (e:Exception){
                                            println(e)
                                        }
                                    }


                                }else if (door.isNotEmpty()){
                                    /// TODO: add door

                                }else if (fan.isNotEmpty()){
                                    val target_fan = fan[0]
                                    try {
                                        requestQueue.add(Pair(target_fan!!, cmd_status))
                                        if (!isProcessing) {
                                            processQueue(context)
                                        }
                                    }catch (e:Exception){
                                        println(e)
                                    }
                                }else if (light.isNotEmpty()){
                                    val target_light = light[0]

                                    if (target_light != null) {
                                        try {
                                            requestQueue.add(Pair(target_light, cmd_status))
                                            if (!isProcessing) {
                                                processQueue(context)
                                            }
                                        }catch (e:Exception){
                                            println(e)
                                        }


                                    }

                                }else if (plug.isNotEmpty()){
                                    val target_plug = plug[0]
                                    if (target_plug != null) {
                                        try {
                                            requestQueue.add(Pair(target_plug, cmd_status))
                                            if (!isProcessing) {
                                                processQueue(context)
                                            }
                                        }catch (e:Exception){
                                            println(e)
                                        }
                                    }

                                }else if (valve.isNotEmpty()){
                                    val target_valve = valve[0]
                                    if (target_valve != null) {
                                        try {
                                            requestQueue.add(Pair(target_valve, cmd_status))
                                            if (!isProcessing) {
                                                processQueue(context)
                                            }
                                        }catch (e:Exception){
                                            println(e)
                                        }
                                    }
                                }else if (temperature.isNotEmpty()){
                                    val target_temperature = temperature[0]
                                    if (target_temperature != null) {
                                        try {
                                            requestQueue.add(Pair(target_temperature, cmd_status))
                                            if (!isProcessing) {
                                                processQueue(context)
                                            }
                                        }catch (e:Exception){
                                            println(e)
                                        }
                                    }
                                }
                            }else{
                                /// TODO: add scenario

                            }
                            ////////// smssecurity ////////////
                            // / TODO: add security
                            //                    val security = security_db_handler_handler.get
                            //                           //////////////////////////////






                        }else{
                            if((backup_numbers_list.contains(sender)) && (recived_pass== "admin")){
                                if (cmd_type == "rnsc"){

                                    val scenario = scenario_db_handler.getScenarioById(cmd_mac.toInt())

                                    val target_scenario=scenario

                                    try {
                                        val status = UdpListener8089.sharedViewModel.is_doing.value
                                        requestQueue.add(Pair(target_scenario!!, status!!))
                                        if (!isProcessing) {
                                            processQueue(context)
                                        }
                                    }catch (e:Exception){
                                        println(e)
                                    }
                                }




                            }


                        }

                    }catch (e:Exception){
                        println(e)
                    }



                    ///  18***05***14***admin***cmnd~>50:02:91:79:ee:73~>-0--------------
                    /////////////////////// check /////////////////////





                }
            }
        }
    }
}