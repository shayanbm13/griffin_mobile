package com.example.griffinmobile.mudels

class fan() {
    var room_name:String?=null
    var status:String?=null
    var subtype:String?=null
    var ip:String?=null
    var mac:String?=null
    var Fname:String?=null
    var id:Int?=null
    constructor(room_name:String?,status:String?,subtype:String?,ip:String?,mac:String?,Fname:String,id:Int?) : this(){
        this.room_name=room_name
        this.status=status
        this.subtype=subtype
        this.ip=ip
        this.mac=mac
        this.Fname=Fname
        this.id=id



    }
}