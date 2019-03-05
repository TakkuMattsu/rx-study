package com.example.takkumattsu.rxweather

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView

data class PrefectureWeather(
        val prefecture: Prefecture,
        val weather: String?
) {
    override fun equals(other: Any?): Boolean {
        return prefecture == (other as? PrefectureWeather)?.prefecture
    }

}

class PrefecturesAdapter(context: Context): ArrayAdapter<PrefectureWeather>(context, 0) {

    private data class ViewHolder(
            val prefecture: TextView,
            val progress: ProgressBar,
            val weather: TextView
            )

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val target =convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.item_weather_and_prefecture, null)
        val holder = (target.tag as? ViewHolder) ?: {
            val viewHolder = ViewHolder(
                    target.findViewById(R.id.text),
                    target.findViewById(R.id.progress),
                    target.findViewById(R.id.text2)
            )
            target.tag = viewHolder
            viewHolder
        }()
        val item = getItem(position)
        holder.prefecture.text = item.prefecture.name
        holder.weather.text = item.weather
        holder.progress.visibility = item.weather?.let { View.GONE } ?: View.VISIBLE
        return target
    }

    fun addOrReplace(prefectureWeather: PrefectureWeather) {

        val idx = getPosition(prefectureWeather)
        if(idx >= 0) {
            remove(prefectureWeather)
            insert(prefectureWeather, idx)
        } else {
            add(prefectureWeather)
        }
    }
}