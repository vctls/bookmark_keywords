package com.vctls.bookmarkkeywords.ui.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vctls.bookmarkkeywords.MainActivity
import com.vctls.bookmarkkeywords.data.BookmarkDatabase
import com.vctls.bookmarkkeywords.databinding.FragmentFormBinding
import com.vctls.bookmarkkeywords.model.Bookmark
import kotlinx.coroutines.runBlocking

class FormFragment : Fragment() {

    private lateinit var viewModel: FormViewModel
    private var _binding: FragmentFormBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFormBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Hide the fab. TODO Replace it with a save button instead, somehow.
        // FIXME The fab reappears on orientation change anyway.
        val mainActivity = (activity as MainActivity)
        if (mainActivity.hasFab()) {
            mainActivity.fab.hide()
        }

        // TODO Validate form using the viewmodel.
        val name = binding.name
        val template = binding.template
        val keyword = binding.keyword
        // TODO Reuse the fab instead.
        val save = binding.save

        // If fragment has a destination argument,
        //  get the corresponding bookmark for edition.
        var bookmark: Bookmark? = null

        try {
            if (requireArguments().containsKey("keyword")) {
                runBlocking {
                    bookmark = getBookmark(arguments?.get("keyword") as String)
                }
            }
        } catch (e: IllegalStateException) {}

        if (bookmark != null) {
            name.setText(bookmark!!.name)
            template.setText(bookmark!!.template)
            keyword.setText(bookmark!!.keyword)
        }

        save.setOnClickListener {
            // TODO Maybe don't make this blocking.
            runBlocking {
                new(
                    keyword.text.toString(),
                    template.text.toString(),
                    name.text.toString()
                )
                // Switch back to previous fragment. TODO If successful!
                findNavController().popBackStack()
            }
        }

        return root
    }

    private suspend fun new(keyword: String, template: String, name: String?) {
        val bookmark = Bookmark(null, keyword, template, name)
        val db = context?.let { BookmarkDatabase.getInstance(it) }
        db?.bookmarkDao()?.insertAll(bookmark)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Show the fab again.
        val mainActivity = (activity as MainActivity)
        if (mainActivity.hasFab()) {
            mainActivity.fab.show()
        }
    }

    /**
     * Get the template from a bookmark in the database.
     * TODO This should probably be moved elsewhere.
     */
    private suspend fun getBookmark(keyword: String): Bookmark? {
        val db = context?.let { BookmarkDatabase.getInstance(it) }
        if (db != null) {
            return db.bookmarkDao().findByKeyword(keyword)
        }
        return null
    }
}
