package com.example.griffinmobile.mudels

class Thermostst() {

    var room_name: String?=null
    var mood: String?=null
    var temperature: String?=null
    var current_temperature: String?=null
    var fan_status: String?=null
    var ip: String?=null
    var mac: String?=null
    var on_off: String?=null
    var subtype: String?=null
    var name: String?=null
    var id:Int?=null

    constructor(room_name:String,mood:String  ,temperature:String?,current_temperature:String?,fan_status:String? ,ip:String?,mac:String?,on_off:String?,subtype:String?,name:String? ,id:Int?) :this(){
        this.room_name=room_name
        this.mood=mood
        this.temperature=temperature
        this.current_temperature=current_temperature
        this.fan_status=fan_status
        this.ip=ip
        this.mac=mac
        this.on_off=on_off
        this.subtype=subtype
        this.name=name
        this.id=id

    }

}