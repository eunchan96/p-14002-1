package com.back.domain.member.member.entity

import com.back.domain.member.member.repository.MemberAttrRepository
import jakarta.persistence.Column
import jakarta.persistence.Entity
import java.util.*

@Entity
class Member(
    id: Int,
    username: String,
    var password: String? = null,
    var nickname: String,
    @field:Column(unique = true) var apiKey: String,
) : BaseMember(id, username) {
    companion object {
        lateinit var attrRepository: MemberAttrRepository
    }

    constructor() : this(0)

    constructor(id: Int, username: String, nickname: String) : this(
        id, username, "", nickname, ""
    )

    constructor(username: String, password: String?, nickname: String) : this (
        0, username, password, nickname, UUID.randomUUID().toString()
    )

    constructor(id: Int) : this(id, "", "")

    val name: String
        get() = nickname

    fun modifyApiKey(apiKey: String) {
        this.apiKey = apiKey
    }

    fun modify(nickname: String, profileImgUrl: String?) {
        this.nickname = nickname
        profileImgUrl?.let { this.profileImgUrl = it }
    }
}
