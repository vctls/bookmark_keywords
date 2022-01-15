package vctls.bookmarkkeywords.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vctls.bookmarkkeywords.model.Bookmark

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmark")
    suspend fun getAll(): MutableList<Bookmark>

    @Query("SELECT * FROM bookmark WHERE id IN (:bookmarkIds)")
    suspend fun loadAllByIds(bookmarkIds: IntArray): List<Bookmark>

    @Query("SELECT * FROM bookmark WHERE keyword = :keyword LIMIT 1")
    suspend fun findByKeyword(keyword: String): Bookmark?

    /**
     * Insert a list of bookmarks.
     * If a keyword exists, it will be ignored.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg bookmarks: Bookmark): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(bookmark: Bookmark): Long

    @Delete
    suspend fun delete(bookmark: Bookmark)
}
