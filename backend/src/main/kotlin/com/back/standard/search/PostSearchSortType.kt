package com.back.standard.search

import com.back.standard.extensions.toCamelCase

enum class PostSearchSortType {
    ID,
    ID_ASC,
    TITLE,
    TITLE_ASC,
    AUTHOR,
    AUTHOR_ASC,
    CREATED_AT,
    CREATED_AT_ASC;

    val isAsc: Boolean = name.endsWith("_ASC")

    val property: String = name.removeSuffix("_ASC").toCamelCase()
}