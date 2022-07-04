package com.windy.retrofit3.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.windy.retrofit3.sample.databinding.ActivityMainBinding
import retrofit3.Retrofit3

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    fun test() {
        val build = Retrofit3.Builder().build()
    }
}