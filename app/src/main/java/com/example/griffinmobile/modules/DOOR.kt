package com.example.griffinmobile.mudels

class DOOR () {
    var type :String? = null
    var sub_type : String?=null
    var URL :String? = null
    var name :String? = null
    var status :String? = null
    var mac :String? = null
    var ip :String? = null
    var id :Int?=null
    constructor(type :String? , sub_type :String?, ip :String?, sub_typemac:String?, status :String? , URL :String?, name :String? , id:Int?) :this() {

        this.id=id
        this.type = type
        this.mac = mac
        this.ip = ip
        this.sub_type=sub_type
        this.name=name
        this.status=status
        this.URL=URL



    }




}