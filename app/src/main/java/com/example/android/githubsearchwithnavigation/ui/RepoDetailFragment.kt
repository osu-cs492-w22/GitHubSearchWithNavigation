package com.example.android.githubsearchwithnavigation.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.githubsearchwithnavigation.R
import com.example.android.githubsearchwithnavigation.data.GitHubRepo
import com.google.android.material.snackbar.Snackbar

const val EXTRA_GITHUB_REPO = "com.example.android.githubsearchwithnavigation.GitHubRepo"

class RepoDetailFragment : Fragment(R.layout.repo_detail) {
    private var repo: GitHubRepo? = null
    private var isBookmarked = false

    private val viewModel: BookmarkedReposViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

//        if (intent != null && intent.hasExtra(EXTRA_GITHUB_REPO)) {
//            repo = intent.getSerializableExtra(EXTRA_GITHUB_REPO) as GitHubRepo
//            findViewById<TextView>(R.id.tv_repo_name).text = repo!!.name
//            findViewById<TextView>(R.id.tv_repo_stars).text = repo!!.stars.toString()
//            findViewById<TextView>(R.id.tv_repo_description).text = repo!!.description
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.activity_repo_detail, menu)
//        val bookmarkItem = menu.findItem(R.id.action_bookmark)
//        viewModel.getBookmarkedRepoByName(repo!!.name).observe(viewLifecycleOwner) { bookmarkedRepo ->
//             when (bookmarkedRepo) {
//                null -> {
//                    isBookmarked = false
//                    bookmarkItem.isChecked = false
//                    bookmarkItem.icon = AppCompatResources.getDrawable(
//                        requireContext(),
//                        R.drawable.ic_action_bookmark_off
//                    )
//                }
//                else -> {
//                    isBookmarked = true
//                    bookmarkItem.isChecked = true
//                    bookmarkItem.icon = AppCompatResources.getDrawable(
//                        requireContext(),
//                        R.drawable.ic_action_bookmark_on
//                    )
//                }
//            }
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_view_repo -> {
                viewRepoOnWeb()
                true
            }
            R.id.action_share -> {
                shareRepo()
                true
            }
            R.id.action_bookmark -> {
                toggleRepoBookmark(item)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * This method toggles the state of the bookmark icon in the top app bar whenever the user
     * clicks it.
     */
    private fun toggleRepoBookmark(menuItem: MenuItem) {
        if (repo != null) {
            isBookmarked = !isBookmarked
            when (isBookmarked) {
                true -> {
                    viewModel.addBookmarkedRepo(repo!!)
                }
                false -> {
                    viewModel.removeBookmarkedRepo(repo!!)
                }
            }
        }
    }

    private fun viewRepoOnWeb() {
        if (repo != null) {
            val intent: Intent = Uri.parse(repo!!.url).let {
                Intent(Intent.ACTION_VIEW, it)
            }
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Snackbar.make(
                    requireView(),
                    R.string.action_view_repo_error,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun shareRepo() {
        if (repo != null) {
            val text = getString(R.string.share_text, repo!!.name, repo!!.url)
            val intent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent, null))
        }
    }
}