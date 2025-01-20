package com.example.griffinmobile.mudels

class simcard_messageresponse (){
    var scenario_r:String?=null
    var module_r:String?=null
    var sensor_r:String?=null
    var security_r:String?=null
    var id:Int?=null
    constructor( scenario_r:String, module_r:String ,sensor_r:String  ,security_r:String ,id:Int ):this(){

        this.scenario_r=scenario_r
        this.module_r=module_r
        this.sensor_r=sensor_r
        this.id=id


    }
}