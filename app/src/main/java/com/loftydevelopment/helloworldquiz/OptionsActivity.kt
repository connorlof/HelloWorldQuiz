package com.loftydevelopment.helloworldquiz

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class OptionsActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_activity_otions)

        mAuth = FirebaseAuth.getInstance()
        val soundCheck = findViewById<CheckBox>(R.id.cbSound)
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPref.edit()
        val accountylyt = findViewById<ConstraintLayout>(R.id.accountlyt)
        val tvSingIn = findViewById<TextView>(R.id.tvSignIn)

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

        if(mAuth!!.currentUser != null) {
            accountylyt.visibility = View.VISIBLE
            tvSingIn.visibility = View.INVISIBLE
        }else{
            accountylyt.visibility = View.INVISIBLE
            tvSingIn.visibility = View.VISIBLE
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
