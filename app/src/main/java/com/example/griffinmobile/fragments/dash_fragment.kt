package com.example.griffinmobile.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.griffinmobile.R
import com.example.griffinmobile.SettingsActivity
import com.example.griffinmobile.apdapters.ViewPagerAdapter_dashboard_vertical_center
import com.example.griffinmobile.apdapters.dashboard_down_adapter
import com.example.griffinmobile.database.setting_network_db
import com.example.griffinmobile.modules.connectToWiFiAndPerformAction
import com.example.griffinmobile.mudels.SharedViewModel
import com.example.griffinmobile.mudels.handler
import com.example.griffinmobile.mudels.network_manual
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.dnsoverhttps.DnsOverHttps
import org.json.JSONObject
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import kotlin.math.ceil


class dash_fragment : Fragment() {

val SharedviewModel: SharedViewModel by activityViewModels()

    private val handler2 = Handler(Looper.getMainLooper())
    private val handler = Handler(Looper.getMainLooper())
    private var isCheckingWiFiAndGPS = 0 // پرچم کنترلی
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    fun percent_calculator(count:Int ,current:Int ): Double {
        val a= ceil((count.toDouble()/3.00))
        val b = 100/a

        return b+current

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val wifiManager = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // درخواست مجوز مکان
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // درخواست مجوز
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        // دریافت موقعیت مکانی
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity(), OnSuccessListener<Location> { location ->
            if (location != null) {
                // موقعیت مکانی پیدا شد
                val latitude = location.latitude
                val longitude = location.longitude
                println("Latitude: $latitude, Longitude: $longitude")
            } else {
                println("No location detected")
            }
        })


//        fun isConnectedToWifi(context: Context): Boolean {
//            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
//            return wifiManager?.isWifiEnabled == true && wifiManager.connectionInfo.ipAddress != 0
//        }
//
//        fun checkAndConnectToWiFi(context: Context) {
//            try {
//                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//                val wifiInfo = wifiManager.connectionInfo
//                val ssid = wifiInfo.ssid.replace("\"", "")
//                val db_ssid = setting_network_db.getInstance(context).get_from_db_network_manual(1)?.modem_ssid
//
//                if (ssid != db_ssid) {
//                    val networkDbHandler = setting_network_db.getInstance(context).get_from_db_network_manual(1)
//                    networkDbHandler?.modem_ssid?.let { modemSSID ->
//                        connectToWiFiAndPerformAction(
//                            context = context,
//                            ssid = modemSSID,
//                            password = networkDbHandler.modem_password,
//                            onConnected = {
//                                Toast.makeText(context, "Connected to Main network", Toast.LENGTH_SHORT).show()
//                            },
//                            onTimeout = {
//                                Toast.makeText(context, "Connection timed out", Toast.LENGTH_SHORT).show()
//                            }
//                        )
//                    }
//                } else {
//                    Toast.makeText(context, "Already connected to the correct network", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                println(e)
//            }
//        }
//
//        fun checkAndRequestWiFi(context: Context, onWiFiEnabled: () -> Unit) {
//            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//            val handler = Handler(Looper.getMainLooper())
//            val interval: Long = 4000 // بررسی هر 4 ثانیه
//            val timeout: Long = 20000 // تایم‌اوت 20 ثانیه
//            val startTime = System.currentTimeMillis()
//
//            val wifiChecker = object : Runnable {
//                override fun run() {
//                    val isWiFiEnabled = wifiManager.isWifiEnabled
//
//                    if (isWiFiEnabled) {
//                        handler.removeCallbacks(this) // توقف بررسی
//                        onWiFiEnabled() // ادامه عملکرد بعد از روشن شدن Wi-Fi
//                    } else if (System.currentTimeMillis() - startTime < timeout) {
//                        handler.postDelayed(this, interval) // بررسی مجدد
//                    } else {
//                        handler.removeCallbacks(this) // توقف بررسی در صورت تایم‌اوت
//                        Toast.makeText(context, "Wi-Fi was not turned on within the timeout period", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//
//            if (!wifiManager.isWifiEnabled) {
//                Toast.makeText(context, "Please turn on your Wi-Fi", Toast.LENGTH_SHORT).show()
//                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
//                context.startActivity(intent)
//                handler.post(wifiChecker)
//            } else {
//                onWiFiEnabled() // اگر Wi-Fi روشن بود، مستقیماً عملکرد بعدی را اجرا کن
//            }
//        }
//
//        fun checkAndRequestGPS(context: Context, onGPSEnabled: () -> Unit) {
//            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//            val handler = Handler(Looper.getMainLooper())
//            val interval: Long = 4000 // بررسی هر 4 ثانیه
//            val timeout: Long = 20000 // تایم‌اوت 20 ثانیه
//            val startTime = System.currentTimeMillis()
//
//            val gpsChecker = object : Runnable {
//                override fun run() {
//                    val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//
//                    if (isGPSEnabled) {
//                        handler.removeCallbacks(this) // توقف بررسی
//                        onGPSEnabled() // ادامه عملکرد بعد از روشن شدن GPS
//                    } else if (System.currentTimeMillis() - startTime < timeout) {
//                        handler.postDelayed(this, interval) // بررسی مجدد
//                    } else {
//                        handler.removeCallbacks(this) // توقف بررسی در صورت تایم‌اوت
//                        Toast.makeText(context, "GPS was not turned on within the timeout period", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//
//            val sharedPreferences = context.getSharedPreferences("GPS_Settings", Context.MODE_PRIVATE)
//            val isGPSSettingsPageOpen = sharedPreferences.getBoolean("isGPSSettingsPageOpen", false)
//
//            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !isGPSSettingsPageOpen) {
//                sharedPreferences.edit().putBoolean("isGPSSettingsPageOpen", true).apply()
//                Toast.makeText(context, "Please turn on your GPS", Toast.LENGTH_SHORT).show()
//                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                context.startActivity(intent)
//                handler.post(gpsChecker)
//            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                onGPSEnabled() // اگر GPS روشن بود، مستقیماً عملکرد بعدی را اجرا کن
//            }
//
//            handler.postDelayed({
//                sharedPreferences.edit().putBoolean("isGPSSettingsPageOpen", false).apply()
//            }, 2000) // تنظیم مجدد پرچم بعد از بازگشت از تنظیمات
//        }
//
//        fun checkAndRequestGPSAndWiFi(context: Context) {
//            checkAndRequestGPS(context = context) {
//                checkAndRequestWiFi(context = context) {
//                    checkAndConnectToWiFi(context) // اتصال به Wi-Fi
//                }
//            }
//        }
//        checkAndRequestGPSAndWiFi(requireContext())
//





    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dash_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

// اطمینان از اینکه layoutManager تنظیم شده باشد
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.layoutManager = gridLayoutManager

// ایجاد لیست 12 آیتم
        val itemList = List(12) { "Item ${(it + 1)}" }

// تنظیم آداپتر
        recyclerView.adapter = dashboard_down_adapter(itemList)

// افزودن SnapHelper برای اسکرول خط‌خط
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

// حذف Scrollbar
        recyclerView.isVerticalScrollBarEnabled = false
        recyclerView.isHorizontalScrollBarEnabled = false

// اسکرول با سرعت کم به اندازه یک آیتم
        val layoutManager = recyclerView.layoutManager as? GridLayoutManager
        layoutManager?.let {
            val firstVisiblePosition = it.findFirstVisibleItemPosition()

            // ایجاد اسکرولر با سرعت دلخواه (تغییر این مقدار برای سرعت بیشتر یا کمتر)
            val smoothScroller = object : LinearSmoothScroller(requireContext()) {
                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    // مقدار پایین‌تر، سرعت کندتر
                    return 0.1f // تغییر این مقدار برای تغییر سرعت اسکرول
                }
            }

            smoothScroller.targetPosition = firstVisiblePosition + 1
            it.startSmoothScroll(smoothScroller)
        }



        val centerVerticalViewPager = requireView().findViewById<ViewPager2>(R.id.center_vertical_viewpager)
        centerVerticalViewPager.adapter = ViewPagerAdapter_dashboard_vertical_center(requireActivity())
        centerVerticalViewPager.offscreenPageLimit = 3

    }
    private fun startUpdatingDate() {
        val currentDate = Calendar.getInstance().time

        // قالب برای روز هفته و ماه/سال
        val dayMonthFormatter = SimpleDateFormat("EEEE\nMMMM", Locale.getDefault()) // Saturday\nMay 2023

        // قالب فقط برای عدد روز
        val dayNumberFormatter = SimpleDateFormat("d", Locale.getDefault()) // فقط عدد روز

        val dateTextView=requireView().findViewById<TextView>(R.id.textView2)
        val dayNumberTextView=requireView().findViewById<TextView>(R.id.textView5)

        // دریافت مقادیر قالب‌بندی‌شده
        val formattedDayMonth = dayMonthFormatter.format(currentDate) // Saturday\nMay 2023
        val formattedDayNumber = dayNumberFormatter.format(currentDate) // عدد روز (7)
        println(formattedDayMonth)
        println(formattedDayNumber)

        // تنظیم در TextViewها
        dateTextView.text = formattedDayMonth
        dayNumberTextView.text = formattedDayNumber
    }

    private fun startUpdatingTime() {
        handler.post(object : Runnable {
            override fun run() {
                // گرفتن زمان جاری
                val currentTime = Calendar.getInstance().time
                val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                val formattedTime = formatter.format(currentTime)
                val timeTextView = requireView().findViewById<TextView>(R.id.texsstVie4)

                // ست کردن زمان روی TextView
                timeTextView.text = formattedTime

                // اجرای دوباره بعد از یک ثانیه
                handler.postDelayed(this, 1000)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (isCheckingWiFiAndGPS<4) {
            isCheckingWiFiAndGPS += 1 // شروع بررسی
            checkAndRequestGPSAndWiFi(requireContext())
        }

        startUpdatingTime()
        startUpdatingDate()
        val wifi_status = requireView().findViewById<TextView>(R.id.wifi_status)
        val Wifi_icon= requireView().findViewById<ImageView>(R.id.imageView)

        fun isConnectedToWifi(context: Context): Boolean {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            return wifiManager?.isWifiEnabled == true && wifiManager.connectionInfo.ipAddress != 0
        }

        wifi_status.setOnClickListener {
            try {
                val wifiManager =
                    requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                val ssid = wifiInfo.ssid.replace("\"", "")
                val db_ssid = setting_network_db.getInstance(requireContext())
                    .get_from_db_network_manual(1)?.modem_ssid

                if (ssid != db_ssid) {
                    isCheckingWiFiAndGPS=0
                    if (isCheckingWiFiAndGPS<4) {
                        isCheckingWiFiAndGPS += 1 // شروع بررسی
                        checkAndRequestGPSAndWiFi(requireContext())
                    }
                }
            }catch (e:Exception){

                println(e)
            }
        }
        Wifi_icon.setOnClickListener {

        }

        fun check_connection(context: Context) {
            val timer = Timer()

            val task = object : TimerTask() {
                override fun run() {
                    // کدی که هر 5 دقیقه اجرا می‌شود
                    InternetCheck { isConnected ->
                        if (isConnected) {

                            try {
                                val wifiManager =
                                    context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                                val wifiInfo = wifiManager.connectionInfo
                                val ssid = wifiInfo.ssid.replace("\"", "")
                                val db_ssid = setting_network_db.getInstance(context)
                                    .get_from_db_network_manual(1)?.modem_ssid

                                if (ssid != db_ssid) {
                                    wifi_status.text = "Not Connected"
                                }else{
                                    wifi_status.text = "Connected"

                                }
                            }catch (e:Exception){

                                println(e)
                            }

                        } else {
                            if (isConnectedToWifi(context)) {
                                wifi_status.text = "No internet"

                            } else {
                                wifi_status.text = "No access"

                            }
                        }
                    }.execute()

                }
            }

            // تنظیم زمان شروع و دوره تکرار (هر 5 دقیقه)
            val delay = 0L
            val period = 5 * 60 * 1000L
            timer.scheduleAtFixedRate(task, delay, period)
        }

        check_connection(requireContext())
        handler2.postDelayed({ startUpdatingDate() }, 60000)
        val center_1= requireView().findViewById<ImageView>(R.id.center_1)
        val center_2= requireView().findViewById<ImageView>(R.id.center_2)
        val center_3= requireView().findViewById<ImageView>(R.id.center_3)

        val weather_image=requireView().findViewById<ImageView>(R.id.weather_image)
        val temp_textview=requireView().findViewById<TextView>(R.id.temp_textview)
        val weather_text=requireView().findViewById<TextView>(R.id.weather_text)


        fun weather(loation:String,apiKey:String) {

            val dohClient = OkHttpClient()

            val dns = DnsOverHttps.Builder()
                .client(dohClient)
                .url("https://free.shecan.ir/dns-query".toHttpUrl()) // تغییر URL به free.shecan.ir
                .bootstrapDnsHosts(
                    InetAddress.getByName("178.22.122.100"),
                    InetAddress.getByName("185.51.200.2")
                )
                .build()

// ساختن کلاینت OkHttp با DNS over HTTPS
            val client = OkHttpClient.Builder()
                .dns(dns)
                .build()

// تعریف پارامترهای درخواست
            val baseUrl = "https://api.openweathermap.org/data/2.5/"
            val endpoint = "weather"
            val apiKey =  apiKey// کلید API خود را وارد کنید
            val location = loation // نام مکان مورد نظر

// ساخت URL درخواست
            val urlBuilder = (baseUrl + endpoint).toHttpUrlOrNull()?.newBuilder()
            urlBuilder?.addQueryParameter("q", location)
            urlBuilder?.addQueryParameter("appid", apiKey)

            val url = urlBuilder?.build()?.toString()

// ساخت درخواست
            val request = url?.let { it1 ->
                Request.Builder()
                    .url(it1)
                    .build()
            }

// چاپ درخواست برای بررسی
            println(request)

// ارسال درخواست
            request?.let {
                client.newCall(it).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!it.isSuccessful) throw IOException("Unexpected code $it")

                            // پردازش پاسخ
                            println(it.body?.string())
                        }
                    }
                })
            }


            if (request != null) {
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("Failed to make request: ${e.message}")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()

                            val jsonResponse = JSONObject(responseBody!!)
                            val main = jsonResponse.getJSONObject("main")
                            val temperature = main.getDouble("temp").toInt()-273.15.toInt()
                            val weatherArray = jsonResponse.getJSONArray("weather")
                            val weatherObject = weatherArray.getJSONObject(0).getString("main")
                            val weatherdescription = weatherArray.getJSONObject(0).getString("description")

                            try {
                                requireActivity().runOnUiThread {


                                    weather_text.text = weatherObject
                                    if (weather_text.length()>8){
                                        weather_text.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10F)
                                    }else{
                                        weather_text.setTextSize(TypedValue.COMPLEX_UNIT_PT, 14F)

                                    }

                                    weather_text.text=weatherObject
                                    var righttemp = "$temperature°c"
                                    temp_textview.text=righttemp
                                    if (weatherObject=="Clouds"&& weatherdescription=="overcast clouds"){
                                        weather_image.setImageResource(R.drawable.icon_cloud)
                                    }
                                    if (weatherObject=="Clouds"&& weatherdescription=="broken clouds"||weatherdescription=="scattered clouds"||weatherdescription=="few clouds"||weatherdescription=="haze"||weatherdescription=="Haze"){
                                        weather_image.setImageResource(R.drawable.icon_partly_cloudy)
                                    }

                                    if (weatherObject=="Rain"){
                                        weather_image.setImageResource(R.drawable.icon_rain)
                                    }
                                    if (weatherObject=="Clear"){
                                        weather_image.setImageResource(R.drawable.icon_sunny)
                                    }
                                    if (weatherObject== "Thunderstorm"|| weatherObject=="Squall"||weatherObject=="Tornado"){
                                        weather_image.setImageResource(R.drawable.icon_thunder)
                                    }
                                    if (weatherObject== "Snow"){
                                        weather_image.setImageResource(R.drawable.icon_snow)
                                    }

//


                                    println("temperature: $temperature")

                                    println("weatherObject: $weatherObject")
                                }
                            }catch (e:Exception){


                                println(e)
                            }







                        } else {
                            println("Request failed with code: ${response.code}")
                        }
                    }
                })
            }



        }


        var timer = Timer()
        val databaseHelper = setting_network_db.getInstance(requireContext())
        if (!databaseHelper.isEmptynetwork_tabale()){
            var current_db: network_manual? = databaseHelper.get_from_db_network_manual(1)


            var apiKey=current_db!!.api_key.toString()
            var location=current_db!!.city_name.toString()


            val task = object : TimerTask() {
                override fun run() {
                    weather(location,apiKey)
                }
            }
            // زمان شروع (به طور پیش‌فرض فعلی)
            val startTime = Date()

            // دوره تکرار (هر 3 ساعت)
            val period = 3 * 60 * 60 * 1000L

            // اجرای فانکشن هر 3 ساعت با تاخیر صفر
            timer.scheduleAtFixedRate(task, startTime, period)





        }

        val setting_butten = requireView().findViewById<Button>(R.id.setting_butten)
        setting_butten.setOnClickListener {
            val indent = Intent(requireContext(),SettingsActivity::class.java)
            startActivity(indent)
            requireActivity().finish()

        }

        SharedviewModel.dash_center_index.observe(viewLifecycleOwner, Observer { index ->

            if (index=="1"){

                center_1.setBackgroundResource(R.drawable.fuul_ring_icon)
                center_2.setBackgroundResource(R.drawable.ring_icon)
                center_3.setBackgroundResource(R.drawable.ring_icon)

            }else if (index=="2"){
                center_1.setBackgroundResource(R.drawable.ring_icon)
                center_2.setBackgroundResource(R.drawable.fuul_ring_icon)
                center_3.setBackgroundResource(R.drawable.ring_icon)

            }else if (index=="3"){
                center_1.setBackgroundResource(R.drawable.ring_icon)
                center_2.setBackgroundResource(R.drawable.ring_icon)
                center_3.setBackgroundResource(R.drawable.fuul_ring_icon)

            }



        })


    }
    override fun onDestroy() {
        super.onDestroy()
        // متوقف کردن Handler هنگام تخریب Activity
        handler.removeCallbacksAndMessages(null)
        handler2.removeCallbacksAndMessages(null)
    }

    override fun onPause() {
        super.onPause()

    }

         fun isConnectedToWifi(context: Context): Boolean {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            return wifiManager?.isWifiEnabled == true && wifiManager.connectionInfo.ipAddress != 0
        }

        fun checkAndConnectToWiFi(context: Context) {
            try {
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                val ssid = wifiInfo.ssid.replace("\"", "")
                val db_ssid = setting_network_db.getInstance(context).get_from_db_network_manual(1)?.modem_ssid
                println(db_ssid)
                println(ssid)

                if (ssid != db_ssid) {
                    val networkDbHandler = setting_network_db.getInstance(context).get_from_db_network_manual(1)
                    networkDbHandler?.modem_ssid?.let { modemSSID ->
                        connectToWiFiAndPerformAction(
                            context = context,
                            ssid = modemSSID,
                            password = networkDbHandler.modem_password,
                            onConnected = {
                                Toast.makeText(context, "Connected to Main network", Toast.LENGTH_SHORT).show()
                            },
                            onTimeout = {
//                                Toast.makeText(context, "Connection timed out", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                } else {
//                    Toast.makeText(context, "Already connected to the correct network", Toast.LENGTH_SHORT).show()
                    println("Already connected to the correct network")
                }
            } catch (e: Exception) {
                println(e)
            }
        }

        fun checkAndRequestWiFi(context: Context, onWiFiEnabled: () -> Unit) {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val handler = Handler(Looper.getMainLooper())
            val interval: Long = 4000 // بررسی هر 4 ثانیه
            val timeout: Long = 20000 // تایم‌اوت 20 ثانیه
            val startTime = System.currentTimeMillis()

            val wifiChecker = object : Runnable {
                override fun run() {
                    val isWiFiEnabled = wifiManager.isWifiEnabled

                    if (isWiFiEnabled) {
                        handler.removeCallbacks(this) // توقف بررسی
                        onWiFiEnabled() // ادامه عملکرد بعد از روشن شدن Wi-Fi
                    } else if (System.currentTimeMillis() - startTime < timeout) {
                        handler.postDelayed(this, interval) // بررسی مجدد
                    } else {
                        handler.removeCallbacks(this) // توقف بررسی در صورت تایم‌اوت
                        Toast.makeText(context, "Wi-Fi was not turned on within the timeout period", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            if (!wifiManager.isWifiEnabled) {
                Toast.makeText(context, "Please turn on your Wi-Fi", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                context.startActivity(intent)
                handler.post(wifiChecker)
            } else {
                onWiFiEnabled() // اگر Wi-Fi روشن بود، مستقیماً عملکرد بعدی را اجرا کن
            }
        }

        fun checkAndRequestGPS(context: Context, onGPSEnabled: () -> Unit) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val handler = Handler(Looper.getMainLooper())
            val interval: Long = 4000 // بررسی هر 4 ثانیه
            val timeout: Long = 20000 // تایم‌اوت 20 ثانیه
            val startTime = System.currentTimeMillis()

            val gpsChecker = object : Runnable {
                override fun run() {
                    val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

                    if (isGPSEnabled) {
                        handler.removeCallbacks(this) // توقف بررسی
                        onGPSEnabled() // ادامه عملکرد بعد از روشن شدن GPS
                    } else if (System.currentTimeMillis() - startTime < timeout) {
                        handler.postDelayed(this, interval) // بررسی مجدد
                    } else {
                        handler.removeCallbacks(this) // توقف بررسی در صورت تایم‌اوت
                        Toast.makeText(context, "GPS was not turned on within the timeout period", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val sharedPreferences = context.getSharedPreferences("GPS_Settings", Context.MODE_PRIVATE)
            val isGPSSettingsPageOpen = sharedPreferences.getBoolean("isGPSSettingsPageOpen", false)

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !isGPSSettingsPageOpen) {
                sharedPreferences.edit().putBoolean("isGPSSettingsPageOpen", true).apply()
                Toast.makeText(context, "Please turn on your GPS", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
                handler.post(gpsChecker)
            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                onGPSEnabled() // اگر GPS روشن بود، مستقیماً عملکرد بعدی را اجرا کن
            }

            handler.postDelayed({
                sharedPreferences.edit().putBoolean("isGPSSettingsPageOpen", false).apply()
            }, 2000) // تنظیم مجدد پرچم بعد از بازگشت از تنظیمات
        }

        fun checkAndRequestGPSAndWiFi(context: Context) {
            checkAndRequestGPS(context = context) {
                checkAndRequestWiFi(context = context) {
                    checkAndConnectToWiFi(context) // اتصال به Wi-Fi
                }
            }
        }


}

class InternetCheck(private val callback: (Boolean) -> Unit) : AsyncTask<Void, Void, Boolean>() {

    override fun doInBackground(vararg params: Void?): Boolean {
        return try {
            val sock = Socket()
            val socketAddress = InetSocketAddress("8.8.8.8", 53)
            sock.connect(socketAddress, 3000)
            sock.close()
            true
        } catch (e: IOException) {
            false
        }
    }

    override fun onPostExecute(result: Boolean) {
        callback(result)
    }
}