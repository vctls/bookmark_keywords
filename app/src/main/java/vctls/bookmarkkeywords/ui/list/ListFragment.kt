package vctls.bookmarkkeywords.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.runBlocking
import vctls.bookmarkkeywords.MainActivity
import vctls.bookmarkkeywords.R
import vctls.bookmarkkeywords.model.Bookmark

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

                runBlocking { bookmarks = (activity as MainActivity).getBookmarks() }

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
                            val mainActivity = activity as MainActivity
                            runBlocking {
                                mainActivity.deleteBookmark(keyword)
                            }
                            bookmarks.remove(bookmarks.find { it.keyword == keyword })
                            mainActivity.toast(getString(R.string.bookmark_deleted))

                            val position = viewHolder.bindingAdapterPosition
                            val recyclerViewAdapter = adapter as RecyclerViewAdapter

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
}
