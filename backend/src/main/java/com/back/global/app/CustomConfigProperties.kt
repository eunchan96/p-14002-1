package com.back.global.app

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "custom")
class CustomConfigProperties (
    val notProdMembers: List<NotProdMember>
)  {
    data class NotProdMember(
        val username: String,
        val apiKey: String,
        val nickname: String,
        val profileImgUrl: String
    )
}
