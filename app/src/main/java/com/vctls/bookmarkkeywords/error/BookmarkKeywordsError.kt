package com.vctls.bookmarkkeywords.error

sealed class BookmarkKeywordsError : Exception() {
    object DatabaseNotFoundException : BookmarkKeywordsError()
}
