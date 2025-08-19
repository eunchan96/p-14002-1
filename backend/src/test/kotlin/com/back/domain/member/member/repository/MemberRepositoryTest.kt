package com.back.domain.member.member.repository

import com.back.standard.search.MemberSearchKeywordType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberRepositoryTest {
    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    @DisplayName("findByKeyword")
    fun t1() {
        val pageable = PageRequest.of(0, 10)
        val result = memberRepository.findByKeyword(MemberSearchKeywordType.USERNAME,"user", pageable)

        assertThat(result.all { it.username.contains("user") }).isTrue
    }
}