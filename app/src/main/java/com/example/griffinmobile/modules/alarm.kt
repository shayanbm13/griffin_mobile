package com.example.griffinmobile.mudels

class alarm() {


    var next_status: String?=null
    var id:Int?=null
    var alarm_name:String?=null
    var device_name:String?=null
    var alarm_day:String?=null
    var alarm_tyme:String?=null
    var grooup:String?=null

    constructor(next_status:String? ,id:Int?,alarm_name:String?,device_name:String?,alarm_day:String?,alarm_tyme:String?,grooup:String?) :this(){

        this.next_status=next_status

        this.device_name=device_name
        this.id=id
        this.alarm_name=alarm_name
        this.alarm_day=alarm_day
        this.alarm_tyme=alarm_tyme
        this.grooup=grooup

    }
}