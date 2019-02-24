package com.loftydevelopment.helloworldquiz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login_actvity.*

class LoginActvity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_actvity)

        mAuth = FirebaseAuth.getInstance()
    }

    private fun userLogin(){

        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if(email.isEmpty()){
            etEmail.setError("Email is required")
            etEmail.requestFocus()
            return
        }

        if(password.isEmpty()){
            etPassword.setError("Password is required")
            etPassword.requestFocus()
            return
        }

        progressBar.visibility = View.VISIBLE

        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                progressBar.visibility = View.GONE

                if (task.isSuccessful) {
                    finish()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Error occurred: " + task.exception!!.message, Toast.LENGTH_LONG).show()
                }
            }

    }

    override fun onStart() {
        super.onStart()

        if(mAuth!!.currentUser != null){
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    fun loginListener(view: View){
        userLogin()
    }

    fun signUpListener(view: View){
        finish()
        startActivity(Intent(this, SignUpActivity::class.java))
    }
}
