package com.back.global.app

import com.back.domain.member.member.entity.BaseMember
import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.repository.MemberAttrRepository
import com.back.domain.member.member.repository.MemberRepository
import com.back.domain.post.postUser.entity.PostUser
import com.back.domain.post.postUser.repository.PostUserAttrRepository
import com.back.standard.util.Ut
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.tika.Tika
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class AppConfig (
    environment: Environment,
    objectMapper: ObjectMapper,
    memberAttrRepository: MemberAttrRepository,
    memberRepository: MemberRepository,
    postUserAttrRepository: PostUserAttrRepository,
    @Value("\${custom.site.cookieDomain}") cookieDomain: String,
    @Value("\${custom.site.frontUrl}") siteFrontUrl: String,
    @Value("\${custom.site.backUrl}") siteBackUrl: String,
    @Value("\${custom.genFile.dirPath}") genFileDirPath: String,
    tika: Tika,
) {
    init {
        Companion.environment = environment
        Ut.json.objectMapper = objectMapper
        BaseMember.memberAttrRepository = memberAttrRepository
        BaseMember.memberRepository = memberRepository
        Member.attrRepository = memberAttrRepository
        PostUser.attrRepository = postUserAttrRepository

        _cookieDomain = cookieDomain
        _siteFrontUrl = siteFrontUrl
        _siteBackUrl = siteBackUrl

        _genFileDirPath = genFileDirPath
        _tika = tika
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    companion object {
        private lateinit var environment: Environment

        val isDev: Boolean
            get() = environment.matchesProfiles("dev")

        val isTest: Boolean
            get() = !environment.matchesProfiles("test")

        val isProd: Boolean
            get() = environment.matchesProfiles("prod")

        val isNotProd: Boolean
            get() = !isProd

        private lateinit var _cookieDomain: String
        private lateinit var _siteFrontUrl: String
        private lateinit var _siteBackUrl: String

        val cookieDomain: String by lazy { _cookieDomain }
        val siteFrontUrl: String by lazy { _siteFrontUrl }
        val siteBackUrl: String by lazy { _siteBackUrl }

        private lateinit var _genFileDirPath: String
        val genFileDirPath: String by lazy { _genFileDirPath }

        val tempDirPath: String = System.getProperty("java.io.tmpdir")

        val resourcesSampleDirPath: String by lazy {
            val resource = ClassPathResource("sample")
            if (resource.exists()) {
                resource.file.absolutePath
            } else {
                "src/main/resources/sample"
            }
        }

        private lateinit var _tika: Tika
        val tika: Tika by lazy { _tika }
    }
}
