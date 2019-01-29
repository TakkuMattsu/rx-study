package com.example.takkumattsu.rxweather

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val text = findViewById<TextView>(R.id.hello_text)
        Observable.just("hello Rx!!")
                .delay(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ text.text = it})

//        Observable.just(2,1)
//                .delay(3, TimeUnit.SECONDS)
//                .startWith(3)
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnEach({ Log.d("RxDebug", "$it")})
//                .subscribe(
//                        {text.text = "$it"},
//                        {},
//                        {text.text = "hello Rx!!"} )
        // justで [2,1]
        // delay(1)
        // startwith(3)
        // observeOn
        // onNextでtext.text
        // onCompleteでtext.text 固定で hello Rx!!
    }
}
