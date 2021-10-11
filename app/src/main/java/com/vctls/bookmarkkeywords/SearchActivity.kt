package com.vctls.bookmarkkeywords

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vctls.bookmarkkeywords.databinding.ActivitySearchBinding

// TODO Maybe rename the activity.
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val query = binding.inputQuery
        val go = binding.btnGo

        go.setOnClickListener {

            // TODO Validate input. In view model, probably?
            val queryString = query.text.toString()

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

            val template = findTemplate(keyword)

            if (template === null) {
                // TODO Handle unknown keyword.
                Toast.makeText(
                    applicationContext,
                    R.string.keyword_not_found,
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val url = String.format(template, search)

            // Open URL in default browser.
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }

    private fun findTemplate(keyword: String): String? {
        // TODO Get templates from a database.
        val templates = hashMapOf(
            "du" to "https://duckduckgo.com/%s",
            "g" to "https://www.google.fr/search?newwindow=1&hl=en&q=%s",
            "enfr" to "http://www.wordreference.com/enfr/%s",
            "fren" to "http://www.wordreference.com/fren/%s",
            "cnrtl" to "http://www.cnrtl.fr/definition/%s",
        )
        return templates[keyword]
    }
}
