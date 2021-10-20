package com.vctls.bookmarkkeywords.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vctls.bookmarkkeywords.R
import com.vctls.bookmarkkeywords.data.BookmarkDatabase
import com.vctls.bookmarkkeywords.error.BookmarkKeywordsError
import com.vctls.bookmarkkeywords.model.Bookmark
import kotlinx.coroutines.runBlocking

/**
 * A fragment representing a list of Items.
 */
class ListFragment : Fragment() {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookmark_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                var bookmarks: MutableList<Bookmark>

                // TODO Again, this should probably not be blocking.
                runBlocking { bookmarks = getBookmarks() }

                adapter = RecyclerViewAdapter(bookmarks)

                // Add swipe listener to delete items.
                val itemTouchHelper = ItemTouchHelper(object :
                        ItemTouchHelper.SimpleCallback(
                            0,
                            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                        ) {
                        override fun onMove(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder
                        ): Boolean {
                            TODO("Not yet implemented")
                        }

                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            val bookmarkViewHolder = viewHolder as BookmarkViewHolder
                            val keyword = bookmarkViewHolder.keywordView.text.toString()
                            runBlocking {
                                deleteBookmark(keyword)
                            }
                            bookmarks.remove(bookmarks.find { it.keyword == keyword })
                            Toast.makeText(context, R.string.bookmark_deleted, Toast.LENGTH_SHORT)
                                .show()
                            // FIXME The item is removed from the database but not the view.
                            val position = viewHolder.bindingAdapterPosition
                            val recyclerViewAdapter = adapter as RecyclerViewAdapter
                            // FIXME Whatever notification I use, items get added back in the view
                            //   until I switch to another view and back to the list.
                            recyclerViewAdapter.notifyItemRemoved(position)
                            recyclerViewAdapter.notifyItemRangeChanged(
                                position,
                                recyclerViewAdapter.itemCount
                            )
                        }
                    })

                itemTouchHelper.attachToRecyclerView(view)
            }
        }

        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    private fun getDb(): BookmarkDatabase {
        val db = context?.let { BookmarkDatabase.getInstance(it) }
        // TODO Shouldn't this be an exception?
        if (db == null) {
            Toast.makeText(context, R.string.error_database_not_found, Toast.LENGTH_LONG).show()
            throw BookmarkKeywordsError.DatabaseNotFoundException
        }
        return db
    }

    /**
     * Get all bookmarks the database.
     * TODO This should probably be moved elsewhere.
     */
    private suspend fun getBookmarks(): MutableList<Bookmark> {
        return getDb().bookmarkDao().getAll()
    }

    /**
     * Delete a bookmark identified by its keyword.
     */
    private suspend fun deleteBookmark(keyword: String) {
        val db = getDb()
        getDb().bookmarkDao().findByKeyword(keyword)?.let { db.bookmarkDao().delete(it) }
    }
}
