package com.tailoredapps.codagram.ui.loginscreen

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.tailoredapps.codagram.MainView
import com.tailoredapps.codagram.R


class SplashScreen : AppCompatActivity() {



    lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        handler = Handler()
        handler.postDelayed({

            val intent = Intent(this, MainView::class.java)
            startActivity(intent)
            finish()
        },3000)


    }
}