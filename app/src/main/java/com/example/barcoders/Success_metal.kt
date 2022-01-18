package com.example.barcoders

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_success_metal.*

class Success_metal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_metal)

        val str = intent.getStringExtra("name")
        metaltxt.text = str
    }
}