package com.example.griffinmobile.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.griffinmobile.R
import com.example.griffinmobile.apdapters.adit_room_adapter
import com.example.griffinmobile.database.curtain_db
import com.example.griffinmobile.database.fan_db
import com.example.griffinmobile.database.light_db
import com.example.griffinmobile.database.plug_db
import com.example.griffinmobile.database.room_devices_db
import com.example.griffinmobile.database.Temperature_db

import com.example.griffinmobile.database.rooms_db
import com.example.griffinmobile.database.six_workert_db
import com.example.griffinmobile.database.valve_db
import com.example.griffinmobile.mudels.SharedViewModel
import com.example.griffinmobile.mudels.rooms


class room_edit : Fragment() {


    var adapter : adit_room_adapter?=null
    val sharedViewModel :SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_room_edit, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val edit_rooms_receycelerview = requireView().findViewById<RecyclerView>(R.id.edit_rooms_receycelerview)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2) // تعداد ستون‌ها
        edit_rooms_receycelerview.layoutManager = gridLayoutManager

        val add_btn = requireView().findViewById<Button>(R.id.add_btn)

        add_btn.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.main, room_edit_2()).commit()
            val emptyroom = rooms()
            emptyroom.room_name = "emptyy12"
            sharedViewModel.update_current_room(emptyroom)
        }


        val room_db = rooms_db.getInstance(requireContext())
        val my_rooms = room_db.getAllRooms()



        if (my_rooms.isNotEmpty()){

            sharedViewModel.update_current_room_list(my_rooms)
            sharedViewModel.current_room_list.observe(viewLifecycleOwner , Observer { room_list ->


                adapter = adit_room_adapter(room_list.toMutableList(),requireActivity() ,requireContext()){ room  ,  click_type->

                    if (click_type == "c"){
                        println(room.room_name)


                        sharedViewModel.update_current_room(room)
                        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.main, room_edit_2()).commit()
                    }else{

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
                            val tempDb=Temperature_db.getInstance(requireContext())
                            val valveDb= valve_db.getInstance(requireContext())
                            val sixcDB= six_workert_db.getInstance(requireContext())
                            val lights = lightDb.getAllLightsByRoomName(room.room_name)
                            val curtains = curtainDb.getAllcurtainsByRoomName(room.room_name)
                            val plugs = plugtDb.getPlugsByRoomName(room.room_name)
                            val valve = valveDb.getvalvesByRoomName(room.room_name)
                            val fans = fanDb.getfansByRoomName(room!!.room_name)
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
                            room_db.delete_from_db_rooms(room.id)

                            room_devices_db.getInstance(requireContext()).deleteRoomByRoomId(room.id.toString())
                            Toast.makeText(requireContext(), "Room and devices deleted", Toast.LENGTH_SHORT).show()

                            dialog.dismiss()
                            sharedViewModel.update_current_room_list(room_db.getAllRooms())
                        }
                        cancel_delete.setOnClickListener {
                            Toast.makeText(requireContext(), "Canceled", Toast.LENGTH_SHORT).show()

                            dialog.dismiss()
                        }


                    }
                    
                    

                    
                }
                edit_rooms_receycelerview.adapter = adapter

            })


            val itemTouchHelperCallback = object : ItemTouchHelper.Callback() {
                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    val adapter = recyclerView.adapter as adit_room_adapter
                    return if (adapter.isEditMode) {
                        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                        val swipeFlags = 0
                        makeMovementFlags(dragFlags, swipeFlags)
                    } else {
                        makeMovementFlags(0, 0)
                    }
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition
                    (recyclerView.adapter as adit_room_adapter).onItemMove(fromPosition, toPosition)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // Swipe غیرفعال است
                }

                override fun isLongPressDragEnabled(): Boolean {
                    println("presssss")
                    return (edit_rooms_receycelerview.adapter as adit_room_adapter).isEditMode
                }
            }

            // اضافه کردن ItemTouchHelper به RecyclerView
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(edit_rooms_receycelerview)
            
            

        }



    }

    override fun onResume() {
        super.onResume()
        val roomsDb = rooms_db.getInstance(requireContext())
        sharedViewModel.update_current_room_list(roomsDb.getAllRooms())
        try {

            println(roomsDb.getAllRooms()[0]!!.room_image)
        }catch (e:Exception){
            println(e)
        }

    }

}