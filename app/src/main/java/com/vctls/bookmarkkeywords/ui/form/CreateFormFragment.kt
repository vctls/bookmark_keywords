package com.vctls.bookmarkkeywords.ui.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vctls.bookmarkkeywords.MainActivity
import com.vctls.bookmarkkeywords.data.BookmarkDatabase
import com.vctls.bookmarkkeywords.databinding.FragmentCreateFormBinding
import com.vctls.bookmarkkeywords.model.Bookmark
import kotlinx.coroutines.runBlocking

class CreateFormFragment : Fragment() {

    private lateinit var viewModel: FormViewModel
    private var _binding: FragmentCreateFormBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateFormBinding.inflate(inflater, container, false)
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

        // TODO Maybe don't make this blocking.
        save.setOnClickListener {
            runBlocking {
                new(keyword.text.toString(), template.text.toString(), name.text.toString())
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
}
