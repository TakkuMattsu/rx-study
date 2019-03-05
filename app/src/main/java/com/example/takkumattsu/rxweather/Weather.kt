package com.example.takkumattsu.rxweather

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

object Weather {
    // 都道府県名と null の weather が得られる
    // API アクセスを行い（モックでいい）、都道府県名と weather が設定された
    // PrefectureWeather が得られる
    fun loadWeather(prefecture: Prefecture): Observable<PrefectureWeather> {
        // 初期値 -> 取れた値
        // 取得して取れたらそのObservable<PrefectureWeather>を返す
        // エラーだったらリトライ処理
        // リトライに2回失敗したらエラーを含んだObservable<PrefectureWeather>を返す
        return loadWeather(prefecture.hepburn)
                .map { weather: String ->
                    PrefectureWeather(prefecture, weather)
                }
                .startWith(PrefectureWeather(prefecture, null))
                .retry {  }
    }

    private fun loadWeather(prefecture: String): Observable<String> {
        val loader = when (prefecture) {
            //"yamagata" -> YamagataLoader()
            "oita" -> OitaLoader()
            else -> DefaultLoader()
        }
        return loader.loadWeather(prefecture)
    }

    private interface WeatherLoader {
        fun loadWeather(prefecture: String): Observable<String> = Observable.just("sunny")
    }

    private class DefaultLoader : WeatherLoader

    // 山形は時間がかかる
    private class YamagataLoader : WeatherLoader {
        override fun loadWeather(prefecture: String): Observable<String> {
            // 5秒遅らせる
            return Observable.create<String> { emitter ->
                Thread.sleep(TimeUnit.SECONDS.toMillis(5))
                emitter.onNext("sunny")
                emitter.onComplete()
            }.observeOn(Schedulers.newThread())
        }
    }

    // 大分はエラーが一回くる
    private class OitaLoader : WeatherLoader {
        override fun loadWeather(prefecture: String): Observable<String> {
            return Observable.error<String>(Throwable("${prefecture} 取得失敗"))
        }
    }
}