package com.back.domain.post.postUser.repository

import com.back.domain.post.postUser.entity.PostUserAttr
import com.back.domain.post.postUser.entity.PostUserProxy
import com.back.global.app.AppConfig
import com.back.standard.extensions.getOrThrow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostUserAttrRepositoryTest {
    @Autowired
    private lateinit var postUserAttrRepository: PostUserAttrRepository

    @Autowired
    private lateinit var postUserRepository: PostUserRepository

    @Test
    @DisplayName("saveInt")
    fun t1() {
        val postUser1 = postUserRepository.findByUsername("user1").getOrThrow()
        val attr = PostUserAttr(postUser1, "postCount", 0.toString())
        postUserAttrRepository.save(attr)

        val result = postUserAttrRepository.findBySubjectAndName(postUser1, "postCount")
        assertThat(result).isNotNull
    }

    @Test
    @DisplayName("saveString")
    fun t2() {
        val postUser1 = postUserRepository.findByUsername("user1").getOrThrow()
        val attr = PostUserAttr(postUser1, "grade", "user")
        postUserAttrRepository.save(attr)

        val result = postUserAttrRepository.findBySubjectAndName(postUser1, "grade")
        assertThat(result).isNotNull
    }

    @Test
    @DisplayName("PostUserProxy")
    fun t3() {
        val realPostUser = postUserRepository.getReferenceById(1)

        val postUser = PostUserProxy(
            1,
            "system",
            "시스템",
            realPostUser
        )

        // fulfill 필요없음
        assertThat(postUser.id).isEqualTo(1)
        assertThat(postUser.username).isEqualTo("system")
        assertThat(postUser.name).isEqualTo("시스템")
        assertThat(postUser.redirectToProfileImgUrlOrDefault).isEqualTo("${AppConfig.siteBackUrl}/api/v1/members/1/redirectToProfileImg")
        assertThat(postUser.isAdmin).isTrue()
        assertThat(postUser.authorities).hasSize(1)
        assertThat(postUser.authoritiesAsStringList).containsExactly("ROLE_ADMIN")
        assertThat(postUser).isEqualTo(realPostUser)

        // fulfill 필요함
        assertThat(postUser.createDate).isNotNull
        assertThat(postUser.modifyDate).isNotNull
        assertThat(postUser.profileImgUrl).isBlank
        assertThat(postUser.postsCount).isNotNull
        assertThat(postUser.postCommentsCount).isNotNull
    }
}