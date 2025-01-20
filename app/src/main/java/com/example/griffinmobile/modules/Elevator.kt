package com.example.griffinmobile.mudels

class Elevator() {



    var ip:String?=null
    var mac:String?=null
    var status:String?=null

    var id:Int?=null
    constructor(ip:String?,mac:String?,status:String?,id:Int?) : this(){

        this.ip=ip
        this.mac=mac
        this.status=status

        this.id=id



    }
}