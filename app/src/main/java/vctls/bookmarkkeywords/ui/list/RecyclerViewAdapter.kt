package vctls.bookmarkkeywords.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import vctls.bookmarkkeywords.databinding.ContentBookmarkListBinding
import vctls.bookmarkkeywords.model.Bookmark

/**
 */
class RecyclerViewAdapter(
    private val values: MutableList<Bookmark>
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ContentBookmarkListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.keywordView.text = item.keyword
        holder.templateView.text = item.template
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ContentBookmarkListBinding) :
        RecyclerView.ViewHolder(binding.root), BookmarkViewHolder {
        override val keywordView: TextView = binding.keyword
        override val templateView: TextView = binding.template

        init {
            binding.root.setOnClickListener {
                navigateToBookmark(keywordView.text.toString(), binding.root)
            }
        }

        private fun navigateToBookmark(keyword: String, view: View) {
            val direction = ListFragmentDirections.actionListToForm(keyword)
            view.findNavController().navigate(direction)
        }

        override fun toString(): String {
            return super.toString() + " '" + templateView.text + "'"
        }
    }
}
