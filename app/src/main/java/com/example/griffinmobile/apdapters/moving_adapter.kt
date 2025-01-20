package com.example.griffinmobile.apdapters

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.griffinmobile.R
import com.example.griffinmobile.database.Temperature_db
import com.example.griffinmobile.database.curtain_db
import com.example.griffinmobile.database.fan_db
import com.example.griffinmobile.database.light_db
import com.example.griffinmobile.database.plug_db
import com.example.griffinmobile.database.valve_db

class moving_adapter(
    private val items: MutableList<String>, // لیست به فرمت type:db_id
    private val context: Context,
    private val onItemClick: (String, Int, ConstraintLayout, String) -> Unit // Callback برای مدیریت کلیک و ارسال background
) : RecyclerView.Adapter<moving_adapter.MyViewHolder>() {

    var isEditMode: Boolean = false // وضعیت ادیت
    private val itemStatuses: MutableMap<String, String> = mutableMapOf() // ذخیره وضعیت هر آیتم به صورت موقتی

    // ViewHolder برای هر آیتم
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
        val background: ConstraintLayout = itemView.findViewById(R.id.background)
        val itemmmm: ConstraintLayout = itemView.findViewById(R.id.itemmmm)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.devices_item, parent, false)
        return MyViewHolder(view)
    }

    // شروع انیمیشن لرزش
    private fun startShakeAnimation(view: View) {
        val animator = ObjectAnimator.ofFloat(view, "rotation", -2f, 2f).apply {
            duration = 80 // مدت زمان هر حرکت
            repeatMode = ObjectAnimator.REVERSE // حرکت رفت و برگشتی
            repeatCount = ObjectAnimator.INFINITE // تکرار بی‌نهایت
        }
        animator.start()
        view.tag = animator // نگه‌داری انیمیشن در View برای مدیریت توقف
    }

    // توقف انیمیشن لرزش
    @SuppressLint("NotifyDataSetChanged")
    private fun stopShakeAnimation(view: View) {
        val animator = view.tag as? ObjectAnimator
        animator?.cancel()
        view.rotation = 0f // بازنشانی چرخش
//        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val light_helper = light_db.getInstance(context)
        val fan_db_helper = fan_db.getInstance(context)
        val plug_db_helper = plug_db.getInstance(context)
        val curtain_db_helper = curtain_db.getInstance(context)
        val valve_db_helper = valve_db.getInstance(context)
        val termostat_db_helper = Temperature_db.getInstance(context)

        val item = items[position]
        holder.textView.text = item

        if (isEditMode) { startShakeAnimation(holder.itemView) } else { stopShakeAnimation(holder.itemView) }
        // جدا کردن type و db_id
        val parts = item.split(":")
        if (parts.size == 2) {
            val type = parts[0] // نوع آیتم
            var my_current_status = itemStatuses[item] ?: ""  // وضعیت اولیه از itemStatuses

            // تغییر بک‌گراند بر اساس نوع
            val backgroundRes = when (type) {
                "L" -> {
                    val lightStatus = light_helper.getLightsByid(parts[1])?.status
                    if (lightStatus == "off") {
                        my_current_status = "0"
                        itemStatuses[item] = my_current_status // ذخیره وضعیت
                        R.drawable.light_background_off
                    } else {
                        my_current_status = "1"
                        itemStatuses[item] = my_current_status // ذخیره وضعیت
                        R.drawable.light_background_on
                    }
                }
                "F" -> {
                    val fanStatus = fan_db_helper.get_from_db_fan(parts[1].toInt())?.status
                    if (fanStatus == "0") {
                        my_current_status = "0"
                        itemStatuses[item] = my_current_status // ذخیره وضعیت
                        R.drawable.fan_background_off
                    } else {
                        my_current_status = "1"
                        itemStatuses[item] = my_current_status // ذخیره وضعیت
                        R.drawable.fan_background_on
                    }
                }
                "P" -> {
                    val plugStatus = plug_db_helper.get_from_db_Plug(parts[1].toInt())?.status
                    if (plugStatus == "0") {
                        my_current_status = "0"
                        itemStatuses[item] = my_current_status // ذخیره وضعیت
                        R.drawable.plug_background_off
                    } else {
                        my_current_status = "1"
                        itemStatuses[item] = my_current_status // ذخیره وضعیت
                        R.drawable.plug_background_on
                    }
                }
                "V" -> {
                    val valveStatus = valve_db_helper.get_from_db_valve(parts[1].toInt())?.status
                    if (valveStatus == "0") {
                        my_current_status = "0"
                        itemStatuses[item] = my_current_status // ذخیره وضعیت
                        R.drawable.valve_background_off
                    } else {
                        my_current_status = "1"
                        itemStatuses[item] = my_current_status // ذخیره وضعیت
                        R.drawable.valve_background_on
                    }
                }
                "T" -> {
                    val termoststStatus = termostat_db_helper.get_from_db_Temprature(parts[1].toInt())?.on_off
                    if (termoststStatus=="0"){
                        my_current_status = "0"

                        itemStatuses[item] = my_current_status
                        R.drawable.termostat_background_off

                    }else{


                        my_current_status = "1"

                        itemStatuses[item] = my_current_status
                        R.drawable.termostat_background_on


                    }

                }

                else -> {}
            }

            // تنظیم بک‌گراند به آیتم
            holder.background.setBackgroundResource(backgroundRes as Int)

            // شروع یا توقف انیمیشن لرزش بر اساس حالت ادیت
            if (isEditMode) {
                startShakeAnimation(holder.itemView)
            } else {
                stopShakeAnimation(holder.itemView)

            }

            // هندل کلیک
            holder.itemmmm.setOnClickListener {
                if (!isEditMode){

                    val dbId = parts[1].toIntOrNull() ?: -1
                    if (dbId != -1) {
                        println(type)
                        // تغییر بک‌گراند بعد از کلیک
                        val newBackgroundRes = when (type) {
                            "L" -> if (my_current_status == "0") {
                                my_current_status = "1"
                                itemStatuses[item] = my_current_status // ذخیره وضعیت
                                R.drawable.light_background_on
                            } else {
                                my_current_status = "0"
                                itemStatuses[item] = my_current_status // ذخیره وضعیت
                                R.drawable.light_background_off
                            }
                            "F" -> if (my_current_status == "0") {
                                my_current_status = "1"
                                itemStatuses[item] = my_current_status // ذخیره وضعیت
                                R.drawable.fan_background_on
                            } else {
                                my_current_status = "0"
                                itemStatuses[item] = my_current_status // ذخیره وضعیت
                                R.drawable.fan_background_off
                            }
                            "P" -> if (my_current_status == "0") {
                                my_current_status = "1"
                                itemStatuses[item] = my_current_status // ذخیره وضعیت
                                R.drawable.plug_background_on
                            } else {
                                my_current_status = "0"
                                itemStatuses[item] = my_current_status // ذخیره وضعیت
                                R.drawable.plug_background_off
                            }
                            "V" -> if (my_current_status == "0") {
                                my_current_status = "1"
                                itemStatuses[item] = my_current_status // ذخیره وضعیت
                                R.drawable.valve_background_on
                            } else {
                                my_current_status = "0"
                                itemStatuses[item] = my_current_status // ذخیره وضعیت
                                R.drawable.valve_background_off
                            }
                            "T" -> if (my_current_status == "0") {
                                my_current_status = "1"
                                itemStatuses[item] = my_current_status // ذخیره وضعیت
                                R.drawable.termostat_background_on
                            } else {
                                my_current_status = "0"
                                itemStatuses[item] = my_current_status // ذخیره وضعیت
                                R.drawable.termostat_background_off
                            }

                            else -> {}
                        }

                        holder.background.setBackgroundResource(newBackgroundRes as Int)

                        // ارسال background به کال‌بک به همراه type و dbId
                        onItemClick(type, dbId, holder.background, my_current_status)
                    } else {
                        Log.e("AdapterError", "Invalid db_id format for item: $item")
                    }
                }else{

                    val dbId = parts[1].toIntOrNull() ?: -1
                    if (dbId != -1) {
                        println(type)



                        // ارسال background به کال‌بک به همراه type و dbId
                        onItemClick(type, dbId, holder.background, my_current_status)
                    } else {
                        Log.e("AdapterError", "Invalid db_id format for item: $item")
                    }
                }

            }
        } else {
            Log.e("AdapterError", "Invalid item format: $item")
        }
    }

    override fun getItemCount(): Int = items.size

    fun getItemsOrder(): String {
        return items.joinToString(",") { item ->
            // جدا کردن type و db_id
            val parts = item.split(":")
            if (parts.size == 2) {
                "${parts[0]}:${parts[1]}"  // فرمت خروجی مشابه "L:1" خواهد بود
            } else {
                item  // در صورتیکه فرمت صحیح نباشد، خود آیتم را برمی‌گرداند
            }
        }  // در نهایت کل لیست را درون براکت‌ها قرار می‌دهیم

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newItems: List<String>) {
        // جایگزینی آیتم‌های موجود با آیتم‌های جدید
        items.clear()
        items.addAll(newItems)

        // پاک کردن وضعیت آیتم‌های قبلی، در صورت نیاز
        itemStatuses.clear()

        // اعلام تغییر داده‌ها به RecyclerView
        notifyDataSetChanged()
    }
    fun resetData() {
        val oldSize = items.size
        items.clear()
//        items.addAll(newItems)
        itemStatuses.clear()
        notifyItemRangeRemoved(0, oldSize)
//        notifyItemRangeInserted(0, newItems.size)
    }
    // متد برای جابه‌جا کردن آیتم‌ها
    fun onItemMove(fromPosition: Int, toPosition: Int) {
        val fromItem = items[fromPosition]
        val fromStatus = itemStatuses[fromItem]

        // جابجایی آیتم‌ها در لیست
        items.removeAt(fromPosition)
        items.add(toPosition, fromItem)

        // حفظ وضعیت‌ها هنگام جابجایی
        if (fromStatus != null) {
            itemStatuses[fromItem] = fromStatus
        }

        // اطمینان از اینکه موقعیت‌ها به‌درستی آپدیت می‌شوند
        notifyItemMoved(fromPosition, toPosition)
    }
}