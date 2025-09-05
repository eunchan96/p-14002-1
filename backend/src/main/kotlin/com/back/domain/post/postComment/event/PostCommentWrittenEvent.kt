package com.back.domain.post.postComment.event

import com.back.domain.post.post.dto.PostDto
import com.back.domain.post.postComment.dto.PostCommentDto
import com.back.domain.post.postUser.dto.PostUserDto

data class PostCommentWrittenEvent(
    val actor: PostUserDto,
    val owner: PostUserDto,
    val post: PostDto,
    val postComment: PostCommentDto
)