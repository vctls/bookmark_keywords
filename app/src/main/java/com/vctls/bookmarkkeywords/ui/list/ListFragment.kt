package com.vctls.bookmarkkeywords.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vctls.bookmarkkeywords.R
import com.vctls.bookmarkkeywords.data.BookmarkDatabase
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
                var bookmarks: List<Bookmark>

                // TODO Again, this should probably not be blocking.
                runBlocking { bookmarks = getBookmarks() }

                adapter = RecyclerViewAdapter(bookmarks)
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

    /**
     * Get all bookmarks the database.
     * TODO This should probably be moved elsewhere.
     */
    private suspend fun getBookmarks(): List<Bookmark> {
        val db = context?.let { BookmarkDatabase.getInstance(it) }

        // TODO Shouldn't this be an exception?
        if (db == null) {
            Toast.makeText(context, "Database not found", Toast.LENGTH_LONG).show()
            return emptyList()
        }

        return db.bookmarkDao().getAll()
    }
}
