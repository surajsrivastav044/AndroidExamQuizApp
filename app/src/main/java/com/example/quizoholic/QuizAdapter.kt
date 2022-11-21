package com.example.quizoholic

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.quizoholic.models.Question
import com.example.quizoholic.models.Quiz
import org.json.JSONObject

class QuizAdapter(private val quizList: ArrayList<Quiz>, private val context: Context) :
    RecyclerView.Adapter<QuizAdapter.ViewHolder>() {

    private val urlMap = mapOf(
        "MATHS" to "https://opentdb.com/api.php?amount=10&category=19&type=multiple",
        "COMPUTER" to "https://opentdb.com/api.php?amount=10&category=18&type=multiple",
        "HISTORY" to "https://opentdb.com/api.php?amount=10&category=23&difficulty=easy&type=multiple",
        "MUSIC" to "https://opentdb.com/api.php?amount=10&category=12&difficulty=easy&type=multiple",
        "POLITICS" to "https://opentdb.com/api.php?amount=10&category=24&difficulty=easy&type=multiple",
        "SPORTS" to "https://opentdb.com/api.php?amount=10&category=21&type=multiple",
        "GEOGRAPHY" to "https://opentdb.com/api.php?amount=10&category=21&type=multiple",
        "ANIMALS" to "https://opentdb.com/api.php?amount=10&category=27&difficulty=easy&type=multiple"
    )

    private val progressDialog = ProgressDialog(context)

    private lateinit var selectedQuiz: String

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: TextView = itemView.findViewById(R.id.btnSelectedQuiz)
        val imageView: ImageView = itemView.findViewById(R.id.imgSelectedQuiz)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val quizView = inflater.inflate(R.layout.quiz_row, parent, false)
        return ViewHolder(quizView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentObj = quizList[position]
        val button = holder.button
        val imageView = holder.imageView
        button.text = currentObj.quizName
        imageView.setImageResource(currentObj.quizImage)
        button.setOnClickListener {
            selectedQuiz = currentObj.quizName

            // set progress dialog
            progressDialog.setMessage("Please wait...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            // prepare a list of all questions from api
            FetchData().start()
        }
    }

    override fun getItemCount(): Int {
        return quizList.size
    }

    inner class FetchData : Thread() {
        override fun run() {
            val selectedQuizURL = urlMap[selectedQuiz].toString()
            val requestQueue = Volley.newRequestQueue(context)
            val stringRequest = StringRequest(Request.Method.GET, selectedQuizURL, {
                try {
                    println("Response is $it")
                    val jsonObject = JSONObject(it)
                    val jsonArray = jsonObject.getJSONArray("results")
                    for (i in 0 until jsonArray.length()) {
                        val currentObject = jsonArray.getJSONObject(i)

                        // shuffle options to set correct option at random position
                        val optionNumbersShuffled = arrayListOf(0, 1, 2, 3)
                        optionNumbersShuffled.shuffle()

                        // stores corresponding options of optionNumbersShuffled list
                        val optionsList = arrayListOf<String>()
                        var correctOptionNumber = 0
                        for (j in 0 until optionNumbersShuffled.size) {
                            if (optionNumbersShuffled[j] == 3) {
                                optionsList.add(currentObject.getString("correct_answer"))
                                correctOptionNumber = j
                            } else optionsList.add(
                                currentObject.getJSONArray("incorrect_answers").get(optionNumbersShuffled[j]).toString()
                            )
                        }

                        // create new question and add to list of questions
                        val question = Question(
                            question = currentObject.getString("question"),
                            difficulty = currentObject.getString("difficulty"),
                            correct_answer = correctOptionNumber,
                            option1 = optionsList[0],
                            option2 = optionsList[1],
                            option3 = optionsList[2],
                            option4 = optionsList[3]
                        )
                        Constants.questionsList.add(question)
                    }
                    progressDialog.dismiss()
                    move()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }, {
                Toast.makeText(
                    context,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            })
            requestQueue.add(stringRequest)
        }

        private fun move() {
            val intent = Intent(context, QuestionsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

}