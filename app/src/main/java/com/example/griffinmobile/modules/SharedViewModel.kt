package com.example.griffinmobile.mudels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.griffinmobile.modules.home

//import com.example.griffinmobile.fragment.griffin_home_frags.lights

object  Config {
    var mode :String = ""

}

class SharedViewModel : ViewModel() {
    private val _shared_ssid = MutableLiveData<String>()
    private val _shared_style_image_rec = MutableLiveData<String>()
    private val _custom_room_name = MutableLiveData<String>()
    private val _rooms = MutableLiveData<List<String>>()
    private val _selected_rooms = MutableLiveData<rooms>()
    private val _selected_home = MutableLiveData<home>()
    private val _light_to_learn_list = MutableLiveData<List<Light?>>()
    private val _six_worker_list = MutableLiveData<List<six_workert?>>()
    private val _Scenario_list = MutableLiveData<List<scenario?>>()
    private val _temp_to_learn_list = MutableLiveData<List<Thermostst?>>()
    private val _plug_to_learn_list = MutableLiveData<List<Plug?>>()
    private val _valve_to_learn_list = MutableLiveData<List<valve?>>()
    private val _fan_to_learn_list = MutableLiveData<List<fan?>>()
    private val _curtain_to_learn_list = MutableLiveData<List<curtain?>>()
    private val _current_room = MutableLiveData<rooms?>()
    private val _current_room_list = MutableLiveData<List<rooms?>>()
    private val _Favorite_list = MutableLiveData<List<Favorite?>>()
    private val _current_temp = MutableLiveData<String?>()
    private val _current_camera = MutableLiveData<camera?>()
    private val _current_IR = MutableLiveData<IR?>()
    private val _cam_update = MutableLiveData<String?>()
    private val _IR_update = MutableLiveData<String?>()
    private val _style_update = MutableLiveData<String?>()
    private val _is_image_changed = MutableLiveData<String?>()
    private val _light_ref_status = MutableLiveData<String?>()
    private val _music_ref_status = MutableLiveData<String?>()
    private val _current_pole_six_wirker = MutableLiveData<String?>()
    private val _current_six_wirker = MutableLiveData<String?>()
    private val _current_device = MutableLiveData<Any?>()
    private val _current_scenario = MutableLiveData<scenario?>()
    private val _current_IR_learning = MutableLiveData<String?>()
    private val _dash_center_index = MutableLiveData<String?>()
    private val _current_devices = MutableLiveData<String?>()
    var mode = String
    private val _is_doing = MutableLiveData<String?>().apply { value = "not bussy" }

    private val udpMessage = MutableLiveData<String>()

    val countdownLiveData = MutableLiveData<Long>()


    val shared_ssid: LiveData<String>
        get() = _shared_ssid
    val shared_style_image_rec: LiveData<String>
        get() = _shared_style_image_rec
    val custom_room_name: LiveData<String>
        get() = _custom_room_name
    val rooms: LiveData<List<String>>
        get() = _rooms
    val selected_rooms: LiveData<rooms>
        get() = _selected_rooms
    val selected_home: LiveData<home>
        get() = _selected_home
    val light_to_learn_list: LiveData<List<Light?>>
        get() = _light_to_learn_list
    val six_worker_list: LiveData<List<six_workert?>>
        get() = _six_worker_list
    val Scenario_list: LiveData<List<scenario?>>
        get() = _Scenario_list
    val Favorite_list: LiveData<List<Favorite?>>
        get() = _Favorite_list
    val plug_to_learn_list: LiveData<List<Plug?>>
        get() = _plug_to_learn_list
    val valve_to_learn_list: LiveData<List<valve?>>
        get() = _valve_to_learn_list
    val fan_to_learn_list: LiveData<List<fan?>>
        get() = _fan_to_learn_list
    val temp_to_learn_list: LiveData<List<Thermostst?>>
        get() = _temp_to_learn_list
    val curtain_to_learn_list: LiveData<List<curtain?>>
        get() = _curtain_to_learn_list
    val current_temp: LiveData<String?>
        get() = _current_temp
    val current_room: LiveData<rooms?>
        get() = _current_room
    val current_room_list: LiveData<List<rooms?>>
        get() = _current_room_list
    val current_camera: LiveData<camera?>
        get() = _current_camera
    val current_IR: LiveData<IR?>
        get() = _current_IR
    val cam_update: LiveData<String?>
        get() = _cam_update
    val IR_update: LiveData<String?>
        get() = _IR_update
    val music_ref_status: LiveData<String?>
        get() = _cam_update
    val style_update: LiveData<String?>
        get() = _style_update
    val is_image_changed: LiveData<String?>
        get() = _is_image_changed
    val is_doing: LiveData<String?>
        get() = _is_doing
    val light_ref_status: LiveData<String?>
        get() = _light_ref_status
    val current_pole_six_wirker: LiveData<String?>
        get() = _current_pole_six_wirker
    val current_six_wirker: LiveData<String?>
        get() = _current_six_wirker
    val current_device: LiveData<Any?>
        get() = _current_device
    val current_scenario: LiveData<scenario?>
        get() = _current_scenario
    val current_IR_learning: LiveData<String?>
        get() = _current_IR_learning
    val dash_center_index: LiveData<String?>
        get() = _dash_center_index

    val current_devices: LiveData<String?>
        get() = _current_devices

//    val received_udp: LiveData<String?>
//        get() = _received_udp



    fun update_shared_ssid(newText: String) {
        _shared_ssid.value = newText
    }

    fun update_custom_room_name(newText: String) {
        _custom_room_name.value = newText
    }

    fun update_shared_style_image_rec(newText: String) {
        _shared_style_image_rec.value = newText
    }
    fun update_rooms(newText: List<String>) {
        _rooms.value = newText
    }
    fun update_selected_rooms(newText: rooms) {
        _selected_rooms.value = newText
    }
    fun update_selected_home(newText: home) {
        _selected_home.value = newText
    }
    fun update_six_worker_list(newText: List<six_workert?>) {
        _six_worker_list.value = newText
    }
    fun update_light_to_learn_list(newText: List<Light?>) {
        _light_to_learn_list.value = newText
    }
    fun update_Scenario_list(newText: List<scenario?>) {
        _Scenario_list.value = newText
    }
    fun update_curtain_to_learn_list(newText: List<curtain?>) {
        _curtain_to_learn_list.value = newText
    }
    fun update_temp_to_learn_list(newText: List<Thermostst?>) {
        _temp_to_learn_list.value = newText
    }
    fun update_plug_to_learn_list(newText: List<Plug?>) {
        _plug_to_learn_list.value = newText
    }
    fun update_valve_to_learn_list(newText: List<valve?>) {
        _valve_to_learn_list.value = newText
    }
    fun update_fan_to_learn_list(newText: List<fan?>) {
        _fan_to_learn_list.value = newText
    }
    fun update_current_temp(newText: String?) {
        _current_temp.value = newText
    }
    fun update__music_ref_status(newText: String?) {
        _music_ref_status.value = newText
    }
    fun update_current_room(newText: rooms?) {
        _current_room.value = newText
    }

    fun update_current_room_list(newText: List<rooms?>) {
        _current_room_list.value = newText
    }
    fun update_Favorite_list(newText: List<Favorite?>) {
        _Favorite_list.value = newText
    }
    fun update_current_camera(newText: camera?) {
        _current_camera.value = newText
    }
    fun update_current_IR(newText: IR?) {
        _current_IR.value = newText
    }
    fun update_cam_update(newText: String?) {
        _cam_update.value = newText
    }
    fun update_IR_update(newText: String?) {
        _IR_update.value = newText
    }
    fun update_style_update(newText: String?) {
        _style_update.value = newText
    }
    fun update_is_image_changed(newText: String?) {
        _is_image_changed.value = newText
    }
    fun update_is_doing(newText: String?) {
        _is_doing.value = newText
    }
    fun update_light_ref_status(newText: String?) {
        _light_ref_status.value = newText
    }
    fun update_current_pole_six_wirker(newText: String?) {
        _current_pole_six_wirker.value = newText
    }
    fun update_current_six_wirker(newText: String?) {
        _current_six_wirker.value = newText
    }
    fun update_current_device(newText: Any?) {
        _current_device.value = newText
    }

    fun update_current_scenario(newText: scenario?) {
        _current_scenario.value = newText
    }
    fun update_current_IR_learning(newText: String?) {
        _current_IR_learning.value = newText
    }
    fun update_dash_center_index(newText: String?) {
        _dash_center_index.value = newText
    }
    fun update_current_devices(newText: String?) {
        _current_devices.value = newText
    }

}