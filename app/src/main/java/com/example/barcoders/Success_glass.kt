package com.example.barcoders

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_success_glass.*

class Success_glass : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_glass)

      val str = intent.getStringExtra("name")
      glasstxt.text = str
    }
}