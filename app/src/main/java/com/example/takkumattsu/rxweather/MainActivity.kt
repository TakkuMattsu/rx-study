package com.example.takkumattsu.rxweather

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val text = findViewById<TextView>(R.id.hello_text)
        val timer = Observable.interval(1, TimeUnit.SECONDS, Schedulers.newThread()).take(4)
        val counter = Observable.just("3", "2", "1", "Hello RX" )
                Observable
                .zip(timer, counter, BiFunction<Long, String, String> {_ , num -> num })
                .doOnEach { Log.d("RxWeather", "do on each $it") }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ text.text = it })
    }
}
