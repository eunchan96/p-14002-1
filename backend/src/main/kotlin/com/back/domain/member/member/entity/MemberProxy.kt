package com.back.domain.member.member.entity

import java.time.LocalDateTime

class MemberProxy(
    id: Int,
    username: String,
    nickname: String,
    private val real: Member
) : Member(id, username, nickname) {
    override var nickname: String
        get() = super.nickname
        set(value) {
            super.nickname = value
            real.nickname = value
        }

    override var createDate: LocalDateTime
        get() = real.createDate
        set(value) {
            real.createDate = value
        }

    override var modifyDate: LocalDateTime
        get() = real.modifyDate
        set(value) {
            real.modifyDate = value
        }

    override var profileImgUrl: String?
        get() = real.profileImgUrl
        set(value) {
            real.profileImgUrl = value
        }

    override var apiKey: String
        get() = real.apiKey
        set(value) {
            real.apiKey = value
        }

    override var password: String?
        get() = real.password
        set(value) {
            real.password = value
        }
}