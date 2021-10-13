package com.vctls.bookmarkkeywords.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.vctls.bookmarkkeywords.model.Bookmark

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmark")
    suspend fun getAll(): List<Bookmark>

    @Query("SELECT * FROM bookmark WHERE id IN (:bookmarkIds)")
    suspend fun loadAllByIds(bookmarkIds: IntArray): List<Bookmark>

    @Query("SELECT * FROM bookmark WHERE keyword = :keyword LIMIT 1")
    suspend fun findByKeyword(keyword: String): Bookmark?

    @Insert
    suspend fun insertAll(vararg users: Bookmark)

    @Delete
    suspend fun delete(user: Bookmark)
}
