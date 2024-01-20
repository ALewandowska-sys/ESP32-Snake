package com.example.connectesp32

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException

class ServerConnection {

    private val hostAddress : String = "192.168.4.1"
    private val client = OkHttpClient()
    private var responseSuccessful: Boolean = false

    fun getUrlAddress(): String{
        return "http://$hostAddress"
    }

    fun sendGetRequest(path: String, params: Map<String, String>, handleResponse: (Boolean) -> Unit) {
        val httpUrl = prepareHttpUrl(path, params)

        val request = Request.Builder()
            .url(httpUrl)
            .get()
            .build()
        Log.d("URL: ", request.toString())

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                responseSuccessful = false
                Log.e("Exception for GET", e.toString())
                handleResponse(responseSuccessful)
            }
            override fun onResponse(call: Call, response: Response) {
                Log.d("Response for GET", response.toString())
                responseSuccessful = response.isSuccessful
                response.close()
                handleResponse(responseSuccessful)
            }
        })
    }

    private fun prepareHttpUrl(path: String, params: Map<String, String>): HttpUrl{
        return HttpUrl.Builder()
            .scheme("http")
            .host(hostAddress)
            .addPathSegments(path)
            .apply {
                params.forEach { (key, value) ->
                    addQueryParameter(key, value)
                }
            }
            .build()
    }
}