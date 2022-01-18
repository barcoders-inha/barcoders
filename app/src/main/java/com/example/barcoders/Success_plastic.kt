package com.example.barcoders

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_success_glass.*
import kotlinx.android.synthetic.main.activity_success_plastic.*

class Success_plastic : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_plastic)

        val str = intent.getStringExtra("name")
        plastictxt.text = str
    }
}