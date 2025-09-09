package com.back.domain.post.postUser.entity

import com.back.domain.member.member.entity.BaseMember
import com.back.domain.member.member.entity.Member
import com.back.domain.post.postUser.repository.PostUserAttrRepository
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.springframework.data.annotation.Immutable

// Member.kt : 회원 도메인 전용 객체
// PostUser.kt : 게시글 도메인 전용 객체
// MEMBER 테이블을 같이 공유한다

// 분리 장점 : 글과 관련된 필드는 PostUser 에만 넣어주면 되고, Post 도메인에는 PostUser 엔티티 사용, MSA 구현 시 유용
// DDD 에서 하나의 테이블 => N개의 엔티티 클래스
// MSA 에서는 자연스럽게 각 MicroService 에서 필요한 엔티티만 사용
@Entity
@Immutable
@Table(name = "MEMBER")
class PostUser(
    id: Int,
    username: String,
    @field:Column(name = "NICKNAME") var name: String, // NICKNAME 필드를 name 이라는 다른 필드명으로 사용 가능
    profileImgUrl: String? = null,
) : BaseMember(id, username, profileImgUrl) {
    constructor(member: Member) : this(
        member.id,
        member.username,
        member.name,
        member.profileImgUrl
    )

    // 코프링에서 엔티티에 `by lazy` 필드가 제대로 작동하게 하려면
    // kotlin("plugin.jpa")에 의해서 만들어지는 인자 없는 생성자로는 부족하다.
    // 귀찮지만 이렇게 직접 만들어야 한다.
    constructor() : this(0, "", "")

    companion object {
        lateinit var attrRepository: PostUserAttrRepository
    }

    @delegate:Transient
    private val postsCountAttr by lazy {
        attrRepository.findBySubjectAndName(this, "postsCount")
            ?: PostUserAttr(this, "postsCount", "0")
    }

    @delegate:Transient
    private val postCommentsCountAttr by lazy {
        attrRepository.findBySubjectAndName(this, "postCommentsCount")
            ?: PostUserAttr(this, "postCommentsCount", "0")
    }

    var postsCount: Int
        get() = postsCountAttr.value.toInt()
        set(value) {
            postsCountAttr.value = value.toString()
            attrRepository.save(postsCountAttr)
        }

    var postCommentsCount: Int
        get() = postCommentsCountAttr.value.toInt()
        set(value) {
            postCommentsCountAttr.value = value.toString()
            attrRepository.save(postCommentsCountAttr)
        }

    fun incrementPostsCount() = postsCount++
    fun decrementPostsCount() = postsCount--

    fun incrementPostCommentsCount() = postCommentsCount++
    fun decrementPostCommentsCount() = postCommentsCount--
}