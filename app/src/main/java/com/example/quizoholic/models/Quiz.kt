package com.example.quizoholic.models

import com.example.quizoholic.R

class Quiz(
    val quizName: String = "Title",
    val quizImage: Int = 0,
) {
    fun createQuizList(): ArrayList<Quiz> {
        val listOfQuizNames = arrayOf(
            Pair("MATHS", R.drawable.maths),
            Pair("COMPUTER", R.drawable.computer),
            Pair("HISTORY", R.drawable.history),
            Pair("MUSIC", R.drawable.python),
            Pair("POLITICS", R.drawable.politics),
            Pair("SPORTS", R.drawable.python),
            Pair("GEOGRAPHY", R.drawable.python1),
            Pair("ANIMALS", R.drawable.javascript))

        val list: ArrayList<Quiz> = ArrayList()
        listOfQuizNames.forEach { list.add(Quiz(quizName = it.first, quizImage = it.second)) }
        return list
    }
}


