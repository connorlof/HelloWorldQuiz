package com.loftydevelopment.helloworldquiz

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_options.*
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser



class OptionsActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

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

            etDisplayName.setText(mAuth!!.currentUser!!.displayName)

        }else{
            accountylyt.visibility = View.INVISIBLE
            tvSingIn.visibility = View.VISIBLE
        }
    }

    fun saveDisplayListener(view: View){

        val display = etDisplayName.text.toString().trim()

        if(display.length < 2){
            etDisplayName.setError("Minumum of 2 characters.")
            etDisplayName.requestFocus()
            return
        }

        progressBarName.visibility = View.VISIBLE

        val user = mAuth!!.getCurrentUser()

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(display).build()

        user!!.updateProfile(profileUpdates)

        progressBarName.visibility = View.GONE

        Toast.makeText(this, "Display name successfully updated.", Toast.LENGTH_LONG).show()

    }

    fun savePasswordListener(view: View){

        val oldPass = etOldPw.text.toString().trim()
        val newPass = etNewPw.text.toString().trim()
        val newPassConfirm = etNewPw2.text.toString().trim()

        if(!oldPass.isEmpty()){

            if(!newPass.isEmpty()){

                if(newPass.length < 6){
                    etNewPw.setError("Minimum of 6 characters")
                    etNewPw.requestFocus()
                    return
                }

                if(!newPass.equals(newPassConfirm)){
                    etNewPw2.setError("New passwords must match")
                    etNewPw2.requestFocus()
                    return
                }

                progressBarPw.visibility = View.VISIBLE

                val user: FirebaseUser?
                user = FirebaseAuth.getInstance().currentUser
                val email = user!!.email
                val credential = EmailAuthProvider.getCredential(email!!, oldPass)

                user.reauthenticate(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.updatePassword(newPass).addOnCompleteListener { task ->

                            progressBarPw.visibility = View.GONE

                            if (!task.isSuccessful) {
                                //Failed
                                Toast.makeText(this, "Something went wrong. Please try again later..", Toast.LENGTH_LONG).show()
                            }else{
                                Toast.makeText(this, "Password successfully updated.", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {

                        progressBarPw.visibility = View.GONE

                        //Failed
                        etOldPw.setError("Authentication Failed")
                        etOldPw.requestFocus()
                    }
                }

            }

        }

    }

    fun menuListener(view: View){

        finish()
        startActivity(Intent(this, MainActivity::class.java))

    }
}
