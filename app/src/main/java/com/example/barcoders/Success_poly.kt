package com.example.barcoders

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_success_glass.*
import kotlinx.android.synthetic.main.activity_success_poly.*

class Success_poly : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_poly)

        val str = intent.getStringExtra("name")
        polytxt.text = str
    }
}