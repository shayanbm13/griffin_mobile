package com.example.griffinmobile.mudels

class scenario() {

    var light:String?=null
    var thermostat:String?=null
    var curtain:String?=null
    var valve:String?=null
    var fan:String?=null
    var plug:String?=null
    var music:String?=null
    var scenario_name:String?=null
    var in_ex:String?=null
    var tag:String?=null
    var id:Int?=null
    constructor(light:String?,thermostat:String?,curtain:String?,valve:String?,fan:String?,plug:String,scenario_name:String,id:Int?,music:String,in_ex:String,tag:String) : this(){
        this.light=light
        this.thermostat=thermostat
        this.curtain=curtain
        this.valve=valve
        this.fan=fan
        this.plug=plug
        this.scenario_name=scenario_name
        this.music=music
        this.in_ex=in_ex
        this.id=id
        this.tag=tag



    }
}