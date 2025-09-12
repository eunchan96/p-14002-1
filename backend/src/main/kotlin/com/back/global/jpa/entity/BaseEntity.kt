package com.back.global.jpa.entity

import com.back.standard.util.Ut
import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0
) {
    val modelName: String
        get() = Ut.str.lcfirst(this::class.simpleName!!)

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is BaseTime) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}