package com.back.domain.post.post.service

import com.back.domain.member.member.entity.Member
import com.back.domain.post.post.entity.Post
import com.back.domain.post.post.repository.PostRepository
import com.back.domain.post.postComment.entity.PostComment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class PostService(
    private val postRepository: PostRepository
) {
    fun count(): Long {
        return postRepository.count()
    }

    fun write(author: Member, title: String, content: String): Post {
        val post = Post(author, title, content)

        return postRepository.save(post)
    }

    fun findById(id: Int): Optional<Post> = postRepository.findById(id)

    fun findAll(): List<Post> = postRepository.findAll()

    fun modify(post: Post, title: String, content: String) = post.modify(title, content)

    fun writeComment(author: Member, post: Post, content: String): PostComment = post.addComment(author, content)

    fun deleteComment(post: Post, postComment: PostComment): Boolean = post.deleteComment(postComment)

    fun modifyComment(postComment: PostComment, content: String) = postComment.modify(content)

    fun delete(post: Post) = postRepository.delete(post)

    fun findLatest(): Post? = postRepository.findFirstByOrderByIdDesc()

    fun flush() = postRepository.flush()

    fun findByListedPage(pageable: Pageable): Page<Post> = postRepository.findAll(pageable)
}