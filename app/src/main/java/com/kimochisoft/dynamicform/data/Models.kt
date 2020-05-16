package com.kimochisoft.dynamicform.data

typealias SuccessfulCallback<T> = (data: T) -> Unit

typealias FailureCallback = (exception: Exception) -> Unit

data class Elements(
    var componentType: String? = null,
    var fieldNameKey: String? = null,
    var fieldNameView: String ?= null,
    var max: Int ?= null,
    var min: Int ?= null,
    var ordinal: Int ?= null,
    var valueString: String ?= null,
    var valueInt: Int ?= null
)

