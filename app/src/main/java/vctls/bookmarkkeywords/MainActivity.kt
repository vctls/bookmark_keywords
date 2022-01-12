package vctls.bookmarkkeywords

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.runBlocking
import vctls.bookmarkkeywords.data.BookmarkDatabase
import vctls.bookmarkkeywords.databinding.ActivityMainBinding
import vctls.bookmarkkeywords.error.BookmarkKeywordsError
import vctls.bookmarkkeywords.model.Bookmark
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date

const val CREATE_FILE = 1

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    // Make fab accessible to allow manipulation it on specific fragments.
    lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fab = binding.appBarMain.fab

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_go,
                R.id.nav_list
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.appBarMain.fab.setOnClickListener {
            // Use the navigation controller to move to the form instead of the fragment manager.
            // TODO Any way to animate this?
            navController.navigate(R.id.nav_form)
        }

        binding.navView.menu.findItem(R.id.nav_export).setOnMenuItemClickListener {
            export()
            true
        }
    }

    fun hasFab(): Boolean {
        return ::fab.isInitialized
    }

    /**
     * Export the database to a file.
     */
    private fun export() {
        val filename =
            SimpleDateFormat(getString(R.string.date_format)).format(Date()) + getString(R.string.export_suffix)
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, filename)
        }
        startActivityForResult(intent, CREATE_FILE)
    }

    /**
     * Add the necessary double quotes for CSV strings.
     */
    private fun quote(string: String?): String {
        return "\"" + string?.replace("\"", "\\\"") + "\""
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_FILE) {
            val uri = data?.data
            when (resultCode) {
                RESULT_OK -> if (uri != null) {

                    // Get the bookmarks.
                    runBlocking {
                        val bookmarks = getBookmarks()
                        val builder = StringBuilder()

                        // Make a CSV string.
                        builder.append("\"name\";\"template\";\"keyword\"\n")
                        for (bookmark in bookmarks) {
                            builder
                                .append(quote(bookmark.name) + ";")
                                .append(quote(bookmark.template) + ";")
                                .append(quote(bookmark.keyword) + "\n")
                        }
                        writeInFile(uri, builder.toString())
                    }
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.toast_export_ok),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                RESULT_CANCELED -> {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.toast_export_nok),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun writeInFile(uri: Uri, text: String) {
        val outputStream: OutputStream?
        try {
            outputStream = contentResolver.openOutputStream(uri)
            val bw = BufferedWriter(OutputStreamWriter(outputStream))
            bw.write(text)
            bw.flush()
            bw.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Get the Database.
     */
    private fun getDb(): BookmarkDatabase {
        val context = this.applicationContext
        val db = context?.let { BookmarkDatabase.getInstance(it) }
        if (db == null) {
            Toast.makeText(context, R.string.error_database_not_found, Toast.LENGTH_LONG).show()
            throw BookmarkKeywordsError.DatabaseNotFoundException
        }
        return db
    }

    /**
     * Get all bookmarks the database.
     */
    suspend fun getBookmarks(): MutableList<Bookmark> {
        return getDb().bookmarkDao().getAll()
    }

    /**
     * Delete a bookmark identified by its keyword.
     */
    suspend fun deleteBookmark(keyword: String) {
        val db = getDb()
        getDb().bookmarkDao().findByKeyword(keyword)?.let { db.bookmarkDao().delete(it) }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
