package com.kimochisoft.dynamicform.networking

import android.util.Log
import com.google.gson.Gson
import com.kimochisoft.dynamicform.data.ErrorResponse
import com.kimochisoft.dynamicform.data.FailureCallback
import com.kimochisoft.dynamicform.data.SuccessfulCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestCallback<T>(val success: SuccessfulCallback<T>, val failure: FailureCallback) :
    Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            Log.d(RestCallback::class.java.simpleName, "onResponse isSuccessful = true")

            val data = response.body()

            if (data == null) {
                val exception = NullPointerException("server body response is null")

                Log.d(RestCallback::class.java.simpleName, exception.message.toString())
                failure(exception)
            } else {
                success(data)
            }
        } else {
            Log.d(RestCallback::class.java.simpleName, "onResponse isSuccessful = false")

            val errorBody = response.errorBody()

            if (errorBody == null) {
                val exception = NullPointerException("server error body response is null")

                Log.d(RestCallback::class.java.simpleName, exception.message.toString())
                failure(exception)
            } else {
                try {
                    val data = Gson().fromJson(errorBody.string(), ErrorResponse::class.java)

                    if (data == null) {
                        val exception = IllegalArgumentException("cannot parse error body, invalid format")

                        Log.d(RestCallback::class.java.simpleName, exception.message.toString())
                        failure(exception)
                    } else {
                        Log.d(RestCallback::class.java.simpleName, data.message)
                        failure(Exception(data.message))
                    }
                } catch (exception: Exception) {
                    Log.d(RestCallback::class.java.simpleName, exception.message.toString())
                    failure(exception)
                }
            }
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        Log.d(RestCallback::class.java.simpleName, "onFailure + ${t.stackTrace}")
        failure(Exception(t))
    }
}