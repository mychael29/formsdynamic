package com.kimochisoft.dynamicform.data

import com.google.gson.JsonElement
import com.google.gson.JsonObject

data class ServerResponse<T>(val message: String, val data: T)

data class ErrorResponse(val code: String, val error: String, val message: String)

typealias FormDynamicResponse = ServerResponse<JsonObject>

typealias FormSaveResponse = ServerResponse<JsonElement>


