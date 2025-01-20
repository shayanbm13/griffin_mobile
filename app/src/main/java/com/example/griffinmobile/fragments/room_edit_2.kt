package com.example.griffinmobile.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.griffinmobile.R
import com.example.griffinmobile.apdapters.stylesAdapter
import com.example.griffinmobile.database.Temperature_db
import com.example.griffinmobile.database.curtain_db
import com.example.griffinmobile.database.fan_db
import com.example.griffinmobile.database.light_db
import com.example.griffinmobile.database.plug_db
import com.example.griffinmobile.database.room_devices_db
import com.example.griffinmobile.database.rooms_db
import com.example.griffinmobile.database.six_workert_db
import com.example.griffinmobile.database.valve_db
import com.example.griffinmobile.mudels.SharedViewModel
import com.example.griffinmobile.mudels.rooms


class room_edit_2 : Fragment() {

    val sharedViewModel : SharedViewModel by activityViewModels()
    private lateinit var imageAdapter: stylesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Define custom behavior for back press in this fragment
                if (shouldHandleBack()) {
                    // Do something like navigating back in the fragment
                } else {
                    // Let the system handle it or finish the fragment
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        })

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_room_edit_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        val ok_btn = view.findViewById<Button>(R.id.ok_btn)
        val Delete_btn = view.findViewById<Button>(R.id.Delete_btn)
        val style_1 = view.findViewById<Button>(R.id.style_1)
        val style_2 = view.findViewById<Button>(R.id.style_2)
        val style_3 = view.findViewById<Button>(R.id.style_3)
        val home_style_recyceler_view = view.findViewById<RecyclerView>(R.id.home_style_recyceler_view)
        val room_image = view.findViewById<ImageView>(R.id.imageView5)
        val style_name = view.findViewById<TextView>(R.id.style_name)
        val select_image_help = view.findViewById<TextView>(R.id.select_image_help)
        val room_name = view.findViewById<EditText>(R.id.room_name)
        val close_btn = view.findViewById<Button>(R.id.close_btn)



        val roomsDb=rooms_db.getInstance(requireContext())
        val roomDevicesDb=room_devices_db.getInstance(requireContext())
        var new_name  =  ""
        close_btn.setOnClickListener {

            shouldHandleBack()


        }




        val image_style1= listOf(R.drawable.s1_bathroom1_1,R.drawable.s1_bathroom2_1,R.drawable.s1_bathroom3_1,R.drawable.s1_dining_room_1,R.drawable.s1_gust_room_1,R.drawable.s1_kids_room_1,R.drawable.s1_kitchen_1,R.drawable.s1_kitchen2_1,R.drawable.s1_living_room_1,R.drawable.s1_master_room_1,R.drawable.s1_room_1,R.drawable.s1_tv_room_1,R.drawable.s1_yard_1,)
        val image_style2= listOf(R.drawable.s2_bathroom1_1,R.drawable.s2_bathroom2_1,R.drawable.s2_bathroom3_1,R.drawable.s2_dining_room_1,R.drawable.s2_gust_room_1,R.drawable.s2_kids_room_1,R.drawable.s2_kitchen_1,R.drawable.s2_kitchen2_1,R.drawable.s2_living_room_1,R.drawable.s2_master_room_1,R.drawable.s2_room_1,R.drawable.s2_tv_room_1,R.drawable.s2_yard_1,)
        val image_style3= listOf(R.drawable.s3_bathroom1_1,R.drawable.s3_bathroom2_1,R.drawable.s3_bathroom3_1,R.drawable.s3_dining_room_1,R.drawable.s3_gust_room_1,R.drawable.s3_kids_room_1,R.drawable.s3_kitchen_1,R.drawable.s3_kitchen2_1,R.drawable.s3_living_room_1,R.drawable.s3_master_room_1,R.drawable.s3_room_1,R.drawable.s3_tv_room_1,R.drawable.s3_yard_1,)
        val image_officestyle= listOf(R.drawable.of_confrance_room_1,R.drawable.of_kitchen_room_1,R.drawable.of_managment_room_1,R.drawable.of_managment2_room_1,R.drawable.of_office_room_1,R.drawable.of_office_room2_1,R.drawable.of_wc_1)


        fun change_style(image_style:List<Int>?){


            home_style_recyceler_view.layoutManager = GridLayoutManager(activity, 2)

//                image_style?.let { stylesAdapter(it,this) }!!
            home_style_recyceler_view.adapter = stylesAdapter(image_style){i ->
                println(i.dropLast(1))
                new_name=i.dropLast(1)
                select_image_help.visibility=View.GONE
                val imageName = new_name + "2"
                println(imageName)
                when(imageName[1]){
                    '1' -> style_name.setText("Style : 1")
                    '2' ->style_name.setText("Style : 2")
                    '3' ->style_name.setText("Style : 3")
                }
                imageName?.let {
                    val imageResource =
                        resources.getIdentifier(it, "drawable", requireActivity().packageName)
                    if (imageResource != 0) {
                        Glide.with(requireContext())
                            .load(imageResource)
                            .into(room_image)
                    }
                }

            }
        }
        fun style1(){
            style_1.setBackgroundResource(R.drawable.selected_btn)
            style_2.setBackgroundResource(R.drawable.background_gradiant_2)
            style_3.setBackgroundResource(R.drawable.background_gradiant_2)
            home_style_recyceler_view.visibility = View.INVISIBLE
            val animation2 = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
            home_style_recyceler_view.startAnimation(animation2)
            home_style_recyceler_view.visibility = View.VISIBLE
            change_style(image_style1)


        }
        fun style2(){
            style_1.setBackgroundResource(R.drawable.background_gradiant_2)
            style_2.setBackgroundResource(R.drawable.selected_btn)
            style_3.setBackgroundResource(R.drawable.background_gradiant_2)
            home_style_recyceler_view.visibility = View.INVISIBLE
            val animation2 = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
            home_style_recyceler_view.startAnimation(animation2)
            home_style_recyceler_view.visibility = View.VISIBLE
            change_style(image_style2)

        }
        fun style3(){
            style_1.setBackgroundResource(R.drawable.background_gradiant_2)
            style_2.setBackgroundResource(R.drawable.background_gradiant_2)
            style_3.setBackgroundResource(R.drawable.selected_btn)
            home_style_recyceler_view.visibility = View.INVISIBLE
            val animation2 = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
            home_style_recyceler_view.startAnimation(animation2)
            home_style_recyceler_view.visibility = View.VISIBLE
            change_style(image_style3)
        }












        sharedViewModel.current_room.observe(viewLifecycleOwner , Observer { room ->

            if (room!!.room_name == "emptyy12"){
                Delete_btn.visibility=View.INVISIBLE
                select_image_help.visibility=View.VISIBLE
                room_name.setText("")
                style_name.setText("Style : ")

                val imageName = room.room_image + "2"
                when(imageName[1]){
                    '1' -> style_name.setText("Style : 1")
                    '2' ->style_name.setText("Style : 2")
                    '3' ->style_name.setText("Style : 3")
                }
                println(imageName)
                imageName?.let {
                    val imageResource =
                        resources.getIdentifier(it, "drawable", requireActivity().packageName)
                    if (imageResource != 0) {
                        Glide.with(requireContext())
                            .load(imageResource)
                            .into(room_image)
                    }
                }


                style_1.setOnClickListener {
                    style1()
                }
                style_2.setOnClickListener {
                    style2()
                }
                style_3.setOnClickListener {
                    style3()
                }


                room_image.setOnClickListener {
                    style1()

                    val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
                    val animation2 = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
                    home_style_recyceler_view.startAnimation(animation2)
                    home_style_recyceler_view.visibility = View.VISIBLE


                    style_1.startAnimation(animation)
                    style_1.visibility = View.VISIBLE

                    style_2.startAnimation(animation)
                    style_2.visibility = View.VISIBLE

                    style_3.startAnimation(animation)
                    style_3.visibility = View.VISIBLE

                }
                ok_btn.setOnClickListener {
                    val all_names = mutableListOf<String>()
                    for (room in roomsDb.getAllRooms()){
                        room!!.room_name?.let { it1 -> all_names.add(it1) }

                    }


                    val new_rooms = rooms()

                    var is_pass1 = 0
                    var is_pass2 = 0
                    var is_pass3 = 0

                    if (new_name!= ""){
                        println(new_name)
                        new_rooms.room_image=new_name
                        println("1")
                        is_pass1 = 1
                    }else {
                        Toast.makeText(requireContext(), "Please enter a image ", Toast.LENGTH_SHORT).show()
                        is_pass1 = 0
                    }
                    if ((room_name.text.toString().trim()!= "" ) && ( room_name.text.isNotEmpty())){
                        new_rooms.room_name=room_name.text.toString().trim()
                        is_pass2 = 1
                        println("1")
                    }else {
                        Toast.makeText(requireContext(), "Please enter a name ", Toast.LENGTH_SHORT).show()
                        is_pass2 = 0
                    }
                    if (!(all_names.contains(room_name.text.toString().trim())) && !(all_names.contains(room_name.text.toString())) ){
                        new_rooms.room_name=room_name.text.toString().trim()
                        is_pass3 = 1
                        println("1")
                    }else {
                        Toast.makeText(requireContext(), "Please enter a unique  name ", Toast.LENGTH_SHORT).show()
                        is_pass3 = 0
                    }

                    if (is_pass1 == 1&&is_pass2 == 1&&is_pass3 == 1){
                        println(new_rooms.room_image)
                        println(room_name.text.toString())
                        room_name.text.toString()
                        roomsDb.set_to_db_rooms(new_rooms)
                        shouldHandleBack()


                    }



                }

            }else{
                Delete_btn.visibility=View.VISIBLE
                select_image_help.visibility=View.GONE
                room_name.setText(room!!.room_name)

                val imageName = room.room_image + "2"
                when(imageName[1]){
                    '1' -> style_name.setText("Style : 1")
                    '2' ->style_name.setText("Style : 2")
                    '3' ->style_name.setText("Style : 3")
                }
                println(imageName)
                imageName?.let {
                    val imageResource =
                        resources.getIdentifier(it, "drawable", requireActivity().packageName)
                    if (imageResource != 0) {
                        Glide.with(requireContext())
                            .load(imageResource)
                            .into(room_image)
                    }
                }

                room!!.room_image
                room!!.id



                style_1.setOnClickListener {
                    style1()
                }
                style_2.setOnClickListener {
                    style2()
                }
                style_3.setOnClickListener {
                    style3()
                }


                room_image.setOnClickListener {
                    style1()

                    val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
                    val animation2 = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
                    home_style_recyceler_view.startAnimation(animation2)
                    home_style_recyceler_view.visibility = View.VISIBLE


                    style_1.startAnimation(animation)
                    style_1.visibility = View.VISIBLE

                    style_2.startAnimation(animation)
                    style_2.visibility = View.VISIBLE

                    style_3.startAnimation(animation)
                    style_3.visibility = View.VISIBLE

                }
                ok_btn.setOnClickListener {



                    room.room_name=room_name.text.toString().trim()
                    if (new_name!= ""){
                        room.room_image=new_name

                    }
                    roomsDb.updateRoomById(room.id,room)
                    shouldHandleBack()


                }

                Delete_btn.setOnClickListener {
                    val dialog = Dialog(requireContext())
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    // تنظیم لایه دلخواه برای دیالوگ
                    dialog.setContentView(R.layout.delete_all_or_pole)
                    val text_msg = dialog.findViewById<TextView>(R.id.text_msg)
                    val yes_delete = dialog.findViewById<Button>(R.id.yes_delete)
                    val cancel_delete = dialog.findViewById<Button>(R.id.cancel_delete)
                    text_msg.setText("Are you sure you want to delete this room? all devices in this room will be deleted ")
                    yes_delete.setText("Delete")
                    cancel_delete.setText("Cancel")
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                    yes_delete.setOnClickListener {


                        val curtainDb= curtain_db.getInstance(requireContext())
                        val fanDb= fan_db.getInstance(requireContext())
                        val lightDb= light_db.getInstance(requireContext())
                        val plugtDb= plug_db.getInstance(requireContext())
                        val tempDb= Temperature_db.getInstance(requireContext())
                        val valveDb= valve_db.getInstance(requireContext())
                        val sixcDB= six_workert_db.getInstance(requireContext())
                        val lights = lightDb.getAllLightsByRoomName(room.room_name)
                        val fans = fanDb.getfansByRoomName(room!!.room_name)
                        val curtains = curtainDb.getAllcurtainsByRoomName(room.room_name)
                        val plugs = plugtDb.getPlugsByRoomName(room.room_name)
                        val valve = valveDb.getvalvesByRoomName(room.room_name)
                        val temps  = tempDb.getThermostatsByRoomName(room.room_name)
                        val sixc = sixcDB.getAllsix_workerts()

                        for (light in lights){

                            if (light != null) {
                                lightDb.delete_from_db_light(light.id)
                            }
                            for (six in sixc){
                                if (six!!.mac == light!!.mac){
                                    sixcDB.delete_from_db_six_workert(six.id)
                                }
                            }

                        }
                        for (fan in fans){
                            if (fan != null) {
                                fanDb.delete_from_db_fan(fan.id)
                            }
                        }
                        for(curtain in curtains){
                            if (curtain != null) {
                                curtainDb.delete_from_db_curtain(curtain.id)
                            }
                        }
                        for (plug in plugs){
                            if (plug != null) {
                                plugtDb.delete_from_db_Plug(plug.id)
                            }
                        }

                        for (valve in valve){
                            if (valve != null) {
                                valveDb.delete_from_db_valve(valve.id)
                            }
                        }
                        for (temp in temps){
                            if (temp != null) {
                                tempDb.delete_from_db_Temprature(temp.id)
                            }
                        }
                        roomsDb.delete_from_db_rooms(room.id)

                        room_devices_db.getInstance(requireContext()).deleteRoomByRoomId(room.id.toString())
                        Toast.makeText(requireContext(), "Room and devices deleted", Toast.LENGTH_SHORT).show()

                        roomDevicesDb.deleteRoomByRoomId(room.id.toString())
                        dialog.dismiss()
                        sharedViewModel.update_current_room_list(roomsDb.getAllRooms())

                        shouldHandleBack()
                    }
                    cancel_delete.setOnClickListener {
                        Toast.makeText(requireContext(), "Canceled", Toast.LENGTH_SHORT).show()

                        dialog.dismiss()
                    }




                }

            }



        })




    }



    private fun shouldHandleBack(): Boolean {
        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.main, room_edit()).commit()
        return false
    }
}