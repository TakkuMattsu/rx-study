package com.example.takkumattsu.rxweather

import android.os.Bundle
import android.support.annotation.RawRes
import android.support.v7.app.AppCompatActivity
import android.util.Log
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
        val text = findViewById<TextView>(R.id.hello_text)
        readPrefecture(R.raw.prefectures)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { Log.d("RxWeather", "prefecture ${it.name}") }
    }

    private fun readPrefecture(@RawRes from: Int): Observable<Prefecture> {
        // jsonを読み込む
        val observablePrefecture: Observable<List<Prefecture>> = Observable
                .just(from) // Observable Int に変換
                .map {      // raw の json を読み込んで Observable<String> に変換
                    resources.openRawResource(it)
                            .bufferedReader()
                            .use { it.readText() }
                }
                .map {      // String を Gson で Array<Array<String> にして、 Observable<List<Prefecture> に変換
                    val objects = Gson().fromJson<Array<Array<String>>>(it, object : TypeToken<Array<Array<String>>>() {}.type)
                    objects.map {
                        Prefecture(it[0], it[1], it[2])
                    }
                }
        return observablePrefecture
                .flatMap { list ->
                    // Observable<List<Prefecture> を List<Observable<Preficture>>
                    val obs: List<Observable<Prefecture>> = list.map { Observable.just(it) }
                    // List<Observable<Preficture>> を 順番通りに Observable<Preficture> に変換
                    Observable.concat(obs)
                }
    }
}
