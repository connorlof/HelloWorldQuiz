package com.loftydevelopment.helloworldquiz

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

class Score : Comparable<Score>{

    var uid: String? = null
    var displayName: String? = null
    @ServerTimestamp var date: Date? = null
    var quizScore: Int? = null

    constructor(){}

    constructor(uid: String, displayName: String, date: Date, quizScore: Int) {
        this.uid = uid
        this.displayName = displayName
        this.date = date
        this.quizScore = quizScore
    }

    override fun compareTo(other: Score): Int {
        return other.quizScore!! - this.quizScore!!
    }

}