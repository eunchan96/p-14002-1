package com.back.domain.member.member.entity

import com.back.global.jpa.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*

@Entity
class Member(
    id: Int,
    @field:Column(unique = true) var username: String,
    var password: String? = null,
    var nickname: String,
    @field:Column(unique = true) var apiKey: String,
    var profileImgUrl: String?
) : BaseEntity(id) {
    constructor(id: Int, username: String, nickname: String) : this(
        id, username, "", nickname, "", null
    )

    constructor(username: String, password: String?, nickname: String, profileImgUrl: String?) : this (
        0, username, password, nickname, UUID.randomUUID().toString(), profileImgUrl
    )

    val name: String
        get() = nickname

    val profileImgUrlOrDefault: String
        get() {
            return profileImgUrl ?: "https://placehold.co/600x600?text=U_U"
        }

    fun modifyApiKey(apiKey: String) {
        this.apiKey = apiKey
    }

    val isAdmin: Boolean
        get() {
            if ("system" == username) return true
            if ("admin" == username) return true

            return false
        }

    val authorities: List<GrantedAuthority>
        get() = this.authoritiesAsStringList
            .map { SimpleGrantedAuthority(it) }

    private val authoritiesAsStringList: List<String>
        get() {
            val authorities = mutableListOf<String>()

            if (isAdmin) authorities.add("ROLE_ADMIN")

            return authorities
        }

    fun modify(nickname: String, profileImgUrl: String) {
        this.nickname = nickname
        this.profileImgUrl = profileImgUrl
    }
}
