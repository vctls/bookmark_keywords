package vctls.bookmarkkeywords.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.runBlocking
import vctls.bookmarkkeywords.R
import vctls.bookmarkkeywords.data.BookmarkDatabase
import vctls.bookmarkkeywords.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val root: View = binding.root
        val query = binding.inputQuery
        val go = binding.btnGo

        query.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                go(v.text.toString())
                true
            } else {
                false
            }
        }
        go.setOnClickListener { go(query.text.toString()) }

        // Immediately focus the query field on opening the home view.
        query.requestFocus()

        return root
    }

    /**
     * Extract the keyword from the text query,
     * find the corresponding template,
     * replace placeholders with the given value
     * then open the resulting URL in the default browser.
     */
    private fun go(queryString: String) {
        // TODO Validate input. In view model, probably?

        // Get keyword as first word of string.
        val spaceIndex = queryString.indexOf(' ')
        val search = if (spaceIndex == -1) {
            ""
        } else {
            queryString.substring(spaceIndex + 1)
        }

        // If there is no space in the query, or nothing after the space
        // consider it as a simple bookmark and not a search.
        val keyword = if (search == "") {
            queryString
        } else {
            queryString.substring(0, spaceIndex)
        }

        var template: String
        // TODO Check if there is a cleaner way of handling this with coroutines.
        runBlocking {
            template = getTemplate(keyword)
        }

        if (template === "") {
            // Handle unknown keyword.
            Toast.makeText(
                context,
                R.string.error_keyword_not_found,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val url = String.format(template, search)

        // Try to open URL with the default app.
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            startActivity(browserIntent)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                R.string.error_compatible_app_not_found,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Get the template from a bookmark in the database.
     * TODO This should probably be moved elsewhere.
     */
    private suspend fun getTemplate(keyword: String): String {
        val db = context?.let { BookmarkDatabase.getInstance(it) }
        if (db != null) {
            return db.bookmarkDao().findByKeyword(keyword)?.template ?: ""
        }
        return ""
    }
}
