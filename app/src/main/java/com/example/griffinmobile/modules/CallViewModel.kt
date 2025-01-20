//package com.example.griffinmobile.mudels
//import androidx.lifecycle.ViewModel
//import org.linphone.core.*
//import org.linphone.core.tools.Log
//
//class CallViewModel : ViewModel() {
//    private lateinit var core: Core
//    private lateinit var call: Call
//
//    fun initializeSip() {
//        // Initialize Linphone Core
//        val factory = Factory.instance()
//        factory.setDebugMode(true, "Hello Linphone")
//        core = factory.createCore(null, null, this) // Replace YourApplication with your Application class
//
//        // Configure SIP account details
//        val username = "your_username" // Replace with your SIP username
//        val password = "your_password" // Replace with your SIP password
//        val domain = "your_sip_server_domain" // Replace with your SIP server domain
//
//        val authInfo = Factory.instance().createAuthInfo(username, null, password, null, null, domain, null)
//
//        val accountParams = core.createAccountParams()
//        val identity = Factory.instance().createAddress("sip:$username@$domain")
//        accountParams.identityAddress = identity
//
//        val address = Factory.instance().createAddress("sip:$domain")
//        accountParams.serverAddress = address
//        accountParams.isRegisterEnabled = true
//
//        val account = core.createAccount(accountParams)
//        core.addAuthInfo(authInfo)
//        core.addAccount(account)
//        core.defaultAccount = account
//    }
//
//    fun makeCall(destination: String) {
//        // Set up the call parameters
//        val callParams = core.createCallParams(null)
//        val destinationAddress = Factory.instance().createAddress("sip:$destination")
//
//        // Make the call
//        call = core.invite(destinationAddress.toString())!!
//        call.start()
//    }
//
//    fun stopSip() {
//        // Stop Linphone Core when it's no longer needed
//        if (::core.isInitialized) {
//            core.stop()
//        }
//    }
//}