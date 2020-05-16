package com.kimochisoft.dynamicform.networking

import com.google.gson.JsonObject
import com.kimochisoft.dynamicform.data.FormDynamicResponse
import com.kimochisoft.dynamicform.data.FormSaveResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FormsDynamicApi {

    @GET("/form")
    fun getForms(): Call<FormDynamicResponse>

    @POST("/form")
    fun setForm(@Body body: JsonObject): Call<FormSaveResponse>

}