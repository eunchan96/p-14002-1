package com.back.domain.post.postUser.entity

import com.back.global.jpa.entity.BaseTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.NaturalId

// Member.kt : 회원 도메인 전용 객체
// PostUser.kt : 게시글 도메인 전용 객체
// MEMBER 테이블을 같이 공유한다

// 분리 장점 : 글과 관련된 필드는 PostUser 에만 넣어주면 되고, Post 도메인에는 PostUser 엔티티 사용, MSA 구현 시 유용
// DDD 에서 하나의 테이블 => N개의 엔티티 클래스
// MSA 에서는 자연스럽게 각 MicroService 에서 필요한 엔티티만 사용
@Entity
@Table(name = "MEMBER")
class PostUser(
    id: Int,
    @field:NaturalId @field:Column(unique = true) val username: String,
    @field:Column(name = "NICKNAME") var name: String, // NICKNAME 필드를 name 이라는 다른 필드명으로 사용 가능
    var profileImgUrl: String?,
    var postCount: Int, // PostUser 에만 추가된 필드
) : BaseTime(id) {
}