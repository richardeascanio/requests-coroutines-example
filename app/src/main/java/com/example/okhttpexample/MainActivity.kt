package com.example.okhttpexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private val gson = Gson()
    private var res = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            setNewText("Click!")
            button.isEnabled = false
            button.isClickable = false
            fakeApiRequest()

        }
    }

    private fun setNewText(input: String) {
        val newText = text.text.toString() + "\n$input"
        text.text = newText
    }

    private fun fakeApiRequest() {
        CoroutineScope(IO).launch {
            val result = async {
                Log.i("Debug", "LAUNCHING JOB")
                getResultFromApi()
//                postRequestFromApi()
            }.await()
            writeResponse(res)
            Log.i("Debug", "JOB FINISHED")
        }
    }

    private suspend fun writeResponse(txt: String) {
        withContext(Main) {
            text.text = txt
        }
    }

    private fun getResultFromApi() {
        // GET request
        val request = Request.Builder()
            .url("https://jsonplaceholder.typicode.com/posts")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val result = response.body!!.string()
            Log.i("Debug", "onResponse: $result")
            val arrayTutorialType = object : TypeToken<Array<Todo>>() {}.type
            var todos: Array<Todo> = gson.fromJson(result, arrayTutorialType)

            todos.forEachIndexed { idx, tod ->
                res += tod.title
                Log.i("Debug", "getResultFromApi: Item $idx: \n ${tod.title}")
            }
//            val todo: Todo = gson.fromJson(result, Todo::class.java)
//            Log.i("Debug", "onResponse: todo userId: ${todo.userId}")
//            Log.i("Debug", "onResponse: todo id: ${todo.id}")
//            Log.i("Debug", "onResponse: todo title: ${todo.title}")
//            Log.i("Debug", "onResponse: todo completed: ${todo.completed}")
//            res = todo.title
        }
    }

    private fun postRequestFromApi() {
        // POST Request
        val send = JSONObject();
        send.put("title", "foo")
        send.put("body", "this is a new post request with coroutines")
        send.put("userId", 1)

        val request = Request.Builder()
            .url("https://jsonplaceholder.typicode.com/posts")
            .post(send.toString().toRequestBody())
            .addHeader("Content-type", "application/json; charset=UTF-8")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            Log.i("Debug", "postRequestFromApi: ${response.body!!.string()}")
        }
    }
}

