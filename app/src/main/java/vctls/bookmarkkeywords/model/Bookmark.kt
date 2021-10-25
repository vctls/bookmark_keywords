package vctls.bookmarkkeywords.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["keyword"], unique = true)])
/**
 * @property id Technical auto-generated primary key. Not to be used by the user.
 * @property keyword The short string by which the bookmark can be invoked. Has to be unique.
 * @property template The URL template of the bookmark. Usually, this would be an HTTP(S) URL
 * with a placeholder for the searched expression, but it can be anything, really.
 * @property name An optional name describing the bookmark.
 */
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val keyword: String,
    val template: String,
    val name: String?,
    // TODO Maybe provide full-text search.
    // TODO Maybe add indices.
    // TODO Maybe add timestamps for creation, update, usage...
    // TODO Add tags to make search easier, like in Firefox bookmarks.
    // TODO Enable multiple URLs to search on multiple sites at once?
    //  Or maybe implement "nested" bookmarks?
    // TODO Add an icon.
)
