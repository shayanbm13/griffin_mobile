package com.example.griffinmobile.mudels

class security() {
    var alarm_duration   :String?=null
    var arm_active_deley :String?=null
    var alarm_triger_deley :String?=null
    var active_scenario :String?=null
    var password_security :String?=null
    var id : Int? =null


    constructor(alarm_duration:String? ,arm_active_deley:String? ,  alarm_triger_deley:String? ,active_scenario:String?,password_security:String? , id: Int? ):this(){
        this.alarm_duration=alarm_duration
        this.arm_active_deley=arm_active_deley
        this.alarm_triger_deley =alarm_triger_deley
        this.active_scenario=active_scenario
        this.password_security=password_security
        this.id=id

    }

}