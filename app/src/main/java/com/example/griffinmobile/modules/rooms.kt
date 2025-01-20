package com.example.griffinmobile.mudels

class rooms (){
    var room_name:String? = null
    var room_type:String? = null
    var room_image:String? = null
    var id:Int? = null

    constructor(room_name:String,room_type:String,room_image:String,id:Int):this(){
        this.room_name=room_name
        this.room_type=room_type
        this.room_image=room_image
        this.id=id

    }


}