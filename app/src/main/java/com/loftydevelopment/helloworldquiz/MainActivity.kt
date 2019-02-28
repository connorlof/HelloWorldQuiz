package com.loftydevelopment.helloworldquiz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var userUid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

    }

    override fun onStart() {
        super.onStart()

        invalidateOptionsMenu()

        if(mAuth!!.currentUser != null){

            tvLoginStatus.text = "Logged In: Yes"
            userUid = mAuth!!.currentUser!!.uid

        }else{
            tvLoginStatus.text = "Logged In: No"
            userUid = "local"
        }

    }

    fun startQuiz(view:View){

        val quizActivity = Intent(this, QuizActivity::class.java)
        startActivity(quizActivity)

    }

    fun loadScores(view:View){

        //TODO handle offline score logic
        if(mAuth!!.currentUser != null){

            val scoreActivity = Intent(this, ScoreActivity::class.java)
            scoreActivity.putExtra("uid", userUid)
            startActivity(scoreActivity)

        }else{
            Toast.makeText(this, "Must be logged in to view scores!", Toast.LENGTH_LONG).show()
        }



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        var inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.user_menu, menu)

        var menuItemLogin: MenuItem = menu!!.findItem(R.id.menuLogin)
        var menuItemLogout: MenuItem = menu.findItem(R.id.menuLogout)
        var menuItemSignUp: MenuItem = menu.findItem(R.id.menuSignUp)

        if(mAuth!!.currentUser != null){
            menuItemLogin.isVisible = false
            menuItemSignUp.isVisible = false
            menuItemLogout.isVisible = true
        }else{
            menuItemLogin.isVisible = true
            menuItemSignUp.isVisible = true
            menuItemLogout.isVisible = false
        }


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId){
            R.id.menuOptions -> {
                startActivity(Intent(this, OptionsActivity::class.java))
            }
            R.id.menuLogin -> {
                startActivity(Intent(this, LoginActvity::class.java))
            }
            R.id.menuSignUp -> {
                startActivity(Intent(this, SignUpActivity::class.java))
            }
            R.id.menuLogout -> {
                Toast.makeText(this, "Logout selected", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        return true
    }

}
