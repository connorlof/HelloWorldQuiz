package com.loftydevelopment.helloworldquiz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class OptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
    }

    fun saveListener(view: View){

        //TODO save account setting in database

        //TODO save application settings in shared pref

        finish()
        startActivity(Intent(this, MainActivity::class.java))

    }

    fun cancelListener(view: View){

        finish()
        startActivity(Intent(this, MainActivity::class.java))

    }
}
