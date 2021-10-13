package com.vctls.bookmarkkeywords.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vctls.bookmarkkeywords.model.Bookmark

private const val NAME = "bookmark"

@Database(entities = [Bookmark::class], version = 1, exportSchema = false)
abstract class BookmarkDatabase : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao

    // code below courtesy of https://github.com/googlesamples/android-sunflower;
    companion object {

        @Volatile
        private var instance: BookmarkDatabase? = null

        fun getInstance(context: Context): BookmarkDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): BookmarkDatabase {
            return Room.databaseBuilder(
                context, BookmarkDatabase::class.java,
                NAME
            ).build()
        }
    }
}
