package com.example.griffinmobile.mudels

import android.content.Context
import com.bumptech.glide.disklrucache.DiskLruCache.Value
import com.example.griffinmobile.database.*
fun extractRTSPInfo(url: String): Map<String, String> {
    val result = mutableMapOf<String, String>()

    // الگوی با پورت
    val regexWithPort = Regex("rtsp://(.*?):(.*?)@(.*?):(.*?)/.*\\?channel=(.*?)&subtype=(.*?)$")
    // الگوی بدون پورت
    val regexWithoutPort = Regex("rtsp://(.*?):(.*?)@(.*?)/.*\\?channel=(.*?)&subtype=(.*?)$")

    val matchResultWithPort = regexWithPort.find(url)
    val matchResultWithoutPort = regexWithoutPort.find(url)

    if (matchResultWithPort != null) {
        // اگر پورت وجود داشته باشد
        result["username"] = matchResultWithPort.groupValues[1]
        result["password"] = matchResultWithPort.groupValues[2]
        result["ipAddress"] = matchResultWithPort.groupValues[3]
        result["port"] = matchResultWithPort.groupValues[4]
        result["channel"] = matchResultWithPort.groupValues[5]
        result["subtype"] = matchResultWithPort.groupValues[6]

        println("URL with port:")
        println("Username: ${result["username"]}")
        println("Password: ${result["password"]}")
        println("IP Address: ${result["ipAddress"]}")
        println("Port: ${result["port"]}")
        println("Channel: ${result["channel"]}")
        println("Subtype: ${result["subtype"]}")

    } else if (matchResultWithoutPort != null) {
        // اگر پورت وجود نداشته باشد
        result["username"] = matchResultWithoutPort.groupValues[1]
        result["password"] = matchResultWithoutPort.groupValues[2]
        result["ipAddress"] = matchResultWithoutPort.groupValues[3]
        result["channel"] = matchResultWithoutPort.groupValues[4]
        result["subtype"] = matchResultWithoutPort.groupValues[5]

        println("URL without port:")
        println("Username: ${result["username"]}")
        println("Password: ${result["password"]}")
        println("IP Address: ${result["ipAddress"]}")
        println("Port: No port")
        println("Channel: ${result["channel"]}")
        println("Subtype: ${result["subtype"]}")

    } else {
        // اگر هیچ الگوی معتبری مطابق نبود
        println("Invalid RTSP URL format")
    }

    return result
}

fun sync_decoder(context: Context,received_msg:String,tag:String){
    var msg=received_msg.split("~>")
    val roomsDb=rooms_db.getInstance(context)
    val light_db_handler =light_db.getInstance(context)
    val fan_db_handler =fan_db.getInstance(context)
    val thermostat_db_handler =Temperature_db.getInstance(context)
    val curtain_db_handler =curtain_db.getInstance(context)
    val plug_db_handler =plug_db.getInstance(context)
    val valve_db_handler =valve_db.getInstance(context)
    val door_db_handler =door_db.getInstance(context)
    val camera_db =camera_db.getInstance(context)
    val sixworker_db =six_workert_db.getInstance(context)
    val scenarioDb =scenario_db.Scenario_db.getInstance(context)
    val six_workert_db =six_workert_db.getInstance(context)

    if (msg[0]=="syro"){

        var type =msg[3]
        println(type)
        if ((type != "Door") ||( type != "Security") ){



            var new_type=type
            val room = rooms()
            room.room_name=msg[2]

            when(new_type){
                "Hall"-> {
                    new_type = "livingroom"
                }
            }
            room.room_type=new_type
            println(new_type.substring(0,3))
            when (new_type.substring(0,4)){
                "bath" -> room.room_image="s1_bathroom1_"
                "Bedr" -> room.room_image="s1_room_"
                "bedr" -> room.room_image="s1_room_"
                "Bath" -> room.room_image="s1_bathroom1_"
                "dini" -> room.room_image="s1_dining_room_"
                "Dini" -> room.room_image="s1_dining_room_"
                "gust" ->  room.room_image="s1_gust_room_"
                "Gust" ->  room.room_image="s1_gust_room_"
                "gues" ->  room.room_image="s1_gust_room_"
                "kids" ->  room.room_image="s1_kids_room_"
                "Kids" ->  room.room_image="s1_kids_room_"
                "kitc" -> room.room_image="s1_kitchen_"
                "Kitc" -> room.room_image="s1_kitchen_"
                "livi" -> room.room_image="s1_living_room_"
                "Livi" -> room.room_image="s1_living_room_"
                "mast" -> room.room_image="s1_master_room_"
                "Mast" -> room.room_image="s1_master_room_"
                "tv_r" -> room.room_image="s1_tv_room_"
                "Tv_r" -> room.room_image="s1_tv_room_"
                "yard" ->  room.room_image="s1_yard_"
                "Yard" ->  room.room_image="s1_yard_"
                "room" -> room.room_image="s1_room_"
                "Room" -> room.room_image="s1_room_"
                else -> {"no sync"}

            }
            if ( type =="Door"){

                println("Door Room canceld")
            }else if (type=="Security"){
                println("security Room canceld")

            }else{

                roomsDb.set_to_db_rooms(room)

            }


        }





    }else if (msg[0]=="symo"){

        var type = msg[4]
        if (type =="Lght"){

            val light = Light()
            light.room_name=msg[2]
            light.Lname=msg[3]
            light.sub_type=msg[5]
            light.status="off"
            light.mac=msg[7]
            light.ip=msg[8]
            light_db_handler.set_to_db_light(light)

        }else if(type =="Fano"){
            val Fan = fan()
            Fan.room_name=msg[2]
            Fan.Fname=msg[3]
            Fan.subtype=msg[5]
            Fan.status="1"
            Fan.mac=msg[7]
            Fan.ip=msg[8]
            fan_db_handler.set_to_db_fan(Fan)
        }else if (type =="Tmpr" ){
            val thermostat =Thermostst()
            thermostat.room_name=msg[2]
            thermostat.name=msg[3]
            thermostat.subtype=msg[5]
            thermostat.fan_status = "0"
            thermostat.mood = "0"
            thermostat.on_off = "0"
            thermostat.subtype = "0000"
            thermostat.temperature = "16"
            thermostat.mac=msg[7]
            thermostat.ip=msg[8]
            thermostat_db_handler.set_to_db_Temprature(thermostat)

        }else if (type =="Crtn" ){
            val curtain = curtain()
            curtain.room_name=msg[2]
            curtain.Cname=msg[3]
            curtain.sub_type=msg[5]
            curtain.status="00"
            curtain.mac=msg[7]
            curtain.ip=msg[8]
            curtain_db_handler.set_to_db_curtain(curtain)

        }else if (type =="Plug" ){
            val plug = Plug()
            plug.room_name=msg[2]
            plug.Pname=msg[3]
            plug.subtype=msg[5]
            plug.status="0"
            plug.mac=msg[7]
            plug.ip=msg[8]
            plug_db_handler.set_to_db_plug(plug)



        }else if (type =="ElVa" ){

            val valve = valve()
            valve.room_name=msg[2]
            valve.Vname=msg[3]
            valve.subtype=msg[5]
            valve.status="0"
            valve.mac=msg[7]
            valve.ip=msg[8]
            valve_db_handler.set_to_db_valve(valve)
        }else if (type =="Incm" ){

            val door = DOOR()
            door.name=msg[3]
            door.type=msg[4]
            door.sub_type=msg[5]
            door.status=msg[6]
            door.mac=msg[7]
            door.ip=msg[8]
            door_db_handler.set_to_db_DOOR(door)
        }else if (type =="Camr" ){

            val Camera = camera()
            val url = msg[9]
            println("urlllllllllllllllllllll " +url)
            var url_list_decoded = extractRTSPInfo(url)
            println(url_list_decoded)
            Camera.ip=url_list_decoded["ipAddress"]
            Camera.subtype=url_list_decoded["subtype"]
            Camera.port=url_list_decoded["port"]
            Camera.chanel=url_list_decoded["channel"]
            Camera.pass=url_list_decoded["password"]
            Camera.tag=tag
            Camera.user=url_list_decoded["username"]
            Camera.CAMname=msg[3]



            camera_db.set_to_db_camera(Camera)
        }else if (type =="SixC" ){
            val sixWorkert= six_workert()

            val listed_response = msg[6].split("!")


            sixWorkert.name=msg[3]
            sixWorkert.sub_type=msg[5]
            sixWorkert.type=listed_response[0]
            sixWorkert.pole_num=listed_response[1]
            sixWorkert.status=listed_response[2]
            sixWorkert.work_name=listed_response[3]

            sixWorkert.mac=msg[7]
            sixWorkert.ip=msg[8]
            six_workert_db.set_to_db_six_workert(sixWorkert)


        }else if (msg[0] =="sysx" ){

            if (msg[5]=="Scenario"){
                val six = six_workert()
//            six.type=msg[2]
                six.work_name=msg[6]
                six.name=msg[2]
                six.sub_type=msg[5]
                six.status=msg[6]
                six.mac=msg[7]
                six.ip=msg[8]
                six_workert_db.set_to_db_six_workert(six)

            }else if (msg[5]=="Scenario"){


            }



        }


    }else if (msg[0] =="sysc" ){
        println("innnnnnnnnnnnnnnnnnnnnnnnnnnn")
        val scenaio_name = msg[2]
        println("index0 " +msg[0])
        println("index1 " +msg[1])
        println("index2 " +msg[2])
        println("index3 " +msg[3])
        println("index4 " +msg[4])

        val macs = msg[5].split("***")
        val statuses = msg[6].split("***")

        var scenario_light = mutableListOf<String>()
        var scenario_fan = mutableListOf<String>()
        var scenario_thermostat = mutableListOf<String>()
        var scenario_curtain = mutableListOf<String>()
        var scenario_plug = mutableListOf<String>()
        var scenario_valve = mutableListOf<String>()


        for (mac in macs){
            val current_index = macs.indexOf(mac)
//            println(light_db_handler.getAllLights())

            val lights = light_db_handler.getLightsByMacAddress(mac)
            val fans = fan_db_handler.getfansByMacAddress(mac)
            val thermostats = thermostat_db_handler.getThermostatsByMac(mac)
            val curtains = curtain_db_handler.getcurtainsByMacAddress(mac)
            val plugs = plug_db_handler.getPlugsByMacAddress(mac)
            val valves = valve_db_handler.getvalvesByMacAddress(mac)

            println("1")
            println(lights)
            if (lights.isNotEmpty()){
                println("2")
                val listed_status =statuses[current_index].toList()

                for (light in lights){
                    val index = lights.indexOf(light)
//                    println(statuses[current_index])
//                    println(statuses[current_index][index])
                    when(statuses[current_index][index]){
                        '1' -> scenario_light.add(light!!.Lname!!.trim()+"#on".trim())
                        '0' -> scenario_light.add(light!!.Lname!!.trim()+"#off".trim())
                    }

                }
                println(scenario_light)

            }
            if (fans.isNotEmpty()){
                val listed_status =statuses[current_index].toList()

                for (fan in fans){
                    val index = fans.indexOf(fan)
                    when(statuses[current_index][index]){
                        '1' -> scenario_fan.add(fan!!.Fname!!.trim()+"#on".trim())
                        '0' -> scenario_fan.add(fan!!.Fname!!.trim()+"#off".trim())
                    }

                }

            }
            if (thermostats.isNotEmpty()){
                val listed_status =statuses[current_index].toList()

                for (thermostat in thermostats){
                    val index = thermostats.indexOf(thermostat)
                    val temperstatus = statuses[current_index].toMutableList()
                    scenario_thermostat.add(thermostat!!.name!!.trim()+"#${temperstatus[0]}!${temperstatus[3]}${temperstatus[4]}$${temperstatus[6]}@${temperstatus[5]}".trim())


                }

            }
            if (curtains.isNotEmpty()){
                val listed_status =statuses[current_index].toList()

                for (curtain in curtains){
                    val index = curtains.indexOf(curtain)
                    val curtainstatus = statuses[index].toMutableList()
                    scenario_curtain.add(curtain!!.Cname!!.trim()+"#${curtainstatus[0]}${curtainstatus[1]}".trim())


                }

            }
            if (plugs.isNotEmpty()){
                val listed_status =statuses[current_index].toList()

                for (plug in plugs){
                    val index = plugs.indexOf(plug)
                    scenario_plug.add(plug!!.Pname!!.trim()+"#${statuses[index]}".trim())


                }

            }
            if (valves.isNotEmpty()){
                val listed_status =statuses[current_index].toList()

                for (valve in valves){
                    val index = valves.indexOf(valve)
                    scenario_valve.add(valve!!.Vname!!.trim()+"#${statuses[index]}".trim())


                }

            }

        }


        val my_scenario =scenario()
        my_scenario.scenario_name=scenaio_name
        my_scenario.light=scenario_light.joinToString(",")
        println(scenario_light)
        println(my_scenario.light)
        my_scenario.fan=scenario_fan.joinToString(",")
        my_scenario.thermostat=scenario_thermostat.joinToString (",")
        my_scenario.curtain=scenario_curtain.joinToString (",")
        my_scenario.plug=scenario_plug.joinToString (",")
        my_scenario.valve=scenario_valve.joinToString(",")
        my_scenario.tag=tag
        scenarioDb.set_to_db_Scenario(my_scenario)


    }


}