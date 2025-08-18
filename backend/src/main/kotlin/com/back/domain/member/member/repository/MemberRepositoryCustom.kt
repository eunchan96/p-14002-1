package com.back.domain.member.member.repository

import com.back.domain.member.member.entity.Member
import com.back.standard.search.MemberSearchKeywordType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MemberRepositoryCustom {
    fun findByKeyword(
        keywordType: MemberSearchKeywordType,
        keyword: String,
        pageable: Pageable
    ): Page<Member>
}