package com.kimochisoft.dynamicform.managers

import android.content.Context
import com.google.gson.JsonObject
import com.kimochisoft.dynamicform.data.*
import com.kimochisoft.dynamicform.networking.FormsDynamicApi
import com.kimochisoft.dynamicform.networking.RestCallback
import com.kimochisoft.dynamicform.networking.RestClient

class DynamicFormManager(private val context: Context) {

    fun getDynamicForm(success: SuccessfulCallback<JsonObject>, failure: FailureCallback) {
        val call = RestClient.create(context).create(FormsDynamicApi::class.java).getForms()
        call.enqueue(RestCallback({
            success(it.data)
        }, failure))
    }

    fun saveDynamicForm(form: JsonObject, success: SuccessfulCallback<String>, failure: FailureCallback) {
        val call = RestClient.create(context).create(FormsDynamicApi::class.java).setForm(form)
        call.enqueue(RestCallback({
            success(it.message)
        }, failure))
    }
}