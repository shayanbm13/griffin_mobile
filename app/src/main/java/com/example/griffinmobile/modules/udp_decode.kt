package com.example.griffinmobile.mudels


fun extract_response(response:String):MutableList<String>{
    var final_list= mutableListOf<String>()
    val indices = mutableListOf<Int>()

    var macip=""
    var type =""
    var pole_num =""
    var ip =""
    var key_status =""

    var currentIndex = response.indexOf("~")
    while (currentIndex >= 0) {
        indices.add(currentIndex)
        currentIndex = response.indexOf("~", currentIndex + 1)
    }



    if(response.replace("'", "").substring(0,indices[0]) =="resp"){
        macip=response.replace("'", "").substring(indices[0]+2 , indices[1])
        type =response.replace("'", "").substring(indices[1]+2 , indices[2])
        pole_num =response.replace("'", "").substring(indices[2]+2 ,response.replace("'","").length)
        final_list.add(macip)
        final_list.add(type)
        final_list.add(pole_num)
        return final_list
    }else if(response.replace("'", "").substring(0,indices[0]) =="fdbk"){

        macip=response.replace("'", "").substring(indices[0]+2 , indices[1])
        pole_num =response.replace("'", "").substring(indices[2]+2 , indices[3])
        key_status =response.replace("'", "").substring(indices[3]+2 , indices[4])
        ip =response.replace("'", "").substring(indices[5]+2 , response.replace("'","").length)
        final_list.add(macip)
        final_list.add(pole_num)
        final_list.add(key_status)
        final_list.add(ip)
        return final_list
    }else if(response.replace("'", "").substring(0,indices[0]) =="learn"){

        macip=response.replace("'", "").substring(indices[0]+2 , indices[1])
        pole_num =response.replace("'", "").substring(indices[2]+2 , indices[3])
        key_status =response.replace("'", "").substring(indices[3]+2 , indices[4])
        ip =response.replace("'", "").substring(indices[4]+2 , response.replace("'","").length)
        final_list.add(macip)
        final_list.add(pole_num)
        final_list.add(key_status)
        final_list.add(ip)
        return final_list
    }else if(response.replace("'", "").substring(0,indices[0]) =="lern"){

        macip=response.replace("'", "").substring(indices[0]+2 , indices[1])
        type=response.replace("'", "").substring(indices[1]+2 , indices[2])
        pole_num =response.replace("'", "").substring(indices[2]+2 , indices[3])
        key_status =response.replace("'", "").substring(indices[3]+2 , indices[4])
        ip =response.replace("'", "").substring(indices[5]+2 , response.replace("'","").length)
        final_list.add(macip)
        final_list.add(pole_num)
        final_list.add(key_status)
        final_list.add(ip)
        final_list.add(type)
        return final_list
    }else if(response.startsWith("duty")){
        val current_response = response.split("~>").toMutableList()

        return current_response


    }else{
        return final_list
    }



}