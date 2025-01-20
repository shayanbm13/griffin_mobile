package com.example.griffinmobile.mudels

class valve () {
    var room_name:String?=null
    var status:String?=null
    var subtype:String?=null
    var ip:String?=null
    var mac:String?=null
    var Vname:String?=null
    var id:Int?=null

    constructor(room_name:String?, status:String?, subtype:String?, ip:String?, mac:String?, Vname:String, id:Int?) :this(){
        this.room_name = room_name
        this.status = status
        this.subtype = subtype
        this.ip = ip
        this.mac = mac
        this.Vname = Vname
        this.id = id


    }
}