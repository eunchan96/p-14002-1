package com.back.global.app

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "custom")
class CustomConfigProperties (
    val notProdMembers: List<NotProdMember>
)  {
    data class NotProdMember(
        @JvmField val username: String,
        @JvmField val apiKey: String,
        @JvmField val nickname: String,
        @JvmField val profileImgUrl: String
    )
}
