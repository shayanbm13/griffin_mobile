package com.example.griffinmobile.mudels

class camera() {

    var ip: String?=null
    var id:Int?=null
    var CAMname:String?=null
    var user:String?=null
    var pass:String?=null
    var port:String?=null
    var chanel:String?=null
    var subtype:String?=null
    var tag:String?=null


    constructor(CAMname:String?, user:String?,pass:String?,port:String?,chanel:String?,subtype:String?,ip:String?,tag:String?,id:Int?) :this() {
        this.CAMname=CAMname
        this.user=user
        this.pass=pass
        this.port=port
        this.chanel=chanel
        this.subtype=subtype
        this.ip=ip
        this.tag=tag
        this.id=id

    }

}