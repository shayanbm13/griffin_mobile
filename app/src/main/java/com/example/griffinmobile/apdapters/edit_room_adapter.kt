package com.example.griffinmobile.apdapters

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.griffinmobile.R
import com.example.griffinmobile.mudels.rooms

class adit_room_adapter(
    private val items: MutableList<rooms?>, private val app_activity: FragmentActivity, private val context: Context,
    private val onItemClick: (rooms, String) -> Unit // Callback برای مدیریت کلیک و ارسال background
) : RecyclerView.Adapter<adit_room_adapter.MyViewHolder>() {

    var isEditMode: Boolean = false // وضعیت ادیت


    // ViewHolder برای هر آیتم
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.room_name)
        val imageView5: ImageView = itemView.findViewById(R.id.imageView5)

        val itemmmm: ConstraintLayout = itemView.findViewById(R.id.item_latout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.room_item, parent, false)
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


        val item = items[position]
        holder.textView.text = item!!.room_name
        val imageName = item.room_image + "2"
        println(imageName)
        imageName?.let {
            val imageResource =
                context.resources.getIdentifier(it, "drawable", app_activity.packageName)
            if (imageResource != 0) {
                Glide.with(context)
                    .load(imageResource)
                    .into(holder.imageView5)
            }
        }


        if (isEditMode) { startShakeAnimation(holder.itemView) } else { stopShakeAnimation(holder.itemView) }
        // جدا کردن type و db_id

        if (isEditMode) {
            startShakeAnimation(holder.itemView)
        } else {
            stopShakeAnimation(holder.itemView)

        }

        // هندل کلیک
        holder.itemmmm.setOnClickListener {
            onItemClick(item,"c")


        }

        holder.itemmmm.setOnLongClickListener {
            onItemClick(item,"h")

            notifyDataSetChanged()
            true
        }

    }

    override fun getItemCount(): Int = items.size

//    fun getItemsOrder(): String {
//        return items.joinToString(",") { item ->
//            // جدا کردن type و db_id
//            val parts = item.split(":")
//            if (parts.size == 2) {
//                "${parts[0]}:${parts[1]}"  // فرمت خروجی مشابه "L:1" خواهد بود
//            } else {
//                item  // در صورتیکه فرمت صحیح نباشد، خود آیتم را برمی‌گرداند
//            }
//        }  // در نهایت کل لیست را درون براکت‌ها قرار می‌دهیم
//
//    }

    @SuppressLint("NotifyDataSetChanged")
//    fun updateData(newItems: List<String>) {
//        // جایگزینی آیتم‌های موجود با آیتم‌های جدید
//        items.clear()
//        items.addAll(newItems)
//
//        // پاک کردن وضعیت آیتم‌های قبلی، در صورت نیاز
//        itemStatuses.clear()
//
//        // اعلام تغییر داده‌ها به RecyclerView
//        notifyDataSetChanged()
//    }
//    fun resetData() {
//        val oldSize = items.size
//        items.clear()
////        items.addAll(newItems)
//        itemStatuses.clear()
//        notifyItemRangeRemoved(0, oldSize)
////        notifyItemRangeInserted(0, newItems.size)
//    }
    // متد برای جابه‌جا کردن آیتم‌ها
    fun onItemMove(fromPosition: Int, toPosition: Int) {
        val fromItem = items[fromPosition]
//        val fromStatus = itemStatuses[fromItem]

        // جابجایی آیتم‌ها در لیست
        items.removeAt(fromPosition)
        items.add(toPosition, fromItem)

        // حفظ وضعیت‌ها هنگام جابجایی
//        if (fromStatus != null) {
//            itemStatuses[fromItem] = fromStatus
//        }

        // اطمینان از اینکه موقعیت‌ها به‌درستی آپدیت می‌شوند
        notifyItemMoved(fromPosition, toPosition)
    }
}