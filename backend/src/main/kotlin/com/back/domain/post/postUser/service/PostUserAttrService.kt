package com.back.domain.post.postUser.service

import com.back.domain.post.postUser.entity.PostUser
import com.back.domain.post.postUser.entity.PostUserAttr
import com.back.domain.post.postUser.repository.PostUserAttrRepository
import org.springframework.stereotype.Service

@Service
class PostUserAttrService(
    private val postUserAttrRepository: PostUserAttrRepository
) {
    fun findBySubjectAndName(subject: PostUser, name: String): PostUserAttr? = postUserAttrRepository.findBySubjectAndName(subject, name)
}