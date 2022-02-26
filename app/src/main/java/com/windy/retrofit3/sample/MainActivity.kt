package com.windy.retrofit3.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import retrofit3.Retrofit3
import retrofit3.http.GET

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun test() {
        val build = Retrofit3.Builder().build()
    }
}