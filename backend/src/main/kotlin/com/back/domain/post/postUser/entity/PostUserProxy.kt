package com.back.domain.post.postUser.entity

import java.time.LocalDateTime

class PostUserProxy(
    id: Int,
    username: String,
    name: String,
    profileImgUrl: String? = null,
    private val real: PostUser
) : PostUser(id, username, name, profileImgUrl) {
    override var name: String
        get() = super.name
        set(value) {
            super.name = value
            real.name = value
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

    override var postsCount: Int
        get() = real.postsCount
        set(value) {
            real.postsCount = value
        }

    override var postCommentsCount: Int
        get() = real.postCommentsCount
        set(value) {
            real.postCommentsCount = value
        }
}