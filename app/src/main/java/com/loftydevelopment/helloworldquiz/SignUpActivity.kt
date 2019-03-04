package com.loftydevelopment.helloworldquiz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.android.synthetic.main.activity_sign_up.*
import com.google.firebase.auth.UserProfileChangeRequest



class SignUpActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()

    }

    private fun registerUser(){

        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val passwordConfirm = etPasswordConfirm.text.toString().trim()

        if(email.isEmpty()){
            etEmail.setError("Email is required")
            etEmail.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Invalid email address")
            etEmail.requestFocus()
            return
        }

        if(password.isEmpty()){
            etPassword.setError("Password is required")
            etPassword.requestFocus()
            return
        }

        if(password.length < 6){
            etPassword.setError("Minimum of 6 characters")
            etPassword.requestFocus()
            return
        }

        if(!password.equals(passwordConfirm)){
            etPasswordConfirm.setError("Passwords must match")
            etPasswordConfirm.requestFocus()
            return
        }

        progressBar.visibility = View.VISIBLE

        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if(mAuth!!.currentUser != null){

                    var splitEmail = email.split("@")

                    val user = mAuth!!.getCurrentUser()

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(splitEmail[0]).build()

                    user!!.updateProfile(profileUpdates)

                }

                progressBar.visibility = View.GONE

                if (task.isSuccessful) {
                    finish()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                } else {

                    if(task.exception is FirebaseAuthUserCollisionException){
                        Toast.makeText(this, "Email address is already registered", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this, "Error occurred: " + task.exception!!.message, Toast.LENGTH_LONG).show()
                    }

                }

            }

    }

    fun loginListener(view: View){
        finish()
        startActivity(Intent(this, LoginActvity::class.java))
    }

    fun signUpListener(view: View){
        registerUser()
    }
}
