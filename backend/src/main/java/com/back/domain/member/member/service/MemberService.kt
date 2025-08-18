package com.back.domain.member.member.service

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.repository.MemberRepository
import com.back.global.exception.ServiceException
import com.back.global.rsData.RsData
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class MemberService(
    private val authTokenService: AuthTokenService,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder

) {
    fun count(): Long {
        return memberRepository.count()
    }

    @JvmOverloads
    fun join(username: String, password: String?, nickname: String, profileImgUrl: String? = null): Member {
        memberRepository
            .findByUsername(username)?.let {
                throw ServiceException("409-1", "이미 존재하는 아이디입니다.")
            }

        val member = Member(
            username,
            password?.takeIf { it.isNotBlank() }?.let { passwordEncoder.encode(it) },
            nickname,
            profileImgUrl
        )

        return memberRepository.save<Member>(member)
    }

    fun findByUsername(username: String): Member? {
        return memberRepository.findByUsername(username)
    }

    fun findByApiKey(apiKey: String): Member? {
        return memberRepository.findByApiKey(apiKey)
    }

    fun genAccessToken(member: Member): String {
        return authTokenService.genAccessToken(member)
    }

    fun payload(accessToken: String): Map<String, Any>? {
        return authTokenService.payload(accessToken)
    }

    fun findById(id: Int): Optional<Member> {
        return memberRepository.findById(id)
    }

    fun findAll(): List<Member> {
        return memberRepository.findAll()
    }

    fun checkPassword(member: Member, password: String) {
        if (!passwordEncoder.matches(password, member.password))
            throw ServiceException(
                "401-1",
                "비밀번호가 일치하지 않습니다."
            )
    }

    fun modifyOrJoin(username: String, password: String, nickname: String, profileImgUrl: String): RsData<Member> {
        findByUsername(username)?.let {
            modify(it, nickname, profileImgUrl)
            return RsData("200-1", "회원 정보가 수정되었습니다.", it)
        } ?: run {
            var joined = join(username, password, nickname, profileImgUrl)
            return RsData("201-1", "회원가입이 완료되었습니다.", joined)
        }
    }

    private fun modify(member: Member, nickname: String, profileImgUrl: String) {
        member.modify(nickname, profileImgUrl)
    }

    fun findByListedPage(pageable: Pageable): Page<Member> {
        return memberRepository.findAll(pageable)
    }
}