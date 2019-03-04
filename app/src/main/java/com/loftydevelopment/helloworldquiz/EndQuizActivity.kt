package com.loftydevelopment.helloworldquiz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_end_quiz.*
import com.google.android.gms.ads.AdRequest


class EndQuizActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_quiz)

        val bundle:Bundle = intent.extras
        val score = bundle.getInt("score")

        tvEndScore.text = "Final Score: " + score

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

    }

    fun playAgain(view:View){

        val quizActivity = Intent(this, QuizActivity::class.java)
        startActivity(quizActivity)

    }

    fun mainMenu(view:View){

        val menuActivity = Intent(this, MainActivity::class.java)
        startActivity(menuActivity)

    }


    override fun onBackPressed() {

        val menuActivity = Intent(this, MainActivity::class.java)
        startActivity(menuActivity)

    }

}
