package com.example.griffinmobile.mudels

class simcard_accountsecurity() {
    var smsanswer_on_off :String?=null
    var admin_number :String?=null
    var backup_number1 :String?=null
    var backup_number2 :String?=null
    var backup_number3 :String?=null
    var backup_number4 :String?=null
    var id:Int?=null

    constructor(smsanswer_on_off:String,admin_number:String,backup_number1:String,backup_number2:String,backup_number3:String,backup_number4:String,id:Int):this(){

        this.smsanswer_on_off=smsanswer_on_off
        this.admin_number=admin_number
        this.backup_number1=backup_number1
        this.backup_number2=backup_number2
        this.backup_number3=backup_number3
        this.backup_number4=backup_number4
        this.id=id
    }

}