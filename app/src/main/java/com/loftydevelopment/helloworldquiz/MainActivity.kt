package com.loftydevelopment.helloworldquiz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

    }

    override fun onStart() {
        super.onStart()

        if(mAuth!!.currentUser != null){
            tvLoginStatus.text = "Logged In: Yes"
        }else{
            tvLoginStatus.text = "Logged In: No"
        }

    }

    fun startQuiz(view:View){

        val quizActivity = Intent(this, QuizActivity::class.java)
        startActivity(quizActivity)

    }

    fun loadScores(view:View){

        val scoreActivity = Intent(this, ScoreActivity::class.java)
        startActivity(scoreActivity)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        var inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.user_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId){
            R.id.menuOptions -> {
                Toast.makeText(this, "Options selected", Toast.LENGTH_SHORT).show()
            }
            R.id.menuLogin -> {
                startActivity(Intent(this, LoginActvity::class.java))
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
