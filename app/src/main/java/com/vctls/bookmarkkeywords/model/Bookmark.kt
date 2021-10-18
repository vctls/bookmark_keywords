package com.vctls.bookmarkkeywords.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val keyword: String,
    val template: String,
    val name: String?,
    // TODO Maybe provide full-text search.
    // TODO Maybe add indices.
    // TODO Maybe add timestamps for creation, update, usage...
    // TODO Add tags to make search easier, as in Firefox bookmarks.
    // TODO Enable multiple URLs to search on multiple sites at once?
    //  Or maybe implement "nested" bookmarks?
    // TODO Add an icon.
)
