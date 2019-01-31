package com.example.takkumattsu.rxweather

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

//data class PrefectureWeather(
//        val prefecture: Prefecture,
//        val weather: String?,
//        val error: Throwable?
//) {
//    override fun equals(other: Any?): Boolean {
//        return prefecture == (other as? PrefectureWeather)?.prefecture
//    }
//}
sealed class LoadResult(open val prefecture: Prefecture) {
    data class Success(override val prefecture: Prefecture, val weather: String): LoadResult(prefecture) {
        override fun equals(other: Any?): Boolean {
            return super.equals(other)
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }
    }

    data class Failed(override val prefecture: Prefecture ,val throwable: Throwable): LoadResult(prefecture) {
        override fun equals(other: Any?): Boolean {
            return super.equals(other)
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }
    }

    data class InProgress(override val prefecture: Prefecture): LoadResult(prefecture) {
        override fun equals(other: Any?): Boolean {
            return super.equals(other)
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }
    }

    override fun equals(other: Any?): Boolean {
        return prefecture == (other as? LoadResult)?.prefecture
    }

    override fun hashCode(): Int {
        return prefecture.hashCode()
    }
}

class PrefecturesAdapter(context: Context): ArrayAdapter<LoadResult>(context, 0) {

    private data class ViewHolder(
            val prefecture: TextView,
            val progress: ProgressBar,
            val weather: TextView,
            val retryButton: Button
            )
    //val onClickRetry2: ((Prefecture) -> Unit)? = null
    private val clickRetryObserver = PublishSubject.create<Prefecture>()
    val onClickRetry: Observable<Prefecture> = clickRetryObserver

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val target =convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.item_weather_and_prefecture, null)
        val holder = (target.tag as? ViewHolder) ?: {
            val viewHolder = ViewHolder(
                    target.findViewById(R.id.text),
                    target.findViewById(R.id.progress),
                    target.findViewById(R.id.text2),
                    target.findViewById(R.id.button)
            )
            target.tag = viewHolder
            viewHolder
        }()

        val item = getItem(position)
        when(item) {
            is LoadResult.Success -> {
                holder.prefecture.text = item.prefecture.name
                holder.weather.text = item.weather
                holder.weather.visibility = View.VISIBLE
                holder.progress.visibility = View.GONE
                holder.retryButton.visibility = View.GONE
            }
            is LoadResult.Failed -> {
                holder.prefecture.text = item.prefecture.name
                holder.weather.visibility = View.GONE
                holder.progress.visibility = View.GONE
                holder.retryButton.visibility = View.VISIBLE
            }
            is LoadResult.InProgress -> {
                holder.prefecture.text = item.prefecture.name
                holder.progress.visibility = View.VISIBLE
                holder.retryButton.visibility = View.GONE
                holder.weather.visibility = View.GONE
            }
        }
        holder.retryButton.setOnClickListener {
            clickRetryObserver.onNext(item.prefecture)
        }
        return target
    }

    fun addOrReplace(loadResult: LoadResult) {

        val idx = getPosition(loadResult)
        if(idx >= 0) {
            remove(loadResult)
            insert(loadResult, idx)
        } else {
            add(loadResult)
        }
    }
}