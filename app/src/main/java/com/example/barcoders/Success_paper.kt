package com.example.barcoders

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_success_paper.*

class Success_paper : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_paper)

        val str = intent.getStringExtra("name")
        papertxt.text = str
    }
}