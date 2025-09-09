package com.back.domain.member.member.repository

import com.back.domain.member.member.entity.MemberProxy
import com.back.standard.extensions.getOrThrow
import com.back.standard.search.MemberSearchKeywordType
import com.back.standard.search.MemberSearchSortType
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

    @Test
    @DisplayName("findByKeyword - sortByAsc")
    fun t2() {
        val pageable = PageRequest.of(0, 10, MemberSearchSortType.USERNAME_ASC.sortBy)
        val result = memberRepository.findByKeyword(MemberSearchKeywordType.USERNAME,"user", pageable).content

        for (i in 0 until result.size - 1) {
            assertThat(result[i].username).isLessThan(result[i + 1].username)
        }
    }

    @Test
    @DisplayName("findByUsername cached")
    fun t3() {
        memberRepository.findByUsername("admin").getOrThrow() // 2번 회원 로드
        memberRepository.findByUsername("admin").getOrThrow() // 캐시
        memberRepository.findById(2).getOrThrow() // 캐시

        memberRepository.findById(1).getOrThrow() // 1번 회원 로드
        memberRepository.findByUsername("system").getOrThrow() // 캐시
    }

    @Test
    @DisplayName("getReferenceById로 불러온 객체는 id 조회 이외의 필드 접근 시점에 SELECT 쿼리가 실행된다.")
    fun t4() {
        val member = memberRepository.getReferenceById(1)
        println(member.id)
        member.nickname = "시스템" // 쿼리 발생
        println(member.nickname)
    }

    @Test
    @DisplayName("MemberProxy")
    fun t5() {
        val realMember = memberRepository.getReferenceById(1)

        val member = MemberProxy(
            1,
            "system",
            "시스템",
            realMember
        )

        // fulfill 필요없음
        assertThat(member.id).isEqualTo(1)
        assertThat(member.username).isEqualTo("system")
        assertThat(member.nickname).isEqualTo("시스템")
        assertThat(member.name).isEqualTo("시스템")
        assertThat(member.redirectToProfileImgUrlOrDefault).isEqualTo("/api/v1/members/1/redirectToProfileImg")
        assertThat(member.isAdmin).isTrue()
        assertThat(member.authorities).hasSize(1)
        assertThat(member.authoritiesAsStringList).containsExactly("ROLE_ADMIN")
        assertThat(member).isEqualTo(realMember)

        // fulfill 필요함
        assertThat(member.createDate).isNotNull()
        assertThat(member.modifyDate).isNotNull()
        assertThat(member.profileImgUrl).isNull()
        assertThat(member.apiKey).isNotNull()
        assertThat(member.password).isNotNull()
    }
}