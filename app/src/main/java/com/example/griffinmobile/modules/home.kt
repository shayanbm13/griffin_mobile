package com.example.griffinmobile.modules

class home() {
    var home_name: String? = null
    var tag : String? = null
    var location : String? = null  //  N,E
    var current_select : String? = null
    var id : Int?=null
    
    constructor(home_name:String? ,tag: String?,location: String?,current_select: String? ,id:Int?):this() {
        this.home_name =home_name
        this.tag =tag
        this.location =location
        this.current_select =current_select
        this.id=id
        
    }
    
    
}