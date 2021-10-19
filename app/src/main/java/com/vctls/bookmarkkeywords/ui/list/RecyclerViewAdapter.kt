package com.vctls.bookmarkkeywords.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.vctls.bookmarkkeywords.databinding.ContentBookmarkListBinding
import com.vctls.bookmarkkeywords.model.Bookmark

/**
 */
class RecyclerViewAdapter(
    private val values: List<Bookmark>
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
        holder.idView.text = item.keyword
        holder.contentView.text = item.template
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ContentBookmarkListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content

        init {
            binding.root.setOnClickListener {
                navigateToBookmark(idView.text.toString(), binding.content)
            }
        }

        private fun navigateToBookmark(keyword: String, view: View) {
            val direction = ListFragmentDirections.actionListToForm(keyword)
            view.findNavController().navigate(direction)
        }

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}
