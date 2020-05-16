package com.kimochisoft.dynamicform.data

import java.io.Serializable

data class FormRequest(
    val elements: List<Elements>,
    val id: Int
)