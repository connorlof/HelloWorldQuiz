package com.loftydevelopment.helloworldquiz

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_options.*

class OptionsActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        mAuth = FirebaseAuth.getInstance()

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPref.edit()

        cbSound.isChecked = sharedPref.contains("checked") && sharedPref.getBoolean("checked", false)

        cbSound?.setOnCheckedChangeListener{view, isChecked ->
            if(isChecked){
                editor.putBoolean("checked", true)
                editor.commit()
            }else{
                editor.putBoolean("checked", false)
                editor.commit()
            }
        }

        if(mAuth!!.currentUser != null) {
            accountlyt.visibility = View.VISIBLE
            tvSignIn.visibility = View.INVISIBLE

            etDisplayName.setText(mAuth!!.currentUser!!.displayName)

        }else{
            accountlyt.visibility = View.INVISIBLE
            tvSignIn.visibility = View.VISIBLE
        }
    }

    fun saveDisplayListener(view: View){

        val display = etDisplayName.text.toString().trim()

        if(display.length < 2){
            etDisplayName.setError("Minumum of 2 characters.")
            etDisplayName.requestFocus()
            return
        }

        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputManager!!.hideSoftInputFromWindow(
            currentFocus!!.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )

        if(isOnline()){

            progressBarName.visibility = View.VISIBLE

            val user = mAuth!!.getCurrentUser()

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(display).build()

            user!!.updateProfile(profileUpdates)

            //Update all scores related to the user
            db = FirebaseFirestore.getInstance()
            db!!.collection("scores").get().addOnSuccessListener { queryDocumentSnapshots ->

                progressBarName.visibility = View.GONE

                if (!queryDocumentSnapshots.isEmpty) {

                    val list = queryDocumentSnapshots.documents

                    for (d in list) {

                        val s = d.toObject(Score::class.java)

                        if(s!!.uid == user!!.uid){
                            db!!.collection("scores").document(d.id).update("displayName", display)
                        }

                    }

                }

                Toast.makeText(this, "Display name successfully updated.", Toast.LENGTH_LONG).show()

            }

        }else{

            Toast.makeText(this, "No network connection detected. Could not update.", Toast.LENGTH_LONG).show()

        }



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

                val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                inputManager!!.hideSoftInputFromWindow(
                    currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )

                if(isOnline()){

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

                }else{

                    Toast.makeText(this, "No network connection detected. Could not update.", Toast.LENGTH_LONG).show()

                }

            }

        }

    }

    fun menuListener(view: View){

        finish()
        startActivity(Intent(this, MainActivity::class.java))

    }

    private fun isOnline(): Boolean {

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo

        if (netInfo != null && netInfo.isConnected) {
            return true
        }

        return false

    }

}
