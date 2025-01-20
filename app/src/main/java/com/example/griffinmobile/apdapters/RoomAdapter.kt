package com.example.griffinmobile.apdapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.griffinmobile.R
import com.example.griffinmobile.mudels.rooms

class RoomAdapter(
    private val roomList: List<rooms?>,
    private val onRoomClick: (rooms) -> Unit // Callback برای بازگرداندن room_name
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnRoom: Button = itemView.findViewById(R.id.room_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_style_item, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = roomList[position]
        holder.btnRoom.text = room!!.room_name // تنظیم نام اتاق روی متن دکمه
        holder.btnRoom.setOnClickListener {
            room.room_name?.let { roomName -> onRoomClick(room) }
        }
    }

    override fun getItemCount(): Int = roomList.size
}