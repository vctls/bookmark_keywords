package vctls.bookmarkkeywords.error

sealed class BookmarkKeywordsError : Exception() {
    object DatabaseNotFoundException : BookmarkKeywordsError() {
        private fun readResolve(): Any = DatabaseNotFoundException
    }
}
