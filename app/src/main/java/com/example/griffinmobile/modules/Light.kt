package com.example.griffinmobile.mudels

class Light() {

    var room_name: String?=null
    var sub_type: String?=null
    var status: String?=null
    var ip: String?=null
    var mac: String?=null
    var id:Int?=null
    var Lname:String?=null

    constructor(room_name:String,sub_type:String  ,status:String? ,ip:String?,mac:String? ,id:Int?,Lname:String?) :this(){
        this.room_name=room_name
        this.sub_type=sub_type
        this.status=status
        this.ip=ip
        this.mac=mac
        this.id=id
        this.Lname=Lname

    }

}