package com.back.global.rsData

import net.minidev.json.annotate.JsonIgnore

data class RsData<T>(
    val resultCode: String,
    val msg: String,
    val data: T = null as T
) {
    @get:JsonIgnore
    val statusCode: Int by lazy {
        resultCode.split("-", limit = 2)[0].toInt()
    }

    @JsonIgnore
    val isSuccess = statusCode < 400

    @JsonIgnore
    val isFail = !isSuccess
}
