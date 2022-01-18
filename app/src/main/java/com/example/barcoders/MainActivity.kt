package com.example.barcoders

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.view.animation.Animation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.view.animation.AnimationUtils
import androidx.constraintlayout.motion.widget.Debug.getState

import com.google.zxing.integration.android.IntentIntegrator
import android.app.Application
import com.google.android.gms.common.config.GservicesValue.value


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var fab_open: Animation? = null
    private var fab_close: Animation? = null
    private var isFabOpen = false
    private var fab: FloatingActionButton? = null
    private var fab1: FloatingActionButton? = null
    private var fab2: FloatingActionButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fab_open = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close)
        fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab1 = findViewById<View>(R.id.fab1) as FloatingActionButton
        fab2 = findViewById<View>(R.id.fab2) as FloatingActionButton
        fab!!.setOnClickListener(this) //+버튼
        fab1!!.setOnClickListener(this) //휴지통 버튼
        fab2!!.setOnClickListener(this) //돋보기 버튼
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.fab -> anim()
            R.id.fab1 -> {
                anim()
                val intent = Intent(this@MainActivity, Maptivity::class.java)
                startActivity(intent)
            }
            R.id.fab2 -> {
                anim()
                Toast.makeText(this, "Button2", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun anim() {
        if (isFabOpen) {
            fab1!!.startAnimation(fab_close)
            fab2!!.startAnimation(fab_close)
            fab1!!.isClickable = false
            fab2!!.isClickable = false
            isFabOpen = false
        } else {
            fab1!!.startAnimation(fab_open)
            fab2!!.startAnimation(fab_open)
            fab1!!.isClickable = true
            fab2!!.isClickable = true
            isFabOpen = true
        }
    }


    fun  startBarcodeReader(view: View){
        IntentIntegrator(this).initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.contents !=null){
                //위 변수가 식별번호 저장
                // Toast.makeText(this, "scanned: ${result.contents} format: ${result.formatName}", Toast.LENGTH_LONG).show();
                val value = result.contents
                val intent = Intent(this, SubActivity::class.java)
                intent.putExtra("number",value)
                startActivity(intent);
            }
            else{
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()

            }


        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}