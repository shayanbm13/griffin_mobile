package com.example.griffinmobile.mudels

class six_workert() {

    var type: String?=null
    var sub_type: String?=null
    var pole_num: String?=null
    var status: String?=null
    var ip: String?=null
    var mac: String?=null
    var id:Int?=null
    var name:String?=null
    var work_name:String?=null

    constructor(type:String,sub_type:String  ,pole_num:String  ,status:String? ,ip:String?,mac:String? ,id:Int?,name:String?,work_name:String?) :this(){
        this.type=type
        this.sub_type=sub_type
        this.status=status
        this.pole_num=pole_num
        this.ip=ip
        this.mac=mac
        this.id=id
        this.name=name
        this.work_name=work_name

    }

}