package com.example.android.githubsearchwithnavigation.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.githubsearchwithnavigation.R
import com.example.android.githubsearchwithnavigation.data.GitHubRepo
import com.example.android.githubsearchwithnavigation.data.LoadingStatus
import com.google.android.material.progressindicator.CircularProgressIndicator

class GitHubSearchFragment : Fragment(R.layout.github_search) {
    private val TAG = "GitHubSearchFragment"

    private val repoListAdapter = GitHubRepoListAdapter(::onGitHubRepoClick)
    private val viewModel: GitHubSearchViewModel by viewModels()

    private lateinit var searchBoxET: EditText
    private lateinit var searchResultsListRV: RecyclerView
    private lateinit var searchErrorTV: TextView
    private lateinit var loadingIndicator: CircularProgressIndicator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchBoxET = view.findViewById(R.id.et_search_box)
        searchResultsListRV = view.findViewById(R.id.rv_search_results)
        searchErrorTV = view.findViewById(R.id.tv_search_error)
        loadingIndicator = view.findViewById(R.id.loading_indicator)

        searchResultsListRV.layoutManager = LinearLayoutManager(requireContext())
        searchResultsListRV.setHasFixedSize(true)

        searchResultsListRV.adapter = repoListAdapter

        viewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            repoListAdapter.updateRepoList(searchResults)
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                LoadingStatus.LOADING -> {
                    loadingIndicator.visibility = View.VISIBLE
                    searchResultsListRV.visibility = View.INVISIBLE
                    searchErrorTV.visibility = View.INVISIBLE
                }
                LoadingStatus.ERROR -> {
                    loadingIndicator.visibility = View.INVISIBLE
                    searchResultsListRV.visibility = View.INVISIBLE
                    searchErrorTV.visibility = View.VISIBLE
                }
                else -> {
                    loadingIndicator.visibility = View.INVISIBLE
                    searchResultsListRV.visibility = View.VISIBLE
                    searchErrorTV.visibility = View.INVISIBLE
                }
            }
        }

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val searchBtn: Button = view.findViewById(R.id.btn_search)
        searchBtn.setOnClickListener {
            val query = searchBoxET.text.toString()
            if (!TextUtils.isEmpty(query)) {
                val sort = sharedPrefs.getString(
                    getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_default)
                )
                val user = sharedPrefs.getString(
                    getString(R.string.pref_user_key),
                    null
                )
                val languages = sharedPrefs.getStringSet(
                    getString(R.string.pref_language_key),
                    null
                )
                val firstIssues = sharedPrefs.getInt(
                    getString(R.string.pref_first_issues_key),
                    0
                )
                viewModel.loadSearchResults(query, sort, user, languages, firstIssues)
                searchResultsListRV.scrollToPosition(0)
            }
        }
    }

    private fun onGitHubRepoClick(repo: GitHubRepo) {
        val directions = GitHubSearchFragmentDirections.navigateToRepoDetail(repo, 16)
        findNavController().navigate(directions)
    }
}