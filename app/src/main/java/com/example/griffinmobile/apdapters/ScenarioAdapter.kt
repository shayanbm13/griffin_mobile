package com.example.griffinmobile.apdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.griffinmobile.R
import com.example.griffinmobile.mudels.scenario

class ScenarioAdapter(
    private var items: List<scenario?>,
    private val onItemClick: (scenario) -> Unit
) : RecyclerView.Adapter<ScenarioAdapter.ViewHolder>() {

    private val clickStates = MutableList(items.size) { false }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: Button = itemView.findViewById(R.id.scenario_add_scenario)

        fun bind(item: scenario) {
            // تنظیم متن دکمه
            button.text = item.scenario_name

            // تنظیم تصویر اولیه بر اساس وضعیت کلیک
            val position = adapterPosition // دریافت موقعیت در آداپتر

            button.setOnClickListener {
                // تغییر وضعیت کلیک برای این مورد
                clickStates[position] = !clickStates[position]

                // اگر نیاز باشد با وضعیت کلیک کاری انجام دهید

                onItemClick(item)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.scenario_addscenario_model, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        if (item != null) {
            holder.bind(item)
        }
    }
    fun setItems(newItems: List<scenario?>) {
        items = newItems
        clickStates.clear()
        clickStates.addAll(List(newItems.size) { false }) // مقداردهی اولیه وضعیت کلیک برای موارد جدید
        notifyDataSetChanged() // در اینجا اهمیت دارد که RecyclerView را تازه کنید
    }

    override fun getItemCount(): Int {
        return items.size
    }
    fun clearItems() {
        items = emptyList()
        notifyDataSetChanged()
    }

}