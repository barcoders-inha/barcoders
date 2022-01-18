package com.example.barcoders

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_success_glass.*
import kotlinx.android.synthetic.main.activity_success_vinyl.*

class Success_vinyl : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_vinyl)

        val str = intent.getStringExtra("name")
        vinyltxt.text = str
    }
}