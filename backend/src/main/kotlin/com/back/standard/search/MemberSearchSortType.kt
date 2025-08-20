package com.back.standard.search

import com.back.standard.extensions.toCamelCase

enum class MemberSearchSortType {
    ID,
    ID_ASC,
    NICKNAME,
    NICKNAME_ASC,
    USERNAME,
    USERNAME_ASC,
    CREATED_AT,
    CREATED_AT_ASC;

    val isAsc: Boolean = name.endsWith("_ASC")

    val property: String = name.removeSuffix("_ASC").toCamelCase()
}