package com.back.domain.member.member.entity

import com.back.global.jpa.entity.BaseTime
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY
import org.hibernate.annotations.NaturalId

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["subject_id", "name"])
    ]
)
class MemberAttr(
    subject: Member,
    name: String,
    value: String,
) : BaseTime() {
    @field:NaturalId
    @field:ManyToOne(fetch = LAZY)
    @field:JoinColumn(name = "subject_id")
    val subject = subject

    @field:NaturalId
    val name = name

    @field:Column(name = "val", columnDefinition = "TEXT")
    var value: String = value
}