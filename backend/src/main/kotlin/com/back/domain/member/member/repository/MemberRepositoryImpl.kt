package com.back.domain.member.member.repository

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.entity.QMember
import com.back.standard.search.MemberSearchKeywordType
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils

class MemberRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : MemberRepositoryCustom {
    override fun findByKeyword(keywordType: MemberSearchKeywordType, keyword: String, pageable: Pageable): Page<Member> {
        val builder = BooleanBuilder()

        if (keyword.isNotBlank()) {
            applyKeywordFilter(keywordType, keyword, builder)
        }

        // query
        val membersQuery = createMembersQuery(builder)

        // sort
        applySorting(pageable, membersQuery)

        // paging
        membersQuery.offset(pageable.offset).limit(pageable.pageSize.toLong())

        // total
        val totalQuery = createTotalQuery(builder)

        return PageableExecutionUtils.getPage(membersQuery.fetch(), pageable) { totalQuery.fetchOne()!! }
    }

    private fun applyKeywordFilter(kwType: MemberSearchKeywordType, kw: String, builder: BooleanBuilder) {
        when (kwType) {
            MemberSearchKeywordType.username -> builder.and(QMember.member.username.containsIgnoreCase(kw))
            MemberSearchKeywordType.nickname -> builder.and(QMember.member.nickname.containsIgnoreCase(kw))
            else -> builder.and(
                QMember.member.username.containsIgnoreCase(kw)
                    .or(QMember.member.nickname.containsIgnoreCase(kw))
            )
        }
    }

    private fun createMembersQuery(builder: BooleanBuilder): JPAQuery<Member> {
        return jpaQueryFactory
            .selectFrom(QMember.member)
            .where(builder)
    }

    private fun applySorting(pageable: Pageable, membersQuery: JPAQuery<Member>) {
        for (o in pageable.sort) {
            val pathBuilder: PathBuilder<*> = PathBuilder<Any?>(QMember.member.type, QMember.member.metadata)

            membersQuery.orderBy(
                OrderSpecifier(
                    if (o.isAscending) Order.ASC else Order.DESC,
                    pathBuilder[o.property] as Expression<Comparable<*>>
                )
            )
        }
    }

    private fun createTotalQuery(builder: BooleanBuilder): JPAQuery<Long> {
        return jpaQueryFactory
            .select(QMember.member.count())
            .from(QMember.member)
            .where(builder)
    }
}