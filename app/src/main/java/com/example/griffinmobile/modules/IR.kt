package com.example.griffinmobile.mudels

class IR() {

    var id : Int? = null
    var sub_type:String?=null
    var mac:String?=null
    var ip:String?=null
    var cmd:String?=null

    var name:String?=null
    var type:String?=null
    constructor(sub_type:String?,mac:String?,ip:String?,cmd:String?,name:String?,type:String?,id:Int?) : this(){

        this.id=id
        this.sub_type=sub_type
        this.mac=mac
        this.ip=ip
        this.cmd=cmd

        this.name=name
        this.type=type


    }
}