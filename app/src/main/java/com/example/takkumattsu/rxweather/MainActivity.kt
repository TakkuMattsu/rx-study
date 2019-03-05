package com.example.takkumattsu.rxweather

import android.os.Bundle
import android.support.annotation.RawRes
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val listView = findViewById<ListView>(R.id.list)
        val adapter = PrefecturesAdapter(this)
        listView.adapter = adapter

        readPrefecture(R.raw.prefectures)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { prefecture: Prefecture ->
                    Weather.loadWeather(prefecture)
                }
                .subscribe({ prefectureWeather: PrefectureWeather ->
                    adapter.addOrReplace(prefectureWeather)
                })
    }

    private fun readPrefecture(@RawRes from: Int): Observable<Prefecture> {
        return Observable
                .just(from) // Observable Int に変換
                .map { id: Int ->
                    // raw の json を読み込んで Observable<String> に変換
                    resources.openRawResource(id)
                            .bufferedReader()
                            .use {
                                it.readText()
                            }
                }
                .map { json: String ->
                    // String を Gson で Array<Array<String> にして、 Observable<List<Prefecture> に変換
                    val objects = Gson().fromJson<Array<Array<String>>>(json, object : TypeToken<Array<Array<String>>>() {}.type)
                    objects.map { p: Array<String> ->
                        Prefecture(p[0], p[1], p[2])
                    }
                }.flatMap { list: List<Prefecture> ->
                    // Observable<List<Prefecture> を List<Observable<Preficture>>
                    val obs: List<Observable<Prefecture>> = list.map { p: Prefecture ->
                        Observable.just(p)
                    }
                    // List<Observable<Preficture>> を 順番通りに Observable<Preficture> に変換
                    Observable.concat(obs)
                }
    }
}
