package com.loftydevelopment.helloworldquiz

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckBox

class OptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        val soundCheck = findViewById<CheckBox>(R.id.cbSound)
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPref.edit()

        soundCheck.isChecked = sharedPref.contains("checked") && sharedPref.getBoolean("checked", false)

        soundCheck?.setOnCheckedChangeListener{view, isChecked ->
            if(isChecked){
                editor.putBoolean("checked", true)
                editor.commit()
            }else{
                editor.putBoolean("checked", false)
                editor.commit()
            }
        }
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
