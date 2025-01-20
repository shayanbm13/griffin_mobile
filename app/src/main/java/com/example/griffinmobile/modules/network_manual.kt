package com.example.griffinmobile.mudels

class network_manual() {
    var modem_ssid:String? = null
    var modem_password: String? =null
    var api_key: String? =null
    var city_name : String? = null
    var homes : String? = null
    var id : Int? = null

    constructor(modem_ssid :String, modem_password :String,api_key :String,city_name:String,homes:String,id:Int):this() {
        this.modem_ssid=modem_ssid
        this.modem_password=modem_password
        this.api_key=api_key
        this.city_name=city_name
        this.homes=homes
        this.id=id



    }

}