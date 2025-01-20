package com.example.griffinmobile.mudels

class simcard_security() {

    var username:String? = null
    var password: String? =null
    var id : Int? = null

    constructor(username :String, password :String,id:Int):this() {
        this.username=username
        this.password=password

        this.id=id



    }

}